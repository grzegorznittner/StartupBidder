package com.startupbidder.vo;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class BidListVO extends BaseResultVO {
	@JsonProperty("bids") private List<BidVO> bids;
	@JsonProperty("other_user_profile") private UserShortVO otherUser;
	@JsonProperty("bids_props")	private ListPropertiesVO bidsProperties;
	@JsonProperty("valid_actions") private String validActions[];
	public List<BidVO> getBids() {
		return bids;
	}
	public void setBids(List<BidVO> bids) {
		this.bids = bids;
	}
	public UserShortVO getOtherUser() {
		return otherUser;
	}
	public void setOtherUser(UserShortVO otherUser) {
		this.otherUser = otherUser;
	}
	public ListPropertiesVO getBidsProperties() {
		return bidsProperties;
	}
	public void setBidsProperties(ListPropertiesVO bidsProperties) {
		this.bidsProperties = bidsProperties;
	}
	public void setValidActions(String validActions) {
		this.validActions = StringUtils.split(validActions, ',');
	}
}
