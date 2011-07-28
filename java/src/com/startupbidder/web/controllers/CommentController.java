package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.startupbidder.vo.CommentListVO;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

public class CommentController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(CommentController.class.getName());

	private CommentListVO comments = null;
	private CommentVO comment = null;
	
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
			return create(request);
		} else if ("PUT".equalsIgnoreCase(request.getMethod())) {
			return update(request);
		} else if ("DELETE".equalsIgnoreCase(request.getMethod())) {
			return delete(request);
		}
		return null;
	}

	/*
	 * DELETE /comment?id=<id> 
	 */
	private HttpHeaders delete(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("delete");
		
		String commentId = getCommandOrParameter(request, 2, "id");
		if (StringUtils.isEmpty(commentId)) {
			comment = ServiceFacade.instance().deleteComment(getLoggedInUser(), commentId);
			if (comment == null) {
				log.log(Level.WARNING, "Comment not found!");
				headers.setStatus(500);
			}
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
		HttpHeaders headers = new HttpHeadersImpl("save");
		
		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String commentString = request.getParameter("comment");
		if (!StringUtils.isEmpty(commentString)) {
			comment = mapper.readValue(commentString, CommentVO.class);
			log.log(Level.INFO, "Creating comment: " + comment);
			comment = ServiceFacade.instance().createComment(getLoggedInUser(), comment);
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
			comment = mapper.readValue(commentString, CommentVO.class);
			log.log(Level.INFO, "Updating comment: " + comment);
			if (comment.getId() == null) {
				log.log(Level.WARNING, "Commend id not provided!");
				headers.setStatus(500);
			} else {
				comment = ServiceFacade.instance().updateComment(getLoggedInUser(), comment);
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
