package utility;

import converter.Instrument;
import converter.InstrumentSetting;

public class Settings {

    private Settings(){}
    private static final Settings instance = new Settings();
    public static Settings getInstance(){
        return instance;
    }
    
    public int errorSensitivity = 4;
    public String outputFolder = System.getProperty("user.home");
    public int tsNum = 4;
    public int tsDen = 4;
    public String title = "";
    public String artist = "";
    
    public String[][] guitarTuning = {{"E","4"},{"B","3"},{"G","3"},{"D","3"},{"A","2"},{"E","2"}};
    
    public DoubleDigitStyle ddStyle = DoubleDigitStyle.NOTE_ON_SECOND_DIGIT_STRETCH;
    
    public InstrumentSetting instrumentSetting = InstrumentSetting.AUTO;
    public Instrument detectedInstrument = Instrument.NONE;
    
    public Instrument getInstrument() {
    	switch (instrumentSetting) {
    	case AUTO: return detectedInstrument;
    	case GUITAR: return Instrument.GUITAR;
    	case BASS: return Instrument.BASS;
    	case DRUMS: return Instrument.DRUMS;
    	default: return Instrument.GUITAR;
    	}
    }
}