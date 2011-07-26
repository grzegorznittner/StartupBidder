package com.startupbidder.web.controllers;

import javax.servlet.http.HttpServletRequest;

import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

public class UserController extends ModelDrivenController {
	private Object model = null;

	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) {
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
			if ("create".equalsIgnoreCase(getCommand(1))) {
				return create(request);
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

	private HttpHeaders create(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("create");
		headers.setStatus(501);
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
