package client;

import java.util.HashMap;

import exception.WrongPassword;

public class Bdd {
	private static HashMap<String, Client> clients;

	public Bdd() {
		clients = new HashMap<String, Client>();
	}

	public static Client Login(String login, String mdp) throws WrongPassword {
		// Mauvais mdp
		if ((clients.get(login) != null) && (!clients.get(login).getMdp().equals(mdp))) {
			System.out.println(login + " // ["+ mdp + "]");
			System.out.println(clients.get(login));
			throw new WrongPassword("Mauvais mot de passe !");
		}
		// creation de compte
		else if (clients.get(login) == null) {
			Client newClient = new Client(mdp);
			clients.put(login, newClient);
			return newClient;
		} else {
			return clients.get(login);
		}
	}
}
