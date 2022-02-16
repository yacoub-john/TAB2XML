package XMLParser;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GuitarParser {

	private static ArrayList<String> notesList = new ArrayList<>();
	private ArrayList<String> fret = new ArrayList<>();
	private ArrayList<String> string = new ArrayList<>();

	public static void parseGuitar(NodeList measures, ArrayList<Integer> nNPM, Document doc) {
		
		
		for(int k = 0; k < nNPM.size(); k++ ) {
			
			System.out.println("Measure: " + k + "number of notes: " + nNPM.get(k));
		}

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

			System.out.println("*********************");


			NodeList notes = doc.getElementsByTagName("note");
			System.out.println("Amount of notes is: " + notes.getLength());

			NodeList steps =  doc.getElementsByTagName("step");
			NodeList octaves = doc.getElementsByTagName("octave");
			NodeList durations = doc.getElementsByTagName("duration");
			NodeList voices = doc.getElementsByTagName("voice");
			NodeList types = doc.getElementsByTagName("type");
			NodeList strings= doc.getElementsByTagName("string");
			NodeList frets = doc.getElementsByTagName("fret");



			for(int j = 0; j < notes.getLength(); j++) {

				String note = "";
				
				System.out.println("Note: " + ( j+1));
			
				if(steps.item(j) != null) {

					Element step = (Element) steps.item(j);    
					String  stepValue = step.getTextContent();
					System.out.println("Step: " +  stepValue);
					note += stepValue;
				}


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
					note += typeValue.substring(0,1);

				}

				if(strings.item(j) != null) {

					Element string = (Element) strings.item(j);    
					String  stringValue = string.getTextContent();
					System.out.println("String: " +  stringValue);

				}

				if(frets.item(j) != null) {

					Element fret = (Element) frets.item(j);    
					String  fretValue = fret.getTextContent();
					System.out.println("Fret: " +  fretValue);

				}
	
				System.out.println(note);
				notesList.add(note);
				System.out.println("--------------------");
				
			}

			NOD = "";
			NOF = "";
			NOS = "";

		}
		
		PlayNotes.JfugueTest.GetNotes(notesList);
		
	}
	
	
}
