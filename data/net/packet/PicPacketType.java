package be.alexandreliebh.picacademy.data.net.packet;

import be.alexandreliebh.picacademy.data.net.packet.auth.PicConnectionPacket;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicDisconnectionPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicClearBoardPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicDrawPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicGameInfoPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicMessagePacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicRoundInfoPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicWordPickedPacket;

public enum PicPacketType {

	CONNECTION("CON", PicConnectionPacket.class),
	DISCONNECTION("DIS", PicDisconnectionPacket.class),
	GAME_INFO("GIN", PicGameInfoPacket.class),
	ROUND_INFO("RIN", PicRoundInfoPacket.class),	
	DRAW("DRA", PicDrawPacket.class),
	CLEAR("CLE", PicClearBoardPacket.class),
	WORD_PICKED("WOP", PicWordPickedPacket.class),
	MESSAGE("MES", PicMessagePacket.class),
	BAD("BAD", null);

	private String header;
	private Class<? extends PicAbstractPacket> pacClass;

	private PicPacketType(String header, Class<? extends PicAbstractPacket> pacClass) {
		this.header = header;
		this.pacClass = pacClass;
	}

	public String getHeader() {
		return header;
	}
	
	public Class<? extends PicAbstractPacket> getPacketClass() {
		return pacClass;
	}

}
