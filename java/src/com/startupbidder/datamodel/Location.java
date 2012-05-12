/**
 * 
 */
package com.startupbidder.datamodel;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Id;
import javax.persistence.PrePersist;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*30)
public class Location extends BaseObject {
	public Location() {
	}
	public Location(String briefAddress) {
		this.briefAddress = briefAddress;
		this.value = 1;
	}
	
	@Id public Long id;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}

	public String briefAddress;
	@Indexed public int value;

	public String getWebKey() {
		return new Key<Location>(Location.class, id).getString();
	}
	
	public static class TopComparator implements Comparator<Location> {
		public int compare(Location o1, Location o2) {
			if (o1.value < o2.value) {
				return 1;
			} else if (o1.value > o2.value) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}

