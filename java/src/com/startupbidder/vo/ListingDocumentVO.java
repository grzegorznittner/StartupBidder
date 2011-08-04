package com.startupbidder.vo;

import java.util.Date;

import com.google.appengine.api.blobstore.BlobKey;

public class ListingDocumentVO {
	private String id;
	private String listing;
	private BlobKey blob;
	private Date created;
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
