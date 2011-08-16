package com.startupbidder.dto;

import com.google.appengine.api.datastore.Entity;

/**
 * Object as it is used to store aggregated data about user.
 * 
 * @author greg
 */
public class UserStatisticsDTO extends AbstractDTO {
	public static final String NUM_OF_COMMENTS = "numberOfComments";
	private int numberOfComments;
	public static final String NUM_OF_BIDS = "numberOfBids";
	private int numberOfBids;
	public static final String NUM_OF_LISTINGS = "numberOfListings";
	private int numberOfListings;
	public static final String NUM_OF_VOTES = "numberOfVotes";
	private int numberOfVotes;
	
	public UserStatisticsDTO() {
	}
	
	@Override
	String getKind() {
		return "UserStat";
	}

	@Override
	public Entity toEntity() {
		Entity entity = new Entity(id);
		entity.setProperty(NUM_OF_COMMENTS, this.numberOfComments);
		entity.setProperty(NUM_OF_BIDS, this.numberOfBids);
		entity.setProperty(NUM_OF_LISTINGS, this.numberOfListings);
		entity.setProperty(NUM_OF_VOTES, this.numberOfVotes);
		
		return entity;
	}
	
	public static UserStatisticsDTO fromEntity(Entity entity) {
		UserStatisticsDTO dto = new UserStatisticsDTO();
		dto.setKey(entity.getKey());
		dto.numberOfComments = (Integer)entity.getProperty(NUM_OF_COMMENTS);
		dto.numberOfBids = (Integer)entity.getProperty(NUM_OF_BIDS);
		dto.numberOfListings = (Integer)entity.getProperty(NUM_OF_LISTINGS);
		dto.numberOfVotes = (Integer)entity.getProperty(NUM_OF_VOTES);

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

	@Override
	public String toString() {
		return "UserStatistics [numberOfComments=" + numberOfComments
				+ ", numberOfBids=" + numberOfBids + ", numberOfListings="
				+ numberOfListings + ", numberOfVotes=" + numberOfVotes + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserStatisticsDTO other = (UserStatisticsDTO) obj;
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
