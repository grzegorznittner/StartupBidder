package com.startupbidder.vo;

import java.util.Date;

public class ListingVO {
	private String id;
	private String name;
	private int   startingValuation;
	private Date  startingValuationDate;
	private int   averageValuation;
	private int   medianValuation;
	private Date  listedOn;
	private Date  closingOn;
	private String state;
	private String summary;
	private String owner;
	private int numberOfComments;
	private int numberOfBids;
	private int rating;
	
	public ListingVO() {
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStartingValuation() {
		return startingValuation;
	}

	public void setStartingValuation(int startingValuation) {
		this.startingValuation = startingValuation;
	}

	public Date getStartingValuationDate() {
		return startingValuationDate;
	}

	public void setStartingValuationDate(Date startingValuationDate) {
		this.startingValuationDate = startingValuationDate;
	}

	public int getAverageValuation() {
		return averageValuation;
	}

	public void setAverageValuation(int averageValuation) {
		this.averageValuation = averageValuation;
	}

	public int getMedianValuation() {
		return medianValuation;
	}

	public void setMedianValuation(int medianValuation) {
		this.medianValuation = medianValuation;
	}

	public Date getClosingOn() {
		return closingOn;
	}

	public void setClosingOn(Date closingOn) {
		this.closingOn = closingOn;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getListedOn() {
		return listedOn;
	}

	public void setListedOn(Date listedOn) {
		this.listedOn = listedOn;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
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

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "ListingVO [id=" + id + ", name=" + name
				+ ", startingValuation=" + startingValuation
				+ ", startingValuationDate=" + startingValuationDate
				+ ", averageValuation=" + averageValuation
				+ ", medianValuation=" + medianValuation + ", listedOn="
				+ listedOn + ", closingOn=" + closingOn + ", state=" + state
				+ ", summary=" + summary + ", owner=" + owner
				+ ", numberOfComments=" + numberOfComments + ", numberOfBids="
				+ numberOfBids + ", rating=" + rating + "]";
	}

}
