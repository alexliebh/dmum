package be.alexandreliebh.picacademy.data.net.packet;

import be.alexandreliebh.picacademy.data.net.PicSocketedUser;

public abstract class PicAbstractPacket {

	private final PicPacketType type;
	private PicSocketedUser sender;

	public PicAbstractPacket(PicPacketType type) {
		this.type = type;
	}

	public PicPacketType getType() {
		return type;
	}

	public PicSocketedUser getSender() {
		return sender;
	}
	
	public void setSender(PicSocketedUser sender) {
		this.sender = sender;
	}

	@Override
	public String toString() {
		return "PicPacket[" + type + "(" + type.getHeader() + ")] sent by " + sender.toString();
	}

}
