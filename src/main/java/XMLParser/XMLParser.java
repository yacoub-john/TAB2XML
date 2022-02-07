package XMLParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;


/*
 * Get the Document Builder
 * Get Document
 * Normalize the xml structure
 * Get all the element by the tag name
 * */
 public class XMLParser {
	 
	private static String xml = "";
	private static Document doc ;

	public static void getXml(Document doc) {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Get Document
            Document document = doc;

            // Normalize the xml structure
            document.getDocumentElement().normalize();

            // Get all the element by the tag name

            NodeList creatorList  = document.getElementsByTagName("creator");
            System.out.println(creatorList.getLength());
            
           // Node creator = creatorList.item(0);
            //System.out.println(creator.toString());
            
            
            Element child = (Element) creatorList.item(0);
           
            
            
            
            
            
            
            
            
            
            
            
            
           
//            for(int i = 0; i <laptopList.getLength(); i++) {
//                Node laptop = laptopList.item(i);
//            if(laptop.getNodeType() == Node.ELEMENT_NODE) {
//
//                 Element laptopElement = (Element) laptop;
//                  System.out.println("Laptop Name: " + laptopElement.getAttribute("identification"));
//               
//
//                   NodeList laptopDetails =  laptop.getChildNodes();
//                   for(int j = 0; j < laptopDetails.getLength(); j++){
//                       Node detail = laptopDetails.item(j);
//                       if(detail.getNodeType() == Node.ELEMENT_NODE) {
//                           Element detailElement = (Element) detail;
//                           System.out.println("     " + detailElement.getTagName() + ": " + detailElement.getAttribute("identification"));
//                       }
//
//                  }
//
//                }
//           }


     } catch (ParserConfigurationException e) {
          e.printStackTrace();
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
