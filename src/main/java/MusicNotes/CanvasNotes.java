package MusicNotes;

import javafx.util.Duration;
import java.util.ArrayList;
// For animation
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
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
	private ArrayList<Integer> xPlacementsG = new ArrayList<>();
	private ArrayList<Integer> yPlacementsG = new ArrayList<>();
	private ArrayList<Integer> xPlacementsD = new ArrayList<>();
	private ArrayList<Integer> yPlacementsD = new ArrayList<>();
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
		notesLengthList = noteLengthRecieved;
		stemList = stemRecieved;
		noteInstrumentIDList = noteInstrumIDRecieved;

	}

	public void printNotes(Canvas canv, ScrollPane scroll, AnchorPane anchor) {

		canvas = canv;
		scrollPane = scroll;
		anchorPane = anchor;
		//Set starting X and Y values 
		currentX = 100;


		if(instrumentName.equals("Guitar")) {
			currentY = 40;
		}
		else {
			currentY=75;
		}
		nNPMCounter = 0;

		graphics_context = canv.getGraphicsContext2D();

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
			graphics_context.setLineWidth(8);
			graphics_context.strokeLine(70, currentY+16, 70, currentY+32);

			graphics_context.strokeLine(83, currentY+16,83, currentY+32);




			//graphics_context.fillText("\uD834\uDD25",currentX-40, currentY+75);


		}
	}

	//Horizontal Lines
	public void printHorizontalLines(GraphicsContext graphics_context) {

		graphics_context.setStroke(Color.BLACK);
		graphics_context.setLineWidth(1);


		if(instrumentName.equals("Guitar")) {
			graphics_context.strokeLine(50, currentY+125, canvasWidth - 50, currentY+125);
			graphics_context.strokeLine(50, currentY, canvasWidth - 50, currentY);
			graphics_context.strokeLine(50, currentY+25, canvasWidth - 50, currentY+25);
			graphics_context.strokeLine(50, currentY+50, canvasWidth - 50, currentY+50);
			graphics_context.strokeLine(50, currentY+75, canvasWidth - 50, currentY+75);
			graphics_context.strokeLine(50, currentY+100, canvasWidth - 50, currentY+100);
		}
		else if (instrumentName.equals("Drumset")) {

			graphics_context.strokeLine(50, currentY, canvasWidth - 50, currentY);
			graphics_context.strokeLine(50, currentY+12, canvasWidth - 50, currentY+12);
			graphics_context.strokeLine(50, currentY+24, canvasWidth - 50, currentY+24);
			graphics_context.strokeLine(50, currentY+36, canvasWidth - 50, currentY+36);
			graphics_context.strokeLine(50, currentY+48, canvasWidth - 50, currentY+48);
			//		graphics_context.strokeLine(50, currentY+50, canvasWidth - 50, currentY+50);
		}
	}

	// Vertical/Measure Lines
	public void printVertical(GraphicsContext graphics_context) {

		if(instrumentName.equals("Guitar")) {
			graphics_context.setStroke(Color.BLACK);
			graphics_context.setLineWidth(3);
			graphics_context.strokeLine(currentX, currentY, currentX, currentY+125);
		}
		else if (instrumentName.equals("Drumset")) {
			graphics_context.setStroke(Color.BLACK);
			graphics_context.setLineWidth(3);
			graphics_context.strokeLine(currentX, currentY+1, currentX, currentY+47);
		}

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

				if(currentY+400 > canvasHeight) {
					anchorPane.setPrefHeight(canvasHeight+500);
					canvas.setHeight(canvasHeight+500);
					canvasHeight = canvasHeight + 500;
				}

				currentY = currentY + 200;
				currentX = 55;


				printHorizontalLines(graphics_context);
			}

			if(stringList.get(i).equals("1")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY, currentX+12,currentY);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+5); 

				xPlacementsG.add(currentX);
				yPlacementsG.add(currentY+5);

			}

			else if(stringList.get(i).equals("2")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+25, currentX+12,currentY+25);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+30); 

				xPlacementsG.add(currentX);
				yPlacementsG.add(currentY+30);

			}

			else if(stringList.get(i).equals("3")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+50, currentX+12,currentY+50);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+55); 

				xPlacementsG.add(currentX);
				yPlacementsG.add(currentY+55);

			}
			else if(stringList.get(i).equals("4")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+75, currentX+12,currentY+75);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+80); 

				xPlacementsG.add(currentX);
				yPlacementsG.add(currentY+80);

			}
			else if(stringList.get(i).equals("5")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+100, currentX+12,currentY+100);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+105); 

				xPlacementsG.add(currentX);
				yPlacementsG.add(currentY+105);

			}
			else if(stringList.get(i).equals("6")) {
				graphics_context.setStroke(Color.WHITE);
				graphics_context.strokeLine(currentX, currentY+125, currentX+12,currentY+125);

				graphics_context.setFill(Color.BLACK);
				graphics_context.fillText(fretList.get(i), currentX, currentY+130); 

				xPlacementsG.add(currentX);
				yPlacementsG.add(currentY+130);

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
		Parser.GuitarParser.jfugueTester.getCanvas(graphics_context, xPlacementsG, yPlacementsG);
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


			Font font = new Font("Arial Rounded MT Bold", 70);
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
				
				xPlacementsD.add(currentX);
				yPlacementsD.add(currentY);

			}


			else if(note.equals("E")) { //E Tells us the vertical location 


				if(number == 4) { //4 Tells us it the first instance of E that goes on last line

					graphics_context.setFill(Color.BLACK); //Note shape is from the duration
					graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+53); 


					// adding vertical line
					graphics_context.setLineWidth(3);
					graphics_context.strokeLine(currentX+20, currentY+10 ,currentX+20,  currentY-41);


					//Storing the value of x and y
					xPlacementsD.add(currentX);
					yPlacementsD.add(currentY);



				}

				else if(number == 5) {

					graphics_context.setFill(Color.BLACK); //Note shape is from the duration
					graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+12); 

					// adding vertical line
					graphics_context.setLineWidth(3);
					graphics_context.strokeLine(currentX+20, currentY+5 ,currentX+20,  currentY-41);


					//Storing the value of x and y
					xPlacementsD.add(currentX);
					yPlacementsD.add(currentY);


				}
			}
			else if(note.equals("F")) { //F Tells us the vertical location 


				if(number == 4) { //4 Tells us it the first instance of E that goes on last line

					graphics_context.setFill(Color.BLACK); //Note shape is from the duration
					graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+48); 
					// adding vertical line
					graphics_context.setLineWidth(3);
					graphics_context.strokeLine(currentX+20, currentY ,currentX+20,  currentY-41);


					//Storing the value of x and y
					xPlacementsD.add(currentX);
					yPlacementsD.add(currentY);


				}

				else if(number == 5) {

					graphics_context.setFill(Color.BLACK); //Note shape is from the duration
					graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+5); 

					// adding vertical line
					graphics_context.setLineWidth(3);
					graphics_context.strokeLine(currentX+20, currentY+5 ,currentX+20,  currentY-41);


					//Storing the value of x and y
					xPlacementsD.add(currentX);
					yPlacementsD.add(currentY);



				}


			}
			else if(note.equals("G")) {


				graphics_context.setFill(Color.BLACK); //Note shape is from the duration
				graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+41); 


				// adding vertical line
				graphics_context.setLineWidth(3);
				graphics_context.strokeLine(currentX+20, currentY ,currentX+20,  currentY-41);

				//Storing the value of x and y
				xPlacementsD.add(currentX);
				yPlacementsD.add(currentY);



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

				//Storing the value of x and y
				xPlacementsD.add(currentX);
				yPlacementsD.add(currentY);


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

				//Storing the value of x and y
				xPlacementsD.add(currentX);
				yPlacementsD.add(currentY);


			}
			else if(note.equals("C")) {


				graphics_context.setFill(Color.BLACK); //Note shape is from the duration
				graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+24);

				// adding vertical line
				graphics_context.setLineWidth(3);
				graphics_context.strokeLine(currentX+20, currentY,currentX+20,  currentY-41);

				//Storing the value of x and y
				xPlacementsD.add(currentX);
				yPlacementsD.add(currentY);


			}	
			else if(note.equals("D")) {


				graphics_context.setFill(Color.BLACK); //Note shape is from the duration
				graphics_context.fillText(getNoteShape(notesLengthList.get(i)), currentX, currentY+17);

				// adding vertical line
				graphics_context.setLineWidth(3);
				graphics_context.strokeLine(currentX+20, currentY ,currentX+20,  currentY-41);

				//Storing the value of x and y
				xPlacementsD.add(currentX);
				yPlacementsD.add(currentY);

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
				currentX+=40;
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

	public void highlight() {



		//Code for moving animation
		//		  DoubleProperty x  = new SimpleDoubleProperty();
		//	        DoubleProperty y  = new SimpleDoubleProperty();

		//	        Timeline timeline = new Timeline(
		//	            new KeyFrame(Duration.seconds(0),
		//	                    new KeyValue(x, 0),
		//	                    new KeyValue(y, 0)
		//	            ),
		//	            new KeyFrame(Duration.seconds(3),
		//	                    new KeyValue(x, canvasWidth - 50),
		//	                    new KeyValue(y, canvasHeight - 50)
		//	            )
		//	        );
		//	        timeline.setAutoReverse(false);
		//	        
		//	        
		//	     //   AnimationTimer timer = new AnimationTimer() {
		//	        	 int counter = 0;
		//	            @Override
		//	            public void handle(long now) {
		//	            	//timeline.setCycleCount(1);
		//	    	       
		//	            	
		//	            	Font font = new Font("Arial Rounded MT Bold", 70);
		//	        		graphics_context.setFont(font);
		//	        		graphics_context.setLineWidth(3);
		//	        		
		//	    			graphics_context.fillText("\u2304", xPlacementsD.get(1), currentY-60);
		//	    			
		//	    			Integer[][] clearArray= new Integer[1][2];
		//
		//	    			
		//	    			
		//	    			for(int i=0; i<xPlacementsD.size();i++) {
		//	    				
		////	    				graphics_context.strokeLine(xPlacementsD.get(i)-20, currentY-60 ,xPlacementsD.get(i), currentY-110 );
		////	    				graphics_context.strokeLine(xPlacementsD.get(i)+20, currentY-60, xPlacementsD.get(i), currentY-110);
		//	    				
		//	    				graphics_context.fillText("\u2304", xPlacementsD.get(counter), currentY-60);
		//	    				System.out.println(counter);
		//	    				if(counter < xPlacementsD.size()) {
		//	    					if(clearArray[0][0]!=0 && clearArray[0][1]!=0) {
		//		    					graphics_context.clearRect(clearArray[0][0], clearArray[0][1], 50, 40);
		//		    					
		//		    				}
		//		    				
		//		    				clearArray[0][0]=  xPlacementsD.get(counter)-20;
		//		    				clearArray[0][1]=  yPlacementsD.get(counter)-60;
		//		    				
		//		    			
		//		    			counter++;
		//	    				}
		//	    				else {
		//	    					
		//	    					this.stop();
		//	    				}
		//	            }
		//	        };
		//	        timer.start();
		//	       // timeline.play();

		//    }

		//		}

		Font font = new Font("Arial Bold", 20);
		graphics_context.setFont(font);
		Integer[][] clearArray= new Integer[1][2];
		for(int i=0; i<xPlacementsD.size();i++) {

//			graphics_context.strokeLine(xPlacementsD.get(i)-20, currentY-60 ,xPlacementsD.get(i), currentY-110 );
//			graphics_context.strokeLine(xPlacementsD.get(i)+20, currentY-60, xPlacementsD.get(i), currentY-110);
			graphics_context.setFill(Color.RED);
			graphics_context.fillText("\u2304", xPlacementsD.get(i)+5, currentY-60); //
			System.out.println("Current "+i);
			if(i < xPlacementsD.size()) {
				if(clearArray[0][0]!=null && clearArray[0][1]!=null) {
			//		graphics_context.clearRect(clearArray[0][0], clearArray[0][1], 50, 40);

				}

				clearArray[0][0]=  xPlacementsD.get(i)-20;
				clearArray[0][1]=  yPlacementsD.get(i)-60;	

			}

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
}





