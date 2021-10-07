package GUI;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.prefs.Preferences;

import converter.Score;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import utility.MusicXMLCreator;

public class SettingsWindowController extends Application {

	private MainViewController mvc;
	//private static Window convertWindow = new Stage();
	Preferences prefs = Preferences.userNodeForPackage(MainApp.class);

	@FXML private ComboBox<String> errorSensitivity;
	@FXML private ComboBox<String> cmbNumerator;
	@FXML private ComboBox<String> cmbDenominator;

	public void setMainViewController(MainViewController mvcInput) {
		mvc = mvcInput;
	}

	@FXML private void handleErrorSensitivity() {
		prefs.put("errorSensitivity", errorSensitivity.getValue().toString() );
		changeErrorSensitivity(errorSensitivity.getValue().toString());
	}

	@FXML
	private void handleChangeFolder() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(new File("src"));
		File selected = dc.showDialog(MainApp.STAGE);
		mvc.outputFolderField.setText(selected.getAbsolutePath());

		prefs.put("outputFolder", selected.getAbsolutePath());
	}

	@FXML
	private void handleTSNumerator() {
		String value = cmbNumerator.getValue().toString();
		prefs.put("tsNumerator", value);
		Score.DEFAULT_BEAT_COUNT = Integer.parseInt(value);
	}
	@FXML
	private void handleTSDenominator() {
		String value = cmbDenominator.getValue().toString();
		prefs.put("tsDenominator", value);
		Score.DEFAULT_BEAT_TYPE = Integer.parseInt(value);
	}

	private void changeErrorSensitivity(String prefValue) {
		switch (prefValue) {
		case "Level 1 - Minimal Error Checking" -> MainView.ERROR_SENSITIVITY = 1;
		case "Level 3 - Advanced Error Checking" -> MainView.ERROR_SENSITIVITY = 3;
		case "Level 4 - Detailed Error Checking" -> MainView.ERROR_SENSITIVITY = 4;
		default -> MainView.ERROR_SENSITIVITY = 2;
		}

		mvc.mainView.refresh();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

	}
}