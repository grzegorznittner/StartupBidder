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
	@JsonProperty("other_user_profile") private UserShortVO otherUser;
	@JsonProperty("messages_props")	private ListPropertiesVO messagesProperties;
	public List<PrivateMessageVO> getMessages() {
		return messages;
	}
	public void setMessages(List<PrivateMessageVO> messages) {
		this.messages = messages;
	}
	public UserShortVO getOtherUser() {
		return otherUser;
	}
	public void setOtherUser(UserShortVO otherUser) {
		this.otherUser = otherUser;
	}
	public ListPropertiesVO getMessagesProperties() {
		return messagesProperties;
	}
	public void setMessagesProperties(ListPropertiesVO messagesProperties) {
		this.messagesProperties = messagesProperties;
	}
}
