package converter.measure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import converter.Score;
import converter.ScoreComponent;
import converter.instruction.RepeatType;
import converter.instruction.TimeSignature;
import converter.measure_line.TabDrumString;
import converter.measure_line.TabGuitarString;
import converter.measure_line.TabString;
import converter.note.NoteFactory;
import converter.note.TabNote;
import models.measure.Backup;
import models.measure.attributes.Attributes;
import models.measure.barline.BarLine;
import models.measure.barline.Repeat;
import models.measure.direction.Direction;
import models.measure.direction.DirectionType;
import models.measure.direction.Words;
import utility.AnchoredText;
import utility.Range;
import utility.Settings;
import utility.ValidationError;

public abstract class TabMeasure extends ScoreComponent {
    public static int MEASURE_INDEX;
    
    protected int measureCount;
    protected int beatCount = Settings.getInstance().tsNum;
    protected int beatType = Settings.getInstance().tsDen;
    List<AnchoredText> data;
    List<AnchoredText> nameData;
    public int lineCount;
    public List<TabString> tabStringList = new ArrayList<>();
    boolean isFirstMeasureInGroup;
    List<List<TabNote>> voiceSortedNoteList;   // a list of voices where each voice is a sorted list of notes

    boolean repeatStart = false;
    boolean repeatEnd = false;
    int repeatCount = 0;
    public boolean changesTimeSignature = false;
    protected int divisions;
    protected boolean split1 = false;
    protected boolean nonIntegerDivisions = false;
    protected boolean unSupportedDivisions = false;
    protected int[] supportedDivisions = {1,2,3,4,6,8,12,16,24,48};
    
    public TabMeasure(List<AnchoredText> inputData, List<AnchoredText> inputNameData, boolean isFirstMeasureInGroup) {
        this.measureCount = ++MEASURE_INDEX;
        this.data = inputData;
        this.nameData = inputNameData;
        this.lineCount = this.data.size();
        this.isFirstMeasureInGroup = isFirstMeasureInGroup;
        createTabStringList();
        this.voiceSortedNoteList = this.getVoiceSortedNoteList();
        setChords();
        setDurations();
    }

	/**
	 * Populates the tabStringList attribute
	 */
	protected void createTabStringList() {
		for (int i = 0; i < data.size(); i++) {
			tabStringList.add(newTabString(i + 1, data.get(i), nameData.get(i)));
		}
	}

    /**
     * Abstract method to create the corresponding type of TabString
     * @param line the contents of the TabString
     * @param nameAndPosition the name of the TabString
     * @param position  the index at which the contents of the measure line can be found in the root string from which it
     *                 was derived (i.e Score.tabText)
     * @return a TabString either of type TabGuitarString, TabBassString, or TabDrumString
     */
    protected abstract TabString newTabString(int stringNumber, AnchoredText data, AnchoredText name);
    
//    protected void setChords() {
//    	
//	    for (List<TabNote> voice : this.voiceSortedNoteList) {
//	    	if (voice.size() == 0) continue;
//	    	List<TabNote> graceChord = new ArrayList<>();
//	        List<TabNote> chord = new ArrayList<>();
//	        TabNote previousNote = voice.get(0);
//	        if (previousNote.isGrace) graceChord.add(previousNote);
//            else chord.add(previousNote);
//	        
//	        for (int i = 1; i < voice.size(); i++) {
//	        	TabNote currentNote = voice.get(i);
//	        	if (previousNote.distance == currentNote.distance) {
//		            if (currentNote.isGrace) graceChord.add(currentNote);
//		            else chord.add(currentNote);
//	        	}
//	        	else {
//	        		// Create the chords
//	        		
//	        		graceChord = new ArrayList<>();
//		        	chord = new ArrayList<>();
//		        	if (currentNote.isGrace) graceChord.add(currentNote);
//		            else chord.add(currentNote);
//	        	}
//	        		
////	            if (previousNote != null && previousNote.distance == currentNote.distance)
////	                currentNote.startsWithPreviousNote = true;
//	            previousNote = currentNote;
//	        }
//	    }
//	}

    protected void setChords() {
	    for (List<TabNote> voice : this.voiceSortedNoteList) {
	        TabNote previousNote = null;
	        for (TabNote currentNote : voice) {
	            if (currentNote.isGrace) continue;
	            if (previousNote != null && previousNote.distance == currentNote.distance)
	                currentNote.startsWithPreviousNote = true;
	            previousNote = currentNote;
	        }
	    }
	}
    
