package be.alexandreliebh.picacademy.data.game;

import java.util.List;

public class PicRound {

	private byte id;
	private String word;
	private List<String> words;
	private short drawingUserID;

	public PicRound(List<String> words, short drawingUserID) {
		this.words = words;
		this.word = "";
		this.id = -1;
		this.drawingUserID = drawingUserID;
	}

	public short getDrawingUser() {
		return drawingUserID;
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
	
	public void setRoundId(byte id) {
		this.id = id;
	}
}
