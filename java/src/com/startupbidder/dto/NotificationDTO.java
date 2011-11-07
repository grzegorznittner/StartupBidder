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
public class NotificationDTO extends AbstractDTO {
	public static enum Type {NEW_BID_FOR_YOUR_LISTING, YOUR_BID_WAS_REJECTED, YOUR_BID_WAS_ACTIVATED,
		YOUR_BID_WAS_ACCEPTED, YOU_ACCEPTED_BID, YOU_PAID_BID, BID_PAID_FOR_YOUR_LISTING, BID_WAS_WITHDRAWN,
		NEW_VOTE_FOR_YOU, NEW_COMMENT_FOR_YOUR_LISTING, NEW_COMMENT_FOR_MONITORED_LISTING,
		YOUR_PROFILE_WAS_MODIFIED, NEW_VOTE_FOR_YOUR_LISTING, NEW_LISTING};

	public static final String USER = "user";
	private String user;
	public static final String TYPE = "type";
	private Type type;
	public static final String OBJECT = "object";
	private String object;
	public static final String MESSAGE = "message";
	private String message;
	public static final String CREATED = "created";
	private Date   created;
	public static final String EMAIL_DATE = "emailDate";
	private Date   emailDate;
	public static final String ACKNOWLEDGED = "acknowledged";
	private boolean acknowledged = false;

	@Override
	String getKind() {
		return "Notification";
	}

	@Override
	public Entity toEntity() {
		Entity user = new Entity(id);
		user.setProperty(MOCK_DATA, (Boolean)this.mockData);
		user.setProperty(ACKNOWLEDGED, (Boolean)this.acknowledged);
		user.setProperty(USER, StringUtils.defaultIfEmpty(this.user, ""));
		user.setProperty(OBJECT, StringUtils.defaultIfEmpty(this.object, ""));
		user.setProperty(MESSAGE, this.message);
		user.setProperty(CREATED, this.created);
		user.setProperty(EMAIL_DATE, this.emailDate);
		user.setProperty(TYPE, this.type.toString());
		return user;
	}
	
	public static NotificationDTO fromEntity(Entity entity) {
		NotificationDTO notif = new NotificationDTO();
		notif.setKey(entity.getKey());
		if (entity.hasProperty(MOCK_DATA)) {
			notif.setMockData((Boolean)entity.getProperty(MOCK_DATA));
		}
		if (entity.hasProperty(ACKNOWLEDGED)) {
			notif.setAcknowledged((Boolean)entity.getProperty(ACKNOWLEDGED));
		}
		notif.setUser((String)entity.getProperty(USER));
		notif.setObject((String)entity.getProperty(OBJECT));
		notif.setMessage((String)entity.getProperty(MESSAGE));
		notif.setCreated((Date)entity.getProperty(CREATED));
		notif.setEmailDate((Date)entity.getProperty(EMAIL_DATE));
		if (!StringUtils.isEmpty((String)entity.getProperty(TYPE))) {
			notif.setType(NotificationDTO.Type.valueOf((String)entity.getProperty(TYPE)));
		}
		return notif;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public boolean isAcknowledged() {
		return acknowledged;
	}

	public void setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}

	public Date getEmailDate() {
		return emailDate;
	}

	public void setEmailDate(Date emailDate) {
		this.emailDate = emailDate;
	}

	@Override
	public String toString() {
		return "NotificationDTO [user=" + user + ", type=" + type + ", object="
				+ object + ", message=" + message + ", created=" + created
				+ ", emailDate=" + emailDate + ", acknowledged=" + acknowledged
				+ ", id=" + id + ", mockData=" + mockData + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (acknowledged ? 1231 : 1237);
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result
				+ ((emailDate == null) ? 0 : emailDate.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
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
		if (!(obj instanceof NotificationDTO))
			return false;
		NotificationDTO other = (NotificationDTO) obj;
		if (acknowledged != other.acknowledged)
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (emailDate == null) {
			if (other.emailDate != null)
				return false;
		} else if (!emailDate.equals(other.emailDate))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
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
