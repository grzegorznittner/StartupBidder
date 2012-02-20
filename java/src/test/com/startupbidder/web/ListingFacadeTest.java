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
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingAndUserVO;
import com.startupbidder.vo.ListingListVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.ListingFacade;
import com.startupbidder.web.UserMgmtFacade;

public class ListingFacadeTest extends BaseFacadeTest {
	private static final Logger log = Logger.getLogger(ListingFacadeTest.class.getName());
	
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
	public void testCreateListing() {
		ListingVO listing = new ListingVO();
		listing.setName("New listing");
		listing.setSuggestedAmount(30000);
		listing.setSuggestedPercentage(35);
		
		ListingVO newListing = ListingFacade.instance().createListing(loggedInUser, listing);
		assertNotNull("Listing not created", newListing);
		assertEquals("Name not stored", "New listing", newListing.getName());
		assertNull("Summary should be null", newListing.getSummary());
		assertEquals("Proper owner set", loggedInUser.getId(), newListing.getOwner());
		assertEquals("State is not NEW", Listing.State.NEW.toString(), newListing.getState());
		assertEquals("Suggested amount not stored properly", 30000, newListing.getSuggestedAmount());
		assertEquals("Suggested percentage not stored properly", 35, newListing.getSuggestedPercentage());
		assertEquals("Suggested valuation not calculated properly", 30000 * 100 / 35, newListing.getSuggestedValuation());
		//assertNotNull("Modified date should be set", newListing.getModified());
		
		listing = new ListingVO();
		listing.setName("New listing");
		listing.setSummary("Summary");
		listing.setSuggestedAmount(30000);
		listing.setSuggestedPercentage(35);
		listing.setSuggestedValuation(5);
		
		// overwriting valuation if provided
		newListing = ListingFacade.instance().createListing(loggedInUser, listing);
		assertNotNull("Listing not created", newListing);
		assertEquals("Name not stored", "New listing", newListing.getName());
		assertEquals("Summary should be set", "Summary", newListing.getSummary());
		assertEquals("State is not NEW", Listing.State.NEW.toString(), newListing.getState());
		assertEquals("Proper owner set", loggedInUser.getId(), newListing.getOwner());
		assertEquals("Suggested amount not stored properly", 30000, newListing.getSuggestedAmount());
		assertEquals("Suggested percentage not stored properly", 35, newListing.getSuggestedPercentage());
		assertEquals("Suggested valuation not calculated properly", 30000 * 100 / 35, newListing.getSuggestedValuation());
		//assertNotNull("Modified date should be set", newListing.getModified());
		
		listing = new ListingVO();
		// empty listing
		newListing = ListingFacade.instance().createListing(loggedInUser, listing);
		assertNotNull("Listing not created", newListing);
		assertNull("Name not provided", newListing.getName());
		assertNull("Summary not provided", newListing.getSummary());
		assertEquals("State is not NEW", Listing.State.NEW.toString(), newListing.getState());
		assertEquals("Proper owner set", loggedInUser.getId(), newListing.getOwner());
		assertEquals("Suggested amount should be 0", 0, newListing.getSuggestedAmount());
		assertEquals("Suggested percentage should be 0", 0, newListing.getSuggestedPercentage());
		assertEquals("Suggested valuation should be 0", 0, newListing.getSuggestedValuation());
		//assertNotNull("Modified date should be set", newListing.getModified());

		// we allow for creating listing with wrong values, we verify everything before activation
		listing = new ListingVO();
		listing.setSuggestedAmount(10);
		newListing = ListingFacade.instance().createListing(loggedInUser, listing);
		assertNotNull("Suggested valuation too low but listing should be created", newListing);

		listing = new ListingVO();
		listing.setSuggestedPercentage(-10);
		newListing = ListingFacade.instance().createListing(loggedInUser, listing);
		assertNotNull("Suggested percentage wrong but listing should be created", newListing);
		
		listing = new ListingVO();
		listing.setSuggestedValuation(-30);
		newListing = ListingFacade.instance().createListing(loggedInUser, listing);
		assertNotNull("Suggested percentage wrong but listing should be created", newListing);
	}
	
