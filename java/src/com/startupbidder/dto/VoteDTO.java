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
public class VoteDTO extends AbstractDTO implements Serializable {
	public static final String LISTING = "listing";
	private String listing;
	public static final String USER = "user";
	private String user;
	public static final String VOTER = "voter";
	private String voter;
	public static final String VALUE = "value";
	private long value;
	public static final String COMMENTED_ON = "commentedOn";
	private Date commentedOn;
	
	public VoteDTO() {
	}

	public String getKind() {
		return "Vote";
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

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public String getVoter() {
		return voter;
	}

	public void setVoter(String voter) {
		this.voter = voter;
	}

	public Date getCommentedOn() {
		return commentedOn;
	}

	public void setCommentedOn(Date commentedOn) {
		this.commentedOn = commentedOn;
	}

	@Override
	public String toString() {
		return "VoteDTO [id=" + getIdAsString() + ", listing=" + listing + ", user=" + user + ", voter="
				+ voter + ", value=" + value + ", commentedOn=" + commentedOn
				+ "]";
	}

	@Override
	public Entity toEntity() {
		Entity vote = new Entity(id);
		vote.setProperty(MOCK_DATA, (Boolean)this.mockData);
		vote.setProperty(COMMENTED_ON, this.commentedOn);
		vote.setProperty(LISTING, this.listing);
		vote.setProperty(USER, this.user);
		vote.setProperty(VOTER, this.voter);
		vote.setProperty(VALUE, this.value);
		return vote;
	}

	public static VoteDTO fromEntity(Entity entity) {
		VoteDTO vote = new VoteDTO();
		vote.setKey(entity.getKey());
		if (entity.hasProperty(MOCK_DATA)) {
			vote.setMockData((Boolean)entity.getProperty(MOCK_DATA));
		}
		vote.setCommentedOn((Date)entity.getProperty(COMMENTED_ON));
		vote.setListing((String)entity.getProperty(LISTING));
		vote.setUser((String)entity.getProperty(USER));
		vote.setVoter((String)entity.getProperty(VOTER));
		vote.setValue((Long)entity.getProperty(VALUE));
		return vote;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((commentedOn == null) ? 0 : commentedOn.hashCode());
		result = prime * result + ((listing == null) ? 0 : listing.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + (int) (value ^ (value >>> 32));
		result = prime * result + ((voter == null) ? 0 : voter.hashCode());
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
		VoteDTO other = (VoteDTO) obj;
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
		if (value != other.value)
			return false;
		if (voter == null) {
			if (other.voter != null)
				return false;
		} else if (!voter.equals(other.voter))
			return false;
		return true;
	}

}
