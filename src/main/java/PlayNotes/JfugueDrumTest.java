package PlayNotes;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;

import org.jfugue.player.ManagedPlayer;
import org.jfugue.player.Player;


public class JfugueDrumTest {
	public static void main(String[] args) throws InterruptedException, InvalidMidiDataException, MidiUnavailableException {
		String str= "T120 V9 A5I B51 c";

		Player player= new Player();
		Sequence s= player.getSequence(str);
		ManagedPlayer mplayer = player.getManagedPlayer();
		
		//	  player.play(str);
		mplayer.start(s);

		mplayer.pause();



		mplayer.start(s);


	}
}


