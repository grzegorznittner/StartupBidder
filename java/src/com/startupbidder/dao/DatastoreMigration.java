/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.QueryResultIterable;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.web.ListingFacade;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class DatastoreMigration {
	private static Objectify getOfy() {
		Objectify ofy = ObjectifyService.begin();
		return ofy;
	}
	
	public static String associateImages() {
		StringBuffer report = new StringBuffer();
		report.append("Listing's migration:<br/>\n<ul>\n");
		String dataPath = MockDataBuilder.getTestDataPath() + "pics/";
		
		Map<String, String[]> picMap = new HashMap<String, String[]>();
		picMap.put("Semantic Search", new String[] {dataPath + "semantic_1.jpg", dataPath + "semantic_2.jpg"});
		picMap.put("Local news sites", new String[] {dataPath + "localnews_1.jpg", dataPath + "localnews_2.jpg"});
		picMap.put("MisLead", new String[] {dataPath + "mislead_1.jpg", dataPath + "mislead_2.jpg"});
		picMap.put("Micropayments", new String[] {dataPath + "micropayments_1.jpg", dataPath + "micropayments_2.png", dataPath + "micropayments_3.png", dataPath + "micropayments_4.jpg"});
		picMap.put("Flight twitter", new String[] {dataPath + "flight_1.jpg", dataPath + "flight_2.jpg"});
		picMap.put("Better company car", new String[] {dataPath + "car_1.jpg", dataPath + "car_2.jpg", dataPath + "car_3.jpg", dataPath + "car_4.jpg", dataPath + "car_5.jpg"});
		picMap.put("On-the-fly conference", new String[] {dataPath + "conference_1.jpg", dataPath + "conference_2.jpg", dataPath + "conference_3.jpg"});
		picMap.put("Computer Training Camp", new String[] {dataPath + "training_1.jpg", dataPath + "training_2.jpg", dataPath + "training_3.jpg"});
		picMap.put("Village rainwater", new String[] {dataPath + "village_1.jpg", dataPath + "village_2.jpg", dataPath + "village_3.jpg"});
		picMap.put("De Vegetarische Slager", new String[] {dataPath + "vegetarian_1.jpg", dataPath + "vegetarian_2.jpg", dataPath + "vegetarian_3.jpg"});
		picMap.put("Social recommendations", new String[] {dataPath + "recommendations_1.png", dataPath + "recommendations_2.jpg", dataPath + "recommendations_3.jpg", dataPath + "recommendations_4.jpg"});
		picMap.put("Panty by Post", new String[] {dataPath + "panty_1.jpg", dataPath + "panty_2.jpg"});
		picMap.put("Computer Upgrading Service", new String[] {dataPath + "computer_1.jpg", dataPath + "computer_2.jpg"});
		
		QueryResultIterable<Key<Listing>> l = getOfy().query(Listing.class).fetchKeys();
		Map<Key<Listing>, Listing> listings = getOfy().get(l);
		
		for (Listing listing : listings.values()) {
			if (listing.name != null && picMap.containsKey(listing.name.trim())) {
				// import pictures
				String images[] = picMap.get(listing.name.trim());
				report.append("<li>updating listing '" + listing.name + "' with images: ");
				for (String image : images) {
					report.append(image + ", ");
				}
				ListingFacade.instance().updateMockListingPictures(listing, images);
			}
		}
		
		report.append("<br/>\n</ul>\n");
		return report.toString();
	}
	
	public static String migrate201207051222_to_current() {
		StringBuffer report = new StringBuffer();
		
		/* migrating SBUser
		 * old users get notifyEnabled=false
		 * new users get notifyEnabled=true
		 */
		Calendar listingUpdateDate = Calendar.getInstance();
		listingUpdateDate.set(2012, 7, 5);
		report.append("SBUser's migration:<br/>\n<ul>\n");
		QueryResultIterable<Key<SBUser>> u = getOfy().query(SBUser.class).fetchKeys();
		Map<Key<SBUser>, SBUser> users = getOfy().get(u);
		List<SBUser> userMigration = new ArrayList<SBUser>();
		for (SBUser user : users.values()) {
			if ("grzegorz.nittner@gmail.com".equalsIgnoreCase(user.email)
					|| "johnarleyburns@gmail.com".equalsIgnoreCase(user.email)
					|| "johnbettiol@gmail.com".equalsIgnoreCase(user.email)) {
				report.append("<li>enabling notifications for " + user.nickname);
				user.notifyEnabled = true;
			} else if (user.joined != null && user.joined.after(listingUpdateDate.getTime())) {
				report.append("<li>enabling notifications for " + user.nickname + " - new user");
				user.notifyEnabled = true;
			} else {
				report.append("<li>disabling notifications for " + user.nickname + " - old user");
				user.notifyEnabled = false;
			}
			userMigration.add(user);
		}
		getOfy().put(userMigration);
		report.append("<br/>\n</ul>\n");
		
		return report.toString();
	}

	
	public static String migrate201205101249_to_20120620() {
		StringBuffer report = new StringBuffer();
		
		/* migrating SBUser
		 * added property nicknammeLower
		 */
		report.append("SBUser's migration:<br/>\n<ul>\n");
		QueryResultIterable<Key<SBUser>> u = getOfy().query(SBUser.class).fetchKeys();
		Map<Key<SBUser>, SBUser> users = getOfy().get(u);
		List<SBUser> userMigration = new ArrayList<SBUser>();
		for (SBUser user : users.values()) {
			if (user.nicknameLower == null) {
				user.genNicknameLower();
				report.append("<li>setting nicknameLower: " + user.nicknameLower);
				userMigration.add(user);
			} else {
				report.append("<li>not updating: " + user.nicknameLower);
			}
		}
		getOfy().put(userMigration);
		report.append("<br/>\n</ul>\n");
		
		/* migrating Listing
		 * added properties: pic1Id, pic2Id, pic3Id, pic4Id, pic5Id
		 * nothing to do
		 */
		QueryResultIterable<Key<Listing>> l = getOfy().query(Listing.class).fetchKeys();
		Map<Key<Listing>, Listing> listings = getOfy().get(l);
		
		/* migrating Notification
		 * removed: context, mockData, fromUser, fromUserEmail, parentNotification
		 * added: listingOwnerUser
		 */
		report.append("Notification's migration:<br/>\n<ul>\n");
		QueryResultIterable<Key<Notification>> n = getOfy().query(Notification.class).fetchKeys();
		Map<Key<Notification>, Notification> notifications = getOfy().get(n);
		List<Notification> notifMigration = new ArrayList<Notification>();
		for (Notification notif : notifications.values()) {
			if (notif.listingOwnerUser == null && notif.listing != null) {
				Listing listing = listings.get(notif.listing);
				if (listing != null) {
					report.append("<li>updating listingOwnerUser: " + listing.owner);
					notif.listingOwnerUser = listing.owner;
					notifMigration.add(notif);
				} else {
					report.append("<li>can't find listing " + notif.listing + " for " + notif.getKey());
				}
			} else {
				report.append("<li>skip update " + notif.getKey());
			}
		}
		getOfy().put(notifMigration);
		report.append("<br/>\n</ul>\n");
		
		/* migrating Monitor
		 * added: userEmail, userNickname
		 */
		report.append("Monitor migration:<br/>\n<ul>\n");
		QueryResultIterable<Key<Monitor>> m = getOfy().query(Monitor.class).fetchKeys();
		Map<Key<Monitor>, Monitor> monitors = getOfy().get(m);
		List<Monitor> monitorMigration = new ArrayList<Monitor>();
		for (Monitor monitor : monitors.values()) {
			if (monitor.userEmail == null || monitor.userNickname == null) {
				SBUser user = users.get(monitor.user);
				if (user != null) {
					report.append("<li>updating userEmail: " + user.email + ", userNickname: " + user.nickname);
					monitor.userEmail = user.email;
					monitor.userNickname = user.nickname;
					monitorMigration.add(monitor);
				} else {
					report.append("<li>can't find user " + monitor.user);
				}
			} else {
				report.append("<li>skip update " + monitor.getKey());
			}
		}
		getOfy().put(monitorMigration);
		report.append("<br/>\n</ul>\n");
		
		/* migrating Comment
		 * added: listingName
		 */
		report.append("Comment's migration:<br/>\n<ul>\n");
		QueryResultIterable<Key<Comment>> c = getOfy().query(Comment.class).fetchKeys();
		Map<Key<Comment>, Comment> comments = getOfy().get(c);
		List<Comment> commentMigration = new ArrayList<Comment>();
		for (Comment comment : comments.values()) {
			if (comment.listingName == null) {
				Listing listing = listings.get(comment.listing);
				if (listing != null) {
					report.append("<li>updating listingName: " + listing.name);
					comment.listingName = listing.name;
					commentMigration.add(comment);
				} else {
					report.append("<li>can't find listing " + comment.listing);
				}
			} else {
				report.append("<li>skip update " + comment.getKey());
			}
		}
		getOfy().put(commentMigration);
		report.append("<br/>\n</ul>\n");
		
		// we need to delete Bids
		/* migrating Comment
		 * added: listingName
		 */
		report.append("Bid's migration:<br/>\n<ul>\n");
		Calendar bidDeletionDate = Calendar.getInstance();
		bidDeletionDate.set(2012, 6, 10);
		QueryResultIterable<Key<Bid>> b = getOfy().query(Bid.class).fetchKeys();
		Map<Key<Bid>, Bid> bids = getOfy().get(b);
		List<Bid> bidMigration = new ArrayList<Bid>();
		for (Bid bid : bids.values()) {
			if (bidDeletionDate.after(bid.modified)) {
				bidMigration.add(bid);
				report.append("<li>deleting: " + bid);
			} else {
				report.append("<li>skip update: " + bid);
			}
		}
		getOfy().delete(bidMigration);
		report.append("<br/>\n</ul>\n");
		
		return report.toString();
	}
}
