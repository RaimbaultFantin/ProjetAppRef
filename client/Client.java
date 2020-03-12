package client;

import java.util.Vector;

public class Client {
	private String mdp;
	private String url;
	private Vector<String> messages;

	public Client(String mdp) {
		super();
		this.mdp = mdp;
		this.url = null;
		this.messages = new Vector<String>();
	}

	@Override
	public String toString() {
		return mdp;
	}

	public void setUrl(String url) {
		this.url = "ftp://" + url.replaceAll("\\s+", "") + "/";
	}

	public String getUrl() {
		return url;
	}

	public String getMdp() {
		return mdp;
	}

	public boolean hasUrl() {
		return url != null;
	}

	public void addMessage(String message) {
		this.messages.add(message);
	}

	public void sendMessage(Client receveur, String message) {
		receveur.addMessage(message);
	}

	public String getMessages() {
		String str = "";
		for (String msg : messages) {
			synchronized (messages) {
				str += " [ " + msg + " ] ";
			}
		}
		return str;
	}

}
