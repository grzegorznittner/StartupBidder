package com.startupbidder.vo;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

public class UserVO {
	@JsonProperty("num")
	private int orderNumber;
	@JsonProperty("profile_id")
	private String id;
	@JsonProperty("username")
	private String nickname;
	@JsonProperty("name")
	private String firstName;
	@JsonProperty("surename")
	private String lastName;
	@JsonProperty("email")
	private String email;
	@JsonProperty("open_id")
	private String openId;
	@JsonProperty("title")
	private String title;
	@JsonProperty("organization")
	private String organization;
	@JsonProperty("facebook")
	private String facebook;
	@JsonProperty("twitter")
	private String twitter;
	@JsonProperty("linkedin")
	private String linkedin;
	@JsonProperty("investor")
	private boolean accreditedInvestor;
	@JsonProperty("joined_date")
	private Date   joined;
	@JsonProperty("last_login")
	private Date   lastLoggedIn;
	@JsonProperty("modified")
	private Date   modified;
	@JsonProperty("num_listings")
	private int numberOfListings;
	@JsonProperty("num_bids")
	private int numberOfBids;
	@JsonProperty("num_comments")
	private int numberOfComments;
	@JsonProperty("status")
	private String status;
	
	public UserVO() {
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public String getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(String linkedin) {
		this.linkedin = linkedin;
	}

	public boolean isAccreditedInvestor() {
		return accreditedInvestor;
	}

	public void setAccreditedInvestor(boolean accreditedInvestor) {
		this.accreditedInvestor = accreditedInvestor;
	}

	public Date getJoined() {
		return joined;
	}

	public void setJoined(Date joined) {
		this.joined = joined;
	}

	public Date getLastLoggedIn() {
		return lastLoggedIn;
	}

	public void setLastLoggedIn(Date lastLoggedIn) {
		this.lastLoggedIn = lastLoggedIn;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
	public int getNumberOfListings() {
		return numberOfListings;
	}
	public void setNumberOfListings(int numberOfListings) {
		this.numberOfListings = numberOfListings;
	}
	public int getNumberOfBids() {
		return numberOfBids;
	}
	public void setNumberOfBids(int numberOfBids) {
		this.numberOfBids = numberOfBids;
	}
	public int getNumberOfComments() {
		return numberOfComments;
	}
	public void setNumberOfComments(int numberOfComments) {
		this.numberOfComments = numberOfComments;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	@Override
	public String toString() {
		return "UserVO [orderNumber=" + orderNumber + ", id=" + id
				+ ", nickname=" + nickname + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email + ", openId="
				+ openId + ", title=" + title + ", organization="
				+ organization + ", facebook=" + facebook + ", twitter="
				+ twitter + ", linkedin=" + linkedin + ", accreditedInvestor="
				+ accreditedInvestor + ", joined=" + joined + ", lastLoggedIn="
				+ lastLoggedIn + ", modified=" + modified
				+ ", numberOfListings=" + numberOfListings + ", numberOfBids="
				+ numberOfBids + ", numberOfComments=" + numberOfComments
				+ ", status=" + status + "]";
	}
}
