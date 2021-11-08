package converter.measure_line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import converter.Instrument;
import converter.Score;
import converter.note.TabNote;
import utility.AnchoredText;
import utility.DrumUtils;
import utility.GuitarUtils;
import utility.Settings;
import utility.ValidationError;

public class TabBassString extends TabGuitarString{
    public TabBassString(int stringNumber, AnchoredText dataAT, AnchoredText nameAT) {
        super(stringNumber, dataAT, nameAT);
        
        this.noteList = this.createNoteList(stringNumber, this.line, position);
    }

    @Override
    public List<ValidationError> validate() {
        //List<ValidationError> result = new ArrayList<>(super.validate());

        if (!GuitarUtils.isValidName(this.name)) {
            String message = DrumUtils.getNickNameSet().contains(this.name.strip())
                    ? "A Bass string name is expected here."
                    : "Invalid measure line name.";
            addError(
                    message,
                    1,
                    getRanges());
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
}
