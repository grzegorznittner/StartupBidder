package com.startupbidder.vo;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.startupbidder.util.DateSerializer;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class CommentVO {
	@JsonProperty("num")
	private int orderNumber;
	@JsonProperty("comment_id")
	private String id;
	@JsonProperty("listing_id")
	private String listing;
	@JsonProperty("listing_title")
	private String listingName;
	@JsonProperty("profile_id")
	private String user;
	@JsonProperty("profile_username")
	private String userName;
	@JsonProperty("text")
	private String comment;
	@JsonProperty("comment_date")
	@JsonSerialize(using=DateSerializer.class)
	private Date   commentedOn;
	
	public CommentVO() {
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

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public String toString() {
		return "CommentVO [orderNumber=" + orderNumber + ", id=" + id
				+ ", listing=" + listing + ", listingName=" + listingName
				+ ", user=" + user + ", userName=" + userName + ", comment="
				+ comment + ", commentedOn=" + commentedOn + "]";
	}
}
