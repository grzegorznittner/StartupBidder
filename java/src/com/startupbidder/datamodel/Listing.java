/**
 * 
 */
package com.startupbidder.datamodel;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.PrePersist;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfNotNull;
import com.startupbidder.util.ListingToStringStyle;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*30)
public class Listing extends BaseObject<Listing> {
	/**
	 * NEW - just created, not submited by user
	 * POSTED - submited by user, passed simple verfication, needs to be checked by Admins to be available on website
	 * ACTIVE - verified by admins, available on website
	 * WITHDRAWN - discarded by owner, still available on website if you have direct link, but users cannot post it
	 * FROZEN - only admins can do that, it's done when listing needs an action from admins (eg. law violation)
	 * CLOSED - automatic action done after certain time or when bid was accepted
	 */
	public enum State {NEW, POSTED, ACTIVE, CLOSED, WITHDRAWN, FROZEN};
	/**
	 * Determines type of the listing.
	 */
	public enum Type {APPLICATION, COMPANY};
	/**
	 * List of available values for platform property.
	 * It's not verified now.
	 */
	public enum Platform {IOS, ANDROID, WINDOWS_PHONE, DESKTOP, WEBSITE, OTHER};
	/**
	 * List of supported listing currencies.
	 */
	public enum Currency {USD, EUR, GBP};
	/**
	 * List of company stage values.
	 */
	public enum Stage {CONCEPT, STARTUP, ESTABLISHED};

	public Key<Listing> getKey() {
		return new Key<Listing>(Listing.class, id);
	}
	@Id public Long id;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}
	@PrePersist void updateSuggestedValuation() {
		if (suggestedPercentage == 0 || !askedForFunding) {
			this.suggestedValuation = 0;
		} else {
			this.suggestedValuation = suggestedAmount * 100 / suggestedPercentage;
		}
	}

	@Indexed public Key<SBUser> owner;
	public String contactEmail;
	public String founders;
	
	public String name;
	public String mantra;
	public String summary;
	public String website;
	
	/** Category of the listing */
	@Indexed public String category;
	@Indexed public Type type = Type.APPLICATION;
	public String platform;
	public Stage stage = Stage.STARTUP;
	
	/** Notes stored by system */
	public String notes;
	
	@Indexed public Date  created;
	@Indexed public Date  posted;
	@Indexed public Date  listedOn;
	@Indexed(IfNotNull.class) public Date  closingOn;
	@Indexed public State state = State.NEW;
	
	// address parts
	@Indexed public String country;
	@Indexed(IfNotNull.class) public String usState;
	@Indexed(IfNotNull.class) public String usCounty;
	@Indexed public String city;
	public String briefAddress;
	public String address;
	public Double latitude;
	public Double longitude;
	
	/* Has business model canvas */
	public boolean hasBmc;
	/* Has investor presentation */
	public boolean hasIp;
	public boolean askedForFunding;
	public int   suggestedValuation;
	public int   suggestedPercentage;
	public int   suggestedAmount;
	public Currency currency = Currency.USD;
	public Key<ListingDoc> businessPlanId;
	public Key<ListingDoc> presentationId;
	public Key<ListingDoc> financialsId;
	public Key<ListingDoc> logoId;
	public Key<ListingDoc> pic1Id, pic2Id, pic3Id, pic4Id, pic5Id;
	public String logoBase64;
	public String videoUrl;
		
	/** Answers for standard questions */
	public String answer1;
	public String answer2;
	public String answer3;
	public String answer4;
	public String answer5;
	public String answer6;
	public String answer7;
	public String answer8;
	public String answer9;
	public String answer10;
	public String answer11;
	public String answer12;
	public String answer13;
	public String answer14;
	public String answer15;
	public String answer16;
	public String answer17;
	public String answer18;
	public String answer19;
	public String answer20;
	public String answer21;
	public String answer22;
	public String answer23;
	public String answer24;
	public String answer25;
	public String answer26;
	public String answer27;
	
	public String valuationData;
	
	public String getWebKey() {
		return new Key<Listing>(Listing.class, id).getString();
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ListingToStringStyle.instance);
	}
}
