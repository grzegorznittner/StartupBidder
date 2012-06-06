package com.startupbidder.vo;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class BidListVO extends BaseResultVO implements OrderBook {
	@JsonProperty("listing") private ListingVO listing;
	@JsonProperty("bids") private List<BidVO> bids;
	@JsonProperty("investor") private UserShortVO investor;
	@JsonProperty("investor_bids") private List<AnonBidVO> investorBids;
	@JsonProperty("owner_bids") private List<AnonBidVO> ownerBids;
	@JsonProperty("accepted_bids") private List<AnonBidVO> acceptedBids;
	@JsonProperty("bids_props")	private ListPropertiesVO bidsProperties;
	@JsonProperty("valid_actions") private String validActions[];
	public ListingVO getListing() {
		return listing;
	}
	public void setListing(ListingVO listing) {
		this.listing = listing;
	}
	public List<BidVO> getBids() {
		return bids;
	}
	public void setBids(List<BidVO> bids) {
		this.bids = bids;
	}
	public UserShortVO getInvestor() {
		return investor;
	}
	public void setInvestor(UserShortVO investor) {
		this.investor = investor;
	}
	public ListPropertiesVO getBidsProperties() {
		return bidsProperties;
	}
	public void setBidsProperties(ListPropertiesVO bidsProperties) {
		this.bidsProperties = bidsProperties;
	}
	public void setValidActions(String validActions) {
		this.validActions = StringUtils.split(validActions, ',');
	}
	public List<AnonBidVO> getInvestorBids() {
		return investorBids;
	}
	public void setInvestorBids(List<AnonBidVO> investorBids) {
		this.investorBids = investorBids;
	}
	public List<AnonBidVO> getOwnerBids() {
		return ownerBids;
	}
	public void setOwnerBids(List<AnonBidVO> ownerBids) {
		this.ownerBids = ownerBids;
	}
	public List<AnonBidVO> getAcceptedBids() {
		return acceptedBids;
	}
	public void setAcceptedBids(List<AnonBidVO> acceptedBids) {
		this.acceptedBids = acceptedBids;
	}
}
