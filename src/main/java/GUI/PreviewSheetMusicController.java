package GUI;


import java.io.File;

import org.jfugue.player.Player;

import javafx.scene.control.TextField;
import MusicNotes.CanvasNotes;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import utility.Settings;
import javafx.scene.canvas.Canvas;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Window;
import javafx.stage.Stage;
import models.ScorePartwise;
import models.measure.Measure;



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
    @FXML private AnchorPane anchor;
    @FXML private ScrollPane scroll;
    @FXML private Button gotoMeasureButton;
	@FXML private TextField gotoMeasureField;
	public static CanvasNotes canvasNote = new CanvasNotes();
	public boolean playing = false;

	public PreviewSheetMusicController() {}

	public void setMainViewController(MainViewController mvcInput) {
		mvc = mvcInput;
		scroll.setFitToWidth(true);
		scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		canvasNote.printNotes(canvas, scroll, anchor);
	}

	
	@FXML
	void handleMusic(ActionEvent event) {
		
		if(playing == false) {

			playMusic.setText("Pause Music");
			playing = true;
			
			
			if(Parser.XMLParser.instrument.equals("Guitar")) {
				Parser.GuitarParser.jfugueTester.playNotes();
			}
			
			else if(Parser.XMLParser.instrument.equals("Drumset")) {
				Parser.DrumParser.drumTest.playNotes();
				Player player = new Player();
				player.play(Parser.DrumParser.drumTest.total);
			}
		}
		
		
		if(playing) {
			playMusic.setText("Play Music");
			playing = false;
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
//		mvc.convertWindow.hide();
	
		

			WritableImage screenshot = anchor.snapshot(null, null);
			Printer printer = Printer.getDefaultPrinter();
			PageLayout layout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);

			double pagePrintableWidth = layout.getPrintableWidth();
			double pagePrintableHeight = layout.getPrintableHeight(); 
			final double scaleX = pagePrintableWidth / (1.5*screenshot.getWidth());
			final double scaleY = pagePrintableHeight / screenshot.getHeight();
			final ImageView print_node = new ImageView(screenshot);
			print_node.getTransforms().add(new Scale(scaleX, scaleX));

			PrinterJob printSheet = PrinterJob.createPrinterJob();

			if (printSheet != null && printSheet.showPrintDialog(anchor.getScene().getWindow())) {

				double numberOfPages = Math.ceil(scaleX / scaleY);
				Translate gridTransform = new Translate(0, 0);
				print_node.getTransforms().add(gridTransform);
				for (int i = 0; i < numberOfPages; i++) {
					gridTransform.setY(-i * (pagePrintableHeight / scaleX));
					printSheet.printPage(layout, print_node);
				}

				printSheet.endJob();

			}

		

	}


    @FXML
    void handleGotoMeasure(ActionEvent event) {

    }

	@Override
	public void start(Stage primaryStage) throws Exception {}
}
