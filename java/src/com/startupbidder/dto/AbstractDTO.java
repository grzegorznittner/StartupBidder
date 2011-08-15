package com.startupbidder.dto;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

/**
 * Abstract class for entities.
 * @author greg
 */
public abstract class AbstractDTO {
	protected Key id;
	
	/**
	 * Returns entity's key
	 * @return
	 */
	public Key getKey() {
		return id;
	}
	
	/**
	 * Sets key
	 * @param id Entity's key
	 */
	public void setKey(Key id) {
		this.id = id;
	}
	
	/**
	 * Converts Key object into string which can be passed to web client
	 * @return String representing Key
	 */
	public String getIdAsString() {
		return KeyFactory.keyToString(getKey());
	}
	
	/**
	 * Sets id by key provided in string format (eg. from web client)
	 * @param stringId String representing object key
	 */
	public void setIdFromString(String stringId) {
		setKey(KeyFactory.stringToKey(stringId));
	}

	/**
	 * Returns kind of the specific entity object
	 * @return Kind of the entity
	 */
	abstract String getKind();

	/**
	 * Generates key from provided id and entity's kind
	 * @param id
	 */
	public void createKey(String id) {
		setKey(KeyFactory.createKey(getKind(), id));
	}
	
	/**
	 * Return query object for this class
	 */
	public Query getQuery() {
		return new Query(getKind());
	}
	
	/**
	 * Creates Entity object from this object 
	 * @return AppEngine's entity object
	 */
	public abstract Entity toEntity();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractDTO other = (AbstractDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
