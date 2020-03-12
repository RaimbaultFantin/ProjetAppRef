package client;

public class Message {
	private Client emetteur;
	private String contenu;
	
	public Message(Client emetteur, String contenu) {
		this.emetteur = emetteur;
		this.contenu = contenu;
	}
}
