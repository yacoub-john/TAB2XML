package PlayNotes;
import java.util.ArrayList;

import org.jfugue.player.Player;

public class JfugueForDrum {

	ArrayList<String> instruments = new ArrayList<>();
	ArrayList<String> instrumentsID = new ArrayList<>();
	
	private ArrayList<String> notesList = new ArrayList<>();
	private ArrayList<Integer> chordList = new ArrayList<>();
	private ArrayList<String> noteHeadList = new ArrayList<>();
	private ArrayList<Integer> noteLengthList = new ArrayList<>();
	private ArrayList<String> stemList = new ArrayList<>();
	private ArrayList<Integer> nNPM = new ArrayList<Integer>();
	private ArrayList<String> noteInstrumentIDList = new ArrayList<>();
	
	private  int nNPMCounter = 0;


	//Receive notes from Drum Parser
	public void getNotes(ArrayList<String> notesRecieved, ArrayList<Integer> chordsRecieved, ArrayList<String> noteHeadsRecieved, ArrayList<Integer> noteLengthRecieved, ArrayList<String> stemRecieved, ArrayList<String> noteInstrumIDRecieved, ArrayList<Integer> nNPMrecieved) {
		
		notesList = notesRecieved;
		chordList = chordsRecieved;
		noteHeadList = noteHeadsRecieved;
		noteLengthList = noteLengthRecieved;
		stemList = stemRecieved;
		noteInstrumentIDList = noteInstrumIDRecieved;
		
		getIntruments();
	}
	
	public void getIntruments() {
		
		instruments.add("HIGH_FLOOR_TOM");
		instrumentsID.add("P1-I44");
		
		instruments.add("RIDE_BELL");
		instrumentsID.add("P1-I54");

		instruments.add("RIDE_CYMBAL_1");
		instrumentsID.add("P1-I52");
		
		instruments.add("LO_TOM");
		instrumentsID.add("P1-I46");
		
		instruments.add("BASS_DRUM");
		instrumentsID.add("P1-I36");
		
		instruments.add("OPEN_HI_HAT");
		instrumentsID.add("P1-I47");
		
		instruments.add("CHINESE_CYMBAL");
		instrumentsID.add("P1-I53");
		
		instruments.add("PEDAL_HI_HAT");
		instrumentsID.add("P1-I45");
		
		instruments.add("LO_FLOOR_TOM");
		instrumentsID.add("P1-I42");
		
		instruments.add("LO_MID_TOM");
		instrumentsID.add("P1-I48");
		
		instruments.add("CRASH_CYMBAL_1");
		instrumentsID.add("P1-I50");
		
		instruments.add("CLOSED_HI_HAT");
		instrumentsID.add("P1-I43");
		
		instruments.add("ACOUSTIC_SNARE");
		instrumentsID.add("P1-I39");
		
	}
	
	public void playNotes() {
		
		Player player = new Player();
		String total = "";
		String str="T120 V0 ";
		total += str;
		
		for(int i = 0; i < notesList.size(); i++) {
			
			
			String noteInstrum = "";
			//System.out.println("Note instrum: " + noteInstrumentIDList.get(i));
			
			for(int j = 0; j < instrumentsID.size(); j++) {
				
				//System.out.println("Instrum list " + noteInstrumentIDList.get(i));

				
				if(noteInstrumentIDList.get(i).equals(instrumentsID.get(j))) {
					noteInstrum = instruments.get(j);
					total += "V0 I[" + noteInstrum + "] "; 
					break;
				}
				
			}
			
			total+= notesList.get(i);

			
			if((i+1)<chordList.size() && chordList.get(i+1)==0) 
			{
				//total+="+";
			}
			
			else {
				//player.play(str+notes.get(i));
				total += " ";
			}
						
			if(nNPMCounter<nNPM.size() && i == (nNPM.get(nNPMCounter) - 1)) {
				total += "| ";
				//player.play("|");
				nNPMCounter ++;
			}
			
		}
		
		System.out.println(total);
		player.play(total);
		nNPMCounter = 0;
	}

	public static void main(String[] args) {
		Player player1 = new Player();
		player1.play("V0 I[HI_MID_TOM] Eq Ch. | Eq Ch. | Dq Eq Dq Cq   V1 I[Flute] Rw | Rw | GmajQQQ CmajQ");
	}
}

