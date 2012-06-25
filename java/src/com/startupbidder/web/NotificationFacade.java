package com.startupbidder.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.startupbidder.dao.BidObjectifyDatastoreDAO;
import com.startupbidder.dao.MessageObjectifyDatastoreDAO;
import com.startupbidder.dao.NotificationObjectifyDatastoreDAO;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.Listing.State;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.Notification.Type;
import com.startupbidder.datamodel.PrivateMessage;
import com.startupbidder.datamodel.QuestionAnswer;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.vo.BaseVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.NotificationListVO;
import com.startupbidder.vo.NotificationVO;
import com.startupbidder.vo.UserVO;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class NotificationFacade {
	private static final Logger log = Logger.getLogger(NotificationFacade.class.getName());
	private static NotificationFacade instance;
	
	private DateTimeFormatter timeStampFormatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss_SSS");

	public static NotificationFacade instance() {
		if (instance == null) {
			instance = new NotificationFacade();
		}
		return instance;
	}
	
	private NotificationFacade() {
	}
	
	public NotificationObjectifyDatastoreDAO getDAO () {
		return NotificationObjectifyDatastoreDAO.getInstance();
	}
	public ObjectifyDatastoreDAO getListingDAO () {
		return ObjectifyDatastoreDAO.getInstance();
	}
	public ObjectifyDatastoreDAO getUserDAO () {
		return ObjectifyDatastoreDAO.getInstance();
	}
	public BidObjectifyDatastoreDAO getBidDAO () {
		return BidObjectifyDatastoreDAO.getInstance();
	}
	public MessageObjectifyDatastoreDAO getMessageDAO () {
		return MessageObjectifyDatastoreDAO.getInstance();
	}

	public NotificationListVO getUnreadNotificationsForUser(UserVO loggedInUser, ListPropertiesVO notifProperties) {
		NotificationListVO list = new NotificationListVO();
		if (loggedInUser == null) {
			list.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			list.setErrorMessage("User not logged in.");
			log.log(Level.WARNING, "User not logged in!");
			return list;
		}
		List<NotificationVO> notifications = DtoToVoConverter.convertNotifications(
				getDAO().getUnreadUserNotifications(VoToModelConverter.convert(loggedInUser), notifProperties));
		notifProperties.setNumberOfResults(notifications.size());
		list.setNotifications(notifications);
		list.setNotificationsProperties(notifProperties);
		
		return list;
	}

    public NotificationListVO getNotificationsForUser(UserVO loggedInUser, ListPropertiesVO notifProperties) {
        NotificationListVO list = new NotificationListVO();
        if (loggedInUser == null) {
            list.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
            list.setErrorMessage("User not logged in.");
            log.log(Level.WARNING, "User not logged in!");
            return list;
        }
        List<NotificationVO> notifications = null;

        notifications = DtoToVoConverter.convertNotifications(
                getDAO().getAllUserNotifications(VoToModelConverter.convert(loggedInUser), notifProperties));
        notifProperties.setTotalResults(notifications.size());
        list.setNotifications(notifications);
        list.setNotificationsProperties(notifProperties);

        return list;
    }

    public NotificationListVO getNotificationsForUserAndMarkRead(UserVO loggedInUser, ListPropertiesVO notifProperties) {
        NotificationListVO list = new NotificationListVO();
        if (loggedInUser == null) {
            list.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
            list.setErrorMessage("User not logged in.");
            log.log(Level.WARNING, "User not logged in!");
            return list;
        }
        List<NotificationVO> notifications = null;

        notifications = DtoToVoConverter.convertNotifications(
                getDAO().getAllUserNotificationsAndMarkRead(VoToModelConverter.convert(loggedInUser), notifProperties));
        notifProperties.setTotalResults(notifications.size());
        list.setNotifications(notifications);
        list.setNotificationsProperties(notifProperties);

        return list;
    }

	public NotificationVO getNotification(UserVO loggedInUser, String notifId) {
		Notification notification = getDAO().getNotification(BaseVO.toKeyId(notifId));
		if (notification == null) {
			log.warning("Notification with id '" + notifId + "' not found!");
		}
		notification.read = true;
		getDAO().storeNotification(notification);
		NotificationVO notificationVO = DtoToVoConverter.convert(notification);
		return notificationVO;
	}

	public void scheduleCommentNotification(Comment comment) {
		String taskName = timeStampFormatter.print(new Date().getTime()) + "schedule_comment_notification_" + comment.getWebKey();
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/schedule-comment-notifications")
				.param("id", "" + comment.getWebKey())
				.taskName(taskName));
	}

	public void scheduleQANotification(QuestionAnswer qa) {
		String taskName = timeStampFormatter.print(new Date().getTime()) + "schedule_qa_notification_" + qa.getWebKey();
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/schedule-qa-notifications")
				.param("id", "" + qa.getWebKey())
				.taskName(taskName));
	}

	public void schedulePrivateMessageNotification(PrivateMessage message) {
		String taskName = timeStampFormatter.print(new Date().getTime()) + "schedule_message_notification_" + message.getWebKey();
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/schedule-message-notifications")
				.param("id", "" + message.getWebKey())
				.taskName(taskName));
	}

	public void scheduleListingStateNotification(Listing listing) {
		String taskName = timeStampFormatter.print(new Date().getTime()) + "schedule_listing_notification_" + listing.getWebKey();
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/schedule-listing-notifications")
				.param("id", "" + listing.getWebKey())
				.taskName(taskName));
	}

	public void scheduleBidNotification(Bid bid) {
		String taskName = timeStampFormatter.print(new Date().getTime()) + "schedule_bid_notification_" + bid.getWebKey();
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/schedule-bid-notifications")
				.param("id", "" + bid.getWebKey())
				.taskName(taskName));
	}

	public List<NotificationVO> createListingStateNotification(String listingId) {
		Listing listing = getListingDAO().getListing(BaseVO.toKeyId(listingId));
		SBUser listingOwner = getUserDAO().getUser(listing.owner.getString());
		List<Notification> toStore = new ArrayList<Notification>();
		
		Notification notification = new Notification(listing, listingOwner);
		switch (listing.state) {
		case NEW:
			notification.message = "Listing has been sent back for correction";
			break;
		case POSTED:
			notification.message = "Listing has been posted by an owner";
			notification.type = Notification.Type.NEW_LISTING;
			break;
		case ACTIVE:
			notification.message = "Listing has been activated";
			notification.type = Notification.Type.LISTING_ACTIVATED;
			break;
		case FROZEN:
			notification.message = "Listing has been frozen";
			notification.type = Notification.Type.LISTING_FROZEN;
			break;
		case WITHDRAWN:
			notification.message = "Listing has been withdrawn";
			notification.type = Notification.Type.LISTING_WITHDRAWN;
			break;
		case CLOSED:
			notification.message = "Listing has been closed";
			break;
		}
		
		if (listing.state == State.NEW || listing.state == State.ACTIVE || listing.state == State.FROZEN) {
			Notification ownerNotif = (Notification)notification.copy();
			ownerNotif.user = listingOwner.getKey();
			ownerNotif.userEmail = listingOwner.email;
			ownerNotif.userNickname = listingOwner.nickname;
			log.info("Creating notification: " + ownerNotif);
			toStore.add(ownerNotif);
		}

		if (listing.state == State.FROZEN || listing.state == State.ACTIVE 
				|| listing.state == State.WITHDRAWN || listing.state == State.CLOSED) {
			Notification monitoredNotif = null;
			ListPropertiesVO props = new ListPropertiesVO();
			props.setMaxResults(1000);
			for (Monitor monitor : getListingDAO().getMonitorsForListing(listing.id, props)) {
				if (monitor.userEmail != null) {
					monitoredNotif = (Notification)notification.copy();
					monitoredNotif.user = monitor.user;
					monitoredNotif.userEmail = monitor.userEmail;
					monitoredNotif.userNickname = monitor.userNickname;
					log.info("Creating notification: " + monitoredNotif);
					toStore.add(monitoredNotif);
				}
			}
		}

		if (!toStore.isEmpty()) {
			for (Notification notif : getDAO().storeNotifications(toStore)) {
				String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notif.type + "_" + notif.user.getId();
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notif.getWebKey())
						.taskName(taskName));
			}
		}
		return DtoToVoConverter.convertNotifications(toStore);
	}
	
	public List<NotificationVO> createCommentNotification(String commentId) {
		Comment comment = getListingDAO().getComment(BaseVO.toKeyId(commentId));
		Listing listing = getListingDAO().getListing(comment.listing.getId());
		SBUser listingOwner = getUserDAO().getUser(listing.owner.getString());
		Notification notification = new Notification(listing, listingOwner);
		List<Notification> toStore = new ArrayList<Notification>();
		
		if (listing.owner.getId() != comment.user.getId()) {
			// comment from user, we need to notify monitoring users and owner
			Notification ownerNotif = (Notification)notification.copy();
			ownerNotif.type = Notification.Type.NEW_COMMENT_FOR_YOUR_LISTING;
			ownerNotif.message = comment.comment;
			ownerNotif.user = listingOwner.getKey();
			ownerNotif.userEmail = listingOwner.email;
			ownerNotif.userNickname = listingOwner.nickname;
			log.info("Creating notification: " + ownerNotif);
			toStore.add(ownerNotif);
		}
		
		Notification monitoredNotif = null;
		ListPropertiesVO props = new ListPropertiesVO();
		props.setMaxResults(1000);
		for (Monitor monitor : getListingDAO().getMonitorsForListing(listing.id, props)) {
			if (monitor.userEmail != null) {
				monitoredNotif = (Notification)notification.copy();
				monitoredNotif.type = Notification.Type.NEW_COMMENT_FOR_MONITORED_LISTING;
				monitoredNotif.message = comment.comment;
				monitoredNotif.user = monitor.user;
				monitoredNotif.userEmail = monitor.userEmail;
				monitoredNotif.userNickname = monitor.userNickname;
				log.info("Creating notification: " + monitoredNotif);
				toStore.add(monitoredNotif);
			}
		}
		
		for (Notification notif : getDAO().storeNotifications(toStore)) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notif.type + "_" + notif.user.getId();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notif.getWebKey())
					.taskName(taskName));
		}
		return DtoToVoConverter.convertNotifications(toStore);
	}

	public NotificationVO createBidNotification(String bidId) {
		Bid bid = getBidDAO().getBid(BaseVO.toKeyId(bidId));
		Listing listing = getListingDAO().getListing(bid.listing.getId());
		SBUser listingOwner = getUserDAO().getUser(listing.owner.getString());
		SBUser toUser = getUserDAO().getUser(bid.userB.getString());
		SBUser investor = getUserDAO().getUser(bid.userA.equals(listing.owner) ? bid.userB.getString() : bid.userA.getString());
		
		Notification notification = new Notification(listing, listingOwner);
		notification.user = toUser.getKey();
		notification.userEmail = toUser.email;
		notification.userNickname = toUser.nickname;
		notification.investor = investor.getKey();
		switch (bid.type) {
		case INVESTOR_POST:
			notification.type = Notification.Type.NEW_BID_FOR_YOUR_LISTING;
			break;
		case INVESTOR_COUNTER:
		case OWNER_COUNTER:
			notification.type = Notification.Type.YOUR_BID_WAS_COUNTERED;
			break;
		case OWNER_ACCEPT:
		case INVESTOR_ACCEPT:
			notification.type = Notification.Type.YOUR_BID_WAS_ACCEPTED;
			break;
		case INVESTOR_REJECT:
		case OWNER_REJECT:
			notification.type = Notification.Type.YOUR_BID_WAS_REJECTED;
			break;
		case INVESTOR_WITHDRAW:
		case OWNER_WITHDRAW:
			notification.type = Notification.Type.BID_WAS_WITHDRAWN;
			break;
		}
		log.info("Creating notification: " + notification);
		notification = getDAO().storeNotification(notification)[0];
		if (notification != null) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notification.type + "_" + notification.user.getId();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notification.getWebKey())
					.taskName(taskName));
		} else {
			log.warning("Can't schedule notification " + notification);
		}
		return DtoToVoConverter.convert(notification);
	}

	public NotificationVO createPrivateMessageNotification(String messageId) {
		PrivateMessage message = getMessageDAO().getMessage(BaseVO.toKeyId(messageId));
		
		Notification notification = new Notification();
		notification.user = message.userB;
		notification.userEmail = message.userBEmail;
		notification.userNickname = message.userBNickname;
		notification.type = Type.PRIVATE_MESSAGE;
		notification.fromUserNickname = message.userANickname;
		notification.message = message.text;
		notification.read = false;
		log.info("Creating notification: " + notification);
		notification = getDAO().storeNotification(notification)[0];
		if (notification != null) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notification.type + "_" + notification.user.getId();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notification.getWebKey())
					.taskName(taskName));
		} else {
			log.warning("Can't schedule notification " + notification);
		}
		return DtoToVoConverter.convert(notification);
	}

	public NotificationVO createQANotification(String qaId) {
		QuestionAnswer qa = getListingDAO().getQuestionAnswer(BaseVO.toKeyId(qaId));
		Listing listing = getListingDAO().getListing(qa.listing.getId());
		SBUser listingOwner = getUserDAO().getUser(listing.owner.getString());
		
		Notification notification = new Notification(listing, listingOwner);
		if (qa.answerDate != null) {
			// this is answer
			SBUser questionAuthor = getUserDAO().getUser(qa.user.getString());			
			notification.user = qa.user;
			notification.userEmail = questionAuthor.email;
			notification.userNickname = questionAuthor.nickname;
			notification.message = qa.answer;
		} else {
			// this is question
			notification.user = qa.listingOwner;
			notification.userEmail = listingOwner.email;
			notification.userNickname = listingOwner.nickname;
			notification.message = qa.question;
		}
		notification.type = Type.ASK_LISTING_OWNER;
		notification.read = false;
		log.info("Creating notification: " + notification);
		notification = getDAO().storeNotification(notification)[0];
		if (notification != null) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notification.type + "_" + notification.user.getId();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notification.getWebKey())
					.taskName(taskName));
		} else {
			log.warning("Can't schedule notification " + notification);
		}
		return DtoToVoConverter.convert(notification);
	}

}
