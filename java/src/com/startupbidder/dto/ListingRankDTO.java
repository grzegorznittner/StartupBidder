package com.startupbidder.dto;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;

public class ListingRankDTO extends AbstractDTO {
	public static final String RANK = "rank";
	private float rank;
	public static final String LISTING = "listing";
	private String listing;
	public static final String DATE = "date";
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
		entity.setProperty(DATE, this.date);
		entity.setProperty(LISTING, this.listing);
		entity.setProperty(RANK, this.rank);		
		return entity;
	}
	public static ListingRankDTO fromEntity(Entity entity) {
		ListingRankDTO dto = new ListingRankDTO();
		dto.setKey(entity.getKey());
		dto.date = (Date)entity.getProperty(DATE);
		dto.listing = (String)entity.getProperty(LISTING);
		dto.rank = ((Double)entity.getProperty(RANK)).floatValue();
		return dto;
	}
	@Override
	public String toString() {
		return "ListingRankDTO [rank=" + rank + ", listing=" + listing
				+ ", date=" + date + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((listing == null) ? 0 : listing.hashCode());
		result = prime * result + Float.floatToIntBits(rank);
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
		ListingRankDTO other = (ListingRankDTO) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (listing == null) {
			if (other.listing != null)
				return false;
		} else if (!listing.equals(other.listing))
			return false;
		if (Float.floatToIntBits(rank) != Float.floatToIntBits(other.rank))
			return false;
		return true;
	}
	
}
