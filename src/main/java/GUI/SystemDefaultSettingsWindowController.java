package GUI;

import java.io.File;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import utility.Settings;

public class SystemDefaultSettingsWindowController extends Application {

	private MainViewController mvc;
	Preferences prefs;

	@FXML public TextField outputFolderField;
	@FXML private ComboBox<String> cmbErrorSensitivity;
	@FXML private ComboBox<String> cmbNumerator;
	@FXML private ComboBox<String> cmbDenominator;

	public SystemDefaultSettingsWindowController() {
		prefs = Preferences.userRoot();
	}
	
	public void setMainViewController(MainViewController mvcInput) {
		mvc = mvcInput;
	}

	public void initialize() {
		Settings s = Settings.getInstance();
		
		String outputFolder = s.outputFolder;
		if (outputFolder == null)
			outputFolderField.setPromptText("Not set yet...");
		else
			outputFolderField.setText(outputFolder);
		
		cmbErrorSensitivity.getItems().removeAll(cmbErrorSensitivity.getItems());
		cmbErrorSensitivity.getItems().addAll("Level 1 - Minimal Error Checking", "Level 2 - Standard Error Checking", "Level 3 - Advanced Error Checking", "Level 4 - Detailed Error Checking");
		int err = s.errorSensitivity;
		cmbErrorSensitivity.getSelectionModel().select(err - 1);
		
		cmbNumerator.getItems().removeAll(cmbNumerator.getItems());
		for (int i =1; i<=16; i++) cmbNumerator.getItems().add(i + "");
		int num = s.tsNum;
		cmbNumerator.getSelectionModel().select(num + "");
		
		cmbDenominator.getItems().removeAll(cmbDenominator.getItems());
		cmbDenominator.getItems().addAll("2", "4", "8", "16", "32");
		int den = s.tsDen;
		cmbDenominator.getSelectionModel().select(den + "");
		
	}
	
	@FXML private void handleErrorSensitivity() {
		int err;
		switch (cmbErrorSensitivity.getValue().toString()) {
		case "Level 1 - Minimal Error Checking" -> err = 1;
		case "Level 2 - Standard Error Checking" -> err = 2;
		case "Level 3 - Advanced Error Checking" -> err = 3;
		case "Level 4 - Detailed Error Checking" -> err = 4;
		default -> err = 4;
		}
		prefs.put("errorSensitivity", err+"");
		mvc.mainView.refresh();
	}

	@FXML
	private void handleChangeFolder() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(new File("src"));
		File selected = dc.showDialog(MainApp.STAGE);
		outputFolderField.setText(selected.getAbsolutePath());
		//Settings.getInstance().outputFolder = selected.getAbsolutePath();

		prefs.put("outputFolder", selected.getAbsolutePath());
	}

	@FXML
	private void handleTSNumerator() {
		String value = cmbNumerator.getValue().toString();
		prefs.put("tsNum", value);
		//Settings.getInstance().tsNum = Integer.parseInt(value);
	}
	
	@FXML
	private void handleTSDenominator() {
		String value = cmbDenominator.getValue().toString();
		prefs.put("tsDen", value);
		//Settings.getInstance().tsDen = Integer.parseInt(value);
	}


	@Override
	public void start(Stage primaryStage) throws Exception {

	}
}