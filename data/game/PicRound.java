package be.alexandreliebh.picacademy.data.game;

import java.util.List;

public class PicRound {

	private byte id;
	private String word;
	private List<String> words;
	private PicUser drawingUser;

	public PicRound(byte roundID, List<String> words, PicUser drawingUser) {
		this.words = words;
		this.id = roundID;
		this.drawingUser = drawingUser;
	}

	public PicUser getDrawingUser() {
		return drawingUser;
	}

	public String getWord() {
		return word;
	}

	public List<String> getWords() {
		return words;
	}

	public byte getRoundId() {
		return id;
	}

	public void setWord(String word) {
		this.word = word;
	}

}
