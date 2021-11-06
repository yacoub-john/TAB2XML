package converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import converter.instruction.Instruction;
import converter.instruction.RepeatType;
import converter.measure.BassMeasure;
import converter.measure.DrumMeasure;
import converter.measure.GuitarMeasure;
import converter.measure.TabMeasure;
import utility.DrumUtils;
import utility.GuitarUtils;
import utility.Patterns;
import utility.Range;
import utility.Settings;
import utility.ValidationError;

public class TabRow extends ScoreComponent {

    public List<TabMeasure> tabMeasures;
	public List<String> lines = new ArrayList<>();
	public List<Integer> positions = new ArrayList<>();
    public List<Instruction> instructions = new ArrayList<>();
    
    /**
     * Creates a TabRow object from a List of Strings which represent the lines in the tablature row
     * @param origin a List<String> containing the lines which are meant to represent a tablature row. Each String in
     *              "origin" begins with a tag indicating the index at which it is positioned in the root string from
     *               which it was extracted (i.e Score.tabText). (i.e "[startIdx]stringContent" )
     *               "origin" is guaranteed to be a valid **representation** of a TabRow as it is only instantiated
     *               from the TabSection class, which has to be made up of measure groups before it can be constructed
     *               (look at TabSection.getInstance() and TabSection.PATTERN). However, though it is
     *               guaranteed to be a valid representation of a measure group(i.e it looks like a measure group), the
     *               measure group which it is representing is not guaranteed to be valid itself.
     */
    public TabRow(List<String> origin) {
    	
    	//Extract lines and positions, should really pass those separately
        for (String lineWithTag : origin) {
            Matcher tagMatcher = Pattern.compile("^\\[[0-9]+\\]").matcher(lineWithTag);
            tagMatcher.find();
            int startIdx = Integer.parseInt(tagMatcher.group().replaceAll("[\\[\\]]",""));
            String line = lineWithTag.substring(tagMatcher.end());

            this.positions.add(startIdx);
            this.lines.add(line);
        }
        
        tabMeasures = createTabMeasureList(lines, positions);
        
    }

    public TabRow() {}
	/**
     * Creates a List of TabMeasure objects from the provided string representation of a TabRow.
     * These TabMeasure objects are not guaranteed to be valid. You can find out if all the TabMeasure
     * objects in this TabRow are actually valid by calling the TabRow.validate() method.
     * @param tabRowLines A List (parallel with "positions") that contains the lines of the measure group which is to
     *                          be split into separate measures
     * @param positions A parallel List (parallel with "measureGroupLines") that contains the index at which the
     *                  corresponding measure group line in "measureGroupLines" can be found in the root string from which
     *                  it was derived (i.e Score.ROOT_STRING)
     * @return A List of Measures derived from their String representation, "measureGroupLines".
     */
    private List<TabMeasure> createTabMeasureList(List<String> tabRowLines, List<Integer> positions){
        
    	List<TabMeasure> measureList = new ArrayList<>();

        // Setting up three parallel arrays to store the information of each measure

        // 1. The text of each line
        List<List<String>> textList = new ArrayList<>();
        // 2. The position of each line in Score.tabText
        List<List<Integer>> positionsList = new ArrayList<>();
        // 3. The name of each line (string tuning or drum part)
        List<List<String[]>> namesList = new ArrayList<>();

        //Iterate over all the lines of the tabRow, e.g. all six for guitar
        for (int i=0; i < tabRowLines.size(); i++) {
            String currentLine = tabRowLines.get(i);
            int currentLineStartPos = positions.get(i);
            //Find the name at the beginning of a text line
            //Returns an array of two strings, first is the line name, next is the position of it (as a string)
            int offset = currentLineStartPos;
            String[] lineName = nameOf(currentLine, currentLineStartPos);
            currentLineStartPos += Integer.parseInt(lineName[1]);
            offset = Integer.parseInt(lineName[1]) - offset + lineName[0].length();
            if (Settings.getInstance().getInstrument() == Instrument.GUITAR && i < 6) {
            	if (lineName[0] == "") lineName[0] = Settings.getInstance().guitarTuning[i][0];    // Keep using what ever tuning was previously set if this is guitar
            	Settings.getInstance().guitarTuning[i][0] = lineName[0];  // Update tuning. Only likely to make a difference for the first measure
            }
            if (Settings.getInstance().getInstrument() == Instrument.BASS && i < 4) {
            	if (lineName[0] == "") lineName[0] = Settings.getInstance().bassTuning[i][0];    // Keep using what ever tuning was previously set if this is bass
            	Settings.getInstance().bassTuning[i][0] = lineName[0];  // Update tuning. Only likely to make a difference for the first measure
            }
            int measureCount = 0;
            
            Matcher measureInsidesMatcher = Pattern.compile(Patterns.insidesPattern()).matcher(currentLine.substring(offset));
            while (measureInsidesMatcher.find()) {
                measureCount++;
                String measureLineString = measureInsidesMatcher.group();
                int measurePosition = currentLineStartPos+measureInsidesMatcher.start();    //the starting position of the insides of this measure in the root string Score.tabText

                if (textList.size()<measureCount) {
                    textList.add(new ArrayList<>());
                    positionsList.add(new ArrayList<>());
                    namesList.add(new ArrayList<>());
                }

                // Get the particular measure we are interested in and add this line to its list of lines
                List<String> measureLines = textList.get(measureCount-1);  //-1 cuz of zero indexing
                List<Integer> measurePositions = positionsList.get(measureCount-1);
                List<String[]> measureNames = namesList.get(measureCount-1);
                measureLines.add(measureLineString);
                measurePositions.add(measurePosition);
                measureNames.add(lineName);
            }
        }
        boolean isFirstMeasureInGroup = true;
        //Iterate over all the extracted measures
        for (int i = 0; i < textList.size(); i++) {
            List<String> measureLineList = textList.get(i);
            List<Integer> measureLinePositionList = positionsList.get(i);
            List<String[]> measureLineNameList = namesList.get(i);

            measureList.add(from(measureLineList, measureLineNameList, measureLinePositionList, isFirstMeasureInGroup));
            isFirstMeasureInGroup = false;
        }
        return measureList;
    }

