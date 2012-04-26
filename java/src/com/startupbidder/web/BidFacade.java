/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.googlecode.objectify.Key;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.Bid.Action;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.vo.BaseVO;
import com.startupbidder.vo.BidAndUserVO;
import com.startupbidder.vo.BidListVO;
import com.startupbidder.vo.BidVO;
import com.startupbidder.vo.BidsForListingVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.UserBasicVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.ListingFacade.UpdateReason;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class BidFacade {
	private static final Logger log = Logger.getLogger(BidFacade.class.getName());
	
	private int BID_MINIMUM_VALUE = 1000;
	private int BID_MINIMUM_PERCENTAGE = 1;
	private int BID_MAXIMUM_PERCENTAGE = 75;
	
	private static BidFacade instance;
	
	public static BidFacade instance() {
		if (instance == null) {
			instance = new BidFacade();
		}
		return instance;
	}
	
	private BidFacade() {
	}
	
	private ObjectifyDatastoreDAO getDAO() {
		return ObjectifyDatastoreDAO.getInstance();
	}
	
	private String printBidList(List<Bid> bidList) {
		StringBuffer buf = new StringBuffer();
		buf.append("\n");
		for (Bid bid : bidList) {
			buf.append("   ").append(bid.actor).append(" ").append(bid.action).append(" ").append(bid.comment).append("\n");
		}
		return buf.toString();
	}
	
//	public BidVO deleteBid(UserVO loggedInUser, String bidId) {
//		BidVO bid = DtoToVoConverter.convert(getDAO().deleteBid(loggedInUser.toKeyId(), BaseVO.toKeyId(bidId)));
//		ListingFacade.instance().scheduleUpdateOfListingStatistics(bid.getListing(), UpdateReason.NONE);
//		return bid;
//	}
	
	private void verifyBasicBidData(BidVO bidVO, BidsForListingVO result) {
		StringBuffer warnings = new StringBuffer();
		
		if (StringUtils.isEmpty(bidVO.getListing())) {
			warnings.append("Bid cannot be created without listing id.");
		}
		if (StringUtils.isEmpty(bidVO.getUser())) {
			warnings.append("Bid cannot be created without bidder id.");
		}
		if (StringUtils.isEmpty(bidVO.getAction())) {
			warnings.append("Bid cannot be created without action.");
		}
		if (StringUtils.isEmpty(bidVO.getActor())) {
			warnings.append("Bid cannot be created without actor.");
		}
		Bid.Action action = Bid.Action.valueOf(bidVO.getAction());
		if (action == Bid.Action.ACTIVATE) {
			if (bidVO.getValue() <= BID_MINIMUM_VALUE) {
				warnings.append("Bid cannot be created with value less than " + BID_MINIMUM_VALUE + ".");
			}
			if (bidVO.getPercentOfCompany() <= BID_MINIMUM_PERCENTAGE) {
				warnings.append("Bid cannot be created with percentage stake less than " + BID_MINIMUM_PERCENTAGE + ".");
			}
			if (bidVO.getPercentOfCompany() >= BID_MAXIMUM_PERCENTAGE) {
				warnings.append("Bid cannot be created with percentage stake more than " + BID_MAXIMUM_PERCENTAGE + ".");
			}
			if (bidVO.getExpires() == null) {
				warnings.append("Bid must have expire date.");
			}
		}
		
		if (warnings.length() > 0) {
			log.log(Level.WARNING, warnings.toString(), new Exception("Validation error"));
			result.setErrorMessage(warnings.toString());
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
		}
	}
	
	private void verifyListing(Listing listing, BidsForListingVO result) {
		StringBuffer warnings = new StringBuffer();
		if (listing == null) {
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			warnings.append("Listing doesn't exist. ");
		}
		if (listing.state != Listing.State.ACTIVE) {
			result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
			warnings.append("Only ACTIVE listings can be bid (was " + listing.state.toString() + ").");
		}
		
		if (warnings.length() > 0) {
			log.log(Level.WARNING, warnings.toString(), new Exception("Validation error"));
			result.setErrorMessage(warnings.toString());
		}
	}
	
	private void verifyOwnerAndBidder(SBUser owner, SBUser bidder, UserVO loggedInUser, Bid bid,
			Listing listing, BidsForListingVO result) {
		StringBuffer warnings = new StringBuffer();
		if (owner == null) {
			warnings.append("Listing owner cannot be found in datastore! ");
		}
		if (bidder == null) {
			warnings.append("Bidder user cannot be found in datastore! ");
		}
		if (bid.actor == Bid.Actor.OWNER && loggedInUser.toKeyId() != listing.owner.getId()) {
			warnings.append("User is not an owner of the bid. ");
		}
		if (bid.actor == Bid.Actor.BIDDER && loggedInUser.toKeyId() == listing.owner.getId()) {
			warnings.append("Listing owner cannot play bidder role for his own listing.");
		}
		
		if (warnings.length() > 0) {
			log.log(Level.WARNING, warnings.toString(), new Exception("Validation error"));
			result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
			result.setErrorMessage(warnings.toString());
		}
	}
	
	/**
	 * Verifies bid lists and checks if bidding is possible
	 * Returned map contains lists sorted by placed date, which simplifies further checks.
	 * @param bidMap Map bids grouped by bidder user
	 * @param bid A new bid which is going to be made
	 * @param result Result object used to store error messages
	 */
	private void verifyAndPrepareBids(Map<Key<SBUser>, List<Bid>> bidMap, Bid bid, BidsForListingVO result) {
		for (List<Bid> list : bidMap.values()) {
			Collections.sort(list, new Bid.PlacedComparator());
			for (Bid b : list) {
				if (b.actor == Bid.Actor.OWNER && b.action == Bid.Action.ACCEPT) {
					log.log(Level.WARNING,
							"There is ACCEPT bid from owner. No more bidding available. Bid history: " + printBidList(list),
							new Exception("Validation error"));
					result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
					result.setErrorMessage("There is accepted bid available, bidding not possible.");
					return;
				}
			}
		}
		Bid.Action previousAction = Bid.Action.CANCEL;
		Bid.Actor previousActor = Bid.Actor.OWNER;
		List<Bid> bidderList = bidMap.get(bid.bidder);
		for (Bid b : bidderList) {
			switch(previousAction) {
			case CANCEL:
				if (b.actor != Bid.Actor.BIDDER) {
					log.log(Level.WARNING, "Bid structure is inconsistant. " 
							+ "Owner has started bidding. Bid history: " + bidderList,
							new Exception("Validation error"));
					result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
					result.setErrorMessage("Bid structure is inconsistant. "
							+ "Owner has started bidding, which was invalid.");
					return;
				}
				if (b.action != Bid.Action.ACTIVATE) {
					log.log(Level.WARNING, "Bid structure is inconsistant. " 
							+ b.actor.toString() + " has done " + b.action + ", which is invalid. Bid history: " + printBidList(bidderList),
							new Exception("Validation error"));
					result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
					result.setErrorMessage("Bid structure is inconsistant. "
							+ b.actor.toString() + " has done " + b.action + ", which was invalid.");
					return;
				}
				break;
			case ACTIVATE:
			case UPDATE:
				if (b.action == Bid.Action.ACTIVATE && previousActor == b.actor) {
					log.log(Level.WARNING, "Bid structure is inconsistant. " 
							+ b.actor.toString() + " has already activated bid. Bid history: " + printBidList(bidderList),
							new Exception("Validation error"));
					result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
					result.setErrorMessage("Bid structure is inconsistant. "
							+ b.actor.toString() + " has already activated bid, now bid should be updated.");
					return;
				}
				break;
			case ACCEPT:
				// no more actions possible, but that was verified before
				break;
			}
			if (previousActor != b.actor && b.action == Bid.Action.UPDATE) {
				log.log(Level.WARNING, "Bid structure is inconsistant. " 
						+ b.actor.toString() + " has not activated bid but is making UPDATE. Bid history: " + printBidList(bidderList),
						new Exception("Validation error"));
				result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
				result.setErrorMessage("Bid structure is inconsistant. "
						+ b.actor.toString() + " has not activated bid, but is making update.");
				return;
			}
			previousActor = b.actor;
			previousAction = b.action;
		}
		if (bid.actor == previousActor && bid.action == Bid.Action.ACTIVATE) {
			log.log(Level.WARNING, bid.actor.toString() + " has already activated bid, now update could be made. Bid history: " + printBidList(bidderList),
					new Exception("Validation error"));
			result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
			result.setErrorMessage(bid.actor.toString() + " has already activated bid, now update could be made.");
			return;
		}
		if (previousAction == Bid.Action.CANCEL && bid.action != Bid.Action.ACTIVATE) {
			log.log(Level.WARNING, bid.actor.toString() + " is trying to " + bid.action + ", which is invalid. Bid history: " + printBidList(bidderList),
					new Exception("Validation error"));
			result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
			result.setErrorMessage("Only new bid can be made now, " + bid.action + " is invalid.");
			return;
		}
		if (bid.action == Bid.Action.ACCEPT) {
			// we have to verify if what is going to be accepted equals last ACTIVATED offer
			// not taking into account CANCELLED offers
			int index = bidderList.size() - 1;
			Bid lastActivatedBid = null;
			for (; index >=0; index--) {
				lastActivatedBid = bidderList.get(index);
				if (lastActivatedBid.action != Action.ACCEPT) {
					break;
				}
			}
			if (lastActivatedBid.action != Action.ACTIVATE && lastActivatedBid.action != Action.UPDATE) {
				log.log(Level.WARNING, "OWNER is trying to ACCEPT offer but there is no available active/updated offer. Bid history: " + printBidList(bidderList),
						new Exception("Validation error"));
				result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
				result.setErrorMessage("Owner cannot accept bid as there is no active bid.");
				return;
			} else {
				// copying last active offer parameters to this bid
				bid.fundType = lastActivatedBid.fundType;
				bid.expires = lastActivatedBid.expires;
				bid.interestRate = lastActivatedBid.interestRate;
				bid.percentOfCompany = lastActivatedBid.percentOfCompany;
				bid.valuation = lastActivatedBid.valuation;
				bid.value = lastActivatedBid.value;
			}
		}

	}
	
	public BidsForListingVO makeBid(UserVO loggedInUser, BidVO bidVO) {
		BidsForListingVO result = new BidsForListingVO();
		verifyBasicBidData(bidVO, result);
		if (result.getErrorCode() != ErrorCodes.OK) {
			return result;
		}

		Bid bid = VoToModelConverter.convert(bidVO);
		Listing listing = getDAO().getListing(bid.listing.getId());
		verifyListing(listing, result);
		if (result.getErrorCode() != ErrorCodes.OK) {
			return result;
		}
		result.setListing(DtoToVoConverter.convert(listing));
		
		SBUser owner = getDAO().getUser(listing.owner.getString());
		SBUser bidder = getDAO().getUser(bidVO.getUser());
		verifyOwnerAndBidder(owner, bidder, loggedInUser, bid, listing, result);
		if (result.getErrorCode() != ErrorCodes.OK) {
			return result;
		}

		// get all bids for listing and verify structure of bids
		Map<Key<SBUser>, List<Bid>> bidMap = getDAO().getAllBids(bid.listing);
		verifyAndPrepareBids(bidMap, bid, result);
		if (result.getErrorCode() != ErrorCodes.OK) {
			return result;
		}
		
		// prepare new bid based on passed parameters
		List<Bid> bidderBids = null;
		if (bidMap.containsKey(bid.bidder)) {
			bidderBids = bidMap.get(bid.bidder);
		} else {
			bidderBids = new ArrayList<Bid>();
			bidMap.put(bid.bidder, bidderBids);
		}
		Bid newBid = prepareBid(listing, owner, bidder, bidderBids, bid);
		newBid = getDAO().makeBid(loggedInUser.toKeyId(), newBid);
		bidderBids.add(newBid);
		
		result.setBidsPerUser(DtoToVoConverter.convertBidMap(bidMap));

		if (newBid != null) {
			UserMgmtFacade.instance().scheduleUpdateOfUserStatistics(loggedInUser.getId(),
					bidderBids.size() == 1 ? UserMgmtFacade.UpdateReason.NEW_BID : UserMgmtFacade.UpdateReason.BID_UPDATE);
			ListingFacade.instance().scheduleUpdateOfListingStatistics(listing.getWebKey(),
					bidderBids.size() == 1 ? ListingFacade.UpdateReason.NEW_BID : ListingFacade.UpdateReason.BID_UPDATE);
			ServiceFacade.instance().createNotification(listing.owner.toString(), newBid.getWebKey(), Notification.Type.NEW_BID_FOR_YOUR_LISTING, "");
		}
		return result;
	}
	
	private Bid prepareBid(Listing listing, SBUser owner, SBUser bidder, List<Bid> bidderBids, Bid bidParameters) {
		Bid newBid = new Bid();
		newBid.action = bidParameters.action;
		newBid.actor = bidParameters.actor;
		newBid.bidder = new Key<SBUser>(SBUser.class, bidder.id);
		newBid.bidderName = bidder.nickname;
		newBid.listingOwner = listing.owner;
		newBid.listing = new Key<Listing>(Listing.class, listing.id);
		newBid.listingName = listing.name;
		newBid.comment = bidParameters.comment;

		if (bidParameters.action != Bid.Action.CANCEL) {
			newBid.fundType = bidParameters.fundType;
			newBid.expires = bidParameters.expires;
			newBid.interestRate = bidParameters.interestRate;
			newBid.percentOfCompany = bidParameters.percentOfCompany;
			newBid.valuation = bidParameters.valuation;
			newBid.value = bidParameters.value;
		}
				
		return newBid;
	}


	public BidVO counterOfferByOwner(UserVO loggedInUser, BidVO bid) {
		BidVO newBid = DtoToVoConverter.convert(
				getDAO().counterOfferedByOwner(loggedInUser.toKeyId(), VoToModelConverter.convert(bid)));
		if (newBid != null) {
			ListingFacade.instance().scheduleUpdateOfListingStatistics(newBid.getListing(), UpdateReason.BID_UPDATE);
			ServiceFacade.instance().createNotification(newBid.getUser(), newBid.getId(), Notification.Type.YOUR_BID_WAS_COUNTERED, "");
		}
		return newBid;
	}
	
	public BidVO counterOfferByInvestor(UserVO loggedInUser, BidVO bid) {
		BidVO newBid = DtoToVoConverter.convert(
				getDAO().counterOfferedByInvestor(loggedInUser.toKeyId(), VoToModelConverter.convert(bid)));
		if (newBid != null) {
			ListingFacade.instance().scheduleUpdateOfListingStatistics(newBid.getListing(), UpdateReason.BID_UPDATE);
			ServiceFacade.instance().createNotification(newBid.getUser(), newBid.getId(), Notification.Type.YOUR_BID_WAS_COUNTERED, "");
		}
		return newBid;
	}

	public BidVO withdrawBid(UserVO loggedInUser, String bidId) {
		BidVO bid = DtoToVoConverter.convert(
				getDAO().withdrawBid(loggedInUser.toKeyId(), BaseVO.toKeyId(bidId)));
		if (bid != null) {
			ListingFacade.instance().scheduleUpdateOfListingStatistics(bid.getListing(), UpdateReason.NONE);
			ServiceFacade.instance().createNotification(bid.getListingOwner(), bid.getId(), Notification.Type.BID_WAS_WITHDRAWN, "");
		}
		return bid;
	}
	
	public BidVO acceptBid(UserVO loggedInUser, String bidId) {
		BidVO bid = DtoToVoConverter.convert(
				getDAO().acceptBid(loggedInUser.toKeyId(), BaseVO.toKeyId(bidId)));
		if (bid != null) {
			ServiceFacade.instance().createNotification(bid.getUser(), bid.getId(), Notification.Type.YOUR_BID_WAS_ACCEPTED, "");
			ServiceFacade.instance().createNotification(bid.getListingOwner(), bid.getId(), Notification.Type.YOU_ACCEPTED_BID, "");
			ListingFacade.instance().scheduleUpdateOfListingStatistics(bid.getListing(), UpdateReason.NONE);
		}
		return bid;
	}

//	public BidVO rejectBid(UserVO loggedInUser, String bidId) {
//		BidVO bid = DtoToVoConverter.convert(
//				getDAO().rejectBid(loggedInUser.toKeyId(), BaseVO.toKeyId(bidId)));
//		if (bid != null) {
//			ServiceFacade.instance().createNotification(bid.getUser(), bid.getId(), Notification.Type.YOUR_BID_WAS_REJECTED, "");
//			ListingFacade.instance().scheduleUpdateOfListingStatistics(bid.getListing(), UpdateReason.NONE);
//		}
//		return bid;
//	}

	public BidVO markBidAsPaid(UserVO loggedInUser, String bidId) {
		BidVO bid = DtoToVoConverter.convert(
				getDAO().markBidAsPaid(loggedInUser.toKeyId(), BaseVO.toKeyId(bidId)));
		if (bid != null) {
			ServiceFacade.instance().createNotification(bid.getUser(), bid.getId(), Notification.Type.YOU_PAID_BID, "");
			ListingFacade.instance().scheduleUpdateOfListingStatistics(bid.getListing(), UpdateReason.NONE);
		}
		return bid;
	}

	/**
	 * Returns bid for a given id and corresponding user profile
	 * @param bidId Bid id
	 */
	public BidAndUserVO getBid(UserVO loggedInUser, String bidId) {
		BidVO bid = DtoToVoConverter.convert(getDAO().getBid(BaseVO.toKeyId(bidId)));
		UserVO user = UserMgmtFacade.instance().getUser(loggedInUser, bid.getUser()).getUser();
		ListingVO listing = DtoToVoConverter.convert(
				getDAO().getListing(BaseVO.toKeyId(bid.getListing())));
		bid.setUserName(user.getNickname());
		bid.setListingName(listing.getName());
		
		BidAndUserVO bidAndUser = new BidAndUserVO();
		bidAndUser.setBid(bid);
		bidAndUser.setUser(new UserBasicVO(user));
		
		return bidAndUser;
	}

	/**
	 * Returns list of listing's bids
	 */
	public BidListVO getBidsForListing(UserVO loggedInUser, String listingId, ListPropertiesVO bidProperties) {		
		BidListVO list = new BidListVO();
		ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(BaseVO.toKeyId(listingId)));
		if (listing == null) {
			log.log(Level.WARNING, "Listing '" + listingId + "' not found");
			bidProperties.setNumberOfResults(0);
			bidProperties.setStartIndex(0);
			bidProperties.setTotalResults(0);
		} else {
			Monitor monitor = loggedInUser != null ? getDAO().getListingMonitor(loggedInUser.toKeyId(), listing.toKeyId()) : null;
			ListingFacade.instance().applyListingData(loggedInUser, listing, monitor);
			List<BidVO> bids = DtoToVoConverter.convertBids(
					getDAO().getBidsForListing(BaseVO.toKeyId(listingId)));
			int index = bidProperties.getStartIndex() > 0 ? bidProperties.getStartIndex() : 1;
			for (BidVO bid : bids) {
				bid.setUserName(getDAO().getUser(bid.getUser()).nickname);
				bid.setListingOwner(listing.getOwner());
				bid.setOrderNumber(index++);
			}			
			list.setBids(bids);
			list.setListing(listing);
			
			bidProperties.setNumberOfResults(bids.size());
			bidProperties.setStartIndex(0);
			bidProperties.setTotalResults(bids.size());
		}
		list.setBidsProperties(bidProperties);
		
		return list;
	}
	
	private void prepareBidList(ListPropertiesVO bidProperties, List<BidVO> bids, UserVO user) {
		int index = bidProperties.getStartIndex() > 0 ? bidProperties.getStartIndex() : 1;
		for (BidVO bid : bids) {
			Listing listing = getDAO().getListing(BaseVO.toKeyId(bid.getListing()));
			bid.setUserName(user.getNickname());
			bid.setListingName(listing.name);
			bid.setListingOwner(listing.owner.getString());
			bid.setOrderNumber(index++);
		}
		bidProperties.setNumberOfResults(bids.size());
		bidProperties.setStartIndex(0);
		bidProperties.setTotalResults(bids.size());
	}

	/**
	 * Returns list of user's bids
	 */
	public BidListVO getBidsForUser(UserVO loggedInUser, String userId, ListPropertiesVO bidProperties) {
		BidListVO list = new BidListVO();
		List<BidVO> bids = null;

		UserVO user = UserMgmtFacade.instance().getUser(loggedInUser, userId).getUser();
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
			return null;
		}

		bids = DtoToVoConverter.convertBids(
				getDAO().getBidsForUser(BaseVO.toKeyId(userId)));
		prepareBidList(bidProperties, bids, user);
		list.setBids(bids);
		list.setBidsProperties(bidProperties);
		list.setUser(new UserBasicVO(user));
		
		return list;
	}
	
	public BidListVO getBidsAcceptedByUser(UserVO loggedInUser, String userId, ListPropertiesVO bidProperties) {
		BidListVO list = new BidListVO();
		List<BidVO> bids = null;

		UserVO user = UserMgmtFacade.instance().getUser(loggedInUser, userId).getUser();
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
			return null;
		}
		
		bids = DtoToVoConverter.convertBids(getDAO().getBidsAcceptedByUser(BaseVO.toKeyId(userId)));
		prepareBidList(bidProperties, bids, user);
		list.setBids(bids);
		list.setBidsProperties(bidProperties);
		list.setUser(new UserBasicVO(user));
		
		return list;
	}

	public BidListVO getBidsFundedByUser(UserVO loggedInUser, String userId, ListPropertiesVO bidProperties) {
		BidListVO list = new BidListVO();
		List<BidVO> bids = null;

		UserVO user = UserMgmtFacade.instance().getUser(loggedInUser, userId).getUser();
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
			return null;
		}
		
		bids = DtoToVoConverter.convertBids(getDAO().getBidsFundedByUser(userId));
		prepareBidList(bidProperties, bids, user);
		list.setBids(bids);
		list.setBidsProperties(bidProperties);
		list.setUser(new UserBasicVO(user));
		
		return list;
	}

}
