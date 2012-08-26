package com.startupbidder.vo;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class UserListingsForUsersVO extends UserListingsForAdminVO {
	@JsonProperty("profile") private UserBasicVO user;
	public UserBasicVO getUserBasic() {
		return user;
	}
	public void setUserBasic(UserBasicVO user) {
		this.user = user;
	}
}
