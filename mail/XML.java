package mail;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class XML {
	public static void main(String argv[]) {
		try {
			// creating a constructor of file class and parsing an XML file
			File file = new File("C:\\Users\\Fantin\\Desktop\\Fantin\\Java\\xmlfile.xml");
			// an instance of factory that gives a document builder
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			// an instance of builder to parse the specified xml file
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			String str = "";
			str += ("Root element: " + doc.getDocumentElement().getNodeName());
			NodeList nodeList = doc.getElementsByTagName("student");
			// nodeList is not iterable, so we are using for loop
			for (int itr = 0; itr < nodeList.getLength(); itr++) {
				Node node = nodeList.item(itr);
				str += ("\nNode Name :" + node.getNodeName());
			}
			SendEmail.send("raimbault.fantin.pro@gmail.com", str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
