package converter.note;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import converter.Instrument;
import models.measure.note.notations.Notations;
import models.measure.note.notations.Slide;
import models.measure.note.notations.technical.HammerOn;
import models.measure.note.notations.technical.Harmonic;
import models.measure.note.notations.technical.Natural;
import models.measure.note.notations.technical.PullOff;
import models.measure.note.notations.technical.Technical;
import utility.AnchoredText;
import utility.DoubleDigitStyle;
import utility.Patterns;
import utility.Settings;

public class GuitarNoteFactory extends NoteFactory {
	
	public GuitarNoteFactory() {
		notePattern = Patterns.GUITAR_NOTE_PATTERN;
		noteGroupPattern = Patterns.GUITAR_NOTE_GROUP_PATTERN;
		connectorPattern = Patterns.GUITAR_NOTE_CONNECTOR;
	}

	@Override
	protected AnchoredText createConnector(List<TabNote> noteList, int idx, int endIdx, int endNote) {
		Matcher connectorMatcher = Pattern.compile(connectorPattern).matcher(noteText.substring(endNote, endIdx));
		AnchoredText connectorAT = new AnchoredText("", 0, 0);
		if (!connectorMatcher.find()) {
			if (endNote < endIdx)
				noteList.add(new InvalidNote(stringNumber, noteText.substring(endNote, endIdx), position + endNote,
						lineName, distanceFromMeasureStart + idx));
			// TODO Did commenting this affect errors with connectors? return noteList;
		} else {
			connectorAT.text = connectorMatcher.group();
			connectorAT.positionInLine = connectorMatcher.end();
		}
		return connectorAT;
	}
	
	@Override
	protected List<TabNote> createNote(String origin, int position, int distanceFromMeasureStart) {
	    List<TabNote> noteList = new ArrayList<>();
	    noteList.addAll(createGrace(origin, position, distanceFromMeasureStart));
	    if (!noteList.isEmpty()) return noteList;
	    TabNote harmonic = createHarmonic(origin, position, distanceFromMeasureStart);
	    if (harmonic!=null) {
	    	harmonic.distance ++; //So the distance is to the fret number, not the square bracket
	        noteList.add(harmonic);
	        return noteList;
	    }
	    TabNote fret = createFret(origin, position, distanceFromMeasureStart);
	    if (fret!=null) {
	        noteList.add(fret);
	        return noteList;
	    }
	    noteList.add((TabNote) new InvalidNote(stringNumber, origin, position, lineName, distanceFromMeasureStart));
	    return noteList;
	}

	protected GuitarNote createFret(String origin, int position, int distanceFromMeasureStart) {
		if (!origin.matches(Patterns.FRET))
			return null;
		if ((Settings.getInstance().ddStyle == DoubleDigitStyle.NOTE_ON_SECOND_DIGIT_STRETCH)
				|| (Settings.getInstance().ddStyle == DoubleDigitStyle.NOTE_ON_SECOND_DIGIT_NO_STRETCH))
			if (origin.length() == 2)
				distanceFromMeasureStart++;
		return instantiateNote(origin, position, distanceFromMeasureStart);
	}

	protected GuitarNote instantiateNote(String origin, int position, int distanceFromMeasureStart) {
			return new GuitarNote(stringNumber, origin, position, lineName, distanceFromMeasureStart);
	}
	
	protected List<TabNote> createGrace(String origin, int position, int distanceFromMeasureStart) {
		List<TabNote> noteList = new ArrayList<>();
		if (!origin.matches(Patterns.GRACE))
			return noteList;
		Matcher graceNoteMatcher = Pattern.compile("(?<=g)" + Patterns.FRET + "(?![0-9])").matcher(origin);
		Matcher gracePairMatcher = Pattern.compile("(?<!g])" + Patterns.FRET + "$").matcher(origin);
		Matcher relationshipMatcher = Pattern.compile("(?<=[0-9])[^0-9](?=[0-9])").matcher(origin);
		graceNoteMatcher.find();
		gracePairMatcher.find();
		relationshipMatcher.find();
		GuitarNote graceNote = createFret(graceNoteMatcher.group(), position + graceNoteMatcher.start(),
				distanceFromMeasureStart + graceNoteMatcher.start());
		GuitarNote gracePair = createFret(gracePairMatcher.group(), position + gracePairMatcher.start(),
				distanceFromMeasureStart + gracePairMatcher.start());
		String relationship = relationshipMatcher.group();
		if (relationship.equals("h"))
			hammerOn(graceNote, gracePair, true);
		else if (relationship.equals("p"))
			pullOff(graceNote, gracePair, true);
		grace(graceNote, gracePair);
		noteList.add(graceNote);
		noteList.add(gracePair);
		return noteList;
	}

