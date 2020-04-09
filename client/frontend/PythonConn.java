package be.alexandreliebh.picacademy.client.frontend;

import java.util.ArrayList;

public class PythonConn {

	private ArrayList<String> users;
	
	public PythonConn() {
		users = new ArrayList<>();
		users.add("Alex");
		users.add("Wladimir");
		users.add("Alfred");
		users.add("Francois");
	}
	
	public ArrayList<String> getUsers() {
		return users;
	}
	
}
