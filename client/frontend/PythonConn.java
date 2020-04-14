package be.alexandreliebh.picacademy.client.frontend;

public class PythonConn {

	private boolean[] updates;
	
	public static final int GAME_ID = 0;
	public static final int ROUND_ID = 1;
	public static final int WORDS = 2;
	public static final int WORD = 3;
	public static final int TIMER = 4;
	public static final int STATE = 5;
	public static final int BOARD = 6;
	public static final int USERS = 7;
	public static final int MAIN_USER = 8;
	public static final int ROUND_END = 9;
	public static final int MESSAGES = 10;
	
	public PythonConn() {
		this.updates = new boolean[11];
		for (int i = 0; i < updates.length; i++) {
			updates[i] = false;
		}
	}
	
	public void toUpdate(int id) {
		this.updates[id] = true;
	}
	
	public void updated(int id) {
		this.updates[id] = false;
	}
	
}
