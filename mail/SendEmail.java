package mail;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;
import java.util.Properties;

public class SendEmail {

    public static void send(String recipient, String analyse) throws Exception{
    	System.out.println("preparing email");
        Properties prop = new Properties();

        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        
        String myAccount = "imabotcreatedbyrf2@gmail.com";
        String myPassword = "myBOTpassword94!";

        Session session = Session.getInstance(prop, new Authenticator() {
        	protected PasswordAuthentication getPasswordAuthentication() {
        		return new PasswordAuthentication(myAccount, myPassword);
        	}
		});
        Message msg = prepareMessage(session, myPassword, recipient, analyse);

        Transport.send(msg);

    }

	private static Message prepareMessage(Session session, String myAccount, String recipient, String analyse) {
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(myAccount));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			message.setSubject("Analyse (vraiment pas top) d'un fichier XML");
			message.setText(analyse);
			return message;
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return null;
	}
}