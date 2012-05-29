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
		Listing listing = getGeneralDAO().getListing(BaseVO.toKeyId(listingId));
		SBUser owner = getGeneralDAO().getUser(listing.owner.getString());
		Bid.Type bidType = Bid.Type.valueOf(type);

		BidUser[] shorts = getDAO().getBidShorts(listing, owner, investor);
		String validationText = validateBid(shorts, bidType);
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
		Listing listing = getGeneralDAO().getListing(BaseVO.toKeyId(listingId));
		SBUser investor = getGeneralDAO().getUser(investorId);
		Bid.Type bidType = Bid.Type.valueOf(type);
		
		BidUser[] shorts = getDAO().getBidShorts(listing, investor, owner);
		String validationText = validateBid(shorts, bidType);
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

	private String validateBid(BidUser[] shorts, Type bidType) {
		if (shorts[0] == null) {
			if (bidType == Bid.Type.INVESTOR_POST) {
				return null;
			} else {
				return "No previous bids.";
			}
		}
		switch (shorts[0].type) {
		case INVESTOR_ACCEPT:
			break;
		case INVESTOR_COUNTER:
			break;
		case INVESTOR_POST:
			break;
		case INVESTOR_REJECT:
			break;
		case INVESTOR_WITHDRAW:
			break;
		case OWNER_ACCEPT:
			break;
		case OWNER_COUNTER:
			break;
		case OWNER_REJECT:
			break;
		case OWNER_WITHDRAW:
			break;
		}
		return null;
	}

	public BidUserListVO getBidUsers(UserVO loggedInUser, String listingId, ListPropertiesVO listProperties) {
		BidUserListVO result = new BidUserListVO();
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage("User not logged in");
			return result;
		}
		SBUser user = VoToModelConverter.convert(loggedInUser);
		Listing listing = getGeneralDAO().getListing(BaseVO.toKeyId(listingId));
		List<BidUserVO> bids = DtoToVoConverter.convertBidUsers(
				getDAO().getBidShortList(listing, user, listProperties));
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
			investor = getGeneralDAO().getUser(investorId);
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
		log.info("Retrieving bids between '" + owner.nickname + "' (" + owner.id + ") and '" + investor.nickname + "' (" + investor.id + ")");
		List<Bid> msgs = getDAO().getBidList(listing, owner, investor, listProperties);
		List<BidVO> msgsVO = DtoToVoConverter.convertBids(msgs);
		getDAO().updateReadFlag(listing, owner, investor, msgs);
		log.info("Returning " + msgsVO.size() + " messages.");
		result.setBids(msgsVO);
		result.setOtherUser(new UserShortVO(DtoToVoConverter.convert(investor)));
		result.setBidsProperties(listProperties);
		return result;
	}

}
