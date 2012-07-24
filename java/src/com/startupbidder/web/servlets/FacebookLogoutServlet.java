package com.startupbidder.web.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.startupbidder.util.FacebookHelper;

@SuppressWarnings("serial")
public class FacebookLogoutServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(FacebookLogoutServlet.class.getName());
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	log.info("Facebook logout - removing facebook related session objects");
        FacebookHelper.logoutUser(request);
        response.sendRedirect(request.getContextPath()+ "/");
    }
}
