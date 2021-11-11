package converter.note;

public class BassNoteFactory extends GuitarNoteFactory {
	
	public BassNoteFactory() {
		super();
	}

	protected GuitarNote instantiateNote(String origin, int position, int distanceFromMeasureStart) {
			return new BassNote(stringNumber, origin, position, lineName, distanceFromMeasureStart);
	}
	
}
