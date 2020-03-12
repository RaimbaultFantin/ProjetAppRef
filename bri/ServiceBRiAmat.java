package bri;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

import client.Bdd;
import client.Client;
import exception.WrongPassword;

public class ServiceBRiAmat extends AbstractService {

	public ServiceBRiAmat(Socket socket) {
		client = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);

			Client currentClient = null;

			while (currentClient == null) {
				out.println("Entrez votre login");
				identifiant = in.readLine();
				out.println("Entrez votre mot de passe");
				String mdp = in.readLine();
				try {
					currentClient = Bdd.Login(identifiant, mdp);
				} catch (WrongPassword e) {
					out.println(e.getMessage() + PRESS_ENTER);
					in.readLine();
				}
			}
			while (true) {
				out.println(ServiceRegistry.toStringue() + " ##Tapez le numéro de service désiré :");
				int choix = Integer.parseInt(in.readLine());

				// instancier le service numéro "choix" en lui passant la socket "client"
				Constructor<? extends Service> constr = ServiceRegistry.getServiceClass(choix)
						.getConstructor(Socket.class);

				try {
					// si on met un new Thread on passe directement dans le client close et ça
					// retourne null
					// alors que en utilisant run() on reste dans la pile d'exec de ServiceBRI et on
					// ne passe pas dans le close()
					constr.newInstance(client).run();
					in.readLine();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException | NoSuchMethodException | SecurityException e) {
			// Fin du service
		}

		try {
			client.close();
		} catch (IOException e2) {
		}
	}

	protected void finalize() throws Throwable {
		client.close();
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();
	}

}
