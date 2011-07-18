package com.startupbidder.web.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import com.startupbidder.vo.UserListVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

public class UserController extends ModelDrivenController {
	private UserListVO users = null;
	private UserVO user = null;

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
		HttpHeaders headers = new DefaultHttpHeaders("create");
		headers.setStatus(501);
		return headers;
	}

	private HttpHeaders create(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("create");
		headers.setStatus(501);
		return headers;
	}

	private HttpHeaders all(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("all");
		users = ServiceFacade.instance().getAllUsers();
		return headers;
	}

	private HttpHeaders topInvestor(HttpServletRequest request) {
    	user = ServiceFacade.instance().getTopInvestor();
        return new DefaultHttpHeaders("index").disableCaching();
	}

	private HttpHeaders get(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
    	user = ServiceFacade.instance().getUser(userId);
        return new DefaultHttpHeaders("index").disableCaching();
	}

	private HttpHeaders index(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 1, "id");
    	user = ServiceFacade.instance().getUser(userId);
        return new DefaultHttpHeaders("index").disableCaching();
	}

	@Override
	public Object getModel() {
		return user != null ? user : users;
	}

}
