package com.startupbidder.dto;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Entity;

public class ListingDocumentDTO extends AbstractDTO {
	public enum Type {BUSINESS_PLAN, PRESENTATION, FINANCIALS};

	public static final String BLOB = "blob";
	private BlobKey blob;
	public static final String CREATED = "created";
	private Date created;
	public static final String TYPE = "type";
	private Type type;

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
	public BlobKey getBlob() {
		return blob;
	}
	public void setBlob(BlobKey blob) {
		this.blob = blob;
	}
	@Override
	public Entity toEntity() {
		Entity entity = new Entity(id);
		entity.setProperty(BLOB, this.blob);
		entity.setProperty(TYPE, this.type != null ? this.type.toString() : null);
		entity.setProperty(CREATED, this.created);		
		return entity;
	}
	public static ListingDocumentDTO fromEntity(Entity entity) {
		ListingDocumentDTO dto = new ListingDocumentDTO();
		dto.setKey(entity.getKey());
		dto.blob = (BlobKey)entity.getProperty(BLOB);
		dto.created = (Date)entity.getProperty(CREATED);
		if (!StringUtils.isEmpty((String)entity.getProperty(TYPE))) {
			dto.type = Type.valueOf((String)entity.getProperty(TYPE));
		}
		return dto;
	}
	@Override
	public String toString() {
		return "ListingDocumentDTO [blob=" + blob
				+ ", created=" + created + ", type=" + type + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((blob == null) ? 0 : blob.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
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
		if (type != other.type)
			return false;
		return true;
	}
	
}
