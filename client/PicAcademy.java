package be.alexandreliebh.picacademy.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import be.alexandreliebh.picacademy.client.game.PicGameLoop;
import be.alexandreliebh.picacademy.client.net.PicNetClient;
import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.net.PacketUtil.DisconnectionReason;
import be.alexandreliebh.picacademy.data.net.PicAddress;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicDisconnectionPacket;

/**
 * Point d'entrée du programme client
 * Crée la connexion au serveur
 * 
 * @author Alexandre Liebhaberg
 *
 */
public class PicAcademy {

	private static PicAcademy INSTANCE;

	/**
	 * Sera plus tard remplacé par une input du programme python
	 */
	public final String NAME = "Bob";
	
	private final PicAddress SERVER_ADDRESS = new PicAddress(InetAddress.getByName("46.105.251.41"), 9999);

	private final PicNetClient netClient;
	
	private PicGameLoop gLoop;

	private PicAcademy(String[] args) throws UnknownHostException {
		INSTANCE = this;
		this.gLoop = new PicGameLoop();
		
		System.out.println(PicConstants.CLIENT_CONSOLE_ART + "Client started");

		this.netClient = new PicNetClient();
		this.netClient.connect(SERVER_ADDRESS);
		this.netClient.start();

		this.setupShutdownHook();
		
		if (PicConstants.debugMode) {
			System.out.println("Debug mode : ON");
		} else {
			System.out.println("Debug mode : OFF");
		}
		
		new Thread("Commands") {
			public void run() {
				Scanner sc = new Scanner(System.in);
				boolean running = true;
				while (running) {
					String str = sc.nextLine();
					if (str.equalsIgnoreCase("q")) {
						System.exit(0);
					}
				}
				sc.close();
			};
		}.start();
	}

	/**
	 * Exécuté quand le programme se ferme
	 */
	private void setupShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				netClient.sendPacket(new PicDisconnectionPacket(netClient.getUserObject(), DisconnectionReason.LEFT));
				System.out.println("Disconnected from the server");
			}
		});
	}
	
	public PicGameLoop getGameLoop() {
		return gLoop;
	}

	public static void main(String[] args) throws UnknownHostException {
		new PicAcademy(args);
	}

	public static PicAcademy getInstance() {
		return INSTANCE;
	}
	
	public PicNetClient getNetClient() {
		return netClient;
	}

}
