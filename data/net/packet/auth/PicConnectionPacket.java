package be.alexandreliebh.picacademy.data.net.packet.auth;

import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicConnectionPacket extends PicAbstractPacket {

	private PicUser user;
	private boolean isResponse;

	public PicConnectionPacket(PicUser user) {
		super(PicPacketType.CONNECTION);
		this.user = user;
		setResponse(false);
	}

	public void setUser(PicUser na) {
		this.user = na;
	}

	public PicUser getUser() {
		return user;
	}

	public boolean isResponse() {
		return isResponse;
	}

	public void setResponse(boolean isResponse) {
		this.isResponse = isResponse;
	}

}
