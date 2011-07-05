package com.startupbidder.web.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import com.startupbidder.vo.BusinessPlanVO;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

public class ListingController extends ModelDrivenController {
	private static int DEFAULT_MAX_RESULTS = 5;
	
	private List<BusinessPlanVO> listings = null;
	private BusinessPlanVO listing = null;
	
	public HttpHeaders executeAction(HttpServletRequest request) {
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			// GET method handler
			
			if("top".equalsIgnoreCase(getCommand(1))) {
				return top(request);
			} else if("active".equalsIgnoreCase(getCommand(1))) {
				return active(request);
			} else if("valued".equalsIgnoreCase(getCommand(1))) {
				return valued(request);
			} else if("popular".equalsIgnoreCase(getCommand(1))) {
				return popular(request);
			} else if("discussed".equalsIgnoreCase(getCommand(1))) {
				return discussed(request);
			} else if("latest".equalsIgnoreCase(getCommand(1))) {
				return latest(request);
			} else if("closing".equalsIgnoreCase(getCommand(1))) {
				return closing(request);
			} else if("user".equalsIgnoreCase(getCommand(1))) {
				return user(request);
			} else {
				// default action
				return top(request);
			}
		} else if ("PUT".equalsIgnoreCase(request.getMethod()) ||
				"POST".equalsIgnoreCase(request.getMethod())) {
			return create(request);
		} else if ("DELETE".equalsIgnoreCase(request.getMethod())) {
			return delete(request);
		}

		return null;
	}
	
	// DELETE /listing/
    private HttpHeaders delete(HttpServletRequest request) {
    	HttpHeaders headers = new DefaultHttpHeaders("create");
		headers.setStatus(501);
		return headers;
	}

    // PUT /listing/
    // POST /listing/
	private HttpHeaders create(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("create");
		headers.setStatus(501);
		return headers;
	}

	// GET /listings/closing
	private HttpHeaders closing(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("create");
		headers.setStatus(501);
		return headers;
	}

	// GET /listings/latest
	private HttpHeaders latest(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("create");
		headers.setStatus(501);
		return headers;
	}

	// GET /listings/discussed
	private HttpHeaders discussed(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("create");
		headers.setStatus(501);
		return headers;
	}

	// GET /listings/popular
	private HttpHeaders popular(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("create");
		headers.setStatus(501);
		return headers;
	}

	// GET /listings/valued
	private HttpHeaders valued(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("create");
		headers.setStatus(501);
		return headers;
	}

	// GET /listings/top
    private HttpHeaders top(HttpServletRequest request) {
    	String maxItemsStr = request.getParameter("maxItems");
    	int maxItems = maxItemsStr != null ? Integer.parseInt(maxItemsStr) : DEFAULT_MAX_RESULTS;
    	listings = ServiceFacade.instance().getTopBusinessPlans(maxItems, request.getParameter("cursor"));
        return new DefaultHttpHeaders("top").disableCaching();
    }

    // GET /listings/active
    private HttpHeaders active(HttpServletRequest request) {
    	String maxItemsStr = request.getParameter("maxItems");
    	int maxItems = maxItemsStr != null ? Integer.parseInt(maxItemsStr) : DEFAULT_MAX_RESULTS;
    	listings = ServiceFacade.instance().getActiveBusinessPlans(maxItems, request.getParameter("cursor"));
        return new DefaultHttpHeaders("active").disableCaching();
    }

    // GET /listings/user
    private HttpHeaders user(HttpServletRequest request) {
    	String maxItemsStr = request.getParameter("maxItems");
    	int maxItems = maxItemsStr != null ? Integer.parseInt(maxItemsStr) : DEFAULT_MAX_RESULTS;
    	String userId = request.getParameter("user");
    	listings = ServiceFacade.instance().getUserBusinessPlans(userId, maxItems, request.getParameter("cursor"));
        return new DefaultHttpHeaders("active").disableCaching();
    }

    public Object getModel() {
    	return listings != null ? listings : listing;
    }
}
