package be.alexandreliebh.picacademy.data.net.packet.game;

import java.util.List;

import be.alexandreliebh.picacademy.data.game.PicGame;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicGameInfoPacket extends PicAbstractGamePacket {

	private final List<PicUser> users;

	public PicGameInfoPacket(PicGame game) {
		super(PicPacketType.GAME_INFO, game.getGameID());
		this.users = game.getUsers();
	}

	public List<PicUser> getUsers() {
		return users;
	}

}
