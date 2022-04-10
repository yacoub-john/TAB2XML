package MusicNotes;

import java.util.ArrayList;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;	
import javafx.scene.text.Font;

@SuppressWarnings("unused")
public class CanvasNotes {

	private String instrumentName = "";
	private ArrayList<String> stringList;
	private ArrayList<String> fretList;
	private ArrayList<Integer> chordList;
	private ArrayList<Integer> notesPerMeasure;
	private ArrayList<String> alterList;
	private ArrayList<Integer> xPlacments = new ArrayList<>();
	private ArrayList<Integer> yPlacements = new ArrayList<>();
	private ArrayList<String> noteLenghtList;

	private ArrayList<String> notesList = new ArrayList<>();
	private ArrayList<String> noteHeadList = new ArrayList<>();
	private ArrayList<Integer> notesLengthList = new ArrayList<>();
	private ArrayList<String> stemList = new ArrayList<>();
	private ArrayList<Integer> nNPM = new ArrayList<Integer>();
	private ArrayList<String> noteInstrumentIDList = new ArrayList<>();
	private GraphicsContext graphics_context;
	private Canvas canvas;
	private ScrollPane scrollPane;
	private AnchorPane anchorPane;

	public int lineSpacing = 12;
	public int noteSpacing = 40;
	public int noteSize = 40;
	public String font = "Arial";
	private int nNPMCounter = 0;
	private int currentX = 0;
	private int currentY = 0;

	private  double canvasWidth = 0;
	private  double canvasHeight = 0;

