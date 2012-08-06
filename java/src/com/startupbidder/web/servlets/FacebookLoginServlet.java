package com.startupbidder.web.servlets;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.startupbidder.util.FacebookHelper;

@SuppressWarnings("serial")
public class FacebookLoginServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(FacebookLoginServlet.class.getName());
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
        	String targetUrl = request.getParameter("url");
        	if (!StringUtils.isEmpty(targetUrl)) {
        		request.getSession().setAttribute(FacebookHelper.SESSION_FACEBOOK_TARGET_URL, targetUrl);
        	} else {
        		request.getSession().setAttribute(FacebookHelper.SESSION_FACEBOOK_TARGET_URL, "/");
        	}
        	String authorizeUrl = FacebookHelper.getAuthorizeUrl(request);
            log.info("Sending redirection to " + authorizeUrl);
            response.sendRedirect(authorizeUrl);
        } catch (Exception e) {
        	log.log(Level.WARNING, "Facebook login error", e);
            response.sendRedirect(request.getContextPath() + "/login_error.html");
        }
    }
}
