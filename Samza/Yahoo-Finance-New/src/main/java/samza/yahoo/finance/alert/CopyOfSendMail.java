package samza.yahoo.finance.alert;

import java.util.Properties;


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
public class CopyOfSendMail {
	Logger log=Logger.getLogger(CopyOfSendMail.class);
	String to = "jopandy679@gmail.com";
	String from = "jopandy679@gmail.com";
	Properties props = null;

	public CopyOfSendMail() {
		props = new Properties();
		props.put("mail.smtp.host" , "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port" , "465");
		props.put("mail.smtp.socketFactory.class" , "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth","true");
		props.put("mail.smtp.port", "465");
	}

	public void createAlert(String message, Double value) {
		System.out.println("alert called" + message + value);
		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(
								from, "jothipandiyan.jp");
					}
				});

		try {
			Message mimemessage = new MimeMessage(session);
			mimemessage.setFrom(new InternetAddress(from));
			
			mimemessage.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			
			mimemessage.setSubject("Update of Stock");
			mimemessage.setText("Dear Subscriber," + "\n\n "+message+" --> "+value);

			Transport.send(mimemessage);
		
			System.out.println("Done");

		}catch(AddressException e){
			log.debug("Address Exception occurred.... Please check the mail id whether correct or not "+e.getMessage());			
		}catch(MessagingException e){
			log.debug("Message Exception occurred.. "+e.getMessage());
		}
		
	}
}