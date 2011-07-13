package com.startupbidder.vo;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility=Visibility.NONE,
		setterVisibility=Visibility.NONE, fieldVisibility=Visibility.NONE)
public class BidVO {
	@JsonProperty("num")
	private int orderNumber;
	@JsonProperty("bid_id")
	private String id;
	@JsonProperty("profile_id")
	private String user;
	@JsonProperty("profile_username")
	private String userName;
	@JsonProperty("listing_id")
	private String listing;
	@JsonProperty("listing_title")
	private String listingName;
	@JsonProperty("bid_date")
	private Date   placed;
	@JsonProperty("amount")
	private int    value;
	@JsonProperty("equity_pct")
	private int    percentOfCompany;
	@JsonProperty("bid_type")
	private String fundType;
	@JsonProperty("valuation")
	private int valuation;
	@JsonProperty("interest_rate")
	private int interestRate;
	@JsonProperty("status")
	private String status;

	
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

	public String getListing() {
		return listing;
	}

	public void setListing(String listing) {
		this.listing = listing;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getListingName() {
		return listingName;
	}

	public void setListingName(String listingName) {
		this.listingName = listingName;
	}

	public int getValuation() {
		return valuation;
	}

	public void setValuation(int valuation) {
		this.valuation = valuation;
	}

	public int getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(int interestRate) {
		this.interestRate = interestRate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public String toString() {
		return "BidVO [id=" + id + ", user=" + user + ", userName=" + userName
				+ ", listing=" + listing + ", listingName=" + listingName
				+ ", placed=" + placed + ", value=" + value
				+ ", percentOfCompany=" + percentOfCompany + ", fundType="
				+ fundType + ", valuation=" + valuation + ", interestRate="
				+ interestRate + ", status=" + status + ", orderNumber="
				+ orderNumber + "]";
	}

}
