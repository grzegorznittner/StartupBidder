package com.startupbidder.web;

import java.io.UnsupportedEncodingException;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import com.google.appengine.api.utils.SystemProperty;
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
	
	private static EmailService instance = null;
	
	public static EmailService instance() {
		if (instance == null) {
			instance = new EmailService();
		}
		return instance;
	}
	
	private EmailService() {
	}
	
	private String getServiceLocation () {
		return SystemProperty.environment.value() == SystemProperty.Environment.Value.Development ?
				"http://localhost:7777" : "http://www.startupbidder.com";
	}
	
	private String createLinkUrl(Object object) {
		if (object instanceof UserVO) {
			return getServiceLocation() + "/user/get?id=" + ((UserVO)object).getId();
		} else if (object instanceof ListingVO) {
			return getServiceLocation() + "/listing/get?id=" + ((ListingVO)object).getId();
		} else if (object instanceof CommentVO) {
			return getServiceLocation() + "/comment/get?id=" + ((CommentVO)object).getId();
		} else if (object instanceof BidVO) {
			return getServiceLocation() + "/bid/get?id=" + ((BidVO)object).getId();
		} else {
			return "";
		}
	}

	private void sendEmail(Map<String, String> to, Map<String, String> cc, Map<String, String> bcc,
			String subject, String text, String html) {
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("admin@startupbidder.com", "StartupBidder.com Admin"));
            
            for (Map.Entry<String, String> recipient : to.entrySet()) {
            	msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(recipient.getKey(), recipient.getValue()));
            }
            for (Map.Entry<String, String> recipient : cc.entrySet()) {
            	msg.addRecipient(Message.RecipientType.CC,
                             new InternetAddress(recipient.getKey(), recipient.getValue()));
            }
            for (Map.Entry<String, String> recipient : bcc.entrySet()) {
            	msg.addRecipient(Message.RecipientType.BCC,
                             new InternetAddress(recipient.getKey(), recipient.getValue()));
            }
            msg.setSubject("[StartupBidder.com] " + subject);
            msg.setText(text);
            
    		Multipart mp = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(html, "text/html");
            mp.addBodyPart(htmlPart);
            //msg.setContent(mp);

            Transport.send(msg);
        } catch (AddressException e) {
            log.log(Level.SEVERE, "Wrong email address", e);
        } catch (MessagingException e) {
        	log.log(Level.SEVERE, "Error sending email", e);
        } catch (UnsupportedEncodingException e) {
        	log.log(Level.SEVERE, "Wrong encoding", e);
		}
	}

	public void sendAcceptedBidNotification (BidVO bid, ListingVO listing, UserVO listingOwner, UserVO bidder) {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>Congratulations on reaching a preliminary funding agreement!</h2></p>");
		textBody.append("Congratulations on reaching a preliminary funding agreement!\n");

		htmlBody.append("<p><h2>" + "<a href=\"" + createLinkUrl(listing) + "\">" + listing.getName() + "</a>" + " posted by " + listingOwner.getName() + "</h2></p>");
		textBody.append("\n" + listing.getName() + " ( " + createLinkUrl(listing) + " )" + " posted by " + listingOwner.getName() + "\n");

		htmlBody.append("<p><h3>" + listing.getSummary() + "</h3></p>");
		textBody.append(listing.getSummary() + "\n");

		htmlBody.append("<p><h3>Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "</h3></p>");
		textBody.append("Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "\n");

		double fee = bid.getValue() * 0.005;
		htmlBody.append("<p><h3>Bid fee " + new Formatter().format ("%.0f", fee) + "(0.5% of funding amount) payable Net 30 by " + listingOwner.getName() + "</h3></p>");
		textBody.append("Bid fee " + new Formatter().format ("%.0f", fee) + "(0.5% of funding amount) payable Net 30 by " + listingOwner.getName() + "\n");

		htmlBody.append("<p>Click to see " + "<a href=\"" + createLinkUrl(bid) + "\">" + "bid details" + "</a>" + "</p>");
		textBody.append("Click to see bid details " + createLinkUrl(listing) + "\n");
		
		htmlBody.append("<p>Listing owner details: " + listingOwner.getName() + " <" + listingOwner.getEmail() + "></p>");
		textBody.append("Listing owner details: " + listingOwner.getName() + " <" + listingOwner.getEmail() + ">\n");

		htmlBody.append("<p>Investor details: " + bidder.getName() + " <" + bidder.getEmail() + "></p>");
		textBody.append("Investor details: " + bidder.getName() + " <" + bidder.getEmail() + ">\n");

		htmlBody.append("<p>Dear <b>" + bidder.getName() + "</b>, please contact <b>" + listingOwner.getName() + "</b> to proceed with a formal funding agreement.</p>");
		textBody.append("Dear " + bidder.getName() + ", please contact " + listingOwner.getName() + " to proceed with a formal funding agreement.\n");

		htmlBody.append("<p>Email should be sent to " + bidder.getName() + " <" + bidder.getEmail() + "></p>");
		textBody.append("Email should be sent to " + bidder.getName() + " <" + bidder.getEmail() + ">.\n");
		
		htmlBody.append("</body></html>");
		
		Map<String, String> to = new LinkedHashMap<String, String>();
		to.put("grzegorz.nittner@gmail.com", "Greg Nittner");
		to.put("johnarleyburns@gmail.com", "John A. Burns");
		Map<String, String> cc = new LinkedHashMap<String, String>();
		Map<String, String> bcc = new LinkedHashMap<String, String>();
		sendEmail(to, cc, bcc, "Bid was accepted!", textBody.toString(), htmlBody.toString());
	}
	
	public void sendYourBidAcceptedNotification(NotificationVO notification,
			BidVO bid, ListingVO listing, UserVO listingOwner, UserVO bidder) {
		sendAcceptedBidNotification(bid, listing, listingOwner, bidder);
	}

	public void sendPaidBidNotification(NotificationVO notification,
			BidVO bid, ListingVO listing, UserVO listingOwner, UserVO bidder) {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>Thank you! We received your payment.</h2></p>");
		textBody.append("Thank you! We received your payment.\n");

		htmlBody.append("<p><h2>" + "<a href=\"" + createLinkUrl(listing) + "\">" + listing.getName() + "</a>" + " posted by " + listingOwner.getName() + "</h2></p>");
		textBody.append("\n" + listing.getName() + " ( " + createLinkUrl(listing) + " )" + " posted by " + listingOwner.getName() + "\n");

		htmlBody.append("<p><h3>" + listing.getSummary() + "</h3></p>");
		textBody.append(listing.getSummary() + "\n");

		htmlBody.append("<p><h3>Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "</h3></p>");
		textBody.append("Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "\n");
		htmlBody.append("<p>Click to see " + "<a href=\"" + createLinkUrl(bid) + "\">" + "bid details" + "</a>" + "</p>");
		textBody.append("Click to see bid details " + createLinkUrl(listing) + "\n");
		

		htmlBody.append("<p>Listing owner details: " + listingOwner.getName() + " <" + listingOwner.getEmail() + "></p>");
		textBody.append("Listing owner details: " + listingOwner.getName() + " <" + listingOwner.getEmail() + ">\n");

		htmlBody.append("<p>Email should be sent to " + bidder.getName() + " <" + bidder.getEmail() + "></p>");
		textBody.append("Email should be sent to " + bidder.getName() + " <" + bidder.getEmail() + ">.\n");

		htmlBody.append("</body></html>");
		
		Map<String, String> to = new LinkedHashMap<String, String>();
		to.put("grzegorz.nittner@gmail.com", "Greg Nittner");
		to.put("johnarleyburns@gmail.com", "John A. Burns");
		Map<String, String> cc = new LinkedHashMap<String, String>();
		Map<String, String> bcc = new LinkedHashMap<String, String>();
		sendEmail(to, cc, bcc, "Payment recived", textBody.toString(), htmlBody.toString());
	}

	public void sendBidPaidForYourListingNotification(NotificationVO notification,
			BidVO bid, ListingVO listing, UserVO listingOwner, UserVO bidder) {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>We received payment for transaction related to your listing.</h2></p>");
		textBody.append("We received payment for transaction related to your listing.\n");

		htmlBody.append("<p><h2>" + "<a href=\"" + createLinkUrl(listing) + "\">" + listing.getName() + "</a>" + " posted by " + listingOwner.getName() + "</h2></p>");
		textBody.append("\n" + listing.getName() + " ( " + createLinkUrl(listing) + " )" + " posted by " + listingOwner.getName() + "\n");

		htmlBody.append("<p><h3>" + listing.getSummary() + "</h3></p>");
		textBody.append(listing.getSummary() + "\n");

		htmlBody.append("<p><h3>Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "</h3></p>");
		textBody.append("Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "\n");
		htmlBody.append("<p>Click to see " + "<a href=\"" + createLinkUrl(bid) + "\">" + "bid details" + "</a>" + "</p>");
		textBody.append("Click to see bid details " + createLinkUrl(listing) + "\n");
		

		htmlBody.append("<p>Investor details: " + bidder.getName() + " <" + bidder.getEmail() + "></p>");
		textBody.append("Investor details: " + bidder.getName() + " <" + bidder.getEmail() + ">\n");

		htmlBody.append("<p>Email should be sent to " + listingOwner.getName() + " <" + listingOwner.getEmail() + "></p>");
		textBody.append("Email should be sent to " + listingOwner.getName() + " <" + listingOwner.getEmail() + ">.\n");

		htmlBody.append("</body></html>");
		
		Map<String, String> to = new LinkedHashMap<String, String>();
		to.put("grzegorz.nittner@gmail.com", "Greg Nittner");
		to.put("johnarleyburns@gmail.com", "John A. Burns");
		Map<String, String> cc = new LinkedHashMap<String, String>();
		Map<String, String> bcc = new LinkedHashMap<String, String>();
		sendEmail(to, cc, bcc, "Payment recived", textBody.toString(), htmlBody.toString());
	}

	public void sendYourBidActivatedNotification(NotificationVO notification,
			BidVO bid, ListingVO listing, UserVO listingOwner, UserVO bidder) {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>Your bid was activated</h2></p>");
		textBody.append("Your bid was activated\n");

		htmlBody.append("<p><h2>" + "<a href=\"" + createLinkUrl(listing) + "\">" + listing.getName() + "</a>" + " posted by " + listingOwner.getName() + "</h2></p>");
		textBody.append("\n" + listing.getName() + " ( " + createLinkUrl(listing) + " )" + " posted by " + listingOwner.getName() + "\n");

		htmlBody.append("<p><h3>" + listing.getSummary() + "</h3></p>");
		textBody.append(listing.getSummary() + "\n");

		htmlBody.append("<p><h3>Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "</h3></p>");
		textBody.append("Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "\n");
		htmlBody.append("<p>Click to see " + "<a href=\"" + createLinkUrl(bid) + "\">" + "bid details" + "</a>" + "</p>");
		textBody.append("Click to see bid details " + createLinkUrl(listing) + "\n");		

		htmlBody.append("<p>Listing owner details: " + listingOwner.getName() + " <" + listingOwner.getEmail() + "></p>");
		textBody.append("Listing owner details: " + listingOwner.getName() + " <" + listingOwner.getEmail() + ">\n");

		htmlBody.append("<p>Email should be sent to " + bidder.getName() + " <" + bidder.getEmail() + "></p>");
		textBody.append("Email should be sent to " + bidder.getName() + " <" + bidder.getEmail() + ">.\n");

		htmlBody.append("</body></html>");
		
		Map<String, String> to = new LinkedHashMap<String, String>();
		to.put("grzegorz.nittner@gmail.com", "Greg Nittner");
		to.put("johnarleyburns@gmail.com", "John A. Burns");
		Map<String, String> cc = new LinkedHashMap<String, String>();
		Map<String, String> bcc = new LinkedHashMap<String, String>();
		sendEmail(to, cc, bcc, "Bid was activated", textBody.toString(), htmlBody.toString());
	}

	public void sendYourBidRejectedNotification(NotificationVO notification,
			BidVO bid, ListingVO listing, UserVO listingOwner, UserVO bidder) {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>Your bid was rejected</h2></p>");
		textBody.append("Your bid was rejected\n");

		htmlBody.append("<p><h2>" + "<a href=\"" + createLinkUrl(listing) + "\">" + listing.getName() + "</a>" + " posted by " + listingOwner.getName() + "</h2></p>");
		textBody.append("\n" + listing.getName() + " ( " + createLinkUrl(listing) + " )" + " posted by " + listingOwner.getName() + "\n");

		htmlBody.append("<p><h3>" + listing.getSummary() + "</h3></p>");
		textBody.append(listing.getSummary() + "\n");

		htmlBody.append("<p><h3>Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "</h3></p>");
		textBody.append("Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "\n");
		htmlBody.append("<p>Click to see " + "<a href=\"" + createLinkUrl(bid) + "\">" + "bid details" + "</a>" + "</p>");
		textBody.append("Click to see bid details " + createLinkUrl(listing) + "\n");		

		htmlBody.append("<p>Listing owner details: " + listingOwner.getName() + " <" + listingOwner.getEmail() + "></p>");
		textBody.append("Listing owner details: " + listingOwner.getName() + " <" + listingOwner.getEmail() + ">\n");

		htmlBody.append("<p>Email should be sent to " + bidder.getName() + " <" + bidder.getEmail() + "></p>");
		textBody.append("Email should be sent to " + bidder.getName() + " <" + bidder.getEmail() + ">.\n");

		htmlBody.append("</body></html>");
		
		Map<String, String> to = new LinkedHashMap<String, String>();
		to.put("grzegorz.nittner@gmail.com", "Greg Nittner");
		to.put("johnarleyburns@gmail.com", "John A. Burns");
		Map<String, String> cc = new LinkedHashMap<String, String>();
		Map<String, String> bcc = new LinkedHashMap<String, String>();
		sendEmail(to, cc, bcc, "Bid was rejected", textBody.toString(), htmlBody.toString());
	}

	public void sendNewBidForYourListingNotification(NotificationVO notification,
			BidVO bid, ListingVO listing, UserVO listingOwner, UserVO bidder) {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>New bid was placed for your listing</h2></p>");
		textBody.append("New bid was placed for your listing\n");

		htmlBody.append("<p><h2>" + "<a href=\"" + createLinkUrl(listing) + "\">" + listing.getName() + "</a>" + " posted by " + listingOwner.getName() + "</h2></p>");
		textBody.append("\n" + listing.getName() + " ( " + createLinkUrl(listing) + " )" + " posted by " + listingOwner.getName() + "\n");

		htmlBody.append("<p><h3>" + listing.getSummary() + "</h3></p>");
		textBody.append(listing.getSummary() + "\n");

		htmlBody.append("<p><h3>Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "</h3></p>");
		textBody.append("Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "\n");
		htmlBody.append("<p>Click to see " + "<a href=\"" + createLinkUrl(bid) + "\">" + "bid details" + "</a>" + "</p>");
		textBody.append("Click to see bid details " + createLinkUrl(listing) + "\n");		

		htmlBody.append("<p>Investor details: " + bidder.getName() + " <" + bidder.getEmail() + "></p>");
		textBody.append("Investor details: " + bidder.getName() + " <" + bidder.getEmail() + ">\n");

		htmlBody.append("<p>Email should be sent to " + listingOwner.getName() + " <" + listingOwner.getEmail() + "></p>");
		textBody.append("Email should be sent to " + listingOwner.getName() + " <" + listingOwner.getEmail() + ">.\n");

		htmlBody.append("</body></html>");
		
		Map<String, String> to = new LinkedHashMap<String, String>();
		to.put("grzegorz.nittner@gmail.com", "Greg Nittner");
		to.put("johnarleyburns@gmail.com", "John A. Burns");
		Map<String, String> cc = new LinkedHashMap<String, String>();
		Map<String, String> bcc = new LinkedHashMap<String, String>();
		sendEmail(to, cc, bcc, "New bid placed", textBody.toString(), htmlBody.toString());
	}

	public void sendAcceptedBidNotification(NotificationVO notification,
			BidVO bid, ListingVO listing, UserVO listingOwner, UserVO bidder) {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>You accepted bid</h2></p>");
		textBody.append("You accepted bid\n");

		htmlBody.append("<p><h2>" + "<a href=\"" + createLinkUrl(listing) + "\">" + listing.getName() + "</a>" + " posted by " + listingOwner.getName() + "</h2></p>");
		textBody.append("\n" + listing.getName() + " ( " + createLinkUrl(listing) + " )" + " posted by " + listingOwner.getName() + "\n");

		htmlBody.append("<p><h3>" + listing.getSummary() + "</h3></p>");
		textBody.append(listing.getSummary() + "\n");

		htmlBody.append("<p><h3>Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "</h3></p>");
		textBody.append("Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "\n");
		htmlBody.append("<p>Click to see " + "<a href=\"" + createLinkUrl(bid) + "\">" + "bid details" + "</a>" + "</p>");
		textBody.append("Click to see bid details " + createLinkUrl(listing) + "\n");		

		htmlBody.append("<p>Investor details: " + bidder.getName() + " <" + bidder.getEmail() + "></p>");
		textBody.append("Investor details: " + bidder.getName() + " <" + bidder.getEmail() + ">\n");

		htmlBody.append("<p>Email should be sent to " + listingOwner.getName() + " <" + listingOwner.getEmail() + "></p>");
		textBody.append("Email should be sent to " + listingOwner.getName() + " <" + listingOwner.getEmail() + ">.\n");

		htmlBody.append("</body></html>");
		
		Map<String, String> to = new LinkedHashMap<String, String>();
		to.put("grzegorz.nittner@gmail.com", "Greg Nittner");
		to.put("johnarleyburns@gmail.com", "John A. Burns");
		Map<String, String> cc = new LinkedHashMap<String, String>();
		Map<String, String> bcc = new LinkedHashMap<String, String>();
		sendEmail(to, cc, bcc, "You accepted bid", textBody.toString(), htmlBody.toString());
	}

	public void sendBidWithdrawnNotification(NotificationVO notification,
			BidVO bid, ListingVO listing, UserVO listingOwner, UserVO bidder) {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>Bid was withdrawn for your listing</h2></p>");
		textBody.append("Bid was withdrawn for your listing\n");

		htmlBody.append("<p><h2>" + "<a href=\"" + createLinkUrl(listing) + "\">" + listing.getName() + "</a>" + " posted by " + listingOwner.getName() + "</h2></p>");
		textBody.append("\n" + listing.getName() + " ( " + createLinkUrl(listing) + " )" + " posted by " + listingOwner.getName() + "\n");

		htmlBody.append("<p><h3>" + listing.getSummary() + "</h3></p>");
		textBody.append(listing.getSummary() + "\n");

		htmlBody.append("<p><h3>Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "</h3></p>");
		textBody.append("Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "\n");
		htmlBody.append("<p>Click to see " + "<a href=\"" + createLinkUrl(bid) + "\">" + "bid details" + "</a>" + "</p>");
		textBody.append("Click to see bid details " + createLinkUrl(listing) + "\n");		

		htmlBody.append("<p>Investor details: " + bidder.getName() + " <" + bidder.getEmail() + "></p>");
		textBody.append("Investor details: " + bidder.getName() + " <" + bidder.getEmail() + ">\n");

		htmlBody.append("<p>Email should be sent to " + listingOwner.getName() + " <" + listingOwner.getEmail() + "></p>");
		textBody.append("Email should be sent to " + listingOwner.getName() + " <" + listingOwner.getEmail() + ">.\n");

		htmlBody.append("</body></html>");
		
		Map<String, String> to = new LinkedHashMap<String, String>();
		to.put("grzegorz.nittner@gmail.com", "Greg Nittner");
		to.put("johnarleyburns@gmail.com", "John A. Burns");
		Map<String, String> cc = new LinkedHashMap<String, String>();
		Map<String, String> bcc = new LinkedHashMap<String, String>();
		sendEmail(to, cc, bcc, "Bid withdrawn", textBody.toString(), htmlBody.toString());
	}

	public void sendNewCommentForYourListingNotification(NotificationVO notification,
			CommentVO comment, ListingVO listing, UserVO commenter, UserVO listingOwner) {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>New comment posted for your listing</h2></p>");
		textBody.append("New comment posted for your listing\n");

		htmlBody.append("<p>Click to see " + "<a href=\"" + createLinkUrl(comment) + "\">" + "comment" + "</a>" + "</p>");
		textBody.append("Click to see comment " + createLinkUrl(comment) + "\n");		

		htmlBody.append("<p><h3>" + "<a href=\"" + createLinkUrl(listing) + "\">" + listing.getName() + "</a>" + "</h3></p>");
		textBody.append(listing.getName() + " ( " + createLinkUrl(listing) + " )" + "\n");

		htmlBody.append("<p>Email should be sent to " + listingOwner.getName() + " <" + listingOwner.getEmail() + "></p>");
		textBody.append("Email should be sent to " + listingOwner.getName() + " <" + listingOwner.getEmail() + ">.\n");

		htmlBody.append("</body></html>");
		
		Map<String, String> to = new LinkedHashMap<String, String>();
		to.put("grzegorz.nittner@gmail.com", "Greg Nittner");
		to.put("johnarleyburns@gmail.com", "John A. Burns");
		Map<String, String> cc = new LinkedHashMap<String, String>();
		Map<String, String> bcc = new LinkedHashMap<String, String>();
		sendEmail(to, cc, bcc, "New comment for your listing", textBody.toString(), htmlBody.toString());
	}

	public void sendNewCommentForMonitoredListingNotification(NotificationVO notification,
			UserVO monitoringUser, CommentVO comment, ListingVO listing, UserVO commenter, UserVO listingOwner) {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>New comment posted for monitored listing</h2></p>");
		textBody.append("New comment posted for monitored listing\n");

		htmlBody.append("<p>Click to see " + "<a href=\"" + createLinkUrl(comment) + "\">" + "comment" + "</a>" + "</p>");
		textBody.append("Click to see comment " + createLinkUrl(comment) + "\n");		

		htmlBody.append("<p><h3>" + "<a href=\"" + createLinkUrl(listing) + "\">" + listing.getName() + "</a>" + "</h3></p>");
		textBody.append(listing.getName() + " ( " + createLinkUrl(listing) + " )" + "\n");

		htmlBody.append("<p>Email should be sent to " + monitoringUser.getName() + " <" + monitoringUser.getEmail() + "></p>");
		textBody.append("Email should be sent to " + monitoringUser.getName() + " <" + monitoringUser.getEmail() + ">.\n");

		htmlBody.append("</body></html>");
		
		Map<String, String> to = new LinkedHashMap<String, String>();
		to.put("grzegorz.nittner@gmail.com", "Greg Nittner");
		to.put("johnarleyburns@gmail.com", "John A. Burns");
		Map<String, String> cc = new LinkedHashMap<String, String>();
		Map<String, String> bcc = new LinkedHashMap<String, String>();
		sendEmail(to, cc, bcc, "New comment for monitored listing", textBody.toString(), htmlBody.toString());
	}

	public void sendYourProfileWasModifiedNotification(NotificationVO notification, UserVO user) {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>Your profile was modified</h2></p>");
		textBody.append("Your profile was modified\n");

		htmlBody.append("<p><h3>" + "<a href=\"" + createLinkUrl(user) + "\">" + user.getName() + "</a>" + "</h3></p>");
		textBody.append(user.getName() + " ( " + createLinkUrl(user) + " )" + "\n");

		htmlBody.append("<p>Email should be sent to " + user.getName() + " <" + user.getEmail() + "></p>");
		textBody.append("Email should be sent to " + user.getName() + " <" + user.getEmail() + ">.\n");

		htmlBody.append("</body></html>");
		
		Map<String, String> to = new LinkedHashMap<String, String>();
		to.put("grzegorz.nittner@gmail.com", "Greg Nittner");
		to.put("johnarleyburns@gmail.com", "John A. Burns");
		Map<String, String> cc = new LinkedHashMap<String, String>();
		Map<String, String> bcc = new LinkedHashMap<String, String>();
		sendEmail(to, cc, bcc, "Your profile was modified", textBody.toString(), htmlBody.toString());
	}

	public void sendNewVoteForYourProfileNotification(NotificationVO notification, UserVO user) {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>New vote for your profile</h2></p>");
		textBody.append("New vote for your profile\n");

		htmlBody.append("<p><h3>" + "<a href=\"" + createLinkUrl(user) + "\">" + user.getName() + "</a>" + "</h3></p>");
		textBody.append(user.getName() + " ( " + createLinkUrl(user) + " )" + "\n");

		htmlBody.append("<p>Email should be sent to " + user.getName() + " <" + user.getEmail() + "></p>");
		textBody.append("Email should be sent to " + user.getName() + " <" + user.getEmail() + ">.\n");

		htmlBody.append("</body></html>");
		
		Map<String, String> to = new LinkedHashMap<String, String>();
		to.put("grzegorz.nittner@gmail.com", "Greg Nittner");
		to.put("johnarleyburns@gmail.com", "John A. Burns");
		Map<String, String> cc = new LinkedHashMap<String, String>();
		Map<String, String> bcc = new LinkedHashMap<String, String>();
		sendEmail(to, cc, bcc, "New vote for your profile", textBody.toString(), htmlBody.toString());
	}

	public void sendNewVoteForYourListingNotification(NotificationVO notification,
			ListingVO listing, UserVO listingOwner) {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>New vote for your listing</h2></p>");
		textBody.append("New vote for your listing\n");

		htmlBody.append("<p><h3>" + "<a href=\"" + createLinkUrl(listing) + "\">" + listing.getName() + "</a>" + "</h3></p>");
		textBody.append(listing.getName() + " ( " + createLinkUrl(listing) + " )" + "\n");

		htmlBody.append("<p>Email should be sent to " + listingOwner.getName() + " <" + listingOwner.getEmail() + "></p>");
		textBody.append("Email should be sent to " + listingOwner.getName() + " <" + listingOwner.getEmail() + ">.\n");

		htmlBody.append("</body></html>");
		
		Map<String, String> to = new LinkedHashMap<String, String>();
		to.put("grzegorz.nittner@gmail.com", "Greg Nittner");
		to.put("johnarleyburns@gmail.com", "John A. Burns");
		Map<String, String> cc = new LinkedHashMap<String, String>();
		Map<String, String> bcc = new LinkedHashMap<String, String>();
		sendEmail(to, cc, bcc, "New vote for your listing", textBody.toString(), htmlBody.toString());
	}

	public void sendNewListingNotification(NotificationVO notification,
			UserVO monitoringUser, ListingVO listing, UserVO listingOwner) {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>New listing</h2></p>");
		textBody.append("New listing\n");

		htmlBody.append("<p><h3>" + "<a href=\"" + createLinkUrl(listing) + "\">" + listing.getName() + "</a>" + "</h3></p>");
		textBody.append(listing.getName() + " ( " + createLinkUrl(listing) + " )" + "\n");

		htmlBody.append("<p>Email should be sent to " + monitoringUser.getName() + " <" + monitoringUser.getEmail() + "></p>");
		textBody.append("Email should be sent to " + monitoringUser.getName() + " <" + monitoringUser.getEmail() + ">.\n");

		htmlBody.append("</body></html>");
		
		Map<String, String> to = new LinkedHashMap<String, String>();
		to.put("grzegorz.nittner@gmail.com", "Greg Nittner");
		to.put("johnarleyburns@gmail.com", "John A. Burns");
		Map<String, String> cc = new LinkedHashMap<String, String>();
		Map<String, String> bcc = new LinkedHashMap<String, String>();
		sendEmail(to, cc, bcc, "New listing", textBody.toString(), htmlBody.toString());
	}
}
