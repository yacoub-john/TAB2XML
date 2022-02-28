package GUI;


import java.io.File;


import javafx.scene.control.TextField;
import MusicNotes.CanvasNotes;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import utility.Settings;
import javafx.scene.canvas.Canvas;


public class PreviewSheetMusicController extends Application{

	private MainViewController mvc;
	public Highlighter highlighter;

	public Window convertWindow;
	
	@FXML public  Canvas canvas;
    @FXML private Button Save;
    @FXML private ImageView image ;
    @FXML private AnchorPane musicPane;
    @FXML private Button playMusic;
    @FXML private Button Edit;

    @FXML private Button gotoMeasureButton;
	@FXML private TextField gotoMeasureField;
	public static CanvasNotes canvasNote = new CanvasNotes();

	public PreviewSheetMusicController() {}

	public void setMainViewController(MainViewController mvcInput) {
		mvc = mvcInput;
		canvasNote.printNotes(canvas);
	}

	@FXML
	void handleMusic(ActionEvent event) {
		
		if(Parser.XMLParser.instrument.equals("Guitar")) {
			Parser.GuitarParser.jfugueTester.playNotes();
		}
		
		else if(Parser.XMLParser.instrument.equals("Drumset")) {
			//Parser.DrumParser.midiTester.playNotes();
			Parser.DrumParser.drumTest.playNotes();
		}
	}

	@FXML
	void handleSave() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save As");
		// FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MusicXML files", "*.musicxml", "*.xml", "*.mxl");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF Document", "*.pdf");

		fileChooser.getExtensionFilters().add(extFilter);

		File initialDir = new File(Settings.getInstance().outputFolder);
		String initialName = null;
		// if (!fileNameField.getText().isBlank() && fileNameField.getText().length()<50)
		//   initialName = fileNameField.getText().strip();

		if (mvc.saveFile != null) {
			if (initialName == null) {
				String name = mvc.saveFile.getName();
				if(name.contains("."))
					name = name.substring(0, name.lastIndexOf('.'));
				initialName = name;
			}
			File parentDir = new File(mvc.saveFile.getParent());
			if (parentDir.exists())
				initialDir = parentDir;
		}
		if (initialName != null)
			fileChooser.setInitialFileName(initialName);

		if (!(initialDir.exists() && initialDir.canRead()))
			initialDir = new File(System.getProperty("user.home"));

		fileChooser.setInitialDirectory(initialDir);

		File file = fileChooser.showSaveDialog(convertWindow);

		if (file != null) {
			mvc.converter.saveMusicXMLFile(file);
			mvc.saveFile = file;
			mvc.convertWindow.hide();

		}
	}

	@FXML 
	void  editInput() {
		mvc.convertWindow.hide();

	}


    @FXML
    void handleGotoMeasure(ActionEvent event) {

    }

	@Override
	public void start(Stage primaryStage) throws Exception {}
}
