package GUI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.event.MouseOverTextEvent;

import converter.Converter;
import converter.Score;
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

	@FXML public CodeArea mainText;

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
		mainText.replaceText(mvc.converter.getMusicXML());

	}
		@FXML
	private void saveMXLButtonHandle() {
//		Parent root;
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("GUI/convertWindow.fxml"));
//			root = loader.load();
//			ConvertWindowController controller = loader.getController();
//			controller.setMainViewController(this);
//			convertWindow = this.openNewWindow(root, "ConversionOptions");
//		} catch (IOException e) {
//			Logger logger = Logger.getLogger(getClass().getName());
//			logger.log(Level.SEVERE, "Failed to create new Window.", e);
//		}
	}

	@FXML
	private void handleGotoMeasure() {
		//TODO Must rewrite
		int measureNumber = Integer.parseInt( gotoMeasureField.getText() );
		if (!highlighter.goToMeasure(measureNumber)) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Measure " + measureNumber + " could not be found.");
			alert.setHeaderText(null);
			alert.show();
		}

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

	}
}