package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.startupbidder.vo.UserVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

public class UserController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(UserController.class.getName());

	private Object model = null;

	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			// GET method handler
			
			if("all".equalsIgnoreCase(getCommand(1))) {
				return all(request);
			} else if("get".equalsIgnoreCase(getCommand(1))) {
				return get(request);
			} else if("topinvestor".equalsIgnoreCase(getCommand(1))) {
				return topInvestor(request);
			} else if("loggedin".equalsIgnoreCase(getCommand(1))) {
				return loggedin(request);
			} else if("votes".equalsIgnoreCase(getCommand(1))) {
				return votes(request);
			} else {
				return index(request);
			}
		} else if ("PUT".equalsIgnoreCase(request.getMethod()) ||
				"POST".equalsIgnoreCase(request.getMethod())) {
			if ("save".equalsIgnoreCase(getCommand(1))) {
				return save(request);
			} else if("activate".equalsIgnoreCase(getCommand(1))) {
				return activate(request);
			} else if("deactivate".equalsIgnoreCase(getCommand(1))) {
				return deactivate(request);
			}
		} else if ("DELETE".equalsIgnoreCase(request.getMethod())) {
			return delete(request);
		}
		return null;
	}
	
	private HttpHeaders delete(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("create");
		headers.setStatus(501);
		return headers;
	}

	private HttpHeaders save(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("save");
		
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String listingString = request.getParameter("profile");
		if (!StringUtils.isEmpty(listingString)) {
			ObjectMapper mapper = new ObjectMapper();
			UserVO user = mapper.readValue(request.getInputStream(), UserVO.class);
			if (user.getId() == null) {
				//model = ServiceFacade.instance().createUser(getLoggedInUser(), user);
				headers.setStatus(501);
			} else {
				model = ServiceFacade.instance().updateUser(getLoggedInUser(), user);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'profile' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}

	private HttpHeaders all(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("all");
		model = ServiceFacade.instance().getAllUsers(getLoggedInUser());
		return headers;
	}

	private HttpHeaders topInvestor(HttpServletRequest request) {
		model = ServiceFacade.instance().getTopInvestor(getLoggedInUser());
        return new HttpHeadersImpl("index").disableCaching();
	}

	private HttpHeaders loggedin(HttpServletRequest request) {
		model = getLoggedInUser();
        return new HttpHeadersImpl("loggedin").disableCaching();
	}

	private HttpHeaders get(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
    	model = ServiceFacade.instance().getUser(getLoggedInUser(), userId);
        return new HttpHeadersImpl("get").disableCaching();
	}

	private HttpHeaders index(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 1, "id");
    	model = ServiceFacade.instance().getUser(getLoggedInUser(), userId);
        return new HttpHeadersImpl("index").disableCaching();
	}

	private HttpHeaders activate(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
    	if (userId == null) {
    		userId = getLoggedInUser().getId();
    	}
    	model = ServiceFacade.instance().activateUser(getLoggedInUser(), userId);

    	HttpHeaders headers = new HttpHeadersImpl("activate");
		return headers;
	}

	private HttpHeaders deactivate(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
    	if (userId == null) {
    		userId = getLoggedInUser().getId();
    	}
    	model = ServiceFacade.instance().deactivateUser(getLoggedInUser(), userId);

		HttpHeaders headers = new HttpHeadersImpl("deactivate");
		return headers;
	}

	private HttpHeaders votes(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
    	if (userId == null) {
    		userId = getLoggedInUser().getId();
    	}
    	model = ServiceFacade.instance().userVotes(getLoggedInUser(), userId);

		HttpHeaders headers = new HttpHeadersImpl("deactivate");
		return headers;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
