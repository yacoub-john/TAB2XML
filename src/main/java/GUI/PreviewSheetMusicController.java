package GUI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class PreviewSheetMusicController extends Application{

    private MainViewController mvc;
	public Highlighter highlighter;

	
    @FXML  private Button Save;
    @FXML  private ImageView image ;
    @FXML private AnchorPane musicPane;
    @FXML private Button playMusic;

    public PreviewSheetMusicController() {}
    
    public void setMainViewController(MainViewController mvcInput) {
    	mvc = mvcInput;
    }
    
    @FXML
    void handleMusic(ActionEvent event) {

    }

    @FXML
    void handleSave(ActionEvent event) {

    }

    
    @Override
	public void start(Stage primaryStage) throws Exception {}
}
