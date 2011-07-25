package com.startupbidder.dao;

import java.util.List;

import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.ListingDTO;
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
	 * Returns user with specified open id identifier
	 * @param userId
	 * @return
	 */
	UserDTO getUserByOpenId(String openId);
	
	/**
	 * Creates new user
	 * @param userId
	 * @param email
	 * @param nickname
	 */
	UserDTO createUser(String userId, String email, String nickname);

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
	 * Returns list of all registered users
	 * @return List of users
	 */
	List<UserDTO> getAllUsers();

	/**
	 * Returns user which posted most of bids
	 */
	UserDTO getTopInvestor();

	/**
	 * Creates new listing
	 * @param listing Listing object to create
	 * @return Created listing with updated fields
	 */
	ListingDTO createListing(ListingDTO listing);
	
	/**
	 * Updates listing
	 * @param listing Listing object to update
	 * @return Updated listing object
	 */
	ListingDTO updateListing(ListingDTO listing);

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
	 * @return List of listings
	 */
	List<ListingDTO> getActiveListings(ListPropertiesVO listingProperties);
	
	/**
	 * Returns most valued listings
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of listings
	 */
	List<ListingDTO> getMostValuedListings(ListPropertiesVO listingProperties);

	/**
	 * Returns most commented listings
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 */
	List<ListingDTO> getMostDiscussedListings(ListPropertiesVO listingProperties);

	/**
	 * Returns most voted listings
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 */
	List<ListingDTO> getMostPopularListings(ListPropertiesVO listingProperties);

	/**
	 * Returns recently added listings
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 */
	List<ListingDTO> getLatestListings(ListPropertiesVO listingProperties);

	/**
	 * Returns listings which will close soon
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 */
	List<ListingDTO> getClosingListings(ListPropertiesVO listingProperties);

	/**
	 * Set listing's status to ACTIVE
	 * @param listingId Listing id
	 * @return Update listing or null
	 */
	ListingDTO activateListing(String listingId);

	/**
	 * Set listing's status to WITHDRAWN
	 * @param listingId Listing id
	 * @return Update listing or null
	 */
	ListingDTO withdrawListing(String listingId);

	/**
	 * Value up listing
	 *
	 * @param listingId Listing identifier
	 * @param userId User identifier
	 * @return Listing with new vote number
	 */
	ListingDTO valueUpListing(String listingId, String userId);
	
	/**
	 * Value down listing
	 *
	 * @param listingId Listing identifier
	 * @param userId User identifier
	 * @return Listing with new vote number
	 */
	ListingDTO valueDownListing(String listingId, String userId);
	
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
	 * Returns number of votes for listing
	 * @param listingId Listing id
	 * @return Number of votes
	 */
	int getNumberOfVotes(String listingId);
	
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

	/**
	 * Checks whether user can for for listing.
	 * User can vote only once for a particular listing.
	 */
	boolean canVote(String userId, String listingId);

	/**
	 * Activates user.
	 * @param userId User id (internal one not open id)
	 * @return User with updated data
	 */
	UserDTO activateUser(String userId);

	/**
	 * Deactivates user.
	 * @param userId User id (internal one not open id)
	 * @return User with updated data
	 */
	UserDTO deactivateUser(String userId);
}
