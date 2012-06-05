package test.com.startupbidder.datamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.BidUser;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.ListingStats;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.PrivateMessage;
import com.startupbidder.datamodel.PrivateMessageUser;
import com.startupbidder.datamodel.QuestionAnswer;
import com.startupbidder.datamodel.Rank;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.SBUser.Status;
import com.startupbidder.datamodel.SystemProperty;
import com.startupbidder.datamodel.UserStats;
import com.startupbidder.datamodel.Vote;
import com.startupbidder.vo.UserVO;

public class ObjectifyDatastoreTest {
	private static final Logger log = Logger.getLogger(ObjectifyDatastoreTest.class.getName());
	
	private LocalServiceTestHelper helper = null;
	
	private User googleUser = null;
	private List<SBUser> userList = null;
	private UserVO loggedInUser = null;

	static {
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
	}

	private List<SBUser> createTestUsers(User googleUser) {
		Objectify ofy = ObjectifyService.begin();
		List<SBUser> list = new ArrayList<SBUser>();
		
		SBUser user = new SBUser("ab@gmail.com", "Abn Bece", "ab", "+49123456789", "Charlottenstr 12, Dusseldorf, Germany",
				true, true, Status.ACTIVE);
		ofy.put(user);
		list.add(user);
		log.info("Added user: " + user);

		user = new SBUser("business@yahoo.com", "Business Guy", "guy", "+44123456789", "Bulevar Av 13, Los Angeles, US",
				false, false, Status.ACTIVE);
		ofy.put(user);
		list.add(user);
		log.info("Added user: " + user);

		user = new SBUser("bob@aol.com", "Spongebob Squarepants", "Bob", "+1000111222333", "Stolichnaja 999, Moscow, Russia",
				false, false, Status.ACTIVE);
		ofy.put(user);
		list.add(user);
		log.info("Added user: " + user);

		if (googleUser != null) {
			user = new SBUser(googleUser.getEmail(), "Loggedin User", "user", "+34123456789", "Backer Street 45, Menchester, UK",
					false, false, Status.ACTIVE);
			
			ofy.put(user);
			list.add(user);
			log.info("Added user: " + user);
		}
		return list;
	}
	
	@Before
	public void setUp() {
		LocalDatastoreServiceTestConfig dbConfig = new LocalDatastoreServiceTestConfig();
//		dbConfig.setNoStorage(false);
//		dbConfig.setBackingStoreLocation("tests/local_db.bin");
//		dbConfig.setNoIndexAutoGen(false);
		helper = new LocalServiceTestHelper(
				new LocalTaskQueueTestConfig(),
				new LocalUserServiceTestConfig(),
				dbConfig
				)
			.setEnvIsAdmin(true).setEnvIsLoggedIn(true)
			.setEnvEmail("admin@startupbidder.com").setEnvAuthDomain("google.com");
		helper.setUp();
		
		UserService userService = UserServiceFactory.getUserService();
		googleUser = userService.getCurrentUser();
		assertNotNull("User service returned null user", googleUser);
		userList = createTestUsers(googleUser);
	}
	
	@After
	public void tearDown() {
		//LocalDatastoreServiceTestConfig.getLocalDatastoreService().clearProfiles();
		helper.tearDown();
	}

	/*
 	@Test
	public void testGetUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetUserByEmail() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateUserString() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateUserStringStringStringStringBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateUserStatistics() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetUserStatistics() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateListingStatistics() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetListingStatistics() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateUser() {
		fail("Not yet implemented");
	}
*/
	
	@Test
	public void testGetAllUsers() {
		List<SBUser> users = ObjectifyDatastoreDAO.getInstance().getAllUsers();
		assertEquals(userList.size(), users.size());
	}
	
/*
	@Test
	public void testGetTopInvestor() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetUserVotes() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateListing() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateListing() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetListing() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAllListings() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetUserActiveListings() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetUserListings() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTopListings() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetActiveListings() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMostValuedListings() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMostDiscussedListings() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMostPopularListings() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLatestListings() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClosingListings() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpadateListingState() {
		fail("Not yet implemented");
	}

	@Test
	public void testValueUpListing() {
		fail("Not yet implemented");
	}

	@Test
	public void testValueUpUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCommentsForListing() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCommentsForUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBidsForListing() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBidsForUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBidsAcceptedByUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBidsFundedByUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNumberOfVotesForListing() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNumberOfVotesForUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetActivity() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBid() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetComment() {
		fail("Not yet implemented");
	}

	@Test
	public void testUserCanVoteForListing() {
		fail("Not yet implemented");
	}

	@Test
	public void testUserCanVoteForUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testActivateUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeactivateUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckUserName() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteComment() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateComment() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateComment() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteBid() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateBid() {
		fail("Not yet implemented");
	}

	@Test
	public void testActivateBid() {
		fail("Not yet implemented");
	}

	@Test
	public void testRejectBid() {
		fail("Not yet implemented");
	}

	@Test
	public void testWithdrawBid() {
		fail("Not yet implemented");
	}

	@Test
	public void testAcceptBid() {
		fail("Not yet implemented");
	}

	@Test
	public void testMarkBidAsPaid() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBidsByDate() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSystemProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetSystemProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSystemProperties() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateListingDocument() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetListingDocument() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAllListingDocuments() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteDocument() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateNotification() {
		fail("Not yet implemented");
	}

	@Test
	public void testAcknowledgeNotification() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetUserNotification() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAllUserNotification() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNotification() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetMonitor() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeactivateMonitor() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMonitorsForObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMonitorsForUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClass() {
		fail("Not yet implemented");
	}

	@Test
	public void testHashCode() {
		fail("Not yet implemented");
	}

	@Test
	public void testEquals() {
		fail("Not yet implemented");
	}

	@Test
	public void testClone() {
		fail("Not yet implemented");
	}

	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotify() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotifyAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testWaitLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testWaitLongInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testWait() {
		fail("Not yet implemented");
	}

	@Test
	public void testFinalize() {
		fail("Not yet implemented");
	}
*/
}
