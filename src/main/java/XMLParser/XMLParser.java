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
			System.out.println("Part " + i + ": " + st1);


		}

		NodeList measures = doc.getElementsByTagName("measure");
		System.out.println("Amount of Measures is: " + measures.getLength());
		
		int note = 0;

		for(int i = 0; i < measures.getLength(); i++) {

			NodeList divisions  =  doc.getElementsByTagName("divisions");
			Element division = (Element) divisions.item(i);    
			String  NOD = division.getTextContent();
			System.out.println("Number of divisions in measure " + i + ": " + NOD);


			NodeList fifths =  doc.getElementsByTagName("fifths");
			String  NOF = "";
			if(fifths.item(i) != null) {
			
				Element fifth = (Element) fifths.item(i);    
				NOF = fifth.getTextContent();
				System.out.println("Number of fifths in measure " + i + ": " + NOF);
			}
			
			NodeList signs =  doc.getElementsByTagName("sign");
			String  NOS = "";

			if ( signs.item(i) != null ) {
				Element sign = (Element) signs.item(i);    
				NOS = sign.getTextContent();
				System.out.println("Sign Measure " + i + ": " + NOS);
			}
			
			
			
			NodeList notes = doc.getElementsByTagName("note");
			System.out.println("Amount of notes is: " + notes.getLength());
			
			NodeList steps =  doc.getElementsByTagName("step");
			System.out.println("Amount of steps is: " + steps.getLength());

			for(int j = 0; j < notes.getLength(); j++) {
				
			
				if(steps.item(j) != null) {
				
					Element step = (Element) steps.item(j);    
					String  stepValue= step.getTextContent();
					System.out.println("Step " + (j+1) + ": " +  stepValue);
				
				}
				
			}

			NOD = "";
			NOF = "";
			NOS = "";
			
			
			

		}








		for(int i = 0; i < scorePart.getLength(); i++) {

			Node partname = scorePart.item(i);
			System.out.println(partname.getAttributes());

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
