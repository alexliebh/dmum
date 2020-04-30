package be.alexandreliebh.picacademy.server.net;

import java.awt.Point;
import java.io.IOException;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.net.PacketUtil;
import be.alexandreliebh.picacademy.data.net.PicSocketedUser;
import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicConnectionPacket;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicDisconnectionPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicClearBoardPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicDrawPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicMessagePacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicWordPickedPacket;
import be.alexandreliebh.picacademy.data.net.packet.utility.PicPingPacket;
import be.alexandreliebh.picacademy.data.ui.PicColor;
import be.alexandreliebh.picacademy.server.PicAcademyServer;
import be.alexandreliebh.picacademy.server.game.PicGameLifecycle;

/**
 * Classe qui gère le traitement des packets entrants pour le serveur
 * 
 * @author Alexandre Liebhaberg
 *
 */
public class PicGameServerParser {

	private final PicGameServer server;

	public PicGameServerParser(PicGameServer picServer) {
		this.server = picServer;
	}

	/**
	 * Récupère le packet cru et le transforme en {@link PicAbstractPacket} puis
	 * sous-traite dans une fonction séparée
	 * 
	 * @param dPa packet cru reçu du socket
	 * @throws IOException
	 */
	public void parsePacket(String packet) throws IOException {
		PicAbstractPacket pa = PacketUtil.getBytesAsPacket(packet.getBytes());

		if (PicConstants.DEBUG_MODE) {
			System.out.println("[+] Received packet of type " + pa.getType());
		}

		switch (pa.getType()) {
		case CONNECTION:
			handleConnection((PicConnectionPacket) pa);
			break;

		case DISCONNECTION:
			handleDisconnection((PicDisconnectionPacket) pa);
			break;

		case CLEAR:
			handleClear((PicClearBoardPacket) pa);
			break;

		case DRAW:
			handleDraw((PicDrawPacket) pa);
			break;

		case WORD_PICKED:
			handleWordPicked((PicWordPickedPacket) pa);
			break;

		case MESSAGE:
			handleMessage((PicMessagePacket) pa);
			break;

		case PING:
			handlePing((PicPingPacket) pa);
			break;

		default:
			break;
		}
	}

	/**
	 * Traite la reception d'un packet de type {@link PicPacketType} CONNECTION
	 * 
	 * 
	 * @param pa {@link PicAbstractPacket}
	 */
	private void handleConnection(PicConnectionPacket pa) {
		PicSocketedUser nu = pa.getUser();
		PicAcademyServer.getInstance().getGameManager().sendUserToGame(nu, server.getLifecycle().getGame().getGameID());

		PicConnectionPacket cp = new PicConnectionPacket(nu, true);
		this.server.sendPacket(cp, nu);

		cp = new PicConnectionPacket(cp, false);
		this.server.broadcastPacketToGame(cp);
	}

	/**
	 * Traite la reception d'un packet de type {@link PicPacketType} DISCONNECTION A
	 * la déconnexion d'un joueur
	 * 
	 * @param pa  {@link PicAbstractPacket}
	 * @param dPa
	 */
	private void handleDisconnection(PicDisconnectionPacket pa) {
		PicDisconnectionPacket dp = (PicDisconnectionPacket) pa;

		PicAcademyServer.getInstance().getGameManager().removeUser(dp.getUser());
		this.server.broadcastPacketToGame(dp);

	}

	/**
	 * Traite la reception d'un packet de type {@link PicPacketType} DRAW Reception
	 * des données de dessin
	 * 
	 * @param pa  {@link PicAbstractPacket}
	 * @param dPa
	 */
	private void handleDraw(PicDrawPacket pa) {
		PicDrawPacket pdp = (PicDrawPacket) pa;
		PicColor color = pdp.getColor();
		for (Point pi : pdp.getLocations()) {
			server.getLifecycle().getGame().getBoard().setPixel(pi, color);

		}
		this.server.broadcastPacketToGame(pdp);
	}

	/**
	 * Traite la reception d'un packet de type {@link PicPacketType} CLEAR Vide la
	 * représentation de la zone de dessin
	 * 
	 * @param pa  {@link PicAbstractPacket}
	 * @param dPa
	 */
	private void handleClear(PicClearBoardPacket pa) {
		PicClearBoardPacket pdp = (PicClearBoardPacket) pa;
		server.getLifecycle().getGame().getBoard().resetBoard();
		this.server.broadcastPacketToGame(pdp);
	}

	/**
	 * Traite la reception d'un packet de type {@link PicPacketType} WORD_PICKED
	 * Lorsque le dessinateur a choisi un mot
	 * 
	 * @param pa  {@link PicAbstractPacket}
	 * @param dPa
	 */
	private void handleWordPicked(PicWordPickedPacket pa) {
		PicWordPickedPacket wpp = (PicWordPickedPacket) pa;
		server.getLifecycle().getGame().getCurrentRound().setWord(wpp.getWord());
		System.out.println(server.getLifecycle().getGame().getIdentifier() + " The word \"" + wpp.getWord() + "\" was chosen");
		updateGames();

		this.server.broadcastPacketToGame(wpp);
		server.getLifecycle().startDrawing();
	}

	private void handleMessage(PicMessagePacket pa) {
		PicMessagePacket pmp = (PicMessagePacket) pa;
		PicGameLifecycle plc = server.getLifecycle();

		byte score = plc.calculateWordScore(pmp.getMessage().getContent());
		pmp.getMessage().setScore(score);
		plc.addToPlayerScore(pmp.getSender().getID(), score);

		System.out.println(plc.getGame().getIdentifier() + " [" + pmp.getMessage().getSenderID() + "] : " + pmp.getMessage().getContent() + " (" + score + ")");
		this.server.broadcastPacketToGame(pmp);
	}

	private void handlePing(PicPingPacket pp) {
		PicAcademyServer.getInstance().getGameManager().addPingable(pp.getSender());
	}
	
	private void updateGames() {
		PicAcademyServer.getInstance().getGameManager().updateGames();
	}

}
