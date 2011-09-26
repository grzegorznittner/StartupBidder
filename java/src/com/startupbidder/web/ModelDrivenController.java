package com.startupbidder.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.startupbidder.vo.BaseResultVO;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.UserVO;

public abstract class ModelDrivenController {
	private static final Logger log = Logger.getLogger(ModelDrivenController.class.getName());
	
	private static int DEFAULT_MAX_RESULTS = 5;
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
		if (user != null) {
			loggedInUser = ServiceFacade.instance().getLoggedInUserData(user);
			if (loggedInUser == null) {
				// first time logged in
				loggedInUser = ServiceFacade.instance().createUser(user);
			}
		} else {
			// not logged in
			loggedInUser = null;
		}
		
		command = decomposeRequest(request.getPathInfo());
		
		HttpHeaders headers = null;
		try {
			headers = executeAction(request);
			if (headers == null) {
				log.log(Level.INFO, request.getMethod() + " " + getCommand(1) + " is not supported!");
			}
		} catch (Exception e) {
			headers = new HttpHeadersImpl();
			headers.setStatus(501);
			log.log(Level.SEVERE, "Error handling request", e);
		}
		
		Object model = getModel();
		if (model instanceof BaseResultVO) {
			String appHost = SystemProperty.environment.value() == SystemProperty.Environment.Value.Development ?
					"http://localhost:" + request.getLocalPort() : "http://www.startupbidder.com";
			if (loggedInUser != null) {
				((BaseResultVO) model).setLoggedUser(loggedInUser);
				((BaseResultVO) model).setLogoutUrl(userService.createLogoutURL(appHost));
			} else {
				((BaseResultVO) model).setLoginUrl(userService.createLoginURL(appHost, "startupbidder.com"));
			}
		}
		
		return headers;
	}

	public void generateJson(HttpServletResponse response) {
		ObjectMapper mapper = new ObjectMapper();
		try {
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
		
		String maxItemsStr = request.getParameter("max_results");
		
		int maxItems = maxItemsStr != null ? Integer.parseInt(maxItemsStr) : DEFAULT_MAX_RESULTS;
		listingProperties.setMaxResults(maxItems);
		listingProperties.setNextCursor(request.getParameter("next_cursor"));
		listingProperties.setPrevCursor(request.getParameter("prev_cursor"));
		
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
