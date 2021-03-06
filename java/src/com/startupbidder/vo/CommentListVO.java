package com.startupbidder.vo;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.startupbidder.web.UserMgmtFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class CommentListVO extends BaseResultVO implements UserDataUpdatableContainer {
	@JsonProperty("comments") private List<CommentVO> comments;
	// not returned in JSON
	private ListPropertiesVO commentsProperties;
	@JsonProperty("listing") private ListingVO listing;
	@JsonProperty("profile") private UserBasicVO user;

	public void updateUserData() {
		List<UserDataUpdatable> updatable = new ArrayList<UserDataUpdatable>();
		if (comments != null) updatable.addAll(comments);
		if (listing != null) updatable.add(listing);
		
		UserMgmtFacade.instance().updateUserData(updatable);
	}

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
	public void setUser(UserBasicVO user) {
		this.user = user;
	}
	public UserBasicVO getUser() {
		return user;
	}
}
