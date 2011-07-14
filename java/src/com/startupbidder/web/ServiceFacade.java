package com.startupbidder.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;

import com.startupbidder.dao.DatastoreDAO;
import com.startupbidder.dao.MockDatastoreDAO;
import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.UserDTO;
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
	 * Returns investor which put the highest number of bids
	 */
	public UserVO getTopInvestor() {
		UserVO user = DtoToVoConverter.convert(getDAO().getTopInvestor());
		
		UserStatistics stat = getDAO().getUserStatistics(user.getId());
		user.setNumberOfListings(stat.getNumberOfListings());
		user.setNumberOfBids(stat.getNumberOfBids());
		user.setNumberOfComments(stat.getNumberOfComments());
		
		return user;
	}

	private void computeListingData(ListingVO listing) {
		// set user data
		UserDTO user = getDAO().getUser(listing.getOwner());
		listing.setOwnerName(user != null ? user.getNickname() : "<<unknown>>");
		// set number of comments and number of votes
		listing.setNumberOfComments(getDAO().getActivity(listing.getId()));
		listing.setNumberOfVotes(getDAO().getNumberOfVotes(listing.getId()));
		
		// calculate median for bids and set total number of bids
		List<Integer> values = new ArrayList<Integer>();
		List<BidDTO> bids = getDAO().getBidsForListing(listing.getId());
		for (BidDTO bid : bids) {
			values.add(bid.getValue());
		}
		Collections.sort(values);
		int median = 0;
		if (values.size() == 0) {
			median = 0;
		} else if (values.size() == 1) {
			median = values.get(0);
		} else if (values.size() % 2 == 1) {
			median = values.get(values.size() / 2 + 1);
		} else {
			median = (values.get(values.size() / 2 - 1) + values.get(values.size() / 2)) / 2;
		}
		log.log(Level.INFO, "Values for '" + listing.getId() + "': " + values + ", median: " + median);
		listing.setMedianValuation(median);
		listing.setNumberOfBids(bids.size());
		
		// calculate daysAgo and daysLeft
		Days daysAgo = Days.daysBetween(new DateTime(listing.getListedOn()), new DateTime());
		listing.setDaysAgo(daysAgo.getDays());

		Days daysLeft = Days.daysBetween(new DateTime(), new DateTime(listing.getClosingOn()));
		listing.setDaysLeft(daysLeft.getDays());
	}
	
	/**
	 * Returns listings created by specified user
	 * 
	 * @param userId User identifier
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of user's listings
	 */
	public ListingListVO getUserListings(String userId, ListPropertiesVO listingProperties) {
		
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getUserListings(userId, listingProperties));
		int index = listingProperties.getStartIndex();
		for (ListingVO listing : listings) {
			computeListingData(listing);
			listing.setOrderNumber(index++);
		}
		
		ListingListVO list = new ListingListVO();
		list.setListings(listings);
		list.setListingsProperties(listingProperties);

		return list;
	}
	
	/**
	 * Returns top rated listings
	 * 
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of listings
	 */
	public ListingListVO getTopListings(ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getTopListings(listingProperties));
		int index = listingProperties.getStartIndex();
		for (ListingVO listing : listings) {
			computeListingData(listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);
		list.setListingsProperties(listingProperties);

		return list;
	}

	/**
	 * Returns listings sorted by number of bids
	 * 
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of listings
	 */
	public ListingListVO getActiveListings(ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getActiveListings(listingProperties));
		int index = listingProperties.getStartIndex();
		for (ListingVO listing : listings) {
			computeListingData(listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);

		return list;
	}

	/**
	 * Returns listings sorted by median valuation
	 * @param listingProperties
	 * @return
	 */
	public ListingListVO getMostValuedListings(ListPropertiesVO listingProperties) {
		ListPropertiesVO tmpProperties = new ListPropertiesVO();
		tmpProperties.setMaxResults(Integer.MAX_VALUE);
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getTopListings(tmpProperties));
		
		for (ListingVO listing : listings) {
			computeListingData(listing);
		}
		Collections.sort(listings, new Comparator<ListingVO> () {
			public int compare(ListingVO left, ListingVO right) {
				if (left.getMedianValuation() == right.getMedianValuation()) {
					return 0;
				} else if (left.getMedianValuation() > right.getMedianValuation()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		
		listingProperties.setTotalResults(listings.size());
		listings = listings.subList(0, listingProperties.getMaxResults() > listings.size() ? listings.size() : listingProperties.getMaxResults());
		listingProperties.setNumberOfResults(listings.size());
		
		ListingListVO list = new ListingListVO();
		list.setListings(listings);
		list.setListingsProperties(listingProperties);

		return list;
	}

	/**
	 * Returns the most commented listings
	 * @param listingProperties
	 * @return List of listings
	 */
	public ListingListVO getMostDiscussedListings(ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getMostDiscussedListings(listingProperties));
		int index = listingProperties.getStartIndex();
		for (ListingVO listing : listings) {
			computeListingData(listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);

		return list;
	}

	/**
	 * Returns the most voted listings
	 * @param listingProperties
	 * @return List of listings
	 */
	public ListingListVO getMostPopularListings(ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getMostPopularListings(listingProperties));
		int index = listingProperties.getStartIndex();
		for (ListingVO listing : listings) {
			computeListingData(listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);

		return list;
	}

	/**
	 * Value up listing
	 *
	 * @param listingId Listing id
	 * @param userId User identifier
	 * @return Number of votes per listing
	 */
	public int valueUpListing(String listingId, String userId) {
		return getDAO().valueUpListing(listingId, userId);
	}
	
	/**
	 * Value down listing
	 *
	 * @param listingId Listing id
	 * @param userId User identifier
	 * @return Number of votes per listing
	 */
	public int valueDownListing(String listingId, String userId) {
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

			commentProperties.setNumberOfResults(0);
			commentProperties.setStartIndex(0);
			commentProperties.setTotalResults(0);
		} else {
			List<CommentVO> comments = DtoToVoConverter.convertComments(getDAO().getCommentsForListing(listingId));
			int index = commentProperties.getStartIndex();
			for (CommentVO comment : comments) {
				comment.setUserName(getDAO().getUser(comment.getUser()).getNickname());
				comment.setOrderNumber(index++);
			}
			list.setComments(comments);
			list.setListing(listing);

			commentProperties.setNumberOfResults(comments.size());
			commentProperties.setStartIndex(0);
			commentProperties.setTotalResults(comments.size());
		}
		list.setCommentsProperties(commentProperties);

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
			commentProperties.setNumberOfResults(0);
			commentProperties.setStartIndex(0);
			commentProperties.setTotalResults(0);
		} else {
			List<CommentVO> comments = DtoToVoConverter.convertComments(getDAO().getCommentsForUser(userId));
			int index = commentProperties.getStartIndex();
			for (CommentVO comment : comments) {
				comment.setUserName(user.getNickname());
				ListingDTO listing = getDAO().getListing(comment.getListing());
				if (listing == null) {
					log.log(Level.SEVERE, "Comment '" + comment.getId() + "' doesn't have listing id");
				}
				comment.setListingName(listing.getName());
				comment.setOrderNumber(index++);
			}
			list.setComments(comments);

			commentProperties.setNumberOfResults(comments.size());
			commentProperties.setStartIndex(0);
			commentProperties.setTotalResults(comments.size());
		}
		list.setCommentsProperties(commentProperties);
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
			bidProperties.setNumberOfResults(0);
			bidProperties.setStartIndex(0);
			bidProperties.setTotalResults(0);
		} else {
			List<BidVO> bids = DtoToVoConverter.convertBids(getDAO().getBidsForListing(listingId));
			int index = bidProperties.getStartIndex();
			for (BidVO bid : bids) {
				bid.setUserName(getDAO().getUser(bid.getUser()).getNickname());
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
			bidProperties.setNumberOfResults(0);
			bidProperties.setStartIndex(0);
			bidProperties.setTotalResults(0);
		} else {
			List<BidVO> bids = DtoToVoConverter.convertBids(getDAO().getBidsForUser(userId));
			int index = bidProperties.getStartIndex();
			for (BidVO bid : bids) {
				bid.setUserName(user.getNickname());
				bid.setListingName(getDAO().getListing(bid.getListing()).getName());
				bid.setOrderNumber(index++);
			}
			list.setBids(bids);
			bidProperties.setNumberOfResults(bids.size());
			bidProperties.setStartIndex(0);
			bidProperties.setTotalResults(bids.size());
		}
		list.setBidsProperties(bidProperties);
		return list;
	}
	
	/**
	 * Returns business plan's rating
	 * @param businessPlanId Business plan id
	 * @return Current rating
	 */
	public int getRating(String businessPlanId) {
		return getDAO().getNumberOfVotes(businessPlanId);
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

	public ListingVO getListing(String listingId) {
		ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(listingId));
		computeListingData(listing);
		return listing;
	}

}
