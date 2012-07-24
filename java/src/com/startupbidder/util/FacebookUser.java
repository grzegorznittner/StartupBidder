package com.startupbidder.util;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class FacebookUser implements Serializable {
	private static final long serialVersionUID = 256345255675637L;
	
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	
	public FacebookUser(String id, String firstName, String lastName, String email) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
