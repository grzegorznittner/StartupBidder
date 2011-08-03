package com.startupbidder.dto;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;

public class SystemPropertyDTO extends AbstractDTO {
	private String name;
	private String value;
	private Date created;
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
		entity.setProperty("name", this.name);
		entity.setProperty("created", this.created);
		entity.setProperty("author", this.author);
		entity.setUnindexedProperty("value", this.value);
		return entity;
	}

	public static SystemPropertyDTO fromEntity(Entity entity) {
		SystemPropertyDTO dto = new SystemPropertyDTO();
		dto.setKey(entity.getKey());
		dto.name = (String)entity.getProperty("name");
		dto.created = (Date)entity.getProperty("created");
		dto.author = (String)entity.getProperty("author");
		dto.value = (String)entity.getProperty("value");
		return dto;
	}

	@Override
	public String toString() {
		return "SystemPropertyDTO [name=" + name + ", value=" + value
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
