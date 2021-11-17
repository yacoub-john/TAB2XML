package converter.instruction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import converter.ScoreComponent;
import converter.TabRow;
import converter.TabSection;
import converter.measure.TabMeasure;
import converter.note.TabNote;
import utility.AnchoredText;
import utility.Patterns;
import utility.Range;
import utility.Settings;
import utility.ValidationError;

public class Timing extends Instruction {
	private static String supportedTimings = "whqestg";
    public static String PATTERN = "(?<=^|\\n)XQ.*(?=$|\\n)";
    private Map<Integer, String> timings = new TreeMap<>();
    private Map<Integer, String> unrecognized = new TreeMap<>();
    private boolean tab = false;
    private int divisions = 1;
    Set<Integer> divFactors = Set.of(1, 2, 4); // Whole, half, and quarter notes are always included
    
    public Timing(AnchoredText inputAT, boolean isTop) {
        super(inputAT, isTop);
        // Start at 2 to bypass XQ
        Matcher timingMatcher = Pattern.compile("[" + supportedTimings + "](\\.*|[1-9]{0,2})?").matcher(at.text);
		while (timingMatcher.find())
		{
			timings.put(timingMatcher.start(), timingMatcher.group());
			//TODO Add range to recognizedRanges
		}
//        for (int i = 2; i < at.text.length(); i++)
//        {
//        	if (at.text.charAt(i) == '\t') {
//        		tab = true; break;
//        	}
//        	if (at.text.charAt(i) != ' ') {
//        		Matcher timingMatcher = Pattern.compile("[" + supportedTimings + "](\\.*|[1-9]{0,2})").matcher(at.text.substring(i,i+1));
//        		if (timingMatcher.find()) timings.put(i, at.text.charAt(i));
//        		else unrecognized.put(i, at.text.charAt(i));
//        	}
//        	explicitDivisions = 1;
//        }
    }

	@Override
	public <E extends ScoreComponent> void applyTo(E scoreComponent) {
		if (!validate().isEmpty() || this.getHasBeenApplied()) {
			this.setHasBeenApplied(true);
			return;
		}

		if (scoreComponent instanceof TabSection) {
			TabSection tabSection = (TabSection) scoreComponent;
			TabRow tabRow = tabSection.getTabRow();
			// Range tabRowRange = tabRow.getRelativeRange();
//                if (measureGroupRange==null) continue;
//                if (!measureGroupRange.contains(this.getRange())) continue;
			for (TabMeasure measure : tabRow.getMeasureList()) {
				Range measureRange = measure.getRelativeRange();
				if (measureRange == null || !measureRange.overlaps(this.getRange()))
					continue;
				List<List<List<TabNote>>> noteList = measure.getVoiceSortedChordList();
				List<String> measureTimings = new ArrayList<>();
				for (List<List<TabNote>> chordList : noteList) {
					for (int i = 0; i < chordList.size() - 1; i++) {
						List<TabNote> chord = chordList.get(i);
						int currentChordDistance = chord.get(0).distance;
						int chordPositionInLine = measure.data.get(0).positionInLine + currentChordDistance;
						String givenTiming = timings.get(chordPositionInLine);
						measureTimings.add(givenTiming);
					}
				}
				updateDivisions(measureTimings);
				measure.setDivisions(this.divisions);
				for (List<List<TabNote>> chordList : noteList) {
					for (int i = 0; i < chordList.size() - 1; i++) {
						List<TabNote> chord = chordList.get(i);
						int currentChordDistance = chord.get(0).distance;
						int chordPositionInLine = measure.data.get(0).positionInLine + currentChordDistance;
						String givenTiming = timings.get(chordPositionInLine);
						for (TabNote note : chord)
							note.setDuration(givenTiming, this.divisions);

					}
				}
			}
		}
	}   

	private void updateDivisions(List<String> mt) {
		divisions = 1;
		for (String timing : mt) {
			int dotCount = 0;
			int tuplet = 1;
			char base = timing.charAt(0);
			if (timing.length() > 1) {
				if (timing.charAt(1) == '.') dotCount = timing.length() - 1;
				else tuplet = Integer.parseInt(timing.substring(1));
			}
			divUpdate(tuplet);
			int durValue = 1;
			switch (base) {
			case 'w': durValue = 1; break;
			case 'h': durValue = 2; break;
			case 'q': durValue = 4; break;
			case 'e': durValue = 8; break;
			case 's': durValue = 16; break;
			case 't': durValue = 32; break;
			default: assert false: "There should not be any other base timing characters";
			}
			int factor = (int) Math.pow(2, dotCount);
			divUpdate(durValue * factor);
		}
		divisions = lcm(divFactors);
	}

	private void divUpdate(int i) {
		divFactors.add(i);
	}

	private static int lcm(int a, int b)
	{
	    return a * (b / gcd(a, b));
	}

	private static int lcm(Set<Integer> input)
	{
	    int result = 1;
	    for(int i: input) result = lcm(result, i);
	    return result;
	}
	
	private static int gcd(int a, int b)
	{
	    while (b > 0)
	    {
	        int temp = b;
	        b = a % b; // % is remainder
	        a = temp;
	    }
	    return a;
	}
	
	public List<ValidationError> validate() {
        if (tab) {
            addError(
                    "The TAB character is not allowed in a timing line.",
                    2,
                    getRanges());
        }
        //TODO Add error if there are unrecognized Ranges

        return errors;
    }

}
