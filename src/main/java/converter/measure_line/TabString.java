package converter.measure_line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import converter.Instrument;
import converter.Score;
import converter.ScoreComponent;
import converter.note.Note;
import converter.note.NoteFactory;
import utility.DrumUtils;
import utility.GuitarUtils;
import utility.Patterns;
import utility.Settings;
import utility.ValidationError;

public abstract class TabString implements ScoreComponent {
    public String line;
    public String name;
    int namePosition;
    int position;
    public List<Note> noteList;
    public Instrument instrument;

    protected TabString(int stringNumber, String line, String[] namesAndPosition, int position) {
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

    
	/**
	 * Generates a list of notes of any type (guitar, bass, drums)
	 * @param stringNumber
	 * @param line
	 * @param position
	 * @return the list of Note objects
	 */
	protected List<Note> createNoteList(int stringNumber, String line, int position) {
		List<Note> noteList = new ArrayList<>();
		Matcher noteMatcher = Pattern.compile(Note.PATTERN).matcher(line);
		while (noteMatcher.find()) {
			String match = noteMatcher.group();
			String leadingStr = line.substring(0, noteMatcher.start()).replaceAll("\s", "");
			int distanceFromMeasureStart = leadingStr.length();
			if (!match.isBlank()) {
				NoteFactory nf = new NoteFactory(stringNumber, match, position + noteMatcher.start(), this.instrument, this.name, distanceFromMeasureStart);
				noteList.addAll(nf.getNotes());
			}
		}
		return noteList;
	}
    
    public boolean isGuitar(boolean strictCheck) {
    	boolean x = GuitarUtils.getValidGuitarNames().contains(this.name.strip());
        if (!x) return false;
        if (!strictCheck) return true;
        for (Note note : this.noteList) {
            if (!note.isGuitar())
                return false;
        }
        return true;
    }

    public boolean isDrum(boolean strictCheck) {
    	boolean x = DrumUtils.getNickNameSet().contains(this.name.strip());
        if (!x) return false;
        if (!strictCheck) return true;
        for (Note note : this.noteList) {
            if (!note.isDrum())
                return false;
        }
        return true;
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
	    Matcher matcher = Pattern.compile(Patterns.INSIDES_PATTERN).matcher("|"+line+"|");
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