	@Test
	public void testLongStringForNewListing() {
		ListingVO listing = new ListingVO();
		String name = RandomStringUtils.randomAlphabetic(512);
		listing.setName(name);
		ListingVO newListing = ListingFacade.instance().createListing(loggedInUser, listing);
		assertEquals("Long name stored", name, newListing.getName());
		
		listing = new ListingVO();
		String summary = RandomStringUtils.randomAlphabetic(8196);
		listing.setSummary(summary);
		newListing = ListingFacade.instance().createListing(loggedInUser, listing);
		assertEquals("Long summary stored", summary, newListing.getSummary());
	}
	
	@Test
	public void testCreateNotValidListing() {
		ListingVO listing = new ListingVO();
		ListingVO newListing = ListingFacade.instance().createListing(null, listing);
		assertNull("Listing with empty owner", newListing);
	}
	
	@Test
	public void testGetListing() {
		Listing expected = super.listingList.get(0);
		ListingAndUserVO returned = ListingFacade.instance().getListing(loggedInUser, expected.getWebKey());
		assertNotNull("Listing is a test one, should exist", returned);
		assertEquals(expected.name, returned.getListing().getName());
		assertEquals(expected.summary, returned.getListing().getSummary());
		assertEquals(expected.owner.getString(), returned.getListing().getOwner());
		assertEquals(expected.state.toString(), returned.getListing().getState());
		assertEquals(expected.suggestedAmount, returned.getListing().getSuggestedAmount());
		assertEquals(expected.suggestedPercentage, returned.getListing().getSuggestedPercentage());

		expected = super.listingList.get(5);
		returned = ListingFacade.instance().getListing(loggedInUser, expected.getWebKey());
		assertNotNull("Listing is a test one, should exist", returned);
		assertEquals(expected.name, returned.getListing().getName());
		assertEquals(expected.summary, returned.getListing().getSummary());
		assertEquals(expected.owner.getString(), returned.getListing().getOwner());
		assertEquals(expected.state.toString(), returned.getListing().getState());
		assertEquals(expected.suggestedAmount, returned.getListing().getSuggestedAmount());
		assertEquals(expected.suggestedPercentage, returned.getListing().getSuggestedPercentage());

		expected = super.listingList.get(7);
		returned = ListingFacade.instance().getListing(loggedInUser, expected.getWebKey());
		assertNotNull("Listing is a test one, should exist", returned);
		assertEquals(expected.name, returned.getListing().getName());
		assertEquals(expected.summary, returned.getListing().getSummary());
		assertEquals(expected.owner.getString(), returned.getListing().getOwner());
		assertEquals(expected.state.toString(), returned.getListing().getState());
		assertEquals(expected.suggestedAmount, returned.getListing().getSuggestedAmount());
		assertEquals(expected.suggestedPercentage, returned.getListing().getSuggestedPercentage());
		
		expected = super.listingList.get(8);
		returned = ListingFacade.instance().getListing(loggedInUser, expected.getWebKey());
		assertNotNull("Listing is a test one, should exist", returned);
		assertEquals(expected.name, returned.getListing().getName());
		assertEquals(expected.summary, returned.getListing().getSummary());
		assertEquals(expected.owner.getString(), returned.getListing().getOwner());
		assertEquals(expected.state.toString(), returned.getListing().getState());
		assertEquals(expected.suggestedAmount, returned.getListing().getSuggestedAmount());
		assertEquals(expected.suggestedPercentage, returned.getListing().getSuggestedPercentage());

		expected = super.listingList.get(9);
		returned = ListingFacade.instance().getListing(loggedInUser, expected.getWebKey());
		assertNotNull("Listing is a test one, should exist", returned);
		assertEquals(expected.name, returned.getListing().getName());
		assertEquals(expected.summary, returned.getListing().getSummary());
		assertEquals(expected.owner.getString(), returned.getListing().getOwner());
		assertEquals(expected.state.toString(), returned.getListing().getState());
		assertEquals(expected.suggestedAmount, returned.getListing().getSuggestedAmount());
		assertEquals(expected.suggestedPercentage, returned.getListing().getSuggestedPercentage());

		expected = super.listingList.get(10);
		returned = ListingFacade.instance().getListing(loggedInUser, expected.getWebKey());
		assertNotNull("Listing is a test one, should exist", returned);
		assertEquals(expected.name, returned.getListing().getName());
		assertEquals(expected.summary, returned.getListing().getSummary());
		assertEquals(expected.owner.getString(), returned.getListing().getOwner());
		assertEquals(expected.state.toString(), returned.getListing().getState());
		assertEquals(expected.suggestedAmount, returned.getListing().getSuggestedAmount());
		assertEquals(expected.suggestedPercentage, returned.getListing().getSuggestedPercentage());

	}
	
