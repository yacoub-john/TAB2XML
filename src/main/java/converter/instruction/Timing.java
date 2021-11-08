package converter.instruction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
	private static String supportedTimings = "gtT";
    public static String PATTERN = "(?<=^|\\n|)QX.*(?=$|\\n";
    private Map<Integer, Character> timings = new TreeMap<>();
    private Map<Integer, Character> unrecognized = new TreeMap<>();
    private boolean tab = false;
    
    public Timing(AnchoredText inputAT, boolean isTop) {
        super(inputAT, isTop);
        // Start at 2 to bypass QX
        for (int i = 2; i < at.text.length(); i++)
        {
        	if (at.text.charAt(i) == '\t') {
        		tab = true; break;
        	}
        	if (at.text.charAt(i) != ' ') {
        		Matcher timingMatcher = Pattern.compile("[" + supportedTimings + "]").matcher(at.text.substring(i,i+1));
        		if (timingMatcher.find()) timings.put(i, at.text.charAt(i));
        		else unrecognized.put(i, at.text.charAt(i));
        	}
        }
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
                //Range tabRowRange = tabRow.getRelativeRange();
//                if (measureGroupRange==null) continue;
//                if (!measureGroupRange.contains(this.getRange())) continue;
                for (TabMeasure measure : tabRow.getMeasureList()) {
                    Range measureRange = measure.getRelativeRange();
                    if (measureRange == null || !measureRange.overlaps(this.getRange())) continue;
                    List<List<List<TabNote>>> noteList = measure.getVoiceSortedChordList();
                    for (List<List<TabNote>> chordList : noteList) {
            			for (int i = 0; i < chordList.size() - 1; i++) {
            				List<TabNote> chord = chordList.get(i);
            			    List<TabNote> nextChord = chordList.get(i+1);
            			    int currentChordDistance = chord.get(0).distance;
            			}
                    }
//                    boolean itWorked = measure.setTimeSignature(this.beatCount, this.beatType);
//                    this.setHasBeenApplied(itWorked);
//                    if (itWorked) measure.changesTimeSignature = true;
                }
            
        }
    }

    public List<ValidationError> validate() {
        if (tab) {
            addError(
                    "The TAB character is not allowed in a timing line.",
                    2,
                    getRanges());
        }
//        else
//        if (beatCount<=0 || beatType<=0) {
//            addError(
//                    "Invalid beat " + (this.beatCount<=0?"count" : "type") + " value.",
//                    2,
//                    getRanges());
//        }else if (!isValid(this.beatCount, this.beatType)) {
//            addError(
//                    "Unsupported time signature.",
//                    2,
//                    getRanges());
//        }
        return errors;
    }

//    public static boolean isValid(int beatCount, int beatType) {
////        return switch (beatCount + "/" + beatType) {
////            case "2/4", "2/2", "3/8", "3/4", "3/2", "4/8", "4/4", "4/2", "6/8", "6/4", "9/8", "9/4", "12/8", "12/4" -> true;
////            default -> false;
////        };
//    	boolean result = true;
//    	int[] validDens = {2,4,8,16,32};
//    	if (!Arrays.stream(validDens).anyMatch(i -> i == beatType)) result = false;
//    	return result;
//    }
}
