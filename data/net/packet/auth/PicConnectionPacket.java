package be.alexandreliebh.picacademy.data.net.packet.auth;

import be.alexandreliebh.picacademy.data.net.PicSocketedUser;
import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicConnectionPacket extends PicAbstractPacket {

	private final PicSocketedUser user;
	private final boolean isResponse;

	public PicConnectionPacket(PicSocketedUser user, boolean response) {
		super(PicPacketType.CONNECTION);
		this.user = user;
		this.isResponse = response;
	}
	
	public PicConnectionPacket(PicConnectionPacket pac, boolean response) {
		super(PicPacketType.CONNECTION);
		this.user = pac.getUser();
		this.isResponse = response;
	}

	public PicSocketedUser getUser() {
		return user;
	}

	public boolean isResponse() {
		return isResponse;
	}

}
