package com.startupbidder.vo;


public class RatingVO {
	private String id;
	private String businessPlan;
	private String user;
	private int value;
	
	public RatingVO() {
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
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
		return "RatingDTO [id=" + id + ", businessPlan=" + businessPlan
				+ ", user=" + user + ", value=" + value + "]";
	}
	
}
