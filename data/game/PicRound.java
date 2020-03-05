package be.alexandreliebh.picacademy.data.game;

import java.util.List;

public class PicRound {

	private String word;
	private List<String> words;
	private PicUser drawingUser;

	public PicRound(List<String> words) {
		this.words = words;
	}

	public void setDrawingUser(PicUser drawingUser) {
		this.drawingUser = drawingUser;
	}

	public PicUser getDrawingUser() {
		return drawingUser;
	}

	public String getWord() {
		return word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public List<String> getWords() {
		return words;
	}
	 
}
