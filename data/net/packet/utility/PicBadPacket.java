package be.alexandreliebh.picacademy.data.net.packet.utility;

import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicBadPacket extends PicAbstractPacket{

	public PicBadPacket() {
		super(PicPacketType.BAD);
	}

}
