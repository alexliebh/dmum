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
import be.alexandreliebh.picacademy.data.net.PicSocketedUser;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicDisconnectionPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicGameInfoPacket;
import be.alexandreliebh.picacademy.data.net.packet.utility.PicPingPacket;
import be.alexandreliebh.picacademy.data.util.TimedRepeater;
import be.alexandreliebh.picacademy.server.PicAcademyServer;
import be.alexandreliebh.picacademy.server.net.PicGlobalServer;

/**
 * Classe générale gérant les parties et les joueurs
 * 
 * @author Alexandre Liebhaberg
 *
 */

public class PicGameManager {

	private final ConcurrentHashMap<Byte, PicGameLifecycle> lifecycles;
	private short pidCounter = 0; // Player ID
	private byte gidCounter = 0; // Game ID

	private final PicGlobalServer netServer;

	private final List<PicSocketedUser> unpingables;

	public PicGameManager() {
		this.lifecycles = new ConcurrentHashMap<>(PicConstants.MAX_GAMES);
		this.netServer = PicAcademyServer.getInstance().getGlobalServer();
		this.unpingables = Collections.synchronizedList(new ArrayList<>(PicConstants.MAX_ONLINE_PLAYERS));

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
				continue;
			} else if (g.getUserCount() == 0) {
				this.stopGame(g.getGameID());
				continue;
			} else if (g.getCurrentRound() != null && !g.getCurrentRound().getWord().isEmpty() && g.getState().equals(PicGameState.PICKING)) {
				g.setState(PicGameState.PLAYING);
				continue;
			} else if (g.getState().equals(PicGameState.STOP)) {
				stopGame(g.getGameID());

//				for (PicSocketedUser user : g.getUsers()) {
//					TODO send back to menu
//				}
				this.updateGames();
				continue;
			}
		}
	}
	
	public void initGames() {
		for (int i = 0; i < PicConstants.MAX_GAMES; i++) {
			createGame(10000+i);
		}
	}

	/**
	 * Donne un ID au joueur
	 * 
	 * @param user le joueur à identifier
	 * @return user avec ID de joueur
	 */
	public PicSocketedUser identifyUser(PicSocketedUser user) {
		return (PicSocketedUser) user.setID(++pidCounter);
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
	
	public PicGame sendUserToGame(PicSocketedUser user, byte id) {
		PicGameLifecycle lc = lifecycles.get(id);
		if(!lc.getGameServer().isRunning())
			lc.getGameServer().start();
		PicGame game = lc.getGame();
		game.addUser(user);
		this.netServer.sendPacket(new PicGameInfoPacket(game), user);
		return game;
	}

	/**
	 * Crée une partie vide
	 * 
	 * @return partie vide
	 */
	private PicGame createGame(int port) {
		PicGame g = new PicGame(++gidCounter);
		PicGameLifecycle ng = new PicGameLifecycle(g, port);
		this.lifecycles.put(g.getGameID(), ng);
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
					for (PicGameLifecycle lc : lifecycles.values()) {
						lc.getGameServer().broadcastPacketToGame(new PicPingPacket());
						unpingables.addAll(lc.getGame().getUsers());
					}

				}
			}
		});
	}

	private void removeUnpingedUsers() {
		for (PicSocketedUser picUser : unpingables) {
			System.err.println(picUser.getIdentifier() + " timed out");
			unpingables.remove(picUser);

			PicDisconnectionPacket pdp = new PicDisconnectionPacket(picUser, DisconnectionReason.TIME_OUT);
			getLifecyclePerUser(picUser).getGameServer().broadcastPacketToGame(pdp);
			
			removeUser(picUser);
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
			for (PicSocketedUser user : game.getUsers()) {
				System.out.println("    " + user.getIdentifier());
			}
		}
		System.out.println();
	}

	public synchronized void addPingable(PicSocketedUser u) {
		try {
			getLifecyclePerUser(u);
		} catch (Exception e) {
			this.netServer.sendPacket(new PicDisconnectionPacket(u, DisconnectionReason.TIME_OUT), u);
			return;
		}

		Iterator<PicSocketedUser> uu = this.unpingables.iterator();
		while (uu.hasNext()) {
			PicSocketedUser picUser = uu.next();
			if (picUser.getID() == u.getID()) {
				uu.remove();
			}
		}
	}

	public ConcurrentHashMap<Byte, PicGameLifecycle> getLifecycles() {
		return lifecycles;
	}

}
