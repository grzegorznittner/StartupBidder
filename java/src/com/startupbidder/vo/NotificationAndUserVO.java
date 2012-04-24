package com.startupbidder.vo;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class NotificationAndUserVO extends BaseResultVO {
	@JsonProperty("notification") private NotificationVO notification;

	public NotificationVO getNotification() {
		return notification;
	}

	public void setNotification(NotificationVO notification) {
		this.notification = notification;
	}

}
