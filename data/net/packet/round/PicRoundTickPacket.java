package be.alexandreliebh.picacademy.data.net.packet.round;

import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;
import be.alexandreliebh.picacademy.data.net.packet.game.PicAbstractGamePacket;

public class PicRoundTickPacket extends PicAbstractGamePacket {

	private final byte tick;

	public PicRoundTickPacket(byte tick, byte gameID) {
		super(PicPacketType.ROUND_TICK, gameID);
		this.tick = tick;
	}

	public byte getTick() {
		return tick;
	}

}
