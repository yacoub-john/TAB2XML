package utility;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import converter.Instrument;
import converter.note.NoteFactory;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DrumUtils {
    private static String IdToFullNamePath = "DrumIDtoFullName.csv";
    private static String NameRulesPath = "DrumNameRules.csv";
    private static String NameToIdPath = "DrumNameToID.csv";
    private static String PartInfoPath = "DrumPartInfo.csv";
    private static HashMap<String, String> NAME_CASE_SENSITIVITY_MAP = getNameCaseSensitivityMap();
    private static HashMap<String, String> NAME_TO_ID_MAP = getNameToIDMap();
    private static HashMap<String, String> ID_TO_FULL_NAME_MAP = getIDtoFullNameMap();
    private static HashMap<String, String> DISPLAY_STEP_MAP = getDisplayStepMap();
    private static HashMap<String, String> DISPLAY_OCTAVE_MAP = getDisplayOctaveMap();


    public static Set<String> DRUM_NAME_SET = getNameSet();
    public static Set<String> DRUM_PART_ID_SET = getPartIDSet();


    public static String getPartID(String partName, String noteSymbol) {
        String name = getTrueName(partName, noteSymbol);
        return name!=null ? NAME_TO_ID_MAP.get(name) : null;
    }
    public static String getPartID(String partName) {
        String name = getTrueName(partName);
        return name!=null ? NAME_TO_ID_MAP.get(name) : null;
    }
    public static String getFullName(String partID) {
        return ID_TO_FULL_NAME_MAP.get(partID.toUpperCase());
    }

    public static String getDisplayStep(String partID) {
        return DISPLAY_STEP_MAP.get(partID);
    }

    public static int getDisplayOctave(String partID) {
        return Integer.parseInt(DISPLAY_OCTAVE_MAP.get(partID));
    }

    public static boolean isValidName(String partName) {
        return getTrueName(partName)!=null;
    }

    public static boolean isSupportedName(String partName) {
        String partID = getPartID(partName);
        return partID != null && DRUM_PART_ID_SET.contains(partID);
    }

    private static String getTrueName(String partName, String noteSymbol) {
        if (partName.strip().equalsIgnoreCase("HH")) {
            if (noteSymbol.strip().equalsIgnoreCase("x"))
                return "HH";
            else if (noteSymbol.strip().equalsIgnoreCase("o"))
                return "hh";
        }
        return getTrueName(partName);
    }

    private static String getTrueName(String partName) {
        partName = partName.strip();
        if (DRUM_NAME_SET.contains(partName))
            return partName;
        else if (DRUM_NAME_SET.contains(partName.toUpperCase()) && !isCaseSensitive(partName.toUpperCase()))
            return partName.toUpperCase();
        else if (DRUM_NAME_SET.contains(partName.toLowerCase()) && !isCaseSensitive(partName.toLowerCase()))
            return partName.toLowerCase();
        return null;
    }

    /**
     *
     * @param partName precondition: must be a name which is contained (case-sensitive) in the first column of the file "DrumNameRules.csv"
     * @return
     */
    private static boolean isCaseSensitive(String partName) {
        return Boolean.parseBoolean(NAME_CASE_SENSITIVITY_MAP.get(partName));
    }

    private static Iterable<CSVRecord> parseCSV(String path) {
        Iterable<CSVRecord> records = null;
        try (Reader reader = new FileReader(DrumUtils.class.getResource(path).getFile())) {
            records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

    private static HashMap<String, String> getNameCaseSensitivityMap() {
        HashMap<String, String> nameCaseSensitivityMap = new HashMap<>();
        try (Reader reader = new FileReader(DrumUtils.class.getResource(NameRulesPath).getFile())) {
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : records) {
                nameCaseSensitivityMap.put(record.get("Name"), record.get("Case Sensitive"));
            }
            return nameCaseSensitivityMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nameCaseSensitivityMap;
    }

    private static HashMap<String, String> getNameToIDMap() {
        HashMap<String, String> nameToIdMap = new HashMap<>();
        try (Reader reader = new FileReader(DrumUtils.class.getResource(NameToIdPath).getFile())) {
            Iterable <CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : records) {
                nameToIdMap.put(record.get("Name"), record.get("Part ID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nameToIdMap;
    }

    private static HashMap<String, String> getIDtoFullNameMap() {
        HashMap<String, String> idToFullNameMap = new HashMap<>();
        try (Reader reader = new FileReader(DrumUtils.class.getResource(IdToFullNamePath).getFile())) {
            Iterable <CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : records) {
                idToFullNameMap.put(record.get("Part ID"), record.get("Full Name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idToFullNameMap;
    }

    private static HashMap<String, String> getDisplayStepMap() {
        HashMap<String, String> idToFullNameMap = new HashMap<>();
        try (Reader reader = new FileReader(DrumUtils.class.getResource(PartInfoPath).getFile())) {
            Iterable <CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : records) {
                idToFullNameMap.put(record.get("Part ID"), record.get("Display Step"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idToFullNameMap;
    }

    private static HashMap<String, String> getDisplayOctaveMap() {
        HashMap<String, String> idToFullNameMap = new HashMap<>();
        try (Reader reader = new FileReader(DrumUtils.class.getResource(PartInfoPath).getFile())) {
            Iterable <CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : records) {
                idToFullNameMap.put(record.get("Part ID"), record.get("Display Octave"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idToFullNameMap;
    }


    private static Set<String> getNameSet() {
        return getNameToIDMap().keySet();
    }

    private static Set<String> getPartIDSet() {
        return getDisplayStepMap().keySet();
    }
    
    public static double isDrumLineLikelihood(String name, String line, Instrument instrumentBias) {
	    double instrumentBiasWeight = 0.2;  // weight attached when we are told to have a bias for drum notes
	    double lineNameWeight = 0.5;  // weight attached when the line name is a drum line name
	    double noteGroupWeight = 0.3;   // ratio of notes that are drum notes vs {all other notes, both valid and invalid}
	
	    if (!DrumUtils.isValidName(name))
	        return 0;
	    double score = lineNameWeight + (instrumentBias==Instrument.DRUMS ? instrumentBiasWeight : 0);
	    line = line.replaceAll("\s", "");
	
	    int charGroups = 0;
	    Matcher charGroupMatcher = Pattern.compile("[^-]+").matcher(line);
	    while (charGroupMatcher.find())
	        charGroups++;
	
	    int noteGroups = 0;
	    Matcher noteGroupMatcher = Pattern.compile(NoteFactory.DRUM_NOTE_GROUP_PATTERN).matcher(line);
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


//	private static double adjustRawConfidenceScore(double confidence) {
//	    //Exponential Decay (increasing form)
//	    //https://people.richland.edu/james/lecture/m116/logs/models.html
//	    double lowerLimit = 0.5;
//	    double size = 0.5;
//	    return lowerLimit + 0.5*(1-Math.exp(-5*confidence));
//	}


	


	public static double isDrumMeasureLikelihood(List<String> lineList, List<String[]> lineNameList) {
	    double score = 0;
	    int lineCount = lineList.size();
	    for (int i=0; i<lineCount; i++) {
	        score += isDrumLineLikelihood(lineNameList.get(i)[0], lineList.get(i), Instrument.AUTO);
	    }
	    if (lineCount==0)
	        score += 1; //if there is risk of zero division error, assign the full weight
	    else
	        score += (score/lineCount);
	
	    return score;
	}


}
