package com.startupbidder.vo;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * Contains properties which describes various list results.
 * It's also used to pass parameters to the data layer.
 * 
 * @author greg
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE,
		setterVisibility=Visibility.NONE, fieldVisibility=Visibility.NONE)
public class ListPropertiesVO {
	@JsonProperty("start_index")
	private int startIndex;
	@JsonProperty("max_results")
	private int maxResults;
	@JsonProperty("total_results")
	private int totalResults;
	@JsonProperty("num_results")
	private int numberOfResults;
	@JsonProperty("prev_cursor")
	private String prevCursor;
	@JsonProperty("next_cursor")
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
