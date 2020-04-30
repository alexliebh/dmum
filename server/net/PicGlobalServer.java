package be.alexandreliebh.picacademy.server.net;

import java.io.IOException;
import java.net.Socket;

import be.alexandreliebh.picacademy.data.net.PicSocketedUser;
import be.alexandreliebh.picacademy.data.net.packet.auth.PicConnectionInitPacket;
import be.alexandreliebh.picacademy.server.PicAcademyServer;

public class PicGlobalServer extends PicNetServer {

	public PicGlobalServer(int port) throws IOException {
		super(port, "Global Server");
		System.out.println("Global server on port " + port + " ready");
	}

	@Override
	protected void accept() {
		try {
			Socket s = socket.accept();
			PicConnectionInitPacket cInit = new PicConnectionInitPacket(s);
			PicSocketedUser user = new PicSocketedUser("", s);
			user = PicAcademyServer.getInstance().getGameManager().identifyUser(user);
			sendPacket(cInit, user);
			s.close();
		} catch (IOException e) {
		}
	}

	@Override
	protected void receivePacket() {
		
	}

}
