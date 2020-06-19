package be.alexandreliebh.picacademy.server.net;

import java.awt.Point;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.game.PicGame;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.PacketUtil;
import be.alexandreliebh.picacademy.data.net.PicAddress;
import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicConnectionPacket;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicDisconnectionPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicClearBoardPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicDrawPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicMessagePacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicWordPickedPacket;
import be.alexandreliebh.picacademy.data.ui.PicColor;
import be.alexandreliebh.picacademy.server.game.PicGameLifecycle;
import be.alexandreliebh.picacademy.server.game.PicGameManager;

/**
 * Classe qui gère le traitement des packets entrants pour le serveur
 * 
 * @author Alexandre Liebhaberg
 *
 */
public class PicServerParser {

	private final PicNetServer server;
	private final PicGameManager gameManager;

	public PicServerParser(PicNetServer picServer, PicGameManager gameManager) {
		this.server = picServer;
		this.gameManager = gameManager;
	}

	/**
	 * Récupère le packet cru et le transforme en {@link PicAbstractPacket} puis
	 * sous-traite dans une fonction séparée
	 * 
	 * @param dPa packet cru reçu du socket
	 * @throws IOException
	 */
	public void parsePacket(DatagramPacket dPa) throws IOException {
		PicAbstractPacket pa = PacketUtil.getBytesAsPacket(dPa.getData());

		if (PicConstants.DEBUG_MODE) {
			System.out.println("[+] Received packet of type " + pa.getType());
		}

		switch (pa.getType()) {
		case CONNECTION:
			handleConnection(pa, dPa);
			break;
			
		case DISCONNECTION:
			handleDisconnection(pa);
			break;
			
		case CLEAR:
			handleClear(pa);
			break;
			
		case DRAW:
			handleDraw(pa);
			break;
			
		case WORD_PICKED:
			handleWordPicked(pa);
			break;
			
		case MESSAGE:
			handleMessage(pa);
			break;
			
		default:
			break;
		}
	}

	/**
	 * Traite la reception d'un packet de type {@link PicPacketType} CONNECTION
	 * 
	 * 
	 * @param pa  {@link PicAbstractPacket}
	 * @param dPa
	 */
	private void handleConnection(PicAbstractPacket pa, DatagramPacket dPa) {
		PicUser nu = pa.getSender().setAddress(new PicAddress((InetSocketAddress) dPa.getSocketAddress()));
		nu = this.gameManager.identifyUser(nu);

		PicGame game = this.gameManager.addUserToGame(nu);

		PicConnectionPacket cp = new PicConnectionPacket(nu, true);
		this.server.sendPacket(cp, nu);

		cp = new PicConnectionPacket(cp, false);
		this.server.broadcastPacketToGame(cp, game);

		this.gameManager.updateGames();

	}

	/**
	 * Traite la reception d'un packet de type {@link PicPacketType} DISCONNECTION A
	 * la déconnexion d'un joueur
	 * 
	 * @param pa  {@link PicAbstractPacket}
	 * @param dPa
	 */
	private void handleDisconnection(PicAbstractPacket pa) {
		PicDisconnectionPacket dp = (PicDisconnectionPacket) pa;

		PicGame game = null;
		try {
			game = this.gameManager.getLifecyclePerUser(dp.getUser()).getGame();
		} catch (Exception e) {
			return;
		}

		this.gameManager.removeUser(dp.getUser());
		this.server.broadcastPacketToGame(dp, game);

	}

	/**
	 * Traite la reception d'un packet de type {@link PicPacketType} DRAW Reception
	 * des données de dessin
	 * 
	 * @param pa  {@link PicAbstractPacket}
	 * @param dPa
	 */
	private void handleDraw(PicAbstractPacket pa) {
		PicDrawPacket pdp = (PicDrawPacket) pa;
		PicGameLifecycle lc = this.gameManager.getLifecyclePerID(pdp.getGameID());
		PicColor color = pdp.getColor();
		for (Point pi : pdp.getLocations()) {
			lc.getGame().getBoard().setPixel(pi.x, pi.y, color);

		}
		this.server.broadcastPacketToGame(pdp, lc.getGame());
	}

	/**
	 * Traite la reception d'un packet de type {@link PicPacketType} CLEAR Vide la
	 * représentation de la zone de dessin
	 * 
	 * @param pa  {@link PicAbstractPacket}
	 * @param dPa
	 */
	private void handleClear(PicAbstractPacket pa) {
		PicClearBoardPacket pdp = (PicClearBoardPacket) pa;
		PicGameLifecycle lc = this.gameManager.getLifecyclePerID(pdp.getGameID());
		lc.getGame().getBoard().resetBoard();
		this.server.broadcastPacketToGame(pdp, lc.getGame());
	}

	/**
	 * Traite la reception d'un packet de type {@link PicPacketType} WORD_PICKED
	 * Lorsque le dessinateur a choisi un mot
	 * 
	 * @param pa  {@link PicAbstractPacket}
	 * @param dPa
	 */
	private void handleWordPicked(PicAbstractPacket pa) {
		PicWordPickedPacket wpp = (PicWordPickedPacket) pa;
		PicGameLifecycle lc = this.gameManager.getLifecyclePerID(wpp.getGameID());
		lc.getGame().getCurrentRound().setWord(wpp.getWord());
		System.out.println(lc.getGame().getIdentifier() + " The word \"" + wpp.getWord() + "\" was chosen");
		this.gameManager.updateGames();

		this.server.broadcastPacketToGame(wpp, lc.getGame());
		lc.startDrawing();
	}

	private void handleMessage(PicAbstractPacket pa) {
		PicMessagePacket pmp = (PicMessagePacket) pa;
		PicGameLifecycle plc = this.gameManager.getLifecyclePerID(pmp.getGameID());

		int score = plc.calculateWordScore(pmp.getMessage().getContent());
		pmp.getMessage().setScore(score);
		plc.addToPlayerScore(pmp.getSender().getID(), score);
		pmp.getMessage().setSuccessful(plc.isWordSimilar(pmp.getMessage().getContent(), pmp.getSender().getID()));

		System.out.println(plc.getGame().getIdentifier() + " [" + pmp.getMessage().getSenderID() + "] : " + pmp.getMessage().getContent() + " (" + score + ")");
		this.server.broadcastPacketToGame(pmp, plc.getGame());
	}

}
