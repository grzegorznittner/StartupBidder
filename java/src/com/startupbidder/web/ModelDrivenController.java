package com.startupbidder.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.startupbidder.util.FacebookHelper;
import com.startupbidder.util.FacebookUser;
import com.startupbidder.util.TwitterHelper;
import com.startupbidder.vo.BaseResultVO;
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.UserDataUpdatableContainer;
import com.startupbidder.vo.UserVO;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public abstract class ModelDrivenController {
	private static final Logger log = Logger.getLogger(ModelDrivenController.class.getName());
	
	private static int DEFAULT_MAX_RESULTS = 5;
    private static int MAX_RESULTS = 20;
	private String command[];
	private UserVO loggedInUser;
	
	/**
	 * Executes action handler for particular controller
	 * @param request 
	 * @return Http headers and return code
	 */
	abstract protected HttpHeaders executeAction(HttpServletRequest request)
		throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * Returns object which should be trasformed into one of the result types (JSON, HTML)
	 */
	abstract public Object getModel();
	
	public final HttpHeaders execute(HttpServletRequest request) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		FacebookUser fbUser = FacebookHelper.getFacebookUser(request);
		twitter4j.User twitterUser = TwitterHelper.getTwitterUser(request);

		if (user != null) {
			// logged in via Google
			loggedInUser = UserMgmtFacade.instance().getLoggedInUser(user);
			if (loggedInUser == null) {
				// first time logged in
				loggedInUser = UserMgmtFacade.instance().createUser(user);
			}
			if (loggedInUser != null) {
				loggedInUser.setAdmin(userService.isUserAdmin());
			}
		} else if (fbUser != null) {
			// login via Facebook
			log.info("Logged in via Facebook as " + fbUser.getId() + ", email: " + fbUser.getEmail());
			loggedInUser = UserMgmtFacade.instance().getLoggedInUser(fbUser);
			if (loggedInUser == null) {
				log.info("User not found via facebook id " + fbUser.getId() + ", email: " + fbUser.getEmail());
				loggedInUser = UserMgmtFacade.instance().createUser(fbUser);
			}
		} else if (twitterUser != null) {
			// login via Twitter
			log.info("Logged in via Twitter as " + twitterUser.getScreenName());
			loggedInUser = UserMgmtFacade.instance().getLoggedInUser(twitterUser);
			if (loggedInUser == null) {
				log.info("User not found via twitter id " + twitterUser.getId() + ", screen name '" + twitterUser.getScreenName() + "'");
				loggedInUser = UserMgmtFacade.instance().createUser(twitterUser);
			}
		} else {
			// not logged in
			loggedInUser = null;
		}

		if (loggedInUser != null && !StringUtils.isEmpty(loggedInUser.getEditedListing())) {
			// calling this method checks also if listing is in valid state
			ListingFacade.instance().editedListing(loggedInUser);
		}
		if (loggedInUser != null && StringUtils.isNotEmpty(request.getHeader("X-AppEngine-Country"))) {
			Map<String, String> locationHeaders = new HashMap<String, String>();
			locationHeaders.put("X-AppEngine-Country", request.getHeader("X-AppEngine-Country"));
			locationHeaders.put("X-AppEngine-Region", request.getHeader("X-AppEngine-Region"));
			locationHeaders.put("X-AppEngine-City", request.getHeader("X-AppEngine-City"));
			locationHeaders.put("X-AppEngine-CityLatLong", request.getHeader("X-AppEngine-CityLatLong"));
			loggedInUser.setLocationHeaders(locationHeaders);
		}
		
		command = decomposeRequest(request.getPathInfo());
		
		HttpHeaders headers = null;
		try {
			headers = executeAction(request);
			if (headers == null) {
				log.log(Level.INFO, request.getMethod() + " " + getCommand(1) + " is not supported!");
			}
			Object model = getModel();
			if (model instanceof UserDataUpdatableContainer) {
				((UserDataUpdatableContainer)model).updateUserData();
			}
			if (model instanceof BaseResultVO) {
				String appHost = TwitterHelper.getApplicationUrl(request);
				if (loggedInUser != null) {
					((BaseResultVO) model).setLoggedUser(loggedInUser);
					if (user != null) {
						((BaseResultVO) model).setLogoutUrl(userService.createLogoutURL(appHost));
					} else if (fbUser != null) {
						((BaseResultVO) model).setLogoutUrl(FacebookHelper.getLogoutUrl(request));
					} else if (twitterUser != null) {
						loggedInUser.setNickname(twitterUser.getScreenName());
						((BaseResultVO) model).setLogoutUrl(TwitterHelper.getLogoutUrl(request));
					}
				} else {
					((BaseResultVO) model).setLoginUrl(userService.createLoginURL(appHost, "startupbidder.com"));					
					if (FacebookHelper.getFacebookAuthParams() != null) {
						((BaseResultVO) model).setFacebookLoginUrl(FacebookHelper.getLoginUrl(request));
					}
					if (TwitterHelper.configureTwitterFactory() != null) {
						((BaseResultVO) model).setTwitterLoginUrl(TwitterHelper.getLoginUrl(request));
					}
				}
				
				if (((BaseResultVO) model).getErrorCode() != ErrorCodes.OK) {
					headers.setStatus(500);
				}				
			}
		} catch (Exception e) {
			headers = new HttpHeadersImpl();
			headers.setStatus(501);
			log.log(Level.SEVERE, "Error handling request", e);
		}
				
		return headers;
	}

	public void generateJson(HttpServletResponse response) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			response.setContentType("application/json;charset=UTF-8");
			mapper.writeValue(response.getWriter(), getModel());
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateHtml(HttpServletResponse response) {
		PrintWriter writer;
		try {
			response.setContentType("text/html;charset=UTF-8");
			writer = response.getWriter();
			writer.println("<html><head><title>Startupbidder.com</title></head><body>");
			String modelHtml = getModel() != null ? getModel().toString() : "Result is empty.";
			modelHtml.replaceAll("]", "]<br/>");
			writer.println(modelHtml);
			writer.println("</body></html>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns command encoded in the path info.
	 * eg. for request uri: /listings/top.json?maxItems=5&cursor=qerqsdfgsdfgh43t6dsfhg
	 *     available commands are: listings (0), top (1)
	 * 
	 * @param index Command order number
	 * @return String representing command or empty string
	 */
	protected String getCommand(int index) {
		return index < command.length ? command[index] : "";
	}
	
	private String[] decomposeRequest(String path) {
		int dotPos = path.indexOf('.');
		int questionPos = path.indexOf('.');
		// handling -1
		dotPos = dotPos < 0 ? path.length() : dotPos;
		questionPos = questionPos < 0 ? path.length() : questionPos;
		
		path = path.substring(0, dotPos > questionPos ? questionPos : dotPos);
		StringTokenizer tokenizer = new StringTokenizer(path, "/");
		
		List<String> pathElements = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			pathElements.add(tokenizer.nextToken());
		}
		
		log.log(Level.INFO, "Commands: " + pathElements.toString());
		return pathElements.toArray(new String[0]);
	}

	protected ListPropertiesVO getListProperties(HttpServletRequest request) {
		ListPropertiesVO listingProperties = new ListPropertiesVO();
		
		try {
			String maxItemsStr = request.getParameter("max_results");
			int maxItems = maxItemsStr != null ? Integer.parseInt(maxItemsStr) : DEFAULT_MAX_RESULTS;
	        if (maxItems > MAX_RESULTS) { // avoid DoS attacks
	            maxItems = MAX_RESULTS;
	        }
			listingProperties.setMaxResults(maxItems);
		} catch (NumberFormatException e) {
			listingProperties.setMaxResults(DEFAULT_MAX_RESULTS);
		}
		
		try {
			String startIndexStr = request.getParameter("start_index");
			int startIndex = startIndexStr != null ? Integer.parseInt(startIndexStr) : 1;
			listingProperties.setStartIndex(startIndex);
		} catch (NumberFormatException e) {
			listingProperties.setStartIndex(1);
		}
		
		listingProperties.setNextCursor(request.getParameter("next_cursor"));
		listingProperties.setRequestData(request);
		
		return listingProperties;
	}

	protected String getCommandOrParameter(HttpServletRequest request, int commandNum, String parameter) {
		if ("".equals(getCommand(commandNum))) {
			return request.getParameter(parameter);
		} else {
			return getCommand(commandNum);
		}
	}
	
	protected UserVO getLoggedInUser() {
		return loggedInUser;
	}
}
