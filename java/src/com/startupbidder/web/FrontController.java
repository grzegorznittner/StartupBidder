package com.startupbidder.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.rest.HttpHeaders;

import com.startupbidder.web.controllers.ListingController;
import com.startupbidder.web.controllers.UserController;

@SuppressWarnings("serial")
public class FrontController extends HttpServlet {
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		//String mappings = config.getInitParameter("mappings");
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		
		ModelDrivenController controller = null;
		HttpHeaders headers = null;
		if (pathInfo.startsWith("user")) {
			controller = new UserController();
			headers = ((UserController)controller).execute(request);
		} else if (pathInfo.contains("listing")) {
			controller = new ListingController();
			headers = ((ListingController)controller).execute(request);
		} else if (pathInfo.contains("bid")) {
			controller = new ListingController();
			headers = ((ListingController)controller).execute(request);
		} else if (pathInfo.contains("comment")) {
			controller = new ListingController();
			headers = ((ListingController)controller).execute(request);
		}  else {
			
		}
		
		if (headers != null) {
			headers.apply(request, response, controller.getModel());
		}
		
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
