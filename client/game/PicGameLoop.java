package be.alexandreliebh.picacademy.client.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.alexandreliebh.picacademy.client.PicAcademy;
import be.alexandreliebh.picacademy.client.frontend.PythonConn;
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
	private TimedRepeater drawingRepeater;
	private short mainUserID;

	private byte gameID;
	private byte roundID;

	private List<PicMessage> messages;

	private String word;
	private List<String> words;

	private byte timer;

	private boolean connected;
	private PythonConn front;
	
	public PicGameLoop() {
		this.board = new PicDrawingBoard();
		this.users = new ArrayList<PicUser>();
		this.messages = new ArrayList<>();
		this.timer = -1;
		this.connected = false;
		this.unsentUnits = new ArrayList<>();
		this.color = PicColor.WHITE;
	}

	public void startPicking() {
		this.state = PicGameState.PICKING;
		front.toUpdate(PythonConn.STATE);
		if (isMainUser()) {	
			this.initRepeater();
			System.out.println("Pick a word : " + LoadingUtil.listToString(words, "|"));
			return;
		}
		System.out.println(this.mainUserID + " is picking a word");
	}

	public void startDrawing() {
		if (isMainUser()) {
			drawingRepeater.startMs(new Runnable() {
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
		this.front.toUpdate(PythonConn.ROUND_END);
		if (this.isMainUser()) {
			this.drawingRepeater.stop();
		}
		this.board.resetBoard();
	}

	public void receiveMessage(PicMessage msg) {
		msg.setUsername(getUserFromId(msg.getSenderID()).getUsername());
		System.out.println(msg.getUsername() + ": " + msg.getContent() + "(" + msg.getScore() + ")");
		this.messages.add(msg);
		this.front.toUpdate(PythonConn.MESSAGES);
	}

	
	//Front functions
	public void sendMessage(String msg_content) {
		PicMessage msg = new PicMessage(this.currUser.getID(), msg_content);
		PicMessagePacket pmp = new PicMessagePacket(msg, gameID);
		PicAcademy.getInstance().getNetClient().sendPacket(pmp);
	}

	public void chooseWord(String word) {
		PicWordPickedPacket pwpp = new PicWordPickedPacket(gameID, word);
		PicAcademy.getInstance().getNetClient().sendPacket(pwpp);
	}

	public void drawUnit(int row, int col) {
		this.unsentUnits.add(new Point(row, col));
	}

	public void drawAllUnits(Point[] points) {
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
		this.drawingRepeater = new TimedRepeater(0, 350);
	}

	private void sendUndrawnUnits() {
		if (!unsentUnits.isEmpty()) {
			Point[] points = new Point[this.unsentUnits.size()];
			PicDrawPacket drp = new PicDrawPacket(gameID, color, this.unsentUnits.toArray(points));
			PicAcademy.getInstance().getNetClient().sendPacket(drp);
			this.unsentUnits.clear();
		}
	}

	public void setGameInfo(byte gi, List<PicUser> u) {
		this.gameID = gi;
		this.users = u;
		this.state = PicGameState.WAITING;
		this.front.toUpdate(PythonConn.USERS, PythonConn.STATE, PythonConn.GAME_ID);

	}

	public void setRoundInfo(byte rid, short mainUserId, List<String> words) {
		this.roundID = rid;
		this.mainUserID = mainUserId;
		this.words = words;
		this.front.toUpdate(PythonConn.WORDS, PythonConn.ROUND_ID, PythonConn.MAIN_USER);
	}
	
	public void setUnitsToDraw(List<Point> points, PicColor color) {
		for (Point point : points) {
			board.setPixel(point, color);
		}
		this.front.toUpdate(PythonConn.BOARD);
	}

	// Front-end getters
	public byte getTimer() {
		return timer;
	}

	public List<PicUser> getUsers() {
		return users;
	}

	public PicDrawingBoard getBoard() {
		return board;
	}

	public byte getGameID() {
		return gameID;
	}

	public PicUser getMainUser() {
		return getUserFromId(mainUserID);
	}

	public boolean isMainUser() {
		return mainUserID == PicAcademy.getInstance().getNetClient().getUserObject().getID();
	}

	public int getWordLength() {
		return word.length();
	}
	
	public List<PicMessage> getMessages() {
		return messages;
	}

	// Net setters
	public void setTimer(byte timer) {
		this.timer = timer;
		this.front.toUpdate(PythonConn.TIMER);
	}

	public void addUser(PicUser user) {
		this.users.add(user);
		this.front.toUpdate(PythonConn.USERS);
	}

	public void removeUser(PicUser user) {
		this.users.remove(user);
		this.front.toUpdate(PythonConn.USERS);
	}

	public PicGameState getState() {
		return state;
	}

	public byte getRoundID() {
		return roundID;
	}

	public List<String> getWords() {
		return words;
	}

	public void setWord(String word) {
		this.state = PicGameState.PLAYING;
		this.word = word;
		this.front.toUpdate(PythonConn.STATE, PythonConn.WORD);
		System.out.println("The word \"" + word + "\" was chosen");

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
	
	public void setFront(PythonConn front) {
		this.front = front;
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
