package be.alexandreliebh.picacademy.data.game;

public enum PicGameState {

	WAITING("Waiting for players"),
	PICKING("Waiting for the artist to pick a word"),
	PLAYING("Game in progress"),
	FINISHED("Game is finished");

	private String state;

	private PicGameState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}
}
