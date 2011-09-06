package com.startupbidder.dto;

import java.io.Serializable;
import java.util.Date;

import com.google.appengine.api.datastore.Entity;

/**
 * Object as it is used to store aggregated data about user.
 * 
 * @author greg
 */
@SuppressWarnings("serial")
public class ListingStatisticsDTO extends AbstractDTO implements Serializable {
	public static final String LISTING = "listing";
	private String listing;
	public static final String MEDIAN_VALUATION = "medianValuation";
	private float medianValuation;
	public static final String NUM_OF_COMMENTS = "numberOfComments";
	private int numberOfComments;
	public static final String NUM_OF_BIDS = "numberOfBids";
	private int numberOfBids;
	public static final String NUM_OF_LISTINGS = "numberOfListings";
	private int numberOfListings;
	public static final String NUM_OF_VOTES = "numberOfVotes";
	private int numberOfVotes;
	public static final String DATE = "date";
	private Date date;
	
	public ListingStatisticsDTO() {
	}
	
	@Override
	String getKind() {
		return "ListingStat";
	}

	@Override
	public Entity toEntity() {
		Entity entity = new Entity(id);
		entity.setProperty(NUM_OF_COMMENTS, this.numberOfComments);
		entity.setProperty(NUM_OF_BIDS, this.numberOfBids);
		entity.setProperty(NUM_OF_LISTINGS, this.numberOfListings);
		entity.setProperty(NUM_OF_VOTES, this.numberOfVotes);
		entity.setProperty(DATE, this.date);
		entity.setProperty(LISTING, this.listing);
		entity.setProperty(MEDIAN_VALUATION, this.medianValuation);
		
		return entity;
	}
	
	public static ListingStatisticsDTO fromEntity(Entity entity) {
		ListingStatisticsDTO dto = new ListingStatisticsDTO();
		dto.setKey(entity.getKey());
		dto.numberOfComments = (Integer)entity.getProperty(NUM_OF_COMMENTS);
		dto.numberOfBids = (Integer)entity.getProperty(NUM_OF_BIDS);
		dto.numberOfListings = (Integer)entity.getProperty(NUM_OF_LISTINGS);
		dto.numberOfVotes = (Integer)entity.getProperty(NUM_OF_VOTES);
		dto.date = (Date)entity.getProperty(DATE);
		dto.listing = (String)entity.getProperty(LISTING);
		dto.medianValuation = (Float)entity.getProperty(MEDIAN_VALUATION);

		return dto;
	}

	public int getNumberOfComments() {
		return numberOfComments;
	}

	public void setNumberOfComments(int numberOfComments) {
		this.numberOfComments = numberOfComments;
	}

	public int getNumberOfBids() {
		return numberOfBids;
	}

	public void setNumberOfBids(int numberOfBids) {
		this.numberOfBids = numberOfBids;
	}

	public int getNumberOfListings() {
		return numberOfListings;
	}

	public void setNumberOfListings(int numberOfListings) {
		this.numberOfListings = numberOfListings;
	}

	public int getNumberOfVotes() {
		return numberOfVotes;
	}

	public void setNumberOfVotes(int numberOfVotes) {
		this.numberOfVotes = numberOfVotes;
	}

	public String getListing() {
		return listing;
	}

	public void setListing(String listing) {
		this.listing = listing;
	}

	public float getMedianValuation() {
		return medianValuation;
	}

	public void setMedianValuation(float medianValuation) {
		this.medianValuation = medianValuation;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "ListingStatisticsDTO [listing=" + listing
				+ ", medianValuation=" + medianValuation
				+ ", numberOfComments=" + numberOfComments + ", numberOfBids="
				+ numberOfBids + ", numberOfListings=" + numberOfListings
				+ ", numberOfVotes=" + numberOfVotes + ", date=" + date + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((listing == null) ? 0 : listing.hashCode());
		result = prime * result + Float.floatToIntBits(medianValuation);
		result = prime * result + numberOfBids;
		result = prime * result + numberOfComments;
		result = prime * result + numberOfListings;
		result = prime * result + numberOfVotes;
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
		ListingStatisticsDTO other = (ListingStatisticsDTO) obj;
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
		if (Float.floatToIntBits(medianValuation) != Float
				.floatToIntBits(other.medianValuation))
			return false;
		if (numberOfBids != other.numberOfBids)
			return false;
		if (numberOfComments != other.numberOfComments)
			return false;
		if (numberOfListings != other.numberOfListings)
			return false;
		if (numberOfVotes != other.numberOfVotes)
			return false;
		return true;
	}

}
