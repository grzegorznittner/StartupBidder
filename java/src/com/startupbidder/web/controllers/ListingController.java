package com.startupbidder.web.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingListVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

public class ListingController extends ModelDrivenController {
	private ListingListVO listings = null;
	private ListingVO listing = null;
	
	public HttpHeaders executeAction(HttpServletRequest request) {
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			// GET method handler
			
			if("top".equalsIgnoreCase(getCommand(1))) {
				return top(request);
			} else if("active".equalsIgnoreCase(getCommand(1))) {
				return active(request);
			} else if("valuation".equalsIgnoreCase(getCommand(1))) {
				return valuation(request);
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
			} else if("get".equalsIgnoreCase(getCommand(1))) {
				return get(request);
			} else {
				// default action
				return index(request);
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
		ListPropertiesVO listingProperties = getListProperties(request);
    	listings = ServiceFacade.instance().getTopListings(listingProperties);
        return new DefaultHttpHeaders("valued").disableCaching();
	}

	// GET /listings/latest
	private HttpHeaders latest(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	listings = ServiceFacade.instance().getTopListings(listingProperties);
        return new DefaultHttpHeaders("valued").disableCaching();
	}

	// GET /listings/discussed
	private HttpHeaders discussed(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	listings = ServiceFacade.instance().getTopListings(listingProperties);
        return new DefaultHttpHeaders("valued").disableCaching();
	}

	// GET /listings/popular
	private HttpHeaders popular(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	listings = ServiceFacade.instance().getTopListings(listingProperties);
        return new DefaultHttpHeaders("valued").disableCaching();
	}

	// GET /listings/valued
	private HttpHeaders valuation(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	listings = ServiceFacade.instance().getMostValuedListings(listingProperties);
        return new DefaultHttpHeaders("valued").disableCaching();
	}

	// GET /listings/top
    private HttpHeaders top(HttpServletRequest request) {
    	ListPropertiesVO listingProperties = getListProperties(request);
    	listings = ServiceFacade.instance().getTopListings(listingProperties);
        return new DefaultHttpHeaders("top").disableCaching();
    }

    // GET /listings/active
    private HttpHeaders active(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	listings = ServiceFacade.instance().getActiveListings(listingProperties);
        return new DefaultHttpHeaders("active").disableCaching();
    }

    // GET /listings/user
    private HttpHeaders user(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
		ListPropertiesVO listingProperties = getListProperties(request);

    	listings = ServiceFacade.instance().getUserListings(userId, listingProperties);
        return new DefaultHttpHeaders("user").disableCaching();
    }

    // GET /listings/get
    private HttpHeaders get(HttpServletRequest request) {
    	String listingId = getCommandOrParameter(request, 2, "id");
    	listing = ServiceFacade.instance().getListing(listingId);
        return new DefaultHttpHeaders("get").disableCaching();
    }

    // GET /listings/
    private HttpHeaders index(HttpServletRequest request) {
    	String listingId = getCommandOrParameter(request, 1, "id");
    	listing = ServiceFacade.instance().getListing(listingId);
        return new DefaultHttpHeaders("index").disableCaching();
    }

	public Object getModel() {
    	return listings != null ? listings : listing;
    }
}
