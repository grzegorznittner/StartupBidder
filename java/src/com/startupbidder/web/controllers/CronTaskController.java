package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.startupbidder.web.DocService;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ListingFacade;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;
import com.startupbidder.web.UserMgmtFacade;

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
			log.warning("Cron actions can be only executed by AppEngine's Cron or StartupBidder admins");
			return null;
		}
		
		if("update-listing-stats".equalsIgnoreCase(getCommand(1))) {
			return updateListingStats(request);
		} else if("update-user-stats".equalsIgnoreCase(getCommand(1))) {
			return updateUserStats(request);
		}
		return null;
	}

	private HttpHeaders updateListingStats(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("update-listing-stats");
		
		model = ListingFacade.instance().updateAllListingStatistics();

		return headers;
	}
	
	private HttpHeaders updateUserStats(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("update-user-stats");
		
		model = UserMgmtFacade.instance().updateAllUserStatistics();

		return headers;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
