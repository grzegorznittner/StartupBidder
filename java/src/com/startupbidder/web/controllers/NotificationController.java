package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

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
			} else if("get_thread".equalsIgnoreCase(getCommand(1))) {
				return getThread(request);
			}
		}
		return null;
	}

	/*
	 * GET /notification/ack?id=<listing id>
	 */
	private HttpHeaders ack(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("ack");
		
		String listingId = getCommandOrParameter(request, 2, "id");
		if (!StringUtils.isEmpty(listingId)) {
			model = ServiceFacade.instance().markNotificationAsRead(getLoggedInUser(), listingId);
			if (model == null) {
				headers.setStatus(500);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'id' is not provided!");
			headers.setStatus(500);
		}

		return headers;
	}

	private HttpHeaders unread(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("unread");
		
		ListPropertiesVO notifProperties = getListProperties(request);
		model = ServiceFacade.instance().getUnreadNotificationsForUser(getLoggedInUser(), notifProperties);
		
		return headers;
	}

	/*
	 *  /notifications/user/.html
	 */
	private HttpHeaders user(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("user");
		
		ListPropertiesVO notifProperties = getListProperties(request);
		model = ServiceFacade.instance().getNotificationsForUser(getLoggedInUser(), notifProperties);
		
		return headers;
	}

	private HttpHeaders get(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("get");
		String notifId = getCommandOrParameter(request, 2, "id");
		model = ServiceFacade.instance().getNotification(getLoggedInUser(), notifId);
		return headers;
	}

	private HttpHeaders getThread(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("get_thread");
		String contextId = getCommandOrParameter(request, 2, "id");
		model = ServiceFacade.instance().getNotificationThread(getLoggedInUser(), contextId);
		return headers;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
