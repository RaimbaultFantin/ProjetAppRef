package appli;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

import bri.ServeurBRi;
import bri.Service;
import bri.ServiceRegistry;
import client.Bdd;

public class BRiLaunch {
	private final static int PORT_PROG = 3000;
	private final static int PORT_AMAT = 4000;

	public static void main(String[] args) {
		
		new Bdd();
		System.out.println("Bienvenue dans votre gestionnaire dynamique d'activité BRi");
		System.out.println("Pour ajouter une activité, celle-ci doit être présente sur votre serveur ftp");
		System.out.println("A tout instant, en tapant le nom de la classe, vous pouvez l'intégrer");
		System.out.println("Les clients se connectent au serveur 3000 pour lancer une activité");

		new Thread(new ServeurBRi(PORT_PROG)).start();
		new Thread(new ServeurBRi(PORT_AMAT)).start();
	}
}
