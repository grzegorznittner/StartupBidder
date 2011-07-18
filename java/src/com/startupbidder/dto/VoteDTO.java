package com.startupbidder.dto;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;


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

	@Override
	public Entity toEntity() {
		Entity vote = new Entity(id);
		vote.setProperty("commentedOn", this.commentedOn);
		vote.setProperty("listing", this.listing);
		vote.setProperty("user", this.user);
		vote.setProperty("value", this.value);
		return vote;
	}

	public static VoteDTO fromEntity(Entity entity) {
		VoteDTO vote = new VoteDTO();
		vote.setKey(entity.getKey());
		vote.setCommentedOn((Date)entity.getProperty("commentedOn"));
		vote.setListing((String)entity.getProperty("listing"));
		vote.setUser((String)entity.getProperty("user"));
		vote.setValue((Integer)entity.getProperty("value"));
		return vote;
	}
}
