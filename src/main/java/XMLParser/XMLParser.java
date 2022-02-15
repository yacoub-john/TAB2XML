package XMLParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;


public class XMLParser {
	

	public static void getXml(Document doc) {


		doc.getDocumentElement().normalize();


		NodeList partNameList  =  doc.getElementsByTagName("part-name");
		String partName = "";

		for(int i = 0; i < partNameList.getLength(); i++) {

			Element child = (Element) partNameList.item(i);    
			String  st1 = child.getTextContent();
			System.out.println("Part " + (i + 1)  + ": " + st1);
			
			if(i == 0) {
				
			 partName = st1;
			}

		}
		

		NodeList measures = doc.getElementsByTagName("measure"); 
		System.out.println("Amount of Measures is: " + measures.getLength());
		ArrayList<Integer> nNPM = new ArrayList<Integer>();
		
		for(int i = 0; i < measures.getLength(); i++) {
			
			NodeList measure = (NodeList) measures.item(i); 
			
			System.out.println("Total number of notes in measure " + i +" is: " + (measure.getLength()-3)/2);
			nNPM.add((measure.getLength()-3)/2);
		}
		
	
		
		if(partName.equals("Guitar")) {
		
			GuitarParser.parseGuitar(measures, nNPM, doc);
			
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
