package be.alexandreliebh.picacademy.client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import be.alexandreliebh.picacademy.client.PicAcademy;
import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.PacketUtil;
import be.alexandreliebh.picacademy.data.net.PicAddress;
import be.alexandreliebh.picacademy.data.net.PicSocketedUser;
import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicConnectionPacket;
import be.alexandreliebh.picacademy.data.util.TimedScheduler;

public class PicNetClient {

	private Socket localSocket;
	private PicAddress localAddress;
	private PrintStream socketOut;
	private BufferedReader socketIn;

	private Thread receiveThread;
	private boolean running;

	private final PicClientParser pParser;
	private final TimedScheduler scheduler;

	private PicSocketedUser userObject;

	public PicNetClient() {
		try {
			this.localSocket = new Socket();
		} catch (Exception e) {
			System.err.println("You're not connected to the Internet");
			System.exit(-1);
		}

		this.userObject = new PicSocketedUser(PicAcademy.getInstance().getUsername());
		try {
			this.userObject.setSocket(localSocket);
		} catch (IOException e) {
		}

		this.pParser = new PicClientParser(this);

		this.scheduler = new TimedScheduler(3);
	}

	public boolean connect(PicAddress address) {
		try {
			System.out.println("Trying to connect to the server");
			this.localSocket.connect(address.toInetSocketAddress());
			this.socketOut = new PrintStream(this.localSocket.getOutputStream());
			this.socketIn = new BufferedReader(new InputStreamReader(this.localSocket.getInputStream()));

			this.sendPacket(new PicConnectionPacket(this.userObject, false));
			this.scheduler.start((new Runnable() {
				public void run() {
					if (!PicAcademy.getInstance().getGameLoop().isConnected()) {
						System.err.println("Couldn't connect to the server.");
						System.exit(404);
					}
				}
			}));
			this.listen();
			return true;
		} catch (IOException e) {
			System.err.println("Error while connecting to the server");
			System.exit(-1);
			return false;
		}
	}

	public boolean sendPacket(PicAbstractPacket pa) {
		try {
			if (PicConstants.DEBUG_MODE) {
				System.out.println("[-] Sent packet of type " + pa.getType());
			}
			pa.setSender(this.userObject);
			byte[] packBytes = PacketUtil.getPacketAsBytes(pa);
			this.socketOut.println(packBytes);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void listen() {
		this.receiveThread = new Thread("Receive") {
			public void run() {
				while (running) {
					try {
						String inPacket = socketIn.readLine();
						pParser.parsePacket(inPacket);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		};

		this.receiveThread.start();
	}

	public synchronized void start() {
		this.running = true;
	}

	public synchronized void stop() throws IOException {
		this.running = false;
		this.localSocket.close();
	}

	public PicUser getUserObject() {
		return userObject;
	}

	public PicAddress getLocalAddress() {
		return localAddress;
	}

	public void setUserObject(PicSocketedUser userObject) {
		this.userObject = userObject;
		this.localSocket = userObject.getSocket();
		this.localAddress = userObject.getAddress();
	}

}
