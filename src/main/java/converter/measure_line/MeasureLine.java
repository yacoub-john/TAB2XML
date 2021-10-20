package converter.measure_line;

import converter.Instrument;
import converter.Score;
import converter.ScoreComponent;
import converter.note.Note;
import converter.note.NoteFactory;
import utility.DrumUtils;
import utility.Settings;
import utility.Patterns;
import utility.ValidationError;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MeasureLine implements ScoreComponent {
    public String line;
    public String name;
    int namePosition;
    int position;
    public List<Note> noteList;
    public Instrument instrument;

    protected MeasureLine(String line, String[] namesAndPosition, int position) {
        this.line = line;
        this.name = namesAndPosition[0];
        this.namePosition = Integer.parseInt(namesAndPosition[1]);
        this.position = position;
    }




    public List<Note> getNoteList() {
        List<Note> noteList = new ArrayList<>();
        for (ValidationError error : this.validate()) {
            if (error.getPriority() <= Score.CRITICAL_ERROR_CUTOFF) {
                return noteList;
            }
        }
        for (Note note : this.noteList) {
            List<ValidationError> errors = note.validate();
            boolean criticalError = false;
            for (ValidationError error : errors) {
                if (error.getPriority() <= Score.CRITICAL_ERROR_CUTOFF) {
                    criticalError = true;
                    break;
                }
            }
            if (!criticalError)
                noteList.add(note);
        }
        return noteList;
    }

    protected List<Note> createNoteList(String line, int position) {
        List<Note> noteList = new ArrayList<>();
        Matcher noteMatcher = Pattern.compile(Note.PATTERN).matcher(line);
        while(noteMatcher.find()) {
            String match = noteMatcher.group();
            String leadingStr = line.substring(0, noteMatcher.start()).replaceAll("\s", "");
            int distanceFromMeasureStart = leadingStr.length();
            if (!match.isBlank())
                noteList.addAll(Note.from(match, position+noteMatcher.start(), this.instrument, this.name, distanceFromMeasureStart));
        }
        return noteList;
    }

    public boolean isGuitar(boolean strictCheck) {
    	boolean x = GuitarMeasureLine.NAME_LIST.contains(this.name.strip());
        if (!x) return false;
        if (!strictCheck) return true;
        for (Note note : this.noteList) {
            if (!note.isGuitar())
                return false;
        }
        return true;
    }

    public boolean isDrum(boolean strictCheck) {
    	boolean x = DrumUtils.isValidName(this.name.strip());
        if (!x) return false;
        if (!strictCheck) return true;
        for (Note note : this.noteList) {
            if (!note.isDrum())
                return false;
        }
        return true;
    }



    //---------------------------------------------regex stuff----------------------------------------------------------

    public static String INSIDES_PATTERN = createInsidesPattern();
    // e------------ or |e---------------- or |e|-------------------- when it is the first measure of the measure group (start of line, SOL)
    public static String PATTERN_SOL = "(" + createMeasureNameSOLPattern() + createInsidesPattern() + ")";
    //|--------------------- when it is in between other measures (middle of line, MIDL)
    public static String PATTERN_MIDL = "("+Patterns.DIVIDER+"+" + createInsidesPattern()+")";

    private static String getComponentPattern() {
        return "[^-\\n"+Patterns.DIVIDER_COMPONENTS+"]";
    }


    public static String[] nameOf(String measureLineStr, int lineStartIdx) {
        Pattern measureLineNamePttrn = Pattern.compile(createMeasureNameExtractPattern());
        Matcher measureLineNameMatcher = measureLineNamePttrn.matcher(measureLineStr);
        if (measureLineNameMatcher.find())
            return new String[] {measureLineNameMatcher.group(), ""+(lineStartIdx+measureLineNameMatcher.start())};
        else
            return null;
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
    	pattern.append("("+GuitarMeasureLine.INSIDES_PATTERN_SPECIAL_CASE);
    	pattern.append("|"+DrumMeasureLine.INSIDES_PATTERN_SPECIAL_CASE);
    	pattern.append("|(?<=(?:[ \\r\\n]"+createGenericMeasureNamePattern()+")(?=[ -][^");
    	pattern.append(Patterns.DIVIDER_COMPONENTS+"])|"+Patterns.DIVIDER+")"+Patterns.DIVIDER);
    	pattern.append("?(?:(?: *[-*]+)|(?: *"+getComponentPattern()+"+ *-+))(?:"+getComponentPattern());
        pattern.append("+-+)*(?:"+getComponentPattern()+"+ *)?(?:"+Patterns.DIVIDER+"?(?="+Patterns.DIVIDER+")))");
        return pattern.toString();
    }

    private static String createMeasureNameExtractPattern() {
        StringBuilder pattern = new StringBuilder();
        pattern.append("(?<=^"+Patterns.DIVIDER+"*"+")");
        pattern.append(Patterns.WHITESPACE+"*");
        pattern.append(createGenericMeasureNamePattern());
        pattern.append(Patterns.WHITESPACE+"*");
        pattern.append("(?="+"-" + "|" +Patterns.DIVIDER+")");  // what's ahead is a dash or a divider

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

    public static String createGenericMeasureNamePattern() {
        Iterator<String> measureLineNames = MeasureLine.createLineNameSet().iterator();
        StringBuilder pattern = new StringBuilder();
        pattern.append("(?:[a-zA-Z]{1,3}|(?:"+measureLineNames.next());
        while(measureLineNames.hasNext()) {
            pattern.append("|"+measureLineNames.next());
        }
        pattern.append("))");
        return pattern.toString();
    }


    private static Set<String> createLineNameSet() {
        HashSet<String> nameSet = new HashSet<>();
        nameSet.addAll(GuitarMeasureLine.createLineNameSet());
        nameSet.addAll(DrumUtils.DRUM_NAME_SET);
        //BIL
        nameSet.add("");
        return nameSet;
    }

    public String recreateLineString(int maxMeasureLineLength) {
	    StringBuilder outStr = new StringBuilder();
	    if (this.noteList.isEmpty()) {
	        for (int i=0; i<this.line.length(); i++) {
	            String str = String.valueOf(this.line.charAt(i));
	            if (str.matches("\s")) continue;
	            outStr.append(str);
	        }
	        outStr.append("|");
	        return outStr.toString();
	    }
	
	    double maxRatio = 0;
	    for (Note note : this.noteList) {
	        maxRatio = Math.max(maxRatio, note.durationRatio);
	    }
	    int actualLineDistance = maxMeasureLineLength;
	
	
	    int prevNoteEndDist = 0;
	    for (Note note : this.noteList) {
	        if (!note.validate().isEmpty()) continue;
	        int dashCount = note.distance-prevNoteEndDist;
	        outStr.append("-".repeat(Math.max(0, dashCount)));
	        outStr.append(note.sign);
	        prevNoteEndDist = note.distance + note.sign.length();
	    }
	    outStr.append("-".repeat(Math.max(0, actualLineDistance - prevNoteEndDist)));
	    outStr.append("|");
	    return outStr.toString();
	}




	/**
	 * TODO Validates this MeasureLine object by ensuring if the amount of whitespace contained in this measureline is not
	 * above a certain percentage of the total length of the line, as this can lead to the program interpreting chords
	 * and timings vastly different than the user expects. This method does not validate its aggregated Note objects.
	 * That job is left up to its concrete GuitarMeasureLine and DrumMeasureLine classes.
	 * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
	 * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
	 * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
	 * found in the root string from which it was derived (i.e Score.tabText).
	 * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
	 */
	public List<ValidationError> validate() {
	    List<ValidationError> result = new ArrayList<>();
	    int ERROR_SENSITIVITY = Settings.getInstance().errorSensitivity;
	    if (name==null) {
	        ValidationError error = new ValidationError(
	                "invalid measure line name.",
	                1,
	                new ArrayList<>(Collections.singleton(new Integer[]{
	                        this.position,
	                        this.position+this.line.length()
	                }))
	        );
	        if (ERROR_SENSITIVITY>= error.getPriority())
	            result.add(error);
	    }
	    Matcher matcher = Pattern.compile(MeasureLine.INSIDES_PATTERN).matcher("|"+line+"|");
	    if (!matcher.find() || !matcher.group().equals(this.line.strip())) {     // "|"+name because the MeasureLine.INSIDES_PATTERN expects a newline, space, or | to come before
	        ValidationError error = new ValidationError(
	                "invalid measure line.",
	                1,
	                new ArrayList<>(Collections.singleton(new Integer[]{
	                        this.position,
	                        this.position+this.line.length()
	                }))
	        );
	        if (ERROR_SENSITIVITY>= error.getPriority())
	            result.add(error);
	    }
	
	    if (this.line.length()-this.line.replaceAll("\s", "").length() != 0) {
	        ValidationError error = new ValidationError(
	                "Adding whitespace might result in different timing than you expect.",
	                3,
	                new ArrayList<>(Collections.singleton(new Integer[]{
	                        this.position,
	                        this.position+this.line.length()
	                }))
	        );
	        if (ERROR_SENSITIVITY>= error.getPriority())
	            result.add(error);
	    }
	
	    return result;
	}




	@Override
    public String toString() {
        return this.name.strip()+"|"+this.recreateLineString(this.line.length())+"|";
    }

}
