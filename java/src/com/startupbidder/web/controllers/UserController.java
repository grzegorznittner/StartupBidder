package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.startupbidder.util.TwitterHelper;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.PrivateMessageListVO;
import com.startupbidder.vo.PrivateMessageUserListVO;
import com.startupbidder.vo.PrivateMessageVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.MessageFacade;
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
			} else if("message_users".equalsIgnoreCase(getCommand(1))) {
				return messageUsers(request);
			} else if("messages".equalsIgnoreCase(getCommand(1))) {
				return messages(request);
			} else if("loggedin".equalsIgnoreCase(getCommand(1))) {
				return loggedin(request);
			} else if("check_user_name".equalsIgnoreCase(getCommand(1))) {
				return checkUserName(request);
			} else if("confirm_update_email".equalsIgnoreCase(getCommand(1))) {
				return confirmEmailUpdate(request);
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
			} else if("send_message".equalsIgnoreCase(getCommand(1))) {
				return sendMessage(request);
			} else if("request_update_email".equalsIgnoreCase(getCommand(1))) {
				return requestEmailUpdate(request);
			}
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
			log.log(Level.INFO, "Autosaving user: " + user);
			String name = (String)user.get("name");
			String nickname = (String)user.get("nickname");
			String phone = (String)user.get("phone");
			String location = (String)user.get("location");
			Boolean investor = BooleanUtils.toBooleanObject((String)user.get("investor"));
			Boolean notifyEnabled = BooleanUtils.toBooleanObject((String)user.get("notify_enabled"));
			model = UserMgmtFacade.instance().updateUser(getLoggedInUser(),
					name, nickname, location, phone, investor, notifyEnabled);
			if (model == null) {
				log.log(Level.WARNING, "User autosave error!");
				headers.setStatus(500);
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

	private HttpHeaders checkUserName(HttpServletRequest request) {
    	String userName = getCommandOrParameter(request, 2, "name");
    	model = UserMgmtFacade.instance().checkUserNameIsValid(getLoggedInUser(), userName);

    	HttpHeaders headers = new HttpHeadersImpl("check_user_name");
		return headers;
	}

    // POST /user/send_message
    private HttpHeaders sendMessage(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("send_message");

		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String messageString = request.getParameter("message");
		if (!StringUtils.isEmpty(messageString)) {
			JsonNode rootNode = mapper.readValue(messageString, JsonNode.class);
			String userId = null;
			if (rootNode.get("profile_id") != null) {
				userId = rootNode.get("profile_id").getValueAsText();
			}
			String text = null;
			if (rootNode.get("text") != null) {
				text = rootNode.get("text").getValueAsText();
			}
			PrivateMessageVO message = MessageFacade.instance().sendPrivateMessage(getLoggedInUser(), userId, text);
			model = message;
		} else {
			log.severe("Missing message parameter!");
			headers.setStatus(500);
		}
		return headers;
    }

	/*
	 *  /user/message_users/
	 */
	private HttpHeaders messageUsers(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("message_users");
		
		ListPropertiesVO listProperties = getListProperties(request);
		PrivateMessageUserListVO result = MessageFacade.instance().getPrivateMessageUsers(getLoggedInUser(), listProperties);
		model = result;
		
		return headers;
	}
	
	/*
	 *  /user/messages/?id=ag1zdGFydHVwYmlkZGVychQLEgdMaXN0aW5nIgdtaXNsZWFkDA.html
	 */
	private HttpHeaders messages(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("messages");
		
		ListPropertiesVO listProperties = getListProperties(request);
		String userId = getCommandOrParameter(request, 2, "id");
		PrivateMessageListVO result = MessageFacade.instance().getPrivateMessages(getLoggedInUser(), userId, listProperties);
		model = result;
		return headers;
	}

	private HttpHeaders requestEmailUpdate(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("request_update_email");
		
		String email = getCommandOrParameter(request, 2, "email");
		twitter4j.User twitterUser = TwitterHelper.getTwitterUser(request);
		if (twitterUser != null) {
			model = UserMgmtFacade.instance().requestEmailUpdate(twitterUser, email);
		} else {
			log.warning("User not logged in via Twitter while trying to update email: " + email);
		}
		
		return headers;
	}

	private HttpHeaders confirmEmailUpdate(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("confirm_update_email");
		
		String id = getCommandOrParameter(request, 2, "id");
		String token = getCommandOrParameter(request, 3, "token");
		model = UserMgmtFacade.instance().confirmEmailUpdate(id, token);
		
		return headers;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
