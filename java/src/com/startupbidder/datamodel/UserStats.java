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
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*30*2)
public class UserStats extends BaseObject<UserStats> {
	public Key<UserStats> getKey() {
		return new Key<UserStats>(UserStats.class, id);
	}
	// long value of id for UserStats is the same as SBUser's one 
	@Id public Long id;
	
	public boolean mockData;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	
	@Indexed public Key<SBUser> user;
	@Indexed public SBUser.Status status;

	public long numberOfComments;
	public long numberOfBids;
	public long numberOfAcceptedBids;
	public long numberOfFundedBids;
	public long numberOfRejectedBids;
	public long numberOfListings;
	public long numberOfVotes;
	public long numberOfVotesAdded;
	public long sumOfBids;
	public long sumOfAcceptedBids;
	public long sumOfFundedBids;
	public long numberOfNotifications;

	public String getWebKey() {
		return new Key<UserStats>(UserStats.class, id).getString();
	}
}
