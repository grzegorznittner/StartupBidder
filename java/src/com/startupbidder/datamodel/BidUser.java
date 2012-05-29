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
	public BidUser(Bid msg) {
		this.direction = msg.direction;
		this.read = msg.read;
		this.text = msg.text;
		this.userA = msg.userA;
		this.userAEmail = msg.userAEmail;
		this.userANickname = msg.userANickname;
		this.userB = msg.userB;
		this.userBEmail = msg.userBEmail;
		this.userBNickname = msg.userBNickname;
		this.created = msg.created;
	}

	public String toString() {
		return "BidUser: " + this.amount + " for " + this.percentage + "% " + this.type
				+ " "+ userANickname + (direction == Direction.A_TO_B ? " -> " : " <- ") + userBNickname;
	}
}
