package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.util.AuthenticationException;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.SystemProperty;
import com.startupbidder.vo.SystemPropertyVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;
import com.startupbidder.web.UserMgmtFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class SystemController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(SystemController.class.getName());
	
	private Object model;

	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			if("set-property".equalsIgnoreCase(getCommand(1))) {
				return setProperty(request);
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

		String name1 = request.getParameter("name.1");
		if (!StringUtils.isEmpty(name)) {
			String value1 = request.getParameter("value.1");
			property = new SystemPropertyVO();
			property.setName(name1);
			property.setValue(value1);
			ServiceFacade.instance().setSystemProperty(getLoggedInUser(), property);
			
			if (SystemProperty.GOOGLEDOC_USER.equals(name) && SystemProperty.GOOGLEDOC_PASSWORD.equals(name1)) {
				DocsService client = new DocsService("www-startupbidder-v1");
				try {
					client.setUserCredentials(value, value1);
					model = "Google Doc user/password verified!";
				} catch (AuthenticationException e) {
					model = "Google Doc user/password verification error!";
					log.log(Level.SEVERE, "Error while logging to GoogleDoc!", e);
				}
			}
		}

		return headers;
	}

	private HttpHeaders clearDatastore(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("clear-datastore");
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user != null) {
			UserVO loggedInUser = UserMgmtFacade.instance().getLoggedInUserData(user);
			String deletedObjects = ServiceFacade.instance().clearDatastore(loggedInUser);
			model = deletedObjects;
		} else {
			headers.setStatus(500);
		}
		return headers;
	}
	
	private HttpHeaders createMockDatastore(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("create-mock-datastore");
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user != null) {
			UserVO loggedInUser = UserMgmtFacade.instance().getLoggedInUserData(user);
			model = ObjectifyDatastoreDAO.getInstance().createMockDatastore(loggedInUser != null ? loggedInUser.toKeyId() : 0);
		} else {
			headers.setStatus(500);
		}
		return headers;
	}

	private HttpHeaders printDatastoreContents(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("print-datastore");
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		UserVO loggedInUser = UserMgmtFacade.instance().getLoggedInUserData(user);
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			String printedObjects = ServiceFacade.instance().printDatastoreContents(loggedInUser);
			model = printedObjects;
		} else {
			headers.setStatus(500);
		}
		return headers;
	}

	private HttpHeaders exportDatastore(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("export-datastore");
		
		UserService userService = UserServiceFactory.getUserService();
		UserVO loggedInUser = UserMgmtFacade.instance().getLoggedInUserData(userService.getCurrentUser());
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
