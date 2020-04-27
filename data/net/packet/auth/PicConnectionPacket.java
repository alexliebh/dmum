package be.alexandreliebh.picacademy.data.net.packet.auth;

import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicConnectionPacket extends PicAbstractPacket {

	private final PicUser user;
	private final boolean isResponse;

	public PicConnectionPacket(PicUser user, boolean response) {
		super(PicPacketType.CONNECTION);
		this.user = user;
		this.isResponse = response;
	}
	
	public PicConnectionPacket(PicConnectionPacket pac, boolean response) {
		super(PicPacketType.CONNECTION);
		this.user = pac.getUser();
		this.isResponse = response;
	}

	public PicUser getUser() {
		return user;
	}

	public boolean isResponse() {
		return isResponse;
	}

}
