package be.alexandreliebh.picacademy.data.net.packet.auth;

import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.PacketUtil.DisconnectionReason;
import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicDisconnectionPacket extends PicAbstractPacket {

	private final PicUser user;
	private final DisconnectionReason reason;
	
	public PicDisconnectionPacket(PicUser user, DisconnectionReason reason) {
		super(PicPacketType.DISCONNECTION);
		this.user = user;
		this.reason = reason;
	}
	
	public DisconnectionReason getReason() {
		return reason;
	}
	
	public PicUser getUser() {
		return user;
	}

}
