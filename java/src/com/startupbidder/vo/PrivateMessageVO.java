package com.startupbidder.vo;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.startupbidder.util.DateSerializer;
import com.startupbidder.util.LowecaseSerializer;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class PrivateMessageVO extends BaseVO {
	@JsonProperty("direction") @JsonSerialize(using=LowecaseSerializer.class) private String direction;
	@JsonProperty("create_date") @JsonSerialize(using=DateSerializer.class) private Date created;
	@JsonProperty("text") private String text;
	@JsonProperty("read") private boolean read;
	public PrivateMessageVO() {
	}
	public String getId() {
		return "not_set";
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
}
