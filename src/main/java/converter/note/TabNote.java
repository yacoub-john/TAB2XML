package converter.note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import converter.Instrument;
import converter.ScoreComponent;
import models.measure.note.Chord;
import models.measure.note.Dot;
import models.measure.note.TimeModification;
import utility.Patterns;
import utility.Range;
import utility.Settings;
import utility.ValidationError;

public abstract class TabNote extends ScoreComponent implements Comparable<TabNote> {
    public boolean startsWithPreviousNote;
    public String origin;
    public String lineName;
    public int dotCount;
    public Instrument instrument;
    int stringNumber;
    public int distance;
    public int graceDistance = 0;
    int position;
    public int duration;
    //public double durationRatio;
    public String sign;
    public int voice;
    public boolean isGrace;
    //public static boolean SLASHED_GRACE = true;
    protected Map<NoteModelDecorator, String> noteDecorMap = new LinkedHashMap<>();
    int divisions = 0;
    int beatType;
    int beatCount;
    boolean isTriplet;
    public boolean mustSplit;
    


    // A pattern that matches the note components of a measure line, like (2h7) or 8s3 or 12 or 4/2, etc.
    // It doesn't have to match the correct notation. It should be as vague as possible, so it matches anything that "looks"
    //like a note component (e.g it should match something like e|-------h3(-----|, even though it is invalid ) this makes it so that
    //even though incorrect, we still recognise the whole thing as a measure, and we get to the stage where we are trying to convert this
    //particular note. We thus will know the exact place where the problem is instead of the whole measure not being recognised as an
    // actual measure just because of that error and we flag the whole measure as an error instead of this one, small, specific
    // area of hte measure (the pattern for detecting measure groups uses this pattern)
    public static String COMPONENT_PATTERN = "[^-\\n\\r"+Patterns.DIVIDER_COMPONENTS+"]";
    public static String PATTERN = getNotePattern();

    private static String getNotePattern() {
        return "(" + NoteFactory.GUITAR_NOTE_GROUP_PATTERN + "|" + NoteFactory.DRUM_NOTE_GROUP_PATTERN + "|" + COMPONENT_PATTERN+"+" + ")";
    }

    public TabNote(int stringNumber, String text, int position, String lineName, int distanceFromMeasureStart) {
        this.origin = text;
        this.lineName = lineName;
        this.position = position;
        this.stringNumber = stringNumber;
        this.duration = 1;
        this.distance = distanceFromMeasureStart;
        this.voice = 1;
    }
    
    public TabNote(int stringNumber, String origin, int position, String lineName, int distanceFromMeasureStart, int voice) {
        this(stringNumber, origin, position, lineName, distanceFromMeasureStart);
        this.voice = voice;
    }
    
    // Copy constructor used for tied notes
    public TabNote(TabNote n) {
        this.startsWithPreviousNote = n.startsWithPreviousNote;
        this.origin = n.origin;
        this.lineName = n.lineName;
        //public int dotCount;
        this.instrument = n.instrument;
        this.stringNumber = n.stringNumber;
        //public int distance;
        //int position;
        //public int duration;
        this.sign = n.sign;
        this.voice = n.voice;
        this.isGrace = n.isGrace;
        //TODO Look into this, probably must copy decorations
        noteDecorMap = new LinkedHashMap<>();
        this.divisions = n.divisions;
        this.beatType = n.beatType;
        this.beatCount = n.beatCount;
        //boolean isTriplet;
        this.mustSplit = n.mustSplit;
    }
    
    public void setDivisions(int divisions) {
    	this.divisions = divisions;
    	if (getType().equals("1024th")) this.mustSplit = true;
    }
    
    public void setBeatType(int bt) {
    	this.beatType = bt;
    }
    
    public void setBeatCount(int bc) {
    	this.beatCount = bc;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
        if (divisions > 0) 
        	if (getType().equals("1024th")) 
        		this.mustSplit = true;
        	else
        		this.mustSplit = false;
    }
    
    public int getDuration() {
    	return duration;
    }

    public boolean addDecorator(NoteModelDecorator noteDecor, String message) {
        this.noteDecorMap.put(noteDecor, message);
        return true;
    }

