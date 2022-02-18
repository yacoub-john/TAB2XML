package MusicNotes;
//Java Program to create a canvas with specified
//width and height(as arguments of constructor),
//add it to the stage and also add a circle and
//rectangle on it
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.Group;

public class MusicNoteEffects extends Application {

	// launch the application
	public void start(Stage stage)
	{

		// set title for the stage
		stage.setTitle("creating canvas");

		// create a canvas
		Canvas canvas = new Canvas(100.0f, 100.0f);

		// graphics context
		GraphicsContext graphics_context =
			canvas.getGraphicsContext2D();

		// set fill for rectangle
		graphics_context.setFill(Color.RED);
		graphics_context.fillRect(20, 20, 70, 70);
//
//		// set fill for oval
//		graphics_context.setFill(Color.BLUE);
//		graphics_context.fillOval(30, 30, 70, 70);
//
//		// create a Group
		Group group = new Group(canvas);

		// create a scene
		Scene scene = new Scene(group, 200, 200);

		// set the scene
		stage.setScene(scene);

		stage.show();
	}

	// Main Method
	public static void main(String args[])
	{

		// launch the application
		launch(args);
	}
}
