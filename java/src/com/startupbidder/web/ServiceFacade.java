package com.startupbidder.web;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.startupbidder.dao.DatastoreDAO;
import com.startupbidder.dao.MockDatastoreDAO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.UserStatistics;
import com.startupbidder.dto.VoToDtoConverter;
import com.startupbidder.vo.BidListVO;
import com.startupbidder.vo.BidVO;
import com.startupbidder.vo.CommentListVO;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingListVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.UserVO;

public class ServiceFacade {
	private static final Logger log = Logger.getLogger(ServiceFacade.class.getName());
	private static ServiceFacade instance;
	
	public static ServiceFacade instance() {
		if (instance == null) {
			instance = new ServiceFacade();
		}
		return instance;
	}
	
	private DatastoreDAO getDAO () {
		return MockDatastoreDAO.getInstance();
	}
	
	/**
	 * Returns user data object by userId
	 * 
	 * @param userId User identifier
	 * @return User data as JsonNode
	 */
	public UserVO getUser(String userId) {
		UserVO user = DtoToVoConverter.convert(getDAO().getUser(userId));
		
		UserStatistics stat = getDAO().getUserStatistics(userId);
		user.setNumberOfListings(stat.getNumberOfListings());
		user.setNumberOfBids(stat.getNumberOfBids());
		user.setNumberOfComments(stat.getNumberOfComments());
		
		return user;
	}
	
	/**
	 * Updates/creates user data
	 * If id is empty or user doesn't exist in the repository it will be created.
	 * 
	 * @param userData User data object
	 */
	public UserVO updateUser(UserVO userData) {
		getDAO().updateUser(VoToDtoConverter.convert(userData));
		return DtoToVoConverter.convert(getDAO().getUser(userData.getId()));
	}
	
	/**
	 * Returns business plans created by specified user
	 * 
	 * @param userId User identifier
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of business plans as JsonNode's tree
	 */
	public ListingListVO getUserBusinessPlans(String userId, ListPropertiesVO listingProperties) {
		
		List<ListingVO> bpList = DtoToVoConverter.convertListings(getDAO().getUserListings(userId, listingProperties));
		for (ListingVO bp : bpList) {
			bp.setNumberOfComments(getDAO().getActivity(bp.getId()));
			bp.setRating(getDAO().getRating(bp.getId()));
			bp.setNumberOfBids(getDAO().getBidsForListing(bp.getId()).size());
		}
		
		ListingListVO list = new ListingListVO();
		list.setListings(bpList);
		list.setListingsProperties(listingProperties);

		return list;
	}
	
	/**
	 * Returns top rated business plans
	 * 
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of business plans
	 */
	public ListingListVO getTopBusinessPlans(ListPropertiesVO listingProperties) {
		List<ListingVO> bpList = DtoToVoConverter.convertListings(getDAO().getTopListings(listingProperties));
		for (ListingVO bp : bpList) {
			bp.setNumberOfComments(getDAO().getActivity(bp.getId()));
			bp.setRating(getDAO().getRating(bp.getId()));
			bp.setNumberOfBids(getDAO().getBidsForListing(bp.getId()).size());
		}
		ListingListVO list = new ListingListVO();
		list.setListings(bpList);
		list.setListingsProperties(listingProperties);

		return list;
	}
	
	/**
	 * Returns most active business plans
	 * 
	 * @param userId User identifier
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of business plans
	 */
	public ListingListVO getActiveBusinessPlans(ListPropertiesVO listingProperties) {
		List<ListingVO> bpList = DtoToVoConverter.convertListings(getDAO().getActiveListings(listingProperties));
		for (ListingVO bp : bpList) {
			bp.setNumberOfComments(getDAO().getActivity(bp.getId()));
			bp.setRating(getDAO().getRating(bp.getId()));
			bp.setNumberOfBids(getDAO().getBidsForListing(bp.getId()).size());
		}
		ListingListVO list = new ListingListVO();
		list.setListings(bpList);
		list.setListingsProperties(listingProperties);

		return list;
	}
	
	/**
	 * Value up business plan
	 *
	 * @param businessPlanId Business plan identifier
	 * @param userId User identifier
	 * @return Business plan rating
	 */
	public int valueUpBusinessPlan(String businessPlanId, String userId) {
		return getDAO().valueUpListing(businessPlanId, userId);
	}
	
