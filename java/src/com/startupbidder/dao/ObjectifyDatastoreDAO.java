package com.startupbidder.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.datanucleus.util.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.google.appengine.api.datastore.QueryResultIterable;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.ListingStats;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.PaidBid;
import com.startupbidder.datamodel.Rank;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.SystemProperty;
import com.startupbidder.datamodel.UserStats;
import com.startupbidder.datamodel.Vote;
import com.startupbidder.vo.ListPropertiesVO;

/**
 * Datastore implementation which uses Google's AppEngine Datastore through Objectify interfaces.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class ObjectifyDatastoreDAO {
	private static final Logger log = Logger.getLogger(ObjectifyDatastoreDAO.class.getName());
	static ObjectifyDatastoreDAO instance;
		
	public static ObjectifyDatastoreDAO getInstance() {
		if (instance == null) {
			instance = new ObjectifyDatastoreDAO();
		}
		return instance;
	}

	private ObjectifyDatastoreDAO() {
	}
	
	public String clearDatastore() {
		return iterateThroughDatastore(true, new ArrayList<Object>());
	}
	
	public String printDatastoreContents() {
		return iterateThroughDatastore(false, new ArrayList<Object>());
	}
	
	public List<Object> exportDatastoreContents() {
		List<Object> dtoList = new ArrayList<Object>();
		iterateThroughDatastore(false, dtoList);
		return dtoList;
	}
	
	public String createMockDatastore(long loggedInUserId) {
		// delete logged in user as he will be recreated
		getOfy().delete(SBUser.class, loggedInUserId);
		
		initMocks();
		return iterateThroughDatastore(false, new ArrayList<Object>());
	}
	
	public String iterateThroughDatastore(boolean delete, List<Object> dtoList) {
		StringBuffer outputBuffer = new StringBuffer();
		outputBuffer.append("<a href=\"/setup\">Setup page</a>");
		if (delete) {
			outputBuffer.append("<p>Deleted objects:</p>");
		} else {
			outputBuffer.append("<p>Datastore objects:</p>");
		}
		
		List<Key<SBUser>> userKeys = new ArrayList<Key<SBUser>>();
		CollectionUtils.addAll(userKeys, getOfy().query(SBUser.class).fetchKeys().iterator());
		outputBuffer.append("<p>Users (" + userKeys.size() + "):</p>");
		for (SBUser obj : getOfy().get(userKeys).values()) {
			outputBuffer.append(obj).append("<br/>");
		}
		if (delete) {
			getOfy().delete(userKeys);
		}
		
		List<Key<UserStats>> userStatKeys = new ArrayList<Key<UserStats>>();
		CollectionUtils.addAll(userStatKeys, getOfy().query(UserStats.class).fetchKeys().iterator());
		outputBuffer.append("<p>User stats (" + userStatKeys.size() + "):</p>");
		for (UserStats obj : getOfy().get(userStatKeys).values()) {
			outputBuffer.append(obj).append("<br/>");
		}
		if (delete) {
			getOfy().delete(userStatKeys);
		}
		
		List<Key<Bid>> bidKeys = new ArrayList<Key<Bid>>();
		CollectionUtils.addAll(bidKeys, getOfy().query(Bid.class).fetchKeys().iterator());
		outputBuffer.append("<p>Bids (" + bidKeys.size() + "):</p>");
		if (bidKeys.size() > 0) {
			//for (Bid obj : getOfy().get(bidKeys).values()) {
			for (Key<Bid> key : bidKeys) {
				Bid obj = getOfy().get(key);
				outputBuffer.append(obj).append("<br/>");
			}
			if (delete) {
				getOfy().delete(bidKeys);
			}
		}
		
		List<Key<PaidBid>> paidBidKeys = new ArrayList<Key<PaidBid>>();
		CollectionUtils.addAll(paidBidKeys, getOfy().query(PaidBid.class).fetchKeys().iterator());
		outputBuffer.append("<p>Paid bids (" + paidBidKeys.size() + "):</p>");
		if (paidBidKeys.size() > 0) {
			for (PaidBid obj : getOfy().get(paidBidKeys).values()) {
				outputBuffer.append(obj).append("<br/>");
			}
			if (delete) {
				getOfy().delete(paidBidKeys);
			}
		}
		
		List<Key<Comment>> comKeys = new ArrayList<Key<Comment>>();
		CollectionUtils.addAll(comKeys, getOfy().query(Comment.class).fetchKeys().iterator());
		outputBuffer.append("<p>Comments (" + comKeys.size() + "):</p>");
		if (comKeys.size() > 0) {
			for (Comment obj : getOfy().get(comKeys).values()) {
				outputBuffer.append(obj).append("<br/>");
			}
			if (delete) {
				getOfy().delete(comKeys);
			}
		}

		List<Key<Listing>> listingKeys = new ArrayList<Key<Listing>>();
		CollectionUtils.addAll(listingKeys, getOfy().query(Listing.class).fetchKeys().iterator());
		outputBuffer.append("<p>Listings (" + listingKeys.size() + "):</p>");
		if (listingKeys.size() > 0) {
//			for (Listing obj : getOfy().get(listingKeys).values()) {
//				outputBuffer.append(obj).append("<br/>");
//			}
			if (delete) {
				getOfy().delete(listingKeys);
			}
		}

		List<Key<ListingDoc>> listingDocKeys = new ArrayList<Key<ListingDoc>>();
		CollectionUtils.addAll(listingDocKeys, getOfy().query(ListingDoc.class).fetchKeys().iterator());
		outputBuffer.append("<p>Listing docs (" + listingDocKeys.size() + "):</p>");
		if (listingDocKeys.size() > 0) {
//			for (ListingDoc obj : getOfy().get(listingDocKeys).values()) {
//				outputBuffer.append(obj).append("<br/>");
//			}
			if (delete) {
				getOfy().delete(listingDocKeys);
			}
		}
		
		List<Key<ListingStats>> listingStatKeys = new ArrayList<Key<ListingStats>>();
		CollectionUtils.addAll(listingStatKeys, getOfy().query(ListingStats.class).fetchKeys().iterator());
//		outputBuffer.append("<p>Listing stats (" + listingStatKeys.size() + "):</p>");
//		for (ListingStats obj : getOfy().get(listingStatKeys).values()) {
//			outputBuffer.append(obj).append("<br/>");
//		}
		if (delete) {
			getOfy().delete(listingStatKeys);
		}
		
		List<Key<Rank>> rankKeys = new ArrayList<Key<Rank>>();
		CollectionUtils.addAll(rankKeys, getOfy().query(Rank.class).fetchKeys().iterator());
//		outputBuffer.append("<p>Listing stats (" + rankKeys.size() + "):</p>");
//		for (Rank obj : getOfy().get(rankKeys).values()) {
//			outputBuffer.append(obj).append("<br/>");
//		}
		if (delete) {
			getOfy().delete(rankKeys);
		}

		List<Key<SystemProperty>> propKeys = new ArrayList<Key<SystemProperty>>();
		CollectionUtils.addAll(propKeys, getOfy().query(SystemProperty.class).fetchKeys().iterator());
//		outputBuffer.append("<p>System properties (" + propKeys.size() + "):</p>");
//		for (SystemProperty obj : getOfy().get(propKeys).values()) {
//			outputBuffer.append(obj).append("<br/>");
//		}
		if (delete) {
			getOfy().delete(propKeys);
		}

		List<Key<Vote>> voteKeys = new ArrayList<Key<Vote>>();
		CollectionUtils.addAll(voteKeys, getOfy().query(Vote.class).fetchKeys().iterator());
//		outputBuffer.append("<p>Votes (" + voteKeys.size() + "):</p>");
//		for (Vote obj : getOfy().get(voteKeys).values()) {
//			outputBuffer.append(obj).append("<br/>");
//		}
		if (delete) {
			getOfy().delete(voteKeys);
		}
		
		outputBuffer.append("<p><a href=\"/setup\">Setup page</a></p>");
		return outputBuffer.toString();
	}
	
	private void initMocks() {
		MockDataBuilder mockBuilder = new MockDataBuilder();
		
		List<SBUser> users = mockBuilder.createMockUsers();
		getOfy().put(users);
		
		List<Listing> listings = mockBuilder.createMockListings(users);
		getOfy().put(listings);
		
		getOfy().put(mockBuilder.createMockVotes(users, listings));
		getOfy().put(mockBuilder.generateComments(users, listings));
		getOfy().put(mockBuilder.generateBids(users, listings));

		// updating user stats
		for (SBUser user : users) {
			updateUserStatistics(user.id);
		}
		// update listing stats
		for (Listing listing : listings) {
			updateListingStatistics(listing.id);
		}
	}
	
	private Objectify getOfy() {
		Objectify ofy = ObjectifyService.begin();
		return ofy;
	}

	public SBUser getUser(String userId) {
		try {
			return (SBUser)getOfy().get(Key.create(userId));
		} catch (Exception e) {
			log.log(Level.WARNING, "User '" + userId + "'not found", e);
			return null;
		}
	}

	public SBUser getUserByEmail(String email) {
		SBUser user = getOfy().query(SBUser.class).filter("email =", email).get();
		log.info("User for " + email + " is: " + user);
		return user;
	}

	public SBUser getUserByAuthCookie(String authCookie) {
		SBUser user = getOfy().query(SBUser.class).filter("authCookie =", authCookie).get();
		log.info("User for cookie '" + authCookie + "' is: " + user);
		return user != null && user.status == SBUser.Status.ACTIVE ? user : null;
	}

	public SBUser createUser(String email) {
		SBUser user = getOfy().query(SBUser.class).filter("email =", email).get();
		if (user == null) {
			user = new SBUser();
			user.email = email;
		
			if (email.contains("@")) {
				user.name = email.substring(0, email.indexOf("@"));
			} else {
				user.name = "<not set>";
			}
			user.modified = user.lastLoggedIn = user.joined = new Date();
			user.status = SBUser.Status.ACTIVE;
			
			getOfy().put(user);
		} else {
			log.warning("User with email '" + email + "' already exists!");
			return null;
		}
		return user;
	}
	
	public SBUser createUser(String email, String password, String authCookie, String name, String location, boolean investor) {
		SBUser user = getOfy().query(SBUser.class).filter("email =", email).get();
		if (user == null) {
			user = new SBUser();
			user.email = email;
			user.name = name;
			user.password = password;
			user.authCookie = authCookie;
			user.location = location;
			user.investor = investor;
			user.status = SBUser.Status.CREATED;
			user.joined = new Date();
			user.activationCode = "" + email.hashCode() + user.joined.hashCode();
			
			getOfy().put(user);
			return user;
		} else {
			log.warning("User with email '" + email + "' already exists!");
			return null;
		}
	}


	public UserStats updateUserStatistics(long userId) {
		SBUser user = null;
		try {
			user = getOfy().get(SBUser.class, userId);
		} catch (Exception e) {
			log.severe("User with id '" + userId + "' doesn't exist!");
			return null;
		}
		
		UserStats userStats = new UserStats();
		userStats.id = userId;
		userStats.user = new Key<SBUser>(SBUser.class, user.id);
		userStats.status = user.status;
		
		log.info("Updating user statistics, user: " + user.email);

		QueryResultIterable<Key<Bid>> bidsIt = getOfy().query(Bid.class)
				.filter("user =", userStats.user).filter("status =", Bid.Status.ACTIVE).fetchKeys();
		userStats.numberOfBids = CollectionUtils.size(bidsIt.iterator());
		
		// calculating rejected bids
		bidsIt = getOfy().query(Bid.class)
				.filter("user =", userStats.user).filter("status =", Bid.Status.REJECTED).fetchKeys();
		userStats.numberOfRejectedBids = CollectionUtils.size(bidsIt.iterator());
				
		// calculating accepted (and paid) bids for user's listings
		bidsIt = getOfy().query(Bid.class)
				.filter("listingOwner =", userStats.user).filter("status =", Bid.Status.ACCEPTED).fetchKeys();
		Collection<Bid> acceptedBids = getOfy().get(bidsIt).values();
		userStats.numberOfAcceptedBids = acceptedBids.size();		
		// calculating sum of accepted bids
		for(Bid bid : acceptedBids) {
			userStats.sumOfAcceptedBids += bid.valuation;
		}

		// calculating bids funded by user
		bidsIt = getOfy().query(Bid.class).filter("user =", userStats.user).fetchKeys();
		Collection<Bid> userBids = getOfy().get(bidsIt).values();
		for (Bid bid : userBids) {
			userStats.sumOfBids += bid.valuation;
			if (bid.status == Bid.Status.ACCEPTED) {
				userStats.numberOfFundedBids++;
				userStats.sumOfFundedBids += bid.valuation;
			}
		}
		
		QueryResultIterable<Key<Comment>> commentsIt = getOfy().query(Comment.class)
				.filter("user =", userStats.user).fetchKeys();
		userStats.numberOfComments = CollectionUtils.size(commentsIt.iterator());
		
		QueryResultIterable<Key<Listing>> listingsIt = getOfy().query(Listing.class)
				.filter("owner =", userStats.user).filter("state =", Listing.State.ACTIVE).fetchKeys();
		userStats.numberOfListings = CollectionUtils.size(listingsIt.iterator());
		
		QueryResultIterable<Key<Vote>> votesIt = getOfy().query(Vote.class)
				.filter("voter =", userStats.user).fetchKeys();
		userStats.numberOfVotesAdded = CollectionUtils.size(votesIt.iterator());
		votesIt = getOfy().query(Vote.class)
				.filter("user =", userStats.user).fetchKeys();
		userStats.numberOfVotes = CollectionUtils.size(votesIt.iterator());

		QueryResultIterable<Key<Notification>> notifsIt = getOfy().query(Notification.class)
				.filter("user =", userStats.user).filter("acknowledged !=", Boolean.FALSE).fetchKeys();
		userStats.numberOfNotifications = CollectionUtils.size(notifsIt.iterator());
		
		log.info("user: " + userId + ", statistics: " + userStats);

		getOfy().put(userStats);
		
		return userStats;
	}
	
	public UserStats getUserStatistics(long userId) {
//		try {
//			return getOfy().get(new Key<UserStats>(UserStats.class, userId));
//		} catch (Exception e) {
//			log.log(Level.WARNING, "User statistics entity '" + userId + "' not found");
//			return null;
//		}
		throw new java.lang.RuntimeException("User statistics are not implemented");
	}

	public ListingStats updateListingStatistics(long listingId) {
		Listing listing = new Listing();
		try {
			listing = getOfy().get(new Key<Listing>(Listing.class, listingId));
		} catch (Exception e) {
			log.severe("Listing with id '" + listingId + "' doesn't exist!");
			return null;
		}

		ListingStats listingStats = getOfy().find(new Key<ListingStats>(ListingStats.class, listingId));
		
		if (listingStats != null) {
			DateMidnight lastStatMidnight = new DateMidnight(listingStats.previousValuationDate.getTime());
			DateMidnight midnight = new DateMidnight();
			if (lastStatMidnight.isBefore(midnight)) {
				listingStats.previousValuation = listingStats.valuation;
				listingStats.previousValuationDate = listingStats.created;
			}
		} else {
			listingStats = new ListingStats();
			listingStats.id = listingId;
			listingStats.listing = new Key<Listing>(Listing.class, listingId);
			listingStats.previousValuation = listing.suggestedValuation;
			listingStats.previousValuationDate = listing.listedOn;
		}
		
		listingStats.state = listing.state;
		
		QueryResultIterable<Key<Bid>> bidsIt = getOfy().query(Bid.class)
				.filter("listing =", listingStats.listing).filter("status !=", Bid.Status.WITHDRAWN).fetchKeys();
		listingStats.numberOfBids = CollectionUtils.size(bidsIt.iterator());
		
		QueryResultIterable<Key<Comment>> commentsIt = getOfy().query(Comment.class)
				.filter("listing =", listingStats.listing).fetchKeys();
		listingStats.numberOfComments = CollectionUtils.size(commentsIt.iterator());

		QueryResultIterable<Key<Vote>> votesIt = getOfy().query(Vote.class)
				.filter("listing =", listingStats.listing).fetchKeys();
		listingStats.numberOfVotes = CollectionUtils.size(votesIt.iterator());

		// calculate valuation for listing (max accepted bid or suggested valuation)
		Bid mostValuedBid = null;		
		// calculate median for bids and set total number of bids
		List<Integer> values = new ArrayList<Integer>();
		for (Bid bid : getBidsForListing(listingId)) {
			if (mostValuedBid == null || mostValuedBid.valuation < bid.valuation) {
				mostValuedBid = bid;
			}
			values.add(bid.value);
		}
		if (mostValuedBid != null) {
			listingStats.valuation = mostValuedBid.valuation;
		} else {
			listingStats.valuation = listing.suggestedValuation;
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
		listingStats.medianValuation = median;
		
		double timeFactor = Math.pow((double)(Days.daysBetween(new DateTime(listing.listedOn), new DateTime()).getDays() + 2), 1.5d);
		double score = (listingStats.numberOfVotes + listingStats.numberOfComments + listingStats.numberOfVotes + median) / timeFactor;
		listingStats.score = score;
		
		listingStats.created = new Date();
		log.info("listing: " + listingId + ", statistics: " + listingStats);
		getOfy().put(listingStats);
		
		return listingStats;
	}
	
	public ListingStats getListingStatistics(long listingId) {
		try {
			return getOfy().get(new Key<ListingStats>(ListingStats.class, listingId));
		} catch (Exception e) {
			log.log(Level.WARNING, "Listing statistics entity '" + listingId + "'not found", e);
			return null;
		}
	}

	public SBUser updateUser(SBUser newUser) {
		try {
			SBUser user = getOfy().get(SBUser.class, newUser.id);
			user.name = newUser.name;
			user.nickname = newUser.nickname;
			user.location = newUser.location;
			user.phone = newUser.phone;
			user.investor = newUser.investor;
			user.notifyEnabled = newUser.notifyEnabled;
			user.password = newUser.password;
			user.authCookie = newUser.authCookie;
			
			getOfy().put(user);
			
			log.log(Level.INFO, "Updated user: " + user);
			return user;
		} catch (Exception e) {
			log.log(Level.WARNING, "User '" + newUser.id + "' not found", e);
			return null;
		}
	}

	public List<SBUser> getAllUsers() {
		QueryResultIterable<Key<SBUser>> usersIt = getOfy().query(SBUser.class)
				.order("email").fetchKeys();
		List<SBUser> users = new ArrayList<SBUser>(getOfy().get(usersIt).values());
		return users;
	}

	public SBUser getTopInvestor() {
		UserStats topUserStat = getOfy().query(UserStats.class)
				.filter("status", SBUser.Status.ACTIVE)
				.order("-sumOfBids").get();
		if (topUserStat != null) {
			return getOfy().get(topUserStat.user);
		} else {
			log.warning("User statistics not available, returning first user");
			return getOfy().query(SBUser.class).get();
		}
	}

	public List<Vote> getUserVotes(long userId) {
		QueryResultIterable<Key<Vote>> votesIt = getOfy().query(Vote.class)
				.filter("user =", new Key<SBUser>(SBUser.class, userId))
				.fetchKeys();
		List<Vote> votes = new ArrayList<Vote>(getOfy().get(votesIt).values());
		return votes;
	}

	public Listing createListing(Listing listing) {
		getOfy().put(listing);
		return listing;
	}

	public Listing updateListing(Listing newListing) {
		try {
			Listing listing = getOfy().get(new Key<Listing>(Listing.class, newListing.id));
			listing.name = newListing.name;
			listing.summary = newListing.summary;
			listing.suggestedAmount = newListing.suggestedAmount;
			listing.suggestedPercentage = newListing.suggestedPercentage;
			listing.businessPlanId = newListing.businessPlanId;
			listing.presentationId = newListing.presentationId;
			listing.financialsId = newListing.financialsId;
			listing.state = newListing.state;
			listing.closingOn = newListing.closingOn;
			listing.created = newListing.created;
			listing.posted = newListing.posted;

			getOfy().put(listing);
			return listing;
		} catch (Exception e) {
			log.log(Level.WARNING, "Listing entity name '" + newListing.name
					+ "', id '" + newListing.id + "' not found", e);
			return null;
		}
	}

	public Listing getListing(long listingId) {
		try {
			return getOfy().get(new Key<Listing>(Listing.class, listingId));
		} catch (Exception e) {
			log.log(Level.WARNING, "Listing entity '" + listingId + "'not found", e);
			return null;
		}
	}

	public List<Listing> getAllListings() {
		QueryResultIterable<Key<Listing>> listingsIt = getOfy().query(Listing.class)
				.order("-listedOn").prefetchSize(20).fetchKeys();
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingsIt).values());
		return listings;
	}

	public List<Listing> getUserActiveListings(long userId, ListPropertiesVO listingProperties) {
		QueryResultIterable<Key<Listing>> listingsIt = getOfy().query(Listing.class)
				.filter("owner =", new Key<SBUser>(SBUser.class, userId))
				.filter("state =", Listing.State.ACTIVE)
				.order("-listedOn").prefetchSize(listingProperties.getMaxResults()).fetchKeys();
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingsIt).values());
		listingProperties.setNumberOfResults(listings.size());
		return listings;
	}

	public List<Listing> getUserListings(long userId, ListPropertiesVO listingProperties) {
		QueryResultIterable<Key<Listing>> listingsIt = getOfy().query(Listing.class)
				.filter("owner =", new Key<SBUser>(SBUser.class, userId))
				.order("-listedOn").prefetchSize(listingProperties.getMaxResults()).fetchKeys();
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingsIt).values());
		listingProperties.setNumberOfResults(listings.size());
		return listings;
	}

	public List<Listing> getTopListings(ListPropertiesVO listingProperties) {
		QueryResultIterable<Key<ListingStats>> topListingsStat = getOfy().query(ListingStats.class)
				.filter("state", Listing.State.ACTIVE)
				.order("-score").prefetchSize(listingProperties.getMaxResults()).fetchKeys();
		
		List<Key<Listing>> listingKeys = new ArrayList<Key<Listing>>();
		for(ListingStats stat : getOfy().get(topListingsStat).values()) {
			listingKeys.add(stat.listing);
		}
		
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingKeys).values());
		listingProperties.setNumberOfResults(listings.size());
		return listings;
	}

	public List<Listing> getActiveListings(ListPropertiesVO listingProperties) {
		QueryResultIterable<Key<Listing>> listingsIt = getOfy().query(Listing.class)
				.filter("state =", Listing.State.ACTIVE)
				.order("-listedOn").prefetchSize(listingProperties.getMaxResults()).fetchKeys();
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingsIt).values());
		listingProperties.setNumberOfResults(listings.size());
		return listings;
	}

	public List<Listing> getMostValuedListings(ListPropertiesVO listingProperties) {
		QueryResultIterable<Key<ListingStats>> topListingsStat = getOfy().query(ListingStats.class)
				.filter("state =", Listing.State.ACTIVE)
				.order("-valuation").prefetchSize(listingProperties.getMaxResults()).fetchKeys();
		
		List<Key<Listing>> listingKeys = new ArrayList<Key<Listing>>();
		for(ListingStats stat : getOfy().get(topListingsStat).values()) {
			listingKeys.add(stat.listing);
		}
		
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingKeys).values());
		listingProperties.setNumberOfResults(listings.size());
		return listings;
	}

	public List<Listing> getMostDiscussedListings(ListPropertiesVO listingProperties) {
		QueryResultIterable<Key<ListingStats>> topListingsStat = getOfy().query(ListingStats.class)
				.filter("state =", Listing.State.ACTIVE)
				.order("-numberOfComments").prefetchSize(listingProperties.getMaxResults()).fetchKeys();
		
		List<Key<Listing>> listingKeys = new ArrayList<Key<Listing>>();
		for(ListingStats stat : getOfy().get(topListingsStat).values()) {
			listingKeys.add(stat.listing);
		}
		
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingKeys).values());
		listingProperties.setNumberOfResults(listings.size());
		return listings;
	}

	public List<Listing> getMostPopularListings(ListPropertiesVO listingProperties) {
		QueryResultIterable<Key<ListingStats>> topListingsStat = getOfy().query(ListingStats.class)
				.filter("state =", Listing.State.ACTIVE)
				.order("-numberOfVotes").prefetchSize(listingProperties.getMaxResults()).fetchKeys();
		
		List<Key<Listing>> listingKeys = new ArrayList<Key<Listing>>();
		for(ListingStats stat : getOfy().get(topListingsStat).values()) {
			listingKeys.add(stat.listing);
		}
		
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingKeys).values());
		listingProperties.setNumberOfResults(listings.size());
		return listings;
	}

	public List<Listing> getLatestListings(ListPropertiesVO listingProperties) {
		QueryResultIterable<Key<Listing>> listingsIt = getOfy().query(Listing.class)
				.filter("state =", Listing.State.ACTIVE)
				.order("-listedOn").prefetchSize(listingProperties.getMaxResults()).fetchKeys();
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingsIt).values());
		listingProperties.setNumberOfResults(listings.size());
		return listings;
	}

	public List<Listing> getClosingListings(ListPropertiesVO listingProperties) {
		QueryResultIterable<Key<Listing>> listingsIt = getOfy().query(Listing.class)
				.filter("state =", Listing.State.ACTIVE)
				.order("-closingOn").prefetchSize(listingProperties.getMaxResults()).fetchKeys();
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingsIt).values());
		listingProperties.setNumberOfResults(listings.size());
		return listings;
	}

	public Listing valueUpListing(long listingId, long voterId) {
		try {
			Listing listing = getOfy().get(Listing.class, listingId);
			
			if (listing.owner.getId() == voterId) {
				Vote vote = getOfy().query(Vote.class).filter("listing =", new Key<Listing>(Listing.class, listingId))
						.filter("voter =", new Key<SBUser>(SBUser.class, voterId)).get();
				if (vote == null) {
					vote = new Vote();
					vote.listing = new Key<Listing>(Listing.class, listingId);
					vote.user = null;
					vote.voter = new Key<SBUser>(SBUser.class, voterId);
					vote.value = 1;
					vote.commentedOn = new Date();
					
					getOfy().put(vote);
					log.info("User '" + voterId + "' voted for listing '" + listingId + "'");
					
					return listing;
				} else {
					log.log(Level.WARNING, "User '" + voterId + "' has already voted for listing '" + listingId + "'");
				}
			} else {
				log.log(Level.WARNING, "User '" + voterId + "' owns listing '" + listingId + "', cannot vote");
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Listing entity '" + listingId + "'not found", e);
		}
		return null;
	}

	public SBUser valueUpUser(long userId, long voterId) {
		try {
			SBUser user = getOfy().get(SBUser.class, userId);
			
			if (user.id.longValue() == voterId) {
				Vote vote = getOfy().query(Vote.class).filter("user =", new Key<SBUser>(SBUser.class, userId))
						.filter("voter =", new Key<SBUser>(SBUser.class, voterId)).get();
				if (vote == null) {
					vote = new Vote();
					vote.listing = null;
					vote.user = new Key<SBUser>(SBUser.class, userId);
					vote.voter = new Key<SBUser>(SBUser.class, voterId);
					vote.value = 1;
					vote.commentedOn = new Date();
					
					getOfy().put(vote);
					log.info("User '" + voterId + "' voted for user '" + userId + "'");
					
					return user;
				} else {
					log.log(Level.WARNING, "User '" + voterId + "' has already voted for user '" + userId + "'");
				}
			} else {
				log.log(Level.WARNING, "User '" + userId + "' cannot vote for himself/herself");
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "User entity '" + userId + "'not found", e);
		}
		return null;
	}
	
	public List<Comment> getCommentsForListing(long listingId) {
		QueryResultIterable<Key<Comment>> commentsIt = getOfy().query(Comment.class)
				.filter("listing =", new Key<Listing>(Listing.class, listingId))
				.order("-commentedOn").fetchKeys();
		List<Comment> comments = new ArrayList<Comment>(getOfy().get(commentsIt).values());
		return comments;
	}

	public List<Comment> getCommentsForUser(long userId) {
		QueryResultIterable<Key<Comment>> commentsIt = getOfy().query(Comment.class)
				.filter("user =", new Key<SBUser>(SBUser.class, userId))
				.order("-commentedOn").fetchKeys();
		List<Comment> comments = new ArrayList<Comment>(getOfy().get(commentsIt).values());
		return comments;
	}

	public List<Bid> getBidsForListing(long listingId) {
		QueryResultIterable<Key<Bid>> bidsIt = getOfy().query(Bid.class)
				.filter("listing =", new Key<Listing>(Listing.class, listingId))
				.order("-placed").fetchKeys();
		List<Bid> bids = new ArrayList<Bid>(getOfy().get(bidsIt).values());
		return bids;
	}

	public List<Bid> getBidsForUser(long userId) {
		QueryResultIterable<Key<Bid>> bidsIt = getOfy().query(Bid.class)
				.filter("user =", new Key<SBUser>(SBUser.class, userId))
				.order("-placed").fetchKeys();
		List<Bid> bids = new ArrayList<Bid>(getOfy().get(bidsIt).values());
		return bids;
	}
	
	public List<Bid> getBidsAcceptedByUser(long userId) {
		QueryResultIterable<Key<Bid>> bidsIt = getOfy().query(Bid.class)
				.filter("listingOwner =", new Key<SBUser>(SBUser.class, userId))
				.filter("status =", Bid.Status.ACCEPTED)
				.order("-placed").fetchKeys();
		List<Bid> bids = new ArrayList<Bid>(getOfy().get(bidsIt).values());
		return bids;
	}

	public List<Bid> getBidsFundedByUser(String userId) {
		QueryResultIterable<Key<Bid>> bidsIt = getOfy().query(Bid.class)
				.filter("user =", new Key<SBUser>(SBUser.class, userId))
				.filter("status =", Bid.Status.ACCEPTED)
				.order("-placed").fetchKeys();
		List<Bid> bids = new ArrayList<Bid>(getOfy().get(bidsIt).values());
		return bids;
	}

	public int getNumberOfVotesForListing(long listingId) {
		return getOfy().query(Vote.class)
				.filter("listing =", new Key<Listing>(Listing.class, listingId))
				.count();
	}

	public int getNumberOfVotesForUser(long userId) {
		return getOfy().query(Vote.class)
				.filter("user =", new Key<SBUser>(SBUser.class, userId))
				.count();
	}
	
	public int getActivity(long listingId) {
		return getOfy().query(Comment.class)
				.filter("listing =", new Key<Listing>(Listing.class, listingId))
				.count();
	}

	public Bid getBid(long bidId) {
		try {
			return getOfy().get(Bid.class, bidId);
		} catch (Exception e) {
			log.log(Level.WARNING, "Bid entity '" + bidId + "' not found", e);
			return null;
		}
	}

	public Comment getComment(String commentId) {
		try {
			return getOfy().get(Comment.class, commentId);
		} catch (Exception e) {
			log.log(Level.WARNING, "Comment entity '" + commentId + "' not found", e);
			return null;
		}
	}

	public boolean userCanVoteForListing(long voterId, long listingId) {
		Listing listing = null;
		try {
			listing = getOfy().get(Listing.class, listingId);
		} catch (Exception e) {
			log.log(Level.WARNING, "Listing entity '" + listingId + "' not found", e);
			return false;
		}
		boolean notOwnerOfListing = (voterId != listing.owner.getId());
		
		Vote vote = getOfy().query(Vote.class).filter("voter =", new Key<SBUser>(SBUser.class, voterId))
			.filter("listing =", new Key<Listing>(Listing.class, listingId)).get();
		boolean notVotedForListing = (vote == null);

		return notOwnerOfListing && notVotedForListing;
	}
	
	public boolean userCanVoteForUser(long voterId, long userId) {
		SBUser user = null;
		try {
			user = getOfy().get(SBUser.class, userId);
		} catch (Exception e) {
			log.log(Level.WARNING, "User entity '" + userId + "' not found", e);
			return false;
		}
		boolean notOwnerOfListing = (voterId != user.id);
		
		Vote vote = getOfy().query(Vote.class).filter("voter =", new Key<SBUser>(SBUser.class, voterId))
			.filter("user =", new Key<SBUser>(SBUser.class, userId)).get();
		boolean notVotedForListing = (vote == null);

		return notOwnerOfListing && notVotedForListing;
	}

	public SBUser activateUser(long userId, String activationCode) {
		try {
			SBUser user = getOfy().get(SBUser.class, userId);
			if (user.status == SBUser.Status.ACTIVE) {
				// for already activated users don't do anything
				return user;
			}
			if (StringUtils.areStringsEqual(user.activationCode, activationCode)) {
				user.status = SBUser.Status.ACTIVE;
				getOfy().put(user);
				return user;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "User with id '" + userId + "' not found!");
			return null;
		}
	}

	public SBUser deactivateUser(long userId) {
		try {
			SBUser user = getOfy().get(SBUser.class, userId);
			user.status = SBUser.Status.DEACTIVATED;
			getOfy().put(user);
			return user;
		} catch (Exception e) {
			log.log(Level.WARNING, "User with id '" + userId + "' not found!");
			return null;
		}
	}

	public boolean checkNickName(String nickName) {
		return getOfy().query(SBUser.class).filter("nickname =", nickName).count() == 0;
	}

	public void deleteComment(long commentId) {
		try {
			getOfy().delete(Comment.class, commentId);
		} catch (Exception e) {
			log.log(Level.WARNING, "Comment with id '" + commentId + "' not found!");
		}
	}

	public Comment createComment(Comment comment) {
		comment.commentedOn = new Date();
		getOfy().put(comment);
		return comment;
	}

	public Comment updateComment(Comment newComment) {
		try {
			Comment comment = getOfy().get(Comment.class, newComment.id);			
			comment.comment = newComment.comment;
			getOfy().put(comment);
			return comment;
		} catch (Exception e) {
			log.log(Level.WARNING, "Comment with id '" + newComment.id + "' not found!");
			return null;
		}
	}

	public Bid deleteBid(long loggedInUser, long bidId) {
		try {
			Bid bid = getOfy().get(Bid.class, bidId);
			if (loggedInUser != bid.bidder.getId()) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the bid " + bid);
				return null;
			}

			getOfy().delete(Bid.class, bidId);
			return bid;
		} catch (Exception e) {
			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
			return null;
		}
	}

	public Bid createBid(long loggedInUser, Bid bid) {
		bid.bidder = new Key<SBUser>(SBUser.class, loggedInUser);
		Listing listing = null;
		try {
			listing = getOfy().get(bid.listing);
		} catch (Exception e) {
			log.log(Level.WARNING, "Bidding for non existing listing with id '" + bid.listing + "'!");
			return null;
		}

		// checking if users has already have POSTED or ACTIVE bids
		QueryResultIterable<Key<Bid>> bidKeyIt = getOfy().query(Bid.class).filter("user =", bid.bidder)
				.filter("listing =", bid.listing).fetchKeys();
		Bid updatedBid = null;
		for (Bid bidForListing : getOfy().get(bidKeyIt).values()) {
			if (bidForListing.status == Bid.Status.POSTED
					|| bidForListing.status == Bid.Status.ACTIVE) {
				updatedBid = bidForListing;
				break;
			}
		}
		if (updatedBid != null) {
			log.info("Bid already exists: " + updatedBid);
			updatedBid.fundType = bid.fundType;
			updatedBid.percentOfCompany = bid.percentOfCompany;
			updatedBid.value = bid.value;
			updatedBid.valuation = bid.valuation;
			updatedBid.comment = bid.comment;
			log.info("Updating bid: " + updatedBid);
			getOfy().put(updatedBid);
			return updatedBid;
		} else {
			bid.listingOwner = listing.owner;
			bid.placed = new Date();
			bid.status = Bid.Status.POSTED;
			log.info("Creating new bid: " + bid);
			getOfy().put(bid);
			return bid;
		}
	}

	public Bid activateBid(long loggedInUser, long bidId) {
		try {
			Bid bid = getOfy().get(Bid.class, bidId);
			if (bid.status != Bid.Status.POSTED) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is trying to activate bid '" + bid + "' which is not POSTED!");
				return null;
			}
			if (loggedInUser != bid.listingOwner.getId()) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the listing " + bid.listing);
				return null;
			}
			
			bid.status = Bid.Status.ACTIVE;
			log.info("Activating bid: " + bid);
			getOfy().put(bid);
			return bid;
		} catch (Exception e) {
			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
			return null;
		}
	}

	public Bid rejectBid(long loggedInUser, long bidId) {
		try {
			Bid bid = getOfy().get(Bid.class, bidId);
			if (bid.status != Bid.Status.POSTED && bid.status != Bid.Status.ACTIVE) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is trying to reject bid '" + bid + "' which is not POSTED or ACTIVE!");
				return null;
			}
			if (loggedInUser != bid.listingOwner.getId()) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the listing " + bid.listing);
				return null;
			}
			
			bid.status = Bid.Status.REJECTED;
			log.info("Rejecting bid: " + bid);
			getOfy().put(bid);
			return bid;
		} catch (Exception e) {
			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
			return null;
		}
	}

	public Bid withdrawBid(long loggedInUser, long bidId) {
		try {
			Bid bid = getOfy().get(Bid.class, bidId);
			if (loggedInUser != bid.bidder.getId()) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the bid " + bid);
				return null;
			}
			
			bid.status = Bid.Status.WITHDRAWN;
			log.info("Withdrawing bid: " + bid);
			getOfy().put(bid);
			return bid;
		} catch (Exception e) {
			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
			return null;
		}
	}

	public Bid acceptBid(long loggedInUser, long bidId) {
		try {
			Bid bid = getOfy().get(Bid.class, bidId);
			if (Bid.Status.ACTIVE != bid.status) {
				log.log(Level.WARNING, "Bid is not active. " + bid);
				return null;
			}
			Listing listing = getOfy().get(bid.listing);
			if (loggedInUser != listing.owner.getId()) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the listing. " + listing + ", " + bid);
				return null;
			}
			if (Listing.State.ACTIVE != listing.state) {
				log.log(Level.WARNING, "Listing '" + bid.listing + "' is not active. " + listing + ", " + bid);
				return null;
			}
			SBUser bidder = getOfy().get(bid.bidder);
			if (SBUser.Status.ACTIVE != bidder.status) {
				log.log(Level.WARNING, "Bidder is not active. " + bidder + ", " + bid);
				return null;
			}
			
			bid.status = Bid.Status.ACCEPTED;
			log.info("Accepting bid: " + bid);
			getOfy().put(bid);
			return bid;
		} catch (Exception e) {
			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
			return null;
		}
	}

	public Bid markBidAsPaid(long loggedInUser, long bidId) {
		try {
			Bid bid = getOfy().get(Bid.class, bidId);
			if (Bid.Status.ACCEPTED != bid.status) {
				log.log(Level.WARNING, "Bid is not accepted. " + bid);
				return null;
			}
			Listing listing = getOfy().get(bid.listing);
			if (loggedInUser != listing.owner.getId()) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the listing. " + listing + ", " + bid);
				return null;
			}
			
			if (getOfy().query(PaidBid.class).filter("bid =", new Key<Bid>(Bid.class, bidId)).count() > 0) {
				log.log(Level.WARNING, "Bid with id '" + bidId + "' is already marked as paid!");
				return null;
			}

			PaidBid paidBid = new PaidBid(bid);
			getOfy().put(paidBid);
			log.info("Marked bid as active: " + paidBid);
			return paidBid;
		} catch (Exception e) {
			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
			return null;
		}
	}
	
	public List<Bid> getBidsByDate(ListPropertiesVO bidsProperties) {
		QueryResultIterable<Key<Bid>> bidsIt = getOfy().query(Bid.class)
				.filter("status =", Bid.Status.ACTIVE)
				.order("-placed").fetchKeys();
		List<Bid> bids = new ArrayList<Bid>(getOfy().get(bidsIt).values());
		return bids;
	}

	public SystemProperty getSystemProperty(String name) {
		return getOfy().find(SystemProperty.class, name);
	}

	public SystemProperty setSystemProperty(SystemProperty property) {
		SystemProperty existingProp = getOfy().find(SystemProperty.class, property.name);
		if (existingProp != null) {
			existingProp.value = property.value;
			getOfy().put(existingProp);
			return existingProp;
		} else {
			property.created = new Date();
			getOfy().put(property);
			return property;
		}
	}

	public List<SystemProperty> getSystemProperties() {
		QueryResultIterable<Key<SystemProperty>> propsIt = getOfy().query(SystemProperty.class)
				.order("+name").fetchKeys();
		List<SystemProperty> props = new ArrayList<SystemProperty>(getOfy().get(propsIt).values());
		return props;
	}

	public ListingDoc createListingDocument(ListingDoc doc) {
		doc.created = new Date();
		getOfy().put(doc);
		return doc;
	}

	public ListingDoc getListingDocument(long docId) {
		return getOfy().find(ListingDoc.class, docId);
	}

	public List<ListingDoc> getAllListingDocuments() {
		QueryResultIterable<Key<ListingDoc>> docsIt = getOfy().query(ListingDoc.class)
				.order("+name").fetchKeys();
		List<ListingDoc> docs = new ArrayList<ListingDoc>(getOfy().get(docsIt).values());
		return docs;
	}

	public void deleteDocument(long docId) {
		getOfy().delete(ListingDoc.class, docId);
	}

	public Notification createNotification(Notification notification) {
		notification.created = new Date();
		getOfy().put(notification);
		return notification;
	}

	public Notification acknowledgeNotification(long notificationId) {
		try {
			Notification notification = getOfy().get(Notification.class, notificationId);
			notification.acknowledged = true;
			getOfy().put(notification);
			return notification;
		} catch (Exception e) {
			log.log(Level.WARNING, "Notification with id '" + notificationId + "' not found!");
			return null;
		}
	}

	public List<Notification> getUserNotification(long userId, ListPropertiesVO notificationProperties) {
		QueryResultIterable<Key<Notification>> notIt = getOfy().query(Notification.class)
				.filter("user =", new Key<SBUser>(SBUser.class, userId))
				.filter("acknowledged =", Boolean.FALSE)
				.order("+created").fetchKeys();
		List<Notification> nots = new ArrayList<Notification>(getOfy().get(notIt).values());
		return nots;
	}

	public List<Notification> getAllUserNotification(long userId, ListPropertiesVO notificationProperties) {
		QueryResultIterable<Key<Notification>> notIt = getOfy().query(Notification.class)
				.filter("user =", new Key<SBUser>(SBUser.class, userId))
				.order("+created").fetchKeys();
		List<Notification> nots = new ArrayList<Notification>(getOfy().get(notIt).values());
		return nots;
	}

	public Notification getNotification(long notifId) {
		try {
			return getOfy().get(Notification.class, notifId);
		} catch (Exception e) {
			log.log(Level.WARNING, "Notification with id '" + notifId + "' not found!");
			return null;
		}
	}

	public Monitor setMonitor(Monitor monitor) {
		QueryResultIterable<Key<Monitor>> notIt = getOfy().query(Monitor.class)
				.filter("user =", monitor.user)
				.filter("object =", monitor.object)
				.filter("type =", monitor.type)
				.order("+created").fetchKeys();
		Monitor existingMonitor = getOfy().find(notIt.iterator().next());

		if (existingMonitor != null) {
			// monitor already exists
			existingMonitor.active = true;
			existingMonitor.deactivated = null;
			
			getOfy().put(existingMonitor);
			return existingMonitor;
		} else {
			// we need to create new monitor
			monitor.created = new Date();
			monitor.deactivated = null;
			monitor.active = true;

			getOfy().put(monitor);
			return monitor;
		}
	}

	public Monitor deactivateMonitor(long monitorId) {
		try {
			Monitor monitor = getOfy().get(Monitor.class, monitorId);
			monitor.active = false;
			monitor.deactivated = new Date();
			getOfy().put(monitor);
			return monitor;
		} catch (Exception e) {
			log.log(Level.WARNING, "Notification with id '" + monitorId + "' not found!");
			return null;
		}
	}

	public List<Monitor> getMonitorsForObject(long objectId, Monitor.Type type) {
		Key<Monitor.Monitored> objectKey = null;
		switch(type) {
		case BID:
			objectKey = new Key<Monitor.Monitored>(Bid.class, objectId);
			break;
		case LISTING:
			objectKey = new Key<Monitor.Monitored>(Listing.class, objectId);
			break;
		case USER:
			objectKey = new Key<Monitor.Monitored>(SBUser.class, objectId);
			break;
		}
		QueryResultIterable<Key<Monitor>> monIt = getOfy().query(Monitor.class)
				.filter("object =", objectKey)
				.filter("type =", type)
				.order("+created").fetchKeys();
		List<Monitor> mons = new ArrayList<Monitor>(getOfy().get(monIt).values());
		return mons;
	}

	public List<Monitor> getMonitorsForUser(long userId, Monitor.Type type) {
		QueryResultIterable<Key<Monitor>> monIt = getOfy().query(Monitor.class)
				.filter("user =", new Key<SBUser>(SBUser.class, userId))
				.filter("type =", type)
				.order("+created").fetchKeys();
		List<Monitor> mons = new ArrayList<Monitor>(getOfy().get(monIt).values());
		return mons;
	}

}
