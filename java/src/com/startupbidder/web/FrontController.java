package com.startupbidder.web;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.startupbidder.web.controllers.BidController;
import com.startupbidder.web.controllers.CommentController;
import com.startupbidder.web.controllers.FileController;
import com.startupbidder.web.controllers.ListingController;
import com.startupbidder.web.controllers.SystemController;
import com.startupbidder.web.controllers.TaskController;
import com.startupbidder.web.controllers.UserController;

@SuppressWarnings("serial")
public class FrontController extends HttpServlet {
	private static final Logger log = Logger.getLogger(FrontController.class.getName());
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		//String mappings = config.getInitParameter("mappings");
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		log.log(Level.INFO, "pathInfo=" + pathInfo);
		
		ModelDrivenController controller = null;
		HttpHeaders headers = null;
		if (pathInfo.startsWith("/user")) {
			controller = new UserController();
		} else if (pathInfo.startsWith("/listing")) {
			controller = new ListingController();
		} else if (pathInfo.startsWith("/bid")) {
			controller = new BidController();
		} else if (pathInfo.startsWith("/comment")) {
			controller = new CommentController();
		} else if (pathInfo.startsWith("/system")) {
			controller = new SystemController();
		} else if (pathInfo.startsWith("/file")) {
			controller = new FileController();
		} else if (pathInfo.startsWith("/task")) {
			controller = new TaskController();
		} else {
			log.log(Level.WARNING, "Unknown action '" + pathInfo + "'");
		}
		
		if (controller != null) {
			log.log(Level.INFO, "Created controller: " + controller.getClass().getCanonicalName());
			headers = ((ModelDrivenController)controller).execute(request);
			if (controller.getModel() != null) {
				log.log(Level.INFO, "Returned object: " + controller.getModel().getClass().getCanonicalName());
			} else {
				log.log(Level.SEVERE, "Returned object is NULL");
			}
		} else {
			log.log(Level.INFO, request.getMethod() + " " + request.getPathInfo() + " is not supported!");
			response.setStatus(501);
			return;
		}
		
		if (headers != null) {
			if (headers.apply(request, response, controller.getModel()) != null) {
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
	}
}
