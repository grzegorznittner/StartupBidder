package com.startupbidder.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility=Visibility.NONE,
		setterVisibility=Visibility.NONE, fieldVisibility=Visibility.NONE)
public class BidListVO {
	@JsonProperty("bids")
	private List<BidVO> bids;
	@JsonProperty("bids_props")
	private ListPropertiesVO bidsProperties;
	@JsonProperty("listing")
	private ListingVO listing;
	
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
	
	@Override
	public String toString() {
		return "BidListVO [bids=" + bids + ", bidsProperties=" + bidsProperties
				+ ", listing=" + listing + "]";
	}
}
