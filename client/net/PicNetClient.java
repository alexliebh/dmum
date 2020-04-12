package be.alexandreliebh.picacademy.client.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import be.alexandreliebh.picacademy.client.PicAcademy;
import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.game.PicUser;
import be.alexandreliebh.picacademy.data.net.PacketUtil;
import be.alexandreliebh.picacademy.data.net.PicAddress;
import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicConnectionPacket;
import be.alexandreliebh.picacademy.data.util.NetworkUtil;

public class PicNetClient {

	private DatagramSocket localSocket;
	private PicAddress localAddress;

	private PicAddress serverAddress;

	private Thread receiveThread;
	private boolean running;

	private PicClientParser pParser;

	private PicUser userObject;

	public PicNetClient() {
		try {
			this.localSocket = new DatagramSocket();
			this.localAddress = new PicAddress(NetworkUtil.getIPAdress(), this.localSocket.getLocalPort());
		} catch (IOException e) {
			System.err.println("You're not connected to the Internet");
//			e.printStackTrace();
		}

		this.userObject = new PicUser(PicAcademy.getInstance().getUsername(), this.localAddress);
 
		this.pParser = new PicClientParser(this);

	}

	public boolean connect(PicAddress address) {
		this.serverAddress = address;
		try {
			this.localSocket.connect(address.toInetSocketAddress());
			this.sendPacket(new PicConnectionPacket(this.userObject));

			this.listen();
			return true;
		} catch (SocketException e) {

			e.printStackTrace();

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
			DatagramPacket packet = new DatagramPacket(packBytes, packBytes.length);
			this.localSocket.send(packet);
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
						byte[] packetBuffer = new byte[PicConstants.PACKET_SIZE];
						DatagramPacket packet = new DatagramPacket(packetBuffer, packetBuffer.length);
						localSocket.receive(packet);
						pParser.parsePacket(packet);
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

	//TODO Appeler stop()
	public synchronized void stop() { // NO_UCD (unused code)
		this.running = false;
		this.localSocket.close();
	}

	public void setLocalAddress(PicAddress localAdress) {
		this.localAddress = localAdress;
		this.userObject.setAddress(localAdress);
	}

	public PicAddress getLocalAddress() {
		return localAddress;
	}

	public PicAddress getServerAddress() {
		return serverAddress;
	}

	public PicUser getUserObject() {
		return userObject;
	}

	public void setUserObject(PicUser userObject) {
		this.userObject = userObject;
	}

}
