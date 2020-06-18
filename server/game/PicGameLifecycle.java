package be.alexandreliebh.picacademy.server.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.game.PicGame;
import be.alexandreliebh.picacademy.data.game.PicGameState;
import be.alexandreliebh.picacademy.data.game.PicRound;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.packet.round.PicRoundEndPacket;
import be.alexandreliebh.picacademy.data.net.packet.round.PicRoundInfoPacket;
import be.alexandreliebh.picacademy.data.net.packet.round.PicRoundTickPacket;
import be.alexandreliebh.picacademy.data.util.LoadingUtil;
import be.alexandreliebh.picacademy.server.PicAcademyServer;
import be.alexandreliebh.picacademy.server.game.PicGameScheduler.PicTimeListener;
import be.alexandreliebh.picacademy.server.net.PicNetServer;

/**
 * Classe qui gère le déroulement d'une partie
 * 
 * @author Alexandre Liebhaberg
 */
public class PicGameLifecycle {

	private final PicGame game;
	private final PicNetServer net;

	private final PicWordGenerator generator;
	private final PicGameScheduler timer;

	private final List<PicUser> pickedUsers;

	private final Random rand = new Random();

	public PicGameLifecycle(PicGame game) {
		this.game = game;
		this.net = PicAcademyServer.getInstance().getNetServer();
		this.pickedUsers = new ArrayList<PicUser>();
		this.generator = new PicWordGenerator(PicAcademyServer.getInstance().getWords());
		this.timer = new PicGameScheduler();
		this.timer.addTimeListener(getTimeListener());

	}

	/**
	 * Lance une manche de jeu
	 */
	public void startPicking() {
		// Si tous les joueurs ont déjà été sélectionnées, un nouveau cycle commence,
		// ils peuvent donc tous être choisis
		if (this.pickedUsers.size() == this.game.getUserCount()) {
			this.pickedUsers.clear();
		}
		

		short mainID = this.pickMainPlayer();
		List<String> words = this.generator.getRandomWords(3);
		
		PicRound round = new PicRound(words, mainID);
		round = game.nextRound(round);
		PicRoundInfoPacket rip = new PicRoundInfoPacket(round, this.game.getGameID());

		this.game.setState(PicGameState.PICKING);

		System.out.println(this.game.getIdentifier() + " main player: " + mainID + "  " + LoadingUtil.listToString(words, "|"));

		this.net.broadcastPacketToGame(rip, game);

		PicAcademyServer.getInstance().getGameManager().updateGames();
	}

	public synchronized void startDrawing() {
		if (game.getCurrentRound().getRoundId() != 0) {
			this.timer.restart();
		} else {
			this.timer.start();
		}
		System.out.println(game.getIdentifier() + " Round " + (game.getCurrentRound().getRoundId() + (byte) 1) + " started");
	}

	public void endRound() {
		System.out.println(game.getIdentifier() + " Round " + (game.getCurrentRound().getRoundId() + (byte) 1) + " ended");
		net.broadcastPacketToGame(new PicRoundEndPacket(game.getGameID()), game);
		this.game.getBoard().resetBoard();
		this.game.setState(PicGameState.FINISHED);

		if (isOver()) {
			endGame();
		} else {
			restart(5);
		}

	}

	private void endGame() {
		System.out.println(this.game.getIdentifier() + " All rounds are over");
		this.game.setState(PicGameState.STOP);
		PicAcademyServer.getInstance().getGameManager().updateGames();
	}

	private void restart(int timeInSeconds) {
		Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
			public void run() {
				startPicking();
			}
		}, timeInSeconds, TimeUnit.SECONDS);
	}

	/**
	 * Choisit un joueur pour être le dessinateur
	 * 
	 * @return un joueur pas encore pris dans le cycle
	 */
	private short pickMainPlayer() {
		boolean inPicked = true;
		PicUser user = null;
		while (inPicked) {
			int ind = this.rand.nextInt(PicConstants.MAX_PLAYERS_PER_GAME);
			user = this.game.getUsers().get(ind);
			inPicked = this.pickedUsers.contains(user);
		}
		this.pickedUsers.add(user);
		return user.getID();
	}

	public int calculateWordScore(String msg) {
		if(null == game.getCurrentRound().getWord() || this.game.getCurrentRound().getWord().equals(""))
			return 0;
		String word = this.game.getCurrentRound().getWord();
		int max = Math.max(msg.length(), word.length());
		int score = (max - LoadingUtil.hammingDist(word, msg.toUpperCase())) + timer.getTimer();
		return score;
	}
	
	public boolean isWordSimilar(String msg) {
		return msg.toUpperCase().equalsIgnoreCase(this.game.getCurrentRound().getWord().toUpperCase());
	}

	public void addToPlayerScore(short pid, int score) {
		getUserFromId(pid).addToScore(score);
	}

	public PicTimeListener getTimeListener() {
		return new PicTimeListener() {

			public void onTimeTick(byte timer) {
				net.broadcastPacketToGame(new PicRoundTickPacket(timer, game.getGameID()), game);
			}

			public void onRoundEnd() {
				endRound();
			}
		};
	}

	public PicUser getUserFromId(short id) {
		for (PicUser picUser : game.getUsers()) {
			if (picUser.getID() == id) {
				return picUser;
			}
		}
		throw new IllegalArgumentException("The ID doesn't fit any user");
	}

	private boolean isOver() {
		return this.game.getCurrentRound().getRoundId() == this.game.getRoundAmount() - 1;
	}

	public void stop() {
		this.timer.stop();
		this.game.stop();
		System.out.println(game.getIdentifier() + " has been stopped");
	}

	public PicGame getGame() {
		return game;
	}

}
