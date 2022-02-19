package Parser;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import GUI.PreviewSheetMusicController;
import PlayNotes.JfugueTest;

public class GuitarParser {

	private ArrayList<String> notesList = new ArrayList<>();
	private ArrayList<String> alterList = new ArrayList<>();
	private ArrayList<String> fretList = new ArrayList<>();
	private ArrayList<String> stringList = new ArrayList<>();
	private ArrayList<String> noteLengthList = new ArrayList<>();
	public static JfugueTest jfugueTester = new JfugueTest();


	public void parseGuitar(NodeList measures, ArrayList<Integer> nNPM, Document doc) {

		for(int i = 0; i < measures.getLength(); i++) {


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


			NodeList staffLines  =  doc.getElementsByTagName("staff-lines");
			Element staffLine = (Element) staffLines.item(0);    
			String  NOST = staffLine.getTextContent();

			System.out.println("*********************");


			NodeList tuningSteps =  doc.getElementsByTagName("tuning-step");

			if (i == 1) {
				System.out.println("Number of staff Lines" + ": " + NOST);
				System.out.println("Staff detals: ");

			}


			for (int k = 0; k < tuningSteps.getLength(); k++) {

				if (i == 1) {

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

			NOD = "";
			NOF = "";
			NOS = "";
		}

		System.out.println("*********************");


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


		String[] alterExistList = new String[alters.getLength()];
		int alterExistCounter = 0;

		for(int i = 0; i < alterExistList.length; i++) {

			Element alter = (Element) alters.item(i);    
			String alterValue= alter.getTextContent();
			alterExistList[i] = alterValue;
		}


		for(int j = 0; j < notes.getLength(); j++) {


			NodeList singleNote = (NodeList) notes.item(j);
			NodeList technical = (NodeList) singleNote.item(1); //1: Technical  3: Number of Notes 

			/*
			 * When cord exits move the technical section one below
			 * Technical shows the details of each note
			 */

			if(technical.getLength() == 0) { 

				technical = (NodeList) singleNote.item(3);
			}

			if(technical.getLength() == 5) {

				alterList.add("Non");
			}

			else if(technical.getLength() == 7) {

				alterList.add(alterExistList[alterExistCounter]);
				alterExistCounter++;
			}


			String note = "";

			System.out.println("Note: " + (j+1));

			if(steps.item(j) != null) {

				Element step = (Element) steps.item(j);    
				String  stepValue= step.getTextContent();
				System.out.println("Step: " +  stepValue);
				note += stepValue;
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
					note += "w";
				}
				
				else if(typeValue.equals("half")) {
					note += "h";
				}
				
				else if(typeValue.equals("quarter")) {
					note += "q";
				}
				
				else if(typeValue.equals("eighth")) {
					note += "i";
				}
				
				else if(typeValue.equals("sixteenth")) {
					note += "s";
				}
				
				else if(typeValue.equals("32nd")) {
					note += "t";
				}
				
				else if(typeValue.equals("64th")) {
					note += "x";
				}
				
				else if(typeValue.equals("128th")) {
					note += "o";
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

		PreviewSheetMusicController.canvasNote.getNotes(stringList, fretList, nNPM, alterList, noteLengthList);
		jfugueTester.getNotes(notesList, nNPM);

	}


}
