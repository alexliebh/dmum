package be.alexandreliebh.picacademy.data;

import be.alexandreliebh.picacademy.data.game.PicGame;

public class PicConstants {

	public static final int PACKET_SIZE = 512;
	
	public static final int MAX_PLAYERS_PER_GAME = 3;
	public static final int MAX_ONLINE_PLAYERS = 30;
	
	public static final int MAX_GAMES = MAX_ONLINE_PLAYERS/MAX_PLAYERS_PER_GAME;
	
	public static final int AMOUNT_OF_ROUNDS = 3;
	
	public static final int GRID_SIZE = 120;
	
	public static final PicGame NO_GAME = new PicGame((byte)-1);
	
	public static final boolean debugMode = false;
	
	
	public static final String SERVER_CONSOLE_ART = 
			"------------------------------------------------------------\r\n"+
		    "  _____ _                            _                      \r\n" + 
			" |  __ (_)        /\\                | |                     \r\n" + 
			" | |__) |  ___   /  \\   ___ __ _  __| | ___ _ __ ___  _   _ \r\n" + 
			" |  ___/ |/ __| / /\\ \\ / __/ _` |/ _` |/ _ \\ '_ ` _ \\| | | |\r\n" + 
			" | |   | | (__ / ____ \\ (_| (_| | (_| |  __/ | | | | | |_| |\r\n" + 
			" |_|   |_|\\___/_/__  \\_\\___\\__,_|\\__,_|\\___|_| |_| |_|\\__, |\r\n" + 
			"             / ____|                                   __/ |\r\n" + 
			"            | (___   ___ _ ____   _____ _ __          |___/ \r\n" + 
			"             \\___ \\ / _ \\ '__\\ \\ / / _ \\ '__|               \r\n" + 
			"             ____) |  __/ |   \\ V /  __/ |                  \r\n" + 
			"            |_____/ \\___|_|    \\_/ \\___|_|                  \r\n" + 
			"                                                            \r\n" + 
			"                                                            \r\n"+
			"------------------------------------------------------------\r\n";
	public static final String CLIENT_CONSOLE_ART = 
			"------------------------------------------------------------\r\n"+
			"  _____ _                            _                      \r\n" + 
			" |  __ (_)        /\\                | |                     \r\n" + 
			" | |__) |  ___   /  \\   ___ __ _  __| | ___ _ __ ___  _   _ \r\n" + 
			" |  ___/ |/ __| / /\\ \\ / __/ _` |/ _` |/ _ \\ '_ ` _ \\| | | |\r\n" + 
			" | |   | | (__ / ____ \\ (_| (_| | (_| |  __/ | | | | | |_| |\r\n" + 
			" |_|   |_|\\___/_/ ___\\_\\___\\__,_|\\__,_|\\___|_| |_| |_|\\__, |\r\n" + 
			"                 / ____| (_)          | |              __/ |\r\n" + 
			"                | |    | |_  ___ _ __ | |_            |___/ \r\n" + 
			"                | |    | | |/ _ \\ '_ \\| __|                 \r\n" + 
			"                | |____| | |  __/ | | | |_                  \r\n" + 
			"                 \\_____|_|_|\\___|_| |_|\\__|                 \r\n" + 
			"                                                            \r\n" + 
			"                                                            \r\n" + 
			"------------------------------------------------------------\r\n";

	
}