    /**
     * Creates an instance of the abstract Measure class whose concrete type is either GuitarMeasure or DrumMeasure, depending
     * on if the features of the input String Lists resemble a drum measure or a Guitar measure(this is determined by the
     * MeasureLine.isGuitar() and MeasureLine.isDrum() methods). If its features could not be deciphered or it has features
     * of both guitar and drum features, it defaults to creating a GuitarMeasure object and further error checking can
     * be done by calling GuitarMeasure().validate() on the object.
     * @param lineList A list of the insides of each measure lines that makes up this measure (without the line names) (parallel list with the other two List parameters)
     * @param lineNameList A list of the names of each the measure lines that makes up this measure (parallel list with the other two List parameters)
     * @param linePositionList A list of the positions of the insides of each of the measure lines that make up this (parallel list with the other two List parameters)
     *                         measure, where a line's position is the index at which the line is located in the root
     *                         String from which it was derived (Score.ROOT_STRING)
     * @param isFirstMeasureInGroup specifies weather this measure is the first one in its measure group. (useful to know, so we only add the xml measure attributes to the first measure)
     *
     * @return A Measure object which is either of type GuitarMeasure if the measure was understood to be a guitar
     * measure, or of type DrumMeasure if the measure was understood to be of type DrumMeasure
     */
    private TabMeasure from(List<String> lineList, List<String[]> lineNameList, List<Integer> linePositionList, boolean isFirstMeasureInGroup) {
        boolean repeatStart = checkRepeatStart(lineList);
        boolean repeatEnd = checkRepeatEnd(lineList);
        String repeatCountStr = extractRepeatCount(lineList);
        removeRepeatMarkings(lineList, linePositionList, repeatStart, repeatEnd, repeatCountStr);
        int repeatCount = 1;
        if (!repeatCountStr.isEmpty()) {
            Matcher numMatcher = Pattern.compile("(?<=\\])[0-9]+").matcher(repeatCountStr);
            numMatcher.find();
            repeatCountStr = numMatcher.group();
            repeatCount = Integer.parseInt(repeatCountStr);
        }

        TabMeasure measure = switch (Settings.getInstance().getInstrument()) {
        case GUITAR -> new GuitarMeasure(lineList, lineNameList, linePositionList, isFirstMeasureInGroup);
        case BASS -> new BassMeasure(lineList, lineNameList, linePositionList, isFirstMeasureInGroup);
        case DRUMS -> new DrumMeasure(lineList, lineNameList, linePositionList, isFirstMeasureInGroup);
        case NONE -> detectAndCreateMeasure(lineList, lineNameList, linePositionList, isFirstMeasureInGroup);
    };
    
        if (repeatStart)
            measure.setRepeat(repeatCount, RepeatType.START);
        if (repeatEnd)
            measure.setRepeat(repeatCount, RepeatType.END);
        return measure;
    }

