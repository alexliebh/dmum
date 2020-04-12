package be.alexandreliebh.picacademy.data.net.packet.game;

import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicWordPickedPacket extends PicAbstractGamePacket {

	private String word;

	public PicWordPickedPacket(byte gameID, String word) {
		super(PicPacketType.WORD_PICKED, gameID);
		this.word = word;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

}
