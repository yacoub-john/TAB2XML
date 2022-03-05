package testCases;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Parser.XMLParser;
import java.io.*;
import java.nio.charset.*;
import org.apache.commons.io.*;



class testParser {
	
	public String readFile() throws IOException {
	    File file = new File("GuitarXML.txt");
	    return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	}

	@Test
	void testXMLParser1() {
		
		String parse = "<part-name>Guitar</part-name>";
		
		XMLParser xmlParser = new XMLParser();
		try {
			xmlParser.loadXMLFromString(parse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertEquals("Guitar",XMLParser.getInstrument());
	}
	
	@Test
	void testXMLParser2() {
		
		String parse = readFile(GuitarXML);
		
		XMLParser xmlParser = new XMLParser();
		try {
			xmlParser.loadXMLFromString(parse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(xmlParser.getnNPM().get(0));
		assertEquals(8,xmlParser.getnNPM().get(1),0.2);
		
	}

}

