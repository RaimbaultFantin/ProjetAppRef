package bri;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import javax.print.attribute.standard.Severity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import exception.AlreadyInException;
import exception.BriException;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partagée en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		servicesClasses = new Vector<Class<? extends Service>>();
	}
	private static Vector<Class<? extends Service>> servicesClasses;

	// renvoie la classe de service (numService -1)
	public static Class<? extends Service> getServiceClass(int numService) {
		return servicesClasses.get(numService - 1);
	}

	// ajoute une classe de service après contrôle de la norme BLTi
	public static void addService(Class<? extends Service> classe) throws BriException, AlreadyInException {
		if (contains(classe))
			throw new AlreadyInException("Vous ne pouvez pas ajouter deux fois le même service");
		if (normeBri(classe))
			servicesClasses.add(classe);
	}

	public static void updateService(int indexService, Class<? extends Service> classe) throws BriException {
		if (normeBri(classe))
			servicesClasses.set(indexService - 1, classe);
	}

	// liste les activités présentes
	public static StringBuilder toStringue() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < servicesClasses.size(); i++) {
			synchronized (servicesClasses) {
				str.append(servicesClasses.get(i).getSimpleName() + " [" + (i + 1) + "] | ");
			}
		}
		return str;
	}

	private static boolean contains(Class<?> service) {
		for (Class<? extends Service> class1 : servicesClasses) {
			if (class1.getName().equals(service.getName())) {
				System.out.println(class1.getName() + " et " + service.getName());
				return true;
			}
		}
		return false;
	}

	public static Service newService(ServerSocket client) throws IOException {
		switch (client.getLocalPort()) {
		case 3000:
			return new ServiceBRiProg(client.accept());
		case 4000:
			return new ServiceBRiAmat(client.accept());
		default:
			break;
		}
		return null;
	}

	// NORME BRI
	private static boolean normeBri(Class<?> service) throws BriException {
		return implementsServiceInterface(service) && isPublic(service) && isNotAbstract(service)
				&& hasPublicConstructorWithoutException(service) && hasSocketAttributeFinal(service)
				&& hasStaticToStringueWithoutException(service);

	}

	private static boolean implementsServiceInterface(Class<?> classe) throws BriException {
		for (Class<?> c : classe.getInterfaces()) {
			if (c.getSimpleName().equals("Service")) {
				return true;
			}
		}
		throw new BriException("La classe n'implémente pas l'interface Service");
	}

	private static boolean isPublic(Class<?> classe) throws BriException {
		if (Modifier.isPublic(classe.getClass().getModifiers())) {
			return true;
		} else {
			throw new BriException("La classe n'est pas public");
		}
	}

	private static boolean isNotAbstract(Class<?> classe) throws BriException {
		if (!Modifier.isAbstract(classe.getModifiers())) {
			return true;
		} else {
			throw new BriException("La classe ne doit pas être abstract");
		}
	}

	private static boolean hasPublicConstructorWithoutException(Class<?> classe) throws BriException {
		for (Constructor<?> c : classe.getConstructors()) {
			if (c.getExceptionTypes().length == 0 && Modifier.isPublic(c.getModifiers())) {
				return true;
			}
		}
		throw new BriException("Pas de public constructeur sans exception");
	}

	private static boolean hasSocketAttributeFinal(Class<?> classe) throws BriException {
		for (Field f : classe.getDeclaredFields()) {
			if (f.getType().isAssignableFrom(Socket.class)) {
				if (Modifier.isFinal(f.getModifiers())) {
					if (Modifier.isPrivate(f.getModifiers())) {
						return true;
					}
				}
			}
		}
		throw new BriException("La socket n'est pas conforme");
	}

	private static boolean hasStaticToStringueWithoutException(Class<?> classe) throws BriException {
		for (Method m : classe.getMethods()) {
			if (Modifier.isStatic(m.getModifiers()) && m.getName().equals("toStringue")
					&& m.getAnnotatedExceptionTypes().length == 0) {
				return true;
			}
		}
		throw new BriException("Votre classe ne contient pas de méthode toStringue sans exception");
	}

}
