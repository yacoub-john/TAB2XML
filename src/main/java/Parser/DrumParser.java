package Parser;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DrumParser {


	public void parseDrums(NodeList measures, ArrayList<Integer> nNPM, Document doc) {
		
		NodeList instrumentNames =  doc.getElementsByTagName("instrument-name");
		NodeList midiChannels =  doc.getElementsByTagName("midi-channel");
		
		
		for (int j = 0; j<instrumentNames.getLength(); j++) {

			Element instrumentName = (Element) instrumentNames.item(j);    
			String  NOI = instrumentName.getTextContent();
			System.out.println("instrument name is " + NOI);

		}
		
		

		for (int k = 0; k<midiChannels.getLength(); k++) {

			Element midiChannel = (Element) midiChannels.item(k);    
			String  NOC = midiChannel.getTextContent();
			System.out.println("midi-Channel " + NOC);

		


		}
	}
}