	/**
	 * Value down business plan
	 *
	 * @param listingId Business plan identifier
	 * @param userId User identifier
	 * @return Business plan rating
	 */
	public int valueDownBusinessPlan(String listingId, String userId) {
		return getDAO().valueDownListing(listingId, userId);
	}
	
	/**
	 * Returns list of listing's comments
	 * 
	 * @param listingId Listing id
	 * @param cursor Cursor string
	 * @return List of comments
	 */
	public CommentListVO getCommentsForListing(String listingId, ListPropertiesVO commentProperties) {
		CommentListVO list = new CommentListVO();
		ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(listingId));
		if (listing == null) {
			log.log(Level.WARNING, "Listing '" + listingId + "' not found");
		} else {
			List<CommentVO> comments = DtoToVoConverter.convertComments(getDAO().getCommentsForListing(listingId));
			for (CommentVO comment : comments) {
				comment.setUserName(getDAO().getUser(comment.getUser()).getNickname());
			}
			list.setComments(comments);
			list.setCommentsProperties(commentProperties);
			list.setListing(listing);
		}

		return list;
	}
	
	/**
	 * Returns list of user's comments
	 * @param listingId User id
	 * @param cursor Cursor string
	 * @return List of comments
	 */
	public CommentListVO getCommentsForUser(String userId, ListPropertiesVO commentProperties) {
		CommentListVO list = new CommentListVO();

		UserVO user = DtoToVoConverter.convert(getDAO().getUser(userId));
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
		} else {
			List<CommentVO> comments = DtoToVoConverter.convertComments(getDAO().getCommentsForUser(userId));
			for (CommentVO comment : comments) {
				comment.setUserName(user.getNickname());
				ListingDTO listing = getDAO().getListing(comment.getListing());
				if (listing == null) {
					log.log(Level.SEVERE, "Comment '" + comment.getId() + "' doesn't have listing id");
				}
				comment.setListingName(listing.getName());
			}

			list.setComments(comments);
			list.setCommentsProperties(commentProperties);
		}
		return list;
	}
	
	/**
	 * Returns list of listing's bids
	 * @param listingId Listing id
	 * @param cursor Cursor string
	 * @return List of bids
	 */
	public BidListVO getBidsForListing(String listingId, ListPropertiesVO bidProperties) {		
		BidListVO list = new BidListVO();
		ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(listingId));
		if (listing == null) {
			log.log(Level.WARNING, "Listing '" + listingId + "' not found");
		} else {
			List<BidVO> bids = DtoToVoConverter.convertBids(getDAO().getBidsForListing(listingId));
			for (BidVO bid : bids) {
				bid.setUserName(getDAO().getUser(bid.getUser()).getNickname());
			}
			
			list.setBids(bids);
			list.setBidsProperties(bidProperties);
			list.setListing(listing);
		}
		
		return list;
	}
	
	/**
	 * Returns list of user's bids
	 * @param listingId Listing id
	 * @param cursor Cursor string
	 * @return List of bids
	 */
	public BidListVO getBidsForUser(String userId, ListPropertiesVO bidProperties) {
		BidListVO list = new BidListVO();

		UserVO user = DtoToVoConverter.convert(getDAO().getUser(userId));
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
		} else {
			List<BidVO> bids = DtoToVoConverter.convertBids(getDAO().getBidsForUser(userId));
			for (BidVO bid : bids) {
				bid.setUserName(user.getNickname());
				bid.setListingName(getDAO().getListing(bid.getListing()).getName());
			}

			list.setBids(bids);
			list.setBidsProperties(bidProperties);
		}
		return list;
	}
	
	/**
	 * Returns business plan's rating
	 * @param businessPlanId Business plan id
	 * @return Current rating
	 */
	public int getRating(String businessPlanId) {
		return getDAO().getRating(businessPlanId);
	}
	
	/**
	 * Returns business plan's activity (number of comments)
	 * @param businessPlanId Business plan id
	 * @return Activity
	 */
	public int getActivity(String businessPlanId) {
		return getDAO().getActivity(businessPlanId);
	}

	public BidVO getBid(String bidId) {
		return DtoToVoConverter.convert(getDAO().getBid(bidId));
	}

	public CommentVO getComment(String commentId) {
		return DtoToVoConverter.convert(getDAO().getComment(commentId));
	}

}
