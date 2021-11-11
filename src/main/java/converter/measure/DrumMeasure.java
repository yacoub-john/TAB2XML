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
import utility.AnchoredText;
import utility.Settings;
import utility.ValidationError;

public class DrumMeasure extends TabMeasure {

    public DrumMeasure(List<AnchoredText> inputData, List<AnchoredText> inputNameData, boolean isFirstMeasureInGroup) {
        super(inputData, inputNameData, isFirstMeasureInGroup);
    }
    
    @Override
	protected int adjustDivisionsForDoubleCharacterNotes(int usefulMeasureLength) {
		return usefulMeasureLength;
	}
    
	@Override
	protected int adjustDurationForSpecialCases(int duration, List<TabNote> chord, List<TabNote> nextChord) {
		// Duration should be 1 for choked cymbals
//		boolean choke = false;
//		for (TabNote note : chord) {
//			if (note.origin.equals("#")) {
//				choke = true;
//				break;
//			}
//		}
//		if (choke)
//			duration = 1;
		return duration;
	}

	protected TabString newTabString(int stringNumber, AnchoredText data, AnchoredText name)
	{
		return new TabDrumString(stringNumber, data, name);
	}
	
	protected Attributes getAttributesModel() {
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
//	    if (!(this.tabStringList.get(0) instanceof TabDrumString)) {
//	        addError(
//	                "All measure lines in this measure must be drum measure lines.",
//	                1,
//	                this.getRanges());
//	    }
	    
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
