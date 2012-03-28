package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.startupbidder.datamodel.Notification;
import com.startupbidder.vo.BidAndUserVO;
import com.startupbidder.vo.BidVO;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.ListingAndUserVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.MonitorListVO;
import com.startupbidder.vo.MonitorVO;
import com.startupbidder.vo.NotificationVO;
import com.startupbidder.vo.UserAndUserVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.DocService;
import com.startupbidder.web.EmailService;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ListingFacade;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;
import com.startupbidder.web.UserMgmtFacade;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class TaskController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(TaskController.class.getName());
	private Object model;

	private String queueName;
	private String taskName;
	private int taskRetryCount;
	private String failFast;
	
	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		queueName = request.getHeader("X-AppEngine-QueueName");
		taskName = request.getHeader("X-AppEngine-TaskName");
		taskRetryCount = 0;
		if (StringUtils.isNotEmpty(request.getHeader("X-AppEngine-TaskRetryCount"))) {
			taskRetryCount = NumberUtils.createLong(request.getHeader("X-AppEngine-TaskRetryCount")).intValue();
		}
		failFast = request.getHeader("X-AppEngine-FailFast");
		
		log.log(Level.INFO, "Task '" + taskName + "' called of queue '" + queueName
				+ "', taksRetryCount=" + taskRetryCount + ", failFast=" + failFast);
		
		if("calculate-user-stats".equalsIgnoreCase(getCommand(1))) {
			return calculateUserStats(request);
		} else if("calculate-listing-stats".equalsIgnoreCase(getCommand(1))) {
			return calculateListingStats(request);
		}  else if("update-mock-listing-images".equalsIgnoreCase(getCommand(1))) {
			return updateMockListingImages(request);
		} else if("send-accepted-bid-notification".equalsIgnoreCase(getCommand(1))) {
			return sendAcceptedBidNotification(request);
		} else if("send-notification".equalsIgnoreCase(getCommand(1))) {
			return sendNotification(request);
		}
		return null;
	}

	private HttpHeaders calculateUserStats(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("calculate-user-stats");
		
		String userId = getCommandOrParameter(request, 2, "id");
		UserMgmtFacade.instance().calculateUserStatistics(userId);
		
		return headers;
	}

	private HttpHeaders calculateListingStats(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("calculate-listing-stats");
		
		String listingId = getCommandOrParameter(request, 2, "id");
		ListingFacade.instance().calculateListingStatistics(ListingVO.toKeyId(listingId));
		ListingVO listing = ListingFacade.instance().getListing(null, listingId).getListing();
		DocService.instance().updateListingData(listing);
		
		return headers;
	}

	private HttpHeaders updateMockListingImages(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("update-mock-listing-images");
		
		String listingId = getCommandOrParameter(request, 2, "id");
		ListingFacade.instance().updateMockListingImages(ListingVO.toKeyId(listingId));
		
		return headers;
	}

	private HttpHeaders sendAcceptedBidNotification(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("send-accepted-bid-notification");
		
		String bidId = getCommandOrParameter(request, 2, "id");
		BidAndUserVO bid = null; //BidFacade.instance().getBid(null, bidId);
		UserAndUserVO listingOwner = UserMgmtFacade.instance().getUser(null, bid.getBid().getListingOwner());
		UserAndUserVO investor = UserMgmtFacade.instance().getUser(null, bid.getBid().getUser());
		ListingAndUserVO listing = ListingFacade.instance().getListing(null, bid.getBid().getListing());
		
		EmailService.instance().sendAcceptedBidNotification(bid.getBid(), listing.getListing(),
				listingOwner.getUser(), investor.getUser());
		
		return headers;
	}

	private HttpHeaders sendNotification(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("send-notification");
		
		String notifId = getCommandOrParameter(request, 2, "id");
		NotificationVO notification = ServiceFacade.instance().getNotification(null, notifId);
		Notification.Type type = Notification.Type.valueOf(notification.getType());

		if (!notification.isAcknowledged()) {
			Object notifObject = getNotificationObject(type, notification);
			log.info("Sending notification " + notification + " for object " + notifObject);
			if (notifObject instanceof BidVO) {
				sendBidNotification(type, notification, (BidVO)notifObject);
			} else if (notifObject instanceof CommentVO) {
				sendCommentNotification(type, notification, (CommentVO)notifObject);
			} else if (notifObject instanceof UserVO) {
				sendProfileNotification(type, notification, (UserVO)notifObject);
			} else if (notifObject instanceof ListingVO) {
				sendListingNotification(type, notification, (ListingVO)notifObject);
			}
		} else {
			log.info("Notification email was already sent for " + notification);
		}
		
		return headers;
	}
	
	private void sendBidNotification(Notification.Type type, NotificationVO notification, BidVO bid) {
		UserAndUserVO listingOwner = UserMgmtFacade.instance().getUser(null, bid.getListingOwner());
		if (!listingOwner.getUser().getNotifications().contains(type)) {
			log.info("User '" + listingOwner.getUser().getName() + "' is not subscribed to get '" + type + "' email notification.");
			return;
		}
		UserAndUserVO investor = UserMgmtFacade.instance().getUser(null, bid.getUser());
		ListingAndUserVO listing = ListingFacade.instance().getListing(null, bid.getListing());

		switch (type) {
		case YOUR_BID_WAS_ACCEPTED:
			EmailService.instance().sendYourBidAcceptedNotification(notification, bid, listing.getListing(),
					listingOwner.getUser(), investor.getUser());
			break;
		case YOUR_BID_WAS_COUNTERED:
			EmailService.instance().sendYourBidActivatedNotification(notification, bid, listing.getListing(),
					listingOwner.getUser(), investor.getUser());
			break;
		case YOUR_BID_WAS_REJECTED:
			EmailService.instance().sendYourBidRejectedNotification(notification, bid, listing.getListing(),
					listingOwner.getUser(), investor.getUser());
			break;
		case NEW_BID_FOR_YOUR_LISTING:
			EmailService.instance().sendNewBidForYourListingNotification(notification, bid, listing.getListing(),
					listingOwner.getUser(), investor.getUser());
			break;
		case YOU_PAID_BID:
			EmailService.instance().sendPaidBidNotification(notification, bid, listing.getListing(),
					listingOwner.getUser(), investor.getUser());
			break;
		case BID_PAID_FOR_YOUR_LISTING:
			EmailService.instance().sendBidPaidForYourListingNotification(notification, bid, listing.getListing(),
					listingOwner.getUser(), investor.getUser());
			break;
		case YOU_ACCEPTED_BID:
			EmailService.instance().sendAcceptedBidNotification(notification, bid, listing.getListing(),
				listingOwner.getUser(), investor.getUser());
			break;
		case BID_WAS_WITHDRAWN:
			EmailService.instance().sendBidWithdrawnNotification(notification, bid, listing.getListing(),
					listingOwner.getUser(), investor.getUser());
			break;
		}
	}
	
	private void sendCommentNotification(Notification.Type type, NotificationVO notification, CommentVO comment) {
		ListingAndUserVO listing = ListingFacade.instance().getListing(null, comment.getListing());
		UserAndUserVO listingOwner = UserMgmtFacade.instance().getUser(null, listing.getListing().getOwner());
		UserAndUserVO commenter = UserMgmtFacade.instance().getUser(null, comment.getUser());
		switch (type) {
		case NEW_COMMENT_FOR_YOUR_LISTING:
			if (!listingOwner.getUser().getNotifications().contains(type)) {
				log.info("User '" + listingOwner.getUser().getName() + "' is not subscribed to get '" + type + "' email notification.");
				return;
			}
			EmailService.instance().sendNewCommentForYourListingNotification(notification, comment, listing.getListing(),
					commenter.getUser(), listingOwner.getUser());
			break;
		case NEW_COMMENT_FOR_MONITORED_LISTING:
			MonitorListVO list = ServiceFacade.instance().getMonitorsForObject(null, comment.getListing(), "LISTING");
			for (MonitorVO monitor : list.getMonitors()) {
				UserVO monitoringUser = UserMgmtFacade.instance().getUser(null, monitor.getUser()).getUser();
				if (!monitoringUser.getNotifications().contains(type)) {
					log.info("User '" + monitoringUser.getName() + "' is not subscribed to get '" + type + "' email notification.");
					continue;
				}
				EmailService.instance().sendNewCommentForMonitoredListingNotification(notification, monitoringUser,
						comment, listing.getListing(), commenter.getUser(), listingOwner.getUser());
			}
			break;
		}
	}
	
	private void sendProfileNotification(Notification.Type type, NotificationVO notification, UserVO user) {
		if (!user.getNotifications().contains(type)) {
			log.info("User '" + user.getName() + "' is not subscribed to get '" + type + "' email notification.");
			return;
		}
		switch (type) {
		case YOUR_PROFILE_WAS_MODIFIED:
			EmailService.instance().sendYourProfileWasModifiedNotification(notification, user);
			break;
		case NEW_VOTE_FOR_YOU:
			EmailService.instance().sendNewVoteForYourProfileNotification(notification, user);
			break;
		}
	}
	
	private void sendListingNotification(Notification.Type type, NotificationVO notification, ListingVO listing) {
		UserAndUserVO listingOwner = UserMgmtFacade.instance().getUser(null, listing.getOwner());
		switch (type) {
		case NEW_VOTE_FOR_YOUR_LISTING:
			if (!listingOwner.getUser().getNotifications().contains(type)) {
				log.info("User '" + listingOwner.getUser().getName() + "' is not subscribed to get '" + type + "' email notification.");
				return;
			}
			EmailService.instance().sendNewVoteForYourListingNotification(notification, listing, listingOwner.getUser());
			break;
		case NEW_LISTING:
			MonitorListVO list = ServiceFacade.instance().getMonitorsForObject(null, listing.getId(), "LISTING");
			for (MonitorVO monitor : list.getMonitors()) {
				UserVO monitoringUser = UserMgmtFacade.instance().getUser(null, monitor.getUser()).getUser();
				if (!monitoringUser.getNotifications().contains(type)) {
					log.info("User '" + monitoringUser.getName() + "' is not subscribed to get '" + type + "' email notification.");
					continue;
				}
				EmailService.instance().sendNewListingNotification(notification, monitoringUser, listing, listingOwner.getUser());
			}
			break;
		}
	}
	
	private Object getNotificationObject(Notification.Type type, NotificationVO notification) {
		switch (type) {
		case BID_PAID_FOR_YOUR_LISTING:
		case BID_WAS_WITHDRAWN:
		case YOUR_BID_WAS_ACCEPTED:
		case YOUR_BID_WAS_COUNTERED:
		case YOUR_BID_WAS_REJECTED:
		case NEW_BID_FOR_YOUR_LISTING:
		case YOU_PAID_BID:
		case YOU_ACCEPTED_BID:
			// object is bid
			return null; //ServiceFacade.instance().getBid(null, notification.getObject()).getBid();
		case NEW_COMMENT_FOR_YOUR_LISTING:
		case NEW_COMMENT_FOR_MONITORED_LISTING:
			// object is comment
			return ServiceFacade.instance().getComment(null, notification.getObject()).getComment();
		case YOUR_PROFILE_WAS_MODIFIED:
		case NEW_VOTE_FOR_YOU:
			// object is profile
			return UserMgmtFacade.instance().getUser(null, notification.getObject()).getUser();
		case NEW_VOTE_FOR_YOUR_LISTING:
		case NEW_LISTING:
			// object is listing
			return ListingFacade.instance().getListing(null, notification.getObject()).getListing();
		}
		return null;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
