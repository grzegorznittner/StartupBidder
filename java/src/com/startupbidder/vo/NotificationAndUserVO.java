package com.startupbidder.vo;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

import com.startupbidder.web.UserMgmtFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class NotificationAndUserVO extends BaseResultVO implements UserDataUpdatableContainer {
	@JsonProperty("notification") private NotificationVO notification;
	@JsonProperty("listing") private ListingVO listing;
	public void updateUserData() {
		UserMgmtFacade.instance().updateUserData(listing);
	}
	public NotificationVO getNotification() {
		return notification;
	}
	public void setNotification(NotificationVO notification) {
		this.notification = notification;
	}
	public ListingVO getListing() {
		return listing;
	}
	public void setListing(ListingVO listing) {
		this.listing = listing;
	}
}
