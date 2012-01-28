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
public class PaidBid extends Bid {
	@Indexed public Key<Bid> bid;
	
	public PaidBid() {
	}
	
	public PaidBid(Bid bid) {
		this.bidder = bid.bidder;
		this.comment = bid.comment;
		this.fundType = bid.fundType;
		this.interestRate = bid.interestRate;
		this.listing = bid.listing;
		this.listingOwner = bid.listingOwner;
		this.mockData = bid.mockData;
		this.percentOfCompany = bid.percentOfCompany;
		this.placed = bid.placed;
		this.status = bid.status;
		this.valuation = bid.valuation;
		this.value = bid.value;
		this.bid = new Key<Bid>(Bid.class, bid.id);
	}

	public String getWebKey() {
		return new Key<PaidBid>(PaidBid.class, id).getString();
	}
}
