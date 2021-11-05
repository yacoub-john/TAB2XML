package converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import converter.instruction.Instruction;
import converter.instruction.InvalidRepeat;
import converter.instruction.Repeat;
import converter.instruction.TimeSignature;
import utility.Patterns;
import utility.Range;
import utility.Settings;
import utility.ValidationError;

public class TabSection extends ScoreComponent {

	//                           a measure line at start of line(with name)          zero or more middle measure lines       (optional |'s and spaces then what's ahead is end of line)
    private static String LINE_PATTERN = "(" + Patterns.START_OF_LINE          +          Patterns.MIDDLE_OF_LINE + "*"    +   "("+ Patterns.DIVIDER+"*"+Patterns.WHITESPACE+"*)"     +  ")";

    String origin;  //the string that was passed to the constructor upon the instantiation of this class
    int position;   //the index in Score.rootString at which the String "MeasureCollection().origin" is located
    int endIndex;
    private TabRow tabRow;
    public static String PATTERN = getRegexPattern();
    boolean isFirstCollection;
    private List<Instruction> instructionList = new ArrayList<>();
    private boolean repeatsFound = false;

    public TabSection(String origin, int position, boolean isFirstCollection) {
        this.origin = origin;
        this.position = position;
        this.endIndex = position+this.origin.length();
        this.isFirstCollection = isFirstCollection;
        createTabRowAndInstructionList(origin);
        for (Instruction instruction : this.instructionList)
            instruction.applyTo(this);
    }

	/**
	 * Separates the provided String representation of a TabSection into
	 * Instructions, and a TabRow collection (of one?)
	 * 
	 * @param origin the String representation of a TabSection from which the
	 *               instructions, and the TabRow collection are extracted. Each
	 *               String stored in instructionList and tabRowList begins with a
	 *               tag (i.e "[startIdx]stringContent" ) specifying the start index
	 *               of the String in Score.tabText
	 */
	private void createTabRowAndInstructionList(String origin) {

		//Range tabRowRange = null;
		List<String> lines = new ArrayList<>();
		List<Integer> starts= new ArrayList<>();
		List<String> tabRowLines = new ArrayList<>();
		List<Integer> tabRowStarts= new ArrayList<>();
		// Match one or more tablature lines 
		//Matcher matcher = Pattern.compile("((^|\\n)" + tabRowLinePattern() + ")+").matcher(origin);
		//Matcher lineMatcher = Pattern.compile("((^|\\n)" + tabRowLinePattern() + ")").matcher(origin);  // Match only one line
		//Matcher lineMatcher = Pattern.compile("(^" + tabRowLinePattern() + ")").matcher(origin);  // Match only one line
		Matcher lineMatcher = Pattern.compile("(?<=^|\\n)[^\\n]+(?=$|\\n)").matcher(origin);
        while (lineMatcher.find()) { // go through each line
        	String l = lineMatcher.group();
        	lines.add(l);
        	starts.add(lineMatcher.start());
        }
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			int start = starts.get(i);
			Matcher topInstructionMatcher = Pattern.compile("((^|\\n)" + Instruction.LINE_PATTERN + ")+").matcher(line);
			if (topInstructionMatcher.find()) {  // no need for loop as only one line
				this.instructionList.addAll(extractInstructions(topInstructionMatcher.group(), this.position + start + topInstructionMatcher.start(), true));
				continue;
			}
			Matcher tabRowMatcher = Pattern.compile("((^|\\n)" + tabRowLinePattern() + ")").matcher(line); 
			if (tabRowMatcher.find()) { 		
				if (line.charAt(0) == '\n') {
					line = line.substring(1);
					start++;
				}
				tabRowStarts.add(start);
				tabRowLines.add(line);
			}
		}
		//TODO redo this error
		//else addError ("No tablature detected", 1, getRanges()); Also assert tabRowStart is not -1

