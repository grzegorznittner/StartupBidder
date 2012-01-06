package com.startupbidder.helper.email;

import java.io.File;
import java.util.Properties;
import java.util.Scanner;

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
	private static String password = "";
	private static String userName = "grzegorz.nittner@gmail.com";

	public void send(String from, String to, String subject, String filename) {
		try {
			Session session = getLocalSession();
			session.setDebug(true);
			MimeMessage message = new MimeMessage(session);
			System.out.println("User authenticated...");
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.addRecipient(Message.RecipientType.CC, new InternetAddress("grzegorz.nittner@gmail.com"));
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
            transport.connect("smtp.gmail.com", userName, password);
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
	    props.put("mail.smtp.port", "587");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.debug", "true");
	    
	    Authenticator auth = new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        
		return Session.getDefaultInstance(props, auth);
	}

	public static void main(String argv[]) {
		System.out.println("Password for account '" + userName + "': ");
		Scanner in = new Scanner(System.in);
		password = in.nextLine();
		in.close();
		
		String from = "grzegorz.nittner@gmail.com";
		String to = "johnarleyburns@gmail.com";

		new HtmlEmail().send(from, to, "Welcome to startupbidder", "E:/projects/startupbidder/email-templates/html/email-welcome-to-startupbidder.html");
		new HtmlEmail().send(from, to, "This week on startupbidder", "E:/projects/startupbidder/email-templates/html/email-this-week-on-startupbidder.html");
		new HtmlEmail().send(from, to, "Listing 'Social Recommendations' received bid!", "E:/projects/startupbidder/email-templates/html/email-owner-receives-bid.html");
	}
}
