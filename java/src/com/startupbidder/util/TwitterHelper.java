package com.startupbidder.util;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.SystemProperty;

public class TwitterHelper {
	public static final String CACHE_TWITTER_FACTORY_PARAMS = "twitter_factory_params";
	
	public static final String SERVLET_TWITTER_CALLBACK = "/twitter_callback";
	
	public static final String SESSION_TWITTER_REQUEST_TOKEN = "twitter_requestToken";
	public static final String SESSION_TWITTER_OBJECT = "twitter_object";
	public static final String SESSION_TWITTER_USER = "twitter_user";
	
	public static final String PARAM_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	
	@SuppressWarnings("serial")
	public static class TwitterParams implements Serializable {
		TwitterParams(SystemProperty cKey, SystemProperty cSecret, SystemProperty aToken, SystemProperty aSecret) {
			consumerKey = cKey.value;
			consumerSecret = cSecret.value;
			accessToken = aToken.value;
			accessTokenSecret = aSecret.value;
		}
		TwitterParams(SystemProperty cKey, SystemProperty cSecret) {
			consumerKey = cKey.value;
			consumerSecret = cSecret.value;
		}
		
		String consumerKey, consumerSecret, accessToken, accessTokenSecret;
	}
	
	public static User getTwitterUser(HttpServletRequest request) {
		return (User)request.getSession().getAttribute(TwitterHelper.SESSION_TWITTER_USER);
	}
	
	public static Twitter getTwitterObject(HttpServletRequest request) {
		return (Twitter)request.getSession().getAttribute(TwitterHelper.SESSION_TWITTER_OBJECT);
	}
	
	public static TwitterFactory configureTwitterFactory() {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		
		TwitterParams params = (TwitterParams)mem.get(CACHE_TWITTER_FACTORY_PARAMS);
		if (params == null) {
			SystemProperty consumerKey = ObjectifyDatastoreDAO.getInstance().getSystemProperty(SystemProperty.TWITTER_CONSUMER_KEY);
			SystemProperty consumerSecret = ObjectifyDatastoreDAO.getInstance().getSystemProperty(SystemProperty.TWITTER_CONSUMER_SECRET);
//			SystemProperty accessToken = ObjectifyDatastoreDAO.getInstance().getSystemProperty(SystemProperty.TWITTER_ACCESS_TOKEN);
//			SystemProperty accessTokenSecret = ObjectifyDatastoreDAO.getInstance().getSystemProperty(SystemProperty.TWITTER_ACCESS_TOKEN_SECRET);
//			if (consumerKey != null && consumerSecret != null && accessToken != null && accessTokenSecret != null) {
//				params = new TwitterParams(consumerKey, consumerSecret, accessToken, accessTokenSecret);
//				mem.put(CACHE_TWITTER_FACTORY_PARAMS, params, Expiration.byDeltaSeconds(10 * 60));
//			}
			if (consumerKey != null && consumerSecret != null) {
				params = new TwitterParams(consumerKey, consumerSecret);
				mem.put(CACHE_TWITTER_FACTORY_PARAMS, params, Expiration.byDeltaSeconds(10 * 60));
			}
		}
		
		if (params != null) {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey(params.consumerKey)
			  .setOAuthConsumerSecret(params.consumerSecret)
			  .setOAuthAccessToken(params.accessToken)
			  .setOAuthAccessTokenSecret(params.accessTokenSecret);
			return new TwitterFactory(cb.build());
		}
		return null;
	}
	
}
