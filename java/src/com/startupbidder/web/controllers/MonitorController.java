package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.startupbidder.vo.MonitorVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class MonitorController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(MonitorController.class.getName());

	private Object model;
	
	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			// GET method handler			
			if("active-for-object".equalsIgnoreCase(getCommand(1))) {
				return activeForObject(request);
			} else if("active-for-user".equalsIgnoreCase(getCommand(1))) {
				return activeForUser(request);
			}
		} else if ("POST".equalsIgnoreCase(request.getMethod())) {
			if("set".equalsIgnoreCase(getCommand(1))) {
				return set(request);
			} else if("deactivate".equalsIgnoreCase(getCommand(1))) {
				return deactivate(request);
			}
		}
		return null;
	}

	/*
	 * POST /monitor/set?monitor=<monitor json>
	 */
	private HttpHeaders set(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("create");
		
		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String notifString = request.getParameter("monitor");
		if (!StringUtils.isEmpty(notifString)) {
			MonitorVO monitor = mapper.readValue(notifString, MonitorVO.class);
			log.log(Level.INFO, "Creating monitor: " + monitor);
			monitor = ServiceFacade.instance().setMonitor(getLoggedInUser(), monitor);
			model = monitor;
			if (monitor == null) {
				log.log(Level.WARNING, "Monitor not created!");
				headers.setStatus(500);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'monitor' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}

	/*
	 * GET /monitor/deactivate?id=<monitor id>
	 */
	private HttpHeaders deactivate(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("deactivate");
		
		String monitorId = getCommandOrParameter(request, 2, "id");
		if (!StringUtils.isEmpty(monitorId)) {
			model = ServiceFacade.instance().deactivateMonitor(getLoggedInUser(), monitorId);
			if (model == null) {
				headers.setStatus(500);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'id' is not provided!");
			headers.setStatus(500);
		}

		return headers;
	}

	/*
	 * GET /monitor/active-for-user?type=<type name>
	 * type name := ()
	 */
	private HttpHeaders activeForUser(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("active-for-user");
		
		String type = getCommandOrParameter(request, 2, "type");
		model = ServiceFacade.instance().getMonitorsForUser(getLoggedInUser(), null, type);
		
		return headers;
	}

	/*
	 * GET /monitor/active-for-object?id=<object id>&type=<type name>
	 * type name := ()
	 */
	private HttpHeaders activeForObject(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("active-for-object");
		
		String objectId = getCommandOrParameter(request, 2, "id");
		String type = getCommandOrParameter(request, 3, "type");
		model = ServiceFacade.instance().getMonitorsForObject(getLoggedInUser(), objectId, type);
		
		return headers;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
