package be.alexandreliebh.picacademy.server;

import java.io.IOException;
import java.util.List;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.util.LoadingUtil;
import be.alexandreliebh.picacademy.server.game.PicGameManager;
import be.alexandreliebh.picacademy.server.net.PicNetServer;

/**
 * Point d'entrée du programme du Serveur Met en place le Socket pour recevoir
 * les requetes des clients Crée les parties pour acceuillir les joueurs Charge
 * les mots d'une liste hébergée sur le serveur
 * 
 * @author Alexandre Liebhaberg
 * 
 */
public class PicAcademyServer {

	private PicNetServer server;
	private PicGameManager gameManager;

	private static PicAcademyServer INSTANCE;

	private List<String> words;

//	private boolean running;

	private PicAcademyServer(String[] args) throws IOException {
		INSTANCE = this;
//		this.running = true;

		int port = Integer.parseInt(args[0]);

		System.out.println(PicConstants.SERVER_CONSOLE_ART + "Server started on port " + port);

		// Lance la gérance du networking et ouvre un socket sur le port
		this.server = new PicNetServer(port);
		this.server.start();

		this.setupGameManager();

		this.loadWords();
		this.setupDebugging();

	}

	private void setupGameManager() {
		// Lance la gérance des parties et des joueurs
		this.gameManager = new PicGameManager();
		this.server.setManager(this.gameManager);
	}

	private void setupDebugging() {
		if (PicConstants.debugMode) {
			System.out.println("Debug mode : ON");
		} else {
			System.out.println("Debug mode : OFF");
		}

	}

	private void loadWords() {
		// Charge les mots à partir du fichier words.csv
		this.words = LoadingUtil.loadWords("words");
	}

	public static void main(String[] args) {
		try {
			new PicAcademyServer(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PicNetServer getNetServer() {
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

}
