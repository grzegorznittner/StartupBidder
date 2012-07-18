package com.startupbidder.web.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.RequestToken;

import static com.startupbidder.util.TwitterHelper.*;

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
        } catch (TwitterException e) {
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/");
    }

}
