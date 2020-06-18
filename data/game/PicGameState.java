package be.alexandreliebh.picacademy.data.game;

public enum PicGameState {

	WAITING("En attente de joueurs"),
	PICKING("En  attente d'un choix de mots"),
	PLAYING(""),
	FINISHED("Fin de la manche"),
	STOP("Fin du jeu");

	private String state;

	private PicGameState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}
}
