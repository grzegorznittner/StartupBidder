/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.datamodel;

import java.util.Date;

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
public class ListingDoc extends BaseObject {
	public enum Type {BUSINESS_PLAN, PRESENTATION, FINANCIALS};

	public BlobKey blob;
	public Date created;
	public Type type;

	public String getWebKey() {
		return new Key<ListingDoc>(ListingDoc.class, id).getString();
	}
}
