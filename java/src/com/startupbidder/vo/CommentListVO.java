package com.startupbidder.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class CommentListVO {
	@JsonProperty("comments")
	private List<CommentVO> comments;
	// not returned in JSON
	private ListPropertiesVO commentsProperties;
	@JsonProperty("listing")
	private ListingVO listing;
	@JsonProperty("profile")
	private UserVO user;
	
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
	public void setUser(UserVO user) {
		this.user = user;
	}
	public UserVO getUser() {
		return user;
	}
	@Override
	public String toString() {
		return "CommentListVO [comments=" + comments + ", commentsProperties="
				+ commentsProperties + ", listing=" + listing + ", user="
				+ user + "]";
	}
}
