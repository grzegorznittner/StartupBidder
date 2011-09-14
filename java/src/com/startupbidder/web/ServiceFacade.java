package com.startupbidder.web;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.datanucleus.util.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.utils.SystemProperty;
import com.startupbidder.dao.AppEngineDatastoreDAO;
import com.startupbidder.dao.DatastoreDAO;
import com.startupbidder.dao.MockDatastoreDAO;
import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.ListingDocumentDTO;
import com.startupbidder.dto.ListingStatisticsDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.UserStatisticsDTO;
import com.startupbidder.dto.VoToDtoConverter;
import com.startupbidder.vo.BidAndUserVO;
import com.startupbidder.vo.BidListVO;
import com.startupbidder.vo.BidVO;
import com.startupbidder.vo.CommentListVO;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.GraphDataVO;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.ListingListVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.SystemPropertyVO;
import com.startupbidder.vo.UserListVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.vo.UserVotesVO;
import com.startupbidder.vo.VoteVO;

public class ServiceFacade {
	private static final Logger log = Logger.getLogger(ServiceFacade.class.getName());
	private static ServiceFacade instance;
	
	public enum Datastore {MOCK, APPENGINE};
	public static Datastore currentDAO = Datastore.APPENGINE;

	private enum UserStatsUpdateReason {NEW_BID, NEW_COMMENT, NEW_LISTING, NEW_VOTE, NONE};
	private enum ListingStatsUpdateReason {NEW_BID, NEW_COMMENT, NEW_VOTE, NONE};
	
	private Cache cache;
	private static final String USER_STATISTICS_KEY = "userStats";
	private static final String LISTING_STATISTICS_KEY = "listingStats";
	
	public static ServiceFacade instance() {
		if (instance == null) {
			instance = new ServiceFacade();
		}
		return instance;
	}
	
