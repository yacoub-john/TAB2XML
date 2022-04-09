package Parser;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import PlayNotes.JfugueForDrum;
import PlayNotes.MidiTest;

import GUI.PreviewSheetMusicController;

public class DrumParser {

	public ArrayList<String> instrumentNameList = new ArrayList<>();
	public ArrayList<String> instrumentIDList = new ArrayList<>();
	public ArrayList<Integer> midiChannelList = new ArrayList<>();
	public ArrayList<Integer> midiProgramList = new ArrayList<>();
	public ArrayList<Integer> midiUnpitchList = new ArrayList<>();
	public ArrayList<Double> midiVolumeList = new ArrayList<>();
	public ArrayList<Integer> midiPanlist = new ArrayList<>();

	public   ArrayList<String> notesList = new ArrayList<>();
	public static ArrayList<Integer> chordList = new ArrayList<>();
	public static ArrayList<String> noteHeadList = new ArrayList<>();
	public static ArrayList<Integer> noteLengthList = new ArrayList<>();
	public static ArrayList<String> stemList = new ArrayList<>();
	public static  ArrayList<String> noteInstrumentIDList = new ArrayList<>();
	public static MidiTest midiTester = new MidiTest();
	public static JfugueForDrum drumTest = new JfugueForDrum();
	
	public ArrayList<Integer> barlineList = new ArrayList<Integer>();
	public ArrayList<Integer> directionList = new ArrayList<Integer>();

