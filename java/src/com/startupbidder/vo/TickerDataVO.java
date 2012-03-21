package com.startupbidder.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	@JsonProperty("type")
	private String type;
	@JsonProperty("created")
	private Date created;
	@JsonProperty("items")
	private List<TickerDataItemVO> items = new ArrayList<TickerDataItemVO>();
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public void addItem(TickerDataItemVO item) {
		items.add(item);
	}
	public List<TickerDataItemVO> getItems() {
		return items;
	}

	@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
			fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
	public static class TickerDataItemVO {
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
			return "[id=" + id + ", value=" + value + ", change="
					+ change + "]";
		}
	}
}
