package be.alexandreliebh.picacademy.data.game;

public class PicGameInfo {

	private byte userCount;

	private PicGameState state;
	private final byte gameID;

	public PicGameInfo(byte gameID) {
		this.gameID = gameID;
	}

	public byte getUserCount() {
		return userCount;
	}

	public void setUserCount(byte userCount) {
		this.userCount = userCount;
	}

	public PicGameState getState() {
		return state;
	}

	public void setState(PicGameState state) {
		this.state = state;
	}

	public byte getGameID() {
		return gameID;
	}

}