	public void parseDrums(ArrayList<String> detials,NodeList measures, ArrayList<Integer> nNPM, Document doc) {
		
		 notesList = new ArrayList<>();
		 chordList = new ArrayList<>();
		 noteHeadList = new ArrayList<>();
		 noteLengthList = new ArrayList<>();
		 stemList = new ArrayList<>();
		 noteInstrumentIDList = new ArrayList<>();
		
		boolean hasBarline = false;
		boolean HasDirection = false;
		
		// Factor for SLUR and DOT
		
		NodeList barlines =  doc.getElementsByTagName("barline");
		NodeList barStyles =  doc.getElementsByTagName("bar-style");
		NodeList repeats =  doc.getElementsByTagName("repeat");
		
		NodeList directions=  doc.getElementsByTagName("direction");
		NodeList words =  doc.getElementsByTagName("words");

		
		for(int i=0; i < measures.getLength(); i++) {
			
			NodeList measure = (NodeList) measures.item(i); 
			
			for(int s = 0; s<measure.getLength(); s++) {

				Node notesMeasure = (Node) measure.item(s); // Note of First Measure
				//detials.add("Measure " + i + " Attribute: " + notesMeasure);

				if(notesMeasure.getNodeName().equals("barline")) {
					//detials.add("Has Barline!");
					hasBarline = true;
				}

				if(notesMeasure.getNodeName().equals("direction")) {
					//detials.add("Has Direction!");
					HasDirection = true;
				}		
			}
			
			if(barlines.item(i) != null && hasBarline) {

				Element barLine = (Element) barlines.item(i);    
				String bar = barLine.getAttribute("location");
				detials.add("Bar location of measure " + (i+1) + ": " + bar);
			}
			
			if(repeats.item(i) != null && hasBarline) {

				Element repeat = (Element) repeats.item(i);    
				String repeatDirection = repeat.getAttribute("direction");
				
				if(repeatDirection.equals("backward")) {
					String repeatValue = repeat.getAttribute("times");
					detials.add("Repeat Direction of measure " + (i+1) + ": " + repeatDirection);
					detials.add("Repeat Value of measure " + (i+1) + ": " + repeatValue);
				}
				else {
					detials.add("Repeat Direction of measure " + (i+1) + ": " + repeatDirection);
				}
				
			}
			
			if(barStyles.item(i) != null && hasBarline) {

				Element barStyle = (Element) barStyles.item(i);    
				String style = barStyle.getTextContent();
				detials.add("Barstyle of measure " + (i+1) + ": " + style);
			}
			
			hasBarline = false;
			HasDirection = false;
		}



		NodeList instrumentNames =  doc.getElementsByTagName("instrument-name");

		
		for (int j = 0; j<instrumentNames.getLength(); j++) {

			Element instrumentName = (Element) instrumentNames.item(j);    
			String  NOI = instrumentName.getTextContent();
			detials.add("Instrument " + (j+1) + ": " + NOI);

			instrumentNameList.add(NOI);
		}


		detials.add("==================================");

		NodeList midiChannels =  doc.getElementsByTagName("midi-channel");
		NodeList midiPrograms =  doc.getElementsByTagName("midi-program");
		NodeList midiUnpitcheds =  doc.getElementsByTagName("midi-unpitched");
		NodeList volumes =  doc.getElementsByTagName("volume");
		NodeList pans =  doc.getElementsByTagName("pan");

		for (int k = 0; k<midiChannels.getLength(); k++) {

			String instrumentID = "P1-I";

			Element midiChannel = (Element) midiChannels.item(k);    
			String  NOC = midiChannel.getTextContent();
			midiChannelList.add(Integer.parseInt(NOC));
			detials.add("midi-Channel: " + NOC);

			Element midiProgram = (Element) midiPrograms.item(k);    
			String  NOP = midiProgram.getTextContent();
			midiProgramList.add(Integer.parseInt(NOP));
			detials.add("midi-Program: " + NOP);


			Element midiUnpitched = (Element) midiUnpitcheds.item(k);    
			String  NOU = midiUnpitched.getTextContent();
			instrumentID += NOU;
			instrumentIDList.add(instrumentID);
			midiUnpitchList.add(Integer.parseInt(NOU));
			detials.add("midi-Unpitched: " + NOU);

			Element volume= (Element) volumes.item(k);    
			String  NOV = volume.getTextContent();
			midiVolumeList.add(Double.parseDouble(NOV));
			detials.add("Volume: " + NOV);

			Element pan = (Element) pans.item(k);    
			String  NOPA = pan.getTextContent();
			detials.add("Pan: " + NOPA);
			midiPanlist.add(Integer.parseInt(NOPA));


			detials.add("==================================");

		}

		detials.add("Midi Channels: " + midiChannelList);
		detials.add("Midi Program: " + midiProgramList);
		detials.add("Midi Unpitched: " + midiUnpitchList);
		detials.add("Midi Volumes: " + midiVolumeList);
		detials.add("Midi Pans: " + midiPanlist);
		detials.add("instrument ID List: " + instrumentIDList);
		detials.add("");

		for (int i=0; i<measures.getLength(); i++) {

			NodeList divisions  =  doc.getElementsByTagName("divisions");
			Element division = (Element) divisions.item(i);    
			String  NOD = division.getTextContent();
			detials.add("Number of divisions in measure " + (i + 1) + ": " + NOD);


			NodeList fifths =  doc.getElementsByTagName("fifths");
			String  NOF = "";

			if(fifths.item(i) != null) {

				Element fifth = (Element) fifths.item(i);    
				NOF = fifth.getTextContent();
				detials.add("Fifth of measure " + ( i+1) + ": " + NOF);
			}

			NodeList signs =  doc.getElementsByTagName("sign");
			String  NOS = "";

			if ( signs.item(i) != null ) {
				Element sign = (Element) signs.item(i);    
				NOS = sign.getTextContent();
				detials.add("Sign: " + NOS);
			}

			NodeList Lines =  doc.getElementsByTagName("line");


			if ( Lines.item(i) != null ) {
				Element Line = (Element) Lines.item(i);    
				String NOL = Line.getTextContent();
				detials.add("Line: " + NOL);
			}


			if(barStyles.item(i) != null) {

				Element barStyle = (Element) barStyles.item(i);    
				String	NOB = barStyle.getTextContent();
				detials.add("Bar styles in measure " + (i+1)  + NOB);
			}
			
			
			if(words.item(i) != null) {

				Element word = (Element) words.item(i);    
				String	NOW = word.getTextContent();
				detials.add("words in measure " + (i+1)  + NOW);
			}


		}

		NodeList notes = doc.getElementsByTagName("note");
		detials.add("");
		detials.add("Amount of notes is: " + notes.getLength());
		detials.add("--------------------");


		NodeList steps =  doc.getElementsByTagName("display-step");
		NodeList octaves = doc.getElementsByTagName("display-octave");
		NodeList durations = doc.getElementsByTagName("duration");
		NodeList noteInstrumID = doc.getElementsByTagName("instrument");
		NodeList voices = doc.getElementsByTagName("voice");
		NodeList types = doc.getElementsByTagName("type");
		NodeList stems= doc.getElementsByTagName("stem");
		NodeList noteHeads = doc.getElementsByTagName("notehead");


		String[] noteHeadExistList = new String[noteHeads.getLength()];
		int noteHeadExistsCounter = 0;

		for(int i = 0; i < noteHeadExistList.length; i++) {

			Element noteHead = (Element) noteHeads.item(i);    
			String noteHeadValue= noteHead.getTextContent();
			noteHeadExistList[i] = noteHeadValue;
		}


		for(int j = 0; j < notes.getLength(); j++) {


			NodeList singleNote = (NodeList) notes.item(j);		

			boolean hasChord = false;
			boolean hasNoteHead = false;

			for(int k = 0; k < singleNote.getLength(); k++) {

				Node singleNoteElement = (Node) singleNote.item(k);				

				if(singleNoteElement.getNodeName().equals("chord")) {
					hasChord = true;
				}

				if(singleNoteElement.getNodeName().equals("notehead")) {
					hasNoteHead = true;
				}


			}

			String note = "";

			detials.add("Note: " + (j+1));


			if(hasChord) {
				detials.add("Chord: 0");
				chordList.add(0);

			}

			else {
				detials.add("Chord: 1");
				chordList.add(1);
			}


			if(octaves.item(j) != null) {

				Element octave = (Element) octaves.item(j);    
				String  octaveValue = octave.getTextContent();
				detials.add("Octave: " +  octaveValue);
				note += octaveValue;
			}

			if(steps.item(j) != null) {

				Element step = (Element) steps.item(j);    
				String  stepValue= step.getTextContent();
				detials.add("Step: " +  stepValue);
				note += stepValue;
			}


			if(durations.item(j) != null) {

				Element duration = (Element) durations.item(j);    
				String  durationValue = duration.getTextContent();
				detials.add("Duration: " +  durationValue);
				noteLengthList.add(Integer.parseInt(durationValue));
			}

			if(noteInstrumID.item(j) != null) {

				Element noteinstrumIDElement = (Element) noteInstrumID.item(j);    
				String  instrumIDText = noteinstrumIDElement.getAttribute("id");
				detials.add("Instrument ID: " +  instrumIDText);
				noteInstrumentIDList.add(instrumIDText);
			}

			if(voices.item(j) != null) {

				Element voice = (Element) voices.item(j);    
				String  voiceValue = voice.getTextContent();
				detials.add("Voice: " +  voiceValue);

			}

			if(types.item(j) != null) {

				Element type = (Element) types.item(j);    
				String  typeValue = type.getTextContent();
				detials.add("Type: " +  typeValue);
			}

			if(stems.item(j) != null) {

				Element stem = (Element) stems.item(j);    
				String  stemValue = stem.getTextContent();
				detials.add("Stem: " +  stemValue);
				stemList.add(stemValue);
			}

			if(hasNoteHead) {
				detials.add("Note Head: " +  noteHeadExistList[noteHeadExistsCounter]);
				noteHeadList.add(noteHeadExistList[noteHeadExistsCounter]);
				noteHeadExistsCounter++;
			}
			else {
				detials.add("Note Head: non" );
				noteHeadList.add("non");


			}

			detials.add("Note: " + note);
			notesList.add(note);
			detials.add("--------------------");		
		}

		detials.add("");
		detials.add("Notes: " + notesList);
		detials.add("Chords: " + chordList);
		detials.add("Note Heads: " + noteHeadList);
		detials.add("Notes Length: " + noteLengthList);
		detials.add("Stems: " + stemList);
		detials.add("Note Instrument ID: " + noteInstrumentIDList);
		detials.add("");

		for(int i = 0; i< nNPM.size(); i++) {
			if(i != 0) {
				nNPM.set(i, (nNPM.get(i) + nNPM.get(i-1)));
			}
		}

		drumTest.getNotes(notesList, chordList, noteHeadList, noteLengthList, stemList, noteInstrumentIDList, nNPM);
		PreviewSheetMusicController.canvasNote.getNotesDrums("Drumset", notesList, chordList, noteHeadList, noteLengthList, stemList, noteInstrumentIDList, nNPM);


	}
}


