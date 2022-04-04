package Parser;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import GUI.PreviewSheetMusicController;
import PlayNotes.JfugueTest;

public class GuitarParser {

	public ArrayList<String> notesList = new ArrayList<>();
	public ArrayList<String> alterList = new ArrayList<>();
	public ArrayList<Integer> chordList = new ArrayList<>();
	public ArrayList<String> fretList = new ArrayList<>();
	public ArrayList<String> stringList = new ArrayList<>();
	public ArrayList<String> noteLengthList = new ArrayList<>();
	
	public ArrayList<Integer> numOfSlur = new ArrayList<>();
	public ArrayList<String> slurNumber = new ArrayList<>();
	public ArrayList<String> slurType = new ArrayList<>();
	public ArrayList<String> slurPlacement = new ArrayList<>();
	
	
	public static JfugueTest jfugueTester = new JfugueTest();



	public void parseGuitar(NodeList measures, ArrayList<Integer> nNPM, Document doc) {

		for(int i = 0; i < measures.getLength(); i++) {


			NodeList divisions  =  doc.getElementsByTagName("divisions");
			Element division = (Element) divisions.item(i);    
			String NOD = division.getTextContent();
			System.out.println("Number of divisions in measure " + (i + 1) + ": " + NOD);


			String NOF = "";

			NodeList fifths =  doc.getElementsByTagName("fifths");
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


			NOD = "";
			NOF = "";
			NOS = "";
		}


		NodeList tuningSteps =  doc.getElementsByTagName("tuning-step");
		NodeList staffLines  =  doc.getElementsByTagName("staff-lines");
		Element staffLine = (Element) staffLines.item(0);    
		String  NOST = staffLine.getTextContent();

		System.out.println();
		System.out.println("*********************");
		System.out.println("Number of staff Lines" + ": " + NOST);
		System.out.println("Staff detals: ");

		for (int k = 0; k < tuningSteps.getLength(); k++) {

			if ( tuningSteps.item(k) != null) {

				Element tuningStep = (Element) tuningSteps.item(k);    
				String  NOTS = tuningStep.getTextContent();

				NodeList tuningOctaves =  doc.getElementsByTagName("tuning-octave");
				Element tuningOctave = (Element) tuningOctaves.item(k);    
				String  NOTO = tuningOctave.getTextContent();

				int x = k + 1;

				System.out.println("Line = " + x );
				System.out.println("tuning-step: " +  NOTS);
				System.out.println("tuning-octave: " +  NOTO);

			}

		}

		System.out.println("*********************");
		System.out.println();

		NodeList notes = doc.getElementsByTagName("note");
		System.out.println("Amount of notes is: " + notes.getLength());
		System.out.println();

		NodeList steps =  doc.getElementsByTagName("step");
		NodeList alters = doc.getElementsByTagName("alter");
		NodeList octaves = doc.getElementsByTagName("octave");
		NodeList durations = doc.getElementsByTagName("duration");
		NodeList voices = doc.getElementsByTagName("voice");
		NodeList types = doc.getElementsByTagName("type");
		NodeList strings= doc.getElementsByTagName("string");
		NodeList frets = doc.getElementsByTagName("fret");

		NodeList slurs = doc.getElementsByTagName("slur");


		String[] alterExistList = new String[alters.getLength()];
		int alterExistCounter = 0;

		for(int i = 0; i < alterExistList.length; i++) {

			Element alter = (Element) alters.item(i);    
			String alterValue= alter.getTextContent();
			alterExistList[i] = alterValue;
		}


		for(int j = 0; j < notes.getLength(); j++) {
			
			int noslurs =0;
			boolean hasSlurs = false;
			int counter =0;


			NodeList singleNote = (NodeList) notes.item(j);
			NodeList technical = (NodeList) singleNote.item(1); //1: Technical  3: Number of Notes 

			boolean hasChord = false;
			boolean hasAlter = false;
			
			String NOS = "";
			String POS = "";
			String TOS = "";
			

			 if (slurs.item(j) != null) {
				
				Element slur = (Element) slurs.item(j);    
				 NOS = slur.getAttribute("number");
				 TOS = slur.getAttribute("type");
				 POS = slur.getAttribute("placement");
				System.out.println("slur number is : " + NOS);
				System.out.println("slur type is : " + TOS);
				System.out.println("slur placement is : " + POS);
				
			 }
			
			

			//Checks if the current note has a chord or attribute 

			for(int k = 0; k < singleNote.getLength(); k++) {

				Node singleNoteElement = (Node) singleNote.item(k);

				if(singleNoteElement.getNodeName().equals("chord")) {
					hasChord = true;
				}
				
			
				
			
				
				if(singleNoteElement.getNodeName().equals("notations")) {
					NodeList notation = (NodeList)singleNoteElement;
					for(int l=0; l<notation.getLength(); l++) {
						if(notation.item(l).getNodeName().equals("slur")) {
							hasSlurs = true;
							noslurs++;
							
							
							
							
							
						}
					}
				}
				
			}	
    
			if(hasSlurs) {
				
				    numOfSlur.add(noslurs);
					slurNumber.add(NOS);
					slurType.add(TOS);
					slurPlacement.add(POS);
					
					
				
				
			}
			
			else {
				
				numOfSlur.add(0);
				slurNumber.add("NAN");
				slurType.add("NAN");
				slurPlacement.add("NAN");
				
			}
	

			if(hasChord) {
				chordList.add(0);
			}

			else {
				chordList.add(1);
			}



			/*
			 * When cord exits move the technical section one below
			 * Technical shows the details of each note
			 */

			if(hasChord) { 

				technical = (NodeList) singleNote.item(3);

			}

			//Checks if the current note has a alter alter attribute (part of technical list)
			for(int k = 0; k < technical.getLength(); k++) {

				Node technicalElement = (Node) technical.item(k);

				if(technicalElement.getNodeName().equals("alter")) {
					hasAlter = true;
				}


			}

			if(hasAlter) {

				alterList.add(alterExistList[alterExistCounter]);
				alterExistCounter++;
			}

			else {

				alterList.add("Non");
			}


			String note = "";

			System.out.println("Note: " + (j+1));

			if(steps.item(j) != null) {

				Element step = (Element) steps.item(j);    
				String  stepValue= step.getTextContent();
				System.out.println("Step: " +  stepValue);
				note += stepValue;
			}

			if(hasAlter && alterList.get(j).equals("1")) {
				note += "#";
			}

			if(hasAlter && alterList.get(j).equals("-1")) {
				note += "b";
			}
			System.out.println("Alter: " +  alterList.get(j));

			if(octaves.item(j) != null) {

				Element octave = (Element) octaves.item(j);    
				String  octaveValue = octave.getTextContent();
				System.out.println("Octave: " +  octaveValue);
				note += octaveValue;
			}

			if(durations.item(j) != null) {

				Element duration = (Element) durations.item(j);    
				String  durationValue = duration.getTextContent();
				System.out.println("Duration: " +  durationValue);
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
				noteLengthList.add(typeValue);

				/*Duration 		Character
				 * whole 	   		w
				 * half 				h
				 * quarter 			q
				 * eighth 			i
				 * sixteenth 		s
				 * thirty-second 	t
				 * sixty-fourth 		x
				 * one-twenty-eighth o
				 */

				if(typeValue.equals("whole")) {
					note += "W";
				}

				else if(typeValue.equals("half")) {
					note += "H";
				}

				else if(typeValue.equals("quarter")) {
					note += "Q";
				}

				else if(typeValue.equals("eighth")) {
					note += "I";
				}

				else if(typeValue.equals("16th")) {
					note += "S";
				}

				else if(typeValue.equals("32nd")) {
					note += "T";
				}

				else if(typeValue.equals("64th")) {
					note += "X";
				}

				else if(typeValue.equals("128th")) {
					note += "O";
				}

				else {
					note += "";
				}

			}

			if(strings.item(j) != null) {

				Element string = (Element) strings.item(j);    
				String  stringValue = string.getTextContent();
				System.out.println("String: " +  stringValue);
				stringList.add(stringValue);

			}

			if(frets.item(j) != null) {

				Element fret = (Element) frets.item(j);    
				String  fretValue = fret.getTextContent();
				System.out.println("Fret: " +  fretValue);
				fretList.add(fretValue);
			}

			
			
			notesList.add(note);
			System.out.println("--------------------");

			

		}
		
		System.out.println(numOfSlur);
		System.out.println(slurNumber);
		System.out.println(slurType);
		System.out.println(slurPlacement);


		for(int i = 0; i< nNPM.size(); i++) {
			if(i != 0) {
				nNPM.set(i, (nNPM.get(i) + nNPM.get(i-1)));
			}
		}


		jfugueTester.getNotes(notesList, nNPM, stringList, fretList, chordList, alterList);
		PreviewSheetMusicController.canvasNote.getNotesGuitar("Guitar",stringList, fretList, nNPM, alterList, noteLengthList, chordList);

	}

}
