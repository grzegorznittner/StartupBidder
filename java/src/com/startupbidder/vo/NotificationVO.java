package com.startupbidder.vo;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.startupbidder.datamodel.Notification;
import com.startupbidder.util.DateSerializer;
import com.startupbidder.util.LowecaseSerializer;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class NotificationVO extends BaseVO {

	@JsonProperty("num") private int orderNumber;
	@JsonProperty("notify_id") private String id;
	@JsonProperty("notify_type") @JsonSerialize(using=LowecaseSerializer.class)	private String type;
    @JsonProperty("user_id") private String user;
	@JsonProperty("user_nickname") private String userNickname;
    @JsonProperty("from_user_id") private String fromUser;
	@JsonProperty("from_user_nickname") private String fromUserNickname;
    @JsonProperty("parent_notify_id") private String parentNotification;
	@JsonProperty("listing_id")	private String listing;
	@JsonProperty("listing_name") private String listingName;
	@JsonProperty("listing_owner") private String listingOwner;
	@JsonProperty("listing_category") private String listingCategory;
	@JsonProperty("listing_brief_address") private String listingBriefAddress;
	@JsonProperty("listing_mantra") private String listingMantra;
	/** The following 3 values are generated based on notification type (are not stored in datastore) */
	@JsonProperty("title") private String title;
	@JsonProperty("text_1") private String text1;
	@JsonProperty("text_2") private String text2;
	@JsonProperty("text_3") private String text3;
	
	@JsonProperty("create_date") @JsonSerialize(using=DateSerializer.class) private Date created;
	@JsonProperty("sent_date") @JsonSerialize(using=DateSerializer.class) private Date sentDate;
	@JsonProperty("read") private boolean read;
	
	@Override
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
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
    public String getFromUserNickname() {
        return fromUserNickname;
    }
    public void setFromUserNickname(String fromUserNickname) {
        this.fromUserNickname = fromUserNickname;
    }
    public String getFromUser() {
        return fromUser;
    }
    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }
    public String getParentNotification() {
        return parentNotification;
    }
    public void setParentNotification(String parentNotification) {
        this.parentNotification = parentNotification;
    }
	@JsonProperty("listing_logo_url")
	public String getListingLogoLink() {
		return getServiceLocation() + "/listing/logo?id=" + this.listing;
	}
	@JsonProperty("link")
	public String getLink() {
		String link = getServiceLocation ();
		Notification.Type type = Notification.Type.valueOf(this.type);
		switch (type) {
		case BID_PAID_FOR_YOUR_LISTING:
		case BID_WAS_WITHDRAWN:
		case YOUR_BID_WAS_ACCEPTED:
		case YOUR_BID_WAS_COUNTERED:
		case YOUR_BID_WAS_REJECTED:
		case NEW_BID_FOR_YOUR_LISTING:
		case YOU_PAID_BID:
		case YOU_ACCEPTED_BID:
			// link to bid page
			link += "/company-page.html?page=bids&id=" + this.listing;
		break;
		case NEW_COMMENT_FOR_YOUR_LISTING:
		case NEW_COMMENT_FOR_MONITORED_LISTING:
			// link to comment page
			link += "/company-page.html?page=comments&id=" + this.listing;
		break;
		case NEW_LISTING:
		case ASK_LISTING_OWNER:
		case PRIVATE_MESSAGE:
			// link to listing
			link += "/company-page.html?id=" + this.listing;
		break;
		default:
			// will point to main page
		}
		return link;
	}
}
