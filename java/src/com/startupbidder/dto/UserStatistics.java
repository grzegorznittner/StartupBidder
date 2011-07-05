package com.startupbidder.dto;

/**
 * This is not classic Data Transer Object as it is used only to carry
 * aggregated data about user.
 * 
 * @author greg
 */
public class UserStatistics {
	int numberOfComments;
	int numberOfBids;
	int numberOfListings;
	
	public UserStatistics() {
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

	@Override
	public String toString() {
		return "UserStatistics [numberOfComments=" + numberOfComments
				+ ", numberOfBids=" + numberOfBids + ", numberOfListings="
				+ numberOfListings + "]";
	}
}
