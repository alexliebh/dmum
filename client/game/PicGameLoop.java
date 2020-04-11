package be.alexandreliebh.picacademy.client.game;

import java.util.ArrayList;
import java.util.List;

import be.alexandreliebh.picacademy.client.PicAcademy;
import be.alexandreliebh.picacademy.client.frontend.PicUpdateType;
import be.alexandreliebh.picacademy.data.game.PicGameState;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.packet.game.PicMessagePacket;
import be.alexandreliebh.picacademy.data.ui.PicDrawingBoard;
import be.alexandreliebh.picacademy.data.ui.PicMessage;
import be.alexandreliebh.picacademy.data.util.LoadingUtil;

public class PicGameLoop {

	private List<PicUser> users;
	private PicGameState state;

	private PicUser currUser;
	
	private PicDrawingBoard board;

	private byte gameID;
	private byte roundID;

	private short mainUserID;
	private byte roundCount;

	private String word;
	private List<String> words;

	private List<PicMessage> unsentMessages;
	

	public PicGameLoop() {
		this.board = new PicDrawingBoard();
		this.users = new ArrayList<PicUser>();
		this.unsentMessages = new ArrayList<>();
	}

	public void start() {
		if (this.mainUserID == PicAcademy.getInstance().getNetClient().getUserObject().getID()) {
			System.out.println("Pick a word : " + LoadingUtil.listToString(words, " "));
			return;
		}
		System.out.println(this.mainUserID + " is picking a word");
	}

	public void updateFrontEnd(PicUpdateType... type) {

		for (PicUpdateType updateType : type) {
			switch (updateType) {
			case PLAYERS:
				break;
			case CHAT:
				break;
			case WORD_PICKED:
				break;

			default:
				break;
			}
		}

	}
	
	public void receiveMessage(PicMessage msg) {
		msg.setUsername(getUserFromId(msg.getSenderID()).getUsername());
		System.out.println(msg.getUsername()+": "+msg.getContent());
		this.unsentMessages.add(msg);
	}
	
	public void sendMessage(String msg_content) {
		PicMessage msg = new PicMessage(this.currUser.getID(), msg_content);
		PicMessagePacket pmp = new PicMessagePacket(msg, gameID);
		PicAcademy.getInstance().getNetClient().sendPacket(pmp);
	}

	public PicUser getUserFromId(short id) {
		for (PicUser picUser : users) {
			if (picUser.getID() == id) {
				return picUser;
			}
		}
		throw new IllegalArgumentException("The ID doesn't fit any user");
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
		return getUserFromId(mainUserID);
	}

	public void setMainUserID(short mainUserID) {
		this.mainUserID = mainUserID;
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

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}

	public void setCurrentUser(PicUser userObject) {
		this.currUser = userObject;
	}

}
