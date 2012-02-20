/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.datamodel;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public abstract class BaseObject {
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
