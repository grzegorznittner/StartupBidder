package com.startupbidder.web.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.startupbidder.util.TwitterHelper;

import static com.startupbidder.util.TwitterHelper.*;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

@SuppressWarnings("serial")
public class TwitterLoginServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(TwitterLoginServlet.class.getName());
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
        	TwitterFactory tf = TwitterHelper.configureTwitterFactory();
            Twitter twitter = tf.getInstance();
            request.getSession().setAttribute(SESSION_TWITTER_OBJECT, twitter);
            
            StringBuffer callbackURL = request.getRequestURL();
            int index = callbackURL.lastIndexOf("/");
            callbackURL.replace(index, callbackURL.length(), "").append(SERVLET_TWITTER_CALLBACK);

            log.info("Requesting token for callback url " + callbackURL);
            RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL.toString());
            request.getSession().setAttribute(SESSION_TWITTER_REQUEST_TOKEN, requestToken);
            String authUrl = requestToken.getAuthenticationURL();
            log.info("Sending redirection to " + authUrl);
            response.sendRedirect(authUrl);

        } catch (TwitterException e) {
            throw new ServletException(e);
        }
    }

}
