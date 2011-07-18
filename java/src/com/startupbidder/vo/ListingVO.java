package com.startupbidder.vo;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.startupbidder.util.DateSerializer;
import com.startupbidder.util.LowecaseSerializer;

@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class ListingVO {
	@JsonProperty("num")
	private int orderNumber;
	@JsonProperty("listing_id")
	private String id;
	@JsonProperty("title")
	private String name;
	@JsonProperty("suggested_amt")
	private int suggestedAmount;
	@JsonProperty("suggested_pct")
	private int suggestedPercentage;
	@JsonProperty("suggested_val")
	private int suggestedValuation;
	@JsonProperty("median_valuation")
	private int   medianValuation;
	@JsonProperty("listing_date")
	@JsonSerialize(using=DateSerializer.class)
	private Date  listedOn;
	@JsonProperty("closing_date")
	@JsonSerialize(using=DateSerializer.class)
	private Date  closingOn;
	@JsonProperty("status")
	@JsonSerialize(using=LowecaseSerializer.class)
	private String state;
	@JsonProperty("summary")
	private String summary;
	@JsonProperty("profile_id")
	private String owner;
	@JsonProperty("profile_username")
	private String ownerName;
	@JsonProperty("num_comments")
	private int numberOfComments;
	@JsonProperty("num_bids")
	private int numberOfBids;
	@JsonProperty("num_votes")
	private int numberOfVotes;
	
	@JsonProperty("days_ago")
	private int daysAgo;
	@JsonProperty("days_left")
	private int daysLeft;
	
	public ListingVO() {
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getMedianValuation() {
		return medianValuation;
	}

	public void setMedianValuation(int medianValuation) {
		this.medianValuation = medianValuation;
	}

	public Date getClosingOn() {
		return closingOn;
	}

	public void setClosingOn(Date closingOn) {
		this.closingOn = closingOn;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getListedOn() {
		return listedOn;
	}

	public void setListedOn(Date listedOn) {
		this.listedOn = listedOn;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public int getNumberOfComments() {
		return numberOfComments;
	}

	public void setNumberOfComments(int numberOfComments) {
		this.numberOfComments = numberOfComments;
	}

	public int getNumberOfBids() {
		return numberOfBids;
	}

	public void setNumberOfBids(int numberOfBids) {
		this.numberOfBids = numberOfBids;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public int getNumberOfVotes() {
		return numberOfVotes;
	}

	public void setNumberOfVotes(int numberOfVotes) {
		this.numberOfVotes = numberOfVotes;
	}

	public int getSuggestedAmount() {
		return suggestedAmount;
	}

	public void setSuggestedAmount(int suggestedAmount) {
		this.suggestedAmount = suggestedAmount;
	}

	public int getSuggestedPercentage() {
		return suggestedPercentage;
	}

	public void setSuggestedPercentage(int suggestedPercentage) {
		this.suggestedPercentage = suggestedPercentage;
	}

	public int getSuggestedValuation() {
		return suggestedValuation;
	}

	public void setSuggestedValuation(int suggestedValuation) {
		this.suggestedValuation = suggestedValuation;
	}

	public int getDaysAgo() {
		return daysAgo;
	}

	public void setDaysAgo(int daysAgo) {
		this.daysAgo = daysAgo;
	}

	public int getDaysLeft() {
		return daysLeft;
	}

	public void setDaysLeft(int daysLeft) {
		this.daysLeft = daysLeft;
	}

	@Override
	public String toString() {
		return "ListingVO [orderNumber=" + orderNumber + ", id=" + id
				+ ", name=" + name
				+ ", medianValuation=" + medianValuation + ", listedOn="
				+ listedOn + ", closingOn=" + closingOn + ", state=" + state
				+ ", summary=" + summary + ", owner=" + owner + ", ownerName="
				+ ownerName + ", numberOfComments=" + numberOfComments
				+ ", numberOfBids=" + numberOfBids
				+ ", numberOfVotes=" + numberOfVotes + ", suggestedAmount="
				+ suggestedAmount + ", suggestedPercentage="
				+ suggestedPercentage + ", suggestedValuation="
				+ suggestedValuation + ", daysAgo=" + daysAgo + ", daysLeft="
				+ daysLeft + "]";
	}
}