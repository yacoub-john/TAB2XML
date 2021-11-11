package utility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import converter.note.NoteFactory;

public class GuitarUtils {
    public static String[] KEY_LIST = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};


    public static boolean isValidName(String name) {
        return getValidGuitarNames().contains(name.strip());
    }

	public static Set<String> getValidGuitarNames() {
	    String[] names = KEY_LIST;
	    HashSet<String> nameList = new HashSet<>();
	    HashSet<String> result = new HashSet<>();
	    nameList.addAll(Arrays.asList(names));
	    for (String name : names) {
	        nameList.add(name.toLowerCase());
	    }
	    for (String name : nameList) {
	    	for (int octave = 0; octave < 10 ; octave++)
	    	{
	    		result.add(name + octave);
	    	}
	    }
	    result.addAll(nameList);
	    // The guitar string can be nameless (standard tuning assumed)
	    result.add("");
	    return result;
	}
    
	public static String toOneString(String[][] tuning)
	{
		String result = "";
		Settings s = Settings.getInstance();
		for (int i=0; i< 6; i++)
		{
			for (int j=0; j< 2; j++)
				result += s.guitarTuning[i][j];
			if (i < 5)result += ",";
		}
		return result;
	}
	
	public static double isGuitarMeasureLikelihood(List<String> lineList, List<String[]> lineNameList) {
	    double score = 0;
	    int lineCount = lineList.size();
	    for (int i=0; i<lineCount; i++) {
	        score += isGuitarLineLikelihood(lineNameList.get(i)[0], lineList.get(i));
	    }
	    if (lineCount==0)
	    	//TODO This should really be an exception
	        score += 1; //if there is risk of zero division error, assign the full weight
	    else
	        score += (score/lineCount);
	
	    return score;
	}
	
//	public static double isBassMeasureLikelihood(List<String> lineList, List<String[]> lineNameList) {
//	    double withinSizeBias = 0.1;  //weight for if the number of lines in this measure is within the size cap of Bass measures
//	
//	    //---------this code block must be the exact same as isGuitarMeasureLikelihood (except the PREV_MEASURE_TYPE part)
//	    double guitarScore = isGuitarMeasureLikelihood(lineList, lineNameList);
//	    double bassScore = guitarScore;
//	    if (lineList.size()>=BassMeasure.MIN_LINE_COUNT && lineList.size()<=BassMeasure.MAX_LINE_COUNT)
//	        bassScore += withinSizeBias;
//	    return bassScore;
//	}
	
	public static double isGuitarLineLikelihood(String name, String line) {
	    double lineNameWeight = 0.5;  // weight attached when the line name is a guitar line name
	    double noteGroupWeight = 0.5;   // ratio of notes that are guitar notes vs {all other notes, both valid and invalid}
	
	    if (!GuitarUtils.isValidName(name))
	        return 0;
	    double score = lineNameWeight;
	    line = line.replaceAll("\s", "");
	
	    int charGroups = 0;
	    Matcher charGroupMatcher = Pattern.compile("[^-]+").matcher(line);
	    while (charGroupMatcher.find())
	        charGroups++;
	
	    int noteGroups = 0;
	    Matcher noteGroupMatcher = Pattern.compile(Patterns.GUITAR_NOTE_GROUP_PATTERN).matcher(line);
	    while (noteGroupMatcher.find()) {
	        //in-case a guitar note group has -'s inside it (e.g 5---h3 is a valid guitar note group for a hammer on,
	        // but will distort the ratio of character group to note group because one note group contains 2 character groups)
	        charGroupMatcher = Pattern.compile("[^-]+").matcher(line);
	        while(charGroupMatcher.find())
	            noteGroups++;
	    }
	    if (charGroups==0)
	        score += noteGroupWeight;
	    else
	        score += ((double) noteGroups/(double) charGroups)*noteGroupWeight;
	    return score;
	}

}
