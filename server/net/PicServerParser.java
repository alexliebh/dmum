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
import be.alexandreliebh.picacademy.data.net.packet.game.PicGameInfoPacket;
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

	private PicNetServer server;
	private PicGameManager gameManager;

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

		if (PicConstants.debugMode) {
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

		PicConnectionPacket cp = new PicConnectionPacket(nu);
		cp.setResponse(true);
		this.server.sendPacket(cp, nu);

		PicGameInfoPacket gip = new PicGameInfoPacket(game);
		this.server.sendPacket(gip, nu);

		cp.setResponse(false);
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
			game = this.gameManager.getGamePerUser(dp.getUser());
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
		PicGame game = this.gameManager.getGamePerID(pdp.getGameID());
		PicColor color = pdp.getColor();
		for (Point pi : pdp.getLocations()) {
			game.getBoard().setPixel(pi, color);

		}
		this.server.broadcastPacketToGame(pdp, game);
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
		PicGame game = this.gameManager.getGamePerID(pdp.getGameID());
		game.getBoard().resetBoard();
		this.server.broadcastPacketToGame(pdp, game);
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
		PicGame game = this.gameManager.getGamePerID(wpp.getGameID());
		game.getCurrentRound().setWord(wpp.getWord());
		System.out.println(game.getIdentifier() +" The word \""+ wpp.getWord() + "\" was chosen");
		this.gameManager.updateGames();
		
		this.server.broadcastPacketToGame(wpp, game);
	}

	private void handleMessage(PicAbstractPacket pa) {
		PicMessagePacket pmp = (PicMessagePacket) pa;
		PicGameLifecycle plc = this.gameManager.getLifecyclePerID(pmp.getGameID());

		byte score = plc.calculateWordScore(pmp.getMessage().getContent());

		System.out.println(plc.getGame().getIdentifier() + " [" + pmp.getMessage().getSenderID() + "] : " + pmp.getMessage().getContent() + " (" + score + ")");
		this.server.broadcastPacketToGame(pa, plc.getGame());
	}

}
