package com.startupbidder.helper.email;

import java.io.File;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;

public class HtmlEmail {

	public void send(String from, String to, String subject, String filename) {
		try {
			Session session = getLocalSession();
			session.setDebug(true);
			MimeMessage message = new MimeMessage(session);
			System.out.println("User authenticated...");
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);

			Multipart multipart = new MimeMultipart();

			BodyPart htmlPart = new MimeBodyPart();

			String htmlBody = FileUtils.readFileToString(new File(filename));
			System.out.println("    html length: " + htmlBody.getBytes().length);
			htmlPart.setContent(htmlBody, "text/html");
			htmlPart.setDisposition(BodyPart.INLINE);
			multipart.addBodyPart(htmlPart);

			message.setContent(multipart);

			System.out.println("... sending email");
			Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", "grzegorz.nittner@gmail.com", "gregaga10a");
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
			System.out.println("... done.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Session getLocalSession() {
		Properties props = System.getProperties();
		props.put("mail.smtp.host", "smtp.gmail.com");
	    //props.put("mail.smtp.user", "grzegorz.nittner");
		//props.put("mail.password", "gregaga10a");
	    props.put("mail.smtp.port", "587");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.debug", "true");
	    
	    Authenticator auth = new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("grzegorz.nittner@gmail.com", "gregaga10a");
            }
        };
        
		return Session.getDefaultInstance(props, auth);
	}

	public void sendEmail() {
		String from = "grzegorz.nittner@gmail.com";
		String to = "grzegorz.nittner@gmail.com";
		String subject = "Welcome to startupbidder";

		send(from, to, subject, "E:/projects/startupbidder/email-templates/html/email-welcome-to-startupbidder.html");
	}

	public static void main(String argv[]) {
		new HtmlEmail().sendEmail();
	}
}
