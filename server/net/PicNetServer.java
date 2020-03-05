package be.alexandreliebh.picacademy.server.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.game.PicGame;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.PacketUtil;
import be.alexandreliebh.picacademy.data.net.PicAddress;
import be.alexandreliebh.picacademy.data.net.packet.PicPacket;
import be.alexandreliebh.picacademy.data.util.NetworkUtil;
import be.alexandreliebh.picacademy.server.game.PicGameManager;

/**
 * Classe gérant la connnexion aux clients
 * 
 * @author Alexandre Liebhaberg
 */
public class PicNetServer {

	/**
	 * Socket du serveur
	 */
	private DatagramSocket socket;

	/**
	 * Adresse locale du serveur
	 */
	private PicAddress localAddress;

	/**
	 * Thread qui va recevoir les packets
	 */
	private Thread receive;

	private PicServerParser parser;
	private PicGameManager gameManager;

	private boolean running;

	/**
	 * Utilisateur fictif du serveur pour envoyer des packets
	 */
	private PicUser serverUser;

	/**
	 * Ouvre le socket sur le port spécifié
	 * 
	 * @param port sur lequel ouvrir le serveur
	 * @throws IOException
	 */
	public PicNetServer(int port) throws IOException {
		this.socket = new DatagramSocket(port);

		// this.localAdress = new PicAddress(InetAddress.getLocalHost(),
		// socket.getLocalPort());
		this.localAddress = new PicAddress(NetworkUtil.getIPAdress(), this.socket.getLocalPort());
		this.serverUser = new PicUser("SERVER", localAddress).setID((short) 0);

		this.listen();
		System.out.println("Server ready and listening");
	}

	/**
	 * Récupère les packets et les envoies pour se faire traiter
	 */
	private void listen() {
		this.receive = new Thread("Receive") {
			public void run() {
				while (running) {
					try {
						byte[] packetBuffer = new byte[PicConstants.PACKET_SIZE];
						DatagramPacket packet = new DatagramPacket(packetBuffer, packetBuffer.length);
						socket.receive(packet);
						parser.parsePacket(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		this.receive.start();
		System.out.println("Listening thread started");
	}

	/**
	 * Envoie un packet à un utilisateur
	 * 
	 * @param PicPacket packet à envoyer
	 * @param PicUser   utilisateur à qui envoyer le packet
	 */
	public void sendPacket(PicPacket pa, PicUser user) {
		try {
			pa.setSender(this.serverUser);
			
			if (PicConstants.debugMode) {				
				System.out.println("[-] Sent packet of type " + pa.getType());
			}
			
			byte[] packBytes = PacketUtil.getPacketAsBytes(pa);
			DatagramPacket packet = new DatagramPacket(packBytes, packBytes.length);
			packet.setSocketAddress(user.getAddress().toInetSocketAddress());
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Envoie un packet à tous les utilisateurs connectés au serveur
	 * 
	 * @param PicPacket packet à envoyer
	 */
	public void broadcastPacket(PicPacket pa) {
		for (PicUser u : this.gameManager.getUsers().values()) {
			sendPacket(pa, u);
		}
	}
	
	/**
	 * Envoie un packet à tous les utilisateurs connectés au serveur
	 * 
	 * @param PicPacket packet à envoyer
	 */
	public void broadcastPacketToGame(PicPacket pa, PicGame game) {
		for (PicUser u : game.getUsers()) {
			sendPacket(pa, u);
		}
	}
	
	

	public synchronized void start() {
		this.running = true;
	}

	public synchronized void stop() {
		this.running = false;
		this.socket.close();
	}

	public PicAddress getLocalAdress() {
		return localAddress;
	}

	public PicUser getServerUser() {
		return serverUser;
	}

	/**
	 * Donne un GameManager pour le serveur ainsi que le Parser pour traiter les
	 * packets reçus.
	 * 
	 * @param PicGameManager Classe qui gère l'assignement de parties pour les
	 *                       joueurs
	 */
	public void setManager(PicGameManager gameManager) {
		this.gameManager = gameManager;
		this.parser = new PicServerParser(this, gameManager);
		System.out.println("Parser ready !");

	}

}
