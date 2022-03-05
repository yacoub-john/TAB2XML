package MusicNotes;


import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
	private ArrayList<Integer> xPlacments = new ArrayList<>();
	private ArrayList<Integer> yPlacements = new ArrayList<>();
	private ArrayList<String> noteLenghtList;
	
	private ArrayList<String> notesList = new ArrayList<>();
	private ArrayList<String> noteHeadList = new ArrayList<>();
	private ArrayList<Integer> noteLengthList = new ArrayList<>();
	private ArrayList<String> stemList = new ArrayList<>();
	private ArrayList<Integer> nNPM = new ArrayList<Integer>();
	private ArrayList<String> noteInstrumentIDList = new ArrayList<>();

	private int nNPMCounter = 0;
	private int currentX = 0;
	private int currentY = 0;

	private  double canvasWidth = 0;
	private  double canvasHeight = 0;

	public void getNotesGuitar(String recievedInstrument, ArrayList<String> recievedString, ArrayList<String> recievedFret, ArrayList<Integer> nNPM, ArrayList<String> recievedAlter, ArrayList<String> noteLengthRecieved, ArrayList<Integer> recievedChord ) {

		stringList = recievedString;
		fretList = recievedFret;
		notesPerMeasure = nNPM;
		//alterList = recievedAlter;
		noteLenghtList = noteLengthRecieved;
		chordList = recievedChord;
		instrumentName = recievedInstrument;

	}
	public void getNotesDrums(String recievedInstrument, ArrayList<String> notesRecieved, ArrayList<Integer> chordsRecieved, ArrayList<String> noteHeadsRecieved, ArrayList<Integer> noteLengthRecieved, ArrayList<String> stemRecieved, ArrayList<String> noteInstrumIDRecieved, ArrayList<Integer> nNPMrecieved) {
		instrumentName = recievedInstrument;
		notesPerMeasure = nNPMrecieved;
		notesList = notesRecieved;
		chordList = chordsRecieved;
		noteHeadList = noteHeadsRecieved;
		noteLengthList = noteLengthRecieved;
		stemList = stemRecieved;
		noteInstrumentIDList = noteInstrumIDRecieved;

	}

	public void printNotes(Canvas canv) {
		
		//Set starting X and Y values 
		currentX = 100;
		//currentY = 35;
		currentY=50;
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
		
		if(instrumentName.equals("Guitar")) {
			printNotesGuitar(graphics_context);
		}
		else if (instrumentName.equals("Drumset")) {
			printNotesDrums(graphics_context);

		}

		// Border
		graphics_context.setStroke(Color.BLACK);
		graphics_context.setLineWidth(4);
		graphics_context.strokeRect(0, 0, canvasWidth, canvasHeight);
		
		
	}

	// Prints type of Sheet Music
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
		
		else if (instrumentName.equals("Drumset")) {
			Font font = new Font("Arial Rounded MT Bold", 80);
			graphics_context.setFont(font);
			graphics_context.setFill(Color.BLACK);
			
//			graphics_context.fillRect(55,currentY+25,10,50);
//			graphics_context.fillRect(65,currentY+25,10,50);
//			graphics_context.setStroke(Color.BLACK);
//			graphics_context.setLineWidth(10);
//			graphics_context.strokeLine(75, currentY+30, 75, currentY+70);
//			
//			graphics_context.strokeLine(90, currentY+30, 90, currentY+70);
			graphics_context.fillText("\uD834\uDD25",currentX, currentY+75);

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
		
		if(instrumentName.equals("Guitar")) {
			graphics_context.strokeLine(50, currentY+125, canvasWidth - 50, currentY+125);
		}
		
	}

	// Vertical/Measure Lines
	public void printVertical(GraphicsContext graphics_context) {

		graphics_context.setStroke(Color.BLACK);
		graphics_context.setLineWidth(3);
		graphics_context.strokeLine(currentX, currentY, currentX, currentY+125);

	}


	// Prints Guitar Notes
	public void printNotesGuitar(GraphicsContext graphics_context) {
		Font font = new Font("Arial", 20);
		graphics_context.setFont(font);
				
		for(int i = 0; i < fretList.size(); i++) {
			
			if(notesPerMeasure.get(nNPMCounter) == i) {
				
				printVertical(graphics_context);
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
				
				xPlacments.add(currentX);
				yPlacements.add(currentY+5);
				
			}

			else if(stringList.get(i).equals("2")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+25, currentX+12,currentY+25);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+30); 
				
				xPlacments.add(currentX);
				yPlacements.add(currentY+30);
				
			}

			else if(stringList.get(i).equals("3")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+50, currentX+12,currentY+50);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+55); 
				
				xPlacments.add(currentX);
				yPlacements.add(currentY+55);

			}
			else if(stringList.get(i).equals("4")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+75, currentX+12,currentY+75);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+80); 
				
				xPlacments.add(currentX);
				yPlacements.add(currentY+80);

			}
			else if(stringList.get(i).equals("5")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+100, currentX+12,currentY+100);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+105); 
				
				xPlacments.add(currentX);
				yPlacements.add(currentY+105);

			}
			else if(stringList.get(i).equals("6")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+125, currentX+12,currentY+125);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+130); 
				
				xPlacments.add(currentX);
				yPlacements.add(currentY+130);

			}
			
			if(noteLenghtList.get(i).equals("eighth")) {
				// Vertical Line under the note
				graphics_context.setStroke(Color.BLACK);
				graphics_context.setLineWidth(1.1);
				graphics_context.strokeLine(currentX+6, currentY+135, currentX+6, currentY+165);
				
				if(i+1 < noteLenghtList.size() && noteLenghtList.get(i+1).equals("eighth")) {
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
		Parser.GuitarParser.jfugueTester.getCanvas(graphics_context, xPlacments, yPlacements);
	}
	
	public String getNoteShape(int duration) {
		
		/*
		 * BPM/Tempo = 60
		 * 1  -> 64 ->  4000 ms
		 * 1/2  -> 32 -> 2000 ms
		 * 1/4 -> 16 -> 1000 ms	        
		 * 1/8 -> 8 -> 500 ms	
		 * 1/16 -> 4 -> 250 ms	
		 * 1//32 -> 2 -> 125 ms	
		 * 1/64  -> 1 -> 63 ms	
		 * 1/128 -> 1/2 -> 31 ms
		 * 1/256 -> 1/4 -> 15 ms
		 * 1/512  -> 1/8 -> 8 ms
		 * 1/1024 -> 1/16 - > 4 ms
		 */
		
		String noteShape  = "";
		
		if(duration == 64) {
			return "\uD834\uDD5D";
		}

		else if(duration == 32) {
			return "\uD834\uDD5E";
		}

		else if(duration == 16) {
			return "\uD834\uDD5F";
		}

		else if(duration == 8) {
			return "\uD834\uDD60";
		}

		else if(duration == 4) {
			return "\uD834\uDD61";
		}

		else if(duration == 2) {
			return "\uD834\uDD62";
		}

		else if(duration == 1) {
			return "\uD834\uDD63";
		}
		
		return noteShape;
	}
	// Change the value in fill text based on the return value of the above function
	public void printNotesDrums(GraphicsContext graphics_context) {
		
		for(int i = 0; i < notesList.size(); i++) {
			
			String note = notesList.get(i).substring(1,2);
			int number = Integer.parseInt(notesList.get(i).substring(0,1));
			
			if(note.equals("E")) { //E Tells us the vertical location 
				
				
				if(number == 4) { //4 Tells us it the first instance of E that goes on last line
					
					graphics_context.setStroke(Color.WHITE);
					graphics_context.strokeLine(currentX, currentY+125, currentX+12,currentY+125);

					graphics_context.setFill(Color.BLACK); //Note shape is from the duration
					graphics_context.fillText("\uD834\uDD60", currentX, currentY+125); 
					
				}
				
				else if(number == 5) {
					
					graphics_context.setStroke(Color.WHITE);
					graphics_context.strokeLine(currentX, currentY+125, currentX+12,currentY+125);

					graphics_context.setFill(Color.BLACK); //Note shape is from the duration
					graphics_context.fillText("\uD834\uDD60", currentX, currentY+16); 
					
					
				}
				if(note.equals("F")) { //E Tells us the vertical location 
					
					
					if(number == 4) { //4 Tells us it the first instance of E that goes on last line
						
						graphics_context.setStroke(Color.WHITE);
						graphics_context.strokeLine(currentX, currentY+125, currentX+12,currentY+125);

						graphics_context.setFill(Color.BLACK); //Note shape is from the duration
						graphics_context.fillText("\uD834\uDD60", currentX, currentY+116); 
						
					}
					
					else if(number == 5) {
						
						graphics_context.setStroke(Color.WHITE);
						graphics_context.strokeLine(currentX, currentY+125, currentX+12,currentY+125);

						graphics_context.setFill(Color.BLACK); //Note shape is from the duration
						graphics_context.fillText("\uD834\uDD60", currentX, currentY); 
						
						
					}
				
				
				
							
		}
				
				
				
				
				
				
				
				
				
				
			
		}
		
		
//		
//		currentX+=20;
//		Font font = new Font("Bravura", 60);
//		graphics_context.setFont(font);
//		// Fisrt drum symbol
//		graphics_context.fillText("\uD834\uDD5F",currentX , currentY+95); // Simple Note
//		
//		graphics_context.setFill(Color.BLACK);
//		graphics_context.setLineWidth(3.2);
//		graphics_context.fillText("\uD834\uDD66",currentX+16.5, currentY+13);  //musical symbol combining sprechgesang stem
//		graphics_context.strokeLine(currentX+16.5, currentY+13 ,currentX+16.5, currentY+60);
//		graphics_context.setLineWidth(2);
//		graphics_context.strokeLine(currentX+10, currentY-9.5 ,currentX+22.5,  currentY-9.5);
//		
//		currentX+=30;
//		graphics_context.setLineWidth(3.2);
//		graphics_context.strokeLine(currentX, currentY ,currentX+10,  currentY-9.5);
//		graphics_context.strokeLine(currentX, currentY-9.5 ,currentX+10,  currentY);
//		graphics_context.strokeLine(currentX+10, currentY-9.5 ,currentX+10,  currentY-30);
//		//graphics_context.fillText("\uD834\uDD66",currentX+16.5, currentY+15);   
//		
//		currentX+=30;
//		graphics_context.fillText("\uD834\uDD5F",currentX , currentY+50); // Simple Note
//		graphics_context.strokeLine(currentX+5, currentY ,currentX+15,  currentY-9.5);
//		graphics_context.strokeLine(currentX+5, currentY-9.5 ,currentX+15,  currentY);
//		graphics_context.strokeLine(currentX+16.5, currentY+10,currentX+16.5, currentY-30);
//		
//		currentX+=30;
//		graphics_context.setLineWidth(3.2);
//		graphics_context.strokeLine(currentX, currentY ,currentX+10,  currentY-9.5);
//		graphics_context.strokeLine(currentX, currentY-9.5 ,currentX+10,  currentY);
//		graphics_context.strokeLine(currentX+10, currentY-9.5 ,currentX+10,  currentY-30);
//		
//		graphics_context.setLineWidth(5);
//		graphics_context.strokeLine(currentX-73, currentY-30,currentX+10 ,currentY-30);
//		currentX+=30;
//		
//		
//		//Second drum symbol
//		graphics_context.setLineWidth(3.2);
//		graphics_context.fillText("\uD834\uDD5F",currentX , currentY+50); // Simple Note
//		graphics_context.fillText("\uD834\uDD5F",currentX , currentY+95); // Simple Note
//		graphics_context.strokeLine(currentX+16.5, currentY+80,currentX+16.5, currentY);
//		graphics_context.strokeLine(currentX+16.5, currentY+10,currentX+16.5, currentY-19);
//		currentX+=30;
//		graphics_context.fillText("\uD834\uDD5F",currentX , currentY+50); // Simple Note
//		graphics_context.strokeLine(currentX+16.5, currentY+10,currentX+16.5, currentY-19);
//		currentX+=30;
//		graphics_context.fillText("\uD834\uDD5F",currentX , currentY+50); // Simple Note
//		graphics_context.strokeLine(currentX+16.5, currentY+10,currentX+16.5, currentY-19);
//		currentX+=30;
//		graphics_context.fillText("\uD834\uDD5F",currentX , currentY+50); // Simple Note
//		graphics_context.strokeLine(currentX+16.5, currentY+10,currentX+16.5, currentY-19);
//		
//		graphics_context.setLineWidth(5);
//		graphics_context.strokeLine(currentX-73, currentY-19,currentX+16 ,currentY-19);
//		graphics_context.strokeLine(currentX-73, currentY-10,currentX+16 ,currentY-10);
//
//		
		
		/*
		 * keep this for others
		 */
//		graphics_context.fillText("\uD834\uDD66",currentX+16.5, currentY+15);   
//		graphics_context.strokeLine(currentX+16.5, currentY+15 ,currentX+16.5, currentY+60);
//		
		
		
	}
	
	}
}
