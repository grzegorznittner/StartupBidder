package com.startupbidder.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
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
public class BidDTO extends AbstractDTO implements Serializable {
	public enum FundType {SYNDICATE, SOLE_INVESTOR, COMMON, PREFERRED, NOTE};
	/**
	 * Bid flow: POSTED -> ACTIVE or REJECTED -> ACCEPTED -> PAID
	 * Apart from that bid can be always marked as REJECTED.
	 */
	public enum Status { POSTED, ACTIVE, WITHDRAWN, REJECTED, ACCEPTED, PAID};
	
	public static final String USER = "user";
	private String user;
	public static final String LISTING = "listing";
	private String listing;
	public static final String LISTING_OWNER = "listingOwner";
	private String listingOwner;
	public static final String PLACED = "placed";
	private Date   placed;
	public static final String VALUE = "value";
	private int    value;
	public static final String PERCENT_OF_COMPANY = "percentOfCompany";
	private int    percentOfCompany;
	public static final String VALUATION = "valuation";
	private int    valuation;
	public static final String FUND_TYPE = "fundType";
	private FundType fundType;
	public static final String STATUS = "status";
	private Status status = Status.ACTIVE;
	public static final String COMMENT = "comment";
	private String comment;
	public static final String INTEREST_RATE = "interestRate";
	private int interestRate;
	
	public BidDTO() {
	}
	
	public String getKind() {
		return "Bid";
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getListing() {
		return listing;
	}

	public void setListing(String businessPlan) {
		this.listing = businessPlan;
	}

	public Date getPlaced() {
		return placed;
	}

	public void setPlaced(Date placed) {
		this.placed = placed;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getPercentOfCompany() {
		return percentOfCompany;
	}

	public void setPercentOfCompany(int percentOfCompany) {
		this.percentOfCompany = percentOfCompany;
	}

	public FundType getFundType() {
		return fundType;
	}

	public void setFundType(FundType fundType) {
		this.fundType = fundType;
	}

	public void setValuation(int valuation) {
		this.valuation = valuation;
	}

	public int getValuation() {
		return valuation;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getListingOwner() {
		return listingOwner;
	}

	public void setListingOwner(String listingOwner) {
		this.listingOwner = listingOwner;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(int interestRate) {
		this.interestRate = interestRate;
	}

	public Entity toEntity() {
		Entity bid = new Entity(this.id);
		bid.setProperty(MOCK_DATA, (Boolean)this.mockData);
		bid.setProperty(FUND_TYPE, fundType != null ? fundType.toString() : FundType.COMMON.toString());
		bid.setProperty(STATUS, status != null ? status.toString() : Status.ACTIVE.toString());
		bid.setProperty(LISTING, listing);
		bid.setProperty(LISTING_OWNER, listingOwner);
		bid.setProperty(PERCENT_OF_COMPANY, percentOfCompany);
		bid.setProperty(INTEREST_RATE, interestRate);
		bid.setProperty(PLACED, placed);
		bid.setProperty(USER, user);
		bid.setProperty(VALUATION, valuation);
		bid.setProperty(VALUE, value);
		bid.setUnindexedProperty(COMMENT, this.comment != null ? new Text(this.comment) : null);
		return bid;
	}
	
	public static BidDTO fromEntity(Entity entity) {
		BidDTO bid = new BidDTO();
		bid.setKey(entity.getKey());
		if (entity.hasProperty(MOCK_DATA)) {
			bid.setMockData((Boolean)entity.getProperty(MOCK_DATA));
		}
		if (!StringUtils.isEmpty((String)entity.getProperty(FUND_TYPE))) {
			bid.setFundType(FundType.valueOf((String)entity.getProperty(FUND_TYPE)));
		}
		if (!StringUtils.isEmpty((String)entity.getProperty(STATUS))) {
			bid.setStatus(Status.valueOf((String)entity.getProperty(STATUS)));
		}
		bid.setListing((String)entity.getProperty(LISTING));
		bid.setListingOwner((String)entity.getProperty(LISTING_OWNER));
		bid.setPercentOfCompany(((Long)entity.getProperty(PERCENT_OF_COMPANY)).intValue());
		bid.setPlaced((Date)entity.getProperty(PLACED));
		bid.setUser((String)entity.getProperty(USER));
		bid.setValuation(((Long)entity.getProperty(VALUATION)).intValue());
		bid.setValue(((Long)entity.getProperty(VALUE)).intValue());
		Text commentText = (Text)entity.getProperty(COMMENT);
		bid.comment = commentText != null ? commentText.getValue() : null;
		if (entity.getProperty(INTEREST_RATE) != null) {
			bid.setInterestRate(((Long)entity.getProperty(INTEREST_RATE)).intValue());
		}
		return bid;
	}

	@Override
	public String toString() {
		return "BidDTO [user=" + user + ", listing=" + listing
				+ ", listingOwner=" + listingOwner + ", placed=" + placed
				+ ", value=" + value + ", percentOfCompany=" + percentOfCompany
				+ ", valuation=" + valuation + ", fundType=" + fundType
				+ ", status=" + status + ", comment=" + comment
				+ ", interestRate=" + interestRate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result
				+ ((fundType == null) ? 0 : fundType.hashCode());
		result = prime * result + interestRate;
		result = prime * result + ((listing == null) ? 0 : listing.hashCode());
		result = prime * result
				+ ((listingOwner == null) ? 0 : listingOwner.hashCode());
		result = prime * result + percentOfCompany;
		result = prime * result + ((placed == null) ? 0 : placed.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + valuation;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof BidDTO))
			return false;
		BidDTO other = (BidDTO) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (fundType != other.fundType)
			return false;
		if (interestRate != other.interestRate)
			return false;
		if (listing == null) {
			if (other.listing != null)
				return false;
		} else if (!listing.equals(other.listing))
			return false;
		if (listingOwner == null) {
			if (other.listingOwner != null)
				return false;
		} else if (!listingOwner.equals(other.listingOwner))
			return false;
		if (percentOfCompany != other.percentOfCompany)
			return false;
		if (placed == null) {
			if (other.placed != null)
				return false;
		} else if (!placed.equals(other.placed))
			return false;
		if (status != other.status)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (valuation != other.valuation)
			return false;
		if (value != other.value)
			return false;
		return true;
	}

}
