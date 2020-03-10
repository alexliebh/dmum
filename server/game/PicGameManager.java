package be.alexandreliebh.picacademy.server.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.game.PicGame;
import be.alexandreliebh.picacademy.data.game.PicGameState;
import be.alexandreliebh.picacademy.data.game.PicRound;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.util.LoadingUtil;

/**
 * Classe générale gérant les parties et les joueurs
 * 
 * @author Alexandre Liebhaberg
 *
 */

public class PicGameManager {

	private List<PicGame> games;

	private List<String> words;

	private Random random;

	private short pidCounter = 0; // Player ID
	private byte gidCounter = 0; // Game ID

	public PicGameManager() {
		this.games = new ArrayList<>(PicConstants.MAX_GAMES);

		this.random = new Random();
	}

	/**
	 * Charge les mots utilisés pour le jeu
	 * 
	 * @param fileName Fichier CSV contenant le fichier (sans extension)
	 * @return boolean si le chargement des mots a marché
	 */
	public boolean loadWords(String fileName) {
		try {
			this.words = new ArrayList<>();
			this.words.addAll(LoadingUtil.loadCSV(fileName));
			return true;
		} catch (IOException e) {
			String[] er = { "ERROR" };
			this.words.addAll(Arrays.asList(er));
			return false;
		}
	}

	private void updateGames() {
		for (PicGame g : games) {
			if (g.getState().equals(PicGameState.WAITING) && g.getUserCount() >= 6) {
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
		this.games.add(ng);
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
		PicRound rounds[] = new PicRound[PicConstants.AMOUNT_OF_ROUNDS];
		for (int i = 0; i < rounds.length; i++) {
			rounds[i] = new PicRound(Arrays.asList(getRandomWord(), getRandomWord(), getRandomWord()));
		}

		return new PicGame(rounds, ++gidCounter);
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
		throw new IllegalArgumentException("The user is not in a game");
	}
	
	public List<PicGame> getGames() {
		return games;
	}

	private String getRandomWord() {
		int rIndex = random.nextInt(this.words.size());
		String word = this.words.get(rIndex);
		this.words.remove(rIndex);
		return word;
	}

}
