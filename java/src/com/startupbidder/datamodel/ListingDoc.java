/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.datamodel;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.PrePersist;

import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*10)
public class ListingDoc extends BaseObject<ListingDoc> {
	public static enum Type {BUSINESS_PLAN, PRESENTATION, FINANCIALS, LOGO, PIC1, PIC2, PIC3, PIC4, PIC5};
	public Key<ListingDoc> getKey() {
		return new Key<ListingDoc>(ListingDoc.class, id);
	}
	@Id public Long id;
	
	public boolean mockData;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	
	public BlobKey blob;
	public Date created;
	public Type type;

	public String getWebKey() {
		return new Key<ListingDoc>(ListingDoc.class, id).getString();
	}
}
