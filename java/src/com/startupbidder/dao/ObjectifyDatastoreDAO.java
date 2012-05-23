package com.startupbidder.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.NotificationVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.Category;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.ListingLocation;
import com.startupbidder.datamodel.ListingStats;
import com.startupbidder.datamodel.Location;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.PaidBid;
import com.startupbidder.datamodel.QuestionAnswer;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.SystemProperty;
import com.startupbidder.datamodel.UserStats;
import com.startupbidder.datamodel.Vote;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.ListingFacade;

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
	
	private Objectify getOfy() {
		Objectify ofy = ObjectifyService.begin();
		return ofy;
	}
	
	public static class CursorHandler<T> {
		public List<Key<T>> handleQuery(ListPropertiesVO listProperties, Query<T> query) {
			if (StringUtils.isNotEmpty(listProperties.getNextCursor())) {
				log.info("Starting query from cursor: " + listProperties.getNextCursor());
				query.startCursor(Cursor.fromWebSafeString(listProperties.getNextCursor()));
				listProperties.setPrevCursor(listProperties.getNextCursor());
			}
			QueryResultIterator<Key<T>> qands = query.fetchKeys().iterator();
			
			List<Key<T>> keyList = new ArrayList<Key<T>>();
			while (qands.hasNext()) {
				keyList.add(qands.next());
				if (keyList.size() >= listProperties.getMaxResults()) {
					if (qands.hasNext()) {
						listProperties.setNextCursor(qands.getCursor().toWebSafeString());
						listProperties.setNumberOfResults(keyList.size());
						listProperties.updateMoreResultsUrl();
					}
					break;
				}
			}
			listProperties.setNumberOfResults(keyList.size());
			return keyList;
		}
	}

	public SBUser getUser(String userId) {
		try {
			return (SBUser)getOfy().get(Key.create(userId));
		} catch (Exception e) {
			log.log(Level.WARNING, "User '" + userId + "' not found", e);
			return null;
		}
	}

    public SBUser getUserByEmail(String email) {
        SBUser user = getOfy().query(SBUser.class).filter("email =", email).get();
        log.info("Existing user for email " + email + " is " + user);
        return user;
    }

    public SBUser getUserByNickname(String nickname) {
        String nicknameLower = nickname.toLowerCase();
        SBUser user = getOfy().query(SBUser.class).filter("nicknameLower =", nicknameLower).get();
        log.info("Existing user for nickname " + nickname + " is " + user);
        return user;
    }

	public SBUser getUserByAuthCookie(String authCookie) {
		SBUser user = getOfy().query(SBUser.class).filter("authCookie =", authCookie).get();
		log.info("Existing user for cookie " + authCookie + " is " + user);
		return user != null && user.status == SBUser.Status.ACTIVE ? user : null;
	}

	public SBUser createUser(String email, String nickname) {
        String userEmail = StringUtils.isNotEmpty(email) ? email : "anonymous" + String.valueOf(RandomUtils.nextInt(1000000000)) + "@startupbidder.com";
        SBUser user = getUserByEmail(userEmail);
        if (user != null) {
            log.warning("User with email '" + userEmail + "' already exists, cannot create user.");
            return null;
        }
        String userNickname = (StringUtils.isNotEmpty(nickname) && !nickname.equalsIgnoreCase(email))
                    ? nickname
                    : (userEmail.contains("@") ? email.substring(0, email.indexOf("@")) : "anonymous" + String.valueOf(RandomUtils.nextInt(1000000000)));
        user = getUserByNickname(userNickname);
        if (user != null) {
            log.warning("User with nickname " + userNickname + " already exists, cannot create user.");
            return null;
        }
    	user = new SBUser();
        user.email = userEmail;
        user.nickname = userNickname;
        user.nicknameLower = userNickname.toLowerCase();
	    user.modified = user.lastLoggedIn = user.joined = new Date();
		user.status = SBUser.Status.ACTIVE;
		getOfy().put(user);
        log.info("Created user with nickname " + user.nickname + " as " + user);
		return user;
	}
	
	public SBUser createUser(String email, String password, String authCookie, String name, String location, boolean investor) {
        String userEmail = StringUtils.isNotEmpty(email) ? email : "anonymous" + String.valueOf(RandomUtils.nextInt(1000000000)) + "@startupbidder.com";
        SBUser user = getUserByEmail(email);
        if (user != null) {
            log.warning("User with email '" + userEmail + "' already exists, cannot create user.");
            return null;
        }
        String userNickname = userEmail.contains("@") ? email.substring(0, email.indexOf("@")) : "anonymous" + String.valueOf(RandomUtils.nextInt(1000000000));
        user = getUserByNickname(userNickname);
        if (user != null) {
            log.warning("User with nickname matching '" + userNickname + "' case insensitive already exists, cannot create user.");
            return null;
        }
        user = new SBUser();
        user.email = userEmail;
        user.nickname = userNickname;
        user.nicknameLower = userNickname.toLowerCase();
		user.name = name;
		user.password = password;
		user.authCookie = authCookie;
		user.location = location;
		user.investor = investor;
        user.modified = user.lastLoggedIn = user.joined = new Date();
		user.status = SBUser.Status.CREATED;
		user.joined = new Date();
		user.activationCode = "" + email.hashCode() + user.joined.hashCode();
		getOfy().put(user);
        log.info("Created user with defaulted nickname " + user.nickname + " as " + user);
		return user;
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
/*
		QueryResultIterable<Key<Bid>> bidsIt = getOfy().query(Bid.class)
				.filter("user =", userStats.user).filter("status =", Bid.Action.ACTIVATE).fetchKeys();
		userStats.numberOfBids = CollectionUtils.size(bidsIt.iterator());
		
		// calculating rejected bids
//		bidsIt = getOfy().query(Bid.class)
//				.filter("user =", userStats.user).filter("status =", Bid.Action.REJECTED).fetchKeys();
//		userStats.numberOfRejectedBids = CollectionUtils.size(bidsIt.iterator());
				
		// calculating accepted (and paid) bids for user's listings
		bidsIt = getOfy().query(Bid.class)
				.filter("listingOwner =", userStats.user).filter("status =", Bid.Action.ACCEPT).fetchKeys();
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
			if (bid.action == Bid.Action.ACCEPT) {
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
*/
		QueryResultIterable<Key<Notification>> notifsIt = getOfy().query(Notification.class)
				.filter("user =", userStats.user)
                .filter("read =", Boolean.FALSE)
                .fetchKeys();

        userStats.numberOfNotifications = CollectionUtils.size(notifsIt.iterator());
		
		log.info("user: " + userId + ", statistics: " + userStats);

		getOfy().put(userStats);
		
		return userStats;
	}
	
	public UserStats getUserStatistics(long userId) {
		try {
			return getOfy().get(new Key<UserStats>(UserStats.class, userId));
		} catch (Exception e) {
			log.log(Level.WARNING, "User statistics entity '" + userId + "' not found");
			return null;
		}
		//throw new java.lang.RuntimeException("User statistics are not implemented");
	}

	public ListingStats updateListingStatistics(long listingId) {
		Listing listing = null;
		try {
			listing = getOfy().find(new Key<Listing>(Listing.class, listingId));
		} catch (Exception e) {
			log.log(Level.WARNING, "Wrong listing key", e);
		}
		if (listing == null) {
			log.severe("Listing with id '" + listingId + "' doesn't exist!");
			return null;
		}

		ListingStats listingStats = getOfy().find(new Key<ListingStats>(ListingStats.class, listingId));
		if (listingStats != null) {
			DateMidnight lastStatMidnight = new DateMidnight(
					listingStats.previousValuationDate != null ? listingStats.previousValuationDate.getTime() : 0);
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
				.filter("listing =", listingStats.listing).filter("status !=", Bid.Action.CANCEL).fetchKeys();
		listingStats.numberOfBids = CollectionUtils.size(bidsIt.iterator());
		
		QueryResultIterable<Key<Comment>> commentsIt = getOfy().query(Comment.class)
				.filter("listing =", listingStats.listing).fetchKeys();
		listingStats.numberOfComments = CollectionUtils.size(commentsIt.iterator());

		QueryResultIterable<Key<Monitor>> monitorIt = getOfy().query(Monitor.class)
				.filter("monitoredListing =", listingStats.listing)
				.filter("active =", Boolean.TRUE).fetchKeys();
		listingStats.numberOfMonitors = CollectionUtils.size(monitorIt.iterator());

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

        int numQuestions = 0;
        int numMessages = 0;
        ListPropertiesVO props = new ListPropertiesVO();
        props.setMaxResults(1000);
        List<NotificationVO> notifications = DtoToVoConverter.convertNotifications(getPublicListingNotifications(listingId, props));
        for (NotificationVO notification : notifications) {
            Notification.Type type = Notification.Type.valueOf(notification.getType());
            if (type == Notification.Type.ASK_LISTING_OWNER) {
                numQuestions++;
            }
            else if (type == Notification.Type.PRIVATE_MESSAGE) {
                numMessages++;
            }
        }
        listingStats.numberOfQuestions = numQuestions;
        listingStats.numberOfMessages = numMessages;
        
		double timeFactor = Math.pow((double)(Days.daysBetween(new DateTime(listing.listedOn), new DateTime()).getDays() + 2), 1.5d);
		double score = (listingStats.numberOfMonitors + listingStats.numberOfComments + 10*listingStats.numberOfBids + listingStats.numberOfQuestions + listingStats.numberOfMessages + (median/1000)) / timeFactor;
		listingStats.score = score;
		
		listingStats.created = new Date();
		log.info("listing: " + listingId + ", statistics: " + listingStats);
		getOfy().put(listingStats);

		return listingStats;
	}
	
	public ListingStats getListingStatistics(long listingId) {
		return getOfy().find(new Key<ListingStats>(ListingStats.class, listingId));
	}
	
	public ListingStats storeListingStatistics(ListingStats stats) {
		getOfy().put(stats);
		return stats;
	}

	public SBUser updateUser(SBUser newUser) {
		try {
			SBUser user = getOfy().get(SBUser.class, newUser.id);
			user.name = newUser.name;
			user.nickname = newUser.nickname;
            user.nicknameLower = newUser.nickname.toLowerCase();
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
/*
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
*/
	public Listing createListing(Listing listing) {
		SBUser owner = getOfy().get(listing.owner);
		if (owner.editedListing == null) {
			getOfy().put(listing);
			owner.editedListing = new Key<Listing>(Listing.class, listing.id);
			getOfy().put(owner);
			return listing;
		} else {
			return getOfy().get(owner.editedListing);
		}
	}

	public Listing updateListingStateAndDates(Listing newListing) {
		try {
			Listing listing = getOfy().get(new Key<Listing>(Listing.class, newListing.id));
			Listing.State oldState = listing.state;
			listing.state = newListing.state;
			listing.closingOn = newListing.closingOn;
			listing.listedOn = newListing.listedOn;
			listing.created = newListing.created;
			listing.posted = newListing.posted;
			getOfy().put(listing);
			
			// listing activation should allow for editing new listing by the owner
			if (oldState == Listing.State.POSTED && newListing.state == Listing.State.ACTIVE) {
				SBUser owner = getOfy().get(listing.owner);
				owner.editedListing = null;
				getOfy().put(owner);
			}
			
			updateListingLocationsAndCategories(listing, oldState);
			log.info("Updated listing: " + listing);
			return listing;
		} catch (Exception e) {
			log.log(Level.WARNING, "Listing entity name '" + newListing.name
					+ "', id '" + newListing.id + "' not found", e);
			return null;
		}
	}

	private void updateListingLocationsAndCategories(Listing listing, Listing.State oldState) {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		// all listing locations cache is also modified in method ListingFacade.getAllListingLocations

		// listing activation should allow for editing new listing by the owner
		if (oldState == Listing.State.POSTED && listing.state == Listing.State.ACTIVE) {
			// storing location data
			ListingLocation loc = new ListingLocation(listing);
			getOfy().put(loc);
			List<Object[]> result = (List<Object[]>)mem.get(ListingFacade.MEMCACHE_ALL_LISTING_LOCATIONS);
			if (result != null) {
				result.add(new Object[]{loc.getWebKey(), loc.latitude, loc.longitude});
			}
			// updating category
			for (Category cat : getCategories()) {
				if (cat.name.equals(listing.category)) {
					cat.count++;
					getOfy().put(cat);
				}
			}
		}
		if (listing.state == Listing.State.CLOSED || listing.state == Listing.State.WITHDRAWN) {
			getOfy().delete(new ListingLocation(listing));
			List<Object[]> result = (List<Object[]>)mem.get(ListingFacade.MEMCACHE_ALL_LISTING_LOCATIONS);
			Object[] array = null;
			for (int i = 0; i < result.size(); i++) {
				array = result.get(i);
				if (listing.getWebKey().equals(array[0])) {
					result.remove(i);
					break;
				}
			}
			// updating category
			for (Category cat : getCategories()) {
				if (cat.name.equals(listing.category)) {
					cat.count--;
					getOfy().put(cat);
				}
			}
		}
	}
	
	public Listing storeListing(Listing newListing) {
		log.info("Storing " + newListing);
		getOfy().put(newListing);
		return newListing;
	}

	public Listing getListing(long listingId) {
		try {
			return getOfy().get(new Key<Listing>(Listing.class, listingId));
		} catch (Exception e) {
			log.log(Level.WARNING, "Listing entity '" + listingId + "' not found", e);
			return null;
		}
	}

	public List<Listing> getListings(List<Long> listingIds) {
		List<Key<Listing>> keys = new ArrayList<Key<Listing>>();
		for (Long id : listingIds) {
			keys.add(new Key<Listing>(Listing.class, id));
		}
		
		return getListingsByKeys(keys);
	}

	public List<Listing> getListingsByKeys(List<Key<Listing>> listingKeys) {
		try {
			return new ArrayList<Listing>(getOfy().get(listingKeys).values());
		} catch (Exception e) {
			log.log(Level.WARNING, "Error fetching list of listings by keys '" + listingKeys + "'.", e);
			return null;
		}
	}

	public Listing deleteEditedListing(long listingId) {
		Listing listing = getOfy().get(new Key<Listing>(Listing.class, listingId));
		if (listing != null && (listing.state == Listing.State.NEW || listing.state == Listing.State.POSTED)) {
			getOfy().delete(listing);
			SBUser user = getOfy().get(listing.owner);
			user.editedListing = null;
			getOfy().put(user);
		}
		return listing;
	}

    public List<Listing> getAllListings() {
        int maxResults = 20;
        QueryResultIterable<Key<Listing>> listingsIt = getOfy().query(Listing.class)
                .order("-listedOn")
                .limit(maxResults)
                .prefetchSize(maxResults)
                .chunkSize(maxResults)
                .fetchKeys();
        List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingsIt).values());
        return listings;
    }

    public List<Listing> getAllListingsInternal() { // use with care
        QueryResultIterable<Key<Listing>> listingsIt = getOfy().query(Listing.class)
                .order("-listedOn")
                .fetchKeys();
        List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingsIt).values());
        return listings;
    }

	public List<Listing> getUserNewOrPostedListings(long userId) {
		QueryResultIterable<Key<Listing>> listingsIt = getOfy().query(Listing.class)
				.filter("owner =", new Key<SBUser>(SBUser.class, userId))
				.filter("state =", Listing.State.NEW)
				.order("-listedOn").fetchKeys();
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingsIt).values());

		listingsIt = getOfy().query(Listing.class)
				.filter("owner =", new Key<SBUser>(SBUser.class, userId))
				.filter("state =", Listing.State.POSTED)
				.order("-listedOn").fetchKeys();
		listings.addAll(new ArrayList<Listing>(getOfy().get(listingsIt).values()));

		return listings;
	}

	public List<Long> getListingsIdsForCategory(String category, ListPropertiesVO listingProperties) {
		QueryResultIterable<Key<Listing>> listingsIt = getOfy().query(Listing.class)
				.filter("category =", category)
				.order("-listedOn")
                .limit(listingProperties.getMaxResults() * 2)
                .prefetchSize(listingProperties.getMaxResults() * 2)
                .chunkSize(listingProperties.getMaxResults() * 2)
                .fetchKeys();
		List<Long> listingsIds = new ArrayList<Long>();
		for (Key<Listing> key : listingsIt) {
			listingsIds.add(key.getId());
		}
		return listingsIds;
	}

	public List<Long> getListingsIdsForLocation(String country, String state, String city, ListPropertiesVO listingProperties) {
		Query<Listing> query = getOfy().query(Listing.class);
		if (country != null) {
			query = query.filter("country =", country);
		}
		if (state != null) {
			query = query.filter("usState =", state);
		}
		if (city != null) {
			query = query.filter("city =", city);
		}
		QueryResultIterable<Key<Listing>> listingsIt = query.order("-listedOn")
                .limit(listingProperties.getMaxResults() * 2)
                .prefetchSize(listingProperties.getMaxResults() * 2)
                .chunkSize(listingProperties.getMaxResults() * 2)
				.fetchKeys();		
		List<Long> listingsIds = new ArrayList<Long>();
		for (Key<Listing> key : listingsIt) {
			listingsIds.add(key.getId());
		}
		return listingsIds;
	}

	public List<Listing> getUserActiveListings(long userId, ListPropertiesVO listingProperties) {
		Query<Listing> query = getOfy().query(Listing.class)
				.filter("owner =", new Key<SBUser>(SBUser.class, userId))
				.filter("state =", Listing.State.ACTIVE)
				.order("-listedOn")
                .chunkSize(listingProperties.getMaxResults())
				.prefetchSize(listingProperties.getMaxResults());
		List<Key<Listing>> keyList = new CursorHandler<Listing>().handleQuery(listingProperties, query);
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(keyList).values());
		return listings;
	}

	public List<Listing> getUserListings(long userId, Listing.State listingState, ListPropertiesVO listingProperties) {
		Query<Listing> query = getOfy().query(Listing.class)
				.filter("owner =", new Key<SBUser>(SBUser.class, userId));
		if (listingState != null) {
			query.filter("state", listingState);
		}
        query.order("-listedOn")
       			.chunkSize(listingProperties.getMaxResults())
       			.prefetchSize(listingProperties.getMaxResults());
		List<Key<Listing>> keyList = new CursorHandler<Listing>().handleQuery(listingProperties, query);
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(keyList).values());
		return listings;
	}

	public List<Listing> getTopListings(ListPropertiesVO listingProperties) {
		Query<ListingStats> query = getOfy().query(ListingStats.class)
				.filter("state", Listing.State.ACTIVE)
				.order("-score")
                .chunkSize(listingProperties.getMaxResults())
                .prefetchSize(listingProperties.getMaxResults());
		
		List<Key<ListingStats>> keyList = new CursorHandler<ListingStats>().handleQuery(listingProperties, query);
		List<Key<Listing>> listingKeys = new ArrayList<Key<Listing>>();
		for(ListingStats stat : getOfy().get(keyList).values()) {
			listingKeys.add(stat.listing);
		}
		
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingKeys).values());
		return listings;
	}

	public List<Listing> getPostedListings(ListPropertiesVO listingProperties) {
		Query<Listing> query = getOfy().query(Listing.class)
				.filter("state =", Listing.State.POSTED)
                .order("-posted")
                .chunkSize(listingProperties.getMaxResults())
                .prefetchSize(listingProperties.getMaxResults());
		List<Key<Listing>> keyList = new CursorHandler<Listing>().handleQuery(listingProperties, query);
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(keyList).values());
		return listings;
	}

	public List<Listing> getActiveListings(ListPropertiesVO listingProperties) {
		Query<Listing> query = getOfy().query(Listing.class)
				.filter("state =", Listing.State.ACTIVE)
				.order("-listedOn")
                .chunkSize(listingProperties.getMaxResults())
                .prefetchSize(listingProperties.getMaxResults());
		List<Key<Listing>> keyList = new CursorHandler<Listing>().handleQuery(listingProperties, query);
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(keyList).values());
		return listings;
	}

	public List<Listing> getFrozenListings(ListPropertiesVO listingProperties) {
		Query<Listing> query = getOfy().query(Listing.class)
				.filter("state =", Listing.State.FROZEN)
				.order("-listedOn")
                .chunkSize(listingProperties.getMaxResults())
                .prefetchSize(listingProperties.getMaxResults());
		List<Key<Listing>> keyList = new CursorHandler<Listing>().handleQuery(listingProperties, query);
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(keyList).values());
		return listings;
	}

	public List<Listing> getMostValuedListings(ListPropertiesVO listingProperties) {
		Query<ListingStats> query = getOfy().query(ListingStats.class)
				.filter("state =", Listing.State.ACTIVE)
				.order("-valuation")
                .chunkSize(listingProperties.getMaxResults())
                .prefetchSize(listingProperties.getMaxResults());

		List<Key<ListingStats>> keyList = new CursorHandler<ListingStats>().handleQuery(listingProperties, query);
		List<Key<Listing>> listingKeys = new ArrayList<Key<Listing>>();
		for(ListingStats stat : getOfy().get(keyList).values()) {
			listingKeys.add(stat.listing);
		}
		
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingKeys).values());
		return listings;
	}

	public List<Listing> getMostDiscussedListings(ListPropertiesVO listingProperties) {
		Query<ListingStats> query = getOfy().query(ListingStats.class)
				.filter("state =", Listing.State.ACTIVE)
				.order("-numberOfComments")
                .chunkSize(listingProperties.getMaxResults())
                .prefetchSize(listingProperties.getMaxResults());
		
		List<Key<ListingStats>> keyList = new CursorHandler<ListingStats>().handleQuery(listingProperties, query);
		List<Key<Listing>> listingKeys = new ArrayList<Key<Listing>>();
		for(ListingStats stat : getOfy().get(keyList).values()) {
			listingKeys.add(stat.listing);
		}
		
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingKeys).values());
		return listings;
	}

	public List<Listing> getMostPopularListings(ListPropertiesVO listingProperties) {
		Query<ListingStats> query = getOfy().query(ListingStats.class)
				.filter("state =", Listing.State.ACTIVE)
				.order("-numberOfVotes")
                .chunkSize(listingProperties.getMaxResults())
                .prefetchSize(listingProperties.getMaxResults());
		
		List<Key<ListingStats>> keyList = new CursorHandler<ListingStats>().handleQuery(listingProperties, query);
		List<Key<Listing>> listingKeys = new ArrayList<Key<Listing>>();
		for(ListingStats stat : getOfy().get(keyList).values()) {
			listingKeys.add(stat.listing);
		}
		
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(listingKeys).values());
		return listings;
	}

	public List<Listing> getLatestListings(ListPropertiesVO listingProperties) {
		Query<Listing> query = getOfy().query(Listing.class)
				.filter("state =", Listing.State.ACTIVE)
				.order("-listedOn")
                .chunkSize(listingProperties.getMaxResults())
                .prefetchSize(listingProperties.getMaxResults());
		List<Key<Listing>> keyList = new CursorHandler<Listing>().handleQuery(listingProperties, query);
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(keyList).values());
		return listings;
	}

	public List<Listing> getClosingListings(ListPropertiesVO listingProperties) {
		Query<Listing> query = getOfy().query(Listing.class)
				.filter("state =", Listing.State.ACTIVE)
				.order("closingOn")
                .chunkSize(listingProperties.getMaxResults())
                .prefetchSize(listingProperties.getMaxResults());
		List<Key<Listing>> keyList = new CursorHandler<Listing>().handleQuery(listingProperties, query);
		List<Listing> listings = new ArrayList<Listing>(getOfy().get(keyList).values());
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
/*
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
*/
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
				.filter("bidder =", new Key<SBUser>(SBUser.class, userId))
				.order("-placed").fetchKeys();
		List<Bid> bids = new ArrayList<Bid>(getOfy().get(bidsIt).values());
		return bids;
	}
	
	public List<Bid> getBidsAcceptedByUser(long userId) {
		QueryResultIterable<Key<Bid>> bidsIt = getOfy().query(Bid.class)
				.filter("listingOwner =", new Key<SBUser>(SBUser.class, userId))
				.filter("status =", Bid.Action.ACCEPT)
				.order("-placed").fetchKeys();
		List<Bid> bids = new ArrayList<Bid>(getOfy().get(bidsIt).values());
		return bids;
	}

	public List<Bid> getBidsFundedByUser(String userId) {
		QueryResultIterable<Key<Bid>> bidsIt = getOfy().query(Bid.class)
				.filter("bidder =", new Key<SBUser>(SBUser.class, userId))
				.filter("status =", Bid.Action.ACCEPT)
				.order("-placed").fetchKeys();
		List<Bid> bids = new ArrayList<Bid>(getOfy().get(bidsIt).values());
		return bids;
	}

	public int getNumberOfVotesForListing(long listingId) {
		return getOfy().query(Vote.class)
				.filter("listing =", new Key<Listing>(Listing.class, listingId))
				.count();
	}
