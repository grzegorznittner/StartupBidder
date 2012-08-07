/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class ListingToStringStyle extends ToStringStyle {
	private static final long serialVersionUID = 8467832298923563L;
	
	public static ListingToStringStyle instance = new ListingToStringStyle();

	protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
		if ("logoBase64".equals(fieldName)) {
			String logo = StringUtils.substring((String)value, 0, 32);
			buffer.append(logo).append("... (size ").append(StringUtils.length((String)value)).append(")");
		} else {
			super.appendDetail(buffer, fieldName, value);
		}
	}
}
