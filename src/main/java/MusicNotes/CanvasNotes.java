package MusicNotes;

import java.util.ArrayList;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CanvasNotes {

	private ArrayList<String> stringList;
	private ArrayList<String> fretList;
	private ArrayList<Integer> notesPerMeasure;
	private ArrayList<String> alterList;
	private ArrayList<String> noteLenghtList;
	private int currentX = 0;
	private int currentY = 0;
	
	private  double canvasWidth = 0;
	private  double canvasHeight = 0;
	
	public void getNotes(ArrayList<String> recievedString, ArrayList<String> recievedFret, ArrayList<Integer> nNPM, ArrayList<String> alterRecieved, ArrayList<String> noteLengthRecieved ) {

		stringList = recievedString;
		fretList = recievedFret;
		notesPerMeasure = nNPM;
		alterList = alterRecieved;
		noteLenghtList = noteLengthRecieved;
		
	}
	
	public void printNotes(Canvas canv) {
		
		GraphicsContext graphics_context = canv.getGraphicsContext2D();

		 canvasWidth = graphics_context.getCanvas().getWidth();
		 canvasHeight = graphics_context.getCanvas().getHeight();

		//Background
		graphics_context.setFill(Color.WHITE);
		graphics_context.fillRect(0, 0, canvasWidth, canvasHeight);

		//Horizontal Lines
		graphics_context.setStroke(Color.BLACK);
		graphics_context.setLineWidth(1);
		graphics_context.strokeLine(50, 100, canvasWidth - 50, 100);
		graphics_context.strokeLine(50, 125, canvasWidth - 50, 125);
		graphics_context.strokeLine(50, 150, canvasWidth - 50, 150);
		graphics_context.strokeLine(50, 175, canvasWidth - 50, 175);
		graphics_context.strokeLine(50, 200, canvasWidth - 50, 200);

		// Vertical Lines
		printMeasures(graphics_context);
		
		
		Font font = new Font("Bravura", 40);
		graphics_context.setFont(font);
		
		graphics_context.setFill(Color.BLACK);
		graphics_context.fillText("\uD834\uDD1E", 10, 40);	// G Clef
		graphics_context.fillText("\uD834\uDD22", 40, 40); // F Clef 
		graphics_context.fillText("\u266A", 80, 40); // Simple Note
		

		font = new Font("Arial", 20);
		graphics_context.setFont(font);
		graphics_context.fillText("2", 120, 40); // Number 2
			
		graphics_context.setStroke(Color.WHITE);
		graphics_context.strokeLine(75, 175, 85, 175);

		
		graphics_context.setFill(Color.BLACK);
		graphics_context.fillText("2", 75, 180); // Number 2

		
		// Border
		graphics_context.setStroke(Color.BLACK);
		graphics_context.setLineWidth(4);
		graphics_context.strokeRect(0, 0, canvasWidth, canvasHeight);

	}
	
	
	public void printMeasures(GraphicsContext graphics_context) {
		
		graphics_context.setStroke(Color.BLACK);
		graphics_context.setLineWidth(1);
			
			
		graphics_context.strokeLine((canvasWidth-50)/3, 100, (canvasWidth-50)/3, 200);
		graphics_context.strokeLine(2*(canvasWidth-50)/3, 100, 2*(canvasWidth-50)/3, 200);
		
	}
	
	
	


}
