package com.startupbidder.dao;

import java.util.*;
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
import com.startupbidder.web.UserMgmtFacade;

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

    public List<Notification> getAllUserNotificationsAndMarkRead(SBUser user, ListPropertiesVO listProperties) {
        Query<Notification> query = getOfy().query(Notification.class)
                .filter("user =", user.getKey())
                .order("-created")
                .chunkSize(listProperties.getMaxResults())
                .prefetchSize(listProperties.getMaxResults());
        List<Key<Notification>> keyList = new CursorHandler<Notification>().handleQuery(listProperties, query);
        List<Notification> notifications = new ArrayList<Notification>(getOfy().get(keyList).values());
        
        // need to mark all notifications as read, but without changing the returned notifications,
        // so that user still knows what was unread before so it is highlighted
        Set<Notification> markedRead = new HashSet<Notification>();
        for (Notification notification : notifications) {
            if (!notification.read) {
                notification.read = true;
                markedRead.add(notification);
            }
        }
        if (markedRead.size() > 0) {
            getOfy().put(notifications); // toss to DB
            UserMgmtFacade.instance().calculateUserStatistics(user.getKey().getString()); // update stats so num_notifications is correct
            // now flip back so user knows what was unread before
            for (Notification notification : markedRead) {
                notification.read = false; // object pointer should cause this to update the notifications list
            }
        }
        return notifications;
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

	public Notification[] storeNotification(Notification ... notificationsArray) {
        List<Notification> notifications = new ArrayList<Notification>();
        for (Notification notification : notificationsArray) {
            notifications.add(notification);
        }
        return storeNotifications(notifications);
	}

	public Notification[] storeNotifications(List<Notification> notifications) {
        // check for missing create dates
        for (Notification notification : notifications) {
            if (notification.created == null) {
                notification.created = new Date();
            }
        }
		getOfy().put(notifications);
        updateUserStatsForNotifications(notifications);
		return notifications.toArray(new Notification[]{});
	}
    
    private void updateUserStatsForNotifications(List<Notification> notifications) {
        // first find unique users
        Set<Key<SBUser>> users = new HashSet<Key<SBUser>>();
        for (Notification notification: notifications) {
            users.add(notification.user);
        }
        for (Key<SBUser> user : users) {
            UserMgmtFacade.instance().calculateUserStatistics(user.getString()); // update stats so num_notifications is correct 
        }
    }
    
	public Notification getNotification(long notifId) {
		return getOfy().find(new Key<Notification>(Notification.class, notifId));
	}
}
