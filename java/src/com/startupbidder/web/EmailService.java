package com.startupbidder.web;

import java.io.File;
import java.util.HashMap;
import java.util.List;
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
import org.apache.commons.lang3.StringUtils;

import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.SystemProperty;
import com.startupbidder.vo.BaseVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingTileVO;
import com.startupbidder.vo.NotificationVO;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class EmailService {
	private static final Logger log = Logger.getLogger(EmailService.class.getName());
	
	private static final String WELCOME_IMAGE_URL = "http://www.startupbidder.com/img/email-welcome.jpg";
	private static final String NOTIFICATION_IMAGE_URL = "http://www.startupbidder.com/img/email-notification.jpg";
	
	private static final String LINK_TO_HEADER_IMAGE = "##NOTIFICATION_LINK_TO_HEADER_IMAGE##";
	
	private static final String LINK_TO_VIEW_ON_STARTUPBIDDER = "##NOTIFICATION_LINK_TO_VIEW_ON_STARTUPBIDDER##";
	private static final String NOTIFICATION_TITLE = "##NOTIFICATION_TITLE##";
	private static final String NOTIFICATION_TITLE_ESCAPED = "##NOTIFICATION_TITLE_ESCAPED##";
	private static final String TEXT_NO_LINK = "##NOTIFICATION_TEXT_NO_LINK##";
	private static final String VISIT_LISTING_TEXT = "##NOTIFICATION_VISIT_LISTING_TEXT##";
	private static final String LINK_TO_LISTING = "##NOTIFICATION_LINK_TO_LISTING##";
	private static final String LINK_TO_LISTING_LOGO = "##NOTIFICATION_LINK_TO_LISTING_LOGO##";
	private static final String LISTING_NAME = "##NOTIFICATION_LISTING_NAME##";
	private static final String LISTING_CATEGORY_LOCATION = "##NOTIFICATION_LISTING_CATEGORY_LOCATION##";
	private static final String LISTING_MANTRA = "##NOTIFICATION_LISTING_MANTRA##";
	private static final String COPYRIGHT_TEXT = "##NOTIFICATION_COPYRIGHT_TEXT##";
	private static final String LINK_TO_UPDATE_PROFILE_PAGE = "##LINK_TO_UPDATE_PROFILE_PAGE##";

	private static final String LINK_TO_LISTING_1 = "##NOTIFICATION_LINK_TO_LISTING_1##";
	private static final String LINK_TO_LISTING_1_LOGO = "##NOTIFICATION_LINK_TO_LISTING_1_LOGO##";
	private static final String LISTING_1_NAME = "##NOTIFICATION_LISTING_1_NAME##";
	private static final String LISTING_1_CATEGORY_LOCATION = "##NOTIFICATION_LISTING_1_CATEGORY_LOCATION##";
	private static final String LISTING_1_MANTRA = "##NOTIFICATION_LISTING_1_MANTRA##";

	private static final String LINK_TO_LISTING_2 = "##NOTIFICATION_LINK_TO_LISTING_2##";
	private static final String LINK_TO_LISTING_2_LOGO = "##NOTIFICATION_LINK_TO_LISTING_2_LOGO##";
	private static final String LISTING_2_NAME = "##NOTIFICATION_LISTING_2_NAME##";
	private static final String LISTING_2_CATEGORY_LOCATION = "##NOTIFICATION_LISTING_2_CATEGORY_LOCATION##";
	private static final String LISTING_2_MANTRA = "##NOTIFICATION_LISTING_2_MANTRA##";

	private static final String LINK_TO_LISTING_3 = "##NOTIFICATION_LINK_TO_LISTING_3##";
	private static final String LINK_TO_LISTING_3_LOGO = "##NOTIFICATION_LINK_TO_LISTING_3_LOGO##";
	private static final String LISTING_3_NAME = "##NOTIFICATION_LISTING_3_NAME##";
	private static final String LISTING_3_CATEGORY_LOCATION = "##NOTIFICATION_LISTING_3_CATEGORY_LOCATION##";
	private static final String LISTING_3_MANTRA = "##NOTIFICATION_LISTING_3_MANTRA##";

	private static final String NOTIFICATION_TEXT_1 = "##NOTIFICATION_TEXT_1##";
	private static final String NOTIFICATION_TEXT_2 = "##NOTIFICATION_TEXT_2##";
	private static final String NOTIFICATION_TEXT_3 = "##NOTIFICATION_TEXT_3##";
	private static final String NOTIFICATION_LINK_TEXT = "##NOTIFICATION_LINK_TEXT##";
	private static final String NOTIFICATION_LINK_HREF = "##NOTIFICATION_LINK_HREF##";

	private static EmailService instance = null;
	
	public static EmailService instance() {
		if (instance == null) {
			instance = new EmailService();
		}
		return instance;
	}
	
	private EmailService() {
	}
	
	public void sendAdmin(String from, String to, String subject, String htmlBody) throws AddressException, MessagingException {
		log.info("Email to: " + to + " subject:" + subject);
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);

		Multipart multipart = new MimeMultipart();
		BodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(htmlBody, "text/html");
		htmlPart.setDisposition(BodyPart.INLINE);
		multipart.addBodyPart(htmlPart);

		message.setContent(multipart);

		Transport.send(message, message.getAllRecipients());
	}
		
	public void send(String from, String to, String subject, String htmlBody) throws AddressException, MessagingException {
		log.info("Email to: " + to + " subject:" + subject);
		SystemProperty noBccAdmins = ObjectifyDatastoreDAO.getInstance().getSystemProperty("notification_no_bcc_admins");
		SystemProperty realReceivers = ObjectifyDatastoreDAO.getInstance().getSystemProperty("notification_real_receivers");
		
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		if (realReceivers != null && realReceivers.booleanValue()) {
			// notification_real_receivers == true
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			if (noBccAdmins == null || (noBccAdmins !=null && !noBccAdmins.booleanValue())) {
				// notification_no_bcc_admins == null OR notification_no_bcc_admins == false
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress("admins"));
			}
			message.setSubject(subject);
		} else {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("admins"));
			message.setSubject(subject + " real addressee " + to);
		}

		Multipart multipart = new MimeMultipart();
		BodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(htmlBody, "text/html");
		htmlPart.setDisposition(BodyPart.INLINE);
		multipart.addBodyPart(htmlPart);

		message.setContent(multipart);

		Transport.send(message, message.getAllRecipients());
	}
	
	public boolean sendNotificationEmail(NotificationVO notification) {
		if (Notification.Type.ADMIN_REQUEST_TO_BECOME_DRAGON.toString().equalsIgnoreCase(notification.getType())) {
			return sendAdminNotification(notification);
		} if (Notification.Type.PRIVATE_MESSAGE.toString().equalsIgnoreCase(notification.getType())) {
			return send3ListingNotification(notification);
		} else {
			return sendListingNotification(notification);
		}
	}

	private boolean sendListingNotification(NotificationVO notification) {
		String htmlTemplateFile = "./WEB-INF/email-templates/notification.html";
		try {
			Map<String, String> props = prepareListingNotificationProps(notification);
			String htmlTemplate = FileUtils.readFileToString(new File(htmlTemplateFile), "UTF-8");
			String htmlBody = applyProperties(htmlTemplate, props);
			String subject = props.get(NOTIFICATION_TITLE);
			send("admin@startupbidder.com", notification.getUserEmail(), subject, htmlBody);
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error sending notification email", e);
			return false;
		}
	}

	public Map<String, String> prepareListingNotificationProps(NotificationVO notification) {
		Map<String, String> props = new HashMap<String, String>();
		
		props.put(LINK_TO_VIEW_ON_STARTUPBIDDER, "http://www.startupbidder.com/notification-page.html?id=" + notification.getId());
		props.put(NOTIFICATION_TITLE, notification.getTitle());
		props.put(NOTIFICATION_TITLE_ESCAPED, notification.getTitle().replaceAll("\\.", "<span>.</span>"));
		props.put(TEXT_NO_LINK, notification.getText1().replaceAll("\\.", "<span>.</span>") + " <br/> <br/> <i>"
				+ notification.getText2().replaceAll("\\.", "<span>.</span>") + "</i>");
		props.put(VISIT_LISTING_TEXT, notification.getText3());
		props.put(LINK_TO_LISTING, notification.getLink());
		props.put(LINK_TO_LISTING_LOGO, notification.getListingLogoLink());
		props.put(LISTING_NAME, notification.getListingName().replaceAll("\\.", "<span>.</span>"));
		props.put(LISTING_CATEGORY_LOCATION, notification.getListingCategory() + " <br/>" + notification.getListingBriefAddress());
		props.put(LISTING_MANTRA, notification.getListingMantra().replaceAll("\\.", "<span>.</span>"));
		props.put(COPYRIGHT_TEXT, "2012 startupbidder.com");
		props.put(LINK_TO_UPDATE_PROFILE_PAGE, "http://www.startupbidder.com/edit-profile-page.html");
		return props;
	}

	private boolean send3ListingNotification(NotificationVO notification) {
		String htmlTemplateFile = "./WEB-INF/email-templates/welcome-email.html";
		try {
			ListPropertiesVO listingsProps = new ListPropertiesVO();
			listingsProps.setMaxResults(3);
			List<ListingTileVO> listingsTiles = DtoToVoConverter.convertListingTiles(
					ObjectifyDatastoreDAO.getInstance().getTopListings(listingsProps));
			ListingTileVO listings[] = new ListingTileVO[3];
			listings[0] = listingsTiles.get(0);
			listings[1] = listingsTiles.get(1);
			listings[2] = listingsTiles.get(2);
			Map<String, String> props = prepare3ListingNotificationProps(notification, listings);
			String htmlTemplate = FileUtils.readFileToString(new File(htmlTemplateFile), "UTF-8");
			String htmlBody = applyProperties(htmlTemplate, props);
			String subject = props.get(NOTIFICATION_TITLE);
			send("admin@startupbidder.com", notification.getUserEmail(), subject, htmlBody);
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error sending 3 listing notification email", e);
			return false;
		}
	}

	public Map<String, String> prepare3ListingNotificationProps(NotificationVO notification, ListingTileVO listings[]) {
		Map<String, String> props = new HashMap<String, String>();
		
		props.put(LINK_TO_VIEW_ON_STARTUPBIDDER, "http://www.startupbidder.com/notification-page.html?id=" + notification.getId());
		props.put(NOTIFICATION_TITLE, notification.getTitle());
		props.put(NOTIFICATION_TITLE_ESCAPED, notification.getTitle().replaceAll("\\.", "<span>.</span>"));
		props.put(TEXT_NO_LINK, notification.getText1().replaceAll("\\.", "<span>.</span>") + " <i>"
				+ notification.getText2().replaceAll("\\.", "<span>.</span>") + "</i>");
		props.put(VISIT_LISTING_TEXT, notification.getText3());
		
		props.put(LINK_TO_HEADER_IMAGE, NOTIFICATION_IMAGE_URL);
		
		props.put(LINK_TO_LISTING_1, BaseVO.getServiceLocation() + "/company-page.html?id=" + listings[0].getId());
		props.put(LINK_TO_LISTING_1_LOGO, BaseVO.getServiceLocation() + "/listing/logo?id=" + listings[0].getId());
		props.put(LISTING_1_NAME, listings[0].getName().replaceAll("\\.", "<span>.</span>"));
		props.put(LISTING_1_CATEGORY_LOCATION, listings[0].getCategory() + " <br/>" + listings[0].getBriefAddress());
		props.put(LISTING_1_MANTRA, listings[0].getMantra().replaceAll("\\.", "<span>.</span>"));

		props.put(LINK_TO_LISTING_2, BaseVO.getServiceLocation() + "/company-page.html?id=" + listings[1].getId());
		props.put(LINK_TO_LISTING_2_LOGO, BaseVO.getServiceLocation() + "/listing/logo?id=" + listings[1].getId());
		props.put(LISTING_2_NAME, listings[1].getName().replaceAll("\\.", "<span>.</span>"));
		props.put(LISTING_2_CATEGORY_LOCATION, listings[1].getCategory() + " <br/>" + listings[1].getBriefAddress());
		props.put(LISTING_2_MANTRA, listings[1].getMantra().replaceAll("\\.", "<span>.</span>"));

		props.put(LINK_TO_LISTING_3, BaseVO.getServiceLocation() + "/company-page.html?id=" + listings[2].getId());
		props.put(LINK_TO_LISTING_3_LOGO, BaseVO.getServiceLocation() + "/listing/logo?id=" + listings[2].getId());
		props.put(LISTING_3_NAME, listings[2].getName().replaceAll("\\.", "<span>.</span>"));
		props.put(LISTING_3_CATEGORY_LOCATION, listings[2].getCategory() + " <br/>" + listings[2].getBriefAddress());
		props.put(LISTING_3_MANTRA, listings[2].getMantra().replaceAll("\\.", "<span>.</span>"));

		props.put(COPYRIGHT_TEXT, "2012 startupbidder.com");
		props.put(LINK_TO_UPDATE_PROFILE_PAGE, "http://www.startupbidder.com/edit-profile-page.html");
		return props;
	}

	private boolean sendAdminNotification(NotificationVO notification) {
		String htmlTemplateFile = "./WEB-INF/email-templates/access-email.html";
		try {
			Map<String, String> props = prepareAdminNotificationProps(notification);
			String htmlTemplate = FileUtils.readFileToString(new File(htmlTemplateFile), "UTF-8");
			String htmlBody = applyProperties(htmlTemplate, props);
			String subject = props.get(NOTIFICATION_TITLE);
			sendAdmin("admin@startupbidder.com", notification.getUserEmail(), subject, htmlBody);
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error sending notification email", e);
			return false;
		}
	}
	
	public Map<String, String> prepareAdminNotificationProps(NotificationVO notification) {
		Map<String, String> props = new HashMap<String, String>();
		
		props.put(NOTIFICATION_TITLE, notification.getTitle());
		props.put(NOTIFICATION_TITLE_ESCAPED, notification.getTitle().replaceAll("\\.", "<span>.</span>"));
		props.put(NOTIFICATION_TEXT_1, notification.getText1().replaceAll("\\.", "<span>.</span>"));
		props.put(NOTIFICATION_TEXT_2, notification.getText2().replaceAll("\\.", "<span>.</span>"));
		props.put(NOTIFICATION_TEXT_3, notification.getText3().replaceAll("\\.", "<span>.</span>"));
		props.put(NOTIFICATION_LINK_HREF, notification.getLink());
		props.put(NOTIFICATION_LINK_TEXT, notification.getListingOwner());
		props.put(COPYRIGHT_TEXT, "2012 startupbidder.com");
		return props;
	}

	public boolean sendEmailVerification(SBUser userByTwitter) {
		String htmlTemplateFile = "./WEB-INF/email-templates/access-email.html";
		try {
			// send verification email with link /user/confirm_user_email?id=<twitter_id>&token=<token>
			String activationUrl = com.google.appengine.api.utils.SystemProperty.environment.value() == com.google.appengine.api.utils.SystemProperty.Environment.Value.Development ?
					"http://localhost:7777" : "http://www.startupbidder.com";
			activationUrl += "/user/confirm_user_email?id=" + userByTwitter.twitterId + "&token=" +userByTwitter.activationCode;
			
			Map<String, String> props = prepareEmailVerificationProps(userByTwitter.twitterEmail, activationUrl);
			String htmlTemplate = FileUtils.readFileToString(new File(htmlTemplateFile), "UTF-8");
			String htmlBody = applyProperties(htmlTemplate, props);
			String subject = props.get(NOTIFICATION_TITLE);
			send("admin@startupbidder.com", userByTwitter.twitterEmail, subject, htmlBody);
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error sending email verification email", e);
			return false;
		}
	}

	public Map<String, String> prepareEmailVerificationProps(String receiverEmail, String accessUrl) {
		Map<String, String> props = new HashMap<String, String>();
		
		props.put(NOTIFICATION_TITLE, "Email address verification");
		props.put(NOTIFICATION_TITLE_ESCAPED, "Email address verification");
		props.put(NOTIFICATION_TEXT_1, "By clicking on ");
		props.put(NOTIFICATION_LINK_HREF, accessUrl);
		props.put(NOTIFICATION_LINK_TEXT, "verification link");
		props.put(NOTIFICATION_TEXT_2, "you confirm that email address " + receiverEmail + " belongs to you.");
		props.put(NOTIFICATION_TEXT_3, "If you already logged in to startupbidder<span>.</span>com with Facebook or Google your account will be merged.");
		props.put(COPYRIGHT_TEXT, "2012 startupbidder.com");
		return props;
	}

	public boolean sendAccessEmail(String receiverEmail, String accessUrl) {
		String htmlTemplateFile = "./WEB-INF/email-templates/access-email.html";
		try {
			Map<String, String> props = prepareAccessEmailProps(receiverEmail, accessUrl);
			String htmlTemplate = FileUtils.readFileToString(new File(htmlTemplateFile), "UTF-8");
			String htmlBody = applyProperties(htmlTemplate, props);
			String subject = props.get(NOTIFICATION_TITLE);
			send("admin@startupbidder.com", receiverEmail, subject, htmlBody);
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error sending notification email", e);
			return false;
		}
	}
	
	public Map<String, String> prepareAccessEmailProps(String receiverEmail, String accessUrl) {
		Map<String, String> props = new HashMap<String, String>();
		
		props.put(NOTIFICATION_TITLE, "You requested access to startupbidder.com");
		props.put(NOTIFICATION_TITLE_ESCAPED, "You requested access to startupbidder<span>.</span>com");
		props.put(NOTIFICATION_TEXT_1, "Please");
		props.put(NOTIFICATION_LINK_HREF, accessUrl);
		props.put(NOTIFICATION_LINK_TEXT, "click on the link");
		props.put(NOTIFICATION_TEXT_2, "to access startupbidder.com");
		props.put(NOTIFICATION_TEXT_3, "Access is granted for limited time and for the browser window you'll open above link.");
		props.put(COPYRIGHT_TEXT, "2012 startupbidder.com");
		return props;
	}

	public String applyProperties(String htmlTemplate, Map<String, String> props) {
		for (Map.Entry<String, String> entry : props.entrySet()) {
			htmlTemplate = StringUtils.replace(htmlTemplate, entry.getKey(), entry.getValue());
		}
		return htmlTemplate;
	}
}
