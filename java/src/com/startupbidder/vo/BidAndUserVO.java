package com.startupbidder.vo;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility=Visibility.NONE,
		setterVisibility=Visibility.NONE, fieldVisibility=Visibility.NONE)
public class BidAndUserVO {
	@JsonProperty("bid")
	private BidVO bid;
	@JsonProperty("profile")
	private UserVO user;
	public BidVO getBid() {
		return bid;
	}
	public void setBid(BidVO bid) {
		this.bid = bid;
	}
	public UserVO getUser() {
		return user;
	}
	public void setUser(UserVO user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "BidAndUserVO [bid=" + bid + ", user=" + user + "]";
	}
}
