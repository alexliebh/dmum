package be.alexandreliebh.picacademy.data.ui;

public class PicMessage {

	private short senderID;
	private String username;

	private String content;

	public PicMessage(short senderID, String content) {
		this.senderID = senderID;
		this.content = content;
		this.username = "";
	}

	public String getContent() {
		return content;
	}

	public short getSenderID() {
		return senderID;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String toString() {
		return getUsername()+"µ"+getContent();
	}

}
