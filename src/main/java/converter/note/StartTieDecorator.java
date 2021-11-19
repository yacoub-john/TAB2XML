package converter.note;

import models.measure.note.Note;
import models.measure.note.notations.Notations;
import models.measure.note.notations.Tied;

public class StartTieDecorator implements NoteModelDecorator {

	@Override
	public boolean applyTo(Note noteModel) {
            if (noteModel.getNotations() == null) noteModel.setNotations(new Notations());
    	    Notations notations = noteModel.getNotations();
            Tied tied = new Tied("start");
            notations.setTied(tied);
            return true;
	}
}
