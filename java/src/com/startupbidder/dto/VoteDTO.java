package com.startupbidder.dto;

import java.util.Date;


public class VoteDTO extends AbstractDTO {
	private String listing;
	private String user;
	private int value;
	private Date commentedOn;
	
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

	public Date getCommentedOn() {
		return commentedOn;
	}

	public void setCommentedOn(Date commentedOn) {
		this.commentedOn = commentedOn;
	}

	@Override
	public String toString() {
		return "VoteDTO [idAsString" + getIdAsString() + ", listing=" + listing + ", user=" + user + ", value="
				+ value + ", commentedOn=" + commentedOn + "]";
	}

}
