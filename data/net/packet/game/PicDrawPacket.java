package be.alexandreliebh.picacademy.data.net.packet.game;

import java.awt.Point;

import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;
import be.alexandreliebh.picacademy.data.ui.PicColor;

public class PicDrawPacket extends PicAbstractGamePacket {

	private Point[] locations;
	private PicColor color;

	public PicDrawPacket(byte gID, PicColor color, Point... locations) {
		super(PicPacketType.DRAW, gID);
		this.locations = locations;
		this.color = color;
	}

	public Point[] getLocations() {
		return locations;
	}

	public PicColor getColor() {
		return color;
	}

}
