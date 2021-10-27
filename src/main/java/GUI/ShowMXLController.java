package GUI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.event.MouseOverTextEvent;

import converter.Converter;
import converter.Score;
import converter.measure.TabMeasure;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import utility.MusicXMLCreator;
import utility.Settings;

public class ShowMXLController extends Application {
	
	
	
	public File saveFile;
	
    private MainViewController mvc;

	public Highlighter highlighter;

	@FXML public CodeArea mxlText;

	@FXML  TextField gotoMeasureField;
	@FXML  Button goToline;


	public ShowMXLController() {

	}

	@FXML 
	public void initialize() {
		
	}

    public void setMainViewController(MainViewController mvcInput) {
    	mvc = mvcInput;
    }
    
    public void update() {
		mxlText.replaceText(mvc.converter.getMusicXML());
		mxlText.moveTo(0);
		mxlText.requestFollowCaret();
        mxlText.requestFocus();
	}
		@FXML
	private void saveMXLButtonHandle() {
		mvc.saveMXLButtonHandle();
	}

	@FXML
	private void handleGotoMeasure() {
		//TODO Must rewrite
		int measureNumber = Integer.parseInt(gotoMeasureField.getText() );
		if (!goToMeasure(measureNumber)) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Measure " + measureNumber + " could not be found.");
			alert.setHeaderText(null);
			alert.show();
		}
	}

    private boolean goToMeasure(int measureCount) {
    	//Pattern textBreakPattern = Pattern.compile("((\\R|^)[ ]*(?=\\R)){2,}|^|$");
    	Pattern mxlMeasurePattern = Pattern.compile("<measure number=\"" + measureCount + "\">");
        Matcher mxlMeasureMatcher = mxlMeasurePattern.matcher(mxlText.getText());
        
        if (mxlMeasureMatcher.find()) {
        	int pos = mxlMeasureMatcher.start();
        	mxlText.moveTo(pos);
        	mxlText.requestFollowCaret();
        	Pattern newLinePattern = Pattern.compile("\\R");
        	Matcher newLineMatcher = newLinePattern.matcher(mxlText.getText().substring(pos));
        	for (int i = 0; i < 30; i++) newLineMatcher.find();
        	int endPos = newLineMatcher.start();
        	mxlText.moveTo(pos+endPos);
        	mxlText.requestFollowCaret();
        	//mxlText.moveTo(pos);
            mxlText.requestFocus();
            return true;
            }
        else return false;        
    }
    
	@Override
	public void start(Stage primaryStage) throws Exception {

	}
}