	private ServiceFacade() {
		try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
        } catch (CacheException e) {
            log.log(Level.SEVERE, "Cache couldn't be created!!!");
        }
	}
	
	private DatastoreDAO getDAO () {
		if (currentDAO.equals(Datastore.MOCK)) {
			return MockDatastoreDAO.getInstance();
		} else {
			return AppEngineDatastoreDAO.getInstance();
		}
	}
	
	public String clearDatastore(User user) {
		return getDAO().clearDatastore();
	}

	public String printDatastoreContents(User user) {
		return getDAO().printDatastoreContents();
	}
	
	public String createMockDatastore(User user) {
		return getDAO().createMockDatastore();
	}
	
	public UserVO getLoggedInUserData(User loggedInUser) {
		if (loggedInUser == null) {
			return null;
		}
		UserVO user = DtoToVoConverter.convert(getDAO().getUserByOpenId(loggedInUser.getUserId()));
		if (user == null) {
			return null;
		}
		applyUserStatistics(user);
		return user;
	}
	
	/**
	 * Returns user data object by userId
	 * 
	 * @param userId User identifier
	 * @return User data as JsonNode
	 */
	public UserVO getUser(UserVO loggedInUser, String userId) {
		UserVO user = DtoToVoConverter.convert(getDAO().getUser(userId));
		if (user == null) {
			return null;
		}
		applyUserStatistics(user);
		if (loggedInUser != null) {
			user.setVotable(getDAO().userCanVoteForUser(loggedInUser.getId(), user.getId()));
		} else {
			user.setVotable(false);
		}
		return user;
	}
	
	public UserVO createUser(User loggedInUser) {
		UserVO user = DtoToVoConverter.convert(getDAO().createUser(
				loggedInUser.getUserId(), loggedInUser.getEmail(), loggedInUser.getNickname()));
		applyUserStatistics(user);
		return user;
	}
	
	/**
	 * Updates/creates user data
	 * If id is empty or user doesn't exist in the repository it will be created.
	 * 
	 * @param userData User data object
	 */
	public UserVO updateUser(UserVO loggedInUser, UserVO userData) {
		UserDTO oldUser = getDAO().getUser(userData.getId());
		if (!(oldUser != null && StringUtils.areStringsEqual(oldUser.getNickname(), userData.getNickname()))) {
			if (!checkUserName(loggedInUser, userData.getNickname())) {
				return null;
			}
		}
		UserVO user = DtoToVoConverter.convert(getDAO().updateUser(VoToDtoConverter.convert(userData)));
		applyUserStatistics(user);
		return user;
	}

	/**
	 * Returns list of all registered users
	 * @return List of users
	 */
	public UserListVO getAllUsers(UserVO loggedInUser) {
		List<UserVO> users = DtoToVoConverter.convertUsers(getDAO().getAllUsers());
		int index = 1;
		for (UserVO user : users) {
			applyUserStatistics(user);
			user.setOrderNumber(index++);
		}

		UserListVO userList = new UserListVO();
		userList.setUsers(users);
		userList.setLoggedUser(loggedInUser);
		return userList;
	}

	/**
	 * Returns investor which put the highest number of bids
	 */
	public UserVO getTopInvestor(UserVO loggedInUser) {
		UserVO user = DtoToVoConverter.convert(getDAO().getTopInvestor());
		applyUserStatistics(user);
		return user;
	}

	public UserVO activateUser(UserVO loggedInUser, String userId) {
		UserVO user = DtoToVoConverter.convert(getDAO().activateUser(userId));
		applyUserStatistics(user);
		return user;
	}

	public UserVO deactivateUser(UserVO loggedInUser, String userId) {
		UserVO user = DtoToVoConverter.convert(getDAO().deactivateUser(userId));
		applyUserStatistics(user);
		return user;
	}

	public UserVotesVO userVotes(UserVO loggedInUser, String userId) {
		UserVotesVO userVotes = new UserVotesVO();
		UserVO user = DtoToVoConverter.convert(getDAO().getUser(userId));
		applyUserStatistics(user);
		userVotes.setLoggedUser(user);
		
		List<VoteVO> votes = DtoToVoConverter.convertVotes(getDAO().getUserVotes(userId));
		for (VoteVO vote : votes) {
			vote.setUserName(user.getName());
			ListingDTO listing = getDAO().getListing(vote.getListing());
			vote.setListingName(listing.getName());
		}
		userVotes.setVotes(votes);
		
		return userVotes;
	}

	public Boolean checkUserName(UserVO loggedInUser, String userName) {
		return getDAO().checkUserName(userName);
	}
	
	private void scheduleUpdateOfUserStatistics(String userId, UserStatsUpdateReason reason) {
		log.log(Level.INFO, "Scheduling user stats update for '" + userId + "', reason: " + reason);
		UserStatisticsDTO userStats = (UserStatisticsDTO)cache.get(USER_STATISTICS_KEY + userId);
		if (userStats != null) {
			switch(reason) {
			case NEW_BID:
				userStats.setNumberOfBids(userStats.getNumberOfBids() + 1);
				break;
			case NEW_COMMENT:
				userStats.setNumberOfComments(userStats.getNumberOfComments() + 1);
				break;
			case NEW_LISTING:
				userStats.setNumberOfListings(userStats.getNumberOfListings() + 1);
				break;
			case NEW_VOTE:
				userStats.setNumberOfVotes(userStats.getNumberOfVotes() + 1);
				break;
			default:
				// reason can be also null
				break;
			}
			cache.put(USER_STATISTICS_KEY + userId, userStats);
		}
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/calculate-user-stats").param("id", userId)
				.taskName("user_stats_update_reason_" + reason ));
	}
	
	public UserStatisticsDTO calculateUserStatistics(String userId) {
		log.log(Level.INFO, "Calculating user stats for '" + userId + "'");
		UserStatisticsDTO userStats = getDAO().updateUserStatistics(userId);
		log.log(Level.INFO, "Calculated user stats for '" + userId + "' : " + userStats);
		cache.put(USER_STATISTICS_KEY + userId, userStats);
		return userStats;
	}
	
	private UserStatisticsDTO getUserStatistics(String userId) {
		UserStatisticsDTO userStats = (UserStatisticsDTO)cache.get(USER_STATISTICS_KEY + userId);
		if (userStats == null) {
			userStats = getDAO().getUserStatistics(userId);
			if (userStats == null) {
				// calculating user stats here may be disabled here
				userStats = calculateUserStatistics(userId);
			}
		}
		log.log(Level.INFO, "User stats for '" + userId + "' : " + userStats);
		return userStats;
	}
	
	private void applyUserStatistics(UserVO user) {
		if (user != null && user.getId() != null) {
			UserStatisticsDTO userStats = getUserStatistics(user.getId());
			user.setNumberOfBids(userStats.getNumberOfBids());
			user.setNumberOfComments(userStats.getNumberOfComments());
			user.setNumberOfListings(userStats.getNumberOfListings());
			user.setNumberOfVotes(userStats.getNumberOfVotes());
		}
	}
	
	private void computeListingData(UserVO loggedInUser, ListingVO listing) {
		// set user data
		UserDTO user = getDAO().getUser(listing.getOwner());
		listing.setOwnerName(user != null ? user.getNickname() : "<<unknown>>");
		
		ListingStatisticsDTO listingStats = getListingStatistics(listing.getId());
		listing.setNumberOfBids(listingStats.getNumberOfBids());
		listing.setNumberOfComments(listingStats.getNumberOfComments());
		listing.setNumberOfVotes(listingStats.getNumberOfVotes());
		listing.setMedianValuation((int)listingStats.getValuation());
		
		// calculate daysAgo and daysLeft
		Days daysAgo = Days.daysBetween(new DateTime(listing.getListedOn()), new DateTime());
		listing.setDaysAgo(daysAgo.getDays());

		Days daysLeft = Days.daysBetween(new DateTime(), new DateTime(listing.getClosingOn()));
		listing.setDaysLeft(daysLeft.getDays());
		
		if (loggedInUser != null) {
			listing.setVotable(getDAO().userCanVoteForListing(loggedInUser.getId(), listing.getId()));
		} else {
			listing.setVotable(false);
		}
	}
	
	private void scheduleUpdateOfListingStatistics(String listingId, ListingStatsUpdateReason reason) {
		log.log(Level.INFO, "Scheduling listing stats update for '" + listingId + "', reason: " + reason);
		ListingStatisticsDTO listingStats = (ListingStatisticsDTO)cache.get(LISTING_STATISTICS_KEY + listingId);
		if (listingStats != null) {
			switch(reason) {
			case NEW_BID:
				listingStats.setNumberOfBids(listingStats.getNumberOfBids() + 1);
				break;
			case NEW_COMMENT:
				listingStats.setNumberOfComments(listingStats.getNumberOfComments() + 1);
				break;
			case NEW_VOTE:
				listingStats.setNumberOfVotes(listingStats.getNumberOfVotes() + 1);
				break;
			default:
				// reason can be also null
				break;
			}
			cache.put(LISTING_STATISTICS_KEY + listingId, listingStats);
		}
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/calculate-listing-stats").param("id", listingId)
				.taskName("listing_stats_update_reason_" + reason ));
	}
	
	public ListingStatisticsDTO calculateListingStatistics(String listingId) {
		log.log(Level.INFO, "Calculating listing stats for '" + listingId + "'");
		ListingStatisticsDTO listingStats = getDAO().updateListingStatistics(listingId);
		log.log(Level.INFO, "Calculated listing stats for '" + listingId + "' : " + listingStats);
		cache.put(LISTING_STATISTICS_KEY + listingId, listingStats);
		return listingStats;
	}
	
	private ListingStatisticsDTO getListingStatistics(String listingId) {
		ListingStatisticsDTO listingStats = (ListingStatisticsDTO)cache.get(LISTING_STATISTICS_KEY + listingId);
		if (listingStats == null) {
			listingStats = getDAO().getListingStatistics(listingId);
			if (listingStats == null) {
				// calculating user stats here may be disabled here
				listingStats = calculateListingStatistics(listingId);
			}
		}
		log.log(Level.INFO, "Listing stats for '" + listingId + "' : " + listingStats);
		return listingStats;
	}
	
	/**
	 * Returns listings created by specified user
	 * 
	 * @param userId User identifier
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of user's listings
	 */
	public ListingListVO getUserListings(UserVO loggedInUser, String userId, ListPropertiesVO listingProperties) {
		
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getUserListings(userId, listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			computeListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		
		ListingListVO list = new ListingListVO();
		list.setListings(listings);
		list.setListingsProperties(listingProperties);
		list.setUser(getUser(loggedInUser, userId));
		list.setLoggedUser(loggedInUser);

		return list;
	}
	
	/**
	 * Returns top rated listings
	 * 
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of listings
	 */
	public ListingListVO getTopListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getTopListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			computeListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);
		list.setListingsProperties(listingProperties);
		list.setLoggedUser(loggedInUser);

		return list;
	}

	/**
	 * Returns listings sorted by number of bids
	 * 
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of listings
	 */
	public ListingListVO getActiveListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getActiveListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			computeListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
		list.setLoggedUser(loggedInUser);

		return list;
	}

	/**
	 * Returns listings sorted by median valuation
	 * @param listingProperties
	 * @return
	 */
	public ListingListVO getMostValuedListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		ListPropertiesVO tmpProperties = new ListPropertiesVO();
		tmpProperties.setMaxResults(Integer.MAX_VALUE);
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getTopListings(tmpProperties));
		
		for (ListingVO listing : listings) {
			computeListingData(loggedInUser, listing);
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
		
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			listing.setOrderNumber(index++);
		}
		
		ListingListVO list = new ListingListVO();
		list.setListings(listings);
		list.setListingsProperties(listingProperties);
		list.setLoggedUser(loggedInUser);

		return list;
	}

	/**
	 * Returns the most commented listings
	 * @param listingProperties
	 * @return List of listings
	 */
	public ListingListVO getMostDiscussedListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getMostDiscussedListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			computeListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
		list.setLoggedUser(loggedInUser);
		
		return list;
	}

	/**
	 * Returns the most voted listings
	 * @param listingProperties
	 * @return List of listings
	 */
	public ListingListVO getMostPopularListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getMostPopularListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			computeListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
		list.setLoggedUser(loggedInUser);

		return list;
	}

	public ListingListVO getLatestListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getLatestListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			computeListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
		list.setLoggedUser(loggedInUser);

		return list;
	}

	public ListingListVO getClosingListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getClosingListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			computeListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
		list.setLoggedUser(loggedInUser);

		return list;
	}

	/**
	 * Value up listing
	 */
	public ListingVO valueUpListing(UserVO loggedInUser, String listingId) {
		if (loggedInUser == null) {
			return null;
		}
		ListingVO listing =  DtoToVoConverter.convert(getDAO().valueUpListing(listingId, loggedInUser.getId()));
		if (listing != null) {
			scheduleUpdateOfListingStatistics(listing.getId(), ListingStatsUpdateReason.NEW_VOTE);
			computeListingData(loggedInUser, listing);
		}
		return listing;
	}
	
	/**
	 * Value up user
	 */
	public UserVO valueUpUser(UserVO voter, String userId) {
		if (voter == null) {
			return null;
		}
		UserVO user =  DtoToVoConverter.convert(getDAO().valueUpUser(userId, voter.getId()));
		if (user != null) {
			scheduleUpdateOfUserStatistics(userId, UserStatsUpdateReason.NEW_VOTE);
			applyUserStatistics(user);
		}
		return user;
	}
	
	/**
	 * Value down listing
	 *
	 * @param listingId Listing id
	 * @param userId User identifier
	 * @return Number of votes per listing
	 */
	public ListingVO valueDownListing(UserVO loggedInUser, String listingId) {
//		if (loggedInUser == null) {
//			return null;
//		}
//		ListingVO listing =  DtoToVoConverter.convert(getDAO().valueDownListing(listingId, loggedInUser.getId()));
//		computeListingData(loggedInUser, listing);
//		return listing;
		return null;
	}
	
	/**
	 * Returns list of listing's comments
	 * 
	 * @param listingId Listing id
	 * @param cursor Cursor string
	 * @return List of comments
	 */
	public CommentListVO getCommentsForListing(UserVO loggedInUser, String listingId, ListPropertiesVO commentProperties) {
		CommentListVO list = new CommentListVO();
		ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(listingId));
		if (listing == null) {
			log.log(Level.WARNING, "Listing '" + listingId + "' not found");

			commentProperties.setNumberOfResults(0);
			commentProperties.setStartIndex(0);
			commentProperties.setTotalResults(0);
		} else {
			computeListingData(loggedInUser, listing);
			List<CommentVO> comments = DtoToVoConverter.convertComments(getDAO().getCommentsForListing(listingId));
			int index = commentProperties.getStartIndex() > 0 ? commentProperties.getStartIndex() : 1;
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
		list.setLoggedUser(loggedInUser);

		return list;
	}
	
	/**
	 * Returns list of user's comments
	 * @param listingId User id
	 * @param cursor Cursor string
	 * @return List of comments
	 */
	public CommentListVO getCommentsForUser(UserVO loggedInUser, String userId, ListPropertiesVO commentProperties) {
		CommentListVO list = new CommentListVO();

		UserVO user = getUser(loggedInUser, userId);
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
			commentProperties.setNumberOfResults(0);
			commentProperties.setStartIndex(0);
			commentProperties.setTotalResults(0);
		} else {
			List<CommentVO> comments = DtoToVoConverter.convertComments(getDAO().getCommentsForUser(userId));
			int index = commentProperties.getStartIndex() > 0 ? commentProperties.getStartIndex() : 1;
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
		list.setUser(user);
		list.setLoggedUser(loggedInUser);
		return list;
	}
	
	/**
	 * Returns list of listing's bids
	 * @param listingId Listing id
	 * @param cursor Cursor string
	 * @return List of bids
	 */
	public BidListVO getBidsForListing(UserVO loggedInUser, String listingId, ListPropertiesVO bidProperties) {		
		BidListVO list = new BidListVO();
		ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(listingId));
		if (listing == null) {
			log.log(Level.WARNING, "Listing '" + listingId + "' not found");
			bidProperties.setNumberOfResults(0);
			bidProperties.setStartIndex(0);
			bidProperties.setTotalResults(0);
		} else {
			computeListingData(loggedInUser, listing);
			List<BidVO> bids = DtoToVoConverter.convertBids(getDAO().getBidsForListing(listingId));
			int index = bidProperties.getStartIndex() > 0 ? bidProperties.getStartIndex() : 1;
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
		list.setLoggedUser(loggedInUser);
		
		return list;
	}
	
	/**
	 * Returns list of user's bids
	 * @param listingId Listing id
	 * @param cursor Cursor string
	 * @return List of bids
	 */
	public BidListVO getBidsForUser(UserVO loggedInUser, String userId, ListPropertiesVO bidProperties) {
		BidListVO list = new BidListVO();

		UserVO user = getUser(loggedInUser, userId);
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
			bidProperties.setNumberOfResults(0);
			bidProperties.setStartIndex(0);
			bidProperties.setTotalResults(0);
		} else {
			List<BidVO> bids = DtoToVoConverter.convertBids(getDAO().getBidsForUser(userId));
			int index = bidProperties.getStartIndex() > 0 ? bidProperties.getStartIndex() : 1;
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
		list.setUser(user);
		list.setLoggedUser(loggedInUser);
		
		return list;
	}
	
	/**
	 * Returns listing's rating
	 * @param listingId Listing id
	 * @return Current rating
	 */
	public int getRating(User loggedInUser, String listingId) {
		return getDAO().getNumberOfVotesForListing(listingId);
	}
	
	/**
	 * Returns listings's activity (number of comments)
	 * @param listingId Business plan id
	 * @return Activity
	 */
	public int getActivity(User loggedInUser, String listingId) {
		return getDAO().getActivity(listingId);
	}
 
	/**
	 * Returns bid for a given id and corresponding user profile
	 * @param bidId Bid id
	 */
	public BidAndUserVO getBid(UserVO loggedInUser, String bidId) {
		BidVO bid = DtoToVoConverter.convert(getDAO().getBid(bidId));
		UserVO user = getUser(loggedInUser, bid.getUser());
		ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(bid.getListing()));
		bid.setUserName(user.getNickname());
		bid.setListingName(listing.getName());
		
		BidAndUserVO bidAndUser = new BidAndUserVO();
		bidAndUser.setBid(bid);
		bidAndUser.setUser(user);
		bidAndUser.setLoggedUser(loggedInUser);
		
		return bidAndUser;
	}

	public CommentVO getComment(UserVO loggedInUser, String commentId) {
		return DtoToVoConverter.convert(getDAO().getComment(commentId));
	}

	public ListingVO getListing(UserVO loggedInUser, String listingId) {
		ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(listingId));
		computeListingData(loggedInUser, listing);
		return listing;
	}

	public ListingVO createListing(UserVO loggedInUser, ListingVO listing) {
		if (loggedInUser == null) {
			return null;
		}
		listing.setState(ListingDTO.State.ACTIVE.toString());
		listing.setOwner(loggedInUser.getId());
		
		DateMidnight midnight = new DateMidnight();
		listing.setClosingOn(midnight.plus(Days.days(30)).toDate());
		ListingVO newListing = DtoToVoConverter.convert(getDAO().createListing(VoToDtoConverter.convert(listing)));
		scheduleUpdateOfUserStatistics(loggedInUser.getId(), UserStatsUpdateReason.NEW_LISTING);
		computeListingData(loggedInUser, newListing);
		return newListing;
	}

	public ListingVO updateListing(UserVO loggedInUser, ListingVO listing) {
		if (loggedInUser != null && !StringUtils.areStringsEqual(loggedInUser.getId(), listing.getOwner())) {
			return null;
		}
		ListingVO updatedListing = DtoToVoConverter.convert(getDAO().updateListing(VoToDtoConverter.convert(listing)));
		computeListingData(loggedInUser, updatedListing);
		return updatedListing;
	}

	public ListingVO activateListing(UserVO loggedInUser, String listingId) {
		ListingVO updatedListing = DtoToVoConverter.convert(getDAO().activateListing(listingId));
		computeListingData(loggedInUser, updatedListing);
		return updatedListing;
	}

	public ListingVO withdrawListing(UserVO loggedInUser, String listingId) {
		ListingVO updatedListing = DtoToVoConverter.convert(getDAO().withdrawListing(listingId));
		computeListingData(loggedInUser, updatedListing);
		return updatedListing;
	}

	public CommentVO deleteComment(UserVO loggedInUser, String commentId) {
		CommentVO comment = DtoToVoConverter.convert(getDAO().deleteComment(commentId));
		return comment;
	}

	public CommentVO createComment(UserVO loggedInUser, CommentVO comment) {
		comment = DtoToVoConverter.convert(getDAO().createComment(VoToDtoConverter.convert(comment)));
		scheduleUpdateOfUserStatistics(loggedInUser.getId(), UserStatsUpdateReason.NEW_COMMENT);
		scheduleUpdateOfListingStatistics(comment.getListing(), ListingStatsUpdateReason.NEW_COMMENT);
		return comment;
	}

	public CommentVO updateComment(UserVO loggedInUser, CommentVO comment) {
		comment = DtoToVoConverter.convert(getDAO().updateComment(VoToDtoConverter.convert(comment)));
		return comment;
	}

	public BidVO deleteBid(UserVO loggedInUser, String bidId) {
		BidVO bid = DtoToVoConverter.convert(getDAO().deleteBid(bidId));
		scheduleUpdateOfListingStatistics(bid.getListing(), ListingStatsUpdateReason.NONE);
		return bid;
	}

	public BidVO createBid(UserVO loggedInUser, BidVO bid) {
		bid.setStatus(BidDTO.Status.ACTIVE.toString());
		bid = DtoToVoConverter.convert(getDAO().createBid(VoToDtoConverter.convert(bid)));
		scheduleUpdateOfUserStatistics(loggedInUser.getId(), UserStatsUpdateReason.NEW_BID);
		return bid;
	}

	public BidVO updateBid(UserVO loggedInUser, BidVO bid) {
		bid = DtoToVoConverter.convert(getDAO().updateBid(VoToDtoConverter.convert(bid)));
		scheduleUpdateOfListingStatistics(bid.getListing(), ListingStatsUpdateReason.NONE);
		return bid;
	}

	public BidVO activateBid(UserVO loggedInUser, String bidId) {
		BidVO bid = DtoToVoConverter.convert(getDAO().activateBid(bidId));
		scheduleUpdateOfListingStatistics(bid.getListing(), ListingStatsUpdateReason.NEW_BID);
		return bid;
	}

	public BidVO withdrawBid(UserVO loggedInUser, String bidId) {
		BidVO bid = DtoToVoConverter.convert(getDAO().withdrawBid(bidId));
		scheduleUpdateOfListingStatistics(bid.getListing(), ListingStatsUpdateReason.NONE);
		return bid;
	}

	public GraphDataVO getBidsStatistics(UserVO loggedInUser) {		
		ListPropertiesVO bidsProperties = new ListPropertiesVO();
		bidsProperties.setMaxResults(100);
		List<BidDTO> bids = getDAO().getBidsByDate(bidsProperties);

		int[] values = new int[2];
		if (bids.size() > 1) {
			int bidTimeSpan = Math.abs(Days.daysBetween(new DateTime(bids.get(0).getPlaced()),
					new DateTime(bids.get(bids.size() - 1).getPlaced())).getDays());
			
			values = new int[bidTimeSpan];
			DateMidnight midnight = new DateMidnight();
			for (BidDTO bid : bids) {
				int days = Math.abs(Days.daysBetween(new DateTime(bid.getPlaced().getTime()), midnight).getDays());
				if (days < values.length) {
					values[days]++;
				}
			}
		}
		
		GraphDataVO data = new GraphDataVO();
		data.setLabel(values.length + " Day Bid Valume");
		data.setxAxis("days ago");
		data.setyAxis("num bids");
		data.setValues(values);

		return data;
	}
	
	public SystemPropertyVO getSystemProperty(UserVO loggedInUser, String name) {
		return DtoToVoConverter.convert(getDAO().getSystemProperty(name));
	}

	public List<SystemPropertyVO> getSystemProperties(UserVO loggedInUser) {
		return DtoToVoConverter.convertSystemProperties(getDAO().getSystemProperties());
	}

	public SystemPropertyVO setSystemProperty(UserVO loggedInUser, SystemPropertyVO property) {
		if (loggedInUser == null) {
			return null;
		}
		property.setAuthor(loggedInUser.getEmail());
		return DtoToVoConverter.convert(getDAO().setSystemProperty(VoToDtoConverter.convert(property)));
	}

	public ListingDocumentVO createListingDocument(UserVO loggedInUser, ListingDocumentVO doc) {
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production
				&& loggedInUser == null) {
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			blobstoreService.delete(doc.getBlob());
			return null;
		}
		ListingDocumentDTO docDTO = VoToDtoConverter.convert(doc);
		docDTO.setCreated(new Date());
		return DtoToVoConverter.convert(getDAO().createListingDocument(docDTO));
	}
	
	public ListingDocumentVO getListingDocument(UserVO loggedInUser, String docId) {
		return DtoToVoConverter.convert(getDAO().getListingDocument(docId));
	}
	
	public ListingDocumentVO deleteDocument(UserVO loggedInUser, String docId) {
		return DtoToVoConverter.convert(getDAO().deleteDocument(docId));
	}
	
	public List<ListingDocumentVO> getAllListingDocuments(UserVO loggedInUser) {
		if (loggedInUser == null) {
			return null;
		}
		return DtoToVoConverter.convertListingDocuments(getDAO().getAllListingDocuments());
	}
	
	public String[] createUploadUrls(UserVO loggedInUser, String uploadUrl, int numberOfUrls) {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		String[] urls = new String[numberOfUrls];
		while (numberOfUrls > 0) {
			String discreteUploadUrl = uploadUrl + (uploadUrl.endsWith("/") ? "" : "/") ;
			discreteUploadUrl += "" + new Date().getTime() + numberOfUrls + loggedInUser.hashCode();
			urls[--numberOfUrls] = blobstoreService.createUploadUrl(discreteUploadUrl);
		}
		return urls;
	}

}