	@Test
	public void testGetNonValidListing() {
		ListingAndUserVO returned = ListingFacade.instance().getListing(loggedInUser, "fakekey"); //new Key<Listing>(Listing.class, 1000).getString());
		assertNull("Key was fake so listing should be null", returned);

		returned = ListingFacade.instance().getListing(loggedInUser, null);
		assertNull("Key was null so listing should be null", returned);
	}
	
	@Test
	public void testUpdateFailedListing() {
		assertFalse("Logged in user should not be an admin", UserServiceFactory.getUserService().isUserAdmin());
		
		ListingVO listing = new ListingVO();
		listing.setId(new Key<Listing>(Listing.class, 999).getString());
		listing.setName("fakename");
		listing.setOwner(super.userList.get(1).getWebKey());
		ListingVO updatedListing = ListingFacade.instance().updateListing(loggedInUser, listing);
		assertNull("Listing with given id should not be present", updatedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(6));
		// setting owner here should not be taken into account
		listing.setOwner(loggedInUser.getId());
		updatedListing = ListingFacade.instance().updateListing(loggedInUser, listing);
		assertNull("Logged in user is not owner of the listing", updatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(1));
		updatedListing = ListingFacade.instance().updateListing(DtoToVoConverter.convert(super.userList.get(0)), listing);
		assertNull("Logged in user is not owner of the listing", updatedListing);
	}
	