/*
	public int getNumberOfVotesForUser(long userId) {
		return getOfy().query(Vote.class)
				.filter("bidder =", new Key<SBUser>(SBUser.class, userId))
				.count();
	}
*/
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

	public Comment getComment(long commentId) {
		return getOfy().find(Comment.class, commentId);
	}
/*
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
*/
	public SBUser activateUser(long userId, String activationCode) {
		try {
			SBUser user = getOfy().get(SBUser.class, userId);
			if (user.status == SBUser.Status.ACTIVE) {
				// for already activated users don't do anything
				return user;
			}
			if (StringUtils.equals(user.activationCode, activationCode)) {
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

	public boolean checkNickNameInUse(String nickName) {
		return getOfy().query(SBUser.class).filter("nicknameLower =", nickName.toLowerCase()).count() != 0;
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

	/**
	 * Returns all bids for listing.
	 */
	public Map<Key<SBUser>, List<Bid>> getAllBids(Key<Listing> listing) {
		Map<Key<SBUser>, List<Bid>> result = new HashMap<Key<SBUser>, List<Bid>>();
		QueryResultIterable<Key<Bid>> bidKeys = getOfy().query(Bid.class).filter("listing =", listing).fetchKeys();
		Collection<Bid> bids = getOfy().get(bidKeys).values();
		
		for (Bid bid : bids) {
			if (result.containsKey(bid.bidder)) {
				result.get(bid.bidder).add(bid);
			} else {
				List<Bid> bidList = new ArrayList<Bid>();
				bidList.add(bid);
				result.put(bid.bidder, bidList);
			}
		}
		return result;
	}

	public Bid makeBid(long loggedInUser, Bid bid) {
		getOfy().put(bid);
		return bid;
	}

	public Bid counterOfferedByOwner(long loggedInUser, Bid newBid) {
		try {
			Bid bid = getOfy().get(Bid.class, newBid.id);
			if (loggedInUser != bid.listingOwner.getId()) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the listing " + bid.listing);
				return null;
			}
			
			bid.action = Bid.Action.ACTIVATE;
			log.info("Activating bid: " + bid);
			getOfy().put(bid);
			return bid;
		} catch (Exception e) {
			log.log(Level.WARNING, "Bid with id '" + newBid.id + "' not found!");
			return null;
		}
	}

	public Bid counterOfferedByInvestor(long loggedInUser, Bid newBid) {
		try {
			Bid bid = getOfy().get(Bid.class, newBid.id);
			if (loggedInUser != bid.listingOwner.getId()) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the listing " + bid.listing);
				return null;
			}
			
			bid.action = Bid.Action.ACTIVATE;
			log.info("Activating bid: " + bid);
			getOfy().put(bid);
			return bid;
		} catch (Exception e) {
			log.log(Level.WARNING, "Bid with id '" + newBid.id + "' not found!");
			return null;
		}
	}

//	public Bid rejectBid(long loggedInUser, long bidId) {
//		try {
//			Bid bid = getOfy().get(Bid.class, bidId);
//			if (bid.status != Bid.Action.ACTIVE) {
//				log.log(Level.SEVERE, "User '" + loggedInUser + "' is trying to reject bid '" + bid + "' which is not POSTED or ACTIVE!");
//				return null;
//			}
//			if (loggedInUser != bid.listingOwner.getId()) {
//				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the listing " + bid.listing);
//				return null;
//			}
//			
//			bid.status = Bid.Action.REJECTED;
//			log.info("Rejecting bid: " + bid);
//			getOfy().put(bid);
//			return bid;
//		} catch (Exception e) {
//			log.log(Level.WARNING, "Bid with id '" + bidId + "' not found!");
//			return null;
//		}
//	}

	public Bid withdrawBid(long loggedInUser, long bidId) {
		try {
			Bid bid = getOfy().get(Bid.class, bidId);
			if (loggedInUser != bid.bidder.getId()) {
				log.log(Level.SEVERE, "User '" + loggedInUser + "' is not the owner of the bid " + bid);
				return null;
			}
			
			bid.action = Bid.Action.CANCEL;
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
			if (Bid.Action.ACTIVATE != bid.action) {
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
			
			bid.action = Bid.Action.ACCEPT;
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
			if (Bid.Action.ACCEPT != bid.action) {
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
				.filter("status =", Bid.Action.ACTIVATE)
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
		QueryResultIterable<Key<ListingDoc>> docsIt = getOfy().query(ListingDoc.class).fetchKeys();
		List<ListingDoc> docs = new ArrayList<ListingDoc>(getOfy().get(docsIt).values());
		return docs;
	}

	public void deleteDocument(long docId) {
		log.info("Deleting document id = " + docId);
		getOfy().delete(ListingDoc.class, docId);
	}

	public Notification[] storeNotification(Notification ...notifications) {
		getOfy().put(notifications);
		for (Notification notif : notifications) {
			updateUserStatistics(notif.userA.getId());
		}
        return notifications;
	}

	public List<Notification> markNotificationAsRead(long userId, long listingId) {
		QueryResultIterable<Notification> notifIt = getOfy().query(Notification.class)
				.filter("userA =", new Key<SBUser>(SBUser.class, userId))
				.filter("listing =", new Key<Listing>(Listing.class, listingId))
				.filter("read =", Boolean.FALSE).fetch();
		List<Notification> updateList = new ArrayList<Notification>();
		for (Notification notif : notifIt) {
			notif.read = true;
			updateList.add(notif);
		}
		if (updateList.size() > 0) {
			getOfy().put(updateList);
			// since all notification belong to one user we just need to update his/her statistics
			updateUserStatistics(userId);
		}
		return updateList;
	}

	public List<Notification> getUnreadNotifications(long userId, long listingId, ListPropertiesVO listProperties) {
		Query<Notification> query = getOfy().query(Notification.class)
				.filter("userA =", new Key<SBUser>(SBUser.class, userId))
				.filter("listing =", new Key<Listing>(Listing.class, listingId))
				.filter("read =", false)
				.filter("display =", true)
				.order("-created")
				.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<Notification>> keyList = new CursorHandler<Notification>().handleQuery(listProperties, query);
		List<Notification> nots = new ArrayList<Notification>(getOfy().get(keyList).values());
		return nots;
	}

	public List<Notification> getAllUserNotifications(long userId, ListPropertiesVO listProperties) {
		Query<Notification> query = getOfy().query(Notification.class)
				.filter("userA =", new Key<SBUser>(SBUser.class, userId))
				.filter("display =", true)
				.order("read")
				.order("-created")
				.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<Notification>> keyList = new CursorHandler<Notification>().handleQuery(listProperties, query);
		List<Notification> nots = new ArrayList<Notification>(getOfy().get(keyList).values());
		return nots;
	}

	public List<Notification> getUnreadUserNotifications(long userId, ListPropertiesVO listProperties) {
		Query<Notification> query = getOfy().query(Notification.class)
				.filter("userA =", new Key<SBUser>(SBUser.class, userId))
				.filter("read =", false)
				.filter("display =", true)
				.order("-created")
				.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<Notification>> keyList = new CursorHandler<Notification>().handleQuery(listProperties, query);
		List<Notification> nots = new ArrayList<Notification>(getOfy().get(keyList).values());
		return nots;
	}

	public List<Notification> getUserListingNotifications(long userId, long listingId, Notification.Type type, ListPropertiesVO listProperties) {
		Query<Notification> query = getOfy().query(Notification.class)
				.filter("userA =", new Key<SBUser>(SBUser.class, userId))
				.filter("listing =", new Key<Listing>(Listing.class, listingId))
				.filter("type =", type)
				.filter("display =", true)
				.order("read")
				.order("-created")
				.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<Notification>> keyList = new CursorHandler<Notification>().handleQuery(listProperties, query);
		List<Notification> nots = new ArrayList<Notification>(getOfy().get(keyList).values());
		return nots;
	}

	public List<Notification> getPublicListingNotifications(long listingId, ListPropertiesVO listProperties) {
		Query<Notification> query = getOfy().query(Notification.class)
				.filter("direction =", Notification.Direction.A_TO_B)
				.filter("listing =", new Key<Listing>(Listing.class, listingId))
				.filter("replied =", true)
				//.filter("display =", true) @FIXME: this type of filtering is removing valid q&a
				.order("-created")
				.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<Notification>> keyList = new CursorHandler<Notification>().handleQuery(listProperties, query);
		List<Notification> nots = new ArrayList<Notification>(getOfy().get(keyList).values());
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
	
	public List<Notification> getNotificationThread(long notifId) {
		Query<Notification> query = getOfy().query(Notification.class)
				.filter("context =", notifId)
				.filter("direction =", Notification.Direction.A_TO_B)
				.order("-created")
				.chunkSize(200)
       			.prefetchSize(200);
		return new ArrayList<Notification>(getOfy().get(query.fetchKeys()).values());
	}
	
	public Monitor getListingMonitor(long userId, long listingId) {
		return getListingMonitor(new Key<SBUser>(SBUser.class, userId), new Key<Listing>(Listing.class, listingId));
	}
	
	private Monitor getListingMonitor(Key<SBUser> userKey, Key<Listing> listingKey) {
		Query<Monitor> query = getOfy().query(Monitor.class).filter("user =", userKey)
				.filter("monitoredListing =", listingKey);		
		return query.get();
	}

	public Monitor setMonitor(Monitor monitor) {
		Monitor existingMonitor = getListingMonitor(monitor.user, monitor.monitoredListing);
		if (existingMonitor != null) {
			// monitor already exists
			existingMonitor.active = true;
			existingMonitor.deactivated = null;
			
			getOfy().put(existingMonitor);
			log.info("Updated existing monitor: " + existingMonitor);
			return existingMonitor;
		} else {
			// we need to create new monitor
			monitor.created = new Date();
			monitor.deactivated = null;
			monitor.active = true;

			getOfy().put(monitor);
			log.info("Set new monitor: " + monitor);
			return monitor;
		}
	}

	public Monitor deactivateListingMonitor(long userId, long listingId) {
		try {
			Monitor monitor = getListingMonitor(userId, listingId);
			monitor.active = false;
			monitor.deactivated = new Date();
			getOfy().put(monitor);
			return monitor;
		} catch (Exception e) {
			log.log(Level.WARNING, "Monitor for listing '" + listingId + "' by user '" + userId + "'not found!");
			return null;
		}
	}

	public List<Monitor> getMonitorsForListing(long objectId, ListPropertiesVO listProperties) {
		Query<Monitor> query = getOfy().query(Monitor.class)
				.filter("monitoredListing =", new Key<Listing>(Listing.class, objectId))
				.filter("active =", true)
       			.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<Monitor>> keyList = new CursorHandler<Monitor>().handleQuery(listProperties, query);
		List<Monitor> mons = new ArrayList<Monitor>(getOfy().get(keyList).values());
		return mons;
	}

	public List<Monitor> getMonitorsForUser(long userId, ListPropertiesVO listProperties) {
		Query<Monitor> query = getOfy().query(Monitor.class)
				.filter("user =", new Key<SBUser>(SBUser.class, userId))
				.filter("active =", true)
       			.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<Monitor>> keyList = new CursorHandler<Monitor>().handleQuery(listProperties, query);
		List<Monitor> mons = new ArrayList<Monitor>(getOfy().get(keyList).values());
		return mons;
	}
	
	public List<Listing> getMonitoredListings(long userId, ListPropertiesVO listProperties) {
		List<Key<Listing>> monitoredListingKeys = new ArrayList<Key<Listing>>();
		List<Monitor> monitors = getMonitorsForUser(userId, listProperties);
		log.info("Fetched monitors for user '" + userId + "': " + monitors);
		for (Monitor monitor : monitors) {
			monitoredListingKeys.add(new Key<Listing>(Listing.class, monitor.monitoredListing.getId()));
		}
		return getListingsByKeys(monitoredListingKeys);
	}
/*
	public Map<String, Monitor> getMonitorsMapForUser(long userId, ListPropertiesVO listProperties) {
		Map<String, Monitor> result = new HashMap<String, Monitor>();
		for (Monitor monitor : getMonitorsForUser(userId, listProperties)) {
			result.put(monitor.monitoredListing.getString(), monitor);
		}
		return result;
	}
*/
	public List<Category> getCategories() {
		QueryResultIterable<Key<Category>> catIt = getOfy().query(Category.class)
				.order("name").fetchKeys();
		List<Category> categories = new ArrayList<Category>(getOfy().get(catIt).values());
		return categories;
	}

	public void storeLocations(List<Location> locations) {
        log.info("Storing locations: " + locations);
		Collections.sort(locations, new Location.TopComparator());
		List<Location> topLocations = locations.subList(0, locations.size() > 20 ? 20 : locations.size() - 1);
        log.info("Top locations: " + topLocations);
		
		QueryResultIterable<Key<Location>> locIt = getOfy().query(Location.class).fetchKeys();
		getOfy().delete(locIt);
		getOfy().put(topLocations);
	}
	
	public void storeCategories(List<Category> categories) {
		getOfy().put(categories);
	}
	
	public List<Location> getTopLocations() {
		QueryResultIterable<Key<Location>> locIt = getOfy().query(Location.class)
				.order("-value").fetchKeys();
		List<Location> locations = new ArrayList<Location>(getOfy().get(locIt).values());
		return locations;
	}

	public List<ListingLocation> getAllListingLocations() {
		return getOfy().query(ListingLocation.class).list();
	}

	public QuestionAnswer askListingOwner(SBUser user, Listing listing, String text) {
		QuestionAnswer qa = new QuestionAnswer();
		qa.question = text;
		qa.created = new Date();
		qa.listing = listing.getKey();
		qa.listingOwner = listing.owner;
		qa.published = false;
		qa.user = user.getKey();
		qa.userNickname = user.nickname;
		getOfy().put(qa);
		
		return qa;
	}
	
	public QuestionAnswer answerQuestion(QuestionAnswer qa, String answer, boolean publish) {
		qa.answer = answer;
		qa.answerDate = new Date();
		qa.published = publish;
		getOfy().put(qa);
		return qa;
	}
	
	public QuestionAnswer getQuestionAnswer(long qaId) {
		return getOfy().find(QuestionAnswer.class, qaId);
	}
	
	public List<QuestionAnswer> getQuestionAnswersForListingOwner(Listing listing, ListPropertiesVO listProperties) {
		Query<QuestionAnswer> query = getOfy().query(QuestionAnswer.class)
				.filter("listing =", listing.getKey())
				.order("created")
       			.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<QuestionAnswer>> keyList = new CursorHandler<QuestionAnswer>().handleQuery(listProperties, query);
		List<QuestionAnswer> mons = new ArrayList<QuestionAnswer>(getOfy().get(keyList).values());
		return mons;
	}
	
	public List<QuestionAnswer> getQuestionAnswersForUser(SBUser user, Listing listing, ListPropertiesVO listProperties) {
		Query<QuestionAnswer> query = getOfy().query(QuestionAnswer.class)
				.filter("listing =", listing.getKey())
				.filter("user =", user.getKey())
				.filter("published =", false)
				.order("created")
       			.chunkSize(100)
       			.prefetchSize(100);
		// fetching all unanswered questions asked by user
		List<Key<QuestionAnswer>> keyList = new ArrayList<Key<QuestionAnswer>>();
		for (Key<QuestionAnswer> key : query.fetchKeys()) {
			keyList.add(key);
		}
		if (keyList.size() < listProperties.getMaxResults()) {
			// fetching public questions
			listProperties.setMaxResults(listProperties.getMaxResults() - keyList.size());
			query = getOfy().query(QuestionAnswer.class)
				.filter("listing =", listing.getKey())
				.filter("published =", true)
				.order("created")
       			.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
			keyList.addAll(new CursorHandler<QuestionAnswer>().handleQuery(listProperties, query));
		}
		List<QuestionAnswer> qandas = new ArrayList<QuestionAnswer>(getOfy().get(keyList).values());
		return qandas;
	}

	/**
	 * Returns only public Q&As
	 */
	public List<QuestionAnswer> getQuestionAnswers(Listing listing, ListPropertiesVO listProperties) {
		Query<QuestionAnswer> query = getOfy().query(QuestionAnswer.class)
				.filter("listing =", listing.getKey())
				.filter("published =", true)
				.order("created")
       			.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<QuestionAnswer>> keyList = new CursorHandler<QuestionAnswer>().handleQuery(listProperties, query);
		List<QuestionAnswer> qandas = new ArrayList<QuestionAnswer>(getOfy().get(keyList).values());
		return qandas;
	}
}
