package be.alexandreliebh.picacademy.server.net;

import java.io.IOException;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.net.PicSocketedUser;
import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.server.game.PicGameLifecycle;

/**
 * Classe gérant la connnexion aux clients
 * 
 * @author Alexandre Liebhaberg
 */
public class PicGameServer extends PicNetServer {

	private PicGameServerParser parser;
	private PicGameLifecycle lifecycle;
	
	private Thread accepter;

	public PicGameServer(int port, PicGameLifecycle lifeCycle) throws IOException {
		super(port, "Game Server ("+port+")");
		
		this.lifecycle = lifeCycle;
		
		this.accepter = new Thread(new Runnable() {
			public void run() {
				byte i = 0;
				while (i < PicConstants.MAX_PLAYERS_PER_GAME) {
					accept();
					i++;
				}
				try {
					accepter.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		this.parser = new PicGameServerParser(this);
		System.out.println("Game server on port " + port + " ready");

	}

	@Override
	protected void accept() {
		try {
			this.socket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void receivePacket() {
		try {
			String compPacket = this.socketIn.readLine();
			this.parser.parsePacket(compPacket);
		} catch (IOException e) {
			System.err.println("ERROR while receiving a packet");
		}
	}

	/**
	 * Envoie un packet à tous les utilisateurs connectés au serveur
	 * 
	 * @param PicAbstractPacket packet à envoyer
	 */
	public void broadcastPacketToGame(PicAbstractPacket pa) {
		for (PicSocketedUser u : lifecycle.getGame().getUsers()) {
			sendPacket(pa, u);
		}
	}
	
	@Override
	public synchronized void start() {
		this.accepter.start();
		super.start();
	}
	
	@Override
	public synchronized void stop() throws IOException {
		try {
			this.accepter.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.stop();
	}
	
	public PicGameLifecycle getLifecycle() {
		return lifecycle;
	}

}
