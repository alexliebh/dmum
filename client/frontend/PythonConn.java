package be.alexandreliebh.picacademy.client.frontend;

import java.awt.Color;

import be.alexandreliebh.picacademy.client.game.PicGameLoop;
import be.alexandreliebh.picacademy.data.ui.PicColor;

public class PythonConn {

	private boolean[] updaters;

	private PicGameLoop game;

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
	public static final int CLOSE = 11;

	public PythonConn(PicGameLoop gloop) {
		this.updaters = new boolean[12];
		for (int i = 0; i < updaters.length; i++) {
			updaters[i] = false;
		}
		this.game = gloop;
	}

	public void toUpdate(int... ids) {
		for (int id : ids) {
			this.updaters[id] = true;
		}
	}

	public void updated(int id) {
		this.updaters[id] = false;
	}

	public PicGameLoop getGame() {
		return game;
	}

	public boolean[] getUpdaters() {
		return updaters;
	}

	public int[][] getColors() {
		int[][] colors = new int[PicColor.values().length][3];
		for (PicColor c : PicColor.values()) {
			Color color = c.getColor();
			int[] rgb = new int[3];
			rgb[0] = color.getRed();
			rgb[1] = color.getGreen();
			rgb[2] = color.getBlue();
			colors[c.getId()] = rgb;
		}
		return colors;
	}

}
