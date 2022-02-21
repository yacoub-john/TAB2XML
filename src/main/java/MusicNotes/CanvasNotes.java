package MusicNotes;

import java.util.ArrayList;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CanvasNotes {
	
	private String instrumentName = "";
	private ArrayList<String> stringList;
	private ArrayList<String> fretList;
	private ArrayList<Integer> chordList;
	private ArrayList<Integer> notesPerMeasure;
//	private ArrayList<String> alterList;
	private ArrayList<String> noteLenghtList;

	private int nNPMCounter = 0;
	private int currentX = 0;
	private int currentY = 0;

	private  double canvasWidth = 0;
	private  double canvasHeight = 0;

	public void getNotes(String recievedInstrument, ArrayList<String> recievedString, ArrayList<String> recievedFret, ArrayList<Integer> nNPM, ArrayList<String> recievedAlter, ArrayList<String> noteLengthRecieved, ArrayList<Integer> recievedChord ) {

		stringList = recievedString;
		fretList = recievedFret;
		notesPerMeasure = nNPM;
		//alterList = recievedAlter;
		noteLenghtList = noteLengthRecieved;
		chordList = recievedChord;
		instrumentName = recievedInstrument;

	}

	public void printNotes(Canvas canv) {
		
		//Set starting X and Y values 
		currentX = 100;
		currentY = 35;
		nNPMCounter = 0;

		GraphicsContext graphics_context = canv.getGraphicsContext2D();

		canvasWidth = graphics_context.getCanvas().getWidth();
		canvasHeight = graphics_context.getCanvas().getHeight();

		//Background
		graphics_context.setFill(Color.WHITE);
		graphics_context.fillRect(0, 0, canvasWidth, canvasHeight);

		//Horizontal Lines
		printHorizontalLines(graphics_context);

		// Sheet Music Type
		printType(graphics_context);

		//		graphics_context.setStroke(Color.WHITE);
		//		graphics_context.strokeLine(100, 85, 110, 85);
		//		
		//		graphics_context.setFill(Color.BLACK);
		//		graphics_context.fillText("2", 100, 90); // Number 2


		//		graphics_context.fillText("\uD834\uDD1E", 10, 40);	// G Clef
		//		graphics_context.fillText("\uD834\uDD22", 40, 40); // F Clef 
		//		graphics_context.fillText("\u266A", 80, 40); // Simple Note
		//		graphics_context.fillText("2", 20, 40); // Number 2

		// Notes
		printNotes(graphics_context);


		// Border
		graphics_context.setStroke(Color.BLACK);
		graphics_context.setLineWidth(4);
		graphics_context.strokeRect(0, 0, canvasWidth, canvasHeight);
		
		
	}


	public void printType(GraphicsContext graphics_context) {
		
		if(instrumentName.equals("Guitar")) {
			
			Font font = new Font("Arial", 15);
			graphics_context.setFont(font);
			graphics_context.setFill(Color.BLACK);
			//graphics_context.fillText("Guitar",50,currentY-15);

			font = new Font("Arial Rounded MT Bold", 40);
			graphics_context.setFont(font);
			graphics_context.setFill(Color.BLACK);
			graphics_context.fillText("T",55,currentY+45);
			graphics_context.fillText("A",54,currentY+80);
			graphics_context.fillText("B",55,currentY+114);
		}
	}
	
	//Horizontal Lines
	public void printHorizontalLines(GraphicsContext graphics_context) {

		graphics_context.setStroke(Color.BLACK);
		graphics_context.setLineWidth(1);

		graphics_context.strokeLine(50, currentY, canvasWidth - 50, currentY);
		graphics_context.strokeLine(50, currentY+25, canvasWidth - 50, currentY+25);
		graphics_context.strokeLine(50, currentY+50, canvasWidth - 50, currentY+50);
		graphics_context.strokeLine(50, currentY+75, canvasWidth - 50, currentY+75);
		graphics_context.strokeLine(50, currentY+100, canvasWidth - 50, currentY+100);
		graphics_context.strokeLine(50, currentY+125, canvasWidth - 50, currentY+125);
	}

	// Vertical/Measure Lines
	public void printVertical(GraphicsContext graphics_context) {

		graphics_context.setStroke(Color.BLACK);
		graphics_context.setLineWidth(3);
		graphics_context.strokeLine(currentX, currentY, currentX, currentY+125);

	}



	public void printNotes(GraphicsContext graphics_context) {
		Font font = new Font("Arial", 20);
		graphics_context.setFont(font);
		
		System.out.println(notesPerMeasure);
		
		for(int i = 0; i < fretList.size(); i++) {
			
			
			if(notesPerMeasure.get(nNPMCounter) == i) {
				
				printVertical(graphics_context);
				System.out.println(i);
				nNPMCounter ++;
				currentX += 30;
			}
			
			if( (currentX + 40) >= canvasWidth ) {
				currentY = currentY + 200;
				currentX = 55;
				printHorizontalLines(graphics_context);
			}

			if(stringList.get(i).equals("1")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY, currentX+12,currentY);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+5); 

			}

			else if(stringList.get(i).equals("2")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+25, currentX+12,currentY+25);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+30); 
				
			}

			else if(stringList.get(i).equals("3")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+50, currentX+12,currentY+50);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+55); 

			}
			else if(stringList.get(i).equals("4")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+75, currentX+12,currentY+75);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+80); 

			}
			else if(stringList.get(i).equals("5")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+100, currentX+12,currentY+100);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+105); 

			}
			else if(stringList.get(i).equals("6")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+125, currentX+12,currentY+125);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+130); 

			}
			
			if(noteLenghtList.get(i).equals("eighth")) {
				// Vertical Line under the note
				graphics_context.setStroke(Color.BLACK);
				graphics_context.setLineWidth(1.1);
				graphics_context.strokeLine(currentX+6, currentY+135, currentX+6, currentY+165);
				
				if(noteLenghtList.get(i+1).equals("eighth")) {
					// Vertical Line under the note
					graphics_context.setStroke(Color.BLACK);
					graphics_context.setLineWidth(3);
					graphics_context.strokeLine(currentX+7, currentY+165, currentX+45, currentY+165);
				}
			}
			
			if(i < chordList.size() - 1 && chordList.get(i+1) != 0) {
				currentX += 40;

			}
			else if(i == chordList.size() - 1) {
				currentX += 40;

			}
			
			if(i == fretList.size()-1) {
				printVertical(graphics_context);
			}
			
		}
		
		currentX = (int) (canvasWidth-50);
		printVertical(graphics_context);
	}



}
