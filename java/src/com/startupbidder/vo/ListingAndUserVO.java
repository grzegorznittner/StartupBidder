package com.startupbidder.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class ListingAndUserVO extends BaseResultVO implements UserDataUpdatableContainer {
	@JsonProperty("listing") private ListingVO listing;
	@JsonProperty("categories") private Map<String, String> categories;

	public void updateUserData() {
		UserMgmtFacade.instance().updateUserData(listing);
	}
	
	public ListingVO getListing() {
		return listing;
	}
	public void setListing(ListingVO listing) {
		this.listing = listing;
	}
	public Map<String, String> getCategories() {
		return categories;
	}
	public void setCategories(Map<String, String> categories) {
		this.categories = categories;
	}
}
