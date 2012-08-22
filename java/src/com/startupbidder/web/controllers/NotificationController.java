package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.googlecode.objectify.Key;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.NotificationVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.EmailService;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.NotificationFacade;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class NotificationController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(NotificationController.class.getName());

	private Object model;
	
	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			// GET method handler
			
			if("unread".equalsIgnoreCase(getCommand(1))) {
				return unread(request);
			} else if("user".equalsIgnoreCase(getCommand(1))) {
				return user(request);
			} else if("get".equalsIgnoreCase(getCommand(1))) {
				return get(request);
			} else if("test".equalsIgnoreCase(getCommand(1))) {
				return test(request);
			}
		}
		return null;
	}

	private HttpHeaders unread(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("unread");
		
		ListPropertiesVO notifProperties = getListProperties(request);
		model = NotificationFacade.instance().getUnreadNotificationsForUser(getLoggedInUser(), notifProperties);
		
		return headers;
	}

	/*
	 *  /notifications/user/.html
	 */
	private HttpHeaders user(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("user");
		
		ListPropertiesVO notifProperties = getListProperties(request);
		model = NotificationFacade.instance().getNotificationsForUserAndMarkRead(getLoggedInUser(), notifProperties);
		
		return headers;
	}

	private HttpHeaders get(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("get");
		String notifId = getCommandOrParameter(request, 2, "id");
		model = NotificationFacade.instance().getNotification(getLoggedInUser(), notifId);
		return headers;
	}
	
	private HttpHeaders test(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("test");
		String type = getCommandOrParameter(request, 2, "type");
		
		UserVO loggedInUser = getLoggedInUser();
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			model = "Not an admin!";
			headers.setStatus(500);
		} else {
			NotificationVO notification = prepareTestNotification(loggedInUser, type);
			EmailService.instance().sendNotificationEmail(notification);
			model = notification;
		}
		return headers;
	}

	private NotificationVO prepareTestNotification(UserVO loggedInUser, String type) {
		Notification.Type notifType = Notification.Type.valueOf(type.toUpperCase());
		
		Notification notif = new Notification();
		notif.read = false;
		notif.type = notifType;
		notif.user = new Key<SBUser>(SBUser.class, loggedInUser.toKeyId());
		notif.userEmail = loggedInUser.getEmail();
		notif.userNickname = loggedInUser.getNickname();
		notif.fromUserNickname = "Author";
		
		ListPropertiesVO listingProperties = new ListPropertiesVO();
		listingProperties.setMaxResults(5);
		Listing listing = ObjectifyDatastoreDAO.getInstance().getTopListings(listingProperties).get(new Random().nextInt(5));
		
		notif.listing = listing.getKey();
		notif.listingBriefAddress = listing.briefAddress;
		notif.listingCategory = listing.category;
		notif.listingMantra = listing.mantra;
		notif.listingName = listing.name;
		notif.listingOwner = "ListingOwner";
		notif.listingOwnerUser = listing.owner;
		notif.listing = listing.getKey();
		
		notif.investor = notif.user;
		
		switch(notifType) {
		case NEW_LISTING:
			notif.message = "New listing has been posted";
			break;
		case LISTING_ACTIVATED:
			notif.message = "List has been activated";
			break;
		case LISTING_FROZEN:
			notif.message = "List has been frozen";
			break;
		case LISTING_WITHDRAWN:
			notif.message = "List has been withdrawn";
			break;
        case LISTING_SENT_BACK:
			notif.message = "List has been sent back";
            break;
		case NEW_COMMENT_FOR_MONITORED_LISTING:
			notif.message = "New comment for listing";
			break;
		case NEW_COMMENT_FOR_YOUR_LISTING:
			notif.message = "New comment for your listing";
			break;
		case ASK_LISTING_OWNER:
			notif.message = "New question for your listing";
			break;
		case PRIVATE_MESSAGE:
			notif.message = "Private message for you";
			break;
		case NEW_BID_FOR_YOUR_LISTING:
			notif.message = "New bid for your listing";
			break;
		case YOUR_BID_WAS_COUNTERED:
			notif.message = "Your bid has been countered";
			break;
		case YOU_ACCEPTED_BID:
			notif.message = "You accepted bid";
			break;
		case YOUR_BID_WAS_ACCEPTED:
			notif.message = "Your bid has been accepted";
			break;
		case BID_WAS_WITHDRAWN:
			notif.message = "Your bid has been withdrawn";
			break;
		case YOUR_BID_WAS_REJECTED:
			notif.message = "Your bid has been rejected";
			break;
		case YOU_PAID_BID:
		case BID_PAID_FOR_YOUR_LISTING:
			// payments are not handled yet
			break;
		case ADMIN_REQUEST_TO_BECOME_DRAGON:
			notif.message = "Request for a dragon's badge";
			break;
		}
		NotificationVO notifVO = DtoToVoConverter.convert(notif);
		notifVO.setId("notification_id");
		return notifVO;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
