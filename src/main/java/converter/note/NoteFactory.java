package converter.note;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.measure.note.Grace;
import models.measure.note.notations.Notations;
import models.measure.note.notations.Slur;
import models.measure.note.notations.Tied;
import models.measure.note.notations.technical.Technical;
import utility.AnchoredText;
import utility.Range;

/**
 * A class to extract notes from single notes or combinations of notes
 * e.g. 6h8p6, or special types of notes like harmonics or grace notes
 */
public abstract class NoteFactory {
	protected int stringNumber;
    protected String noteText;
	protected String lineName;
    protected int distanceFromMeasureStart;
	protected int position;
    String noteGroupPattern, notePattern;
	protected String connectorPattern;
	
    public List<TabNote> getNotes(int inputStringNumber, String inputNoteText, int inputPosition, String inputLineName, int inputDistance) {
    	this.stringNumber = inputStringNumber;
    	this.noteText = inputNoteText;
    	this.position = inputPosition;
    	this.lineName = inputLineName;
        this.distanceFromMeasureStart = inputDistance;
        
	    List<TabNote> noteList = new ArrayList<>();
	    Matcher noteGroupMatcher = Pattern.compile(noteGroupPattern).matcher(noteText);
	    if (!noteGroupMatcher.find()) {
	        noteList.add(new InvalidNote(stringNumber, noteText, position, lineName, distanceFromMeasureStart));
	        return noteList;
	    }
	    
	    Range noteRange = new Range(noteGroupMatcher.start(), noteGroupMatcher.end());
	    
	    if (noteRange.getStart() > 0) {
	        noteList.add(new InvalidNote(stringNumber, noteText.substring(0, noteRange.getStart()), position, lineName, distanceFromMeasureStart));
	    }
	    
	    if (noteRange.getEnd() < noteText.length()) {
	        noteList.add(new InvalidNote(stringNumber, noteText.substring(noteRange.getEnd()), position, lineName, distanceFromMeasureStart + noteRange.getEnd()));
	    }
	    
	    noteList.addAll(getNotes(noteRange));
	    return noteList;
	}

	protected List<TabNote> getNotes(Range r) {
	    List<TabNote> noteList = new ArrayList<>();
	    int idx = r.getStart();
	    int endIdx = r.getEnd();
	    if (idx >= endIdx) return noteList;
	    
	    Matcher noteMatcher = Pattern.compile(notePattern).matcher(noteText.substring(idx, endIdx));
	    TabNote note1;
	    if (!noteMatcher.find()) {
	        noteList.add(new InvalidNote(stringNumber, noteText.substring(idx, endIdx), position+idx, lineName, distanceFromMeasureStart+idx));
	        return noteList;
	    }else {
	        List<TabNote> notes = createNote(noteMatcher.group(), position+idx+noteMatcher.start(), distanceFromMeasureStart+idx+noteMatcher.start());
	        noteList.addAll(notes);
	        note1 = notes.get(notes.size()-1);  //It is always the last note that builds a relationship. e.g you dont wanna get the grace note. you wanna get the grace pair because it is what will be creating a relationship with other notes
	    }
	    int endNote = idx+noteMatcher.end();
	    
	    AnchoredText connectorAT = createConnector(noteList, idx, endIdx, endNote);
	    Range newRange = new Range(endNote + connectorAT.positionInLine, endIdx);
	    List<TabNote> remainingNotes = getNotes(newRange);
	    if (remainingNotes.isEmpty())
	        return noteList;
	    TabNote note2 = remainingNotes.get(0);
	    noteList.addAll(remainingNotes);
	
	    if (!connectorAT.text.isBlank())
	        addRelationship(note1, note2, connectorAT.text);
	    return noteList;
	}

    protected abstract List<TabNote> createNote(String origin, int position, int distanceFromMeasureStart);

	protected abstract AnchoredText createConnector(List<TabNote> noteList, int idx, int endIdx, int endNote);

	protected abstract void addRelationship(TabNote note1, TabNote note2, String relationship);
 
    protected boolean slur(TabNote note1, TabNote note2) {
        String message = "success";

        AtomicInteger slurNum = new AtomicInteger();
        note1.addDecorator((noteModel) -> {
            if (noteModel.getNotations()==null)
                noteModel.setNotations(new Notations());
            Notations notations = getNonNullNotations(noteModel);
            Slur slur = new Slur("start");
            slurNum.set(slur.getNumber());
            if (notations.getSlurs()==null) notations.setSlurs(new ArrayList<>());
            notations.getSlurs().add(slur);
            return true;
        }, message);
        note2.addDecorator((noteModel) -> {
            if (noteModel.getNotations()==null)
                noteModel.setNotations(new Notations());
            Notations notations = getNonNullNotations(noteModel);
            Slur slur = new Slur("stop", slurNum.get());
            if (notations.getSlurs()==null) notations.setSlurs(new ArrayList<>());
            notations.getSlurs().add(slur);
            return true;
        }, message);
        return true;
    }
    
    public boolean tie(TabNote note1, TabNote note2) {
        String message = "success";
        note1.addDecorator((noteModel) -> {
            if (noteModel.getNotations()==null)
                noteModel.setNotations(new Notations());
            Notations notations = getNonNullNotations(noteModel);
            Tied tied = new Tied("start");
            notations.setTied(tied);
            return true;
        }, message);
        note2.addDecorator((noteModel) -> {
            if (noteModel.getNotations()==null)
                noteModel.setNotations(new Notations());
            Notations notations = getNonNullNotations(noteModel);
            Tied tied = new Tied("stop");
            notations.setTied(tied);
            return true;
        }, message);
        return true;
    }

    protected boolean grace(TabNote graceNote, TabNote gracePair) {
        boolean success;
        success = slur(graceNote, gracePair);
        if (success) {
            grace(graceNote);
            graceNote.isGrace = true;
            graceNote.graceDistance = -1;
        }
        return success;
    }

    protected boolean grace(TabNote note) {
        note.addDecorator((noteModel) -> {
        	//noteModel.setGrace(TabNote.SLASHED_GRACE ? new Grace("yes") : new Grace());
        	noteModel.setGrace(new Grace());
            noteModel.setDuration(null);
            noteModel.setChord(null);
            return true;
        }, "success");
        return true;
    }

    protected Technical getNonNullTechnical(models.measure.note.Note noteModel) {
    	if (noteModel.getNotations() == null) noteModel.setNotations(new Notations());
	    Notations notations = noteModel.getNotations();
	    if (notations.getTechnical() == null) notations.setTechnical(new Technical());
	    Technical technical = notations.getTechnical();
	    return technical;
    }
    
    protected Notations getNonNullNotations(models.measure.note.Note noteModel) {
    	if (noteModel.getNotations() == null) noteModel.setNotations(new Notations());
	    Notations notations = noteModel.getNotations();
	    return notations;
    }

}
