package com.startupbidder.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class PrivateMessageListVO extends BaseResultVO {
	@JsonProperty("messages") private List<PrivateMessageVO> messages;
	@JsonProperty("other_user_profile") private UserBasicVO otherUser;
	public List<PrivateMessageVO> getMessages() {
		return messages;
	}
	public void setMessages(List<PrivateMessageVO> messages) {
		this.messages = messages;
	}
	public UserBasicVO getOtherUser() {
		return otherUser;
	}
	public void setOtherUser(UserBasicVO otherUser) {
		this.otherUser = otherUser;
	}
}
