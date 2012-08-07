package com.startupbidder.vo;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown=true)
public class ListingTileVO extends BaseVO implements Serializable {	
	private static final long serialVersionUID = 454597852345456L;
	
	@JsonProperty("num") protected int orderNumber;
	@JsonProperty("listing_id")	protected String id;
	@JsonProperty("title") protected String name;
	@JsonProperty("asked_fund") protected boolean askedForFunding;
	@JsonProperty("suggested_amt") protected int suggestedAmount;
	@JsonProperty("suggested_pct") protected int suggestedPercentage;
	@JsonProperty("suggested_val") protected int suggestedValuation;
	@JsonProperty("currency") @JsonSerialize(using=LowecaseSerializer.class) protected String currency;
	@JsonProperty("modified_date") @JsonSerialize(using=DateSerializer.class) protected Date  modified;
	@JsonProperty("created_date") @JsonSerialize(using=DateSerializer.class) protected Date  created;
	@JsonProperty("posted_date") @JsonSerialize(using=DateSerializer.class) protected Date  postedOn;
	@JsonProperty("listing_date") @JsonSerialize(using=DateSerializer.class) protected Date  listedOn;
	@JsonProperty("closing_date") @JsonSerialize(using=DateSerializer.class) protected Date  closingOn;
	@JsonProperty("status")	@JsonSerialize(using=LowecaseSerializer.class) protected String state;
	@JsonProperty("mantra")	protected String mantra;
	@JsonProperty("summary") protected String summary;
	@JsonProperty("website") protected String website;
	@JsonProperty("category") protected String category;
	@JsonProperty("type") @JsonSerialize(using=LowecaseSerializer.class) protected String type;
	@JsonProperty("platform") @JsonSerialize(using=LowecaseSerializer.class) protected String platform;
	@JsonProperty("profile_id") protected String owner;
	@JsonProperty("profile_username") protected String ownerName;
	@JsonProperty("brief_address") protected String briefAddress;
	@JsonProperty("latitude") protected Double latitude;
	@JsonProperty("longitude") protected Double longitude;
	@JsonProperty("mockData") protected boolean mockData;
	@JsonProperty("logo") protected String logo;
	
	public ListingTileVO() {
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

	public boolean isMockData() {
		return mockData;
	}

	public void setMockData(boolean mockData) {
		this.mockData = mockData;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
