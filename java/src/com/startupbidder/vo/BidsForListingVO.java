package com.startupbidder.vo;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * Container for bids returned for specified listing
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class BidsForListingVO extends BaseResultVO {
	@JsonProperty("listing")
	private ListingVO listing;
	@JsonProperty("profile")
	private UserBasicVO user;

	@JsonProperty("bids_per_user")
	private Map<String, List<BidVO>> bidsPerUser;

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
	public Map<String, List<BidVO>> getBidsPerUser() {
		return bidsPerUser;
	}
	public void setBidsPerUser(Map<String, List<BidVO>> bidsPerUser) {
		this.bidsPerUser = bidsPerUser;
	}
}
