package be.alexandreliebh.picacademy.server.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.game.PicGame;
import be.alexandreliebh.picacademy.data.game.PicGameState;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.PacketUtil.DisconnectionReason;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicDisconnectionPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicGameInfoPacket;
import be.alexandreliebh.picacademy.data.net.packet.utility.PicPingPacket;
import be.alexandreliebh.picacademy.data.util.TimedRepeater;
import be.alexandreliebh.picacademy.server.PicAcademyServer;
import be.alexandreliebh.picacademy.server.net.PicNetServer;

/**
 * Classe générale gérant les parties et les joueurs
 * 
 * @author Alexandre Liebhaberg
 *
 */

public class PicGameManager {

	private ConcurrentHashMap<Byte, PicGameLifecycle> lifecycles;
	private short pidCounter = 0; // Player ID
	private byte gidCounter = 0; // Game ID

	private PicNetServer netServer;

	private List<PicUser> unpingables;

	public PicGameManager() {
		this.lifecycles = new ConcurrentHashMap<>(PicConstants.MAX_GAMES);
		this.netServer = PicAcademyServer.getInstance().getNetServer();
		this.unpingables = new ArrayList<>(PicConstants.MAX_ONLINE_PLAYERS);
		this.unpingables = Collections.synchronizedList(this.unpingables);

	}

	/**
	 * Nettoie les parties :
	 * <ul>
	 * <li>Si une partie est prête, elle est lancée</li>
	 * <li>Si une partie est vide, elle est supprimée</li>
	 * </ul>
	 */
	public void updateGames() {
		for (PicGameLifecycle lc : lifecycles.values()) {
			PicGame g = lc.getGame();
			if (g.getState().equals(PicGameState.WAITING) && g.getUserCount() >= PicConstants.MAX_PLAYERS_PER_GAME) {
				this.startGame(g.getGameID());
			} else if (g.getUserCount() == 0) {
				this.stopGame(g.getGameID());
			} else if (g.getCurrentRound() != null && !g.getCurrentRound().getWord().isEmpty() && g.getState().equals(PicGameState.PICKING)) {
				g.setState(PicGameState.PLAYING);
			} else if (g.getState().equals(PicGameState.STOP)) {
				stopGame(g.getGameID());

				displayGames();
				for (PicUser user : g.getUsers()) {
					addUserToGame(user);
				}
				displayGames();
				updateGames();
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
		PicGame g = getLifecyclePerUser(user).getGame();
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
		if (!this.lifecycles.isEmpty()) {
			for (PicGameLifecycle lc : lifecycles.values()) {
				PicGame game = lc.getGame();
				if (game.getState().equals(PicGameState.WAITING)) {
					return sendToGame(user, game);
				}
			}

		}

		PicGame ng = createGame();
		return sendToGame(user, ng);

	}

	/**
	 * Connecte un utilisateur à une partieq
	 * 
	 * @param user à envoyer dans la partie
	 * @param game partie dans laquelle on envoie le joueur
	 * @return la partie dans laquelle le joueur est envoyé
	 */
	private PicGame sendToGame(PicUser user, PicGame game) {
		game.addUser(user);
		this.netServer.sendPacket(new PicGameInfoPacket(game), user);
		return game;
	}

	/**
	 * Crée une partie vide
	 * 
	 * @return partie vide
	 */
	private PicGame createGame() {
		PicGame g = new PicGame(++gidCounter);
		this.lifecycles.put(g.getGameID(), new PicGameLifecycle(g));
		return g;
	}

	/**
	 * Lance la partie
	 * 
	 * @param Id de la partie
	 */
	private void startGame(byte gameID) {
		this.lifecycles.get(gameID).startPicking();
		startPinging();
	}

	/**
	 * Arrête la partie
	 * 
	 * @param Id de la partie
	 */
	private void stopGame(byte gameID) {
		this.lifecycles.get(gameID).stop();
		this.lifecycles.remove(gameID);
	}

	public void startPinging() {
		TimedRepeater tr = new TimedRepeater(0, 10);
		tr.start(new Runnable() {
			public void run() {
				synchronized (this) {
					removeUnpingedUsers();
					netServer.broadcastPacket(new PicPingPacket());
					for (PicGameLifecycle lc : lifecycles.values()) {
						unpingables.addAll(lc.getGame().getUsers());
					}

				}
			}
		});
	}

	private void removeUnpingedUsers() {
		for (PicUser picUser : unpingables) {
			System.err.println(picUser.getIdentifier() + " timed out");
			unpingables.remove(picUser);
			PicDisconnectionPacket pdp = new PicDisconnectionPacket(picUser, DisconnectionReason.TIME_OUT);
			netServer.broadcastPacketToGame(pdp, getLifecyclePerUser(picUser).getGame());
			getLifecyclePerUser(picUser).getGame().removeUser(picUser);
		}
	}

	/**
	 * Récupère la partie dans laquelle le joueur est
	 * 
	 * @param user le joueur dont on veut la partie
	 * @return la partie dans laquelle le joueur est
	 */
	public final PicGameLifecycle getLifecyclePerUser(PicUser user) throws IllegalArgumentException {
		for (PicGameLifecycle lc : lifecycles.values()) {
			if (lc.getGame().hasUser(user.getID())) {
				return lc;
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
	public final PicGameLifecycle getLifecyclePerID(byte gameID) {
		try {
			return lifecycles.get(gameID);
		} catch (Exception e) {
			throw new IllegalArgumentException("The ID doesn't fit any lifecycle");
		}
	}

	public void displayGames() {
		for (PicGameLifecycle lc : lifecycles.values()) {
			PicGame game = lc.getGame();
			System.out.println();
			System.out.println(game.getIdentifier() + " :");
			for (PicUser user : game.getUsers()) {
				System.out.println("    " + user.getIdentifier());
			}
		}
		System.out.println();
	}

	public synchronized void addPingable(PicUser u) {
		Iterator<PicUser> uu = this.unpingables.iterator();
		while (uu.hasNext()) {
			PicUser picUser = uu.next();
			if (picUser.getID() == u.getID()) {
				uu.remove();
			}
		}
	}

	public ConcurrentHashMap<Byte, PicGameLifecycle> getLifecycles() {
		return lifecycles;
	}

}
