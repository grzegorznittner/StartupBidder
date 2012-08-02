package com.startupbidder.vo;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class ImportQueryResultsVO extends BaseResultVO {
	@JsonProperty("query_results")
	private Map<String, String> queryResults;

	public Map<String, String> getQueryResults() {
		return queryResults;
	}

	public void setQueryResults(Map<String, String> queryResults) {
		this.queryResults = queryResults;
	}
}
