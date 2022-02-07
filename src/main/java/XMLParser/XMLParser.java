package XMLParser;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import java.io.StringReader;


/*
 * Get the Document Builder
 * Get Document
 * Normalize the xml structure
 * Get all the element by the tag name
 * */
 public class XMLParser {
	 
	//private static String xml = "";
	//private static Document doc ;

	public static void getXml(Document doc) {
		
 
		
            // Normalize the xml structure

          //  document.getDocumentElement().normalize();

            // Get all the element by the tag name

          //  NodeList creatorList  = document.getElementsByTagName("creator");
          //  System.out.println(creatorList.getLength());
            
           // Node creator = creatorList.item(0);
            //System.out.println(creator.toString());
            
            
          //  Element child = (Element) creatorList.item(0);
           
            
            
            
            
            
            
            
            
            
            
            
            
           
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

            doc.getDocumentElement().normalize();
            
            
//            // Get all the element by the tag name
//            NodeList scorePartwise = doc.getElementsByTagName("score-partwise");
//            
//            NodeList scoreDetails =  scorePartwise.getChildNodes();
//            //Node Detail
//            
//            //NodeList scorePartwise = (NodeList) listObject.item(0);
//            Node indentification = scorePartwise.item(0);
//            //Node creator = indentification.item(0);
//            		
//            Element creatorType = (Element) indentification;
//            System.out.println(creatorType); 
//            		
//            for(int j = 0; j < laptopDetails.getLength(); j++){
//              Node detail = laptopDetails.item(j);
//              if(detail.getNodeType() == Node.ELEMENT_NODE) {
//                  Element detailElement = (Element) detail;
//                  System.out.println("     " + detailElement.getTagName() + ": " + detailElement.getAttribute("value"));
//              }
//            }
		
            
//            	NodeList scoreParts = doc.getElementsByTagName("score-partwise");
//            	
//            	for(int i = 0; i < scoreParts.getLength(); i++) {
//            		System.out.println(scoreParts.item(i));
//            	}
//            	
//            	//   NodeList identification = scoreParts.;
//                Node creator = identification.getFirstChild();
//                System.out.println("Creator is "  + creator.getTextContent());
                
                
//                NodeList idenList = scoreParts.getChildNodes();
//                Node iden = idenList.item(1);
//                
//                
//                NodeList creatorList = iden.getChildNodes();
//                Node creator = creatorList.item(1);
//              
//                
//                
//                if(creator.getNodeType() == Node.ELEMENT_NODE) {
//                    Element detailElement = (Element) creator;
//                    System.out.println("     " + detailElement.getTagName() + ": " + detailElement.getAttribute("value"));
//                }
//                
            
            
//          
//          if(scoreParts.getNodeType() == Node.ELEMENT_NODE) {
//
//
//              NodeList laptopDetails =  scoreParts.getChildNodes();
//              
//              for(int j = 0; j < laptopDetails.getLength(); j++){
//                  Node detail = laptopDetails.item(j);
//                  if(detail.getNodeType() == Node.ELEMENT_NODE) {
//                      Element detailElement = (Element) detail;
//                      System.out.println("     " + detailElement.getTagName() + ": " + j);
//                  }
//
//
//          } 
//      } 
            
        

      
            	
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
                
                	
                for(int i = 0; i < measures.getLength(); i++) {
                	
                    NodeList divisions  =  doc.getElementsByTagName("divisions");
                    Element division = (Element) divisions.item(i);    
                    String  NOD = division.getTextContent();
                    System.out.println("Number of divisions in measure " + i + ": " + NOD);
                    
                    
                    NodeList fifths =  doc.getElementsByTagName("fifths");
                    Element fifth = (Element) fifths.item(i);    
                    String  NOF = fifth.getTextContent();
                    System.out.println("Number of fifths in measure " + i + ": " + NOF);
                    
                 
                    NodeList signs =  doc.getElementsByTagName("sign");
                    String  NOS = "";
                    
                    if ( signs.item(i) != null ) {
                    Element sign = (Element) signs.item(i);    
                      NOS = sign.getTextContent();
                    System.out.println("sign of the measure " + i + ": " + NOS);
                    }
                    
                    NOD = "";
                    NOF = "";
                    NOS = "";
                    
                    
                    
                }
                
                
                
       
//                	
//                	NodeList measure = measures.item(0).getChildNodes();
//                	NodeList attributes = measure.item(1).getChildNodes();
//                	Element	divisions = (Element)attributes.item(0);
//                	String NOD = divisions.getTextContent();
//                	System.out.println("Number of divisions in " + "measure " + 0 + "is " + NOD);
                
                
                
                
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
