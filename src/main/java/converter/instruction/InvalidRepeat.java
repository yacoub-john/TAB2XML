package converter.instruction;

import java.util.List;

import converter.ScoreComponent;
import utility.ValidationError;

public class InvalidRepeat extends Instruction {
	public InvalidRepeat(String content, int position, boolean isTop) {
		super(content, position, isTop);
		hasBeenApplied = true;
	}

	// Invalid (nested) repeats only create a validation error
	public <E extends ScoreComponent> void applyTo(E scoreComponent) {
	}

	public List<ValidationError> validate() {
		super.validate();
		addError("Nested repeats are not supported. Will be ignored.", 3, getRanges());
		return errors;
	}
}
