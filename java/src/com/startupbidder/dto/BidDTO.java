package com.startupbidder.dto;

import java.util.Date;

public class BidDTO extends AbstractDTO {
	public enum FundType {SYNDICATE, SOLE_INVESTOR};
	
	private String user;
	private String businessPlan;
	private Date   placed;
	private int    value;
	private int    percentOfCompany;
	private FundType fundType;
	
	public BidDTO() {
	}
	
	public String getKind() {
		return "Bid";
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

	public FundType getFundType() {
		return fundType;
	}

	public void setFundType(FundType fundType) {
		this.fundType = fundType;
	}

	@Override
	public String toString() {
		return "BidDTO [id=" + id + ", user=" + user + ", businessPlan="
				+ businessPlan + ", placed=" + placed + ", value=" + value
				+ ", percentOfCompany=" + percentOfCompany + ", fundType="
				+ fundType + "]";
	}

}
