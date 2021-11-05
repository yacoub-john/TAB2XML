package converter.measure_line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import converter.Instrument;
import converter.Score;
import converter.note.TabNote;
import utility.DrumUtils;
import utility.GuitarUtils;
import utility.Settings;
import utility.ValidationError;

public class TabGuitarString extends TabString {

    public static String COMPONENT = "[0-9hHpPsS\\/\\\\]";

    public TabGuitarString(int stringNumber, String line, String[] nameAndPosition, int position) {
        super(stringNumber, line, nameAndPosition, position);
        
    }

    public List<ValidationError> validate() {
        
        if (!GuitarUtils.isValidName(this.name)) {
            String message = DrumUtils.getNickNameSet().contains(this.name.strip())
                    ? "A Guitar measure line is expected here."
                    : "Invalid measure line.";

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
