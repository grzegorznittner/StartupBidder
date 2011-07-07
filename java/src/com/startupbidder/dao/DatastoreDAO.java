package com.startupbidder.dao;

import java.util.List;

import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.UserStatistics;

public interface DatastoreDAO {
	/**
	 * Returns user data object by userId
	 * 
	 * @param userId User identifier
	 * @return User data
	 */
	UserDTO getUser(String userId);

	/**
	 * Return user statistics like number of comments, bids, etc
	 * @param userId User identifier
	 */
	UserStatistics getUserStatistics(String userId);
	
	/**
	 * Updates/creates user data
	 * If id is empty or user doesn't exist in the repository it will be created.
	 * 
	 * @param user User data object
	 */
	void updateUser(UserDTO user);
	
	/**
	 * Returns business plans created by specified user
	 * 
	 * @param userId User identifier
	 * @param maxItems Maximum number of items returned in the call
	 * @return List of business plans
	 */
	List<ListingDTO> getUserListings(String userId, int maxItems);
	
	/**
	 * Returns top rated business plans
	 * 
	 * @param maxItems Maximum number of items returned in the call
	 * @return List of business plans
	 */
	List<ListingDTO> getTopListings(int maxItems);
	
	/**
	 * Returns most active business plans
	 * 
	 * @param userId User identifier
	 * @param maxItems Maximum number of items returned in the call
	 * @return List of business plans
	 */
	List<ListingDTO> getActiveListings(int maxItems);
	
	/**
	 * Value up listing
	 *
	 * @param listingId Listing identifier
	 * @param userId User identifier
	 * @return Listing rating
	 */
	int valueUpListing(String listingId, String userId);
	
	/**
	 * Value down listing
	 *
	 * @param listingId Listing identifier
	 * @param userId User identifier
	 * @return Listing's rating
	 */
	int valueDownListing(String listingId, String userId);
	
	/**
	 * Returns list of listing's comments
	 * 
	 * @param listingId Listing id
	 * @return List of comments
	 */
	List<CommentDTO> getComments(String listingId);
	
	/**
	 * Returns list of listing's bids
	 * @param listingId Listing id
	 * @return List of bids
	 */
	List<BidDTO> getBids(String listingId);
	
	/**
	 * Returns listing's rating
	 * @param listingId Listing id
	 * @return Current rating
	 */
	int getRating(String listingId);
	
	/**
	 * Returns listing's activity (number of comments)
	 * @param listingId Listing id
	 * @return Activity
	 */
	int getActivity(String listingId);

	/**
	 * Returns bid
	 */
	BidDTO getBid(String bidId);
}
