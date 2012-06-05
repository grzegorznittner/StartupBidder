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
public class ShortNotificationVO extends BaseVO {
	@JsonProperty("notify_type") @JsonSerialize(using=LowecaseSerializer.class)	private String type;
	@JsonProperty("title") private String title;
	private String listing;
	@JsonProperty("text_1") private String text1;	
	@JsonProperty("create_date") @JsonSerialize(using=DateSerializer.class) private Date created;
	@JsonProperty("read") private boolean read;
	public String getId() {
		return "not_set";
	}
	public void setListing(String listing) {
		this.listing = listing;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
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
