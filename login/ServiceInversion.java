package login;

import java.net.Socket;
import java.io.*;
import bri.Service;

public class ServiceInversion implements Service {
	
	private static int cpt = 1;
	
	private final int numero;
	private final Socket client;
	
	public ServiceInversion(final Socket socket) {
		this.numero = cpt ++;
		this.client = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);
			out.println("Tapez un texte a inverser");
			String line = in.readLine();
			String invLine = new String (new StringBuffer(line).reverse());
			out.println(invLine);
		}
		catch (IOException e) {
		}
		//Fin du service d'inversion
	}
	
	protected void finalize() throws Throwable {
		 client.close(); 
	}

	public static String toStringue() {
		return "Inversion de texte";
	}
}
