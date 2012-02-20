/**
 * 
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
public class Listing extends BaseObject implements Monitor.Monitored {
	public enum State {NEW, POSTED, ACTIVE, CLOSED, WITHDRAWN, FROZEN};

	@Id public Long id;
	
	public boolean mockData;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	@PrePersist void updateSuggestedValuation() {
		if (suggestedPercentage == 0) {
			this.suggestedValuation = 0;
		} else {
			this.suggestedValuation = suggestedAmount * 100 / suggestedPercentage;
		}
	}
	
	@Indexed public Key<SBUser> owner;
	
	public String name;
	public String summary;
	
	@Indexed public Date  created;
	@Indexed public Date  posted;
	@Indexed public Date  listedOn;
	@Indexed public Date  closingOn;
	@Indexed public State state = State.NEW;
	
	public int   suggestedValuation;
	public int   suggestedPercentage;
	public int   suggestedAmount;
	public Key<ListingDoc> businessPlanId;
	public Key<ListingDoc> presentationId;
	public Key<ListingDoc> financialsId;
	
	public String getWebKey() {
		return new Key<Listing>(Listing.class, id).getString();
	}
}
