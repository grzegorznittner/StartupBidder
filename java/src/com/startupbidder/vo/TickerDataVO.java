package com.startupbidder.vo;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class TickerDataVO {
	@JsonProperty("id")
	private String id;
	@JsonProperty("value")
	private long value;
	@JsonProperty("change")
	private long change;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	public long getChange() {
		return change;
	}
	public void setChange(long change) {
		this.change = change;
	}
	@Override
	public String toString() {
		return "TickerDataVO [id=" + id + ", value=" + value + ", change="
				+ change + "]";
	}
}
