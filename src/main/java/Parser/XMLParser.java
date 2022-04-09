package Parser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.fxmisc.richtext.CodeArea;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import GUI.MusicInfoController;

import java.io.StringReader;
import java.util.ArrayList;


public class XMLParser extends GuitarParser {
	
	public static String instrument = "";
	public static ArrayList<Integer> nNPM = new ArrayList<Integer>();
	public int numOfmeasures = 0;
	public NodeList out = null;
	public Document document = null;
	public static ArrayList<String> details;
	public static GuitarParser guitarParser = new GuitarParser();
	DrumParser drumParser = new DrumParser();

	
	
	public void getXml(Document doc) {


		doc.getDocumentElement().normalize();
		details = new ArrayList<String>();
		nNPM = new ArrayList<Integer>();
		document = doc;


		NodeList partNameList  =  doc.getElementsByTagName("part-name");
		String partName = "";

		for(int i = 0; i < partNameList.getLength(); i++) {

			Element child = (Element) partNameList.item(i);    
			String  st1 = child.getTextContent();
			System.out.println("Part " + (i + 1)  + ": " + st1);
			details.add("Part " + (i + 1)  + ": " + st1);
			if(i == 0) {
				
			 partName = st1;
			}

		}
		

		NodeList measures = doc.getElementsByTagName("measure"); 
		System.out.println("Amount of Measures is: " + measures.getLength());
		numOfmeasures  = measures.getLength();
		
		
		
		for(int i = 0; i < measures.getLength(); i++) {
			
			NodeList measure = (NodeList) measures.item(i); 
			System.out.println("Total number of notes in measure " + i +" is: " + (measure.getLength()-3)/2);
			details.add("Total number of notes in measure " + i +" is: " + (measure.getLength()-3)/2);

			nNPM.add((measure.getLength()-3)/2);
			
			
		}
		
        

		if(partName.equals("Guitar")) {
			instrument = "Guitar";
			guitarParser.parseGuitar(details,measures, nNPM, doc);
			
		}
		
		else if (partName.equals("Drumset")) {
			instrument = "Drumset";
			drumParser.parseDrums(details,measures, nNPM, doc);
		}
		
		out = measures;
		

	}
	
	public void loadXMLFromString(String xml) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		getXml( builder.parse(is) );


	}
	
	public ArrayList<String> getDetails() {
		
		return details;
	}
	
public ArrayList<Integer> getnNpm() {
		
		return nNPM;
	}
	
	
	

}
