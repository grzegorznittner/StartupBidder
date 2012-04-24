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

	@JsonProperty("notify_id")
	private String id;
	@JsonProperty("notify_type")
	@JsonSerialize(using=LowecaseSerializer.class)
	private String type;
	@JsonProperty("listing_id")
	private String listing;
	@JsonProperty("title")
	private String title;
	@JsonProperty("text")
	private String text;
	@JsonProperty("create_date")
	@JsonSerialize(using=DateSerializer.class)
	private Date   created;
	@JsonProperty("sent_date")
	@JsonSerialize(using=DateSerializer.class)
	private Date   sentDate;
	@JsonProperty("read")
	private boolean read;
	/* It's not sent as JSON */
	private String user;
	
	@Override
	public String getId() {
		return id;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
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
	public void setId(String id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	@JsonProperty("link")
	public String getLink() {
		String link = "";
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
			// link to bid
			link = "/company-page.html?page=bids&id=" + this.listing;
		break;
		case NEW_COMMENT_FOR_YOUR_LISTING:
		case NEW_COMMENT_FOR_MONITORED_LISTING:
			// link to comment
			link = "/company-page.html?page=comments&id=" + this.listing;
		break;
		case NEW_LISTING:
			// link to listing
			link = "/company-page.html?id=" + this.listing;
		break;
		default:
			link = "not_recognized";
		}
		return link;
	}
}
