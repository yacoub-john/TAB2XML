package Parser;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import PlayNotes.MidiTest;

import GUI.PreviewSheetMusicController;

public class DrumParser {
	
	private ArrayList<String> instrumentNameList = new ArrayList<>();
	private ArrayList<String> instrumentIDList = new ArrayList<>();
	private ArrayList<Integer> midiChannelList = new ArrayList<>();
	private ArrayList<Integer> midiProgramList = new ArrayList<>();
	private ArrayList<Integer> midiUnpitchList = new ArrayList<>();
	private ArrayList<Double> midiVolumeList = new ArrayList<>();
	private ArrayList<Integer> midiPanlist = new ArrayList<>();
	
	private ArrayList<String> notesList = new ArrayList<>();
	private ArrayList<Integer> chordList = new ArrayList<>();
	private ArrayList<String> noteHeadList = new ArrayList<>();
	private ArrayList<Integer> noteLengthList = new ArrayList<>();
	private ArrayList<String> stemList = new ArrayList<>();
	private ArrayList<String> noteInstrumentIDList = new ArrayList<>();
	public static MidiTest midiTester = new MidiTest();


	public void parseDrums(NodeList measures, ArrayList<Integer> nNPM, Document doc) {

		NodeList instrumentNames =  doc.getElementsByTagName("instrument-name");
        


		for (int j = 0; j<instrumentNames.getLength(); j++) {

			Element instrumentName = (Element) instrumentNames.item(j);    
			String  NOI = instrumentName.getTextContent();
			System.out.println("Instrument " + (j+1) + ": " + NOI);
			
			instrumentNameList.add(NOI);
			

		}
		

		System.out.println("==================================");

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
			System.out.println("midi-Channel: " + NOC);

			Element midiProgram = (Element) midiPrograms.item(k);    
			String  NOP = midiProgram.getTextContent();
			midiProgramList.add(Integer.parseInt(NOP));
			System.out.println("midi-Program: " + NOP);
			

			Element midiUnpitched = (Element) midiUnpitcheds.item(k);    
			String  NOU = midiUnpitched.getTextContent();
			instrumentID += NOU;
			instrumentIDList.add(instrumentID);
			midiUnpitchList.add(Integer.parseInt(NOU));
			System.out.println("midi-Unpitched: " + NOU);

			Element volume= (Element) volumes.item(k);    
			String  NOV = volume.getTextContent();
			midiVolumeList.add(Double.parseDouble(NOV));
			System.out.println("Volume: " + NOV);

			Element pan = (Element) pans.item(k);    
			String  NOPA = pan.getTextContent();
			System.out.println("Pan: " + NOPA);
			midiPanlist.add(Integer.parseInt(NOPA));


			System.out.println("==================================");

		}
		
		System.out.println("Midi Channels: " + midiChannelList);
		System.out.println("Midi Program: " + midiProgramList);
		System.out.println("Midi Unpitched: " + midiUnpitchList);
		System.out.println("Midi Volumes: " + midiVolumeList);
		System.out.println("Midi Pans: " + midiPanlist);
        System.out.println("instrument ID List: " + instrumentIDList);
        System.out.println();
		
		for (int i=0; i<measures.getLength(); i++) {
			
			NodeList divisions  =  doc.getElementsByTagName("divisions");
			Element division = (Element) divisions.item(i);    
			String  NOD = division.getTextContent();
			System.out.println("Number of divisions in measure " + (i + 1) + ": " + NOD);


			NodeList fifths =  doc.getElementsByTagName("fifths");
			String  NOF = "";

			if(fifths.item(i) != null) {

				Element fifth = (Element) fifths.item(i);    
				NOF = fifth.getTextContent();
				System.out.println("Fifth of measure " + ( i+1) + ": " + NOF);
			}
			
			NodeList signs =  doc.getElementsByTagName("sign");
			String  NOS = "";

			if ( signs.item(i) != null ) {
				Element sign = (Element) signs.item(i);    
				NOS = sign.getTextContent();
				System.out.println("Sign: " + NOS);
			}
			
			NodeList Lines =  doc.getElementsByTagName("line");
			

			if ( Lines.item(i) != null ) {
				Element Line = (Element) Lines.item(i);    
				String NOL = Line.getTextContent();
				System.out.println("Line: " + NOL);
			}
		}
		
		NodeList notes = doc.getElementsByTagName("note");
		System.out.println();
		System.out.println("Amount of notes is: " + notes.getLength());
		System.out.println("--------------------");


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

			System.out.println("Note: " + (j+1));
			
			
			if(hasChord) {
				System.out.println("Chord: 0");
				chordList.add(0);

			}
			
			else {
				System.out.println("Chord: 1");
				chordList.add(1);
			}
			
			
			if(octaves.item(j) != null) {

				Element octave = (Element) octaves.item(j);    
				String  octaveValue = octave.getTextContent();
				System.out.println("Octave: " +  octaveValue);
				note += octaveValue;
			}
			
			if(steps.item(j) != null) {

				Element step = (Element) steps.item(j);    
				String  stepValue= step.getTextContent();
				System.out.println("Step: " +  stepValue);
				note += stepValue;
			}


			if(durations.item(j) != null) {

				Element duration = (Element) durations.item(j);    
				String  durationValue = duration.getTextContent();
				System.out.println("Duration: " +  durationValue);
				noteLengthList.add(Integer.parseInt(durationValue));
			}
			
			if(noteInstrumID.item(j) != null) {

				Element noteinstrumIDElement = (Element) noteInstrumID.item(j);    
				String  instrumIDText = noteinstrumIDElement.getAttribute("id");
				System.out.println("Instrument ID: " +  instrumIDText);
				noteInstrumentIDList.add(instrumIDText);
			}

			if(voices.item(j) != null) {

				Element voice = (Element) voices.item(j);    
				String  voiceValue = voice.getTextContent();
				System.out.println("Voice: " +  voiceValue);

			}

			if(types.item(j) != null) {

				Element type = (Element) types.item(j);    
				String  typeValue = type.getTextContent();
				System.out.println("Type: " +  typeValue);
			}

			if(stems.item(j) != null) {

				Element stem = (Element) stems.item(j);    
				String  stemValue = stem.getTextContent();
				System.out.println("Stem: " +  stemValue);
				stemList.add(stemValue);
			}

			if(hasNoteHead) {
				System.out.println("Note Head: " +  noteHeadExistList[noteHeadExistsCounter]);
				noteHeadList.add(noteHeadExistList[noteHeadExistsCounter]);
				noteHeadExistsCounter++;
			}
			else {
				System.out.println("Note Head: non" );
				noteHeadList.add("non");


			}
			
			System.out.println("Note: " + note);
			notesList.add(note);
			System.out.println("--------------------");		
		}

        System.out.println();
		System.out.println("Notes: " + notesList);
		System.out.println("Chords: " + chordList);
		System.out.println("Note Heads: " + noteHeadList);
		System.out.println("Notes Length: " + noteLengthList);
		System.out.println("Stems: " + stemList);
        System.out.println("Note Instrument ID: " + noteInstrumentIDList);
        System.out.println();
		
        midiTester.getNotes(notesList, chordList, noteHeadList, noteLengthList, stemList, noteInstrumentIDList);
		PreviewSheetMusicController.canvasNote.getNotesDrums("Drumset", nNPM);
	}
}


