package com.startupbidder.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.startupbidder.web.UserMgmtFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class DiscoverListingsVO extends BaseResultVO implements UserDataUpdatableContainer {
	@JsonProperty("top_listings") private List<ListingTileVO> topListings;
	@JsonProperty("closing_listings") private List<ListingTileVO> closingListings;
	@JsonProperty("latest_listings") private List<ListingTileVO> latestListings;
	@JsonProperty("monitored_listings") private List<ListingTileVO> monitoredListings;
	@JsonProperty("users_listings") private List<ListingTileVO> usersListings;
	@JsonProperty("edited_listing") private ListingVO editedListing;
	@JsonProperty("categories") private Map<String, Integer> categories;
	@JsonProperty("top_locations") private Map<String, Integer> topLocations;
	
	public void updateUserData() {
		List<UserDataUpdatable> updatable = new ArrayList<UserDataUpdatable>();
		if (topListings != null) updatable.addAll(topListings);
		if (closingListings != null) updatable.addAll(closingListings);
		if (latestListings != null) updatable.addAll(latestListings);
		if (monitoredListings != null) updatable.addAll(monitoredListings);
		if (usersListings != null) updatable.addAll(usersListings);
		if (editedListing != null) updatable.add(editedListing);
		
		UserMgmtFacade.instance().updateUserData(updatable);
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
	public List<ListingTileVO> getTopListings() {
		return topListings;
	}
	public void setTopListings(List<ListingTileVO> topListings) {
		this.topListings = topListings;
	}
	public List<ListingTileVO> getClosingListings() {
		return closingListings;
	}
	public void setClosingListings(List<ListingTileVO> closingListings) {
		this.closingListings = closingListings;
	}
	public List<ListingTileVO> getLatestListings() {
		return latestListings;
	}
	public void setLatestListings(List<ListingTileVO> latestListings) {
		this.latestListings = latestListings;
	}
	public List<ListingTileVO> getUsersListings() {
		return usersListings;
	}
	public void setUsersListings(List<ListingTileVO> usersListings) {
		this.usersListings = usersListings;
	}
	public ListingVO getEditedListing() {
		return editedListing;
	}
	public void setEditedListing(ListingVO editedListing) {
		this.editedListing = editedListing;
	}
	public List<ListingTileVO> getMonitoredListings() {
		return monitoredListings;
	}
	public void setMonitoredListings(List<ListingTileVO> monitoredListings) {
		this.monitoredListings = monitoredListings;
	}
}
