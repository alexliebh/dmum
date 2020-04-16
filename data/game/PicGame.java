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
	private byte roundID;

	private final PicDrawingBoard board;

	public PicGame(byte id) {
		this.users = new ArrayList<>(PicConstants.MAX_PLAYERS_PER_GAME);
		this.rounds = new PicRound[PicConstants.AMOUNT_OF_ROUNDS * PicConstants.MAX_PLAYERS_PER_GAME];
		this.roundID = -1;
		this.gameID = id;
		setState(PicGameState.WAITING);

		this.board = new PicDrawingBoard();
	}

	public PicGame addUser(PicUser user) {
		this.users.add(user);
		this.userCount++;
		System.out.println("[*] " + getIdentifier() + " += " + user.getIdentifier());
		return this;
	}

	public void removeUser(PicUser user) {
		this.users.remove(user);
		this.userCount--;
		System.out.println("[*] " + getIdentifier() + " -= " + user.getIdentifier());

	}

	public PicRound nextRound(PicRound round) {
		roundID++;
		round.setRoundId(roundID);
		rounds[roundID] = round;
		return round;
	}

	public PicRound getCurrentRound() {
		try {
			return rounds[roundID];
		} catch (Exception e) {
			return null;
		}
	}

	public void setState(PicGameState state) {
		this.state = state;
		System.out.println("Game (Id:" + this.gameID + ") [" + this.userCount + "/" + PicConstants.MAX_PLAYERS_PER_GAME + "] {" + (roundID+1) + "/" + (rounds.length - 1) + "} is now " + state.toString());
	}

	public void stop() {
		System.out.println("Game (Id:" + this.gameID + ") [" + this.userCount + "/" + PicConstants.MAX_PLAYERS_PER_GAME + "] is closing");
	}

	public boolean hasUser(short userID) {
		for (PicUser picUser : users) {
			if (picUser.getID() == userID) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return "Game (Id:" + this.gameID + ") [" + this.userCount + "/" + PicConstants.MAX_PLAYERS_PER_GAME + "] {" + this.state + "}";
	}

	public String getIdentifier() {
		return "Game (ID:" + this.gameID + ")";
	}

	public List<PicUser> getUsers() {
		return users;
	}

	public byte getGameID() {
		return gameID;
	}

	public PicGameState getState() {
		return state;
	}

	public byte getUserCount() {
		return userCount;
	}

	public PicDrawingBoard getBoard() {
		return board;
	}

	public int getRoundAmount() {
		return rounds.length;
	}

}
