package com.startupbidder.dto;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.google.appengine.api.datastore.Entity;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
@JsonAutoDetect(getterVisibility=Visibility.DEFAULT, setterVisibility=Visibility.DEFAULT,
		fieldVisibility=Visibility.DEFAULT, isGetterVisibility=Visibility.DEFAULT)
public class MonitorDTO extends AbstractDTO {
	public static enum Type {LISTING, BID, USER};

	public static final String USER = "user";
	private String user;
	public static final String TYPE = "type";
	private Type type;
	public static final String OBJECT = "object";
	private String object;
	public static final String CREATED = "created";
	private Date   created;
	public static final String DEACTIVATED = "deactivated";
	private Date   deactivated;
	public static final String ACTIVE = "active";
	private boolean active;

	@Override
	String getKind() {
		return "Monitor";
	}

	@Override
	public Entity toEntity() {
		Entity entity = new Entity(id);
		entity.setProperty(MOCK_DATA, (Boolean)this.mockData);
		entity.setProperty(ACTIVE, (Boolean)this.active);
		entity.setProperty(USER, StringUtils.defaultIfEmpty(this.user, ""));
		entity.setProperty(OBJECT, StringUtils.defaultIfEmpty(this.object, ""));
		entity.setProperty(DEACTIVATED, this.deactivated);
		entity.setProperty(CREATED, this.created);
		entity.setProperty(TYPE, this.type.toString());
		return entity;
	}
	
	public static MonitorDTO fromEntity(Entity entity) {
		MonitorDTO monitor = new MonitorDTO();
		monitor.setKey(entity.getKey());
		if (entity.hasProperty(MOCK_DATA)) {
			monitor.setMockData((Boolean)entity.getProperty(MOCK_DATA));
		}
		if (entity.hasProperty(ACTIVE)) {
			monitor.setActive((Boolean)entity.getProperty(ACTIVE));
		}
		monitor.setUser((String)entity.getProperty(USER));
		monitor.setObject((String)entity.getProperty(OBJECT));
		monitor.setDeactivated((Date)entity.getProperty(DEACTIVATED));
		monitor.setCreated((Date)entity.getProperty(CREATED));
		if (!StringUtils.isEmpty((String)entity.getProperty(TYPE))) {
			monitor.setType(MonitorDTO.Type.valueOf((String)entity.getProperty(TYPE)));
		}
		return monitor;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public Date getDeactivated() {
		return deactivated;
	}

	public void setDeactivated(Date deactivated) {
		this.deactivated = deactivated;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "NotificationDTO [user=" + user + ", type=" + type + ", object="
				+ object + ", deactivated=" + deactivated + ", created=" + created
				+ ", acknowledged=" + active + ", id=" + id
				+ ", mockData=" + mockData + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((deactivated == null) ? 0 : deactivated.hashCode());
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof MonitorDTO))
			return false;
		MonitorDTO other = (MonitorDTO) obj;
		if (active != other.active)
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (deactivated == null) {
			if (other.deactivated != null)
				return false;
		} else if (!deactivated.equals(other.deactivated))
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (type != other.type)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
}
