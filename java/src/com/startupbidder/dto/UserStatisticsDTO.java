package com.startupbidder.dto;

import com.google.appengine.api.datastore.Entity;

/**
 * Object as it is used to store aggregated data about user.
 * 
 * @author greg
 */
@SuppressWarnings("serial")
public class UserStatisticsDTO extends AbstractDTO {
	public static final String USER = "user";
	private String user;
	public static final String STATUS = "status";
	private String status;
	public static final String NUM_OF_COMMENTS = "numberOfComments";
	private long numberOfComments;
	public static final String NUM_OF_BIDS = "numberOfBids";
	private long numberOfBids;
	public static final String NUM_OF_LISTINGS = "numberOfListings";
	private long numberOfListings;
	public static final String NUM_OF_VOTES = "numberOfVotes";
	private long numberOfVotes;
	public static final String NUM_OF_VOTES_RECEIVED = "numberOfVotesReceived";
	private long numberOfVotesReceived;
	public static final String SUM_OF_BIDS = "sumOfBids";
	private long sumOfBids;
	
	public UserStatisticsDTO() {
	}
	
	@Override
	String getKind() {
		return "UserStat";
	}

	@Override
	public Entity toEntity() {
		Entity entity = new Entity(id);
		entity.setProperty(USER, this.user);
		entity.setProperty(STATUS, this.status);
		entity.setProperty(NUM_OF_COMMENTS, this.numberOfComments);
		entity.setProperty(NUM_OF_BIDS, this.numberOfBids);
		entity.setProperty(NUM_OF_LISTINGS, this.numberOfListings);
		entity.setProperty(NUM_OF_VOTES, this.numberOfVotes);
		entity.setProperty(NUM_OF_VOTES_RECEIVED, this.numberOfVotesReceived);
		entity.setProperty(SUM_OF_BIDS, this.sumOfBids);
		
		return entity;
	}
	
	public static UserStatisticsDTO fromEntity(Entity entity) {
		UserStatisticsDTO dto = new UserStatisticsDTO();
		dto.setKey(entity.getKey());
		dto.user = (String)entity.getProperty(USER);
		dto.status = (String)entity.getProperty(STATUS);
		dto.numberOfComments = toLong(entity.getProperty(NUM_OF_COMMENTS));
		dto.numberOfBids = toLong(entity.getProperty(NUM_OF_BIDS));
		dto.numberOfListings = toLong(entity.getProperty(NUM_OF_LISTINGS));
		dto.numberOfVotes = toLong(entity.getProperty(NUM_OF_VOTES));
		dto.numberOfVotesReceived = toLong(entity.getProperty(NUM_OF_VOTES_RECEIVED));
		dto.sumOfBids = toLong(entity.getProperty(SUM_OF_BIDS));

		return dto;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public long getNumberOfListings() {
		return numberOfListings;
	}

	public void setNumberOfListings(long numberOfListings) {
		this.numberOfListings = numberOfListings;
	}

	public long getNumberOfVotes() {
		return numberOfVotes;
	}

	public void setNumberOfVotes(long numberOfVotes) {
		this.numberOfVotes = numberOfVotes;
	}

	public long getSumOfBids() {
		return sumOfBids;
	}

	public void setSumOfBids(long sumOfBids) {
		this.sumOfBids = sumOfBids;
	}

	public long getNumberOfVotesReceived() {
		return numberOfVotesReceived;
	}

	public void setNumberOfVotesReceived(long numberOfVotesReceived) {
		this.numberOfVotesReceived = numberOfVotesReceived;
	}

	@Override
	public String toString() {
		return "UserStatisticsDTO [id=" + getIdAsString() + ", user=" + user + ", status=" + status
				+ ", numberOfComments=" + numberOfComments + ", numberOfBids="
				+ numberOfBids + ", numberOfListings=" + numberOfListings
				+ ", numberOfVotes=" + numberOfVotes
				+ ", numberOfVotesReceived=" + numberOfVotesReceived
				+ ", sumOfBids=" + sumOfBids + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (numberOfBids ^ (numberOfBids >>> 32));
		result = prime * result
				+ (int) (numberOfComments ^ (numberOfComments >>> 32));
		result = prime * result
				+ (int) (numberOfListings ^ (numberOfListings >>> 32));
		result = prime * result
				+ (int) (numberOfVotes ^ (numberOfVotes >>> 32));
		result = prime
				* result
				+ (int) (numberOfVotesReceived ^ (numberOfVotesReceived >>> 32));
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + (int) (sumOfBids ^ (sumOfBids >>> 32));
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		UserStatisticsDTO other = (UserStatisticsDTO) obj;
		if (numberOfBids != other.numberOfBids)
			return false;
		if (numberOfComments != other.numberOfComments)
			return false;
		if (numberOfListings != other.numberOfListings)
			return false;
		if (numberOfVotes != other.numberOfVotes)
			return false;
		if (numberOfVotesReceived != other.numberOfVotesReceived)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (sumOfBids != other.sumOfBids)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
