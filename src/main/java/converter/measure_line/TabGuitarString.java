package converter.measure_line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import converter.Instrument;
import converter.Score;
import converter.note.Note;
import utility.DrumUtils;
import utility.GuitarUtils;
import utility.Settings;
import utility.ValidationError;

public class TabGuitarString extends TabString {
    //public static List<String> OCTAVE_LIST = createOctaveList();
    public static String COMPONENT = "[0-9hHpPsS\\/\\\\]";

    //Not used
//    private static ArrayList<String> createOctaveList() {
//        String[] names = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
//        ArrayList<String> nameList = new ArrayList<>();
//        nameList.addAll(Arrays.asList(names));
//        return nameList;
//    }

    public TabGuitarString(int stringNumber, String line, String[] nameAndPosition, int position) {
        super(stringNumber, line, nameAndPosition, position);
        this.instrument = Instrument.GUITAR;
        this.noteList = this.createNoteList(stringNumber, this.line, position);
    }

    public List<ValidationError> validate() {
        List<ValidationError> result = new ArrayList<>(super.validate());

        
        if (!GuitarUtils.isValidName(this.name)) {
            String message = DrumUtils.isValidName(this.name)
                    ? "A Guitar measure line is expected here."
                    : "Invalid measure line.";

            ValidationError error = new ValidationError(
                    message,
                    1,
                    new ArrayList<>(Collections.singleton(new Integer[]{
                            this.namePosition,
                            this.position+this.line.length()
                    }))
            );
            int ERROR_SENSITIVITY = Settings.getInstance().errorSensitivity;
            if (ERROR_SENSITIVITY>= error.getPriority())
                result.add(error);
        }

        for (ValidationError error : result) {
            if (error.getPriority() <= Score.CRITICAL_ERROR_CUTOFF) {
                return result;
            }
        }
        for (Note note : this.noteList)
            result.addAll(note.validate());

        return result;
    }
}
