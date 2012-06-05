package test.com.startupbidder.web;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.startupbidder.dao.MockDataBuilder;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.BidUser;
import com.startupbidder.datamodel.Category;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.ListingLocation;
import com.startupbidder.datamodel.ListingStats;
import com.startupbidder.datamodel.Location;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.PrivateMessage;
import com.startupbidder.datamodel.PrivateMessageUser;
import com.startupbidder.datamodel.QuestionAnswer;
import com.startupbidder.datamodel.Rank;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.SystemProperty;
import com.startupbidder.datamodel.UserStats;
import com.startupbidder.datamodel.Vote;
import com.startupbidder.vo.ListingTileVO;

public abstract class BaseFacadeAbstractTest {
	private static final Logger log = Logger.getLogger(BaseFacadeAbstractTest.class.getName());
	
	private LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalTaskQueueTestConfig(),
			new LocalUserServiceTestConfig(),
			new LocalDatastoreServiceTestConfig().setNoStorage(false).setBackingStoreLocation("tests/temp_test_data.bin"))
				.setEnvIsAdmin(false).setEnvIsLoggedIn(true)
				.setEnvEmail("jpfowler@startupbidder.com").setEnvAuthDomain("google.com");
	
	private int nanoHttpdPort = 0;
	private NanoHTTPD nanoHttpd;
	protected MockDataBuilder mocks;
	protected User googleUser;
	
	void setupNanoHttpd() {
		try {
			File docs = new File("tests/test-docs/calc.xls");
			log.log(Level.INFO, "Doc file: " + docs.getAbsoluteFile());
			log.log(Level.INFO, "Doc folder: " + docs.getParentFile().getAbsolutePath() + ", is dir: " + docs.getParentFile().isDirectory());
			nanoHttpd = new NanoHTTPD(nanoHttpdPort, docs.getParentFile());
			nanoHttpdPort = nanoHttpd.getLocalPort();
			log.log(Level.INFO, "NanoHTTPD is running on port " + nanoHttpdPort);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while starting NanoHTTPD", e);
			assertTrue("NanoHTTPD cannot be started!", false);
		}
	}
	
	void tearDownNanoHttpd() {
		nanoHttpd.stop();
	}
	
	void setupDatastore() {
		log.info("Copying datastore test file.");
		File testDataDBFile = new File("tests/test-docs/test_data_db.bin");
		try {
			FileUtils.copyFile(testDataDBFile, new File("tests/temp_test_data.bin"));
		} catch (IOException e) {
			assertTrue("Error copying test_data_db.bin file!", false);
		}
		helper.setUp();
		log.info("Initilizing datastore.");
		
		Objectify ofy = ObjectifyService.begin();
		log.info("Checking users.");
		List<SBUser> users = ofy.query(SBUser.class).list();
		log.info("Users checked, users:" + users.size());
		assertTrue("Test users should be available!", users.size() > 0);
		List<Listing> listings = ofy.query(Listing.class).list();
		assertTrue("Test listings should be available!", listings.size() > 0);
		
		mocks = new MockDataBuilder();
		mocks.createMockDatastore(false, false);
		
		UserService userService = UserServiceFactory.getUserService();
		googleUser = userService.getCurrentUser();
		assertNotNull("User service returned null user", googleUser);
	}
	
	void tearDownDatastore() {
		helper.tearDown();
		FileUtils.deleteQuietly(new File("tests/temp_test_data.bin"));
	}

	String getTestDocUrl(String resource) {
		return "http://127.0.0.1:" + nanoHttpdPort + "/" + resource; 
	}
	
	protected List<Listing> listingList = null;
	protected List<Bid> bidList = null;

	static {
		Handler fh = new ConsoleHandler();
	    Logger.getLogger("").addHandler(fh);
	    Logger.getLogger("").setLevel(Level.FINEST);
	    
		ObjectifyService.register(SBUser.class);
		ObjectifyService.register(Listing.class);
		ObjectifyService.register(UserStats.class);
		ObjectifyService.register(Bid.class);
		ObjectifyService.register(BidUser.class);
		ObjectifyService.register(Comment.class);
		ObjectifyService.register(ListingDoc.class);
		ObjectifyService.register(ListingStats.class);
		ObjectifyService.register(Monitor.class);
		ObjectifyService.register(Notification.class);
		ObjectifyService.register(QuestionAnswer.class);
		ObjectifyService.register(PrivateMessage.class);
		ObjectifyService.register(PrivateMessageUser.class);
		ObjectifyService.register(Rank.class);
		ObjectifyService.register(SystemProperty.class);
		ObjectifyService.register(Vote.class);
		ObjectifyService.register(Category.class);
		ObjectifyService.register(Location.class);
		ObjectifyService.register(ListingLocation.class);
	}
	
