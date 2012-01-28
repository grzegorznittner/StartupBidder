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
public class NotificationVO {

	@JsonProperty("notification_id")
	private String id;
	@JsonProperty("mockData")
	private boolean mockData;
	@JsonProperty("profile_id")
	private String user;
	@JsonProperty("profile_username")
	private String userName;
	@JsonProperty("type")
	@JsonSerialize(using=LowecaseSerializer.class)
	private String type;
	@JsonProperty("object_id")
	private String object;
	@JsonProperty("message")
	private String message;
	@JsonProperty("create_date")
	@JsonSerialize(using=DateSerializer.class)
	private Date   created;
	@JsonProperty("email_date")
	@JsonSerialize(using=DateSerializer.class)
	private Date   emailDate;
	@JsonProperty("ackn")
	private boolean acknowledged;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isMockData() {
		return mockData;
	}
	public void setMockData(boolean mockData) {
		this.mockData = mockData;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getEmailDate() {
		return emailDate;
	}
	public void setEmailDate(Date emailDate) {
		this.emailDate = emailDate;
	}
	public boolean isAcknowledged() {
		return acknowledged;
	}
	public void setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	@JsonProperty("link")
	public String getLink() {
		String link = "";
		Notification.Type type = Notification.Type.valueOf(this.type);
		switch (type) {
		case BID_PAID_FOR_YOUR_LISTING:
		case BID_WAS_WITHDRAWN:
		case YOUR_BID_WAS_ACCEPTED:
		case YOUR_BID_WAS_ACTIVATED:
		case YOUR_BID_WAS_REJECTED:
		case NEW_BID_FOR_YOUR_LISTING:
		case YOU_PAID_BID:
		case YOU_ACCEPTED_BID:
			// link to bid
			link = "/bid/get/?id=" + this.object;
		break;
		case NEW_COMMENT_FOR_YOUR_LISTING:
		case NEW_COMMENT_FOR_MONITORED_LISTING:
			// link to comment
			link = "/comment/get/?id=" + this.object;
		break;
		case YOUR_PROFILE_WAS_MODIFIED:
		case NEW_VOTE_FOR_YOU:
			// link to profile
			link = "/user/get/?id=" + this.object;
		break;
		case NEW_VOTE_FOR_YOUR_LISTING:
		case NEW_LISTING:
			// link to listing
			link = "/listing/get/?id=" + this.object;
		break;
		default:
			link = "not_recognized";
		}
		return link;
	}
	@Override
	public String toString() {
		return "NotificationVO [id=" + id + ", mockData=" + mockData
				+ ", user=" + user + ", userName=" + userName + ", type="
				+ type + ", object=" + object + ", message="
				+ message + ", created=" + created + ", emailDate=" + emailDate
				+ ", acknowledged=" + acknowledged + "]";
	}
}
