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

public class MainViewController extends Application {
	
	private int HOVER_DELAY = 30;
	private Preferences prefs;
	public File saveFile;
	private static boolean isEditingSavedFile;
	private String InstrumentSetting = "auto";

	public Window convertWindow;
	public Window settingsWindow;

	public MainView mainView;

	@FXML public CodeArea TEXT_AREA;

	@FXML private TextField gotoMeasureField;
	@FXML private CheckBox wrapCheckbox;
	@FXML private BorderPane borderPane;
	@FXML private Button convertButton;
	@FXML private Button previewButton;
	@FXML private Button goToline;
	@FXML private ComboBox<String> cmbScoreType;


	public MainViewController() {
		Settings s = Settings.getInstance();
		prefs = Preferences.userRoot();
		s.outputFolder = prefs.get("outputFolder", System.getProperty("user.home"));
		s.tsNum = Integer.parseInt(prefs.get("tsNum", "4"));
		s.tsDen = Integer.parseInt(prefs.get("tsDen", "4"));
		s.errorSensitivity = Integer.parseInt(prefs.get("errorSensitivity", "4"));
	}

	@FXML 
	public void initialize() {
		mainView = new MainView(TEXT_AREA, convertButton, previewButton);
		initializeTextAreaErrorPopups();
		ContextMenu context = new ContextMenu();
		MenuItem menuItem = new MenuItem("Play Notes");
		menuItem.setOnAction(e -> {
			new NotePlayer(TEXT_AREA);
		});
		context.getItems().add(menuItem);
		TEXT_AREA.setContextMenu(context);
	}

