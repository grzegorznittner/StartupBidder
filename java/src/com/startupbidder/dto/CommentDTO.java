package com.startupbidder.dto;

import java.util.Date;

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
		return "CommentDTO [id=" + id + ", listing=" + listing
				+ ", user=" + user + ", comment=" + comment + ", commentedOn="
				+ commentedOn + "]";
	}
	
}
