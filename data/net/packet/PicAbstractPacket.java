package be.alexandreliebh.picacademy.data.net.packet;

import be.alexandreliebh.picacademy.data.game.PicUser;

public abstract class PicAbstractPacket {

	private final PicPacketType type;
	private PicUser sender;

	public PicAbstractPacket(PicPacketType type) {
		this.type = type;
	}

	public PicPacketType getType() {
		return type;
	}

	public PicUser getSender() {
		return sender;
	}
	
	public void setSender(PicUser sender) {
		this.sender = sender;
	}

	@Override
	public String toString() {
		return "PicPacket[" + type + "(" + type.getHeader() + ")] sent by " + sender.toString();
	}

}
