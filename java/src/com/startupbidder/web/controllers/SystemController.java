package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.SystemPropertyVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class SystemController extends ModelDrivenController {
	private Object model;

	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			if("set-property".equalsIgnoreCase(getCommand(1))) {
				return setProperty(request);
			} else if("set-datastore".equalsIgnoreCase(getCommand(1))) {
				return setDatastore(request);
			} else if("clear-datastore".equalsIgnoreCase(getCommand(1))) {
				return clearDatastore(request);
			} else if("print-datastore".equalsIgnoreCase(getCommand(1))) {
				return printDatastoreContents(request);
			} else if("create-mock-datastore".equalsIgnoreCase(getCommand(1))) {
				return createMockDatastore(request);
			} else if("export-datastore".equalsIgnoreCase(getCommand(1))) {
				return exportDatastore(request);
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

	private HttpHeaders clearDatastore(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("clear-datastore");
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user != null) {
			UserVO loggedInUser = ServiceFacade.instance().getLoggedInUserData(user);
			String deletedObjects = ServiceFacade.instance().clearDatastore(loggedInUser);
			model = deletedObjects;
		} else {
			headers.setStatus(500);
		}
		return headers;
	}

	private HttpHeaders printDatastoreContents(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("print-datastore");
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		UserVO loggedInUser = ServiceFacade.instance().getLoggedInUserData(user);
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			String printedObjects = ServiceFacade.instance().printDatastoreContents(loggedInUser);
			model = printedObjects;
		} else {
			headers.setStatus(500);
		}
		return headers;
	}

	private HttpHeaders createMockDatastore(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("create-mock-datastore");
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		UserVO loggedInUser = ServiceFacade.instance().getLoggedInUserData(user);
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			String printedObjects = ServiceFacade.instance().createMockDatastore(loggedInUser);
			model = printedObjects;
		} else {
			headers.setStatus(500);
		}
		return headers;
	}

	private HttpHeaders exportDatastore(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("export-datastore");
		
		UserService userService = UserServiceFactory.getUserService();
		UserVO loggedInUser = ServiceFacade.instance().getLoggedInUserData(userService.getCurrentUser());
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			model = ServiceFacade.instance().exportDatastoreContents(loggedInUser);
		
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd_HHmm_ss");
			headers.addHeader("Content-Disposition", "attachment; filename=export" + fmt.print(new Date().getTime()) + ".json");
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
