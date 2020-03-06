package be.alexandreliebh.picacademy.client.net;

import java.awt.Point;
import java.io.IOException;
import java.net.DatagramPacket;

import be.alexandreliebh.picacademy.client.PicAcademy;
import be.alexandreliebh.picacademy.client.game.PicGameLoop;
import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.PacketUtil;
import be.alexandreliebh.picacademy.data.net.packet.PicPacket;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicConnectionPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicDrawPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicGameInfoPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicRoundInfoPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicWordPickedPacket;

/**
 * Classe qui gère le traitement des packets entrants pour le client
 * 
 * @author Alexandre Liebhaberg
 *
 */
public class PicClientParser {

	private PicNetClient client;
	private PicGameLoop gLoop;

	public PicClientParser(PicNetClient client) {
		this.client = client;
		this.gLoop = PicAcademy.getInstance().getGameLoop();
	}

	/**
	 * Récupère le packet cru et le transforme en {@link PicPacket} puis sous-traite
	 * dans une fonction séparée
	 * 
	 * @param dPa packet cru reçu du socket
	 * @throws IOException
	 */
	public void parsePacket(DatagramPacket dPa) throws IOException {
		PicPacket pa = PacketUtil.getBytesAsPacket(dPa.getData());

		if (PicConstants.debugMode) {
			System.out.println("[+] Received packet of type " + pa.getType().toString());
		}

		switch (pa.getType()) {
		case CONNECTION:
			handleConnection(pa);
			break;
		case GAME_INFO:
			handleGameInfo(pa);
			break;
		case CLEAR:
			handleClearBoard(pa);
			break;
		case DRAW:
			handleColorPixelOnBoard(pa);
			break;
		case WORD_PICKED:
			handleWordPicked(pa);
			break;
		case ROUND_INFO:
			handleRoundInfo(pa);
			break;

		default:
			break;
		}

	}

	private void handleConnection(PicPacket pa) {
		PicConnectionPacket cp = (PicConnectionPacket) pa;
		PicUser nu = cp.getUser();
		if (cp.isResponse()) {
			this.client.setUserObject(nu);
			System.out.println("You're connected ! ID = " + this.client.getUserObject().getID());
		} else {
			if (nu.getID() == this.client.getUserObject().getID()) {
				return;
			}
			this.gLoop.addUser(nu);
			System.out.println("User " + nu.getIdentifier() + " connected to the game");
		}
	}

	private void handleGameInfo(PicPacket pa) {
		PicGameInfoPacket gip = (PicGameInfoPacket) pa;
		this.gLoop.setGameID(gip.getGameID());
		this.gLoop.setUsers(gip.getUsers());
		this.gLoop.setState(gip.getState());
		System.out.println("Logged in game ID:" + gip.getGameID());
	}

	private void handleClearBoard(PicPacket pa) {
		this.gLoop.getBoard().resetBoard();
	}

	private void handleColorPixelOnBoard(PicPacket pa) {
		PicDrawPacket pdp = (PicDrawPacket) pa;
		for (Point p : pdp.getLocations()) {
			this.gLoop.getBoard().setPixel(p, pdp.getColor());
		}
	}

	private void handleWordPicked(PicPacket pa) {
		PicWordPickedPacket wpp = (PicWordPickedPacket) pa;
		this.gLoop.setWord(wpp.getWord());
	}

	private void handleRoundInfo(PicPacket pa) {
		PicRoundInfoPacket rip = (PicRoundInfoPacket) pa;
		this.gLoop.setMainUser(this.gLoop.getUserFromId(rip.getMainPlayerId()));
		this.gLoop.setRoundID(rip.getRoundId());
		this.gLoop.setWords(rip.getWords());

	}

}