    //TODO Should replace with exception
	private TabMeasure detectAndCreateMeasure(List<String> lineList, List<String[]> lineNameList, List<Integer> linePositionList, boolean isFirstMeasureInGroup) {
    	double guitarLikelihood = GuitarUtils.isGuitarMeasureLikelihood(lineList, lineNameList);
        double drumLikelihood = DrumUtils.isDrumMeasureLikelihood(lineList, lineNameList);
        //double bassLikelihood = GuitarUtils.isBassMeasureLikelihood(lineList, lineNameList);

        //adjusting values
//        double guitarLikelihoodAdj = guitarLikelihood*(1-FOLLOW_PREV_MEASURE_WEIGHT) + (PREV_MEASURE_TYPE==Instrument.GUITAR ? FOLLOW_PREV_MEASURE_WEIGHT : 0);
//        double drumLikelihoodAdj = drumLikelihood*(1-FOLLOW_PREV_MEASURE_WEIGHT) + (PREV_MEASURE_TYPE==Instrument.DRUMS ? FOLLOW_PREV_MEASURE_WEIGHT : 0);
//        double bassLikelihoodAdj = bassLikelihood*(1-FOLLOW_PREV_MEASURE_WEIGHT) + (PREV_MEASURE_TYPE==Instrument.BASS ? FOLLOW_PREV_MEASURE_WEIGHT : 0);

        double guitarLikelihoodAdj = guitarLikelihood;
        double drumLikelihoodAdj = drumLikelihood;
        //double bassLikelihoodAdj = bassLikelihood;
        
        TabMeasure measure;
        if (guitarLikelihoodAdj >= drumLikelihoodAdj) {
        	// If it's a 6-string bass, the user has to set it explicitly in Current Song Settings
        	if (lineList.size() >= 6)
        		measure = new GuitarMeasure(lineList, lineNameList, linePositionList, isFirstMeasureInGroup);
        	else
        		measure = new BassMeasure(lineList, lineNameList, linePositionList, isFirstMeasureInGroup);
//            PREV_MEASURE_TYPE = Instrument.GUITAR;
//            // the more confident we are about what type of measure this is, the more we want the next measure to be likely to follow it.
//            //dont use the guitarLikelihoodAdj "Adj" score to calculate confidence or else the effect will build on itself everytime we adjust the FOLLOW_PREV_MEASURE_WEIGHT value
//            double confidenceScore = guitarLikelihood-Math.min(Math.max(drumLikelihood, bassLikelihood), guitarLikelihood);

            //FOLLOW_PREV_MEASURE_WEIGHT = FOLLOW_PREV_MEASURE_WEIGHT * adjustRawConfidenceScore(confidenceScore);;
//        }else if (bassLikelihoodAdj >= drumLikelihoodAdj){
//            measure = new BassMeasure(lineList, lineNameList, linePositionList, isFirstMeasureInGroup);
//            PREV_MEASURE_TYPE = Instrument.BASS;
//            double confidenceScore = bassLikelihood-Math.min(Math.max(drumLikelihood, guitarLikelihood)*2, bassLikelihood);
//            //FOLLOW_PREV_MEASURE_WEIGHT = FOLLOW_PREV_MEASURE_WEIGHT * adjustRawConfidenceScore(confidenceScore);;
        }else {
            measure = new DrumMeasure(lineList, lineNameList, linePositionList, isFirstMeasureInGroup);
//            PREV_MEASURE_TYPE = Instrument.DRUMS;
//            double confidenceScore = drumLikelihood-Math.min(Math.max(bassLikelihood, guitarLikelihood)*2, drumLikelihood);
//            //FOLLOW_PREV_MEASURE_WEIGHT = FOLLOW_PREV_MEASURE_WEIGHT * adjustRawConfidenceScore(confidenceScore);;
        }
		return measure;
	}


