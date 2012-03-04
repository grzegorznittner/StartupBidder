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
public class Notification extends BaseObject {
	public static enum Type {NEW_BID_FOR_YOUR_LISTING, YOUR_BID_WAS_REJECTED, YOUR_BID_WAS_COUNTERED,
		YOUR_BID_WAS_ACCEPTED, YOU_ACCEPTED_BID, YOU_PAID_BID, BID_PAID_FOR_YOUR_LISTING, BID_WAS_WITHDRAWN,
		NEW_VOTE_FOR_YOU, NEW_COMMENT_FOR_YOUR_LISTING, NEW_COMMENT_FOR_MONITORED_LISTING,
		YOUR_PROFILE_WAS_MODIFIED, NEW_VOTE_FOR_YOUR_LISTING, NEW_LISTING};

	@Id public Long id;
	
	public boolean mockData;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
		
	@Indexed public Key<SBUser> user;
	@Indexed public Type type;
	public Key<BaseObject> object;
	public String message;
	public Date   created;
	public Date   emailDate;
	@Indexed public boolean acknowledged = false;

	public String getWebKey() {
		return new Key<Notification>(Notification.class, id).getString();
	}
}
