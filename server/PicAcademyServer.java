package be.alexandreliebh.picacademy.server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.util.LoadingUtil;
import be.alexandreliebh.picacademy.data.util.NetworkUtil;
import be.alexandreliebh.picacademy.server.game.PicGameManager;
import be.alexandreliebh.picacademy.server.net.PicGlobalServer;

/**
 * Point d'entrée du programme du Serveur Met en place le Socket pour recevoir
 * les requetes des clients Crée les parties pour acceuillir les joueurs Charge
 * les mots d'une liste hébergée sur le serveur
 * 
 * @author Alexandre Liebhaberg
 * 
 */
public class PicAcademyServer {

	private final PicGlobalServer server;
	private PicGameManager gameManager;

	private static PicAcademyServer INSTANCE;

	private List<String> words;
	
	private InetAddress localIP;

	private PicAcademyServer(String[] args) throws IOException {
		INSTANCE = this;

		int port = Integer.parseInt(args[0]);

		System.out.println(PicConstants.SERVER_CONSOLE_ART + "Server started on port " + port);

		this.localIP = NetworkUtil.getIPAdress();
		
		// Lance la gérance du networking et ouvre un socket sur le port
		this.server = new PicGlobalServer(port);
		this.server.start();

		this.setupGameManager();

		this.loadWords();
		this.setupDebugging();

	}

	private void setupGameManager() {
		// Lance la gérance des parties et des joueurs
		this.gameManager = new PicGameManager();
		this.gameManager.initGames();
	}

	private void setupDebugging() {
		if (PicConstants.DEBUG_MODE) {
			System.out.println("Debug mode : ON");
		} else {
			System.out.println("Debug mode : OFF");
		}
		
		if (PicConstants.DISPLAY_JSON) {
			System.out.println("JSON mode : ON");
		} else {
			System.out.println("JSON mode : OFF");
		}

	}

	private void loadWords() {
		this.words = LoadingUtil.loadWords("words");
	}

	public static void main(String[] args) {
		try {
			new PicAcademyServer(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PicGlobalServer getGlobalServer() {
		return server;
	}

	public PicGameManager getGameManager() {
		return gameManager;
	}

	public static PicAcademyServer getInstance() {
		return INSTANCE;
	}

	public List<String> getWords() {
		return words;
	}

	public InetAddress getLocalIP() {
		return localIP;
	}
	
}
