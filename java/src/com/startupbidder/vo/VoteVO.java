package com.startupbidder.vo;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class VoteVO extends BaseVO {
	@JsonProperty("id")
	private String id;
	@JsonProperty("listing_id")
	private String listing;
	@JsonProperty("listing_name")
	private String listingName;
	@JsonProperty("user_id")
	private String user;
	@JsonProperty("user_name")
	private String userName;
	private long value;
	@JsonProperty("mockData")
	private boolean mockData;
	
	public VoteVO() {
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getListing() {
		return listing;
	}

	public void setListing(String listing) {
		this.listing = listing;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public String getListingName() {
		return listingName;
	}

	public void setListingName(String listingName) {
		this.listingName = listingName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isMockData() {
		return mockData;
	}

	public void setMockData(boolean mockData) {
		this.mockData = mockData;
	}
}
