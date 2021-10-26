package converter;

import java.util.List;

import GUI.MainViewController;
import utility.MusicXMLCreator;
import utility.ValidationError;

public class Converter {

	private Score score;
	private MusicXMLCreator mxlc;
	private MainViewController mvc;
	
	public Converter(MainViewController mvc) {
		this.mvc = mvc;
		score = new Score(mvc.mainText.getText());
		mxlc = new MusicXMLCreator(score);
	}
	
	public void update() {
		score = new Score(mvc.mainText.getText());
		mxlc = new MusicXMLCreator(score);
	}
	
	public String getMusicXML() {
		return mxlc.generateMusicXML();
	}
	
	public boolean hasNothing() {
		return score.getTabSectionList().isEmpty();
	}
	
	public List<ValidationError> validate() {
		return score.validate();
	}
}
