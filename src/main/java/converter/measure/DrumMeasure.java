package converter.measure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import converter.Score;
import converter.measure_line.TabDrumString;
import converter.measure_line.TabString;
import converter.note.TabNote;
import models.measure.Backup;
import models.measure.attributes.Attributes;
import models.measure.attributes.Clef;
import models.measure.attributes.Key;
import models.measure.attributes.Time;
import models.measure.barline.BarLine;
import models.measure.barline.Repeat;
import models.measure.direction.Direction;
import models.measure.direction.DirectionType;
import models.measure.direction.Words;
import utility.Settings;
import utility.ValidationError;

public class DrumMeasure extends TabMeasure {

    public DrumMeasure(List<String> lines, List<String[]> lineNamesAndPositions, List<Integer> linePositions, boolean isFirstMeasureInGroup) {
        super(lines, lineNamesAndPositions, linePositions, isFirstMeasureInGroup);
    }
    
    @Override
	protected int adjustDivisionsForDoubleCharacterNotes(int usefulMeasureLength) {
		return usefulMeasureLength;
	}
    
	@Override
	protected int adjustDurationForSpecialCases(int duration, List<TabNote> chord, List<TabNote> nextChord) {
		// Duration should be 1 for choked cymbals
		boolean choke = false;
		for (TabNote note : chord) {
			if (note.origin.equals("#")) {
				choke = true;
				break;
			}
		}
//		if (choke)
//			duration = 1;
		return duration;
	}

	protected TabString newTabString(int stringNumber, String line, String[] nameAndPosition, int position)
	{
		return new TabDrumString(stringNumber, line, nameAndPosition, position);
	}
	
	private Attributes getAttributesModel() {
        Attributes attributes = new Attributes();
        attributes.setDivisions(this.divisions);
        
        if (this.changesTimeSignature)
            attributes.setTime(new Time(this.beatCount, this.beatType));

        if (this.measureCount == 1) {
        	attributes.setKey(new Key(0));
            attributes.setClef(new Clef("percussion", 2));
            
        }
        return attributes;
    }
    
	@Override
	public models.measure.Measure getModel() {
	    models.measure.Measure measureModel = new models.measure.Measure();
	    measureModel.setNumber(this.measureCount);
	    measureModel.setAttributes(this.getAttributesModel());
	
	    List<models.measure.note.Note> noteBeforeBackupModels = new ArrayList<>();
	    List<models.measure.note.Note> noteAfterBackupModels = new ArrayList<>();
	    for (int i=0; i<this.voiceSortedNoteList.size(); i++) {
	        List<TabNote> voice = this.voiceSortedNoteList.get(i);
	        double backupDuration = 0;
	        double currentChordDuration = 0;
	        for (TabNote note : voice) {
	            if (note.voice==1)
	                noteBeforeBackupModels.add(note.getModel());
	            if (note.voice==2)
	                noteAfterBackupModels.add(note.getModel());
	            if (note.startsWithPreviousNote)
	                currentChordDuration = Math.max(currentChordDuration, note.duration);
	            else {
	                backupDuration += currentChordDuration;
	                currentChordDuration = note.duration;
	            }
	        }
	        backupDuration += currentChordDuration;
	        if (voice.get(0).voice==1)
	            measureModel.setNotesBeforeBackup(noteBeforeBackupModels);
	        if (voice.get(0).voice==2)
	            measureModel.setNotesAfterBackup(noteAfterBackupModels);
	        if (i+1<this.voiceSortedNoteList.size()) {
	            measureModel.setBackup(new Backup((int)backupDuration));
	        }
	    }
	
	    List<BarLine> barLines = new ArrayList<>();
	    if (this.isRepeatStart()) {
	        BarLine barLine = new BarLine();
	        barLines.add(barLine);
	        barLine.setLocation("left");
	        barLine.setBarStyle("heavy-light");
	
	        Repeat repeat = new Repeat();
	        repeat.setDirection("forward");
	        barLine.setRepeat(repeat);
	
	        Direction direction = new Direction();
	        direction.setPlacement("above");
	        measureModel.setDirection(direction);
	
	        DirectionType directionType = new DirectionType();
	        direction.setDirectionType(directionType);
	
	        Words words = new Words();
	        words.setRelativeX(256.17);
	        words.setRelativeX(16.01);
	        words.setRepeatText("Repeat "+this.repeatCount+" times");
	        directionType.setWords(words);
	    }
	
	    if (this.isRepeatEnd()) {
	        BarLine barLine = new BarLine();
	        barLines.add(barLine);
	        barLine.setLocation("right");
	        barLine.setBarStyle("light-heavy");
	
	        Repeat repeat = new Repeat();
	        repeat.setDirection("backward");
	        barLine.setRepeat(repeat);
	    }
	
	    if (!barLines.isEmpty())
	        measureModel.setBarlines(barLines);
	    return measureModel;
	}

	/**
	 * Validates that all TabString objects in this DrumMeasure are TabDrumString objects, and validates its
	 * aggregated TabString objects. It stops evaluation at the first aggregated object which fails validation.
	 * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
	 * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
	 * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
	 * found in the root string from which it was derived (i.e Score.tabText).
	 * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
	 */
	@Override
	public List<ValidationError> validate() {
	
	    //-----------------Validate yourself-------------------------
	    super.validate(); //this validates if all TabString objects in this measure are of the same type
	    
	    // If we are here, all TabString objects are of the same type. Now, all we need to do is check if they are actually drum measures
	    if (!(this.tabStringList.get(0) instanceof TabDrumString)) {
	        addError(
	                "All measure lines in this measure must be drum measure lines.",
	                1,
	                this.getRanges());
	    }
	    
	    for (ValidationError error : errors) {
	        if (error.getPriority() <= Score.CRITICAL_ERROR_CUTOFF) {
	            return errors;
	        }
	    }
	    //-----------------Validate Aggregates (only if you don't have critical errors)------------------		
	    for (TabString measureLine : this.tabStringList) {
	        errors.addAll(measureLine.validate());
	    }
	
	    return errors;
	}
}
