package com.startupbidder.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.datanucleus.util.StringUtils;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.ListingDocumentDTO;
import com.startupbidder.dto.ListingStatisticsDTO;
import com.startupbidder.dto.PaidBidDTO;
import com.startupbidder.dto.SystemPropertyDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.UserStatisticsDTO;
import com.startupbidder.dto.VoteDTO;
import com.startupbidder.vo.ListPropertiesVO;

/**
 * Data access object which handles all interaction with underlying persistence layer.
 * This implementation uses internal hash maps for storing data.
 *  
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class MockDatastoreDAO implements DatastoreDAO {
	private static final Logger log = Logger.getLogger(MockDatastoreDAO.class.getName());
	
	static MockDatastoreDAO instance;
	
	public static DatastoreDAO getInstance() {
		if (instance == null) {
			instance = new MockDatastoreDAO();
		}
		return instance;
	}
	
	public Map<String, ListingDTO> lCache = new HashMap<String, ListingDTO>();
	public Map<String, VoteDTO> voteCache = new HashMap<String, VoteDTO>();
	public Map<String, CommentDTO> commentCache = new HashMap<String, CommentDTO>();
	public Map<String, BidDTO> bidCache = new HashMap<String, BidDTO>();
	public Map<String, PaidBidDTO> paidBidCache = new HashMap<String, PaidBidDTO>();
	public Map<String, UserDTO> userCache = new HashMap<String, UserDTO>();
	public Map<String, SystemPropertyDTO> propCache = new HashMap<String, SystemPropertyDTO>();
	public Map<String, ListingDocumentDTO> docCache = new HashMap<String, ListingDocumentDTO>();

	public MockDatastoreDAO() {
		MockDataBuilder mocks = new MockDataBuilder();
		userCache = mocks.createMockUsers();
		lCache = mocks.createMockListings(userCache);
		commentCache = mocks.generateComments(userCache.values(), lCache.values());
		bidCache = mocks.generateBids(userCache.values(), lCache.values());
		voteCache = mocks.createMockVotes(userCache.values(), lCache.values());
	}
	
	public String clearDatastore() {
		return "Operation not supported for MockDatastore";
	}
	
	public String printDatastoreContents() {
		return "Operation not supported for MockDatastore";
	}
	
	public String createMockDatastore() {
		return "Operation not supported for MockDatastore";
	}

	public UserDTO getUser(String key) {
		UserDTO user = (UserDTO)userCache.get(key);		
		return user;
	}
	
	public UserDTO getUserByOpenId(String openId) {
		for(UserDTO user : userCache.values()) {
			if (StringUtils.areStringsEqual(user.getOpenId(), openId)) {
				return user;
			}
		}
		return null;
	}
	
	public UserDTO createUser(String userId, String email, String nickname) {
		UserDTO user = new UserDTO();
		if (email != null && (email.contains("grzegorz.nittner") || email.contains("johnarleyburns"))) {
			user = getTopInvestor();
		} else {
			user.createKey(userId);
		}
		
		user.setOpenId(userId);
		user.setNickname(nickname != null ? nickname : "The " + userId);
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
		
		userCache.put(user.getIdAsString(), user);
		return user;
	}
	
	public UserStatisticsDTO updateUserStatistics(String userId) {
		UserStatisticsDTO stat = new UserStatisticsDTO();
		
		int numberOfListings = 0;
		for (ListingDTO bp : lCache.values()) {
			if (StringUtils.areStringsEqual(userId, bp.getOwner())) {
				numberOfListings++;
			}
		}
		stat.setNumberOfListings(numberOfListings);
		
		int numberOfBids = 0;
		for (BidDTO bid : bidCache.values()) {
			if (StringUtils.areStringsEqual(userId, bid.getUser())) {
				numberOfBids++;
			}
		}
		stat.setNumberOfBids(numberOfBids);
		
		int numberOfComments = 0;
		for (CommentDTO comment : commentCache.values()) {
			if (StringUtils.areStringsEqual(userId, comment.getUser())) {
				numberOfComments++;
			}
		}
		stat.setNumberOfComments(numberOfComments);
		
		return stat;
	}
	
	public ListingStatisticsDTO updateListingStatistics(String listingId) {
		ListingStatisticsDTO listingStats = new ListingStatisticsDTO();
		
		// set number of comments and number of votes
		listingStats.setNumberOfComments(getActivity(listingId));
		listingStats.setNumberOfVotes(getNumberOfVotesForListing(listingId));

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
		log.log(Level.INFO, "Values for '" + listingId + "': " + values + ", median: " + median);
		listingStats.setValuation(median);
		listingStats.setNumberOfBids(bids.size());
		
		return listingStats;
	}
	
	public ListingStatisticsDTO getListingStatistics(String listingId) {
		return getListingStatistics(listingId);
	}
	
	@Override
	public UserStatisticsDTO getUserStatistics(String userId) {
		return updateUserStatistics(userId);
	}

	public UserDTO updateUser(UserDTO newUser) {
		if (!userCache.containsKey(newUser.getIdAsString())) {
			log.log(Level.WARNING, "User '" + newUser.getIdAsString() + "' doesn't exist in the repository");
			return null;
		}
		UserDTO user = userCache.get(newUser.getIdAsString());
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
	}
	
	public List<UserDTO> getAllUsers() {
		return new ArrayList<UserDTO>(userCache.values());
	}
	
	public Boolean checkUserName(String userName) {
		for (UserDTO user : userCache.values()) {
			if (StringUtils.areStringsEqual(userName, user.getNickname())) {
				return false;
			}
		}
		return true;
	}
	
	public UserDTO getTopInvestor() {
		// computes number of bids posted by users <user_id, number of bids>
		Map<String, Integer> userBids = new HashMap<String, Integer>();
		for(BidDTO bid : bidCache.values()) {
			if(userBids.containsKey(bid.getUser())) {
				userBids.put(bid.getUser(), userBids.get(bid.getUser()) + 1);
			} else {
				userBids.put(bid.getUser(), 1);
			}
		}
		// sort rating cache
		List<Map.Entry<String, Integer>> bidsPerUser = new ArrayList<Map.Entry<String, Integer>>(userBids.entrySet());
		Collections.sort(bidsPerUser, new Comparator<Map.Entry<String, Integer>> () {
			public int compare(Map.Entry<String, Integer> left, Map.Entry<String, Integer> right) {
				if (left.getValue() == right.getValue()) {
					return 0;
				} else if (left.getValue() > right.getValue()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		return userCache.get(bidsPerUser.get(0).getKey());
	}
	
	public boolean userCanVoteForListing(String voterId, String listingId) {
		ListingDTO listing = getListing(listingId);
		if (listing != null && StringUtils.areStringsEqual(listing.getOwner(), voterId)) {
			// voter cannot vote for his own listings
			return false;
		}
		log.log(Level.INFO, "Check votes for user '" + voterId + "' and listing '" + listingId + "'");
		for (VoteDTO vote : voteCache.values()) {
			log.log(Level.INFO, "  --> " + vote.toString());
			if (StringUtils.areStringsEqual(vote.getListing(), listingId)) {
				if (StringUtils.areStringsEqual(vote.getVoter(), voterId)) {
					// voter has already voted for that listing
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean userCanVoteForUser(String voterId, String userId) {
		UserDTO user = getUser(userId);
		if (user != null && StringUtils.areStringsEqual(user.getIdAsString(), voterId)) {
			// user cannot vote for his himself
			return false;
		}
		log.log(Level.INFO, "Check votes for voter '" + voterId + "' and user '" + userId + "'");
		for (VoteDTO vote : voteCache.values()) {
			log.log(Level.INFO, "  --> " + vote.toString());
			if (StringUtils.areStringsEqual(vote.getUser(), userId)) {
				if (StringUtils.areStringsEqual(vote.getVoter(), voterId)) {
					// voter has already voted for that user
					return false;
				}
			}
		}
		return true;
	}
	
	public UserDTO activateUser(String userId) {
		UserDTO user = userCache.get(userId);
		if (user == null) {
			log.log(Level.WARNING, "User with id '" + userId + "' not found!");
			return null;
		}
		user.setStatus(UserDTO.Status.ACTIVE);
		return user;
	}

	public UserDTO deactivateUser(String userId) {
		UserDTO user = userCache.get(userId);
		if (user == null) {
			log.log(Level.WARNING, "User with id '" + userId + "' not found!");
			return null;
		}
		user.setStatus(UserDTO.Status.DEACTIVATED);
		return user;
	}
	
	public List<VoteDTO> getUserVotes(String userId) {
		List<VoteDTO> votes = new ArrayList<VoteDTO>();
		for (VoteDTO vote : voteCache.values()) {
			if (StringUtils.areStringsEqual(vote.getVoter(), userId)) {
				votes.add(vote);
			}
		}
		
		return votes;
	}
	
	public ListingDTO getListing(String listingId) {
		if (!lCache.containsKey(listingId)) {
			log.log(Level.WARNING, "Listing '" + listingId + "' not found");
		}
		return lCache.get(listingId);
	}
	
	public ListingDTO createListing(ListingDTO listing) {
		listing.createKey(listing.getName() + listing.getOwner());
		listing.setState(ListingDTO.State.CREATED);
		listing.setListedOn(new Date());
		
		lCache.put(listing.getIdAsString(), listing);
		
		return listing;
	}
	
	public ListingDTO updateListing(ListingDTO newListing) {
		if (!lCache.containsKey(newListing.getIdAsString())) {
			log.log(Level.WARNING, "Listning '' doesn't exist in the repository");
			return null;
		}
		ListingDTO listing = lCache.get(newListing.getIdAsString());
		listing.setName(newListing.getName());
		listing.setSuggestedAmount(newListing.getSuggestedAmount());
		listing.setSuggestedPercentage(newListing.getSuggestedPercentage());
		listing.setSuggestedValuation(newListing.getSuggestedValuation());
		listing.setBusinessPlanId(newListing.getBusinessPlanId());
		listing.setPresentationId(newListing.getPresentationId());
		listing.setFinancialsId(newListing.getFinancialsId());
		listing.setSummary(newListing.getSummary());
		return listing;
	}

	public ListingDTO activateListing(String listingId) {
		ListingDTO listing = getListing(listingId);
		if (listing != null) {
			listing.setState(ListingDTO.State.ACTIVE);
		}
		return listing;
	}

	public ListingDTO withdrawListing(String listingId) {
		ListingDTO listing = getListing(listingId);
		if (listing != null) {
			listing.setState(ListingDTO.State.WITHDRAWN);
		}
		return listing;
	}

	
	public List<ListingDTO> getTopListings(ListPropertiesVO listingProperties) {
		List<ListingDTO> list = new ArrayList<ListingDTO>();
		
		// calculates vote number per listing
		Map<String, Integer> accVotes = new HashMap<String, Integer>();
		for(VoteDTO vote : voteCache.values()) {
			if (vote.getListing() != null) {
				if(accVotes.containsKey(vote.getListing())) {
					accVotes.put(vote.getListing(), accVotes.get(vote.getListing()) + 1);
				} else {
					accVotes.put(vote.getListing(), 1);
				}
			}
		}
		// sort rating cache
		List<Map.Entry<String, Integer>> rating = new ArrayList<Map.Entry<String, Integer>>(accVotes.entrySet());
		Collections.sort(rating, new Comparator<Map.Entry<String, Integer>> () {
			public int compare(Map.Entry<String, Integer> left, Map.Entry<String, Integer> right) {
				if (left.getValue() == right.getValue()) {
					return 0;
				} else if (left.getValue() > right.getValue()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		
		int maxItems = listingProperties.getMaxResults();
		for (Map.Entry<String, Integer> bpEntry : rating) {
			log.log(Level.INFO, "BP " + bpEntry.getKey() + " - rating " + bpEntry.getValue());
			if (maxItems-- > 0) {
				list.add(lCache.get(bpEntry.getKey()));
			}
		}
		
		listingProperties.setNumberOfResults(list.size());
		listingProperties.setTotalResults(lCache.size());
		return list;
	}

	public List<ListingDTO> getMostDiscussedListings(ListPropertiesVO listingProperties) {
		List<ListingDTO> list = new ArrayList<ListingDTO>();
		
		// create activity list
		Map<String, Integer> sortedBP = new HashMap<String, Integer>();
		for (CommentDTO comment : commentCache.values()) {
			if (!sortedBP.containsKey(comment.getListing())) {
				sortedBP.put(comment.getListing(), 1);
			} else {
				sortedBP.put(comment.getListing(), sortedBP.get(comment.getListing()) + 1);
			}
		}
		
		// sort activity list
		List<Map.Entry<String, Integer>> active = new ArrayList<Map.Entry<String, Integer>>(sortedBP.entrySet());
		Collections.sort(active, new Comparator<Map.Entry<String, Integer>> () {
			public int compare(Map.Entry<String, Integer> left, Map.Entry<String, Integer> right) {
				if (left.getValue() == right.getValue()) {
					return 0;
				} else if (left.getValue() > right.getValue()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		int maxItems = listingProperties.getMaxResults();
		for (Map.Entry<String, Integer> bpEntry : active) {
			log.log(Level.INFO, "BP " + bpEntry.getKey() + " - activity " + bpEntry.getValue());
			if (maxItems-- > 0) {
				list.add(lCache.get(bpEntry.getKey()));
			}
		}
		
		listingProperties.setNumberOfResults(list.size());
		listingProperties.setTotalResults(lCache.size());
		return list;
	}
	
	public List<ListingDTO> getMostPopularListings(ListPropertiesVO listingProperties) {
		List<ListingDTO> list = new ArrayList<ListingDTO>();
		
		// create activity list
		Map<String, Integer> listingIds = new HashMap<String, Integer>();
		for (VoteDTO vote : voteCache.values()) {
			if (vote.getListing() != null) {
				if (!listingIds.containsKey(vote.getListing())) {
					listingIds.put(vote.getListing(), 1);
				} else {
					listingIds.put(vote.getListing(), listingIds.get(vote.getListing()) + 1);
				}
			}
		}
		
		// sort activity list
		List<Map.Entry<String, Integer>> active = new ArrayList<Map.Entry<String, Integer>>(listingIds.entrySet());
		Collections.sort(active, new Comparator<Map.Entry<String, Integer>> () {
			public int compare(Map.Entry<String, Integer> left, Map.Entry<String, Integer> right) {
				if (left.getValue() == right.getValue()) {
					return 0;
				} else if (left.getValue() > right.getValue()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		int maxItems = listingProperties.getMaxResults();
		for (Map.Entry<String, Integer> bpEntry : active) {
			log.log(Level.INFO, "BP " + bpEntry.getKey() + " - votes " + bpEntry.getValue());
			if (maxItems-- > 0) {
				list.add(lCache.get(bpEntry.getKey()));
			}
		}
		
		listingProperties.setNumberOfResults(list.size());
		listingProperties.setTotalResults(lCache.size());
		return list;
	}
	
	public List<ListingDTO> getUserActiveListings(String userId, ListPropertiesVO listingProperties) {
		log.log(Level.INFO, "Active listings created by " + userId);
		List<ListingDTO> list = new ArrayList<ListingDTO>();
		for (ListingDTO bp : lCache.values()) {
			if (StringUtils.areStringsEqual(bp.getOwner(), userId)
					&& ListingDTO.State.ACTIVE == bp.getState()) {
				log.log(Level.INFO, "BP " + bp.getKey());
				list.add(bp);
			}
		}
		int maxItems = listingProperties.getMaxResults();
		listingProperties.setTotalResults(list.size());
		
		list.subList(0, (list.size() < maxItems ? list.size() : maxItems));
		listingProperties.setNumberOfResults(list.size());
		return list;
	}
	
	public List<ListingDTO> getUserListings(String userId, ListPropertiesVO listingProperties) {
		log.log(Level.INFO, "All listings created by " + userId);
		List<ListingDTO> list = new ArrayList<ListingDTO>();
		for (ListingDTO bp : lCache.values()) {
			if (StringUtils.areStringsEqual(bp.getOwner(), userId)) {
				log.log(Level.INFO, "BP " + bp.getKey());
				list.add(bp);
			}
		}
		int maxItems = listingProperties.getMaxResults();
		listingProperties.setTotalResults(list.size());
		
		list.subList(0, (list.size() < maxItems ? list.size() : maxItems));
		listingProperties.setNumberOfResults(list.size());
		return list;
	}
	
	/**
	 * Do not use it!!!
	 * It calculates most valued listing based on max bids, not median
	 */
	public List<ListingDTO> getMostValuedListings(ListPropertiesVO listingProperties) {
		List<BidDTO> bids = new ArrayList<BidDTO>(bidCache.values());
		// sort activity list
		Collections.sort(bids, new Comparator<BidDTO> () {
			public int compare(BidDTO left, BidDTO right) {
				if (left.getValuation() == right.getValuation()) {
					return 0;
				} else if (left.getValuation() > right.getValuation()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		List<String> listingIds = new ArrayList<String>();
		for (BidDTO bid : bids) {
			if (!listingIds.contains(bid.getListing())) {
				listingIds.add(bid.getListing());
			}
			if (listingIds.size() > listingProperties.getMaxResults()) {
				break;
			}
		}
		
		List<ListingDTO> list = new ArrayList<ListingDTO>();
		for (String listingId : listingIds) {
			list.add(lCache.get(listingId));
		}

		listingProperties.setTotalResults(lCache.size());
		listingProperties.setNumberOfResults(list.size());
		return list;
	}
	
	public List<ListingDTO> getActiveListings(ListPropertiesVO listingProperties) {
		List<ListingDTO> list = new ArrayList<ListingDTO>();
		
		// create activity list
		Map<String, Integer> listingIds = new HashMap<String, Integer>();
		for (BidDTO bid : bidCache.values()) {
			if (!listingIds.containsKey(bid.getListing())) {
				listingIds.put(bid.getListing(), 1);
			} else {
				listingIds.put(bid.getListing(), listingIds.get(bid.getListing()) + 1);
			}
		}
		
		// sort activity list
		List<Map.Entry<String, Integer>> active = new ArrayList<Map.Entry<String, Integer>>(listingIds.entrySet());
		Collections.sort(active, new Comparator<Map.Entry<String, Integer>> () {
			public int compare(Map.Entry<String, Integer> left, Map.Entry<String, Integer> right) {
				if (left.getValue() == right.getValue()) {
					return 0;
				} else if (left.getValue() > right.getValue()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		int maxItems = listingProperties.getMaxResults();
		for (Map.Entry<String, Integer> bpEntry : active) {
			log.log(Level.INFO, "BP " + bpEntry.getKey() + " - bids " + bpEntry.getValue());
			if (maxItems-- > 0) {
				list.add(lCache.get(bpEntry.getKey()));
			}
		}
		
		listingProperties.setNumberOfResults(list.size());
		listingProperties.setTotalResults(lCache.size());
		return list;
	}
	
	public List<ListingDTO> getLatestListings(ListPropertiesVO listingProperties) {
		// sort by listed on date
		List<ListingDTO> listings = new ArrayList<ListingDTO>(lCache.values());
		Collections.sort(listings, new Comparator<ListingDTO> () {
			public int compare(ListingDTO left, ListingDTO right) {
				if (left.getListedOn().getTime() == right.getListedOn().getTime()) {
					return 0;
				} else if (left.getListedOn().getTime() > right.getListedOn().getTime()) {
					return -1;
				} else {
					return 1;
				}
			}
		});

		listings = listings.subList(0, listingProperties.getMaxResults() > listings.size() ? listings.size() : listingProperties.getMaxResults());
		listingProperties.setNumberOfResults(listings.size());
		listingProperties.setTotalResults(lCache.size());
		return listings;
	}

	public List<ListingDTO> getClosingListings(ListPropertiesVO listingProperties) {
		// sort by listed on date
		List<ListingDTO> listings = new ArrayList<ListingDTO>();
		for (ListingDTO listing : lCache.values()) {
			if (listing.getState().equals(ListingDTO.State.ACTIVE)) {
				listings.add(listing);
			}
		}
		Collections.sort(listings, new Comparator<ListingDTO> () {
			public int compare(ListingDTO left, ListingDTO right) {
				if (left.getClosingOn().getTime() == right.getClosingOn().getTime()) {
					return 0;
				} else if (left.getClosingOn().getTime() < right.getClosingOn().getTime()) {
					return -1;
				} else {
					return 1;
				}
			}
		});

		listings = listings.subList(0, listingProperties.getMaxResults() > listings.size() ? listings.size() : listingProperties.getMaxResults());
		listingProperties.setNumberOfResults(listings.size());
		listingProperties.setTotalResults(lCache.size());
		return listings;
	}


	public ListingDTO valueUpListing(String listingId, String voterId) {
		ListingDTO listing = getListing(listingId);
		if (StringUtils.areStringsEqual(listing.getOwner(), voterId)) {
			// user cannot vote for his own listings
			return null;
		}
		int numberOfVotes = 0;
		boolean alreadyVoted = false;
		for (VoteDTO vote : voteCache.values()) {
			if (StringUtils.areStringsEqual(vote.getListing(), listingId)) {
				numberOfVotes++;
				if (StringUtils.areStringsEqual(vote.getVoter(), voterId)) {
					// user has already voted for that listing
					alreadyVoted = true;
				}
			}
		}
		if (!alreadyVoted) {
			VoteDTO vote = new VoteDTO();
			vote.setListing(listingId);
			vote.setVoter(voterId);
			vote.setUser(null);
			vote.setValue(1);
			vote.createKey(String.valueOf(vote.hashCode()));
			numberOfVotes++;
			voteCache.put(vote.getIdAsString(), vote);
		}
		return listing;
	}
	
	@Override
	public UserDTO valueUpUser(String userId, String voterId) {
		UserDTO listing = getUser(userId);
		if (StringUtils.areStringsEqual(listing.getIdAsString(), voterId)) {
			// user cannot vote for his own user
			return null;
		}
		int numberOfVotes = 0;
		boolean alreadyVoted = false;
		for (VoteDTO vote : voteCache.values()) {
			if (StringUtils.areStringsEqual(vote.getListing(), userId)) {
				numberOfVotes++;
				if (StringUtils.areStringsEqual(vote.getVoter(), voterId)) {
					// user has already voted for that user
					alreadyVoted = true;
				}
			}
		}
		if (!alreadyVoted) {
			VoteDTO vote = new VoteDTO();
			vote.setListing(null);
			vote.setUser(userId);
			vote.setVoter(voterId);
			vote.setValue(1);
			vote.createKey(String.valueOf(vote.hashCode()));
			numberOfVotes++;
			voteCache.put(vote.getIdAsString(), vote);
		}
		return listing;
	}
	
	@Override
	public List<CommentDTO> getCommentsForListing(String listingId) {
		List<CommentDTO> comments = new ArrayList<CommentDTO>();
		for (CommentDTO comment : commentCache.values()) {
			if (StringUtils.areStringsEqual(comment.getListing(), listingId)) {
				comments.add(comment);
			}
		}
		return comments;
	}

	@Override
	public List<CommentDTO> getCommentsForUser(String userId) {
		List<CommentDTO> comments = new ArrayList<CommentDTO>();
		for (CommentDTO comment : commentCache.values()) {
			if (StringUtils.areStringsEqual(comment.getUser(), userId)) {
				comments.add(comment);
			}
		}
		return comments;
	}

	@Override
	public List<BidDTO> getBidsForListing(String listingId) {
		List<BidDTO> bids = new ArrayList<BidDTO>();
		for (BidDTO bid : bidCache.values()) {
			if (StringUtils.areStringsEqual(bid.getListing(), listingId)) {
				bids.add(bid);
			}
		}
		return bids;
	}
	
	public List<BidDTO> getBidsForUser(String userId) {
		List<BidDTO> bids = new ArrayList<BidDTO>();
		for (BidDTO bid : bidCache.values()) {
			if (StringUtils.areStringsEqual(bid.getUser(), userId)
					&& bid.getStatus() != BidDTO.Status.WITHDRAWN) {
				bids.add(bid);
			}
		}
		return bids;
	}
	
	public List<BidDTO> getBidsAcceptedByUser(String userId) {
		List<BidDTO> bids = new ArrayList<BidDTO>();
		for (BidDTO bid : bidCache.values()) {
			if (StringUtils.areStringsEqual(bid.getListingOwner(), userId)
					&& bid.getStatus() == BidDTO.Status.ACCEPTED) {
				bids.add(bid);
			}
		}
		return bids;
	}

	public List<BidDTO> getBidsFundedByUser(String userId) {
		List<BidDTO> bids = new ArrayList<BidDTO>();
		for (BidDTO bid : bidCache.values()) {
			if (StringUtils.areStringsEqual(bid.getUser(), userId)
					&& bid.getStatus() == BidDTO.Status.ACCEPTED) {
				bids.add(bid);
			}
		}
		return bids;
	}

	@Override
	public int getNumberOfVotesForListing(String listingId) {
		int numberOfVotes = 0;
		for (VoteDTO vote : voteCache.values()) {
			if (StringUtils.areStringsEqual(vote.getListing(), listingId)) {
				numberOfVotes++;
			}
		}
		return numberOfVotes;
	}
	
	@Override
	public int getNumberOfVotesForUser(String userId) {
		int numberOfVotes = 0;
		for (VoteDTO vote : voteCache.values()) {
			if (StringUtils.areStringsEqual(vote.getUser(), userId)) {
				numberOfVotes++;
			}
		}
		return numberOfVotes;
	}

	@Override
	public int getActivity(String listingId) {
		int activity = 0;
		for (CommentDTO comment : commentCache.values()) {
			if (StringUtils.areStringsEqual(comment.getListing(), listingId)) {
				activity++;
			}
		}
		log.log(Level.INFO, "Activity for " + listingId + " is " + activity);
		return activity;
	}

	@Override
	public BidDTO getBid(String bidId) {
		return bidCache.get(bidId);
	}	

	@Override
	public CommentDTO getComment(String commentId) {
		return commentCache.get(commentId);
	}
	
	public CommentDTO deleteComment(String commentId) {
		return commentCache.remove(commentId);
	}

	public CommentDTO createComment(CommentDTO comment) {
		comment.createKey("" + comment.hashCode());
		comment.setCommentedOn(new Date());
		commentCache.put(comment.getIdAsString(), comment);
		return comment;
	}

	public CommentDTO updateComment(CommentDTO newComment) {
		if (!commentCache.containsKey(newComment.getIdAsString())) {
			log.log(Level.WARNING, "Comment '" + newComment.getIdAsString() + "' doesn't exist!");
			return null;
		} else {
			CommentDTO comment = commentCache.get(newComment.getIdAsString());
			comment.setComment(newComment.getComment());
			return comment;
		}
	}

	public BidDTO deleteBid(String bidId) {
		return bidCache.remove(bidId);
	}
	
	public BidDTO activateBid(String loggedInUser, String bidId) {
		if (bidCache.containsKey(bidId)) {
			BidDTO bid = bidCache.get(bidId);
			bid.setStatus(BidDTO.Status.ACTIVE);
			return bid;
		} else {
			return null;
		}
	}

	public BidDTO withdrawBid(String loggedInUser, String bidId) {
		if (bidCache.containsKey(bidId)) {
			BidDTO bid = bidCache.get(bidId);
			bid.setStatus(BidDTO.Status.WITHDRAWN);
			return bid;
		} else {
			return null;
		}
	}

	@Override
	public BidDTO acceptBid(String loggedInUser, String bidId) {
		if (bidCache.containsKey(bidId)) {
			BidDTO bid = bidCache.get(bidId);
			bid.setStatus(BidDTO.Status.ACCEPTED);
			return bid;
		} else {
			return null;
		}
	}
	
	public PaidBidDTO markBidAsPaid(String loggedInUser, String bidId) {
		if (bidCache.containsKey(bidId)) {
			BidDTO bid = bidCache.get(bidId);
			PaidBidDTO paidBid = PaidBidDTO.fromEntity(bid.toEntity());
			paidBidCache.put(paidBid.getIdAsString(), paidBid);
			return paidBid;
		} else {
			return null;
		}
	}
	
	public BidDTO createBid(BidDTO bid) {
		bid.createKey("" + bid.hashCode());
		bid.setPlaced(new Date());
		bidCache.put(bid.getIdAsString(), bid);
		return bid;
	}

	public BidDTO updateBid(String loggedInUser, BidDTO newBid) {
		if (newBid.getStatus() != BidDTO.Status.PAID) {
			if (!bidCache.containsKey(newBid.getIdAsString())) {
				log.log(Level.WARNING, "Bid '" + newBid.getIdAsString() + "' doesn't exist!");
				return null;
			} else {
				BidDTO bid = bidCache.get(newBid.getIdAsString());
				bid.setFundType(newBid.getFundType());
				bid.setValue(newBid.getValue());
				bid.setValuation(newBid.getValuation());
				return bid;
			}
		} else {
			log.info("Bid is marked as paid and cannot be modified");
			return bidCache.get(newBid.getIdAsString());
		}
	}
	
	public List<BidDTO> getBidsByDate(ListPropertiesVO bidsProperties) {
		List<BidDTO> bids = new ArrayList<BidDTO>(bidCache.values());
		
		Collections.sort(bids, new Comparator<BidDTO> () {
			public int compare(BidDTO left, BidDTO right) {
				if (left.getPlaced().getTime() == right.getPlaced().getTime()) {
					return 0;
				} else if (left.getPlaced().getTime() < right.getPlaced().getTime()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		return bids.subList(0, bidsProperties.getMaxResults() < bids.size() ? bidsProperties.getMaxResults() : bids.size());
	}

	public SystemPropertyDTO getSystemProperty(String name) {
		return propCache.get(name);
	}

	public SystemPropertyDTO setSystemProperty(SystemPropertyDTO property) {
		property.createKey(property.getName());
		property.setCreated(new Date());
		propCache.put(property.getName(), property);
		return property;
	}
	
	public List<SystemPropertyDTO> getSystemProperties() {
		List<SystemPropertyDTO> props = new ArrayList<SystemPropertyDTO>();
		for (SystemPropertyDTO prop : propCache.values()) {
			if (prop.getName().contains("pass")) {
				SystemPropertyDTO newProp = new SystemPropertyDTO();
				newProp.setName(prop.getName());
				newProp.setValue("******");
				newProp.setAuthor(prop.getAuthor());
				newProp.setCreated(prop.getCreated());
				props.add(newProp);
			} else {
				props.add(prop);
			}
		}
		return props;
	}
	
	public ListingDocumentDTO createListingDocument(ListingDocumentDTO doc) {
		doc.createKey(doc.getBlob().getKeyString());
		docCache.put(doc.getIdAsString(), doc);
		return doc;
	}
	
	public ListingDocumentDTO getListingDocument(String docId) {
		return docCache.get(docId);
	}
	
	public List<ListingDocumentDTO> getAllListingDocuments() {
		return new ArrayList<ListingDocumentDTO>(docCache.values());
	}
	
	public ListingDocumentDTO deleteDocument(String docId) {
		if (docCache.containsKey(docId)) {
			ListingDocumentDTO doc = docCache.remove(docId);
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			blobstoreService.delete(doc.getBlob());
			return doc;
		} else {
			return null;
		}
	}
	
}
