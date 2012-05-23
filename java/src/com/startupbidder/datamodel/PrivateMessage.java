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
public class PrivateMessage extends BaseObject<PrivateMessage> {
	public static enum Direction {A_TO_B, B_TO_A};
	public PrivateMessage () {
	}
	public PrivateMessage(SBUser toUser, SBUser fromUser, String text) {
		this.created = new Date();
		this.read = false;
		this.text = text;
		this.direction = PrivateMessage.Direction.A_TO_B;
		this.userA = fromUser.getKey();
		this.userAEmail = fromUser.email;
		this.userANickname = fromUser.nickname;
		this.userB = toUser.getKey();
		this.userAEmail = toUser.email;
		this.userANickname = toUser.nickname;
	}
	public Key<PrivateMessage> getKey() {
		return new Key<PrivateMessage>(PrivateMessage.class, id);
	}
	@Id public Long id;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	
	@Indexed public Key<SBUser> userA;
    public String userANickname;
	public String userAEmail;
	@Indexed public Key<SBUser> userB;
    public String userBNickname;
	public String userBEmail;
	@Indexed public Direction direction;
	
	@Indexed public Date   created;
	public String text;
	@Indexed public boolean read;

	public PrivateMessage createCrossMessage() {
		PrivateMessage cross = new PrivateMessage();
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
		return cross;
	}
	public String getWebKey() {
		return new Key<PrivateMessage>(PrivateMessage.class, id).getString();
	}
}
