package com.startupbidder.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.rest.HttpHeaders;

import com.startupbidder.web.controllers.ListingController;

@SuppressWarnings("serial")
public class FrontController extends HttpServlet {
	
	private ServiceFacade service = new ServiceFacade();

	@Override
	public void init(ServletConfig config) throws ServletException {
		//String mappings = config.getInitParameter("mappings");
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String query = request.getQueryString();
		String pathInfo = request.getPathInfo();
		
		ModelDrivenController controller = null;
		HttpHeaders headers = null;
		if (pathInfo.startsWith("search")) {
			
		} else if (pathInfo.contains("listing")) {
			controller = new ListingController();
			headers = ((ListingController)controller).execute(request);
		} else {
			
		}
		
		headers.apply(request, response, controller.getModel());
		
		if (request.getRequestURI().endsWith(".html")) {
			// default is plain/text
			controller.generateJson(response);
		} else {
			// default is JSON
			response.setContentType("application/json");
			controller.generateJson(response);
		}
		
	}
	
}
