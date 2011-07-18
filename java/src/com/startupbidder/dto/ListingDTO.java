package com.startupbidder.dto;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;

public class ListingDTO extends AbstractDTO {

	public enum State {CREATED, ACTIVE, CLOSED, WITHDRAWN};
	
	private String name;
	private int   suggestedValuation;
	private int   suggestedPercentage;
	private int   suggestedAmount;
	private int   medianValuation;
	private Date  listedOn;
	private Date  closingOn;
	private State state;
	private String summary;
	private String owner;
	
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

	@Override
	public String toString() {
		return "ListingDTO [idAsString" + getIdAsString() + ", name=" + name + ", suggestedValuation="
				+ suggestedValuation + ", suggestedPercentage="
				+ suggestedPercentage + ", suggestedAmount=" + suggestedAmount
				+ ", medianValuation=" + medianValuation + ", listedOn="
				+ listedOn + ", closingOn=" + closingOn + ", state=" + state
				+ ", summary=" + summary + ", owner=" + owner + "]";
	}

	@Override
	public Entity toEntity() {
		Entity listing = new Entity(id);
		listing.setProperty("closingOn", this.closingOn);
		listing.setProperty("listedOn", this.listedOn);
		listing.setProperty("medianValuation", this.medianValuation);
		listing.setProperty("name", this.name);
		listing.setProperty("owner", this.owner);
		listing.setProperty("state", this.state.toString());
		listing.setProperty("suggestedAmount", this.suggestedAmount);
		listing.setProperty("suggestedPercentage", this.suggestedPercentage);
		listing.setProperty("suggestedValuation", this.suggestedValuation);
		listing.setProperty("summary", this.summary);
		return listing;
	}

	public static ListingDTO fromEntity(Entity entity) {
		ListingDTO listing = new ListingDTO();
		listing.setKey(entity.getKey());
		listing.closingOn = (Date)entity.getProperty("closingOn");
		listing.listedOn = (Date)entity.getProperty("listedOn");
		listing.medianValuation = (Integer)entity.getProperty("medianValuation");
		listing.name = (String)entity.getProperty("name");
		listing.owner = (String)entity.getProperty("owner");
		listing.state = ListingDTO.State.valueOf((String)entity.getProperty("state"));
		listing.suggestedAmount = (Integer)entity.getProperty("suggestedAmount");
		listing.suggestedPercentage = (Integer)entity.getProperty("suggestedPercentage");
		listing.suggestedValuation = (Integer)entity.getProperty("suggestedValuation");
		listing.summary = (String)entity.getProperty("summary");
		return listing;
	}
}
