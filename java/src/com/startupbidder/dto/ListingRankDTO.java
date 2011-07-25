package com.startupbidder.dto;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;

public class ListingRankDTO extends AbstractDTO {
	private float rank;
	private String listing;
	private Date date;

	@Override
	String getKind() {
		return "Rank";
	}
	public float getRank() {
		return rank;
	}
	public void setRank(float rank) {
		this.rank = rank;
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
		entity.setProperty("rank", this.rank);		
		return entity;
	}
	public static ListingRankDTO fromEntity(Entity entity) {
		ListingRankDTO dto = new ListingRankDTO();
		dto.setKey(entity.getKey());
		dto.date = (Date)entity.getProperty("date");
		dto.listing = (String)entity.getProperty("listing");
		dto.rank = (Float)entity.getProperty("rank");
		return dto;
	}
}
