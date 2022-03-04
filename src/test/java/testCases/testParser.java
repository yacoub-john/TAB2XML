package testCases;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Parser.XMLParser;

import converter.Converter;


class testParser {

	@Test
	void testXMLParser1() throws Exception {
		
		String parse = "<part-name>Guitar</part-name>";
		
		XMLParser xmlParser = new XMLParser();
		xmlParser.loadXMLFromString(parse);
		
		assertEquals("Guitar",XMLParser.getInstrument());
		
	
		

	}

}
