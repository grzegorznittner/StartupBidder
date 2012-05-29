package com.startupbidder.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class BidUserListVO extends BaseResultVO {
	@JsonProperty("users") private List<BidUserVO> bids;
	@JsonProperty("bids_props")	private ListPropertiesVO bidsProperties;
	public List<BidUserVO> getBids() {
		return bids;
	}
	public void setBids(List<BidUserVO> bids) {
		this.bids = bids;
	}
	public ListPropertiesVO getBidsProperties() {
		return bidsProperties;
	}
	public void setBidsProperties(ListPropertiesVO bidsProperties) {
		this.bidsProperties = bidsProperties;
	}
}
