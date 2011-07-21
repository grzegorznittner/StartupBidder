package com.startupbidder.web.controllers;

import javax.servlet.http.HttpServletRequest;

import com.startupbidder.vo.CommentListVO;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

public class CommentController extends ModelDrivenController {

	private CommentListVO comments = null;
	private CommentVO comment = null;
	
	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) {
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			// GET method handler
			
			if("all".equalsIgnoreCase(getCommand(1))) {
				return all(request);
			} else if("user".equalsIgnoreCase(getCommand(1))) {
				return user(request);
			} else if("listing".equalsIgnoreCase(getCommand(1))) {
				return listing(request);
			} else if("get".equalsIgnoreCase(getCommand(1))) {
				return get(request);
			} else {
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

	private HttpHeaders delete(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("delete");
		headers.setStatus(501);
		return headers;
	}

	private HttpHeaders create(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("create");
		headers.setStatus(501);
		return headers;
	}

	/*
	 *  /comments/user/ag1zdGFydHVwYmlkZGVychQLEgdMaXN0aW5nIgdtaXNsZWFkDA/.html
	 *  /comments/listing/?id=ag1zdGFydHVwYmlkZGVychQLEgdMaXN0aW5nIgdtaXNsZWFkDA.html
	 */
	private HttpHeaders listing(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("listing");
		
		ListPropertiesVO commentProperties = getListProperties(request);
		String listingId = getCommandOrParameter(request, 2, "id");
		comments = ServiceFacade.instance().getCommentsForListing(getLoggedInUser(), listingId, commentProperties);
		
		return headers;
	}

	/*
	 *  /comments/user/ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw/.html
	 *  /comments/user/?id=ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw.html
	 */
	private HttpHeaders user(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("user");
		
		ListPropertiesVO commentProperties = getListProperties(request);
		String userId = getCommandOrParameter(request, 2, "id");
		comments = ServiceFacade.instance().getCommentsForUser(getLoggedInUser(), userId, commentProperties);
		
		return headers;
	}

	private HttpHeaders index(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("index");
		String commentId = getCommandOrParameter(request, 1, "id");
		comment = ServiceFacade.instance().getComment(getLoggedInUser(), commentId);
		return headers;
	}

	private HttpHeaders get(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("get");
		String commentId = getCommandOrParameter(request, 2, "id");
		comment = ServiceFacade.instance().getComment(getLoggedInUser(), commentId);
		return headers;
	}

	private HttpHeaders all(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("all");
		headers.setStatus(501);
		return headers;
	}

	@Override
	public Object getModel() {
		return comments != null ? comments : comment;
	}

}
