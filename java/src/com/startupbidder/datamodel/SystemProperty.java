/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.datamodel;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.PrePersist;

import org.apache.commons.lang3.builder.ToStringBuilder;

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
@Cached(expirationSeconds=60*30*2)
public class SystemProperty {
	public static final String GOOGLEDOC_USER = "googledoc.user";
	public static final String GOOGLEDOC_PASSWORD = "googledoc.password";
	public static final String GOOGLEPLACES_API_KEY = "googleplaces.apikey";

	public static final String TWITTER_CONSUMER_KEY = "twitter.consumer.key";
	public static final String TWITTER_CONSUMER_SECRET = "twitter.consumer.secret";
	public static final String TWITTER_ACCESS_TOKEN = "twitter.access.token";
	public static final String TWITTER_ACCESS_TOKEN_SECRET = "twitter.access.token.secret";
	
	public static final String FACEBOOK_CLIENT_ID = "facebook.client.id";
	public static final String FACEBOOK_CLIENT_SECRET = "facebook.client.secret";

	@Id public String name;
	public String value;
	@Indexed public Date created;
	public String author;

	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	
	public boolean booleanValue() {
		if (value == null) {
			return false;
		}
		return new Boolean(value);
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getWebKey() {
		return new Key<SystemProperty>(SystemProperty.class, name).getString();
	}
}
