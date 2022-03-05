package testCases;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

import Parser.XMLParser;



class testParser {
	
	String parse = " <identification>\n"
			+ "    <creator type=\"composer\"></creator>\n"
			+ "  </identification>\n"
			+ "  <part-list>\n"
			+ "    <score-part id=\"P1\">\n"
			+ "      <part-name>Guitar</part-name>\n"
			+ "    </score-part>\n"
			+ "  </part-list>\n"
			+ "  <part id=\"P1\">\n"
			+ "    <measure number=\"1\">\n"
			+ "      <attributes>\n"
			+ "        <divisions>16</divisions>\n"
			+ "        <key>\n"
			+ "          <fifths>0</fifths>\n"
			+ "        </key>\n"
			+ "        <clef>\n"
			+ "          <sign>TAB</sign>\n"
			+ "          <line>5</line>\n"
			+ "        </clef>\n"
			+ "        <staff-details>\n"
			+ "          <staff-lines>6</staff-lines>\n"
			+ "          <staff-tuning line=\"1\">\n"
			+ "            <tuning-step>E</tuning-step>\n"
			+ "            <tuning-octave>2</tuning-octave>\n"
			+ "          </staff-tuning>\n"
			+ "          <staff-tuning line=\"2\">\n"
			+ "            <tuning-step>A</tuning-step>\n"
			+ "            <tuning-octave>2</tuning-octave>\n"
			+ "          </staff-tuning>\n"
			+ "          <staff-tuning line=\"3\">\n"
			+ "            <tuning-step>D</tuning-step>\n"
			+ "            <tuning-octave>3</tuning-octave>\n"
			+ "          </staff-tuning>\n"
			+ "          <staff-tuning line=\"4\">\n"
			+ "            <tuning-step>G</tuning-step>\n"
			+ "            <tuning-octave>3</tuning-octave>\n"
			+ "          </staff-tuning>\n"
			+ "          <staff-tuning line=\"5\">\n"
			+ "            <tuning-step>B</tuning-step>\n"
			+ "            <tuning-octave>3</tuning-octave>\n"
			+ "          </staff-tuning>\n"
			+ "          <staff-tuning line=\"6\">\n"
			+ "            <tuning-step>E</tuning-step>\n"
			+ "            <tuning-octave>4</tuning-octave>\n"
			+ "          </staff-tuning>\n"
			+ "        </staff-details>\n"
			+ "      </attributes>\n"
			+ "      <note>\n"
			+ "        <pitch>\n"
			+ "          <step>E</step>\n"
			+ "          <octave>2</octave>\n"
			+ "        </pitch>\n"
			+ "        <duration>8</duration>\n"
			+ "        <voice>1</voice>\n"
			+ "        <type>eighth</type>\n"
			+ "        <notations>\n"
			+ "          <technical>\n"
			+ "            <string>6</string>\n"
			+ "            <fret>0</fret>\n"
			+ "          </technical>\n"
			+ "        </notations>\n"
			+ "      </note>\n"
			+ "      <note>\n"
			+ "        <pitch>\n"
			+ "          <step>B</step>\n"
			+ "          <octave>2</octave>\n"
			+ "        </pitch>\n"
			+ "        <duration>8</duration>\n"
			+ "        <voice>1</voice>\n"
			+ "        <type>eighth</type>\n"
			+ "        <notations>\n"
			+ "          <technical>\n"
			+ "            <string>5</string>\n"
			+ "            <fret>2</fret>\n"
			+ "          </technical>\n"
			+ "        </notations>\n"
			+ "      </note>\n"
			+ "      <note>\n"
			+ "        <pitch>\n"
			+ "          <step>E</step>\n"
			+ "          <octave>3</octave>\n"
			+ "        </pitch>\n"
			+ "        <duration>8</duration>\n"
			+ "        <voice>1</voice>\n"
			+ "        <type>eighth</type>\n"
			+ "        <notations>\n"
			+ "          <technical>\n"
			+ "            <string>4</string>\n"
			+ "            <fret>2</fret>\n"
			+ "          </technical>\n"
			+ "        </notations>\n"
			+ "      </note>\n"
			+ "      <note>\n"
			+ "        <pitch>\n"
			+ "          <step>G</step>\n"
			+ "          <alter>1</alter>\n"
			+ "          <octave>3</octave>\n"
			+ "        </pitch>\n"
			+ "        <duration>8</duration>\n"
			+ "        <voice>1</voice>\n"
			+ "        <type>eighth</type>\n"
			+ "        <notations>\n"
			+ "          <technical>\n"
			+ "            <string>3</string>\n"
			+ "            <fret>1</fret>\n"
			+ "          </technical>\n"
			+ "        </notations>\n"
			+ "      </note>\n"
			+ "      <note>\n"
			+ "        <pitch>\n"
			+ "          <step>B</step>\n"
			+ "          <octave>3</octave>\n"
			+ "        </pitch>\n"
			+ "        <duration>8</duration>\n"
			+ "        <voice>1</voice>\n"
			+ "        <type>eighth</type>\n"
			+ "        <notations>\n"
			+ "          <technical>\n"
			+ "            <string>2</string>\n"
			+ "            <fret>0</fret>\n"
			+ "          </technical>\n"
			+ "        </notations>\n"
			+ "      </note>\n"
			+ "      <note>\n"
			+ "        <pitch>\n"
			+ "          <step>E</step>\n"
			+ "          <octave>4</octave>\n"
			+ "        </pitch>\n"
			+ "        <duration>8</duration>\n"
			+ "        <voice>1</voice>\n"
			+ "        <type>eighth</type>\n"
			+ "        <notations>\n"
			+ "          <technical>\n"
			+ "            <string>1</string>\n"
			+ "            <fret>0</fret>\n"
			+ "          </technical>\n"
			+ "        </notations>\n"
			+ "      </note>\n"
			+ "      <note>\n"
			+ "        <pitch>\n"
			+ "          <step>B</step>\n"
			+ "          <octave>3</octave>\n"
			+ "        </pitch>\n"
			+ "        <duration>8</duration>\n"
			+ "        <voice>1</voice>\n"
			+ "        <type>eighth</type>\n"
			+ "        <notations>\n"
			+ "          <technical>\n"
			+ "            <string>2</string>\n"
			+ "            <fret>0</fret>\n"
			+ "          </technical>\n"
			+ "        </notations>\n"
			+ "      </note>\n"
			+ "      <note>\n"
			+ "        <pitch>\n"
			+ "          <step>G</step>\n"
			+ "          <alter>1</alter>\n"
			+ "          <octave>3</octave>\n"
			+ "        </pitch>\n"
			+ "        <duration>8</duration>\n"
			+ "        <voice>1</voice>\n"
			+ "        <type>eighth</type>\n"
			+ "        <notations>\n"
			+ "          <technical>\n"
			+ "            <string>3</string>\n"
			+ "            <fret>1</fret>\n"
			+ "          </technical>\n"
			+ "        </notations>\n"
			+ "      </note>\n"
			+ "    </measure>\n"
			+ "    <measure number=\"2\">\n"
			+ "      <attributes>\n"
			+ "        <divisions>16</divisions>\n"
			+ "        <key>\n"
			+ "          <fifths>0</fifths>\n"
			+ "        </key>\n"
			+ "      </attributes>\n"
			+ "      <note>\n"
			+ "        <pitch>\n"
			+ "          <step>E</step>\n"
			+ "          <octave>4</octave>\n"
			+ "        </pitch>\n"
			+ "        <duration>64</duration>\n"
			+ "        <voice>1</voice>\n"
			+ "        <type>whole</type>\n"
			+ "        <notations>\n"
			+ "          <technical>\n"
			+ "            <string>1</string>\n"
			+ "            <fret>0</fret>\n"
			+ "          </technical>\n"
			+ "        </notations>\n"
			+ "      </note>\n"
			+ "      <note>\n"
			+ "        <chord/>\n"
			+ "        <pitch>\n"
			+ "          <step>B</step>\n"
			+ "          <octave>3</octave>\n"
			+ "        </pitch>\n"
			+ "        <duration>64</duration>\n"
			+ "        <voice>1</voice>\n"
			+ "        <type>whole</type>\n"
			+ "        <notations>\n"
			+ "          <technical>\n"
			+ "            <string>2</string>\n"
			+ "            <fret>0</fret>\n"
			+ "          </technical>\n"
			+ "        </notations>\n"
			+ "      </note>\n"
			+ "      <note>\n"
			+ "        <chord/>\n"
			+ "        <pitch>\n"
			+ "          <step>G</step>\n"
			+ "          <alter>1</alter>\n"
			+ "          <octave>3</octave>\n"
			+ "        </pitch>\n"
			+ "        <duration>64</duration>\n"
			+ "        <voice>1</voice>\n"
			+ "        <type>whole</type>\n"
			+ "        <notations>\n"
			+ "          <technical>\n"
			+ "            <string>3</string>\n"
			+ "            <fret>1</fret>\n"
			+ "          </technical>\n"
			+ "        </notations>\n"
			+ "      </note>\n"
			+ "      <note>\n"
			+ "        <chord/>\n"
			+ "        <pitch>\n"
			+ "          <step>E</step>\n"
			+ "          <octave>3</octave>\n"
			+ "        </pitch>\n"
			+ "        <duration>64</duration>\n"
			+ "        <voice>1</voice>\n"
			+ "        <type>whole</type>\n"
			+ "        <notations>\n"
			+ "          <technical>\n"
			+ "            <string>4</string>\n"
			+ "            <fret>2</fret>\n"
			+ "          </technical>\n"
			+ "        </notations>\n"
			+ "      </note>\n"
			+ "      <note>\n"
			+ "        <chord/>\n"
			+ "        <pitch>\n"
			+ "          <step>B</step>\n"
			+ "          <octave>2</octave>\n"
			+ "        </pitch>\n"
			+ "        <duration>64</duration>\n"
			+ "        <voice>1</voice>\n"
			+ "        <type>whole</type>\n"
			+ "        <notations>\n"
			+ "          <technical>\n"
			+ "            <string>5</string>\n"
			+ "            <fret>2</fret>\n"
			+ "          </technical>\n"
			+ "        </notations>\n"
			+ "      </note>\n"
			+ "      <note>\n"
			+ "        <chord/>\n"
			+ "        <pitch>\n"
			+ "          <step>E</step>\n"
			+ "          <octave>2</octave>\n"
			+ "        </pitch>\n"
			+ "        <duration>64</duration>\n"
			+ "        <voice>1</voice>\n"
			+ "        <type>whole</type>\n"
			+ "        <notations>\n"
			+ "          <technical>\n"
			+ "            <string>6</string>\n"
			+ "            <fret>0</fret>\n"
			+ "          </technical>\n"
			+ "        </notations>\n"
			+ "      </note>\n"
			+ "    </measure>\n"
			+ "  </part>\n"
			+ "</score-partwise>";
	
	
//	public String getGuitarXML() throws FileNotFoundException {
//	
//	File file = new File("/TAB2XML_G14/GuitarXML.txt");
//	
//	Scanner scan = new Scanner(file);
//	
//	String parse = "";
//	
//	
//	while (scan.hasNextLine()) {
//		
//		parse = parse.concat(scan.nextLine() + "\n");
//		
//	}
//	
//	scan.close();
//	     
//	return parse;
//	}
	
	
	
	
	

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
	
//	@Test
//	void testXMLParser2()  {
		
//		String parse = getGuitarXML();
		
//		XMLParser xmlParser = new XMLParser();
//		try {
//			xmlParser.loadXMLFromString(this.parse);
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
//		
//		
//		assertEquals(8,xmlParser.getnNPM().get(0),0.2);
//		
//	}

}

