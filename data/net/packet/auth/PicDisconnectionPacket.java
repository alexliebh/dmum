package be.alexandreliebh.picacademy.data.net.packet.auth;

import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.PacketUtil.DisconnectionReason;
import be.alexandreliebh.picacademy.data.net.packet.PicPacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicDisconnectionPacket extends PicPacket {

	private DisconnectionReason reason;
	private PicUser user;
	
	public PicDisconnectionPacket(DisconnectionReason reason) {
		super(PicPacketType.DISCONNECTION);
		this.reason = reason;
		this.user = getSender();
	}
	
	public PicDisconnectionPacket(PicUser user, DisconnectionReason reason) {
		this(reason);
		this.user = user;
	}
	
	public DisconnectionReason getReason() {
		return reason;
	}
	
	public PicUser getUser() {
		return user;
	}

}
