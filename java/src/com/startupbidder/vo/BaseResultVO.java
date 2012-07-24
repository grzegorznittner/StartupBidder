package com.startupbidder.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Base class for VO classes contains some common objects always send to the client.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class BaseResultVO {
	@JsonProperty("login_url") private String loginUrl;
	@JsonProperty("logout_url") private String logoutUrl;
	@JsonProperty("loggedin_profile") private UserBasicVO loggedUser;
	@JsonProperty("twitter_login_url") private String twitterLoginUrl;
	@JsonProperty("fb_login_url") private String facebookLoginUrl;
	
	@JsonProperty("error_code") private int errorCode = ErrorCodes.OK;
	@JsonProperty("error_msg") private String errorMessage;

	public String getLogoutUrl() {
		return logoutUrl;
	}
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
	public UserBasicVO getLoggedUser() {
		return loggedUser;
	}
	public void setLoggedUser(UserVO loggedUser) {
		this.loggedUser = new UserBasicVO(loggedUser);
	}
	public void setLoggedUser(UserBasicVO loggedUser) {
		this.loggedUser = loggedUser;
	}
	public String getLoginUrl() {
		return loginUrl;
	}
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public String getTwitterLoginUrl() {
		return twitterLoginUrl;
	}
	public void setTwitterLoginUrl(String twitterLoginUrl) {
		this.twitterLoginUrl = twitterLoginUrl;
	}
	public String getFacebookLoginUrl() {
		return facebookLoginUrl;
	}
	public void setFacebookLoginUrl(String facebookLoginUrl) {
		this.facebookLoginUrl = facebookLoginUrl;
	}
}
