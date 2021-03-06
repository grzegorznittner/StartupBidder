package com.startupbidder.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

import com.startupbidder.web.UserMgmtFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class OrderBookVO extends BaseResultVO implements OrderBook, UserDataUpdatableContainer {
	@JsonProperty("listing") private ListingVO listing;
	@JsonProperty("investor_bids") private List<AnonBidVO> investorBids;
	@JsonProperty("owner_bids") private List<AnonBidVO> ownerBids;
	@JsonProperty("accepted_bids") private List<AnonBidVO> acceptedBids;
	public void updateUserData() {
		UserMgmtFacade.instance().updateUserData(listing);
	}
	public ListingVO getListing() {
		return listing;
	}
	public void setListing(ListingVO listing) {
		this.listing = listing;
	}
	@Override
	public List<AnonBidVO> getInvestorBids() {
		return investorBids;
	}
	@Override
	public void setInvestorBids(List<AnonBidVO> investorBids) {
		this.investorBids = investorBids;
	}
	@Override
	public List<AnonBidVO> getOwnerBids() {
		return ownerBids;
	}
	@Override
	public void setOwnerBids(List<AnonBidVO> ownerBids) {
		this.ownerBids = ownerBids;
	}
	@Override
	public List<AnonBidVO> getAcceptedBids() {
		return acceptedBids;
	}
	@Override
	public void setAcceptedBids(List<AnonBidVO> acceptedBids) {
		this.acceptedBids = acceptedBids;
	}
}
