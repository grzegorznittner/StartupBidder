package com.startupbidder.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
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
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.QuestionAnswer;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.vo.BaseVO;
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
import com.startupbidder.vo.PrivateMessageUserVO;
import com.startupbidder.vo.PrivateMessageVO;
import com.startupbidder.vo.QuestionAnswerVO;
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

	public CommentListVO getCommentsForListing(UserVO loggedInUser, String listingId, ListPropertiesVO commentProperties) {
		CommentListVO list = new CommentListVO();
		ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(BaseVO.toKeyId(listingId)));
		if (listing == null) {
			log.log(Level.WARNING, "Listing '" + listingId + "' not found");

			commentProperties.setNumberOfResults(0);
			commentProperties.setStartIndex(0);
			commentProperties.setTotalResults(0);
		} else {
			Monitor monitor = loggedInUser != null ? getDAO().getListingMonitor(loggedInUser.toKeyId(), listing.toKeyId()) : null;
			ListingFacade.instance().applyListingData(loggedInUser, listing, monitor);
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
		Comment comment = getDAO().getComment(BaseVO.toKeyId(commentId));
		if (comment == null) {
			log.log(Level.WARNING, "Comment entity '" + commentId + "' not found");
			return null;
		}
		return DtoToVoConverter.convert(comment);
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
		} else if (!StringUtils.equals(comment.getUser(), loggedInUser.getId())) {
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
		Listing listing = getDAO().getListing(BaseVO.toKeyId(comment.getListing()));
		if (listing == null) {
			log.warning("Listing with id '" + comment.getListing() + "' doesn't exist.");
			return null;
		}
		
		comment.setListingName(listing.name);
		comment.setUser(loggedInUser.getId());
		comment.setUserName(loggedInUser.getNickname());
		Comment commentDTO = VoToModelConverter.convert(comment);
		comment = DtoToVoConverter.convert(getDAO().createComment(commentDTO));
		comment.setListingName(listing.name);

		Monitor monitor = new Monitor();
		monitor.monitoredListing = new Key<Listing>(Listing.class, commentDTO.listing.getId());
		monitor.user = new Key<SBUser>(SBUser.class, loggedInUser.toKeyId());
		getDAO().setMonitor(monitor);
		
		UserMgmtFacade.instance().scheduleUpdateOfUserStatistics(loggedInUser.getId(), UserMgmtFacade.UpdateReason.NEW_COMMENT);
		ListingFacade.instance().scheduleUpdateOfListingStatistics(comment.getListing(), UpdateReason.NEW_COMMENT);
		
		createCommentNotification(commentDTO, Notification.Type.NEW_COMMENT_FOR_YOUR_LISTING);
		return comment;
	}

	public CommentVO updateComment(UserVO loggedInUser, CommentVO comment) {
		if (StringUtils.isEmpty(comment.getId())) {
			log.warning("Comment id was not provided!");
			return null;
		}
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

	public void createCommentNotification(Comment comment, Notification.Type type) {
		Listing listing = getDAO().getListing(comment.listing.getId());
		if (listing.owner.getId() == comment.user.getId()) {
			// we don't send notifications for our own comments
			log.info("Notification for comment id " + comment.id + " won't be sent as it was listing owner's comment.");
			return;
		}
		SBUser listingOwner = getDAO().getUser(listing.owner.getString());
		SBUser toUser = type == Notification.Type.NEW_COMMENT_FOR_YOUR_LISTING ? listingOwner : getDAO().getUser(comment.user.getString());
		
		if (type == Notification.Type.NEW_COMMENT_FOR_MONITORED_LISTING || 
				type == Notification.Type.NEW_COMMENT_FOR_YOUR_LISTING) {
			ListPropertiesVO props = new ListPropertiesVO();
			props.setMaxResults(100);
			List<Notification> notifications = getDAO().getUnreadNotifications(toUser.id, listing.id, props);
			for (Notification notif : notifications) {
				if (notif.type == Notification.Type.NEW_COMMENT_FOR_MONITORED_LISTING || 
						notif.type == Notification.Type.NEW_COMMENT_FOR_YOUR_LISTING) {
					log.info("Notification won't be created as previous one for comment is still unread: " + notif);
					return;
				}
			}
		}
		Notification notification = new Notification();
		notification.userA = new Key<SBUser>(SBUser.class, toUser.id);
		notification.userAEmail = toUser.email;
		notification.userANickname = toUser.nickname;
		notification.listing = new Key<Listing>(Listing.class, listing.id);
		notification.listingName = listing.name;
		notification.listingOwner = listing.owner.getString();
		notification.listingMantra = listing.mantra;
		notification.listingBriefAddress = listing.briefAddress;
		notification.listingCategory = listing.category;
		notification.type = type;
		notification.created = new Date();
		notification.read = false;
		notification.display = true;
		notification.sentDate = null;
        notification.message = comment.comment;
		notification = getDAO().storeNotification(notification)[0];
		if (notification != null) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notification.type + "_" + notification.userA.getId();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notification.getWebKey())
					.taskName(taskName));
		} else {
			log.warning("Can't schedule notification " + notification);
		}
	}

	public void createBidNotification(String toUserId, Bid bid, Notification.Type type) {
		Listing listing = getDAO().getListing(bid.listing.getId());
		SBUser toUser = getDAO().getUser(toUserId);
		
		Notification notification = new Notification();
		notification.userA = new Key<SBUser>(SBUser.class, toUser.id);
		notification.userAEmail = toUser.email;
		notification.userANickname = toUser.nickname;
		notification.listing = new Key<Listing>(Listing.class, listing.id);
		notification.listingName = listing.name;
		notification.listingOwner = listing.owner.getString();
		notification.listingMantra = listing.mantra;
		notification.listingBriefAddress = listing.briefAddress;
		notification.listingCategory = listing.category;
		notification.type = type;
		notification.created = new Date();
		notification.read = false;
		notification.display = true;
		notification.sentDate = null;
		notification = getDAO().storeNotification(notification)[0];
		if (notification != null) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notification.type + "_" + notification.userA.getId();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notification.getWebKey())
					.taskName(taskName));
		} else {
			log.warning("Can't schedule notification " + notification);
		}
	}

	public void createListingActivatedNotification(Listing listing, Notification.Type type) {
		SBUser toUser = getDAO().getUser(listing.owner.getString());
		
		Notification notification = new Notification();
		notification.userA = new Key<SBUser>(SBUser.class, toUser.id);
		notification.userAEmail = toUser.email;
		notification.userANickname = toUser.nickname;
		notification.listing = new Key<Listing>(Listing.class, listing.id);
		notification.listingName = listing.name;
		notification.listingOwner = listing.owner.getString();
		notification.listingMantra = listing.mantra;
		notification.listingBriefAddress = listing.briefAddress;
		notification.listingCategory = listing.category;
		notification.type = type;
		notification.created = new Date();
		notification.read = false;
		notification.display = true;
		notification.sentDate = null;
		notification = getDAO().storeNotification(notification)[0];
		if (notification != null) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notification.type + "_" + notification.userA.getId();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notification.getWebKey())
					.taskName(taskName));
		} else {
			log.warning("Can't schedule notification " + notification);
		}
	}
	
	public QuestionAnswerVO askOwner(UserVO loggedInUser, String text, String listingId) {
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			return null;
		}
		
		Listing listing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (listing == null) {
			log.warning("Listing '" + listingId + "' doesn't exist.");
			return null;
		}
		if (listing.state != Listing.State.ACTIVE) {
			log.warning("Listing '" + listingId + "' is not active.");
			return null;
		}
		SBUser listingOwner = getDAO().getUser(listing.owner.getString());
		SBUser fromUser = VoToModelConverter.convert(loggedInUser);
		if (StringUtils.isEmpty(fromUser.nickname)) {
			// in dev environment sometimes nickname is empty
			fromUser = getDAO().getUser(fromUser.getWebKey());
		}
		log.info("User " + loggedInUser.getNickname() + " is asking question about listing '" + listing.name + "' onwed by user " + listingOwner.nickname);
		QuestionAnswer qa = getDAO().askListingOwner(fromUser, listing, text);
		return DtoToVoConverter.convert(qa);
	}

	public QuestionAnswerVO answerQuestion(UserVO loggedInUser, String messageId, String text) {
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			return null;
		}
		
		QuestionAnswer qa = getDAO().getQuestionAnswer(BaseVO.toKeyId(messageId));
		if (qa == null) {
			log.warning("QuestionAnswer '" + messageId + "' doesn't exist.");
			return null;
		}
		if (loggedInUser.toKeyId() != qa.listingOwner.getId()) {
			log.warning("User '" + loggedInUser.getNickname() + "' is replying to question not addressed to him. Original question: " + qa);
			return null;
		}
		if (qa.answerDate != null) {
			log.warning("Question is already answered.");
			return null;
		}
		Listing listing = getDAO().getListing(qa.listing.getId());
		if (listing == null) {
			log.warning("Listing '" + qa.listing.getString() + "' doesn't exist.");
			return null;
		}
		if (listing.state != Listing.State.ACTIVE) {
			log.warning("Listing '" + qa.listing.getString() + "' is not active.");
			return null;
		}
		
		log.info("User '" + loggedInUser.getNickname() + "' (" + loggedInUser.getId() + ") is replying to question: " + qa);
		qa = getDAO().answerQuestion(qa, text, true);
		log.info("Answered q&a: " + qa);
		return DtoToVoConverter.convert(qa);
	}
	
	public List<QuestionAnswerVO> getQuestionsAndAnswers(UserVO loggedInUser, String listingId, ListPropertiesVO listProperties) {
		Listing listing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (loggedInUser == null) {
			return DtoToVoConverter.convertQuestionAnswers(getDAO().getQuestionAnswers(listing, listProperties));
		}
		if (loggedInUser.toKeyId() == listing.owner.getId()) {
			return DtoToVoConverter.convertQuestionAnswers(getDAO().getQuestionAnswersForListingOwner(listing, listProperties));
		} else {
			return DtoToVoConverter.convertQuestionAnswers(getDAO().getQuestionAnswersForUser(VoToModelConverter.convert(loggedInUser), listing, listProperties));
		}
	}

	public Notification createMessageNotification(UserVO fromUser, String message, Listing listing, Notification.Type type) {
		SBUser toUser = getDAO().getUser(listing.owner.getString());
		
		Notification notifA = new Notification();
		Notification notifB = new Notification();
		notifB.message = notifA.message = message;
		notifB.listing = notifA.listing = new Key<Listing>(Listing.class, listing.id);
		notifB.listingName = notifA.listingName = listing.name;
		notifB.listingOwner = notifA.listingOwner = listing.owner.getString();
		notifB.listingMantra = notifA.listingMantra = listing.mantra;
		notifB.listingBriefAddress = notifA.listingBriefAddress = listing.briefAddress;
		notifB.listingCategory = notifA.listingCategory = listing.category;
		notifB.type = notifA.type = type;
		notifB.created = notifA.created = new Date();
		notifB.read = notifA.read = false;
		notifB.replied = notifA.replied = false;
		notifB.sentDate = notifA.sentDate = null;

		notifA.display = true;
		notifA.userA = new Key<SBUser>(SBUser.class, toUser.id);
		notifA.userAEmail = toUser.email;
		notifA.userANickname = toUser.nickname;
		notifA.userB = new Key<SBUser>(SBUser.class, fromUser.toKeyId());
		notifA.userBEmail = fromUser.getEmail();
		notifA.userBNickname = fromUser.getNickname();
		notifA.direction = Notification.Direction.B_TO_A;
		
		notifB.display = false;
		notifB.userB = new Key<SBUser>(SBUser.class, toUser.id);
		notifB.userBEmail = toUser.email;
		notifB.userBNickname = toUser.nickname;
		notifB.userA = new Key<SBUser>(SBUser.class, fromUser.toKeyId());
		notifB.userAEmail = fromUser.getEmail();
		notifB.userANickname = fromUser.getNickname();
		notifB.direction = Notification.Direction.A_TO_B;

		Notification notifs[] = getDAO().storeNotification(notifA, notifB);
		// we update context values and store notifications again
		notifs[0].context = notifs[0].id;
		notifs[1].context = notifs[0].id;
		notifs = getDAO().storeNotification(notifs);
        if (type == Notification.Type.ASK_LISTING_OWNER) {
            ListingFacade.instance().scheduleUpdateOfListingStatistics(listing.getWebKey(), UpdateReason.NEW_QUESTION);
        }
        else if (type == Notification.Type.PRIVATE_MESSAGE) {
            ListingFacade.instance().scheduleUpdateOfListingStatistics(listing.getWebKey(), UpdateReason.NEW_MESSAGE);
        }
        for (Notification not : notifs) {
        	if (not != null) {
        		String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + not.type + "_" + not.userA.getId();
        		Queue queue = QueueFactory.getDefaultQueue();
        		queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + not.getWebKey())
					.taskName(taskName));
        	} else {
        		log.warning("Can't schedule notification " + not);
        	}
        }
		return notifs[0];
	}

	public Notification createReplyNotification(UserVO fromUser, Notification originalNotification, String message) {
		Notification notifA = new Notification();
		Notification notifB = new Notification();
		notifB.message = notifA.message = message;
		notifB.question = notifA.question = originalNotification.message;
		notifB.questionDate = notifA.questionDate = originalNotification.created;
		notifB.listing = notifA.listing = originalNotification.listing;
		notifB.listingName = notifA.listingName = originalNotification.listingName;
		notifB.listingOwner = notifA.listingOwner = originalNotification.listingOwner;
		notifB.listingMantra = notifA.listingMantra = originalNotification.listingMantra;
		notifB.listingBriefAddress = notifA.listingBriefAddress = originalNotification.listingBriefAddress;
		notifB.listingCategory = notifA.listingCategory = originalNotification.listingCategory;
		notifB.type = notifA.type = originalNotification.type;
		notifB.created = notifA.created = new Date();
		notifB.read = notifA.read = false;
		originalNotification.replied = notifB.replied = notifA.replied = true;
		notifB.parentNotification = notifA.parentNotification = new Key<Notification>(Notification.class, originalNotification.id);
		notifB.context = notifA.context = originalNotification.context;
		notifB.sentDate = notifA.sentDate = null;

		notifB.userB = notifA.userA = originalNotification.userA;
		notifB.userBEmail = notifA.userAEmail = originalNotification.userAEmail;
		notifB.userBNickname = notifA.userANickname = originalNotification.userANickname;			

		notifB.userA = notifA.userB = originalNotification.userB;
		notifB.userAEmail = notifA.userBEmail = originalNotification.userBEmail;
		notifB.userANickname = notifA.userBNickname = originalNotification.userBNickname;

		/* notifA.userA = userA
		 * notifA.userB = userB
		 * notifB.userA = userA
		 * notifB.userB = userB
		 * now it should be set opposite
		 */
		if (originalNotification.direction == Notification.Direction.B_TO_A) {
			notifA.direction = Notification.Direction.A_TO_B;
			notifA.display = false;
			notifB.direction = Notification.Direction.B_TO_A;
			notifB.display = true;
		} else {
			notifA.direction = Notification.Direction.B_TO_A;
			notifA.display = true;
			notifB.direction = Notification.Direction.A_TO_B;
			notifB.display = false;
		}
		originalNotification.display = false;

		Notification notifs[] = getDAO().storeNotification(notifA, notifB, originalNotification);
        if (notifA.type == Notification.Type.ASK_LISTING_OWNER) {
            ListingFacade.instance().scheduleUpdateOfListingStatistics(notifA.listing.getString(), UpdateReason.NEW_QUESTION_REPLY);
        }
        else if (notifA.type == Notification.Type.PRIVATE_MESSAGE) {
            ListingFacade.instance().scheduleUpdateOfListingStatistics(notifA.listing.getString(), UpdateReason.NEW_MESSAGE_REPLY);
        }
        for (Notification not : notifs) {
        	if (not != null) {
        		String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + not.type + "_" + not.userA.getId();
        		Queue queue = QueueFactory.getDefaultQueue();
        		queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + not.getWebKey())
					.taskName(taskName));
        	} else {
        		log.warning("Can't schedule notification " + not);
        	}
        }
		return notifs[0];
	}

	public List<NotificationVO> markNotificationAsRead(UserVO loggedInUser, String listingId) {
		List<NotificationVO> notifs = DtoToVoConverter.convertNotifications(
				getDAO().markNotificationAsRead(BaseVO.toKeyId(loggedInUser.getId()), BaseVO.toKeyId(listingId)));
		if (notifs == null) {
			log.warning("Notification for user '" + loggedInUser.getNickname() + "' on listing id " + listingId + " not found!");
		} else {
			log.info("Notification for user '" + loggedInUser.getNickname() + "' on listing id " + listingId + " was marked as read.");
		}
		return notifs;
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
				getDAO().getUnreadUserNotifications(loggedInUser.toKeyId(), notifProperties));
		int num = notifProperties.getStartIndex() > 0 ? notifProperties.getStartIndex() : 1;
		for (NotificationVO notification : notifications) {
			notification.setOrderNumber(num++);
		}
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
				getDAO().getAllUserNotifications(loggedInUser.toKeyId(), notifProperties));
		int num = notifProperties.getStartIndex() > 0 ? notifProperties.getStartIndex() : 1;
		for (NotificationVO notification : notifications) {
			notification.setOrderNumber(num++);
		}
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
	
	public List<NotificationVO> getNotificationThread(UserVO loggedInUser, String contextId) {
		List<Notification> notifThread = getDAO().getNotificationThread(BaseVO.toKeyId(contextId));
		if (notifThread == null) {
			log.warning("Notification thread with id '" + contextId + "' not found!");
		}
		List<Notification> toUpdate = new ArrayList<Notification>();
		for (Notification notif : notifThread) {
			if (!notif.read) {
				notif.read = true;
				toUpdate.add(notif);
			}
		}
		if (toUpdate.size() > 0) {
			getDAO().storeNotification(toUpdate.toArray(new Notification[]{}));
		}
		return DtoToVoConverter.convertNotifications(notifThread);
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
		monitor = DtoToVoConverter.convert(getDAO().setMonitor(VoToModelConverter.convert(monitor)));
		ListingFacade.instance().scheduleUpdateOfListingStatistics(listingId, UpdateReason.NEW_MONITOR);
		return monitor;
	}

	public MonitorVO deactivateListingMonitor(UserVO loggedInUser, String listingId) {
		MonitorVO monitor = DtoToVoConverter.convert(
				getDAO().deactivateListingMonitor(loggedInUser.toKeyId(), BaseVO.toKeyId(listingId)));
		ListingFacade.instance().scheduleUpdateOfListingStatistics(listingId, UpdateReason.DELETE_MONITOR);
		if (monitor == null) {
			log.warning("Monitor for listing id '" + listingId + "' not found!");
		} else {
			log.info("Monitor for listing id '" + listingId + "' was deactivated.");
		}
		return monitor;
	}

	public MonitorListVO getMonitorsForObject(UserVO loggedInUser, String listingId, ListPropertiesVO listProperties) {
		MonitorListVO list = new MonitorListVO();
		List<MonitorVO> monitors = null;

		if (StringUtils.isEmpty(listingId)) {
			log.warning("Parameter listingId not provided!");
			return null;
		}
		monitors = DtoToVoConverter.convertMonitors(
				getDAO().getMonitorsForListing(BaseVO.toKeyId(listingId), listProperties));
		int index = listProperties.getStartIndex() > 0 ? listProperties.getStartIndex() : 1;
		for (MonitorVO monitor : monitors) {
			monitor.setOrderNumber(index++);
		}
		list.setMonitorsProperties(listProperties);
		list.setMonitors(monitors);
		
		return list;
	}

	public MonitorListVO getMonitorsForUser(UserVO loggedInUser, ListPropertiesVO listProperties) {
		MonitorListVO list = new MonitorListVO();
		List<MonitorVO> monitors = null;
		if (loggedInUser == null) {
			log.log(Level.WARNING, "User not logged in!");
			return null;
		}

		monitors = DtoToVoConverter.convertMonitors(getDAO().getMonitorsForUser(loggedInUser.toKeyId(), listProperties));
		int index = listProperties.getStartIndex() > 0 ? listProperties.getStartIndex() : 1;
		for (MonitorVO monitor : monitors) {
			monitor.setOrderNumber(index++);
		}
		list.setMonitorsProperties(listProperties);
		list.setMonitors(monitors);
		list.setUser(loggedInUser);
		
		return list;
	}

}
