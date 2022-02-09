package XMLParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


import java.io.StringReader;
import java.util.ArrayList;



public class XMLParser {
	
	private ArrayList<String> notes = new ArrayList<>();


	public static void getXml(Document doc) {


		doc.getDocumentElement().normalize();


		NodeList scorePart = doc.getElementsByTagName("score-part");
		System.out.println("Number of score parts: " + scorePart.getLength());



		NodeList creatorList  =  doc.getElementsByTagName("creator");
		System.out.println("Number of creators: " + creatorList.getLength());



		NodeList partNameList  =  doc.getElementsByTagName("part-name");
		System.out.println("Number of parts: " + scorePart.getLength());

		for(int i = 0; i < partNameList.getLength(); i++) {

			Element child = (Element) partNameList.item(i);    
			String  st1 = child.getTextContent();
			System.out.println("Part " + (i + 1)  + ": " + st1);


		}

		NodeList measures = doc.getElementsByTagName("measure");
		System.out.println("Amount of Measures is: " + measures.getLength());
		int numberOfMeasures = 0;
	

		for(int i = 0; i < measures.getLength(); i++) {
			
			numberOfMeasures ++;

			NodeList divisions  =  doc.getElementsByTagName("divisions");
			Element division = (Element) divisions.item(i);    
			String  NOD = division.getTextContent();
			System.out.println("Number of divisions in measure " + numberOfMeasures + ": " + NOD);


			NodeList fifths =  doc.getElementsByTagName("fifths");
			String  NOF = "";
			if(fifths.item(i) != null) {
			
				Element fifth = (Element) fifths.item(i);    
				NOF = fifth.getTextContent();
				System.out.println("Fifth of measure " + numberOfMeasures + ": " + NOF);
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
			
			if (numberOfMeasures == 1) {
				System.out.println("Number of staff Lines" + ": " + NOST);
				System.out.println("Staff detals: ");
				
			}
			
			
			for (int k = 0; k < tuningSteps.getLength(); k++) {
				
				if (numberOfMeasures == 1) {
				
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
								
				System.out.println("Note: " + ( j+1));
				
				if(steps.item(j) != null) {
				
					Element step = (Element) steps.item(j);    
					String  stepValue= step.getTextContent();
					System.out.println("Step: " +  stepValue);
				
				}
				
				
				if(octaves.item(j) != null) {
					
					Element octave = (Element) octaves.item(j);    
					String  octaveValue = octave.getTextContent();
					System.out.println("Octave: " +  octaveValue);
				
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
				
				System.out.println("--------------------");
				
				
			}

			NOD = "";
			NOF = "";
			NOS = "";
			
		}


	}

	public static void loadXMLFromString(String xml) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		getXml( builder.parse(is) );


	}









}
