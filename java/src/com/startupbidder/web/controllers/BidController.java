package com.startupbidder.web.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import com.startupbidder.vo.BidListVO;
import com.startupbidder.vo.BidVO;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

public class BidController extends ModelDrivenController {

	private BidListVO bids = null;
	private BidVO bid = null;
	
	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) {
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			// GET method handler
			
			if("all".equalsIgnoreCase(getCommand(1))) {
				return all(request);
			} else if("listing".equalsIgnoreCase(getCommand(1))) {
				return listing(request);
			} else if("user".equalsIgnoreCase(getCommand(1))) {
				return user(request);
			} else {
				return get(request);
			}
		} else if ("PUT".equalsIgnoreCase(request.getMethod()) ||
				"POST".equalsIgnoreCase(request.getMethod())) {
			return create(request);
		} else if ("DELETE".equalsIgnoreCase(request.getMethod())) {
			return delete(request);
		}
		return null;
	}

	private HttpHeaders delete(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("create");
		headers.setStatus(501);
		return headers;
	}

	private HttpHeaders create(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("create");
		headers.setStatus(501);
		return headers;
	}

	private HttpHeaders get(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("get");
		String bidId = getCommandOrParameter(request, 2, "id");
		bid = ServiceFacade.instance().getBid(bidId);
		return headers;
	}

	/*
	 *  /bids/listing/?id=ag1zdGFydHVwYmlkZGVychQLEgdMaXN0aW5nIgdtaXNsZWFkDA
	 */
	private HttpHeaders listing(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("listing");
		
		ListPropertiesVO bidProperties = getListProperties(request);
		String listingId = getCommandOrParameter(request, 2, "id");
		bids = ServiceFacade.instance().getBidsForListing(listingId, bidProperties);
		
		return headers;
	}

	/*
	 *  /bids/user/ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw/
	 *  /bids/user/?id=ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw
	 */
	private HttpHeaders user(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("user");
		
		ListPropertiesVO bidProperties = getListProperties(request);
		String userId = getCommandOrParameter(request, 2, "id");
		bids = ServiceFacade.instance().getBidsForUser(userId, bidProperties);
		
		return headers;
	}

	private HttpHeaders all(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("all");
		headers.setStatus(501);
		return headers;
	}

	@Override
	public Object getModel() {
		return bids != null ? bids : bid;
	}

}
