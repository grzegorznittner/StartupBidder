/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.datamodel;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.googlecode.objectify.Key;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public abstract class BaseObject<T> {
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public abstract Key<T> getKey();
}
