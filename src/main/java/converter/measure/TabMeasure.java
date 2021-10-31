package converter.measure;

import converter.Instrument;
import converter.Score;
import converter.ScoreComponent;
import converter.instruction.RepeatType;
import converter.instruction.TimeSignature;
import converter.measure_line.TabBassString;
import converter.measure_line.TabDrumString;
import converter.measure_line.TabGuitarString;
import converter.measure_line.TabString;
import converter.note.Note;
import converter.note.NoteFactory;
import utility.Settings;
import utility.DrumUtils;
import utility.GuitarUtils;
import utility.Patterns;
import utility.Range;
import utility.ValidationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TabMeasure implements ScoreComponent {
    public static int MEASURE_INDEX;
    
    protected int measureCount;
    protected int beatCount = Settings.getInstance().tsNum;
    protected int beatType = Settings.getInstance().tsDen;
    List<String> lines;
    List<String[]> lineNamesAndPositions;
    public int lineCount;
    List<Integer> positions;
    public List<TabString> tabStringList;
    boolean isFirstMeasureInGroup;
    List<List<Note>> voiceSortedNoteList;   // a list of voices where each voice is a sorted list of notes

    boolean repeatStart = false;
    boolean repeatEnd = false;
    int repeatCount = 0;
    public boolean changesTimeSignature = false;
    int divisions;
//    public boolean changesTimeSignature() {
//        return this.changesTimeSignature;
//    }

    public int getBeatCount() {
        return this.beatCount;
    }
    public int getBeatType() {
        return this.beatType;
    }

    public TabMeasure(List<String> lines, List<String[]> lineNamesAndPositions, List<Integer> linePositions, boolean isFirstMeasureInGroup) {
        this.measureCount = ++MEASURE_INDEX;
        this.lines = lines;
        this.lineCount = this.lines.size();
        this.lineNamesAndPositions = lineNamesAndPositions;
        this.positions = linePositions;
        this.isFirstMeasureInGroup = isFirstMeasureInGroup;
        this.tabStringList = this.createTabStringList(this.lines, this.lineNamesAndPositions, this.positions);
        this.voiceSortedNoteList = this.getVoiceSortedNoteList();
        setChords();
        setDurations();
    }

    /**
     * Creates a List of TabString objects from the provided string representation of a Measure.
     * These TabString objects are not guaranteed to be valid. you can find out if all the TabMeasure
     * objects in this MeasureGroup are actually valid by calling the TabMeasure().validate() method.
     * @param lines a List of Strings where each String represents a line of the measure. It is a parallel list with lineNames and linePositions
     * @param namesAndPosition a List of Strings where each String represents the name of a line of the measure. It is a parallel list with lines and linePositions
     * @param linePositions a List of Integers where each number represents the starting index of a line of the measure,
     *                      where a starting index of a line is the index where the line can be found in
     *                      Score.tabText, from where it was derived. It is a parallel list with lineNames and lines
     * @return A list of TabString objects. The concrete class type of these TabString objects is determined
     * from the input String lists(lines and lineNames), and they are not guaranteed to all be of the same type.
     */
    protected List<TabString> createTabStringList(List<String> lines, List<String[]> namesAndPosition, List<Integer> linePositions) {
        List<TabString> measureLineList = new ArrayList<>();
        for (int i=0; i<lines.size(); i++) {
            String line = lines.get(i);
            String[] nameAndPosition = namesAndPosition.get(i);
            int position = linePositions.get(i);
            //Instrument instrumentBias = this instanceof BassMeasure ? Instrument.BASS : this instanceof DrumMeasure ? Instrument.DRUMS : this instanceof GuitarMeasure ? Instrument.GUITAR : Instrument.AUTO;
            measureLineList.add(newTabString(i+1,line, nameAndPosition, position));
        }
        return measureLineList;
    }

    /**
     * Creates a MeasureLine object from the provided string representation of the MeasureLine. The MeasureLine object
     * is either of type GuitarMeasureLine or DrumMeasureLine depending on if the features of the input Strings resembles
     * a guitar measure line or a drum measure line (this is determined by the MeasureLine.isGuitar() and MeasureLine.isDrum())
     * If it has features that neither belongs to GuitarMeasure nor DrumMeasure or has features shared by both, it defaults
     * to creating a GuitarMeasureLine object, and further error checking can be done by calling GuitarMeasureLine().validate()
     * on the object returned.
     * @param line the contents of the MeasureLine
     * @param nameAndPosition the name of the MeasureLine
     * @param position  the index at which the contents of the measure line can be found in the root string from which it
     *                 was derived (i.e Score.ROOT_STRING)
     * @return a MeasureLine object derived from the information in the input Strings. Either of type GuitarMeasureLine
     * or DrumMeasureLine
     */
    protected abstract TabString newTabString(int stringNumber, String line, String[] nameAndPosition, int position);
    //{
//        if (Settings.getInstance().instrument!=Instrument.AUTO) {
//            return switch (Settings.getInstance().instrument) {
//                case GUITAR -> new TabGuitarString(stringNumber, line, nameAndPosition, position);
//                case BASS -> new TabBassString(stringNumber, line, nameAndPosition, position);
//                case DRUMS -> new TabDrumString(stringNumber, line, nameAndPosition, position);
//                case AUTO -> null;
//            };
//        }else {
//            double guitarLikelihood = GuitarUtils.isGuitarLineLikelihood(nameAndPosition[0], line, bias);
//            double drumLikelihood = DrumUtils.isDrumLineLikelihood(nameAndPosition[0], line, bias);
//            if (guitarLikelihood >= drumLikelihood) {
//                if (prefBass)
//                    return new TabBassString(stringNumber, line, nameAndPosition, position);
//                else
//                    return new TabGuitarString(stringNumber, line, nameAndPosition, position);
//            } else
//                return new TabDrumString(stringNumber, line, nameAndPosition, position);
//        }
//
    
    public void setDurations() {
        for (List<List<Note>> chordList : getVoiceSortedChordList()) {
            int maxMeasureLineLen = getMaxMeasureLineLength();
			
			// Handle all but last chord
			for (int i = 0; i < chordList.size() - 1; i++) {
			    List<Note> chord = chordList.get(i);
			    List<Note> nextChord = chordList.get(i+1);
			    int currentChordDistance = chord.get(0).distance;
			    int nextChordDistance = nextChord.get(0).distance;
			
			    int duration = nextChordDistance-currentChordDistance;
			    duration = adjustDurationForDoubleCharacterNotes(duration, chord, nextChord);
			    
			
			    for (Note note : chord) {
			        note.setDuration(duration);
			    }
			}
			// Handle last chord, as it is a special case (it has no next chord)
			if (!chordList.isEmpty()) {
			    List<Note> chord = chordList.get(chordList.size()-1);
			    int currentChordDistance = chord.get(0).distance;
			
			    int duration = maxMeasureLineLen-currentChordDistance;
			    duration = adjustDurationForDoubleCharacterNotes(duration, chord, null);
			    
			    for (Note note : chord) {
			        note.setDuration(duration);
			    }
			}
        }
    }
    
    protected abstract int adjustDurationForDoubleCharacterNotes(int duration, List<Note> chord, List<Note> nextChord);
    
	protected boolean isDoubleDigit(List<Note> chord) {
    	boolean doubleDigit = false;
    	for (Note note : chord) {
	    	if (note.origin.length() == 2) doubleDigit = true;
	    }
		return doubleDigit;
	}
    
	//    private void calcDurationRatiosOld(List<List<Note>> chordList) {
//        int maxMeasureLineLen = getMaxMeasureLineLength();
//
//        // Handle all but last chord
//		for (int i = 0; i < chordList.size() - 1; i++) {
//            List<Note> chord = chordList.get(i);
//            int currentChordDistance = chord.get(0).distance;
//            int nextChordDistance = chordList.get(i+1).get(0).distance;
//
//            double durationRatio = ((double)(nextChordDistance-currentChordDistance))/maxMeasureLineLen;
//            for (Note note : chord) {
//                note.setDurationRatio(durationRatio);
//            }
//        }
//        // Handle last chord, as it is a special case (it has no next chord)
//        if (!chordList.isEmpty()) {
//            List<Note> chord = chordList.get(chordList.size()-1);
//            int currentChordDistance = chord.get(0).distance;
//
//            double durationRatio = ((double)(maxMeasureLineLen-currentChordDistance))/maxMeasureLineLen;
//            for (Note note : chord) {
//                note.setDurationRatio(durationRatio);
//            }
//        }
//    }
//    
    public List<List<List<Note>>> getVoiceSortedChordList() {
        List<List<List<Note>>> voiceSortedChordList = new ArrayList<>();
        for (List<Note> voice : this.voiceSortedNoteList) {
            List<List<Note>> voiceChordList = new ArrayList<>();
            List<Note> currentChord = new ArrayList<>();
            for (Note note : voice) {
                if (note.startsWithPreviousNote)
                    currentChord.add(note);
                else {
                    currentChord = new ArrayList<>();
                    currentChord.add(note);
                    voiceChordList.add(currentChord);
                }
            }
            voiceSortedChordList.add(voiceChordList);
        }
        return voiceSortedChordList;
    }

    public int setDivisions() {
        //double totalMeasureDuration = (double)beatCount/(double)beatType;
        int measureLength = getMaxMeasureLineLength();
        int firstNotePosition = voiceSortedNoteList.get(0).get(0).distance;
        int usefulMeasureLength = measureLength - firstNotePosition;
        // Must subtract for double digit numbers
        usefulMeasureLength = adjustDivisionsForDoubleCharacterNotes(usefulMeasureLength); 
        int divisor = beatCount * 4 /beatType;
        divisions = (usefulMeasureLength - (usefulMeasureLength % divisor)) / divisor;
        //System.out.println(divisions);
//        double minDurationRatio = 0;
//        for (List<Note> voice : this.voiceSortedNoteList) {
//            for (Note note : voice) {
//                double noteDurationRatio = note.durationRatio;
//                if (noteDurationRatio == 0)
//                    continue;
//                if (minDurationRatio == 0)
//                    minDurationRatio = noteDurationRatio;
//                minDurationRatio = Math.min(minDurationRatio, note.durationRatio);
//            }
//        }
//        if (minDurationRatio==0)
//            minDurationRatio = 1;
//
//        //the number of times you have to divide a whole note to get the note (minDurationRatio*total...) with the shortest duration (i.e a quarter note is 4, an eighth note is 8, 1/16th note is 16, etc)
//        double inverseStandardDuration = 1/(minDurationRatio*totalMeasureDuration);
//
//        //we might not get the exact value we want though (e.g we get a 17th note, but that doesn't exist. we want either a 16th note that is dotted, or a 32th note, cuz those are the standard note types.)
//        double roundedUpInverseStandardDuration = Math.pow(2, Math.ceil(Math.log(inverseStandardDuration)/Math.log(2)));      //we get the shortest nearest note to this note (e.g, if we have a 17th note, this gives us the number 32 as the note 1/32 is the shortest nearest note to 1/17)
//
//        //find out how many times we need to divide a 4th note to get our smallest duration note (e.g say the smallest duration note in our score is a 1/64th note (wholeNOteDuration = 64) then we need to divide a 4th note 64/4 times to get our smallest duration note)
//        double divisions = roundedUpInverseStandardDuration*0.25;
//        this.divisions = (int) Math.ceil(divisions);
        for (List<Note> voice : this.voiceSortedNoteList) {
            for (Note note : voice) {
                note.setDivisions(this.divisions);
            }
        }
        return divisions;
    }

    protected abstract int adjustDivisionsForDoubleCharacterNotes(int usefulMeasureLength);

    public boolean setRepeat(int repeatCount, RepeatType repeatType) {
        if (repeatCount<0)
            return false;
        if (!(repeatType == RepeatType.START || repeatType == RepeatType.END))
            return false;
        this.repeatCount = repeatCount;
        if (repeatType == RepeatType.START)
            this.repeatStart = true;
        if (repeatType == RepeatType.END)
            this.repeatEnd = true;
        return true;
    }

    public boolean isRepeatStart() {
        return this.repeatStart;
    }

    public boolean isRepeatEnd() {
        return this.repeatEnd;
    }

    public boolean setTimeSignature(int beatCount, int beatType) {
        if (!TimeSignature.isValid(beatCount, beatType))
            return false;
        this.beatCount = beatCount;
        this.beatType = beatType;
        for (List<Note> voice : this.voiceSortedNoteList) {
            for (Note note : voice) {
                note.setBeatType(this.beatType);
                note.setBeatCount(this.beatCount);
            }
        }
        return true;
    }

    public int getMaxMeasureLineLength() {
        int maxLen = 0;
        for (TabString mLine : this.tabStringList) {
            maxLen = Math.max(maxLen, mLine.line.replace("\s", "").length());
        }
        return maxLen;
    }



    /**
     * Creates a string representation of the index position range of each line making up this Measure instance,
     * where each index position range describes the location where the lines of this Measure can be found in the
     * root string from which it was derived (i.e Score.ROOT_STRING)
     * @return a String representing the index range of each line in this Measure, formatted as follows:
     * "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public List<Integer[]> getLinePositions() {
        List<Integer[]> linePositions = new ArrayList<>();
        for (int i=0; i<this.lines.size(); i++) {
            int startIdx = this.positions.get(i);
            int endIdx = startIdx+this.lines.get(i).length();
            linePositions.add(new Integer[]{startIdx, endIdx});
        }
        return linePositions;
    }

    protected void setChords() {
        for (List<Note> voice : this.voiceSortedNoteList) {
            Note previousNote = null;
            for (Note currentNote : voice) {
                if (currentNote.isGrace) continue;
                if (previousNote != null && previousNote.distance == currentNote.distance)
                    currentNote.startsWithPreviousNote = true;
                previousNote = currentNote;
            }
        }
    }

    public List<List<Note>> getVoiceSortedNoteList() {
        List<List<Note>> voiceSortedNoteList = new ArrayList<>();
        HashMap<Integer, Integer> voiceToIndexMap = new HashMap<>();
        int currentIdx = 0;
        for (Note note : this.getSortedNoteList()) {
            if (!voiceToIndexMap.containsKey(note.voice)) {
                voiceToIndexMap.put(note.voice, currentIdx++);
                voiceSortedNoteList.add(new ArrayList<>());
            }
            int idx = voiceToIndexMap.get(note.voice);
            List<Note> voice = voiceSortedNoteList.get(idx);
            voice.add(note);
        }
        return voiceSortedNoteList;
    }

    public List<Note> getSortedNoteList() {
        List<Note> sortedNoteList = new ArrayList<>();
        for (TabString tabString : this.tabStringList) {
            sortedNoteList.addAll(tabString.getNoteList());
        }
        Collections.sort(sortedNoteList);
        return sortedNoteList;
    }

    public boolean isGuitar(boolean strictCheck) {
        for (TabString measureLine : this.tabStringList) {
            if (!measureLine.isGuitar(strictCheck))
                return false;
        }
        return true;
    }

    public boolean isDrum(boolean strictCheck) {
        for (TabString measureLine : this.tabStringList) {
            if (!measureLine.isDrum(strictCheck))
                return false;
        }
        return true;
    }
    public boolean isBass(boolean strictCheck) {
        for (TabString measureLine : this.tabStringList) {
            if (!measureLine.isGuitar(strictCheck))
                return false;
        }
        return this.tabStringList.size() >= BassMeasure.MIN_LINE_COUNT && this.tabStringList.size() <= BassMeasure.MAX_LINE_COUNT;
    }

    /**
     * @return the range of the first line of the measure in its text line
     */
    public Range getRelativeRange() {
        if (this.lines.isEmpty()) return null;
        int position;
        if (this.isFirstMeasureInGroup)
            position = Integer.parseInt(lineNamesAndPositions.get(0)[1]);   // use the starting position of the name instead.
        else
            position = this.positions.get(0);       // Took that -1 away: use the starting position of the inside of the measure minus one, so that it also captures the starting line of that measure "|"
        int relStartPos = position-Score.tabText.substring(0,position).lastIndexOf("\n");
        String line = this.lines.get(0);
        int lineLength = 0;
        if (line.matches("[^|]*\\|\\s*"))   //if it ends with a |
            lineLength = line.length()-1;
        else
            lineLength = line.length();
        int relEndPos = relStartPos + lineLength;
        return new Range(relStartPos, relEndPos);
    }

    public int getCount() {
        return this.measureCount;
    }

	public abstract models.measure.Measure getModel();

	/**
	 * Validates if all MeasureLine objects which this Measure object aggregates are instances of the same concrete
	 * MeasureLine Class (i.e they're all GuitarMeasureLine instances or all DrumMeasureLine objects). It does not
	 * validate its aggregated objects. That job is left up to its concrete classes (this is an abstract class)
	 * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
	 * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
	 * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
	 * found in the root string from which it was derived (i.e Score.ROOT_STRING).
	 * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
	 */
	public List<ValidationError> validate() {
	    List<ValidationError> result = new ArrayList<>();
	    int ERROR_SENSITIVITY = Settings.getInstance().errorSensitivity;
	
	    boolean hasGuitarMeasureLines = true;
	    boolean hasDrumMeasureLines = true;
	    boolean lineSizeEqual = true;
	
	    int previousLineLength = -1;
	    for (TabString tabString : this.tabStringList) {
	        hasGuitarMeasureLines &= tabString instanceof TabGuitarString;
	        hasDrumMeasureLines &= tabString instanceof TabDrumString;
	
	        int currentLineLength = tabString.line.replace("\s", "").length();
	        lineSizeEqual &= (previousLineLength<0) || previousLineLength==currentLineLength;
	        previousLineLength = currentLineLength;
	    }
	    if (!(hasGuitarMeasureLines || hasDrumMeasureLines)) {
	        ValidationError error = new ValidationError(
	                "All measure lines in a measure must be of the same type (i.e. all guitar measure lines or all drum measure lines)",
	                1,
	                this.getLinePositions()
	        );
	        if (ERROR_SENSITIVITY>=error.getPriority())
	            result.add(error);
	    }
	
	    if (!lineSizeEqual) {
	        ValidationError error = new ValidationError(
	                "Unequal measure line lengths may lead to incorrect note durations.",
	                2,
	                this.getLinePositions()
	        );
	        if (ERROR_SENSITIVITY>=error.getPriority())
	            result.add(error);
	    }
	    return result;
	}

	@Override
	public String toString() {
	    StringBuilder stringOut = new StringBuilder();
	    if (TimeSignature.isValid(this.beatCount, this.beatType))
	        stringOut.append(this.beatCount+"/"+this.beatType+"\n");
	    for (int i=0; i<this.tabStringList.size()-1; i++) {
	        TabString measureLine = this.tabStringList.get(i);
	        stringOut.append(measureLine.name);
	        stringOut.append("|");
	        stringOut.append(measureLine.recreateLineString(getMaxMeasureLineLength()));
	        stringOut.append("\n");
	    }
	    if (!this.tabStringList.isEmpty()) {
	        TabString measureLine = this.tabStringList.get(this.tabStringList.size()-1);
	        stringOut.append(measureLine.name);
	        stringOut.append("|");
	        stringOut.append(measureLine.recreateLineString(getMaxMeasureLineLength()));
	        stringOut.append("\n");
	    }
	
	    return stringOut.toString();
	}
}