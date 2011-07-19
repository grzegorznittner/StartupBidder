package com.startupbidder.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class UserListVO {
	@JsonProperty("users")
	private List<UserVO> users;
	@JsonProperty("profile")
	private UserVO user;
	@JsonProperty("loggedin_profile")
	private UserVO loggedUser;
	
	public List<UserVO> getUsers() {
		return users;
	}
	public void setUsers(List<UserVO> users) {
		this.users = users;
	}
	public UserVO getUser() {
		return user;
	}
	public void setUser(UserVO user) {
		this.user = user;
	}
	public UserVO getLoggedUser() {
		return loggedUser;
	}
	public void setLoggedUser(UserVO loggedUser) {
		this.loggedUser = loggedUser;
	}
	@Override
	public String toString() {
		return "UserListVO [users=" + users + ", user=" + user
				+ ", loggedUser=" + loggedUser + "]";
	}
}
