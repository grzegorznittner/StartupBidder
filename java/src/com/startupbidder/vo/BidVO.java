package com.startupbidder.vo;

import java.util.Date;

public class BidVO {
	private String id;
	private String user;
	private String businessPlan;
	private Date   placed;
	private int    value;
	private int    percentOfCompany;
	private String fundType;
	
	public BidVO() {
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getBusinessPlan() {
		return businessPlan;
	}

	public void setBusinessPlan(String businessPlan) {
		this.businessPlan = businessPlan;
	}

	public Date getPlaced() {
		return placed;
	}

	public void setPlaced(Date placed) {
		this.placed = placed;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getPercentOfCompany() {
		return percentOfCompany;
	}

	public void setPercentOfCompany(int percentOfCompany) {
		this.percentOfCompany = percentOfCompany;
	}

	public String getFundType() {
		return fundType;
	}

	public void setFundType(String fundType) {
		this.fundType = fundType;
	}

	@Override
	public String toString() {
		return "BidVO [id=" + id + ", user=" + user + ", businessPlan="
				+ businessPlan + ", placed=" + placed + ", value=" + value
				+ ", percentOfCompany=" + percentOfCompany + ", fundType="
				+ fundType + "]";
	}

}
