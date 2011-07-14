package com.startupbidder.dto;

import java.util.Date;

public class BidDTO extends AbstractDTO {
	public enum FundType {SYNDICATE, SOLE_INVESTOR};
	
	private String user;
	private String listing;
	private Date   placed;
	private int    value;
	private int    percentOfCompany;
	private int    valuation;
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

	public String getListing() {
		return listing;
	}

	public void setListing(String businessPlan) {
		this.listing = businessPlan;
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

	public void setValuation(int valuation) {
		this.valuation = valuation;
	}

	public int getValuation() {
		return valuation;
	}

	@Override
	public String toString() {
		return "BidDTO [user=" + user + ", listing=" + listing + ", placed="
				+ placed + ", value=" + value + ", percentOfCompany="
				+ percentOfCompany + ", valuation=" + valuation + ", fundType="
				+ fundType + "]";
	}

}
