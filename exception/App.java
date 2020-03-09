package exception;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import bri.Service;
import client.Client;

public class App {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		m4();
	}
	
	public static void m1() throws AlreadyInException {
		throw new AlreadyInException("tout au fond");
	}
	
	public static void m2() throws AlreadyInException {
		m1();
	}
	
	public static void m3() throws AlreadyInException {
		m2();
	}
	
	public static void m4() {
		try {
			Class<? extends Service> classe = loadClassByNameAndUser("serviceFaux");
		} catch (MalformedURLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			System.out.println("aie");
		}
	}
	
	private static Class<? extends Service> loadClassByNameAndUser(String className)
			throws MalformedURLException, ClassNotFoundException {
		URL[] classLoaderUrls;
		classLoaderUrls = new URL[] { new URL("ftp://localhost:2121/") };
		URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls);
		Class classe = (Class<? extends Service>) urlClassLoader.loadClass("login." + className);
		return classe;
	}

}
