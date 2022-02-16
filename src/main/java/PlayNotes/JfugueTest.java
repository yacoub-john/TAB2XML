package PlayNotes;

import java.util.ArrayList;

import org.jfugue.player.Player;
	

	public class JfugueTest {
		public static void GetNotes(ArrayList<String>NotesReceived) {
			Player player = new Player();
			String str="V0 I[Guitar]";
			for(int i=0;i<NotesReceived.size();i++) {
				 player.play(str+ " " + NotesReceived.get(i));
			}
				
		}
		
	  }

