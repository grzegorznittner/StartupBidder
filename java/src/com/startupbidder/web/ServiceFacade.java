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
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.vo.BaseVO;
import com.startupbidder.vo.CommentListVO;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingAndUserVO;
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

	public CommentListVO deleteComment(UserVO loggedInUser, String commentId) {
		CommentListVO result = new CommentListVO();
		if (loggedInUser == null) {
			log.log(Level.WARNING, "User is not logged in!", new Exception("Not logged in user"));
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage("User is not logged in!");
			return result;
		}
		CommentVO comment = getComment(loggedInUser, commentId);
		if (loggedInUser.isAdmin()) {
			log.info("Admin is going to delete comment: " + comment);
		} else if (!StringUtils.areStringsEqual(comment.getUser(), loggedInUser.getId())) {
			log.info("Comment author is going to delete comment: " + comment);
		} else {
			Listing listing = getDAO().getListing(BaseVO.toKeyId(comment.getListing()));
			if (listing.id == loggedInUser.toKeyId()) {
				log.info("Listing owner is going to delete comment: " + comment);
			} else {
				log.warning("User '" + loggedInUser.getNickname() + "' is not allowed to delete comment: " + comment);
				result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
				result.setErrorMessage("User is not allowed to delete this comment.");
				return result;
			}
		}
		ListingFacade.instance().scheduleUpdateOfListingStatistics(comment.getListing(), UpdateReason.DELETE_COMMENT);
		getDAO().deleteComment(BaseVO.toKeyId(commentId));
		return getCommentsForListing(loggedInUser, comment.getListing(), new ListPropertiesVO());
	}

	public CommentVO createComment(UserVO loggedInUser, CommentVO comment) {
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			return null;
		}
		
		comment.setUser(loggedInUser.getId());
		comment.setUserName(loggedInUser.getNickname());
		Comment commentDTO = VoToModelConverter.convert(comment);
		comment = DtoToVoConverter.convert(getDAO().createComment(commentDTO));

		Monitor monitor = new Monitor();
		monitor.monitoredListing = new Key<Listing>(Listing.class, commentDTO.listing.getId());
		monitor.user = new Key<SBUser>(SBUser.class, loggedInUser.toKeyId());
		getDAO().setMonitor(monitor);
		
		Listing listing = getDAO().getListing(commentDTO.listing.getId());
		
		UserMgmtFacade.instance().scheduleUpdateOfUserStatistics(loggedInUser.getId(), UserMgmtFacade.UpdateReason.NEW_COMMENT);
		ListingFacade.instance().scheduleUpdateOfListingStatistics(comment.getListing(), UpdateReason.NEW_COMMENT);
		
		createNotification(listing.owner.getString(), comment.getListing(), 
				Notification.Type.NEW_COMMENT_FOR_YOUR_LISTING, "A new comment for listing by " + loggedInUser.getNickname());
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

	public void createNotification(String userId, String listingId, Notification.Type type, String message) {
		if (type == Notification.Type.NEW_COMMENT_FOR_MONITORED_LISTING || 
				type == Notification.Type.NEW_COMMENT_FOR_YOUR_LISTING) {
			List<Notification> notifications = getDAO().getUnreadNotifications(BaseVO.toKeyId(userId), BaseVO.toKeyId(listingId));
			for (Notification not : notifications) {
				if (not.type == Notification.Type.NEW_COMMENT_FOR_MONITORED_LISTING || 
						not.type == Notification.Type.NEW_COMMENT_FOR_YOUR_LISTING) {
					log.info("Notification won't be created as previous one for comment is still unread: " + not);
					return;
				}
			}
		}
		Notification notification = new Notification();
		notification.user = new Key<SBUser>(userId);
		notification.listing = new Key<Listing>(listingId);
		notification.type = type;
		notification.created = new Date();
		notification.read = false;
		notification.sentDate = null;
		notification.text = message;
		notification = getDAO().storeNotification(notification);
		if (notification != null) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notification.type + "_" + notification.user.getId();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notification.getWebKey())
					.taskName(taskName));
		} else {
			log.warning("Can't schedule notification " + notification);
		}
	}

	public NotificationVO markNotificationAsRead(UserVO loggedInUser, String listingId) {
		NotificationVO notification = DtoToVoConverter.convert(
				getDAO().markNotificationAsRead(BaseVO.toKeyId(loggedInUser.getId()), BaseVO.toKeyId(listingId)));
		if (notification == null) {
			log.warning("Notification for user '" + loggedInUser.getNickname() + "' on listing id " + listingId + " not found!");
		} else {
			log.info("Notification for user '" + loggedInUser.getNickname() + "' on listing id " + listingId + " was marked as read.");
		}
		return notification;
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
				getDAO().getUserNotification(loggedInUser.toKeyId(), notifProperties));
		notifProperties.setNumberOfResults(notifications.size());
		list.setNotifications(notifications);
		list.setNotificationsProperties(notifProperties);
		list.setUser(new UserBasicVO(loggedInUser));
		
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
				getDAO().getAllUserNotification(loggedInUser.toKeyId(), notifProperties));
		notifProperties.setTotalResults(notifications.size());
		list.setNotifications(notifications);
		list.setNotificationsProperties(notifProperties);
		list.setUser(new UserBasicVO(loggedInUser));
		
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
	
	public MonitorVO setListingMonitor(UserVO loggedInUser, String listingId) {
		if (loggedInUser == null) {
			log.log(Level.WARNING, "User not logged in!");
			return null;
		}
		if (StringUtils.isEmpty(listingId)) {
			log.warning("Listing id is empty!");
			return null;
		}
		MonitorVO monitor = new MonitorVO();
		monitor.setUser(loggedInUser.getId());
		monitor.setUserName(loggedInUser.getNickname());
		monitor.setListingId(listingId);
		monitor.setType(Monitor.Type.LISTING.toString());
		monitor = DtoToVoConverter.convert(getDAO().setMonitor(VoToModelConverter.convert(monitor)));
		return monitor;
	}

	public MonitorVO deactivateListingMonitor(UserVO loggedInUser, String listingId) {
		MonitorVO monitor = DtoToVoConverter.convert(
				getDAO().deactivateListingMonitor(loggedInUser.toKeyId(), BaseVO.toKeyId(listingId)));
		if (monitor == null) {
			log.warning("Monitor for listing id '" + listingId + "' not found!");
		} else {
			log.info("Monitor for listing id '" + listingId + "' was deactivated.");
		}
		return monitor;
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
