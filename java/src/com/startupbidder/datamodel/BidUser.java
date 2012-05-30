/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.datamodel;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*30)
public class BidUser extends Bid {
	public int counter = 1;
	
	public BidUser() {
	}
	public BidUser(Bid bid) {
		this.listing = bid.listing;
		this.direction = bid.direction;
		this.read = bid.read;
		this.text = bid.text;
		this.userA = bid.userA;
		this.userAEmail = bid.userAEmail;
		this.userANickname = bid.userANickname;
		this.userB = bid.userB;
		this.userBEmail = bid.userBEmail;
		this.userBNickname = bid.userBNickname;
		this.created = bid.created;
		this.type = bid.type;
		this.amount = bid.amount;
		this.percentage = bid.percentage;
		this.value = bid.value;
	}

	public String toString() {
		return "BidUser: " + this.amount + " for " + this.percentage + "% " + this.type
				+ " "+ userANickname + (direction == Direction.A_TO_B ? " -> " : " <- ") + userBNickname;
	}
}
