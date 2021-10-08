package utility;

public class Settings {

    private Settings(){}
    private static final Settings instance = new Settings();
    public static Settings getInstance(){
        return instance;
    }
    
    public int errorSensitivity = 2;
    public String outputFolder = null;
    public int tsNum = 4;
    public int tsDen = 4;
    public String title = "";
    public String artist = "";
    
}