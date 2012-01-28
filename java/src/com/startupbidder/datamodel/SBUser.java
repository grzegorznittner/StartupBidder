/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.datamodel;

import java.util.Date;

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
@Cached(expirationSeconds=60*60*2)
public class SBUser extends BaseObject implements Monitor.Monitored {
	public enum Status {CREATED, ACTIVE, DEACTIVATED};
	
	@Indexed public boolean admin;
	@Indexed public String email;
	@Indexed public boolean investor;
	@Indexed public Status status;
	
	public String openId;

	public String name;
	public String nickname;
	public String phone;
	public String address;
	
	public boolean notifyEnabled;
		
	@Indexed public Date joined;
	public Date lastLoggedIn;

	public String getWebKey() {
		return new Key<SBUser>(SBUser.class, id).getString();
	}
}
