package utility;

import converter.measure_line.GuitarMeasureLine;

public class GuitarUtils {

    public static boolean isValidName(String name) {
        return GuitarMeasureLine.NAME_LIST.contains(name.strip());
    }
    
}
