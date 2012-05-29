package com.startupbidder.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class OldBidListVO extends BaseResultVO {
	@JsonProperty("bids")
	private List<OldBidVO> bids;
	// Not returned in JSON
	private ListPropertiesVO bidsProperties;
	@JsonProperty("listing")
	private ListingVO listing;
	@JsonProperty("profile")
	private UserBasicVO user;
	
	public List<OldBidVO> getBids() {
		return bids;
	}
	public void setBids(List<OldBidVO> bids) {
		this.bids = bids;
	}
	public ListPropertiesVO getBidsProperties() {
		return bidsProperties;
	}
	public void setBidsProperties(ListPropertiesVO bidsProperties) {
		this.bidsProperties = bidsProperties;
	}
	public ListingVO getListing() {
		return listing;
	}
	public void setListing(ListingVO listing) {
		this.listing = listing;
	}
	public void setUser(UserBasicVO user) {
		this.user = user;
	}
	public UserBasicVO getUser() {
		return user;
	}
}