//	protected void setupUsers() {
//		UserService userService = UserServiceFactory.getUserService();
//		googleUser = userService.getCurrentUser();
//		assertNotNull("User service returned null user", googleUser);
//		userList = createTestUsers(googleUser);
//		
//		googleUserVO = UserMgmtFacade.instance().getLoggedInUserData(googleUser);
//		assertNotNull("Logged in user not stored in datastore", googleUserVO);
//		assertEquals("Email address should be used", googleUser.getEmail(), googleUserVO.getEmail());
//		assertEquals("User should be ACTIVE", SBUser.Status.ACTIVE.toString(), googleUserVO.getStatus());
//		assertEquals("User was present and it should be returned", userList.get(GOOGLEUSER).name, googleUserVO.getName());
//		
//		anotherUserVO = UserMgmtFacade.instance().getUser(admin, userList.get(1).getWebKey()).getUser();
//		assertNotNull("Logged in user not stored in datastore", anotherUserVO);
//		assertNotNull("Email address should be used", anotherUserVO.getEmail());
//		assertEquals("User should be ACTIVE", SBUser.Status.ACTIVE.toString(), anotherUserVO.getStatus());
//		assertNotNull("User was present and it should be returned", anotherUserVO.getName());
//	}

	static int GOOGLEUSER = 3;
	static int BIDDER1 = 2;
	static int BIDDER2 = 0;
	static int BIDDER3 = 1;
	static int OWNER1 = 1;
	static int OWNER2 = 3;
	static int LISTING1_OWNER1 = 5;
	static int LISTING2_OWNER1 = 6;
	static int LISTING1_OWNER2 = 1;
	static int LISTING2_OWNER2 = 2;
	static int LISTING3_OWNER2 = 3;
	static int NEW_LISTING_OWNER2 = 7;
	static int POSTED_LISTING_OWNER2 = 8;
	static int WITHDRAWN_LISTING_OWNER2 = 11;
	static int CLOSED_LISTING_OWNER2 = 12;
	
	
	
//	private List<SBUser> createTestUsers(User googleUser) {
//		Objectify ofy = ObjectifyService.begin();
//		List<SBUser> list = new ArrayList<SBUser>();
//		
//		SBUser user = new SBUser("ab@gmail.com", "Abn Bece", "ab", "+49123456789", "Charlottenstr 12, Dusseldorf, Germany",
//				false, true, Status.ACTIVE);
//		ofy.put(user);
//		list.add(user);
//
//		user = new SBUser("business@yahoo.com", "Business Guy", "guy", "+44123456789", "Bulevar Av 13, Los Angeles, US",
//				false, false, Status.ACTIVE);
//		ofy.put(user);
//		list.add(user);
//
//		user = new SBUser("bob@aol.com", "Spongebob Squarepants", "Bob", "+1000111222333", "Stolichnaja 999, Moscow, Russia",
//				false, false, Status.ACTIVE);
//		ofy.put(user);
//		list.add(user);
//
//		if (googleUser != null) {
//			googleSBUser = new SBUser(googleUser.getEmail(), "Loggedin User", "user", "+34123456789", "Backer Street 45, Menchester, UK",
//					false, false, Status.ACTIVE);
//			
//			ofy.put(googleSBUser);
//			list.add(googleSBUser);
//		}
//
//		user = new SBUser("admin@startupbidder.com", "The Admin", "admin", "+1099111222333", "Furstenwall 200, Dusseldorf, Germany",
//				true, false, Status.ACTIVE);
//		ofy.put(user);
//		admin = DtoToVoConverter.convert(user);
//		list.add(user);
//
//		user = new SBUser("nonactive@startupbidder.com", "Not active", "notactive", "+1099111222333", "Furstenwall 200, Dusseldorf, Germany",
//				true, false, Status.DEACTIVATED);
//		ofy.put(user);
//		admin = DtoToVoConverter.convert(user);
//		list.add(user);
//
//		return list;
//	}
	
//	protected void setupListings() {
//		assertNotNull("execute setupUsers() first", googleUserVO);
//
//		List<Category> categories = new MockDataBuilder().createCategories();
//		Objectify ofy = ObjectifyService.begin();
//		ofy.put(categories);
//		
//		createTestListings();
//	}
	
