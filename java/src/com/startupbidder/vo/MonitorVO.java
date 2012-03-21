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
public class MonitorVO extends BaseVO {
	@JsonProperty("num")
	private int orderNumber;
	@JsonProperty("monitor_id")
	private String id;
	@JsonProperty("object_id")
	private String objectId;
	@JsonProperty("type")
	@JsonSerialize(using=LowecaseSerializer.class)
	private String type;
	@JsonProperty("profile_id")
	private String user;
	@JsonProperty("profile_username")
	private String userName;
	@JsonProperty("create_date")
	@JsonSerialize(using=DateSerializer.class)
	private Date   created;
	@JsonProperty("deactivate_date")
	@JsonSerialize(using=DateSerializer.class)
	private Date   deactivated;
	@JsonProperty("mockData")
	private boolean mockData;
	@JsonProperty("active")
	private boolean active;
	public MonitorVO() {
	}
	public int getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getDeactivated() {
		return deactivated;
	}
	public void setDeactivated(Date deactivated) {
		this.deactivated = deactivated;
	}
	public boolean isMockData() {
		return mockData;
	}
	public void setMockData(boolean mockData) {
		this.mockData = mockData;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
}
