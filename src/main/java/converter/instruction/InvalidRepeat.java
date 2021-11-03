package converter.instruction;

import java.util.List;

import converter.ScoreComponent;
import utility.ValidationError;

public class InvalidRepeat extends Instruction {
	InvalidRepeat(String content, int position, RelativePosition relativePosition) {
		super(content, position, relativePosition);
		hasBeenApplied = true;
	}

	// Invalid (nested) repeats only create a validaiton error
	public <E extends ScoreComponent> void applyTo(E scoreComponent) {
	}

	public List<ValidationError> validate() {
		super.validate();
		addError("Nested repeats are not supported. Will be ignored.", 3, getRanges());
		return errors;
	}
}
