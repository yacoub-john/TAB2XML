package utility;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Patterns {
    public static final String WHITESPACE = "[^\\S\\n\\r]";
    public static final String COMMENT = "^[^\\S\\n\\r]*#.+(?=\\n)";
    public static final String DIVIDER = getDivider();
    public static final String DIVIDER_COMPONENTS = "|{}";


    private static String getDivider() {
        return "["+DIVIDER_COMPONENTS+"]";
    }
    
    public static String INSIDES_PATTERN = createInsidesPattern();
    // e------------ or |e---------------- or |e|-------------------- when it is the first measure of the measure group (start of line, SOL)
    public static String PATTERN_SOL = "(" + createMeasureNameSOLPattern() + createInsidesPattern() + ")";
    //|--------------------- when it is in between other measures (middle of line, MIDL)
    public static String PATTERN_MIDL = "("+Patterns.DIVIDER+"+" + createInsidesPattern()+")";

    public static String INSIDES_PATTERN_SPECIAL_CASE = "$a"; // doesn't match anything
    
    private static String getComponentPattern() {
        return "[^-\\n"+Patterns.DIVIDER_COMPONENTS+"]";
    }

    /**
     * A very general, very vague "inside a measure line" pattern. We want to be as general and vague as possible so that
     * we delay catching erroneous user input until we are able to pinpoint where the error is exactly. e.g. if this
     * pattern directly detects a wrong note here, a Note object will never be created. It will just tell the user the
     * measure line where the error is, not the precise note which caused the error.
     * This regex pattern verifies if it is surrounded by |'s or a measure line name and captures a max of one | at each end
     * only if it is surrounded by more than one | (i.e ||------| extracts |------ and ||------||| extracts |------|, but |------| extracts ------)
     * @return the bracket-enclosed String regex pattern.
     */
    private static String createInsidesPattern() {
    	StringBuilder pattern = new StringBuilder();
    	pattern.append("("+INSIDES_PATTERN_SPECIAL_CASE);
    	pattern.append("|"+INSIDES_PATTERN_SPECIAL_CASE);
    	pattern.append("|(?<=(?:[ \\r\\n]"+createGenericMeasureNamePattern()+")(?=[ -][^");
    	pattern.append(Patterns.DIVIDER_COMPONENTS+"])|"+Patterns.DIVIDER+")"+Patterns.DIVIDER);
    	pattern.append("?(?:(?: *[-*]+)|(?: *"+getComponentPattern()+"+ *-+))(?:"+getComponentPattern());
        pattern.append("+-+)*(?:"+getComponentPattern()+"+ *)?(?:"+Patterns.DIVIDER+"?(?="+Patterns.DIVIDER+")))");
        return pattern.toString();
    }

    private static String createMeasureNameSOLPattern() {
        StringBuilder pattern = new StringBuilder();
        pattern.append("(?:");
        pattern.append(Patterns.WHITESPACE+"*"+Patterns.DIVIDER+"*");
        pattern.append(Patterns.WHITESPACE+"*");
        pattern.append(createGenericMeasureNamePattern());
        pattern.append(Patterns.WHITESPACE+"*");
        pattern.append("(?:(?=-)|(?:"+Patterns.DIVIDER+"+))");
        pattern.append(")");

        return pattern.toString();
    }


    public static String createMeasureNameExtractPattern() {
        StringBuilder pattern = new StringBuilder();
        pattern.append("(?<=^"+Patterns.DIVIDER+"*"+")");
        pattern.append(Patterns.WHITESPACE+"*");
        pattern.append(createGenericMeasureNamePattern());
        pattern.append(Patterns.WHITESPACE+"*");
        pattern.append("(?="+"-" + "|" +Patterns.DIVIDER+")");  // what's ahead is a dash or a divider

        return pattern.toString();
    }
    
    public static String createGenericMeasureNamePattern() {
        Iterator<String> measureLineNames = getValidNames().iterator();
        StringBuilder pattern = new StringBuilder();
        pattern.append("(?:[a-zA-Z]{1,3}|(?:"+measureLineNames.next());
        while(measureLineNames.hasNext()) {
            pattern.append("|"+measureLineNames.next());
        }
        pattern.append("))");
        return pattern.toString();
    }
    
    private static Set<String> getValidNames() {
        HashSet<String> nameSet = new HashSet<>();
        nameSet.addAll(GuitarUtils.getValidGuitarNames());
        nameSet.addAll(DrumUtils.DRUM_NAME_SET);
        return nameSet;
    }
}
