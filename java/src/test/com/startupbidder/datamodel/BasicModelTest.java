package test.com.startupbidder.datamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.api.gbase.client.Location;
import com.google.appengine.api.datastore.QueryResultIterable;
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

public class BasicModelTest {
	private LocalServiceTestHelper helper = null;
	
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

	@Before
	public void setUp() throws Exception {
		LocalDatastoreServiceTestConfig dbConfig = new LocalDatastoreServiceTestConfig();
//		dbConfig.setNoStorage(false);
//		dbConfig.setBackingStoreLocation("tests/local_db.bin");
//		dbConfig.setNoIndexAutoGen(false);
		helper = new LocalServiceTestHelper(dbConfig);
		helper.setUp();
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

	@Test
	public void testSBUser() {
		Objectify ofy = ObjectifyService.begin();
		
		SBUser user = new SBUser();
		user.email = "grzegorz.nittner@gmail.com";
		user.location = "Malinowa 47, Rybnik, Poland";
		user.country = "PL";
		user.admin = true;
		user.investor = false;
		user.name = "Grzegorz Nittner";
		user.nickname = "Greg";
		user.notifyEnabled = true;
		user.phone = "+48515261941";
		
		Key<SBUser> key = ofy.put(user);
		assertNotNull("Key is null", key);
		assertNotNull(user.id);
		
		SBUser retrievedUser = ofy.get(key);
		assertNotNull("Retrieved user is null", retrievedUser);
		assertNotNull("Modifed date is not set", user.modified);
		assertEquals(user.location, retrievedUser.location);
		assertEquals(user.country, retrievedUser.country);
		assertEquals(user.admin, retrievedUser.admin);
		assertEquals(user.investor, retrievedUser.investor);
		assertEquals(user.name, retrievedUser.name);
		assertEquals(user.nickname, retrievedUser.nickname);
		assertEquals(user.notifyEnabled, retrievedUser.notifyEnabled);
		assertEquals(user.phone, retrievedUser.phone);
		
		SBUser retrievedUser2 = ofy.query(SBUser.class).filter("email", "grzegorz.nittner@gmail.com").get();
		assertNotNull("Retrieved user is null", retrievedUser2);
		assertEquals(user.location, retrievedUser2.location);
		assertEquals(user.investor, retrievedUser2.investor);

		QueryResultIterable<Key<SBUser>> userIt = ofy.query(SBUser.class)
				.filter("email", "grzegorz.nittner@gmail.com").fetchKeys();
		SBUser retrievedUser3 = ofy.get(userIt.iterator().next());
		assertNotNull("Retrieved user is null", retrievedUser3);
		assertEquals(user.nickname, retrievedUser3.nickname);
		assertEquals(user.notifyEnabled, retrievedUser3.notifyEnabled);
		assertEquals(user.phone, retrievedUser3.phone);
		
		QueryResultIterable<Key<SBUser>> userIt2 = ofy.query(SBUser.class)
				.order("email").fetchKeys();
		SBUser retrievedUser4 = ofy.get(userIt2.iterator().next());
		assertNotNull("Retrieved user is null", retrievedUser4);
		assertEquals(user.nickname, retrievedUser4.nickname);
		assertEquals(user.notifyEnabled, retrievedUser4.notifyEnabled);
		assertEquals(user.phone, retrievedUser4.phone);
	}

	@Test
	public void testListing() {
		Objectify ofy = ObjectifyService.begin();
		
		SBUser owner = new SBUser();
		Key<SBUser> ownerKey = ofy.put(owner);
		assertNotNull("Owner not stored", owner);
		
		Listing l = new Listing();
		l.name = "ListingA";
		l.owner = ownerKey;
		l.created = new Date();
		l.state = Listing.State.POSTED;
		
		Key<Listing> lk = ofy.put(l);
		assertNotNull(lk);
		assertNotNull(l.id);
		
		Listing l2 = ofy.get(lk);
		
		assertNotNull(l2.modified);
		assertEquals(l.name, l2.name);
		assertEquals(l.owner, ownerKey);
		assertEquals(l.created, l2.created);
		assertEquals(l.state, l2.state);
		
		l2.name = "ListingB";
		ofy.put(l2);
		
		Listing l3 = ofy.get(lk);
		assertEquals(l2.name, l3.name);
		
		Listing l4 = ofy.query(Listing.class).filter("owner", ownerKey).get();
		assertEquals(l4.id, l2.id);
		
		String humanReadableKey = l4.owner.getString();
		
		Listing l5 = ofy.query(Listing.class).filter("owner", Key.create(humanReadableKey)).get();
		assertEquals(l5.id, l2.id);
		
		QueryResultIterable<Listing> result = ofy.query(Listing.class).filter("state", Listing.State.POSTED).fetch();
		int count = 0;
		for(Listing listing : result) {
			assertEquals("Listing is not POSTED", listing.state, Listing.State.POSTED);
			count++;
		}
		assertTrue("Expected listings with state POSTED", count > 0);
	}

	@Test
	public void testBid() {
		Objectify ofy = ObjectifyService.begin();
		
		Bid bid = new Bid();
		bid.comment = "Comment for bid";
		bid.fundType = Bid.FundType.PREFERRED;
		bid.interestRate = 10;
		bid.percentOfCompany = 45;
		bid.placed = new Date();
		bid.status = Bid.Status.POSTED;
		bid.valuation = 4500;
		bid.value = 100;
		bid.bidder = new Key<SBUser>(SBUser.class, 1000L);
		bid.listing = new Key<Listing>(Listing.class, 1001L);
		bid.listingOwner = new Key<SBUser>(SBUser.class, 1002L);
		
		Key<Bid> key = ofy.put(bid);
		assertNotNull("Key is null", key);
		assertNotNull(bid.id);
		
		Bid retrievedUser = ofy.get(key);
		assertNotNull("Retrieved user is null", retrievedUser);
		assertNotNull("Modifed date is not set", bid.modified);
		assertEquals(bid.comment, retrievedUser.comment);
		assertEquals(bid.fundType, retrievedUser.fundType);
		assertEquals(bid.interestRate, retrievedUser.interestRate);
		assertEquals(bid.percentOfCompany, retrievedUser.percentOfCompany);
		assertEquals(bid.placed, retrievedUser.placed);
		assertEquals(bid.status, retrievedUser.status);
		assertEquals(bid.valuation, retrievedUser.valuation);
		assertEquals(bid.value, retrievedUser.value);
		assertEquals(bid.bidder, retrievedUser.bidder);
		assertEquals(bid.listing, retrievedUser.listing);
		assertEquals(bid.listingOwner, retrievedUser.listingOwner);
	}

}
