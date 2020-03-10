package be.alexandreliebh.picacademy.server.game;

import java.util.ArrayList;
import java.util.List;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.game.PicGame;
import be.alexandreliebh.picacademy.data.game.PicGameState;
import be.alexandreliebh.picacademy.data.game.PicUser;

/**
 * Classe générale gérant les parties et les joueurs
 * 
 * @author Alexandre Liebhaberg
 *
 */

public class PicGameManager {

	private List<PicGame> games;
	private List<PicGameLifecycle> lifecycles;

	private PicWordGenerator wordsGen;

	private short pidCounter = 0; // Player ID
	private byte gidCounter = 0; // Game ID

	public PicGameManager() {
		this.games = new ArrayList<>(PicConstants.MAX_GAMES);
		this.lifecycles = new ArrayList<>(PicConstants.MAX_GAMES);
		this.wordsGen = new PicWordGenerator();
	}

	private void updateGames() {
		for (int i = 0; i < this.games.size(); i++) {
			PicGame g = this.games.get(i);
			if (g.getState().equals(PicGameState.WAITING) && g.getUserCount() >= 6) {
				this.startGame(i);
			}
		}
	}

	public boolean loadWords(String path) {
		return wordsGen.loadWords(path);
	}

	/**
	 * Ajoute un utilisateur au jeu et le connecte à une partie
	 * 
	 * @param user à ajouter au jeu
	 * @return le joueur avec son ID de joueur
	 */
	public PicGame addUserToGame(PicUser user) {
		return this.findGame(user);
	}

	public PicUser addUser(PicUser user) {
		return user.setID(++pidCounter);
	}

	/**
	 * Retire l'utilisateur du jeu et donc de sa partie
	 * 
	 * @param user à enlever du jeu
	 */
	public void removeUser(PicUser user) {
		PicGame g = getGamePerUser(user);
		g.removeUser(user);
	}

	/**
	 * Trouve une partie à un utilisateur qui rejoint le jeu Si une partie attend
	 * des joueurs, il rejoint celle-ci, sinon une nouvelle partie est créée
	 * 
	 * @param user
	 * @return la partie dans laquelle le joueur est envoyé
	 */
	private PicGame findGame(PicUser user) {
		if (!this.games.isEmpty()) {
			for (PicGame game : this.games) {
				if (game.getState().equals(PicGameState.WAITING)) {
					return sendToGame(user, game);
				}
			}

		}

		PicGame ng = createGame();
		return sendToGame(user, ng);

	}

	/**
	 * Connecte un utilisateur à une partie
	 * 
	 * @param user à envoyer dans la partie
	 * @param game partie dans laquelle on envoie le joueur
	 * @return la partie dans laquelle le joueur est envoyé
	 */
	private PicGame sendToGame(PicUser user, PicGame game) {
		game.addUser(user);
		this.updateGames();
		return game;
	}

	/**
	 * Crée une partie vide
	 * 
	 * @return
	 */
	private PicGame createGame() {
		PicGame g = new PicGame(++gidCounter);
		this.games.add(g);
		this.lifecycles.add(new PicGameLifecycle(g));
		return g;
	}
	
	private void startGame(int index) {
		PicGame g = this.games.get(index);
		g.setState(PicGameState.PICKING);
		new Thread(this.lifecycles.get(index), g.getIdentifier()).start();
	}

	public final PicGame getGamePerUser(PicUser user) {
		for (PicGame g : games) {
			for (PicUser u : g.getUsers()) {
				if (user.getID() == u.getID()) {
					return g;
				}
			}
		}
		throw new IllegalArgumentException("The user is not in a game");
	}

	public final PicGame getGamePerID(byte gameID) {
		for (PicGame picGame : games) {
			if (picGame.getGameID() == gameID) {
				return picGame;
			}
		}
		throw new IllegalArgumentException("The ID doesn't fit any game");
	}

	public List<PicGame> getGames() {
		return games;
	}

}
