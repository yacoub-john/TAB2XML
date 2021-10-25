package converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import converter.measure.TabMeasure;
import converter.measure_line.TabDrumString;
import custom_exceptions.InvalidScoreTypeException;
import custom_exceptions.MixedScoreTypeException;
import custom_exceptions.TXMLException;
import models.Creator;
import models.Identification;
import models.Part;
import models.ScorePartwise;
import models.part_list.PartList;
import models.part_list.ScoreInstrument;
import models.part_list.ScorePart;
import utility.DrumUtils;
import utility.Settings;
import utility.ValidationError;

public class Score implements ScoreComponent {
    private List<TabSection> tabSectionList;
    // Score.tabText is only public for the JUnit tester to work. Its access modifier should be protected so that
    // it will not be changed by anything outside the converter package as the "position" instance variable of other
    // classes in this package (e.g TabString) shows the position of the measure line in this String, thus they depend
    // on this String staying the same. It cannot be final as we will want to create different Score objects to convert
    // different Strings.
    public static String tabText;
    private Map<Integer, String> scoreTextFragments;
    public static Instrument INSTRUMENT_MODE = Instrument.AUTO;
    public String instrumentType;
    //public static int GLOBAL_DIVISIONS = 1;
    public static int CRITICAL_ERROR_CUTOFF = 1;
    public String title;
    public String artist;

