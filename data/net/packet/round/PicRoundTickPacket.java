package be.alexandreliebh.picacademy.data.net.packet.round;

import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;
import be.alexandreliebh.picacademy.data.net.packet.game.PicAbstractGamePacket;

public class PicRoundTickPacket extends PicAbstractGamePacket{

	private byte timer;
	
	public PicRoundTickPacket(byte timer, byte gameID) {
		super(PicPacketType.ROUND_TICK, gameID);
		this.timer = timer;
	}
	
	public byte getTimer() {
		return timer;
	}

}
