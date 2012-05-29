package com.startupbidder.vo;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class OldBidAndUserVO extends BaseResultVO {
	@JsonProperty("bid")
	private OldBidVO bid;
	@JsonProperty("profile")
	private UserBasicVO user;

	public OldBidVO getBid() {
		return bid;
	}
	public void setBid(OldBidVO bid) {
		this.bid = bid;
	}
	public UserBasicVO getUser() {
		return user;
	}
	public void setUser(UserBasicVO user) {
		this.user = user;
	}
}
