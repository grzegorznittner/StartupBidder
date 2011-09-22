package com.startupbidder.dao;

import java.util.List;

import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.ListingDocumentDTO;
import com.startupbidder.dto.ListingStatisticsDTO;
import com.startupbidder.dto.SystemPropertyDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.UserStatisticsDTO;
import com.startupbidder.dto.VoteDTO;
import com.startupbidder.vo.ListPropertiesVO;

public interface DatastoreDAO {
	/**
	 * Removes all entities from datastore
	 */
	String clearDatastore();
	
	/**
	 * Prints datastore contents
	 */
	String printDatastoreContents();

	/**
	 * Fills datastore with mock data
	 */
	String createMockDatastore();
	
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
	 * Updates user statistics like number of comments, bids, etc
	 * @param userId User identifier
	 */
	UserStatisticsDTO updateUserStatistics(String userId);
	
	/**
	 * Return user statistics data
	 */
	UserStatisticsDTO getUserStatistics(String userId);
	
	/**
	 * Updates/creates user data
	 * If id is empty or user doesn't exist in the repository it will be created.
	 * 
	 * @param user User data object
	 */
	UserDTO updateUser(UserDTO user);

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
	 * Returns list of user's votes
	 * @param userId User id
	 */
	List<VoteDTO> getUserVotes(String userId);
	
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
	 * Returns listing's statistics
	 */
	ListingStatisticsDTO getListingStatistics(String listingId);

	/**
	 * Updates listing's statistics
	 */
	ListingStatisticsDTO updateListingStatistics(String listingId);
	
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
	 * @param voterId Voter identifier
	 */
	ListingDTO valueUpListing(String listingId, String voterId);
	
	/**
	 * Value up user
	 *
	 * @param userId User identifier
	 * @param voterId Voter identifier
	 */
	UserDTO valueUpUser(String userId, String voterId);
	
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
	 */
	int getNumberOfVotesForListing(String listingId);
	
	/**
	 * Returns number of votes for user
	 * @param userId User id
	 */
	int getNumberOfVotesForUser(String userId);
	
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
	 * Checks whether user can for listing.
	 * User can vote only once for a particular listing.
	 */
	boolean userCanVoteForListing(String voterId, String listingId);
	
	/**
	 * Checks whether voter can for user.
	 * Voter can vote only once for a particular user.
	 */
	boolean userCanVoteForUser(String voterId, String userId);

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

	/**
	 * Checks availability of the particular user name
	 * @param userName
	 * @return true if user name is available
	 */
	Boolean checkUserName(String userName);

	/**
	 * Removes comment
	 * @param commentId
	 */
	CommentDTO deleteComment(String commentId);

	/**
	 * Creates new comment
	 * @param comment
	 */
	CommentDTO createComment(CommentDTO comment);

	/**
	 * Updates comment
	 * @param comment
	 */
	CommentDTO updateComment(CommentDTO comment);

	/**
	 * Deletes bid
	 * @param bidId
	 */
	BidDTO deleteBid(String bidId);

	/**
	 * Creates bid
	 * @param bid
	 */
	BidDTO createBid(BidDTO bid);

	/**
	 * Updates bid
	 * @param bid
	 */
	BidDTO updateBid(String loggedInUser, BidDTO bid);

	/**
	 * Sets bid status to ACTIVE
	 * @param bidId
	 */
	BidDTO activateBid(String loggedInUser, String bidId);

	/**
	 * Sets bid status to WITHDRAWN
	 * @param bidId
	 */
	BidDTO withdrawBid(String loggedInUser, String bidId);

	/**
	 * Sets bid status to ACCEPTED
	 */
	BidDTO acceptBid(String loggedInUser, String bidId);

	/**
	 * Sets bid status to PAYED 
	 */
	BidDTO markBidAsPayed(String loggedInUser, String bidId);
	
	/**
	 * Returns list of bids sorted by post date.
	 * @param bidsProperties
	 */
	List<BidDTO> getBidsByDate(ListPropertiesVO bidsProperties);

	/**
	 * Gets system property.
	 * @param name Name of the property
	 */
	SystemPropertyDTO getSystemProperty(String name);

	/**
	 * Sets system property.
	 * @param property Property to set, replaces previous one if exists
	 */
	SystemPropertyDTO setSystemProperty(SystemPropertyDTO property);

	/**
	 * Returns all system properties.
	 * Password properties will be set to "***"
	 */
	List<SystemPropertyDTO> getSystemProperties();

	/**
	 * Stores listing document
	 */
	ListingDocumentDTO createListingDocument(ListingDocumentDTO docDTO);

	/**
	 * Returns listing document by id (doc id, not blob key)
	 */
	ListingDocumentDTO getListingDocument(String docId);

	/**
	 * Return all stored documents
	 */
	List<ListingDocumentDTO> getAllListingDocuments();

	/**
	 * Deletes document
	 */
	ListingDocumentDTO deleteDocument(String docId);

}
