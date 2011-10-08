package com.startupbidder.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
public class PaidBidDTO extends BidDTO implements Serializable {
	public static final String BID = "bid_id";
	private String bid;
	
	public PaidBidDTO() {
		super.setStatus(Status.PAID);
	}
	
	public String getKind() {
		return "PaidBid";
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}
	
	public Status getStatus() {
		return Status.PAID;
	}
	
	public void setStatus(Status notUser) {
		super.setStatus(Status.PAID);
	}

	public Entity toEntity() {
		Entity bid = new Entity(this.id);
		bid.setProperty(MOCK_DATA, (Boolean)this.mockData);
		bid.setProperty(FUND_TYPE, getFundType() != null ? getFundType().toString() : FundType.COMMON.toString());
		bid.setProperty(STATUS, Status.PAID.toString());
		bid.setProperty(BID, this.bid);
		bid.setProperty(LISTING, getListing());
		bid.setProperty(LISTING_OWNER, getListingOwner());
		bid.setProperty(PERCENT_OF_COMPANY, getPercentOfCompany());
		bid.setProperty(PLACED, getPlaced());
		bid.setProperty(USER, getUser());
		bid.setProperty(VALUATION, getValuation());
		bid.setProperty(VALUE, getValue());
		return bid;
	}
	
	public static PaidBidDTO fromEntity(Entity entity) {
		PaidBidDTO bid = new PaidBidDTO();
		if (entity.getKind().equals(bid.getKind())) {
			bid.setKey(entity.getKey());
			bid.setBid((String)entity.getProperty(BID));
		} else {
			// PaidBid can be also constructed from Bid
			bid.createKey("" + entity.getKey().getId());
			bid.setBid(KeyFactory.keyToString(entity.getKey()));
		}
		if (entity.hasProperty(MOCK_DATA)) {
			bid.setMockData((Boolean)entity.getProperty(MOCK_DATA));
		}
		if (!StringUtils.isEmpty((String)entity.getProperty(FUND_TYPE))) {
			bid.setFundType(FundType.valueOf((String)entity.getProperty(FUND_TYPE)));
		}
		bid.setListing((String)entity.getProperty(LISTING));
		bid.setListingOwner((String)entity.getProperty(LISTING_OWNER));
		bid.setPercentOfCompany(((Long)entity.getProperty(PERCENT_OF_COMPANY)).intValue());
		bid.setPlaced((Date)entity.getProperty(PLACED));
		bid.setUser((String)entity.getProperty(USER));
		bid.setValuation(((Long)entity.getProperty(VALUATION)).intValue());
		bid.setValue(((Long)entity.getProperty(VALUE)).intValue());
		return bid;
	}

	@Override
	public String toString() {
		return "PaidBidDTO [bid=" + bid + ", " + super.toString() + "]";
	}

}
