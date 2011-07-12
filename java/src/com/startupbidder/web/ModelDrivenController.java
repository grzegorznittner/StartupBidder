package com.startupbidder.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.rest.HttpHeaders;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.startupbidder.vo.ListPropertiesVO;

public abstract class ModelDrivenController {
	private static final Logger log = Logger.getLogger(ModelDrivenController.class.getName());
	
	private static int DEFAULT_MAX_RESULTS = 5;
	private String command[];
	
	/**
	 * Executes action handler for particular controller
	 * @param request 
	 * @return Http headers and return code
	 */
	abstract protected HttpHeaders executeAction(HttpServletRequest request);
	
	public HttpHeaders execute(HttpServletRequest request) {
		command = decomposeRequest(request.getPathInfo());
		
		return executeAction(request);
	}

	/**
	 * Returns object which should be trasformed into one of the result types (JSON, HTML)
	 */
	abstract public Object getModel();
	
	@SuppressWarnings("rawtypes")
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
}
