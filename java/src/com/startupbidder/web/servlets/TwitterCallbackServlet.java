package com.startupbidder.web.servlets;

import static com.startupbidder.util.TwitterHelper.PARAM_TWITTER_OAUTH_VERIFIER;
import static com.startupbidder.util.TwitterHelper.SESSION_TWITTER_OBJECT;
import static com.startupbidder.util.TwitterHelper.SESSION_TWITTER_REQUEST_TOKEN;
import static com.startupbidder.util.TwitterHelper.SESSION_TWITTER_USER;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.startupbidder.util.TwitterHelper;

import twitter4j.Twitter;
import twitter4j.User;
import twitter4j.auth.RequestToken;

@SuppressWarnings("serial")
public class TwitterCallbackServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(TwitterCallbackServlet.class.getName());
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Twitter twitter = (Twitter) request.getSession().getAttribute(SESSION_TWITTER_OBJECT);
        RequestToken requestToken = (RequestToken) request.getSession().getAttribute(SESSION_TWITTER_REQUEST_TOKEN);
        String verifier = request.getParameter(PARAM_TWITTER_OAUTH_VERIFIER);
        try {
            twitter.getOAuthAccessToken(requestToken, verifier);
            User twitterUser = twitter.showUser(twitter.getId());
            log.info("Logged in with Twitter, user: " + twitterUser);
            request.getSession().setAttribute(SESSION_TWITTER_USER, twitterUser);
            
            request.getSession().removeAttribute(SESSION_TWITTER_REQUEST_TOKEN);
        } catch (Exception e) {
            log.log(Level.WARNING, "Twitter login error", e);
            response.sendRedirect(request.getContextPath() + "/login_error.html");
        }
        String targetUrl = (String)request.getSession().getAttribute(TwitterHelper.SESSION_TWITTER_TARGET_URL);
        request.getSession().removeAttribute(TwitterHelper.SESSION_TWITTER_TARGET_URL);
        if (StringUtils.isEmpty(targetUrl)) {
        	targetUrl = "/";
        }
        response.sendRedirect(request.getContextPath() + targetUrl);
    }

}
