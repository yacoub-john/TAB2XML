package converter.measure_line;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import converter.Score;
import converter.note.DrumNoteFactory;
import converter.note.GuitarNoteFactory;
import converter.note.NoteFactory;
import converter.note.TabNote;
import utility.AnchoredText;
import utility.DrumPiece;
import utility.DrumUtils;
import utility.ValidationError;

public class TabDrumString extends TabString {
    public static Set<DrumPiece> USED_DRUM_PARTS = new HashSet<>();
    private DrumPiece drumPiece;

    public TabDrumString(int stringNumber, AnchoredText dataAT, AnchoredText nameAT) {
        super(stringNumber, dataAT, nameAT);    
        drumPiece = DrumUtils.getDrumPiece(name.strip(), line.strip());
        if (drumPiece != null) USED_DRUM_PARTS.add(drumPiece);
    }
    
	@Override
	protected NoteFactory createNoteFactory() {
		return new DrumNoteFactory();
	}

    public List<ValidationError> validate() {
        
        if (!DrumUtils.getNickNameSet().contains(this.name.strip())) {
            addError("This drum piece is not recognized. Update Settings -> Current Song Settings to include it", 1, getRanges());
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
