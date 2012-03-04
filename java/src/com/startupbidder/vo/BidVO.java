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
public class BidVO extends BaseVO {
	@JsonProperty("num")
	private int orderNumber;
	@JsonProperty("bid_id")
	private String id;
	@JsonProperty("profile_id")
	private String user;
	@JsonProperty("listing_profile_id")
	private String listingOwner;
	@JsonProperty("profile_username")
	private String userName;
	@JsonProperty("listing_id")
	private String listing;
	@JsonProperty("listing_title")
	private String listingName;
	@JsonProperty("bid_date")
	@JsonSerialize(using=DateSerializer.class)
	private Date   placed;
	@JsonProperty("expires_date")
	@JsonSerialize(using=DateSerializer.class)
	private Date   expires;
	@JsonProperty("amount")
	private int    value;
	@JsonProperty("equity_pct")
	private int    percentOfCompany;
	@JsonProperty("bid_type")
	@JsonSerialize(using=LowecaseSerializer.class)
	private String fundType;
	@JsonProperty("valuation")
	private int valuation;
	@JsonProperty("interest_rate")
	private int interestRate;
	@JsonProperty("mockData")
	private boolean mockData;
	@JsonProperty("action")
	@JsonSerialize(using=LowecaseSerializer.class)
	private String action;
	@JsonProperty("actor")
	@JsonSerialize(using=LowecaseSerializer.class)
	private String actor;
	@JsonProperty("bid_note")
	private String comment;
	
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

	public String getListingOwner() {
		return listingOwner;
	}

	public void setListingOwner(String listingOwner) {
		this.listingOwner = listingOwner;
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

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public boolean isMockData() {
		return mockData;
	}

	public void setMockData(boolean mockData) {
		this.mockData = mockData;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	@Override
	public String toString() {
		return "BidVO [orderNumber=" + orderNumber + ", id=" + id + ", user="
				+ user + ", listingOwner=" + listingOwner + ", userName="
				+ userName + ", listing=" + listing + ", listingName="
				+ listingName + ", placed=" + placed + ", expires=" + expires
				+ ", value=" + value + ", percentOfCompany=" + percentOfCompany
				+ ", fundType=" + fundType + ", valuation=" + valuation
				+ ", interestRate=" + interestRate + ", mockData=" + mockData
				+ ", action=" + action + ", actor=" + actor + ", comment="
				+ comment + "]";
	}

}
