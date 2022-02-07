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
                
                Node partName = scorePart.item(0);
                System.out.println(partName.ELEMENT_NODE);

                
                NodeList measures = doc.getElementsByTagName("measure");
                System.out.println("Amount of Measures is: " + measures.getLength());
                
                
                
                
                for(int i = 0; i < measures.getLength(); i++) {
                	
                	
                	
                }
                
                
        		System.setProperty("ExecutionEnv", "part-list");
        		String creator = getTestData("score-partwise","score-part",doc);
                System.out.println(creator);
	}
	
	
	private static String getTestData(String SectionName, String NodeName, Document doc) 
	{
	

		
		String env =System.getProperty("ExecutionEnv");
		//System.out.println("Environment is :" + env );
		String path ="object/"+SectionName+"/"+env+"/"+NodeName;
		System.out.println("Path is : " +path );
		
		String final_value = null;
		NodeList nodeList;
		Node node;
		XPath xPath;
		
		try
		{
				xPath= XPathFactory.newInstance().newXPath();
				nodeList=(NodeList) xPath.compile(path).evaluate(doc, XPathConstants.NODESET);
		}
		catch(Exception e)
		{
			return null;
		}
		
		for(int temp=0;temp<nodeList.getLength();temp++)
		{
			node=nodeList.item(temp);
			if(node.getNodeName().equals(NodeName))
			{
				final_value=node.getTextContent().trim();
			}
		}
		
		
		
		return final_value;
	}
	
	
	public static void loadXMLFromString(String xml) throws Exception
	{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xml));
	    getXml( builder.parse(is) );
	    
	    
	}
}
