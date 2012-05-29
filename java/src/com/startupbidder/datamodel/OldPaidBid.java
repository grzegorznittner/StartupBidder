/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.datamodel;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*30)
public class OldPaidBid extends OldBid {
	@Indexed public Key<OldBid> bid;
	
	public OldPaidBid() {
	}
	
	public OldPaidBid(OldBid bid) {
		this.bidder = bid.bidder;
		this.comment = bid.comment;
		this.fundType = bid.fundType;
		this.interestRate = bid.interestRate;
		this.listing = bid.listing;
		this.listingOwner = bid.listingOwner;
		this.mockData = bid.mockData;
		this.percentOfCompany = bid.percentOfCompany;
		this.placed = bid.placed;
		this.action = bid.action;
		this.valuation = bid.valuation;
		this.value = bid.value;
		this.bid = new Key<OldBid>(OldBid.class, bid.id);
	}

	public String getWebKey() {
		return new Key<OldPaidBid>(OldPaidBid.class, id).getString();
	}
}
