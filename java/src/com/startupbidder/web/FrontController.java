package com.startupbidder.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.startupbidder.web.controllers.CommentController;
import com.startupbidder.web.controllers.CronTaskController;
import com.startupbidder.web.controllers.FileController;
import com.startupbidder.web.controllers.ListingController;
import com.startupbidder.web.controllers.MonitorController;
import com.startupbidder.web.controllers.NotificationController;
import com.startupbidder.web.controllers.SystemController;
import com.startupbidder.web.controllers.TaskController;
import com.startupbidder.web.controllers.UserController;
import com.startupbidder.web.servlets.WarmupListener;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
public class FrontController extends HttpServlet {
	private static final Logger log = Logger.getLogger(FrontController.class.getName());
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if ("GET".equals(request.getMethod()) && !"true".equalsIgnoreCase(request.getHeader("X-AppEngine-Cron"))
				&& "startupbidder.appspot.com".equals(request.getServerName())) {
			String redirectUrl = request.getScheme() + "://www.startupbidder.com" + request.getServletPath();
			String queryString = request.getQueryString();
			if (StringUtils.isNotEmpty(queryString)) {
				redirectUrl += "?" + queryString;
			}
			log.info("Got request to startupbidder.appspot.com, redirecting to: " + redirectUrl);
			response.sendRedirect(redirectUrl);
			return;
		}
		
		if (StringUtils.endsWith(pathInfo, ".css")) {
			response.setContentType("text/css");
			IOUtils.copy(new FileInputStream(WarmupListener.MAIN_CSS_FILE), response.getOutputStream());
			return;
		} else if (StringUtils.endsWith(pathInfo, ".js")) {
			response.setContentType("text/javascript");
			if (StringUtils.countMatches(pathInfo, "/") == 2) {
				IOUtils.copy(new FileInputStream(WarmupListener.MAIN_JS_FILE), response.getOutputStream());
				return;
			} else {
				String parts[] = pathInfo.split("/");
				String jsName = parts[parts.length - 1];
				IOUtils.copy(new FileInputStream(WarmupListener.JS_FOLDER + "/" + jsName), response.getOutputStream());
				return;
			}
		}
		
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
			headers = ((ModelDrivenController)controller).execute(request);
			if (controller.getModel() != null) {
			} else {
				log.log(Level.WARNING, "Returned object is NULL");
			}
		} else {
			log.log(Level.WARNING, request.getMethod() + " " + request.getPathInfo() + " is not supported!  Redirecting to error page.");
			response.sendRedirect("/error-page.html");
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
