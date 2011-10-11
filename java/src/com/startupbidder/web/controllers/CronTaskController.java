package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

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
public class CronTaskController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(CronTaskController.class.getName());
	private Object model;

	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		if (!("true".equalsIgnoreCase(request.getHeader("X-AppEngine-Cron")) || getLoggedInUser().isAdmin())) {
			return null;
		}
		
		if("update-listing-stats".equalsIgnoreCase(getCommand(1))) {
			return updateListingStats(request);
		}
		return null;
	}

	private HttpHeaders updateListingStats(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("update-listing-stats");
		
		model = ServiceFacade.instance().updateAllListingStatistics();

		return headers;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
