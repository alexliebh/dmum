package be.alexandreliebh.picacademy.server.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.PacketUtil;
import be.alexandreliebh.picacademy.data.net.PicAddress;
import be.alexandreliebh.picacademy.data.net.PicSocketedUser;
import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.server.PicAcademyServer;

/**
 * Classe gérant la connnexion aux clients
 * 
 * @author Alexandre Liebhaberg
 */
public abstract class PicNetServer {

	/**
	 * Socket du serveur
	 */
	protected final ServerSocket socket;

	/**
	 * Adresse locale du serveur
	 */
	protected final PicAddress localAddress;

	protected PrintStream socketOut;
	protected BufferedReader socketIn;

	/**
	 * Thread qui va recevoir les packets
	 */
	protected Thread receive;

	/**
	 * Utilisateur fictif du serveur pour envoyer des packets
	 */
	protected final PicSocketedUser serverUser;

	private boolean running;

	private String name;

	/**
	 * Ouvre le socket sur le port spécifié
	 * 
	 * @param port sur lequel ouvrir le serveur
	 * @throws IOException
	 */
	public PicNetServer(int port, String name) throws IOException {
		this.name = name;
		this.socket = new ServerSocket(port);

		this.localAddress = new PicAddress(PicAcademyServer.getInstance().getLocalIP(), this.socket.getLocalPort());
		this.serverUser = (PicSocketedUser) new PicSocketedUser("SERVER").setID((short) 0);
	}

	/**
	 * Récupère les packets et les envoies pour se faire traiter
	 */
	private void listen() {
		this.receive = new Thread("Receive for "+name) {
			public void run() {
				while (running) {
					receivePacket();
				}
			}
		};
		this.receive.start();
		System.out.println("Listening thread started");
	}

	protected abstract void accept();

	protected abstract void receivePacket();

	/**
	 * Envoie un packet à un utilisateur
	 * 
	 * @param pa   PicAbstractPacket packet à envoyer
	 * @param user PicUser utilisateur à qui envoyer le packet
	 */
	public void sendPacket(PicAbstractPacket pa, PicSocketedUser user) {
		try {
			pa.setSender(this.serverUser);

			if (PicConstants.DEBUG_MODE) {
				System.out.println("[-] Sent packet of type " + pa.getType());
			}

			byte[] packBytes = PacketUtil.getPacketAsBytes(pa);
			user.getOutStream().println(packBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void start() {
		this.running = true;
		this.listen();
	}

	public synchronized void stop() throws IOException {
		this.running = false;
		this.socket.close();
	}
	
	public boolean isRunning() {
		return running;
	}

	public PicAddress getLocalAdress() {
		return localAddress;
	}

	public PicUser getServerUser() {
		return serverUser;
	}

}
