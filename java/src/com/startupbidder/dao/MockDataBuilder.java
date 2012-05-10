package com.startupbidder.dao;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.objectify.NotFoundException;
import com.startupbidder.web.controllers.ListingController;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.joda.time.DateTime;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.Category;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.ListingLocation;
import com.startupbidder.datamodel.ListingStats;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.PaidBid;
import com.startupbidder.datamodel.Rank;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.SystemProperty;
import com.startupbidder.datamodel.UserStats;
import com.startupbidder.datamodel.Vote;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.ListingFacade;

/**
 * Generates mock data.
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class MockDataBuilder {
	private static final Logger log = Logger.getLogger(MockDataBuilder.class.getName());
	static ObjectifyDatastoreDAO instance;

	public UserVO GREG;
	public UserVO JOHN;
	public UserVO AHMED;
	public UserVO JACOB;
	public UserVO INSIDER;
	public UserVO MADMAX;
	public UserVO THEONE;
	public UserVO JENNY;
	public UserVO EMPEROR;
	public UserVO BURNTSKY;
	public UserVO DRAGON;
    public UserVO ANGEL;
    public UserVO STARTUPLY;
    public static final String ANGEL_EMAIL = "api@angel.co";
    public static final String STARTUPLY_EMAIL = "contact@startuply.com";
	public UserVO NOT_ACTIVATED;

	public List<SBUser> users;
	public List<Listing> listings;
	
	private Listing gregsListing;
	private Listing johnsListing;

    private boolean isIdInitialized = false;
	private long id = 2000L;

    private static Map<Long, String> logoUrls = new ConcurrentHashMap<Long, String>();

	private long id() {
        if (!isIdInitialized) {
            try {
                Listing maxListing = getOfy().query(Listing.class).order("-id").chunkSize(1).prefetchSize(1).get();
                if (maxListing == null) {
                    log.info("No listing IDs found, starting from default id: " + id);
                }
                else {
                    id = maxListing.id;
                }
            }
            catch (NotFoundException e) {
                log.info("No listing IDs found, starting from default id: " + id);
            }
            isIdInitialized = true;
        }
		return id++;
	}

	public String clearDatastore(UserVO loggedInUser) {
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			log.log(Level.WARNING, "User '" + loggedInUser + "' is not an admin.");
			return "User '" + loggedInUser + "' is not an admin.";
		}
		StringBuffer output = new StringBuffer();

		QueryResultIterable<Key<SBUser>> u = getOfy().query(SBUser.class).fetchKeys();
		output.append("Deleted users: " + u.toString() + "</br>");
		getOfy().delete(u);
		
		QueryResultIterable<Key<UserStats>> us = getOfy().query(UserStats.class).fetchKeys();
		output.append("Deleted user stats: " + us.toString() + "</br>");
		getOfy().delete(us);		

		QueryResultIterable<Key<Listing>> l = getOfy().query(Listing.class).fetchKeys();
		output.append("Deleted listings: " + l.toString() + "</br>");
		getOfy().delete(l);
		
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		QueryResultIterable<ListingDoc> docs = getOfy().query(ListingDoc.class).fetch();
		for (ListingDoc doc : docs) {
			try {
				blobstoreService.delete(doc.blob);
			} catch (Exception e) {
				log.log(Level.INFO, "Error deleting blob", e);
			}
		}
		QueryResultIterable<Key<ListingDoc>> d = getOfy().query(ListingDoc.class).fetchKeys();
		output.append("Deleted listing docs: " + docs.toString() + "</br>");
		getOfy().delete(d);

        QueryResultIterable<Key<ListingLocation>> locs = getOfy().query(ListingLocation.class).fetchKeys();
        output.append("Deleted listing locations: " + locs.toString() + "</br>");
        getOfy().delete(locs);

		QueryResultIterable<Key<ListingStats>> ls = getOfy().query(ListingStats.class).fetchKeys();
		output.append("Deleted listings stats: " + ls.toString() + "</br>");
		getOfy().delete(ls);		

		QueryResultIterable<Key<Bid>> b = getOfy().query(Bid.class).fetchKeys();
		output.append("Deleted bids: " + b.toString() + "</br>");
		getOfy().delete(b);
		
		QueryResultIterable<Key<Comment>> c = getOfy().query(Comment.class).fetchKeys();
		output.append("Deleted comments: " + c.toString() + "</br>");
		getOfy().delete(c);
		
		QueryResultIterable<Key<Monitor>> m = getOfy().query(Monitor.class).fetchKeys();
		output.append("Deleted monitors: " + m.toString() + "</br>");
		getOfy().delete(m);
		
		QueryResultIterable<Key<Notification>> n = getOfy().query(Notification.class).fetchKeys();
		output.append("Deleted notifications: " + n.toString() + "</br>");
		getOfy().delete(n);
		
		QueryResultIterable<Key<Rank>> r = getOfy().query(Rank.class).fetchKeys();
		output.append("Deleted ranks: " + r.toString() + "</br>");
		getOfy().delete(r);
		
		QueryResultIterable<Key<Vote>> v = getOfy().query(Vote.class).fetchKeys();
		output.append("Deleted votes: " + v.toString() + "</br>");
		getOfy().delete(v);
		
		output.append("</br>");
		return output.toString();
	}
	
	public String createMockDatastore(boolean storeData, boolean initializeNow) {
		StringBuffer output = new StringBuffer();
		
		List<SBUser> users = createMockUsers();
		if (storeData) {
			getOfy().put(users);
			output.append("Inserted " + users.size() + " users.</br>");
		
			getOfy().put(createCategories());
		}
		
		List<Listing> listings = createMockListings(users);
		
		if (storeData) {
			getOfy().put(listings);
			output.append("Inserted " + listings.size() + " listings.</br>");
		}
		
		if (storeData) {
			List<ListingLocation> listingLocList = new ArrayList<ListingLocation>();
			for (Listing listing : listings) {
				if (listing.state == Listing.State.NEW || listing.state == Listing.State.POSTED) {
					SBUser owner = getOfy().find(listing.owner);
					owner.editedListing = new Key<Listing>(Listing.class, listing.id);
					getOfy().put(owner);
				} else if (listing.state == Listing.State.ACTIVE) {
					listingLocList.add(new ListingLocation(listing));
				}
                ObjectifyDatastoreDAO.getInstance().updateListingStatistics(listing.id);

				if (initializeNow) {
					ListingFacade.instance().updateMockListingImages(listing.id);
				} else {
					String taskName = new Date().getTime() + "_mock_listing_file_update_" + listing.getWebKey();
					Queue queue = QueueFactory.getDefaultQueue();
					queue.add(TaskOptions.Builder.withUrl("/task/update-mock-listing-images").param("id", listing.getWebKey())
							.taskName(taskName).countdownMillis(10000));
				}
			}
			output.append("Listing statistics updated and file update scheduled.</br>");
			getOfy().put(listingLocList);
			output.append("Listing location data stored.</br>");
		}
		
		return output.toString();
	}
	
    public String importAngelListData(String fromId, String toId) {
        isIdInitialized = false; // reset
        StringBuffer output = new StringBuffer();
        List<SBUser> users = createAngelListUsers();
        for (SBUser user : users) {
            try {
                getOfy().get(SBUser.class, user.id);
            }
            catch (NotFoundException e) {
                getOfy().put(user);
                output.append("Inserted AngelList User: " + user.name + "</br>");
            }
        }
        
        try {
            getOfy().get(Category.class, 1);
        }
        catch (NotFoundException e) {
            getOfy().put(createCategories());
            output.append("Inserted Categories");
        }

        List<Listing> listings = createAngelListListings(users, fromId, toId);
        for (Listing listing : listings) {
            try {
                getOfy().get(Listing.class, listing.id);
            }
            catch (NotFoundException e) {
                getOfy().put(listing);
                output.append("Inserted AngelList Listing: " + listing.id + ".</br>");
            }
        }

        List<ListingLocation> listingLocList = new ArrayList<ListingLocation>();
        for (Listing listing : listings) {
            try {
                listingLocList.add(new ListingLocation(listing));
                ObjectifyDatastoreDAO.getInstance().updateListingStatistics(listing.id);
                ListingFacade.instance().updateMockListingImages(listing.id);
            }
            catch (Exception e) {
                log.log(Level.WARNING, "Exception while processing listing id: " + listing.id, e);
            }
        }
        output.append("Listing statistics updated and file update scheduled.</br>");
        getOfy().put(listingLocList);
        output.append("Listing location data stored.</br>");
        
        return output.toString();
    }

    public String importStartuplyData(int fromId, int toId) {
        isIdInitialized = false;
        StringBuffer output = new StringBuffer();
        List<SBUser> users = createStartuplyUsers();
        for (SBUser user : users) {
            try {
                getOfy().get(SBUser.class, user.id);
            }
            catch (NotFoundException e) {
                getOfy().put(user);
                output.append("Inserted Startuply User: " + user.name + "</br>");
            }
        }

        try {
            getOfy().get(Category.class, 1);
        }
        catch (NotFoundException e) {
            getOfy().put(createCategories());
            output.append("Inserted Categories");
        }

        List<Listing> listings = createStartuplyListings(users, fromId, toId);
        for (Listing listing : listings) {
            try {
                getOfy().get(Listing.class, listing.id);
            }
            catch (NotFoundException e) {
                getOfy().put(listing);
                output.append("Inserted Startuply Listing: " + listing.id + ".</br>");
            }
        }

        List<ListingLocation> listingLocList = new ArrayList<ListingLocation>();
        for (Listing listing : listings) {
            try {
                listingLocList.add(new ListingLocation(listing));
                ObjectifyDatastoreDAO.getInstance().updateListingStatistics(listing.id);
                ListingFacade.instance().updateMockListingImages(listing.id, false);
            }
            catch (Exception e) {
                log.log(Level.WARNING, "Exception while processing listing id: " + listing.id, e);
            }
        }
        output.append("Listing statistics updated and file update scheduled.</br>");
        getOfy().put(listingLocList);
        output.append("Listing location data stored.</br>");

        return output.toString();
    }

    public String printDatastoreContents(UserVO loggedInUser) {
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			log.log(Level.WARNING, "User '" + loggedInUser + "' is not an admin.");
			return "User '" + loggedInUser + "' is not an admin.";
		}
		return iterateThroughDatastore(false, new ArrayList<Object>());
	}
	
	public List<Object> exportDatastoreContents(UserVO loggedInUser) {
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			log.log(Level.WARNING, "User '" + loggedInUser + "' is not an admin.");
			return null;
		}
		List<Object> dtoList = new ArrayList<Object>();
		iterateThroughDatastore(false, dtoList);
		return dtoList;
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
		
		List<Key<Category>> catKeys = new ArrayList<Key<Category>>();
		CollectionUtils.addAll(catKeys, getOfy().query(Category.class).fetchKeys().iterator());
		outputBuffer.append("<p>Categories (" + catKeys.size() + "):</p>");
		if (catKeys.size() > 0) {
			//for (Bid obj : getOfy().get(bidKeys).values()) {
			for (Key<Category> key : catKeys) {
				Category obj = getOfy().get(key);
				outputBuffer.append(obj).append("<br/>");
			}
			if (delete) {
				getOfy().delete(catKeys);
			}
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
	
	private Objectify getOfy() {
		Objectify ofy = ObjectifyService.begin();
		return ofy;
	}


	public List<Category> createCategories() {
		List<Category> categories = new ArrayList<Category>();
		categories.add(new Category(1, "Biotech"));
		categories.add(new Category(2, "Chemical"));
		categories.add(new Category(3, "Retail"));
		categories.add(new Category(4, "Electronics"));
		categories.add(new Category(5, "Energy"));
		categories.add(new Category(6, "Environmental"));
		categories.add(new Category(7, "Financial"));
		categories.add(new Category(8, "Hardware"));
		categories.add(new Category(9, "Healthcare"));
		categories.add(new Category(10, "Industrial"));
		categories.add(new Category(11, "Internet"));
		categories.add(new Category(12, "Manufacturing"));
		categories.add(new Category(13, "Media"));
		categories.add(new Category(14, "Medical"));
		categories.add(new Category(15, "Pharma"));
		categories.add(new Category(16, "Software"));
		categories.add(new Category(17, "Telecom"));
		categories.add(new Category(18, "Other"));

		return categories;
	}
	
	/**
	 * Generates random comments for listings
	 */
	public List<Comment> generateComments(Collection<SBUser> usersList, Collection<Listing> listings) {
		List<Comment> comments = new ArrayList<Comment>();
		
		List<SBUser> users = new ArrayList<SBUser>(usersList);
		for (Listing bp : listings) {
			int commentNum = new Random().nextInt(30);
			while (--commentNum > 0) {
				Comment comment = new Comment();
				comment.id = id();
				comment.mockData = true;
				comment.listing = new Key<Listing>(Listing.class, bp.id);
				comment.user = new Key<SBUser>(SBUser.class, users.get(new Random().nextInt(users.size())).id);
				comment.commentedOn = new Date(System.currentTimeMillis() - commentNum * 18 * 60 * 60 * 1000);
				comment.comment = "Comment " + commentNum;
				
				comments.add(comment);
			}
		}
		return comments;
	}	

	/**
	 * Generates mock users
	 */
	public List<SBUser> createMockUsers() {
		List<SBUser> users = new ArrayList<SBUser>();
		
		SBUser user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "Dead";
		user.name = "Ahmed";
		user.email = "deadahmed@startupbidder.com";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 22 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.DEACTIVATED;
		user.lastLoggedIn = new Date();
		AHMED = DtoToVoConverter.convert(user);
		users.add(user); // 0

		user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "fowler";
		user.name = "Jacob";
		user.email = "jpfowler@startupbidder.com";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 23 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.ACTIVE;
		user.lastLoggedIn = new Date();
		JACOB = DtoToVoConverter.convert(user);
		users.add(user); // 1

		user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "The Insider";
		user.name = "Joseph Jones";
		user.email = "insider@startupbidder.com";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 31 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.ACTIVE;
		INSIDER = DtoToVoConverter.convert(user);
		users.add(user); // 2

		user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "The Dragon";
		user.name = "Silvaneus P. Dragonopoulous";
		user.email = "dragon@startupbidder.com";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 26 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.ACTIVE;
		user.lastLoggedIn = new Date();
		user.investor = true;
		DRAGON = DtoToVoConverter.convert(user);
		users.add(user); // 3

		user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "MadMax";
		user.name = "Max Reissman";
		user.email = "madmax@startupbidder.com";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 35 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.ACTIVE;
		user.investor = true;
		user.lastLoggedIn = new Date();
		MADMAX = DtoToVoConverter.convert(user);
		users.add(user); // 4

		user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "The One";
		user.name = "Bruce Wayne";
		user.email = "bruce@startupbidder.com";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.DEACTIVATED;
		user.investor = true;
		user.lastLoggedIn = new Date();
		THEONE = DtoToVoConverter.convert(user);
		users.add(user); // 5

    	user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "jenny";
		user.name = "Jennifer Rodriguez";
		user.email = "jenny@io.com";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.DEACTIVATED;
		user.investor = true;
		user.lastLoggedIn = new Date();
		JENNY = DtoToVoConverter.convert(user);
		users.add(user); // 6

    	user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "Emperor 7";
		user.name = "Xiaofu Li";
		user.email = "li@xingminginvestments.cn";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.DEACTIVATED;
		user.investor = true;
		user.lastLoggedIn = new Date();
		EMPEROR = DtoToVoConverter.convert(user);
		users.add(user); // 7

    	user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "BurntSky";
		user.name = "Hitomi Karankawa";
		user.email = "hitomik@burntsky.com";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.DEACTIVATED;
		user.investor = true;
		user.lastLoggedIn = new Date();
		BURNTSKY = DtoToVoConverter.convert(user);
		users.add(user); // 8
		
		user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.admin = true;
		user.nickname = "arley";
		user.name = "John A. Burns";
		user.email = "johnarleyburns@gmail.com";
		user.openId = user.email;
		user.joined = new Date(0L);
		user.status = SBUser.Status.ACTIVE;
		user.investor = true;
		user.lastLoggedIn = new Date();
		users.add(user);
		JOHN = DtoToVoConverter.convert(user); // 9

        user = new SBUser();
        user.id = id();
        user.mockData = true;
        user.admin = true;
        user.nickname = "Greg";
        user.name = "Grzegorz Nittner";
        user.email = "grzegorz.nittner@gmail.com";
        user.openId = user.email;
        user.joined = new Date(0L);
        user.status = SBUser.Status.ACTIVE;
        user.investor = true;
        user.lastLoggedIn = new Date();
        users.add(user); // 9
        GREG = DtoToVoConverter.convert(user);


        user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.admin = true;
		user.nickname = "NonActive";
		user.name = "Non Active";
		user.email = "nonactive@startupbidder.com";
		user.activationCode = RandomStringUtils.randomAlphanumeric(64);
		user.joined = new Date();
		user.status = SBUser.Status.CREATED;
		user.investor = false;
		user.lastLoggedIn = null;
		users.add(user); // 10
		NOT_ACTIVATED = DtoToVoConverter.convert(user);
		
		return users;
	}

    public List<SBUser> createAngelListUsers() {
        List<SBUser> users = new ArrayList<SBUser>();
        SBUser user = new SBUser();
        user.id = id();
        user.mockData = true;
        user.admin = true;
        user.nickname = "The Angel";
        user.name = "Angelica Listopoulous";
        user.email = ANGEL_EMAIL;
        user.openId = user.email;
        user.joined = new Date(0L);
        user.status = SBUser.Status.ACTIVE;
        user.investor = true;
        user.lastLoggedIn = new Date();
        users.add(user);
        ANGEL = DtoToVoConverter.convert(user);
        return users;
    }

    public List<SBUser> createStartuplyUsers() {
        List<SBUser> users = new ArrayList<SBUser>();
        SBUser user = new SBUser();
        user.id = id();
        user.mockData = true;
        user.admin = true;
        user.nickname = "Startuply";
        user.name = "Startus Uplius";
        user.email = STARTUPLY_EMAIL;
        user.openId = user.email;
        user.joined = new Date(0L);
        user.status = SBUser.Status.ACTIVE;
        user.investor = true;
        user.lastLoggedIn = new Date();
        users.add(user);
        STARTUPLY = DtoToVoConverter.convert(user);
        return users;
    }
	/**
	 * Generates mock listings
	 */
	public List<Listing> createMockListings(List<SBUser> users) {
		/**
		 * Users: GREG, JOHN, AHMED, JACOB, INSIDER, MADMAX, THEONE, DRAGON;
		 */
		List<Listing> listings = new ArrayList<Listing>();

		Listing bp = prepareListing(INSIDER, " Indian courier service", Listing.State.NEW, "Other", 35000, 20,
				"Our couriers are the best", "India has one of the largest deaf populations in the world, but social stigmas have eliminated many job opportunities for the roughly 6 percent of the population that is affected. Aiming to empower this isolated group economically while tapping into a growth market, Mirakle Couriers is a messenger service that hires only deaf workers. The company puts a heavy emphasis on the training of employees - right down to the finer points of personal grooming.");
		listings.add(bp); // 0
		
		bp = prepareListing(JACOB, "Web Video", Listing.State.NEW, "Media", 50000, 40,
				"It'll be your tube", "The success of several videos on YouTube has led to marketers thinking of online videos as a social media marketing tool.  Businesses, however, lack the skills and capabilities of producing quality videos.  If you are great at making videos, this is one service that you can offer to companies today!");
		listings.add(bp); // 1
		
		bp = prepareListing(AHMED, "Pack your fluids", Listing.State.NEW, "Medical", 48000, 35,
				"Targeting travellers, beauty retailer finds a niche in 3 fluid ounces", "Ever since authorities placed rigorous limits on liquids allowed on flights, travellers have had to figure out how to both pack their favourite toiletries and comply with those regulations. Helping consumers avoid bag-check charges or confiscation of their toiletries and cosmetics, 3floz sells beauty and grooming products in TSA-approved sizes only.");
		listings.add(bp); // 2
		
		bp = prepareListing(JOHN, "Eyewear for $99", Listing.State.NEW, "Medical", 48000, 35,
				"Buy-one-give-one indie eyewear sells for $99 per pair", "The market for prescription eyewear has traditionally been dominated by high prices, little innovation and a few large competitors. That's why we've seen online discounters emerge, and it's also why Warby Parker has set its sights on the industry - so to speak - with a paradigm-busting model that aims to combine independent design, \"buy one, give one\" generosity and some long-overdue pricing transparency.");
		listings.add(bp); // 3
		
		bp = prepareListing(THEONE, "Panty by Post", Listing.State.POSTED, "Other", 35000, 20,
				"Luxury women's panties by curated subscription", "Panty by Post is a Canadian venture that offers a selection of women's underwear by monthly subscription. Customers can order panties individually, or they can sign up for subscriptions lasting two, three, six or 12 months. A different panty is then sent every month, each wrapped in an attractive mailing package. It's a great example of subscription-based retail, offering curation alongside convenience. One to apply to a category you're passionate about.");
		listings.add(bp); // 4
		
		bp = prepareListing(MADMAX, "Computer Upgrading Service", Listing.State.POSTED, "Hardware", 35000, 33,
				"If it could be upgraded we'll do that", "Starting a business that specializes in upgrading existing computer systems with new internal and external equipment is a terrific homebased business to initiate that has great potential to earn an outstanding income for the operator of the business. A computer upgrading service is a very easy business to get rolling, providing you have the skills and equipment necessary to complete upgrading tasks, such as installing more memory into the hard drive, replacing a hard drive, or adding a new disk drive to the computer system. Ideally, to secure the most profitable segment of the potential market, the service should specialize in upgrading business computers as there are many reasons why a business would upgrade a computer system as opposed to replacing the computer system. Additionally, managing the business from a homebased location while providing clients with a mobile service is the best way to keep operating overheads minimized and potentially increases the size of the target market by expanding the service area, due to the fact the business operates on a mobile format.");
		listings.add(bp); // 5
		
		bp = prepareListing(DRAGON, "MisLead", Listing.State.ACTIVE, "Industrial", 20000, 25,
				"Misleading is our mantra", "Executive summary for <b>MisLead</b>");
		listings.add(bp); // 6
	
		bp = prepareListing(THEONE, "Computer Training Camp", Listing.State.ACTIVE, "Other", 0, 0,
				"We'll train even lammers", "Starting a computer training camp for children is a terrific new business venture to set in motion. In spite of the fact that many children now receive computer training in school, attending computer camps ensures parents and children a better and more complete understanding of the course material. The computer camps can be operated on a year-round basis or in the summer only. Typically, these camps are one or two days in length and available for various training needs, from beginner to advanced. Once again, this is the kind of children's business that can be operated as an independent business venture or operated in conjunction with a community program or community center.");
		listings.add(bp); // 7

		bp = prepareListing(INSIDER, "Semantic Search", Listing.State.ACTIVE, "Software", 40000, 12,
				"Search is our life", "The fact of the matter is Google, and to a much lesser extent Bing, own the search market. Ask Barry Diller, if you don't believe us. Yet, startups still spring up hoping to disrupt the incumbents. Cuil flopped. Wolfram Alpha is irrelevant. Powerset, which was a semantic search engine was bailed out by Microsoft, which acquired it.");
		listings.add(bp); // 8

		bp = prepareListing(INSIDER, "Social recommendations", Listing.State.ACTIVE, "Software", 0, 0,
				"Learn from other people's mistakes", "It's a very tempting idea. Collect data from people about their tastes and preferences. Then use that data to create recommendations for others. Or, use that data to create recommendations for the people that filled in the information. It doesn't work. The latest to try is Hunch and Get Glue. Hunch is pivoting towards non-consumer-facing white label business. Get Glue has had some success of late, but it's hardly a breakout business.");
		listings.add(bp); // 9

		bp = prepareListing(INSIDER, "Local news sites", Listing.State.ACTIVE, "Media", 49000, 20,
				"Better than free newspaper", "Maybe Tim Armstrong, AOL, and Patch will prove it wrong, but to this point nobody has been able to crack the local news market and make a sustainable business. In theory creating a network of local news sites that people care about is a good idea. You build a community, there's a baked in advertising group with local businesses, and classifieds. But, it appears to be too niche to scale into a big business.");
		listings.add(bp); // 10

		bp = prepareListing(AHMED, "Micropayments", Listing.State.ACTIVE, "Financial", 5000, 49,
				"We can trasfer even a penny", "Micropayments are one idea that's tossed around to solve the problem of paying for content on the Web. If you want to read a New York Times story it would only cost a nickel! Or on Tumblr, if you want to tip a blogger or pay for a small design you could with ease. So far, these micropayment plans have not worked.");
		listings.add(bp); // 11

		bp = prepareListing(AHMED, "De Vegetarische Slager", Listing.State.ACTIVE, "Chemical", 0, 0,
				"Substitution is our answer", "De Vegetarische Slager - the vegetarian butcher - opened a store in The Hague that's dedicated to meat substitutes in the same way a butcher is dedicated to meat. The company's main innovation is its own line of lupin-based, protein-rich products, developed by a Dutch team of scientists and chefs. De Vegetarische Slager is targeting the higher end of the market - consumers willing to pay as much for a meat substitute as they would for the real thing. As more people opt for meatless Mondays or cut out meat altogether, we wouldn't be surprised to see vegetarian butchers pop up on main streets around the world.");
		listings.add(bp); // 12
		
		bp = prepareListing(GREG, "Flight twitter", Listing.State.ACTIVE, "Software", 40000, 50,
				"Twitt a flight", "DJs, promoters, label reps and ‘professional party people' from the Netherlands have persuaded Dutch airline KLM to add an extra flight to its roster. In a new twist on crowd-buying, the initiators of Fly2Miami made a bet with KLM on Twitter to organize a non-stop flight from Amsterdam to Miami. Crowd clout and group buying - turbo-charged by social media - provide companies across industries with new opportunities to empower consumers while improving their bottom line or, at the very least, their brand image.");
		listings.add(bp); // 13
		
		bp = prepareListing(GREG, "Village rainwater", Listing.State.ACTIVE, "Environmental", 0, 0,
				"Village rainwater harvesting system stores enough for a year", "Akash Ganga, or River from the Sky, is a sustainable system that channels rooftop rainwater from every house in a village through gutters, and then pipes it to a network of multitier, underground reservoirs. Currently implemented in six drought-prone villages in the Churu District of Rajasthan, the system captures enough rainwater to meet the drinking needs of an entire village for 12 months. Akash Ganga currently supplies some 10,000 people with fresh water.");
		listings.add(bp); // 14

		bp = prepareListing(JOHN, "Better company car", Listing.State.ACTIVE, "Manufacturing", 250000, 30,
				"We always do better", "Considering how frustrated people are with car companies, you'd think launching a new one would be perfect for a startup. So far, that's not the case. You can point to Tesla as a success, and considering it IPO'd it's hard to argue against it. But, Tesla has sold fewer than 2,000 cars since it was founded in 2003. It's far from certain it will succeed. Even when its next car comes out, Nissan could be making a luxury electric car that competes with Tesla.");
		listings.add(bp); // 15

		bp = prepareListing(JOHN, "On-the-fly conference", Listing.State.ACTIVE, "Other", 35000, 20,
				"Mobile app for group texting and on-the-fly conference calls", "Available for both iPhone and Android, GroupMe is a free tool from New York-based Mindless Dribble that gives groups of friends private text messaging and instant conference calls. As many as 25 people can be included in a group at any one time, but users can create as many groups as they want - one for their basketball team, one for coordinating a surprise party, one for the PTA, one for updating family members while travelling, etc.");
		listings.add(bp); // 16
		
		bp = prepareListing(MADMAX, "Energy Consultant", Listing.State.FROZEN, "Environmental", 35000, 20,
				"We'll help you to save money", "With the increased focus on energy conservation, people have started thinking about how much energy they are using and how it can be used efficiently.  Since energy conservation and efficient usage also bring in savings in monthly expenditure, both businesses as well as households are now more interested than ever in hiring a consultant to audit their energy requirements and suggest changes for increasing energy efficiency.");
		listings.add(bp); // 17
		
		bp = prepareListing(JOHN, "Mobile garage", Listing.State.WITHDRAWN, "Manufacturing", 35000, 20,
				"Mobile garage makes any car greener", "Colorado-based Green Garage specializes in \"green-tuning\" cars to run cleaner, greener and cheaper through sustainable, energy-saving automotive maintenance and repair products. The full-service company begins by bringing the garage to the customer's front door with a valet service whereby it picks up the car, green-tunes it and then drops it off again. Given where the automotive industry began on the sustainability spectrum, it seems safe to say there's plenty of room for improvement, and that's just what we're beginning to see.");
		listings.add(bp); // 18
		
		bp = prepareListing(JOHN, "Plan Retirement", Listing.State.CLOSED, "Financial", 35000, 20,
				"We'll plan your retirement", "People who retire need advice in terms of managing their finances, savings and contingency planning.  With lots of people retiring, a Retirement Planning business can be an ideal opportunity for someone who is good at managing personal finances.");
		listings.add(bp); // 19

		bp = prepareListing(MADMAX, "Acupuncture", Listing.State.CLOSED, "Healthcare", 10000, 40,
				"You'll feel better and more relaxed", "Acupuncture is one of the alternative treatment methodologies that have become popular as a solution to several health problem.  One needs to have a license to practice as an Acupuncturist.  A licensed Acupuncturist can start own practice or get associated with fitness centers which offer Acupuncture as one of their services.");
		listings.add(bp); // 20

		bp = prepareListing(JENNY, "PartyFinder", Listing.State.ACTIVE, "Internet", 20000, 5,
				"Life is a party, so party on", "You've got some free time tonight, but you don't have anything planned.  You could go check in on each of your bars to see what your friends are doing, you could try and instant message everyone, you could see what's happening on facebook and hope someone checks it.  Or you can use PartyFinder.  Simply post where you're going to be and when, and all your closest friends are sent an SMS on where and when the action's at.  It's the best way to get a party going.");
		listings.add(bp); // 21

		bp = prepareListing(BURNTSKY, "Tabify", Listing.State.ACTIVE, "Retail", 100000, 10,
				"Condensing your bookshelf onto your tablet", "Everyone's got that bookshelf they haven't touched in ages.  You don't want to just toss them out, you know you'll get nothing for them at the used bookstore, and besides, you might want to look at them again someday.  But your cramped apartment is getting moreso by the day and you need space.  That's where Tabletify saves the day.  We take all your books, digitize them in full color high resolution down to the last page, and load it onto a USB stick.  Using a patented process we then load it via our app onto your tablet.  Viola, you've got everything you had in a fraction of the space.  And with our Iron Mountain archival service, you'll have all those books for generations to come.  Load it today, load it with Tabify.");
		listings.add(bp); // 22

		bp = prepareListing(EMPEROR, "MysticTea", Listing.State.ACTIVE, "Pharma", 150000, 15,
				"Ancient Chinese secrets brought to your drugstore shelf", "Everyone has heard of accupuncture by now, but not many know the biggest secret of ancient Chinese medicine, the Mystical Tea.  There are actually 12 mystical teas, each one aiding a specific part of the body, bringing comfort and relief in a natural, non-toxic way.  We take this ancient wisdom and package it into a convenient, customer-friendly one-dose application that is sold in drugstores across China.  Now we're bringing this secret to the West.");
		listings.add(bp); // 23

		bp = prepareListing(EMPEROR, "New Century Wines", Listing.State.ACTIVE, "Retail", 40000, 5,
				"The best of European wines in the New China", "We've all heard that the Chinese economy is growing in leaps and bounds.  What you probably don't know, however, is how hard it is to get a good bottle of wine in Beijing.  But we're changing all that, bringing the best of France, Italy, and more to the finest restaurants and shops in China.");
		listings.add(bp); // 24

		return listings;
	}

    public List<Listing> createAngelListListings(List<SBUser> users, String fromId, String toId) {
        List<Listing> listings = new ArrayList<Listing>();
        List<Listing> bps = getAngelListings(fromId, toId);
        for (Listing bpl : bps) {
            listings.add(bpl);
        }
        return listings;
    }

    public List<Listing> createStartuplyListings(List<SBUser> users, int fromId, int toId) {
        List<Listing> listings = new ArrayList<Listing>();
        List<Listing> bps = getStartuplyListings(fromId, toId);
        for (Listing bpl : bps) {
            listings.add(bpl);
        }
        return listings;
    }
    private List<String> fillAngelIds(String fromId, String toId) {
        /*
        String[] presetIds = {
            "6702"
                    ,"409", "19169", "19163", "19164", "19165", "19166", "19167", "19168", "19162", "19160",
                    "19170", "19171", "19174", "19175", "19176", "19177", "19178", "19179",
                    "19181", "19182", "19184", "19185",
                    "19150", "19151", "19152", "19153", "19154", "19155", "19156", "19158", "19159",
                    "1910", "1911", "19149", "19147", "19146", "19145", "19143", "19139",
                    "19138", "19137", "19136", "19134", "19132", "19130",
                    "19000",
                    "19002",
                    "19005",
                    "19006",
                    "19008",
                    "19010",
                    "19013",
                    "19015",
                    "19017",
                    "19020",
                    "19021",
                    "19022",
                    "19023",
                    "19024",
                    "19025",
                    "19027",
                    "19030",
                    "19033",
                    "19034",
                    "19035",
                    "19037",
                    "19039",
                    "19040",
                    "19041",
                    "19042",
                    "19043",
                    "19044",
                    "19046",
                    "19048",
                    "19050",
                    "19052",
                    "19053",
                    "19054",
                    "19056",
                    "19057",
                    "19059",
                    "19061",
                    "19063",
                    "19064",
                    "19065",
                    "19068",
                    "19070",
                    "19071",
                    "19073",
                    "19075",
                    "19076",
                    "19077",
                    "19078",
                    "19080",
                    "19083",
                    "19085",
                    "19087",
                    "19090",
                    "19091",
                    "19092",
                    "19093",
                    "19096",
                    "19097",
                    "19098",
                    "19099",
                    "19101",
                    "19105",
                    "19106",
                    "19110",
                    "19111",
                    "19112",
                    "19113",
                    "19115",
                    "19116",
                    "19117",
                    "19118",
                    "19119",
                    "19120",
                    "19122",
                    "19125",
                    "19126",
                    "19127",
                    "19128",
                    "19129"
        };
        int min = 18500;
        int max = 18800;
        int max = 19200;
        */
        int min = Integer.parseInt(fromId);
        int max = Integer.parseInt(toId);
        List<String> angelIds = new ArrayList<String>();
        for (int i = min; i <= max; i++) {
            angelIds.add(String.valueOf(i));    
        }
        return angelIds;
    }

    private String bestGuessListingType(String name, String mantra, String desc) {
        String listingType = null;
        String[] types = {
            "Biotech",
            "Chemical",
            "Electronics",
            "Energy",
            "Environmental",
            "Financial",
            "Hardware",
            "Healthcare",
            "Industrial",
            "Internet",
            "Manufacturing",
            "Media",
            "Medical",
            "Pharma",
            "Retail",
            "Software",
            "Telecom"
        };
        for (String type : types) {
            if (name.contains(type)) {
                listingType = type;
                break;
            }
        }
        if (listingType == null) {
            for (String type: types) {
                if (mantra != null && mantra.contains(type)) {
                    listingType = type;
                    break;
                }
            }
        }
        if (listingType == null) {
            for (String type: types) {
                if (desc != null && desc.contains(type)) {
                    listingType = type;
                    break;
                }
            }
        }
        if (listingType == null) {
            listingType = "Other";
        }
        return listingType;
    }

    public String deleteAngelListCache(UserVO loggedInUser, String fromId, String toId) {
        if (loggedInUser == null || !loggedInUser.isAdmin()) {
            log.log(Level.WARNING, "User '" + loggedInUser + "' is not an admin.");
            return "User '" + loggedInUser + "' is not an admin.";
        }
        StringBuffer output = new StringBuffer();
        if (StringUtils.isEmpty(fromId) || StringUtils.isEmpty(toId)) {
            QueryResultIterable<Key<AngelListCache>> a = getOfy().query(AngelListCache.class).fetchKeys();
            output.append("Deleted AngelList Cache: " + a.toString() + "</br>");
            getOfy().delete(a);
        }
        else {
            List<String> angelIds = fillAngelIds(fromId, toId);
            for (String angelId : angelIds) {
                String angelPath = ANGEL_STARTUP_API_ROOT + angelId;
                try {
                    AngelListCache angelListCache = getOfy().get(AngelListCache.class, angelPath);
                    getOfy().delete(angelListCache);
                    output.append("Deleted AngelList Cache id: " + angelId);
                }
                catch (NotFoundException e) {
                    ;
                }
            }
        }
        return output.toString();
    }

    public String deleteGeocodeCache(UserVO loggedInUser) {
        if (loggedInUser == null || !loggedInUser.isAdmin()) {
            log.log(Level.WARNING, "User '" + loggedInUser + "' is not an admin.");
            return "User '" + loggedInUser + "' is not an admin.";
        }
        StringBuffer output = new StringBuffer();
        QueryResultIterable<Key<GeocodeLocation>> a = getOfy().query(GeocodeLocation.class).fetchKeys();
        output.append("Deleted Geocode Cache: " + a.toString() + "</br>");
        getOfy().delete(a);
        return output.toString();
    }

    public static final String ANGEL_STARTUP_API_ROOT = "http://api.angel.co/1/startups/";

    private List<Listing> getAngelListings(String fromId, String toId) {
        List<Listing> listings = new ArrayList<Listing>();
        List<String> angelIds = fillAngelIds(fromId, toId);
 
        try {
            Objectify ofy = getOfy();
            for (String angelId : angelIds) {
                String angelPath = ANGEL_STARTUP_API_ROOT + angelId;
                AngelListCache angelListCache = null;
                try {
                    angelListCache = ofy.get(AngelListCache.class, angelPath);
                }
                catch (NotFoundException e) {
                    ;
                }
                if (angelListCache == null) {
                    try {
                        URL url = new URL(angelPath);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoOutput(true);
                        connection.setRequestMethod("GET");
                        StringWriter stringWriter = new StringWriter();
                        IOUtils.copy(connection.getInputStream(), stringWriter, "UTF-8");
                        String angelJson = stringWriter.toString();
                        connection.disconnect();
                        angelListCache = new AngelListCache(angelPath, angelJson);
                        ofy.put(angelListCache);
                    }
                    catch (Exception e) {
                        log.log(Level.WARNING, "Exception while importing AngelList startup: " + angelId, e);
                    }
                }
                if (angelListCache == null) {
                    log.info("Could not load AngelList cache for id: " + angelId);
                }
                else if (StringUtils.isEmpty(angelListCache.json)) {
                    log.info("Unable to import, empty response for AngelList cache for id: " + angelId);
                }
                else if (!angelListCache.json.contains("\"hidden\":false")) {
                    log.info("Unable to import, matching tag not found in AngelList cache for id: " + angelId + " json: " + angelListCache.json);
                }
                else {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        ObjectReader reader = mapper.reader(AngelListing.class);
                        AngelListing result = reader.readValue(angelListCache.json);
                        //SBUser user = users.get(RandomUtils.nextInt(users.size()));
                        String type = bestGuessListingType(result.name, result.high_concept, result.product_desc);
                        int askamt = RandomUtils.nextInt(100)*1000;
                        int askpct = 5 + 5*RandomUtils.nextInt(9);
                        DateTime createdAt = new DateTime(result.created_at);
                        DateTime modifiedAt = new DateTime(result.updated_at);
                        String address = null;
                        if (result.locations != null && result.locations.size() > 0) {
                            AngelListing.AngelLocation loc = result.locations.get(0);
                            address = loc.name;
                        }
                        Listing listing = prepareListing(
                            ANGEL, // DtoToVoConverter.convert(user),
                            result.name,
                            Listing.State.ACTIVE,
                            type,
                            askamt,
                            askpct,
                            result.high_concept,
                            result.product_desc,
                            result.company_url,
                            createdAt,
                            modifiedAt,
                            result.logo_url,
                            address
                        );
                        listings.add(listing);
                        log.info("Added AngelList listing: " + listing);
                    }
                    catch (Exception e) {
                        log.info("Exception while importing AngelList startup: " + angelId);
                        e.printStackTrace();
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return listings;
    }

    public static final String STARTUPLY_ROOT = "http://www.startuply.com";

    private List<String> getStartuplyIds() {
        List<String> startuplyIds = new ArrayList<String>();
        String startuplyPath = STARTUPLY_ROOT + "/Startups/";
        try {
            StartuplyCache startuplyCache = null;
            try {
                startuplyCache = getOfy().get(StartuplyCache.class, startuplyPath);
            }
            catch (NotFoundException e) {
                ;
            }
            if (startuplyCache == null) {
                URL url = new URL(startuplyPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setDoOutput(true);
                connection.setRequestMethod("GET");
                StringWriter stringWriter = new StringWriter();
                IOUtils.copy(connection.getInputStream(), stringWriter, "UTF-8");
                String startuplyPage = stringWriter.toString();
                connection.disconnect();
                if (!StringUtils.isEmpty(startuplyPage)) {
                    startuplyCache = new StartuplyCache(startuplyPath, startuplyPage);
                    //getOfy().put(startuplyCache);       // too large for google
                }
                else {
                    throw new Exception("Could not load startuply main page: "+startuplyPage);
                }
            }
            Pattern p = Pattern.compile("<a href=\"/Companies/([^.]*)[.]aspx\">([^\\<])*</a>");
            Matcher m = p.matcher(startuplyCache.page);
            while (m.find()) {
                String startuplyId = m.group(1);
                startuplyIds.add(startuplyId);
                //String startuplyName = m.group(2);
                //log.info("StartuplyId: " + startuplyId + " name: " + startuplyName);
            }            
        }
        catch (Exception e) {
            log.log(Level.WARNING, "Exception while importing Startuply startups", e);
        }    
        return startuplyIds;
    }
    
    private List<Listing> getStartuplyListings(int fromId, int toId) {
        List<Listing> listings = new ArrayList<Listing>();
        List<String> startuplyIds = getStartuplyIds();
        log.info("Loading " + startuplyIds.size() + " Startuply listings");
        
        try {
            Pattern namePattern = Pattern.compile("<h1 id=\"companyNameHeader\"[^>]*>([^<]*)", Pattern.MULTILINE);
            Pattern websitePattern = Pattern.compile("<a href=\"http://([^\"]*)\">\\1", Pattern.MULTILINE);
            Pattern addressPattern = Pattern.compile("<table id=\"branchTable\".*<td class=\"SortedColumn\">[^<]*</td>\\s*<td[^>]*>([^<]*)", Pattern.MULTILINE | Pattern.DOTALL);
            Pattern logoPattern = Pattern.compile("<img\\s+src=\"([^\"]*)\"\\s+id=\"ctl00_Content_Logo\"", Pattern.MULTILINE);
            Pattern industriesPattern = Pattern.compile("<div[^>]*>Industries</div>\\s*<div[^>]*>\\s*<a[^>]*>([^<]*)</a>\\s*", Pattern.MULTILINE);
            Pattern industries2Pattern = Pattern.compile("\\s*,\\s*<a[^>]*>([^<]*)</a>", Pattern.MULTILINE);
            Pattern missionPattern = Pattern.compile("<h1[^>]*>[^<]* Mission</h1>\\s*<div[^>]*>\\s*([^<]*)", Pattern.MULTILINE);
            Pattern mantraPattern = Pattern.compile("([^\\.]+)");
            Pattern productsPattern = Pattern.compile("<h1[^>]*>[^<]* Products</h1>\\s*<div[^>]*>\\s*([^<]*)", Pattern.MULTILINE);
            Pattern teamPattern = Pattern.compile("<h1[^>]*>[^<]* Team</h1>\\s*<div[^>]*>\\s*([^<]*)", Pattern.MULTILINE);
            Pattern lifePattern = Pattern.compile("<h1[^>]*>Life [^<]</h1>\\s*<div[^>]*>\\s*([^<]*)", Pattern.MULTILINE);
            int counter = 0;
            for (String startuplyId : startuplyIds) {
                counter++;
                if (counter <= fromId || counter >= toId) {
                    continue;
                }

                String startuplyPath = STARTUPLY_ROOT + "/Companies/" + startuplyId + ".aspx";
                StartuplyCache startuplyCache = null;
                try {
                    startuplyCache = getOfy().get(StartuplyCache.class, startuplyPath);
                }
                catch (NotFoundException e) {
                    ;
                }
                if (startuplyCache == null) {
                    try {
                        URL url = new URL(startuplyPath);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoOutput(true);
                        connection.setRequestMethod("GET");
                        StringWriter stringWriter = new StringWriter();
                        IOUtils.copy(connection.getInputStream(), stringWriter, "UTF-8");
                        String startuplyPage = stringWriter.toString();
                        connection.disconnect();
                        if (!StringUtils.isEmpty(startuplyPage)) {
                            startuplyPage = startuplyPage.replaceAll("<br />", ""); // messes up multiline desc if we don't do this
                            startuplyCache = new StartuplyCache(startuplyPath, startuplyPage);
                            getOfy().put(startuplyCache);
                        }
                    }
                    catch (Exception e) {
                        log.log(Level.WARNING, "Exception while importing Startuply startup: "+startuplyId, e);
                    }
                }
                if (startuplyCache == null) {
                    log.info("Could not load Startuply cache for id: " + startuplyId);
                }
                else if (StringUtils.isEmpty(startuplyCache.page)) {
                    log.info("Unable to import, empty response for Startuply cache page for id: " + startuplyId);
                }
                else {
                    try {
                        //log.info(startuplyCache.page);
                        String name = "";
                        Matcher nameMatcher = namePattern.matcher(startuplyCache.page);
                        if (nameMatcher.find()) {
                            name = nameMatcher.group(1);
                        }
                        String address = "";
                        Matcher addressMatcher = addressPattern.matcher(startuplyCache.page);
                        if (addressMatcher.find()) {
                            address = addressMatcher.group(1);
                        }
                        String website = "";
                        Matcher websiteMatcher = websitePattern.matcher(startuplyCache.page);
                        if (websiteMatcher.find()) {
                            website = "http://" + websiteMatcher.group(1);
                        }
                        String logo = "";
                        Matcher logoMatcher = logoPattern.matcher(startuplyCache.page);
                        if (logoMatcher.find()) {
                            logo = STARTUPLY_ROOT + logoMatcher.group(1);
                        }
                        String industries = "";
                        Matcher industriesMatcher = industriesPattern.matcher(startuplyCache.page);
                        if (industriesMatcher.find()) {
                            industries = industriesMatcher.group(1);
                            industriesMatcher.usePattern(industries2Pattern);
                            while (industriesMatcher.find()) {
                                industries += " " + industriesMatcher.group(1);
                            }
                        }
                        String description = "";
                        String mantra = "";
                        Matcher missionMatcher = missionPattern.matcher(startuplyCache.page);
                        if (missionMatcher.find()) {
                            description = missionMatcher.group(1);
                            Matcher mantraMatcher = mantraPattern.matcher(description);
                            if (mantraMatcher.find()) {
                                mantra = mantraMatcher.group(1);
                            }
                        }
                        Matcher productsMatcher = productsPattern.matcher(startuplyCache.page);
                        if (productsMatcher.find()) {
                            String products = productsMatcher.group(1);
                            description += " " + products;
                        }
                        Matcher teamMatcher = teamPattern.matcher(startuplyCache.page);
                        if (teamMatcher.find()) {
                            String team = teamMatcher.group(1);
                            description += " " + team;
                        }                        
                        Matcher lifeMatcher = lifePattern.matcher(startuplyCache.page);
                        if (lifeMatcher.find()) {
                            String life = lifeMatcher.group(1);
                            description += " " + life;
                        }

                        if (StringUtils.isEmpty(name)) {
                            log.info("Unable to import, couldn't find name for Startuply id: " + startuplyId);
                        }
                        else {
                            String type = bestGuessListingType(name, industries, description);
                            //log.info("Matched name:[" + name + "] address:["+address + "] website:["+website + "] logo:["+logo + "] industries:["+industries+"] mantra:["+mantra+"] description:["+description+"]");
                            int askamt = 5*RandomUtils.nextInt(20)*1000;
                            int askpct = 5 + 5*RandomUtils.nextInt(9);
                            if (askamt < 10000) {
                                askamt = 0;
                            }
                            if (StringUtils.isEmpty(mantra)) {
                                mantra = industries;
                            }
                            if (StringUtils.isEmpty(description)) {
                                description = "In summary, " + name + " is a great company in the " + industries + " space.";
                            }
                            Listing listing = prepareListing(
                                    STARTUPLY, // DtoToVoConverter.convert(user),
                                    name,
                                    Listing.State.ACTIVE,
                                    type,
                                    askamt,
                                    askpct,
                                    mantra,
                                    description,
                                    website,
                                    null,
                                    null,
                                    logo,
                                    address
                            );
                            listings.add(listing);
                            //log.info("Added Startuply listing: "+listing);
                            log.info("Added Startuply listing " + counter + " of " + startuplyIds.size() + " name: " + name);
                        }
                    }
                    catch (Exception e) {
                        log.log(Level.WARNING, "Exception while importing Startuply startup: " + startuplyId, e);
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return listings;
    }

    private Listing prepareListing(UserVO owner, String name, Listing.State state, String category, int amount, int percentage,
            String mantra, String summary) {
        return prepareListing(owner, name, state, category, amount, percentage, mantra, summary, null, null, null, null, null);
    }

    private Listing prepareListing(UserVO owner, String name, Listing.State state, String category, int amount, int percentage,
            String mantra, String summary, String companyUrl, DateTime createdAt, DateTime modifiedAt, String logo_url, String address) {
		Listing bp = new Listing();
		bp.id = id();
		bp.name = name;
		bp.summary = summary;
		bp.mantra = mantra;
		bp.owner = new Key<SBUser>(SBUser.class, owner.toKeyId());
		bp.contactEmail = owner.getEmail();
		bp.founders = getFounders(owner.getName());

		bp.askedForFunding = amount > 0;
		bp.suggestedAmount = amount;
		bp.suggestedPercentage = percentage;
		
		bp.category = category;
		bp.state = state;

 		int hours = RandomUtils.nextInt(500) + 80;
		DateTime createdTime = createdAt != null ? createdAt : new DateTime().minusHours(hours);
		if (modifiedAt != null) {
            bp.modified = modifiedAt.toDate();
        }

		bp.created = createdTime.toDate();
		switch(state) {
		case NEW:
			break;
		case POSTED:
			bp.posted = createdTime.plusHours(hours * 5 / 10).toDate();
			break;
		case ACTIVE:
		case FROZEN:
			bp.posted = createdTime.plusHours(hours * 7 / 10).toDate();
			bp.listedOn = createdTime.plusHours(hours * 4 / 10).toDate();
			if (bp.askedForFunding) {
				bp.closingOn = createdTime.plusHours(hours * 4 / 10).plusDays(30).toDate();
			}
			break;
		case WITHDRAWN:
			bp.posted = createdTime.plusHours(hours * 8 / 10).toDate();
			bp.listedOn = createdTime.plusHours(hours * 6 / 10).toDate();
			bp.closingOn = createdTime.plusHours(hours * 6 / 10).plusDays(30).toDate();
			break;
		case CLOSED:
			hours = RandomUtils.nextInt(500) + 33 * 24;
			createdTime = new DateTime().minusHours(hours);
			bp.created = createdTime.toDate();
			bp.posted = createdTime.plusHours(24).toDate();
			bp.listedOn = createdTime.plusHours(48).toDate();
			bp.closingOn = createdTime.plusDays(32).toDate();
			break;
		}

        GeocodeLocation location = null;
        if (address != null) {
            location = getGeocodedLocation(address);
            location.randomize(0.1);
        }
        if (location == null) {
            location = getRandomLocation();
            location.randomize(0.001);
        }

		bp.address = location.address;
		bp.city = location.city;
		bp.usState = "USA".equals(location.country) ? location.state : null;
		bp.country = location.country;
		DtoToVoConverter.updateBriefAddress(bp);
		bp.latitude = location.latitude;
		bp.longitude = location.longitude;

		bp.videoUrl = getVideo();

		bp.website = !StringUtils.isEmpty(companyUrl) ? companyUrl : getWebsite();
		
		bp.answer1 = getQuote();
		bp.answer2 = getQuote();
		bp.answer3 = getQuote();
		bp.answer4 = getQuote();
		bp.answer5 = getQuote();
		bp.answer6 = getQuote();
		bp.answer7 = getQuote();
		bp.answer8 = getQuote();
		bp.answer9 = getQuote();
		bp.answer10 = getQuoteWithNulls();
		bp.answer11 = getQuoteWithNulls();
		bp.answer12 = getQuoteWithNulls();
		bp.answer13 = getQuoteWithNulls();
		bp.answer14 = getQuoteWithNulls();
		bp.answer15 = getQuoteWithNulls();
		bp.answer16 = getQuoteWithNulls();
		bp.answer17 = getQuoteWithNulls();
		bp.answer18 = getQuoteWithNulls();
		bp.answer19 = getQuoteWithNulls();
		bp.answer20 = getQuoteWithNulls();
		bp.answer21 = getQuoteWithNulls();
		bp.answer22 = getQuoteWithNulls();
		bp.answer23 = getQuoteWithNulls();
		bp.answer24 = getQuoteWithNulls();
		bp.answer25 = getQuoteWithNulls();
		bp.answer26 = getQuoteWithNulls();
		
        if (!StringUtils.isEmpty(logo_url)) {
            logo_url = logo_url.replaceAll("^https://", "http://");
            logoUrls.put(bp.id, logo_url);
        }

		return bp;
	}
    
    private GeocodeLocation getGeocodedLocation(String address) {
        try {
            return getOfy().get(GeocodeLocation.class, address);
        }
        catch (NotFoundException e) {
        }       
        
        String addressString = geocodeAddress(address);
        if (StringUtils.isEmpty(addressString)) {
            GeocodeLocation location = fallbackLocation(address);
            getOfy().put(location);
            return location;
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readValue(addressString, JsonNode.class);
            Map<String, String> properties = new HashMap<String, String>();

            if (rootNode.get("results") == null || rootNode.get("results").get(0) == null) {
                throw new Exception("no results found in response: "+addressString);
            }
            JsonNode place = rootNode.get("results").get(0);
            JsonNode addressComponents = place.get("address_components");
            if (addressComponents == null) {
                throw new Exception("no address_components found");
            }
            Iterator<JsonNode> elements = addressComponents.getElements();
            for (; elements.hasNext(); ) {
                String[] comp = ListingController.getAddressComponents(elements.next());
                if (comp != null) {
                    // using type and short_name
                    properties.put("SHORT_" + comp[0], comp[1]);
                    // using type and long_name
                    properties.put("LONG_" + comp[0], comp[2]);
                }
            }
            if ("US".equals(properties.get("SHORT_country"))) {
                properties.put("country", "USA");
                String state = properties.get("SHORT_administrative_area_level_1");
                if (state != null) {
                    properties.put("state", state);
                }
            } else {
                properties.put("country", properties.get("LONG_country"));
            }
            JsonNode formattedAddress = place.get("formatted_address");
            if (formattedAddress == null) {
                throw new Exception("no formatted_address found");
            }
            properties.put("formatted_address", formattedAddress.getValueAsText());
            JsonNode geometry = place.get("geometry");
            if (geometry != null && geometry.get("location") != null) {
                Iterator<JsonNode> locationIt = geometry.get("location").getElements();
                String ta = locationIt.next().getValueAsText();
                String ua = locationIt.next().getValueAsText();
                if (StringUtils.isEmpty(ta)) {
                    throw new Exception("no latitude found");
                }
                if (StringUtils.isEmpty(ua)) {
                    throw new Exception("no longitude found");
                }
                properties.put("latitude", ta);
                properties.put("longitude", ua);
            }
            else {
                throw new Exception("no places location property found");
            }
            GeocodeLocation location = new GeocodeLocation(
                    address,
                    properties.get("formatted_address"),
                    properties.get("LONG_locality"),
                    properties.get("state"),
                    properties.get("country"),
                    new Double(properties.get("latitude")),
                    new Double(properties.get("longitude"))
            );
            getOfy().put(location);
            return location;
        }
        catch (Exception e) {
            log.severe(e.getLocalizedMessage());
        }

        GeocodeLocation location = fallbackLocation(address);
        getOfy().put(location);
        return location;
    }
   
    private GeocodeLocation fallbackLocation(String address) {
        String a = address.toLowerCase(Locale.ENGLISH);
        GeocodeLocation l = null;
        if (a.contains("san francisco")) {
            l = new GeocodeLocation(address, "600 Montgomery St, San Francisco, CA, USA", "San Francisco", "CA", "USA", 37.7952, -122.4028);
        }
        else if (a.contains("new york")) {
            l = new GeocodeLocation(address, "18 Broad St, New York, NY, USA", "New York", "NY", "USA", 40.706833, -74.011028);
        }
        else if (a.contains("berlin")) {
            l = new GeocodeLocation(address, "Platz der Republik 1, Berlin, Germany", "Berlin", null, "Germany", 52.5186, 13.376);
        }
        else if (a.contains("los angeles")) {
            l = new GeocodeLocation(address, "200 Getty Center Drive  Los Angeles, CA, USA", "Los Angeles", "CA", "USA", 34.0775, -118.475);
        }
        else if (a.contains("boston")) {
            l = new GeocodeLocation(address, "9 Oxford Street, Cambridge, MA, USA", "Cambridge", "MA", "USA", 42.374444, -71.116944);
        }
        else if (a.contains("austin")) {
            l = new GeocodeLocation(address, "1100 Congress St, Austin, TX, USA", "Austin", "TX", "USA", 30.274722, -97.740556);
        }
        else {
            l = new GeocodeLocation(address, "Threadneedle St, London EC2R, UK", "London", null, "United Kingdom", 51.51406, -0.08839);
        }
        return l;
    }

    private boolean googleMapsAPIOverCapacity = false;
    
    private String geocodeAddress(String address) {
        String json = null;
        if (googleMapsAPIOverCapacity) {
            return json;
        }
        try {
            //String apiPath = "http://maps.googleapis.com/maps/api/geocode/json?language=en-GB&sensor=false&key=AIzaSyCZxuJD5cknHJ4ygggT-YPLJtIycTP76EY&address=" + URLEncoder.encode(address, "UTF-8");
            String apiPath = "http://maps.googleapis.com/maps/api/geocode/json?language=en-GB&sensor=false&address=" + URLEncoder.encode(address, "UTF-8");
            URL url = new URL(apiPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            StringWriter stringWriter = new StringWriter();
            IOUtils.copy(connection.getInputStream(), stringWriter, "UTF-8");
            String jsonInput = stringWriter.toString();
            connection.disconnect();
            if (jsonInput.contains("\"status\" : \"OVER_QUERY_LIMIT\"")) {
                googleMapsAPIOverCapacity = true;
                log.info("Google Maps API over capacity, shutting down future geocode requests");
                return null;
            }
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                json = jsonInput;
            }
        }
        catch(Exception e) {
            log.log(Level.WARNING, "Could not geocode address " + address, e);
        }
        return json;
    }
    
	private Object[][] mockLocations = {
			{"Lohstra\u00DFe 53, 49074 Osnabr\u00FCck, Germany","Osnabr\u00FCck",null,"Germany", 52.27913570, 8.041329399999995},
			{"Fellenoord 310, 5611 Centrum, The Netherlands","Eindhoven",null,"The Netherlands", 51.44266050, 5.472869100000025},
			{"10 Rue de Passy, 75016 Paris, France","Paris",null,"France", 48.85842180, 2.283493599999929},
			{"Calle del Pintor Cabrera, 29, 03003 Alicante, Spain","Alicante",null,"Spain", 38.34388360, -0.49293009999996684},
			{"Via Amerigo Vespucci 10, 80142 Naples, Italy","Naples",null,"Italy", 40.84653330, 14.270270100000062},
			{"45 L St SW, Washington, DC 20024, USA","Washington","DC","USA", 38.8779990, -77.01035999999999},
			{"671 John F Kennedy Blvd W, Bayonne, NJ 07002, USA","Bayonne","NJ","USA", 40.66865079999999, -74.12083719999998},
			{"273 High St, Perth Amboy, NJ 08861, USA","Perth Amboy","NJ","USA", 40.50690090, -74.26577320000001},
			{"1501 E 22nd St, Los Angeles, CA 90011, USA","Los Angeles","CA","USA", 34.01871330, -118.2481563},
			{"CST Rd, Mumbai, Maharashtra, India","Mumbai","MH","India", 19.07141140, 72.86405230000003},
			{"585\u53F7 Lingling Rd, Xuhui, Shanghai, China, 200030","Shanghai",null,"China", 31.1882940, 121.447633},
			{"Denenchofu, Ota, Tokyo, Japan","Tokyo",null,"Japan", 35.58862750, 139.6735731},
			{"86-108 Castlereagh St, Sydney NSW 2000, Australia","Sydney",null,"Australia", -33.870950, 151.2117541},
			{"R. Santos Lima, 86 - S\u00E3o Crist\u00F3v\u00E3o, Rio de Janeiro, 20940-210, Brazil","Rio de Janeiro",null,"Brazil", -22.89713230, -43.217189899999994},
			{"Paddington, City of Westminster, London W2, UK", "London", null,"United Kingdom",51.516516,-0.18091100000003735},
			{"Dublin Ave, London Borough of Hackney, London E8 4TP, UK","London",null,"United Kingdom",51.5382553,-0.06351169999993544},
			{"Suites, Gordon 39, Tel Aviv, Israel","Tel Aviv",null,"Israel",32.0818919,34.77371649999998},
			{"Tel Aviv, Israel","Tel Aviv",null,"Israel",32.066157,34.7778210000000},
			{"Toronto Division, ON, Canada","Toronto",null,"Canada",43.6689775,-79.2902133},
			{"S Bridge Rd, Singapore","Singapore",null,"Singapore",1.2850154,103.84689030000004},
			{"Red Square, Moscow, Russia","Moscow",null,"Russian Federation",55.75463449999999,37.62149550000004},
			{"Berlin, Germany","Berlin",null,"Germany",52.519171,13.406091199999992},
			{"Calle de Don Luis Bri\u00F1as, 48013 Bilbao, Spain", "Bilbao", null, "Spain", 43.2630245, -2.9474244000000454 },
            {"Shinjuku, Tokyo, Japan", "Tokyo", null, "Japan", 35.6911017, 139.70676300000002 },
            {"Taipei 101, Sinyi District, Taiwan", "Taipei", null, "Taiwan", 25.0334959, 121.5638626 },
            {"5547 Valerie St, Houston, TX 77081, USA", "Houston", "TX", "USA", 29.693061, -95.48842100000002 },
            {"5606 Hazen St, Houston, TX 77081, USA", "Houston", "TX", "USA", 29.691896, -95.48864600000002 }
	};

    private int locationCounter = 0;
    
    List<GeocodeLocation> shuffledLocations = null;
    
	private GeocodeLocation getRandomLocation() {
        if (shuffledLocations == null) {
            List<GeocodeLocation> source = new ArrayList<GeocodeLocation>();
            for (int i = 0; i < mockLocations.length; i++) {
                Object[] l = mockLocations[i];
                source.add(new GeocodeLocation((String)l[0], (String)l[0], (String)l[1], (String)l[2], (String)l[3], (Double)l[4], (Double)l[5]));
            }
            // Fisher-Yates inside-out algorithm
            int n = source.size();
            List<GeocodeLocation> a = new ArrayList<GeocodeLocation>(n);
            for (int i = 0; i < n; i++) {
                a.add(new GeocodeLocation());
            }
            a.set(0, source.get(0));
            for (int i = 1; i < n - 1; i++) {
                int j = RandomUtils.nextInt(i+1);
                a.set(i, a.get(j));
                a.set(j, source.get(i));
            }
            shuffledLocations = a;
        }
        GeocodeLocation location = shuffledLocations.get(locationCounter);
        locationCounter = ++locationCounter % shuffledLocations.size();
        return location;
	}
		
	private String getVideo() {
		return videos[RandomUtils.nextInt(videos.length)];
	}
	
	private String[] videos = {
			"http://www.youtube.com/embed/ufTtT1rKUAk",
			"http://www.youtube.com/embed/qRO38UQGH7A",
			"http://www.youtube.com/embed/1vnDOzPrxxw",
			"http://www.youtube.com/embed/dMkp40_Dr0E",
			"http://www.youtube.com/embed/GVNPbvdW9uA",
			"http://www.youtube.com/embed/08qGjZwj914",
			"http://www.youtube.com/embed/x_hIqmjOwAM",
			"http://www.youtube.com/embed/3WXdxlMwUvk",
			"http://player.vimeo.com/video/36258512?title=0&byline=0&portrait=0",
			"http://player.vimeo.com/video/27973852?title=0&byline=0&portrait=0",
			"http://player.vimeo.com/video/14866982?title=0&byline=0&portrait=0",
			"http://player.vimeo.com/video/20250134?title=0&byline=0&portrait=0",
			"http://player.vimeo.com/video/15060334?title=0&byline=0&portrait=0",
			"http://player.vimeo.com/video/16544905?title=0&byline=0&portrait=0",
            "http://www.youtube.com/embed/kmAjp77ovEo",
            "http://www.youtube.com/watch?v=MnNwzUsne2Y",
            "http://www.youtube.com/embed/wyIlsMjIEW"
//			"http://www.dailymotion.com/video/xe99rh_women-in-the-business-world_lifestyle",
//			"http://www.dailymotion.com/video/xe98f2_small-business-stories-armchair-adv_lifestyle",
//			"http://www.dailymotion.com/video/xe965l_how-to-become-a-financially-indepen_lifestyle",
//			"http://www.dailymotion.com/video/xl9gic_introductions-in-business-meetings_lifestyle"
	};
	
	private String getWebsite() {
		return websites[RandomUtils.nextInt(websites.length)];
	}
	
	private String[] websites = {
			"www.finance.yahoo.com",
			"http://www.money.cnn.com/",
			"http://www.wsj.com/",
			"http://www.google.com/finance",
			"http://www.moneycentral.msn.com/",
			"http://www.businessweek.com/",
			"http://www.bloomberg.com/",
			"http://www.forbes.com/",
			"http://www.marketwatch.com/",
			"http://www.businessinsider.com/",
			"http://www.ft.com/",
			"http://www.cnbc.com/",
			"http://www.fool.com/",
			"http://www.thestreet.com/",
			"http://www.bizjournals.com/",
            "http://www.angel.co/",
            "http://www.ycombinator.com/",
            "http://www.capitalfactory.com/"
	};
	
	public String getBusinessPlan() {
        int r = RandomUtils.nextInt(10);
		if (r < 1) {
			return getTestDataPath() + "refcardz.pdf";
		} else if (r < 2) {
			return getTestDataPath() + "resume.doc";
		} else {
            return null;
        }
	}
	
	public String getFinancials() {
		if (RandomUtils.nextInt(10) < 1) {
			return null;
		} else {
			return getTestDataPath() + "calc.xls";
		}
	}
	
	public String getPresentation() {
		if (RandomUtils.nextInt(10) < 1) {
			return null;
		} else {
			return getTestDataPath() + "business_plan.ppt";
		}
	}
	
	public String getLogo(long listingId) {
        String dataUrl;
        if (logoUrls.containsKey(listingId)) {
            dataUrl = logoUrls.get(listingId);
        }
        else {
            int seed = (int)listingId;
		    dataUrl = getTestDataPath() + logos[seed % logos.length];
        }
        return dataUrl;
	}
	
	private String[] logos = {
			"logo1.jpg",
			"logo2.jpg",
			"logo3.jpg",
			"logo4.jpg",
			"logo5.jpg",
			"logo6.jpg",
			"logo7.jpg",
			"logo8.jpg",
			"logo9.jpg",
			"logo10.jpg",
			"logo11.jpg",
			"logo12.jpg",
			"logo13.jpg",
			"logo14.jpg",
			"logo15.jpg",
			"logo16.jpg",
			"logo17.jpg",
			"logo18.gif",
			"logo19.gif",
			"logo20.jpg"
	};
	
	private String getFounders(String name) {
		if (RandomUtils.nextInt(4) < 2) {
			return name;
		} else {
			return founders[RandomUtils.nextInt(founders.length)];
		}
	}
	
	private String[] founders = {
			"Stan Shih, John Warnock",
			"Jerry Sanders, Ken Olsen",
			"Ross Perot, Paul Galvin",
			"Larry Ellison, Dave Duffield, Al Shugart",
			"Akio Morita",
			"Scott McNeely",
			"Diane Greene",
			"Akiro Kurosawa",
			"Miguel de Cervantes",
			"Yang Chu"
	};
	
	private String getQuote() {
		return quotes[RandomUtils.nextInt(quotes.length)];
	}
	
	private String getQuoteWithNulls() {
		if (RandomUtils.nextInt(4) < 3) {
			return null;
		} else {
			return quotes[RandomUtils.nextInt(quotes.length)];
		}
	}
	
	private String[] quotes = {
			"Imagination is the beginning of creation. You imagine what you desire, you will what you imagine and at last you create what you will.",
			"Being a woman is a terribly difficult task since it consists principally in dealing with men.",
			"As we grow old…the beauty steals inward.",
			"Life is an escalator: You can move forward or backward; you can not remain still.",
			"In all large corporations, there is a pervasive fear that someone, somewhere is having fun with a computer on company time. Networks help alleviate that fear.",
			"Shake off all the fears of servile prejudices, under which weak minds are servilely crouched. Fix reason firmly in her seat, and call on her tribunal for every fact, every opinion. Question with boldness even the existence of a God; because, if there be one, he must more approve of the homage of reason than that of blindfolded fear.",
			"The saying \"Getting there is half the fun\" became obsolete with the advent of commercial airlines.",
			"A runners creed: I will win; if I cannot win, I shall be second; if I cannot be second, I shall be third; if I cannot place at all, I shall still do my best.",
			"Frequency of a tragedy does not diminish the wound when it is your own.",
			"For all their strength, men were sometimes like little children.",
			"The more I know about business, the more I'm convinced that it is conducted in homes and churches far more than in office buildings.",
			"Have you ever observed that we pay much more attention to a wise passage when it is quoted than when we read it in the original author?",
			"Arranging a bowl of flowers in the morning can give a sense of quiet in a crowded day - like writing a poem, or saying a prayer.",
			"Rules are just helpful guidelines for stupid people who can't make up their own minds.",
			"Sometimes the cure for restlessness is rest.",
			"The best things carried to excess are wrong.",
			"Read over your compositions, and wherever you meet with a passage which you think is particularly fine, strike it out.",
			"The more I give myself permission to live in the moment and enjoy it without feeling guilty or judgmental about any other time, the better I feel about the quality of my work.",
			"Everyone is a genius at least once a year. The real geniuses simply have their bright ideas closer together.",
			"I love the man that can smile in trouble, that can gather strength from distress, and grow brave by reflection. ‘Tis the business of little minds to shrink; but he whose heart is firm, and whose conscience approves his conduct, will pursue his principles unto death.",
			"A good home must be made, not bought.",
			"Millions of words are written annually purporting to tell how to beat the races, whereas the best possible advice on the subject is found in the three monosyllables: 'Do not try.'",
			"Keep true to the dreams of thy youth.",
			"Most human beings have an almost infinite capacity for taking things for granted.",
			"I was going to have cosmetic surgery until I noticed that the doctor's office was full of portraits by Picasso.",
			"The greatest of faults, I should say, is to be conscious of none.",
			"How my achievements mock me!",
			"Courage is the price that Life exacts for granting peace.",
			"Most advances in science come when a person for one reason or another is forced to change fields.",
			"My father used to say, 'Let them see you and not the suit. That should be secondary.'",
			"Force is all-conquering, but its victories are short-lived.",
			"Everyone has talent. What is rare is the courage to follow the talent to the dark place where it leads.",
			"I think on-stage nudity is disgusting, shameful and damaging to all things American. But if I were 22 with a great body, it would be artistic, tasteful, patriotic and a progressive religious experience.",
			"If you are a terror to many, then beware of many.",
			"Isn't everyone a part of everyone else?",
			"It is when power is wedded to chronic fear that it becomes formidable.",
			"Every human is an artist. The dream of your life is to make beautiful art.",
			"A friend told me that each morning when we get up we have to decide whether we are going to save or savor the world. I don't think that is the decision. It's not an either-or, save or savor. We have to do both, save and savor the world.",
			"The enemy is anybody who's going to get you killed, no matter which side he's on.",
			"Be wise with speed . A fool at forty is a fool indeed.",
			"After one look at this planet any visitor from outer space would say \"I want to see the manager.\"",
			"The cloning of humans is on most of the lists of things to worry about from Science, along with behaviour control, genetic engineering, transplanted heads, computer poetry and the unrestrained growth of plastic flowers.",
			"Memory is a giggling sprite and will not be tamed. She takes flight the moment the present becomes the past.",
			"Nothing inspires forgiveness quite like revenge.",
			"Don't sacrifice your political convictions for the convenience of the hour.",
			"There is a tragic flaw in our precious Constitution, and I don't know what can be done to fix it. This is it: Only nut cases want to be president.",
			"How little of permanent happiness could belong to a couple who were only brought together because their passions were stronger than their virtue.",
			"I am who I choose to be. I always have been what I chose…though not always what I pleased.",
			"I tend to live in the past because most of my life is there.",
            "There are no facts, only interpretations.",
            "Mathematics would certainly have not come into existence if one had known from the beginning that there was in nature no exactly straight line, no actual circle, no absolute magnitude.",
            "He who fights with monsters should look to it that he himself does not become a monster. And when you gaze long into an abyss the abyss also gazes into you.",
            "The surest way to corrupt a youth is to instruct him to hold in higher esteem those who think alike than those who think differently.",
            "Art is the supreme task and the truly metaphysical activity in this life",
            "Once upon a time, in some out of the way corner of that universe which is dispersed into numberless twinkling solar systems, there was a star upon which clever beasts invented knowing.",
            "We believe that we know something about the things themselves when we speak of trees, colors, snow, and flowers; and yet we possess nothing but metaphors for things — metaphors which correspond in no way to the original entities.",
            "Unpleasant, even dangerous, qualities can be found in every nation and every individual",
            "Mystical explanations are considered deep; the truth is, they are not even shallow.",
            "Become who you are.",
            "For believe me! - the secret for harvesting from existence the greatest fruitfulness and the greatest enjoyment is: to live dangerously!",
            "Your will and your foot which has a will to go over and beyond yourselves — that shall constitute your new honor."
	};

	public String getTestDataPath() {
		if(com.google.appengine.api.utils.SystemProperty.environment.value() == com.google.appengine.api.utils.SystemProperty.Environment.Value.Development
				&& new File("./test-docs").exists()) {
			return "./test-docs/";
		} else {
			return "https://github.com/grzegorznittner/StartupBidder/raw/master/tests/test-docs/";
		}
	}
}
