package com.startupbidder.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.UserStatistics;
import com.startupbidder.dto.VoteDTO;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.ListPropertiesVO;

/**
 * Data access object which handles all interaction with underlying persistence layer.
 * By default it responds with mock data, implementation which uses AppEngine Datastore
 * is implemented in derived class.
 *  
 * @author greg
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
	
	Map<String, ListingDTO> lCache = new HashMap<String, ListingDTO>();
	Map<String, VoteDTO> voteCache = new HashMap<String, VoteDTO>();
	Map<String, CommentDTO> commentCache = new HashMap<String, CommentDTO>();
	Map<String, BidDTO> bidCache = new HashMap<String, BidDTO>();
	Map<String, UserDTO> userCache = new HashMap<String, UserDTO>();

	public MockDatastoreDAO() {
		createMockUsers();
		createMockListings();
		generateComments();
		generateBids();
	}
	
	public UserDTO getUser(String key) {
		UserDTO user = (UserDTO)userCache.get(key);		
		return user;
	}
	
	public UserDTO getUserByOpenId(String openId) {
		for(UserDTO user : userCache.values()) {
			if (user.getOpenId().equals(openId)) {
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
	
	public UserStatistics getUserStatistics(String userId) {
		UserStatistics stat = new UserStatistics();
		
		int numberOfListings = 0;
		for (ListingDTO bp : lCache.values()) {
			if (userId.equals(bp.getOwner())) {
				numberOfListings++;
			}
		}
		stat.setNumberOfListings(numberOfListings);
		
		int numberOfBids = 0;
		for (BidDTO bid : bidCache.values()) {
			if (userId.equals(bid.getUser())) {
				numberOfBids++;
			}
		}
		stat.setNumberOfBids(numberOfBids);
		
		int numberOfComments = 0;
		for (CommentDTO comment : commentCache.values()) {
			if (userId.equals(comment.getUser())) {
				numberOfComments++;
			}
		}
		stat.setNumberOfComments(numberOfComments);
		
		return stat;
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
	
	public boolean canVote(String userId, String listingId) {
		log.log(Level.INFO, "Check votes for user '" + userId + "' and listing '" + listingId + "'");
		for (VoteDTO vote : voteCache.values()) {
			log.log(Level.INFO, "  --> " + vote.toString());
			if (vote.getListing().equals(listingId)) {
				if (vote.getUser().equals(userId)) {
					// user has already voted for that listing
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
			if (vote.getUser().equals(userId)) {
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
			if(accVotes.containsKey(vote.getListing())) {
				accVotes.put(vote.getListing(), accVotes.get(vote.getListing()) + 1);
			} else {
				accVotes.put(vote.getListing(), 1);
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
			if (!listingIds.containsKey(vote.getListing())) {
				listingIds.put(vote.getListing(), 1);
			} else {
				listingIds.put(vote.getListing(), listingIds.get(vote.getListing()) + 1);
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
	
	public List<ListingDTO> getUserListings(String userId, ListPropertiesVO listingProperties) {
		log.log(Level.INFO, "Business plans owned by " + userId);
		List<ListingDTO> list = new ArrayList<ListingDTO>();
		for (ListingDTO bp : lCache.values()) {
			if (bp.getOwner().equals(userId)) {
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


	public ListingDTO valueUpListing(String listingId, String userId) {
		ListingDTO listing = getListing(listingId);
		int numberOfVotes = 0;
		boolean alreadyVoted = false;
		for (VoteDTO vote : voteCache.values()) {
			if (vote.getListing().equals(listingId)) {
				numberOfVotes++;
				if (vote.getUser().equals(userId)) {
					// user has already voted for that listing
					alreadyVoted = true;
				}
			}
		}
		if (!alreadyVoted) {
			VoteDTO vote = new VoteDTO();
			vote.setListing(listingId);
			vote.setUser(userId);
			vote.setValue(1);
			vote.createKey(String.valueOf(vote.hashCode()));
			numberOfVotes++;
			voteCache.put(vote.getIdAsString(), vote);
		}
		return listing;
	}
	
	public ListingDTO valueDownListing(String listingId, String userId) {
		log.log(Level.SEVERE, "valueDownListing is not supported now, we only care about number of votes");
		ListingDTO listing = getListing(listingId);
		int numberOfVotes = 0;
		for (VoteDTO vote : voteCache.values()) {
			if (vote.getListing().equals(listingId)) {
				numberOfVotes++;
			}
		}
		return listing;
	}

	@Override
	public List<CommentDTO> getCommentsForListing(String listingId) {
		List<CommentDTO> comments = new ArrayList<CommentDTO>();
		for (CommentDTO comment : commentCache.values()) {
			if (comment.getListing().equals(listingId)) {
				comments.add(comment);
			}
		}
		return comments;
	}

	@Override
	public List<CommentDTO> getCommentsForUser(String userId) {
		List<CommentDTO> comments = new ArrayList<CommentDTO>();
		for (CommentDTO comment : commentCache.values()) {
			if (comment.getUser().equals(userId)) {
				comments.add(comment);
			}
		}
		return comments;
	}

	@Override
	public List<BidDTO> getBidsForListing(String listingId) {
		List<BidDTO> bids = new ArrayList<BidDTO>();
		for (BidDTO bid : bidCache.values()) {
			if (bid.getListing().equals(listingId)) {
				bids.add(bid);
			}
		}
		return bids;
	}
	
	public List<BidDTO> getBidsForUser(String userId) {
		List<BidDTO> bids = new ArrayList<BidDTO>();
		for (BidDTO bid : bidCache.values()) {
			if (bid.getUser().equals(userId)) {
				bids.add(bid);
			}
		}
		return bids;
	}

	@Override
	public int getNumberOfVotes(String listingId) {
		int numberOfVotes = 0;
		for (VoteDTO vote : voteCache.values()) {
			if (vote.getListing().equals(listingId)) {
				numberOfVotes++;
			}
		}
		return numberOfVotes;
	}

	@Override
	public int getActivity(String listingId) {
		int activity = 0;
		for (CommentDTO comment : commentCache.values()) {
			if (comment.getListing().equals(listingId)) {
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

	public BidDTO createBid(BidDTO bid) {
		bid.createKey("" + bid.hashCode());
		bid.setPlaced(new Date());
		bidCache.put(bid.getIdAsString(), bid);
		return bid;
	}

	public BidDTO updateBid(BidDTO newBid) {
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
	}

	/**
	 * Generates random comments for business plans
	 */
	private void generateComments() {
		List<UserDTO> users = new ArrayList<UserDTO>(userCache.values());
		for (ListingDTO bp : lCache.values()) {
			int commentNum = new Random().nextInt(30);
			while (--commentNum > 0) {
				CommentDTO comment = new CommentDTO();
				comment.createKey(commentNum + "_" + bp.hashCode());
				comment.setListing(bp.getIdAsString());
				comment.setUser(users.get(new Random().nextInt(users.size())).getIdAsString());
				comment.setCommentedOn(new Date(System.currentTimeMillis() - commentNum * 45 * 60 * 1000));
				comment.setComment("Comment " + commentNum);
				
				log.log(Level.INFO, comment.toString());
				commentCache.put(comment.getIdAsString(), comment);
			}
		}
	}	

	/**
	 * Generates random bids for listings
	 */
	private void generateBids() {
		List<UserDTO> users = new ArrayList<UserDTO>();
		for (UserDTO user : userCache.values()) {
			if (user.isInvestor()) {
				users.add(user);
			}
		}
		
		for (ListingDTO listing : lCache.values()) {
			int bidNum = new Random().nextInt(15);
			while (--bidNum > 0) {
				BidDTO bid = new BidDTO();
				bid.createKey(bidNum + "_" + listing.hashCode());
				bid.setUser(users.get(new Random().nextInt(users.size())).getIdAsString());
				bid.setListing(listing.getIdAsString());
				bid.setFundType(new Random().nextInt(2) > 0 ? BidDTO.FundType.SYNDICATE : BidDTO.FundType.SOLE_INVESTOR);
				bid.setPercentOfCompany(new Random().nextInt(50) + 10);
				bid.setPlaced(new Date(System.currentTimeMillis() - bidNum * 53 * 60 * 1000));
				bid.setValue(new Random().nextInt(50) * 1000 + listing.getSuggestedValuation());
				// calculate valuation
				bid.setValuation(bid.getValue() * 100 / bid.getPercentOfCompany());
				
				log.log(Level.INFO, bid.toString());
				bidCache.put(bid.getIdAsString(), bid);
			}
		}
	}
	
	/**
	 * Generates mock users
	 */
	private void createMockUsers() {
		String key = "";
		
		UserDTO user = new UserDTO();
		key = "deadahmed";
		user.createKey(key);
		user.setNickname("Dead");
		user.setName("Ahmed");
		user.setEmail("deadahmed@startupbidder.com");
		user.setOpenId(user.getEmail());
		user.setJoined(new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000));
		user.setStatus(UserDTO.Status.ACTIVE);
		user.setFacebook("fb_" + key);
		user.setJoined(new Date());
		user.setLastLoggedIn(new Date());
		user.setModified(new Date());
		user.setTitle("Dr");
		user.setTwitter("twit_" + key);
		user.setLinkedin("ln_" + key);
		userCache.put(user.getIdAsString(), user);
		log.log(Level.INFO, user.toString());

		user = new UserDTO();
		key = "jpfowler";
		user.createKey(key);
		user.setNickname("fowler");
		user.setName("Jackob");
		user.setEmail("jpfowler@startupbidder.com");
		user.setOpenId(user.getEmail());
		user.setJoined(new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000));
		user.setStatus(UserDTO.Status.ACTIVE);
		user.setFacebook("fb_" + key);
		user.setJoined(new Date());
		user.setLastLoggedIn(new Date());
		user.setModified(new Date());
		user.setOrganization("org_" + key);
		user.setTitle("Dr");
		user.setTwitter("twit_" + key);
		user.setLinkedin("ln_" + key);
		userCache.put(user.getIdAsString(), user);
		log.log(Level.INFO, user.toString());

		user = new UserDTO();
		key = "businessinsider";
		user.createKey(key);
		user.setNickname("Insider");
		user.setName("The");
		user.setEmail("insider@startupbidder.com");
		user.setOpenId(user.getEmail());
		user.setJoined(new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000));
		user.setStatus(UserDTO.Status.ACTIVE);
		user.setFacebook("fb_" + key);
		user.setJoined(new Date());
		user.setLastLoggedIn(new Date());
		user.setModified(new Date());
		user.setTwitter("twit_" + key);
		userCache.put(user.getIdAsString(), user);
		log.log(Level.INFO, user.toString());

		user = new UserDTO();
		key = "dragonsden";
		user.createKey(key);
		user.setNickname("The Dragon");
		user.setName("Mark");
		user.setEmail("dragon@startupbidder.com");
		user.setOpenId(user.getEmail());
		user.setJoined(new Date(System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000));
		user.setStatus(UserDTO.Status.ACTIVE);
		user.setFacebook("fb_" + key);
		user.setJoined(new Date());
		user.setLastLoggedIn(new Date());
		user.setModified(new Date());
		user.setOrganization("org_" + key);
		user.setTitle("Dr");
		user.setLinkedin("ln_" + key);
		user.setInvestor(true);
		userCache.put(user.getIdAsString(), user);
		log.log(Level.INFO, user.toString());

		user = new UserDTO();
		key = "crazyinvestor";
		user.createKey(key);
		user.setNickname("MadMax");
		user.setName("Mad");
		user.setEmail("madmax@startupbidder.com");
		user.setOpenId(user.getEmail());
		user.setJoined(new Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000));
		user.setStatus(UserDTO.Status.ACTIVE);
		user.setInvestor(true);
		user.setFacebook("fb_" + key);
		user.setJoined(new Date());
		user.setLastLoggedIn(new Date());
		user.setModified(new Date());
		user.setOrganization("org_" + key);
		user.setTitle("Dr");
		user.setTwitter("twit_" + key);
		user.setLinkedin("ln_" + key);
		userCache.put(user.getIdAsString(), user);
		log.log(Level.INFO, user.toString());

		user = new UserDTO();
		key = "chinese";
		user.createKey(key);
		user.setNickname("The One");
		user.setName("Bruce");
		user.setEmail("madmax@startupbidder.com");
		user.setOpenId(user.getEmail());
		user.setJoined(new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000));
		user.setStatus(UserDTO.Status.DEACTIVATED);
		user.setInvestor(true);
		user.setJoined(new Date());
		user.setLastLoggedIn(new Date());
		user.setModified(new Date());
		user.setOrganization("org_" + key);
		user.setTitle("Dr");
		user.setTwitter("twit_" + key);
		user.setLinkedin("ln_" + key);
		userCache.put(user.getIdAsString(), user);
		log.log(Level.INFO, user.toString());
	}
	
	private void createMockVotes(ListingDTO listing) {
		List<UserDTO> users = new ArrayList<UserDTO>(userCache.values());
		int numOfVotes = new Random().nextInt(users.size());
		while (numOfVotes > 0) {
			VoteDTO vote = new VoteDTO();
			vote.setListing(listing.getIdAsString());
			vote.setUser(users.get(numOfVotes).getIdAsString());
			vote.setValue(1);
			vote.createKey(String.valueOf(vote.hashCode()));
			voteCache.put(vote.getIdAsString(), vote);
			numOfVotes--;
		}
	}

	/**
	 * Generates mock listings
	 */
	private void createMockListings() {
		List<String> userIds = new ArrayList<String>(userCache.keySet());
		int bpNum = 0;
	
		ListingDTO bp = new ListingDTO();
		bp.createKey("mislead");
		bp.setName("MisLead");
		bp.setOwner(userIds.get(bpNum % userIds.size()));
		bp.setSuggestedValuation(20000);
		bp.setSuggestedPercentage(25);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 45 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.CREATED);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 12 * 24 * 60 * 60 * 1000L));
		bp.setSummary("Executive summary for <b>MisLead</b>");
		lCache.put(bp.getIdAsString(), bp);
		log.log(Level.INFO, bp.toString());
		createMockVotes(bp);
	
		bp = new ListingDTO();
		bp.createKey("comp_training_camp");
		bp.setName("Computer Training Camp");
		bp.setOwner(userIds.get(bpNum % userIds.size()));
		bp.setSuggestedValuation(15000);
		bp.setSuggestedPercentage(45);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 5 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.CREATED);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 35 * 24 * 60 * 60 * 1000L));
		bp.setSummary("Starting a computer training camp for children is a terrific new business " +
				"venture to set in motion. In spite of the fact that many children now receive " +
				"computer training in school, attending computer camps ensures parents and" +
				" children a better and more complete understanding of the course material. " +
				"The computer camps can be operated on a year-round basis or in the summer only. " +
				"Typically, these camps are one or two days in length and available for various " +
				"training needs, from beginner to advanced. Once again, this is the kind of children's" +
				" business that can be operated as an independent business venture or operated in" +
				" conjunction with a community program or community center.");
		lCache.put(bp.getIdAsString(), bp);
		log.log(Level.INFO, bp.toString());
		createMockVotes(bp);

		bp = new ListingDTO();
		bp.createKey("comp_upgrading_service");
		bp.setName("Computer Upgrading Service");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setSuggestedValuation(35000);
		bp.setSuggestedPercentage(33);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 15 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.ACTIVE);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 25 * 24 * 60 * 60 * 1000L));
		bp.setSummary("Starting a business that specializes in upgrading existing computer systems" +
				" with new internal and external equipment is a terrific homebased business to " +
				"initiate that has great potential to earn an outstanding income for the operator" +
				" of the business. A computer upgrading service is a very easy business to get" +
				" rolling, providing you have the skills and equipment necessary to complete upgrading" +
				" tasks, such as installing more memory into the hard drive, replacing a hard drive," +
				" or adding a new disk drive to the computer system. Ideally, to secure the most " +
				"profitable segment of the potential market, the service should specialize in " +
				"upgrading business computers as there are many reasons why a business would upgrade" +
				" a computer system as opposed to replacing the computer system. Additionally, managing" +
				" the business from a homebased location while providing clients with a mobile service" +
				" is the best way to keep operating overheads minimized and potentially increases the" +
				" size of the target market by expanding the service area, due to the fact the business" +
				" operates on a mobile format.");
		lCache.put(bp.getIdAsString(), bp);
		log.log(Level.INFO, bp.toString());
		createMockVotes(bp);

		bp = new ListingDTO();
		bp.createKey("semanticsearch");
		bp.setName("Semantic Search");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setSuggestedValuation(40000);
		bp.setSuggestedPercentage(45);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 15 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.ACTIVE);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 24 * 24 * 60 * 60 * 1000L));
		bp.setSummary("The fact of the matter is Google, and to a much lesser extent Bing, " + 
				"own the search market. Ask Barry Diller, if you don't believe us." +
				"Yet, startups still spring up hoping to disrupt the incumbents. " +
				"Cuil flopped. Wolfram Alpha is irrelevant. Powerset, which was a semantic" + 
				" search engine was bailed out by Microsoft, which acquired it.");
		lCache.put(bp.getIdAsString(), bp);
		log.log(Level.INFO, bp.toString());
		createMockVotes(bp);

		bp = new ListingDTO();
		bp.createKey("socialrecommendations");
		bp.setName("Social recommendations");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setSuggestedValuation(15000);
		bp.setSuggestedPercentage(10);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 20 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.ACTIVE);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L));
		bp.setSummary("It's a very tempting idea. Collect data from people about their tastes" +
				" and preferences. Then use that data to create recommendations for others. " +
				"Or, use that data to create recommendations for the people that filled in " +
				"the information. It doesn't work. The latest to try is Hunch and Get Glue." +
				"Hunch is pivoting towards non-consumer-facing white label business. " +
				"Get Glue has had some success of late, but it's hardly a breakout business.");
		lCache.put(bp.getIdAsString(), bp);
		log.log(Level.INFO, bp.toString());
		createMockVotes(bp);

		bp = new ListingDTO();
		bp.createKey("localnewssites");
		bp.setName("Local news sites");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setSuggestedValuation(49000);
		bp.setSuggestedPercentage(20);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 3 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.ACTIVE);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 27 * 24 * 60 * 60 * 1000L));
		bp.setSummary("Maybe Tim Armstrong, AOL, and Patch will prove it wrong, but to this point" +
				" nobody has been able to crack the local news market and make a sustainable business." +
				"In theory creating a network of local news sites that people care about is a good" +
				" idea. You build a community, there's a baked in advertising group with local " +
				"businesses, and classifieds. But, it appears to be too niche to scale into a big" +
				" business.");
		lCache.put(bp.getIdAsString(), bp);
		log.log(Level.INFO, bp.toString());
		createMockVotes(bp);

		bp = new ListingDTO();
		bp.createKey("micropayments");
		bp.setName("Micropayments");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setSuggestedValuation(5000);
		bp.setSuggestedPercentage(49);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 23 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.CREATED);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000L));
		bp.setSummary("Micropayments are one idea that's tossed around to solve the problem" +
				" of paying for content on the Web. If you want to read a New York Times " +
				"story it would only cost a nickel! Or on Tumblr, if you want to tip a blogger" +
				" or pay for a small design you could with ease. So far, these micropayment" +
				" plans have not worked.");
		lCache.put(bp.getIdAsString(), bp);
		log.log(Level.INFO, bp.toString());
		createMockVotes(bp);

		bp = new ListingDTO();
		bp.createKey("kill email");
		bp.setName("Kill email");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setSuggestedValuation(40000);
		bp.setSuggestedPercentage(50);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.ACTIVE);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 78 * 24 * 60 * 60 * 1000L));
		bp.setSummary("If any startup says it's going to eliminate email, it's destined for failure. " +
				"You can iterate on the inbox, and try to improve it, but even that's " +
				"not much of a business. The latest high profile flop in this arena is " +
				"Google Wave. It was supposed to change email forever. It was going to " +
				"displace email. Didn't happen.");
		lCache.put(bp.getIdAsString(), bp);
		log.log(Level.INFO, bp.toString());
		createMockVotes(bp);

		bp = new ListingDTO();
		bp.createKey("better company car");
		bp.setName("Better company car");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setSuggestedValuation(100000);
		bp.setSuggestedPercentage(15);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 8 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.CLOSED);
		bp.setClosingOn(new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000L));
		bp.setSummary("Considering how frustrated people are with car companies, you'd think " +
				"launching a new one would be perfect for a startup. So far, that's not the case. " +
				"You can point to Tesla as a success, and considering it IPO'd it's hard to argue " +
				"against it. But, Tesla has sold fewer than 2,000 cars since it was founded in 2003. " +
				"It's far from certain it will succeed. Even when its next car comes out, Nissan " +
				"could be making a luxury electric car that competes with Tesla.");
		lCache.put(bp.getIdAsString(), bp);
		log.log(Level.INFO, bp.toString());
		createMockVotes(bp);
	}
}
