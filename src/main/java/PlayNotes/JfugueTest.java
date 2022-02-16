package PlayNotes;

	import org.jfugue.player.Player;

	public class JfugueTest {
		public static void main(String[] args) {
	        Player player = new Player();
	        String str="V0 I[Flute]";
	        
	        player.play(str+" Eq Ch. | Eq Ch. | Dq Eq Dq Cq   ");
	    };
	  }

