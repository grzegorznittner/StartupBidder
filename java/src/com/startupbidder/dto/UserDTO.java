package com.startupbidder.dto;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.datastore.Entity;

public class UserDTO extends AbstractDTO {
	public enum Status {CREATED, ACTIVE, DEACTIVATED};

	private String openId;
	private String nickname;
	private String name;
	private String email;
	private String title;
	private String organization;
	private String facebook;
	private String twitter;
	private String linkedin;
	private boolean investor;
	private Date   joined;
	private Date   lastLoggedIn;
	private Date   modified;
	private Status status;
	
	public UserDTO() {
	}
	
	public String getKind() {
		return "User";
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public String getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(String linkedin) {
		this.linkedin = linkedin;
	}

	public boolean isInvestor() {
		return investor;
	}

	public void setInvestor(boolean investor) {
		this.investor = investor;
	}

	public Date getJoined() {
		return joined;
	}

	public void setJoined(Date joined) {
		this.joined = joined;
	}

	public Date getLastLoggedIn() {
		return lastLoggedIn;
	}

	public void setLastLoggedIn(Date lastLoggedIn) {
		this.lastLoggedIn = lastLoggedIn;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "UserDTO [nickname=" + nickname + ", name=" + name
				+ ", email=" + email + ", title="
				+ title + ", organization=" + organization + ", facebook="
				+ facebook + ", twitter=" + twitter + ", linkedin=" + linkedin
				+ ", investor=" + investor + ", joined=" + joined
				+ ", lastLoggedIn=" + lastLoggedIn + ", modified=" + modified
				+ ", status=" + status + "]";
	}

	@Override
	public Entity toEntity() {
		Entity user = new Entity(id);
		user.setProperty("openId", this.openId);
		user.setProperty("email", this.email);
		user.setProperty("facebook", this.facebook);
		user.setProperty("name", this.name);
		user.setProperty("investor", this.investor);
		user.setProperty("joined", this.joined);
		user.setProperty("lastLoggedIn", this.lastLoggedIn);
		user.setProperty("linkedin", this.linkedin);
		user.setProperty("modified", this.modified);
		user.setProperty("nickname", this.nickname);
		user.setProperty("organization", this.organization);
		user.setProperty("title", this.title);
		user.setProperty("twitter", this.twitter);
		user.setProperty("status", this.status != null ? this.status.toString() : null);
		return user;
	}
	
	public static UserDTO fromEntity(Entity entity) {
		UserDTO user = new UserDTO();
		user.setKey(entity.getKey());
		user.setOpenId((String)entity.getProperty("openId"));
		user.setEmail((String)entity.getProperty("email"));
		user.setFacebook((String)entity.getProperty("facebook"));
		user.setName((String)entity.getProperty("name"));
		user.setInvestor((Boolean)entity.getProperty("investor"));
		user.setJoined((Date)entity.getProperty("joined"));
		user.setLastLoggedIn((Date)entity.getProperty("lastLoggedIn"));
		user.setLinkedin((String)entity.getProperty("linkedin"));
		user.setModified((Date)entity.getProperty("modified"));
		user.setNickname((String)entity.getProperty("nickname"));
		user.setOrganization((String)entity.getProperty("organization"));
		user.setTitle((String)entity.getProperty("title"));
		user.setTwitter((String)entity.getProperty("twitter"));
		if (!StringUtils.isEmpty((String)entity.getProperty("status"))) {
			user.setStatus(UserDTO.Status.valueOf((String)entity.getProperty("status")));
		}
		return user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((facebook == null) ? 0 : facebook.hashCode());
		result = prime * result + (investor ? 1231 : 1237);
		result = prime * result + ((joined == null) ? 0 : joined.hashCode());
		result = prime * result
				+ ((lastLoggedIn == null) ? 0 : lastLoggedIn.hashCode());
		result = prime * result
				+ ((linkedin == null) ? 0 : linkedin.hashCode());
		result = prime * result
				+ ((modified == null) ? 0 : modified.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((nickname == null) ? 0 : nickname.hashCode());
		result = prime * result + ((openId == null) ? 0 : openId.hashCode());
		result = prime * result
				+ ((organization == null) ? 0 : organization.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((twitter == null) ? 0 : twitter.hashCode());
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
		UserDTO other = (UserDTO) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (facebook == null) {
			if (other.facebook != null)
				return false;
		} else if (!facebook.equals(other.facebook))
			return false;
		if (investor != other.investor)
			return false;
		if (joined == null) {
			if (other.joined != null)
				return false;
		} else if (!joined.equals(other.joined))
			return false;
		if (lastLoggedIn == null) {
			if (other.lastLoggedIn != null)
				return false;
		} else if (!lastLoggedIn.equals(other.lastLoggedIn))
			return false;
		if (linkedin == null) {
			if (other.linkedin != null)
				return false;
		} else if (!linkedin.equals(other.linkedin))
			return false;
		if (modified == null) {
			if (other.modified != null)
				return false;
		} else if (!modified.equals(other.modified))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nickname == null) {
			if (other.nickname != null)
				return false;
		} else if (!nickname.equals(other.nickname))
			return false;
		if (openId == null) {
			if (other.openId != null)
				return false;
		} else if (!openId.equals(other.openId))
			return false;
		if (organization == null) {
			if (other.organization != null)
				return false;
		} else if (!organization.equals(other.organization))
			return false;
		if (status != other.status)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (twitter == null) {
			if (other.twitter != null)
				return false;
		} else if (!twitter.equals(other.twitter))
			return false;
		return true;
	}
	
}
