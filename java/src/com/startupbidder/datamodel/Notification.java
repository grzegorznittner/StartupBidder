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

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*30)
public class Notification extends BaseObject<Notification> {
	public static enum Type {NEW_BID_FOR_YOUR_LISTING, YOUR_BID_WAS_REJECTED, YOUR_BID_WAS_COUNTERED,
		YOUR_BID_WAS_ACCEPTED, YOU_ACCEPTED_BID, YOU_PAID_BID, BID_PAID_FOR_YOUR_LISTING, BID_WAS_WITHDRAWN,
		NEW_LISTING, LISTING_ACTIVATED, LISTING_FROZEN, LISTING_WITHDRAWN, LISTING_SENT_BACK,
		NEW_COMMENT_FOR_YOUR_LISTING, NEW_COMMENT_FOR_MONITORED_LISTING,
		PRIVATE_MESSAGE, ASK_LISTING_OWNER};

	public Notification() {
	}
	public Notification(Listing listing, SBUser listingOwner) {
		this.listing = listing.getKey();
		this.listingName = listing.name;
		this.listingOwnerUser = listingOwner.getKey();
		this.listingOwner = listingOwner.nickname;
		this.listingCategory = listing.category;
		this.listingMantra = listing.mantra;
		this.listingBriefAddress = listing.briefAddress;
		this.created = new Date();
		this.read = false;
	}
	public Key<Notification> getKey() {
		return new Key<Notification>(Notification.class, id);
	}
	@Id public Long id;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	
	@Indexed public Key<SBUser> user;
    public String userNickname;
	public String userEmail;
	public String fromUserNickname;
	public Key<SBUser> investor;
	@Indexed public Type type;
		
	@Indexed public Key<Listing> listing;
	public String listingName;
	public Key<SBUser> listingOwnerUser;
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
	public Notification copy() {
		Notification newNotif = new Notification();
		newNotif.id = this.id;
		newNotif.created = this.created;
		newNotif.listing = this.listing;
		newNotif.listingBriefAddress = this.listingBriefAddress;
		newNotif.listingCategory = this.listingCategory;
		newNotif.listingMantra = this.listingMantra;
		newNotif.listingName = this.listingName;
		newNotif.listingOwner = this.listingOwner;
		newNotif.listingOwnerUser = this.listingOwnerUser;
		newNotif.message = this.message;
		newNotif.read = this.read;
		newNotif.sentDate = this.sentDate;
		newNotif.type = this.type;
		newNotif.user = this.user;
		newNotif.userEmail = this.userEmail;
		newNotif.userNickname = this.userNickname;
		return newNotif;
	}
	public String getTargetLink() {
		String link = "";
		if (type == null) {
			return "/company-page.html?id=" + this.listing.getString();
		}
		switch (type) {
		case BID_PAID_FOR_YOUR_LISTING:
		case BID_WAS_WITHDRAWN:
		case YOUR_BID_WAS_ACCEPTED:
		case YOUR_BID_WAS_COUNTERED:
		case YOUR_BID_WAS_REJECTED:
		case NEW_BID_FOR_YOUR_LISTING:
		case YOU_PAID_BID:
		case YOU_ACCEPTED_BID:
			if (listingOwnerUser.getId() == user.getId()) {
				if (this.investor != null) {
					link = "/company-owner-investor-bids-page.html?id=" + this.listing.getString() + "&investor_id=" + this.investor.getString();
				} else {
					// workaround for old notifications without investor field
					link = "/company-owner-bids-page.html?id=" + this.listing.getString();
				}
			} else {
				link = "/company-investor-bids-page.html?id=" + this.listing.getString();
			}
		break;
		case NEW_COMMENT_FOR_YOUR_LISTING:
		case NEW_COMMENT_FOR_MONITORED_LISTING:
			// link to comment
			link = "/company-comments-page.html?id=" + this.listing.getString();
		break;
		case LISTING_ACTIVATED:
		case LISTING_FROZEN:
		case LISTING_WITHDRAWN:
		case LISTING_SENT_BACK:
		case NEW_LISTING:
			// link to listing
			link = "/company-page.html?id=" + this.listing.getString();
		break;
		case ASK_LISTING_OWNER:
			link = "/company-questions-page.html?id=" + this.listing.getString();
		break;
		case PRIVATE_MESSAGE:
			link = "/message-group-page.html";
		break;
		default:
			link = "not_recognized";
		}
		return link;
	}
}
