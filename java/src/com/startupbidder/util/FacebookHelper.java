package com.startupbidder.util;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.SystemProperty;

/**
 * Facebook OAuth code was based on the article
 * http://www.richardnichols.net/2010/06/implementing-facebook-oauth-2-0-authentication-in-java/
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class FacebookHelper {
	private static final Logger log = Logger.getLogger(FacebookHelper.class.getName());

	public static final String CACHE_FACEBOOK_PARAMS = "facebook_params";
	
	public static final String SERVLET_FACEBOOK_LOGIN = "/facebook_login";
	public static final String SERVLET_FACEBOOK_LOGOUT = "/facebook_logout";
	public static final String SERVLET_FACEBOOK_CALLBACK = "/facebook_callback";
	
	public static final String SESSION_FACEBOOK_ACCESS_TOKEN = "facebook_accessToken";
	public static final String SESSION_FACEBOOK_TARGET_URL = "facebook_targetUrl";
	public static final String SESSION_FACEBOOK_USER = "facebook_user";
	
	public static final String PARAM_FACEBOOK_CODE = "code";
	
	@SuppressWarnings("serial")
	public static class FacebookAccessToken implements Serializable {
		FacebookAccessToken(String accessToken, int expires) {
			this.accessToken = accessToken;
			this.expires = expires;
		}
		String accessToken;
		int expires;
	}
	
	@SuppressWarnings("serial")
	public static class FacebookParams implements Serializable {
		FacebookParams(SystemProperty cId, SystemProperty cSecret) {
			clientId = cId.value;
			clientSecret = cSecret.value;
		}
		String clientId, clientSecret;
	}
	
	public static FacebookParams getFacebookAuthParams() {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		
		FacebookParams params = (FacebookParams)mem.get(CACHE_FACEBOOK_PARAMS);
		if (params == null) {
			SystemProperty clientId = ObjectifyDatastoreDAO.getInstance().getSystemProperty(SystemProperty.FACEBOOK_CLIENT_ID);
			SystemProperty clientSecret = ObjectifyDatastoreDAO.getInstance().getSystemProperty(SystemProperty.FACEBOOK_CLIENT_SECRET);
			if (clientId != null && clientSecret != null) {
				params = new FacebookParams(clientId, clientSecret);
				mem.put(CACHE_FACEBOOK_PARAMS, params, Expiration.byDeltaSeconds(10 * 60));
			}
		}
		return params;
	}
	
	public static FacebookUser authorizeUser(HttpServletRequest request, String accessToken, int expires) throws MalformedURLException, IOException {
		FacebookAccessToken token = new FacebookAccessToken(accessToken, expires);
		request.getSession().setAttribute(SESSION_FACEBOOK_ACCESS_TOKEN, token);
		
		String accessTokenUrl = "https://graph.facebook.com/me?access_token=" + URLEncoder.encode(accessToken, "UTF-8");
		
        URLConnection con = new URL(accessTokenUrl).openConnection();
		String result = IOUtils.toString(con.getInputStream(), "UTF-8");
		String mimeType = con.getContentType();

		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Result of call to " + accessTokenUrl + " (mimetype: " + mimeType + "): " + result);
		JsonNode rootNode = mapper.readValue(result, JsonNode.class);
        String id = rootNode.get("id").getValueAsText();
        String firstName = rootNode.get("first_name").getValueAsText();
        String lastName = rootNode.get("last_name").getValueAsText();
        String email = rootNode.get("email").getValueAsText();
        
		FacebookUser user = new FacebookUser(id, firstName, lastName, email);
		request.getSession().setAttribute(SESSION_FACEBOOK_USER, user);
		return user;
	}
	
	public static void logoutUser(HttpServletRequest request) {
		request.getSession().removeAttribute(SESSION_FACEBOOK_USER);
		request.getSession().removeAttribute(SESSION_FACEBOOK_ACCESS_TOKEN);
	}
	
	public static FacebookUser getFacebookUser(HttpServletRequest request) {
		return (FacebookUser)request.getSession().getAttribute(FacebookHelper.SESSION_FACEBOOK_USER);
	}
	
	public static String getLoginUrl(HttpServletRequest request) {
		String appUrl = TwitterHelper.getApplicationUrl(request);
		if (appUrl.contains("alocalhost")) {
			return null;
		} else {
			return appUrl + SERVLET_FACEBOOK_LOGIN;
		}
	}
	
	public static String getLogoutUrl(HttpServletRequest request) {
		String appUrl = TwitterHelper.getApplicationUrl(request);
		if (appUrl.contains("alocalhost")) {
			return null;
		} else {
			return appUrl + SERVLET_FACEBOOK_LOGOUT;
		}
	}

	public static String getCallbackUrl(HttpServletRequest request) {
		String appUrl = TwitterHelper.getApplicationUrl(request);
		if (appUrl.contains("alocalhost")) {
			return null;
		} else {
			return appUrl + SERVLET_FACEBOOK_CALLBACK;
		}
	}

	public static String getAuthorizeUrl(HttpServletRequest request) throws UnsupportedEncodingException {
		FacebookParams params = getFacebookAuthParams();
		String callbackUrl = getCallbackUrl(request);
		if (params == null || callbackUrl == null) {
			return null;
		}
		String authUrl = "https://graph.facebook.com/oauth/authorize?"
				+ "client_id=" + URLEncoder.encode(params.clientId, "UTF-8")
				+ "&redirect_uri=" + URLEncoder.encode(callbackUrl, "UTF-8")
				+ "&scope=email&response_type=code";
		return authUrl;
	}

	public static String getAccessTokenUrl(HttpServletRequest request) throws UnsupportedEncodingException {
		String code = request.getParameter(FacebookHelper.PARAM_FACEBOOK_CODE);
        if (StringUtils.isEmpty(code)) {
        	log.warning("Facebook authorization call returned empty/missing 'code' parameter.");
            return null;
        }
		FacebookParams params = getFacebookAuthParams();
		String callbackUrl = getCallbackUrl(request);
		if (params == null || callbackUrl == null) {
			return null;
		}
		String authUrl = "https://graph.facebook.com/oauth/access_token"
				+ "?client_id=" + URLEncoder.encode(params.clientId, "UTF-8")
				+ "&redirect_uri=" + URLEncoder.encode(callbackUrl, "UTF-8")
				+ "&client_secret=" + URLEncoder.encode(params.clientSecret, "UTF-8")
				+ "&code=" + URLEncoder.encode(code, "UTF-8");
		return authUrl;
	}
}
