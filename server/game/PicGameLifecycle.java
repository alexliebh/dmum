package be.alexandreliebh.picacademy.server.game;

import be.alexandreliebh.picacademy.data.game.PicGame;
import be.alexandreliebh.picacademy.server.PicAcademyServer;
import be.alexandreliebh.picacademy.server.net.PicNetServer;

public class PicGameLifecycle implements Runnable {

	private PicGame game;
	private PicNetServer net;

	public PicGameLifecycle(PicGame game) {
		this.game = game;
		this.net = PicAcademyServer.getInstance().getNetServer();

	}

	public void run() {
		
	}

	public PicGame getGame() {
		return game;
	}
}
