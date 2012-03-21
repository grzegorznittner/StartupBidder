package test.com.startupbidder.web;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.Days;
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
import com.googlecode.objectify.Key;
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
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingAndUserVO;
import com.startupbidder.vo.ListingListVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.UserAndUserVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.ListingFacade;
import com.startupbidder.web.UserMgmtFacade;

public class AdminListingFacadeTest extends AdminFacadeAbstractTest {
	private static final Logger log = Logger.getLogger(AdminListingFacadeTest.class.getName());
	
	@Before
	public void setUp() {
		helper.setUp();
		
		setupUsers();
		setupListings();
	}
	
	@After
	public void tearDown() {
		helper.tearDown();
	}
	
	@Test
	public void testGetNonValidListing() {
		ListingAndUserVO returned = ListingFacade.instance().getListing(admin, "fakekey"); //new Key<Listing>(Listing.class, 1000).getString());
		assertNull("Key was fake so listing should be null", returned.getListing());

		returned = ListingFacade.instance().getListing(admin, null);
		assertNull("Key was null so listing should be null", returned.getListing());
	}	

	@Test
	public void testActivateListing() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(8));
		ListingAndUserVO activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNotNull(activatedListing);
		assertNotNull(activatedListing.getListing());
		assertEquals(ErrorCodes.OK, activatedListing.getErrorCode());
		assertFalse("Activated listing should be a new instance of the object", listing == activatedListing.getListing());
		assertEquals("State should be ACTIVE", Listing.State.ACTIVE.toString(), activatedListing.getListing().getState());
		assertNotNull("Posted on date should be set", activatedListing.getListing().getPostedOn());
		assertNotNull("Listed date should be set", activatedListing.getListing().getListedOn());
		assertTrue("Listed date must be between posted date and now",
				activatedListing.getListing().getPostedOn().getTime() < activatedListing.getListing().getListedOn().getTime()
				&& activatedListing.getListing().getListedOn().getTime() < new Date().getTime());
		assertNotNull("Closing date should be set", activatedListing.getListing().getClosingOn());
		DateMidnight midnight = new DateMidnight();
		assertTrue("Closing date should be set 30 days ahead", midnight.plus(Days.days(29)).toDate().getTime() < activatedListing.getListing().getClosingOn().getTime());
		assertEquals("Listing was just activated", 0, activatedListing.getListing().getDaysAgo());
		assertEquals("Listing was just activated and closing should be 30 days ahead", 30, activatedListing.getListing().getDaysLeft());
		assertEquals(listing.getName(), activatedListing.getListing().getName());
		assertEquals(listing.getSummary(), activatedListing.getListing().getSummary());
		assertEquals(listing.getOwner(), activatedListing.getListing().getOwner());
		assertEquals(listing.getSuggestedAmount(), activatedListing.getListing().getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), activatedListing.getListing().getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), activatedListing.getListing().getPresentationId());
		assertEquals(listing.getBuinessPlanId(), activatedListing.getListing().getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), activatedListing.getListing().getFinancialsId());
		
		UserAndUserVO owner = UserMgmtFacade.instance().getUser(admin, listing.getOwner());
		assertNotNull("Owner user should exist", owner.getUser());
		assertNull("After activation owner should have empty edited listing", owner.getUser().getEditedListing());

		listing = DtoToVoConverter.convert(super.listingList.get(14));
		activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNotNull(activatedListing);
		assertNotNull(activatedListing.getListing());
		assertEquals(ErrorCodes.OK, activatedListing.getErrorCode());
		assertFalse("Activated listing should be a new instance of the object", listing == activatedListing.getListing());
		assertEquals("State should be ACTIVE", Listing.State.ACTIVE.toString(), activatedListing.getListing().getState());
		assertNotNull("Posted on date should be set", activatedListing.getListing().getPostedOn());
		assertNotNull("Listed date should be set", activatedListing.getListing().getListedOn());
		assertTrue("Listed date must be between posted date and now",
				activatedListing.getListing().getPostedOn().getTime() < activatedListing.getListing().getListedOn().getTime()
				&& activatedListing.getListing().getListedOn().getTime() < new Date().getTime());
		assertNotNull("Closing date should be set", activatedListing.getListing().getClosingOn());
		midnight = new DateMidnight();
		assertTrue("Closing date should be set 30 days ahead", midnight.plus(Days.days(29)).toDate().getTime() < activatedListing.getListing().getClosingOn().getTime());
		assertEquals("Listing was just activated", 0, activatedListing.getListing().getDaysAgo());
		assertEquals("Listing was just activated and closing should be 30 days ahead", 30, activatedListing.getListing().getDaysLeft());
		assertEquals(listing.getName(), activatedListing.getListing().getName());
		assertEquals(listing.getSummary(), activatedListing.getListing().getSummary());
		assertEquals(listing.getOwner(), activatedListing.getListing().getOwner());
		assertEquals(listing.getSuggestedAmount(), activatedListing.getListing().getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), activatedListing.getListing().getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), activatedListing.getListing().getPresentationId());
		assertEquals(listing.getBuinessPlanId(), activatedListing.getListing().getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), activatedListing.getListing().getFinancialsId());
		
		owner = UserMgmtFacade.instance().getUser(admin, listing.getOwner());
		assertNotNull("Owner user should exist", owner.getUser());
		assertNull("After activation owner should have empty edited listing", owner.getUser().getEditedListing());
	}
	
	@Test
	public void testFailedActivateListing() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingAndUserVO activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNotNull(activatedListing);
		assertNull(activatedListing.getListing());
		assertNotSame("Withdrawn listing cannot be activated", ErrorCodes.OK, activatedListing.getErrorCode());
		
		listing = DtoToVoConverter.convert(super.listingList.get(5));
		activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNotNull(activatedListing);
		assertNull(activatedListing.getListing());
		assertNotSame("Already active listing cannot be activated", ErrorCodes.OK, activatedListing.getErrorCode());

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNotNull(activatedListing);
		assertNull(activatedListing.getListing());
		assertNotSame("Closed listing cannot be activated", ErrorCodes.OK, activatedListing.getErrorCode());

		listing = DtoToVoConverter.convert(super.listingList.get(3));
		activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNotNull(activatedListing);
		assertNull(activatedListing.getListing());
		assertNotSame("Active listing cannot be activated", ErrorCodes.OK, activatedListing.getErrorCode());

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNotNull(activatedListing);
		assertNull(activatedListing.getListing());
		assertNotSame("Active listing cannot be activated", ErrorCodes.OK, activatedListing.getErrorCode());
		
		listing = DtoToVoConverter.convert(super.listingList.get(13));
		activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNotNull(activatedListing);
		assertNull(activatedListing.getListing());
		assertNotSame("New listing cannot be activated", ErrorCodes.OK, activatedListing.getErrorCode());

		listing = DtoToVoConverter.convert(super.listingList.get(8));
		activatedListing = ListingFacade.instance().activateListing(null, listing.getId());
		assertNotNull(activatedListing);
		assertNull(activatedListing.getListing());
		assertNotSame("Posted listing but logged in is null", ErrorCodes.OK, activatedListing.getErrorCode());
	}
	
	@Test
	public void testPostListing() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingAndUserVO postedListing = ListingFacade.instance().postListing(admin, listing.getId());
		assertNotNull(postedListing);
		assertNotSame("We should get failure", ErrorCodes.OK, postedListing.getErrorCode());
		assertNull("Withdrawn listing cannot not be posted", postedListing.getListing());
		
		listing = DtoToVoConverter.convert(super.listingList.get(8));
		postedListing = ListingFacade.instance().postListing(admin, listing.getId());
		assertNotNull(postedListing);
		assertNotSame("We should get failure", ErrorCodes.OK, postedListing.getErrorCode());
		assertNull("Already posted listing cannot be posted", postedListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		postedListing = ListingFacade.instance().postListing(admin, listing.getId());
		assertNotNull(postedListing);
		assertNotSame("We should get failure", ErrorCodes.OK, postedListing.getErrorCode());
		assertNull("Closed listing cannot be posted", postedListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(7));
		postedListing = ListingFacade.instance().postListing(admin, listing.getId());
		assertNotNull(postedListing);
		assertNotSame("We should get failure", ErrorCodes.OK, postedListing.getErrorCode());
		assertNull("New listing, but user is not an owner", postedListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		postedListing = ListingFacade.instance().postListing(admin, listing.getId());
		assertNotNull(postedListing);
		assertNotSame("We should get failure", ErrorCodes.OK, postedListing.getErrorCode());
		assertNull("Active listings cannot be posted", postedListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		postedListing = ListingFacade.instance().postListing(admin, listing.getId());
		assertNotNull(postedListing);
		assertNotSame("We should get failure", ErrorCodes.OK, postedListing.getErrorCode());
		assertNull("Active listing cannot be posted", postedListing.getListing());
	}

	@Test
	public void testGetPostedListings() {
		// logged in user
		ListPropertiesVO listProps = new ListPropertiesVO();
		ListingListVO list = ListingFacade.instance().getPostedListings(admin, listProps);
		assertNotNull("Result should not be empty", list);
		assertNotNull("Logged in user is admin, so list should not be empty", list.getListings());
		assertSame("We should get failure", ErrorCodes.OK, list.getErrorCode());
		List<ListingVO> listings = list.getListings();
		checkListingsReturned(listings, listingList.get(8), listingList.get(9), listingList.get(10), listingList.get(14));
		checkListingsNotReturned(listings, listingList.get(0), listingList.get(1), listingList.get(2), listingList.get(3),
				listingList.get(4), listingList.get(5), listingList.get(6), listingList.get(7), listingList.get(11)
				, listingList.get(12), listingList.get(13));
		assertEquals("Number of result properly set", listings.size(), list.getListingsProperties().getNumberOfResults());
		//assertEquals("Total result properly set", list.getListings().size(), list.getListingsProperties().getTotalResults());
		assertTrue("Sorted by posted on property", listings.get(0).getPostedOn().getTime() >= listings.get(1).getPostedOn().getTime());
		assertTrue("Sorted by posted on property", listings.get(1).getPostedOn().getTime() >= listings.get(2).getPostedOn().getTime());
		assertTrue("Listing should be in POSTED state", Listing.State.POSTED.toString().equalsIgnoreCase(listings.get(0).getState()));
		assertTrue("Listing should be in POSTED state", Listing.State.POSTED.toString().equalsIgnoreCase(listings.get(1).getState()));
		assertTrue("Listing should be in POSTED state", Listing.State.POSTED.toString().equalsIgnoreCase(listings.get(2).getState()));
		assertTrue("Listing should be in POSTED state", Listing.State.POSTED.toString().equalsIgnoreCase(listings.get(3).getState()));		
	}
	
	@Test
	public void testWithdrawListing() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingAndUserVO withdrawnListing = ListingFacade.instance().withdrawListing(admin, listing.getId());
		assertNotNull(withdrawnListing);
		assertNotSame("We should get failure", ErrorCodes.OK, withdrawnListing.getErrorCode());
		assertNull("Withdrawn listing cannot be withdrawn", withdrawnListing.getListing());
		
		listing = DtoToVoConverter.convert(super.listingList.get(7));
		withdrawnListing = ListingFacade.instance().withdrawListing(admin, listing.getId());
		assertNotNull(withdrawnListing);
		assertNotSame("We should get failure", ErrorCodes.OK, withdrawnListing.getErrorCode());
		assertNull("New listing cannot be withdrawn", withdrawnListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		withdrawnListing = ListingFacade.instance().withdrawListing(admin, listing.getId());
		assertNotNull(withdrawnListing);
		assertNotSame("We should get failure", ErrorCodes.OK, withdrawnListing.getErrorCode());
		assertNull("Closed listing cannot be withdrawn", withdrawnListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		withdrawnListing = ListingFacade.instance().withdrawListing(admin, listing.getId());
		assertNotNull(withdrawnListing);
		assertNotSame("We should get failure", ErrorCodes.OK, withdrawnListing.getErrorCode());
		assertNull("Active listing cannot be withdrawn", withdrawnListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		withdrawnListing = ListingFacade.instance().withdrawListing(admin, listing.getId());
		assertNotNull(withdrawnListing);
		assertNotSame("We should get failure", ErrorCodes.OK, withdrawnListing.getErrorCode());
		assertNull("Active listing cannot be withdrawn", withdrawnListing.getListing());
		
		listing = DtoToVoConverter.convert(super.listingList.get(14));
		withdrawnListing = ListingFacade.instance().withdrawListing(admin, listing.getId());
		assertNotNull(withdrawnListing);
		assertNotSame("We should get failure", ErrorCodes.OK, withdrawnListing.getErrorCode());
		assertNull("Posted listing but logged in user is not an owner", withdrawnListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(10));
		withdrawnListing = ListingFacade.instance().withdrawListing(null, listing.getId());
		assertNotNull(withdrawnListing);
		assertNotSame("We should get failure", ErrorCodes.OK, withdrawnListing.getErrorCode());
		assertNull("Posted listing but logged in is null", withdrawnListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(10));
		withdrawnListing = ListingFacade.instance().withdrawListing(admin, listing.getId());
		assertNotNull(withdrawnListing);
		assertNotSame("We should get failure", ErrorCodes.OK, withdrawnListing.getErrorCode());
		assertNull("Admin, but not an owner of the listing. Only owner can withdraw listing.", withdrawnListing.getListing());
	}
	
	@Test
	public void testFreezeListing() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingAndUserVO freezedListing = ListingFacade.instance().freezeListing(admin, listing.getId());
		assertNotNull(freezedListing);
		assertEquals(ErrorCodes.OK, freezedListing.getErrorCode());
		assertNotNull("Withdrawn listing can be freezed by admin", freezedListing.getListing());
		assertFalse("Frozen listing should be a new instance of the object", listing == freezedListing.getListing());
		assertEquals("State should be FROZEN", Listing.State.FROZEN.toString(), freezedListing.getListing().getState());
		
		listing = DtoToVoConverter.convert(super.listingList.get(7));
		freezedListing = ListingFacade.instance().freezeListing(admin, listing.getId());
		assertNotNull(freezedListing);
		assertNotSame(ErrorCodes.OK, freezedListing.getErrorCode());
		assertNull("New listing cannot be freezed by admin", freezedListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		freezedListing = ListingFacade.instance().freezeListing(admin, listing.getId());
		assertNotNull(freezedListing);
		assertEquals(ErrorCodes.OK, freezedListing.getErrorCode());
		assertNotNull("Closed listing can be freezed by admin", freezedListing.getListing());
		assertFalse("Frozen listing should be a new instance of the object", listing == freezedListing.getListing());
		assertEquals("State should be FROZEN", Listing.State.FROZEN.toString(), freezedListing.getListing().getState());

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		freezedListing = ListingFacade.instance().freezeListing(admin, listing.getId());
		assertNotNull(freezedListing);
		assertEquals(ErrorCodes.OK, freezedListing.getErrorCode());
		assertNotNull("Active listing can be freezed by admin", freezedListing.getListing());
		assertFalse("Frozen listing should be a new instance of the object", listing == freezedListing.getListing());
		assertEquals("State should be FROZEN", Listing.State.FROZEN.toString(), freezedListing.getListing().getState());

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		freezedListing = ListingFacade.instance().freezeListing(admin, listing.getId());
		assertNotNull(freezedListing);
		assertEquals(ErrorCodes.OK, freezedListing.getErrorCode());
		assertNotNull("Active listing can be freezed by admin", freezedListing.getListing());
		assertFalse("Frozen listing should be a new instance of the object", listing == freezedListing.getListing());
		assertEquals("State should be FROZEN", Listing.State.FROZEN.toString(), freezedListing.getListing().getState());
		
		listing = DtoToVoConverter.convert(super.listingList.get(14));
		freezedListing = ListingFacade.instance().freezeListing(admin, listing.getId());
		assertNotNull(freezedListing);
		assertNotSame(ErrorCodes.OK, freezedListing.getErrorCode());
		assertNull("Posted listing can be freezed by admin", freezedListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(10));
		freezedListing = ListingFacade.instance().freezeListing(null, listing.getId());
		assertNotNull(freezedListing);
		assertNotSame(ErrorCodes.OK, freezedListing.getErrorCode());
		assertNull("Passed empty logged in user", freezedListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(4));
		freezedListing = ListingFacade.instance().freezeListing(admin, listing.getId());
		assertNotNull(freezedListing);
		assertEquals(ErrorCodes.OK, freezedListing.getErrorCode());
		assertNotNull("Active listing can be freezed by admin", freezedListing.getListing());
		assertFalse("Frozen listing should be a new instance of the object", listing == freezedListing.getListing());
		assertEquals("State should be FROZEN", Listing.State.FROZEN.toString(), freezedListing.getListing().getState());
		assertEquals(listing.getName(), freezedListing.getListing().getName());
		assertEquals(listing.getSummary(), freezedListing.getListing().getSummary());
		assertEquals(listing.getOwner(), freezedListing.getListing().getOwner());
		assertEquals(listing.getClosingOn(), freezedListing.getListing().getClosingOn());
		assertEquals(listing.getSuggestedAmount(), freezedListing.getListing().getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), freezedListing.getListing().getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), freezedListing.getListing().getPresentationId());
		assertEquals(listing.getBuinessPlanId(), freezedListing.getListing().getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), freezedListing.getListing().getFinancialsId());
	}
	
	@Test
	public void testSendBackListingToOwner() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingAndUserVO postedListing = ListingFacade.instance().sendBackListingToOwner(admin, listing.getId());
		assertNotNull(postedListing);
		assertNotSame(ErrorCodes.OK, postedListing.getErrorCode());
		assertNull("Withdrawn listing should not be sent back", postedListing.getListing());
		
		listing = DtoToVoConverter.convert(super.listingList.get(1));
		postedListing = ListingFacade.instance().sendBackListingToOwner(admin, listing.getId());
		assertNotNull(postedListing);
		assertNotSame(ErrorCodes.OK, postedListing.getErrorCode());
		assertNull("Already active listing cannot be sent back", postedListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		postedListing = ListingFacade.instance().sendBackListingToOwner(admin, listing.getId());
		assertNotNull(postedListing);
		assertNotSame(ErrorCodes.OK, postedListing.getErrorCode());
		assertNull("Closed listing cannot be sent back", postedListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(7));
		postedListing = ListingFacade.instance().sendBackListingToOwner(admin, listing.getId());
		assertNotNull(postedListing);
		assertNotSame(ErrorCodes.OK, postedListing.getErrorCode());
		assertNull("New listing cannot be sent back", postedListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(8));
		postedListing = ListingFacade.instance().sendBackListingToOwner(admin, listing.getId());
		assertNotNull(postedListing);
		assertEquals(ErrorCodes.OK, postedListing.getErrorCode());
		assertNotNull("Sending back listing should work", postedListing.getListing());
		assertFalse("Send back listing should be a new instance of the object", listing == postedListing.getListing());
		assertEquals("State should be NEW", Listing.State.NEW.toString(), postedListing.getListing().getState());
		assertEquals(listing.getName(), postedListing.getListing().getName());
		assertEquals(listing.getSummary(), postedListing.getListing().getSummary());
		assertEquals(listing.getOwner(), postedListing.getListing().getOwner());
		assertEquals(listing.getClosingOn(), postedListing.getListing().getClosingOn());
		assertEquals(listing.getSuggestedAmount(), postedListing.getListing().getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), postedListing.getListing().getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), postedListing.getListing().getPresentationId());
		assertEquals(listing.getBuinessPlanId(), postedListing.getListing().getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), postedListing.getListing().getFinancialsId());

		listing = DtoToVoConverter.convert(super.listingList.get(9));
		postedListing = ListingFacade.instance().sendBackListingToOwner(admin, listing.getId());
		assertNotNull(postedListing);
		assertEquals(ErrorCodes.OK, postedListing.getErrorCode());
		assertNotNull("Sending back listing should work", postedListing.getListing());
		assertFalse("Send back listing should be a new instance of the object", listing == postedListing.getListing());
		assertEquals("State should be NEW", Listing.State.NEW.toString(), postedListing.getListing().getState());
		assertEquals(listing.getName(), postedListing.getListing().getName());
		assertEquals(listing.getSummary(), postedListing.getListing().getSummary());
		assertEquals(listing.getOwner(), postedListing.getListing().getOwner());
		assertEquals(listing.getClosingOn(), postedListing.getListing().getClosingOn());
		assertEquals(listing.getSuggestedAmount(), postedListing.getListing().getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), postedListing.getListing().getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), postedListing.getListing().getPresentationId());
		assertEquals(listing.getBuinessPlanId(), postedListing.getListing().getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), postedListing.getListing().getFinancialsId());
	}	
}
