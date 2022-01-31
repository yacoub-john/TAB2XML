package GUI;

import java.io.File;

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

public class PreviewSheetMusicController extends Application{

    private MainViewController mvc;
	public Highlighter highlighter;
	
	public Window convertWindow;

	
    @FXML  private Button Save;
    @FXML  private ImageView image ;
    @FXML private AnchorPane musicPane;
    @FXML private Button playMusic;
    @FXML private Button Edit;

    public PreviewSheetMusicController() {}
    
    public void setMainViewController(MainViewController mvcInput) {
    	mvc = mvcInput;
    }
    
    @FXML
    void handleMusic(ActionEvent event) {

    }

    @FXML
    void handleSave(ActionEvent event) {
    	
    	 FileChooser fileChooser = new FileChooser();
         fileChooser.setTitle("Save As");
         FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MusicXML files", "*.musicxml", "*.xml", "*.mxl");
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
    }
    
    @FXML 
    void  editInput(ActionEvent event) {
     	mvc.convertWindow.hide();
    	
    }

    
    @Override
	public void start(Stage primaryStage) throws Exception {}
}
