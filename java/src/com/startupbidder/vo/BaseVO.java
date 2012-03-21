package com.startupbidder.vo;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.googlecode.objectify.Key;

public abstract class BaseVO {
	public abstract String getId();
	
	public long toKeyId() {
		return Key.create(getId()).getId();
	}

	public static long toKeyId(String id) {
		return Key.create(id).getId();
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
