package com.startupbidder.dto;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;

public class ListingMedianValuationDTO extends AbstractDTO {
	private float medianValuation;
	private String listing;
	private Date date;

	@Override
	String getKind() {
		return "MedianVal";
	}
	public float getMedianValuation() {
		return medianValuation;
	}
	public void setMedianValuation(float medianValuation) {
		this.medianValuation = medianValuation;
	}
	public String getListing() {
		return listing;
	}
	public void setListing(String listing) {
		this.listing = listing;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	@Override
	public Entity toEntity() {
		Entity entity = new Entity(id);
		entity.setProperty("date", this.date);
		entity.setProperty("listing", this.listing);
		entity.setProperty("medianValuation", this.medianValuation);		
		return entity;
	}
	public static ListingMedianValuationDTO fromEntity(Entity entity) {
		ListingMedianValuationDTO dto = new ListingMedianValuationDTO();
		dto.setKey(entity.getKey());
		dto.date = (Date)entity.getProperty("date");
		dto.listing = (String)entity.getProperty("listing");
		dto.medianValuation = (Float)entity.getProperty("medianValuation");
		return dto;
	}
}
