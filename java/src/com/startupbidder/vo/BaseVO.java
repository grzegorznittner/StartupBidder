package com.startupbidder.vo;

import com.googlecode.objectify.Key;

public abstract class BaseVO {
	public abstract String getId();
	
	public long toKeyId() {
		return Key.create(getId()).getId();
	}

	public static long toKeyId(String id) {
		return Key.create(id).getId();
	}
}
