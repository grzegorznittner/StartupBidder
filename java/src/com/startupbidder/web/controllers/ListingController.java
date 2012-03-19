package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingAndUserVO;
import com.startupbidder.vo.ListingPropertyVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ListingFacade;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class ListingController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(ListingController.class.getName());
	
	private Object model = null;
	
	public HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			// GET method handler
			
			if("edited".equalsIgnoreCase(getCommand(1))) {
				if (getCommand(2) != null) {
					return getEditedListingDoc(request, getCommand(2));
				} else {
					return startEditing(request);
				}
			} else if("top".equalsIgnoreCase(getCommand(1))) {
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
			} else if("keyword".equalsIgnoreCase(getCommand(1))) {
				return keyword(request);
			} else if("get-all-documents".equalsIgnoreCase(getCommand(1))) {
				return getAllDocuments(request);
			} else if("categories".equalsIgnoreCase(getCommand(1))) {
				return getCategories(request);
			} else {
				// default action
				return index(request);
			}
		} else if ("POST".equalsIgnoreCase(request.getMethod())) {
			if ("create".equalsIgnoreCase(getCommand(1))) {
				return startEditing(request);
			} else if ("update".equalsIgnoreCase(getCommand(1))) {
				return update(request);
			} else if ("update_field".equalsIgnoreCase(getCommand(1))) {
				return updateField(request);
			} else if("up".equalsIgnoreCase(getCommand(1))) {
				return up(request);
			} else if("post".equalsIgnoreCase(getCommand(1))) {
				return post(request);
			} else if("activate".equalsIgnoreCase(getCommand(1))) {
				return activate(request);
			} else if("withdraw".equalsIgnoreCase(getCommand(1))) {
				return withdraw(request);
			}  else if("delete".equalsIgnoreCase(getCommand(1))) {
				return delete(request);
			}
		}

		return null;
	}
	
	// DELETE /listing/
    private HttpHeaders delete(HttpServletRequest request) {
    	HttpHeaders headers = new HttpHeadersImpl("delete");
		
    	ListingAndUserVO listing = ListingFacade.instance().deleteNewListing(getLoggedInUser());
		if (listing == null) {
			log.log(Level.WARNING, "Listing not deleted, probably didn't exist!");
			headers.setStatus(500);
		}
		model = listing;

		return headers;
	}

    // PUT /listing/create
    // POST /listing/create
	private HttpHeaders startEditing(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("create");
		
		ListingAndUserVO listing = ListingFacade.instance().createListing(getLoggedInUser());
		if (listing == null) {
			log.log(Level.WARNING, "Listing not created!");
			headers.setStatus(500);
		} else {
			// setting upload urls for documents not yet uploaded
			ListingVO l = listing.getListing();
			String[] url = ServiceFacade.instance().createUploadUrls(getLoggedInUser(), "/file/upload", 4);
			
			l.setBuinessPlanUpload(url[0]);
			l.setFinancialsUpload(url[1]);
			l.setPresentationUpload(url[2]);
			l.setLogoUpload(url[3]);
		}
		model = listing;

		return headers;
	}
	
    // POST /listing/edited
	private HttpHeaders getEditedListingDoc(HttpServletRequest request, String docType) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("edited");
		
		ListingAndUserVO listing = ListingFacade.instance().createListing(getLoggedInUser());
		if (listing == null) {
			log.log(Level.WARNING, "Listing not created!");
			headers.setStatus(500);
		} else {
			ListingVO l = listing.getListing();
			String[] url = ServiceFacade.instance().createUploadUrls(getLoggedInUser(), "/file/upload", 1);
			String returnValue = "<upload_url>" + url[0] + "</upload_url><value>";
			ListingDoc.Type type = ListingDoc.Type.valueOf(docType);
			switch (type) {
			case BUSINESS_PLAN:
				returnValue += l.getBuinessPlanId();
				break;
			case FINANCIALS:
				returnValue += l.getFinancialsId();
				break;
			case PRESENTATION:
				returnValue += l.getPresentationId();
				break;
			case LOGO:
				returnValue += l.getLogo();
				break;
			}
			returnValue += "</value>";
			model = returnValue;
		}
		return headers;
	}	

    // PUT /listing/update
    // POST /listing/update
	private HttpHeaders update(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("update");
		
		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String listingString = request.getParameter("listing");
		if (!StringUtils.isEmpty(listingString)) {
			ListingVO listing = mapper.readValue(listingString, ListingVO.class);
			log.log(Level.INFO, "Updating listing: " + listing);
			if (listing.getId() == null) {
				log.log(Level.WARNING, "Listing id is not provided!");
			} else {
				listing = ListingFacade.instance().updateListing(getLoggedInUser(), listing);
				if (listing == null) {
					log.log(Level.WARNING, "Listing not found!");
					headers.setStatus(500);
				}
				String[] url = ServiceFacade.instance().createUploadUrls(getLoggedInUser(), "/file/upload", 4);
				listing.setBuinessPlanUpload(url[0]);
				listing.setFinancialsUpload(url[1]);
				listing.setPresentationUpload(url[2]);
				listing.setLogoUpload(url[3]);
			}
			model = listing;
		} else {
			log.log(Level.WARNING, "Parameter 'listing' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}
	
    // PUT /listing/update_field
    // POST /listing/update_field
	private HttpHeaders updateField(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("update_field");
		
		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String listingString = request.getParameter("listing");
		if (!StringUtils.isEmpty(listingString)) {
			JsonNode rootNode = mapper.readValue(listingString, JsonNode.class);
			List<ListingPropertyVO> properties = new ArrayList<ListingPropertyVO>();
			
			Iterator<Entry<String, JsonNode>> fields = rootNode.getFields();
			for (Entry<String, JsonNode> node; fields.hasNext();) {
				node = fields.next();
				properties.add(new ListingPropertyVO(node.getKey(), node.getValue().getValueAsText()));
			}
			log.log(Level.INFO, "Updating listing: " + properties);
			ListingAndUserVO listing = ListingFacade.instance().updateListingProperties(getLoggedInUser(), properties);
			if (listing != null && listing.getListing() != null) {
				String[] url = ServiceFacade.instance().createUploadUrls(getLoggedInUser(), "/file/upload", 4);
				listing.getListing().setBuinessPlanUpload(url[0]);
				listing.getListing().setFinancialsUpload(url[1]);
				listing.getListing().setPresentationUpload(url[2]);
				listing.getListing().setLogoUpload(url[3]);
			}
			model = listing;
		} else {
			log.log(Level.WARNING, "Parameter 'listing' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}
	
    // GET /listings/keyword
    private HttpHeaders keyword(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	String text = getCommandOrParameter(request, 2, "text");
    	model = ListingFacade.instance().listingKeywordSearch(getLoggedInUser(), text, listingProperties);
        return new HttpHeadersImpl("keyword").disableCaching();
    }

	// GET /listings/closing
	private HttpHeaders closing(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	model = ListingFacade.instance().getClosingActiveListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("valued").disableCaching();
	}

	// GET /listings/latest
	private HttpHeaders latest(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	model = ListingFacade.instance().getLatestActiveListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("valued").disableCaching();
	}

	// GET /listings/discussed
	private HttpHeaders discussed(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	model = ListingFacade.instance().getMostDiscussedActiveListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("valued").disableCaching();
	}

	// GET /listings/popular
	private HttpHeaders popular(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	model = ListingFacade.instance().getMostPopularActiveListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("valued").disableCaching();
	}

	// GET /listings/valuation
	private HttpHeaders valuation(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	model = ListingFacade.instance().getMostValuedActiveListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("valued").disableCaching();
	}

	// GET /listings/top
    private HttpHeaders top(HttpServletRequest request) {
    	ListPropertiesVO listingProperties = getListProperties(request);
    	model = ListingFacade.instance().getTopActiveListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("top").disableCaching();
    }

    // GET /listings/active
    private HttpHeaders active(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	model = ListingFacade.instance().getLatestActiveListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("active").disableCaching();
    }

    // GET /listings/user
    private HttpHeaders user(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
		ListPropertiesVO listingProperties = getListProperties(request);

    	model = ListingFacade.instance().getUserListings(getLoggedInUser(), userId, listingProperties);
        return new HttpHeadersImpl("user").disableCaching();
    }

    // GET /listings/up
    private HttpHeaders up(HttpServletRequest request) {
    	String listingId = getCommandOrParameter(request, 2, "id");
    	model = ListingFacade.instance().valueUpListing(getLoggedInUser(), listingId);
        return new HttpHeadersImpl("up").disableCaching();
    }

    // GET /listings/activate
    private HttpHeaders activate(HttpServletRequest request) {
    	//String listingId = getCommandOrParameter(request, 2, "id");
    	model = ListingFacade.instance().activateListing(getLoggedInUser(), getLoggedInUser().getEditedListing());
        return new HttpHeadersImpl("activate").disableCaching();
    }

    // GET /listings/activate
    private HttpHeaders post(HttpServletRequest request) {
    	//String listingId = getCommandOrParameter(request, 2, "id");
    	model = ListingFacade.instance().postListing(getLoggedInUser(), getLoggedInUser().getEditedListing());
        return new HttpHeadersImpl("post").disableCaching();
    }

    // GET /listings/withdrawn
    private HttpHeaders withdraw(HttpServletRequest request) {
    	String listingId = getCommandOrParameter(request, 2, "id");
    	model = ListingFacade.instance().withdrawListing(getLoggedInUser(), listingId);
        return new HttpHeadersImpl("withdrawn").disableCaching();
    }

    // GET /listings/get
    private HttpHeaders get(HttpServletRequest request) {
    	String listingId = getCommandOrParameter(request, 2, "id");
    	model = ListingFacade.instance().getListing(getLoggedInUser(), listingId);
        return new HttpHeadersImpl("get").disableCaching();
    }

    // GET /listings/
    private HttpHeaders index(HttpServletRequest request) {
    	String listingId = getCommandOrParameter(request, 1, "id");
    	model = ListingFacade.instance().getListing(getLoggedInUser(), listingId);
        return new HttpHeadersImpl("index").disableCaching();
    }

    // GET /listings/get-all-documents
    private HttpHeaders getAllDocuments(HttpServletRequest request) {
    	//String listingId = getCommandOrParameter(request, 1, "get-all-documents");
    	model = ListingFacade.instance().getGoogleDocDocuments();
        return new HttpHeadersImpl("get-all-documents").disableCaching();
    }

    // GET /listings/categories
    private HttpHeaders getCategories(HttpServletRequest request) {
    	//String listingId = getCommandOrParameter(request, 1, "get-all-documents");
    	model = ListingFacade.instance().getCategories();
        return new HttpHeadersImpl("categories").disableCaching();
    }

	public Object getModel() {
    	return model;
    }
}
