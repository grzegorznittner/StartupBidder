package com.startupbidder.dto;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;

public class ListingMedianValuationDTO extends AbstractDTO {
	public static final String MEDIAN_VALUATION = "medianValuation";
	private float medianValuation;
	public static final String LISTING = "listing";
	private String listing;
	public static final String DATE = "date";
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
		entity.setProperty(DATE, this.date);
		entity.setProperty(LISTING, this.listing);
		entity.setProperty(MEDIAN_VALUATION, this.medianValuation);
		return entity;
	}
	public static ListingMedianValuationDTO fromEntity(Entity entity) {
		ListingMedianValuationDTO dto = new ListingMedianValuationDTO();
		dto.setKey(entity.getKey());
		dto.date = (Date)entity.getProperty(DATE);
		dto.listing = (String)entity.getProperty(LISTING);
		dto.medianValuation = (Float)entity.getProperty(MEDIAN_VALUATION);
		return dto;
	}
}