	protected void setDurations() {
		for (List<List<TabNote>> chordList : getVoiceSortedChordList()) {
			// Handle all but last chord
			for (int i = 0; i < chordList.size() - 1; i++) {
			    List<TabNote> chord = chordList.get(i);
			    List<TabNote> nextChord = chordList.get(i+1);
			    int currentChordDistance = chord.get(0).distance;
			    int nextChordDistance = nextChord.get(0).distance;
			
			    int duration = nextChordDistance-currentChordDistance;
			    duration = adjustDurationForSpecialCases(duration, chord, nextChord);
			    
			    for (TabNote note : chord) {			    	
			        note.setDuration(duration);
			    }
			}
			// Handle last chord, as it is a special case (it has no next chord)
			if (!chordList.isEmpty()) {
			    List<TabNote> chord = chordList.get(chordList.size()-1);
			    int currentChordDistance = chord.get(0).distance;
			
			    int duration = getMaxMeasureLineLength() - currentChordDistance;
			    duration = adjustDurationForSpecialCases(duration, chord, null);
			    
			    for (TabNote note : chord) {			    	
			        note.setDuration(duration);
			    }
			}
        }
    }
    
    protected abstract int adjustDurationForSpecialCases(int duration, List<TabNote> chord, List<TabNote> nextChord);
    
	protected boolean isDoubleDigit(List<TabNote> chord) {
    	boolean doubleDigit = false;
    	for (TabNote note : chord) {
	    	if (note.origin.length() == 2) doubleDigit = true;
	    }
		return doubleDigit;
	}
    
