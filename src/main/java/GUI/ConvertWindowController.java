package GUI;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import utility.MusicXMLCreator;

public class ConvertWindowController extends Application {

    private MainViewController mvc;
    private static Window convertWindow = new Stage();
    
    @FXML private TextField titleField;
    @FXML private TextField artistField;
    @FXML private TextField fileNameField;
    
    public void setMainViewController(MainViewController mvcInput) {
    	mvc = mvcInput;
    }
    
    @FXML
    private void saveButtonClicked() {
        MusicXMLCreator.createScore(mvc.TEXT_AREA.getText());
//        if (!titleField.getText().isBlank())
//            MusicXMLCreator.setTitle(titleField.getText());
//        if (!artistField.getText().isBlank())
//            MusicXMLCreator.setArtist(artistField.getText());
        String generatedOutput = MusicXMLCreator.generateMusicXML();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MusicXML files", "*.xml", "*.mxl", "*.musicxml");
        fileChooser.getExtensionFilters().add(extFilter);

        File initialDir = null;
        String initialName = null;
        if (!fileNameField.getText().isBlank() && fileNameField.getText().length()<50)
            initialName = fileNameField.getText().strip();

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

        if (initialDir == null || !(initialDir.exists() && initialDir.canRead()))
            initialDir = new File(System.getProperty("user.home"));
        if (!(initialDir.exists() && initialDir.canRead()))
            initialDir = new File("c:/");

        fileChooser.setInitialDirectory(initialDir);


        File file = fileChooser.showSaveDialog(convertWindow);

        if (file != null) {
            saveToXMLFile(generatedOutput, file);
            mvc.saveFile = file;
            cancelButtonClicked();
        }
    }

    @FXML
    private void cancelButtonClicked()  {
    	mvc.convertWindow.hide();
    }


    private void saveToXMLFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}