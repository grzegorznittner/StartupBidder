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
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*30)
public class Bid extends BaseObject<Bid> {
	public static enum Direction {A_TO_B, B_TO_A};
	public static enum Type {INVESTOR_POST, INVESTOR_COUNTER, INVESTOR_ACCEPT, INVESTOR_REJECT,
		INVESTOR_WITHDRAW, OWNER_ACCEPT, OWNER_REJECT, OWNER_COUNTER, OWNER_WITHDRAW};
	public Bid () {
	}
	public Bid(SBUser toUser, SBUser fromUser, String text, int amount, int percentage, int value, Type type) {
		this.created = new Date();
		this.read = false;
		this.text = text;
		this.direction = Bid.Direction.A_TO_B;
		this.userA = fromUser.getKey();
		this.userAEmail = fromUser.email;
		this.userANickname = fromUser.nickname;
		this.userB = toUser.getKey();
		this.userBEmail = toUser.email;
		this.userBNickname = toUser.nickname;
		this.amount = amount;
		this.percentage = percentage;
		this.value = value;
		this.type = type;
	}
	public Key<Bid> getKey() {
		return new Key<Bid>(Bid.class, id);
	}
	@Id public Long id;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	
	@Indexed public Key<Listing> listing;
	@Indexed public Key<SBUser> userA;
    public String userANickname;
	public String userAEmail;
	@Indexed public Key<SBUser> userB;
    public String userBNickname;
	public String userBEmail;
	public Direction direction;
	
	@Indexed public Date   created;
	public String text;
	public boolean read;
	
	public int amount;
	public int percentage;
	public int value;
	public Type type;

	public Bid createCrossBid() {
		Bid cross = new Bid();
		cross.userA = this.userB;
		cross.userAEmail = this.userBEmail;
		cross.userANickname = this.userBNickname;
		cross.userB = this.userA;
		cross.userBEmail = this.userAEmail;
		cross.userBNickname = this.userANickname;
		cross.direction = this.direction == Direction.A_TO_B ? Direction.B_TO_A : Direction.A_TO_B;
		cross.text = this.text;
		cross.created = this.created;
		cross.read = this.read;
		cross.amount = this.amount;
		cross.percentage = this.percentage;
		cross.value = this.value;
		cross.type = this.type;
		return cross;
	}
	public String getWebKey() {
		return new Key<Bid>(Bid.class, id).getString();
	}
	public String toString() {
		return "Bid: " + this.amount + " for " + this.percentage + "% " + this.type
				+ " "+ userANickname + (direction == Direction.A_TO_B ? " -> " : " <- ") + userBNickname;
	}
}
