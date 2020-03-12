package bri;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

public class ServeurBRi implements Runnable {
	private ServerSocket listen_socket;
	private Class cls;

	public ServeurBRi(int port, Class<? extends Service> cls) {
		try {
			listen_socket = new ServerSocket(port);
			this.cls = cls;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void run() {
		try {
			while (true)
				((Service) cls.getConstructor(Socket.class).newInstance(listen_socket.accept())).run();
		} catch (IOException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			try {
				this.listen_socket.close();
			} catch (IOException e1) {
			}
			System.err.println("Pb sur le port d'écoute :" + e);
		}
	}

	protected void finalize() throws Throwable {
		try {
			this.listen_socket.close();
		} catch (IOException e1) {
		}
	}

	public void lancer() {
		(new Thread(this)).start();
	}
}
