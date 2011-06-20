package com.startupbidder.dto;


public class RatingDTO extends AbstractDTO {
	private String businessPlan;
	private String user;
	private int value;
	
	public RatingDTO() {
	}

	public String getKind() {
		return "Rating";
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
