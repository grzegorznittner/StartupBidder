package com.startupbidder.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class OrderBookVO extends BaseResultVO {
	@JsonProperty("investor_bids") private List<AnonBidVO> investorBids;
	@JsonProperty("owner_bids") private List<AnonBidVO> ownerBids;
	@JsonProperty("accepted_bids") private List<AnonBidVO> acceptedBids;
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
