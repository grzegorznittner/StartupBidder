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
import com.googlecode.objectify.condition.IfNotNull;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*30)
public class Listing extends BaseObject implements Monitor.Monitored {
	/**
	 * NEW - just created, not submited by user
	 * POSTED - submited by user, passed simple verfication, needs to be checked by Admins to be available on website
	 * ACTIVE - verified by admins, available on website
	 * WITHDRAWN - discarded by owner, still available on website if you have direct link, but users cannot post it
	 * FROZEN - only admins can do that, it's done when listing needs an action from admins (eg. law violation)
	 * CLOSED - automatic action done after certain time or when bid was accepted
	 */
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
	public String description;
	public String summary;
	public String website;
	
	/** Category of the listing */
	public Key<Category> category;
	
	@Indexed public Date  created;
	@Indexed public Date  posted;
	@Indexed public Date  listedOn;
	@Indexed public Date  closingOn;
	@Indexed public State state = State.NEW;
	
	// address parts
	@Indexed public String country;
	@Indexed(IfNotNull.class) public String usState;
	@Indexed(IfNotNull.class) public String usCounty;
	@Indexed public String city;
	public String address;
	
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
