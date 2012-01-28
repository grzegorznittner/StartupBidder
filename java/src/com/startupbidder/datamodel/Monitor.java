/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.datamodel;

import java.util.Date;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*30)
public class Monitor extends BaseObject {
	public interface Monitored {};
	
	public static enum Type {LISTING, BID, USER};

	public Key<SBUser> user;
	public Type type;
	public Key<Monitored> object;
	public Date   created;
	public Date   deactivated;
	public boolean active;

	public String getWebKey() {
		return new Key<Monitor>(Monitor.class, id).getString();
	}
}
