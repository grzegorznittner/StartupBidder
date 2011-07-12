package com.startupbidder.vo;

import java.util.List;

public class ListingListVO {
	private List<ListingVO> listings;
	private ListPropertiesVO listingsProperties;
	
	public List<ListingVO> getListings() {
		return listings;
	}
	public void setListings(List<ListingVO> listings) {
		this.listings = listings;
	}
	public ListPropertiesVO getListingsProperties() {
		return listingsProperties;
	}
	public void setListingsProperties(ListPropertiesVO listingsProperties) {
		this.listingsProperties = listingsProperties;
	}
	
	@Override
	public String toString() {
		return "ListingListVO [listings=" + listings + ", listingsProperties="
				+ listingsProperties + "]";
	}
}