	private void initializeTextAreaErrorPopups() {
		TEXT_AREA.setParagraphGraphicFactory(LineNumberFactory.get(TEXT_AREA));
		mainView.enableHighlighting();

		Popup popup = new Popup();
		Label popupMsg = new Label();
		popupMsg.setStyle(
				"-fx-background-color: black;" +
						"-fx-text-fill: white;" +
				"-fx-padding: 5;");
		popup.getContent().add(popupMsg);

		TEXT_AREA.setMouseOverTextDelay(Duration.ofMillis(HOVER_DELAY));
		TEXT_AREA.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
			if (MainView.ACTIVE_ERRORS.isEmpty()) return;
			int chIdx = e.getCharacterIndex();
			String message = MainView.getMessageOfCharAt(chIdx);
			if (message.isEmpty()) return;
			Point2D pos = e.getScreenPosition();
			popupMsg.setText(message);
			popup.show(TEXT_AREA, pos.getX(), pos.getY() + 10);
		});
		TEXT_AREA.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
			popup.hide();
		});
	}

	@FXML
	private void handleUserManual() throws URISyntaxException  {
		URL resource = getClass().getClassLoader().getResource("org.openjfx/UserManual.pdf");
		File file = new File(resource.toURI());

		HostServices hostServices = getHostServices();
		hostServices.showDocument(file.getAbsolutePath());

	}

	@FXML
	private void handleCurrentSongSettings() {
		Parent root;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("GUI/currentSongSettingsWindow.fxml"));
			root = loader.load();
			CurrentSongSettingsWindowController controller = loader.getController();
			controller.setMainViewController(this);
			settingsWindow = this.openNewWindow(root, "Current Song Settings");
		} catch (IOException e) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.log(Level.SEVERE, "Failed to create new Window.", e);
		}
	}
	
	@FXML
	private void handleSystemDefaultSettings() {
		Parent root;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("GUI/systemDefaultSettingsWindow.fxml"));
			root = loader.load();
			SystemDefaultSettingsWindowController controller = loader.getController();
			controller.setMainViewController(this);
			settingsWindow = this.openNewWindow(root, "System Default Settings");
		} catch (IOException e) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.log(Level.SEVERE, "Failed to create new Window.", e);
		}
	}

	@FXML
	private void handleNew() {
		boolean userOkToGoAhead = promptSave();
		if (!userOkToGoAhead) return;
		this.TEXT_AREA.clear();
		isEditingSavedFile = false;
	}

	@FXML
	private void handleOpen() {
		boolean userOkToGoAhead = promptSave();
		if (!userOkToGoAhead) return;

		String userDirectoryString = System.getProperty("user.home");
		File openDirectory;
		if (saveFile!=null && saveFile.canRead()) {
			openDirectory = new File(saveFile.getParent());
		}else
			openDirectory = new File(userDirectoryString);

		if(!openDirectory.canRead()) {
			openDirectory = new File("c:/");
		}

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		fileChooser.setInitialDirectory(openDirectory);
		File openedFile = fileChooser.showOpenDialog(MainApp.STAGE);
		if (openedFile==null) return;
		if (openedFile.exists()) {
			try {
				String newText = Files.readString(Path.of(openedFile.getAbsolutePath())).replace("\r\n", "\n");
				TEXT_AREA.replaceText(new IndexRange(0, TEXT_AREA.getText().length()), newText);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		saveFile = openedFile;
		isEditingSavedFile = true;
		//Is this needed?
		mainView.computeHighlightingAsync();

	}

	@FXML
	private boolean handleSaveAs() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save As");
		fileChooser.setInitialDirectory(new File(Settings.getInstance().outputFolder));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		if (saveFile!=null) {
			fileChooser.setInitialFileName(saveFile.getName());
			fileChooser.setInitialDirectory(new File(saveFile.getParent()));
		}

		File newSaveFile = fileChooser.showSaveDialog(MainApp.STAGE);
		if (newSaveFile==null) return false;
		try {
			FileWriter myWriter = new FileWriter(newSaveFile.getPath());
			myWriter.write(TEXT_AREA.getText());
			myWriter.close();

			saveFile = newSaveFile;
			isEditingSavedFile = true;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@FXML
	private boolean handleSave() {
		if (!isEditingSavedFile || saveFile==null || !saveFile.exists())
			return this.handleSaveAs();
		try {
			FileWriter myWriter = new FileWriter(saveFile.getPath());
			myWriter.write(TEXT_AREA.getText());
			myWriter.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private boolean promptSave() {

		//we don't care about overwriting a blank file. If file is blank, we are ok to go. it doesn't matter if it is saved or not
		if (TEXT_AREA.getText().isBlank())  return true;

		try {
			if (saveFile!=null && Files.readString(Path.of(saveFile.getAbsolutePath())).replace("\r\n", "\n").equals(TEXT_AREA.getText()))
				return true;    //if file didn't change, we are ok to go. no need to save anything, no chance of overwriting.
		}catch (Exception e){
			e.printStackTrace();
		}

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Unsaved file");
		alert.setHeaderText("This document is unsaved and will be overwritten. Do you want to save it first?");
		alert.setContentText("Choose your option.");

		ButtonType buttonTypeSave = new ButtonType("Save");
		ButtonType buttonTypeOverwrite = new ButtonType("Overwrite");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeOverwrite, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();

		boolean userOkToGoAhead = false;
		if (result.get() == buttonTypeSave){
			boolean saved;
			if (isEditingSavedFile) {
				saved = handleSave();
			}else {
				saved = handleSaveAs();
			}
			if (saved)
				userOkToGoAhead = true;
		} else if (result.get() == buttonTypeOverwrite) {
			// ... user chose "Override". we are good to go ahead
			userOkToGoAhead = true;
		}
		//if user chose "cancel", userOkToGoAhead is still false. we are ok.
		return userOkToGoAhead;
	}

	private Window openNewWindow(Parent root, String windowName) {
		Stage stage = new Stage();
		stage.setTitle(windowName);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(MainApp.STAGE);
		stage.setResizable(false);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		return scene.getWindow();
	}

	@FXML
	private void convertButtonHandle() {
		Parent root;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("GUI/convertWindow.fxml"));
			root = loader.load();
			ConvertWindowController controller = loader.getController();
			controller.setMainViewController(this);
			convertWindow = this.openNewWindow(root, "ConversionOptions");
		} catch (IOException e) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.log(Level.SEVERE, "Failed to create new Window.", e);
		}
	}

	@FXML
	private void previewButtonHandle() throws IOException {
		System.out.println("Preview Button Clicked!");
	}

	@FXML
	private void setWrapProperty() {
		TEXT_AREA.setWrapText(this.wrapCheckbox.isSelected());
	}

	@FXML
	private void handleScoreType() {
		InstrumentSetting = cmbScoreType.getValue().toString().strip();
		Score.INSTRUMENT_MODE = MusicXMLCreator.getInstrumentEnum(InstrumentSetting);
		mainView.refresh();
	}

	@FXML
	private void handleGotoMeasure() {
		int measureNumber = Integer.parseInt( gotoMeasureField.getText() );
		if (!mainView.goToMeasure(measureNumber)) {
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