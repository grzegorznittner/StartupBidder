package com.startupbidder.web.servlets;

import static com.startupbidder.util.TwitterHelper.SESSION_TWITTER_OBJECT;
import static com.startupbidder.util.TwitterHelper.SESSION_TWITTER_REQUEST_TOKEN;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

import com.startupbidder.util.TwitterHelper;

@SuppressWarnings("serial")
public class TwitterLoginServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(TwitterLoginServlet.class.getName());
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
        	String targetUrl = request.getParameter("url");
        	if (!StringUtils.isEmpty(targetUrl)) {
        		request.getSession().setAttribute(TwitterHelper.SESSION_TWITTER_TARGET_URL, targetUrl);
        	} else {
        		request.getSession().setAttribute(TwitterHelper.SESSION_TWITTER_TARGET_URL, "/");
        	}

        	TwitterFactory tf = TwitterHelper.configureTwitterFactory();
            Twitter twitter = tf.getInstance();
            request.getSession().setAttribute(SESSION_TWITTER_OBJECT, twitter);
            
            String callbackURL = TwitterHelper.getCallbackUrl(request);

            log.info("Requesting token for callback url " + callbackURL);
            RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL);
            request.getSession().setAttribute(SESSION_TWITTER_REQUEST_TOKEN, requestToken);
            String authUrl = requestToken.getAuthenticationURL();
            log.info("Sending redirection to " + authUrl);
            response.sendRedirect(authUrl);

        } catch (TwitterException e) {
            throw new ServletException(e);
        }
    }

}
