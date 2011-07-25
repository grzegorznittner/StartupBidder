package com.startupbidder.dto;

import java.util.Date;

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
		entity.setProperty("state", this.state);
		entity.setProperty("type", this.type);		
		entity.setProperty("created", this.created);		
		return entity;
	}
	public static ListingDocumentDTO fromEntity(Entity entity) {
		ListingDocumentDTO dto = new ListingDocumentDTO();
		dto.setKey(entity.getKey());
		dto.blob = (String)entity.getProperty("blob");
		dto.listing = (String)entity.getProperty("listing");
		dto.created = (Date)entity.getProperty("created");
		dto.state = State.valueOf((String)entity.getProperty("state"));
		dto.type = Type.valueOf((String)entity.getProperty("type"));
		return dto;
	}
}
