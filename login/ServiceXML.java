package login;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bri.Service;
import mail.SendEmail;
public class ServiceXML implements Service {

	private final Socket client;

	public ServiceXML(final Socket socket) {
		this.client = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			out.println("Entrez le chemin de votre fichier xml");
			String chemin = in.readLine();
			out.println("Entrez votre adresse email");
			String email = "";
			String analyse = "";
			
			String server = "localhost";
	        int port = 2121;
	        String user = "user";
	        String pass = "pass";
	        FTPClient ftpClient = new FTPClient();
			try {
				// creating a constructor of file class and parsing an XML file
				File file = new File(chemin);
				// an instance of factory that gives a document builder
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				// an instance of builder to parse the specified xml file
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(file);
				doc.getDocumentElement().normalize();

				analyse += "Root element: " + doc.getDocumentElement().getNodeName();
				analyse += "\n le fichier XML semble correcte ...";
				SendEmail.send(email, analyse);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
		}
		// Fin du service d'inversion
	}

	protected void finalize() throws Throwable {
		client.close();
	}

	public static String toStringue() {
		return "Service XML";
	}
}
