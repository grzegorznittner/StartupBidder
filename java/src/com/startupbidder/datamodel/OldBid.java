/**
 * 
 */
package com.startupbidder.datamodel;

import java.util.Comparator;
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
public class OldBid extends BaseObject<OldBid> {
	public enum FundType {SYNDICATE, SOLE_INVESTOR, COMMON, PREFERRED, NOTE};

	public enum Action { ACTIVATE, UPDATE, CANCEL, ACCEPT};
	public enum Actor { OWNER, BIDDER };

	public Key<OldBid> getKey() {
		return new Key<OldBid>(OldBid.class, id);
	}
	@Id public Long id;
	
	public boolean mockData;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	@PrePersist void setValuation() {
		if (percentOfCompany == 0) {
			this.valuation = 0;
		} else {
			this.valuation = value * 100 / percentOfCompany;
		}
	}
	
	@Indexed public Key<SBUser> bidder;
	@Indexed public Key<Listing> listing;
	@Indexed public Key<SBUser> listingOwner;
	@Indexed public Action action = Action.ACTIVATE;
	@Indexed public Actor actor = Actor.BIDDER;

	public String listingName;
	public String bidderName;

	@Indexed public Date   placed;
	public Date expires;
	public int value;
	public int percentOfCompany;
	public int valuation;
	public FundType fundType;
	public int interestRate;

	public String comment;

	public String getWebKey() {
		return new Key<OldBid>(OldBid.class, id).getString();
	}
	
	public static class PlacedComparator implements Comparator<OldBid> {
		@Override
		public int compare(OldBid o1, OldBid o2) {
			if (o1.placed != null && o2.placed != null) {
				if (o1.placed.getTime() < o2.placed.getTime()) {
					return -1;
				} else if (o1.placed.getTime() > o2.placed.getTime()) {
					return 1;
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		}
		
	}
}

