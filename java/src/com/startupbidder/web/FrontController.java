package com.startupbidder.web;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.ObjectifyService;
import com.startupbidder.dao.AngelListCache;
import com.startupbidder.dao.GeocodeLocation;
import com.startupbidder.dao.StartuplyCache;
import com.startupbidder.datamodel.OldBid;
import com.startupbidder.datamodel.Category;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.ListingLocation;
import com.startupbidder.datamodel.ListingStats;
import com.startupbidder.datamodel.Location;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.OldPaidBid;
import com.startupbidder.datamodel.PrivateMessage;
import com.startupbidder.datamodel.PrivateMessageUser;
import com.startupbidder.datamodel.QuestionAnswer;
import com.startupbidder.datamodel.Rank;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.SystemProperty;
import com.startupbidder.datamodel.UserStats;
import com.startupbidder.datamodel.Vote;
import com.startupbidder.web.controllers.OldBidController;
import com.startupbidder.web.controllers.CommentController;
import com.startupbidder.web.controllers.CronTaskController;
import com.startupbidder.web.controllers.FileController;
import com.startupbidder.web.controllers.ListingController;
import com.startupbidder.web.controllers.MonitorController;
import com.startupbidder.web.controllers.NotificationController;
import com.startupbidder.web.controllers.SystemController;
import com.startupbidder.web.controllers.TaskController;
import com.startupbidder.web.controllers.UserController;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
public class FrontController extends HttpServlet {
	private static final Logger log = Logger.getLogger(FrontController.class.getName());
	
	static {
		ObjectifyService.register(SBUser.class);
		ObjectifyService.register(Listing.class);
		ObjectifyService.register(UserStats.class);
		ObjectifyService.register(OldBid.class);
		ObjectifyService.register(Comment.class);
		ObjectifyService.register(ListingDoc.class);
		ObjectifyService.register(ListingStats.class);
		ObjectifyService.register(Monitor.class);
		ObjectifyService.register(Notification.class);
		ObjectifyService.register(QuestionAnswer.class);
		ObjectifyService.register(PrivateMessage.class);
		ObjectifyService.register(PrivateMessageUser.class);
		ObjectifyService.register(OldPaidBid.class);
		ObjectifyService.register(Rank.class);
		ObjectifyService.register(SystemProperty.class);
		ObjectifyService.register(Vote.class);
		ObjectifyService.register(Category.class);
		ObjectifyService.register(Location.class);
		ObjectifyService.register(ListingLocation.class);
        ObjectifyService.register(AngelListCache.class);
        ObjectifyService.register(GeocodeLocation.class);
        ObjectifyService.register(StartuplyCache.class);
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		//String mappings = config.getInitParameter("mappings");
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		//log.log(Level.INFO, "pathInfo=" + pathInfo);
		
		ModelDrivenController controller = null;
		HttpHeaders headers = null;
		if (pathInfo.startsWith("/user")) {
			controller = new UserController();
		} else if (pathInfo.startsWith("/listing")) {
			controller = new ListingController();
		} else if (pathInfo.startsWith("/comment")) {
			controller = new CommentController();
		} else if (pathInfo.startsWith("/system")) {
			controller = new SystemController();
		} else if (pathInfo.startsWith("/file")) {
			controller = new FileController();
		} else if (pathInfo.startsWith("/task")) {
			controller = new TaskController();
		} else if (pathInfo.startsWith("/notification")) {
			controller = new NotificationController();
		} else if (pathInfo.startsWith("/monitor")) {
			controller = new MonitorController();
		} else if (pathInfo.startsWith("/cron")) {
			controller = new CronTaskController();
		} else {
			log.log(Level.WARNING, "Unknown action '" + pathInfo + "'");
		}
		
		if (controller != null) {
			//log.log(Level.INFO, "Created controller: " + controller.getClass().getCanonicalName());
			headers = ((ModelDrivenController)controller).execute(request);
			if (controller.getModel() != null) {
				//log.log(Level.INFO, "Returned object class: " + controller.getModel().getClass().getCanonicalName());
				//log.log(Level.INFO, "Returned object: " + controller.getModel().toString());
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
					controller.generateHtml(response);
				} else {
					// default is JSON
					response.setContentType("application/json");
					controller.generateJson(response);
				}
			}
		}		
	}
}
