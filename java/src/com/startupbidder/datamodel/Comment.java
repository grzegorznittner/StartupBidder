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
public class Comment extends BaseObject<Comment> {
	public Key<Comment> getKey() {
		return new Key<Comment>(Comment.class, id);
	}
	@Id public Long id;
	
	public boolean mockData;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	
	@Indexed public Key<Listing> listing;
	@Indexed public Key<SBUser> user;
	@Indexed public Date   commentedOn;
	@Indexed public Key<Comment> parent;
	public String userNickName;
	public String listingName;
	public String comment;

	public String getWebKey() {
		return new Key<Comment>(Comment.class, id).getString();
	}
}
