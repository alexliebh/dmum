package be.alexandreliebh.picacademy.data.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class PicAddress {

	private final InetAddress adress;
	private final int port;

	public PicAddress(InetAddress adress, int port) {
		this.adress = adress;
		this.port = port;
	}

	public PicAddress(InetSocketAddress adress) {
		this.adress = adress.getAddress();
		this.port = adress.getPort();
	}

	public InetSocketAddress toInetSocketAddress() {
		return new InetSocketAddress(this.adress.getCanonicalHostName(), port);
	}

	public InetAddress getAdress() {
		return adress;
	}

	public int getPort() {
		return port;
	}
	
	@Override
	public String toString() {
		return adress.getCanonicalHostName()+":"+port;
	}

}
