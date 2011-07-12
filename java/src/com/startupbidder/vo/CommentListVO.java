package com.startupbidder.vo;

import java.util.List;

public class CommentListVO {
	private List<CommentVO> comments;
	private ListPropertiesVO commentsProperties;
	private ListingVO listing;
	
	public List<CommentVO> getComments() {
		return comments;
	}
	public void setComments(List<CommentVO> comments) {
		this.comments = comments;
	}
	public ListPropertiesVO getCommentsProperties() {
		return commentsProperties;
	}
	public void setCommentsProperties(ListPropertiesVO commentsProperties) {
		this.commentsProperties = commentsProperties;
	}
	public ListingVO getListing() {
		return listing;
	}
	public void setListing(ListingVO listing) {
		this.listing = listing;
	}
	
	@Override
	public String toString() {
		return "CommentListVO [comments=" + comments + ", commentsProperties="
				+ commentsProperties + ", listing=" + listing + "]";
	}
}
