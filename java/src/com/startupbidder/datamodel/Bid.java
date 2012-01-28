/**
 * 
 */
package com.startupbidder.datamodel;

import java.util.Date;

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
public class Bid extends BaseObject implements Monitor.Monitored {
	public enum FundType {SYNDICATE, SOLE_INVESTOR, COMMON, PREFERRED, NOTE};
	/**
	 * Bid flow: POSTED -> ACTIVE or REJECTED -> ACCEPTED -> PAID
	 * Apart from that bid can be always marked as REJECTED or WITHDRAWN
	 */
	public enum Status { POSTED, ACTIVE, WITHDRAWN, REJECTED, ACCEPTED, PAID};

	@Indexed public Key<SBUser> bidder;
	@Indexed public Key<Listing> listing;
	@Indexed public Key<SBUser> listingOwner;
	@Indexed public Status status = Status.POSTED;
	
	@Indexed public Date   placed;
	public int    value;
	public int    percentOfCompany;
	@Indexed public int    valuation;
	public FundType fundType;
	public int interestRate;

	public String comment;

	public String getWebKey() {
		return new Key<Bid>(Bid.class, id).getString();
	}

}
