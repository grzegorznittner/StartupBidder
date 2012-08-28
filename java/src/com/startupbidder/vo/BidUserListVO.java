package com.startupbidder.vo;

import java.util.ArrayList;
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
public class BidUserListVO extends BaseResultVO implements OrderBook, UserDataUpdatableContainer {
	@JsonProperty("listing") private ListingVO listing;
	@JsonProperty("investors") private List<BidUserVO> investors;
	@JsonProperty("investors_props")	private ListPropertiesVO investorsProperties;
	@JsonProperty("investor_bids") private List<AnonBidVO> investorBids;
	@JsonProperty("owner_bids") private List<AnonBidVO> ownerBids;
	@JsonProperty("accepted_bids") private List<AnonBidVO> acceptedBids;
	public void updateUserData() {
		List<UserDataUpdatable> updatable = new ArrayList<UserDataUpdatable>();
		if (investors != null) updatable.addAll(investors);
		if (listing != null) updatable.add(listing);
		
		UserMgmtFacade.instance().updateUserData(updatable);
	}
	public List<BidUserVO> getInvestors() {
		return investors;
	}
	public ListingVO getListing() {
		return listing;
	}
	public void setListing(ListingVO listing) {
		this.listing = listing;
	}
	public void setInvestors(List<BidUserVO> investors) {
		this.investors = investors;
	}
	public ListPropertiesVO getInvestorsProperties() {
		return investorsProperties;
	}
	public void setInvestorsProperties(ListPropertiesVO investorsProperties) {
		this.investorsProperties = investorsProperties;
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
