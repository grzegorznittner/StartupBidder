/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.vo;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public interface UserDataUpdatable {
	/**
	 * Returns user profile id
	 */
	String getUser();

	/**
	 * Sets user avatar
	 */
	void setAvatar(String avatar);
	
	/**
	 * Sets user nickname
	 */
	void setUserNickname(String userNickname);
	
	/**
	 * Sets user class
	 */
	void setUserClass(String userClass);
}
