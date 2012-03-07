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
	public static final String[] UPDATABLE_PROPERTIES = {"title", "mantra", "summary", "website", "category",
			"address", "suggested_amt", "suggested_pct", "answer1", "answer2", "answer3",
			"answer4", "answer5", "answer6", "answer7", "answer8", "answer9", "answer10", "answer11"};
	
	@JsonProperty("num") private int orderNumber;
	@JsonProperty("listing_id")	private String id;
	@JsonProperty("title") private String name;
	@JsonProperty("suggested_amt") private int suggestedAmount;
	@JsonProperty("suggested_pct") private int suggestedPercentage;
	@JsonProperty("suggested_val") private int suggestedValuation;
	@JsonProperty("previous_val") private int previousValuation;
	@JsonProperty("valuation") private int valuation;
	@JsonProperty("median_valuation") private int medianValuation;
	@JsonProperty("score") private int score;
	@JsonProperty("modified_date") @JsonSerialize(using=DateSerializer.class) private Date  modified;
	@JsonProperty("created_date") @JsonSerialize(using=DateSerializer.class) private Date  created;
	@JsonProperty("posted_date") @JsonSerialize(using=DateSerializer.class) private Date  postedOn;
	@JsonProperty("listing_date") @JsonSerialize(using=DateSerializer.class) private Date  listedOn;
	@JsonProperty("closing_date") @JsonSerialize(using=DateSerializer.class) private Date  closingOn;
	@JsonProperty("status")	@JsonSerialize(using=LowecaseSerializer.class) private String state;
	@JsonProperty("mantra")	private String mantra;
	@JsonProperty("summary") private String summary;
	@JsonProperty("website") private String website;
	@JsonProperty("category") private String category;
	@JsonProperty("profile_id") private String owner;
	@JsonProperty("profile_username") private String ownerName;
	@JsonProperty("address") private String address;
	@JsonProperty("num_comments") private long numberOfComments;
	@JsonProperty("num_bids") private long numberOfBids;
	@JsonProperty("num_votes") private long numberOfVotes;
	@JsonProperty("votable") private boolean votable;
	@JsonProperty("days_ago") private int daysAgo;
	@JsonProperty("days_left") private int daysLeft;
	@JsonProperty("mockData") private boolean mockData;
	@JsonProperty("business_plan_id") private String buinessPlanId;
	@JsonProperty("presentation_id") private String presentationId;
	@JsonProperty("financials_id") private String financialsId;
	@JsonProperty("answer1") private String answer1;
	@JsonProperty("answer2") private String answer2;
	@JsonProperty("answer3") private String answer3;
	@JsonProperty("answer4") private String answer4;
	@JsonProperty("answer5") private String answer5;
	@JsonProperty("answer6") private String answer6;
	@JsonProperty("answer7") private String answer7;
	@JsonProperty("answer8") private String answer8;
	@JsonProperty("answer9") private String answer9;
	@JsonProperty("answer10") private String answer10;
	@JsonProperty("answer11") private String answer11;
	
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

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
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

	public String getMantra() {
		return mantra;
	}

	public void setMantra(String mantra) {
		this.mantra = mantra;
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

	public String getAnswer1() {
		return answer1;
	}

	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}

	public String getAnswer2() {
		return answer2;
	}

	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}

	public String getAnswer3() {
		return answer3;
	}

	public void setAnswer3(String answer3) {
		this.answer3 = answer3;
	}

	public String getAnswer4() {
		return answer4;
	}

	public void setAnswer4(String answer4) {
		this.answer4 = answer4;
	}

	public String getAnswer5() {
		return answer5;
	}

	public void setAnswer5(String answer5) {
		this.answer5 = answer5;
	}

	public String getAnswer6() {
		return answer6;
	}

	public void setAnswer6(String answer6) {
		this.answer6 = answer6;
	}

	public String getAnswer7() {
		return answer7;
	}

	public void setAnswer7(String answer7) {
		this.answer7 = answer7;
	}

	public String getAnswer8() {
		return answer8;
	}

	public void setAnswer8(String answer8) {
		this.answer8 = answer8;
	}

	public String getAnswer9() {
		return answer9;
	}

	public void setAnswer9(String answer9) {
		this.answer9 = answer9;
	}

	public String getAnswer10() {
		return answer10;
	}

	public void setAnswer10(String answer10) {
		this.answer10 = answer10;
	}

    public String getAnswer11() {
		return answer11;
	}

	public void setAnswer11(String answer11) {
		this.answer11 = answer11;
	}
}
