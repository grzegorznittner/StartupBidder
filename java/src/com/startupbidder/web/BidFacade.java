package com.startupbidder.web;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.startupbidder.dao.BidObjectifyDatastoreDAO;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.Bid.Type;
import com.startupbidder.datamodel.BidUser;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.vo.BaseVO;
import com.startupbidder.vo.BidListVO;
import com.startupbidder.vo.BidUserListVO;
import com.startupbidder.vo.BidUserVO;
import com.startupbidder.vo.BidVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.UserShortVO;
import com.startupbidder.vo.UserVO;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class BidFacade {
	private static final Logger log = Logger.getLogger(BidFacade.class.getName());
	private static BidFacade instance;
	
	private DateTimeFormatter timeStampFormatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss_SSS");

	public static BidFacade instance() {
		if (instance == null) {
			instance = new BidFacade();
		}
		return instance;
	}
	
	private BidFacade() {
	}
	
	public BidObjectifyDatastoreDAO getDAO () {
		return BidObjectifyDatastoreDAO.getInstance();
	}
	public ObjectifyDatastoreDAO getGeneralDAO () {
		return ObjectifyDatastoreDAO.getInstance();
	}
	
	public BidVO makeBid(UserVO loggedInUser, String listingId, String type, int amount, int percentage, String text) {
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			return null;
		}
		SBUser investor = VoToModelConverter.convert(loggedInUser);
		if (StringUtils.isEmpty(investor.nickname)) {
			// in dev environment sometimes nickname is empty
			investor = getGeneralDAO().getUser(investor.getWebKey());
		}
		Bid.Type bidType = Bid.Type.valueOf(StringUtils.upperCase(type));
		if (bidType != Bid.Type.INVESTOR_ACCEPT && bidType != Bid.Type.INVESTOR_COUNTER
				&& bidType != Bid.Type.INVESTOR_POST && bidType != Bid.Type.INVESTOR_REJECT
				&& bidType != Bid.Type.INVESTOR_WITHDRAW) {
			log.warning("Investor is trying to make a bid using OWNER_* type.");
			return null;
		}
		Listing listing = getGeneralDAO().getListing(BaseVO.toKeyId(listingId));
		if (listing.state != Listing.State.ACTIVE) {
			log.warning("Investor is trying to make a bid for non active listing.");
			return null;
		}
		SBUser owner = getGeneralDAO().getUser(listing.owner.getString());

		BidUser[] shorts = getDAO().getBidShorts(listing, owner, investor);
		String validationText = validateBid(shorts, bidType, amount, percentage);
		if (validationText == null) {
			log.info("User '" + investor.nickname + "' is making '" + bidType + "' for listing '" + listing.name + "' owned by '" + owner.nickname + "'");
			Bid bid = getDAO().makeBid(shorts, listing, owner, investor, bidType, amount, percentage, text);
			return DtoToVoConverter.convert(bid);
		} else {
			log.info("User '" + investor.nickname + "' is making '" + bidType + "' for listing '" + listing.name
					+ "' owned by '" + owner.nickname + "'. Reason: " + validationText);
			return null;
		}
	}

	public BidVO ownerMakesBid(UserVO loggedInUser, String listingId, String investorId, String type, int amount, int percentage, String text) {
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			return null;
		}
		SBUser owner = VoToModelConverter.convert(loggedInUser);
		if (StringUtils.isEmpty(owner.nickname)) {
			// in dev environment sometimes nickname is empty
			owner = getGeneralDAO().getUser(owner.getWebKey());
		}
		Bid.Type bidType = Bid.Type.valueOf(StringUtils.upperCase(type));
		if (bidType != Bid.Type.OWNER_ACCEPT && bidType != Bid.Type.OWNER_COUNTER
				&& bidType != Bid.Type.OWNER_REJECT && bidType != Bid.Type.OWNER_WITHDRAW) {
			log.warning("Owner is trying to make a bid using INVESTOR_* type.");
			return null;
		}
		Listing listing = getGeneralDAO().getListing(BaseVO.toKeyId(listingId));
		if (listing.owner.getId() != loggedInUser.toKeyId()) {
			log.warning("User not an owner of the listing.");
			return null;
		}
		if (listing.state != Listing.State.ACTIVE) {
			log.warning("Owner is trying to make a bid for non active listing.");
			return null;
		}
		SBUser investor = getGeneralDAO().getUser(investorId);
		
		BidUser[] shorts = getDAO().getBidShorts(listing, investor, owner);
		String validationText = validateBid(shorts, bidType, amount, percentage);
		if (validationText == null) {
			log.info("User '" + owner.nickname + "', owner of listing '" + listing.name + "' is making '" + bidType + "' to offer made by '" + investor.nickname + "'");
			Bid bid = getDAO().makeBid(shorts, listing, investor, owner, bidType, amount, percentage, text);
			return DtoToVoConverter.convert(bid);
		} else {
			log.warning("User '" + owner.nickname + "', owner of listing '" + listing.name
					+ "' cannot make '" + bidType + "' to offer made by '" + investor.nickname + "'. "
					+ "Reason: " + validationText);
			return null;
		}
	}

	private String validateBid(BidUser[] shorts, Type bidType, int amount, int percentage) {
		if (shorts[0] == null) {
			if (bidType == Bid.Type.INVESTOR_POST) {
				return null;
			} else {
				return "No previous bids.";
			}
		}
		// checking last bid type
		switch (shorts[0].type) {
		case INVESTOR_POST:
			switch(bidType) {
			case INVESTOR_POST: case INVESTOR_ACCEPT: case INVESTOR_COUNTER: case INVESTOR_REJECT:
				return "Investor has posted an offer. Cannot post/reject/counter/accept it now.";
			case OWNER_WITHDRAW:
				return "Investor has posted an offer. Owner cannot withdraw this offer now.";
			case OWNER_COUNTER: case OWNER_REJECT: case INVESTOR_WITHDRAW:
				return null;
			case OWNER_ACCEPT:
				if (shorts[0].amount == amount && shorts[0].percentage == percentage) {
					return null;
				} else {
					return "Accepted amount/percentage is not the same as in the offer.";
				}
			default:
				return "Not allowed state.";
			}
		case INVESTOR_COUNTER:
			switch(bidType) {
			case INVESTOR_POST: case INVESTOR_ACCEPT: case INVESTOR_COUNTER: case INVESTOR_REJECT:
				return "Investor has already placed counter offer. Cannot post/reject/counter/accept it now.";
			case OWNER_WITHDRAW:
				return "Investor has placed counter offer. Owner cannot withdraw this offer now.";
			case OWNER_COUNTER: case OWNER_REJECT: case INVESTOR_WITHDRAW:
				return null;
			case OWNER_ACCEPT:
				if (shorts[0].amount == amount && shorts[0].percentage == percentage) {
					return null;
				} else {
					return "Accepted amount/percentage is not the same as in the offer.";
				}
			default:
				return "Not allowed state.";
			}
		case OWNER_COUNTER:
			switch(bidType) {
			case OWNER_COUNTER:	case OWNER_ACCEPT: case OWNER_REJECT: case INVESTOR_POST:
				return "Owner has already placed counter offer. Cannot post/reject/counter/accept it now.";
			case INVESTOR_WITHDRAW:
				return "Owner has placed counter offer. Investor cannot withdraw this offer now.";
			case OWNER_WITHDRAW: case INVESTOR_COUNTER: case INVESTOR_REJECT:
				return null;
			case INVESTOR_ACCEPT:
				if (shorts[0].amount == amount && shorts[0].percentage == percentage) {
					return null;
				} else {
					return "Accepted amount/percentage is not the same as in the offer.";
				}
			default:
				return "Not allowed state.";
			}
		case INVESTOR_ACCEPT:
			if (bidType == Type.INVESTOR_POST) {
				return null;
			}
			return "Investor has already accepted an offer.";
		case INVESTOR_REJECT:
			if (bidType == Type.INVESTOR_POST) {
				return null;
			}
			return "Investor has rejected an offer.";
		case INVESTOR_WITHDRAW:
			if (bidType == Type.INVESTOR_POST) {
				return null;
			}
			return "Investor has withdrawn an offer.";
		case OWNER_ACCEPT:
			if (bidType == Type.INVESTOR_POST) {
				return null;
			}
			return "Owner has accepted an offer.";
		case OWNER_REJECT:
			if (bidType == Type.INVESTOR_POST) {
				return null;
			}
			return "Owner has rejected an offer.";
		case OWNER_WITHDRAW:
			if (bidType == Type.INVESTOR_POST) {
				return null;
			}
			return "Owner has withdrawn an offer.";
		}
		return "Not allowed state.";
	}

	public BidUserListVO getBidUsers(UserVO loggedInUser, String listingId, ListPropertiesVO listProperties) {
		BidUserListVO result = new BidUserListVO();
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage("User not logged in");
			return result;
		}
		Listing listing = getGeneralDAO().getListing(BaseVO.toKeyId(listingId));
		if (listing.owner.getId() != loggedInUser.toKeyId()) {
			log.warning("User not an owner of the listing.");
			result.setErrorCode(ErrorCodes.NOT_AN_OWNER);
			result.setErrorMessage("User not an owner of the listing");
			return result;
		}
		SBUser user = VoToModelConverter.convert(loggedInUser);
		log.info("Bid users for user: " + user);
		List<BidUser> bidUsers = getDAO().getBidShortList(listing, user, listProperties);
		for (BidUser bid : bidUsers) {
			log.info("  * " + bid.userA.getId() + " (" + bid.userANickname + ") - "
					+ bid.userB.getId() + " (" + bid.userBNickname + ") - "
					+ bid.direction + " - " + bid.type + " " + bid.amount + " for " + bid.percentage + "%");
		}
		List<BidUserVO> bids = DtoToVoConverter.convertBidUsers(bidUsers);
		result.setBids(bids);
		result.setBidsProperties(listProperties);
		return result;
	}

	public BidListVO getBids(UserVO loggedInUser, String listingId, String investorId, ListPropertiesVO listProperties) {
		BidListVO result = new BidListVO();
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage("User not logged in");
			return result;
		}
		Listing listing = getGeneralDAO().getListing(BaseVO.toKeyId(listingId));
		SBUser owner = null;
		SBUser investor = null;
		if (StringUtils.isEmpty(investorId)) {
			owner = getGeneralDAO().getUser(listing.owner.getString());
			investor = VoToModelConverter.convert(loggedInUser);
		} else {
			if (listing.owner.getId() != loggedInUser.toKeyId()) {
				log.warning("User is not a listing owner.");
				result.setErrorCode(ErrorCodes.NOT_AN_OWNER);
				result.setErrorMessage("User is not a listing owner");
				return result;
			}
			owner = VoToModelConverter.convert(loggedInUser);
			investor = getGeneralDAO().getUser(investorId);
		}
		log.info("Retrieving bids between '" + investor.nickname + "' (" + investor.id + ") and '" + owner.nickname + "' (" + owner.id + ")");
		List<Bid> bids = getDAO().getBidList(listing, investor, owner, listProperties);
		for (Bid bid : bids) {
			log.info("  * " + bid.userA.getId() + " (" + bid.userANickname + ") - "
					+ bid.userB.getId() + " (" + bid.userBNickname + ") - "
					+ bid.direction + " - " + bid.type + " " + bid.amount + " for " + bid.percentage + "%");
		}
		List<BidVO> msgsVO = DtoToVoConverter.convertBids(bids);
		getDAO().updateReadFlag(listing, owner, investor, bids);
		log.info("Returning " + msgsVO.size() + " messages.");
		result.setBids(msgsVO);
		result.setOtherUser(new UserShortVO(DtoToVoConverter.convert(investor)));
		result.setBidsProperties(listProperties);
		return result;
	}

}
