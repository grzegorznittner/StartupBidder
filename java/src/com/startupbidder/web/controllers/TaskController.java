package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.vo.BaseVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.web.DocService;
import com.startupbidder.web.EmailService;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ListingFacade;
import com.startupbidder.web.ListingFacade.UpdateReason;
import com.startupbidder.web.ModelDrivenController;
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
		} else if("send-notification".equalsIgnoreCase(getCommand(1))) {
			return sendNotification(request);
		}
		return null;
	}

	private HttpHeaders calculateUserStats(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("calculate-user-stats");
		
		String userId = getCommandOrParameter(request, 2, "id");
		UserMgmtFacade.instance().calculateUserStatistics(userId);
		model = userId;
		
		return headers;
	}

	private HttpHeaders calculateListingStats(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("calculate-listing-stats");
		
		String listingId = getCommandOrParameter(request, 2, "id");
		String updateType = getCommandOrParameter(request, 3, "update_type");
		UpdateReason reason = UpdateReason.valueOf(updateType);
		
		ListingFacade.instance().calculateListingStatistics(ListingVO.toKeyId(listingId));
		ListingVO listing = ListingFacade.instance().getListing(null, listingId).getListing();
		DocService.instance().updateListingData(listing, reason);
		model = listing;
		
		return headers;
	}

	private HttpHeaders updateMockListingImages(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("update-mock-listing-images");
		
		String listingId = getCommandOrParameter(request, 2, "id");
		ListingFacade.instance().updateMockListingImages(ListingVO.toKeyId(listingId));
		model = listingId;
		
		return headers;
	}

	private HttpHeaders sendNotification(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("send-notification");
		
		String notifId = getCommandOrParameter(request, 2, "id");
		
		Notification notification = ObjectifyDatastoreDAO.getInstance().getNotification(BaseVO.toKeyId(notifId));
		model = DtoToVoConverter.convert(notification);

		if (!notification.read) {			
			log.info("Sending notification: " + notification);
			if (EmailService.instance().sendListingNotification(DtoToVoConverter.convert(notification))) {
				notification.sentDate = new Date();
				ObjectifyDatastoreDAO.getInstance().storeNotification(notification);
			}
		} else {
			log.info("Notification email was already sent for " + notification);
		}
		
		return headers;
	}
	
	@Override
	public Object getModel() {
		return model;
	}

}
