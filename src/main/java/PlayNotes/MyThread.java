package PlayNotes;

import java.util.ArrayList;

import org.jfugue.player.Player;

public class MyThread extends Thread{

	public void run(ArrayList<String> notes) {
		
		if(notes.isEmpty() == false) {
			Player player = new Player();
			String str="V0 I[Guitar] |";
			String total = str;
//			System.out.println(xPlacement);
//			System.out.println(yPlacement);
			for(int i=0;i<notes.size();i++) {
				Parser.GuitarParser.jfugueTester.highLightNotes(i);
				player.play(notes.get(i));
				total += " " + notes.get(i);
			}
//				if(i == (nNPM.get(nNPMCounter) - 1)) {
//					total += " |";
//					player.play("|");
//					nNPMCounter ++;
//				}
			}
//
//			System.out.println(total);
//			//player.play(total);
//			nNPMCounter = 0;
//		}
		
		else {
			System.out.println("No notes to play");
		}
		
	}
	
	public static void main(String[] args) {
		MyThread thread = new MyThread();
		thread.start();
	}
}
