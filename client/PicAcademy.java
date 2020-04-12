package be.alexandreliebh.picacademy.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import be.alexandreliebh.picacademy.client.frontend.PicUpdateType;
import be.alexandreliebh.picacademy.client.game.PicGameLoop;
import be.alexandreliebh.picacademy.client.net.PicNetClient;
import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.PacketUtil.DisconnectionReason;
import be.alexandreliebh.picacademy.data.net.PicAddress;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicDisconnectionPacket;

/**
 * Point d'entrée du programme client Crée la connexion au serveur
 * 
 * @author Alexandre Liebhaberg
 *
 */
public class PicAcademy {

	private static PicAcademy INSTANCE;

	private String username;

	private final PicAddress SERVER_ADDRESS = new PicAddress(InetAddress.getByName("46.105.251.41"), 9999);
	// private final PicAddress SERVER_ADDRESS = new
	// PicAddress(InetAddress.getByName("localhost"), 9999);

	private PicNetClient netClient;

	private PicGameLoop gLoop;

	private PicAcademy(String[] args) throws UnknownHostException {
		System.out.println(PicConstants.CLIENT_CONSOLE_ART + "Client started");

		INSTANCE = this;
		this.gLoop = new PicGameLoop();

		this.setupArgumentsHandling(args);
		this.setupNetClient();
		this.setupDebugging();
		// this.setupFrontendConnection();
		this.setupCommands();
		this.setupShutdownHook();

	}

	private void setupNetClient() {
		this.netClient = new PicNetClient();
		this.netClient.connect(SERVER_ADDRESS);
		this.netClient.start();

	}

	private void setupCommands() {
		new Thread("Commands") {
			public void run() {
				Scanner sc = new Scanner(System.in);
				boolean running = true;
				while (running) {
					String str = sc.nextLine();
					if (str.equalsIgnoreCase("q")) {
						System.exit(0);
					} else if (str.equalsIgnoreCase("u")) {
						List<PicUser> users = new ArrayList<>();
						users.add(new PicUser("bitch", null));
						gLoop.setUsers(users);
						gLoop.updateFrontEnd(PicUpdateType.PLAYERS);
					} else if (str.startsWith("ch")) {
						if (gLoop.isMainUser()) {
							String msg = str.substring(2).trim();
							msg = gLoop.getWords().get(Integer.valueOf(msg)-1);
							gLoop.chooseWord(msg);
						}
					} else if (str.startsWith("c")) {
						String msg = str.substring(2);
						gLoop.sendMessage(msg);
					}
				}
				sc.close();
			};
		}.start();

	}

	private void setupDebugging() {
		if (PicConstants.debugMode) {
			System.out.println("Debug mode : ON");
		} else {
			System.out.println("Debug mode : OFF");
		}

	}

	private void setupArgumentsHandling(String[] args) {
		if (args.length > 1) {
			this.username = args[0];
		} else {
			this.username = "Bob";
		}
	}

	/**
	 * Exécuté quand le programme se ferme
	 */
	private void setupShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				netClient.sendPacket(new PicDisconnectionPacket(netClient.getUserObject(), DisconnectionReason.LEFT));
				System.out.println("Disconnected from the server");
			}
		});
	}

//	private void setupFrontendConnection() {
//		front = new PythonConn();
//		this.gLoop.setFrontEnd(front);
//		this.gateway = new GatewayServer(front);
//		this.gateway.start();
//		System.out.println("Python connection : launched");
//
//	}

	public static void main(String[] args) throws UnknownHostException {
		new PicAcademy(args);
	}

	public PicGameLoop getGameLoop() {
		return gLoop;
	}

	public static PicAcademy getInstance() {
		return INSTANCE;
	}

	public PicNetClient getNetClient() {
		return netClient;
	}

	public String getUsername() {
		return username;
	}

}
