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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((commentedOn == null) ? 0 : commentedOn.hashCode());
		result = prime * result + ((listing == null) ? 0 : listing.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		VoteDTO other = (VoteDTO) obj;
		if (commentedOn == null) {
			if (other.commentedOn != null)
				return false;
		} else if (!commentedOn.equals(other.commentedOn))
			return false;
		if (listing == null) {
			if (other.listing != null)
				return false;
		} else if (!listing.equals(other.listing))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (value != other.value)
			return false;
		return true;
	}
}
