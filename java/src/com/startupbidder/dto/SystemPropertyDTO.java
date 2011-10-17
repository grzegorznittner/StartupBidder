package com.startupbidder.dto;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.google.appengine.api.datastore.Entity;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
@JsonAutoDetect(getterVisibility=Visibility.DEFAULT, setterVisibility=Visibility.DEFAULT,
		fieldVisibility=Visibility.DEFAULT, isGetterVisibility=Visibility.DEFAULT)
public class SystemPropertyDTO extends AbstractDTO implements Serializable {
	public static final String GOOGLEDOC_USER = "googledoc.user";
	public static final String GOOGLEDOC_PASSWORD = "googledoc.password";
	
	public static final String NAME = "name";
	private String name;
	public static final String VALUE = "value";
	private String value;
	public static final String CREATED = "created";
	private Date created;
	public static final String AUTHOR = "author";
	private String author;

	@Override
	String getKind() {
		return "SysProp";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public Entity toEntity() {
		Entity entity = new Entity(id);
		entity.setProperty(MOCK_DATA, (Boolean)this.mockData);
		entity.setProperty(NAME, this.name);
		entity.setProperty(CREATED, this.created);
		entity.setProperty(AUTHOR, this.author);
		entity.setUnindexedProperty(VALUE, this.value);
		return entity;
	}

	public static SystemPropertyDTO fromEntity(Entity entity) {
		SystemPropertyDTO dto = new SystemPropertyDTO();
		dto.setKey(entity.getKey());
		if (entity.hasProperty(MOCK_DATA)) {
			dto.setMockData((Boolean)entity.getProperty(MOCK_DATA));
		}
		dto.name = (String)entity.getProperty(NAME);
		dto.created = (Date)entity.getProperty(CREATED);
		dto.author = (String)entity.getProperty(AUTHOR);
		dto.value = (String)entity.getProperty(VALUE);
		return dto;
	}

	@Override
	public String toString() {
		return "SystemPropertyDTO [id=" + getIdAsString() + ", name=" + name + ", value=" + value
				+ ", created=" + created + ", author=" + author + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		SystemPropertyDTO other = (SystemPropertyDTO) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
