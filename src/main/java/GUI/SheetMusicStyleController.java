package GUI;

import MusicNotes.CanvasNotes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SheetMusicStyleController {

	private MainViewController mvc;
	
    @FXML
    private Button Apply;

    @FXML
    private Button Close;

    @FXML
    private Slider LineSpacing;
    
    
    @FXML
    private Slider NoteSpacing;

    @FXML
    private AnchorPane settingsPane;

    @FXML
    private Slider sliderTempo;

    
    public void setMainViewController(MainViewController mvcInput) {
		mvc = mvcInput;
		LineSpacing = new Slider(0,1,0.1);
		LineSpacing.setShowTickMarks(true);
		LineSpacing.setShowTickLabels(true);
		LineSpacing.setMajorTickUnit(0.25f);
	}
    
    @FXML
    void handleApply(ActionEvent event) {
		CanvasNotes.setLineSpacing((int) LineSpacing.getValue());
		CanvasNotes.setNoteSpacing((int) NoteSpacing.getValue());

    }

    @FXML
    void handleClose(ActionEvent event) {
    	PreviewSheetMusicController.convertWindow.hide();
    }

    @FXML
    void handleDragNoteSpacing(MouseEvent event) {

    }

    @FXML
    void handleDragTempo(MouseEvent event) {

    }

}
