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
import com.googlecode.objectify.condition.IfTrue;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*30)
public class Notification extends BaseObject<Notification> {
	public static enum Direction {A_TO_B, B_TO_A};
	public static enum Type {NEW_BID_FOR_YOUR_LISTING, YOUR_BID_WAS_REJECTED, YOUR_BID_WAS_COUNTERED,
		YOUR_BID_WAS_ACCEPTED, YOU_ACCEPTED_BID, YOU_PAID_BID, BID_PAID_FOR_YOUR_LISTING, BID_WAS_WITHDRAWN,
		NEW_COMMENT_FOR_YOUR_LISTING, NEW_COMMENT_FOR_MONITORED_LISTING, NEW_LISTING,
		PRIVATE_MESSAGE, ASK_LISTING_OWNER};

	public Key<Notification> getKey() {
		return new Key<Notification>(Notification.class, id);
	}
	@Id public Long id;
	public boolean mockData;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	
	@Indexed public Key<SBUser> userA;
    public String userANickname;
	public String userAEmail;
	public Key<SBUser> userB;
    public String userBNickname;
	public String userBEmail;
	@Indexed public Direction direction;
	@Indexed public Type type;
	
	/** All messages in the same conversations have the same context equal to id of first message */
	@Indexed public long context;
	public Key<Notification> parentNotification;
	@Indexed public boolean replied;
	@Indexed(IfTrue.class) public boolean display;
	
	@Indexed public Key<Listing> listing;
	public String listingName;
	public String listingOwner;
	public String listingCategory;
	public String listingBriefAddress;
	public String listingMantra;
	
	public String message;
	public String question;
	public Date questionDate;
	
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
	
	/**
	 * New notification structure:
	 *  - userA, userB - notification sides
	 *  - direction (A_TO_B, B_TO_A) - notification direction
	 *  - replied (true, false) - marked true when there is at least one reply to the original message
	 *  
	 *  user1 - listing1 owner
	 *  user2, user3 - other users
	 *  
	 *     listing,  userA, userB, direction, replied
	 *  1. user2 asks user1 a question about listing1
	 *   * listing1, user1, user2, B_TO_A, false, ASK_LISTING_OWNER
	 *   * listing1, user2, user1, A_TO_B, false, ASK_LISTING_OWNER
	 *  
	 *  2. user1 replies to user2's message
	 *     listing1, user1, user2, B_TO_A, true, ASK_LISTING_OWNER  <- we've updated replied
	 *     listing1, user2, user1, A_TO_B, true, ASK_LISTING_OWNER  <- we've updated replied
	 *   * listing1, user2, user1, B_TO_A, true, ASK_LISTING_OWNER
	 *   * listing1, user1, user2, A_TO_B, true, ASK_LISTING_OWNER
	 *     
	 *  3. user3 asks user1 a question about listing1
	 *     listing1, user1, user2, B_TO_A, true, ASK_LISTING_OWNER
	 *     listing1, user2, user1, A_TO_B, true, ASK_LISTING_OWNER
	 *     listing1, user2, user1, B_TO_A, true, ASK_LISTING_OWNER
	 *     listing1, user1, user2, A_TO_B, true, ASK_LISTING_OWNER
	 *   * listing1, user1, user3, B_TO_A, false, ASK_LISTING_OWNER
	 *   * listing1, user3, user1, A_TO_B, false, ASK_LISTING_OWNER
	 *   
	 *  4. user2 replies to user1 message sent in point 2
	 *     listing1, user1, user2, B_TO_A, true, ASK_LISTING_OWNER
	 *     listing1, user2, user1, A_TO_B, true, ASK_LISTING_OWNER
	 *     listing1, user2, user1, B_TO_A, true, ASK_LISTING_OWNER
	 *     listing1, user1, user2, A_TO_B, true, ASK_LISTING_OWNER
	 *     listing1, user1, user3, B_TO_A, false, ASK_LISTING_OWNER
	 *     listing1, user3, user1, A_TO_B, false, ASK_LISTING_OWNER
	 *     listing1, user1, user2, B_TO_A, true, ASK_LISTING_OWNER
	 *     listing1, user2, user1, A_TO_B, true, ASK_LISTING_OWNER
	 *     
	 *  Query scenarios:
	 *  1. Public listing q&a:  listing=listing1, direction=A_TO_B, replied=true
	 *     listing1, user2, user1, A_TO_B, true, ASK_LISTING_OWNER
	 *     listing1, user1, user2, A_TO_B, true, ASK_LISTING_OWNER
	 *     listing1, user2, user1, A_TO_B, true, ASK_LISTING_OWNER
	 *  
	 *  2. Listing's q&a, owner's view: listing=listing1, userA=user1
	 *     listing1, user1, user2, B_TO_A, true, ASK_LISTING_OWNER
	 *     listing1, user1, user2, A_TO_B, true, ASK_LISTING_OWNER
	 *     listing1, user1, user2, B_TO_A, true, ASK_LISTING_OWNER
	 *     listing1, user1, user3, B_TO_A, false, ASK_LISTING_OWNER
	 *  
	 *  3. Listing's q&a, user2 view: listing=listing1, userA=user2
	 *     listing1, user2, user1, A_TO_B, true, ASK_LISTING_OWNER
	 *     listing1, user2, user1, B_TO_A, true, ASK_LISTING_OWNER
	 *     listing1, user2, user1, A_TO_B, true, ASK_LISTING_OWNER
	 *  
	 *  4. Listing's q&a, user3 view: listing=listing1, userA=user3
	 *     listing1, user3, user1, A_TO_B, false, ASK_LISTING_OWNER
	 */
}
