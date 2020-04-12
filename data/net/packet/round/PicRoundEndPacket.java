package be.alexandreliebh.picacademy.data.net.packet.round;

import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;
import be.alexandreliebh.picacademy.data.net.packet.game.PicAbstractGamePacket;

public class PicRoundEndPacket extends PicAbstractGamePacket{

	public PicRoundEndPacket(byte gameID) {
		super(PicPacketType.ROUND_END, gameID);
	}

}
