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
public class UserListingsVO extends BaseResultVO {
	@JsonProperty("active_listings") private List<ListingVO> activeListings;
	@JsonProperty("withdrawn_listings") private List<ListingVO> withdrawnListings;
	@JsonProperty("frozen_listings") private List<ListingVO> frozenListings;
	@JsonProperty("closed_listings") private List<ListingVO> closedListings;
	@JsonProperty("monitored_listings") private List<ListingVO> commentedListings;
	@JsonProperty("edited_listing") private ListingVO editedListing;
	@JsonProperty("categories") private Map<String, Integer> categories;
	@JsonProperty("top_locations") private Map<String, Integer> topLocations;
	@JsonProperty("admin_posted_listings") private List<ListingVO> adminPostedListings;
	@JsonProperty("admin_frozen_listings") private List<ListingVO> adminFrozenListings;

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
	public ListingVO getEditedListing() {
		return editedListing;
	}
	public void setEditedListing(ListingVO editedListing) {
		this.editedListing = editedListing;
	}
	public List<ListingVO> getActiveListings() {
		return activeListings;
	}
	public void setActiveListings(List<ListingVO> activeListings) {
		this.activeListings = activeListings;
	}
	public List<ListingVO> getWithdrawnListings() {
		return withdrawnListings;
	}
	public void setWithdrawnListings(List<ListingVO> withdrawnListings) {
		this.withdrawnListings = withdrawnListings;
	}
	public List<ListingVO> getFrozenListings() {
		return frozenListings;
	}
	public void setFrozenListings(List<ListingVO> frozenListings) {
		this.frozenListings = frozenListings;
	}
	public List<ListingVO> getClosedListings() {
		return closedListings;
	}
	public void setClosedListings(List<ListingVO> closedListings) {
		this.closedListings = closedListings;
	}
	public List<ListingVO> getCommentedListings() {
		return commentedListings;
	}
	public void setCommentedListings(List<ListingVO> commentedListings) {
		this.commentedListings = commentedListings;
	}
	public List<ListingVO> getAdminPostedListings() {
		return adminPostedListings;
	}
	public void setAdminPostedListings(List<ListingVO> adminPostedListings) {
		this.adminPostedListings = adminPostedListings;
	}
	public List<ListingVO> getAdminFrozenListings() {
		return adminFrozenListings;
	}
	public void setAdminFrozenListings(List<ListingVO> adminFrozenListings) {
		this.adminFrozenListings = adminFrozenListings;
	}
}
