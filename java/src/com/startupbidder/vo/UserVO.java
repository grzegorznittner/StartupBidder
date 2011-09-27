package com.startupbidder.vo;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.startupbidder.util.DateSerializer;
import com.startupbidder.util.LowecaseSerializer;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserVO {
	@JsonProperty("num")
	private int orderNumber;
	@JsonProperty("profile_id")
	private String id;
	@JsonProperty("username")
	private String nickname;
	@JsonProperty("name")
	private String name;
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
	@JsonSerialize(using=DateSerializer.class)
	private Date   joined;
	@JsonProperty("last_login")
	@JsonSerialize(using=DateSerializer.class)
	private Date   lastLoggedIn;
	@JsonProperty("modified")
	@JsonSerialize(using=DateSerializer.class)
	private Date   modified;
	@JsonProperty("num_listings")
	private long numberOfListings;
	@JsonProperty("num_bids")
	private long numberOfBids;
	@JsonProperty("num_comments")
	private long numberOfComments;
	@JsonProperty("num_votes")
	private long numberOfVotes;
	@JsonProperty("status")
	@JsonSerialize(using=LowecaseSerializer.class)
	private String status;
	@JsonProperty("votable")
	private boolean votable;
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public long getNumberOfListings() {
		return numberOfListings;
	}
	public void setNumberOfListings(long numberOfListings) {
		this.numberOfListings = numberOfListings;
	}
	public long getNumberOfBids() {
		return numberOfBids;
	}
	public void setNumberOfBids(long numberOfBids) {
		this.numberOfBids = numberOfBids;
	}
	public long getNumberOfComments() {
		return numberOfComments;
	}
	public void setNumberOfComments(long numberOfComments) {
		this.numberOfComments = numberOfComments;
	}
	public long getNumberOfVotes() {
		return numberOfVotes;
	}
	public void setNumberOfVotes(long numberOfVotes) {
		this.numberOfVotes = numberOfVotes;
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
	public boolean isVotable() {
		return votable;
	}
	public void setVotable(boolean votable) {
		this.votable = votable;
	}
	@Override
	public String toString() {
		return "UserVO [orderNumber=" + orderNumber + ", id=" + id
				+ ", nickname=" + nickname + ", name=" + name + ", email="
				+ email + ", openId=" + openId + ", title=" + title
				+ ", organization=" + organization + ", facebook=" + facebook
				+ ", twitter=" + twitter + ", linkedin=" + linkedin
				+ ", accreditedInvestor=" + accreditedInvestor + ", joined="
				+ joined + ", lastLoggedIn=" + lastLoggedIn + ", modified="
				+ modified + ", numberOfListings=" + numberOfListings
				+ ", numberOfBids=" + numberOfBids + ", numberOfComments="
				+ numberOfComments + ", numberOfVotes=" + numberOfVotes
				+ ", status=" + status + ", votable=" + votable + "]";
	}
}
