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
    public String inputFolder = System.getProperty("user.home");
    public int tsNum = 4;
    public int tsDen = 4;
    public String title = "";
    public String artist = "";
    
    public String[][] guitarTuning = {{"E","4"},{"B","3"},{"G","3"},{"D","3"},{"A","2"},{"E","2"}};
    public String[][] bassTuning = {{"G","2"},{"D","2"},{"A","1"},{"E","1"}};
    
    public DoubleDigitStyle ddStyle = DoubleDigitStyle.NOTE_ON_SECOND_DIGIT_STRETCH;
    
    public int guitarMeasureStartPadding = 3;
    public int drumsMeasureStartPadding = 0;
    
    private InstrumentSetting instrumentSetting = InstrumentSetting.AUTO;
    private Instrument detectedInstrument = Instrument.NONE;
    
    public Instrument getInstrument() {
    	switch (getInstrumentSetting()) {
    	case AUTO: return getDetectedInstrument();
    	case GUITAR: return Instrument.GUITAR;
    	case BASS: return Instrument.BASS;
    	case DRUMS: return Instrument.DRUMS;
    	default: return Instrument.GUITAR;
    	}
    }

	public InstrumentSetting getInstrumentSetting() {
		return instrumentSetting;
	}

	public void setInstrumentSetting(InstrumentSetting instrumentSetting) {
		this.instrumentSetting = instrumentSetting;
		if (instrumentSetting == InstrumentSetting.AUTO) detectedInstrument = Instrument.NONE;
	}

	private Instrument getDetectedInstrument() {
		return detectedInstrument;
	}

	public void setDetectedInstrument(Instrument detectedInstrument) {
		this.detectedInstrument = detectedInstrument;
	}
}