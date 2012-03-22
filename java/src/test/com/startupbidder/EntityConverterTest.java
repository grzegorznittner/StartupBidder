package test.com.startupbidder;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.joda.time.DateMidnight;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.EntityNotFoundException;
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
import com.startupbidder.datamodel.Rank;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.UserStats;
import com.startupbidder.datamodel.Vote;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class EntityConverterTest {
	protected LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalTaskQueueTestConfig(),
			new LocalUserServiceTestConfig(),
			new LocalDatastoreServiceTestConfig().setNoStorage(true))
				.setEnvIsAdmin(false).setEnvIsLoggedIn(true)
				.setEnvEmail("user@startupbidder.com").setEnvAuthDomain("google.com");
	
	@BeforeClass
	public static void registerOfyClasses() {
		ObjectifyService.register(SBUser.class);
		ObjectifyService.register(Listing.class);
		ObjectifyService.register(UserStats.class);
		ObjectifyService.register(Comment.class);
		ObjectifyService.register(Vote.class);
		ObjectifyService.register(Rank.class);
		ObjectifyService.register(Bid.class);
		ObjectifyService.register(ListingDoc.class);
	}
	
	@Before
	public void setUp() {
		helper.setUp();
	}
	
	@After
	public void tearDown() {
		helper.tearDown();
	}

	private Objectify getOfy() {
		Objectify ofy = ObjectifyService.begin();
		return ofy;
	}

	@Test
	public void testBid() throws EntityNotFoundException {
		DateMidnight midnight = new DateMidnight();
		
		Bid bid = new Bid();
		bid.fundType = Bid.FundType.NOTE;
		bid.listing = new Key<Listing>(Listing.class, 1000L);
		bid.percentOfCompany = 44;
		bid.placed = new Date(100000);
		bid.action = Bid.Action.ACTIVATE;
		bid.bidder = new Key<SBUser>(SBUser.class, 1001L);
		bid.bidderName = "Bidder name";
		bid.value = 44444;
		bid.expires = midnight.plusDays(3).toDate();
		bid.placed = midnight.minusDays(1).toDate();
		
		System.out.println("Original: " + bid);
		
		getOfy().put(bid);
		Bid newBid = getOfy().get(Bid.class, bid.id);

		System.out.println("Recreated: " + newBid);
		
		Assert.assertTrue(EqualsBuilder.reflectionEquals(bid, newBid));
	}
	
	@Test
	public void testComment() throws EntityNotFoundException {
		Comment comment = new Comment();
		comment.comment = "comment";
		comment.commentedOn = new Date();
		comment.listing = new Key<Listing>(Listing.class, 1002L);
		comment.user = new Key<SBUser>(SBUser.class, 1003L);
		
		System.out.println("Original: " + comment);
		
		getOfy().put(comment);
		Comment newComment = getOfy().get(Comment.class, comment.id);

		System.out.println("Recreated: " + newComment);
		
		Assert.assertTrue(EqualsBuilder.reflectionEquals(comment, newComment));
	}
	
	@Test
	public void testListing() throws EntityNotFoundException {
		Listing listing = new Listing();
		listing.closingOn = new Date(9999999);
		listing.listedOn = new Date();
		listing.name = "listing name";
		listing.owner = new Key<SBUser>(SBUser.class, 1005L);
		listing.state = Listing.State.WITHDRAWN;
		listing.suggestedAmount = 9999999;
		listing.suggestedPercentage = 49;
		listing.suggestedValuation = 456789;
		listing.summary = "summary";
		
		System.out.println("Original: " + listing);
		
		getOfy().put(listing);
		Listing newListing = getOfy().get(Listing.class, listing.id);

		System.out.println("Recreated: " + newListing);
		
		Assert.assertTrue(EqualsBuilder.reflectionEquals(listing, newListing));
	}
	
	@Test
	public void testUser() throws EntityNotFoundException {
		SBUser user = new SBUser();
		user.email = "email";
		user.investor = true;
		user.joined = new Date(4444444);
		user.lastLoggedIn = new Date(222222);
		user.modified = new Date(333333);
		user.name = "name";
		user.nickname = "nickame";
		user.openId = "open_id";
		user.status = SBUser.Status.DEACTIVATED;
		
//		List<Notification.Type> notifications = new ArrayList<Notification.Type>();
//		notifications.add(Notification.Type.BID_PAID_FOR_YOUR_LISTING);
//		notifications.add(Notification.Type.NEW_COMMENT_FOR_YOUR_LISTING);
//		notifications.add(Notification.Type.NEW_LISTING);
//		user.setNotifications(notifications);
		
		System.out.println("Original: " + user);
		getOfy().put(user);
		
		SBUser newUser = getOfy().get(SBUser.class, user.id);

		System.out.println("Recreated: " + newUser);
		
		Assert.assertTrue(EqualsBuilder.reflectionEquals(user, newUser));
	}
	
	@Test
	public void testVote() throws EntityNotFoundException {
		Vote vote = new Vote();
		vote.commentedOn = new Date();
		vote.listing = new Key<Listing>(Listing.class, 1002L);
		vote.user = new Key<SBUser>(SBUser.class, 1003L);
		vote.value = 1;
		
		System.out.println("Original: " + vote);
		
		getOfy().put(vote);
		Vote newVote = getOfy().get(Vote.class, vote.id);

		System.out.println("Recreated: " + newVote);
		
		Assert.assertTrue(EqualsBuilder.reflectionEquals(vote, newVote));

	}

	@Test
	public void testListingRank() throws EntityNotFoundException {
		Rank rank = new Rank();
		rank.date = new Date();
		rank.listing = new Key<Listing>(Listing.class, 1002L);
		rank.rank = 1;
		
		System.out.println("Original: " + rank);
		
		getOfy().put(rank);
		Rank newRank = getOfy().get(Rank.class, rank.id);

		System.out.println("Recreated: " + newRank);
		
		Assert.assertTrue(EqualsBuilder.reflectionEquals(rank, newRank));
	}
	
	@Test
	public void testListingDocument() throws EntityNotFoundException {
		ListingDoc doc = new ListingDoc();
		doc.blob = new BlobKey("xxxxx");
		doc.created = new Date();
		doc.type = ListingDoc.Type.PRESENTATION;
		
		System.out.println("Original: " + doc);
		
		getOfy().put(doc);
		ListingDoc newDoc = getOfy().get(ListingDoc.class, doc.id);

		System.out.println("Recreated: " + newDoc);
		
		Assert.assertTrue(EqualsBuilder.reflectionEquals(doc, newDoc));
	}
}

