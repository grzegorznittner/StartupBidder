package com.startupbidder.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.startupbidder.dao.ObjectifyDatastoreDAO.CursorHandler;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.UserVO;

/**
 * Datastore implementation which uses Google's AppEngine Datastore through Objectify interfaces.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class NotificationObjectifyDatastoreDAO {
	private static final Logger log = Logger.getLogger(NotificationObjectifyDatastoreDAO.class.getName());
	static NotificationObjectifyDatastoreDAO instance;
		
	public static NotificationObjectifyDatastoreDAO getInstance() {
		if (instance == null) {
			instance = new NotificationObjectifyDatastoreDAO();
		}
		return instance;
	}

	private NotificationObjectifyDatastoreDAO() {
	}
	
	private Objectify getOfy() {
		Objectify ofy = ObjectifyService.begin();
		return ofy;
	}
	
	public List<Notification> getAllUserNotifications(UserVO user, ListPropertiesVO listProperties) {
		return getAllUserNotifications(VoToModelConverter.convert(user), listProperties);
	}

	public List<Notification> getAllUserNotifications(SBUser user, ListPropertiesVO listProperties) {
		Query<Notification> query = getOfy().query(Notification.class)
				.filter("user =", user.getKey())
				.order("-created")
       			.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<Notification>> keyList = new CursorHandler<Notification>().handleQuery(listProperties, query);
		List<Notification> notifs = new ArrayList<Notification>(getOfy().get(keyList).values());
		return notifs;
	}

	public List<Notification> getUnreadUserNotifications(SBUser user, ListPropertiesVO listProperties) {
		Query<Notification> query = getOfy().query(Notification.class)
				.filter("user =", user.getKey())
				.filter("read =", false)
				.order("-created")
       			.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<Notification>> keyList = new CursorHandler<Notification>().handleQuery(listProperties, query);
		List<Notification> notifs = new ArrayList<Notification>(getOfy().get(keyList).values());
		return notifs;
	}

	public List<Notification> getUnreadListingNotifications(SBUser user, Listing listing, ListPropertiesVO listProperties) {
		Query<Notification> query = getOfy().query(Notification.class)
				.filter("user =", user.getKey())
				.filter("listing =", listing.getKey())
				.filter("read =", false)
				.order("-created")
       			.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<Notification>> keyList = new CursorHandler<Notification>().handleQuery(listProperties, query);
		List<Notification> notifs = new ArrayList<Notification>(getOfy().get(keyList).values());
		return notifs;
	}

	public Notification[] storeNotification(Notification ... notifications) {
		getOfy().put(notifications);
		return notifications;
	}

	public Notification[] storeNotifications(List<Notification> notifications) {
		getOfy().put(notifications);
		return notifications.toArray(new Notification[]{});
	}

	public Notification getNotification(long notifId) {
		return getOfy().find(new Key<Notification>(Notification.class, notifId));
	}
}
