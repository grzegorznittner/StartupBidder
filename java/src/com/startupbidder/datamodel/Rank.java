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
import com.googlecode.objectify.annotation.Unindexed;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*30)
public class Rank extends BaseObject<Rank> {
	public Key<Rank> getKey() {
		return new Key<Rank>(Rank.class, id);
	}
	@Id public Long id;
	
	public boolean mockData;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	
	public Key<Listing> listing;
	public Key<SBUser> user;
	public Date date;
	public float rank;

	public String getWebKey() {
		return new Key<Monitor>(Monitor.class, id).getString();
	}
}
