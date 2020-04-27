package be.alexandreliebh.picacademy.data.net.packet.game;

import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicClearBoardPacket extends PicAbstractGamePacket {
	
	public PicClearBoardPacket(byte gameID) {
		super(PicPacketType.CLEAR, gameID);
	}
	
}
