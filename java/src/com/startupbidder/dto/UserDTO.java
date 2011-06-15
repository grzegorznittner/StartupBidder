package com.startupbidder.dto;

import java.util.Date;

import com.google.appengine.api.datastore.Key;

public class UserDTO {
	private Key id;
	private String nickname;
	private String firstName;
	private String lastName;
	private String email;
	private String title;
	private String organization;
	private String facebook;
	private String twitter;
	private String linkedin;
	private boolean accredited;
	private Date   joined;
	private Date   lastLoggedIn;
	private Date   modified;
	
	public UserDTO() {
	}
	
	public Key getId() {
		return id;
	}
	public void setId(Key id) {
		this.id = id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public boolean isAccredited() {
		return accredited;
	}

	public void setAccredited(boolean accredited) {
		this.accredited = accredited;
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

	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", nickname=" + nickname + ", firstName="
				+ firstName + ", lastName=" + lastName + ", email=" + email
				+ ", title=" + title + ", organization=" + organization
				+ ", facebook=" + facebook + ", twitter=" + twitter
				+ ", linkedin=" + linkedin + ", accredited=" + accredited
				+ ", joined=" + joined + ", lastLoggedIn=" + lastLoggedIn
				+ ", modified=" + modified + "]";
	}
}
