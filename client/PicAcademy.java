package be.alexandreliebh.picacademy.client;

import java.awt.Point;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

import be.alexandreliebh.picacademy.client.game.PicGameLoop;
import be.alexandreliebh.picacademy.client.net.PicNetClient;
import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.PacketUtil.DisconnectionReason;
import be.alexandreliebh.picacademy.data.net.PicAddress;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicDisconnectionPacket;
import be.alexandreliebh.picacademy.data.ui.PicColor;

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
//	private final PicAddress SERVER_ADDRESS = new PicAddress(InetAddress.getByName("localhost"), 9999);

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
					try {
						if (str.equalsIgnoreCase("q")) {
							System.exit(0);
						} else if (str.startsWith("ch")) {
							if (gLoop.isMainUser()) {
								String msg = str.substring(2).trim();
								msg = gLoop.getWords().get(Integer.valueOf(msg) - 1);
								gLoop.chooseWord(msg);
							}
						} else if (str.startsWith("c")) {
							String msg = str.substring(2);
							gLoop.sendMessage(msg);
						} else if (str.equalsIgnoreCase("l")) {
							for (PicUser u : gLoop.getUsers()) {
								System.out.println(u.getIdentifier());
							}
						} else if (str.equalsIgnoreCase("d")) {
							System.out.println("drawing random units");
							Point[] points = new Point[40];
							Random rand = new Random();
							for (int i = 0; i < points.length; i++) {
								int x = rand.nextInt(PicConstants.GRID_SIZE_X / 8);
								int y = rand.nextInt(PicConstants.GRID_SIZE_Y / 8);
								points[i] = new Point(x, y);
							}
							gLoop.drawUnits(PicColor.BLUE, points);
						} else if (str.equalsIgnoreCase("b")) {
							System.out.println(PicAcademy.getInstance().getGameLoop().getBoard().toString());
						}
					} catch (Exception e) {
						System.err.println("Error, try again");
					}
				}
				sc.close();
			};
		}.start();

	}

	private void setupDebugging() {
		if (PicConstants.DEBUG_MODE) {
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
				System.out.println("Disconnected");
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
