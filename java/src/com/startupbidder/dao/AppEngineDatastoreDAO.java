package com.startupbidder.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.datanucleus.util.StringUtils;

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
import com.startupbidder.dto.ListingMedianValuationDTO;
import com.startupbidder.dto.ListingStatisticsDTO;
import com.startupbidder.dto.RankDTO;
import com.startupbidder.dto.SystemPropertyDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.UserStatisticsDTO;
import com.startupbidder.dto.VoteDTO;
import com.startupbidder.vo.ListPropertiesVO;

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
	
	public String createMockDatastore() {
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

		outputBuffer.append("<p>Median valuation:</p>");
		keys = new ArrayList<Key>();
		query = new ListingMedianValuationDTO().getQuery();
		pq = getDatastoreService().prepare(query);
		for (Entity entity : pq.asIterable()) {
			keys.add(entity.getKey());
			outputBuffer.append(ListingMedianValuationDTO.fromEntity(entity).toString()).append("<br/>");
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
		for (UserDTO user : ((MockDatastoreDAO)MockDatastoreDAO.getInstance()).userCache.values()) {
			getDatastoreService().put(user.toEntity());
		}
		for (SystemPropertyDTO sp : ((MockDatastoreDAO)MockDatastoreDAO.getInstance()).propCache.values()) {
			getDatastoreService().put(sp.toEntity());
		}
		for (ListingDocumentDTO ld : ((MockDatastoreDAO)MockDatastoreDAO.getInstance()).docCache.values()) {
			getDatastoreService().put(ld.toEntity());
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
		Entity userEntity = pq.asSingleEntity();
		if (userEntity != null) {
			user = UserDTO.fromEntity(userEntity);
			return user;
		} else {
			return null;
		}
	}

	@Override
	public UserDTO createUser(String userId, String email, String nickname) {
		UserDTO user = new UserDTO();
		user.createKey(userId);
		user.setOpenId(userId);
		user.setNickname(nickname != null ? nickname : "" + userId);
		//user.setFirstName("");
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
		UserStatisticsDTO userStats = new UserStatisticsDTO();
		userStats.createKey(userId);
		
		Query query = new BidDTO().getQuery().setKeysOnly();
		query.addFilter(BidDTO.USER, Query.FilterOperator.EQUAL, userId);
		PreparedQuery pq = getDatastoreService().prepare(query);
		userStats.setNumberOfBids(pq.countEntities(FetchOptions.Builder.withDefaults()));
		
		query = new CommentDTO().getQuery().setKeysOnly();
		query.addFilter(CommentDTO.USER, Query.FilterOperator.EQUAL, userId);
		pq = getDatastoreService().prepare(query);
		userStats.setNumberOfComments(pq.countEntities(FetchOptions.Builder.withDefaults()));
		
		query = new ListingDTO().getQuery().setKeysOnly();
		query.addFilter(ListingDTO.OWNER, Query.FilterOperator.EQUAL, userId);
		pq = getDatastoreService().prepare(query);
		userStats.setNumberOfListings(pq.countEntities(FetchOptions.Builder.withDefaults()));
		
		query = new VoteDTO().getQuery().setKeysOnly();
		query.addFilter(VoteDTO.USER, Query.FilterOperator.EQUAL, userId);
		pq = getDatastoreService().prepare(query);
		userStats.setNumberOfVotes(pq.countEntities(FetchOptions.Builder.withDefaults()));

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VoteDTO> getUserVotes(String userId) {
		// TODO Auto-generated method stub
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
			log.log(Level.WARNING, "Listing entity '" + newListing.getKey() + "'not found", e);
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
	public List<ListingDTO> getUserListings(String userId, ListPropertiesVO listingProperties) {
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
	public List<ListingDTO> getTopListings(ListPropertiesVO listingProperties) {
		// TODO Auto-generated method stub
		return new ArrayList<ListingDTO>();
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
		// TODO Auto-generated method stub
		return new ArrayList<ListingDTO>();
	}

	@Override
	public List<ListingDTO> getMostDiscussedListings(ListPropertiesVO listingProperties) {
		// TODO Auto-generated method stub
		return new ArrayList<ListingDTO>();
	}

	@Override
	public List<ListingDTO> getMostPopularListings(ListPropertiesVO listingProperties) {
		// TODO Auto-generated method stub
		return new ArrayList<ListingDTO>();
	}

	@Override
	public List<ListingDTO> getLatestListings(ListPropertiesVO listingProperties) {
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
				vote.setListing(listingId);
				vote.setUser(null);
				vote.setVoter(voterId);
				vote.setValue(1);
				vote.setCommentedOn(new Date());
				vote.createKey(listingId + voterId + vote.getCommentedOn().getTime());
				
				getDatastoreService().put(vote.toEntity());
				
				return listing;
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
				vote.setUser(voterId);
				vote.setListing(null);
				vote.setVoter(voterId);
				vote.setValue(1);
				vote.setCommentedOn(new Date());
				vote.createKey(userId + voterId + vote.getCommentedOn().getTime());
				
				getDatastoreService().put(vote.toEntity());
				
				return user;
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
		query.addFilter(BidDTO.STATUS, Query.FilterOperator.EQUAL, BidDTO.Status.ACTIVE.toString());
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
		query.addFilter(BidDTO.STATUS, Query.FilterOperator.EQUAL, BidDTO.Status.ACTIVE.toString());
		query.addSort(BidDTO.PLACED, Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity bid : pq.asIterable()) {
			bids.add(BidDTO.fromEntity(bid));
		}
		return bids;
	}

	@Override
	public int getNumberOfVotesForListing(String listingId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfVotesForUser(String userId) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getActivity(String listingId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BidDTO getBid(String bidId) {
		BidDTO bid = new BidDTO();
		bid.setIdFromString(bidId);
		
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
	public boolean userCanVoteForListing(String userId, String listingId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean userCanVoteForUser(String voterId, String userId) {
		// TODO Auto-generated method stub
		return false;
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
		return pq.asSingleEntity() == null;
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
	public BidDTO deleteBid(String bidId) {
		BidDTO bid = new BidDTO();
		bid.setIdFromString(bidId);
		try {
			Entity bidEntity = getDatastoreService().get(bid.getKey());
			getDatastoreService().delete(bid.getKey());
			return BidDTO.fromEntity(bidEntity);
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
			return null;
		}
	}

	@Override
	public BidDTO createBid(BidDTO bid) {
		bid.setPlaced(new Date());
		bid.createKey(bid.getListing() + bid.getUser() + bid.getPlaced().getTime());
		
		getDatastoreService().put(bid.toEntity());
		return bid;
	}

	@Override
	public BidDTO updateBid(BidDTO newBid) {
		try {
			Entity bidEntity = getDatastoreService().get(newBid.getKey());
			BidDTO bid = BidDTO.fromEntity(bidEntity);
			
			bid.setFundType(newBid.getFundType());
			bid.setValue(newBid.getValue());
			bid.setValuation(newBid.getValuation());

			getDatastoreService().put(bid.toEntity());
			return bid;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Bid with id '" + newBid.getKey() + "' not found!");
			return null;
		}
	}

	@Override
	public BidDTO activateBid(String bidId) {
		try {
			BidDTO bid = new BidDTO();
			bid.setIdFromString(bidId);
			Entity bidEntity = getDatastoreService().get(bid.getKey());
			bid = BidDTO.fromEntity(bidEntity);
			
			bid.setStatus(BidDTO.Status.ACTIVE);

			getDatastoreService().put(bid.toEntity());
			return bid;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
			return null;
		}
	}

	@Override
	public BidDTO withdrawBid(String bidId) {
		try {
			BidDTO bid = new BidDTO();
			bid.setIdFromString(bidId);
			Entity bidEntity = getDatastoreService().get(bid.getKey());
			bid = BidDTO.fromEntity(bidEntity);
			
			bid.setStatus(BidDTO.Status.WITHDRAWN);

			getDatastoreService().put(bid.toEntity());
			return bid;
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
			return null;
		}
	}

	@Override
	public List<BidDTO> getBidsByDate(ListPropertiesVO bidsProperties) {
		// TODO Auto-generated method stub
		return new ArrayList<BidDTO>();
	}

	@Override
	public SystemPropertyDTO getSystemProperty(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SystemPropertyDTO setSystemProperty(SystemPropertyDTO property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SystemPropertyDTO> getSystemProperties() {
		List<SystemPropertyDTO> props = new ArrayList<SystemPropertyDTO>();
		return props;
	}

	@Override
	public ListingDocumentDTO createListingDocument(ListingDocumentDTO docDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListingDocumentDTO getListingDocument(String docId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ListingDocumentDTO> getAllListingDocuments() {
		// TODO Auto-generated method stub
		return new ArrayList<ListingDocumentDTO>();
	}

	@Override
	public ListingDocumentDTO deleteDocument(String docId) {
		// TODO Auto-generated method stub
		return null;
	}

}