	public  void setLineSpacing(int lineSpacingRecieved) {
		lineSpacing = lineSpacingRecieved;
	}
	public  void setNoteSpacing(int noteSpacingRecieved) {
		noteSpacing = noteSpacingRecieved;
	}

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
		notesLengthList = noteLengthRecieved;
		stemList = stemRecieved;
		noteInstrumentIDList = noteInstrumIDRecieved;

	}
	
	public void getCanvas(Canvas canv, ScrollPane scroll, AnchorPane anchor) {
		canvas = canv;
		scrollPane = scroll;
		anchorPane = anchor;
	}
	
	public void clearCanvas() {
		graphics_context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}
	
	public void printNotes() {

		currentX = 100;

		if(instrumentName.equals("Guitar")) {
			int num = 40;
			lineSpacing = 20;
			num = Math.abs(40 - lineSpacing);
			currentY = lineSpacing+num;
		}
		else {
			currentY=75;
		}
		nNPMCounter = 0;

		graphics_context = canvas.getGraphicsContext2D();

		canvasWidth = graphics_context.getCanvas().getWidth();
		canvasHeight = graphics_context.getCanvas().getHeight();

		//Background
		//		graphics_context.setFill(Color.WHITE);
		//		graphics_context.fillRect(0, 0, canvasWidth, canvasHeight);

		//Horizontal Lines
		printHorizontalLines(graphics_context);

		// Sheet Music Type
		printType(graphics_context);

		//		graphics_context.setStroke(Color.WHITE);
		//		graphics_context.strokeLine(100, 85, 110, 85);
		//		
		//		graphics_context.setFill(Color.BLACK);
		//		graphics_context.fillText("2", 100, 90); // Number 2


		//		graphics_context.fillText("?", 10, 40);	// G Clef
		//		graphics_context.fillText("?", 40, 40); // F Clef 
		//		graphics_context.fillText("?", 80, 40); // Simple Note
		//		graphics_context.fillText("2", 20, 40); // Number 2

		// Notes

		if(instrumentName.equals("Guitar")) {
			noteSize = 10;
			printNotesGuitar(graphics_context);
		}
		else if (instrumentName.equals("Drumset")) {
			noteSize = 70;
			printNotesDrums(graphics_context);

		}

		// Border
		graphics_context.setStroke(Color.BLACK);
		graphics_context.setLineWidth(4);
		graphics_context.strokeRect(0, 0, canvasWidth, canvasHeight-10);


	}

	// Prints type of Sheet Music
	public void printType(GraphicsContext graphics_context) {

		if(instrumentName.equals("Guitar")) {

			Font font = new Font("Arial", noteSize+5);
			graphics_context.setFont(font);
			graphics_context.setFill(Color.BLACK);
			//graphics_context.fillText("Guitar",50,currentY-15);

			font = new Font("Arial Rounded MT Bold", 28);
			graphics_context.setFont(font);
			graphics_context.setFill(Color.BLACK);
			graphics_context.fillText("T",55,currentY+28);
			graphics_context.fillText("A",54,currentY+52);
			graphics_context.fillText("B",54,currentY+75);
		}

		else if (instrumentName.equals("Drumset")) {
			Font font = new Font("Arial Rounded MT Bold", 80);
			graphics_context.setFont(font);
			graphics_context.setFill(Color.BLACK);

			//	graphics_context.fillRect(55,currentY+25,10,50);
			//	graphics_context.fillRect(65,currentY+25,10,50);
			//	graphics_context.setStroke(Color.BLACK);
			graphics_context.setLineWidth(8);
			graphics_context.strokeLine(70, currentY+16, 70, currentY+32);
			graphics_context.strokeLine(83, currentY+16,83, currentY+32);

			//graphics_context.fillText("?",currentX-40, currentY+75);

		}
	}

	//Horizontal Lines
	public void printHorizontalLines(GraphicsContext graphics_context) {

		graphics_context.setStroke(Color.BLACK);
		graphics_context.setLineWidth(1);


		if(instrumentName.equals("Guitar")) {
//			graphics_context.strokeLine(50, currentY+125, canvasWidth - 50, currentY+125);
//			graphics_context.strokeLine(50, currentY, canvasWidth - 50, currentY);
//			graphics_context.strokeLine(50, currentY+25, canvasWidth - 50, currentY+25);
//			graphics_context.strokeLine(50, currentY+50, canvasWidth - 50, currentY+50);
//			graphics_context.strokeLine(50, currentY+75, canvasWidth - 50, currentY+75);
//			graphics_context.strokeLine(50, currentY+100, canvasWidth - 50, currentY+100);
			graphics_context.strokeLine(50, currentY, canvasWidth - 50, currentY);
			graphics_context.strokeLine(50, currentY+lineSpacing, canvasWidth - 50, currentY+lineSpacing);
			graphics_context.strokeLine(50, currentY+(2*lineSpacing), canvasWidth - 50, currentY+(2*lineSpacing));
			graphics_context.strokeLine(50, currentY+(3*lineSpacing), canvasWidth - 50, currentY+(3*lineSpacing));
			graphics_context.strokeLine(50, currentY+(4*lineSpacing), canvasWidth - 50, currentY+(4*lineSpacing));
			graphics_context.strokeLine(50, currentY+(4*lineSpacing), canvasWidth - 50, currentY+(4*lineSpacing));
			graphics_context.strokeLine(50, currentY+(5*lineSpacing), canvasWidth - 50, currentY+(5*lineSpacing));

			
		}
		else if (instrumentName.equals("Drumset")) {

			graphics_context.strokeLine(50, currentY, canvasWidth - 50, currentY);
			graphics_context.strokeLine(50, currentY+lineSpacing, canvasWidth - 50, currentY+lineSpacing);
			graphics_context.strokeLine(50, currentY+(2*lineSpacing), canvasWidth - 50, currentY+(2*lineSpacing));
			graphics_context.strokeLine(50, currentY+(3*lineSpacing), canvasWidth - 50, currentY+(3*lineSpacing));
			graphics_context.strokeLine(50, currentY+(4*lineSpacing), canvasWidth - 50, currentY+(4*lineSpacing));
			//		graphics_context.strokeLine(50, currentY+50, canvasWidth - 50, currentY+50);
		}
	}

	// Vertical/Measure Lines
	public void printVertical(GraphicsContext graphics_context) {

		if(instrumentName.equals("Guitar")) {
			graphics_context.setStroke(Color.BLACK);
			graphics_context.setLineWidth(3);
			graphics_context.strokeLine(currentX, currentY+1, currentX, currentY+(5*lineSpacing)-1);
		}
		else if (instrumentName.equals("Drumset")) {
			graphics_context.setStroke(Color.BLACK);
			graphics_context.setLineWidth(3);
			graphics_context.strokeLine(currentX, currentY+1, currentX, currentY+(4*lineSpacing)-1);
		}

	}


	// Prints Guitar Notes
	public void printNotesGuitar(GraphicsContext graphics_context) {
		Font fontsize = new Font("Arial", noteSize);
		graphics_context.setFont(fontsize);

		for(int i = 0; i < fretList.size(); i++) {

			if(notesPerMeasure.get(nNPMCounter) == i) {

				printVertical(graphics_context);
				nNPMCounter ++;
				currentX += 30;
			}


			if (currentX+30>=canvasWidth-50) {

				if(currentY+400 > canvasHeight) {
					anchorPane.setPrefHeight(canvasHeight+500);
					canvas.setHeight(canvasHeight+500);
					canvasHeight = canvasHeight + 500;
				}

				currentX=(int) (canvasWidth-50);
				printVertical(graphics_context);
				currentY+=169;
				printHorizontalLines(graphics_context);
				currentX=50;
			}

			if(stringList.get(i).equals("1")) {
				graphics_context.setStroke(Color.WHITE);

				if( Integer.parseInt(fretList.get(i)) < 10) {
					graphics_context.strokeLine(currentX, currentY, currentX+8,currentY);
					graphics_context.setFill(Color.BLACK);
					graphics_context.fillText(fretList.get(i), currentX+1, currentY+5); 
					xPlacments.add(currentX+1);
					yPlacements.add(currentY+5);

				}
				else {
					graphics_context.strokeLine(currentX-2, currentY, currentX+11,currentY);
					graphics_context.setFill(Color.BLACK);
					graphics_context.fillText(fretList.get(i), currentX-2, currentY+5); 
					xPlacments.add(currentX-2);
					yPlacements.add(currentY+5);
				}

			}

			else if(stringList.get(i).equals("2")) {
				graphics_context.setStroke(Color.WHITE);

				if( Integer.parseInt(fretList.get(i)) < 10) {
					graphics_context.strokeLine(currentX, (currentY+1*lineSpacing), currentX+8,(currentY+1*lineSpacing));
					graphics_context.setFill(Color.BLACK);
					graphics_context.fillText(fretList.get(i), currentX+1, (currentY+1*lineSpacing)+5);
					xPlacments.add(currentX+1);
					yPlacements.add((currentY+1*lineSpacing)+5);

				}
				else {
					graphics_context.strokeLine(currentX-2, (currentY+1*lineSpacing), currentX+11,(currentY+1*lineSpacing));
					graphics_context.setFill(Color.BLACK);
					graphics_context.fillText(fretList.get(i), currentX-2, (currentY+1*lineSpacing)+5);
					xPlacments.add(currentX-2);
					yPlacements.add((currentY+1*lineSpacing)+5);
				}

			}

			else if(stringList.get(i).equals("3")) {
				graphics_context.setStroke(Color.WHITE);
				
				if( Integer.parseInt(fretList.get(i)) < 10) {
					graphics_context.strokeLine(currentX, (currentY+2*lineSpacing), currentX+8,(currentY+2*lineSpacing));
					graphics_context.setFill(Color.BLACK);
					graphics_context.fillText(fretList.get(i), currentX+1, (currentY+2*lineSpacing)+5);
					xPlacments.add(currentX+1);
					yPlacements.add((currentY+2*lineSpacing)+5);

				}
				else {
					graphics_context.strokeLine(currentX-2, (currentY+2*lineSpacing), currentX+11,(currentY+2*lineSpacing));
					graphics_context.setFill(Color.BLACK);
					graphics_context.fillText(fretList.get(i), currentX-2, (currentY+2*lineSpacing)+5);
					xPlacments.add(currentX-2);
					yPlacements.add((currentY+2*lineSpacing)+5);
				}

			}
			else if(stringList.get(i).equals("4")) {
				graphics_context.setStroke(Color.WHITE);

				if( Integer.parseInt(fretList.get(i)) < 10) {
					graphics_context.strokeLine(currentX, (currentY+3*lineSpacing), currentX+8,(currentY+3*lineSpacing));
					graphics_context.setFill(Color.BLACK);
					graphics_context.fillText(fretList.get(i), currentX+1, (currentY+3*lineSpacing)+5);
					xPlacments.add(currentX+1);
					yPlacements.add((currentY+3*lineSpacing)+5);

				}
				else {
					graphics_context.strokeLine(currentX-2, (currentY+3*lineSpacing), currentX+11,(currentY+3*lineSpacing));
					graphics_context.setFill(Color.BLACK);
					graphics_context.fillText(fretList.get(i), currentX-2, (currentY+3*lineSpacing)+5);
					xPlacments.add(currentX-2);
					yPlacements.add((currentY+3*lineSpacing)+5);
				}
			}
			else if(stringList.get(i).equals("5")) {
				graphics_context.setStroke(Color.WHITE);

				if( Integer.parseInt(fretList.get(i)) < 10) {
					graphics_context.strokeLine(currentX, (currentY+4*lineSpacing), currentX+8,(currentY+4*lineSpacing));
					graphics_context.setFill(Color.BLACK);
					graphics_context.fillText(fretList.get(i), currentX+1, (currentY+4*lineSpacing)+5);
					xPlacments.add(currentX+1);
					yPlacements.add((currentY+4*lineSpacing)+5);

				}
				else {
					graphics_context.strokeLine(currentX-2, (currentY+4*lineSpacing), currentX+11,(currentY+4*lineSpacing));
					graphics_context.setFill(Color.BLACK);
					graphics_context.fillText(fretList.get(i), currentX-2, (currentY+4*lineSpacing)+5);
					xPlacments.add(currentX-2);
					yPlacements.add((currentY+4*lineSpacing)+5);
				}

			}
			else if(stringList.get(i).equals("6")) {
				graphics_context.setStroke(Color.WHITE);

				if( Integer.parseInt(fretList.get(i)) < 10) {
					graphics_context.strokeLine(currentX, (currentY+5*lineSpacing), currentX+8,(currentY+5*lineSpacing));
					graphics_context.setFill(Color.BLACK);
					graphics_context.fillText(fretList.get(i), currentX+1, (currentY+5*lineSpacing)+5);
					xPlacments.add(currentX+1);
					yPlacements.add((currentY+5*lineSpacing)+5);

				}
				else {
					graphics_context.strokeLine(currentX-2, (currentY+5*lineSpacing), currentX+11,(currentY+5*lineSpacing));
					graphics_context.setFill(Color.BLACK);
					graphics_context.fillText(fretList.get(i), currentX-2, (currentY+5*lineSpacing)+5);
					xPlacments.add(currentX-2);
					yPlacements.add((currentY+5*lineSpacing)+5);
				}

			}
			
			if(noteLenghtList.get(i).equals("half")) {
				// Vertical Line under the note
				graphics_context.setStroke(Color.BLACK);
				graphics_context.setLineWidth(1.1);
				graphics_context.strokeLine(currentX+4, ((currentY+5*lineSpacing)+20), currentX+4, ((currentY+5*lineSpacing)+30));
				graphics_context.setFill(Color.BLACK);
				fontsize = new Font("Arial", 300);
				graphics_context.fillText(".", currentX+7, (currentY+5*lineSpacing)+25);

				
			}
			
			if(noteLenghtList.get(i).equals("quarter")) {
				// Vertical Line under the note
				graphics_context.setStroke(Color.BLACK);
				graphics_context.setLineWidth(1.1);
				graphics_context.strokeLine(currentX+4, ((currentY+5*lineSpacing)+10), currentX+4, ((currentY+5*lineSpacing)+30));
				graphics_context.setFill(Color.BLACK);
				fontsize = new Font("Arial", 300);
				graphics_context.fillText(".", currentX+7, (currentY+5*lineSpacing)+20);

				
			}
			

			if(noteLenghtList.get(i).equals("eighth")) {
				// Vertical Line under the note
				graphics_context.setStroke(Color.BLACK);
				graphics_context.setLineWidth(1.1);
				graphics_context.strokeLine(currentX+4, ((currentY+5*lineSpacing)+10), currentX+4, ((currentY+5*lineSpacing)+30));

				if(i+1 < noteLenghtList.size() && noteLenghtList.get(i+1).equals("eighth") && (currentX+80<canvasWidth-50) && notesPerMeasure.get(nNPMCounter) != i+1) {
					// Horizontal Line under the note
					graphics_context.setStroke(Color.BLACK);
					graphics_context.setLineWidth(3);
					graphics_context.strokeLine(currentX+5, ((currentY+5*lineSpacing)+30), currentX+43, ((currentY+5*lineSpacing)+30));
				}
			}
			
			if(noteLenghtList.get(i).equals("16th")) {
				// Vertical Line under the note
				graphics_context.setStroke(Color.BLACK);
				graphics_context.setLineWidth(1.1);
				graphics_context.strokeLine(currentX+4, ((currentY+5*lineSpacing)+10), currentX+4, ((currentY+5*lineSpacing)+30) );

				if(i+1 < noteLenghtList.size() && noteLenghtList.get(i+1).equals("16th") && (currentX+80<canvasWidth-50) && notesPerMeasure.get(nNPMCounter) != i+1) {
					// Horizontal Line under the note
					graphics_context.setStroke(Color.BLACK);
					graphics_context.setLineWidth(3);
					graphics_context.strokeLine(currentX+5, ((currentY+5*lineSpacing)+25), currentX+43, ((currentY+5*lineSpacing)+25));
					graphics_context.strokeLine(currentX+5, ((currentY+5*lineSpacing)+30), currentX+43, ((currentY+5*lineSpacing)+30));
				}
			}
			
			
			if(i < chordList.size() - 1 && chordList.get(i+1) != 0) {
				currentX += noteSpacing;

			}
			else if(i == chordList.size() - 1) {
				currentX += noteSpacing;

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
		if(duration==32) {
			noteShape="\uD834\uDD5E";
		}
		else {
			noteShape="\u2669";
		}

		//		if(duration == 64) {
		//			return "\uD834\uDD5D";
		//		}
		//
		//		else if(duration == 32) {
		//			return "\uD834\uDD5E";
		//		}
		//
		//		else if(duration == 16) {
		//			return "\uD834\uDD5F";
		//		}
		//
		//		else if(duration == 8) {
		//			return "\uD834\uDD60";
		//		}
		//
		//		else if(duration == 4) {
		//			return "\uD834\uDD61";
		//		}
		//
		//		else if(duration == 2) {
		//			return "\uD834\uDD62";
		//		}
		//
		//		else if(duration == 1) {
		//			return "\uD834\uDD63";
		//		}
		//		else if(duration == 16) {
		//			
		//			return "\uD834\uDD5F";
		//		}
		//
		//		else if(duration == 8) {
		//			return "\uD834\uDD60";
		//		}
		//
		//		else if(duration == 4) {
		//			return "\uD834\uDD61";
		//		}
		//
		//		else if(duration == 2) {
		//			return "\uD834\uDD62";
		//		}
		//
		//		else if(duration == 1) {
		//			return "\uD834\uDD63";
		//		}
		//		


		return noteShape;
	}
	// Change the value in fill text based on the return value of the above function
	public void printNotesDrums(GraphicsContext graphics_context) {

		int counter=0;  // For tracking when to connect 4 notes together.

		for(int i = 0; i < notesList.size(); i++) {

			if(notesPerMeasure.get(nNPMCounter) == i) {

				printVertical(graphics_context);
				nNPMCounter ++;
				currentX += 30;
			}


			Font font = new Font("Arial Rounded MT Bold", noteSize);
			graphics_context.setFont(font);

			String note = notesList.get(i).substring(1,2);
			int number = Integer.parseInt(notesList.get(i).substring(0,1));

			if (!noteHeadList.get(i).equals("non") && !notesList.get(i).equals("5A")){

				font = new Font("Arial Rounded MT Bold", 60);
				graphics_context.setFont(font);

				if(notesLengthList.get(i)==32) {
					graphics_context.fillText("\uD835\uDD69", currentX, currentY+1);
				}
				else {
					graphics_context.fillText("\uD834\uDD43", currentX, currentY+1);
				}

				// adding vertical line
				graphics_context.setLineWidth(3);
				graphics_context.strokeLine(currentX+20, currentY-13 ,currentX+20,  currentY-41);

			}


			else if(note.equals("E")) { //E Tells us the vertical location 


				if(number == 4) { //4 Tells us it the first instance of E that goes on last line

					graphics_context.setFill(Color.BLACK); //Note shape is from the duration
					graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+53); 


					// adding vertical line
					graphics_context.setLineWidth(3);
					graphics_context.strokeLine(currentX+20, currentY+10 ,currentX+20,  currentY-41);

				}

				else if(number == 5) {

					graphics_context.setFill(Color.BLACK); //Note shape is from the duration
					graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+12); 

					// adding vertical line
					graphics_context.setLineWidth(3);
					graphics_context.strokeLine(currentX+20, currentY+5 ,currentX+20,  currentY-41);

				}
			}
			else if(note.equals("F")) { //F Tells us the vertical location 


				if(number == 4) { //4 Tells us it the first instance of E that goes on last line

					graphics_context.setFill(Color.BLACK); //Note shape is from the duration
					graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+48); 
					// adding vertical line
					graphics_context.setLineWidth(3);
					graphics_context.strokeLine(currentX+20, currentY ,currentX+20,  currentY-41);

				}

				else if(number == 5) {

					graphics_context.setFill(Color.BLACK); //Note shape is from the duration
					graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+5); 

					// adding vertical line
					graphics_context.setLineWidth(3);
					graphics_context.strokeLine(currentX+20, currentY+5 ,currentX+20,  currentY-41);

				}


			}
			else if(note.equals("G")) {


				graphics_context.setFill(Color.BLACK); //Note shape is from the duration
				graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+41); 


				// adding vertical line
				graphics_context.setLineWidth(3);
				graphics_context.strokeLine(currentX+20, currentY ,currentX+20,  currentY-41);

			}
			else if(note.equals("A")) {
				if (number==4) {

					graphics_context.setFill(Color.BLACK); //Note shape is from the duration
					graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+36);
				}
				else if ( number==5 ) {

					font = new Font("Arial Rounded MT Bold", 60);
					graphics_context.setFont(font);

					if(notesLengthList.get(i)==32) {
						font = new Font("Arial Rounded MT Bold", 26);
						graphics_context.setFont(font);

						graphics_context.fillText("\uD835\uDD69", currentX+4, currentY-5);
					}
					else {
						graphics_context.fillText("\uD834\uDD43", currentX+1, currentY-4);
					}

					graphics_context.setLineWidth(1);
					graphics_context.strokeLine(currentX, currentY-12 ,currentX+25,  currentY-12);
				}
				// adding vertical line
				graphics_context.setLineWidth(3);
				graphics_context.strokeLine(currentX+20, currentY ,currentX+20,  currentY-41);


			}
			else if(note.equals("B")) {


				graphics_context.setFill(Color.BLACK); //Note shape is from the duration
				graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+29);
				// adding vertical line
				graphics_context.setLineWidth(3);
				graphics_context.strokeLine(currentX+20, currentY ,currentX+20,  currentY-41);


			}
			else if(note.equals("C")) {


				graphics_context.setFill(Color.BLACK); //Note shape is from the duration
				graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+24);

				// adding vertical line
				graphics_context.setLineWidth(3);
				graphics_context.strokeLine(currentX+20, currentY,currentX+20,  currentY-41);


			}	
			else if(note.equals("D")) {


				graphics_context.setFill(Color.BLACK); //Note shape is from the duration
				graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+17);

				// adding vertical line
				graphics_context.setLineWidth(3);
				graphics_context.strokeLine(currentX+20, currentY ,currentX+20,  currentY-41);

			}


			if(counter==4) {

				if(nNPMCounter-1>=0 && notesPerMeasure.get(nNPMCounter-1) == i) {
					graphics_context.setLineWidth(6);
					currentX-=30;
					graphics_context.strokeLine(currentX-138, currentY-41,currentX-22 ,currentY-41);
					currentX+=30;
				}
				else {
					graphics_context.setLineWidth(6);
					graphics_context.setStroke(Color.BLACK); //Note shape is from the duration
					graphics_context.strokeLine(currentX-138, currentY-41,currentX-22 ,currentY-41);
				}


				if(notesLengthList.get(i-1)==4) {
					graphics_context.strokeLine(currentX-138, currentY-31,currentX-22 ,currentY-31);

				}



				counter=0;
			}

			if (i+1<chordList.size()-1 && chordList.get(i+1)!= 0 ) {
				currentX+=noteSpacing;
				counter++;
				System.out.println(counter);
			}


			if (currentX+50>=canvasWidth-50) {

				if(currentY+400 > canvasHeight) {
					anchorPane.setPrefHeight(canvasHeight+500);
					canvas.setHeight(canvasHeight+500);
					canvasHeight = canvasHeight + 500;
				}

				currentX=(int) (canvasWidth-50);
				printVertical(graphics_context);
				currentY+=160;
				printHorizontalLines(graphics_context);
				currentX=50;
				counter=0;
			}


		}

		currentX = (int) (canvasWidth-50);    
		printVertical(graphics_context);
		nNPMCounter ++;

	}

}


