package be.alexandreliebh.picacademy.data.ui;

public class PicMessage {

	private final short senderID;
	private String username;

	private final String content;

	private int score;
	private boolean successful;
	
	public PicMessage(short senderID, String content) {
		this.senderID = senderID;
		this.content = content;
		this.username = "";
		this.score = -1;
		this.successful = false;
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
		return getUsername()+"µ"+getContent()+"µ"+getScore();
	}

	public int getScore() {
		return score;
	}
	
	public boolean isSuccessful() {
		return successful;
	}
	
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
