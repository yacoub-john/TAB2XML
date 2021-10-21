package converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import converter.instruction.Instruction;
import converter.measure.DrumMeasure;
import converter.measure.GuitarMeasure;
import converter.measure.TabMeasure;
import utility.Patterns;
import utility.Range;
import utility.Settings;
import utility.ValidationError;

public class TabRow implements ScoreComponent {

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
            String[] lineName = nameOf(currentLine, currentLineStartPos);
            if (lineName[0] == "") lineName[0] = defaultTuning(i);
            

            int measureCount = 0;
            Matcher measureInsidesMatcher = Pattern.compile(Patterns.INSIDES_PATTERN).matcher(currentLine);
            while (measureInsidesMatcher.find()) {
                measureCount++;
                String measureLineString = measureInsidesMatcher.group();
                int measurePosition = currentLineStartPos+measureInsidesMatcher.start();    //the starting position of the insides of this measure in the root string Score.ROOT_STRING

                if (textList.size()<measureCount) {
                    textList.add(new ArrayList<>());
                    positionsList.add(new ArrayList<>());
                    namesList.add(new ArrayList<>());
                }

                //get the particular measure we are interested in and add this line to its list of lines
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

            measureList.add(TabMeasure.from(measureLineList, measureLineNameList, measureLinePositionList, isFirstMeasureInGroup));
            isFirstMeasureInGroup = false;
        }
        return measureList;
    }

    private String[] nameOf(String measureLineStr, int lineStartIdx) {
        Pattern measureLineNamePttrn = Pattern.compile(Patterns.createMeasureNameExtractPattern());
        Matcher measureLineNameMatcher = measureLineNamePttrn.matcher(measureLineStr);
        if (measureLineNameMatcher.find())
            return new String[] {measureLineNameMatcher.group(), ""+(lineStartIdx+measureLineNameMatcher.start())};
        else
            return null;
    }

    private String defaultTuning(int i) {
    	String result = "";
    	switch (i) {
    	case 0: result = "E"; break;
    	case 1: result = "B"; break;
    	case 2: result = "G"; break;
    	case 3: result = "D"; break;
    	case 4: result = "A"; break;
    	case 5: result = "E"; break;
    	}
    	return result;
	}


	/**
     * Creates a string representation of the index position range of each line making up this MeasureGroup instance,
     * where each index position range describes the location where the lines of this MeasureGroup can be found in the
     * root string from which it was derived (i.e Score.ROOT_STRING)
     * @return a String representing the index range of each line in this MeasureGroup, formatted as follows:
     * "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    private List<Integer[]> getLinePositions() {
        List<Integer[]> linePositions = new ArrayList<>();
        for (int i=0; i<this.lines.size(); i++) {
            int startIdx = this.positions.get(i);
            int endIdx = startIdx+this.lines.get(i).length();
            linePositions.add(new Integer[]{startIdx, endIdx});
        }
        return linePositions;
    }

    public List<models.measure.Measure> getMeasureModels() {
        List<models.measure.Measure> measureModels = new ArrayList<>();
        for (TabMeasure measure : this.tabMeasures) {
            measureModels.add(measure.getModel());
        }
        return measureModels;
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

    public int getDivisions() {
        int divisions = 0;
        for (TabMeasure measure : this.tabMeasures) {
            divisions = Math.max(divisions,  measure.getDivisions());
        }

        return divisions;
    }

    public void setDurations() {
        for (TabMeasure measure : this.tabMeasures) {
            measure.setDurations();
        }
    }

    public List<TabMeasure> getMeasureList() {
        return this.tabMeasures;
    }

    public Range getRelativeRange() {
        if (this.lines.isEmpty()) return null;
        int position = this.positions.get(0);
        int relStartPos = position-Score.tabText.substring(0,position).lastIndexOf("\n");
        int relEndPos = relStartPos + this.lines.get(0).length();
        return new Range(relStartPos, relEndPos);
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
	    List<ValidationError> result = new ArrayList<>();
	    int ERROR_SENSITIVITY = Settings.getInstance().errorSensitivity;
	
	    //--------------Validating yourself--------------------------
	    //making sure all measures in this measure group have the same number of lines
	    boolean hasEqualMeasureLineCount = true;
	    List<Integer[]> failPositions = new ArrayList<>();
	    int measureLineCount = 0;
	    for (TabMeasure measure : this.tabMeasures) {
	        if (measureLineCount==0)
	            measureLineCount = measure.lineCount;
	        else if(measure.lineCount!=measureLineCount) {
	            hasEqualMeasureLineCount = false;
	            failPositions.addAll(measure.getLinePositions());
	        }
	    }
	
	    if (!hasEqualMeasureLineCount) {
	        ValidationError error = new ValidationError(
	                "All measures in a tablature row must have the same number of lines",
	                2,
	                failPositions
	        );
	        if (ERROR_SENSITIVITY>=error.getPriority())
	            result.add(error);
	    }
	
	    boolean hasGuitarMeasures = true;
	    boolean hasDrumMeasures = true;
	    for (TabMeasure measure : this.tabMeasures) {
	        hasGuitarMeasures &= measure instanceof GuitarMeasure;
	        hasDrumMeasures &= measure instanceof DrumMeasure;
	    }
	    if (!(hasGuitarMeasures || hasDrumMeasures)) {
	        ValidationError error = new ValidationError(
	                "All measures in a tablature row must be of the same type (i.e. all guitar measures or all drum measures)",
	                2,
	                this.getLinePositions()
	        );
	        if (ERROR_SENSITIVITY>=error.getPriority())
	            result.add(error);
	    }
	
	    //--------------Validate your aggregates (only if you're valid)-------------------
	    if (!result.isEmpty()) return result;
	
	    for (TabMeasure measure : this.tabMeasures) {
	        result.addAll(measure.validate());
	    }
	    for (Instruction instruction : this.instructions) {
	        result.addAll(instruction.validate());
	    }
	
	    return result;
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