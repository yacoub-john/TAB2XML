package converter.measure_line;

import java.util.List;

import converter.Score;
import converter.note.GuitarNoteFactory;
import converter.note.NoteFactory;
import converter.note.TabNote;
import utility.AnchoredText;
import utility.GuitarUtils;
import utility.ValidationError;

public class TabGuitarString extends TabString {

    public TabGuitarString(int stringNumber, AnchoredText dataAT, AnchoredText nameAT) {
        super(stringNumber, dataAT, nameAT);
    }

    @Override
    public List<ValidationError> validate() {

        if (!GuitarUtils.isValidName(this.name)) {
            String message = "Unrecognized name";
            addError(message, 1, getRanges());
        }

        for (ValidationError error : errors) {
            if (error.getPriority() <= Score.CRITICAL_ERROR_CUTOFF) {
                return errors;
            }
        }

        for (TabNote note : this.noteList)
            errors.addAll(note.validate());

        return errors;
    }

	@Override
	protected NoteFactory createNoteFactory() {
		return new GuitarNoteFactory();
	}
    
}
