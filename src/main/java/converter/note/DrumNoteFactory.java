package converter.note;

import java.util.ArrayList;
import java.util.List;

import utility.AnchoredText;
import utility.Patterns;

public class DrumNoteFactory extends NoteFactory {

	public DrumNoteFactory() {
		notePattern = Patterns.DRUM_NOTE_PATTERN;
		noteGroupPattern = Patterns.DRUM_NOTE_GROUP_PATTERN;
		connectorPattern = Patterns.DRUM_NOTE_CONNECTOR;
	}

	@Override
	protected AnchoredText createConnector(List<TabNote> noteList, int idx, int endIdx, int endNote) {
			AnchoredText connectorAT = new AnchoredText("", 0, 0);
			return connectorAT;
		}

		@Override
		protected List<TabNote> createNote(String origin, int position, int distanceFromMeasureStart) {
			List<TabNote> noteList = new ArrayList<>();
			if (origin.strip().equalsIgnoreCase("x") || origin.strip().equalsIgnoreCase("o")
					|| origin.strip().equalsIgnoreCase("#"))
				noteList.add(new DrumNote(stringNumber, origin, position, this.lineName, distanceFromMeasureStart));
			else if (origin.strip().equalsIgnoreCase("f"))
				noteList.addAll(createFlam(origin, position, distanceFromMeasureStart));
			else if (origin.strip().equalsIgnoreCase("d"))
				noteList.addAll(createDrag(origin, position, distanceFromMeasureStart));
			else if (origin.strip().equalsIgnoreCase("g"))
				noteList.addAll(createGhost(origin, position, distanceFromMeasureStart));
			else
				noteList.add(new InvalidNote(stringNumber, origin, position, lineName, distanceFromMeasureStart));
			return noteList;
		}

		@Override
		protected void addRelationship(TabNote note1, TabNote note2, String relationship) {
			// No relationships for drum notes
		}

		protected List<TabNote> createFlam(String origin, int position, int distanceFromMeasureStart) {
			TabNote graceNote = new DrumNote(stringNumber, origin, position, this.lineName, distanceFromMeasureStart);
			TabNote gracePair = new DrumNote(stringNumber, origin, position, this.lineName, distanceFromMeasureStart);
			grace(graceNote, gracePair);
			List<TabNote> notes = new ArrayList<>();
			notes.add(graceNote);
			notes.add(gracePair);
			return notes;
		}

		// TODO Implement the second grace note for Drag
		protected List<TabNote> createDrag(String origin, int position, int distanceFromMeasureStart) {
//		        Note note1 = createDrumNote(origin, position, distanceFromMeasureStart);
//		        Note note2 = createDrumNote(origin, position, distanceFromMeasureStart);
//		        note2.addDecor((noteModel) -> {
//		            noteModel.setChord(null);
//		            return true;
//		        }, "success");
//		        List<Note> notes = new ArrayList<>();
//		        notes.add(note1);
//		        notes.add(note2);
//		        //slur(note1, note2);
//		        return notes;
			return createFlam(origin, position, distanceFromMeasureStart);
		}

		// TODO Implement the ghost notes
		// <notehead parentheses="yes">normal</notehead>
		protected List<TabNote> createGhost(String origin, int position, int distanceFromMeasureStart) {
//		        Note note1 = createDrumNote(origin, position, distanceFromMeasureStart);
//		        Note note2 = createDrumNote(origin, position, distanceFromMeasureStart);
//		        note2.addDecor((noteModel) -> {
//		            noteModel.setChord(null);
//		            return true;
//		        }, "success");
//		        List<Note> notes = new ArrayList<>();
//		        notes.add(note1);
//		        notes.add(note2);
//		        //slur(note1, note2);
//		        return notes;
			return createFlam(origin, position, distanceFromMeasureStart);
		}

}
