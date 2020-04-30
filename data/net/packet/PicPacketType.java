package be.alexandreliebh.picacademy.data.net.packet;

import be.alexandreliebh.picacademy.data.net.packet.auth.PicConnectionInitPacket;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicConnectionPacket;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicDisconnectionPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicClearBoardPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicDrawPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicGameInfoPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicMessagePacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicWordPickedPacket;
import be.alexandreliebh.picacademy.data.net.packet.round.PicRoundEndPacket;
import be.alexandreliebh.picacademy.data.net.packet.round.PicRoundInfoPacket;
import be.alexandreliebh.picacademy.data.net.packet.round.PicRoundTickPacket;
import be.alexandreliebh.picacademy.data.net.packet.utility.PicPingPacket;

public enum PicPacketType {

	CONNECTION_INIT("CIN", PicConnectionInitPacket.class),
	CONNECTION("CON", PicConnectionPacket.class),
	DISCONNECTION("DIS", PicDisconnectionPacket.class),
	GAME_INFO("GIN", PicGameInfoPacket.class),
	ROUND_INFO("RIN", PicRoundInfoPacket.class),	
	DRAW("DRA", PicDrawPacket.class),
	CLEAR("CLE", PicClearBoardPacket.class),
	WORD_PICKED("WOP", PicWordPickedPacket.class),
	MESSAGE("MES", PicMessagePacket.class),
	ROUND_END("REN", PicRoundEndPacket.class),
	ROUND_TICK("RTI", PicRoundTickPacket.class),
	PING("PIN", PicPingPacket.class),
	BAD("BAD", null);

	private final String header;
	private final Class<? extends PicAbstractPacket> pacClass;

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
