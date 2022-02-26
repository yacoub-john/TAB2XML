package Parser;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import GUI.PreviewSheetMusicController;

public class DrumParser {


	public void parseDrums(NodeList measures, ArrayList<Integer> nNPM, Document doc) {

		NodeList instrumentNames =  doc.getElementsByTagName("instrument-name");



		for (int j = 0; j<instrumentNames.getLength(); j++) {

			Element instrumentName = (Element) instrumentNames.item(j);    
			String  NOI = instrumentName.getTextContent();
			System.out.println("instrument name is " + NOI);

		}

		System.out.println("==================================");

		NodeList midiChannels =  doc.getElementsByTagName("midi-channel");
		NodeList midiPrograms =  doc.getElementsByTagName("midi-program");
		NodeList midiUnpitcheds =  doc.getElementsByTagName("midi-unpitched");
		NodeList volumes =  doc.getElementsByTagName("volume");
		NodeList pans =  doc.getElementsByTagName("pan");

		for (int k = 0; k<midiChannels.getLength(); k++) {

			Element midiChannel = (Element) midiChannels.item(k);    
			String  NOC = midiChannel.getTextContent();
			System.out.println("midi-Channel: " + NOC);

			Element midiProgram = (Element) midiPrograms.item(k);    
			String  NOP = midiProgram.getTextContent();
			System.out.println("midi-Program: " + NOP);

			Element midiUnpitched = (Element) midiUnpitcheds.item(k);    
			String  NOU = midiUnpitched.getTextContent();
			System.out.println("midi-Unpitched: " + NOU);

			Element volume= (Element) volumes.item(k);    
			String  NOV = volume.getTextContent();
			System.out.println("Volume: " + NOV);

			Element pan = (Element) pans.item(k);    
			String  NOPA = pan.getTextContent();
			System.out.println("Pam: " + NOPA);


			System.out.println("==================================");

		}

		int numberOfMeasure = 1;

		for (int i=0; i<measures.getLength(); i++) {

			System.out.println("number of measure " + numberOfMeasure);
			numberOfMeasure ++;

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
		PreviewSheetMusicController.canvasNote.getNotesDrums("Drumset", nNPM);
	}
}


