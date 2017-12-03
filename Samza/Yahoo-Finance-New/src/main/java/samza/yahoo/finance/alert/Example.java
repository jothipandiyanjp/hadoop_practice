package samza.yahoo.finance.alert;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Example {
	public static void main(String args[]) {

		String to = "jopandy679@gmail.com";
		String from = "jopandy679@gmail.com";
		String host = "smtp.gmail.com";
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		Session session = Session.getDefaultInstance(properties);
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));
			message.setSubject("Test Mail from Java Program");
			message.setText("You can send mail from Java program by using mail API, but you need"
					+ "couple of more JAR files e.g. smtp.jar and activation.jar");
			Transport.send(message);
			System.out.println("Email Sent successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}
