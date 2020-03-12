package bri;

import java.io.*;
import java.net.*;

import client.Bdd;
import client.Client;
import exception.AlreadyInException;
import exception.BriException;
import exception.WrongPassword;

public class ServiceBRiProg extends AbstractService {

	public ServiceBRiProg(Socket socket) {
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
				out.println("Entrez l'url de votre serveur ftp, exemple : localhost:3030");
				String url = in.readLine();
				currentClient.setUrl(url.trim());
			}

			String feedback = null;
			while (true) {
				out.println("Ajouter un Service : 1, " + "Mettre a jour un Service : 2, "
						+ "Changez l'url de votre serveur ftp : 3, " + "Ajouter un service depuis une lib Jar : 4"
						+ " ##Tapez le numéro de service désiré :");
				System.out.println(currentClient.getUrl());
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
					feedback = in.readLine();
					feedback = updateService(feedback, currentClient);
					out.println(feedback + PRESS_ENTER);
					in.readLine();
					break;
				case 3:
					out.println("Entrez votre nouvelle url, exemple : localhost:2525");
					feedback = in.readLine();
					currentClient.setUrl(feedback.trim());
					out.println("Votre Url a bien été modifié" + PRESS_ENTER);
					in.readLine();
					break;
				case 4:
					out.println("Entrez le nom du fichier Jar");
					String jarName = in.readLine();
					out.println("Entrez le nom de votre classe");
					String className = in.readLine();
					feedback = addServiceByJar(className, jarName, currentClient);
					out.println(feedback + PRESS_ENTER);
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
		Class<? extends Service> classe;
		try {
			classe = loadClassByNameAndUser(className, clt);
			ServiceRegistry.addService(classe);
			msg = "La classe a été ajouté avec succès !";
		} catch (MalformedURLException e) {
			e.printStackTrace();
			msg = "Veuillez vérifier le nom de votre Url";
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			msg = "La classe que vous essayez d'ajouter est introuvable, "
					+ "vérifier le nom de votre package ou votre URL ftp";
		} catch (BriException | AlreadyInException e) {
			e.printStackTrace();
			msg = e.getMessage();
		} finally {
			return msg;
		}
	}

	@SuppressWarnings("finally")
	private String addServiceByJar(String className, String jarName, Client clt) {
		String msg = null;
		Class<? extends Service> classe;
		try {
			classe = loadClassInJar(className, jarName, clt);
			ServiceRegistry.addService(classe);
			msg = "La classe a été ajouté avec succès !";
		} catch (MalformedURLException e) {
			e.printStackTrace();
			msg = "Veuillez vérifier le nom de votre Url";
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			msg = "La classe que vous essayez d'ajouter est introuvable, "
					+ "vérifier le nom de votre package ou votre URL ftp";
		} catch (BriException | AlreadyInException e) {
			e.printStackTrace();
			msg = e.getMessage();
		} finally {
			return msg;
		}

	}

	@SuppressWarnings("finally")
	private String updateService(String indexAndOrFileNameJar, Client clt) {
		String msg = null;
		String[] tab = indexAndOrFileNameJar.split(" ");
		Class<? extends Service> oldClasse = ServiceRegistry.getServiceClass(Integer.parseInt(tab[0]));
		Class<? extends Service> newClasse;

		try {
			if (tab.length < 2)
				newClasse = loadClassByNameAndUser(oldClasse.getSimpleName(), clt);
			else
				newClasse = loadClassInJar(oldClasse.getSimpleName(), tab[1], clt);
			ServiceRegistry.updateService(Integer.parseInt(tab[0]), newClasse);
			msg = "Le service a été modifié avec succès";
		} catch (MalformedURLException e) {
			e.printStackTrace();
			msg = "Veuillez vérifier le nom de votre Url";
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			msg = "La classe que vous essayez d'ajouter est introuvable, "
					+ "vérifier le nom de votre package ou votre URL ftp";
		} catch (BriException e) {
			e.printStackTrace();
			msg = e.getMessage();
		} finally {
			return msg;
		}

	}

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

//	private Class<? extends Service> loadClassInJar(String className, String jarName, Client clt)
//			throws ClassNotFoundException, MalformedURLException {
//		URL urlJarFile = new URL(clt.getUrl() + identifiant + "/" + jarName + ".jar");
//		URLClassLoader loader = new URLClassLoader(new URL[] { urlJarFile });
//		return (Class<? extends Service>) Class.forName(identifiant + "." + className, true, loader);
//	}
	
	private Class<? extends Service> loadClassInJar(String className, String jarName, Client clt)
			throws ClassNotFoundException, MalformedURLException {
		URLClassLoader tmp = new URLClassLoader(new URL[] { new URL(clt.getUrl() + identifiant + "/" + jarName + ".jar") }) {
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