	@Test
	public void testUpdateExistingListing() {
		assertTrue("Test data should have at least 12 listings: " + super.listingList, super.listingList.size() > 12);
		
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(1));
		listing.setName("Updated name");
		ListingVO updatedListing = ListingFacade.instance().updateListing(loggedInUser, listing);
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
		listing = DtoToVoConverter.convert(super.listingList.get(2));
		listing.setName("Updated name");
		listing.setSummary(RandomStringUtils.randomAlphabetic(64));
		listing.setOwner(super.userList.get(1).getWebKey());
		listing.setState(Listing.State.POSTED.toString());
		listing.setSuggestedAmount(5000);
		listing.setSuggestedPercentage(44);
		listing.setPresentationId(new Key<ListingDoc>(ListingDoc.class, 1001).getString());
		listing.setBuinessPlanId(new Key<ListingDoc>(ListingDoc.class, 1002).getString());
		listing.setFinancialsId(new Key<ListingDoc>(ListingDoc.class, 1003).getString());
		updatedListing = ListingFacade.instance().updateListing(loggedInUser, listing);
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
		updatedListing = ListingFacade.instance().updateListing(loggedInUser, listing);
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
		updatedListing = ListingFacade.instance().updateListing(loggedInUser, listing);
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
		updatedListing = ListingFacade.instance().updateListing(loggedInUser, listing);
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
		updatedListing = ListingFacade.instance().updateListing(loggedInUser, listing);
		assertNull("Withdrawn listing should not be updated", updatedListing);
		
	}

	@Test
	public void testActivateListing() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingVO activatedListing = ListingFacade.instance().activateListing(loggedInUser, listing.getId());
		assertNull("Withdrawn listing cannot be activated", activatedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(8));
		activatedListing = ListingFacade.instance().activateListing(loggedInUser, listing.getId());
		assertNull("Already posted listing cannot be activated", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		activatedListing = ListingFacade.instance().activateListing(loggedInUser, listing.getId());
		assertNull("Closed listing cannot be activated", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		activatedListing = ListingFacade.instance().activateListing(loggedInUser, listing.getId());
		assertNull("Active listing cannot be activated", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		activatedListing = ListingFacade.instance().activateListing(loggedInUser, listing.getId());
		assertNull("Active listing cannot be activated", activatedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(13));
		activatedListing = ListingFacade.instance().activateListing(loggedInUser, listing.getId());
		assertNull("New listing but logged in user is not an owner", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(13));
		activatedListing = ListingFacade.instance().activateListing(null, listing.getId());
		assertNull("New listing but logged in is null", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(7));
		activatedListing = ListingFacade.instance().activateListing(loggedInUser, listing.getId());
		assertNotNull("New listing and logged in user is an owner, should be activated", activatedListing);
		assertFalse("Activated listing should be a new instance of the object", listing == activatedListing);
		assertEquals("State should be ACTIVE", Listing.State.ACTIVE.toString(), activatedListing.getState());
		assertEquals(listing.getName(), activatedListing.getName());
		assertEquals(listing.getSummary(), activatedListing.getSummary());
		assertEquals(listing.getOwner(), activatedListing.getOwner());
		assertEquals(listing.getClosingOn(), activatedListing.getClosingOn());
		assertEquals(listing.getSuggestedAmount(), activatedListing.getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), activatedListing.getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), activatedListing.getPresentationId());
		assertEquals(listing.getBuinessPlanId(), activatedListing.getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), activatedListing.getFinancialsId());
	}
	
	@Test
	public void testPostListing() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingVO postedListing = ListingFacade.instance().postListing(loggedInUser, listing.getId());
		assertNull("Withdrawn listing should not be posted", postedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(8));
		postedListing = ListingFacade.instance().postListing(loggedInUser, listing.getId());
		assertNull("Already posted listing cannot be posted", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		postedListing = ListingFacade.instance().postListing(loggedInUser, listing.getId());
		assertNull("Closed listing cannot be posted", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(7));
		postedListing = ListingFacade.instance().postListing(loggedInUser, listing.getId());
		assertNull("New listing cannot be posted", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		postedListing = ListingFacade.instance().postListing(loggedInUser, listing.getId());
		assertNull("Active listing cannot be posted by non admin", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		postedListing = ListingFacade.instance().postListing(loggedInUser, listing.getId());
		assertNull("Active listing cannot be posted by non admin", postedListing);
	}

	@Test
	public void testFreezeListing() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingVO freezedListing = ListingFacade.instance().freezeListing(loggedInUser, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(7));
		freezedListing = ListingFacade.instance().freezeListing(loggedInUser, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		freezedListing = ListingFacade.instance().freezeListing(loggedInUser, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		freezedListing = ListingFacade.instance().freezeListing(loggedInUser, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		freezedListing = ListingFacade.instance().freezeListing(loggedInUser, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(14));
		freezedListing = ListingFacade.instance().freezeListing(loggedInUser, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(10));
		freezedListing = ListingFacade.instance().freezeListing(null, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(10));
		freezedListing = ListingFacade.instance().freezeListing(loggedInUser, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);
	}
	
	@Test
	public void testSendBackListingToOwner() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingVO postedListing = ListingFacade.instance().sendBackListingToOwner(loggedInUser, listing.getId());
		assertNull("Withdrawn listing cannot be send back to owner", postedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(8));
		postedListing = ListingFacade.instance().sendBackListingToOwner(loggedInUser, listing.getId());
		assertNull("Already posted listing cannot be send back to owner", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		postedListing = ListingFacade.instance().sendBackListingToOwner(loggedInUser, listing.getId());
		assertNull("Closed listing cannot be send back to owner", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(7));
		postedListing = ListingFacade.instance().sendBackListingToOwner(loggedInUser, listing.getId());
		assertNull("New listing cannot be send back to owner", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		postedListing = ListingFacade.instance().sendBackListingToOwner(loggedInUser, listing.getId());
		assertNull("Active listing cannot be send back by non admin", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		postedListing = ListingFacade.instance().sendBackListingToOwner(loggedInUser, listing.getId());
		assertNull("Active listing cannot be send back by non admin", postedListing);
	}
	
	@Test
	public void testGetUserListings() {
		// logged in user the same as the one in query
		ListPropertiesVO listProps = new ListPropertiesVO();
		ListingListVO list = ListingFacade.instance().getUserListings(loggedInUser, loggedInUser.getId(), listProps);
		assertNotNull("Logged in user, so list should not be empty", list);
		checkListingsReturned(list.getListings(), listingList.get(0), listingList.get(1), listingList.get(2), listingList.get(3),
				listingList.get(4), listingList.get(7), listingList.get(8), listingList.get(9), listingList.get(10),
				listingList.get(11), listingList.get(12));
		checkListingsNotReturned(list.getListings(), listingList.get(5), listingList.get(6), listingList.get(14));
		assertEquals("Number of result properly set", list.getListings().size(), list.getListingsProperties().getNumberOfResults());
		//assertEquals("Total result properly set", list.getListings().size(), list.getListingsProperties().getTotalResults());

		// logged in user different to user in query, but listings exist
		listProps = new ListPropertiesVO();
		list = ListingFacade.instance().getUserListings(loggedInUser, super.userList.get(1).getWebKey(), listProps);
		assertNotNull("Logged in user, so list should not be empty", list);
		checkListingsReturned(list.getListings(), listingList.get(5), listingList.get(6));
		checkListingsNotReturned(list.getListings(), listingList.get(14)); // 14 is POSTED listing
		assertEquals("Number of result properly set", list.getListings().size(), list.getListingsProperties().getNumberOfResults());
		//assertEquals("Total result properly set", list.getListings().size(), list.getListingsProperties().getTotalResults());

		// logged in user null, but listings for user exist
		listProps = new ListPropertiesVO();
		list = ListingFacade.instance().getUserListings(null, super.userList.get(1).getWebKey(), listProps);
		assertNotNull("Logged in user, so list should not be empty", list);
		checkListingsReturned(list.getListings(), listingList.get(5), listingList.get(6));
		checkListingsNotReturned(list.getListings(), listingList.get(0), listingList.get(1), listingList.get(2),
				listingList.get(3), listingList.get(4), listingList.get(7), 
				listingList.get(8), listingList.get(9), listingList.get(10),
				listingList.get(11), listingList.get(12), listingList.get(14));
		assertEquals("Number of result properly set", list.getListings().size(), list.getListingsProperties().getNumberOfResults());
		//assertEquals("Total result properly set", list.getListings().size(), list.getListingsProperties().getTotalResults());

		// logged in user different to user in query, listings doesn't exist
		listProps = new ListPropertiesVO();
		list = ListingFacade.instance().getUserListings(loggedInUser, super.userList.get(0).getWebKey(), listProps);
		assertNotNull("Logged in user, so list should not be empty", list);
		assertEquals("We don't have any listings for that user in test data!", 0, list.getListings().size());
		assertEquals("Number of result properly set", list.getListings().size(), list.getListingsProperties().getNumberOfResults());
		//assertEquals("Total result properly set", list.getListings().size(), list.getListingsProperties().getTotalResults());
	}

	@Test
	public void testGetActiveListings() {
		// logged in user
		ListPropertiesVO listProps = new ListPropertiesVO();
		ListingListVO list = ListingFacade.instance().getActiveListings(loggedInUser, listProps);
		assertNotNull("Logged in user, so list should not be empty", list);
		List<ListingVO> listings = list.getListings();
		checkListingsReturned(listings, listingList.get(0), listingList.get(1), listingList.get(2), listingList.get(3),
				listingList.get(4), listingList.get(5), listingList.get(6));
		checkListingsNotReturned(listings, listingList.get(7), listingList.get(8), listingList.get(9), listingList.get(10),
				listingList.get(11), listingList.get(12), listingList.get(14));
		assertEquals("Number of result properly set", listings.size(), list.getListingsProperties().getNumberOfResults());
		//assertEquals("Total result properly set", list.getListings().size(), list.getListingsProperties().getTotalResults());
		assertTrue("Sorted by listedon property", listings.get(0).getListedOn().getTime() > listings.get(1).getListedOn().getTime());
		assertTrue("Sorted by listedon property", listings.get(1).getListedOn().getTime() > listings.get(2).getListedOn().getTime());
		
		// logged in user null, but this should not affect returned list
		listProps = new ListPropertiesVO();
		list = ListingFacade.instance().getActiveListings(null, listProps);
		assertNotNull("Logged in user null, but returned object should not be null", list);
		listings = list.getListings();
		checkListingsReturned(listings, listingList.get(0), listingList.get(1), listingList.get(2), listingList.get(3),
				listingList.get(4), listingList.get(5), listingList.get(6));
		checkListingsNotReturned(listings, listingList.get(7), listingList.get(8), listingList.get(9), listingList.get(10),
				listingList.get(11), listingList.get(12), listingList.get(14));
		assertEquals("Number of result properly set", listings.size(), list.getListingsProperties().getNumberOfResults());
		//assertEquals("Total result properly set", list.getListings().size(), list.getListingsProperties().getTotalResults());
		assertTrue("Sorted by listedon property", listings.get(0).getListedOn().getTime() > listings.get(1).getListedOn().getTime());
		assertTrue("Sorted by listedon property", listings.get(1).getListedOn().getTime() > listings.get(2).getListedOn().getTime());
	}

	@Test
	public void testGetClosingListings() {
		// logged in user
		ListPropertiesVO listProps = new ListPropertiesVO();
		ListingListVO list = ListingFacade.instance().getClosingListings(loggedInUser, listProps);
		assertNotNull("Logged in user, so list should not be empty", list);
		List<ListingVO> listings = list.getListings();
		checkListingsReturned(listings, listingList.get(0), listingList.get(1), listingList.get(2), listingList.get(3),
				listingList.get(4), listingList.get(5), listingList.get(6));
		checkListingsNotReturned(listings, listingList.get(7), listingList.get(8), listingList.get(9), listingList.get(10),
				listingList.get(11), listingList.get(12), listingList.get(14));
		assertEquals("Number of result properly set", listings.size(), list.getListingsProperties().getNumberOfResults());
		//assertEquals("Total result properly set", list.getListings().size(), list.getListingsProperties().getTotalResults());
		assertTrue("Sorted by listedon property", listings.get(0).getClosingOn().getTime() > listings.get(1).getClosingOn().getTime());
		assertTrue("Sorted by listedon property", listings.get(1).getClosingOn().getTime() > listings.get(2).getClosingOn().getTime());
		
		// logged in user null, but this should not affect returned list
		listProps = new ListPropertiesVO();
		list = ListingFacade.instance().getClosingListings(null, listProps);
		assertNotNull("Logged in user null, but returned object should not be null", list);
		listings = list.getListings();
		checkListingsReturned(listings, listingList.get(0), listingList.get(1), listingList.get(2), listingList.get(3),
				listingList.get(4), listingList.get(5), listingList.get(6));
		checkListingsNotReturned(listings, listingList.get(7), listingList.get(8), listingList.get(9), listingList.get(10),
				listingList.get(11), listingList.get(12), listingList.get(14));
		assertEquals("Number of result properly set", listings.size(), list.getListingsProperties().getNumberOfResults());
		//assertEquals("Total result properly set", list.getListings().size(), list.getListingsProperties().getTotalResults());
		assertTrue("Sorted by listedon property", listings.get(0).getClosingOn().getTime() > listings.get(1).getClosingOn().getTime());
		assertTrue("Sorted by listedon property", listings.get(1).getClosingOn().getTime() > listings.get(2).getClosingOn().getTime());
	}
}
