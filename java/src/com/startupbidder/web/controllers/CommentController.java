package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class CommentController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(CommentController.class.getName());

	private Object model;
	
	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
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
		} else if ("POST".equalsIgnoreCase(request.getMethod())) {
			if("create".equalsIgnoreCase(getCommand(1))) {
				return create(request);
			} else if("update".equalsIgnoreCase(getCommand(1))) {
				return update(request);
			} else if("delete".equalsIgnoreCase(getCommand(1))) {
				return delete(request);
			}
		} else if ("DELETE".equalsIgnoreCase(request.getMethod())) {
			return delete(request);
		}
		return null;
	}

	/*
	 * DELETE /comment?id=<id> 
	 * POST /comment/delete?id=<id>
	 */
	private HttpHeaders delete(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("delete");
		
		String commentId = "DELETE".equalsIgnoreCase(request.getMethod())
				? getCommandOrParameter(request, 1, "id") : getCommandOrParameter(request, 2, "id");
		if (!StringUtils.isEmpty(commentId)) {
            // this always returns null
			ServiceFacade.instance().deleteComment(getLoggedInUser(), commentId);
		} else {
			log.log(Level.WARNING, "Parameter 'id' is not provided!");
			headers.setStatus(500);
		}
		
		return headers;
	}

	/*
	 * POST /comment/create?comment=<bid json>
	 */
	private HttpHeaders create(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("create");
		
		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String commentString = request.getParameter("comment");
		if (!StringUtils.isEmpty(commentString)) {
			CommentVO comment = mapper.readValue(commentString, CommentVO.class);
			log.log(Level.INFO, "Creating comment: " + comment);
			comment = ServiceFacade.instance().createComment(getLoggedInUser(), comment);
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
	 * PUT /comment/update?comment=<bid json>
	 */
	private HttpHeaders update(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("update");
		
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
	 *  /comments/user/ag1zdGFydHVwYmlkZGVychQLEgdMaXN0aW5nIgdtaXNsZWFkDA/.html
	 *  /comments/listing/?id=ag1zdGFydHVwYmlkZGVychQLEgdMaXN0aW5nIgdtaXNsZWFkDA.html
	 */
	private HttpHeaders listing(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("listing");
		
		ListPropertiesVO commentProperties = getListProperties(request);
		String listingId = getCommandOrParameter(request, 2, "id");
		model = ServiceFacade.instance().getCommentsForListing(getLoggedInUser(), listingId, commentProperties);
		
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
		model = ServiceFacade.instance().getCommentsForUser(getLoggedInUser(), userId, commentProperties);
		
		return headers;
	}

	private HttpHeaders index(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("index");
		String commentId = getCommandOrParameter(request, 1, "id");
		model = ServiceFacade.instance().getComment(getLoggedInUser(), commentId);
		return headers;
	}

	private HttpHeaders get(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("get");
		String commentId = getCommandOrParameter(request, 2, "id");
		model = ServiceFacade.instance().getComment(getLoggedInUser(), commentId);
		return headers;
	}

	private HttpHeaders all(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("all");
		headers.setStatus(501);
		return headers;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
