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
public class DiscoverListingsVO extends BaseResultVO {
	@JsonProperty("top_listings") private List<ListingVO> topListings;
	@JsonProperty("closing_listings") private List<ListingVO> closingListings;
	@JsonProperty("latest_listings") private List<ListingVO> latestListings;
	@JsonProperty("users_listings") private List<ListingVO> usersListings;
	@JsonProperty("edited_listing") private ListingVO editedListing;
	@JsonProperty("categories") private Map<String, Integer> categories;
	@JsonProperty("top_locations") private Map<String, Integer> topLocations;

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
	public List<ListingVO> getTopListings() {
		return topListings;
	}
	public void setTopListings(List<ListingVO> topListings) {
		this.topListings = topListings;
	}
	public List<ListingVO> getClosingListings() {
		return closingListings;
	}
	public void setClosingListings(List<ListingVO> closingListings) {
		this.closingListings = closingListings;
	}
	public List<ListingVO> getLatestListings() {
		return latestListings;
	}
	public void setLatestListings(List<ListingVO> latestListings) {
		this.latestListings = latestListings;
	}
	public List<ListingVO> getUsersListings() {
		return usersListings;
	}
	public void setUsersListings(List<ListingVO> usersListings) {
		this.usersListings = usersListings;
	}
	public ListingVO getEditedListing() {
		return editedListing;
	}
	public void setEditedListing(ListingVO editedListing) {
		this.editedListing = editedListing;
	}
}
