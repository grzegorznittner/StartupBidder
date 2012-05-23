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
public class PrivateMessageUserListVO extends BaseResultVO {
	@JsonProperty("users") private List<PrivateMessageUserVO> messages;

	public List<PrivateMessageUserVO> getMessages() {
		return messages;
	}

	public void setMessages(List<PrivateMessageUserVO> messages) {
		this.messages = messages;
	}
}
