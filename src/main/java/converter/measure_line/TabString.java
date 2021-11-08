package converter.measure_line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import converter.Instrument;
import converter.Score;
import converter.ScoreComponent;
import converter.note.TabNote;
import converter.note.NoteFactory;
import utility.AnchoredText;
import utility.DrumUtils;
import utility.GuitarUtils;
import utility.Patterns;
import utility.Range;
import utility.Settings;
import utility.ValidationError;

public abstract class TabString extends ScoreComponent {
    public String line;
    public String name;
    int namePosition;
    int position;
    public List<TabNote> noteList;
    

    protected TabString(int stringNumber, AnchoredText dataAT, AnchoredText nameAT) {
        this.line = dataAT.text;
        this.position = dataAT.positionInScore;
        this.name = nameAT.text;
        this.namePosition = nameAT.positionInScore;
        this.noteList = this.createNoteList(stringNumber, this.line, position);
    }

    public List<TabNote> getNoteList() {
        List<TabNote> noteList = new ArrayList<>();
        for (ValidationError error : this.validate()) {
            if (error.getPriority() <= Score.CRITICAL_ERROR_CUTOFF) {
                return noteList;
            }
        }
        for (TabNote note : this.noteList) {
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
	protected List<TabNote> createNoteList(int stringNumber, String line, int position) {
		List<TabNote> noteList = new ArrayList<>();
		Matcher noteMatcher = Pattern.compile(TabNote.PATTERN).matcher(line);
		while (noteMatcher.find()) {
			String match = noteMatcher.group();
			String leadingStr = line.substring(0, noteMatcher.start()).replaceAll("\s", "");
			int distanceFromMeasureStart = leadingStr.length();
			if (!match.isBlank()) {
				NoteFactory nf = new NoteFactory(stringNumber, match, position + noteMatcher.start(), this.name, distanceFromMeasureStart);
				noteList.addAll(nf.getNotes());
			}
		}
		return noteList;
	}
    
    public boolean isGuitar(boolean strictCheck) {
    	boolean x = GuitarUtils.getValidGuitarNames().contains(this.name.strip());
        if (!x) return false;
        if (!strictCheck) return true;
        for (TabNote note : this.noteList) {
            if (!note.isGuitar())
                return false;
        }
        return true;
    }

    public boolean isDrum(boolean strictCheck) {
    	boolean x = DrumUtils.getNickNameSet().contains(this.name.strip());
        if (!x) return false;
        if (!strictCheck) return true;
        for (TabNote note : this.noteList) {
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
	    for (TabNote note : this.noteList) {
	        maxRatio = Math.max(maxRatio, note.durationRatio);
	    }
	    int actualLineDistance = maxMeasureLineLength;
	
	
	    int prevNoteEndDist = 0;
	    for (TabNote note : this.noteList) {
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

	@Override
	public List<Range> getRanges() {
		List<Range> ranges = new ArrayList<>();
		ranges.add(new Range(position,position+line.length()));
		return null;
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
	    
	    if (name==null) {
	        addError(
	                "invalid measure line name.",
	                1,
	                getRanges());
	    }
	    Matcher matcher = Pattern.compile(Patterns.insidesPattern()).matcher("|"+line+"|");
	    if (!matcher.find() || !matcher.group().equals(this.line.strip())) {     // "|"+name because the MeasureLine.INSIDES_PATTERN expects a newline, space, or | to come before
	        addError(
	                "invalid measure line.",
	                1,
	                getRanges());
	        
	    }
	
	    if (this.line.length()-this.line.replaceAll("\s", "").length() != 0) {
	        addError(
	                "Adding whitespace might result in different timing than you expect.",
	                3,
	                getRanges());
	        
	    }
	
	    return errors;
	}

	@Override
    public String toString() {
        return this.name.strip()+"|"+this.recreateLineString(this.line.length())+"|";
    }

}
