package com.startupbidder.web.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import com.google.appengine.api.blobstore.BlobKey;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.Listing.State;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.vo.BaseVO;
import com.startupbidder.vo.BidListVO;
import com.startupbidder.vo.BidUserListVO;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingAndUserVO;
import com.startupbidder.vo.ListingPropertyVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.OrderBook;
import com.startupbidder.vo.QuestionAnswerVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.BidFacade;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ListingFacade;
import com.startupbidder.web.ListingImportService;
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
				if (!StringUtils.isEmpty(getCommand(2))) {
					return getEditedListingDoc(request, getCommand(2), getCommand(3));
				} else {
					return startEditing(request);
				}
			} else if("discover".equalsIgnoreCase(getCommand(1))) {
				return discover(request);
			} else if("discover_user".equalsIgnoreCase(getCommand(1))) {
				return discoverUser(request);
			} else if("top".equalsIgnoreCase(getCommand(1))) {
				return top(request);
			} else if("posted".equalsIgnoreCase(getCommand(1))) {
				return posted(request);
			} else if("frozen".equalsIgnoreCase(getCommand(1))) {
				return frozen(request);
			} else if("valuation".equalsIgnoreCase(getCommand(1))) {
				return valuation(request);
			} else if("popular".equalsIgnoreCase(getCommand(1))) {
				return popular(request);
			} else if("discussed".equalsIgnoreCase(getCommand(1))) {
				return discussed(request);
            } else if("category".equalsIgnoreCase(getCommand(1))) {
                return category(request);
            } else if("location".equalsIgnoreCase(getCommand(1))) {
                return location(request);
            } else if("latest".equalsIgnoreCase(getCommand(1))) {
                return latest(request);
			} else if("closing".equalsIgnoreCase(getCommand(1))) {
				return closing(request);
			} else if("monitored".equalsIgnoreCase(getCommand(1))) {
				return monitored(request);
			} else if("user".equalsIgnoreCase(getCommand(1))) {
				return user(request);
			} else if("get".equalsIgnoreCase(getCommand(1))) {
				return get(request);
			} else if("keyword".equalsIgnoreCase(getCommand(1))) {
				return keyword(request);
			} else if("categories".equalsIgnoreCase(getCommand(1))) {
				return getCategories(request);
			} else if("used_categories".equalsIgnoreCase(getCommand(1))) {
				return getUsedCategories(request);
			} else if("locations".equalsIgnoreCase(getCommand(1))) {
				return getLocations(request);
			} else if("all_listing_locations".equalsIgnoreCase(getCommand(1))) {
				return getAllListingLocations(request);
            } else if("questions_answers".equalsIgnoreCase(getCommand(1))) {
                return questionsAndAnswers(request);
			} else if("order_book".equalsIgnoreCase(getCommand(1))) {
                return orderBook(request);
			} else if("investors".equalsIgnoreCase(getCommand(1))) {
                return investors(request);
			} else if("bids".equalsIgnoreCase(getCommand(1))) {
                return bids(request);
			} else if("comments".equalsIgnoreCase(getCommand(1))) {
				return listingComments(request);
			} else if ("logo".equalsIgnoreCase(getCommand(1))) {
				return logo(request);
			} else if ("picture".equalsIgnoreCase(getCommand(1))) {
				return picture(request);
			} else if ("query_import".equalsIgnoreCase(getCommand(1))) {
				return queryImport(request);
			} else if ("import_types".equalsIgnoreCase(getCommand(1))) {
				return importTypes(request);
			}
		} else if ("POST".equalsIgnoreCase(request.getMethod())) {
			if ("create".equalsIgnoreCase(getCommand(1))) {
				return startEditing(request);
			} else if ("import".equalsIgnoreCase(getCommand(1))) {
				return importListing(request);
			} else if ("update_field".equalsIgnoreCase(getCommand(1))) {
				return updateField(request);
			} else if ("update_address".equalsIgnoreCase(getCommand(1))) {
				return updateAddress(request);
			} else if("post".equalsIgnoreCase(getCommand(1))) {
				return post(request);
			} else if("activate".equalsIgnoreCase(getCommand(1))) {
				return activate(request);
			} else if("send_back".equalsIgnoreCase(getCommand(1))) {
				return sendBack(request);
			} else if("freeze".equalsIgnoreCase(getCommand(1))) {
				return freeze(request);
			} else if("withdraw".equalsIgnoreCase(getCommand(1))) {
				return withdraw(request);
			} else if("delete".equalsIgnoreCase(getCommand(1))) {
				return delete(request);
			} else if("delete_file".equalsIgnoreCase(getCommand(1))) {
				return deleteFile(request);
			} else if("ask_owner".equalsIgnoreCase(getCommand(1))) {
				return askOwner(request);
			} else if("answer_question".equalsIgnoreCase(getCommand(1))) {
				return answerQuestion(request);
			} else if("post_comment".equalsIgnoreCase(getCommand(1))) {
				return postComment(request);
			} else if("update_comment".equalsIgnoreCase(getCommand(1))) {
				return updateComment(request);
			} else if("delete_comment".equalsIgnoreCase(getCommand(1))) {
				return deleteComment(request);
			} else if("make_bid".equalsIgnoreCase(getCommand(1))) {
                return makeBid(request);
			} else if("swap_pictures".equalsIgnoreCase(getCommand(1))) {
                return swapPictures(request);
			}
		}

		return null;
	}
	
	// POST /listing/delete
    private HttpHeaders delete(HttpServletRequest request) {
    	HttpHeaders headers = new HttpHeadersImpl("delete");
		
    	String listingId = getCommandOrParameter(request, 2, "id");
    	ListingAndUserVO listing = ListingFacade.instance().deleteEditedListing(getLoggedInUser(), listingId);
		if (listing == null) {
			log.log(Level.WARNING, "Listing not deleted, probably didn't exist!");
			headers.setStatus(500);
		}
		model = listing;

		return headers;
	}
    
    // POST /listing/delete_file
    private HttpHeaders deleteFile(HttpServletRequest request) {
    	HttpHeaders headers = new HttpHeadersImpl("delete_file");
		
    	String listingId = getCommandOrParameter(request, 2, "id");
    	String typeStr = getCommandOrParameter(request, 3, "type");
    	ListingDoc.Type type = null;
    	try {
    		type = ListingDoc.Type.valueOf(typeStr);
    	} catch (Exception e) {
    		log.log(Level.WARNING, "Wrong document type!", e);
    		headers.setStatus(500);
    		return headers;
    	}
    	
		if (StringUtils.isEmpty(listingId)) {
			listingId = getLoggedInUser().getEditedListing();
		}
    	ListingAndUserVO listing = ListingFacade.instance().deleteListingFile(getLoggedInUser(), listingId, type);
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

        UserVO loggedInUser = getLoggedInUser();
		ListingAndUserVO listing = ListingFacade.instance().createListing(loggedInUser);
		if (listing == null) {
			log.log(Level.WARNING, "Listing not created!");
			headers.setStatus(500);
		} else {
			ListingVO l = listing.getListing();
			if (loggedInUser != null) {
				loggedInUser.setEditedListing(l.getId());
				loggedInUser.setEditedStatus(l.getState()); // reset in case listing state is not NEW
			}
        }
		model = listing;

		return headers;
	}
	
    // GET /listing/query_import
	private HttpHeaders queryImport(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("query_import");

		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String type = request.getParameter("type");
		String query = request.getParameter("query");
		model = ListingImportService.instance().getImportSuggestions(getLoggedInUser(), type, query);
		
		return headers;
	}
	
    // GET /listing/import_types
	private HttpHeaders importTypes(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("import_types");

		model = ListingImportService.instance().availableImportTypes(getLoggedInUser());
		
		return headers;
	}
	
    // PUT /listing/listing
    // POST /listing/listing
	private HttpHeaders importListing(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("import");

		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String type = request.getParameter("type");
		String id = request.getParameter("id");
		
        UserVO user = getLoggedInUser();
		ListingAndUserVO listing = ListingFacade.instance().importListing(user, type, id);
		if (listing == null) {
			log.log(Level.WARNING, "Listing not imported!");
			headers.setStatus(500);
		} else {
			// setting upload urls for documents not yet uploaded
			ListingVO l = listing.getListing();
			String[] url = ServiceFacade.instance().createUploadUrls(getLoggedInUser(), "/file/upload/" + l.getId() + "/", 1);
			l.setUploadUrl(url[0]);
            user.setEditedListing(l.getId());
            user.setEditedStatus(l.getState()); // reset in case listing state is not NEW
        }
		model = listing;

		return headers;
	}
	
    // POST /listing/edited
	private HttpHeaders getEditedListingDoc(HttpServletRequest request, String docType, String listingId) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("edited");
		
		ListingAndUserVO listing = null;
		if (StringUtils.isEmpty(listingId)) {
			listing = ListingFacade.instance().createListing(getLoggedInUser());
		} else {
			listing = ListingFacade.instance().getListing(getLoggedInUser(), listingId);
		}
		
		if (listing == null) {
			log.log(Level.WARNING, "Listing not available!");
			headers.setStatus(500);
		} else {
			ListingVO l = listing.getListing();
			String[] url = ServiceFacade.instance().createUploadUrls(getLoggedInUser(), "/file/upload/" + listingId + "/", 1);
			String returnValue = "<upload_url>" + url[0] + "</upload_url><value>";
			ListingDoc.Type type = null;
			try {
				type = ListingDoc.Type.valueOf(docType);
			} catch (Exception e) {
				log.log(Level.WARNING, "DocType not recognized: " + docType, e);
			}
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

    // PUT /listing/update_field
    // POST /listing/update_field
	private HttpHeaders updateField(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("update_field");
		
		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String listingString = request.getParameter("listing");
		if (!StringUtils.isEmpty(listingString)) {
			JsonNode rootNode = mapper.readValue(listingString, JsonNode.class);
			if (rootNode.get("update_address") != null) {
				return updateAddress(request);
			}
			List<ListingPropertyVO> properties = new ArrayList<ListingPropertyVO>();
			
			String listingId = null;
			Iterator<Entry<String, JsonNode>> fields = rootNode.getFields();
			for (Entry<String, JsonNode> node; fields.hasNext();) {
				node = fields.next();
				String key = node.getKey();
                log.log(Level.INFO, "Recevied property " + key + " with value: " + node.getValue().getValueAsText());
				if (StringUtils.equals("id", key)) {
					listingId = node.getValue().getValueAsText();
				} else {
					properties.add(new ListingPropertyVO(key, node.getValue().getValueAsText()));
				}
			}
			log.log(Level.INFO, "Updating listing: " + properties + ". Provided id: " + listingId);
			ListingAndUserVO listing = ListingFacade.instance().updateListingProperties(getLoggedInUser(), listingId, properties);
			if (listing != null && listing.getListing() != null) {
				String[] url = ServiceFacade.instance().createUploadUrls(getLoggedInUser(), "/file/upload/" + listing.getListing().getId() + "/", 1);
				listing.getListing().setUploadUrl(url[0]);
			}
			model = listing;
		} else {
			log.log(Level.WARNING, "Parameter 'listing' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}
	
    // PUT /listing/update_address
    // POST /listing/update_address
	private HttpHeaders updateAddress(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("update_address");
		
		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String addressString = request.getParameter("listing");
		if (!StringUtils.isEmpty(addressString)) {
			JsonNode rootNode = mapper.readValue(addressString, JsonNode.class);
			Map<String, String> properties = new HashMap<String, String>();
			
			if (rootNode.get("update_address") == null) {
				log.log(Level.WARNING, "JSON element 'update_address' is not available!");
				headers.setStatus(500);
				return headers;
			}
			JsonNode addressComponents = rootNode.get("update_address").get("address_components");
			if (addressComponents != null) {
				Iterator<JsonNode> elements = addressComponents.getElements();
				for (; elements.hasNext(); ) {
					String[] comp = getAddressComponents(elements.next());
					if (comp != null) {
						// using type and short_name
						properties.put("SHORT_" + comp[0], comp[1]);
						// using type and long_name
						properties.put("LONG_" + comp[0], comp[2]);
					}
				}
			} else {
				log.log(Level.WARNING, "JSON element 'address_components' is not available!");
				headers.setStatus(500);
				return headers;
			}
			JsonNode formattedAddress = rootNode.get("update_address").get("formatted_address");
			if (formattedAddress != null) {
				properties.put("formatted_address", formattedAddress.getValueAsText());
			}
			JsonNode geometry = rootNode.get("update_address").get("geometry");
			if (geometry != null && geometry.get("location") != null) {
				Iterator<JsonNode> locationIt = geometry.get("location").getElements();
				String ta = locationIt.next().getValueAsText();
				String ua = locationIt.next().getValueAsText();
				
				properties.put("latitude", ta);
				properties.put("longitude", ua);
			} else {
				log.log(Level.WARNING, "JSON element 'geometry/location/Ta|Ua' is not available!");
				headers.setStatus(500);
				return headers;
			}
			
			log.log(Level.INFO, "Updating listing address: " + properties);
			ListingAndUserVO listing = ListingFacade.instance().updateListingAddressProperties(getLoggedInUser(), properties);
			if (listing != null && listing.getListing() != null) {
				String[] url = ServiceFacade.instance().createUploadUrls(getLoggedInUser(), "/file/upload/" + listing.getListing().getId() + "/", 1);
				listing.getListing().setUploadUrl(url[0]);
			}
			model = listing;
		} else {
			log.log(Level.WARNING, "Parameter 'listing' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}
	
	public static String[] getAddressComponents(JsonNode element) {
		String[] address = null;
		
		JsonNode types = element.get("types");
		if (types != null && types instanceof ArrayNode) {
			String type1 = types.get(0) != null ? types.get(0).getValueAsText() : null;
			String type2 = types.get(1) != null ? types.get(1).getValueAsText() : null;
			
			if (!StringUtils.equals("political", type2)) {
				return null;
			}
			address = new String[3];
			address[0] = type1;
			if (element.get("short_name") != null) {
				address[1] = element.get("short_name").getValueAsText();
			}
			if (element.get("long_name") != null) {
				address[2] = element.get("long_name").getValueAsText();
			}
		}
		return address;
	}
	
    // GET /listings/keyword
    private HttpHeaders keyword(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	String text;
		try {
			text = URLDecoder.decode(getCommandOrParameter(request, 2, "text"), "UTF-8");
	    	model = ListingFacade.instance().listingKeywordSearch(getLoggedInUser(), text, listingProperties);
		} catch (UnsupportedEncodingException e) {
			log.log(Level.SEVERE, "Parameter decoding error", e);
		}
        return new HttpHeadersImpl("keyword").disableCaching();
    }

	// GET /listings/discover
	private HttpHeaders discover(HttpServletRequest request) {
    	model = ListingFacade.instance().getDiscoverListingList(getLoggedInUser());
        return new HttpHeadersImpl("discover").disableCaching();
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
        return new HttpHeadersImpl("latest").disableCaching();
	}

	// GET /listings/category
	private HttpHeaders category(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
        try {
            String categoryString = URLDecoder.decode(getCommandOrParameter(request, 2, "category"), "UTF-8");
            model = ListingFacade.instance().getListingsForCategory(categoryString, listingProperties);
        } catch (UnsupportedEncodingException e) {
            log.log(Level.SEVERE, "Parameter decoding error", e);
        }
        return new HttpHeadersImpl("category").disableCaching();
	}

	// GET /listings/location
	private HttpHeaders location(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
        try {
            String locationString = URLDecoder.decode(getCommandOrParameter(request, 2, "location"), "UTF-8");
            model = ListingFacade.instance().getListingsForLocation(locationString, listingProperties);
        } catch (UnsupportedEncodingException e) {
            log.log(Level.SEVERE, "Parameter decoding error", e);
        }
        return new HttpHeadersImpl("location").disableCaching();
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

    // GET /listings/frozen
    private HttpHeaders frozen(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	model = ListingFacade.instance().getFrozenListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("frozen").disableCaching();
    }

    // GET /listings/posted
    private HttpHeaders posted(HttpServletRequest request) {
		ListPropertiesVO listingProperties = getListProperties(request);
    	model = ListingFacade.instance().getPostedListings(getLoggedInUser(), listingProperties);
        return new HttpHeadersImpl("posted").disableCaching();
    }

	// GET /listings/monitored
	private HttpHeaders monitored(HttpServletRequest request) {
		String userId = getCommandOrParameter(request, 2, "id");
		ListPropertiesVO listingProperties = getListProperties(request);
    	model = ListingFacade.instance().getMonitoredListings(getLoggedInUser(), userId, listingProperties);
        return new HttpHeadersImpl("monitored").disableCaching();
	}

    // GET /listings/user/<state>/
    private HttpHeaders user(HttpServletRequest request) {
    	String state = getCommandOrParameter(request, 2, "state");
    	String id = getCommandOrParameter(request, 3, "id");
		ListPropertiesVO listingProperties = getListProperties(request);

    	model = ListingFacade.instance().getUserListings(getLoggedInUser(), state, id, listingProperties);
        return new HttpHeadersImpl("user").disableCaching();
    }

    // GET /listings/discover_user
    private HttpHeaders discoverUser(HttpServletRequest request) {
    	UserVO loggedIn = getLoggedInUser();
    	model = ListingFacade.instance().getDiscoverUserListings(loggedIn);
        return new HttpHeadersImpl("discover_user").disableCaching();
    }

    // GET /listings/activate
    private HttpHeaders activate(HttpServletRequest request) {
    	String listingId = getCommandOrParameter(request, 2, "id");
    	model = ListingFacade.instance().activateListing(getLoggedInUser(), listingId);
        return new HttpHeadersImpl("activate").disableCaching();
    }

    // POST /listings/send_back
    private HttpHeaders sendBack(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String listingString = request.getParameter("listing");
		if (!StringUtils.isEmpty(listingString)) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readValue(listingString, JsonNode.class);
			String listingId = null;
			if (rootNode.get("id") != null) {
				listingId = rootNode.get("id").getValueAsText();
			}
			String message = null;
			if (rootNode.get("message") != null) {
				message = rootNode.get("message").getValueAsText();
				model = ListingFacade.instance().sendBackListingToOwner(getLoggedInUser(), listingId, message);
			}
		} else {
			String listingId = getCommandOrParameter(request, 2, "id");
			model = ListingFacade.instance().sendBackListingToOwner(getLoggedInUser(), listingId, null);
		}
		return new HttpHeadersImpl("send_back").disableCaching();
    }

    // POST /listings/freeze
    private HttpHeaders freeze(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
    	log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String listingString = request.getParameter("listing");
		if (!StringUtils.isEmpty(listingString)) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readValue(listingString, JsonNode.class);
			String listingId = null;
			if (rootNode.get("id") != null) {
				listingId = rootNode.get("id").getValueAsText();
			}
			String message = null;
			if (rootNode.get("message") != null) {
				message = rootNode.get("message").getValueAsText();
				model = ListingFacade.instance().freezeListing(getLoggedInUser(), listingId, message);
			}
		} else {
			String listingId = getCommandOrParameter(request, 2, "id");
			model = ListingFacade.instance().freezeListing(getLoggedInUser(), listingId, null);
		}
        return new HttpHeadersImpl("activate").disableCaching();
    }

    // POST /listings/activate
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

    // GET /listings/categories
    private HttpHeaders getCategories(HttpServletRequest request) {
    	model = ListingFacade.instance().getCategories();
        return new HttpHeadersImpl("categories").disableCaching();
    }

    // GET /listings/used_categories
    private HttpHeaders getUsedCategories(HttpServletRequest request) {
    	model = ListingFacade.instance().getTopCategories();
        return new HttpHeadersImpl("used_categories").disableCaching();
    }

    // GET /listings/locations
    private HttpHeaders getLocations(HttpServletRequest request) {
    	model = ListingFacade.instance().getTopLocations();
        return new HttpHeadersImpl("locations").disableCaching();
    }

    // GET /listings/all_listing_locations
    private HttpHeaders getAllListingLocations(HttpServletRequest request) {
    	String latLong = request.getHeader("X-AppEngine-CityLatLong");
    	if (StringUtils.isEmpty(latLong)) {
    		latLong = "51.224942,6.775652";
    	}
    	model = ListingFacade.instance().getAllListingLocations(latLong);
        return new HttpHeadersImpl("all_listing_locations").disableCaching();
    }

	private HttpHeaders logo(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("logo");
		
		String listingId = getCommandOrParameter(request, 2, "id");

		Listing listing = ObjectifyDatastoreDAO.getInstance().getListing(BaseVO.toKeyId(listingId));
		if (listing == null) {
			log.log(Level.INFO, "Listing not found!");
			headers.setStatus(500);
			return headers;
		}
		ListingDoc doc = ObjectifyDatastoreDAO.getInstance().getListingDocument(listing.logoId.getId());
		log.log(Level.INFO, "Sending back logo: " + doc);
		if (doc != null && doc.blob != null) {
			headers.addHeader("Cache-Control", "public, max-age=86400");
			headers.setBlobKey(doc.blob);
		} else {
			log.log(Level.INFO, "Document not found or blob not available!");
			headers.setStatus(500);
		}
		model = "Returning logo for listing " + listingId;
		
		return headers;
	}
	
	private HttpHeaders picture(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("picture");
		
		String listingId = getCommandOrParameter(request, 2, "id");
		String picNrStr = getCommandOrParameter(request, 3, "nr");
		int picNr = NumberUtils.toInt(picNrStr, 0);
		if (picNr < 1 || picNr > 5) {
			log.log(Level.INFO, "Wrong picture number, picNr=" + picNr);
			headers.setStatus(500);
			return headers;
		}

		Pair<BlobKey, Listing.State> pictureBlob = ListingFacade.instance().getPictureBlob(listingId, picNr);
		log.log(Level.INFO, "Sending back picture: " + pictureBlob);
		if (pictureBlob != null) {
			if (pictureBlob.getRight() == State.ACTIVE) {
				headers.addHeader("Cache-Control", "public, max-age=86400");
			}
			headers.setBlobKey(pictureBlob.getLeft());
		} else {
			log.log(Level.INFO, "Picture not found or blob not available!");
			headers.setStatus(500);
		}
		model = "Returning picture " + picNr + " for listing " + listingId;
		return headers;
	}

	private HttpHeaders swapPictures(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("swap_pictures");
		
		String listingId = getCommandOrParameter(request, 2, "id");
		String picFromNrStr = getCommandOrParameter(request, 3, "nr_a");
		String picToNrStr = getCommandOrParameter(request, 4, "nr_b");
		int picFromNr = NumberUtils.toInt(picFromNrStr, 0);
		int picToNr = NumberUtils.toInt(picToNrStr, 0);
		if (!ListingFacade.instance().swapPictures(getLoggedInUser(), listingId, picFromNr, picToNr)) {
			log.log(Level.INFO, "Picture swap error!");
			headers.setStatus(500);
			return headers;
		}
		model = "Swaped " + picFromNr + " <->" + picToNr;
		return headers;
	}
	
    private HttpHeaders questionsAndAnswers(HttpServletRequest request) {
        String listingId = getCommandOrParameter(request, 2, "id");
        ListPropertiesVO listingProperties = getListProperties(request);
        model = ServiceFacade.instance().getQuestionsAndAnswers(getLoggedInUser(), listingId, listingProperties);
        return new HttpHeadersImpl("questions_answers").disableCaching();
    }
    
    // GET /listings/ask_owner
    private HttpHeaders askOwner(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("ask_owner");

		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String listingString = request.getParameter("message");
		if (!StringUtils.isEmpty(listingString)) {
			JsonNode rootNode = mapper.readValue(listingString, JsonNode.class);
			String listingId = null;
			if (rootNode.get("listing_id") != null) {
				listingId = rootNode.get("listing_id").getValueAsText();
			}
			String text = null;
			if (rootNode.get("text") != null) {
				text = rootNode.get("text").getValueAsText();
			}
			QuestionAnswerVO qa = ServiceFacade.instance().askOwner(getLoggedInUser(), text, listingId);
			model = qa;
		} else {
			log.severe("Missing message parameter!");
			headers.setStatus(500);
		}
		return headers;
    }

    // GET /listings/answer_question
    private HttpHeaders answerQuestion(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("answer_question");

		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String listingString = request.getParameter("message");
		if (!StringUtils.isEmpty(listingString)) {
			JsonNode rootNode = mapper.readValue(listingString, JsonNode.class);
			String questionId = null;
			if (rootNode.get("question_id") != null) {
				questionId = rootNode.get("question_id").getValueAsText();
			}
			String text = null;
			if (rootNode.get("text") != null) {
				text = rootNode.get("text").getValueAsText();
			}
			log.info("Method answer_question called, parameter question_id: " + questionId);
			QuestionAnswerVO qa = ServiceFacade.instance().answerQuestion(getLoggedInUser(), questionId, text);
			model = qa;
		} else {
			log.severe("Missing message parameter!");
			headers.setStatus(500);
		}
		return headers;
    }

	/*
	 * POST /listing/post_comment?comment=<comment json>
	 */
	private HttpHeaders postComment(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("post_comment");
		
		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String commentString = request.getParameter("comment");
		if (!StringUtils.isEmpty(commentString)) {
			CommentVO comment = mapper.readValue(commentString, CommentVO.class);
			log.log(Level.INFO, "Creating comment: " + comment);
			comment = ServiceFacade.instance().createComment(getLoggedInUser(), comment.getListing(), comment.getComment());
			model = comment;
			if (comment == null) {
				log.log(Level.WARNING, "Comment not created!");
				headers.setStatus(500);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'comment' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}

	/*
	 * PUT /listing/update_comment?comment=<comment json>
	 */
	private HttpHeaders updateComment(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("update_comment");
		
		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String commentString = request.getParameter("comment");
		if (!StringUtils.isEmpty(commentString)) {
			CommentVO comment = mapper.readValue(commentString, CommentVO.class);
			log.log(Level.INFO, "Updating comment: " + comment);
			if (comment == null || comment.getId() == null) {
				log.log(Level.WARNING, "Commend id not provided!");
				headers.setStatus(500);
			} else {
				comment = ServiceFacade.instance().updateComment(getLoggedInUser(), comment);
				model = comment;
				if (comment == null) {
					log.log(Level.WARNING, "Comment not found!");
					headers.setStatus(500);
				}
			}
		} else {
			log.log(Level.WARNING, "Parameter 'comment' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}

	/*
	 * DELETE /comment?id=<id> 
	 * POST /comment/delete?id=<id>
	 */
	private HttpHeaders deleteComment(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("delete_comment");
		
		String commentId = getCommandOrParameter(request, 2, "id");
		if (!StringUtils.isEmpty(commentId)) {
            // this always returns null
			model = ServiceFacade.instance().deleteComment(getLoggedInUser(), commentId);
		} else {
			log.log(Level.WARNING, "Parameter 'id' is not provided!");
			headers.setStatus(500);
		}
		
		return headers;
	}

	/*
	 *  /listing/comments/?id=ag1zdGFydHVwYmlkZGVychQLEgdMaXN0aW5nIgdtaXNsZWFkDA.html
	 */
	private HttpHeaders listingComments(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("comments");
		
		ListPropertiesVO commentProperties = getListProperties(request);
		if (commentProperties.getMaxResults() <= 5) {
			commentProperties.setMaxResults(20);
		}
		String listingId = getCommandOrParameter(request, 2, "id");
		model = ServiceFacade.instance().getCommentsForListing(getLoggedInUser(), listingId, commentProperties);
		
		return headers;
	}

    // POST /listing/make_bid
    private HttpHeaders makeBid(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("make_bid");

		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String bidString = request.getParameter("bid");
		if (!StringUtils.isEmpty(bidString)) {
			JsonNode rootNode = mapper.readValue(bidString, JsonNode.class);
			String listingId = null;
			if (rootNode.get("listing_id") != null) {
				listingId = rootNode.get("listing_id").getValueAsText();
			}
			String investorId = null;
			if (rootNode.get("investor_id") != null) {
				investorId = rootNode.get("investor_id").getValueAsText();
			}
			String text = null;
			if (rootNode.get("text") != null) {
				text = rootNode.get("text").getValueAsText();
			}
			String type = null;
			if (rootNode.get("type") != null) {
				type = rootNode.get("type").getValueAsText();
			}
			int amount = 0;
			if (rootNode.get("amt") != null) {
				amount = rootNode.get("amt").getValueAsInt();
			}
			int percentage = 0;
			if (rootNode.get("pct") != null) {
				percentage = rootNode.get("pct").getValueAsInt();
			}
			log.info("Make bid called with params: listing_id=" + listingId + ", investor_id=" + investorId
					+ ", text=" + text + ", type=" + type + ", amt=" + amount + ", pct=" + percentage);
			BidListVO result = null;
			if (StringUtils.isEmpty(investorId)) {
				result = BidFacade.instance().makeBid(getLoggedInUser(), listingId, type, amount, percentage, text);
			} else {
				result = BidFacade.instance().ownerMakesBid(getLoggedInUser(), listingId, investorId, type, amount, percentage, text);
			}
			model = result;
		} else {
			log.severe("Missing bid parameter!");
			headers.setStatus(500);
		}
		return headers;
    }

	/*
	 *  /listing/bid_users/
	 */
	private HttpHeaders investors(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("investors");
		
		String listingId = getCommandOrParameter(request, 2, "id");
		ListPropertiesVO listProperties = getListProperties(request);
		BidUserListVO result = BidFacade.instance().getInvestors(getLoggedInUser(), listingId, listProperties);
		model = result;
		
		return headers;
	}
	
	/*
	 *  /listing/order_book/
	 */
	private HttpHeaders orderBook(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("order_book");
		
		String listingId = getCommandOrParameter(request, 2, "id");
		OrderBook result = BidFacade.instance().getOrderBook(getLoggedInUser(), listingId);
		model = result;
		
		return headers;
	}
	
	/*
	 *  /listing/bids/?id=<listingid>&investor_id=<userid>.html
	 *  /listing/bids/<listingid>/<from_user_id>/.html
	 */
	private HttpHeaders bids(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("bids");
		
		ListPropertiesVO listProperties = getListProperties(request);
		String listingId = getCommandOrParameter(request, 2, "id");
		String investorId = getCommandOrParameter(request, 3, "investor_id");
		BidListVO result = BidFacade.instance().getBids(getLoggedInUser(), listingId, investorId, listProperties);
		model = result;
		return headers;
	}

	public Object getModel() {
    	return model;
    }
}
