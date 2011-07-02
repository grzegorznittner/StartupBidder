package com.startupbidder.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.rest.HttpHeaders;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;

public abstract class ModelDrivenController {
	private String command[];
	
	abstract protected HttpHeaders executeAction(HttpServletRequest request);
	
	public HttpHeaders execute(HttpServletRequest request) {
		command = decomposeRequest(request.getPathInfo());
		
		return executeAction(request);
	}
	
	abstract public Object getModel();
	
	@SuppressWarnings("rawtypes")
	public void generateJson(HttpServletResponse response) {
		Object model = getModel();
		JsonNode rootNode = null;
		if (model instanceof List) {
			rootNode = JsonNodeFactory.instance.arrayNode();
	    	for (Object item : (List)model) {
				((ArrayNode)rootNode).addPOJO(item);
			}
		} else {
			
		}
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(response.getWriter(), rootNode);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected String getCommand(int index) {
		return index < command.length ? command[index] : "";
	}
	
	private String[] decomposeRequest(String path) {
		List<String> pathElements = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(path, "/");
		
		while (tokenizer.hasMoreTokens()) {
			pathElements.add(tokenizer.nextToken());
		}
		
		return pathElements.toArray(new String[0]);
	}

}
