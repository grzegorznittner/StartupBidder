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
public class BidListVO extends BaseResultVO {
	@JsonProperty("bids")
	private List<BidVO> bids;
	// Not returned in JSON
	private ListPropertiesVO bidsProperties;
	@JsonProperty("listing")
	private ListingVO listing;
	@JsonProperty("profile")
	private UserVO user;
	
	public List<BidVO> getBids() {
		return bids;
	}
	public void setBids(List<BidVO> bids) {
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
	public void setUser(UserVO user) {
		this.user = user;
	}
	public UserVO getUser() {
		return user;
	}
	@Override
	public String toString() {
		return "BidListVO [bids=" + bids + ", bidsProperties=" + bidsProperties
				+ ", listing=" + listing + ", user=" + user + super.toString() + "]";
	}
}
