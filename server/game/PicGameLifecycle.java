package be.alexandreliebh.picacademy.server.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.game.PicGame;
import be.alexandreliebh.picacademy.data.game.PicGameState;
import be.alexandreliebh.picacademy.data.game.PicRound;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.packet.game.PicRoundInfoPacket;
import be.alexandreliebh.picacademy.data.util.LoadingUtil;
import be.alexandreliebh.picacademy.server.PicAcademyServer;
import be.alexandreliebh.picacademy.server.net.PicNetServer;

public class PicGameLifecycle {

	private PicGame game;
	private PicNetServer net;

	private List<PicUser> pickedUsers;

	private final Random rand = new Random();

	public PicGameLifecycle(PicGame game) {
		this.game = game;
		this.net = PicAcademyServer.getInstance().getNetServer();
		this.pickedUsers = new ArrayList<PicUser>();
	}

	public void startRound() {

		if (this.pickedUsers.size() == this.game.getUserCount()) {
			this.pickedUsers.clear();
		}
		this.game.setState(PicGameState.PICKING);

		PicUser main = pickMainPlayer();
		List<String> words = PicWordGenerator.getRandomWords(3);
		PicRound round = new PicRound(this.game.nextRound(), words, main);
		PicRoundInfoPacket rip = new PicRoundInfoPacket(round, this.game.getGameID());

		System.out.println(this.game.getIdentifier() + " main player: " + main.getIdentifier()+ "  "+ LoadingUtil.listToString(words, "|"));

		this.net.broadcastPacketToGame(rip, game);
	}

	private PicUser pickMainPlayer() {
		boolean inPicked = true;
		PicUser user = null;
		while (inPicked) {
			int ind = this.rand.nextInt(PicConstants.MAX_PLAYERS_PER_GAME);
			user = this.game.getUsers().get(ind);
			inPicked = this.pickedUsers.contains(user);
		}
		this.pickedUsers.add(user);
		return user;
	}
}
