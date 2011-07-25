package com.startupbidder.dto;

import java.util.Date;

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
		user.setProperty("status", this.status.toString());
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
		user.setModified((Date)entity.getProperty("linkedin"));
		user.setNickname((String)entity.getProperty("modified"));
		user.setOrganization((String)entity.getProperty("nickname"));
		user.setTitle((String)entity.getProperty("organization"));
		user.setTwitter((String)entity.getProperty("twitter"));
		user.setStatus(UserDTO.Status.valueOf((String)entity.getProperty("status")));
		return user;
	}
	
}
