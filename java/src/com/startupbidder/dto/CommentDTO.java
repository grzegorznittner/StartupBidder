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
	public static final String PROFILE = "profile";
	private String profile;
	public static final String COMMENT = "comment";
	private String comment;
	public static final String COMMENTED_ON = "commentedOn";
	private Date   commentedOn;
	public static final String PARENT = "parent";
	private String parent;
	
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

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	@Override
	public Entity toEntity() {
		Entity comment = new Entity(this.id);
		comment.setProperty(MOCK_DATA, (Boolean)this.mockData);
		comment.setUnindexedProperty(COMMENT, this.comment != null ? new Text(this.comment) : null);
		comment.setProperty(COMMENTED_ON, this.commentedOn);
		comment.setProperty(LISTING, this.listing);
		comment.setProperty(USER, this.user);
		comment.setProperty(PARENT, this.parent);
		comment.setProperty(PROFILE, this.profile);
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
		comment.parent = (String)entity.getProperty(PARENT);
		comment.profile = (String)entity.getProperty(PROFILE);
		return comment;
	}

	@Override
	public String toString() {
		return "CommentDTO [listing=" + listing + ", user=" + user
				+ ", profile=" + profile + ", comment=" + comment
				+ ", commentedOn=" + commentedOn + ", parent=" + parent
				+ ", id=" + id + ", mockData=" + mockData + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result
				+ ((commentedOn == null) ? 0 : commentedOn.hashCode());
		result = prime * result + ((listing == null) ? 0 : listing.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((profile == null) ? 0 : profile.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof CommentDTO))
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
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (profile == null) {
			if (other.profile != null)
				return false;
		} else if (!profile.equals(other.profile))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
