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
	public static final String VALUATION = "valuation";
	private double valuation;
	public static final String NUM_OF_COMMENTS = "numberOfComments";
	private long numberOfComments;
	public static final String NUM_OF_BIDS = "numberOfBids";
	private long numberOfBids;
	public static final String NUM_OF_VOTES = "numberOfVotes";
	private long numberOfVotes;
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
		entity.setProperty(NUM_OF_VOTES, this.numberOfVotes);
		entity.setProperty(DATE, this.date);
		entity.setProperty(LISTING, this.listing);
		entity.setProperty(VALUATION, this.valuation);
		
		return entity;
	}
	
	public static ListingStatisticsDTO fromEntity(Entity entity) {
		ListingStatisticsDTO dto = new ListingStatisticsDTO();
		dto.setKey(entity.getKey());
		dto.numberOfComments = (Long)entity.getProperty(NUM_OF_COMMENTS);
		dto.numberOfBids = (Long)entity.getProperty(NUM_OF_BIDS);
		dto.numberOfVotes = (Long)entity.getProperty(NUM_OF_VOTES);
		dto.date = (Date)entity.getProperty(DATE);
		dto.listing = (String)entity.getProperty(LISTING);
		dto.valuation = (Double)entity.getProperty(VALUATION);

		return dto;
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

	public String getListing() {
		return listing;
	}

	public void setListing(String listing) {
		this.listing = listing;
	}

	public double getValuation() {
		return valuation;
	}

	public void setValuation(double valuation) {
		this.valuation = valuation;
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
				+ ", valuation=" + valuation
				+ ", numberOfComments=" + numberOfComments + ", numberOfBids="
				+ numberOfBids + ", numberOfVotes=" + numberOfVotes + ", date=" + date + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((listing == null) ? 0 : listing.hashCode());
		result = prime * result + (int) (numberOfBids ^ (numberOfBids >>> 32));
		result = prime * result
				+ (int) (numberOfComments ^ (numberOfComments >>> 32));
		result = prime * result
				+ (int) (numberOfVotes ^ (numberOfVotes >>> 32));
		long temp;
		temp = Double.doubleToLongBits(valuation);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (numberOfBids != other.numberOfBids)
			return false;
		if (numberOfComments != other.numberOfComments)
			return false;
		if (numberOfVotes != other.numberOfVotes)
			return false;
		if (Double.doubleToLongBits(valuation) != Double
				.doubleToLongBits(other.valuation))
			return false;
		return true;
	}

}
