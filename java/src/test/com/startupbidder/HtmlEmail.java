package test.com.startupbidder;

import java.io.Console;
import java.io.File;
import java.util.Date;
import java.util.Map;
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

import com.startupbidder.datamodel.Notification;
import com.startupbidder.vo.NotificationVO;
import com.startupbidder.web.EmailService;

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

	public void sendHtml(String from, String to, String subject, String html) {
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

			System.out.println("    html length: " + html.getBytes().length);
			htmlPart.setContent(html, "text/html");
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
//	    props.put("mail.smtp.socks.host", "proxy01.gps.internal.vodafone.com");
//	    props.put("mail.smtp.socks.port", "8080");
	    
	    Authenticator auth = new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        
		return Session.getDefaultInstance(props, auth);
	}
	
	public NotificationVO prepareNotificaiton() {
		NotificationVO notif = new NotificationVO();
		notif.setUser("User");
		notif.setUserNickname("userNickname");
		notif.setUserEmail("userEmail@startupbidder.com");
		notif.setCreated(new Date());
		notif.setSentDate(new Date());
		notif.setListing("ag9zfnN0YXJ0dXBiaWRkZXJyDwsSB0xpc3RpbmcY-7EMDA");
		notif.setListingName("zix.it");
		notif.setListingOwner("listingOwner");
		notif.setListingOwnerId("listingOwnerUser");
		notif.setListingMantra("listingMantra");
		notif.setListingCategory("Software");
		notif.setListingBriefAddress("London, UK");
		notif.setType(Notification.Type.LISTING_ACTIVATED.toString());
		notif.setRead(false);
		notif.setLink("http://www.startupbidder.com");
		notif.setTitle("New listing 'zix.it' posted");
		notif.setText1("A new listing 'zix.it' has been posted by 'listingOwner' on startupbidder.com");
		notif.setText2("");
		notif.setText3("Please visit <a href=\"http://www.startupbidder.com\">company's page at startupbidder.com</a>.");
		
		return notif;
	}

	public boolean sendListingNotification(String emailTo) {
		NotificationVO notification = prepareNotificaiton();
		String htmlTemplateFile = "E:/projects/startupbidder/war/WEB-INF/email-templates/notification.html";
		try {
			Map<String, String> props = EmailService.instance().prepareListingNotificationProps(notification);
			String htmlTemplate = FileUtils.readFileToString(new File(htmlTemplateFile), "UTF-8");
			String htmlBody = EmailService.instance().applyProperties(htmlTemplate, props);
			String subject = props.get("##NOTIFICATION_TITLE##");
			sendHtml("admin@startupbidder.com", emailTo, subject, htmlBody);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String argv[]) {
//		Console console = System.console();
//		password = new String(console.readPassword("Password for account '" + userName + "': "));
		System.out.println("Password for account '" + userName + "': ");
		Scanner in = new Scanner(System.in);
		password = in.nextLine();
		in.close();

		/*
		String from = "grzegorz.nittner@gmail.com";
		String to = "johnarleyburns@gmail.com";
		new HtmlEmail().send(from, to, "Welcome to startupbidder", "E:/projects/startupbidder/email-templates/html/email-welcome-to-startupbidder.html");
		new HtmlEmail().send(from, to, "This week on startupbidder", "E:/projects/startupbidder/email-templates/html/email-this-week-on-startupbidder.html");
		new HtmlEmail().send(from, to, "Listing 'Social Recommendations' received bid!", "E:/projects/startupbidder/email-templates/html/email-owner-receives-bid.html");
		*/
		new HtmlEmail().sendListingNotification("grzegorz.nittner@gmail.com");
		new HtmlEmail().sendListingNotification("grzegorz.nittner@vodafone.com");
	}
}
