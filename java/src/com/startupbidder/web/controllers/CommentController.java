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
			} else if("get".equalsIgnoreCase(getCommand(1))) {
				return get(request);
			} else {
				return index(request);
			}
		} else if ("POST".equalsIgnoreCase(request.getMethod())) {
			// moved to listing controller
		}
		return null;
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
