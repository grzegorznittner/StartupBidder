package com.startupbidder.web;

import java.util.ArrayList;
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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
import com.startupbidder.dto.AbstractDTO;
import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.ListingDocumentDTO;
import com.startupbidder.dto.ListingStatisticsDTO;
import com.startupbidder.dto.MonitorDTO;
import com.startupbidder.dto.NotificationDTO;
import com.startupbidder.dto.NotificationDTO.Type;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.UserStatisticsDTO;
import com.startupbidder.dto.VoToDtoConverter;
import com.startupbidder.vo.BidAndUserVO;
import com.startupbidder.vo.BidListVO;
import com.startupbidder.vo.BidVO;
import com.startupbidder.vo.CommentListVO;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingAndUserVO;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.ListingListVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.MonitorListVO;
import com.startupbidder.vo.MonitorVO;
import com.startupbidder.vo.NotificationListVO;
import com.startupbidder.vo.NotificationVO;
import com.startupbidder.vo.SystemPropertyVO;
import com.startupbidder.vo.UserAndUserVO;
import com.startupbidder.vo.UserListVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.vo.UserVotesVO;
import com.startupbidder.vo.VoteVO;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
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
	
	private DateTimeFormatter timeStampFormatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss_SSS");

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
	
	public DatastoreDAO getDAO () {
		if (currentDAO.equals(Datastore.MOCK)) {
			return MockDatastoreDAO.getInstance();
		} else {
			return AppEngineDatastoreDAO.getInstance();
		}
	}
	
	public String clearDatastore(UserVO loggedInUser) {
		return getDAO().clearDatastore();
	}

	public String printDatastoreContents(UserVO loggedInUser) {
		return getDAO().printDatastoreContents();
	}
	
	public String createMockDatastore(UserVO loggedInUser) {
		return getDAO().createMockDatastore(loggedInUser);
	}
	
	public List<AbstractDTO> exportDatastoreContents(UserVO loggedInUser) {
		return getDAO().exportDatastoreContents();
	}
	
	public UserVO getLoggedInUserData(User loggedInUser) {
		if (loggedInUser == null) {
			return null;
		}
		String email = loggedInUser.getEmail();
		UserVO user = null;
		if (StringUtils.notEmpty(email)) {
			user = DtoToVoConverter.convert(getDAO().getUserByOpenId(email));
		} else {
			user = DtoToVoConverter.convert(getDAO().getUserByOpenId(loggedInUser.getUserId()));
		}
		if (user == null) {
			return null;
		}
		applyUserStatistics(user, user);
		return user;
	}

	/**
	 * Returns user data object by userId
	 * 
	 * @param userId User identifier
	 * @return User data as JsonNode
	 */
	public UserAndUserVO getUser(UserVO loggedInUser, String userId) {
		UserVO user = DtoToVoConverter.convert(getDAO().getUser(userId));
		if (user == null) {
			return null;
		}
		applyUserStatistics(loggedInUser, user);

		UserAndUserVO userAndUser = new UserAndUserVO();
		userAndUser.setUser(user);
		return userAndUser;
	}
	
	public UserVO createUser(User loggedInUser) {
		UserVO user = DtoToVoConverter.convert(getDAO().createUser(
				loggedInUser.getEmail(), loggedInUser.getEmail(), loggedInUser.getNickname()));
		applyUserStatistics(user, user);
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
				log.warning("Nickname for user '" + userData.getId() + "' is not unique!");
				return null;
			}
		}
		if (StringUtils.isEmpty(userData.getName())) {
			log.warning("User's name for user '" + userData.getId() + "' is empty!");
			return null;
		}
		UserVO user = DtoToVoConverter.convert(getDAO().updateUser(VoToDtoConverter.convert(userData)));
		if (user != null) {
			applyUserStatistics(loggedInUser, user);
			createNotification(user.getId(), user.getId(), Type.YOUR_PROFILE_WAS_MODIFIED, "");
		}
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
			applyUserStatistics(loggedInUser, user);
			user.setOrderNumber(index++);
		}

		UserListVO userList = new UserListVO();
		userList.setUsers(users);
		return userList;
	}

	/**
	 * Returns investor which put the highest number of bids
	 */
	public UserVO getTopInvestor(UserVO loggedInUser) {
		UserVO user = DtoToVoConverter.convert(getDAO().getTopInvestor());
		applyUserStatistics(loggedInUser, user);
		return user;
	}

	public UserVO activateUser(UserVO loggedInUser, String userId) {
		UserVO user = DtoToVoConverter.convert(getDAO().activateUser(userId));
		applyUserStatistics(loggedInUser, user);
		return user;
	}

	public UserVO deactivateUser(UserVO loggedInUser, String userId) {
		UserVO user = DtoToVoConverter.convert(getDAO().deactivateUser(userId));
		applyUserStatistics(loggedInUser, user);
		return user;
	}

	public UserVotesVO userVotes(UserVO loggedInUser, String userId) {
		UserVotesVO userVotes = new UserVotesVO();
		UserVO user = DtoToVoConverter.convert(getDAO().getUser(userId));
		applyUserStatistics(loggedInUser, user);
		
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
		return StringUtils.notEmpty(userName) && getDAO().checkUserName(userName);
	}
	
	public void scheduleUpdateOfUserStatistics(String userId, UserStatsUpdateReason reason) {
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
		String taskName = timeStampFormatter.print(new Date().getTime()) + "user_stats_update_" + reason + "_" + userId;
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/calculate-user-stats").param("id", userId)
				.taskName(taskName));
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
	
	public List<UserStatisticsDTO> updateAllUserStatistics() {
		List<UserStatisticsDTO> list = new ArrayList<UserStatisticsDTO>();
		for (UserDTO user : getDAO().getAllUsers()) {
			list.add(calculateUserStatistics(user.getIdAsString()));
		}
		
		return list;
	}
	
	private void applyUserStatistics(UserVO loggedInUser, UserVO user) {
		if (user != null && user.getId() != null) {
			if (loggedInUser != null) {
				user.setVotable(getDAO().userCanVoteForUser(loggedInUser.getId(), user.getId()));
			} else {
				user.setVotable(false);
			}
			
			UserStatisticsDTO userStats = getUserStatistics(user.getId());
			user.setNumberOfBids(userStats.getNumberOfBids());
			user.setNumberOfComments(userStats.getNumberOfComments());
			user.setNumberOfListings(userStats.getNumberOfListings());
			user.setNumberOfVotes(userStats.getNumberOfVotes());
			user.setNumberOfAcceptedBids(userStats.getNumberOfAcceptedBids());
			user.setNumberOfFundedBids(userStats.getNumberOfFundedBids());
			user.setNumberOfNotifications(userStats.getNumberOfNotifications());
		}
	}
	
	private void applyListingData(UserVO loggedInUser, ListingVO listing) {
		// set user data
		UserDTO user = getDAO().getUser(listing.getOwner());
		listing.setOwnerName(user != null ? user.getNickname() : "<<unknown>>");
		
		ListingStatisticsDTO listingStats = getListingStatistics(listing.getId());
		listing.setNumberOfBids(listingStats.getNumberOfBids());
		listing.setNumberOfComments(listingStats.getNumberOfComments());
		listing.setNumberOfVotes(listingStats.getNumberOfVotes());
		listing.setValuation((int)listingStats.getValuation());
		listing.setMedianValuation((int)listingStats.getBidValuation());
		listing.setPreviousValuation((int)listingStats.getPreviousValuation());
		listing.setScore((int)listingStats.getScore());
		
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
	
	public void scheduleUpdateOfListingStatistics(String listingId, ListingStatsUpdateReason reason) {
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
		String taskName = timeStampFormatter.print(new Date().getTime()) + "listing_stats_update_" + reason + "_" + listingId;
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/calculate-listing-stats").param("id", listingId)
				.taskName(taskName));
	}
	
	public ListingStatisticsDTO calculateListingStatistics(String listingId) {
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
	
	public List<ListingStatisticsDTO> updateAllListingStatistics() {
		List<ListingStatisticsDTO> list = new ArrayList<ListingStatisticsDTO>();

		List<ListingDTO> listings = getDAO().getAllListings();
		for (ListingDTO listing : listings) {
			String listingId = listing.getIdAsString();
			list.add(calculateListingStatistics(listingId));
		}
		log.log(Level.INFO, "Updated stats for " + list.size() + " listings: " + list);
		int updatedDocs = DocService.instance().updateListingData(listings);
		log.log(Level.INFO, "Updated docs for " + updatedDocs + " listings.");
		return list;
	}
	
	public ListingListVO listingKeywordSearch(UserVO loggedInUser, String text,
			ListPropertiesVO listingProperties) {
		ListingListVO listingsList = new ListingListVO();
		List<ListingVO> listings = new ArrayList<ListingVO>();
		List<String> ids = DocService.instance().fullTextSearch(text);
		for (String id : ids) {
			ListingAndUserVO listingUser = getListing(loggedInUser, id);
			if (listingUser != null) {
				ListingVO listing = listingUser.getListing();
				listing.setOrderNumber(listings.size() + 1);
				if (ListingDTO.State.ACTIVE.toString().equalsIgnoreCase(listing.getState())) {
					log.info("Active listing added to keyword search results " + listing);
					listings.add(listing);
				} else if (loggedInUser.getId().equals(listing.getOwner())) {
					log.info("Owned listing added to keyword search results " + listing);
					listings.add(listing);
				}
				listingsList.setUser(listingUser.getLoggedUser());
			}
		}
		listingsList.setListings(listings);
		listingProperties.setNumberOfResults(listings.size());
		listingsList.setListingsProperties(listingProperties);
		return listingsList;
	}

	/**
	 * Returns listings created by specified user
	 * 
	 * @param userId User identifier
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of user's listings
	 */
	public ListingListVO getUserListings(UserVO loggedInUser, String userId, ListPropertiesVO listingProperties) {
		
		List<ListingVO> listings = null;
		if (loggedInUser != null && StringUtils.areStringsEqual(userId, loggedInUser.getId())) {
			listings = DtoToVoConverter.convertListings(
				getDAO().getUserListings(userId, listingProperties));
		} else {
			listings = DtoToVoConverter.convertListings(
					getDAO().getUserActiveListings(userId, listingProperties));
		}
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		
		ListingListVO list = new ListingListVO();
		list.setListings(listings);
		list.setListingsProperties(listingProperties);
		list.setUser(getUser(loggedInUser, userId).getUser());

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
			applyListingData(loggedInUser, listing);
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
	public ListingListVO getActiveListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getActiveListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing);
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
	public ListingListVO getMostValuedListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		ListPropertiesVO tmpProperties = new ListPropertiesVO();
		tmpProperties.setMaxResults(Integer.MAX_VALUE);
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getTopListings(tmpProperties));
		
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing);
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
			applyListingData(loggedInUser, listing);
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
	public ListingListVO getMostPopularListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getMostPopularListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);

		return list;
	}

	public ListingListVO getLatestListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getLatestListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);

		return list;
	}

	public ListingListVO getClosingListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getClosingListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);

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
			scheduleUpdateOfUserStatistics(loggedInUser.getId(), UserStatsUpdateReason.NEW_VOTE);
			createNotification(listing.getOwner(), listing.getId(), Type.NEW_VOTE_FOR_YOUR_LISTING, "");
			applyListingData(loggedInUser, listing);
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
			createNotification(user.getId(), user.getId(), Type.NEW_VOTE_FOR_YOU, "");
			applyUserStatistics(voter, user);
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
			applyListingData(loggedInUser, listing);
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

		UserVO user = getUser(loggedInUser, userId).getUser();
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
		return list;
	}
	
	/**
	 * Returns list of listing's bids
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
			applyListingData(loggedInUser, listing);
			List<BidVO> bids = DtoToVoConverter.convertBids(getDAO().getBidsForListing(listingId));
			int index = bidProperties.getStartIndex() > 0 ? bidProperties.getStartIndex() : 1;
			for (BidVO bid : bids) {
				bid.setUserName(getDAO().getUser(bid.getUser()).getNickname());
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
			ListingDTO listing = getDAO().getListing(bid.getListing());
			bid.setUserName(user.getNickname());
			bid.setListingName(listing.getName());
			bid.setListingOwner(listing.getOwner());
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

		UserVO user = getUser(loggedInUser, userId).getUser();
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
			return null;
		}

		bids = DtoToVoConverter.convertBids(getDAO().getBidsForUser(userId));
		prepareBidList(bidProperties, bids, user);
		list.setBids(bids);
		list.setBidsProperties(bidProperties);
		list.setUser(user);
		
		return list;
	}
	
	public BidListVO getBidsAcceptedByUser(UserVO loggedInUser, String userId, ListPropertiesVO bidProperties) {
		BidListVO list = new BidListVO();
		List<BidVO> bids = null;

		UserVO user = getUser(loggedInUser, userId).getUser();
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
			return null;
		}
		
		bids = DtoToVoConverter.convertBids(getDAO().getBidsAcceptedByUser(userId));
		prepareBidList(bidProperties, bids, user);
		list.setBids(bids);
		list.setBidsProperties(bidProperties);
		list.setUser(user);
		
		return list;
	}

	public BidListVO getBidsFundedByUser(UserVO loggedInUser, String userId, ListPropertiesVO bidProperties) {
		BidListVO list = new BidListVO();
		List<BidVO> bids = null;

		UserVO user = getUser(loggedInUser, userId).getUser();
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
			return null;
		}
		
		bids = DtoToVoConverter.convertBids(getDAO().getBidsFundedByUser(userId));
		prepareBidList(bidProperties, bids, user);
		list.setBids(bids);
		list.setBidsProperties(bidProperties);
		list.setUser(user);
		
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
		UserVO user = getUser(loggedInUser, bid.getUser()).getUser();
		ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(bid.getListing()));
		bid.setUserName(user.getNickname());
		bid.setListingName(listing.getName());
		
		BidAndUserVO bidAndUser = new BidAndUserVO();
		bidAndUser.setBid(bid);
		bidAndUser.setUser(user);
		
		return bidAndUser;
	}

	public CommentVO getComment(UserVO loggedInUser, String commentId) {
		return DtoToVoConverter.convert(getDAO().getComment(commentId));
	}

	public ListingAndUserVO getListing(UserVO loggedInUser, String listingId) {
		ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(listingId));
		if (listing != null) {
			applyListingData(loggedInUser, listing);
			ListingAndUserVO listingAndUser = new ListingAndUserVO();
			listingAndUser.setListing(listing);
			return listingAndUser;
		}
		return null;
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
		scheduleUpdateOfListingStatistics(newListing.getId(), ListingStatsUpdateReason.NONE);
		//createNotification(user.getId(), listing.getId(), Type.NEW_LISTING, "");
		applyListingData(loggedInUser, newListing);
		return newListing;
	}

	public ListingVO updateListing(UserVO loggedInUser, ListingVO listing) {
		if (loggedInUser != null && !StringUtils.areStringsEqual(loggedInUser.getId(), listing.getOwner())) {
			return null;
		}
		if (StringUtils.isEmpty(listing.getName())) {
			log.warning("Listing '" + listing.getId() + "' cannot be updated with empty name");
			return null;
		}
		if (StringUtils.isEmpty(listing.getSummary())) {
			log.warning("Listing '" + listing.getId() + "' cannot be updated with empty summary");
			return null;
		}
		ListingVO updatedListing = DtoToVoConverter.convert(getDAO().updateListing(VoToDtoConverter.convert(listing)));
		applyListingData(loggedInUser, updatedListing);
		scheduleUpdateOfListingStatistics(updatedListing.getId(), ListingStatsUpdateReason.NONE);
		return updatedListing;
	}

	public ListingVO activateListing(UserVO loggedInUser, String listingId) {
		ListingVO updatedListing = DtoToVoConverter.convert(getDAO().activateListing(listingId));
		applyListingData(loggedInUser, updatedListing);
		return updatedListing;
	}

	public ListingVO withdrawListing(UserVO loggedInUser, String listingId) {
		ListingVO updatedListing = DtoToVoConverter.convert(getDAO().withdrawListing(listingId));
		applyListingData(loggedInUser, updatedListing);
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
		//createNotification(comment.get, comment.getId(), Type.NEW_COMMENT_FOR_YOUR_LISTING, "");
		return comment;
	}

	public CommentVO updateComment(UserVO loggedInUser, CommentVO comment) {
		if (StringUtils.isEmpty(comment.getComment())) {
			log.warning("Comment '" + comment.getId() + "' cannot be updated with empty text");
			return null;
		}
		comment = DtoToVoConverter.convert(getDAO().updateComment(VoToDtoConverter.convert(comment)));
		return comment;
	}

	public BidVO deleteBid(UserVO loggedInUser, String bidId) {
		BidVO bid = DtoToVoConverter.convert(getDAO().deleteBid(loggedInUser.getId(), bidId));
		scheduleUpdateOfListingStatistics(bid.getListing(), ListingStatsUpdateReason.NONE);
		return bid;
	}

	public BidVO createBid(UserVO loggedInUser, BidVO bid) {
		if (bid.getValue() <= 0) {
			log.warning("Bid cannot be created with non positive value");
			return null;
		}

		bid.setStatus(BidDTO.Status.ACTIVE.toString());
		bid = DtoToVoConverter.convert(getDAO().createBid(loggedInUser.getId(), VoToDtoConverter.convert(bid)));
		if (bid != null) {
			scheduleUpdateOfUserStatistics(loggedInUser.getId(), UserStatsUpdateReason.NEW_BID);
			createNotification(bid.getListingOwner(), bid.getId(), Type.NEW_BID_FOR_YOUR_LISTING, "");
		}
		return bid;
	}

	public BidVO updateBid(UserVO loggedInUser, BidVO bid) {
		if (bid.getValue() <= 0) {
			log.warning("Bid '" + bid.getId() + "' cannot be updated with non positive value");
			return null;
		}
		BidDTO.FundType fundType = BidDTO.FundType.valueOf(bid.getFundType());
		if (fundType != BidDTO.FundType.COMMON
				&& fundType != BidDTO.FundType.NOTE
				&& fundType != BidDTO.FundType.PREFERRED) {
			log.log(Level.WARNING, "Bid id '" + bid.getId() + "' has not valid fund type '" + fundType + "'!");
			return null;
		}

		bid = DtoToVoConverter.convert(getDAO().updateBid(loggedInUser.getId(), VoToDtoConverter.convert(bid)));
		if (bid != null) {
			scheduleUpdateOfListingStatistics(bid.getListing(), ListingStatsUpdateReason.NONE);
			createNotification(bid.getListingOwner(), bid.getId(), Type.NEW_BID_FOR_YOUR_LISTING, "");
		}
		return bid;
	}

	public BidVO activateBid(UserVO loggedInUser, String bidId) {
		BidVO bid = DtoToVoConverter.convert(getDAO().activateBid(loggedInUser.getId(), bidId));
		if (bid != null) {
			scheduleUpdateOfListingStatistics(bid.getListing(), ListingStatsUpdateReason.NEW_BID);
			createNotification(bid.getUser(), bid.getId(), Type.YOUR_BID_WAS_ACTIVATED, "");
		}
		return bid;
	}

	public BidVO withdrawBid(UserVO loggedInUser, String bidId) {
		BidVO bid = DtoToVoConverter.convert(getDAO().withdrawBid(loggedInUser.getId(), bidId));
		if (bid != null) {
			scheduleUpdateOfListingStatistics(bid.getListing(), ListingStatsUpdateReason.NONE);
			createNotification(bid.getListingOwner(), bid.getId(), Type.BID_WAS_WITHDRAWN, "");
		}
		return bid;
	}
	
	public BidVO acceptBid(UserVO loggedInUser, String bidId) {
		BidVO bid = DtoToVoConverter.convert(getDAO().acceptBid(loggedInUser.getId(), bidId));
		if (bid != null) {
			createNotification(bid.getUser(), bid.getId(), Type.YOUR_BID_WAS_ACCEPTED, "");
			createNotification(bid.getListingOwner(), bid.getId(), Type.YOU_ACCEPTED_BID, "");
			scheduleUpdateOfListingStatistics(bid.getListing(), ListingStatsUpdateReason.NONE);
		}
		return bid;
	}

	public BidVO rejectBid(UserVO loggedInUser, String bidId) {
		BidVO bid = DtoToVoConverter.convert(getDAO().rejectBid(loggedInUser.getId(), bidId));
		if (bid != null) {
			createNotification(bid.getUser(), bid.getId(), Type.YOUR_BID_WAS_REJECTED, "");
			scheduleUpdateOfListingStatistics(bid.getListing(), ListingStatsUpdateReason.NONE);
		}
		return bid;
	}

	public BidVO markBidAsPaid(UserVO loggedInUser, String bidId) {
		BidVO bid = DtoToVoConverter.convert(getDAO().markBidAsPaid(loggedInUser.getId(), bidId));
		if (bid != null) {
			createNotification(bid.getUser(), bid.getId(), Type.YOU_PAID_BID, "");
			scheduleUpdateOfListingStatistics(bid.getListing(), ListingStatsUpdateReason.NONE);
		}
		return bid;
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

	public List<ListingDocumentVO> getGoogleDocDocuments() {
		DocService.instance().createFolders();
		return DocService.instance().getAllDocuments();
	}

	public NotificationVO createNotification(UserVO loggedInUser, NotificationVO notification) {
		try {
			NotificationDTO.Type.valueOf(notification.getType().toUpperCase());
		} catch (Exception e) {
			log.log(Level.WARNING, "Notification cannot be created as type is empty or not recognized!", e);
		}
		if (StringUtils.isEmpty(notification.getUser())) {
			log.warning("Notification cannot be created as user is empty!");
		}
		notification.setCreated(new Date());
		notification.setAcknowledged(false);
		notification.setEmailDate(null);
		notification = DtoToVoConverter.convert(getDAO().createNotification(VoToDtoConverter.convert(notification)));
		if (notification != null) {
			scheduleNotification(loggedInUser.getId(), notification);
		}
		return notification;
	}
	
	private void createNotification(String userId, String objectId, Type type, String message) {
		NotificationDTO notification = new NotificationDTO();
		notification.setUser(userId);
		notification.setObject(objectId);
		notification.setType(type);
		notification.setCreated(new Date());
		notification.setAcknowledged(false);
		notification.setEmailDate(null);
		notification = getDAO().createNotification(notification);
		if (notification != null) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notification.getType() + "_" + userId;
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", notification.getIdAsString())
					.taskName(taskName));
		} else {
			log.warning("Can't schedule notification " + notification);
		}
	}

	private void scheduleNotification(String userId, NotificationVO notification) {
		String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notification.getType() + "_" + userId;
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", notification.getId())
				.taskName(taskName));
	}

	public NotificationVO acknowledgeNotification(UserVO loggedInUser, String notifId) {
		NotificationVO notification = DtoToVoConverter.convert(getDAO().acknowledgeNotification(notifId));
		if (notification == null) {
			log.warning("Notification with id '" + notifId + "' not found!");
		} else {
			log.info("Notification with id '" + notifId + "' was acknowledged.");
		}
		return notification;
	}

	public NotificationListVO getAllNotificationsForUser(UserVO loggedInUser, String userId, ListPropertiesVO notifProperties) {
		NotificationListVO list = new NotificationListVO();
		List<NotificationVO> notifications = null;

		UserVO user = getUser(loggedInUser, userId).getUser();
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
			return null;
		}

		notifications = DtoToVoConverter.convertNotifications(getDAO().getUserNotification(userId, notifProperties));
		prepareNotificationList(notifications, user);
		list.setNotifications(notifications);
		list.setNotificationsProperties(notifProperties);
		list.setUser(user);
		
		return list;
	}

	public NotificationListVO getNotificationsForUser(UserVO loggedInUser, String userId, ListPropertiesVO notifProperties) {
		NotificationListVO list = new NotificationListVO();
		List<NotificationVO> notifications = null;

		UserVO user = getUser(loggedInUser, userId).getUser();
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
			return null;
		}

		notifications = DtoToVoConverter.convertNotifications(getDAO().getAllUserNotification(userId, notifProperties));
		notifProperties.setTotalResults(notifications.size());
		prepareNotificationList(notifications, user);
		list.setNotifications(notifications);
		list.setNotificationsProperties(notifProperties);
		list.setUser(user);
		
		return list;
	}

	private void prepareNotificationList(List<NotificationVO> notifications, UserVO user) {
		for (NotificationVO notif : notifications) {
			notif.setUserName(user.getName());
		}
	}

	public NotificationVO getNotification(UserVO loggedInUser, String notifId) {
		NotificationVO notification = DtoToVoConverter.convert(getDAO().getNotification(notifId));
		if (notification == null) {
			log.warning("Notification with id '" + notifId + "' not found!");
		}
		return notification;
	}
	
	public MonitorVO setMonitor(UserVO loggedInUser, MonitorVO monitor) {
		if (StringUtils.isEmpty(monitor.getObjectId())) {
			log.warning("Monitored object id is empty!");
			return null;
		}
		if (StringUtils.isEmpty(monitor.getType())) {
			log.warning("Monitored object type is empty!");
			return null;
		}
		monitor.setActive(true);
		monitor = DtoToVoConverter.convert(getDAO().setMonitor(VoToDtoConverter.convert(monitor)));
		return monitor;
	}

	public MonitorVO deactivateMonitor(UserVO loggedInUser, String monitorId) {
		MonitorVO notification = DtoToVoConverter.convert(getDAO().deactivateMonitor(monitorId));
		if (notification == null) {
			log.warning("Monitor with id '" + monitorId + "' not found!");
		} else {
			log.info("Monitor with id '" + monitorId + "' was deactivated.");
		}
		return notification;
	}

	public MonitorListVO getMonitorsForObject(UserVO loggedInUser, String objectId, String type) {
		MonitorListVO list = new MonitorListVO();
		List<MonitorVO> monitors = null;

		if (StringUtils.isEmpty(objectId)) {
			log.warning("Parameter objectId not provided!");
			return null;
		}
		if (StringUtils.isEmpty(type)) {
			log.warning("Parameter type not provided!");
			return null;
		}
		MonitorDTO.Type typeEnum = null;
		try {
			typeEnum = MonitorDTO.Type.valueOf(type.toUpperCase());
		} catch (Exception e) {
			log.log(Level.WARNING, "Parameter type has wrong value", e);
		}
		monitors = DtoToVoConverter.convertMonitors(getDAO().getMonitorsForObject(objectId, typeEnum));
		list.setMonitors(monitors);
		
		return list;
	}

	public MonitorListVO getMonitorsForUser(UserVO loggedInUser, String userId, String type) {
		MonitorListVO list = new MonitorListVO();
		List<MonitorVO> monitors = null;

		MonitorDTO.Type typeEnum = null;
		if (StringUtils.notEmpty(type)) {
			try {
				typeEnum = MonitorDTO.Type.valueOf(type.toUpperCase());
			} catch (Exception e) {
				log.log(Level.WARNING, "Parameter type has wrong value", e);
			}
		}
		if (StringUtils.isEmpty(userId) && loggedInUser != null) {
			userId = loggedInUser.getId();
		}
		monitors = DtoToVoConverter.convertMonitors(getDAO().getMonitorsForUser(userId, typeEnum));
		list.setMonitors(monitors);
		list.setUser(loggedInUser);
		
		return list;
	}

}
