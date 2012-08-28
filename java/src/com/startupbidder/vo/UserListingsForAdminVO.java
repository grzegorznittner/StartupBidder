package com.startupbidder.vo;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

import com.startupbidder.web.UserMgmtFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class UserListingsForAdminVO extends BaseResultVO implements UserDataUpdatableContainer {
	@JsonProperty("profile") private UserVO user;
	@JsonProperty("active_listings") private List<ListingTileVO> activeListings;
	@JsonProperty("withdrawn_listings") private List<ListingTileVO> withdrawnListings;
	@JsonProperty("frozen_listings") private List<ListingTileVO> frozenListings;
	@JsonProperty("closed_listings") private List<ListingTileVO> closedListings;
	@JsonProperty("edited_listing") private ListingVO editedListing;

	public void updateUserData() {
		List<UserDataUpdatable> updatable = new ArrayList<UserDataUpdatable>();
		if (activeListings != null) updatable.addAll(activeListings);
		if (withdrawnListings != null) updatable.addAll(withdrawnListings);
		if (frozenListings != null) updatable.addAll(frozenListings);
		if (closedListings != null) updatable.addAll(closedListings);
		if (editedListing != null) updatable.add(editedListing);
		
		UserMgmtFacade.instance().updateUserData(updatable);
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
	public UserVO getUser() {
		return user;
	}
	public void setUser(UserVO user) {
		this.user = user;
	}
}
