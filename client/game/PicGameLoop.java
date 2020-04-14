package be.alexandreliebh.picacademy.client.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.alexandreliebh.picacademy.client.PicAcademy;
import be.alexandreliebh.picacademy.data.game.PicGameState;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.packet.game.PicDrawPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicMessagePacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicWordPickedPacket;
import be.alexandreliebh.picacademy.data.ui.PicColor;
import be.alexandreliebh.picacademy.data.ui.PicDrawingBoard;
import be.alexandreliebh.picacademy.data.ui.PicMessage;
import be.alexandreliebh.picacademy.data.util.LoadingUtil;
import be.alexandreliebh.picacademy.data.util.TimedRepeater;

public class PicGameLoop {

	private List<PicUser> users;
	private PicGameState state;

	private PicUser currUser;

	private PicDrawingBoard board;

	// Main user
	private PicColor color;
	private List<Point> unsentUnits;
	private TimedRepeater senderRepeater;
	private short mainUserID;

	private byte gameID;
	private byte roundID;

	private String word;
	private List<String> words;

	private byte timer;

	private boolean connected;

	private List<PicMessage> unsentMessages;

	public PicGameLoop() {
		this.board = new PicDrawingBoard();
		this.users = new ArrayList<PicUser>();
		this.unsentMessages = new ArrayList<>();
		this.timer = -1;
		this.connected = false;
		this.unsentUnits = new ArrayList<>();
		this.color = PicColor.WHITE;
	}

	public void startPicking() {
		if (isMainUser()) {
			this.initRepeater();
			System.out.println("Pick a word : " + LoadingUtil.listToString(words, "|"));
			return;
		}
		System.out.println(this.mainUserID + " is picking a word");
	}

	public void startDrawing() {
		if (isMainUser()) {
			senderRepeater.startMs(new Runnable() {
				public void run() {
					try {
						sendUndrawnUnits();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	public void endRound() {
		System.out.println("Round ended");
		if (this.isMainUser()) {
			this.senderRepeater.stop();
		}
		this.board.resetBoard();
	}

	public void receiveMessage(PicMessage msg) {
		msg.setUsername(getUserFromId(msg.getSenderID()).getUsername());
		System.out.println(msg.getUsername() + ": " + msg.getContent() + "(" + msg.getScore() + ")");
		this.unsentMessages.add(msg);
	}

	public void sendMessage(String msg_content) {
		PicMessage msg = new PicMessage(this.currUser.getID(), msg_content);
		PicMessagePacket pmp = new PicMessagePacket(msg, gameID);
		PicAcademy.getInstance().getNetClient().sendPacket(pmp);
	}

	public void chooseWord(String word) {
		PicWordPickedPacket pwpp = new PicWordPickedPacket(gameID, word);
		PicAcademy.getInstance().getNetClient().sendPacket(pwpp);
	}

	private void sendUndrawnUnits() {
		if (!unsentUnits.isEmpty()) {
			Point[] points = new Point[this.unsentUnits.size()];
			PicDrawPacket drp = new PicDrawPacket(gameID, color, this.unsentUnits.toArray(points));
			PicAcademy.getInstance().getNetClient().sendPacket(drp);
			this.unsentUnits.clear();
		}
	}

	public void addUnit(int row, int col) {
		this.unsentUnits.add(new Point(row, col));
	}

	public void addAllUnits(Point[] points) {
		this.unsentUnits.addAll(Arrays.asList(points));
	}

	public void changeColor(PicColor color) {
		this.color = color;
		sendUndrawnUnits();
	}

	public PicUser getUserFromId(short id) {
		for (PicUser picUser : users) {
			if (picUser.getID() == id) {
				return picUser;
			}
		}
		throw new IllegalArgumentException("The ID doesn't fit any user");
	}

	private void initRepeater() {
		this.senderRepeater = new TimedRepeater(0, 500);
	}

	public List<PicUser> getUsers() {
		return users;
	}

	public byte getTimer() {
		return timer;
	}

	public void setTimer(byte timer) {
		this.timer = timer;
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

	public boolean isMainUser() {
		return mainUserID == PicAcademy.getInstance().getNetClient().getUserObject().getID();
	}

	public void setMainUserID(short mainUserID) {
		this.mainUserID = mainUserID;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
		System.out.println("The word \"" + word + "\" was chosen");

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

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isConnected() {
		return connected;
	}

	//
//	public void updateFrontEnd(PicUpdateType... type) {
//
//		for (PicUpdateType updateType : type) {
//			switch (updateType) {
//			case PLAYERS:
//				break;
//			case CHAT:
//				break;
//			case WORD_PICKED:
//				break;
//
//			default:
//				break;
//			}
//		}
//
//	}

}
