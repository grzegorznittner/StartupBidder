package com.startupbidder.vo;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.google.appengine.api.blobstore.BlobKey;

@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class ListingDocumentVO {
	@JsonProperty("id")
	private String id;
	private String listing;
	@JsonProperty("blob_id")
	private BlobKey blob;
	@JsonProperty("created")
	private Date created;
	@JsonProperty("type")
	private String type;
	private String state;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getListing() {
		return listing;
	}
	public void setListing(String listing) {
		this.listing = listing;
	}
	public BlobKey getBlob() {
		return blob;
	}
	public void setBlob(BlobKey blob) {
		this.blob = blob;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	@Override
	public String toString() {
		return "ListingDocumentVO [listing=" + listing + ", blob=" + blob
				+ ", created=" + created + ", type=" + type + ", state="
				+ state + "]";
	}
}
