package com.startupbidder.vo;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * Contains properties which describes various list results.
 * It's also used to pass parameters to the data layer.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class ListPropertiesVO {
	@JsonProperty("start_index") private int startIndex = 1;
	@JsonProperty("max_results") private int maxResults;
	private int totalResults;
	@JsonProperty("num_results") private int numberOfResults;
	private String prevCursor;
	private String nextCursor;
	@JsonProperty("more_results_url") private String moreResultsUrl;
	private String requestPathInfo;
	private Map<String, String> parameters = new HashMap<String, String>();
	
	public void updateMoreResultsUrl() {
		StringBuffer url = new StringBuffer();
		url.append(requestPathInfo).append("?");
		for (Map.Entry<String, String> param : parameters.entrySet()) {
			if ("next_cursor".equals(param.getKey()) || "start_index".equals(param.getKey())) {
				continue;
			}
			url.append(param.getKey()).append("=").append(param.getValue()).append("&");
		}
		url.append("next_cursor=").append(nextCursor).append("&");
		url.append("start_index=").append(startIndex + numberOfResults);
		moreResultsUrl = url.toString();
	}
	public void setRequestData(HttpServletRequest request) {
		this.requestPathInfo = request.getPathInfo();
		@SuppressWarnings("rawtypes")
		Map paramMap = request.getParameterMap();
		for (Object paramName : paramMap.keySet()) {
			String values[] = (String[])paramMap.get(paramName);
			parameters.put((String)paramName, values[0]);
		}
	}
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
	public String getMoreResultsUrl() {
		return moreResultsUrl;
	}
}
