package com.startupbidder.vo;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.startupbidder.util.DateSerializer;
import com.startupbidder.util.LowecaseSerializer;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class NotificationVO extends BaseVO {
	@JsonProperty("notify_id") private String id;
	@JsonProperty("notify_type") @JsonSerialize(using=LowecaseSerializer.class)	private String type;
    @JsonProperty("user_id") private String user;
	@JsonProperty("user_nickname") private String userNickname;
	private String userEmail;
	@JsonProperty("listing_id")	private String listing;
	@JsonProperty("listing_name") private String listingName;
	@JsonProperty("listing_owner") private String listingOwner;
	private String listingOwnerId;
	@JsonProperty("listing_category") private String listingCategory;
	@JsonProperty("listing_brief_address") private String listingBriefAddress;
	@JsonProperty("listing_mantra") private String listingMantra;
	/** The following 3 values are generated based on notification type (are not stored in datastore) */
	@JsonProperty("title") private String title;
	@JsonProperty("text_1") private String text1;
	private String text2;
	private String text3;
	@JsonProperty("link") private String link;
	
	@JsonProperty("create_date") @JsonSerialize(using=DateSerializer.class) private Date created;
	@JsonProperty("sent_date") @JsonSerialize(using=DateSerializer.class) private Date sentDate;
	@JsonProperty("read") private boolean read;
	
	@JsonProperty("listing_logo_url")
	public String getListingLogoLink() {
		return getServiceLocation() + "/listing/logo?id=" + this.listing;
	}
	
	@Override
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getListing() {
		return listing;
	}
	public void setListing(String listing) {
		this.listing = listing;
	}
	public String getListingName() {
		return listingName;
	}
	public void setListingName(String listingName) {
		this.listingName = listingName;
	}
	public String getListingOwner() {
		return listingOwner;
	}
	public void setListingOwner(String listingOwner) {
		this.listingOwner = listingOwner;
	}
	public String getListingOwnerId() {
		return listingOwnerId;
	}
	public void setListingOwnerId(String listingOwnerId) {
		this.listingOwnerId = listingOwnerId;
	}
	public String getListingCategory() {
		return listingCategory;
	}
	public void setListingCategory(String listingCategory) {
		this.listingCategory = listingCategory;
	}
	public String getListingBriefAddress() {
		return listingBriefAddress;
	}
	public void setListingBriefAddress(String listingBriefAddress) {
		this.listingBriefAddress = listingBriefAddress;
	}
	public String getListingMantra() {
		return listingMantra;
	}
	public void setListingMantra(String listingMantra) {
		this.listingMantra = listingMantra;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText1() {
		return text1;
	}
	public void setText1(String text1) {
		this.text1 = text1;
	}
	public String getText2() {
		return text2;
	}
	public void setText2(String text2) {
		this.text2 = text2;
	}
	public String getText3() {
		return text3;
	}
	public void setText3(String text3) {
		this.text3 = text3;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getSentDate() {
		return sentDate;
	}
	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
    public String getUserNickname() {
		return userNickname;
	}
	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		if (link != null && !link.startsWith("http")) {
			this.link = getServiceLocation() + link;
		} else {
			this.link = link;
		}
	}
}
