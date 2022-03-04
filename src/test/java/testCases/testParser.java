package testCases;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Parser.XMLParser;



class testParser {

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

}
