package com.startupbidder.vo;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class ListingLocationsVO extends BaseResultVO {
	@JsonProperty("map_listings") private List<Object[]> listings;
	@JsonProperty("listings_props")	private ListPropertiesVO listingsProperties;
	@JsonProperty("profile") private UserBasicVO user;

	public List<Object[]> getListings() {
		return listings;
	}
	public void setListings(List<Object[]> listings) {
		this.listings = listings;
	}
	public ListPropertiesVO getListingsProperties() {
		return listingsProperties;
	}
	public void setListingsProperties(ListPropertiesVO listingsProperties) {
		this.listingsProperties = listingsProperties;
	}
	public void setUser(UserBasicVO user) {
		this.user = user;
	}
	public UserBasicVO getUser() {
		return user;
	}
}
