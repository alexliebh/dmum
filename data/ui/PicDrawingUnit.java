package be.alexandreliebh.picacademy.data.ui;

public class PicDrawingUnit implements Comparable<PicDrawingUnit> {
	private int x, y;
	private byte colorID;

	public PicDrawingUnit(int x, int y, byte colorID) {
		this.x = x;
		this.y = y;
		this.colorID = colorID;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public byte getColorID() {
		return colorID;
	}

	@Override
	public int compareTo(PicDrawingUnit o) {
		if (o.x + o.y >= x + y) {
			return +1;
		}
		return -1;
	}

}