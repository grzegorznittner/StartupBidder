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
public class ListingListVO extends BaseResultVO {
	@JsonProperty("listings")
	private List<ListingVO> listings;
	@JsonProperty("listings_props")
	private ListPropertiesVO listingsProperties;
	@JsonProperty("profile")
	private UserVO user;

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
	public void setUser(UserVO user) {
		this.user = user;
	}
	public UserVO getUser() {
		return user;
	}
	@Override
	public String toString() {
		return "ListingListVO [listings=" + listings + ", listingsProperties="
				+ listingsProperties + ", user=" + user + super.toString() + "]";
	}
}
