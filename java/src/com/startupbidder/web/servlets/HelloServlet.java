package com.startupbidder.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class HelloServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) {
			resp.setContentType("text/html");
		} else {
			resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
		}
		
		PrintWriter out = resp.getWriter();
		try {
			out.println("<html><body>");
			out.println("<p>Hello, " + user.getNickname() + "</p>");
			
			if (!user.getNickname().contains("grzegorz.nittner")) {
				out.println("<p>Sorry, you're not authorized to view contents!!!</p>");
				return;
			} else {
				out.println("<p>Place content here!</p>");
			}
			
		} finally {
			out.println("</body></html>");
		}
	}

}
