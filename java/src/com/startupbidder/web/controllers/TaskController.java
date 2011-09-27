package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

/**
 * 
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
		} else if("set-datastore".equalsIgnoreCase(getCommand(1))) {
			return setDatastore(request);
		}
		return null;
	}

	private HttpHeaders calculateUserStats(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("calculate-user-stats");
		
		String userId = getCommandOrParameter(request, 2, "id");
		ServiceFacade.instance().calculateUserStatistics(userId);
		
		return headers;
	}

	private HttpHeaders calculateListingStats(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("calculate-listing-stats");
		
		String listingId = getCommandOrParameter(request, 2, "id");
		ServiceFacade.instance().calculateListingStatistics(listingId);
		
		return headers;
	}

	private HttpHeaders setDatastore(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("set-datastore");

		return headers;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
