package be.alexandreliebh.picacademy.data.net.packet.game;

import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;
import be.alexandreliebh.picacademy.data.ui.PicMessage;

public class PicMessagePacket extends PicAbstractGamePacket {

	private PicMessage msg;
	
	public PicMessagePacket(PicMessage msg, byte gameID) {
		super(PicPacketType.MESSAGE, gameID);
		this.msg = msg;
	}
	
	public PicMessage getMessage() {
		return msg;
	}

}
