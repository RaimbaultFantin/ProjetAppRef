package bri;

import java.io.*;
import java.net.*;

import client.Bdd;
import client.Client;
import exception.AlreadyInException;
import exception.BriException;
import exception.WrongPassword;

class ServiceBRiProg implements Service {

	private final String PRESS_ENTER = ", appuyez sur ENTRÉE";

	private Socket client;
	private String identifiant;

	ServiceBRiProg(Socket socket) {
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

			if (!currentClient.hasUrl()) {
				out.println("Entrez l'url de votre serveur ftp");
				String url = in.readLine();
				currentClient.setUrl(url);
			}

			String feedback = null;
			while (true) {
				out.println("Ajouter un Service : 1, " + "Mettre a jour un Service : 2, "
						+ "Changez l'url de votre serveur ftp : 3, " + "##Tapez le numéro de service désiré :");

				int choix = Integer.parseInt(in.readLine());
				switch (choix) {
				case 1:
					out.println(ServiceRegistry.toStringue() + "Entrez le nom de votre classe pour l'ajouter");
					feedback = in.readLine();
					feedback = addService(feedback, currentClient);
					out.println(feedback + PRESS_ENTER);
					in.readLine();
					break;
				case 2:
					out.println(ServiceRegistry.toStringue() + "Entrez le numero de service a mettre a jour");
					choix = Integer.parseInt(in.readLine());
					feedback = updateService(choix, currentClient);
					out.println(feedback + PRESS_ENTER);
					in.readLine();
					break;
				case 3:
					out.println("Entrez votre nouvelle url");
					feedback = in.readLine();
					currentClient.setUrl(feedback);
					out.println("Votre Url a bien été modifié" + PRESS_ENTER);
					in.readLine();
					break;
				default:
					break;
				}
			}

		} catch (IOException | SecurityException e) {
			// Fin du service
		}

		try {
			client.close();
		} catch (IOException e2) {
		}
	}

	@SuppressWarnings("finally")
	private String addService(String className, Client clt) {
		String msg = null;
		try {
			Class<? extends Service> classe = loadClassByNameAndUser(className, clt);
			ServiceRegistry.addService(classe);
			msg = "Le service a été ajouté avec succès !";
		} catch (NoClassDefFoundError | MalformedURLException | ClassNotFoundException | BriException
				| AlreadyInException e) {
			e.printStackTrace();
			msg = e.toString();
		} finally {
			return msg;
		}

	}

	@SuppressWarnings("finally")
	private String updateService(int indexService, Client clt) {
		String msg = null;
		try {
			Class<? extends Service> oldClasse = ServiceRegistry.getServiceClass(indexService);
			Class<? extends Service> newClasse = loadClassByNameAndUser(oldClasse.getSimpleName(), clt);
			ServiceRegistry.updateService(indexService, newClasse);
			msg = "Le service a été modifié avec succès";
		} catch (NoClassDefFoundError | MalformedURLException | ClassNotFoundException | BriException e) {
			e.printStackTrace();
			msg = e.toString();
		} finally {
			return msg;
		}
	}

//	private Class<? extends Service> loadClassByNameAndUser(String className, Client clt)
//			throws MalformedURLException, ClassNotFoundException {
//		URL[] classLoaderUrls;
//		classLoaderUrls = new URL[] { new URL(clt.getUrl()) };
//		URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls);
//		Class classe = (Class<? extends Service>) urlClassLoader.loadClass(identifiant + "." + className);
//		return classe;
//	}

	private Class<? extends Service> loadClassByNameAndUser(String className, Client clt)
			throws MalformedURLException, ClassNotFoundException {
		URLClassLoader tmp = new URLClassLoader(new URL[] { new URL(clt.getUrl()) }) {
			@Override
			public Class<?> loadClass(String name) throws ClassNotFoundException {
				if ((identifiant + "." + className).equals(name))
					return findClass(name);
				return super.loadClass(name);
			}
		};

		return (Class<? extends Service>) tmp.loadClass(identifiant + "." + className);
	}

	protected void finalize() throws Throwable {
		client.close();
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();
	}

}
