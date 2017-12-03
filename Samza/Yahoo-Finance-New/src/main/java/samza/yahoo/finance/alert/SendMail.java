package samza.yahoo.finance.alert;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class SendMail {
	Logger log=Logger.getLogger(SendMail.class);
	
	private static final String SMTP_HOST_NAME = "gemini.jvmhost.com"; 
	private static final String SMTP_AUTH_USER = "me@domain.com"; 
	private static final String SMTP_AUTH_PWD = "secret"; 
	private static final String emailMsgTxt = "Body"; 
	private static final String emailSubjectTxt = "Update of Stock"; 
	private static final String emailFromAddress = "me@domain.com";
	
	Properties props = null;

	public SendMail() {
		props = new Properties();
		props.put("mail.smtp.host" , "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port" , "465");
		props.put("mail.smtp.socketFactory.class" , "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth","true");
		props.put("mail.smtp.port", "465");
	}

	public void createAlert(String message, Double value) {
		
		Session session = Session.getDefaultInstance(props, new SMTPAuthenticator());
	
		try {
			Message mimemessage = new MimeMessage(session);
			
			mimemessage.setFrom(new InternetAddress(SMTP_AUTH_USER));
			
			mimemessage.setRecipients(Message.RecipientType.TO,InternetAddress.parse(SMTP_AUTH_PWD));
			
			mimemessage.setSubject(emailSubjectTxt);
			
			mimemessage.setText("Dear Subscriber," + "\n\n "+message+" --> "+value);

			Transport.send(mimemessage);
		
		}catch(AddressException e){
			log.debug("Address Exception occurred.... Please check the mail id whether correct or not "+e.getMessage());			
			log.debug("Message Exception occurred.. "+e.getMessage());
		
		}catch(MessagingException e){
		}
	}	
	private  class SMTPAuthenticator extends Authenticator
	{
	    public PasswordAuthentication getPasswordAuthentication()
	    {
	        return new PasswordAuthentication("email@gmail.com", "test1234");
	    }
	}


}