	private static boolean checkRepeatStart(List<String> lines) {
        boolean repeatStart = true;
        int repeatStartMarkCount = 0;
        for (String line : lines) {
            repeatStart &= line.strip().startsWith("|");
            if (line.strip().startsWith("|*")) repeatStartMarkCount++;
        }
        repeatStart &= repeatStartMarkCount>=2;
        return repeatStart;
    }
    private static boolean checkRepeatEnd(List<String> lines) {
        boolean repeatEnd = true;
        int repeatEndMarkCount = 0;
        for (int i=1; i<lines.size(); i++) {
            String line = lines.get(i);
            repeatEnd &= line.strip().endsWith("|");
            if (line.strip().endsWith("*|")) repeatEndMarkCount++;
        }
        repeatEnd &= repeatEndMarkCount>=2;
        return repeatEnd;
    }
    private static String extractRepeatCount(List<String> lines) {
        if (!checkRepeatEnd(lines)) return "";
        Matcher numMatcher = Pattern.compile("(?<=[^0-9])[0-9]+(?=[ ]|"+ Patterns.DIVIDER+"|$)").matcher(lines.get(0));
        if (!numMatcher.find()) return "";
        return "["+numMatcher.start()+"]"+numMatcher.group();
    }

    private static void removeRepeatMarkings(List<String> lines, List<Integer> linePositions, boolean repeatStart, boolean repeatEnd, String repeatCountStr) {
        if (!repeatCountStr.isEmpty()){
            Matcher posMatcher = Pattern.compile("(?<=\\[)[0-9]+(?=\\])").matcher(repeatCountStr);
            Matcher numMatcher = Pattern.compile("(?<=\\])[0-9]+").matcher(repeatCountStr);
            posMatcher.find();
            numMatcher.find();
            int position = Integer.parseInt(posMatcher.group());
            int numLen = numMatcher.group().length();
            String line = lines.get(0);
            line = line.substring(0, position)+"-".repeat(Math.max(numLen-1, 0))+line.substring(position+numLen);
            //remove extra - which overlaps with the |'s
            /*
            -----------4|
            -----------||
            ----------*||   we wanna remove the -'s on the same column as the *'s. we do that for the first measure line in the code right below. the code lower handles the case for the rest of the lines.
            ----------*||
            -----------||
            -----------||
             */
            String tmp1 = line.substring(0, position-1);
            String tmp2 = position>=line.length() ? "" : line.substring(position);
            line = tmp1+tmp2;
            lines.set(0, line);
        }
        for(int i=0; i<lines.size(); i++) {
            String line = lines.get(i);
            int linePosition = linePositions.get(i);
            if (line.startsWith("|*")){
                linePosition+=2;
                line = line.substring(2);
            }else if(line.startsWith("|")) {
                int offset;
                if (repeatStart) offset = 2;
                else offset = 1;
                linePosition += offset;
                line = line.substring(offset);
            }
            if (line.endsWith("*|"))
                line = line.substring(0, line.length()-2);
            else if (line.endsWith("|")) {
                int offset;
                if (repeatEnd) offset = 2;
                else offset = 1;
                line = line.substring(0, line.length() - offset);
            }
            lines.set(i, line);
            linePositions.set(i, linePosition);
        }
    }

    
    public String[] nameOf(String measureLineStr, int pos) {
        Pattern measureLineNamePttrn = Pattern.compile(Patterns.measureNameExtractPattern());
        Matcher measureLineNameMatcher = measureLineNamePttrn.matcher(measureLineStr);
        if (measureLineNameMatcher.find())
            return new String[] {measureLineNameMatcher.group(), "" + (pos + measureLineNameMatcher.start())};
        else
            return null;
    }
	
	    public List<TabMeasure> getMeasureList() {
	        return this.tabMeasures;
	    }


	public boolean isGuitar(boolean strictCheck) {
        for (TabMeasure measure : this.tabMeasures) {
            if (!measure.isGuitar(strictCheck))
                return false;
        }
        return true;
    }

    public boolean isDrum(boolean strictCheck) {
        for (TabMeasure measure : this.tabMeasures) {
            if (!measure.isDrum(strictCheck))
                return false;
        }
        return true;
    }

