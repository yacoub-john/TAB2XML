package GUI;


import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;

import org.jfugue.player.ManagedPlayer;
import org.jfugue.player.Player;

import javafx.scene.control.TextField;
import MusicNotes.CanvasNotes;
import Parser.XMLParser;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import utility.Settings;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	public static Window convertWindow;

	@FXML public  Canvas canvas;
	@FXML private Button Save;
	@FXML private Button editStyle;
	@FXML private ImageView image ;
	@FXML private AnchorPane musicPane;
	@FXML private Button playMusic;
	@FXML private Button Edit;
	@FXML private Button pauseMusic;
	@FXML private AnchorPane anchor;
	@FXML private ScrollPane scroll;
	@FXML private Button gotoMeasureButton;
	@FXML private TextField gotoMeasureField;
	public static CanvasNotes canvasNote = new CanvasNotes();
	public boolean playing = false;
	public boolean havePlayed = false;
	private Thread t1;
	ManagedPlayer mplayer;

	public PreviewSheetMusicController() {}

	public void setMainViewController(MainViewController mvcInput) {
		mvc = mvcInput;
		scroll.setFitToWidth(true);
		scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		canvasNote.printNotes(canvas, scroll, anchor);
	}


	@FXML
	void handleMusic(ActionEvent event) {

		t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				//				if(Parser.XMLParser.instrument.equals("Guitar")) {
				//					Parser.GuitarParser.jfugueTester.playNotes();
				//				}
				//
				//				else if(Parser.XMLParser.instrument.equals("Drumset")) {
				//					Parser.DrumParser.drumTest.playNotes();
				//				}
				//
				//				playing = false;

				if(Parser.XMLParser.instrument.equals("Guitar")) {
					Parser.GuitarParser.jfugueTester.playNotes();
					Parser.GuitarParser.jfugueTester.playNotes();
				}

				else if(Parser.XMLParser.instrument.equals("Drumset")) {
					Parser.DrumParser.drumTest.playNotes();
					Player player = new Player();
					Sequence s= player.getSequence(Parser.DrumParser.drumTest.total);
					//player.play(Parser.DrumParser.drumTest.total);
					mplayer = player.getManagedPlayer();
					try {
						if(havePlayed && playing == false) {
							mplayer.resume();
						}

						else {
							mplayer.start(s);
						}

						playing=true;


					} catch (InvalidMidiDataException | MidiUnavailableException e) {
						e.printStackTrace();
					}

				}

			}
		}); 

		t1.start();
		playing=false;
		if(t1.isAlive()) {
			havePlayed = false;
		}
	}

	@FXML
	void handlePause(ActionEvent event) {

		if(playing) {
			mplayer.pause();
			havePlayed=true;
			playing=false;
		}

		else {
			System.out.println("Nothing Playing");
		}
	}


	@FXML
	void handleSave() {


		WritableImage screenshot = anchor.snapshot(null, null);
		Printer printer = Printer.getDefaultPrinter();
		PageLayout layout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);

		double pagePrintableWidth = layout.getPrintableWidth();
		double pagePrintableHeight = layout.getPrintableHeight(); 
		final double scaleX = pagePrintableWidth / (screenshot.getWidth());

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
void  editInput() {
	mvc.convertWindow.hide();

}

@FXML
void handleStyle(ActionEvent event) {

	Parent root;
	try {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("GUI/sheetMusicStyle.fxml"));
		root = loader.load();
		SheetMusicStyleController controller = loader.getController();
		controller.setMainViewController(mvc);
		convertWindow = openNewWindow(root, "Sheet Music Style Editor");
	} catch (IOException e) {
		Logger logger = Logger.getLogger(getClass().getName());
		logger.log(Level.SEVERE, "Failed to create new Window.", e);
	}


}


Window openNewWindow(Parent root, String windowName) {
	Stage stage = new Stage();
	stage.setTitle(windowName);
	//stage.initModality(Modality.APPLICATION_MODAL);
	stage.initModality(Modality.NONE);
	stage.initOwner(MainApp.STAGE);
	stage.setResizable(false);
	Scene scene = new Scene(root);
	stage.setScene(scene);
	stage.show();
	return scene.getWindow();
}


@FXML
void handleGotoMeasure(ActionEvent event) {

}

@Override
public void start(Stage primaryStage) throws Exception {}
}
