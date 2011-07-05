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
import com.startupbidder.dto.BusinessPlanDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.UserStatistics;

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
	
	Map<String, BusinessPlanDTO> bpCache = new HashMap<String, BusinessPlanDTO>();
	Map<String, Integer> ratingCache = new HashMap<String, Integer>();
	Map<String, CommentDTO> commentCache = new HashMap<String, CommentDTO>();
	Map<String, Integer> bpCommentCache = new HashMap<String, Integer>();
	Map<String, BidDTO> bidCache = new HashMap<String, BidDTO>();
	Map<String, UserDTO> userCache = new HashMap<String, UserDTO>();

	public MockDatastoreDAO() {
		createMockUsers();
		createMockBusinessPlans();
		generateComments();
		generateBids();
	}
	
	public UserDTO getUser(String key) {
		UserDTO user = (UserDTO)userCache.get(key);
		
		if (user == null) {
			user = new UserDTO();
			user.createKey(key);
			user.setNickname("The " + key);
			user.setFirstName(key);
			user.setLastName("van Damm");
			user.setEmail(key + "vandamm@startupbidder.com");
			
			userCache.put(user.getIdAsString(), user);
		}
		
		return user;
	}
	
	public UserStatistics getUserStatistics(String userId) {
		UserStatistics stat = new UserStatistics();
		
		int numberOfListings = 0;		
		for (BusinessPlanDTO bp : bpCache.values()) {
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
	
	public void updateUser(UserDTO user) {
		user.setModified(new Date(System.currentTimeMillis()));
		log.log(Level.INFO, "Updating user " + user.getKey());
		userCache.put(user.getIdAsString(), user);
	}
	
	public List<BusinessPlanDTO> getTopBusinessPlans(int maxItems) {
		List<BusinessPlanDTO> list = new ArrayList<BusinessPlanDTO>();
		
		// sort rating cache
		List<Map.Entry<String, Integer>> rating = new ArrayList<Map.Entry<String, Integer>>(ratingCache.entrySet());
		Collections.sort(rating, new Comparator<Map.Entry<String, Integer>> () {
			@Override
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
		for (Map.Entry<String, Integer> bpEntry : rating) {
			log.log(Level.INFO, "BP " + bpEntry.getKey() + " - rating " + bpEntry.getValue());
			if (maxItems-- > 0) {
				list.add(bpCache.get(bpEntry.getKey()));
			}
		}
		return list;
	}

	public List<BusinessPlanDTO> getActiveBusinessPlans(int maxItems) {
		List<BusinessPlanDTO> list = new ArrayList<BusinessPlanDTO>();
		
		// create activity list
		Map<String, Integer> sortedBP = new HashMap<String, Integer>();
		for (CommentDTO comment : commentCache.values()) {
			if (!sortedBP.containsKey(comment.getBusinessPlan())) {
				sortedBP.put(comment.getBusinessPlan(), 1);
			} else {
				sortedBP.put(comment.getBusinessPlan(), sortedBP.get(comment.getBusinessPlan()) + 1);
			}
		}
		
		// sort activity list
		List<Map.Entry<String, Integer>> active = new ArrayList<Map.Entry<String, Integer>>(sortedBP.entrySet());
		Collections.sort(active, new Comparator<Map.Entry<String, Integer>> () {
			@Override
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
		for (Map.Entry<String, Integer> bpEntry : active) {
			log.log(Level.INFO, "BP " + bpEntry.getKey() + " - activity " + bpEntry.getValue());
			if (maxItems-- > 0) {
				list.add(bpCache.get(bpEntry.getKey()));
			}
		}
		return list;
	}
	
	public List<BusinessPlanDTO> getUserBusinessPlans(String userId, int maxItems) {
		log.log(Level.INFO, "Business plans owned by " + userId);
		List<BusinessPlanDTO> list = new ArrayList<BusinessPlanDTO>();
		for (BusinessPlanDTO bp : bpCache.values()) {
			if (bp.getOwner().equals(userId)) {
				log.log(Level.INFO, "BP " + bp.getKey());
				list.add(bp);
			}
		}
		list.subList(0, (list.size() < maxItems ? list.size() : maxItems));
		return list;
	}
	
	public int valueUpBusinessPlan(String businessPlanId, String userId) {
		Integer valuation = ratingCache.get(businessPlanId);
		if (valuation == null) {
			log.log(Level.INFO, "Up valuation for " + businessPlanId + " -> 1");
			ratingCache.put(businessPlanId, 1);
		} else {
			log.log(Level.INFO, "Up valuation for " + businessPlanId + " -> " + (valuation + 1));
			ratingCache.put(businessPlanId, valuation + 1);
		}
		return ratingCache.get(businessPlanId);
	}

	public int valueDownBusinessPlan(String businessPlanId, String userId) {
		Integer valuation = ratingCache.get(businessPlanId);
		if (valuation == null) {
			log.log(Level.INFO, "Down valuation for " + businessPlanId + " -> -1");
			ratingCache.put(businessPlanId, -1);
		} else {
			log.log(Level.INFO, "Down valuation for " + businessPlanId + " -> " + (valuation - 1));
			ratingCache.put(businessPlanId, valuation - 1);
		}
		return ratingCache.get(businessPlanId);
	}

	@Override
	public List<CommentDTO> getComments(String businessPlanId) {
		List<CommentDTO> comments = new ArrayList<CommentDTO>();
		for (CommentDTO comment : commentCache.values()) {
			if (comment.getBusinessPlan().equals(businessPlanId)) {
				comments.add(comment);
			}
		}
		return comments;
	}

	@Override
	public List<BidDTO> getBids(String businessPlanId) {
		List<BidDTO> bids = new ArrayList<BidDTO>();
		for (BidDTO bid : bidCache.values()) {
			if (bid.getBusinessPlan().equals(businessPlanId)) {
				bids.add(bid);
			}
		}
		return bids;
	}

	@Override
	public int getRating(String businessPlanId) {
		Integer rating = ratingCache.get(businessPlanId);
		log.log(Level.INFO, "Rating for " + businessPlanId + " is " + rating);
		return rating == null ? 0 : rating.intValue();
	}

	@Override
	public int getActivity(String businessPlanId) {
		Integer activity = bpCommentCache.get(businessPlanId);
		log.log(Level.INFO, "Activity for " + businessPlanId + " is " + activity);
		return activity == null ? 0 : activity.intValue();
	}

	/**
	 * Generates random comments for business plans
	 */
	private void generateComments() {
		List<UserDTO> users = new ArrayList<UserDTO>(userCache.values());
		for (BusinessPlanDTO bp : bpCache.values()) {
			int commentNum = new Random().nextInt(30);
			while (--commentNum > 0) {
				CommentDTO comment = new CommentDTO();
				comment.createKey(commentNum + "_" + bp.hashCode());
				comment.setBusinessPlan(bp.getIdAsString());
				comment.setUser(users.get(new Random().nextInt(users.size())).getIdAsString());
				comment.setCommentedOn(new Date(System.currentTimeMillis() - commentNum * 45 * 60 * 1000));
				comment.setComment("Comment " + commentNum);
				
				commentCache.put(comment.getIdAsString(), comment);
				
				if (bpCommentCache.containsKey(bp.getIdAsString())) {
					bpCommentCache.put(bp.getIdAsString(), bpCommentCache.get(bp.getIdAsString()) + 1);
				} else {
					bpCommentCache.put(bp.getIdAsString(), 1);
				}
			}
		}
	}	

	/**
	 * Generates random bids for business plans
	 */
	private void generateBids() {
		List<UserDTO> users = new ArrayList<UserDTO>();
		for (UserDTO user : userCache.values()) {
			if (user.isAccreditedInvestor()) {
				users.add(user);
			}
		}
		
		for (BusinessPlanDTO bp : bpCache.values()) {
			int bidNum = new Random().nextInt(15);
			while (--bidNum > 0) {
				BidDTO bid = new BidDTO();
				bid.createKey(bidNum + "_" + bp.hashCode());
				bid.setUser(users.get(new Random().nextInt(users.size())).getIdAsString());
				bid.setBusinessPlan(bp.getIdAsString());
				bid.setFundType(new Random().nextInt(2) > 0 ? BidDTO.FundType.SYNDICATE : BidDTO.FundType.SOLE_INVESTOR);
				bid.setPercentOfCompany(new Random().nextInt(50) + 10);
				bid.setPlaced(new Date(System.currentTimeMillis() - bidNum * 53 * 60 * 1000));
				bid.setValue(new Random().nextInt(50) * 1000 + bp.getStartingValuation());
				
				bidCache.put(bid.getIdAsString(), bid);
			}
		}
	}
	
	/**
	 * Generates mock users
	 */
	private void createMockUsers() {
		UserDTO user = new UserDTO();
		user.createKey("deadahmed");
		user.setNickname("Dead");
		user.setFirstName("Ahmed");
		user.setLastName("The Terrorist");
		user.setEmail("deadahmed@startupbidder.com");
		user.setJoined(new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000));
		userCache.put(user.getIdAsString(), user);

		user = new UserDTO();
		user.createKey("jpfowler");
		user.setNickname("fowler");
		user.setFirstName("Jackob");
		user.setLastName("Fowler");
		user.setEmail("jpfowler@startupbidder.com");
		user.setJoined(new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000));
		userCache.put(user.getIdAsString(), user);

		user = new UserDTO();
		user.createKey("businessinsider");
		user.setNickname("Insider");
		user.setFirstName("The");
		user.setLastName("Business");
		user.setEmail("insider@startupbidder.com");
		user.setJoined(new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000));
		userCache.put(user.getIdAsString(), user);

		user = new UserDTO();
		user.createKey("dragonsden");
		user.setNickname("The Dragon");
		user.setFirstName("Mark");
		user.setLastName("Den");
		user.setEmail("dragon@startupbidder.com");
		user.setJoined(new Date(System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000));
		user.setAccreditedInvestor(true);
		userCache.put(user.getIdAsString(), user);

		user = new UserDTO();
		user.createKey("crazyinvestor");
		user.setNickname("MadMax");
		user.setFirstName("Mad");
		user.setLastName("Max");
		user.setEmail("madmax@startupbidder.com");
		user.setJoined(new Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000));
		user.setAccreditedInvestor(true);
		userCache.put(user.getIdAsString(), user);

		user = new UserDTO();
		user.createKey("chinese");
		user.setNickname("The One");
		user.setFirstName("Bruce");
		user.setLastName("Leen");
		user.setEmail("madmax@startupbidder.com");
		user.setJoined(new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000));
		user.setAccreditedInvestor(true);
		userCache.put(user.getIdAsString(), user);
	}

	/**
	 * Generates mock business plans
	 */
	private void createMockBusinessPlans() {
		List<String> userIds = new ArrayList<String>(userCache.keySet());
		int bpNum = 0;
	
		BusinessPlanDTO bp = new BusinessPlanDTO();
		bp.createKey("mislead");
		bp.setName("MisLead");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setStartingValuation(20000);
		bp.setStartingValuationDate(new Date(System.currentTimeMillis() - 45 * 60 * 60 * 1000));
		bp.setListedOn(new Date(System.currentTimeMillis() - 45 * 60 * 60 * 1000));
		bp.setState(BusinessPlanDTO.State.ACTIVE);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 12 * 24 * 60 * 60 * 1000));
		bp.setSummary("Executive summary for <b>MisLead</b>");
		bpCache.put(bp.getIdAsString(), bp);
		ratingCache.put(bp.getIdAsString(), 5);

	
		bp = new BusinessPlanDTO();
		bp.createKey("semanticsearch");
		bp.setName("Semantic Search");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setStartingValuation(40000);
		bp.setStartingValuationDate(new Date(System.currentTimeMillis() - 15 * 60 * 60 * 1000));
		bp.setListedOn(new Date(System.currentTimeMillis() - 15 * 60 * 60 * 1000));
		bp.setState(BusinessPlanDTO.State.ACTIVE);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 24 * 24 * 60 * 60 * 1000));
		bp.setSummary("The fact of the matter is Google, and to a much lesser extent Bing, " + 
				"own the search market. Ask Barry Diller, if you don't believe us." +
				"Yet, startups still spring up hoping to disrupt the incumbents. " +
				"Cuil flopped. Wolfram Alpha is irrelevant. Powerset, which was a semantic" + 
				" search engine was bailed out by Microsoft, which acquired it.");
		bpCache.put(bp.getIdAsString(), bp);
		ratingCache.put(bp.getIdAsString(), 8);

		bp = new BusinessPlanDTO();
		bp.createKey("socialrecommendations");
		bp.setName("Social recommendations");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setStartingValuation(15000);
		bp.setStartingValuationDate(new Date(System.currentTimeMillis() - 20 * 60 * 60 * 1000));
		bp.setListedOn(new Date(System.currentTimeMillis() - 20 * 60 * 60 * 1000));
		bp.setState(BusinessPlanDTO.State.ACTIVE);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000));
		bp.setSummary("It's a very tempting idea. Collect data from people about their tastes" +
				" and preferences. Then use that data to create recommendations for others. " +
				"Or, use that data to create recommendations for the people that filled in " +
				"the information. It doesn't work. The latest to try is Hunch and Get Glue." +
				"Hunch is pivoting towards non-consumer-facing white label business. " +
				"Get Glue has had some success of late, but it's hardly a breakout business.");
		bpCache.put(bp.getIdAsString(), bp);
		ratingCache.put(bp.getIdAsString(), 45);

		bp = new BusinessPlanDTO();
		bp.createKey("localnewssites");
		bp.setName("Local news sites");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setStartingValuation(49000);
		bp.setStartingValuationDate(new Date(System.currentTimeMillis() - 3 * 60 * 60 * 1000));
		bp.setListedOn(new Date(System.currentTimeMillis() - 3 * 60 * 60 * 1000));
		bp.setState(BusinessPlanDTO.State.ACTIVE);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 27 * 24 * 60 * 60 * 1000));
		bp.setSummary("Maybe Tim Armstrong, AOL, and Patch will prove it wrong, but to this point" +
				" nobody has been able to crack the local news market and make a sustainable business." +
				"In theory creating a network of local news sites that people care about is a good" +
				" idea. You build a community, there's a baked in advertising group with local " +
				"businesses, and classifieds. But, it appears to be too niche to scale into a big" +
				" business.");
		bpCache.put(bp.getIdAsString(), bp);
		ratingCache.put(bp.getIdAsString(), -6);

		bp = new BusinessPlanDTO();
		bp.createKey("micropayments");
		bp.setName("Micropayments");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setStartingValuation(5000);
		bp.setStartingValuationDate(new Date(System.currentTimeMillis() - 23 * 60 * 60 * 1000));
		bp.setListedOn(new Date(System.currentTimeMillis() - 23 * 60 * 60 * 1000));
		bp.setState(BusinessPlanDTO.State.ACTIVE);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000));
		bp.setSummary("Micropayments are one idea that's tossed around to solve the problem" +
				" of paying for content on the Web. If you want to read a New York Times " +
				"story it would only cost a nickel! Or on Tumblr, if you want to tip a blogger" +
				" or pay for a small design you could with ease. So far, these micropayment" +
				" plans have not worked.");
		bpCache.put(bp.getIdAsString(), bp);
		ratingCache.put(bp.getIdAsString(), 0);

		bp = new BusinessPlanDTO();
		bp.createKey("kill email");
		bp.setName("Kill email");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setStartingValuation(40000);
		bp.setStartingValuationDate(new Date(System.currentTimeMillis() - 6 * 60 * 60 * 1000));
		bp.setListedOn(new Date(System.currentTimeMillis() - 6 * 60 * 60 * 1000));
		bp.setState(BusinessPlanDTO.State.ACTIVE);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 78 * 24 * 60 * 60 * 1000));
		bp.setSummary("If any startup says it's going to eliminate email, it's destined for failure. " +
				"You can iterate on the inbox, and try to improve it, but even that's " +
				"not much of a business. The latest high profile flop in this arena is " +
				"Google Wave. It was supposed to change email forever. It was going to " +
				"displace email. Didn't happen.");
		bpCache.put(bp.getIdAsString(), bp);
		ratingCache.put(bp.getIdAsString(), -38);

		bp = new BusinessPlanDTO();
		bp.createKey("better company car");
		bp.setName("Better company car");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setStartingValuation(100000);
		bp.setStartingValuationDate(new Date(System.currentTimeMillis() - 8 * 60 * 60 * 1000));
		bp.setListedOn(new Date(System.currentTimeMillis() - 8 * 60 * 60 * 1000));
		bp.setState(BusinessPlanDTO.State.ACTIVE);
		bp.setClosingOn(new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000));
		bp.setSummary("Considering how frustrated people are with car companies, you'd think " +
				"launching a new one would be perfect for a startup. So far, that's not the case. " +
				"You can point to Tesla as a success, and considering it IPO'd it's hard to argue " +
				"against it. But, Tesla has sold fewer than 2,000 cars since it was founded in 2003. " +
				"It's far from certain it will succeed. Even when its next car comes out, Nissan " +
				"could be making a luxury electric car that competes with Tesla.");
		bpCache.put(bp.getIdAsString(), bp);
		ratingCache.put(bp.getIdAsString(), 9);
	}	
}
