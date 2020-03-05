package be.alexandreliebh.picacademy.data.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public enum PicColor {
	//Inspired by https://sashat.me/2017/01/11/list-of-20-simple-distinct-colors/

	RED((byte) 0, new Color(0xe6194B)),
	GREEN((byte) 1, new Color(0x3cb44b)),
	YELLOW((byte) 2, new Color(0xffe119)),
	BLUE((byte) 3, new Color(0x4363d8)),
	ORANGE((byte) 4, new Color(0xf58231)),
	PURPLE((byte) 5, new Color(0x911eb4)),
	CYAN((byte) 6, new Color(0x42d4f4)),
	MAGENTA((byte) 7, new Color(0xf032e6)),
	LIME((byte) 8, new Color(0xbfef45)),
	PINK((byte) 9, new Color(0xfabebe)),
	TEAL((byte) 10, new Color(0x469990)),
	LAVENDER((byte) 11, new Color(0xe6beff)),
	BROWN((byte) 12, new Color(0x9A6324)),
	BEIGE((byte) 13, new Color(0xfffac8)),
	MARROON((byte) 13, new Color(0x800000)),
	MINT((byte) 14, new Color(0xaaffc3)),
	OLIVE((byte) 15, new Color(0x808000)),
	APRICOT((byte) 16, new Color(0xffd8b1)),
	NAVY((byte) 17, new Color(0x000075)),
	GREY((byte) 18, new Color(0xa9a9a9)),
	WHITE((byte) 19, new Color(0xffffff)),
	BLACK((byte) 21, new Color(0x000000));

	private final byte id;
	private final List<Integer> color;
	
	private PicColor(byte id, Color color) {
		this.id = id;
		this.color = new ArrayList<Integer>(3);
		this.color.add(color.getRed());
		this.color.add(color.getGreen());
		this.color.add(color.getBlue());
	}
	
	public byte getId() {
		return id;
	}
	
	public Color getColor() {
		return new Color(color.get(0), color.get(1), color.get(2));
	}
	
}