		createTabRow(tabRowStarts, tabRowLines);
		}
	
	private void createTabRow(List<Integer> tabRowStarts, List<String> tabRowLines) {
		List<String> tabRowString = new ArrayList<>();
		for (int i=0; i < tabRowLines.size() ; i++)	{
			String line = tabRowLines.get(i);
			int lineStartIdx = tabRowStarts.get(i);
			String tabRowLine = "[" + (this.position + lineStartIdx) + "]" + line;
			tabRowString.add(tabRowLine);
		}
		if (isFirstCollection && Settings.getInstance().getInstrument() == Instrument.GUITAR && tabRowString.size() < 6)
			Settings.getInstance().detectedInstrument = Instrument.BASS;
		this.tabRow = new TabRow(tabRowString);
	}
	
    private List<Instruction> extractInstructions(String line, int position, boolean isTop) {
        List<Instruction> instructionList = new ArrayList<>();
        // Matches the repeat text including any barlines
        Matcher repeatMatcher = Pattern.compile(Repeat.PATTERN).matcher(line);
        boolean repeatsAdded = false;
        
        while((repeatMatcher.find())) {
        	if (repeatsFound)
        		instructionList.add(new InvalidRepeat(repeatMatcher.group(), position+repeatMatcher.start(), isTop));
        	else {
        		repeatsAdded = true;
        		instructionList.add(new Repeat(repeatMatcher.group(), position+repeatMatcher.start(), isTop));
        	}
        }
        if (repeatsAdded) repeatsFound = true;
        
        Matcher timeSigMatcher = Pattern.compile(TimeSignature.PATTERN).matcher(line);
        while(timeSigMatcher.find()) {
            instructionList.add(new TimeSignature(timeSigMatcher.group(), position+timeSigMatcher.start(), isTop));
        }

        return instructionList;
    }

    public static String tabRowLinePattern() {
        return "("+ Patterns.WHITESPACE + "*(" + LINE_PATTERN + Patterns.WHITESPACE + "*)+)";
    }

    /**
     * Creates the regex pattern for detecting a tab section (i.e a collection of tab rows (really one tab row and their corresponding instructions)
     * @return a String regex pattern enclosed in brackets that identifies a tab section pattern (the pattern also captures the newline right before the tab row collection)
     */
    private static String getRegexPattern() {
        // Zero or more instructions, followed by one or more tab rows, followed by zero or more instructions
        return "((^|\\n)"+ Instruction.LINE_PATTERN+")*"          // 0 or more lines separated by newlines, each containing a group of instructions
                + "("                                                                   // then the tab row, which is made of...
                +       "(^|\\n)"                                                           // a start of line or a new line
                +       TabSection.tabRowLinePattern()                               // a tab row followed by whitespace, all repeated one or more times
                + ")+"                                                                  // the tab row I just described is repeated one or more times.
                + "(\\n"+ Instruction.LINE_PATTERN+")*";
    }
	    
	    public TabRow getTabRow() {
	        return this.tabRow;
	    }

@Override
	public List<Range> getRanges() {
		List<Range> ranges = new ArrayList<>();
		ranges.add(new Range(position,position+origin.length()));
		return null;
	}

/**
	 * Validates the aggregated TabRow objects of this class. It stops evaluation at the first aggregated object
	 * which fails validation.
	 * TODO it might be better to not have it stop when one aggregated object fails validation, but instead have it
	 *      validate all of them and return a List of all aggregated objects that failed validation, so the user knows
	 *      all what is wrong with their tablature file, instead of having to fix one problem before being able to see
	 *      what the other problems with their text file is.
	 * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
	 * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
	 * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
	 * found in the root string from which it was derived (i.e Score.tabText).
	 * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
	 */
	public List<ValidationError> validate() {
		errors.addAll(tabRow.validate());
		for (Instruction instruction : this.instructionList) {
			errors.addAll(instruction.validate());
		}
		return errors;
	}

	@Override
    public String toString() {
		//TODO Needs to be updated to include instructions?
        StringBuilder outStr = new StringBuilder();    
        outStr.append(this.tabRow.toString());
        return outStr.toString();
    }

}
