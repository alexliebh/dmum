package be.alexandreliebh.picacademy.data.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

public class NetworkUtil {

	/**
	 * Récupère l'adresse IP de l'ordinateur en testant 3 sites au cas où certains sont downs.
	 * @return l'adresse IP de l'ordi
	 * @throws IOException si problème lors de la connexion au serveurs d'IP
	 */
	public static final InetAddress getIPAdress() throws IOException {
		try {
			String aws = getIPFromWebsite("http://checkip.amazonaws.com");
			System.out.println("Got IP from AWS ("+aws+")");
			return InetAddress.getByName(aws);
		} catch (Exception e) {
			try {
				String ext = getIPFromWebsite("https://myexternalip.com/raw");
				System.out.println("Got IP from ExternalIP");
				return InetAddress.getByName(ext);
			} catch (Exception e1) {
				String echo = getIPFromWebsite("https://ipecho.net/plain");
				System.out.println("Got IP from IpEcho");
				return InetAddress.getByName(echo);
			}
			
		}
	}

	/**
	 * Fait une requete à un serveur d'IP
	 * 
	 * @param url l'adresse du site
	 * @return l'adresse IP de l'ordi
	 * @throws IOException si problème de connexion au serveur d'IP spécifié
	 */
	private static final String getIPFromWebsite(String url) throws IOException {
		URL whatismyip = new URL(url);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine();
			return ip;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
