package com.startupbidder.vo;

import java.util.List;

public class BidListVO {
	private List<BidVO> bids;
	private ListPropertiesVO bidsProperties;
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
