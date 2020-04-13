package be.alexandreliebh.picacademy.data.net.packet.utility;

import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicPingPacket extends PicAbstractPacket {

	public PicPingPacket() {
		super(PicPacketType.PING);
	}

}
