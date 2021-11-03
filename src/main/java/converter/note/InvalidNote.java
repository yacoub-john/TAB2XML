package converter.note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import utility.Settings;
import utility.ValidationError;

public class InvalidNote extends TabNote {

    public InvalidNote(int stringNumber, String origin, int position, String lineName, int distanceFromMeasureStart) {
        super(stringNumber, origin, position, lineName, distanceFromMeasureStart);
    }

    public InvalidNote(InvalidNote n) {
        super(n);
    }
    
    @Override
	public TabNote copy() {
		// TODO Auto-generated method stub
		return new InvalidNote(this);
	}

	@Override
    public models.measure.note.Note getModel() {
        return null;
    }

    public List<ValidationError> validate() {
        

        addError(
                "Unrecognized text, will be ignored.",
                1,
                getRanges());
        return errors;
    }
}
