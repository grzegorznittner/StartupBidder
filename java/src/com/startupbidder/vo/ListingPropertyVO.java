package com.startupbidder.vo;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class ListingPropertyVO {
	@JsonProperty("listing_id")	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL) private String listing;
	@JsonProperty("name") @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL) private String propertyName;
	@JsonProperty("value") @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL) private String propertyValue;
	
	@JsonProperty("error_code") private int errorCode = ErrorCodes.OK;
	@JsonProperty("error_msg") @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL) private String errorMessage;
	
	public ListingPropertyVO() {
	}
	
	public ListingPropertyVO(String name, String value) {
		this.propertyName = name;
		this.propertyValue = value;
	}

	public String getListing() {
		return listing;
	}

	public void setListing(String listing) {
		this.listing = listing;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "ListingPropertyVO [listing=" + listing + ", propertyName="
				+ propertyName + ", propertyValue=" + propertyValue
				+ ", errorCode=" + errorCode + ", errorMessage=" + errorMessage
				+ "]";
	}
}
