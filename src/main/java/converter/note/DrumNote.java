package converter.note;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import converter.Instrument;
import converter.measure_line.TabDrumString;
import models.measure.note.Unpitched;
import utility.DrumPiece;
import utility.DrumPieceInfo;
import utility.DrumUtils;
import utility.Settings;
import utility.ValidationError;

public class DrumNote extends TabNote{

    //String partID;
    private DrumPiece drumPiece;
    private DrumPieceInfo drumPieceInfo;
    //public static String COMPONENT_PATTERN = "[xoXOdDfF#]";

    public DrumNote (int stringNumber, String origin, int position, String lineName, int distanceFromMeasure){
        super(stringNumber, origin, position, lineName, distanceFromMeasure);
        this.instrument = Instrument.DRUMS;
        drumPiece = DrumUtils.getDrumPiece(lineName.strip(), origin.strip());
        if (drumPiece != null)
            TabDrumString.USED_DRUM_PARTS.add(drumPiece);
        //TODO Debug voice 2 issues
        //if ((drumPiece == DrumPiece.Bass_Drum_1) || (drumPiece == DrumPiece.Bass_Drum_2))
        //    this.voice = 2;
        drumPieceInfo = DrumUtils.drumSet.get(drumPiece);
    }

    public DrumNote(DrumNote n)
    {
    	super(n);
    	this.drumPiece = n.drumPiece;
    	this.drumPieceInfo = n.drumPieceInfo;
    }

    @Override
    public models.measure.note.Note getModel(){ 
    	models.measure.note.Note noteModel = super.getModel();
        noteModel.setUnpitched(new Unpitched(drumPieceInfo.getStep(), drumPieceInfo.getOctave()));
        noteModel.setInstrument(new models.measure.note.Instrument(this.drumPieceInfo.getMidiID()));
        //TODO Should test better for bass drum here
        noteModel.setStem(drumPiece == DrumPiece.Bass_Drum_1 ? "down" : "up");
        String noteHead = this.origin.strip();
        if ((noteHead.equalsIgnoreCase("x")) || ((noteHead.equalsIgnoreCase("o") && drumPiece == DrumPiece.Open_Hi_Hat)))
            noteModel.setNotehead("x");
        return noteModel;
    }

    public List<ValidationError> validate() {
        super.validate();
        //int ERROR_SENSITIVITY = Settings.getInstance().errorSensitivity;

        for (NoteModelDecorator noteDecor : this.noteDecorMap.keySet()) {
            String resp = noteDecorMap.get(noteDecor);
            if (resp.equals("success")) continue;
            Matcher matcher = Pattern.compile("(?<=^\\[)[0-9](?=\\])").matcher(resp);
            matcher.find();
            int priority = Integer.parseInt(matcher.group());
            String message = resp.substring(matcher.end()+1);;
            int startIdx = this.position;
            int endIdx = this.position+this.origin.length();

            matcher = Pattern.compile("(?<=^\\[)[0-9]+,[0-9]+(?=\\])").matcher(message);
            if (matcher.find()) {
                String positions = matcher.group();
                matcher = Pattern.compile("[0-9]+").matcher(positions); matcher.find();
                startIdx = Integer.parseInt(matcher.group()); matcher.find();
                endIdx = Integer.parseInt(matcher.group());
                message = message.substring(matcher.end()+2);
            }
            addError(message, priority, getRanges());
        }

        return errors;
    }

	@Override
	public TabNote copy() {
		return new DrumNote(this);
	}

}