    public boolean isBass(boolean strictCheck) {
        for (TabMeasure measure : this.tabMeasures) {
            if (!measure.isBass(strictCheck))
                return false;
        }
        return true;
    }

    /**
     * @return the range of the first line of this TabRow, first character is at position 1
     */
    public Range getRelativeRange() {
        if (this.lines.isEmpty()) return null;
        int position = this.positions.get(0);
        int relStartPos = position-Score.tabText.substring(0,position).lastIndexOf("\n");
        int relEndPos = relStartPos + this.lines.get(0).length();
        return new Range(relStartPos, relEndPos);
    }

    /**
	 * Creates a string representation of the index position range of each line making up this MeasureGroup instance,
	 * where each index position range describes the location where the lines of this MeasureGroup can be found in the
	 * root string from which it was derived (i.e Score.ROOT_STRING)
	 * @return a String representing the index range of each line in this MeasureGroup, formatted as follows:
	 * "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
	 */
	 public List<Range> getRanges() {
	    List<Range> linePositions = new ArrayList<>();
	    for (int i=0; i<this.lines.size(); i++) {
	        int startIdx = this.positions.get(i);
	        int endIdx = startIdx+this.lines.get(i).length();
	        linePositions.add(new Range(startIdx, endIdx));
	    }
	    return linePositions;
	}


	/**
	 * Validates if all Measure objects aggregated in this MeasureGroup have the same number of measure lines. It also
	 * validates that all its aggregate Measure objects are an instance of the same type of Measure class (i.e they're all
	 * GuitarMeasure objects or all DrumMeasure objects). Finally, it validates all its aggregates i.e all Measure objects
	 * and Instruction objects that it aggregates. It stops evaluation at the first aggregated object which fails validation.
	 * TODO it might be better to not have it stop when one aggregated object fails validation, but instead have it
	 *      validate all of them and return a List of all aggregated objects that failed validation, so the user knows
	 *      all what is wrong with their tablature file, instead of having to fix one problem before being able to see
	 *      what the other problems with their text file is.
	 * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
	 * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
	 * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
	 * found in the root string from which it was derived (i.e Score.ROOT_STRING).
	 * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
	 */
	public List<ValidationError> validate() {
	    
	    //--------------Validating yourself--------------------------
	    //making sure all measures in this measure group have the same number of lines
	    boolean hasEqualMeasureLineCount = true;
	    List<Range> failPositions = new ArrayList<>();
	    int measureLineCount = 0;
	    for (TabMeasure measure : this.tabMeasures) {
	        if (measureLineCount==0)
	            measureLineCount = measure.lineCount;
	        else if(measure.lineCount!=measureLineCount) {
	            hasEqualMeasureLineCount = false;
	            failPositions.addAll(measure.getRanges());
	        }
	    }
	
	    if (!hasEqualMeasureLineCount) {
	        addError(
	                "All measures in a tablature row must have the same number of lines",
	                2,
	                failPositions
	        );
	        
	    }
	
	    boolean hasGuitarMeasures = true;
	    boolean hasDrumMeasures = true;
	    for (TabMeasure measure : this.tabMeasures) {
	        hasGuitarMeasures &= measure instanceof GuitarMeasure;
	        hasDrumMeasures &= measure instanceof DrumMeasure;
	    }
	    if (!(hasGuitarMeasures || hasDrumMeasures)) {
	        addError(
	                "All measures in a tablature row must be of the same type (i.e. all guitar measures or all drum measures)",
	                2,
	                this.getRanges()
	        );
	        
	    }
	
	    //--------------Validate your aggregates (only if you're valid)-------------------
	    if (!errors.isEmpty()) return errors;
	
	    for (TabMeasure measure : this.tabMeasures) {
	        errors.addAll(measure.validate());
	    }
	    for (Instruction instruction : this.instructions) {
	        errors.addAll(instruction.validate());
	    }
	
	    return errors;
	}


	@Override
    public String toString() {
        StringBuilder outStr = new StringBuilder();
        for (int i=0; i<this.tabMeasures.size()-1; i++) {
            outStr.append(this.tabMeasures.get(i).toString());
            outStr.append("\n\n");
        }
        if (!this.tabMeasures.isEmpty())
            outStr.append(this.tabMeasures.get(this.tabMeasures.size()-1).toString());
        return outStr.toString();
    }

}