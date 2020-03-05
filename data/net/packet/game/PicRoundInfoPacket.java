package be.alexandreliebh.picacademy.data.net.packet.game;

import be.alexandreliebh.picacademy.data.net.packet.PicGamePacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicRoundInfoPacket extends PicGamePacket {

	private short mainPlayerId;
	private String[] words;

	private byte roundId;

	public PicRoundInfoPacket(byte roundID, short mainPlayerID, byte gameID) {
		super(PicPacketType.ROUND_INFO, gameID);
		this.mainPlayerId = mainPlayerID;
		this.roundId = roundID;
	}

	public short getMainPlayerId() {
		return mainPlayerId;
	}

	public byte getRoundId() {
		return roundId;
	}

	public String[] getWords() {
		return words;
	}

	public void setWords(String[] words) {
		this.words = words;
	}

}
