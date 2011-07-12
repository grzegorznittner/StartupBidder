package com.startupbidder.vo;

/**
 * Contains properties which describes various list results.
 * It's also used to pass parameters to the data layer.
 * 
 * @author greg
 */
public class ListPropertiesVO {
	private int startIndex;
	private int maxResults;
	private int totalResults;
	private int numberOfResults;
	private String prevCursor;
	private String nextCursor;
	
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getTotalResults() {
		return totalResults;
	}
	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}
	public int getNumberOfResults() {
		return numberOfResults;
	}
	public void setNumberOfResults(int numberOfResults) {
		this.numberOfResults = numberOfResults;
	}
	public String getPrevCursor() {
		return prevCursor;
	}
	public void setPrevCursor(String prevCursor) {
		this.prevCursor = prevCursor;
	}
	public String getNextCursor() {
		return nextCursor;
	}
	public void setNextCursor(String nextCursor) {
		this.nextCursor = nextCursor;
	}
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
	
	@Override
	public String toString() {
		return "ListPropertiesVO [startIndex=" + startIndex + ", maxResults="
				+ maxResults + ", totalResults=" + totalResults
				+ ", numberOfResults=" + numberOfResults + ", prevCursor="
				+ prevCursor + ", nextCursor=" + nextCursor + "]";
	}
}
