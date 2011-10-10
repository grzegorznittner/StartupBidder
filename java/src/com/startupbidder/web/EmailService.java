package com.startupbidder.web;

import java.io.UnsupportedEncodingException;
import java.util.Formatter;
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

import com.startupbidder.vo.BidVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.UserVO;

/**
 * 
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
	

	public void sendAcceptedBidNotification (BidVO bid, ListingVO listing, UserVO listingOwner, UserVO bidder) {
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("admin@startupbidder.com", "StartupBidder.com Admin"));
            //msg.addRecipient(Message.RecipientType.TO,
            //                 new InternetAddress("johnarleyburns@gmail.com", "John A. Burns"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress("grzegorz.nittner@gmail.com", "Greg Nittner"));
            msg.setSubject("[StartupBidder.com] Congratulations on reaching a preliminary funding agreement!");
            
            prepareAcceptedBidNotification(msg, bid, listing, listingOwner, bidder);

            Transport.send(msg);
        } catch (AddressException e) {
            log.log(Level.SEVERE, "Wrong email address", e);
        } catch (MessagingException e) {
        	log.log(Level.SEVERE, "Error sending email", e);
        } catch (UnsupportedEncodingException e) {
        	log.log(Level.SEVERE, "Wrong encoding", e);
		}
	}
	
	private void prepareAcceptedBidNotification (Message msg, BidVO bid, ListingVO listing, UserVO listingOwner, UserVO bidder)
			throws MessagingException {
		StringBuffer htmlBody = new StringBuffer();
		StringBuffer textBody = new StringBuffer();
		
		htmlBody.append("<html><head></head><body>");
		
		htmlBody.append("<p><h2>Congratulations on reaching a preliminary funding agreement!</h2></p>");
		textBody.append("Congratulations on reaching a preliminary funding agreement!\n");

		htmlBody.append("<p><h2>" + listing.getName() + " posted by " + listingOwner.getName() + "</h2></p>");
		textBody.append("\n" + listing.getName() + " posted by " + listingOwner.getName() + "\n");

		htmlBody.append("<p><h3>" + listing.getSummary() + "</h3></p>");
		textBody.append(listing.getSummary() + "\n");

		htmlBody.append("<p><h3>Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "</h3></p>");
		textBody.append("Bid details: " + bid.getPercentOfCompany() + "% of the company for " + bid.getValue()
				+ ", " + bid.getFundType() + "\n");

		double fee = bid.getValue() * 0.005;
		htmlBody.append("<p><h3>Bid fee " + new Formatter().format ("%.0f", fee) + "(0.5% of funding amount) payable Net 30 by " + listingOwner.getName() + "</h3></p>");
		textBody.append("Bid fee " + new Formatter().format ("%.0f", fee) + "(0.5% of funding amount) payable Net 30 by " + listingOwner.getName() + "\n");

		htmlBody.append("<p>Listing owner details: " + listingOwner.getName() + " <" + listingOwner.getEmail() + "></p>");
		textBody.append("Listing owner details: " + listingOwner.getName() + " <" + listingOwner.getEmail() + ">\n");

		htmlBody.append("<p>Investor details: " + bidder.getName() + " <" + bidder.getEmail() + "></p>");
		textBody.append("Investor details: " + bidder.getName() + " <" + bidder.getEmail() + ">\n");

		htmlBody.append("<p>Dear <b>" + bidder.getName() + "</b>, please contact <b>" + listingOwner.getName() + "</b> to proceed with a formal funding agreement.</p>");
		textBody.append("Dear " + bidder.getName() + ", please contact " + listingOwner.getName() + " to proceed with a formal funding agreement.\n");

		htmlBody.append("</body></html>");
		
		Multipart mp = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlBody, "text/html");
        mp.addBodyPart(htmlPart);
        msg.setText(textBody.toString());
        //msg.setContent(mp);
	}
}
