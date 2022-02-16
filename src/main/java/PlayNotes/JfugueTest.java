package PlayNotes;

import java.util.ArrayList;

import org.jfugue.player.Player;
	
	
	public class JfugueTest {
		
		private static ArrayList<String> notes;
		
		public static void getNotes(ArrayList<String>NotesReceived) {
			
			notes = NotesReceived;	
		}
		
		public static void playNotes() {
			
			Player player = new Player();
			String str="V0 I[Guitar]";
			
			for(int i=0;i<notes.size();i++) {
				 player.play(str + " " + notes.get(i));
			}
		}
		
	  }

