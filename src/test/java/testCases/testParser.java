package testCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

import Parser.XMLParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;



class testParser {
	
public String getGuitarXML() {
	
	String parse  = """
			<score-partwise version="3.1">
<identification>
	<creator type="composer"></creator>
</identification>
<part-list>
	<score-part id="P1">
		<part-name>Guitar</part-name>
	</score-part>
</part-list>
<part id="P1">
	<measure number="1">
		<attributes>
			<divisions>16</divisions>
			<key>
				<fifths>0</fifths>
			</key>
			<clef>
				<sign>TAB</sign>
				<line>5</line>
			</clef>
			<staff-details>
				<staff-lines>6</staff-lines>
				<staff-tuning line="1">
					<tuning-step>E</tuning-step>
					<tuning-octave>2</tuning-octave>
				</staff-tuning>
				<staff-tuning line="2">
					<tuning-step>A</tuning-step>
					<tuning-octave>2</tuning-octave>
				</staff-tuning>
				<staff-tuning line="3">
					<tuning-step>D</tuning-step>
					<tuning-octave>3</tuning-octave>
				</staff-tuning>
				<staff-tuning line="4">
					<tuning-step>G</tuning-step>
					<tuning-octave>3</tuning-octave>
				</staff-tuning>
				<staff-tuning line="5">
					<tuning-step>B</tuning-step>
					<tuning-octave>3</tuning-octave>
				</staff-tuning>
				<staff-tuning line="6">
					<tuning-step>E</tuning-step>
					<tuning-octave>4</tuning-octave>
				</staff-tuning>
			</staff-details>
		</attributes>
		<note>
			<pitch>
				<step>E</step>
				<octave>2</octave>
			</pitch>
			<duration>8</duration>
			<voice>1</voice>
			<type>eighth</type>
			<notations>
				<technical>
					<string>6</string>
					<fret>0</fret>
				</technical>
			</notations>
		</note>
		<note>
			<pitch>
				<step>B</step>
				<octave>2</octave>
			</pitch>
			<duration>8</duration>
			<voice>1</voice>
			<type>eighth</type>
			<notations>
				<technical>
					<string>5</string>
					<fret>2</fret>
				</technical>
			</notations>
		</note>
		<note>
			<pitch>
				<step>E</step>
				<octave>3</octave>
			</pitch>
			<duration>8</duration>
			<voice>1</voice>
			<type>eighth</type>
			<notations>
				<technical>
					<string>4</string>
					<fret>2</fret>
				</technical>
			</notations>
		</note>
		<note>
			<pitch>
				<step>G</step>
				<alter>1</alter>
				<octave>3</octave>
			</pitch>
			<duration>8</duration>
			<voice>1</voice>
			<type>eighth</type>
			<notations>
				<technical>
					<string>3</string>
					<fret>1</fret>
				</technical>
			</notations>
		</note>
		<note>
			<pitch>
				<step>B</step>
				<octave>3</octave>
			</pitch>
			<duration>8</duration>
			<voice>1</voice>
			<type>eighth</type>
			<notations>
				<technical>
					<string>2</string>
					<fret>0</fret>
				</technical>
			</notations>
		</note>
		<note>
			<pitch>
				<step>E</step>
				<octave>4</octave>
			</pitch>
			<duration>8</duration>
			<voice>1</voice>
			<type>eighth</type>
			<notations>
				<technical>
					<string>1</string>
					<fret>0</fret>
				</technical>
			</notations>
		</note>
		<note>
			<pitch>
				<step>B</step>
				<octave>3</octave>
			</pitch>
			<duration>8</duration>
			<voice>1</voice>
			<type>eighth</type>
			<notations>
				<technical>
					<string>2</string>
					<fret>0</fret>
				</technical>
			</notations>
		</note>
		<note>
			<pitch>
				<step>G</step>
				<alter>1</alter>
				<octave>3</octave>
			</pitch>
			<duration>8</duration>
			<voice>1</voice>
			<type>eighth</type>
			<notations>
				<technical>
					<string>3</string>
					<fret>1</fret>
				</technical>
			</notations>
		</note>
	</measure>
	<measure number="2">
		<attributes>
			<divisions>16</divisions>
			<key>
				<fifths>0</fifths>
			</key>
		</attributes>
		<note>
			<pitch>
				<step>E</step>
				<octave>4</octave>
			</pitch>
			<duration>64</duration>
			<voice>1</voice>
			<type>whole</type>
			<notations>
				<technical>
					<string>1</string>
					<fret>0</fret>
				</technical>
			</notations>
		</note>
		<note>
			<chord/>
			<pitch>
				<step>B</step>
				<octave>3</octave>
			</pitch>
			<duration>64</duration>
			<voice>1</voice>
			<type>whole</type>
			<notations>
				<technical>
					<string>2</string>
					<fret>0</fret>
				</technical>
			</notations>
		</note>
		<note>
			<chord/>
			<pitch>
				<step>G</step>
				<alter>1</alter>
				<octave>3</octave>
			</pitch>
			<duration>64</duration>
			<voice>1</voice>
			<type>whole</type>
			<notations>
				<technical>
					<string>3</string>
					<fret>1</fret>
				</technical>
			</notations>
		</note>
		<note>
			<chord/>
			<pitch>
				<step>E</step>
				<octave>3</octave>
			</pitch>
			<duration>64</duration>
			<voice>1</voice>
			<type>whole</type>
			<notations>
				<technical>
					<string>4</string>
					<fret>2</fret>
				</technical>
			</notations>
		</note>
		<note>
			<chord/>
			<pitch>
				<step>B</step>
				<octave>2</octave>
			</pitch>
			<duration>64</duration>
			<voice>1</voice>
			<type>whole</type>
			<notations>
				<technical>
					<string>5</string>
					<fret>2</fret>
				</technical>
			</notations>
		</note>
		<note>
			<chord/>
			<pitch>
				<step>E</step>
				<octave>2</octave>
			</pitch>
			<duration>64</duration>
			<voice>1</voice>
			<type>whole</type>
			<notations>
				<technical>
					<string>6</string>
					<fret>0</fret>
				</technical>
			</notations>
		</note>
	</measure>
</part>
</score-partwise>


			
			""";
	
	return parse;
	
	
	
}
	
	
	
	

	
	
	
//	File file = new File("/TAB2XML_G14/src/test/java/testCases/GuitarXML.txt");
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
		
		String parse = getGuitarXML();
		
		XMLParser xmlParser = new XMLParser();
		try {
			xmlParser.loadXMLFromString(parse);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		assertEquals("Guitar",XMLParser.instrument);
	}
	
	@Test
	void testXNumberOfNotes() {
		
		
		String parse = getGuitarXML();
		
		
		XMLParser xmlParser = new XMLParser();
		try {
			xmlParser.loadXMLFromString(parse);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		assertEquals(8,xmlParser.nNPM.get(0),0.2);
		assertEquals(14,xmlParser.nNPM.get(1),0.2);
		
	}
	
	@Test
	void testNumberOfMeasures() {
		
		
		String parse = getGuitarXML();
		
		
		XMLParser xmlParser = new XMLParser();
		try {
			xmlParser.loadXMLFromString(parse);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		assertEquals(2,xmlParser.numOfmeasures);
		
	}

}

