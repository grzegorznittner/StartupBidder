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
@Indexed
@Entity
@Cached(expirationSeconds=60*30)
public class ListingStats extends BaseObject<ListingStats> {
	public Key<ListingStats> getKey() {
		return new Key<ListingStats>(ListingStats.class, id);
	}
	@Id public Long id;
	
	@Unindexed public boolean mockData;
	
	@Unindexed public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	
	public Key<Listing> listing;
	public Listing.State state;
	public Listing.Type type;
	public String platform;
	
	public Date created;

    public boolean askedForFunding;
    
	public Listing.Currency currency;
	public double valuation;
	public double previousValuation;
	public Date previousValuationDate;
	public double medianValuation;
	public long numberOfComments;
	public long numberOfBids;
	public long numberOfMonitors;
    public long numberOfQuestions;
    public long numberOfMessages;
	public double score;
	
	public String getWebKey() {
		return new Key<ListingStats>(ListingStats.class, id).getString();
	}
}
