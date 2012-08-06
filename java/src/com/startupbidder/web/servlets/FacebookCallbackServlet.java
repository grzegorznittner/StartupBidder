package com.startupbidder.web.servlets;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.startupbidder.util.FacebookHelper;
import com.startupbidder.util.FacebookUser;

@SuppressWarnings("serial")
public class FacebookCallbackServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(FacebookCallbackServlet.class.getName());
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
        	String accessUrl = FacebookHelper.getAccessTokenUrl(request);
            URLConnection con = new URL(accessUrl).openConnection();
    		String result = IOUtils.toString(con.getInputStream(), "UTF-8");
    		String mimeType = con.getContentType();
    		log.log(Level.INFO, "Result of call to " + accessUrl + " (mimetype: " + mimeType + "): " + result);
    		
    		String accessToken = null;
            Integer expires = null;
            String[] pairs = result.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length != 2) {
                    throw new RuntimeException("Unexpected Facebook auth response: " + response);
                } else {
                    if (kv[0].equals("access_token")) {
                        accessToken = kv[1];
                    }
                    if (kv[0].equals("expires")) {
                        expires = Integer.valueOf(kv[1]);
                    }
                }
            }
            // getting facebook user data and storing it in session
            FacebookUser facebookUser = FacebookHelper.authorizeUser(request, accessToken, expires);
    		log.info("Logged in with Facebook, user: " + facebookUser);
    		
            String targetUrl = (String)request.getSession().getAttribute(FacebookHelper.SESSION_FACEBOOK_TARGET_URL);
            request.getSession().removeAttribute(FacebookHelper.SESSION_FACEBOOK_TARGET_URL);
            if (StringUtils.isEmpty(targetUrl)) {
            	targetUrl = "/";
            }
            response.sendRedirect(request.getContextPath() + targetUrl);
        } catch (Exception e) {
        	log.log(Level.WARNING, "Facebook login error", e);
            response.sendRedirect(request.getContextPath() + "/login_error.html");
        }
    }
}
