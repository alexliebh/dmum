package be.alexandreliebh.picacademy.data.game;

import be.alexandreliebh.picacademy.data.net.PicAddress;

public class PicUser {

	public String username;
	private PicAddress address;

	private int score;
	
	private short ID;

	public PicUser(String username) {
		this.username = username;

		this.ID = -1;
		this.score = 0;
	}

	public String toString() {
		return username + " with ID:" + ID + " connected from " + this.address.toString();
	}

	/*
	 * Setters
	 */

	public PicUser setAddress(PicAddress address) {
		this.address = address;
		return this;
	}

	public PicUser setID(short iD) {
		ID = iD;
		return this;
	}

	/*
	 * Getters
	 */
	public short getID() {
		return ID;
	}

	public PicAddress getAddress() {
		return address;
	}

	public String getUsername() {
		return username;
	}
	
	public String getIdentifier() {
		return username+" ("+getID()+")";
	}
	
	public int getScore() {
		return score;
	}
	
	public void addToScore(int supplement) {
		this.score += supplement;
	}
	
}
