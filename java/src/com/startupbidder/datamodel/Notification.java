/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.datamodel;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.PrePersist;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfNotNull;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*30)
public class Notification extends BaseObject {
	public static enum Type {NEW_BID_FOR_YOUR_LISTING, YOUR_BID_WAS_REJECTED, YOUR_BID_WAS_COUNTERED,
		YOUR_BID_WAS_ACCEPTED, YOU_ACCEPTED_BID, YOU_PAID_BID, BID_PAID_FOR_YOUR_LISTING, BID_WAS_WITHDRAWN,
		NEW_COMMENT_FOR_YOUR_LISTING, NEW_COMMENT_FOR_MONITORED_LISTING, NEW_LISTING,
		PRIVATE_MESSAGE, ASK_LISTING_OWNER};

	@Id public Long id;
	/** All messages in the same conversations have the same context equal to id of first message */
	@Indexed public long context;
	
	public boolean mockData;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	
	@Indexed public Key<SBUser> user;
    public String userNickname;
	public String userEmail;
	@Indexed(IfNotNull.class) public Key<SBUser> fromUser;
	public String fromUserNickname;
	public String fromUserEmail;
    @Indexed(IfNotNull.class) public Key<Notification> parentNotification;
	@Indexed public Type type;
	@Indexed public Key<Listing> listing;
	public String listingName;
	public String listingOwner;
	public String listingCategory;
	public String listingBriefAddress;
	public String listingMantra;
	
	public String message;
	
	@Indexed public Date created;
	public Date   sentDate;
	@Indexed public boolean read = false;

	public String getWebKey() {
		return new Key<Notification>(Notification.class, id).getString();
	}

	public String getTargetLink() {
		String link = "";
		switch (type) {
		case BID_PAID_FOR_YOUR_LISTING:
		case BID_WAS_WITHDRAWN:
		case YOUR_BID_WAS_ACCEPTED:
		case YOUR_BID_WAS_COUNTERED:
		case YOUR_BID_WAS_REJECTED:
		case NEW_BID_FOR_YOUR_LISTING:
		case YOU_PAID_BID:
		case YOU_ACCEPTED_BID:
			// link to bid
			link = "/company-page.html?page=bids&id=" + this.listing.getString();
		break;
		case NEW_COMMENT_FOR_YOUR_LISTING:
		case NEW_COMMENT_FOR_MONITORED_LISTING:
			// link to comment
			link = "/company-page.html?page=comments&id=" + this.listing.getString();
		break;
		case NEW_LISTING:
			// link to listing
			link = "/company-page.html?id=" + this.listing.getString();
		break;
		default:
			link = "not_recognized";
		}
		return link;
	}
}
