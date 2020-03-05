package be.alexandreliebh.picacademy.data.net.packet.game;

import be.alexandreliebh.picacademy.data.net.packet.PicGamePacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicClearBoardPacket extends PicGamePacket {
	

	public PicClearBoardPacket(byte gameID) {
		super(PicPacketType.CLEAR, gameID);
	}
	
}
