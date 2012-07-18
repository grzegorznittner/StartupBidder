package com.startupbidder.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.startupbidder.dao.NotificationObjectifyDatastoreDAO;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.Monitor;
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
import com.startupbidder.vo.QuestionAnswerListVO;
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

	private NotificationObjectifyDatastoreDAO getNotificationDAO() {
		return NotificationObjectifyDatastoreDAO.getInstance();
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
					getDAO().getCommentsForListing(BaseVO.toKeyId(listingId), commentProperties));
			list.setComments(comments);
			list.setListing(listing);
			list.setCommentsProperties(commentProperties);
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
					getDAO().getCommentsForUser(BaseVO.toKeyId(userId), commentProperties));
			list.setComments(comments);
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
		} else if (StringUtils.equals(comment.getUser(), loggedInUser.getId())) {
			log.info("Comment author is going to delete comment: " + comment);
		} else {
			Listing listing = getDAO().getListing(BaseVO.toKeyId(comment.getListing()));
			if (listing.owner.getId() == loggedInUser.toKeyId()) {
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

	public CommentVO createComment(UserVO loggedInUser, String listingId, String commentText) {
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			return null;
		}
		Listing listing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (listing == null) {
			log.warning("Listing with id '" + listingId + "' doesn't exist.");
			return null;
		}
		SBUser user = null;
		if (loggedInUser.getNickname() == null) {
			// sorting out dev env issue where google user doesn't have nickname
			user = getDAO().getUser(loggedInUser.getId());
		} else {
			user = VoToModelConverter.convert(loggedInUser);
		}
		
		Comment comment = new Comment();
		comment.listing = listing.getKey();
		comment.listingName = listing.name;
		comment.user = user.getKey();
		comment.userNickName = user.nickname;
		comment.comment = commentText;
		CommentVO commentVO = DtoToVoConverter.convert(getDAO().createComment(comment));

		setListingMonitor(loggedInUser, listing.getWebKey());
		
		UserMgmtFacade.instance().scheduleUpdateOfUserStatistics(loggedInUser.getId(), UserMgmtFacade.UpdateReason.NEW_COMMENT);
		ListingFacade.instance().scheduleUpdateOfListingStatistics(listingId, UpdateReason.NEW_COMMENT);
		
		NotificationFacade.instance().scheduleCommentNotification(comment);
		return commentVO;
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
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			return null;
		}
		SystemPropertyVO prop = DtoToVoConverter.convert(getDAO().getSystemProperty(name));
		if (prop != null && (StringUtils.contains(prop.getName(), "password")
				|| StringUtils.contains(prop.getName(), "secret"))) {
			String value = prop.getValue();
			if (value.length() < 5) {
				prop.setValue("*****");
			} else {
				prop.setValue(value.substring(0, 4) + "*****");
			}
		}
		return prop;
	}

	public List<SystemPropertyVO> getSystemProperties(UserVO loggedInUser) {
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			return new ArrayList<SystemPropertyVO>();
		}
		List<SystemPropertyVO> result = DtoToVoConverter.convertSystemProperties(getDAO().getSystemProperties());
		for (SystemPropertyVO prop : result) {
			if (StringUtils.contains(prop.getName(), "password") || StringUtils.contains(prop.getName(), "secret")) {
				String value = prop.getValue();
				if (value.length() < 5) {
					prop.setValue("*****");
				} else {
					prop.setValue(value.substring(0, 4) + "*****");
				}
			}
		}
		return result;
	}

	public SystemPropertyVO setSystemProperty(UserVO loggedInUser, SystemPropertyVO property) {
		if (loggedInUser == null) {
			return null;
		}
		property.setAuthor(loggedInUser.getEmail());
		return DtoToVoConverter.convert(getDAO().setSystemProperty(VoToModelConverter.convert(property)));
	}

	public ListingDocumentVO deleteDocument(UserVO loggedInUser, String docId) {
		if (loggedInUser == null) {
			return null;
		}
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
		if (qa != null) {
			setListingMonitor(loggedInUser, listingId);
		}
		NotificationFacade.instance().scheduleQANotification(qa);
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
		qa = getDAO().answerQuestion(listing, qa, text, true);

        ListingFacade.instance().scheduleUpdateOfListingStatistics(listing.getWebKey(), UpdateReason.QUESTION_ANSWERED);
        NotificationFacade.instance().scheduleQANotification(qa);
        
		log.info("Answered q&a: " + qa);
		return DtoToVoConverter.convert(qa);
	}
	
	public QuestionAnswerListVO getQuestionsAndAnswers(UserVO loggedInUser, String listingId, ListPropertiesVO listProperties) {
		QuestionAnswerListVO result = new QuestionAnswerListVO();
		Listing listing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (loggedInUser == null) {
			result.setQuestionAnswers(DtoToVoConverter.convertQuestionAnswers(getDAO().getQuestionAnswers(listing, listProperties)));
		} else if (loggedInUser.toKeyId() == listing.owner.getId()) {
			result.setQuestionAnswers(DtoToVoConverter.convertQuestionAnswers(getDAO().getQuestionAnswersForListingOwner(listing, listProperties)));
		} else {
			result.setQuestionAnswers(DtoToVoConverter.convertQuestionAnswers(
					getDAO().getQuestionAnswersForUser(VoToModelConverter.convert(loggedInUser), listing, listProperties)));
		}
		result.setQuestionAnswersProperties(listProperties);
		result.setListing(DtoToVoConverter.convert(listing));
		result.setUser(loggedInUser != null ? new UserBasicVO(loggedInUser) : null);
		return result;
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
		Listing listing = getDAO().getListing(BaseVO.toKeyId(listingId));
		log.info("User " + loggedInUser.getEmail() + " ("+ loggedInUser.toKeyId() + ") setting monitor on listing: " + listing);
		if (listing == null || listing.owner.getId() == loggedInUser.toKeyId()) {
			log.warning("Listing doesn't exist or user is an owner of this listing. Listing: " + listing);
			return null;
		}
		Monitor monitor = new Monitor();
		monitor.user = new Key<SBUser>(SBUser.class, loggedInUser.toKeyId());
		monitor.userNickname = loggedInUser.getNickname();
		monitor.userEmail = loggedInUser.getEmail();
		monitor.monitoredListing = new Key<Listing>(Listing.class, ListingVO.toKeyId(listingId));
		
		MonitorVO monitorVO = DtoToVoConverter.convert(getDAO().setMonitor(monitor));
		ListingFacade.instance().scheduleUpdateOfListingStatistics(listingId, UpdateReason.NEW_MONITOR);
		return monitorVO;
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
