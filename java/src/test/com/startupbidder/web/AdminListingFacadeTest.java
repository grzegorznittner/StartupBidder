package test.com.startupbidder.web;

import static org.junit.Assert.*;

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
import com.startupbidder.vo.ListingAndUserVO;
import com.startupbidder.vo.ListingVO;
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
	public void testUpdateFailedListing() {
		assertTrue("Logged in user should be an admin", UserServiceFactory.getUserService().isUserAdmin());
		
		ListingVO listing = new ListingVO();
		listing.setId(new Key<Listing>(Listing.class, 999).getString());
		listing.setName("fakename");
		listing.setOwner(super.userList.get(1).getWebKey());
		ListingVO updatedListing = ListingFacade.instance().updateListing(admin, listing);
		assertNull("Listing with given id should not be present", updatedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(6));
		// setting owner here should not be taken into account
		listing.setOwner(admin.getId());
		updatedListing = ListingFacade.instance().updateListing(admin, listing);
		assertNotNull("Logged in user is not owner but is an admin", updatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(1));
		updatedListing = ListingFacade.instance().updateListing(admin, listing);
		assertNotNull("Logged in user is not owner but is an admin", updatedListing);
	}
	
	@Test
	public void testUpdateExistingListing() {
		assertTrue("Test data should have at least 12 listings: " + super.listingList, super.listingList.size() > 12);
		
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(5));
		listing.setName("Updated name");
		ListingVO updatedListing = ListingFacade.instance().updateListing(admin, listing);
		assertNotNull("Listing should be returned", updatedListing);
		assertEquals(listing.getName(), updatedListing.getName());
		assertEquals(listing.getSummary(), updatedListing.getSummary());
		assertEquals(listing.getOwner(), updatedListing.getOwner());
		assertEquals(listing.getState(), updatedListing.getState());
		assertEquals(listing.getSuggestedAmount(), updatedListing.getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), updatedListing.getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), updatedListing.getPresentationId());
		assertEquals(listing.getBuinessPlanId(), updatedListing.getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), updatedListing.getFinancialsId());
		//assertTrue("Modified date should be updated", listing.get);

		// we should be able to update name of active listing
		listing = DtoToVoConverter.convert(super.listingList.get(6));
		listing.setName("Updated name");
		listing.setSummary(RandomStringUtils.randomAlphabetic(64));
		listing.setOwner(super.userList.get(1).getWebKey());
		listing.setState(Listing.State.POSTED.toString());
		listing.setSuggestedAmount(5000);
		listing.setSuggestedPercentage(44);
		listing.setPresentationId(new Key<ListingDoc>(ListingDoc.class, 1001).getString());
		listing.setBuinessPlanId(new Key<ListingDoc>(ListingDoc.class, 1002).getString());
		listing.setFinancialsId(new Key<ListingDoc>(ListingDoc.class, 1003).getString());
		updatedListing = ListingFacade.instance().updateListing(admin, listing);
		assertNotNull("Listing should be updated", updatedListing);
		assertEquals(listing.getName(), updatedListing.getName());
		assertEquals(listing.getSummary(), updatedListing.getSummary());
		assertNotSame(listing.getOwner(), updatedListing.getOwner());
		assertNotSame(listing.getState(), updatedListing.getState());
		assertEquals(listing.getSuggestedAmount(), updatedListing.getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), updatedListing.getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), updatedListing.getPresentationId());
		assertEquals(listing.getBuinessPlanId(), updatedListing.getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), updatedListing.getFinancialsId());

		// we should be able to update new listing
		listing = DtoToVoConverter.convert(super.listingList.get(7));
		listing.setName("Updated name");
		listing.setSummary(RandomStringUtils.randomAlphabetic(64));
		listing.setOwner(super.userList.get(1).getWebKey());
		listing.setState(Listing.State.ACTIVE.toString());
		listing.setSuggestedAmount(5000);
		listing.setSuggestedPercentage(44);
		listing.setPresentationId(new Key<ListingDoc>(ListingDoc.class, 1001).getString());
		listing.setBuinessPlanId(new Key<ListingDoc>(ListingDoc.class, 1002).getString());
		listing.setFinancialsId(new Key<ListingDoc>(ListingDoc.class, 1003).getString());
		updatedListing = ListingFacade.instance().updateListing(admin, listing);
		assertNotNull("Listing should be updated", updatedListing);
		assertEquals(listing.getName(), updatedListing.getName());
		assertEquals(listing.getSummary(), updatedListing.getSummary());
		assertNotSame(listing.getOwner(), updatedListing.getOwner());
		assertNotSame(listing.getState(), updatedListing.getState());
		assertEquals(listing.getSuggestedAmount(), updatedListing.getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), updatedListing.getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), updatedListing.getPresentationId());
		assertEquals(listing.getBuinessPlanId(), updatedListing.getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), updatedListing.getFinancialsId());

		// we should not be able to update closed listing
		listing = DtoToVoConverter.convert(super.listingList.get(12));
		listing.setName("Updated name");
		listing.setSummary(RandomStringUtils.randomAlphabetic(64));
		listing.setOwner(super.userList.get(1).getWebKey());
		listing.setState(Listing.State.ACTIVE.toString());
		listing.setSuggestedAmount(5000);
		listing.setSuggestedPercentage(44);
		listing.setPresentationId(new Key<ListingDoc>(ListingDoc.class, 1001).getString());
		listing.setBuinessPlanId(new Key<ListingDoc>(ListingDoc.class, 1002).getString());
		listing.setFinancialsId(new Key<ListingDoc>(ListingDoc.class, 1003).getString());
		updatedListing = ListingFacade.instance().updateListing(admin, listing);
		assertNull("Closed listing should not be updated", updatedListing);

		// we should not be able to update name of posted listing
		listing = DtoToVoConverter.convert(super.listingList.get(10));
		listing.setName("Updated name");
		listing.setSummary(RandomStringUtils.randomAlphabetic(64));
		listing.setOwner(super.userList.get(1).getWebKey());
		listing.setState(Listing.State.ACTIVE.toString());
		listing.setSuggestedAmount(5000);
		listing.setSuggestedPercentage(44);
		listing.setPresentationId(new Key<ListingDoc>(ListingDoc.class, 1001).getString());
		listing.setBuinessPlanId(new Key<ListingDoc>(ListingDoc.class, 1002).getString());
		listing.setFinancialsId(new Key<ListingDoc>(ListingDoc.class, 1003).getString());
		updatedListing = ListingFacade.instance().updateListing(admin, listing);
		assertNull("Posted listing should not be updated", updatedListing);
		
		// we should not be able to update name of withdrawn listing
		listing = DtoToVoConverter.convert(super.listingList.get(11));
		listing.setName("Updated name");
		listing.setSummary(RandomStringUtils.randomAlphabetic(64));
		listing.setOwner(super.userList.get(1).getWebKey());
		listing.setState(Listing.State.ACTIVE.toString());
		listing.setSuggestedAmount(5000);
		listing.setSuggestedPercentage(44);
		listing.setPresentationId(new Key<ListingDoc>(ListingDoc.class, 1001).getString());
		listing.setBuinessPlanId(new Key<ListingDoc>(ListingDoc.class, 1002).getString());
		listing.setFinancialsId(new Key<ListingDoc>(ListingDoc.class, 1003).getString());
		updatedListing = ListingFacade.instance().updateListing(admin, listing);
		assertNull("Withdrawn listing should not be updated", updatedListing);
		
	}

	@Test
	public void testActivateListing() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingVO activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNull("Withdrawn listing cannot be activated", activatedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(5));
		activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNull("Already active listing cannot be activated", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNull("Closed listing cannot be activated", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(3));
		activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNull("Active listing cannot be activated", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNull("Active listing cannot be activated", activatedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(13));
		activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNull("New listing cannot be activated", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(8));
		activatedListing = ListingFacade.instance().activateListing(null, listing.getId());
		assertNull("Posted listing but logged in is null", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(8));
		activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNotNull("Admin can activate posted listing.", activatedListing);
		assertFalse("Activated listing should be a new instance of the object", listing == activatedListing);
		assertEquals("State should be POSTED", Listing.State.ACTIVE.toString(), activatedListing.getState());
		assertNotNull("Posted on date should be set", activatedListing.getPostedOn());
		assertNotNull("Closing date should be set", activatedListing.getClosingOn());
		DateMidnight midnight = new DateMidnight();
		assertTrue("Closing date should be set 30 days ahead", midnight.plus(Days.days(29)).toDate().getTime() < activatedListing.getClosingOn().getTime());
		assertEquals(listing.getName(), activatedListing.getName());
		assertEquals(listing.getSummary(), activatedListing.getSummary());
		assertEquals(listing.getOwner(), activatedListing.getOwner());
		assertEquals(listing.getSuggestedAmount(), activatedListing.getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), activatedListing.getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), activatedListing.getPresentationId());
		assertEquals(listing.getBuinessPlanId(), activatedListing.getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), activatedListing.getFinancialsId());

		listing = DtoToVoConverter.convert(super.listingList.get(14));
		activatedListing = ListingFacade.instance().activateListing(admin, listing.getId());
		assertNotNull("Admin can activate posted listing.", activatedListing);
		assertFalse("Activated listing should be a new instance of the object", listing == activatedListing);
		assertEquals("State should be POSTED", Listing.State.ACTIVE.toString(), activatedListing.getState());
		//assertNotNull("Posted on date should be set", updatedListing.getPostedOn());
		assertNotNull("Closing date should be set", activatedListing.getClosingOn());
		assertTrue("Closing date should be set 30 days ahead", midnight.plus(Days.days(29)).toDate().getTime() < activatedListing.getClosingOn().getTime());
		assertEquals(listing.getName(), activatedListing.getName());
		assertEquals(listing.getSummary(), activatedListing.getSummary());
		assertEquals(listing.getOwner(), activatedListing.getOwner());
		assertEquals(listing.getSuggestedAmount(), activatedListing.getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), activatedListing.getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), activatedListing.getPresentationId());
		assertEquals(listing.getBuinessPlanId(), activatedListing.getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), activatedListing.getFinancialsId());
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
	public void testWithdrawListing() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingVO withdrawnListing = ListingFacade.instance().withdrawListing(admin, listing.getId());
		assertNull("Withdrawn listing cannot be withdrawn", withdrawnListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(7));
		withdrawnListing = ListingFacade.instance().withdrawListing(admin, listing.getId());
		assertNull("New listing cannot be withdrawn", withdrawnListing);

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		withdrawnListing = ListingFacade.instance().withdrawListing(admin, listing.getId());
		assertNull("Closed listing cannot be withdrawn", withdrawnListing);

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		withdrawnListing = ListingFacade.instance().withdrawListing(admin, listing.getId());
		assertNull("Active listing cannot be withdrawn", withdrawnListing);

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		withdrawnListing = ListingFacade.instance().withdrawListing(admin, listing.getId());
		assertNull("Active listing cannot be withdrawn", withdrawnListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(14));
		withdrawnListing = ListingFacade.instance().withdrawListing(admin, listing.getId());
		assertNull("Posted listing but logged in user is not an owner", withdrawnListing);

		listing = DtoToVoConverter.convert(super.listingList.get(10));
		withdrawnListing = ListingFacade.instance().withdrawListing(null, listing.getId());
		assertNull("Posted listing but logged in is null", withdrawnListing);

		listing = DtoToVoConverter.convert(super.listingList.get(10));
		withdrawnListing = ListingFacade.instance().withdrawListing(admin, listing.getId());
		assertNull("Admin, but not an owner of the listing. Only owner can withdraw listing.", withdrawnListing);
	}
	
	@Test
	public void testFreezeListing() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingVO freezedListing = ListingFacade.instance().freezeListing(admin, listing.getId());
		assertNotNull("Withdrawn listing can be freezed by admin", freezedListing);
		assertFalse("Frozen listing should be a new instance of the object", listing == freezedListing);
		assertEquals("State should be FROZEN", Listing.State.FROZEN.toString(), freezedListing.getState());
		
		listing = DtoToVoConverter.convert(super.listingList.get(7));
		freezedListing = ListingFacade.instance().freezeListing(admin, listing.getId());
		assertNotNull("New listing can be freezed by admin", freezedListing);
		assertFalse("Frozen listing should be a new instance of the object", listing == freezedListing);
		assertEquals("State should be FROZEN", Listing.State.FROZEN.toString(), freezedListing.getState());

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		freezedListing = ListingFacade.instance().freezeListing(admin, listing.getId());
		assertNotNull("Closed listing can be freezed by admin", freezedListing);
		assertFalse("Frozen listing should be a new instance of the object", listing == freezedListing);
		assertEquals("State should be FROZEN", Listing.State.FROZEN.toString(), freezedListing.getState());

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		freezedListing = ListingFacade.instance().freezeListing(admin, listing.getId());
		assertNotNull("Active listing can be freezed by admin", freezedListing);
		assertFalse("Frozen listing should be a new instance of the object", listing == freezedListing);
		assertEquals("State should be FROZEN", Listing.State.FROZEN.toString(), freezedListing.getState());

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		freezedListing = ListingFacade.instance().freezeListing(admin, listing.getId());
		assertNotNull("Active listing can be freezed by admin", freezedListing);
		assertFalse("Frozen listing should be a new instance of the object", listing == freezedListing);
		assertEquals("State should be FROZEN", Listing.State.FROZEN.toString(), freezedListing.getState());
		
		listing = DtoToVoConverter.convert(super.listingList.get(14));
		freezedListing = ListingFacade.instance().freezeListing(admin, listing.getId());
		assertNotNull("Posted listing can be freezed by admin", freezedListing);
		assertFalse("Frozen listing should be a new instance of the object", listing == freezedListing);
		assertEquals("State should be FROZEN", Listing.State.FROZEN.toString(), freezedListing.getState());

		listing = DtoToVoConverter.convert(super.listingList.get(10));
		freezedListing = ListingFacade.instance().freezeListing(null, listing.getId());
		assertNull("Passed empty logged in user", freezedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(10));
		freezedListing = ListingFacade.instance().freezeListing(admin, listing.getId());
		assertNotNull("Listing posted can be freezed by admin", freezedListing);
		assertFalse("Frozen listing should be a new instance of the object", listing == freezedListing);
		assertEquals("State should be FROZEN", Listing.State.FROZEN.toString(), freezedListing.getState());
		assertEquals(listing.getName(), freezedListing.getName());
		assertEquals(listing.getSummary(), freezedListing.getSummary());
		assertEquals(listing.getOwner(), freezedListing.getOwner());
		assertEquals(listing.getClosingOn(), freezedListing.getClosingOn());
		assertEquals(listing.getSuggestedAmount(), freezedListing.getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), freezedListing.getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), freezedListing.getPresentationId());
		assertEquals(listing.getBuinessPlanId(), freezedListing.getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), freezedListing.getFinancialsId());
	}
	
	@Test
	public void testSendBackListingToOwner() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingVO postedListing = ListingFacade.instance().sendBackListingToOwner(admin, listing.getId());
		assertNull("Withdrawn listing should not be sent back", postedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(8));
		postedListing = ListingFacade.instance().sendBackListingToOwner(admin, listing.getId());
		assertNull("Already posted listing cannot be sent back", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		postedListing = ListingFacade.instance().sendBackListingToOwner(admin, listing.getId());
		assertNull("Closed listing cannot be sent back", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(7));
		postedListing = ListingFacade.instance().sendBackListingToOwner(admin, listing.getId());
		assertNull("New listing cannot be sent back", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		postedListing = ListingFacade.instance().sendBackListingToOwner(admin, listing.getId());
		assertNotNull("Sending back listing should work", postedListing);
		assertFalse("Send back listing should be a new instance of the object", listing == postedListing);
		assertEquals("State should be NEW", Listing.State.NEW.toString(), postedListing.getState());
		assertEquals(listing.getName(), postedListing.getName());
		assertEquals(listing.getSummary(), postedListing.getSummary());
		assertEquals(listing.getOwner(), postedListing.getOwner());
		assertEquals(listing.getClosingOn(), postedListing.getClosingOn());
		assertEquals(listing.getSuggestedAmount(), postedListing.getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), postedListing.getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), postedListing.getPresentationId());
		assertEquals(listing.getBuinessPlanId(), postedListing.getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), postedListing.getFinancialsId());

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		postedListing = ListingFacade.instance().sendBackListingToOwner(admin, listing.getId());
		assertNotNull("Sending back listing should work", postedListing);
		assertFalse("Send back listing should be a new instance of the object", listing == postedListing);
		assertEquals("State should be NEW", Listing.State.NEW.toString(), postedListing.getState());
		assertEquals(listing.getName(), postedListing.getName());
		assertEquals(listing.getSummary(), postedListing.getSummary());
		assertEquals(listing.getOwner(), postedListing.getOwner());
		assertEquals(listing.getClosingOn(), postedListing.getClosingOn());
		assertEquals(listing.getSuggestedAmount(), postedListing.getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), postedListing.getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), postedListing.getPresentationId());
		assertEquals(listing.getBuinessPlanId(), postedListing.getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), postedListing.getFinancialsId());
	}
	
	@Test
	public void testCreateListingDocument() {
		fail("Not implemented");
	}

	@Test
	public void testGetListingDocument() {
		fail("Not implemented");
	}
	
	@Test
	public void testGetAllListingDocuments() {
		// getAllListingDocuments should work only for admins
		fail("Not implemented");
	}


}
