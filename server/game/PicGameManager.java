package be.alexandreliebh.picacademy.server.game;

import java.util.ArrayList;
import java.util.HashMap;
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
	private HashMap<Byte, PicGameLifecycle> lifecycles;

	private short pidCounter = 0; // Player ID
	private byte gidCounter = 0; // Game ID

	public PicGameManager() {
		this.games = new ArrayList<>(PicConstants.MAX_GAMES);
		this.lifecycles = new HashMap<>(PicConstants.MAX_GAMES);
	}

	/**
	 * Nettoie les parties :
	 * <ul>
	 * <li>Si une partie est prête, elle est lancée</li>
	 * <li>Si une partie est vide, elle est supprimée</li>
	 * </ul>
	 */
	public void updateGames() {
		for (int i = 0; i < this.games.size(); i++) {
			PicGame g = this.games.get(i);
			if (g.getState().equals(PicGameState.WAITING) && g.getUserCount() >= PicConstants.MAX_PLAYERS_PER_GAME) {
				this.startGame(g.getGameID());
			} else if (g.getUserCount() == 0) {
				this.stopGame(g);
			} else if (g.getCurrentRound() != null && !g.getCurrentRound().getWord().isEmpty() && g.getState().equals(PicGameState.PICKING)) {
				g.setState(PicGameState.PLAYING);
			}
		}
	}

	/**
	 * Ajoute un utilisateur au jeu et le connecte à une partie
	 * 
	 * @param user à ajouter au jeu
	 * @return le joueur avec son ID de joueur
	 */
	public PicGame addUserToGame(PicUser user) {
		PicGame foundGame = this.findGame(user);
		return foundGame;
	}

	/**
	 * Donne un ID au joueur
	 * 
	 * @param user le joueur à identifier
	 * @return user avec ID de joueur
	 */
	public PicUser identifyUser(PicUser user) {
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
		this.updateGames();
	}

	/**
	 * Trouve une partie à un utilisateur qui rejoint le jeu Si une partie attend
	 * des joueurs, il rejoint celle-ci, sinon une nouvelle partie est créée
	 * 
	 * @param user à ajouter dans une partie
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
		return game;
	}

	/**
	 * Crée une partie vide
	 * 
	 * @return partie vide
	 */
	private PicGame createGame() {
		PicGame g = new PicGame(++gidCounter);
		this.games.add(g);
		this.lifecycles.put(g.getGameID(), new PicGameLifecycle(g));
		return g;
	}

	/**
	 * Lance la partie
	 * 
	 * @param Id de la partie
	 */
	private void startGame(byte gameID) {
		this.lifecycles.get(gameID).startRound();
	}

	/**
	 * Arrête la partie
	 * 
	 * @param Id de la partie
	 */
	private void stopGame(PicGame game) {
		game.stop();
		this.games.remove(game);
		this.lifecycles.remove(game.getGameID());
	}

	/**
	 * Récupère la partie dans laquelle le joueur est
	 * 
	 * @param user le joueur dont on veut la partie
	 * @return la partie dans laquelle le joueur est
	 */
	public final PicGame getGamePerUser(PicUser user) throws IllegalArgumentException {
		for (PicGame g : games) {
			if (g.hasUser(user.getID())) {
				return g;
			}
		}
		throw new IllegalArgumentException("The user is not in a game");
	}

	/**
	 * Récupère une partie en fonction de son ID
	 * 
	 * @param gameID ID de la partie
	 * @return la partie avec l'ID spécifié
	 */
	public final PicGame getGamePerID(byte gameID) {
		for (PicGame picGame : games) {
			if (picGame.getGameID() == gameID) {
				return picGame;
			}
		}
		throw new IllegalArgumentException("The ID doesn't fit any game");
	}

	public final PicGameLifecycle getLifecyclePerID(byte gameID) {
		try {
			return lifecycles.get(gameID);
		} catch (Exception e) {
			throw new IllegalArgumentException("The ID doesn't fit any lifecycle");
		}
	}

	public void displayGames() {
		for (PicGame game : games) {
			System.out.println();
			System.out.println(game.getIdentifier() + " :");
			for (PicUser user : game.getUsers()) {
				System.out.println("    " + user.getIdentifier());
			}
		}
		System.out.println();
	}

	public List<PicGame> getGames() {
		return games;
	}

}
