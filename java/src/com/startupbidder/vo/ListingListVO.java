package com.startupbidder.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class ListingListVO {
	@JsonProperty("listings")
	private List<ListingVO> listings;
	@JsonProperty("listings_props")
	private ListPropertiesVO listingsProperties;
	@JsonProperty("profile")
	private UserVO user;
	@JsonProperty("loggedin_profile")
	private UserVO loggedUser;

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
	public UserVO getLoggedUser() {
		return loggedUser;
	}
	public void setLoggedUser(UserVO loggedUser) {
		this.loggedUser = loggedUser;
	}
	@Override
	public String toString() {
		return "ListingListVO [listings=" + listings + ", listingsProperties="
				+ listingsProperties + ", user=" + user + ", loggedUser="
				+ loggedUser + "]";
	}
}
