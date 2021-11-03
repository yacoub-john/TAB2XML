package converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import converter.measure.TabMeasure;
import converter.measure_line.TabDrumString;
import converter.note.TabNote;
import custom_exceptions.InvalidScoreTypeException;
import custom_exceptions.MixedScoreTypeException;
import custom_exceptions.TXMLException;
import models.Creator;
import models.Identification;
import models.Part;
import models.ScorePartwise;
import models.part_list.MIDIInstrument;
import models.part_list.PartList;
import models.part_list.ScoreInstrument;
import models.part_list.ScorePart;
import utility.DrumPiece;
import utility.DrumPieceInfo;
import utility.DrumUtils;
import utility.Range;
import utility.Settings;
import utility.ValidationError;

public class Score extends ScoreComponent {

	public static String tabText;
	private Map<Integer, String> scoreTextFragments;
	private List<TabSection> tabSectionList;
    public static int CRITICAL_ERROR_CUTOFF = 1;
    private List<TabMeasure> tabMeasureList;

    public Score(String textInput) {
    	TabMeasure.MEASURE_INDEX = 0;
    	DrumUtils.createDrumSet();
    	DrumUtils.createDrumNickNames();
        
    	tabText = textInput;
        scoreTextFragments = getScoreTextFragments(tabText);
        tabSectionList = createTabSectionList(scoreTextFragments);
        tabMeasureList = createMeasureList();
        applyTimeSignatureUntilNextChange();
        setDivisions();
        createTiedNotes();
    }

    /**
     * Breaks input text (at wherever it finds blank lines) up into smaller pieces to make further analysis of each
     * piece of text with regex more efficient
     * @param input the string which is to be broken up into its fragments
     * @return an ordered map mapping the position of each broken up piece of text(Integer[startIndex, endIndex]) to the
     * actual piece of text (String)
     */
    public LinkedHashMap<Integer, String> getScoreTextFragments(String input) {
        LinkedHashMap<Integer, String> inputFragments = new LinkedHashMap<>();

        // Finding the point where there is a break between two pieces of text.
        // (i.e. a newline, then a blank line(a line containing nothing or just whitespace) then another newline
        // is considered to be where there is a break between two pieces of text)
        Pattern textBreakPattern = Pattern.compile("((\\R|^)[ ]*(?=\\R)){2,}|^|$");
        Matcher textBreakMatcher = textBreakPattern.matcher(input);

        Integer previousTextBreakEnd = null;
        while(textBreakMatcher.find()) {
            if (previousTextBreakEnd==null) {
                previousTextBreakEnd = textBreakMatcher.end();
                continue;
            }

            int paragraphStart = previousTextBreakEnd;
            int paragraphEnd = textBreakMatcher.start();
            String fragment = tabText.substring(previousTextBreakEnd,paragraphEnd);
            if (!fragment.strip().isEmpty()) {
                inputFragments.put(paragraphStart, fragment);
            }
            previousTextBreakEnd = textBreakMatcher.end();
        }
        return inputFragments;
    }
    
    /**
     * Creates a List of TabSection objects from the extracted fragments of a String.
     * These TabSection objects are not guaranteed to be valid. You can find out if all the TabSection
     * objects in this score are actually valid by calling the Score().validate() method.
     * @param stringFragments A Map which maps an Integer to a String, where the String is the broken up fragments of a
     *                        piece of text, and the Integer is the starting index at which the fragment starts in the
     *                        original text from which the fragments were derived.
     * @return a list of TabSection objects.
     */
    private List<TabSection> createTabSectionList(Map<Integer, String> stringFragments) {
        List<TabSection> tabSectionList = new ArrayList<>();
        boolean isFirstCollection = true;
        for (Map.Entry<Integer, String> fragment : stringFragments.entrySet()) {
			Matcher matcher = Pattern.compile(TabSection.PATTERN).matcher(fragment.getValue());
			while (matcher.find())
				tabSectionList.add(new TabSection(matcher.group(), fragment.getKey() + matcher.start(), isFirstCollection));
			isFirstCollection = false;
        }
        return tabSectionList;
    }

    //TODO Update to use the attribute measures
    private void applyTimeSignatureUntilNextChange() {
	    int currBeatCount = Settings.getInstance().tsNum;
	    int currBeatType = Settings.getInstance().tsDen;
	    for (TabSection tabSection : tabSectionList) {
	        for (TabRow tabRow : tabSection.getTabRowList()) {
	            for (TabMeasure measure : tabRow.getMeasureList()) {
	                if (measure.changesTimeSignature) {
	                    currBeatCount = measure.getBeatCount();
	                    currBeatType = measure.getBeatType();
	                    //continue;
	                }
	                measure.setTimeSignature(currBeatCount, currBeatType);
	            }
	        }
	    }
	}

  
	private void setDivisions() {
//	    int divisions = 0;
//	    for (TabSection tabSection : this.tabSectionList) {
//	        divisions = Math.max(divisions,  tabSection.setDivisions());
//	    }
	    for (TabMeasure tabMeasure : this.tabMeasureList) {
	        tabMeasure.setDivisions();
	    }
	}

