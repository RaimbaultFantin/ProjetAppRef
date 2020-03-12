package bri;

import java.net.Socket;

public abstract class AbstractService implements Service{
	protected final String PRESS_ENTER = ", appuyez sur ENTRÉE";
	protected Socket client;
	protected String identifiant;
}
