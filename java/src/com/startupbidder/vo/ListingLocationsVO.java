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
public class ListingLocationsVO extends BaseResultVO {
	@JsonProperty("map_listings") private List<Object[]> listings;
	@JsonProperty("current_lat") private Double currentLatitude;
	@JsonProperty("current_long") private Double currentLongitude;
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
	public Double getCurrentLatitude() {
		return currentLatitude;
	}
	public void setCurrentLatitude(Double currentLatitude) {
		this.currentLatitude = currentLatitude;
	}
	public Double getCurrentLongitude() {
		return currentLongitude;
	}
	public void setCurrentLongitude(Double currentLongitude) {
		this.currentLongitude = currentLongitude;
	}
}
