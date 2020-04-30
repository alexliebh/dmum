package be.alexandreliebh.picacademy.data.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import be.alexandreliebh.picacademy.data.game.PicUser;

public class PicSocketedUser extends PicUser {

	private Socket socket;
	
	private PrintStream outStream;
	private BufferedReader inStream;

	public PicSocketedUser(String username) {
		super(username);
	}
	
	public PicSocketedUser(String username, Socket socket) throws IOException {
		super(username);
		setSocket(socket);
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) throws IOException {
		this.socket = socket;
		this.outStream = new PrintStream(this.socket.getOutputStream());
		this.inStream = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		setAddress(new PicAddress(socket.getLocalAddress(), socket.getLocalPort()));
	}
	
	public BufferedReader getInStream() {
		return inStream;
	}
	
	public PrintStream getOutStream() {
		return outStream;
	}

	public PicSocketedUser setUserName(String username) {
		this.username = username;
		return this;
	}
	
}