//		
//		currentX+=20;
//		Font font = new Font("Bravura", 60);
//		graphics_context.setFont(font);
//		// Fisrt drum symbol
//		graphics_context.fillText("?",currentX , currentY+95); // Simple Note
//		
//		graphics_context.setFill(Color.BLACK);
//		graphics_context.setLineWidth(3.2);
//		graphics_context.fillText("?",currentX+16.5, currentY+13);  //musical symbol combining sprechgesang stem
//		graphics_context.strokeLine(currentX+16.5, currentY+13 ,currentX+16.5, currentY+60);
//		graphics_context.setLineWidth(2);
//		graphics_context.strokeLine(currentX+10, currentY-9.5 ,currentX+22.5,  currentY-9.5);
//		
//		currentX+=30;
//		graphics_context.setLineWidth(3.2);
//		graphics_context.strokeLine(currentX, currentY ,currentX+10,  currentY-9.5);
//		graphics_context.strokeLine(currentX, currentY-9.5 ,currentX+10,  currentY);
//		graphics_context.strokeLine(currentX+10, currentY-9.5 ,currentX+10,  currentY-30);
//		//graphics_context.fillText("?",currentX+16.5, currentY+15);   
//		
//		currentX+=30;
//		graphics_context.fillText("?",currentX , currentY+50); // Simple Note
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
//		graphics_context.fillText("?",currentX , currentY+50); // Simple Note
//		graphics_context.fillText("?",currentX , currentY+95); // Simple Note
//		graphics_context.strokeLine(currentX+16.5, currentY+80,currentX+16.5, currentY);
//		graphics_context.strokeLine(currentX+16.5, currentY+10,currentX+16.5, currentY-19);
//		currentX+=30;
//		graphics_context.fillText("?",currentX , currentY+50); // Simple Note
//		graphics_context.strokeLine(currentX+16.5, currentY+10,currentX+16.5, currentY-19);
//		currentX+=30;
//		graphics_context.fillText("?",currentX , currentY+50); // Simple Note
//		graphics_context.strokeLine(currentX+16.5, currentY+10,currentX+16.5, currentY-19);
//		currentX+=30;
//		graphics_context.fillText("?",currentX , currentY+50); // Simple Note
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
//		graphics_context.fillText("?",currentX+16.5, currentY+15);   
//		graphics_context.strokeLine(currentX+16.5, currentY+15 ,currentX+16.5, currentY+60);
//









