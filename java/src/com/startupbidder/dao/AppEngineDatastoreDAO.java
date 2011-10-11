package com.startupbidder.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.datanucleus.util.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.ListingDocumentDTO;
import com.startupbidder.dto.ListingStatisticsDTO;
import com.startupbidder.dto.PaidBidDTO;
import com.startupbidder.dto.RankDTO;
import com.startupbidder.dto.SystemPropertyDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.UserStatisticsDTO;
import com.startupbidder.dto.VoToDtoConverter;
import com.startupbidder.dto.VoteDTO;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.UserVO;

/**
 * Datastore implementation which uses Google's AppEngine Datastore.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class AppEngineDatastoreDAO implements DatastoreDAO {
	private static final Logger log = Logger.getLogger(AppEngineDatastoreDAO.class.getName());
	static AppEngineDatastoreDAO instance;
		
	public static DatastoreDAO getInstance() {
		if (instance == null) {
			instance = new AppEngineDatastoreDAO();
		}
		return instance;
	}

	public AppEngineDatastoreDAO() {
	}
	
	public String clearDatastore() {
		return iterateThroughDatastore(true);
	}
	
	public String printDatastoreContents() {
		return iterateThroughDatastore(false);
	}
	
	public String createMockDatastore(UserVO loggedInUser) {
		// delete logged in user as he will be recreated
		UserDTO loggedInUserDTO = VoToDtoConverter.convert(loggedInUser);
		getDatastoreService().delete(loggedInUserDTO.getKey());
		
		initMocks();
		return iterateThroughDatastore(false);
	}
	
	public String iterateThroughDatastore(boolean delete) {
		StringBuffer outputBuffer = new StringBuffer();
		outputBuffer.append("<a href=\"/setup\">Setup page</a>");
		if (delete) {
			outputBuffer.append("<p>Deleted objects:</p>");
		} else {
			outputBuffer.append("<p>Datastore objects:</p>");
		}
		
		outputBuffer.append("<p>Users:</p>");
		ArrayList<Key> keys = new ArrayList<Key>();
		Query query = new UserDTO().getQuery();
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity entity : pq.asIterable()) {
			keys.add(entity.getKey());
			outputBuffer.append(UserDTO.fromEntity(entity).toString()).append("<br/>");
		}
		if (delete) {
			getDatastoreService().delete(keys);
			keys.clear();
		}
		
		outputBuffer.append("<p>Bids:</p>");
		keys = new ArrayList<Key>();
		query = new BidDTO().getQuery();
		pq = getDatastoreService().prepare(query);
		for (Entity entity : pq.asIterable()) {
			keys.add(entity.getKey());
			outputBuffer.append(BidDTO.fromEntity(entity).toString()).append("<br/>");
		}
		if (delete) {
			getDatastoreService().delete(keys);
			keys.clear();
		}
		
		outputBuffer.append("<p>PaidBids:</p>");
		keys = new ArrayList<Key>();
		query = new PaidBidDTO().getQuery();
		pq = getDatastoreService().prepare(query);
		for (Entity entity : pq.asIterable()) {
			keys.add(entity.getKey());
			outputBuffer.append(PaidBidDTO.fromEntity(entity).toString()).append("<br/>");
		}
		if (delete) {
			getDatastoreService().delete(keys);
			keys.clear();
		}
		
		outputBuffer.append("<p>Comments:</p>");
		keys = new ArrayList<Key>();
		query = new CommentDTO().getQuery();
		pq = getDatastoreService().prepare(query);
		for (Entity entity : pq.asIterable()) {
			keys.add(entity.getKey());
			outputBuffer.append(CommentDTO.fromEntity(entity).toString()).append("<br/>");
		}
		if (delete) {
			getDatastoreService().delete(keys);
			keys.clear();
		}
		
		outputBuffer.append("<p>Listing docs:</p>");
		keys = new ArrayList<Key>();
		query = new ListingDocumentDTO().getQuery();
		pq = getDatastoreService().prepare(query);
		for (Entity entity : pq.asIterable()) {
			keys.add(entity.getKey());
			outputBuffer.append(ListingDocumentDTO.fromEntity(entity).toString()).append("<br/>");
		}
		if (delete) {
			getDatastoreService().delete(keys);
			keys.clear();
		}
		
		outputBuffer.append("<p>Listings:</p>");
		keys = new ArrayList<Key>();
		query = new ListingDTO().getQuery();
		pq = getDatastoreService().prepare(query);
		for (Entity entity : pq.asIterable()) {
			keys.add(entity.getKey());
			outputBuffer.append(ListingDTO.fromEntity(entity).toString()).append("<br/>");
		}
		if (delete) {
			getDatastoreService().delete(keys);
			keys.clear();
		}

		outputBuffer.append("<p>Listing statistics:</p>");
		keys = new ArrayList<Key>();
		query = new ListingStatisticsDTO().getQuery();
		pq = getDatastoreService().prepare(query);
		for (Entity entity : pq.asIterable()) {
			keys.add(entity.getKey());
			outputBuffer.append(ListingStatisticsDTO.fromEntity(entity).toString()).append("<br/>");
		}
		if (delete) {
			getDatastoreService().delete(keys);
			keys.clear();
		}
		
		outputBuffer.append("<p>Ranks:</p>");
		keys = new ArrayList<Key>();
		query = new RankDTO().getQuery();
		pq = getDatastoreService().prepare(query);
		for (Entity entity : pq.asIterable()) {
			keys.add(entity.getKey());
			outputBuffer.append(RankDTO.fromEntity(entity).toString()).append("<br/>");
		}
		if (delete) {
			getDatastoreService().delete(keys);
			keys.clear();
		}
		
		outputBuffer.append("<p>System properties:</p>");
		keys = new ArrayList<Key>();
		query = new SystemPropertyDTO().getQuery();
		pq = getDatastoreService().prepare(query);
		for (Entity entity : pq.asIterable()) {
			keys.add(entity.getKey());
			outputBuffer.append(SystemPropertyDTO.fromEntity(entity).toString()).append("<br/>");
		}
		if (delete) {
			getDatastoreService().delete(keys);
			keys.clear();
		}
		
		outputBuffer.append("<p>User statistics:</p>");
		keys = new ArrayList<Key>();
		query = new UserStatisticsDTO().getQuery();
		pq = getDatastoreService().prepare(query);
		for (Entity entity : pq.asIterable()) {
			keys.add(entity.getKey());
			outputBuffer.append(UserStatisticsDTO.fromEntity(entity).toString()).append("<br/>");
		}
		if (delete) {
			getDatastoreService().delete(keys);
			keys.clear();
		}
		
		outputBuffer.append("<p>Votes:</p>");
		keys = new ArrayList<Key>();
		query = new VoteDTO().getQuery();
		pq = getDatastoreService().prepare(query);
		for (Entity entity : pq.asIterable()) {
			keys.add(entity.getKey());
			outputBuffer.append(VoteDTO.fromEntity(entity).toString()).append("<br/>");
		}
		if (delete) {
			getDatastoreService().delete(keys);
			keys.clear();
		}
		
		outputBuffer.append("<a href=\"/setup\">Setup page</a>");
		return outputBuffer.toString();
	}
	
	private void initMocks() {
		for (UserDTO user : ((MockDatastoreDAO)MockDatastoreDAO.getInstance()).userCache.values()) {
			getDatastoreService().put(user.toEntity());
		}
		for (ListingDTO listing : ((MockDatastoreDAO)MockDatastoreDAO.getInstance()).lCache.values()) {
			getDatastoreService().put(listing.toEntity());
		}
		for (VoteDTO vote : ((MockDatastoreDAO)MockDatastoreDAO.getInstance()).voteCache.values()) {
			getDatastoreService().put(vote.toEntity());
		}
		for (CommentDTO comment : ((MockDatastoreDAO)MockDatastoreDAO.getInstance()).commentCache.values()) {
			getDatastoreService().put(comment.toEntity());
		}
		for (BidDTO bid : ((MockDatastoreDAO)MockDatastoreDAO.getInstance()).bidCache.values()) {
			getDatastoreService().put(bid.toEntity());
		}
		for (SystemPropertyDTO sp : ((MockDatastoreDAO)MockDatastoreDAO.getInstance()).propCache.values()) {
			getDatastoreService().put(sp.toEntity());
		}
		for (ListingDocumentDTO ld : ((MockDatastoreDAO)MockDatastoreDAO.getInstance()).docCache.values()) {
			getDatastoreService().put(ld.toEntity());
		}

		// updating user stats
		for (UserDTO user : ((MockDatastoreDAO)MockDatastoreDAO.getInstance()).userCache.values()) {
			updateUserStatistics(user.getIdAsString());
		}
		// update listing stats
		for (ListingDTO listing : ((MockDatastoreDAO)MockDatastoreDAO.getInstance()).lCache.values()) {
			updateListingStatistics(listing.getIdAsString());
		}
	}
	
	private DatastoreService getDatastoreService() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		return datastore;
	}

	@Override
	public UserDTO getUser(String userId) {
		UserDTO user = new UserDTO();
		user.setIdFromString(userId);
		
		Entity userEntity;
		try {
			userEntity = getDatastoreService().get(user.getKey());
			user = UserDTO.fromEntity(userEntity);
			return user;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Entity '" + user.getKey() + "'not found", e);
			return null;
		}
	}

	@Override
	public UserDTO getUserByOpenId(String openId) {
		UserDTO user = new UserDTO();
		Query query = user.getQuery();
		query.addFilter(UserDTO.OPEN_ID, FilterOperator.EQUAL, openId);
		PreparedQuery pq = getDatastoreService().prepare(query);
		List<Entity> users = pq.asList(FetchOptions.Builder.withDefaults());
		if (users.size() > 1) {
			String msg = "Multiple users associated with openId: " + openId + ". ";
			for (Entity userEntity : users) {
				msg += UserDTO.fromEntity(userEntity).toString();
			}
			log.severe(msg);
		}
		if (users.size() == 0) {
			return null;
		} else {
			user = UserDTO.fromEntity(users.get(users.size() - 1));
			return user;
		}
	}

	@Override
	public UserDTO createUser(String userId, String email, String nickname) {
		UserDTO user = new UserDTO();
		Query query = user.getQuery();
		query.addFilter(UserDTO.EMAIL, FilterOperator.EQUAL, email);
		PreparedQuery pq = getDatastoreService().prepare(query);
		if (pq.countEntities(FetchOptions.Builder.withDefaults()) > 0) {
			log.warning("User with email '" + email + "' already exists!");
			return null;
		}

		user.createKey(userId);
		user.setOpenId(userId);
		user.setNickname(nickname != null ? nickname : "" + userId);
		if (email.contains("@")) {
			String name = email.substring(0, email.indexOf("@"));
			user.setName(name);
		} else {
			user.setName("<not set>");
		}
		user.setEmail(email);
		//user.setFacebook("");
		user.setJoined(new Date());
		user.setLastLoggedIn(new Date());
		user.setModified(new Date());
		//user.setOrganization("org_" + key);
		//user.setTitle("Dr");
		//user.setTwitter("twit_" + key);
		//user.setLinkedin("ln_" + key);
		user.setStatus(UserDTO.Status.ACTIVE);
		
		getDatastoreService().put(user.toEntity());

		return user;
	}

	@Override
	public UserStatisticsDTO updateUserStatistics(String userId) {
		UserDTO user = new UserDTO();
		user.setIdFromString(userId);
		try {
			user = UserDTO.fromEntity(getDatastoreService().get(user.getKey()));
		} catch (EntityNotFoundException e) {
			log.severe("User with id '" + userId + "' doesn't exist!");
		}
		
		UserStatisticsDTO userStats = new UserStatisticsDTO();
		userStats.createKey(userId);
		userStats.setUser(userId);
		userStats.setStatus(user.getStatus().toString());
		
		log.info("Updating user statistics, user: " + userId);

		Query query = new BidDTO().getQuery().setKeysOnly();
		query.addFilter(BidDTO.USER, Query.FilterOperator.EQUAL, userId);
		query.addFilter(BidDTO.STATUS, Query.FilterOperator.EQUAL, BidDTO.Status.ACTIVE.toString());
		PreparedQuery pq = getDatastoreService().prepare(query);
		userStats.setNumberOfBids(pq.countEntities(FetchOptions.Builder.withDefaults()));
		
		// calculating accepted (and paid) bids for user's listings
		query = new BidDTO().getQuery().setKeysOnly();
		query.addFilter(BidDTO.LISTING_OWNER, Query.FilterOperator.EQUAL, userId);
		query.addFilter(BidDTO.STATUS, Query.FilterOperator.EQUAL, BidDTO.Status.ACCEPTED.toString());
		pq = getDatastoreService().prepare(query);
		userStats.setNumberOfAcceptedBids(pq.countEntities(FetchOptions.Builder.withDefaults()));
		
		query = new BidDTO().getQuery();
		query.addFilter(BidDTO.LISTING_OWNER, Query.FilterOperator.EQUAL, userId);
		query.addFilter(BidDTO.STATUS, Query.FilterOperator.EQUAL, BidDTO.Status.ACCEPTED.toString());
		query.addSort(BidDTO.VALUATION, Query.SortDirection.DESCENDING);
		pq = getDatastoreService().prepare(query);
		BidDTO bidDTO = null;
		long sumOfAcceptedBids = 0L;
		for (Entity bid : pq.asIterable(FetchOptions.Builder.withDefaults())) {
			bidDTO = BidDTO.fromEntity(bid);
			sumOfAcceptedBids += bidDTO.getValuation();
		}
		userStats.setSumOfAcceptedBids(sumOfAcceptedBids);

		// calculating bids funded by user
		query = new BidDTO().getQuery().setKeysOnly();
		query.addFilter(BidDTO.USER, Query.FilterOperator.EQUAL, userId);
		query.addFilter(BidDTO.STATUS, Query.FilterOperator.EQUAL, BidDTO.Status.ACCEPTED.toString());
		pq = getDatastoreService().prepare(query);
		userStats.setNumberOfFundedBids(pq.countEntities(FetchOptions.Builder.withDefaults()));

		query = new BidDTO().getQuery();
		query.addFilter(BidDTO.USER, Query.FilterOperator.EQUAL, userId);
		pq = getDatastoreService().prepare(query);
		long sumOfBids = 0L;
		long sumOfFoundedBids = 0L;
		for (Entity bid : pq.asIterable(FetchOptions.Builder.withDefaults())) {
			bidDTO = BidDTO.fromEntity(bid);
			sumOfBids += bidDTO.getValuation();
			if (bidDTO.getStatus() == BidDTO.Status.ACCEPTED) {
				sumOfFoundedBids += bidDTO.getValuation();
			}
		}
		userStats.setSumOfBids(sumOfBids);
		userStats.setSumOfFundedBids(sumOfFoundedBids);
		
		query = new CommentDTO().getQuery().setKeysOnly();
		query.addFilter(CommentDTO.USER, Query.FilterOperator.EQUAL, userId);
		pq = getDatastoreService().prepare(query);
		userStats.setNumberOfComments(pq.countEntities(FetchOptions.Builder.withDefaults()));
		
		query = new ListingDTO().getQuery().setKeysOnly();
		query.addFilter(ListingDTO.OWNER, Query.FilterOperator.EQUAL, userId);
		query.addFilter(ListingDTO.STATE, Query.FilterOperator.NOT_EQUAL, ListingDTO.State.CREATED.toString());
		query.addSort(ListingDTO.STATE);
		query.addSort(ListingDTO.OWNER);
		pq = getDatastoreService().prepare(query);
		userStats.setNumberOfListings(pq.countEntities(FetchOptions.Builder.withDefaults()));
		
		query = new VoteDTO().getQuery().setKeysOnly();
		query.addFilter(VoteDTO.VOTER, Query.FilterOperator.EQUAL, userId);
		pq = getDatastoreService().prepare(query);
		userStats.setNumberOfVotesAdded(pq.countEntities(FetchOptions.Builder.withDefaults()));

		query = new VoteDTO().getQuery().setKeysOnly();
		query.addFilter(VoteDTO.USER, Query.FilterOperator.EQUAL, userId);
		pq = getDatastoreService().prepare(query);
		userStats.setNumberOfVotes(pq.countEntities(FetchOptions.Builder.withDefaults()));

		log.info("user: " + userId + ", statistics: " + userStats);

		getDatastoreService().put(userStats.toEntity());
		
		return userStats;
	}
	
	@Override
	public UserStatisticsDTO getUserStatistics(String userId) {
		UserStatisticsDTO userStats = new UserStatisticsDTO();
		userStats.createKey(userId);
		
		try {
			Entity userStatsEntity = getDatastoreService().get(userStats.getKey());
			userStats = UserStatisticsDTO.fromEntity(userStatsEntity);
			return userStats;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "User statistics entity '" + userStats.getKey() + "'not found", e);
			return null;
		}
	}

	@Override
	public ListingStatisticsDTO updateListingStatistics(String listingId) {
		ListingDTO listing = new ListingDTO();
		listing.setIdFromString(listingId);
		try {
			listing = ListingDTO.fromEntity(getDatastoreService().get(listing.getKey()));
		} catch (EntityNotFoundException e) {
			log.severe("Listing with id '" + listingId + "' doesn't exist!");
		}

		ListingStatisticsDTO listingStats = new ListingStatisticsDTO();
		Query query = listingStats.getQuery();
		query.addFilter(ListingStatisticsDTO.LISTING, Query.FilterOperator.EQUAL, listingId);
		PreparedQuery pq = getDatastoreService().prepare(query);
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
		if (list.size() > 0) {
			listingStats = ListingStatisticsDTO.fromEntity(pq.asSingleEntity());
			DateMidnight lastStatMidnight = new DateMidnight(listingStats.getPreviousValuationDate().getTime());
			DateMidnight midnight = new DateMidnight();
			if (lastStatMidnight.isBefore(midnight)) {
				listingStats.setPreviousValuation(listingStats.getValuation());
				listingStats.setPreviousValuationDate(listingStats.getDate());
			}
		} else {
			listingStats.createKey(listingId);
			listingStats.setListing(listingId);
			listingStats.setPreviousValuation(listing.getSuggestedValuation());
			listingStats.setPreviousValuationDate(listing.getListedOn());
		}
		
		listingStats.setStatus(listing.getState().toString());
		
		query = new BidDTO().getQuery().setKeysOnly();
		query.addFilter(BidDTO.LISTING, Query.FilterOperator.EQUAL, listingId);
		pq = getDatastoreService().prepare(query);
		listingStats.setNumberOfBids(pq.countEntities(FetchOptions.Builder.withDefaults()));
		
		query = new CommentDTO().getQuery().setKeysOnly();
		query.addFilter(CommentDTO.LISTING, Query.FilterOperator.EQUAL, listingId);
		pq = getDatastoreService().prepare(query);
		listingStats.setNumberOfComments(pq.countEntities(FetchOptions.Builder.withDefaults()));
		
		query = new VoteDTO().getQuery().setKeysOnly();
		query.addFilter(VoteDTO.LISTING, Query.FilterOperator.EQUAL, listingId);
		pq = getDatastoreService().prepare(query);
		listingStats.setNumberOfVotes(pq.countEntities(FetchOptions.Builder.withDefaults()));

		// calculate valuation for listing (max accepted bid or suggested valuation)
		query = new BidDTO().getQuery();
		query.addFilter(BidDTO.LISTING, Query.FilterOperator.EQUAL, listingId);
		query.addFilter(BidDTO.STATUS, Query.FilterOperator.EQUAL, BidDTO.Status.ACCEPTED.toString());
		query.addSort(BidDTO.VALUATION, Query.SortDirection.DESCENDING);
		pq = getDatastoreService().prepare(query);
		if (pq.asIterator().hasNext()) {
			BidDTO bid = BidDTO.fromEntity(pq.asIterator().next());
			listingStats.setValuation(bid.getValuation());
		} else {
			listingStats.setValuation(listing.getSuggestedValuation());
		}
		
		// calculate median for bids and set total number of bids
		List<Integer> values = new ArrayList<Integer>();
		List<BidDTO> bids = getBidsForListing(listingId);
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
		listingStats.setMedianValuation(median);
		
		double timeFactor = Math.pow((double)(Days.daysBetween(new DateTime(listing.getListedOn()), new DateTime()).getDays() + 2), 1.5d);
		double score = (listingStats.getNumberOfVotes() + listingStats.getNumberOfComments() + listingStats.getNumberOfVotes() + median) / timeFactor;
		listingStats.setScore(score);
		
		listingStats.setDate(new Date());
		log.info("listing: " + listingId + ", statistics: " + listingStats);
		getDatastoreService().put(listingStats.toEntity());
		
		return listingStats;
	}
	
	public ListingStatisticsDTO getListingStatistics(String listingId) {
		ListingStatisticsDTO listingStats = new ListingStatisticsDTO();
		Query query = listingStats.getQuery();
		query.addFilter(ListingStatisticsDTO.LISTING, Query.FilterOperator.EQUAL, listingId);
		PreparedQuery pq = getDatastoreService().prepare(query);
		if (pq.countEntities(FetchOptions.Builder.withDefaults()) > 0) {
			listingStats = ListingStatisticsDTO.fromEntity(pq.asSingleEntity());
			return listingStats;
		} else {
			log.log(Level.WARNING, "User statistics entity for listing '" + listingId + "' not found!");
			return null;
		}
	}

	@Override
	public UserDTO updateUser(UserDTO newUser) {
		try {
			Entity userEntity = getDatastoreService().get(newUser.getKey());
			
			UserDTO user = UserDTO.fromEntity(userEntity);
			user.setEmail(newUser.getEmail());
			user.setFacebook(newUser.getFacebook());
			user.setInvestor(newUser.isInvestor());
			user.setLinkedin(newUser.getLinkedin());
			user.setName(newUser.getName());
			user.setNickname(newUser.getNickname());
			user.setOrganization(newUser.getOrganization());
			user.setTitle(newUser.getTitle());
			user.setTwitter(newUser.getTwitter());
			user.setModified(new Date(System.currentTimeMillis()));
			
			getDatastoreService().put(user.toEntity());
			log.log(Level.INFO, "Updated user: " + user);
			return user;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Entity '" + newUser.getKey() + "'not found", e);
			return null;
		}		
	}

	@Override
	public List<UserDTO> getAllUsers() {
		List<UserDTO> users = new ArrayList<UserDTO>();
		
		Query query = new UserDTO().getQuery();
		query.addSort(UserDTO.NICKNAME, Query.SortDirection.ASCENDING);
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity user : pq.asIterable(FetchOptions.Builder.withLimit(1000))) {
			users.add(UserDTO.fromEntity(user));
		}
		return users;
	}

	@Override
	public UserDTO getTopInvestor() {
		Query query = new UserStatisticsDTO().getQuery();
		query.addFilter(UserStatisticsDTO.STATUS, Query.FilterOperator.EQUAL, UserDTO.Status.ACTIVE.toString());
		query.addSort(UserStatisticsDTO.SUM_OF_BIDS, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		UserStatisticsDTO userStats = UserStatisticsDTO.fromEntity(
				pq.asIterator(FetchOptions.Builder.withLimit(1)).next());
		UserDTO user = new UserDTO();
		user.setIdFromString(userStats.getUser());
		try {
			user = UserDTO.fromEntity(getDatastoreService().get(user.getKey()));
		} catch (EntityNotFoundException e) {
			log.warning("User with id '" + userStats.getUser() + "' not found!");
			user = null;
		}
		return user;
	}

	@Override
	public List<VoteDTO> getUserVotes(String userId) {
		List<VoteDTO> votes = new ArrayList<VoteDTO>();
		Query query = new VoteDTO().getQuery();
		query.addFilter(VoteDTO.USER, Query.FilterOperator.EQUAL, userId);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity vote : pq.asIterable(FetchOptions.Builder.withLimit(1000))) {
			votes.add(VoteDTO.fromEntity(vote));
		}
		return new ArrayList<VoteDTO>();
	}

	@Override
	public ListingDTO createListing(ListingDTO listing) {
		listing.createKey(listing.getName() + listing.getOwner());
		listing.setState(ListingDTO.State.CREATED);
		listing.setListedOn(new Date());
		
		getDatastoreService().put(listing.toEntity());
		return listing;
	}

	@Override
	public ListingDTO updateListing(ListingDTO newListing) {
		Entity listingEntity;
		try {
			listingEntity = getDatastoreService().get(newListing.getKey());
			ListingDTO listing = ListingDTO.fromEntity(listingEntity);
			
			listing.setName(newListing.getName());
			listing.setSuggestedAmount(newListing.getSuggestedAmount());
			listing.setSuggestedPercentage(newListing.getSuggestedPercentage());
			listing.setSuggestedValuation(newListing.getSuggestedValuation());
			listing.setBusinessPlanId(newListing.getBusinessPlanId());
			listing.setPresentationId(newListing.getPresentationId());
			listing.setFinancialsId(newListing.getFinancialsId());
			listing.setSummary(newListing.getSummary());
		
			getDatastoreService().put(listing.toEntity());
			return listing;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Listing entity name '" + newListing.getName()
					+ "', id '" + newListing.getKey() + "' not found", e);
			return null;
		}
	}

	@Override
	public ListingDTO getListing(String listingId) {
		ListingDTO listing = new ListingDTO();
		listing.setIdFromString(listingId);
		try {
			Entity listingEntity = getDatastoreService().get(listing.getKey());
			return ListingDTO.fromEntity(listingEntity);
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Listing entity '" + listingId + "'not found", e);
			return null;
		}
	}

	@Override
	public List<ListingDTO> getAllListings() {
		List<ListingDTO> listings = new ArrayList<ListingDTO>();
		
		Query query = new ListingDTO().getQuery();
		query.addSort(ListingDTO.LISTED_ON, Query.SortDirection.ASCENDING);
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity user : pq.asIterable(FetchOptions.Builder.withLimit(1000))) {
			listings.add(ListingDTO.fromEntity(user));
		}
		return listings;
	}

	@Override
	public List<ListingDTO> getUserActiveListings(String userId, ListPropertiesVO listingProperties) {
		List<ListingDTO> listings = new ArrayList<ListingDTO>();
		
		Query query = new ListingDTO().getQuery();
		query.addFilter(ListingDTO.OWNER, Query.FilterOperator.EQUAL, userId);
		query.addFilter(ListingDTO.STATE, Query.FilterOperator.EQUAL, ListingDTO.State.ACTIVE.toString());
		query.addSort(ListingDTO.LISTED_ON, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity listing : pq.asIterable(FetchOptions.Builder.withLimit(listingProperties.getMaxResults()))) {
			listings.add(ListingDTO.fromEntity(listing));
		}
		return listings;
	}

	@Override
	public List<ListingDTO> getUserListings(String userId, ListPropertiesVO listingProperties) {
		List<ListingDTO> listings = new ArrayList<ListingDTO>();
		
		Query query = new ListingDTO().getQuery();
		query.addFilter(ListingDTO.OWNER, Query.FilterOperator.EQUAL, userId);
		query.addSort(ListingDTO.LISTED_ON, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity listing : pq.asIterable(FetchOptions.Builder.withLimit(listingProperties.getMaxResults()))) {
			listings.add(ListingDTO.fromEntity(listing));
		}
		return listings;
	}

	@Override
	public List<ListingDTO> getTopListings(ListPropertiesVO listingProperties) {
		Query query = new ListingStatisticsDTO().getQuery();
		query.addFilter(ListingStatisticsDTO.STATUS, Query.FilterOperator.EQUAL, ListingDTO.State.ACTIVE.toString());
		query.addSort(ListingStatisticsDTO.SCORE, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		List<Key> listingKeys = new ArrayList<Key>();
		ListingDTO listingDTO = new ListingDTO();
		for (Entity listing : pq.asIterable(FetchOptions.Builder.withLimit(listingProperties.getMaxResults()))) {
			String listingId = ListingStatisticsDTO.fromEntity(listing).getListing();
			listingDTO.setIdFromString(listingId);
			listingKeys.add(listingDTO.getKey());
		}
		log.info("Top listing ids: " + listingKeys);
		
		Map<Key, Entity> listingMap = getDatastoreService().get(listingKeys);
		log.info("Top listings map: " + listingMap);
		List<ListingDTO> listings = new ArrayList<ListingDTO>();
		for (Key listingKey : listingKeys) {
			Entity listingEntity = listingMap.get(listingKey);
			if (listingEntity != null) {
				listings.add(ListingDTO.fromEntity(listingEntity));
			}
		}
		return listings;
	}

	@Override
	public List<ListingDTO> getActiveListings(ListPropertiesVO listingProperties) {
		List<ListingDTO> listings = new ArrayList<ListingDTO>();
		
		Query query = new ListingDTO().getQuery();
		query.addFilter(ListingDTO.STATE, Query.FilterOperator.EQUAL, ListingDTO.State.ACTIVE.toString());
		query.addSort(ListingDTO.LISTED_ON, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity listing : pq.asIterable(FetchOptions.Builder.withLimit(listingProperties.getMaxResults()))) {
			listings.add(ListingDTO.fromEntity(listing));
		}
		return listings;
	}

	@Override
	public List<ListingDTO> getMostValuedListings(ListPropertiesVO listingProperties) {
		Query query = new ListingStatisticsDTO().getQuery();
		query.addFilter(ListingStatisticsDTO.STATUS, Query.FilterOperator.NOT_EQUAL, ListingDTO.State.CREATED.toString());
		query.addSort(ListingStatisticsDTO.STATUS);
		query.addSort(ListingStatisticsDTO.VALUATION, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		List<Key> listingKeys = new ArrayList<Key>();
		ListingDTO listingDTO = new ListingDTO();
		for (Entity listing : pq.asIterable(FetchOptions.Builder.withLimit(listingProperties.getMaxResults()))) {
			String listingId = ListingStatisticsDTO.fromEntity(listing).getListing();
			listingDTO.setIdFromString(listingId);
			listingKeys.add(listingDTO.getKey());
		}
		log.info("Most valued listing ids: " + listingKeys);
		
		Map<Key, Entity> listingMap = getDatastoreService().get(listingKeys);
		log.info("Most valued listings map: " + listingMap);
		List<ListingDTO> listings = new ArrayList<ListingDTO>();
		for (Key listingKey : listingKeys) {
			Entity listingEntity = listingMap.get(listingKey);
			if (listingEntity != null) {
				listings.add(ListingDTO.fromEntity(listingEntity));
			}
		}
		return listings;
	}

	@Override
	public List<ListingDTO> getMostDiscussedListings(ListPropertiesVO listingProperties) {
		Query query = new ListingStatisticsDTO().getQuery();
		query.addFilter(ListingStatisticsDTO.STATUS, Query.FilterOperator.NOT_EQUAL, ListingDTO.State.CREATED.toString());
		query.addSort(ListingStatisticsDTO.STATUS);
		query.addSort(ListingStatisticsDTO.NUM_OF_COMMENTS, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		List<Key> listingKeys = new ArrayList<Key>();
		ListingDTO listingDTO = new ListingDTO();
		for (Entity listing : pq.asIterable(FetchOptions.Builder.withLimit(listingProperties.getMaxResults()))) {
			String listingId = ListingStatisticsDTO.fromEntity(listing).getListing();
			listingDTO.setIdFromString(listingId);
			listingKeys.add(listingDTO.getKey());
		}
		log.info("Most discussed listing ids: " + listingKeys);
		
		Map<Key, Entity> listingMap = getDatastoreService().get(listingKeys);
		log.info("Most discussed listings map: " + listingMap);
		List<ListingDTO> listings = new ArrayList<ListingDTO>();
		for (Key listingKey : listingKeys) {
			Entity listingEntity = listingMap.get(listingKey);
			if (listingEntity != null) {
				listings.add(ListingDTO.fromEntity(listingEntity));
			}
		}
		return listings;
	}

	@Override
	public List<ListingDTO> getMostPopularListings(ListPropertiesVO listingProperties) {
		Query query = new ListingStatisticsDTO().getQuery();
		query.addFilter(ListingStatisticsDTO.STATUS, Query.FilterOperator.NOT_EQUAL, ListingDTO.State.CREATED.toString());
		query.addSort(ListingStatisticsDTO.STATUS);
		query.addSort(ListingStatisticsDTO.NUM_OF_VOTES, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		List<Key> listingKeys = new ArrayList<Key>();
		ListingDTO listingDTO = new ListingDTO();
		for (Entity listing : pq.asIterable(FetchOptions.Builder.withLimit(listingProperties.getMaxResults()))) {
			String listingId = ListingStatisticsDTO.fromEntity(listing).getListing();
			listingDTO.setIdFromString(listingId);
			listingKeys.add(listingDTO.getKey());
		}
		log.info("Most popular listing ids: " + listingKeys);
		
		Map<Key, Entity> listingMap = getDatastoreService().get(listingKeys);
		log.info("Most popular listings map: " + listingMap);
		List<ListingDTO> listings = new ArrayList<ListingDTO>();
		for (Key listingKey : listingKeys) {
			Entity listingEntity = listingMap.get(listingKey);
			if (listingEntity != null) {
				listings.add(ListingDTO.fromEntity(listingEntity));
			}
		}
		return listings;
	}

	@Override
	public List<ListingDTO> getLatestListings(ListPropertiesVO listingProperties) {
		List<ListingDTO> listings = new ArrayList<ListingDTO>();
		
		Query query = new ListingDTO().getQuery();
		query.addFilter(ListingDTO.STATE, Query.FilterOperator.NOT_EQUAL, ListingDTO.State.CREATED.toString());
		query.addSort(ListingDTO.STATE);
		query.addSort(ListingDTO.LISTED_ON, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity listing : pq.asIterable(FetchOptions.Builder.withLimit(listingProperties.getMaxResults()))) {
			listings.add(ListingDTO.fromEntity(listing));
		}
		return listings;
	}

	@Override
	public List<ListingDTO> getClosingListings(ListPropertiesVO listingProperties) {
		List<ListingDTO> listings = new ArrayList<ListingDTO>();
		
		Query query = new ListingDTO().getQuery();
		query.addFilter(ListingDTO.STATE, Query.FilterOperator.EQUAL, ListingDTO.State.ACTIVE.toString());
		query.addSort(ListingDTO.CLOSING_ON, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity listing : pq.asIterable(FetchOptions.Builder.withLimit(listingProperties.getMaxResults()))) {
			listings.add(ListingDTO.fromEntity(listing));
		}
		return listings;
	}

	@Override
	public ListingDTO activateListing(String listingId) {
		try {
			ListingDTO listing = new ListingDTO();
			listing.setIdFromString(listingId);
			Entity listingEntity = getDatastoreService().get(listing.getKey());
			listing = ListingDTO.fromEntity(listingEntity);
			
			listing.setState(ListingDTO.State.ACTIVE);
		
			getDatastoreService().put(listing.toEntity());
			return listing;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Listing entity '" + listingId + "'not found", e);
			return null;
		}
	}

	@Override
	public ListingDTO withdrawListing(String listingId) {
		try {
			ListingDTO listing = new ListingDTO();
			listing.setIdFromString(listingId);
			Entity listingEntity = getDatastoreService().get(listing.getKey());
			listing = ListingDTO.fromEntity(listingEntity);
			
			listing.setState(ListingDTO.State.WITHDRAWN);
		
			getDatastoreService().put(listing.toEntity());
			return listing;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Listing entity '" + listingId + "'not found", e);
			return null;
		}
	}

	@Override
	public ListingDTO valueUpListing(String listingId, String voterId) {
		try {
			ListingDTO listing = new ListingDTO();
			listing.setIdFromString(listingId);
			Entity listingEntity = getDatastoreService().get(listing.getKey());
			listing = ListingDTO.fromEntity(listingEntity);
			
			if (!StringUtils.areStringsEqual(listing.getOwner(), voterId)) {
				VoteDTO vote = new VoteDTO();
				Query query = vote.getQuery().setKeysOnly();
				query.addFilter(VoteDTO.LISTING, Query.FilterOperator.EQUAL, listingId);
				query.addFilter(VoteDTO.VOTER, Query.FilterOperator.EQUAL, voterId);
				PreparedQuery pq = getDatastoreService().prepare(query);
				if (pq.countEntities(FetchOptions.Builder.withDefaults()) == 0) {
					vote.setListing(listingId);
					vote.setUser(null);
					vote.setVoter(voterId);
					vote.setValue(1);
					vote.setCommentedOn(new Date());
					vote.createKey(listingId + voterId + vote.getCommentedOn().getTime());
					
					getDatastoreService().put(vote.toEntity());
					log.info("User '" + voterId + "' voted for listing '" + listingId + "'");
					
					return listing;					
				} else {
					log.log(Level.WARNING, "User '" + voterId + "' has already voted for listing '" + listingId + "'");
				}
			} else {
				log.log(Level.WARNING, "User '" + voterId + "' owns listing '" + listingId + "', cannot vote");
			}
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Listing entity '" + listingId + "'not found", e);
		}
		return null;
	}

	@Override
	public UserDTO valueUpUser(String userId, String voterId) {
		try {
			UserDTO user = new UserDTO();
			user.setIdFromString(userId);
			Entity userEntity = getDatastoreService().get(user.getKey());
			user = UserDTO.fromEntity(userEntity);
			
			if (!StringUtils.areStringsEqual(user.getIdAsString(), voterId)) {
				VoteDTO vote = new VoteDTO();
				Query query = vote.getQuery().setKeysOnly();
				query.addFilter(VoteDTO.USER, Query.FilterOperator.EQUAL, userId);
				query.addFilter(VoteDTO.VOTER, Query.FilterOperator.EQUAL, voterId);
				PreparedQuery pq = getDatastoreService().prepare(query);
				if (pq.countEntities(FetchOptions.Builder.withDefaults()) == 0) {
					vote.setUser(userId);
					vote.setListing(null);
					vote.setVoter(voterId);
					vote.setValue(1);
					vote.setCommentedOn(new Date());
					vote.createKey(userId + voterId + vote.getCommentedOn().getTime());
					
					getDatastoreService().put(vote.toEntity());
					log.info("User '" + voterId + "' voted for user '" + userId + "'");
					
					return user;
				} else {
					log.log(Level.WARNING, "User '" + voterId + "' has already voted for user '" + userId + "'");
				}
			} else {
				log.log(Level.WARNING, "User '" + userId + "' cannot vote for himself/herself");
			}
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "User entity '" + userId + "'not found", e);
		}
		return null;
	}
	
	@Override
	public List<CommentDTO> getCommentsForListing(String listingId) {
		List<CommentDTO> comments = new ArrayList<CommentDTO>();
		
		Query query = new CommentDTO().getQuery();
		query.addFilter(CommentDTO.LISTING, Query.FilterOperator.EQUAL, listingId);
		query.addSort(CommentDTO.COMMENTED_ON, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity comment : pq.asIterable()) {
			comments.add(CommentDTO.fromEntity(comment));
		}
		return comments;
	}

	@Override
	public List<CommentDTO> getCommentsForUser(String userId) {
		List<CommentDTO> comments = new ArrayList<CommentDTO>();
		
		Query query = new CommentDTO().getQuery();
		query.addFilter(CommentDTO.USER, Query.FilterOperator.EQUAL, userId);
		query.addSort(CommentDTO.COMMENTED_ON, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity comment : pq.asIterable()) {
			comments.add(CommentDTO.fromEntity(comment));
		}
		return comments;
	}

	@Override
	public List<BidDTO> getBidsForListing(String listingId) {
		List<BidDTO> bids = new ArrayList<BidDTO>();
		
		Query query = new BidDTO().getQuery();
		query.addFilter(BidDTO.LISTING, Query.FilterOperator.EQUAL, listingId);
		query.addSort(BidDTO.PLACED, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity bid : pq.asIterable()) {
			bids.add(BidDTO.fromEntity(bid));
		}
		return bids;
	}

	@Override
	public List<BidDTO> getBidsForUser(String userId) {
		List<BidDTO> bids = new ArrayList<BidDTO>();
		
		Query query = new BidDTO().getQuery();
		query.addFilter(BidDTO.USER, Query.FilterOperator.EQUAL, userId);
		query.addSort(BidDTO.PLACED, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity bid : pq.asIterable()) {
			bids.add(BidDTO.fromEntity(bid));
		}
		return bids;
	}
	
	public List<BidDTO> getBidsAcceptedByUser(String userId) {
		List<BidDTO> bids = new ArrayList<BidDTO>();
		
		Query query = new BidDTO().getQuery();
		query.addFilter(BidDTO.LISTING_OWNER, Query.FilterOperator.EQUAL, userId);
		query.addFilter(BidDTO.STATUS, Query.FilterOperator.EQUAL, BidDTO.Status.ACCEPTED.toString());
		query.addSort(BidDTO.PLACED, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity bid : pq.asIterable()) {
			bids.add(BidDTO.fromEntity(bid));
		}
		return bids;
	}

	public List<BidDTO> getBidsFundedByUser(String userId) {
		List<BidDTO> bids = new ArrayList<BidDTO>();
		
		Query query = new BidDTO().getQuery();
		query.addFilter(BidDTO.USER, Query.FilterOperator.EQUAL, userId);
		query.addFilter(BidDTO.STATUS, Query.FilterOperator.EQUAL, BidDTO.Status.ACCEPTED.toString());
		query.addSort(BidDTO.PLACED, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity bid : pq.asIterable()) {
			bids.add(BidDTO.fromEntity(bid));
		}
		return bids;
	}

	@Override
	public int getNumberOfVotesForListing(String listingId) {
		Query query = new VoteDTO().getQuery().setKeysOnly();
		query.addFilter(VoteDTO.LISTING, Query.FilterOperator.EQUAL, listingId);
		PreparedQuery pq = getDatastoreService().prepare(query);
		return pq.countEntities(FetchOptions.Builder.withDefaults());
	}

	@Override
	public int getNumberOfVotesForUser(String userId) {
		Query query = new VoteDTO().getQuery().setKeysOnly();
		query.addFilter(VoteDTO.USER, Query.FilterOperator.EQUAL, userId);
		PreparedQuery pq = getDatastoreService().prepare(query);
		return pq.countEntities(FetchOptions.Builder.withDefaults());
	}
	
	@Override
	public int getActivity(String listingId) {
		Query query = new CommentDTO().getQuery().setKeysOnly();
		query.addFilter(CommentDTO.LISTING, Query.FilterOperator.EQUAL, listingId);
		PreparedQuery pq = getDatastoreService().prepare(query);
		return pq.countEntities(FetchOptions.Builder.withDefaults());
	}

	@Override
	public BidDTO getBid(String bidId) {
		BidDTO bid = new BidDTO();
		bid.setIdFromString(bidId);
		log.info("Bid's id created from string '" + bidId + "' is: " + bid.getKey());
		try {
			Entity bidEntity = getDatastoreService().get(bid.getKey());
			bid = BidDTO.fromEntity(bidEntity);
			return bid;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Bid entity '" + bidId + "'not found", e);
			return null;
		}
	}

	@Override
	public CommentDTO getComment(String commentId) {
		CommentDTO comment = new CommentDTO();
		comment.setIdFromString(commentId);
		
		try {
			Entity commentEntity = getDatastoreService().get(comment.getKey());
			comment = CommentDTO.fromEntity(commentEntity);
			return comment;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Comment entity '" + commentId + "'not found", e);
			return null;
		}
	}

	@Override
	public boolean userCanVoteForListing(String voterId, String listingId) {
		ListingDTO listing = new ListingDTO();
		listing.setIdFromString(listingId);
		try {
			listing = ListingDTO.fromEntity(getDatastoreService().get(listing.getKey()));
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Listing entity '" + listingId + "'not found", e);
			return false;
		}
		boolean notOwnerOfListing = !StringUtils.areStringsEqual(voterId, listing.getOwner());
		
		Query query = new VoteDTO().getQuery().setKeysOnly();
		query.addFilter(VoteDTO.VOTER, Query.FilterOperator.EQUAL, voterId);
		query.addFilter(VoteDTO.LISTING, Query.FilterOperator.EQUAL, listingId);
		PreparedQuery pq = getDatastoreService().prepare(query);
		boolean notVotedForListing = pq.countEntities(FetchOptions.Builder.withDefaults()) == 0;

		return notOwnerOfListing && notVotedForListing;
	}
	
	@Override
	public boolean userCanVoteForUser(String voterId, String userId) {
		if (StringUtils.areStringsEqual(voterId, userId)) {
			return false;
		}
		Query query = new VoteDTO().getQuery().setKeysOnly();
		query.addFilter(VoteDTO.VOTER, Query.FilterOperator.EQUAL, voterId);
		query.addFilter(VoteDTO.USER, Query.FilterOperator.EQUAL, userId);
		PreparedQuery pq = getDatastoreService().prepare(query);
		return pq.countEntities(FetchOptions.Builder.withDefaults()) == 0;
	}

	@Override
	public UserDTO activateUser(String userId) {
		UserDTO user = new UserDTO();
		user.setIdFromString(userId);
		Entity userEntity = null;
		try {
			userEntity = getDatastoreService().get(user.getKey());
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "User with id '" + userId + "' not found!");
			return null;
		}
		user = UserDTO.fromEntity(userEntity);
		user.setStatus(UserDTO.Status.ACTIVE);
		getDatastoreService().put(user.toEntity());
		
		return user;
	}

	@Override
	public UserDTO deactivateUser(String userId) {
		UserDTO user = new UserDTO();
		user.setIdFromString(userId);
		Entity userEntity = null;
		try {
			userEntity = getDatastoreService().get(user.getKey());
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "User with id '" + userId + "' not found!");
			return null;
		}
		user = UserDTO.fromEntity(userEntity);
		user.setStatus(UserDTO.Status.DEACTIVATED);
		getDatastoreService().put(user.toEntity());
		
		return user;
	}

	@Override
	public Boolean checkUserName(String userName) {
		Query query = new UserDTO().getQuery();
		query.addFilter(UserDTO.NICKNAME, FilterOperator.EQUAL, userName);
		PreparedQuery pq = getDatastoreService().prepare(query);
		return pq.countEntities(FetchOptions.Builder.withDefaults()) == 0;
	}

	@Override
	public CommentDTO deleteComment(String commentId) {
		CommentDTO comment = new CommentDTO();
		comment.setIdFromString(commentId);
		try {
			Entity commentEntity = getDatastoreService().get(comment.getKey());
			getDatastoreService().delete(comment.getKey());
			return CommentDTO.fromEntity(commentEntity);
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Comment with id '" + commentId + "' not found!");
			return null;
		}
	}

	@Override
	public CommentDTO createComment(CommentDTO comment) {
		comment.setCommentedOn(new Date());
		comment.createKey(comment.getListing() + comment.getUser() + comment.getCommentedOn().getTime());
		
		getDatastoreService().put(comment.toEntity());
		return comment;
	}

	@Override
	public CommentDTO updateComment(CommentDTO newComment) {
		try {
			Entity commentEntity = getDatastoreService().get(newComment.getKey());
			CommentDTO comment = CommentDTO.fromEntity(commentEntity);
			
			comment.setComment(newComment.getComment());
			
			getDatastoreService().put(comment.toEntity());
			return comment;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Comment with id '" + newComment.getKey() + "' not found!");
			return null;
		}
	}

	@Override
	public BidDTO deleteBid(String loggedInUser, String bidId) {
		try {
			BidDTO bid = new BidDTO();
			bid.setIdFromString(bidId);
			Entity bidEntity = getDatastoreService().get(bid.getKey());
			bid = BidDTO.fromEntity(bidEntity);
			
			if (!StringUtils.areStringsEqual(loggedInUser, bid.getUser())) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the bid " + bid);
				return null;
			}

			getDatastoreService().delete(bid.getKey());
			return BidDTO.fromEntity(bidEntity);
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
			return null;
		}
	}

	@Override
	public BidDTO createBid(String loggedInUser, BidDTO bid) {
		bid.setUser(loggedInUser);
		
		ListingDTO listing = new ListingDTO();
		listing.setIdFromString(bid.getListing());
		try {
			listing = ListingDTO.fromEntity(getDatastoreService().get(listing.getKey()));
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Bidding for non existing listing with id '" + bid.getListing() + "'!");
			return null;
		}
		Query query = new BidDTO().getQuery();
		query.addFilter(BidDTO.USER, Query.FilterOperator.EQUAL, loggedInUser);
		query.addFilter(BidDTO.LISTING, Query.FilterOperator.EQUAL, listing.getIdAsString());
		query.addFilter(BidDTO.STATUS, Query.FilterOperator.EQUAL, BidDTO.Status.ACTIVE.toString());
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		Iterator<Entity> bidIt = pq.asIterator(FetchOptions.Builder.withLimit(1));
		if (bidIt.hasNext()) {
			BidDTO updatedBid = BidDTO.fromEntity(bidIt.next());
			log.info("Bid already exists: " + updatedBid);
			updatedBid.setFundType(bid.getFundType());
			updatedBid.setPercentOfCompany(bid.getPercentOfCompany());
			updatedBid.setValue(bid.getValue());
			updatedBid.setValuation(bid.getValuation());
			log.info("Updating bid: " + updatedBid);
			getDatastoreService().put(updatedBid.toEntity());
			return updatedBid;
		} else {
			bid.setListingOwner(listing.getOwner());
			bid.setPlaced(new Date());
			bid.createKey(bid.getListing().substring(0, 3) + bid.getUser().substring(0, 3) + bid.getPlaced().getTime());
			log.info("Creating new bid: " + bid);
			getDatastoreService().put(bid.toEntity());
			return bid;
		}
	}

	@Override
	public BidDTO updateBid(String loggedInUser, BidDTO newBid) {
		try {
			Entity bidEntity = getDatastoreService().get(newBid.getKey());
			BidDTO bid = BidDTO.fromEntity(bidEntity);
			
			if (!StringUtils.areStringsEqual(loggedInUser, bid.getUser())) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the bid " + bid);
				return null;
			}
			
			bid.setFundType(newBid.getFundType());
			bid.setValue(newBid.getValue());
			bid.setValuation(newBid.getValuation());
			bid.setPercentOfCompany(newBid.getPercentOfCompany());
			log.info("Updating bid: " + newBid);
			getDatastoreService().put(bid.toEntity());
			return bid;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Bid with id '" + newBid.getKey() + "' not found!");
			return null;
		}
	}

	@Override
	public BidDTO activateBid(String loggedInUser, String bidId) {
		try {
			BidDTO bid = new BidDTO();
			bid.setIdFromString(bidId);
			Entity bidEntity = getDatastoreService().get(bid.getKey());
			bid = BidDTO.fromEntity(bidEntity);
			
			if (!StringUtils.areStringsEqual(loggedInUser, bid.getUser())) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the bid " + bid);
				return null;
			}
			
			bid.setStatus(BidDTO.Status.ACTIVE);
			log.info("Activating bid: " + bid);
			getDatastoreService().put(bid.toEntity());
			return bid;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
			return null;
		}
	}

	@Override
	public BidDTO withdrawBid(String loggedInUser, String bidId) {
		try {
			BidDTO bid = new BidDTO();
			bid.setIdFromString(bidId);
			Entity bidEntity = getDatastoreService().get(bid.getKey());
			bid = BidDTO.fromEntity(bidEntity);
			
			if (!StringUtils.areStringsEqual(loggedInUser, bid.getUser())) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the bid " + bid);
				return null;
			}
			
			bid.setStatus(BidDTO.Status.WITHDRAWN);
			log.info("Withdrawing bid: " + bid);
			getDatastoreService().put(bid.toEntity());
			return bid;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
			return null;
		}
	}

	@Override
	public BidDTO acceptBid(String loggedInUser, String bidId) {
		try {
			BidDTO bid = new BidDTO();
			bid.setIdFromString(bidId);
			Entity bidEntity = getDatastoreService().get(bid.getKey());
			bid = BidDTO.fromEntity(bidEntity);
			
			if (!BidDTO.Status.ACTIVE.equals(bid.getStatus())) {
				log.log(Level.WARNING, "Bid is not active. " + bid);
				return null;
			}
			ListingDTO listing = getListing(bid.getListing());
			if (!StringUtils.areStringsEqual(loggedInUser, listing.getOwner())) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the listing. " + listing + ", " + bid);
				return null;
			}
			if (!ListingDTO.State.ACTIVE.equals(listing.getState())) {
				log.log(Level.WARNING, "Listing '" + bid.getListing() + "' is not active. " + listing + ", " + bid);
				return null;
			}
			UserDTO bidder = getUser(bid.getUser());
			if (!UserDTO.Status.ACTIVE.equals(bidder.getStatus())) {
				log.log(Level.WARNING, "Bidder is not active. " + bidder + ", " + bid);
				return null;
			}
			
			bid.setStatus(BidDTO.Status.ACCEPTED);
			log.info("Accepting bid: " + bid);
			getDatastoreService().put(bid.toEntity());
			return bid;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
			return null;
		}
	}

	public PaidBidDTO markBidAsPaid(String loggedInUser, String bidId) {
		try {
			BidDTO bid = new BidDTO();
			bid.setIdFromString(bidId);
			Entity bidEntity = getDatastoreService().get(bid.getKey());
			bid = BidDTO.fromEntity(bidEntity);
			
			if (!BidDTO.Status.ACCEPTED.equals(bid.getStatus())) {
				log.log(Level.WARNING, "Bid is not accepted. " + bid);
				return null;
			}
			ListingDTO listing = getListing(bid.getListing());
			if (!StringUtils.areStringsEqual(loggedInUser, listing.getOwner())) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the listing. " + listing + ", " + bid);
				return null;
			}
			
			Query query = new PaidBidDTO().getQuery();
			query.addFilter(PaidBidDTO.BID, Query.FilterOperator.EQUAL, bid.getIdAsString());
			PreparedQuery pq = getDatastoreService().prepare(query);
			if (pq.countEntities(FetchOptions.Builder.withLimit(1)) > 0) {
				log.log(Level.WARNING, "Bid with id '" + bidId + "' is already marked as paid!");
				return null;
			}

			PaidBidDTO paidBid = PaidBidDTO.fromEntity(bidEntity);
			log.info("Marking bid as active: " + bid);
			getDatastoreService().put(paidBid.toEntity());
			return paidBid;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
			return null;
		}
	}
	@Override
	public List<BidDTO> getBidsByDate(ListPropertiesVO bidsProperties) {
		List<BidDTO> bids = new ArrayList<BidDTO>();
		
		Query query = new BidDTO().getQuery();
		query.addFilter(BidDTO.STATUS, Query.FilterOperator.EQUAL, BidDTO.Status.ACTIVE.toString());
		query.addSort(BidDTO.PLACED, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity bid : pq.asIterable(FetchOptions.Builder.withLimit(100))) {
			bids.add(BidDTO.fromEntity(bid));
		}
		return bids;
	}

	@Override
	public SystemPropertyDTO getSystemProperty(String name) {
		Query query = new SystemPropertyDTO().getQuery();
		query.addFilter(SystemPropertyDTO.NAME, Query.FilterOperator.EQUAL, name);
		PreparedQuery pq = getDatastoreService().prepare(query);
		SystemPropertyDTO systemProp = SystemPropertyDTO.fromEntity(
				pq.asIterator(FetchOptions.Builder.withLimit(1)).next());
		return systemProp;
	}

	@Override
	public SystemPropertyDTO setSystemProperty(SystemPropertyDTO property) {
		property.setCreated(new Date());
		property.setIdFromString(property.getName());
		
		getDatastoreService().put(property.toEntity());
		return property;
	}

	@Override
	public List<SystemPropertyDTO> getSystemProperties() {
		List<SystemPropertyDTO> props = new ArrayList<SystemPropertyDTO>();
		Query query = new SystemPropertyDTO().getQuery();
		query.addSort(SystemPropertyDTO.NAME, Query.SortDirection.ASCENDING);
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity systemPropEntity : pq.asIterable(FetchOptions.Builder.withLimit(1000))) {
			SystemPropertyDTO systemProp = SystemPropertyDTO.fromEntity(systemPropEntity);
			props.add(systemProp);
		}
		return props;
	}

	@Override
	public ListingDocumentDTO createListingDocument(ListingDocumentDTO docDTO) {
		docDTO.setCreated(new Date());
		docDTO.createKey("Doc" + docDTO.hashCode());
		
		getDatastoreService().put(docDTO.toEntity());
		return docDTO;
	}

	@Override
	public ListingDocumentDTO getListingDocument(String docId) {
		ListingDocumentDTO docDTO = new ListingDocumentDTO();
		docDTO.setIdFromString(docId);
		
		try {
			docDTO = ListingDocumentDTO.fromEntity(getDatastoreService().get(docDTO.getKey()));
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Listing Document with id '" + docId + "' not found!");
		}
		return docDTO;
	}

	@Override
	public List<ListingDocumentDTO> getAllListingDocuments() {
		List<ListingDocumentDTO> docs = new ArrayList<ListingDocumentDTO>();
		Query query = new ListingDocumentDTO().getQuery();
		query.addSort(ListingDocumentDTO.CREATED, Query.SortDirection.DESCENDING);
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity docEntity : pq.asIterable(FetchOptions.Builder.withLimit(1000))) {
			ListingDocumentDTO systemProp = ListingDocumentDTO.fromEntity(docEntity);
			docs.add(systemProp);
		}
		return docs;
	}

	@Override
	public ListingDocumentDTO deleteDocument(String docId) {
		ListingDocumentDTO comment = new ListingDocumentDTO();
		comment.setIdFromString(docId);
		try {
			Entity commentEntity = getDatastoreService().get(comment.getKey());
			getDatastoreService().delete(comment.getKey());
			return ListingDocumentDTO.fromEntity(commentEntity);
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Listing Document with id '" + docId + "' not found!");
			return null;
		}
	}

}
