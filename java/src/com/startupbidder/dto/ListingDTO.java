package com.startupbidder.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

public class ListingDTO extends AbstractDTO implements Serializable {

	public enum State {NEW, CREATED, ACTIVE, CLOSED, WITHDRAWN};
	
	public static final String NAME = "name";
	private String name;
	public static final String SUGGESTED_VALUATION = "suggestedValuation";
	private int   suggestedValuation;
	public static final String SUGGESTED_PERCENTAGE = "suggestedPercentage";
	private int   suggestedPercentage;
	public static final String SUGGESTED_AMOUNT = "suggestedAmount";
	private int   suggestedAmount;
	public static final String LISTED_ON = "listedOn";
	private Date  listedOn;
	public static final String CLOSING_ON = "closingOn";
	private Date  closingOn;
	public static final String STATE = "state";
	private State state;
	public static final String SUMMARY = "summary";
	private String summary;
	public static final String OWNER = "owner";
	private String owner;
	public static final String BUSINESS_PLAN_ID = "businessPlanId";
	private String businessPlanId;
	public static final String PRESENTATION_ID = "presentationId";
	private String presentationId;
	public static final String FINANCIALS_ID = "financialsId";
	private String financialsId;
	
	public ListingDTO() {
	}

	public String getKind() {
		return "Listing";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSuggestedPercentage() {
		return suggestedPercentage;
	}

	public void setSuggestedPercentage(int suggestedPercentage) {
		this.suggestedPercentage = suggestedPercentage;
	}

	public int getSuggestedAmount() {
		return suggestedAmount;
	}

	public void setSuggestedAmount(int suggestedAmount) {
		this.suggestedAmount = suggestedAmount;
	}

	public int getSuggestedValuation() {
		return suggestedValuation;
	}

	public void setSuggestedValuation(int suggestedValuation) {
		this.suggestedValuation = suggestedValuation;
	}

	public Date getClosingOn() {
		return closingOn;
	}

	public void setClosingOn(Date closingOn) {
		this.closingOn = closingOn;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
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

	public String getBusinessPlanId() {
		return businessPlanId;
	}

	public void setBusinessPlanId(String businessPlanId) {
		this.businessPlanId = businessPlanId;
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

	@Override
	public Entity toEntity() {
		Entity listing = new Entity(id);
		listing.setProperty(CLOSING_ON, this.closingOn);
		listing.setProperty(LISTED_ON, this.listedOn);
		listing.setProperty(NAME, this.name);
		listing.setProperty(OWNER, this.owner);
		listing.setProperty(STATE, this.state != null ? this.state.toString() : null);
		listing.setProperty(SUGGESTED_AMOUNT, this.suggestedAmount);
		listing.setProperty(SUGGESTED_PERCENTAGE, this.suggestedPercentage);
		listing.setProperty(SUGGESTED_VALUATION, this.suggestedValuation);
		listing.setProperty(PRESENTATION_ID, this.presentationId);
		listing.setProperty(BUSINESS_PLAN_ID, this.businessPlanId);
		listing.setProperty(FINANCIALS_ID, this.financialsId);
		listing.setUnindexedProperty(SUMMARY, this.summary != null ? new Text(this.summary) : null);
		return listing;
	}

	public static ListingDTO fromEntity(Entity entity) {
		ListingDTO listing = new ListingDTO();
		listing.setKey(entity.getKey());
		listing.closingOn = (Date)entity.getProperty(CLOSING_ON);
		listing.listedOn = (Date)entity.getProperty(LISTED_ON);
		listing.name = (String)entity.getProperty(NAME);
		listing.owner = (String)entity.getProperty(OWNER);
		if (!StringUtils.isEmpty((String)entity.getProperty(STATE))) {
			listing.state = ListingDTO.State.valueOf((String)entity.getProperty(STATE));
		}
		listing.suggestedAmount = ((Long)entity.getProperty(SUGGESTED_AMOUNT)).intValue();
		listing.suggestedPercentage = ((Long)entity.getProperty(SUGGESTED_PERCENTAGE)).intValue();
		listing.suggestedValuation = ((Long)entity.getProperty(SUGGESTED_VALUATION)).intValue();
		Text summaryText = (Text)entity.getProperty(SUMMARY);
		listing.summary = summaryText != null ? summaryText.getValue() : null;
		listing.presentationId = (String)entity.getProperty(PRESENTATION_ID);
		listing.businessPlanId = (String)entity.getProperty(BUSINESS_PLAN_ID);
		listing.financialsId = (String)entity.getProperty(FINANCIALS_ID);
		return listing;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((businessPlanId == null) ? 0 : businessPlanId.hashCode());
		result = prime * result
				+ ((closingOn == null) ? 0 : closingOn.hashCode());
		result = prime * result
				+ ((financialsId == null) ? 0 : financialsId.hashCode());
		result = prime * result
				+ ((listedOn == null) ? 0 : listedOn.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result
				+ ((presentationId == null) ? 0 : presentationId.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + suggestedAmount;
		result = prime * result + suggestedPercentage;
		result = prime * result + suggestedValuation;
		result = prime * result + ((summary == null) ? 0 : summary.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ListingDTO other = (ListingDTO) obj;
		if (businessPlanId == null) {
			if (other.businessPlanId != null)
				return false;
		} else if (!businessPlanId.equals(other.businessPlanId))
			return false;
		if (closingOn == null) {
			if (other.closingOn != null)
				return false;
		} else if (!closingOn.equals(other.closingOn))
			return false;
		if (financialsId == null) {
			if (other.financialsId != null)
				return false;
		} else if (!financialsId.equals(other.financialsId))
			return false;
		if (listedOn == null) {
			if (other.listedOn != null)
				return false;
		} else if (!listedOn.equals(other.listedOn))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (presentationId == null) {
			if (other.presentationId != null)
				return false;
		} else if (!presentationId.equals(other.presentationId))
			return false;
		if (state != other.state)
			return false;
		if (suggestedAmount != other.suggestedAmount)
			return false;
		if (suggestedPercentage != other.suggestedPercentage)
			return false;
		if (suggestedValuation != other.suggestedValuation)
			return false;
		if (summary == null) {
			if (other.summary != null)
				return false;
		} else if (!summary.equals(other.summary))
			return false;
		return true;
	}

}
