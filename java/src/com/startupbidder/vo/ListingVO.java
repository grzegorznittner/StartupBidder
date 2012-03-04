package com.startupbidder.vo;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.startupbidder.util.DateSerializer;
import com.startupbidder.util.LowecaseSerializer;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ListingVO extends BaseVO {
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
	@JsonProperty("previous_val")
	private int previousValuation;
	@JsonProperty("valuation")
	private int   valuation;
	@JsonProperty("median_valuation")
	private int   medianValuation;
	@JsonProperty("score")
	private int   score;
	@JsonProperty("posted_date")
	@JsonSerialize(using=DateSerializer.class)
	private Date  postedOn;
	@JsonProperty("listing_date")
	@JsonSerialize(using=DateSerializer.class)
	private Date  listedOn;
	@JsonProperty("closing_date")
	@JsonSerialize(using=DateSerializer.class)
	private Date  closingOn;
	@JsonProperty("status")
	@JsonSerialize(using=LowecaseSerializer.class)
	private String state;
	@JsonProperty("description")
	private String description;
	@JsonProperty("summary")
	private String summary;
	@JsonProperty("website")
	private String website;
	@JsonProperty("category")
	private String category;
	@JsonProperty("profile_id")
	private String owner;
	@JsonProperty("profile_username")
	private String ownerName;
	@JsonProperty("address")
	private String address;
	@JsonProperty("num_comments")
	private long numberOfComments;
	@JsonProperty("num_bids")
	private long numberOfBids;
	@JsonProperty("num_votes")
	private long numberOfVotes;
	@JsonProperty("votable")
	private boolean votable;
	@JsonProperty("days_ago")
	private int daysAgo;
	@JsonProperty("days_left")
	private int daysLeft;
	@JsonProperty("mockData")
	private boolean mockData;
	@JsonProperty("business_plan_id")
	private String buinessPlanId;
	@JsonProperty("presentation_id")
	private String presentationId;
	@JsonProperty("financials_id")
	private String financialsId;
	
	public ListingVO() {
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
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

	public int getValuation() {
		return valuation;
	}

	public void setValuation(int valuation) {
		this.valuation = valuation;
	}

	public int getMedianValuation() {
		return medianValuation;
	}

	public void setMedianValuation(int medianValuation) {
		this.medianValuation = medianValuation;
	}

	public Date getListedOn() {
		return listedOn;
	}

	public void setListedOn(Date listedOn) {
		this.listedOn = listedOn;
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

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public long getNumberOfComments() {
		return numberOfComments;
	}

	public void setNumberOfComments(long numberOfComments) {
		this.numberOfComments = numberOfComments;
	}

	public long getNumberOfBids() {
		return numberOfBids;
	}

	public void setNumberOfBids(long numberOfBids) {
		this.numberOfBids = numberOfBids;
	}

	public long getNumberOfVotes() {
		return numberOfVotes;
	}

	public void setNumberOfVotes(long numberOfVotes) {
		this.numberOfVotes = numberOfVotes;
	}

	public boolean isVotable() {
		return votable;
	}

	public void setVotable(boolean votable) {
		this.votable = votable;
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

	public String getBuinessPlanId() {
		return buinessPlanId;
	}

	public void setBuinessPlanId(String buinessPlanId) {
		this.buinessPlanId = buinessPlanId;
	}

	public String getPresentationId() {
		return presentationId;
	}

	public void setPresentationId(String presentationId) {
		this.presentationId = presentationId;
	}

	public String getFinancialsId() {
		return financialsId;
	}

	public void setFinancialsId(String financialsId) {
		this.financialsId = financialsId;
	}

	public boolean isMockData() {
		return mockData;
	}

	public void setMockData(boolean mockData) {
		this.mockData = mockData;
	}

	public int getPreviousValuation() {
		return previousValuation;
	}

	public void setPreviousValuation(int previousValuation) {
		this.previousValuation = previousValuation;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Date getPostedOn() {
		return postedOn;
	}

	public void setPostedOn(Date postedOn) {
		this.postedOn = postedOn;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
