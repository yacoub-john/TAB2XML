package utility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GuitarUtils {
    public static String[] KEY_LIST = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};


    public static boolean isValidName(String name) {
    	 
    	char c = name.charAt(name.length()-1);
    	
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
}
