package be.alexandreliebh.picacademy.client.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import be.alexandreliebh.picacademy.client.PicAcademy;
import be.alexandreliebh.picacademy.client.frontend.PicPythonConn;
import be.alexandreliebh.picacademy.data.game.PicGameState;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.packet.game.PicClearBoardPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicDrawPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicMessagePacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicWordPickedPacket;
import be.alexandreliebh.picacademy.data.ui.PicColor;
import be.alexandreliebh.picacademy.data.ui.PicDrawingBoard;
import be.alexandreliebh.picacademy.data.ui.PicDrawingUnit;
import be.alexandreliebh.picacademy.data.ui.PicMessage;
import be.alexandreliebh.picacademy.data.util.LoadingUtil;
import be.alexandreliebh.picacademy.data.util.TimedRepeater;

public class PicGameLoop {

	private List<PicUser> users;
	private PicGameState state;

	private PicUser currentUser;

	private final PicDrawingBoard board;

	// Main user
	private PicColor color;
	private List<Point> unsentUnits;
	private List<PicDrawingUnit> newUnits;
	private TimedRepeater drawingRepeater;
	private short mainUserID;

	private byte gameID;
	private byte roundID;

	private final List<PicMessage> messages;

	private String word;
	private List<String> words;

	private byte timer;

	private boolean connected;
	private PicPythonConn front;

	public PicGameLoop() {
		this.connected = false;

		this.board = new PicDrawingBoard();

		this.users = new ArrayList<PicUser>();
		this.messages = new ArrayList<>();
		this.timer = -1;

		this.unsentUnits = new ArrayList<>();
		this.newUnits = new ArrayList<>();
		this.color = PicColor.WHITE;
	}

	/*
	 * Front-end methods
	 */
	public boolean sendMessage(String msg_content) {
		PicMessage msg = new PicMessage(this.currentUser.getID(), msg_content);
		PicMessagePacket pmp = new PicMessagePacket(msg, gameID);

		PicAcademy.getInstance().getNetClient().sendPacket(pmp);
		return word.equalsIgnoreCase(msg_content);
	}

	public void chooseWord(String word) {
		PicWordPickedPacket pwpp = new PicWordPickedPacket(gameID, word);
		PicAcademy.getInstance().getNetClient().sendPacket(pwpp);
	}

	public void drawUnit(int row, int col) {
		this.unsentUnits.add(new Point(row, col));
	}

	public void drawAllUnits(Point[] points) {
		for (Point point : points) {
			drawUnit(point.x, point.y);
			
		}
	}

	public void clearBoard() {
		makeBoardEmpty();
		PicClearBoardPacket pcbp = new PicClearBoardPacket(gameID);
		PicAcademy.getInstance().getNetClient().sendPacket(pcbp);
	}
	
	public void makeBoardEmpty() {
		board.resetBoard();
		front.toUpdate(PicPythonConn.CLEAR);
	}

	public void changeColor(int colorIndex) {
		this.color = PicColor.values()[colorIndex];
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

	public void exitGame() {
		System.exit(0);
	}

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

	public String getWord() {
		return word;
	}

	public List<PicMessage> getMessages() {
		return messages;
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

	// Back-end stuff
	private void sendUndrawnUnits() {
		if (!unsentUnits.isEmpty()) {
			Point[] points = new Point[this.unsentUnits.size()];
			PicDrawPacket drp = new PicDrawPacket(gameID, color, this.unsentUnits.toArray(points));
			PicAcademy.getInstance().getNetClient().sendPacket(drp);

			for (Point point : points) {
				board.setPixel(point.x, point.y, color);
			}

			this.front.toUpdate(PicPythonConn.BOARD);
			this.unsentUnits.clear();
		}
	}

	public void startPicking() {
		this.setState(PicGameState.PICKING);
		this.front.toUpdate(PicPythonConn.WORDS);

		if (this.isMainUser()) {
			System.out.println("Pick a word : " + LoadingUtil.listToString(words, "|"));
			return;
		}
		System.out.println(this.mainUserID + " is picking a word");
	}

	public void startDrawing() {
		if (this.isMainUser()) {
			this.initiateDrawLoop();
		}
	}

	private void initiateDrawLoop() {
		if (this.drawingRepeater == null) {
			this.drawingRepeater = new TimedRepeater(0, 100);
			this.drawingRepeater.startMs(new Runnable() {
				public void run() {
					try {
						sendUndrawnUnits();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			return;
		}
		this.drawingRepeater.restart();
	}

	public void endRound() {
		System.out.println("Round ended");
		this.front.toUpdate(PicPythonConn.ROUND_END);

		if (this.isMainUser()) {
			this.drawingRepeater.stop();
		}
		this.messages.clear();
		this.board.resetBoard();
	}

	public void receiveMessage(PicMessage msg) {
		msg.setUsername(getUserFromId(msg.getSenderID()).getUsername());

		System.out.println(msg.getUsername() + ": " + msg.getContent() + "(" + msg.getScore() + ")");
		if (msg.isSuccessful())
			getUserFromId(msg.getSenderID()).addToScore(msg.getScore());
		this.messages.add(msg);
		this.front.toUpdate(PicPythonConn.MESSAGES, PicPythonConn.USERS);
	}

	// Net setters
	public void setGameInfo(byte gi, List<PicUser> u) {
		this.gameID = gi;
		this.users = u;
		this.setState(PicGameState.WAITING);
		this.front.toUpdate(PicPythonConn.USERS, PicPythonConn.GAME_ID);
	}

	public void setRoundInfo(byte rid, short mainUserId, List<String> words) {
		this.roundID = rid;
		this.mainUserID = mainUserId;
		this.words = words;
		this.front.toUpdate(PicPythonConn.MAIN_USER, PicPythonConn.ROUND_ID);

	}

	public void setNewUnitsToDraw(List<Point> points, PicColor color) {
		for (Point point : points) {
			newUnits.add(new PicDrawingUnit(point.x, point.y, color.getId()));
		}
		this.front.toUpdate(PicPythonConn.BOARD);
	}
	
	public List<PicDrawingUnit> getNewUnits() {
		for (PicDrawingUnit u : newUnits) {
			board.setPixel(u.getX(), u.getY(), u.getColorID());
		}
		return newUnits;
	}
	
	public void clearNewUnits() {
		newUnits.clear();
	}

	public void setTimer(byte timer) {
		this.timer = timer;
		this.front.toUpdate(PicPythonConn.TIMER);
	}

	public void addUser(PicUser user) {
		for (PicUser picUser : users) {
			if (picUser.getID() == user.getID()) {
				return;
			}
		}
		this.users.add(user);
		this.front.toUpdate(PicPythonConn.USERS);
	}

	public void removeUser(PicUser user) {
		Iterator<PicUser> usersIterator = this.users.iterator();
		while (usersIterator.hasNext()) {
			PicUser u = usersIterator.next();
			if (u.getID() == user.getID()) {
				usersIterator.remove();
			}
		}
		this.front.toUpdate(PicPythonConn.USERS);
	}

	public void setWord(String word) {
		this.setState(PicGameState.PLAYING);

		this.word = word;
		this.front.toUpdate(PicPythonConn.WORD);

		System.out.println("The word \"" + word + "\" was chosen");
	}

	public void setCurrentUser(PicUser userObject) {
		this.currentUser = userObject;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setFront(PicPythonConn front) {
		this.front = front;
	}

	public void setState(PicGameState state) {
		this.state = state;
		this.front.toUpdate(PicPythonConn.STATE);
	}

}
