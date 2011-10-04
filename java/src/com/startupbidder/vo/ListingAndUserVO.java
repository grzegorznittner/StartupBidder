package com.startupbidder.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class ListingAndUserVO extends BaseResultVO {
	@JsonProperty("listing")
	private ListingVO listing;

	public ListingVO getListing() {
		return listing;
	}
	public void setListing(ListingVO listing) {
		this.listing = listing;
	}
	@Override
	public String toString() {
		return "ListingAndUserVO [listing=" + listing + super.toString() + "]";
	}
}
