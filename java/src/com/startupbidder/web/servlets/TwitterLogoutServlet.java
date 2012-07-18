package com.startupbidder.web.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.startupbidder.util.TwitterHelper.*;

@SuppressWarnings("serial")
public class TwitterLogoutServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(TwitterLogoutServlet.class.getName());
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	log.info("Twitter logout - removing twitter related session objects");
        request.getSession().removeAttribute(SESSION_TWITTER_OBJECT);
        request.getSession().removeAttribute(SESSION_TWITTER_USER);
        response.sendRedirect(request.getContextPath()+ "/");
    }

}
