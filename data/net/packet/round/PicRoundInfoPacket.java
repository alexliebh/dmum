package be.alexandreliebh.picacademy.data.net.packet.round;

import be.alexandreliebh.picacademy.data.game.PicRound;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;
import be.alexandreliebh.picacademy.data.net.packet.game.PicAbstractGamePacket;

public class PicRoundInfoPacket extends PicAbstractGamePacket {

	private final PicRound round;

	public PicRoundInfoPacket(PicRound round, byte gameID) {
		super(PicPacketType.ROUND_INFO, gameID);
		this.round = round;
	}

	public PicRound getRound() {
		return round;
	}

}
