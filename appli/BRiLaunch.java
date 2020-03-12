package appli;

import bri.ServeurBRi;
import client.Bdd;

public class BRiLaunch {
	private final static int PORT_PROG = 3000;
	private final static int PORT_AMAT = 4000;

	public static void main(String[] args) {
		
		new Bdd();
		System.out.println("Bienvenue dans votre gestionnaire dynamique d'activité BRi");
		System.out.println("Port 3000 : programmeur");
		System.out.println("Port 4000 : amateur");
		new Thread(new ServeurBRi(PORT_PROG, bri.ServiceBRiProg.class)).start();
		new Thread(new ServeurBRi(PORT_AMAT, bri.ServiceBRiAmat.class)).start();
	}
}
