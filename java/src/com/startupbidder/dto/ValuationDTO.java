package com.startupbidder.dto;

import com.google.appengine.api.datastore.Key;

public class ValuationDTO {
	private Key id;
	private String businessPlan;
	private String user;
	private int value;
	
	public ValuationDTO() {
	}

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public String getBusinessPlan() {
		return businessPlan;
	}

	public void setBusinessPlan(String businessPlan) {
		this.businessPlan = businessPlan;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ValuationDTO [id=" + id + ", businessPlan=" + businessPlan
				+ ", user=" + user + ", value=" + value + "]";
	}
	
}
