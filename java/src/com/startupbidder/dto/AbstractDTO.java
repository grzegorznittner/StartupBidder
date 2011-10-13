package com.startupbidder.dto;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

/**
 * Abstract class for entities.
 * Implements some common methods used by DTO classes.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public abstract class AbstractDTO implements Serializable {
	protected Key id;
	public static final String MOCK_DATA = "mockData";
	protected boolean mockData = false;
	
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
	
	public boolean isMockData() {
		return mockData;
	}

	public void setMockData(boolean mockData) {
		this.mockData = mockData;
	}

	/**
	 * Converts Key object into string which can be passed to web client
	 * @return String representing Key
	 */
	@JsonProperty("id")
	public String getIdAsString() {
		return KeyFactory.keyToString(getKey());
	}
	
	/**
	 * Sets id by key provided in string format (eg. from web client)
	 * @param stringId String representing object key
	 */
	@JsonProperty("id")
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

	public static Long toLong(Object entityField) {
		return entityField == null ? 0L : (Long)entityField;
	}
	
	public static Double toDouble(Object entityField) {
		return entityField == null ? 0.0 : (Double)entityField;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (mockData ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AbstractDTO))
			return false;
		AbstractDTO other = (AbstractDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (mockData != other.mockData)
			return false;
		return true;
	}
	
}
