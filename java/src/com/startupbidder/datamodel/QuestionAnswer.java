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
public class QuestionAnswer extends BaseObject<QuestionAnswer> {
	public Key<QuestionAnswer> getKey() {
		return new Key<QuestionAnswer>(QuestionAnswer.class, id);
	}
	@Id public Long id;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	/** User which asks questions */
	@Indexed public Key<SBUser> user;
	public String userNickname;
	/** Question is always addressed to the listing owner */
	@Indexed public Key<Listing> listing;
	public Key<SBUser> listingOwner;
	@Indexed public Date created;
	public Date answerDate;
	@Indexed public boolean published;
	public String question;
	public String answer;

	public String getWebKey() {
		return new Key<QuestionAnswer>(QuestionAnswer.class, id).getString();
	}
}