    private void createTiedNotes() {
	boolean noSplit = false;
	while (!noSplit) {
		noSplit = true;
		for (TabMeasure m: tabMeasureList) {	
			if (m.createTiedNotes()) noSplit = false;
		}
	}
}

	public String detectedInstrument() {
		String inst = "None";
		if (isGuitar(false)) inst = "Guitar";
		if (isBass(false)) inst = "Bass";
		if (isDrum(false)) inst = "Drums";
		return inst;
	}

	public List<TabSection> getTabSectionList() {
        return this.tabSectionList;
    }
    
    public List<TabMeasure> getMeasureList() {
        return this.tabMeasureList;
    }
    
    public TabMeasure getMeasure(int measureCount) {
//        for (TabSection mCol : this.getTabSectionList()) {
//            for (TabRow mGroup : mCol.getTabRowList()) {
//                for (TabMeasure measure : mGroup.getMeasureList()) {
//                    int count = measure.getCount();
//                    if (count==measureCount)
//                        return measure;
//                    if (count > measureCount)
//                        return null;
//                }
//            }
//        }
//        return null;
    	
        try {
			return tabMeasureList.get(measureCount);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
    }

    private List<TabMeasure> createMeasureList() {
        List<TabMeasure> measureList = new ArrayList<>();
        for (TabSection tabSection : this.tabSectionList) {
            for (TabRow tabRow : tabSection.getTabRowList()) {
                measureList.addAll(tabRow.getMeasureList());
            }
        }
        return measureList;
    }

//    private int getErrorLevel() {
//        List<ValidationError> errors = this.validate();
//        if (errors.isEmpty()) return 0;
//        int errorLevel = 10;    //random large number (larger than any error priority)
//        for (ValidationError error : errors) {
//            int priority = error.getPriority();
//            errorLevel = Math.min(errorLevel, priority);
//        }
//        return errorLevel;
//    }



    private PartList getDrumPartList() {
        List<ScorePart> scoreParts = new ArrayList<>();
        ScorePart scorePart = new ScorePart("P1", "Drumset");
        List<ScoreInstrument> scoreInstruments = new ArrayList<>();
        List<MIDIInstrument> midiInstruments = new ArrayList<>();
        
        for (DrumPiece d : TabDrumString.USED_DRUM_PARTS) {
        	DrumPieceInfo drumPieceInfo = DrumUtils.drumSet.get(d);
        	String partID = drumPieceInfo.getMidiID();
            scoreInstruments.add(new ScoreInstrument(partID, drumPieceInfo.getFullName()));
            // Assumption: partID is of the form P1-IXX, where XX are digits
            int pitch = Integer.parseInt(partID.substring(4, 6));
            midiInstruments.add(new MIDIInstrument(partID, pitch));
        }
        scorePart.setScoreInstruments(scoreInstruments);
        scorePart.setMIDIInstruments(midiInstruments);
        scoreParts.add(scorePart);

        return new PartList(scoreParts);
    }

    private PartList getGuitarPartList() {
        List<ScorePart> scoreParts = new ArrayList<>();
        scoreParts.add(new ScorePart("P1", "Classical Guitar"));
        return new PartList(scoreParts);
    }

    private PartList getBassPartList() {
        List<ScorePart> scoreParts = new ArrayList<>();
        scoreParts.add(new ScorePart("P1", "Bass"));
        return new PartList(scoreParts);
    }
    
    
  //TODO Update to use the attribute measures
    private boolean isGuitar(boolean strictCheck) {
        if (!strictCheck && Settings.getInstance().instrument != Instrument.AUTO) {
            return Settings.getInstance().instrument == Instrument.GUITAR;
        }
        for (TabSection msurCollection : this.tabSectionList) {
            if (!msurCollection.isGuitar(strictCheck))
                return false;
        }
        return true;
    }

    private boolean isDrum(boolean strictCheck) {
        if (!strictCheck && Settings.getInstance().instrument != Instrument.AUTO) {
            return Settings.getInstance().instrument == Instrument.DRUMS;
        }
        for (TabSection tabSection : this.tabSectionList) {
            if (!tabSection.isDrum(strictCheck))
                return false;
        }
        return true;
    }

    private boolean isBass(boolean strictCheck) {
        if (!strictCheck && Settings.getInstance().instrument != Instrument.AUTO) {
            return Settings.getInstance().instrument == Instrument.BASS;
        }
        for (TabSection msurCollection : this.tabSectionList) {
            if (!msurCollection.isBass(strictCheck))
                return false;
        }
        return true;
    }

//    private int lastReturn(int position) {
//    	return tabText.substring(0,position).lastIndexOf("\n");
//    }
    
    // TODO synchronized because the ScorePartwise model has an instance counter which has to remain the same for all
	// which has to remain the same for all the sub-elements it has as they use that counter. this may turn out to be
	// a bad idea cuz it might clash with the NotePlayer class
	synchronized public ScorePartwise getModel() throws TXMLException {
	    boolean isGuitar = false;
	    boolean isDrum = false;
	    boolean isBass = false;
	
	    if (Settings.getInstance().instrument ==Instrument.GUITAR)
	        isGuitar = true;
	    else if (Settings.getInstance().instrument ==Instrument.DRUMS)
	        isDrum = true;
	    else if (Settings.getInstance().instrument ==Instrument.BASS)
	        isBass = true;
	    else {
	        isGuitar = this.isGuitar(false);
	        isDrum = this.isDrum(false);
	        isBass = this.isBass(false);
	        if (isDrum && isGuitar) {
	            isDrum = this.isDrum(true);
	            isGuitar = this.isGuitar(true);
	        }
	        if (Settings.getInstance().instrument == Instrument.AUTO && ((isDrum && isGuitar)||(isDrum && isBass)))
	            throw new MixedScoreTypeException("Multi-instrument tablatures are not supported at this time.");
	        if (!isDrum && !isGuitar && !isBass)
	            throw new InvalidScoreTypeException("The instrument for this tablature could not be detected. Please specify in Settings -> Current Song Settings.");
	    }
	
	    List<models.measure.Measure> measures = new ArrayList<>();
//	    for (TabSection tabSection : this.tabSectionList) {
//	        measures.addAll(tabSection.getMeasureModels());
//	    }
	    for (TabMeasure tabMeasure : this.tabMeasureList) {
	        measures.add(tabMeasure.getModel());
	    }
	    Part part = new Part("P1", measures);
	    List<Part> parts = new ArrayList<>();
	    parts.add(part);
	
	    PartList partList;
	    if (isDrum)
	        partList = this.getDrumPartList();
	    else if (isGuitar)
	        partList = this.getGuitarPartList();
	    else
	        partList = this.getBassPartList();
	
	    ScorePartwise scorePartwise = new ScorePartwise("3.1", partList, parts);
	   // if (this.title!=null && !this.title.isBlank())
	        scorePartwise.setMovementTitle(Settings.getInstance().title);
	   // if (this.artist!=null && !this.artist.isBlank())
	        scorePartwise.setIdentification(new Identification(new Creator("composer", Settings.getInstance().artist)));
	    return scorePartwise;
	}

	@Override
	public List<Range> getRanges() {
		// TODO Auto-generated method stub
		return null;
	}

	/** TODO modify this javadoc to reflect the new validation paradigm
	 * Ensures that all the lines of the root string (the whole tablature file) is understood as multiple measure collections,
	 * and if so, it validates all MeasureCollection objects it aggregates. It stops evaluation at the first aggregated object which fails validation.
	 * TODO fix the logic. One rootString fragment could contain what is identified as multiple measures (maybe?) and another could be misunderstood so they cancel out and validation passes when it shouldn't
	 * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
	 * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
	 * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
	 * found in the root string from which it was derived (i.e Score.ROOT_STRING).
	 * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
	 */
	
	public List<ValidationError> validate() {
	    
	    int prevEndIdx = 0;
	    ArrayList<Range> positions = new ArrayList<>();
	    for (TabSection msurCollction : this.tabSectionList) {
	    	String uninterpretedFragment = tabText.substring(prevEndIdx,msurCollction.position);
	    	if (!uninterpretedFragment.isBlank()) {
	    		positions.add(new Range(prevEndIdx, prevEndIdx+uninterpretedFragment.length()));
	    	}
	    	prevEndIdx = msurCollction.endIndex;
	    }
	
	    String restOfDocument = tabText.substring(prevEndIdx);
	    if (!restOfDocument.isBlank()) {
	        positions.add(new Range(prevEndIdx, prevEndIdx+restOfDocument.length()));
	    }
	
	    if (!positions.isEmpty()) {
	        addError("This text can't be understood.", 4, positions);
	        
	    }
	
	    //--------------Validate your aggregates (regardless of if you're valid, as there is no significant validation performed upon yourself that preclude your aggregates from being valid)-------------------
	    for (TabSection colctn : this.tabSectionList) {
	        errors.addAll(colctn.validate());
	    }
	
	    return errors;
	}

	@Override
    public String toString() {
        String outStr = "";
        for (TabSection measureCollection : this.tabSectionList) {
            outStr += measureCollection.toString();
            outStr += "\n\n";
        }
        return outStr;
    }

}
