package com.startupbidder.dto;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.datastore.Entity;

public class ListingDocumentDTO extends AbstractDTO {
	public enum Type {BUSINESS_PLAN, PRESENTATION};
	public enum State {UPLOADED, ACTIVE, DELETED};

	private String listing;
	private String blob;
	private Date created;
	private Type type;
	private State state;

	@Override
	String getKind() {
		return "ListingDoc";
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public String getListing() {
		return listing;
	}
	public void setListing(String listing) {
		this.listing = listing;
	}
	public String getBlob() {
		return blob;
	}
	public void setBlob(String blob) {
		this.blob = blob;
	}
	@Override
	public Entity toEntity() {
		Entity entity = new Entity(id);
		entity.setProperty("blob", this.blob);
		entity.setProperty("listing", this.listing);
		entity.setProperty("state", this.state != null ? this.state.toString() : null);
		entity.setProperty("type", this.type != null ? this.type.toString() : null);
		entity.setProperty("created", this.created);		
		return entity;
	}
	public static ListingDocumentDTO fromEntity(Entity entity) {
		ListingDocumentDTO dto = new ListingDocumentDTO();
		dto.setKey(entity.getKey());
		dto.blob = (String)entity.getProperty("blob");
		dto.listing = (String)entity.getProperty("listing");
		dto.created = (Date)entity.getProperty("created");
		if (!StringUtils.isEmpty((String)entity.getProperty("state"))) {
			dto.state = State.valueOf((String)entity.getProperty("state"));
		}
		if (!StringUtils.isEmpty((String)entity.getProperty("type"))) {
			dto.type = Type.valueOf((String)entity.getProperty("type"));
		}
		return dto;
	}
	@Override
	public String toString() {
		return "ListingDocumentDTO [listing=" + listing + ", blob=" + blob
				+ ", created=" + created + ", type=" + type + ", state="
				+ state + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((blob == null) ? 0 : blob.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((listing == null) ? 0 : listing.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ListingDocumentDTO other = (ListingDocumentDTO) obj;
		if (blob == null) {
			if (other.blob != null)
				return false;
		} else if (!blob.equals(other.blob))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (listing == null) {
			if (other.listing != null)
				return false;
		} else if (!listing.equals(other.listing))
			return false;
		if (state != other.state)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