    public String getType() {
    	isTriplet = false;
    	dotCount = 0;
    	int RESOLUTION = 192;  // 3 x 2^6
    	int noteVal = RESOLUTION * duration / (divisions * 4);
    	switch (noteVal) { 
    	case 0: return ""; // Grace note
    	case 3: return "64th";
    	case 4: isTriplet = true; return "32nd";
    	case 6: return "32nd";
    	case 8: isTriplet = true; return "16th";
    	case 9: dotCount = 1; return "32nd";
    	case 12: return "16th";
    	case 16: isTriplet = true; return "eighth";
    	case 18: dotCount = 1; return "16th";
    	case 21: dotCount = 2; return "16th";
    	case 24: return "eighth";
    	case 32: isTriplet = true; return "quarter";
    	case 36: dotCount = 1; return "eighth";
    	case 42: dotCount = 2; return "eighth";
    	case 45: dotCount = 3; return "eighth";
    	case 48: return "quarter";
    	case 64: isTriplet = true; return "half";
    	case 72: dotCount = 1; return "quarter";
    	case 84: dotCount = 2; return "quarter";
    	case 90: dotCount = 3; return "quarter";
    	case 93: dotCount = 4; return "quarter";
    	case 96: return "half";
    	case 128: isTriplet = true; return "whole";
    	case 144: dotCount = 1; return "half"; // 3 quarters
    	case 168: dotCount = 2; return "half";
    	case 180: dotCount = 3; return "half";
    	case 186: dotCount = 4; return "half";
    	case 189: dotCount = 5; return "half";
    	case 192: return "whole";
    	case 288: dotCount = 1; return "whole"; 
    	case 336: dotCount = 2; return "whole";
    	case 360: dotCount = 3; return "whole";
    	case 372: dotCount = 4; return "whole";
    	case 378: dotCount = 6; return "whole";
    	default: return "1024th";
    	}
    }

//    public boolean isGuitar() {
//        // remember, invalid notes are still accepted but are created as GuitarNote objects. we want to be able to still convert despite having invalid notes, as long as we warn the user that they have invalid input. We might want to create a new concrete class, InvalidNote, that extends Note to take care of this so that we have the guarantee that this is valid.
//        return this.origin.strip().matches(NoteFactory.FRET);
//    }
//
//    public boolean isDrum() {
//        // remember, invalid notes are still accepted but are created as GuitarNote objects. we want to be able to still convert despite having invalid notes, as long as we warn the user that they have invalid input. We might want to create a new concrete class, InvalidNote, that extends Note to take care of this so that we have the guarantee that this is valid.
//        return this.origin.strip().matches(NoteFactory.DRUM_NOTE_PATTERN);
//    }

    public int compareTo(TabNote other) {
    	int result = this.distance - other.distance;
    	if (result == 0) result = this.graceDistance - other.graceDistance;
        return result;
    }
	
    public models.measure.note.Note getModel() {
    	
    	models.measure.note.Note noteModel = new models.measure.note.Note();
    	
 	    if (this.startsWithPreviousNote) noteModel.setChord(new Chord());
 	    noteModel.setDuration(this.duration);
 	    noteModel.setVoice(this.voice);

 	    String noteType = this.getType();
 	    if (!noteType.isEmpty())
 	        noteModel.setType(noteType);
 	    if (this.isTriplet) {
 	       	TimeModification tm = new TimeModification(3,2);
 	    	noteModel.setTimeModification(tm);	
 	    }
 	    List<Dot> dots = new ArrayList<>();
 	    for (int i=0; i<this.dotCount; i++){
 	        dots.add(new Dot());
 	    }
 	    if (!dots.isEmpty())
 	        noteModel.setDots(dots);

 	    for (NoteModelDecorator noteDecor : this.noteDecorMap.keySet()) {
 	        if (noteDecorMap.get(noteDecor).equals("success"))
 	            noteDecor.applyTo(noteModel);
 	    }
 	   
 	    return noteModel;
    }
	
    public abstract TabNote copy();
    
	@Override
	public List<Range> getRanges() {
		List<Range> ranges = new ArrayList<>();
		ranges.add(new Range(position,position+origin.length()));
		return ranges;
	}
	
	public List<ValidationError> validate() {
	    if (!this.origin.equals(this.origin.strip())) {
	        addError(
	                "Adding whitespace might result in different timing than you expect.",
	                3,
	                getRanges());
	        
	    }
	    return errors;
	}
}
