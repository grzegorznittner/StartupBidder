package com.startupbidder.web.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.startupbidder.vo.SystemPropertyVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

public class SystemController extends ModelDrivenController {
	private Object model;

	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			if("set-property".equalsIgnoreCase(getCommand(1))) {
				return setProperty(request);
			} else if("set-datastore".equalsIgnoreCase(getCommand(1))) {
				return setDatastore(request);
			}
		}
		return null;
	}

	private HttpHeaders setProperty(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("set-property");
		
		String name = request.getParameter("name");
		String value = request.getParameter("value");
		
		SystemPropertyVO property = new SystemPropertyVO();
		property.setName(name);
		property.setValue(value);
		model = ServiceFacade.instance().setSystemProperty(getLoggedInUser(), property);

		name = request.getParameter("name.1");
		if (!StringUtils.isEmpty(name)) {
			value = request.getParameter("value.1");
			property = new SystemPropertyVO();
			property.setName(name);
			property.setValue(value);
			ServiceFacade.instance().setSystemProperty(getLoggedInUser(), property);
		}

		return headers;
	}

	private HttpHeaders setDatastore(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("set-datastore");
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user != null) {
			String type = request.getParameter("type");
			ServiceFacade.currentDAO = ServiceFacade.Datastore.valueOf(type);
			
			// after changing datastore we need to make sure that user is created in datastore
			UserVO loggedInUser = ServiceFacade.instance().getLoggedInUserData(user);
			if (loggedInUser == null) {
				// first time logged in
				loggedInUser = ServiceFacade.instance().createUser(user);
			}
			
			headers.setRedirectUrl("/setup");
		} else {
			headers.setStatus(500);
		}
		return headers;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
