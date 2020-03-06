package be.alexandreliebh.picacademy.data.game;

import java.util.ArrayList;
import java.util.List;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.ui.PicDrawingBoard;

public class PicGame {

	private List<PicUser> users;
	private byte userCount;

	private PicGameState state;
	private final byte gameID;

	private PicRound[] rounds;
	private byte roundCount;
	
	private final PicDrawingBoard board;

	public PicGame(PicRound[] rounds, byte id) {
		this.users = new ArrayList<>(PicConstants.MAX_PLAYERS_PER_GAME);
		this.rounds = rounds;
		this.roundCount = 0;
		this.gameID = id;
		setState(PicGameState.WAITING);
		
		this.board = new PicDrawingBoard();
	}

	public List<PicUser> getUsers() {
		return users;
	}

	public PicGame addUser(PicUser user) {
		this.users.add(user);
		this.userCount++;
		System.out.println("[*] Game (" + gameID + ") += " + user.getIdentifier());
		return this;
	}

	public void removeUser(PicUser user) {
		this.users.remove(user);
		this.userCount--;
		System.out.println("[*] Game (" + gameID + ") -= " + user.getIdentifier());

	}
	
	private void endOfGame() {
		// TODO implement end of game
	}
	
	public PicRound getCurrentRound(){
		return rounds[roundCount];
	}
	
	public void nextRound() {
		if (roundCount + 1 == rounds.length) {
			endOfGame();
			return;
		}
		roundCount++;
	}

	public byte getGameID() {
		return gameID;
	}

	public PicGameState getState() {
		return state;
	}

	public void setState(PicGameState state) {
		this.state = state;
		System.out.println("Game (Id:" + this.gameID + ") ["+this.userCount+"/"+PicConstants.MAX_PLAYERS_PER_GAME+"] is now "+state.toString());
	}

	public byte getUserCount() {
		return userCount;
	}
	
	public PicDrawingBoard getBoard() {
		return board;
	}
	
	public boolean hasUser(PicUser user) {
		return this.users.contains(user);
	}

	public String toString() {
		return "Game (Id:" + this.gameID + ") ["+this.userCount+"/"+PicConstants.MAX_PLAYERS_PER_GAME+"] {"+this.state+"}";
	}

}
