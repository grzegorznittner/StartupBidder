package com.startupbidder.web;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.datanucleus.util.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.BaseObject;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.vo.BaseVO;
import com.startupbidder.vo.BidAndUserVO;
import com.startupbidder.vo.BidListVO;
import com.startupbidder.vo.BidVO;
import com.startupbidder.vo.CommentListVO;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.MonitorListVO;
import com.startupbidder.vo.MonitorVO;
import com.startupbidder.vo.NotificationListVO;
import com.startupbidder.vo.NotificationVO;
import com.startupbidder.vo.SystemPropertyVO;
import com.startupbidder.vo.UserBasicVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.ListingFacade.UpdateReason;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class ServiceFacade {
	private static final Logger log = Logger.getLogger(ServiceFacade.class.getName());
	private static ServiceFacade instance;
	
	private DateTimeFormatter timeStampFormatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss_SSS");

	public static ServiceFacade instance() {
		if (instance == null) {
			instance = new ServiceFacade();
		}
		return instance;
	}
	
	private ServiceFacade() {
	}
	
	public ObjectifyDatastoreDAO getDAO () {
		return ObjectifyDatastoreDAO.getInstance();
	}
	
	/**
	 * Returns list of listing's comments
	 * 
	 * @param listingId Listing id
	 * @param cursor Cursor string
	 * @return List of comments
	 */
	public CommentListVO getCommentsForListing(UserVO loggedInUser, String listingId, ListPropertiesVO commentProperties) {
		CommentListVO list = new CommentListVO();
		ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(BaseVO.toKeyId(listingId)));
		if (listing == null) {
			log.log(Level.WARNING, "Listing '" + listingId + "' not found");

			commentProperties.setNumberOfResults(0);
			commentProperties.setStartIndex(0);
			commentProperties.setTotalResults(0);
		} else {
			ListingFacade.instance().applyListingData(loggedInUser, listing);
			List<CommentVO> comments = DtoToVoConverter.convertComments(
					getDAO().getCommentsForListing(BaseVO.toKeyId(listingId)));
			int index = commentProperties.getStartIndex() > 0 ? commentProperties.getStartIndex() : 1;
			for (CommentVO comment : comments) {
				comment.setUserName(getDAO().getUser(comment.getUser()).nickname);
				comment.setOrderNumber(index++);
			}
			list.setComments(comments);
			list.setListing(listing);

			commentProperties.setNumberOfResults(comments.size());
			commentProperties.setStartIndex(0);
			commentProperties.setTotalResults(comments.size());
		}
		list.setCommentsProperties(commentProperties);

		return list;
	}
	
	/**
	 * Returns list of user's comments
	 * @param listingId User id
	 * @param cursor Cursor string
	 * @return List of comments
	 */
	public CommentListVO getCommentsForUser(UserVO loggedInUser, String userId, ListPropertiesVO commentProperties) {
		CommentListVO list = new CommentListVO();

		UserVO user = UserMgmtFacade.instance().getUser(loggedInUser, userId).getUser();
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
			commentProperties.setNumberOfResults(0);
			commentProperties.setStartIndex(0);
			commentProperties.setTotalResults(0);
		} else {
			List<CommentVO> comments = DtoToVoConverter.convertComments(
					getDAO().getCommentsForUser(BaseVO.toKeyId(userId)));
			int index = commentProperties.getStartIndex() > 0 ? commentProperties.getStartIndex() : 1;
			for (CommentVO comment : comments) {
				comment.setUserName(user.getNickname());
				Listing listing = getDAO().getListing(comment.toKeyId());
				if (listing == null) {
					log.log(Level.SEVERE, "Comment '" + comment.getId() + "' doesn't have listing id");
				}
				comment.setListingName(listing.name);
				comment.setOrderNumber(index++);
			}
			list.setComments(comments);

			commentProperties.setNumberOfResults(comments.size());
			commentProperties.setStartIndex(0);
			commentProperties.setTotalResults(comments.size());
		}
		list.setCommentsProperties(commentProperties);
		list.setUser(new UserBasicVO(user));
		return list;
	}
	


	/**
	 * Returns listing's rating
	 * @param listingId Listing id
	 * @return Current rating
	 */
	public int getRating(User loggedInUser, String listingId) {
		return getDAO().getNumberOfVotesForListing(BaseVO.toKeyId(listingId));
	}
	
	/**
	 * Returns listings's activity (number of comments)
	 * @param listingId Business plan id
	 * @return Activity
	 */
	public int getActivity(User loggedInUser, String listingId) {
		return getDAO().getActivity(BaseVO.toKeyId(listingId));
	}
 
	public CommentVO getComment(UserVO loggedInUser, String commentId) {
		return DtoToVoConverter.convert(getDAO().getComment(commentId));
	}

	public CommentVO deleteComment(UserVO loggedInUser, String commentId) {
		getDAO().deleteComment(BaseVO.toKeyId(commentId));
		return null;
	}

	public CommentVO createComment(UserVO loggedInUser, CommentVO comment) {
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			return null;
		}
		
		Comment commentDTO = VoToModelConverter.convert(comment);
		comment = DtoToVoConverter.convert(getDAO().createComment(commentDTO));

		Monitor monitor = new Monitor();
		monitor.object = new Key<Monitor.Monitored>(Listing.class, commentDTO.listing.getId());
		monitor.type = Monitor.Type.LISTING;
		monitor.user = new Key<SBUser>(SBUser.class, loggedInUser.toKeyId());
		getDAO().setMonitor(monitor);
		
		UserMgmtFacade.instance().scheduleUpdateOfUserStatistics(loggedInUser.getId(), UserMgmtFacade.UpdateReason.NEW_COMMENT);
		ListingFacade.instance().scheduleUpdateOfListingStatistics(comment.getListing(), UpdateReason.NEW_COMMENT);
		
		//createNotification(comment.get, comment.getId(), Type.NEW_COMMENT_FOR_YOUR_LISTING, "");
		return comment;
	}

	public CommentVO updateComment(UserVO loggedInUser, CommentVO comment) {
		if (StringUtils.isEmpty(comment.getComment())) {
			log.warning("Comment '" + comment.getId() + "' cannot be updated with empty text");
			return null;
		}
		comment = DtoToVoConverter.convert(getDAO().updateComment(VoToModelConverter.convert(comment)));
		return comment;
	}

	public SystemPropertyVO getSystemProperty(UserVO loggedInUser, String name) {
		return DtoToVoConverter.convert(getDAO().getSystemProperty(name));
	}

	public List<SystemPropertyVO> getSystemProperties(UserVO loggedInUser) {
		return DtoToVoConverter.convertSystemProperties(getDAO().getSystemProperties());
	}

	public SystemPropertyVO setSystemProperty(UserVO loggedInUser, SystemPropertyVO property) {
		if (loggedInUser == null) {
			return null;
		}
		property.setAuthor(loggedInUser.getEmail());
		return DtoToVoConverter.convert(getDAO().setSystemProperty(VoToModelConverter.convert(property)));
	}

	public ListingDocumentVO deleteDocument(UserVO loggedInUser, String docId) {
		getDAO().deleteDocument(BaseVO.toKeyId(docId));
		return null;
	}
	
	public String[] createUploadUrls(UserVO loggedInUser, String uploadUrl, int numberOfUrls) {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		String[] urls = new String[numberOfUrls];
		while (numberOfUrls > 0) {
			String discreteUploadUrl = uploadUrl + (uploadUrl.endsWith("/") ? "" : "/") ;
			discreteUploadUrl += "" + new Date().getTime() + numberOfUrls + loggedInUser.hashCode();
			urls[--numberOfUrls] = blobstoreService.createUploadUrl(discreteUploadUrl);
		}
		return urls;
	}

	public NotificationVO createNotification(UserVO loggedInUser, NotificationVO notification) {
		try {
			Notification.Type.valueOf(notification.getType().toUpperCase());
		} catch (Exception e) {
			log.log(Level.WARNING, "Notification cannot be created as type is empty or not recognized!", e);
		}
		if (StringUtils.isEmpty(notification.getUser())) {
			log.warning("Notification cannot be created as user is empty!");
		}
		notification.setCreated(new Date());
		notification.setAcknowledged(false);
		notification.setEmailDate(null);
		notification = DtoToVoConverter.convert(getDAO().createNotification(VoToModelConverter.convert(notification)));
		if (notification != null) {
			scheduleNotification(loggedInUser.getId(), notification);
		}
		return notification;
	}
	
	public void createNotification(String userId, String objectId, Notification.Type type, String message) {
		Notification notification = new Notification();
		notification.user = new Key<SBUser>(SBUser.class, userId);
		notification.object = new Key<BaseObject>(objectId);
		notification.type = type;
		notification.created = new Date();
		notification.acknowledged = false;
		notification.emailDate = null;
		notification = getDAO().createNotification(notification);
		if (notification != null) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notification.type + "_" + notification.user.getId();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notification.id)
					.taskName(taskName));
		} else {
			log.warning("Can't schedule notification " + notification);
		}
	}

	private void scheduleNotification(String userId, NotificationVO notification) {
		String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notification.getType() + "_" + userId;
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", notification.getId())
				.taskName(taskName));
	}

	public NotificationVO acknowledgeNotification(UserVO loggedInUser, String notifId) {
		NotificationVO notification = DtoToVoConverter.convert(
				getDAO().acknowledgeNotification(BaseVO.toKeyId(notifId)));
		if (notification == null) {
			log.warning("Notification with id '" + notifId + "' not found!");
		} else {
			log.info("Notification with id '" + notifId + "' was acknowledged.");
		}
		return notification;
	}

	public NotificationListVO getAllNotificationsForUser(UserVO loggedInUser, String userId, ListPropertiesVO notifProperties) {
		NotificationListVO list = new NotificationListVO();
		List<NotificationVO> notifications = null;

		UserVO user = UserMgmtFacade.instance().getUser(loggedInUser, userId).getUser();
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
			return null;
		}

		notifications = DtoToVoConverter.convertNotifications(
				getDAO().getUserNotification(BaseVO.toKeyId(userId), notifProperties));
		notifProperties.setNumberOfResults(notifications.size());
		prepareNotificationList(notifications, user);
		list.setNotifications(notifications);
		list.setNotificationsProperties(notifProperties);
		list.setUser(new UserBasicVO(user));
		
		return list;
	}

	public NotificationListVO getNotificationsForUser(UserVO loggedInUser, String userId, ListPropertiesVO notifProperties) {
		NotificationListVO list = new NotificationListVO();
		List<NotificationVO> notifications = null;

		//UserVO user = getUser(loggedInUser, userId).getUser();
		if (loggedInUser == null) {
			log.log(Level.WARNING, "User not logged in!");
			return null;
		}
		userId = loggedInUser.getId();

		notifications = DtoToVoConverter.convertNotifications(
				getDAO().getAllUserNotification(BaseVO.toKeyId(userId), notifProperties));
		notifProperties.setTotalResults(notifications.size());
		prepareNotificationList(notifications, loggedInUser);
		list.setNotifications(notifications);
		list.setNotificationsProperties(notifProperties);
		list.setUser(new UserBasicVO(loggedInUser));
		
		return list;
	}

	private void prepareNotificationList(List<NotificationVO> notifications, UserVO user) {
		for (NotificationVO notif : notifications) {
			notif.setUserName(user.getName());
		}
	}

	public NotificationVO getNotification(UserVO loggedInUser, String notifId) {
		NotificationVO notification = DtoToVoConverter.convert(
				getDAO().getNotification(BaseVO.toKeyId(notifId)));
		if (notification == null) {
			log.warning("Notification with id '" + notifId + "' not found!");
		}
		return notification;
	}
	
	public MonitorVO setMonitor(UserVO loggedInUser, MonitorVO monitor) {
		if (StringUtils.isEmpty(monitor.getObjectId())) {
			log.warning("Monitored object id is empty!");
			return null;
		}
		if (StringUtils.isEmpty(monitor.getType())) {
			log.warning("Monitored object type is empty!");
			return null;
		}
		monitor.setActive(true);
		monitor = DtoToVoConverter.convert(getDAO().setMonitor(VoToModelConverter.convert(monitor)));
		return monitor;
	}

	public MonitorVO deactivateMonitor(UserVO loggedInUser, String monitorId) {
		MonitorVO notification = DtoToVoConverter.convert(
				getDAO().deactivateMonitor(BaseVO.toKeyId(monitorId)));
		if (notification == null) {
			log.warning("Monitor with id '" + monitorId + "' not found!");
		} else {
			log.info("Monitor with id '" + monitorId + "' was deactivated.");
		}
		return notification;
	}

	public MonitorListVO getMonitorsForObject(UserVO loggedInUser, String objectId, String type) {
		MonitorListVO list = new MonitorListVO();
		List<MonitorVO> monitors = null;

		if (StringUtils.isEmpty(objectId)) {
			log.warning("Parameter objectId not provided!");
			return null;
		}
		if (StringUtils.isEmpty(type)) {
			log.warning("Parameter type not provided!");
			return null;
		}
		Monitor.Type typeEnum = null;
		try {
			typeEnum = Monitor.Type.valueOf(type.toUpperCase());
		} catch (Exception e) {
			log.log(Level.WARNING, "Parameter type has wrong value", e);
		}
		monitors = DtoToVoConverter.convertMonitors(
				getDAO().getMonitorsForObject(BaseVO.toKeyId(objectId), typeEnum));
		list.setMonitors(monitors);
		
		return list;
	}

	public MonitorListVO getMonitorsForUser(UserVO loggedInUser, String userId, String type) {
		MonitorListVO list = new MonitorListVO();
		List<MonitorVO> monitors = null;

		Monitor.Type typeEnum = null;
		if (StringUtils.notEmpty(type)) {
			try {
				typeEnum = Monitor.Type.valueOf(type.toUpperCase());
			} catch (Exception e) {
				log.log(Level.WARNING, "Parameter type has wrong value", e);
			}
		}
		if (StringUtils.isEmpty(userId) && loggedInUser != null) {
			userId = loggedInUser.getId();
		}
		monitors = DtoToVoConverter.convertMonitors(
				getDAO().getMonitorsForUser(BaseVO.toKeyId(userId), typeEnum));
		list.setMonitors(monitors);
		list.setUser(loggedInUser);
		
		return list;
	}

}
