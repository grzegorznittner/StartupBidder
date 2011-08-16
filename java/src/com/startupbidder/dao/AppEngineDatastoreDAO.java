package com.startupbidder.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.ListingDocumentDTO;
import com.startupbidder.dto.SystemPropertyDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.UserStatistics;
import com.startupbidder.dto.VoteDTO;
import com.startupbidder.dto.BidDTO.Status;
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
		query.addFilter("openId", FilterOperator.EQUAL, openId);
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
	public UserStatistics getUserStatistics(String userId) {
		UserStatistics stat = new UserStatistics();
		return stat;
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
		query.addSort("nickname", Query.SortDirection.ASCENDING);
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity user : pq.asIterable()) {
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
		query.addFilter("owner", Query.FilterOperator.EQUAL, userId);
		query.addFilter("state", Query.FilterOperator.EQUAL, ListingDTO.State.ACTIVE.toString());
		query.addSort("listedOn", Query.SortDirection.DESCENDING);
		
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
		query.addFilter("state", Query.FilterOperator.EQUAL, ListingDTO.State.ACTIVE.toString());
		query.addSort("listedOn", Query.SortDirection.DESCENDING);
		
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
		query.addFilter("state", Query.FilterOperator.EQUAL, ListingDTO.State.ACTIVE.toString());
		query.addSort("listedOn", Query.SortDirection.DESCENDING);
		
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
		query.addFilter("state", Query.FilterOperator.EQUAL, ListingDTO.State.ACTIVE.toString());
		query.addSort("closingOn", Query.SortDirection.DESCENDING);
		
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
	public ListingDTO valueUpListing(String listingId, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListingDTO valueDownListing(String listingId, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CommentDTO> getCommentsForListing(String listingId) {
		List<CommentDTO> comments = new ArrayList<CommentDTO>();
		
		Query query = new CommentDTO().getQuery();
		query.addFilter("listing", Query.FilterOperator.EQUAL, listingId);
		query.addSort("commentedOn", Query.SortDirection.DESCENDING);
		
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
		query.addFilter("user", Query.FilterOperator.EQUAL, userId);
		query.addSort("commentedOn", Query.SortDirection.DESCENDING);
		
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
		query.addFilter("listing", Query.FilterOperator.EQUAL, listingId);
		query.addFilter("status", Query.FilterOperator.EQUAL, BidDTO.Status.ACTIVE.toString());
		query.addSort("commentedOn", Query.SortDirection.DESCENDING);
		
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
		query.addFilter("user", Query.FilterOperator.EQUAL, userId);
		query.addFilter("status", Query.FilterOperator.EQUAL, BidDTO.Status.ACTIVE.toString());
		query.addSort("commentedOn", Query.SortDirection.DESCENDING);
		
		PreparedQuery pq = getDatastoreService().prepare(query);
		for (Entity bid : pq.asIterable()) {
			bids.add(BidDTO.fromEntity(bid));
		}
		return bids;
	}

	@Override
	public int getNumberOfVotes(String listingId) {
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
		query.addFilter("nickname", FilterOperator.EQUAL, userName);
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
