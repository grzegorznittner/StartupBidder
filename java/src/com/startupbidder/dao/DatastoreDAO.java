package com.startupbidder.dao;

import java.util.List;

import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.UserStatistics;
import com.startupbidder.vo.ListPropertiesVO;

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
	 * Returns listing by id
	 * @param listingId
	 */
	ListingDTO getListing(String listingId);
	
	/**
	 * Returns business plans created by specified user
	 * 
	 * @param userId User identifier
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of business plans
	 */
	List<ListingDTO> getUserListings(String userId, ListPropertiesVO listingProperties);
	
	/**
	 * Returns top rated business plans
	 * 
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of business plans
	 */
	List<ListingDTO> getTopListings(ListPropertiesVO listingProperties);
	
	/**
	 * Returns most active business plans
	 * 
	 * @param userId User identifier
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of business plans
	 */
	List<ListingDTO> getActiveListings(ListPropertiesVO listingProperties);
	
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
	List<CommentDTO> getCommentsForListing(String listingId);
	
	/**
	 * Returns list of user's comments
	 * @param userId User id
	 */
	List<CommentDTO> getCommentsForUser(String userId);

	/**
	 * Returns list of listing's bids
	 * @param listingId Listing id
	 * @return List of bids
	 */
	List<BidDTO> getBidsForListing(String listingId);
	
	/**
	 * Returns list of user's bids
	 * @param userId Listing id
	 * @return List of bids
	 */
	List<BidDTO> getBidsForUser(String userId);
	
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

	/**
	 * Returns comment
	 */
	CommentDTO getComment(String commentId);

}
