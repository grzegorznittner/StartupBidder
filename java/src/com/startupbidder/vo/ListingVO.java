package com.startupbidder.vo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
	public static final List<String> UPDATABLE_PROPERTIES = Arrays.asList(new String[] {
			"title", "mantra", "summary", "contact_email", "founders",
			"website", "category", "address", "city", "state", "country", "latitude", "longitude",
			"asked_fund", "suggested_amt", "suggested_pct", "video", "answer1", "answer2", "answer3",
			"answer4", "answer5", "answer6", "answer7", "answer8", "answer9", "answer10", "answer11", "answer12", "answer13",
			"answer14", "answer15", "answer16", "answer17", "answer18", "answer19", "answer20", "answer21", "answer22", "answer23",
            "answer24", "answer25", "answer26"
    });
	public static final List<String> FETCHED_PROPERTIES = Arrays.asList(new String[] {"business_plan_url", 
			"presentation_url", "financials_url", "logo_url"});
	
	@JsonProperty("num") private int orderNumber;
	@JsonProperty("listing_id")	private String id;
	@JsonProperty("title") private String name;
	@JsonProperty("asked_fund") private boolean askedForFunding;
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
	@JsonProperty("founders") private String founders;
	@JsonProperty("contact_email") private String contactEmail;
	@JsonProperty("address") private String address;
	@JsonProperty("brief_address") private String briefAddress;
	@JsonProperty("latitude") private Double latitude;
	@JsonProperty("longitude") private Double longitude;
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
	@JsonProperty("logo") private String logo;
	@JsonProperty("video")	private String video;
	@JsonProperty("business_plan_upload") private String buinessPlanUpload;
	@JsonProperty("presentation_upload") private String presentationUpload;
	@JsonProperty("financials_upload") private String financialsUpload;
	@JsonProperty("logo_upload") private String logoUpload;
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
	@JsonProperty("answer12") private String answer12;
	@JsonProperty("answer13") private String answer13;
	@JsonProperty("answer14") private String answer14;
	@JsonProperty("answer15") private String answer15;
	@JsonProperty("answer16") private String answer16;
	@JsonProperty("answer17") private String answer17;
	@JsonProperty("answer18") private String answer18;
	@JsonProperty("answer19") private String answer19;
	@JsonProperty("answer20") private String answer20;
	@JsonProperty("answer21") private String answer21;
	@JsonProperty("answer22") private String answer22;
	@JsonProperty("answer23") private String answer23;
	@JsonProperty("answer24") private String answer24;
	@JsonProperty("answer25") private String answer25;
	@JsonProperty("answer26") private String answer26;
	
	public ListingVO() {
	}
	
	public String dataForSearch() {
		StringBuffer buf = new StringBuffer();
		appendText(buf, name);
		appendText(buf, mantra);
		appendText(buf, summary);
		appendText(buf, website);
		appendText(buf, ownerName);
		appendText(buf, founders);
		appendText(buf, contactEmail);
		appendText(buf, briefAddress);
		appendText(buf, answer1);
		appendText(buf, answer2);
		appendText(buf, answer3);
		appendText(buf, answer4);
		appendText(buf, answer5);
		appendText(buf, answer6);
		appendText(buf, answer7);
		appendText(buf, answer8);
		appendText(buf, answer9);
		appendText(buf, answer10);
		appendText(buf, answer11);
		appendText(buf, answer12);
		appendText(buf, answer13);
		appendText(buf, answer14);
		appendText(buf, answer15);
		appendText(buf, answer16);
		appendText(buf, answer17);
		appendText(buf, answer18);
		appendText(buf, answer19);
		appendText(buf, answer20);
		appendText(buf, answer21);
		appendText(buf, answer22);
		appendText(buf, answer23);
		appendText(buf, answer24);
		appendText(buf, answer25);
		appendText(buf, answer26);
		return buf.toString();
	}
	
	private void appendText(StringBuffer buf, String value) {
		if (value != null) {
			buf.append(value).append("\n");
		}
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

    public String getAnswer12() {
		return answer12;
	}

	public void setAnswer12(String answer12) {
		this.answer12 = answer12;
	}

    public String getAnswer13() {
		return answer13;
	}

	public void setAnswer13(String answer13) {
		this.answer13 = answer13;
	}

	public String getAnswer14() {
		return answer14;
	}

	public void setAnswer14(String answer14) {
		this.answer14 = answer14;
	}

	public String getAnswer15() {
		return answer15;
	}

	public void setAnswer15(String answer15) {
		this.answer15 = answer15;
	}

	public String getAnswer16() {
		return answer16;
	}

	public void setAnswer16(String answer16) {
		this.answer16 = answer16;
	}

	public String getAnswer17() {
		return answer17;
	}

	public void setAnswer17(String answer17) {
		this.answer17 = answer17;
	}

	public String getAnswer18() {
		return answer18;
	}

	public void setAnswer18(String answer18) {
		this.answer18 = answer18;
	}

	public String getAnswer19() {
		return answer19;
	}

	public void setAnswer19(String answer19) {
		this.answer19 = answer19;
	}

	public String getAnswer20() {
		return answer20;
	}

	public void setAnswer20(String answer20) {
		this.answer20 = answer20;
	}

	public String getAnswer21() {
		return answer21;
	}

	public void setAnswer21(String answer21) {
		this.answer21 = answer21;
	}

	public String getAnswer22() {
		return answer22;
	}

	public void setAnswer22(String answer22) {
		this.answer22 = answer22;
	}

	public String getAnswer23() {
		return answer23;
	}

	public void setAnswer23(String answer23) {
		this.answer23 = answer23;
	}

	public String getAnswer24() {
		return answer24;
	}

	public void setAnswer24(String answer24) {
		this.answer24 = answer24;
	}

	public String getAnswer25() {
		return answer25;
	}

	public void setAnswer25(String answer25) {
		this.answer25 = answer25;
	}

	public String getAnswer26() {
		return answer26;
	}

	public void setAnswer26(String answer26) {
		this.answer26 = answer26;
	}

	public boolean isAskedForFunding() {
		return askedForFunding;
	}

	public void setAskedForFunding(boolean askedForFunding) {
		this.askedForFunding = askedForFunding;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getBuinessPlanUpload() {
		return buinessPlanUpload;
	}

	public void setBuinessPlanUpload(String buinessPlanUpload) {
		this.buinessPlanUpload = buinessPlanUpload;
	}

	public String getPresentationUpload() {
		return presentationUpload;
	}

	public void setPresentationUpload(String presentationUpload) {
		this.presentationUpload = presentationUpload;
	}

	public String getFinancialsUpload() {
		return financialsUpload;
	}

	public void setFinancialsUpload(String financialsUpload) {
		this.financialsUpload = financialsUpload;
	}

	public String getLogoUpload() {
		return logoUpload;
	}

	public void setLogoUpload(String logoUpload) {
		this.logoUpload = logoUpload;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getBriefAddress() {
		return briefAddress;
	}

	public void setBriefAddress(String briefAddress) {
		this.briefAddress = briefAddress;
	}

	public String getFounders() {
		return founders;
	}

	public void setFounders(String founders) {
		this.founders = founders;
	}
}
