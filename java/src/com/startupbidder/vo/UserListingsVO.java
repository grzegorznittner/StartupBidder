package com.startupbidder.vo;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class UserListingsVO extends BaseResultVO {
	@JsonProperty("active_listings") private List<ListingTileVO> activeListings;
	@JsonProperty("withdrawn_listings") private List<ListingTileVO> withdrawnListings;
	@JsonProperty("frozen_listings") private List<ListingTileVO> frozenListings;
	@JsonProperty("closed_listings") private List<ListingTileVO> closedListings;
	@JsonProperty("monitored_listings") private List<ListingTileVO> commentedListings;
	@JsonProperty("edited_listing") private ListingVO editedListing;
	@JsonProperty("notifications") private List<NotificationVO> notifications;
	@JsonProperty("categories") private Map<String, Integer> categories;
	@JsonProperty("top_locations") private Map<String, Integer> topLocations;
	@JsonProperty("admin_posted_listings") private List<ListingTileVO> adminPostedListings;
	@JsonProperty("admin_frozen_listings") private List<ListingTileVO> adminFrozenListings;

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
	public List<ListingTileVO> getActiveListings() {
		return activeListings;
	}
	public void setActiveListings(List<ListingTileVO> activeListings) {
		this.activeListings = activeListings;
	}
	public List<ListingTileVO> getWithdrawnListings() {
		return withdrawnListings;
	}
	public void setWithdrawnListings(List<ListingTileVO> withdrawnListings) {
		this.withdrawnListings = withdrawnListings;
	}
	public List<ListingTileVO> getFrozenListings() {
		return frozenListings;
	}
	public void setFrozenListings(List<ListingTileVO> frozenListings) {
		this.frozenListings = frozenListings;
	}
	public List<ListingTileVO> getClosedListings() {
		return closedListings;
	}
	public void setClosedListings(List<ListingTileVO> closedListings) {
		this.closedListings = closedListings;
	}
	public List<ListingTileVO> getCommentedListings() {
		return commentedListings;
	}
	public void setCommentedListings(List<ListingTileVO> commentedListings) {
		this.commentedListings = commentedListings;
	}
	public List<ListingTileVO> getAdminPostedListings() {
		return adminPostedListings;
	}
	public void setAdminPostedListings(List<ListingTileVO> adminPostedListings) {
		this.adminPostedListings = adminPostedListings;
	}
	public List<ListingTileVO> getAdminFrozenListings() {
		return adminFrozenListings;
	}
	public void setAdminFrozenListings(List<ListingTileVO> adminFrozenListings) {
		this.adminFrozenListings = adminFrozenListings;
	}
	public List<NotificationVO> getNotifications() {
		return notifications;
	}
	public void setNotifications(List<NotificationVO> notifications) {
		this.notifications = notifications;
	}
}
