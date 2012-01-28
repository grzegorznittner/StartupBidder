package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.NotificationVO;
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
			
			if("all".equalsIgnoreCase(getCommand(1))) {
				return all(request);
			} else if("user".equalsIgnoreCase(getCommand(1))) {
				return user(request);
			} else if("get".equalsIgnoreCase(getCommand(1))) {
				return get(request);
			} else if("ack".equalsIgnoreCase(getCommand(1))) {
				return ack(request);
			}
		} else if ("POST".equalsIgnoreCase(request.getMethod())) {
			if("create".equalsIgnoreCase(getCommand(1))) {
				return create(request);
			}
		}
		return null;
	}

	/*
	 * POST /notification/create?notification=<bid json>
	 */
	private HttpHeaders create(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("create");
		
		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String notifString = request.getParameter("notification");
		if (!StringUtils.isEmpty(notifString)) {
			NotificationVO notification = mapper.readValue(notifString, NotificationVO.class);
			log.log(Level.INFO, "Creating notification: " + notification);
			notification = ServiceFacade.instance().createNotification(getLoggedInUser(), notification);
			model = notification;
			if (notification == null) {
				log.log(Level.WARNING, "Notification not created!");
				headers.setStatus(500);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'notification' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}

	/*
	 * GET /notification/ack?id=<notification id>
	 */
	private HttpHeaders ack(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("ack");
		
		String notifId = getCommandOrParameter(request, 2, "id");
		if (!StringUtils.isEmpty(notifId)) {
			model = ServiceFacade.instance().acknowledgeNotification(getLoggedInUser(), notifId);
			if (model == null) {
				headers.setStatus(500);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'id' is not provided!");
			headers.setStatus(500);
		}

		return headers;
	}

	private HttpHeaders all(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("all");
		
		ListPropertiesVO notifProperties = getListProperties(request);
		String userId = getCommandOrParameter(request, 2, "id");
		model = ServiceFacade.instance().getAllNotificationsForUser(getLoggedInUser(), userId, notifProperties);
		
		return headers;
	}

	/*
	 *  /notifications/user/ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw/.html
	 *  /notifications/user/?id=ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw.html
	 */
	private HttpHeaders user(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("user");
		
		ListPropertiesVO notifProperties = getListProperties(request);
		//String userId = getCommandOrParameter(request, 2, "id");
		model = ServiceFacade.instance().getNotificationsForUser(getLoggedInUser(), null, notifProperties);
		
		return headers;
	}

	private HttpHeaders get(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("get");
		String notifId = getCommandOrParameter(request, 2, "id");
		model = ServiceFacade.instance().getNotification(getLoggedInUser(), notifId);
		return headers;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
