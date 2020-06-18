package be.alexandreliebh.picacademy.data.ui;

import java.util.TreeSet;

public final class PicDrawingBoard {

	private final TreeSet<PicDrawingUnit> board = new TreeSet<PicDrawingUnit>();

	public PicDrawingBoard() {
		this.resetBoard();
	}

	public void setPixel(int x, int y, PicColor color) {
		this.board.add(new PicDrawingUnit(x, y, color.getId()));
	}
	
	public void setPixel(int x, int y, byte colorID) {
		this.board.add(new PicDrawingUnit(x, y, colorID));
	}

	public void resetBoard() {
		this.board.clear();
	}

	public TreeSet<PicDrawingUnit> getBoard() {
		return board;
	}

	public String toString() {
		PicColor[] colors = PicColor.values();
		StringBuilder builder = new StringBuilder();
		for (PicDrawingUnit unit : board) {
			builder.append("Coords " + unit.getX() + ", " + unit.getY() + " --> " + colors[unit.getColorID()] + "\n");
		}
		return builder.toString();
	}

	
}
