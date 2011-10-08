package com.startupbidder.dto;

import java.io.Serializable;
import java.util.Date;

import com.google.appengine.api.datastore.Entity;

/**
 * Object as it is used to store aggregated data about user.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
public class ListingStatisticsDTO extends AbstractDTO implements Serializable {
	public static final String LISTING = "listing";
	private String listing;
	public static final String STATUS = "status";
	private String status;
	public static final String VALUATION = "valuation";
	private double valuation;
	public static final String MEDIAN_VALUATION = "medianValuation";
	private double medianValuation;
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
		entity.setProperty(MOCK_DATA, (Boolean)this.mockData);
		entity.setProperty(NUM_OF_COMMENTS, (Long)this.numberOfComments);
		entity.setProperty(NUM_OF_BIDS, (Long)this.numberOfBids);
		entity.setProperty(NUM_OF_VOTES, (Long)this.numberOfVotes);
		entity.setProperty(DATE, this.date);
		entity.setProperty(LISTING, this.listing);
		entity.setProperty(STATUS, this.status);
		entity.setProperty(VALUATION, (Double)this.valuation);
		entity.setProperty(MEDIAN_VALUATION, (Double)this.medianValuation);
		
		return entity;
	}
	
	public static ListingStatisticsDTO fromEntity(Entity entity) {
		ListingStatisticsDTO dto = new ListingStatisticsDTO();
		dto.setKey(entity.getKey());
		if (entity.hasProperty(MOCK_DATA)) {
			dto.setMockData((Boolean)entity.getProperty(MOCK_DATA));
		}
		dto.numberOfComments = (Long)entity.getProperty(NUM_OF_COMMENTS);
		dto.numberOfBids = (Long)entity.getProperty(NUM_OF_BIDS);
		dto.numberOfVotes = (Long)entity.getProperty(NUM_OF_VOTES);
		dto.date = (Date)entity.getProperty(DATE);
		dto.listing = (String)entity.getProperty(LISTING);
		dto.status = (String)entity.getProperty(STATUS);
		dto.valuation = entity.getProperty(VALUATION) != null ? (Double)entity.getProperty(VALUATION) : 0.0;
		dto.medianValuation = entity.getProperty(MEDIAN_VALUATION) != null ? (Double)entity.getProperty(MEDIAN_VALUATION) : 0.0;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getBidValuation() {
		return medianValuation;
	}

	public void setBidValuation(double bidValuation) {
		this.medianValuation = bidValuation;
	}

	@Override
	public String toString() {
		return "ListingStatisticsDTO [listing=" + listing + ", status="
				+ status + ", valuation=" + valuation + ", medianValuation="
				+ medianValuation + ", numberOfComments=" + numberOfComments
				+ ", numberOfBids=" + numberOfBids + ", numberOfVotes="
				+ numberOfVotes + ", date=" + date + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(medianValuation);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((listing == null) ? 0 : listing.hashCode());
		result = prime * result + (int) (numberOfBids ^ (numberOfBids >>> 32));
		result = prime * result
				+ (int) (numberOfComments ^ (numberOfComments >>> 32));
		result = prime * result
				+ (int) (numberOfVotes ^ (numberOfVotes >>> 32));
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		if (!(obj instanceof ListingStatisticsDTO))
			return false;
		ListingStatisticsDTO other = (ListingStatisticsDTO) obj;
		if (Double.doubleToLongBits(medianValuation) != Double
				.doubleToLongBits(other.medianValuation))
			return false;
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
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (Double.doubleToLongBits(valuation) != Double
				.doubleToLongBits(other.valuation))
			return false;
		return true;
	}

}
