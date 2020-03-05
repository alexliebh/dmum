package be.alexandreliebh.picacademy.client.game;

import java.util.ArrayList;
import java.util.List;

import be.alexandreliebh.picacademy.data.game.PicGameState;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.ui.PicDrawingBoard;

public class PicGameLoop {

	private List<PicUser> users;
	private PicGameState state;

	private PicDrawingBoard board;

	private byte gameID;
	private byte roundID;

	private PicUser mainUser;
	private byte roundCount;

	private String word;
	private String[] words;

	public PicGameLoop() {
		this.board = new PicDrawingBoard();
		this.users = new ArrayList<PicUser>();
	}

	public PicUser getUserFromId(short id) {
		PicUser us = null;
		for (PicUser picUser : users) {
			if (picUser.getID() == id) {
				us = picUser;
				break;
			}
		}
		return us;
	}

	public List<PicUser> getUsers() {
		return users;
	}

	public void setUsers(List<PicUser> users) {
		this.users = users;
	}
	
	public void addUser(PicUser user) {
		this.users.add(user);
	}
	
	public void removeUser(PicUser user) {
		this.users.remove(user);
	}

	public PicDrawingBoard getBoard() {
		return board;
	}

	public void setBoard(PicDrawingBoard board) {
		this.board = board;
	}

	public byte getGameID() {
		return gameID;
	}

	public void setGameID(byte gameID) {
		this.gameID = gameID;
	}

	public PicUser getMainUser() {
		return mainUser;
	}

	public void setMainUser(PicUser mainUser) {
		this.mainUser = mainUser;
	}

	public byte getRoundCount() {
		return roundCount;
	}

	public void setRoundCount(byte roundCount) {
		this.roundCount = roundCount;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public PicGameState getState() {
		return state;
	}

	public void setState(PicGameState state) {
		this.state = state;
	}

	public byte getRoundID() {
		return roundID;
	}

	public void setRoundID(byte roundID) {
		this.roundID = roundID;
	}

	public String[] getWords() {
		return words;
	}

	public void setWords(String[] words) {
		this.words = words;
	}

}
