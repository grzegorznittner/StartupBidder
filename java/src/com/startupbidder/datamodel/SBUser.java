/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.datamodel;

import java.util.Date;
import java.util.Random;

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
@Cached(expirationSeconds=60*60*2)
public class SBUser extends BaseObject<SBUser> {
	public enum Status {CREATED, ACTIVE, DEACTIVATED};
	public Key<SBUser> getKey() {
		return new Key<SBUser>(SBUser.class, id);
	}
	@Id public Long id;
	
	public boolean mockData;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	
	public boolean admin;
	public String userClass;
	@Indexed public String email;
	@Indexed public boolean investor;
	@Indexed public Status status;
	
	public Key<Listing> editedListing;
	
	public String openId;

	public String name;
    @Indexed public String nickname;
    @Indexed public String nicknameLower;
	public String phone;
	public String location;
	@Indexed public String country;
	
	public boolean notifyEnabled = true;

	@Indexed(IfNotNull.class) public long twitterId;
	public String twitterScreenName;
	@Indexed(IfNotNull.class) public String twitterEmail;
	
	public String password;
	@Indexed(IfNotNull.class) public String activationCode;
	@Indexed(IfNotNull.class) public String authCookie;

	@Indexed public Date joined;
	public Date lastLoggedIn;

	public String getWebKey() {
		return new Key<SBUser>(SBUser.class, id).getString();
	}

	public SBUser() {
	}
	
    public SBUser(String email, String name, String phone, String location,
            boolean admin, boolean investor, Status status) {
        this(
                email,
                name,
                (email.contains("@") ? email.substring(0, email.indexOf("@")) : "anonymous" + String.valueOf(new Random().nextInt(1000000000))),
                phone,
                location,
                admin,
                investor,
                status
        );
    }

    public SBUser(String email, String name, String nickname, String phone, String location,
            boolean admin, boolean investor, Status status) {
        this(
                email,
                name,
                nickname,
                nickname.toLowerCase(),
                phone,
                location,
                admin,
                investor,
                status
        );
    }

    public SBUser(String email, String name, String nickname, String nicknameLower, String phone, String location,
            boolean admin, boolean investor, Status status) {
		this.email = email;
		this.name = name;
		this.nickname = nickname;
        this.nicknameLower = nicknameLower;
		this.phone = phone;
		this.location = location;
		this.admin = admin;
		this.investor = investor;
		this.status = status;
		this.joined = new Date();
		this.activationCode = "" + this.email.hashCode() + this.joined.hashCode();
	}

    public void genNicknameLower() {
        nicknameLower = nickname != null ? nickname.toLowerCase() : null;
    }
    
}