//	private void createTestListings() {
//		/* GU is U3
//		   admin is U4
//		   nonactive is U5
//		   L0 -> active googleuser (A-GU)
//		   L1 -> A-GU
//		   L2 -> A-GU
//		   L3 -> A-GU
//		   L4 -> A-GU
//		   L5 -> A-U1
//		   L6 -> A-U1
//		   L7 -> N-GU
//		   L8 -> P-GU
//		   L9 -> P-GU
//		   L10 -> P-GU
//		   L11 -> W-GU
//		   L12 -> C-GU
//		   L13 -> N-U1
//		   L14 -> P-U1
//		   L15 -> F-U1
//		   L16 -> F-U2
//		*/
//
//		listingList = new ArrayList<Listing>();
//		
//		// Tests rely on order of the objects
//		// Add new objects to the end of the list.
//		
//		Key<SBUser> guKey = new Key<SBUser>(SBUser.class, googleSBUser.id);
//		Key<SBUser> u1Key = new Key<SBUser>(SBUser.class, userList.get(1).id);
//		Key<SBUser> u2Key = new Key<SBUser>(SBUser.class, userList.get(2).id);
//		DateMidnight midnight = new DateMidnight();
//		DateTime now = new DateTime();
//		// prepare data
//		
//		Listing listing = new Listing();
//		listing.name = "L1";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = guKey;
//		listing.state = Listing.State.ACTIVE;
//		listing.suggestedAmount = 100000;
//		listing.suggestedPercentage = 25;
//		listing.listedOn = now.minus(Days.days(10)).toDate();
//		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(30)).toDate();
//		listingList.add(listing); // index=0
//		
//		listing = new Listing();
//		listing.name = "L2";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = guKey;
//		listing.state = Listing.State.ACTIVE;
//		listing.suggestedAmount = 120000;
//		listing.suggestedPercentage = 30;
//		listing.listedOn = now.minus(Days.days(3)).toDate();
//		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(10)).toDate();
//		listingList.add(listing); // index=1
//		
//		listing = new Listing();
//		listing.name = "L3";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = guKey;
//		listing.state = Listing.State.ACTIVE;
//		listing.suggestedAmount = 80000;
//		listing.suggestedPercentage = 40;
//		listing.listedOn = now.minus(Days.days(17)).toDate();
//		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(15)).toDate();
//		listingList.add(listing); // index=2
//		
//		listing = new Listing();
//		listing.name = "L4";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = guKey;
//		listing.state = Listing.State.ACTIVE;
//		listing.suggestedAmount = 30000;
//		listing.suggestedPercentage = 10;
//		listing.listedOn = now.minus(Days.days(8)).toDate();
//		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(50)).toDate();
//		listingList.add(listing); // index=3
//		
//		listing = new Listing();
//		listing.name = "L5";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = guKey;
//		listing.state = Listing.State.ACTIVE;
//		listing.suggestedAmount = 20000;
//		listing.suggestedPercentage = 15;
//		listing.listedOn = now.minus(Days.days(15)).toDate();
//		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(18)).toDate();
//		listingList.add(listing); // index=4
//		
//		listing = new Listing();
//		listing.name = "L6";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = u1Key;
//		listing.state = Listing.State.ACTIVE;
//		listing.suggestedAmount = 10000;
//		listing.suggestedPercentage = 22;
//		listing.listedOn = now.minus(Days.days(4)).toDate();
//		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(30)).toDate();
//		listingList.add(listing); // index=5
//		
//		listing = new Listing();
//		listing.name = "L7";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = u1Key;
//		listing.state = Listing.State.ACTIVE;
//		listing.suggestedAmount = 15000;
//		listing.suggestedPercentage = 27;
//		listing.listedOn = now.minus(Days.days(13)).toDate();
//		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(30)).toDate();
//		listingList.add(listing); // index=6
//		
//		listing = new Listing();
//		listing.name = "L8-new";
//		listing.mantra = RandomStringUtils.randomAlphabetic(16);
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = guKey;
//		listing.state = Listing.State.NEW;
//		listing.askedForFunding = true;
//		listing.suggestedAmount = 80000;
//		listing.suggestedPercentage = 40;
//		listing.listedOn = null;
//		listing.closingOn = null;
//		listing.category = "Healthcare";
//		listing.videoUrl = "http://youtube.com/video";
//		listing.website = "http://www.google.com";
//		listing.logoBase64 = "This is not valid datauri";
//		listing.address = "Malinowa 47, Rybnik, Poland";
//		listing.country = "Poland";
//		listing.city = "Rybnik";
//		listing.latitude = 50.092856;
//		listing.longitude = 18.497610;
//		listing.answer1 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer2 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer3 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer4 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer5 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer6 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer7 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer8 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer9 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer10 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer11 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer12 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer13 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer14 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer15 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer16 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer17 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer18 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer19 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer20 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer21 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer22 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer23 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer24 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer25 = RandomStringUtils.randomAlphabetic(64);
//		listing.answer26 = RandomStringUtils.randomAlphabetic(64);
//		listingList.add(listing); // index=7
//		
//		listing = new Listing();
//		listing.name = "L9-posted";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = guKey;
//		listing.state = Listing.State.POSTED;
//		listing.posted = new Date();
//		listing.suggestedAmount = 400000;
//		listing.suggestedPercentage = 50;
//		listing.posted = now.minus(Days.days(1)).toDate();
//		listingList.add(listing); // index=8
//		
//		listing = new Listing();
//		listing.name = "L10-posted";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = guKey;
//		listing.state = Listing.State.POSTED;
//		listing.posted = new Date();
//		listing.suggestedAmount = 400000;
//		listing.suggestedPercentage = 50;
//		listing.posted = now.minus(Days.days(3)).toDate();
//		listingList.add(listing); // index=9
//		
//		listing = new Listing();
//		listing.name = "L11-posted";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = guKey;
//		listing.state = Listing.State.POSTED;
//		listing.posted = new Date();
//		listing.suggestedAmount = 400000;
//		listing.suggestedPercentage = 50;
//		listing.posted = now.minus(Days.days(5)).toDate();
//		listingList.add(listing); // index=10
//		
//		listing = new Listing();
//		listing.name = "L12-withdrawn";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = guKey;
//		listing.state = Listing.State.WITHDRAWN;
//		listing.suggestedAmount = 400000;
//		listing.suggestedPercentage = 50;
//		listing.listedOn = now.minus(Days.days(7)).toDate();
//		listing.posted = now.minus(Days.days(2)).toDate();
//		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(30)).toDate();
//		listingList.add(listing); // index=11
//		
//		listing = new Listing();
//		listing.name = "L13-closed";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = guKey;
//		listing.state = Listing.State.CLOSED;
//		listing.suggestedAmount = 30000;
//		listing.suggestedPercentage = 40;
//		listing.listedOn = now.minus(Days.days(45)).toDate();
//		listing.posted = now.minus(Days.days(41)).toDate();
//		listing.closingOn = new DateMidnight(listing.listedOn).plus(Days.days(30)).toDate();
//		listingList.add(listing); // index=12
//		
//		listing = new Listing();
//		listing.name = "L14-new";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = u1Key;
//		listing.state = Listing.State.NEW;
//		listing.suggestedAmount = 85000;
//		listing.suggestedPercentage = 42;
//		listingList.add(listing); // index=13
//
//		listing = new Listing();
//		listing.name = "L15-posted";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = u1Key;
//		listing.state = Listing.State.POSTED;
//		listing.suggestedAmount = 400000;
//		listing.suggestedPercentage = 50;
//		listing.askedForFunding = true;
//		listing.created = now.minus(Days.days(3)).toDate();;
//		listing.posted = now.minus(Days.days(2)).toDate();
//		listing.listedOn = now.minus(Days.days(2)).toDate();
//		listingList.add(listing); // index=14
//		
//		listing = new Listing();
//		listing.name = "L16-posted";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = u1Key;
//		listing.state = Listing.State.FROZEN;
//		listing.posted = new Date();
//		listing.suggestedAmount = 50000;
//		listing.suggestedPercentage = 30;
//		listing.created = now.minus(Days.days(5)).toDate();;
//		listing.posted = now.minus(Days.days(4)).toDate();
//		listing.listedOn = now.minus(Days.days(3)).toDate();
//		listing.closingOn = now.plus(Days.days(27)).toDate();
//		listingList.add(listing); // index=15
//		
//		listing = new Listing();
//		listing.name = "L17-posted";
//		listing.summary = RandomStringUtils.randomAlphabetic(64);
//		listing.owner = u2Key;
//		listing.state = Listing.State.FROZEN;
//		listing.posted = new Date();
//		listing.suggestedAmount = 45000;
//		listing.suggestedPercentage = 13;
//		listing.created = now.minus(Days.days(10)).toDate();;
//		listing.posted = now.minus(Days.days(8)).toDate();
//		listing.listedOn = now.minus(Days.days(5)).toDate();
//		listing.closingOn = now.plus(Days.days(25)).toDate();
//		listingList.add(listing); // index=16
//		
//		Objectify ofy = ObjectifyService.begin();
//		ofy.put(listingList);
//	}
	
	protected void setupBids() {
		assertNotNull("Test users should be prepared first", mocks.users);
		assertNotNull("Test listings should be prepared first", listingList);
		
		createTestBids();
	}
	
	private void createTestBids() {
		bidList = new ArrayList<Bid>();
		
		// Tests rely on order of the objects
		// Add new objects to the end of the list.
		
//		bidList.add(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE, 10, 3, 20000, 25));
//		bidList.add(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.OWNER, OldBid.Action.ACTIVATE, 8, 3, 25000, 25));
//		bidList.add(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE, 7, 3, 21000, 25));
//		bidList.add(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.BIDDER, OldBid.Action.CANCEL, 6, 0, 0, 0));
//
//		bidList.add(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE, 5, 3, 15000, 25));
//		bidList.add(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.OWNER, OldBid.Action.ACTIVATE, 4, 3, 25000, 25));
//		bidList.add(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE, 3, 3, 16000, 25));
//		bidList.add(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.OWNER, OldBid.Action.CANCEL, 2, 0, 0, 0));
//
//		bidList.add(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER2, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE, 7, 3, 21000, 30));
//
//		bidList.add(prepareBid(LISTING2_OWNER1, OWNER1, BIDDER1, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE, 5, 3, 5000, 30));
//		bidList.add(prepareBid(LISTING2_OWNER1, OWNER1, BIDDER1, OldBid.Actor.OWNER, OldBid.Action.ACTIVATE, 2, 3, 6000, 30));
//
//		bidList.add(prepareBid(LISTING1_OWNER2, OWNER2, BIDDER1, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE,5, 3, 60000, 50));
//		bidList.add(prepareBid(LISTING1_OWNER2, OWNER2, BIDDER1, OldBid.Actor.OWNER, OldBid.Action.ACTIVATE, 4, 3, 65000, 50));
//
//		Objectify ofy = ObjectifyService.begin();
//		ofy.put(bidList);
	}

