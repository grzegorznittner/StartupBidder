package com.startupbidder.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class NotificationListVO extends BaseResultVO {
	@JsonProperty("notifications")
	private List<NotificationVO> notifications;
	@JsonProperty("notifications_props")
	private ListPropertiesVO notificationsProperties;
	@JsonProperty("profile")
	private UserBasicVO user;
	public List<NotificationVO> getNotifications() {
		return notifications;
	}
	public void setNotifications(List<NotificationVO> notifications) {
		this.notifications = notifications;
	}
	public ListPropertiesVO getNotificationsProperties() {
		return notificationsProperties;
	}
	public void setNotificationsProperties(ListPropertiesVO notificationsProperties) {
		this.notificationsProperties = notificationsProperties;
	}
	public UserBasicVO getUser() {
		return user;
	}
	public void setUser(UserBasicVO user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "NotificationListVO [notifications=" + notifications
				+ ", notificationsProperties=" + notificationsProperties
				+ ", user=" + user + ", " + super.toString() + "]";
	}
}
