package PlayNotes;

import java.util.ArrayList;

import org.jfugue.player.Player;


public class JfugueTest {

	private  ArrayList<String> notes = new ArrayList<>();
	private  ArrayList<Integer> nNPM = new ArrayList<Integer>();
	private  int nNPMCounter = 0;

	public void getNotes(ArrayList<String>NotesReceived, ArrayList<Integer> nNPMRecieved) {

		notes = NotesReceived;	
		nNPM = nNPMRecieved;

		for(int i = 0; i< nNPMRecieved.size(); i++) {
			if(i != 0) {
				nNPM.set(i, (nNPM.get(i) + nNPM.get(i-1)));
			}
		}

	}

	public  void playNotes() {

		if(notes.isEmpty() == false) {
			Player player = new Player();
			String str="V0 I[Guitar] |";
			String total = str;

			for(int i=0;i<notes.size();i++) {
				//player.play(notes.get(i));
				total += " " + notes.get(i);

				if(i == (nNPM.get(nNPMCounter) - 1)) {
					total += " |";
				//	player.play("|");
					nNPMCounter ++;
				}
			}

			System.out.println(total);
			player.play(total);
			nNPMCounter = 0;
		}
		
		else {
			System.out.println("No notes to play");
		}
	}



}

