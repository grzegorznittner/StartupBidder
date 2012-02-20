package test.com.startupbidder.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Assert;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
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
import com.startupbidder.datamodel.SBUser.Status;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.UserMgmtFacade;

public class BaseFacadeTest {
	protected LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalTaskQueueTestConfig(),
			new LocalUserServiceTestConfig(),
			new LocalDatastoreServiceTestConfig().setNoStorage(true))
				.setEnvIsAdmin(false).setEnvIsLoggedIn(true)
				.setEnvEmail("user@startupbidder.com").setEnvAuthDomain("google.com");
	
	protected User googleUser = null;
	protected List<SBUser> userList = null;
	protected SBUser loggedInSBUser = null;
	protected UserVO loggedInUser = null;
	
	protected List<Listing> listingList = null;

	static {
		ObjectifyService.register(SBUser.class);
		ObjectifyService.register(Listing.class);
		ObjectifyService.register(UserStats.class);
		ObjectifyService.register(Bid.class);
		ObjectifyService.register(Comment.class);
		ObjectifyService.register(ListingDoc.class);
		ObjectifyService.register(ListingStats.class);
		ObjectifyService.register(Monitor.class);
		ObjectifyService.register(Notification.class);
		ObjectifyService.register(PaidBid.class);
		ObjectifyService.register(Rank.class);
		ObjectifyService.register(SystemProperty.class);
		ObjectifyService.register(Vote.class);
	}
	
	protected void setupUsers() {
		UserService userService = UserServiceFactory.getUserService();
		googleUser = userService.getCurrentUser();
		assertNotNull("User service returned null user", googleUser);
		userList = createTestUsers(googleUser);
		
		loggedInUser = UserMgmtFacade.instance().getLoggedInUserData(googleUser);
		assertNotNull("Logged in user not stored in datastore", loggedInUser);
		assertEquals("Email address should be used", googleUser.getEmail(), loggedInUser.getEmail());
		assertEquals("User should be ACTIVE", SBUser.Status.ACTIVE.toString(), loggedInUser.getStatus());
		assertEquals("User was present and it should be returned", userList.get(userList.size() - 1).name, loggedInUser.getName());
	}

	private List<SBUser> createTestUsers(User googleUser) {
		Objectify ofy = ObjectifyService.begin();
		List<SBUser> list = new ArrayList<SBUser>();
		
		SBUser user = new SBUser("ab@gmail.com", "Abn Bece", "ab", "+49123456789", "Charlottenstr 12, Dusseldorf, Germany",
				true, true, Status.ACTIVE);
		ofy.put(user);
		list.add(user);

		user = new SBUser("business@yahoo.com", "Business Guy", "guy", "+44123456789", "Bulevar Av 13, Los Angeles, US",
				false, false, Status.ACTIVE);
		ofy.put(user);
		list.add(user);

		user = new SBUser("bob@aol.com", "Spongebob Squarepants", "Bob", "+1000111222333", "Stolichnaja 999, Moscow, Russia",
				false, false, Status.ACTIVE);
		ofy.put(user);
		list.add(user);

		if (googleUser != null) {
			loggedInSBUser = new SBUser(googleUser.getEmail(), "Loggedin User", "user", "+34123456789", "Backer Street 45, Menchester, UK",
					true, false, Status.ACTIVE);
			
			ofy.put(loggedInSBUser);
			list.add(loggedInSBUser);
		}
		return list;
	}
	
	protected void setupListings() {
		assertNotNull("execute setupUsers() first", loggedInUser);
		
		createTestListings();
	}
	
	private void createTestListings() {
		listingList = new ArrayList<Listing>();
		
		// Tests rely on order of the objects
		// Add new objects to the end of the list.
		
		Key<SBUser> userKey = new Key<SBUser>(SBUser.class, loggedInSBUser.id);
		Key<SBUser> userKey2 = new Key<SBUser>(SBUser.class, userList.get(1).id);
		DateMidnight midnight = new DateMidnight();
		DateTime now = new DateTime();
		// prepare data
		
		Listing listing = new Listing();
		listing.name = "L1";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey;
		listing.state = Listing.State.ACTIVE;
		listing.suggestedAmount = 100000;
		listing.suggestedPercentage = 25;
		listing.listedOn = now.minus(Days.days(10)).toDate();
		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(30)).toDate();
		listingList.add(listing); // index=0
		
		listing = new Listing();
		listing.name = "L2";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey;
		listing.state = Listing.State.ACTIVE;
		listing.suggestedAmount = 120000;
		listing.suggestedPercentage = 30;
		listing.listedOn = now.minus(Days.days(3)).toDate();
		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(10)).toDate();
		listingList.add(listing); // index=1
		
		listing = new Listing();
		listing.name = "L3";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey;
		listing.state = Listing.State.ACTIVE;
		listing.suggestedAmount = 80000;
		listing.suggestedPercentage = 40;
		listing.listedOn = now.minus(Days.days(17)).toDate();
		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(15)).toDate();
		listingList.add(listing); // index=2
		
		listing = new Listing();
		listing.name = "L4";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey;
		listing.state = Listing.State.ACTIVE;
		listing.suggestedAmount = 30000;
		listing.suggestedPercentage = 10;
		listing.listedOn = now.minus(Days.days(8)).toDate();
		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(50)).toDate();
		listingList.add(listing); // index=3
		
		listing = new Listing();
		listing.name = "L5";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey;
		listing.state = Listing.State.ACTIVE;
		listing.suggestedAmount = 20000;
		listing.suggestedPercentage = 15;
		listing.listedOn = now.minus(Days.days(15)).toDate();
		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(18)).toDate();
		listingList.add(listing); // index=4
		
		listing = new Listing();
		listing.name = "L6";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey2;
		listing.state = Listing.State.ACTIVE;
		listing.suggestedAmount = 10000;
		listing.suggestedPercentage = 22;
		listing.listedOn = now.minus(Days.days(4)).toDate();
		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(30)).toDate();
		listingList.add(listing); // index=5
		
		listing = new Listing();
		listing.name = "L7";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey2;
		listing.state = Listing.State.ACTIVE;
		listing.suggestedAmount = 15000;
		listing.suggestedPercentage = 27;
		listing.listedOn = now.minus(Days.days(13)).toDate();
		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(30)).toDate();
		listingList.add(listing); // index=6
		
		listing = new Listing();
		listing.name = "L8-new";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey;
		listing.state = Listing.State.NEW;
		listing.suggestedAmount = 80000;
		listing.suggestedPercentage = 40;
		listing.listedOn = null;
		listing.closingOn = null;
		listingList.add(listing); // index=7
		
		listing = new Listing();
		listing.name = "L9-posted";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey;
		listing.state = Listing.State.POSTED;
		listing.suggestedAmount = 400000;
		listing.suggestedPercentage = 50;
		listingList.add(listing); // index=8
		
		listing = new Listing();
		listing.name = "L10-posted";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey;
		listing.state = Listing.State.POSTED;
		listing.suggestedAmount = 400000;
		listing.suggestedPercentage = 50;
		listingList.add(listing); // index=9
		
		listing = new Listing();
		listing.name = "L11-posted";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey;
		listing.state = Listing.State.POSTED;
		listing.suggestedAmount = 400000;
		listing.suggestedPercentage = 50;
		listingList.add(listing); // index=10
		
		listing = new Listing();
		listing.name = "L12-withdrawn";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey;
		listing.state = Listing.State.WITHDRAWN;
		listing.suggestedAmount = 400000;
		listing.suggestedPercentage = 50;
		listing.listedOn = now.minus(Days.days(7)).toDate();
		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(30)).toDate();
		listingList.add(listing); // index=11
		
		listing = new Listing();
		listing.name = "L13-closed";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey;
		listing.state = Listing.State.CLOSED;
		listing.suggestedAmount = 30000;
		listing.suggestedPercentage = 40;
		listing.listedOn = now.minus(Days.days(45)).toDate();
		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(30)).toDate();
		listingList.add(listing); // index=12
		
		listing = new Listing();
		listing.name = "L14-new";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey2;
		listing.state = Listing.State.NEW;
		listing.suggestedAmount = 85000;
		listing.suggestedPercentage = 42;
		listingList.add(listing); // index=13

		listing = new Listing();
		listing.name = "L15-posted";
		listing.summary = RandomStringUtils.randomAlphabetic(64);
		listing.owner = userKey2;
		listing.state = Listing.State.POSTED;
		listing.suggestedAmount = 400000;
		listing.suggestedPercentage = 50;
		listingList.add(listing); // index=14
		
		Objectify ofy = ObjectifyService.begin();
		ofy.put(listingList);
	}

	public void checkListingsReturned(List<ListingVO> list, Listing ... listings) {
		for (Listing listing : listings) {
			boolean contains = false;
			for (ListingVO l : list) {
				if (l.toKeyId() == listing.id) {
					contains = true;
					break;
				}
			}
			Assert.assertTrue("Listing not part of the list: " + listing, contains);
		}
	}

	public void checkListingsNotReturned(List<ListingVO> list, Listing ... listings) {
		for (Listing listing : listings) {
			boolean contains = false;
			for (ListingVO l : list) {
				if (l.toKeyId() == listing.id) {
					contains = true;
					break;
				}
			}
			Assert.assertFalse("Listing is part of the list, but it should not be there " + listing, contains);
		}
	}
}
