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
public class ListingListVO extends BaseResultVO {
	@JsonProperty("listings") private List<ListingTileVO> listings;
	@JsonProperty("monitored_listings") private List<ListingTileVO> monitoredListings;
	@JsonProperty("notifications") private List<NotificationVO> notifications;
	@JsonProperty("listings_props")	private ListPropertiesVO listingsProperties;
	@JsonProperty("profile") private UserBasicVO user;
	@JsonProperty("categories") private Map<String, Integer> categories;
	@JsonProperty("top_locations") private Map<String, Integer> topLocations;

	public List<ListingTileVO> getListings() {
		return listings;
	}
	public void setListings(List<ListingTileVO> listings) {
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
	public Map<String, Integer> getCategories() {
		return categories;
	}
	public void setCategories(Map<String, Integer> categories) {
		this.categories = categories;
	}
	public Map<String, Integer> getTopLocations() {
		return topLocations;
	}
	public void setTopLocations(Map<String, Integer> topLocations) {
		this.topLocations = topLocations;
	}
	public List<NotificationVO> getNotifications() {
		return notifications;
	}
	public void setNotifications(List<NotificationVO> notifications) {
		this.notifications = notifications;
	}
	public List<ListingTileVO> getMonitoredListings() {
		return monitoredListings;
	}
	public void setMonitoredListings(List<ListingTileVO> monitoredListings) {
		this.monitoredListings = monitoredListings;
	}
}
