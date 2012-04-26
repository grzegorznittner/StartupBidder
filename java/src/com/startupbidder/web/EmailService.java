package com.startupbidder.web;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.utils.SystemProperty;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.vo.BidVO;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.NotificationVO;
import com.startupbidder.vo.UserVO;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class EmailService {
	private static final Logger log = Logger.getLogger(EmailService.class.getName());
	
	private static final String LINK_TO_VIEW_ON_STARTUPBIDDER = "##NOTIFICATION_LINK_TO_VIEW_ON_STARTUPBIDDER##";
	private static final String NOTIFICATION_TITLE = "##NOTIFICATION_TITLE##";
	private static final String TEXT_NO_LINK = "##NOTIFICATION_TEXT_NO_LINK##";
	private static final String VISIT_LISTING_TEXT = "##NOTIFICATION_VISIT_LISTING_TEXT##";
	private static final String LINK_TO_LISTING = "##NOTIFICATION_LINK_TO_LISTING##";
	private static final String LINK_TO_LISTING_LOGO = "##NOTIFICATION_LINK_TO_LISTING_LOGO##";
	private static final String LISTING_NAME = "##NOTIFICATION_LISTING_NAME##";
	private static final String LISTING_CATEGORY_LOCATION = "##NOTIFICATION_LISTING_CATEGORY_LOCATION##";
	private static final String LISTING_MANTRA = "##NOTIFICATION_LISTING_MANTRA##";
	private static final String COPYRIGHT_TEXT = "##NOTIFICATION_COPYRIGHT_TEXT##";
	private static final String LINK_TO_UPDATE_PROFILE_PAGE = "##LINK_TO_UPDATE_PROFILE_PAGE##";
	
	private static EmailService instance = null;
	
	public static EmailService instance() {
		if (instance == null) {
			instance = new EmailService();
		}
		return instance;
	}
	
	private EmailService() {
	}
		
	public void send(String from, String to, String subject, String htmlBody) throws AddressException, MessagingException {
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.addRecipient(Message.RecipientType.CC, new InternetAddress("admins"));
		message.setSubject(subject);

		Multipart multipart = new MimeMultipart();
		BodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(htmlBody, "text/html");
		htmlPart.setDisposition(BodyPart.INLINE);
		multipart.addBodyPart(htmlPart);

		message.setContent(multipart);

		Transport.send(message, message.getAllRecipients());
	}

	public boolean sendListingNotification(NotificationVO notification) {
		String htmlTemplateFile = "./WEB-INF/email-templates/notification.html";
		try {
			Map<String, String> props = prepareProperties(notification);
			String htmlTemplate = FileUtils.readFileToString(new File(htmlTemplateFile), "UTF-8");
			String htmlBody = applyProperties(htmlTemplate, props);
			String subject = props.get(NOTIFICATION_TITLE);
			send("admin@startupbidder.com", "grzegorz.nittner@vodafone.com", subject, htmlBody);
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error sending notification email", e);
			return false;
		}
	}
	
	private Map<String, String> prepareProperties(NotificationVO notification) {
		Map<String, String> props = new HashMap<String, String>();
		
		props.put(LINK_TO_VIEW_ON_STARTUPBIDDER, "http://www.startupbidder.com/notification-page.html?id=" + notification.getId());
		props.put(NOTIFICATION_TITLE, notification.getTitle());
		props.put(TEXT_NO_LINK, notification.getText1());
		props.put(VISIT_LISTING_TEXT, notification.getText2());
		props.put(LINK_TO_LISTING, notification.getLink());
		props.put(LINK_TO_LISTING_LOGO, notification.getListingLogoLink());
		props.put(LISTING_NAME, notification.getListingName());
		props.put(LISTING_CATEGORY_LOCATION, notification.getListingCategory() + " <br>" + notification.getListingBriefAddress());
		props.put(LISTING_MANTRA, notification.getListingMantra());
		props.put(COPYRIGHT_TEXT, "2012 startupbidder.com");
		props.put(LINK_TO_UPDATE_PROFILE_PAGE, "http://www.startupbidder.com/edit-profile-page.html");
		return props;
	}

	private String applyProperties(String htmlTemplate, Map<String, String> props) {
		for (Map.Entry<String, String> entry : props.entrySet()) {
			htmlTemplate = StringUtils.replace(htmlTemplate, entry.getKey(), entry.getValue());
		}
		return htmlTemplate;
	}

}
