package com.startupbidder.vo;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class BaseResultVO {
	@JsonProperty("logout_url")
	private String logoutUrl;
	@JsonProperty("loggedin_profile")
	private UserVO loggedUser;

	public String getLogoutUrl() {
		return logoutUrl;
	}
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
	public UserVO getLoggedUser() {
		return loggedUser;
	}
	public void setLoggedUser(UserVO loggedUser) {
		this.loggedUser = loggedUser;
	}
	@Override
	public String toString() {
		return "BaseResultVO [logoutUrl=" + logoutUrl + ", loggedUser="
				+ loggedUser + "]";
	}

}
