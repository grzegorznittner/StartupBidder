package com.startupbidder.dto;

import com.google.appengine.api.datastore.Entity;

/**
 * Object as it is used to store aggregated data about user.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
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
	public static final String NUM_OF_ACCEPTED_BIDS = "numberOfAcceptedBids";
	private long numberOfAcceptedBids;
	public static final String NUM_OF_FUNDED_BIDS = "numberOfFundedBids";
	private long numberOfFundedBids;
	public static final String NUM_OF_LISTINGS = "numberOfListings";
	private long numberOfListings;
	public static final String NUM_OF_VOTES = "numberOfVotes";
	private long numberOfVotes;
	public static final String NUM_OF_VOTES_ADDED = "numberOfVotesAdded";
	private long numberOfVotesAdded;
	public static final String SUM_OF_BIDS = "sumOfBids";
	private long sumOfBids;
	public static final String SUM_OF_ACCEPTED_BIDS = "sumOfAcceptedBids";
	private long sumOfAcceptedBids;
	public static final String SUM_OF_FUNDED_BIDS = "sumOfFundedBids";
	private long sumOfFundedBids;
	
	public UserStatisticsDTO() {
	}
	
	@Override
	String getKind() {
		return "UserStat";
	}

	@Override
	public Entity toEntity() {
		Entity entity = new Entity(id);
		entity.setProperty(MOCK_DATA, (Boolean)this.mockData);
		entity.setProperty(USER, this.user);
		entity.setProperty(STATUS, this.status);
		entity.setProperty(NUM_OF_COMMENTS, this.numberOfComments);
		entity.setProperty(NUM_OF_BIDS, this.numberOfBids);
		entity.setProperty(NUM_OF_LISTINGS, this.numberOfListings);
		entity.setProperty(NUM_OF_VOTES, this.numberOfVotes);
		entity.setProperty(NUM_OF_VOTES_ADDED, this.numberOfVotesAdded);
		entity.setProperty(SUM_OF_BIDS, this.sumOfBids);
		entity.setProperty(SUM_OF_ACCEPTED_BIDS, this.sumOfAcceptedBids);
		entity.setProperty(SUM_OF_FUNDED_BIDS, this.sumOfFundedBids);
		
		return entity;
	}
	
	public static UserStatisticsDTO fromEntity(Entity entity) {
		UserStatisticsDTO dto = new UserStatisticsDTO();
		dto.setKey(entity.getKey());
		if (entity.hasProperty(MOCK_DATA)) {
			dto.setMockData((Boolean)entity.getProperty(MOCK_DATA));
		}
		dto.user = (String)entity.getProperty(USER);
		dto.status = (String)entity.getProperty(STATUS);
		dto.numberOfComments = toLong(entity.getProperty(NUM_OF_COMMENTS));
		dto.numberOfBids = toLong(entity.getProperty(NUM_OF_BIDS));
		dto.numberOfListings = toLong(entity.getProperty(NUM_OF_LISTINGS));
		dto.numberOfVotes = toLong(entity.getProperty(NUM_OF_VOTES));
		dto.numberOfVotesAdded = toLong(entity.getProperty(NUM_OF_VOTES_ADDED));
		dto.sumOfBids = toLong(entity.getProperty(SUM_OF_BIDS));
		dto.sumOfAcceptedBids = toLong(entity.getProperty(SUM_OF_ACCEPTED_BIDS));
		dto.sumOfFundedBids = toLong(entity.getProperty(SUM_OF_FUNDED_BIDS));

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

	public long getSumOfAcceptedBids() {
		return sumOfAcceptedBids;
	}

	public void setSumOfAcceptedBids(long sumOfAcceptedBids) {
		this.sumOfAcceptedBids = sumOfAcceptedBids;
	}

	public long getSumOfFundedBids() {
		return sumOfFundedBids;
	}

	public void setSumOfFundedBids(long sumOfFundedBids) {
		this.sumOfFundedBids = sumOfFundedBids;
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

	public long getNumberOfVotesAdded() {
		return numberOfVotesAdded;
	}

	public void setNumberOfVotesAdded(long numberOfVotesAdded) {
		this.numberOfVotesAdded = numberOfVotesAdded;
	}

	public long getNumberOfAcceptedBids() {
		return numberOfAcceptedBids;
	}

	public void setNumberOfAcceptedBids(long numberOfAcceptedBids) {
		this.numberOfAcceptedBids = numberOfAcceptedBids;
	}

	public long getNumberOfFundedBids() {
		return numberOfFundedBids;
	}

	public void setNumberOfFundedBids(long numberOfFundedBids) {
		this.numberOfFundedBids = numberOfFundedBids;
	}

	@Override
	public String toString() {
		return "UserStatisticsDTO [user=" + user + ", status=" + status
				+ ", numberOfComments=" + numberOfComments + ", numberOfBids="
				+ numberOfBids + ", numberOfAcceptedBids="
				+ numberOfAcceptedBids + ", numberOfFundedBids="
				+ numberOfFundedBids + ", numberOfListings=" + numberOfListings
				+ ", numberOfVotes=" + numberOfVotes
				+ ", numberOfVotesReceived=" + numberOfVotesAdded
				+ ", sumOfBids=" + sumOfBids + ", sumOfAcceptedBids="
				+ sumOfAcceptedBids + ", sumOfPayedBids=" + sumOfFundedBids
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ (int) (numberOfAcceptedBids ^ (numberOfAcceptedBids >>> 32));
		result = prime * result + (int) (numberOfBids ^ (numberOfBids >>> 32));
		result = prime * result
				+ (int) (numberOfComments ^ (numberOfComments >>> 32));
		result = prime * result
				+ (int) (numberOfListings ^ (numberOfListings >>> 32));
		result = prime * result
				+ (int) (numberOfFundedBids ^ (numberOfFundedBids >>> 32));
		result = prime * result
				+ (int) (numberOfVotes ^ (numberOfVotes >>> 32));
		result = prime
				* result
				+ (int) (numberOfVotesAdded ^ (numberOfVotesAdded >>> 32));
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ (int) (sumOfAcceptedBids ^ (sumOfAcceptedBids >>> 32));
		result = prime * result + (int) (sumOfBids ^ (sumOfBids >>> 32));
		result = prime * result
				+ (int) (sumOfFundedBids ^ (sumOfFundedBids >>> 32));
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
		if (numberOfAcceptedBids != other.numberOfAcceptedBids)
			return false;
		if (numberOfBids != other.numberOfBids)
			return false;
		if (numberOfComments != other.numberOfComments)
			return false;
		if (numberOfListings != other.numberOfListings)
			return false;
		if (numberOfFundedBids != other.numberOfFundedBids)
			return false;
		if (numberOfVotes != other.numberOfVotes)
			return false;
		if (numberOfVotesAdded != other.numberOfVotesAdded)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (sumOfAcceptedBids != other.sumOfAcceptedBids)
			return false;
		if (sumOfBids != other.sumOfBids)
			return false;
		if (sumOfFundedBids != other.sumOfFundedBids)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
