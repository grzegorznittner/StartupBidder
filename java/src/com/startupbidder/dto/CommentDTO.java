package com.startupbidder.dto;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
@JsonAutoDetect(getterVisibility=Visibility.DEFAULT, setterVisibility=Visibility.DEFAULT,
		fieldVisibility=Visibility.DEFAULT, isGetterVisibility=Visibility.DEFAULT)
public class CommentDTO extends AbstractDTO implements Serializable {
	public static final String LISTING = "listing";
	private String listing;
	public static final String USER = "user";
	private String user;
	public static final String COMMENT = "comment";
	private String comment;
	public static final String COMMENTED_ON = "commentedOn";
	private Date   commentedOn;
	
	public CommentDTO() {
	}

	public String getKind() {
		return "Comment";
	}

	public String getListing() {
		return listing;
	}

	public void setListing(String listing) {
		this.listing = listing;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getCommentedOn() {
		return commentedOn;
	}

	public void setCommentedOn(Date commentedOn) {
		this.commentedOn = commentedOn;
	}

	@Override
	public String toString() {
		return "CommentDTO [id=" + getIdAsString() + ", listing=" + listing
				+ ", user=" + user + ", comment=" + comment + ", commentedOn="
				+ commentedOn + "]";
	}

	@Override
	public Entity toEntity() {
		Entity comment = new Entity(this.id);
		comment.setProperty(MOCK_DATA, (Boolean)this.mockData);
		comment.setUnindexedProperty(COMMENT, this.comment != null ? new Text(this.comment) : null);
		comment.setProperty(COMMENTED_ON, this.commentedOn);
		comment.setProperty(LISTING, this.listing);
		comment.setProperty(USER, this.user);
		return comment;
	}
	
	public static CommentDTO fromEntity(Entity entity) {
		CommentDTO comment = new CommentDTO();
		comment.setKey(entity.getKey());
		if (entity.hasProperty(MOCK_DATA)) {
			comment.setMockData((Boolean)entity.getProperty(MOCK_DATA));
		}
		Text commentText = (Text)entity.getProperty(COMMENT);
		comment.comment = commentText != null ? commentText.getValue() : null;
		comment.commentedOn = (Date)entity.getProperty(COMMENTED_ON);
		comment.listing = (String)entity.getProperty(LISTING);
		comment.user = (String)entity.getProperty(USER);
		return comment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result
				+ ((commentedOn == null) ? 0 : commentedOn.hashCode());
		result = prime * result + ((listing == null) ? 0 : listing.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		CommentDTO other = (CommentDTO) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (commentedOn == null) {
			if (other.commentedOn != null)
				return false;
		} else if (!commentedOn.equals(other.commentedOn))
			return false;
		if (listing == null) {
			if (other.listing != null)
				return false;
		} else if (!listing.equals(other.listing))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
}
