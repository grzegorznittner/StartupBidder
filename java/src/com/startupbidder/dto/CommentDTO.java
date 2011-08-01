package com.startupbidder.dto;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;

public class CommentDTO extends AbstractDTO {
	private String listing;
	private String user;
	private String comment;
	private Date   commentedOn;
	
	public CommentDTO() {
	}

	public String getKind() {
		return "Comment";
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getCommentedOn() {
		return commentedOn;
	}

	public void setCommentedOn(Date commentedOn) {
		this.commentedOn = commentedOn;
	}

	@Override
	public String toString() {
		return "CommentDTO [idAsString" + getIdAsString() + ", listing=" + listing
				+ ", user=" + user + ", comment=" + comment + ", commentedOn="
				+ commentedOn + "]";
	}

	@Override
	public Entity toEntity() {
		Entity comment = new Entity(this.id);
		comment.setProperty("comment", this.comment);
		comment.setProperty("commentedOn", this.commentedOn);
		comment.setProperty("listing", this.listing);
		comment.setProperty("user", this.user);
		return comment;
	}
	
	public static CommentDTO fromEntity(Entity entity) {
		CommentDTO comment = new CommentDTO();
		comment.setKey(entity.getKey());
		comment.comment = (String)entity.getProperty("comment");
		comment.commentedOn = (Date)entity.getProperty("commentedOn");
		comment.listing = (String)entity.getProperty("listing");
		comment.user = (String)entity.getProperty("user");
		return comment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result
				+ ((commentedOn == null) ? 0 : commentedOn.hashCode());
		result = prime * result + ((listing == null) ? 0 : listing.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		CommentDTO other = (CommentDTO) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
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
		return true;
	}
}