	private boolean hammerOn(GuitarNote note1, GuitarNote note2, boolean onlyMessage) {
		String message = "success";
		boolean success = true;
		if (note1.getFret() > note2.getFret()) {
			int startIdx = note1.position;
			int endIdx = note2.position + note2.origin.length();
			message = "[2][" + startIdx + "," + endIdx + "]Hammer on \"h\" should go from a lower to a higher note.";
			success = false;
		}

		if (onlyMessage) {
			note1.addDecorator((noteModel) -> true, message);
			note2.addDecorator((noteModel) -> true, message);
			return true;
		}

		AtomicInteger hammerOnNum = new AtomicInteger();
		note1.addDecorator((noteModel) -> {
			Technical technical = getNonNullTechnical(noteModel);
			HammerOn hammerOn = new HammerOn("start");
			hammerOnNum.set(hammerOn.getNumber());
			if (technical.getHammerOns() == null)
				technical.setHammerOns(new ArrayList<>());
			technical.getHammerOns().add(hammerOn);
			return true;
		}, message);
		note2.addDecorator((noteModel) -> {
			Technical technical = getNonNullTechnical(noteModel);
			HammerOn hammerOn = new HammerOn("stop", hammerOnNum.get());
			if (technical.getHammerOns() == null)
				technical.setHammerOns(new ArrayList<>());
			technical.getHammerOns().add(hammerOn);
			return true;
		}, message);
		if (success)
			success = slur(note1, note2);
		return success;
	}

	private boolean pullOff(GuitarNote note1, GuitarNote note2, boolean onlyMessage) {
		String message = "success";
		boolean success = true;
		if (note1.getFret() < note2.getFret()) {
			int startIdx = note1.position;
			int endIdx = note2.position + note2.origin.length();
			message = "[2][" + startIdx + "," + endIdx + "]Pull off \"p\" should go from a higher to a lower note.";
			success = false;
		}

		if (onlyMessage) {
			note1.addDecorator((noteModel) -> true, message);
			note2.addDecorator((noteModel) -> true, message);
			return true;
		}

		AtomicInteger pullOffNum = new AtomicInteger();
		note1.addDecorator((noteModel) -> {
			Technical technical = getNonNullTechnical(noteModel);
			PullOff pullOff = new PullOff("start");
			pullOffNum.set(pullOff.getNumber());
			if (technical.getPullOffs() == null)
				technical.setPullOffs(new ArrayList<>());
			technical.getPullOffs().add(pullOff);
			return true;
		}, message);
		note2.addDecorator((noteModel) -> {
			Technical technical = getNonNullTechnical(noteModel);
			PullOff pullOff = new PullOff("stop", pullOffNum.get());
			if (technical.getPullOffs() == null)
				technical.setPullOffs(new ArrayList<>());
			technical.getPullOffs().add(pullOff);
			return true;
		}, message);
		if (success)
			success = slur(note1, note2);
		return success;
	}

	protected TabNote createHarmonic(String origin, int position, int distanceFromMeasureStart) {
		TabNote note;
		if (!origin.matches(Patterns.HARMONIC))
			return null;

		Matcher fretMatcher = Pattern.compile("(?<=\\[)[0-9]+(?=\\])").matcher(origin);
		fretMatcher.find();
		note = createFret(fretMatcher.group(), position + fretMatcher.start(), distanceFromMeasureStart);
		note.addDecorator((noteModel) -> {
			Technical technical = getNonNullTechnical(noteModel);
			technical.setHarmonic(new Harmonic(new Natural()));
			return true;
		}, "success");

		return note;
	}

	@Override
	protected void addRelationship(TabNote note1, TabNote note2, String relationship) {
		switch (relationship.toLowerCase()) {
		case "h" -> hammerOn((GuitarNote) note1, (GuitarNote) note2, false);
		case "p" -> pullOff((GuitarNote) note1, (GuitarNote) note2, false);
		case "/", "\\", "s" -> slide((GuitarNote) note1, (GuitarNote) note2, relationship, false);
		}
	}

	private boolean slide(GuitarNote note1, GuitarNote note2, String symbol, boolean onlyMessage) {
		String message = "success";
		int startIdx = note1.position;
		int endIdx = note2.position + note2.origin.length();
		if (symbol.equals("/") && note1.getFret() > note2.getFret()) {
			// first bracket is the message priority, second is position to be highlighted.
			// dont add a second bracket if you only want the individual notes to be
			// highlighted
			message = "[2][" + startIdx + "," + endIdx + "]Slide up \"/\" should go from a lower to a higher note.";
		} else if (symbol.equals("\\") && note1.getFret() < note2.getFret()) {
			message = "[2][" + startIdx + "," + endIdx + "]Slide down \"/\" should go from a higher to a lower note.";
		}

		if (onlyMessage) {
			note1.addDecorator((noteModel) -> true, message);
			note2.addDecorator((noteModel) -> true, message);
			return true;
		}

		AtomicInteger slideNum = new AtomicInteger();
		note1.addDecorator((noteModel) -> {
			Notations notations = getNonNullNotations(noteModel);
			Slide slide = new Slide("start");
			slideNum.set(slide.getNumber());
			if (notations.getSlides() == null)
				notations.setSlides(new ArrayList<>());
			notations.getSlides().add(slide);
			return true;
		}, message);
		note2.addDecorator((noteModel) -> {
			Notations notations = getNonNullNotations(noteModel);
			Slide slide = new Slide("stop", slideNum.get());
			if (notations.getSlides() == null)
				notations.setSlides(new ArrayList<>());
			notations.getSlides().add(slide);
			return true;
		}, message);
		return true;
	}
}
