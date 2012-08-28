package com.startupbidder.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.startupbidder.datamodel.PictureImport;
import com.startupbidder.datamodel.QuestionAnswer;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.SystemProperty;
import com.startupbidder.datamodel.UserStats;
import com.startupbidder.datamodel.Vote;
import com.startupbidder.vo.ListPropertiesVO;
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

    public SBUser getUserByTwitter(long twitterId) {
        SBUser user = getOfy().query(SBUser.class).filter("twitterId =", twitterId).get();
        log.info("Existing user for email " + twitterId + " is " + user);
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
		return createUser(email, nickname, null);
	}

	public SBUser createUser(String email, String nickname, String name) {
        String userEmail = StringUtils.isNotEmpty(email) ? email : "anonymous" + String.valueOf(new Random().nextInt(1000000000)) + "@startupbidder.com";
        SBUser user = getUserByEmail(userEmail);
        if (user != null) {
            log.warning("User with email '" + userEmail + "' already exists, cannot create user.");
            return null;
        }
        String userNickname = (StringUtils.isNotEmpty(nickname) && !nickname.equalsIgnoreCase(email))
                    ? nickname
                    : (userEmail.contains("@") ? email.substring(0, email.indexOf("@")) : "anonymous" + String.valueOf(new Random().nextInt(1000000000)));
        user = getUserByNickname(userNickname);
        if (user != null) {
            log.warning("User with nickname " + userNickname + " already exists, cannot create user.");
            return null;
        }
    	user = new SBUser();
        user.email = userEmail;
        user.nickname = userNickname;
        user.nicknameLower = userNickname.toLowerCase();
    	user.name = name;
	    user.modified = user.lastLoggedIn = user.joined = new Date();
		user.status = SBUser.Status.ACTIVE;
		user.notifyEnabled = true;
		getOfy().put(user);
        log.info("Created user with nickname " + user.nickname + " as " + user);
		return user;
	}
	
	public SBUser createUser(long twitterId, String twitterScreenName) {
        SBUser user = getUserByTwitter(twitterId);
        if (user != null) {
            log.warning("User with twitterId '" + twitterId + "' already exists, cannot create user.");
            return null;
        }
    	user = new SBUser();
    	user.twitterEmail = "not set";
    	user.twitterId = twitterId;
    	user.twitterScreenName = twitterScreenName;
        user.email = "<twitter_login>";
        user.nickname = twitterScreenName;
        user.nicknameLower = twitterScreenName.toLowerCase();
	    user.modified = user.lastLoggedIn = user.joined = new Date();
		user.status = SBUser.Status.CREATED;
		user.notifyEnabled = false;
		getOfy().put(user);
        log.info("Created user with twitter id " + twitterId + " as " + user);
		return user;
	}
	
	public SBUser createUser(String email, String password, String authCookie, String name, String location, boolean investor) {
        String userEmail = StringUtils.isNotEmpty(email) ? email : "anonymous" + String.valueOf(new Random().nextInt(1000000000)) + "@startupbidder.com";
        SBUser user = getUserByEmail(email);
        if (user != null) {
            log.warning("User with email '" + userEmail + "' already exists, cannot create user.");
            return null;
        }
        String userNickname = userEmail.contains("@") ? email.substring(0, email.indexOf("@")) : "anonymous" + String.valueOf(new Random().nextInt(1000000000));
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
//		for (Bid bid : BidObjectifyDatastoreDAO.getInstance().getBidsForListing(listing)) {
//			if (mostValuedBid == null || mostValuedBid.valuation < bid.valuation) {
//				mostValuedBid = bid;
//			}
//			values.add(bid.value);
//		}
//		if (mostValuedBid != null) {
//			listingStats.valuation = mostValuedBid.valuation;
//		} else {
//			listingStats.valuation = listing.suggestedValuation;
//		}

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

        listingStats.numberOfQuestions = getQuestionAnswersPublishedCount(listing);

		double timeFactor = Math.pow((double)(Days.daysBetween(new DateTime(listing.listedOn), new DateTime()).getDays() + 2), 1.5d);
		double score = (listingStats.numberOfMonitors + listingStats.numberOfComments + 100*listingStats.numberOfBids + 10*listingStats.numberOfQuestions + (median/1000)) / timeFactor;
		listingStats.score = score;
		
        listingStats.askedForFunding = listing.askedForFunding;
        
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
			user.userClass = newUser.userClass;
			user.password = newUser.password;
			user.authCookie = newUser.authCookie;
            user.editedListing = newUser.editedListing;
            user.twitterId = newUser.twitterId;
            user.twitterScreenName = newUser.twitterScreenName;
            user.twitterEmail = newUser.twitterEmail;
            user.googleId = newUser.googleId;
            user.googleName = newUser.googleName;
            user.googleEmail = newUser.googleEmail;
            user.facebookId = newUser.facebookId;
            user.facebookName = newUser.facebookName;
            user.facebookEmail = newUser.facebookEmail;
            user.avatarUrl = newUser.avatarUrl;

			getOfy().put(user);
			
			log.log(Level.INFO, "Updated user: " + user);
			return user;
		} catch (Exception e) {
			log.log(Level.WARNING, "User '" + newUser.id + "' not found", e);
			return null;
		}
	}

	public SBUser prepareUpdateUsersEmailByTwitter(SBUser twitterUser, String email) {
		twitterUser.twitterEmail = email;
		twitterUser.activationCode = RandomStringUtils.randomAlphanumeric(30);
		getOfy().put(twitterUser);
		log.log(Level.INFO, "Updated user: " + twitterUser);
		return twitterUser;
	}
	
	public SBUser updateUsersEmailByTwitter(SBUser twitterUser, String email) {
		SBUser userByEmail = getUserByEmail(email);
		if (userByEmail != null) {
			log.info("There is existing user with email address " + email + ". "
					+ "Old twitter account will be deactivated and twitter data associated with user identified by email.");
			userByEmail.twitterId = twitterUser.twitterId;
			userByEmail.twitterScreenName = twitterUser.twitterScreenName;
			userByEmail.twitterEmail = email;
			twitterUser.status = SBUser.Status.DEACTIVATED;
			twitterUser.activationCode = "Activated for " + email + ". Twitter id: " + twitterUser.twitterId;
			twitterUser.twitterId = 0;
			twitterUser.email = null;
			getOfy().put(userByEmail, twitterUser);
			log.log(Level.INFO, "Updated user identified by email: " + userByEmail
					+ " Deactivated twitter user: " + twitterUser);
		} else {
			twitterUser.email = email;
			twitterUser.twitterEmail = email;
			twitterUser.notifyEnabled = true;
			twitterUser.activationCode = null;
			getOfy().put(twitterUser);
			log.log(Level.INFO, "Updated twitter user: " + twitterUser);
		}
		return twitterUser;
	}
	
	public SBUser setTwitterForEmailAccount(String email, long twitterId, String twitterScreenName) {
		try {
			SBUser userByEmail = getUserByEmail(email);
			if (userByEmail != null) {
				userByEmail.twitterId = twitterId;
				userByEmail.twitterScreenName = twitterScreenName;
				userByEmail.twitterEmail = email;
				getOfy().put(userByEmail);
				log.log(Level.INFO, "Updated user: " + userByEmail);
			}
			return userByEmail;
		} catch (Exception e) {
			log.log(Level.WARNING, "User with email '" + email + "' not found", e);
			return null;
		}
	}	

	public List<SBUser> getAllUsers() {
		QueryResultIterable<Key<SBUser>> usersIt = getOfy().query(SBUser.class)
				.order("nicknameLower").fetchKeys();
		List<SBUser> users = new ArrayList<SBUser>(getOfy().get(usersIt).values());
		return users;
	}
	
	public List<SBUser> getDragons(ListPropertiesVO listingProperties) {
		Query<SBUser> query = getOfy().query(SBUser.class)
				.filter("dragon =", true)
                .order("nicknameLower")
       			.chunkSize(listingProperties.getMaxResults())
       			.prefetchSize(listingProperties.getMaxResults());
		List<Key<SBUser>> keyList = new CursorHandler<SBUser>().handleQuery(listingProperties, query);
		List<SBUser> listings = new ArrayList<SBUser>(getOfy().get(keyList).values());
		return listings;
	}

	public List<SBUser> getListers(ListPropertiesVO listingProperties) {
		Query<SBUser> query = getOfy().query(SBUser.class)
				.filter("lister =", true)
                .order("nicknameLower")
       			.chunkSize(listingProperties.getMaxResults())
       			.prefetchSize(listingProperties.getMaxResults());
		List<Key<SBUser>> keyList = new CursorHandler<SBUser>().handleQuery(listingProperties, query);
		List<SBUser> listings = new ArrayList<SBUser>(getOfy().get(keyList).values());
		return listings;
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
		if (owner.editedListing != null) {
			Listing editedListing = getOfy().find(owner.editedListing);
			if (editedListing != null) {
				return editedListing;
			} else {
				owner.editedListing = null;
			}
		}
		
		getOfy().put(listing);
		owner.editedListing = new Key<Listing>(Listing.class, listing.id);
		getOfy().put(owner);
		return listing;
	}

	public Listing updateListingStateAndDates(Listing newListing, String note) {
		try {
			Listing listing = getOfy().get(new Key<Listing>(Listing.class, newListing.id));
			Listing.State oldState = listing.state;
			listing.state = newListing.state;
			listing.closingOn = newListing.closingOn;
			listing.listedOn = newListing.listedOn;
			listing.created = newListing.created;
			listing.posted = newListing.posted;
			listing.notes += note + "\n";
			getOfy().put(listing);
			
			// listing activation should allow for editing new listing by the owner
			if (oldState == Listing.State.POSTED && newListing.state == Listing.State.ACTIVE) {
				SBUser owner = getOfy().get(listing.owner);
				owner.editedListing = null;
				owner.lister = true;
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
			@SuppressWarnings("unchecked")
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
			@SuppressWarnings("unchecked")
			List<Object[]> result = (List<Object[]>)mem.get(ListingFacade.MEMCACHE_ALL_LISTING_LOCATIONS);
			if (result != null) {
				Object[] array = null;
				for (int i = 0; i < result.size(); i++) {
					array = result.get(i);
					if (listing.getWebKey().equals(array[0])) {
						result.remove(i);
						break;
					}
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
	
	public Listing storeListing(Listing listing) {
		getOfy().put(listing);
		log.info("Put into datastore " + listing);
		return listing;
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

	public Listing deleteListing(long listingId) {
		Key<Listing> listingKey = new Key<Listing>(Listing.class, listingId);
		Listing listing = getOfy().get(listingKey);
		if (listing != null && (listing.state == Listing.State.NEW || listing.state == Listing.State.POSTED)) {
			getOfy().delete(listing);
			SBUser user = getOfy().get(listing.owner);
			if (listingKey.equals(user.editedListing)) {
				user.editedListing = null;
				getOfy().put(user);
			}
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

    public List<Listing> getListingsForCategory(String category, ListPropertiesVO listingProperties) {
        Query<Listing> query = getOfy().query(Listing.class)
            .filter("state =", Listing.State.ACTIVE)
            .order("-listedOn")
            .chunkSize(listingProperties.getMaxResults())
            .prefetchSize(listingProperties.getMaxResults());
        if (category != null) {
            query = query.filter("category =", category);
        }
        List<Key<Listing>> keyList = new CursorHandler<Listing>().handleQuery(listingProperties, query);
        List<Listing> listings = new ArrayList<Listing>(getOfy().get(keyList).values());
        return listings;
    }

    public List<Listing> getListingsForLocation(String country, String state, String city, ListPropertiesVO listingProperties) {
        Query<Listing> query = getOfy().query(Listing.class)
            .filter("state =", Listing.State.ACTIVE)
            .order("-listedOn")
            .chunkSize(listingProperties.getMaxResults())
            .prefetchSize(listingProperties.getMaxResults());
        if (country != null) {
            query = query.filter("country =", country);
        }
        if (state != null) {
            query = query.filter("usState =", state);
        }
        if (city != null) {
            query = query.filter("city =", city);
        }
        List<Key<Listing>> keyList = new CursorHandler<Listing>().handleQuery(listingProperties, query);
        List<Listing> listings = new ArrayList<Listing>(getOfy().get(keyList).values());
        return listings;
    }

	public List<Listing> getTopListings(ListPropertiesVO listingProperties) {
		Query<ListingStats> query = getOfy().query(ListingStats.class)
				.filter("state =", Listing.State.ACTIVE)
                .filter("askedForFunding =", true)
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
	public List<Comment> getCommentsForListing(long listingId, ListPropertiesVO listProperties) {
		Query<Comment> query = getOfy().query(Comment.class)
				.filter("listing =", new Key<Listing>(Listing.class, listingId))
				.order("-commentedOn");
		List<Key<Comment>> keyList = new CursorHandler<Comment>().handleQuery(listProperties, query);
		List<Comment> comments = new ArrayList<Comment>(getOfy().get(keyList).values());
		return comments;
	}

	public List<Comment> getCommentsForUser(long userId, ListPropertiesVO listProperties) {
		Query<Comment> query = getOfy().query(Comment.class)
				.filter("user =", new Key<SBUser>(SBUser.class, userId))
				.order("-commentedOn");
		List<Key<Comment>> keyList = new CursorHandler<Comment>().handleQuery(listProperties, query);
		List<Comment> comments = new ArrayList<Comment>(getOfy().get(keyList).values());
		return comments;
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
				.fetchKeys();
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
	
	public ListingDoc getListingDocument(Key<ListingDoc> docKey) {
		if (docKey != null) {
			return getOfy().find(ListingDoc.class, docKey.getId());
		} else {
			return null;
		}
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
	
	public void storeListingAndDocs(Listing listing, ListingDoc doc1, ListingDoc doc2) {
		if (doc2 == null) {
			getOfy().put(listing, doc1);
		} else if (doc1 == null) {
			getOfy().put(listing, doc2);
		} else {
			getOfy().put(listing, doc1, doc2);
		}
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
	
	public QuestionAnswer answerQuestion(Listing listing, QuestionAnswer qa, String answer, boolean publish) {
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
		listProperties.setNumberOfResults(qandas.size());
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

    public int getQuestionAnswersPublishedCount(Listing listing) {
        QueryResultIterable<Key<QuestionAnswer>> questionIt = getOfy().query(QuestionAnswer.class)
                .filter("listing = ", listing.getKey())
                .filter("published =", true)
                .fetchKeys();
        return CollectionUtils.size(questionIt.iterator());
    }
    
    public void storePictureImports(PictureImport ... pics) {
    	getOfy().put(pics);
    }
    
    public PictureImport getFirstPictureImport(Key<Listing> listingKey) {
    	try {
    		PictureImport pic = getOfy().query(PictureImport.class).filter("listing =", listingKey).fetch().iterator().next();
    		if (pic != null) {
    			getOfy().delete(pic);
    		}
    		return pic;
    	} catch(NoSuchElementException nsee) {
    		return null;
    	} catch (Exception e) {
    		log.log(Level.WARNING, "Error fetching PictureImport", e);
    		return null;
    	}
    }

	public Map<String, SBUser> getUsers(Set<Key<Object>> userKeys) {
		Map<Key<Object>, Object> map = getOfy().get(userKeys);
		Map<String, SBUser> users = new HashMap<String, SBUser>();
		
		for (Map.Entry<Key<Object>, Object> entry : map.entrySet()) {
			users.put(entry.getKey().getString(), (SBUser)entry.getValue());
		}
		return users;
	}

	public Map<String, ListingStats> getListingStatistics(Set<Key<Object>> userKeys) {
		Map<Key<Object>, Object> map = getOfy().get(userKeys);
		Map<String, ListingStats> users = new HashMap<String, ListingStats>();
		
		for (Map.Entry<Key<Object>, Object> entry : map.entrySet()) {
			users.put(entry.getKey().getString(), (ListingStats)entry.getValue());
		}
		return users;
	}
}
