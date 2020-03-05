package be.alexandreliebh.picacademy.data.net.packet;

public abstract class PicGamePacket extends PicPacket {

	private byte gameID;

	public PicGamePacket(PicPacketType type, byte gameID) {
		super(type);
		this.gameID = gameID;
	}

	public byte getGameID() {
		return gameID;
	}

}