//	public Bid prepareBid(int listingIdx, int ownerIdx, int bidderIdx, OldBid.Actor actor, OldBid.Action action, int placedDaysAgo, int expiersInDays, int value, int percent) {
//		DateTime placed = new DateTime().minusDays(placedDaysAgo);
//		
//		OldBid bid = new OldBid();
//		bid.action = action;
//		bid.actor = actor;
//		bid.bidder = new Key<SBUser>(SBUser.class, mocks.users.get(bidderIdx).id);
//		bid.bidderName = mocks.users.get(bidderIdx).nickname;
//		bid.listing = new Key<Listing>(Listing.class, listingList.get(listingIdx).id);
//		bid.listingName = listingList.get(listingIdx).name;
//		bid.listingOwner = new Key<SBUser>(SBUser.class, mocks.users.get(ownerIdx).id);
//		bid.placed = placed.toDate();
//		bid.expires = placed.plusDays(expiersInDays).toDate();
//		bid.value = value;
//		bid.percentOfCompany = percent;
//		bid.fundType = OldBid.FundType.SOLE_INVESTOR;
//		if (actor == OldBid.Actor.BIDDER) {
//			bid.comment = "Bid by " + mocks.users.get(bidderIdx).name;
//		} else {
//			bid.comment = "Bid by " + mocks.users.get(ownerIdx).name;
//		}
//		bid.comment +=  " on " + bid.listingName + " owned by " + mocks.users.get(ownerIdx).nickname + ", action " + bid.action + ", placed " + bid.placed;
//		return bid;
//	}
	
	public void checkListingsReturned(List<ListingTileVO> list, Listing ... listings) {
		for (Listing listing : listings) {
			boolean contains = false;
			for (ListingTileVO l : list) {
				if (l.toKeyId() == listing.id) {
					contains = true;
					break;
				}
			}
			Assert.assertTrue("Listing not part of the list: " + listing, contains);
		}
	}

	public void checkListingsNotReturned(List<ListingTileVO> list, Listing ... listings) {
		for (Listing listing : listings) {
			boolean contains = false;
			for (ListingTileVO l : list) {
				if (l.toKeyId() == listing.id) {
					contains = true;
					break;
				}
			}
			Assert.assertFalse("Listing is part of the list, but it should not be there " + listing, contains);
		}
	}
}
