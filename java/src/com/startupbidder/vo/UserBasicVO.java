package com.startupbidder.vo;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.startupbidder.util.LowecaseSerializer;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserBasicVO extends BaseVO {
	@JsonProperty("profile_id") private String id;
	@JsonProperty("username") private String nickname;
	@JsonProperty("name") private String name;
	@JsonProperty("email") private String email;
	@JsonProperty("investor") private boolean accreditedInvestor;
	@JsonProperty("edited_listing") private String editedListing;
	@JsonProperty("edited_status")	@JsonSerialize(using=LowecaseSerializer.class) private String editedStatus;
	@JsonProperty("num_notifications") private long numberOfNotifications;
	@JsonProperty("votable") private boolean votable;
	@JsonProperty("mockData") private boolean mockData;
	public UserBasicVO() {
	}
	public UserBasicVO(UserVO user) {
		this.id = user.getId();
		this.nickname = user.getNickname();
		this.name = user.getName();
		this.email = user.getEmail();
		this.accreditedInvestor = user.isAccreditedInvestor();
		this.editedListing = user.getEditedListing();
		this.numberOfNotifications = user.getNumberOfNotifications();
		this.votable = user.isVotable();
		this.mockData = user.isMockData();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public boolean isAccreditedInvestor() {
		return accreditedInvestor;
	}
	public void setAccreditedInvestor(boolean accreditedInvestor) {
		this.accreditedInvestor = accreditedInvestor;
	}
	public long getNumberOfNotifications() {
		return numberOfNotifications;
	}
	public void setNumberOfNotifications(long numberOfNotifications) {
		this.numberOfNotifications = numberOfNotifications;
	}
	public boolean isVotable() {
		return votable;
	}
	public void setVotable(boolean votable) {
		this.votable = votable;
	}
	public boolean isMockData() {
		return mockData;
	}
	public void setMockData(boolean mockData) {
		this.mockData = mockData;
	}
	public String getEditedListing() {
		return editedListing;
	}
	public void setEditedListing(String editedListing) {
		this.editedListing = editedListing;
	}
	public String getEditedStatus() {
		return editedStatus;
	}
	public void setEditedStatus(String editedStatus) {
		this.editedStatus = editedStatus;
	}
}