    public List<List<List<TabNote>>> getVoiceSortedChordList() {
        List<List<List<TabNote>>> voiceSortedChordList = new ArrayList<>();
        for (List<TabNote> voice : this.voiceSortedNoteList) {
            List<List<TabNote>> voiceChordList = new ArrayList<>();
            List<TabNote> currentChord = new ArrayList<>();
            for (TabNote note : voice) {
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

    public int getDivisions() {
    	return divisions;
    }
    
	public void setDivisions() {
		int measureLength = getMaxMeasureLineLength();
		if (voiceSortedNoteList.size() == 0)
			divisions = measureLength;
		else {
			int firstNotePosition = voiceSortedNoteList.get(0).get(0).distance;
			int usefulMeasureLength = measureLength - firstNotePosition;
			// Must subtract for double digit numbers
			usefulMeasureLength = adjustDivisionsForDoubleCharacterNotes(usefulMeasureLength);
			// For beatType 2, we double duration and divisions to avoid
			// having divisions be a .5
			int beatTypeFactor = beatType == 2 ? 2 : 1;

			// We do not divide by beatCount, to avoid fractional divisions
			// All durations are multiplied by beatCount to compensate
			divisions = usefulMeasureLength * beatType * beatTypeFactor / 4;
			
			int adjustment = divisions % beatCount;
			if (divisions > adjustment) divisions -= adjustment;

			if (adjustment > 0) {
				System.out.println("Measure " + measureCount + ": Length of measure not good for divisions. Adjusted from " + (divisions + adjustment) + " to " + divisions);
				nonIntegerDivisions = true;
			}
			if (!Arrays.stream(supportedDivisions).anyMatch(i -> i == divisions / beatCount)) {
				System.out.println("Measure " + measureCount + ": Unsupported divisions: " + divisions);
				unSupportedDivisions = true;
			}

			for (List<TabNote> voice : this.voiceSortedNoteList) {
				for (TabNote note : voice) {
					note.setDivisions(this.divisions);
					int factor = beatCount;
					// For beatType 2, we double duration and divisions to avoid
			    	// having divisions be a .5
			    	if (beatType == 2) factor = 2;
			    	// We multiply by beatCount to compensate for divisions 
			    	// multiplied by beatCount also
			    	note.setDuration(note.getDuration() * factor); 
				}
			}
		}
	}
    
    protected abstract int adjustDivisionsForDoubleCharacterNotes(int usefulMeasureLength);

	public boolean createTiedNotes() {
    	boolean somethingToSplit = false;
    	List<List<TabNote>> newNoteList = new ArrayList<>();
    	for (List<List<TabNote>> voice: getVoiceSortedChordList()) {
    		List<TabNote> newVoice = new ArrayList<>();
            newNoteList.add(newVoice);
			int totalDuration = 0;
			for (List<TabNote> chord: voice) {
				if ((chord.get(0).mustSplit) && (chord.get(0).duration > 1)){
					somethingToSplit = true;
					int note1dur = firstNoteDuration(totalDuration, chord.get(0).duration, getDivisions(), getBeatType());
					int note2dur = chord.get(0).duration - note1dur;
					List<TabNote> newChord = new ArrayList<>();
					for (TabNote n : chord) {
						n.setDuration(note1dur);
						newVoice.add(n);
						TabNote newNote = n.copy();
						newNote.setDuration(note2dur);
						new NoteFactory().tie(n,newNote);
						newChord.add(newNote);
					}
					for (TabNote n : newChord) {
						newVoice.add(n);
					}
					totalDuration += note1dur + note2dur;
				}
				else {
					if ((chord.get(0).mustSplit) && (chord.get(0).duration <= 1)){
						split1 = true;
					}
				totalDuration += chord.get(0).duration;
				for (TabNote n : chord) {
					n.mustSplit = false;
					newVoice.add(n);
				}
				}
			}
			
		}
    	this.voiceSortedNoteList = newNoteList;
    	return somethingToSplit;
    }
    
	private int firstNoteDuration(int totalDuration, int duration, int divisions, int den) {
		int firstNote, secondNote = 0;
		int beatDuration = divisions * 4 / den;;
		do {
			
			int beats = totalDuration / beatDuration;
			int offSet = totalDuration - beats * beatDuration;
			firstNote = beatDuration - offSet;
			// TODO Assert cannot be 0
			secondNote = duration - firstNote;
			beatDuration = beatDuration / 2;
			// TODO Assert it does not get to 0
		} while ((firstNote < 1) || (secondNote < 1));
		return firstNote;
	}
    
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
        for (List<TabNote> voice : this.voiceSortedNoteList) {
            for (TabNote note : voice) {
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

    public List<List<TabNote>> getVoiceSortedNoteList() {
        List<List<TabNote>> voiceSortedNoteList = new ArrayList<>();
        HashMap<Integer, Integer> voiceToIndexMap = new HashMap<>();
        int currentIdx = 0;
        for (TabNote note : this.getSortedNoteList()) {
            if (!voiceToIndexMap.containsKey(note.voice)) {
                voiceToIndexMap.put(note.voice, currentIdx++);
                voiceSortedNoteList.add(new ArrayList<>());
            }
            int idx = voiceToIndexMap.get(note.voice);
            List<TabNote> voice = voiceSortedNoteList.get(idx);
            voice.add(note);
        }
        return voiceSortedNoteList;
    }

    public List<TabNote> getSortedNoteList() {
        List<TabNote> sortedNoteList = new ArrayList<>();
        for (TabString tabString : this.tabStringList) {
            sortedNoteList.addAll(tabString.getNoteList());
        }
        Collections.sort(sortedNoteList);
        return sortedNoteList;
    }

//    public boolean isGuitar(boolean strictCheck) {
//        for (TabString measureLine : this.tabStringList) {
//            if (!measureLine.isGuitar(strictCheck))
//                return false;
//        }
//        return true;
//    }
//
//    public boolean isDrum(boolean strictCheck) {
//        for (TabString measureLine : this.tabStringList) {
//            if (!measureLine.isDrum(strictCheck))
//                return false;
//        }
//        return true;
//    }
//    public boolean isBass(boolean strictCheck) {
//        for (TabString measureLine : this.tabStringList) {
//            if (!measureLine.isGuitar(strictCheck))
//                return false;
//        }
//        return this.tabStringList.size() >= BassMeasure.MIN_LINE_COUNT && this.tabStringList.size() <= BassMeasure.MAX_LINE_COUNT;
//    }

    /**
     * @return the range of the first line of the measure in its text line
     * If this is the first measure in the line, the range includes the line name
     */
    public Range getRelativeRange() {
        if (this.data.isEmpty()) return null;
        int relStartPos = data.get(0).positionInLine;
        if (this.isFirstMeasureInGroup)
        	relStartPos = nameData.get(0).positionInLine;
        int relEndPos = relStartPos + data.get(0).text.length();
        return new Range(relStartPos, relEndPos);
    }

    public int getCount() {
        return this.measureCount;
    }

	public int getBeatCount() {
	    return this.beatCount;
	}
	
	public int getBeatType() {
	    return this.beatType;
	}

	@Override
	public List<Range> getRanges() {
		List<Range> linePositions = new ArrayList<>();
		for (int i = 0; i < data.size(); i++) {
			int startIdx = data.get(i).positionInScore;
			int endIdx = startIdx + data.get(i).text.length();
			linePositions.add(new Range(startIdx, endIdx));
		}
		return linePositions;
	}

	protected abstract Attributes getAttributesModel();
	
    public models.measure.Measure getModel() {
        models.measure.Measure measureModel = new models.measure.Measure();
        measureModel.setNumber(this.measureCount);
        measureModel.setAttributes(this.getAttributesModel());

        List<models.measure.note.Note> noteBeforeBackupModels = new ArrayList<>();
        List<models.measure.note.Note> noteAfterBackupModels = new ArrayList<>();
        for (int i=0; i<this.voiceSortedNoteList.size(); i++) {
            List<TabNote> voice = this.voiceSortedNoteList.get(i);
            double backupDuration = 0;
            double currentChordDuration = 0;
            for (TabNote note : voice) {
            	if (note.getModel() != null) { // Invalid notes return null
            		if (note.voice==1)
            			noteBeforeBackupModels.add(note.getModel());
            		if (note.voice==2)
            			noteAfterBackupModels.add(note.getModel());
            		if (note.startsWithPreviousNote)
            			currentChordDuration = Math.max(currentChordDuration, note.duration);
            		else {
            			backupDuration += currentChordDuration;
            			currentChordDuration = note.duration;
            		}
            	}
            }
            backupDuration += currentChordDuration;
            if (voice.get(0).voice==1)
                measureModel.setNotesBeforeBackup(noteBeforeBackupModels);
            if (voice.get(0).voice==2)
                measureModel.setNotesAfterBackup(noteAfterBackupModels);
            if (i+1<this.voiceSortedNoteList.size()) {
                measureModel.setBackup(new Backup((int)backupDuration));
            }
        }

        List<BarLine> barLines = new ArrayList<>();
        if (this.isRepeatStart()) {
            BarLine barLine = new BarLine();
            barLines.add(barLine);
            barLine.setLocation("left");
            barLine.setBarStyle("heavy-light");

            Repeat repeat = new Repeat();
            repeat.setDirection("forward");
            barLine.setRepeat(repeat);

            Direction direction = new Direction();
            direction.setPlacement("above");
            measureModel.setDirection(direction);

            DirectionType directionType = new DirectionType();
            direction.setDirectionType(directionType);

            Words words = new Words();
            words.setRelativeX(256.17);
            words.setRelativeX(16.01);
            words.setRepeatText("Repeat "+this.repeatCount+" times");
            directionType.setWords(words);
        }

        if (this.isRepeatEnd()) {
            BarLine barLine = new BarLine();
            barLines.add(barLine);
            barLine.setLocation("right");
            barLine.setBarStyle("light-heavy");

            Repeat repeat = new Repeat();
            repeat.setDirection("backward");
            barLine.setRepeat(repeat);
        }

        if (!barLines.isEmpty())
            measureModel.setBarlines(barLines);
        return measureModel;
    }

	/**
	 * Validates that all TabString objects have the same length. It also validates that timing was able
	 * to be determined successfully. It does not validate its aggregated objects.
	 *  That job is left up to its concrete classes (this is an abstract class)
	 *  TODO See if that validation can happen here
	 */
	public List<ValidationError> validate() {
	
	    
	    if (unSupportedDivisions) {
	        addError(
	                "Could not determine timing correctly: Unsupported divisions",
	                1, getRanges());
	    }
	    if (nonIntegerDivisions) {
	        addError(
	                "Could not determine timing correctly: Non integer divisions",
	                1, getRanges());
	    }
	    if (split1) {
	        addError(
	                "Could not determine timing correctly: Had to split a duration of 1",
	                1, getRanges());
	    }
	    
	    boolean hasGuitarMeasureLines = true;
	    boolean hasDrumMeasureLines = true;
	    boolean lineSizeEqual = true;
	
	    int previousLineLength = -1;
	    for (TabString tabString : this.tabStringList) {
	        hasGuitarMeasureLines &= tabString instanceof TabGuitarString;
	        hasDrumMeasureLines &= tabString instanceof TabDrumString;
	
	        int currentLineLength = tabString.line.replace("\s", "").length();
	        lineSizeEqual &= (previousLineLength < 0) || previousLineLength == currentLineLength;
	        previousLineLength = currentLineLength;
	    }
	    
	    if (!(hasGuitarMeasureLines || hasDrumMeasureLines)) {
	        addError(
	                "All measure lines in a measure must be of the same type (i.e. all guitar measure lines or all drum measure lines)",
	                1, getRanges());
	    }
	
	    if (!lineSizeEqual) {
	        addError(
	                "Unequal measure line lengths may lead to incorrect note durations.",
	                2, getRanges());
	    }
	    return errors;
	}

//	@Override
//	public String toString() {
//	    StringBuilder stringOut = new StringBuilder();
//	    if (TimeSignature.isValid(this.beatCount, this.beatType))
//	        stringOut.append(this.beatCount+"/"+this.beatType+"\n");
//	    for (int i=0; i<this.tabStringList.size()-1; i++) {
//	        TabString measureLine = this.tabStringList.get(i);
//	        stringOut.append(measureLine.name);
//	        stringOut.append("|");
//	        stringOut.append(measureLine.recreateLineString(getMaxMeasureLineLength()));
//	        stringOut.append("\n");
//	    }
//	    if (!this.tabStringList.isEmpty()) {
//	        TabString measureLine = this.tabStringList.get(this.tabStringList.size()-1);
//	        stringOut.append(measureLine.name);
//	        stringOut.append("|");
//	        stringOut.append(measureLine.recreateLineString(getMaxMeasureLineLength()));
//	        stringOut.append("\n");
//	    }
//	
//	    return stringOut.toString();
//	}
}