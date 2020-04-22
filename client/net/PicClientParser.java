package be.alexandreliebh.picacademy.client.net;

import java.awt.Point;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

import be.alexandreliebh.picacademy.client.PicAcademy;
import be.alexandreliebh.picacademy.client.game.PicGameLoop;
import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.game.PicRound;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.PacketUtil;
import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicConnectionPacket;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicDisconnectionPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicClearBoardPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicDrawPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicGameInfoPacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicMessagePacket;
import be.alexandreliebh.picacademy.data.net.packet.game.PicWordPickedPacket;
import be.alexandreliebh.picacademy.data.net.packet.round.PicRoundEndPacket;
import be.alexandreliebh.picacademy.data.net.packet.round.PicRoundInfoPacket;
import be.alexandreliebh.picacademy.data.net.packet.round.PicRoundTickPacket;
import be.alexandreliebh.picacademy.data.net.packet.utility.PicPingPacket;

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
	 * Récupère le packet cru et le transforme en {@link PicAbstractPacket} puis
	 * sous-traite dans une fonction séparée
	 * 
	 * @param dPa packet cru reçu du socket
	 * @throws IOException
	 */
	public void parsePacket(DatagramPacket dPa) throws IOException {
		PicAbstractPacket pa = PacketUtil.getBytesAsPacket(dPa.getData());

		if (PicConstants.DEBUG_MODE) {
			System.out.println("[+] Received packet of type " + pa.getType().toString());
		}

		switch (pa.getType()) {

		case CONNECTION:
			handleConnection((PicConnectionPacket) pa);
			break;

		case DISCONNECTION:
			handleDisconnection((PicDisconnectionPacket) pa);
			break;

		case GAME_INFO:
			handleGameInfo((PicGameInfoPacket) pa);
			break;

		case CLEAR:
			handleClearBoard((PicClearBoardPacket) pa);
			break;

		case DRAW:
			handleColorPixelOnBoard((PicDrawPacket) pa);
			break;

		case WORD_PICKED:
			handleWordPicked((PicWordPickedPacket) pa);
			break;

		case ROUND_INFO:
			handleRoundInfo((PicRoundInfoPacket) pa);
			break;

		case MESSAGE:
			handleMessage((PicMessagePacket) pa);
			break;

		case ROUND_END:
			handleRoundEnd((PicRoundEndPacket) pa);
			break;

		case ROUND_TICK:
			handleRoundTick((PicRoundTickPacket) pa);
			break;

		case PING:
			handlePing((PicPingPacket) pa);
			break;

		default:
			break;
		}

	}

	private void handleConnection(PicConnectionPacket cp) {
		PicUser nu = cp.getUser();
		if (cp.isResponse()) {
			this.gLoop.setConnected(true);
			this.client.setUserObject(nu);
			this.gLoop.setCurrentUser(nu);
			System.out.println("You're connected ! ID = " + this.client.getUserObject().getID());
		} else {
			if (nu.getID() != this.client.getUserObject().getID()) {
				this.gLoop.addUser(nu);
			}
		}
	}

	private void handleDisconnection(PicDisconnectionPacket dp) {
		PicUser nu = dp.getUser();
		if (nu.getID() == this.client.getUserObject().getID()) {
			System.err.println("Server thinks you " + dp.getReason());
			System.exit(-1);
			return;
		}
		this.gLoop.removeUser(nu);
	}

	private void handleGameInfo(PicGameInfoPacket gip) {
		this.gLoop.setGameInfo(gip.getGameID(), gip.getUsers());
		System.out.println("Logged in game ID:" + gip.getGameID() + " (" + gip.getUsers().size() + " connected users)");
	}

	private void handleClearBoard(PicClearBoardPacket cbp) {
		this.gLoop.getBoard().resetBoard();
	}

	private void handleColorPixelOnBoard(PicDrawPacket pdp) {
		for (Point p : pdp.getLocations()) {
			this.gLoop.getBoard().setPixel(p, pdp.getColor());
		}
		this.gLoop.setUnitsToDraw(Arrays.asList(pdp.getLocations()), pdp.getColor());
	}

	private void handleRoundInfo(PicRoundInfoPacket rip) {
		PicRound round = rip.getRound();
		this.gLoop.setRoundInfo(round.getRoundId(), round.getDrawingUser(), round.getWords());

		this.gLoop.startPicking();

	}

	private void handleWordPicked(PicWordPickedPacket wpp) {
		this.gLoop.setWord(wpp.getWord());
		this.gLoop.startDrawing();
	}

	private void handleMessage(PicMessagePacket pmp) {
		this.gLoop.receiveMessage(pmp.getMessage());
	}

	private void handleRoundEnd(PicRoundEndPacket rep) {
		this.gLoop.endRound();
	}

	private void handleRoundTick(PicRoundTickPacket rtp) {
		this.gLoop.setTimer(rtp.getTick());
	}

	private void handlePing(PicPingPacket pp) {
		this.client.sendPacket(pp);
		this.gLoop.setConnected(true);
	}

}