    public Score(String textInput) {
    	TabMeasure.GLOBAL_MEASURE_COUNT = 0;
        TabMeasure.PREV_MEASURE_TYPE = Instrument.AUTO;
    	tabText = textInput;
        scoreTextFragments = getScoreTextFragments(tabText);
        tabSectionList = createTabSectionList(scoreTextFragments);
        applyTimeSignatureUntilNextChange();
        //GLOBAL_DIVISIONS = 
        setDivisions();
        //setDurations();
        if (INSTRUMENT_MODE == Instrument.AUTO) {
            boolean isGuitar = this.isGuitar(false);
            boolean isDrum = this.isDrum(false);
            boolean isBass = this.isBass(false);
            if (!isBass && !isGuitar && !isDrum)
                this.instrumentType = "invalid";
            if ((isBass && isGuitar) || (isGuitar && isDrum) || (isBass && isDrum))
                this.instrumentType = "mixed";
            else if (isGuitar) this.instrumentType = Instrument.GUITAR.name();
            else if (isDrum) this.instrumentType = Instrument.DRUM.name();
            else if (isBass) this.instrumentType = Instrument.BASS.name();
        }else {
            this.instrumentType = INSTRUMENT_MODE.name();
        }
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

	public int setDivisions() {
	    int divisions = 0;
	    for (TabSection msurCollection : this.tabSectionList) {
	        divisions = Math.max(divisions,  msurCollection.setDivisions());
	    }
	
	    return divisions;
	}

//	public void setDurations() {
//	    for (TabSection msurCollection : this.tabSectionList) {
//	        msurCollection.setDurations();
//	    }
//	}

	public static void setInstrumentMode(Instrument InstrumentMode) {
        INSTRUMENT_MODE = InstrumentMode;
    }

    public TabMeasure getMeasure(int measureCount) {
        for (TabSection mCol : this.getTabSectionList()) {
            for (TabRow mGroup : mCol.getTabRowList()) {
                for (TabMeasure measure : mGroup.getMeasureList()) {
                    int count = measure.getCount();
                    if (count==measureCount)
                        return measure;
                    if (count > measureCount)
                        return null;
                }
            }
        }
        return null;
    }

    public List<TabMeasure> getMeasureList() {
        List<TabMeasure> measureList = new ArrayList<>();
        for (TabSection mCol : this.tabSectionList) {
            for (TabRow mGroup : mCol.getTabRowList()) {
                measureList.addAll(mGroup.getMeasureList());
            }
        }
        return measureList;
    }

    public int getErrorLevel() {
        List<ValidationError> errors = this.validate();
        if (errors.isEmpty()) return 0;
        int errorLevel = 10;    //random large number (larger than any error priority)
        for (ValidationError error : errors) {
            int priority = error.getPriority();
            errorLevel = Math.min(errorLevel, priority);
        }
        return errorLevel;
    }

    public List<TabSection> getTabSectionList() {
        return this.tabSectionList;
    }

    private PartList getDrumPartList() {
        List<ScorePart> scoreParts = new ArrayList<>();
        ScorePart scorePart = new ScorePart("P1", "Drumset");
        List<ScoreInstrument> scoreInstruments = new ArrayList<>();
        for (String partID : TabDrumString.USED_DRUM_PARTS) {
            scoreInstruments.add(new ScoreInstrument(partID, DrumUtils.getFullName(partID)));
        }
        scorePart.setScoreInstruments(scoreInstruments);
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

    public boolean isGuitar(boolean strictCheck) {
        if (!strictCheck && Score.INSTRUMENT_MODE != Instrument.AUTO) {
            return Score.INSTRUMENT_MODE == Instrument.GUITAR;
        }
        for (TabSection msurCollection : this.tabSectionList) {
            if (!msurCollection.isGuitar(strictCheck))
                return false;
        }
        return true;
    }

    public boolean isDrum(boolean strictCheck) {
        if (!strictCheck && Score.INSTRUMENT_MODE != Instrument.AUTO) {
            return Score.INSTRUMENT_MODE == Instrument.DRUM;
        }
        for (TabSection msurCollection : this.tabSectionList) {
            if (!msurCollection.isDrum(strictCheck))
                return false;
        }
        return true;
    }

    public boolean isBass(boolean strictCheck) {
        if (!strictCheck && Score.INSTRUMENT_MODE != Instrument.AUTO) {
            return Score.INSTRUMENT_MODE == Instrument.BASS;
        }
        for (TabSection msurCollection : this.tabSectionList) {
            if (!msurCollection.isBass(strictCheck))
                return false;
        }
        return true;
    }

    public int lastReturn(int position) {
    	return tabText.substring(0,position).lastIndexOf("\n");
    }
    
    // TODO synchronized because the ScorePartwise model has an instance counter which has to remain the same for all
	// which has to remain the same for all the sub-elements it has as they use that counter. this may turn out to be
	// a bad idea cuz it might clash with the NotePlayer class
	synchronized public ScorePartwise getModel() throws TXMLException {
	    boolean isGuitar = false;
	    boolean isDrum = false;
	    boolean isBass = false;
	
	    if (INSTRUMENT_MODE ==Instrument.GUITAR)
	        isGuitar = true;
	    else if (INSTRUMENT_MODE ==Instrument.DRUM)
	        isDrum = true;
	    else if (INSTRUMENT_MODE ==Instrument.BASS)
	        isBass = true;
	    else {
	        isGuitar = this.isGuitar(false);
	        isDrum = this.isDrum(false);
	        isBass = this.isBass(false);
	        if (isDrum && isGuitar) {
	            isDrum = this.isDrum(true);
	            isGuitar = this.isGuitar(true);
	        }
	        if (INSTRUMENT_MODE == Instrument.AUTO && ((isDrum && isGuitar)||(isDrum && isBass)))
	            throw new MixedScoreTypeException("A score must be only of one type");
	        if (!isDrum && !isGuitar && !isBass)
	            throw new InvalidScoreTypeException("The type of this score could not be detected. Specify its type or fix the error in the text input.");
	    }
	
	    List<models.measure.Measure> measures = new ArrayList<>();
	    for (TabSection measureCollection : this.tabSectionList) {
	        measures.addAll(measureCollection.getMeasureModels());
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
	    if (this.title!=null && !this.title.isBlank())
	        scorePartwise.setMovementTitle(this.title);
	    if (this.artist!=null && !this.artist.isBlank())
	        scorePartwise.setIdentification(new Identification(new Creator("composer", this.artist)));
	    return scorePartwise;
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
	    List<ValidationError> result = new ArrayList<>();
	
	    int prevEndIdx = 0;
	    ArrayList<Integer[]> positions = new ArrayList<>();
	    for (TabSection msurCollction : this.tabSectionList) {
	    	String uninterpretedFragment = tabText.substring(prevEndIdx,msurCollction.position);
	    	if (!uninterpretedFragment.isBlank()) {
	    		positions.add(new Integer[]{prevEndIdx, prevEndIdx+uninterpretedFragment.length()});
	    	}
	    	prevEndIdx = msurCollction.endIndex;
	    }
	
	    String restOfDocument = tabText.substring(prevEndIdx);
	    if (!restOfDocument.isBlank()) {
	        positions.add(new Integer[]{prevEndIdx, prevEndIdx+restOfDocument.length()});
	    }
	
	    if (!positions.isEmpty()) {
	        ValidationError error = new ValidationError(
	                "This text can't be understood.",
	                4,
	                positions
	        );
	        int ERROR_SENSITIVITY = Settings.getInstance().errorSensitivity;
	        if (ERROR_SENSITIVITY>=error.getPriority())
	            result.add(error);
	    }
	
	    //--------------Validate your aggregates (regardless of if you're valid, as there is no significant validation performed upon yourself that preclude your aggregates from being valid)-------------------
	    for (TabSection colctn : this.tabSectionList) {
	        result.addAll(colctn.validate());
	    }
	
	    return result;
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
