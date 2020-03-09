package bri;


import java.io.*;
import java.net.*;


public class ServeurBRi implements Runnable {
	private ServerSocket listen_socket;
	
	// Cree un serveur TCP - objet de la classe ServerSocket
	public ServeurBRi(int port) {
		try {
			listen_socket = new ServerSocket(port);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void run() {
		try {
			while(true) 
				new Thread(ServiceRegistry.newService(listen_socket)).start();
		}
		catch (IOException e) { 
			try {this.listen_socket.close();} catch (IOException e1) {}
			System.err.println("Pb sur le port d'écoute :"+e);
		}
	}

	protected void finalize() throws Throwable {
		try {this.listen_socket.close();} catch (IOException e1) {}
	}

	public void lancer() {
		(new Thread(this)).start();		
	}
}
