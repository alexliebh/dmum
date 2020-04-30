package be.alexandreliebh.picacademy.data.net.packet.auth;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import be.alexandreliebh.picacademy.data.game.PicGameInfo;
import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicConnectionInitPacket extends PicAbstractPacket {

	private List<PicGameInfo> games;
	private Socket userSocket;

	public PicConnectionInitPacket(Socket userSocket) {
		super(PicPacketType.CONNECTION_INIT);
		this.games = new ArrayList<>();
		this.userSocket = userSocket;
	}

	public List<PicGameInfo> getGameList() {
		return games;
	}

	public Socket getUserSocket() {
		return userSocket;
	}
}
