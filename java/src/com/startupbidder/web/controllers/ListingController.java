package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingListVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

public class ListingController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(ListingController.class.getName());
	
	private ListingListVO listings = null;
	private ListingVO listing = null;
	
	public HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
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
			if ("save".equalsIgnoreCase(getCommand(1))) {
				return save(request);
			} else if("up".equalsIgnoreCase(getCommand(1))) {
				return up(request);
			}  else if("activate".equalsIgnoreCase(getCommand(1))) {
				return activate(request);
			} else if("withdraw".equalsIgnoreCase(getCommand(1))) {
				return withdraw(request);
			} 
		} else if ("DELETE".equalsIgnoreCase(request.getMethod())) {
			return delete(request);
		}

		return null;
	}
	
	// DELETE /listing/
    private HttpHeaders delete(HttpServletRequest request) {
    	HttpHeaders headers = new HttpHeadersImpl("create");
		headers.setStatus(501);
		return headers;
	}

    // PUT /listing/create
    // POST /listing/create
	private HttpHeaders save(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("save");
		
		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String listingString = request.getParameter("listing");
		if (!StringUtils.isEmpty(listingString)) {
			listing = mapper.readValue(listingString, ListingVO.class);
			if (listing.getId() == null) {
				listing = ServiceFacade.instance().createListing(getLoggedInUser(), listing);
			} else {
				listing = ServiceFacade.instance().updateListing(getLoggedInUser(), listing);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'listing' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}

	// GET /listings/closing
	private HttpHeaders closing(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	listings = ServiceFacade.instance().getClosingListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("valued").disableCaching();
	}

	// GET /listings/latest
	private HttpHeaders latest(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	listings = ServiceFacade.instance().getLatestListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("valued").disableCaching();
	}

	// GET /listings/discussed
	private HttpHeaders discussed(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	listings = ServiceFacade.instance().getMostDiscussedListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("valued").disableCaching();
	}

	// GET /listings/popular
	private HttpHeaders popular(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	listings = ServiceFacade.instance().getMostPopularListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("valued").disableCaching();
	}

	// GET /listings/valuation
	private HttpHeaders valuation(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	listings = ServiceFacade.instance().getMostValuedListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("valued").disableCaching();
	}

	// GET /listings/top
    private HttpHeaders top(HttpServletRequest request) {
    	ListPropertiesVO listingProperties = getListProperties(request);
    	listings = ServiceFacade.instance().getTopListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("top").disableCaching();
    }

    // GET /listings/active
    private HttpHeaders active(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	listings = ServiceFacade.instance().getActiveListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("active").disableCaching();
    }

    // GET /listings/user
    private HttpHeaders user(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
		ListPropertiesVO listingProperties = getListProperties(request);

    	listings = ServiceFacade.instance().getUserListings(getLoggedInUser(), userId, listingProperties);
        return new HttpHeadersImpl("user").disableCaching();
    }

    // GET /listings/up
    private HttpHeaders up(HttpServletRequest request) {
    	String listingId = getCommandOrParameter(request, 2, "id");
    	listing = ServiceFacade.instance().valueUpListing(getLoggedInUser(), listingId);
        return new HttpHeadersImpl("up").disableCaching();
    }

    // GET /listings/activate
    private HttpHeaders activate(HttpServletRequest request) {
    	String listingId = getCommandOrParameter(request, 2, "id");
    	listing = ServiceFacade.instance().activateListing(getLoggedInUser(), listingId);
        return new HttpHeadersImpl("activate").disableCaching();
    }

    // GET /listings/withdrawn
    private HttpHeaders withdraw(HttpServletRequest request) {
    	String listingId = getCommandOrParameter(request, 2, "id");
    	listing = ServiceFacade.instance().withdrawListing(getLoggedInUser(), listingId);
        return new HttpHeadersImpl("withdrawn").disableCaching();
    }

    // GET /listings/get
    private HttpHeaders get(HttpServletRequest request) {
    	String listingId = getCommandOrParameter(request, 2, "id");
    	listing = ServiceFacade.instance().getListing(getLoggedInUser(), listingId);
        return new HttpHeadersImpl("get").disableCaching();
    }

    // GET /listings/
    private HttpHeaders index(HttpServletRequest request) {
    	String listingId = getCommandOrParameter(request, 1, "id");
    	listing = ServiceFacade.instance().getListing(getLoggedInUser(), listingId);
        return new HttpHeadersImpl("index").disableCaching();
    }

	public Object getModel() {
    	return listings != null ? listings : listing;
    }
}
