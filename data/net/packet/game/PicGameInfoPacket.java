package be.alexandreliebh.picacademy.data.net.packet.game;

import java.util.List;

import be.alexandreliebh.picacademy.data.game.PicGame;
import be.alexandreliebh.picacademy.data.game.PicGameState;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicGameInfoPacket extends PicAbstractGamePacket {

	private List<PicUser> users;

	private PicGameState state;
	
	public PicGameInfoPacket(PicGame game) {
		super(PicPacketType.GAME_INFO, game.getGameID());
		this.users = game.getUsers();
		this.state = game.getState();
	}

	public List<PicUser> getUsers() {
		return users;
	}

	public PicGameState getState() {
		return state;
	}
	
	public String toString() {
		return getGameID() + " --> " + state.getState() +" "+users.size();
	}

}
