package com.startupbidder.vo;

import java.util.List;

public interface OrderBook {

	public abstract List<AnonBidVO> getInvestorBids();

	public abstract void setInvestorBids(List<AnonBidVO> investorBids);

	public abstract List<AnonBidVO> getOwnerBids();

	public abstract void setOwnerBids(List<AnonBidVO> ownerBids);

	public abstract List<AnonBidVO> getAcceptedBids();

	public abstract void setAcceptedBids(List<AnonBidVO> acceptedBids);

}