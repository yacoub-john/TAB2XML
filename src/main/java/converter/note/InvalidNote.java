package converter.note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import utility.Settings;
import utility.ValidationError;

public class InvalidNote extends Note {

    public InvalidNote(int stringNumber, String origin, int position, String lineName, int distanceFromMeasureStart) {
        super(stringNumber, origin, position, lineName, distanceFromMeasureStart);
    }

    public InvalidNote(InvalidNote n) {
        super(n);
    }
    
    @Override
	public Note copy() {
		// TODO Auto-generated method stub
		return new InvalidNote(this);
	}

	@Override
    public models.measure.note.Note getModel() {
        return null;
    }

    public List<ValidationError> validate() {
        List<ValidationError> result = new ArrayList<>();

        ValidationError error = new ValidationError(
                "Unrecognized text, will be ignored.",
                1,
                new ArrayList<>(Collections.singleton(new Integer[]{
                        this.position,
                        this.position+this.origin.length()
                }))
        );
        int ERROR_SENSITIVITY = Settings.getInstance().errorSensitivity;
        if (ERROR_SENSITIVITY>= error.getPriority())
            result.add(error);
        return result;
    }
}
