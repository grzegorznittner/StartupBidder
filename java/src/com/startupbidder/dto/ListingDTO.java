package com.startupbidder.dto;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

public class ListingDTO extends AbstractDTO {

	public enum State {NEW, CREATED, ACTIVE, CLOSED, WITHDRAWN};
	
	private String name;
	private int   suggestedValuation;
	private int   suggestedPercentage;
	private int   suggestedAmount;
	private Date  listedOn;
	private Date  closingOn;
	private State state;
	private String summary;
	private String owner;
	private String businessPlanId;
	private String presentationId;
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
		listing.setProperty("closingOn", this.closingOn);
		listing.setProperty("listedOn", this.listedOn);
		listing.setProperty("name", this.name);
		listing.setProperty("owner", this.owner);
		listing.setProperty("state", this.state != null ? this.state.toString() : null);
		listing.setProperty("suggestedAmount", this.suggestedAmount);
		listing.setProperty("suggestedPercentage", this.suggestedPercentage);
		listing.setProperty("suggestedValuation", this.suggestedValuation);
		listing.setProperty("presentationId", this.presentationId);
		listing.setProperty("businessPlanId", this.businessPlanId);
		listing.setProperty("financialsId", this.financialsId);
		listing.setUnindexedProperty("summary", this.summary != null ? new Text(this.summary) : null);
		return listing;
	}

	public static ListingDTO fromEntity(Entity entity) {
		ListingDTO listing = new ListingDTO();
		listing.setKey(entity.getKey());
		listing.closingOn = (Date)entity.getProperty("closingOn");
		listing.listedOn = (Date)entity.getProperty("listedOn");
		listing.name = (String)entity.getProperty("name");
		listing.owner = (String)entity.getProperty("owner");
		if (!StringUtils.isEmpty((String)entity.getProperty("state"))) {
			listing.state = ListingDTO.State.valueOf((String)entity.getProperty("state"));
		}
		listing.suggestedAmount = ((Long)entity.getProperty("suggestedAmount")).intValue();
		listing.suggestedPercentage = ((Long)entity.getProperty("suggestedPercentage")).intValue();
		listing.suggestedValuation = ((Long)entity.getProperty("suggestedValuation")).intValue();
		Text summaryText = (Text)entity.getProperty("summary");
		listing.summary = summaryText != null ? summaryText.getValue() : null;
		listing.presentationId = (String)entity.getProperty("presentationId");
		listing.businessPlanId = (String)entity.getProperty("businessPlanId");
		listing.financialsId = (String)entity.getProperty("financialsId");
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
