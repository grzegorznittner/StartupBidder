package com.startupbidder.dto;

import java.util.Date;

import com.google.appengine.api.datastore.Key;

public class BusinessPlanDTO {

	public enum State {CREATED, ACTIVE, CLOSED, WITHDRAWN};
	
	private Key   id;
	private String name;
	private int   startingValuation;
	private Date  startingValuationDate;
	private int   averageValuation;
	private int   medianValuation;
	private Date  listedOn;
	private Date  closingOn;
	private State state;
	private String summary;
	private String owner;
	
	public BusinessPlanDTO() {
	}

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStartingValuation() {
		return startingValuation;
	}

	public void setStartingValuation(int startingValuation) {
		this.startingValuation = startingValuation;
	}

	public Date getStartingValuationDate() {
		return startingValuationDate;
	}

	public void setStartingValuationDate(Date startingValuationDate) {
		this.startingValuationDate = startingValuationDate;
	}

	public int getAverageValuation() {
		return averageValuation;
	}

	public void setAverageValuation(int averageValuation) {
		this.averageValuation = averageValuation;
	}

	public int getMedianValuation() {
		return medianValuation;
	}

	public void setMedianValuation(int medianValuation) {
		this.medianValuation = medianValuation;
	}

	public Date getClosingOn() {
		return closingOn;
	}

	public void setClosingOn(Date closingOn) {
		this.closingOn = closingOn;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Date getListedOn() {
		return listedOn;
	}

	public void setListedOn(Date listedOn) {
		this.listedOn = listedOn;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "BusinessPlanDTO [id=" + id + ", name=" + name + ", startingValuation="
				+ startingValuation + ", startingValuationDate="
				+ startingValuationDate + ", averageValuation="
				+ averageValuation + ", medianValuation=" + medianValuation
				+ ", listedOn=" + listedOn + ", closingOn=" + closingOn
				+ ", state=" + state + ", summary=" + summary + ", owner="
				+ owner + "]";
	}

}
