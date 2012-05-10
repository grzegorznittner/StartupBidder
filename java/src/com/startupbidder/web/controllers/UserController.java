package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.startupbidder.vo.UserVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.UserMgmtFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
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
			/* } else if("votes".equalsIgnoreCase(getCommand(1))) {
				return votes(request); */
			} else if("check-user-name".equalsIgnoreCase(getCommand(1))) {
				return checkUserName(request);
			} else {
				return index(request);
			}
		} else if ("POST".equalsIgnoreCase(request.getMethod())) {
			if ("update".equalsIgnoreCase(getCommand(1))) {
				return update(request);
			} else if("autosave".equalsIgnoreCase(getCommand(1))) {
				return autoSave(request);
			} else if("activate".equalsIgnoreCase(getCommand(1))) {
				return activate(request);
			} else if("deactivate".equalsIgnoreCase(getCommand(1))) {
				return deactivate(request);
			} else if ("create".equalsIgnoreCase(getCommand(1))) {
				return create(request);
			} else if ("delete".equalsIgnoreCase(getCommand(1))) {
				return delete(request);
			} /* else if ("up".equalsIgnoreCase(getCommand(1))) {
				return up(request);
			} */
		}
		return null;
	}
	
	private HttpHeaders delete(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("delete");
		log.log(Level.WARNING, "Deleting user is not supported! User can be only deactivated.");
		headers.setStatus(501);
		return headers;
	}

	private HttpHeaders create(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("create");
		
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String listingString = request.getParameter("profile");
		if (!StringUtils.isEmpty(listingString)) {
			ObjectMapper mapper = new ObjectMapper();
			UserVO user = mapper.readValue(listingString, UserVO.class);
			user.setId(null);
			//model = ServiceFacade.instance().createUser(getLoggedInUser(), user);
			log.log(Level.WARNING, "User creation is not supported! You need to login using external account.");
			headers.setStatus(501);
		} else {
			log.log(Level.WARNING, "Parameter 'profile' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}

	private HttpHeaders update(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("update");
		
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String profileString = request.getParameter("profile");
		if (!StringUtils.isEmpty(profileString)) {
			ObjectMapper mapper = new ObjectMapper();
			UserVO user = mapper.readValue(profileString, UserVO.class);
			if (user.getId() == null) {
				log.log(Level.WARNING, "User update called but user id not provided.");
				headers.setStatus(501);
			} else {
				log.log(Level.INFO, "Updating user: " + user);
				model = UserMgmtFacade.instance().updateUser(getLoggedInUser(), 
						user.getName(), user.getNickname(), user.getLocation(),
						user.getPhone(), user.isAccreditedInvestor(), user.isNotifyEnabled());
				if (model == null) {
					log.log(Level.WARNING, "User update error!");
					headers.setStatus(500);
				}
			}
		} else {
			log.log(Level.WARNING, "Parameter 'profile' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}

	private HttpHeaders autoSave(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("update");
		
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String profileString = request.getParameter("profile");
		if (!StringUtils.isEmpty(profileString)) {
			ObjectMapper mapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> user = mapper.readValue(profileString, Map.class);
			if (!user.containsKey("profile_id")) {
				log.log(Level.WARNING, "User autosave called but user id not provided.");
				headers.setStatus(501);
			} else {
				log.log(Level.INFO, "Autosaving user: " + user);
				String id = (String)user.get("profile_id");
				if (!StringUtils.equals(id, getLoggedInUser().getId())) {
					log.log(Level.WARNING, "User is not updating his own profile.");
				} else {
					String name = (String)user.get("name");
					String nickname = (String)user.get("nickname");
					String phone = (String)user.get("phone");
					String location = (String)user.get("location");
					Boolean investor = BooleanUtils.toBooleanObject((String)user.get("investor"));
					Boolean notifyEnabled = BooleanUtils.toBooleanObject((String)user.get("notify_enabled"));
					model = UserMgmtFacade.instance().updateUser(getLoggedInUser(), 
							name, nickname, location, phone, investor, notifyEnabled);
				}
				if (model == null) {
					log.log(Level.WARNING, "User autosave error!");
					headers.setStatus(500);
				}
			}
		} else {
			log.log(Level.WARNING, "Parameter 'profile' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}

	private HttpHeaders all(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("all");
		model = UserMgmtFacade.instance().getAllUsers(getLoggedInUser());
		return headers;
	}

	private HttpHeaders topInvestor(HttpServletRequest request) {
		throw new java.lang.RuntimeException("User statistics are not implemented so top investor not available");

//		model = UserMgmtFacade.instance().getTopInvestor(getLoggedInUser());
//        return new HttpHeadersImpl("index").disableCaching();
	}

	private HttpHeaders loggedin(HttpServletRequest request) {
		model = getLoggedInUser();
        return new HttpHeadersImpl("loggedin").disableCaching();
	}

	private HttpHeaders get(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
    	model = UserMgmtFacade.instance().getUser(getLoggedInUser(), userId);
        return new HttpHeadersImpl("get").disableCaching();
	}

	private HttpHeaders index(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 1, "id");
    	model = UserMgmtFacade.instance().getUser(getLoggedInUser(), userId);
        return new HttpHeadersImpl("index").disableCaching();
	}

	private HttpHeaders activate(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
    	if (userId == null) {
    		userId = getLoggedInUser().getId();
    	}
    	String activationCode = getCommandOrParameter(request, 3, "code");
        model = UserMgmtFacade.instance().activateUser(userId, activationCode);

    	HttpHeaders headers = new HttpHeadersImpl("activate");
		return headers;
	}

	private HttpHeaders deactivate(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
    	if (userId == null) {
    		userId = getLoggedInUser().getId();
    	}
    	model = UserMgmtFacade.instance().deactivateUser(getLoggedInUser(), userId);

		HttpHeaders headers = new HttpHeadersImpl("deactivate");
		return headers;
	}

    /*
	private HttpHeaders votes(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
    	if (userId == null) {
    		userId = getLoggedInUser().getId();
    	}
    	model = UserMgmtFacade.instance().userVotes(getLoggedInUser(), userId);

		HttpHeaders headers = new HttpHeadersImpl("deactivate");
		return headers;
	}
    */

	private HttpHeaders checkUserName(HttpServletRequest request) {
    	String userName = getCommandOrParameter(request, 2, "name");
    	model = UserMgmtFacade.instance().checkUserNameIsValid(getLoggedInUser(), userName);

    	HttpHeaders headers = new HttpHeadersImpl("check-user-name");
		return headers;
	}

    /*
	private HttpHeaders up(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
    	if (userId == null) {
    		userId = getLoggedInUser().getId();
    	}
    	model = UserMgmtFacade.instance().valueUpUser(getLoggedInUser(), userId);

    	HttpHeaders headers = new HttpHeadersImpl("up");
		return headers;
	}
    */

	@Override
	public Object getModel() {
		return model;
	}

}
