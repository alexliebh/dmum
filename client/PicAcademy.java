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
 * TODO : appelle le programme python
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
		
		new Thread() {
			public void run() {
				Scanner sc = new Scanner(System.in);
				while (true) {
					String str = sc.nextLine();
					if (str.equalsIgnoreCase("q")) {
						System.exit(0);
					}
					sc.close();
				}
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
				System.out.println("Bye");
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

}
