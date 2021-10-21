package utility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import converter.note.GuitarNote;

public class GuitarUtils {

    public static boolean isValidName(String name) {
        return getValidGuitarNames().contains(name.strip());
    }

	public static Set<String> getValidGuitarNames() {
	    String[] names = GuitarNote.KEY_LIST;
	    HashSet<String> nameList = new HashSet<>();
	    nameList.addAll(Arrays.asList(names));
	    for (String name : names) {
	        nameList.add(name.toLowerCase());
	    }
	    // The guitar string can be nameless (standard tuning assumed)
	    nameList.add("");
	    return nameList;
	}
    
}
