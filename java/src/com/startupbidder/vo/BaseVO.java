package com.startupbidder.vo;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.appengine.api.utils.SystemProperty;
import com.googlecode.objectify.Key;

public abstract class BaseVO {
	public abstract String getId();
	
	public long toKeyId() {
		return Key.create(getId()).getId();
	}

	public static long toKeyId(String id) {
		return Key.create(id).getId();
	}
	
	protected String getServiceLocation () {
		return SystemProperty.environment.value() == SystemProperty.Environment.Value.Development ?
				"http://localhost:7777" : "http://www.startupbidder.com";
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
