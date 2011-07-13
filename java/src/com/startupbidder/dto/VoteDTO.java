package com.startupbidder.dto;


public class VoteDTO extends AbstractDTO {
	private String listing;
	private String user;
	private int value;
	
	public VoteDTO() {
	}

	public String getKind() {
		return "Vote";
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

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "VoteDTO [id=" + id + ", listing=" + listing
				+ ", user=" + user + ", value=" + value + "]";
	}
	
}
