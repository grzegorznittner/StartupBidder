package com.startupbidder.dto;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;

public class BidDTO extends AbstractDTO {
	public enum FundType {SYNDICATE, SOLE_INVESTOR, COMMON, PREFERRED, NOTE};
	public enum Status { ACTIVE, WITHDRAWN };
	
	private String user;
	private String listing;
	private Date   placed;
	private int    value;
	private int    percentOfCompany;
	private int    valuation;
	private FundType fundType;
	private Status status = Status.ACTIVE;
	
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "BidDTO [user=" + user + ", listing=" + listing + ", placed="
				+ placed + ", value=" + value + ", percentOfCompany="
				+ percentOfCompany + ", valuation=" + valuation + ", fundType="
				+ fundType + ", status=" + status + "]";
	}

	public Entity toEntity() {
		Entity bid = new Entity(this.id);
		bid.setProperty("fundType", fundType);
		bid.setProperty("status", status);
		bid.setProperty("listing", listing);
		bid.setProperty("percentOfCompany", percentOfCompany);
		bid.setProperty("placed", placed);
		bid.setProperty("user", user);
		bid.setProperty("valuation", valuation);
		bid.setProperty("value", value);
		return bid;
	}
	
	public static BidDTO fromEntity(Entity entity) {
		BidDTO bid = new BidDTO();
		bid.setKey(entity.getKey());
		bid.setFundType(FundType.valueOf((String)entity.getProperty("fundType")));
		bid.setStatus(Status.valueOf((String)entity.getProperty("status")));
		bid.setListing((String)entity.getProperty("listing"));
		bid.setPercentOfCompany((Integer)entity.getProperty("percentOfCompany"));
		bid.setPlaced((Date)entity.getProperty("placed"));
		bid.setUser((String)entity.getProperty("user"));
		bid.setValuation((Integer)entity.getProperty("valuation"));
		bid.setValue((Integer)entity.getProperty("value"));
		return bid;
	}
}
