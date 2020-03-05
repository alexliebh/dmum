package be.alexandreliebh.picacademy.data.net.packet.game;

import be.alexandreliebh.picacademy.data.game.PicGame;
import be.alexandreliebh.picacademy.data.game.PicGameState;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.packet.PicGamePacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicGameInfoPacket extends PicGamePacket {

	private PicUser[] users;

	private PicGameState state;
	
	public PicGameInfoPacket(PicGame game) {
		super(PicPacketType.GAME_INFO, game.getGameID());
		PicUser[] usersA = new PicUser[game.getUsers().size()];
		usersA = game.getUsers().toArray(usersA);
		this.users = usersA;
		this.state = game.getState();
	}

	public PicUser[] getUsers() {
		return users;
	}

	public PicGameState getState() {
		return state;
	}
	
	public String toString() {
		return getGameID() + " --> " + state.getState() +" "+users.length;
	}

}
