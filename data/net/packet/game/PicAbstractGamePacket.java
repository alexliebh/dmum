package be.alexandreliebh.picacademy.data.net.packet.game;

import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public abstract class PicAbstractGamePacket extends PicAbstractPacket {

	private final byte gameID;

	public PicAbstractGamePacket(PicPacketType type, byte gameID) {
		super(type);
		this.gameID = gameID;
	}

	public byte getGameID() {
		return gameID;
	}

}
