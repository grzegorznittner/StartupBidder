package test.com.startupbidder.web;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
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

public class ListingFacadeTest extends BaseFacadeAbstractTest {
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
		ListingVO newListing = ListingFacade.instance().createListing(googleUserVO);
		assertNotNull("Listing not created", newListing);
		assertNull("Name should be empty", newListing.getName());
		assertNull("Summary should be null", newListing.getSummary());
		assertEquals("Proper owner set", googleUserVO.getId(), newListing.getOwner());
		assertEquals("State is not NEW", Listing.State.NEW.toString(), newListing.getState());
		assertNotNull("Created date should be set", newListing.getCreated());
		//assertNotNull("Modified date should be set", newListing.getModified());
		assertEquals("Edited listing for logged in user should be set", newListing.getId(), googleUserVO.getEditedListing());
		
		ListingVO newListing2 = ListingFacade.instance().createListing(googleUserVO);
		assertNotNull("Listing not created", newListing);
		assertEquals("Only one edited listing allowed", newListing.getId(), newListing2.getId());
		
		ListingVO newListingForAdmin = ListingFacade.instance().createListing(admin);
		assertNotNull("Listing not created", newListingForAdmin);
		assertNull("Name should be empty", newListingForAdmin.getName());
		assertNull("Summary should be null", newListingForAdmin.getSummary());
		assertEquals("Proper owner set", admin.getId(), newListingForAdmin.getOwner());
		assertEquals("State is not NEW", Listing.State.NEW.toString(), newListingForAdmin.getState());
		assertNotNull("Created date should be set", newListingForAdmin.getCreated());
		//assertNotNull("Modified date should be set", newListing.getModified());
		assertEquals("Edited listing for logged in user should be set", newListingForAdmin.getId(), admin.getEditedListing());
		
		assertNotSame("New listing for admin should be different", newListing.getId(), newListingForAdmin.getId());
	}
	
	@Test
	public void testCreateNotValidListing() {
		ListingVO listing = new ListingVO();
		ListingVO newListing = ListingFacade.instance().createListing(null);
		assertNull("Listing with empty owner", newListing);
	}
	
	@Test
	public void testGetListing() {
		Listing expected = super.listingList.get(0);
		ListingAndUserVO returned = ListingFacade.instance().getListing(googleUserVO, expected.getWebKey());
		assertNotNull("Listing is a test one, should exist", returned);
		assertEquals(expected.name, returned.getListing().getName());
		assertEquals(expected.summary, returned.getListing().getSummary());
		assertEquals(expected.owner.getString(), returned.getListing().getOwner());
		assertEquals(expected.state.toString(), returned.getListing().getState());
		assertEquals(expected.suggestedAmount, returned.getListing().getSuggestedAmount());
		assertEquals(expected.suggestedPercentage, returned.getListing().getSuggestedPercentage());

		expected = super.listingList.get(5);
		returned = ListingFacade.instance().getListing(googleUserVO, expected.getWebKey());
		assertNotNull("Listing is a test one, should exist", returned);
		assertEquals(expected.name, returned.getListing().getName());
		assertEquals(expected.summary, returned.getListing().getSummary());
		assertEquals(expected.owner.getString(), returned.getListing().getOwner());
		assertEquals(expected.state.toString(), returned.getListing().getState());
		assertEquals(expected.suggestedAmount, returned.getListing().getSuggestedAmount());
		assertEquals(expected.suggestedPercentage, returned.getListing().getSuggestedPercentage());

		expected = super.listingList.get(7);
		returned = ListingFacade.instance().getListing(googleUserVO, expected.getWebKey());
		assertNotNull("Listing is a test one, should exist", returned);
		assertEquals(expected.name, returned.getListing().getName());
		assertEquals(expected.summary, returned.getListing().getSummary());
		assertEquals(expected.owner.getString(), returned.getListing().getOwner());
		assertEquals(expected.state.toString(), returned.getListing().getState());
		assertEquals(expected.suggestedAmount, returned.getListing().getSuggestedAmount());
		assertEquals(expected.suggestedPercentage, returned.getListing().getSuggestedPercentage());
		
		expected = super.listingList.get(8);
		returned = ListingFacade.instance().getListing(googleUserVO, expected.getWebKey());
		assertNotNull("Listing is a test one, should exist", returned);
		assertEquals(expected.name, returned.getListing().getName());
		assertEquals(expected.summary, returned.getListing().getSummary());
		assertEquals(expected.owner.getString(), returned.getListing().getOwner());
		assertEquals(expected.state.toString(), returned.getListing().getState());
		assertEquals(expected.suggestedAmount, returned.getListing().getSuggestedAmount());
		assertEquals(expected.suggestedPercentage, returned.getListing().getSuggestedPercentage());

		expected = super.listingList.get(9);
		returned = ListingFacade.instance().getListing(googleUserVO, expected.getWebKey());
		assertNotNull("Listing is a test one, should exist", returned);
		assertEquals(expected.name, returned.getListing().getName());
		assertEquals(expected.summary, returned.getListing().getSummary());
		assertEquals(expected.owner.getString(), returned.getListing().getOwner());
		assertEquals(expected.state.toString(), returned.getListing().getState());
		assertEquals(expected.suggestedAmount, returned.getListing().getSuggestedAmount());
		assertEquals(expected.suggestedPercentage, returned.getListing().getSuggestedPercentage());

		expected = super.listingList.get(10);
		returned = ListingFacade.instance().getListing(googleUserVO, expected.getWebKey());
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
		ListingAndUserVO returned = ListingFacade.instance().getListing(googleUserVO, "fakekey"); //new Key<Listing>(Listing.class, 1000).getString());
		assertNull("Key was fake so listing should be null", returned);

		returned = ListingFacade.instance().getListing(googleUserVO, null);
		assertNull("Key was null so listing should be null", returned);
	}
	
	@Test
	public void testUpdateFailedListing() {
		assertFalse("Logged in user should not be an admin", UserServiceFactory.getUserService().isUserAdmin());
		
		ListingVO listing = new ListingVO();
		listing.setId(new Key<Listing>(Listing.class, 999).getString());
		listing.setName("fakename");
		listing.setOwner(super.userList.get(1).getWebKey());
		ListingVO updatedListing = ListingFacade.instance().updateListing(googleUserVO, listing);
		assertNull("Listing with given id should not be present", updatedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(6));
		// setting owner here should not be taken into account
		listing.setOwner(googleUserVO.getId());
		updatedListing = ListingFacade.instance().updateListing(googleUserVO, listing);
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
		ListingVO updatedListing = ListingFacade.instance().updateListing(googleUserVO, listing);
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
		updatedListing = ListingFacade.instance().updateListing(googleUserVO, listing);
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
		updatedListing = ListingFacade.instance().updateListing(googleUserVO, listing);
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
		updatedListing = ListingFacade.instance().updateListing(googleUserVO, listing);
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
		updatedListing = ListingFacade.instance().updateListing(googleUserVO, listing);
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
		updatedListing = ListingFacade.instance().updateListing(googleUserVO, listing);
		assertNull("Withdrawn listing should not be updated", updatedListing);
		
	}

	@Test
	public void testActivateListing() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingVO activatedListing = ListingFacade.instance().activateListing(googleUserVO, listing.getId());
		assertNull("Withdrawn listing cannot be activated", activatedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(7));
		activatedListing = ListingFacade.instance().activateListing(googleUserVO, listing.getId());
		assertNull("New listing cannot be activated", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		activatedListing = ListingFacade.instance().activateListing(googleUserVO, listing.getId());
		assertNull("Closed listing cannot be activated", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		activatedListing = ListingFacade.instance().activateListing(googleUserVO, listing.getId());
		assertNull("Active listing cannot be activated", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		activatedListing = ListingFacade.instance().activateListing(googleUserVO, listing.getId());
		assertNull("Active listing cannot be activated", activatedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(13));
		activatedListing = ListingFacade.instance().activateListing(googleUserVO, listing.getId());
		assertNull("New listing but logged in user is not an owner", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(13));
		activatedListing = ListingFacade.instance().activateListing(null, listing.getId());
		assertNull("New listing but logged in is null", activatedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(8));
		activatedListing = ListingFacade.instance().activateListing(googleUserVO, listing.getId());
		assertNull("Posted listing, but logged in user is an admin", activatedListing);
	}
	
	@Test
	public void testPostListing() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingVO postedListing = ListingFacade.instance().postListing(googleUserVO, listing.getId());
		assertNull("Withdrawn listing cannot be posted", postedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(8));
		postedListing = ListingFacade.instance().postListing(googleUserVO, listing.getId());
		assertNull("Already posted listing cannot be posted", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		postedListing = ListingFacade.instance().postListing(googleUserVO, listing.getId());
		assertNull("Closed listing cannot be posted", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(7));
		postedListing = ListingFacade.instance().postListing(DtoToVoConverter.convert(userList.get(BIDDER1)), listing.getId());
		assertNull("New listing, but user is not an owner", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(7));
		postedListing = ListingFacade.instance().postListing(googleUserVO, listing.getId());
		assertNotNull("New listing can be posted", postedListing);
		assertFalse("Activated listing should be a new instance of the object", listing == postedListing);
		assertEquals("State should be POSTED", Listing.State.POSTED.toString(), postedListing.getState());
		assertNotNull("Posted date should be set", postedListing.getPostedOn());
		assertEquals(listing.getName(), postedListing.getName());
		assertEquals(listing.getSummary(), postedListing.getSummary());
		assertEquals(listing.getOwner(), postedListing.getOwner());
		assertEquals(listing.getClosingOn(), postedListing.getClosingOn());
		assertEquals(listing.getSuggestedAmount(), postedListing.getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), postedListing.getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), postedListing.getPresentationId());
		assertEquals(listing.getBuinessPlanId(), postedListing.getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), postedListing.getFinancialsId());

		listing = DtoToVoConverter.convert(super.listingList.get(4));
		postedListing = ListingFacade.instance().postListing(googleUserVO, listing.getId());
		assertNull("Active listing cannot be posted", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		postedListing = ListingFacade.instance().postListing(googleUserVO, listing.getId());
		assertNull("Active listing cannot be posted, additionally user is not an owner of the listing", postedListing);
	}

	@Test
	public void testFreezeListing() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingVO freezedListing = ListingFacade.instance().freezeListing(googleUserVO, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(7));
		freezedListing = ListingFacade.instance().freezeListing(googleUserVO, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		freezedListing = ListingFacade.instance().freezeListing(googleUserVO, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		freezedListing = ListingFacade.instance().freezeListing(googleUserVO, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		freezedListing = ListingFacade.instance().freezeListing(googleUserVO, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(14));
		freezedListing = ListingFacade.instance().freezeListing(googleUserVO, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(10));
		freezedListing = ListingFacade.instance().freezeListing(null, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(10));
		freezedListing = ListingFacade.instance().freezeListing(googleUserVO, listing.getId());
		assertNull("Only admin can freeze listing", freezedListing);
	}
	
	@Test
	public void testSendBackListingToOwner() {
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(11));
		ListingVO postedListing = ListingFacade.instance().sendBackListingToOwner(googleUserVO, listing.getId());
		assertNull("Withdrawn listing cannot be send back to owner", postedListing);
		
		listing = DtoToVoConverter.convert(super.listingList.get(8));
		postedListing = ListingFacade.instance().sendBackListingToOwner(googleUserVO, listing.getId());
		assertNull("Already posted listing cannot be send back to owner", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		postedListing = ListingFacade.instance().sendBackListingToOwner(googleUserVO, listing.getId());
		assertNull("Closed listing cannot be send back to owner", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(7));
		postedListing = ListingFacade.instance().sendBackListingToOwner(googleUserVO, listing.getId());
		assertNull("New listing cannot be send back to owner", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		postedListing = ListingFacade.instance().sendBackListingToOwner(googleUserVO, listing.getId());
		assertNull("Active listing cannot be send back by non admin", postedListing);

		listing = DtoToVoConverter.convert(super.listingList.get(6));
		postedListing = ListingFacade.instance().sendBackListingToOwner(googleUserVO, listing.getId());
		assertNull("Active listing cannot be send back by non admin", postedListing);
	}
	
	@Test
	public void testGetUserListings() {
		// logged in user the same as the one in query
		ListPropertiesVO listProps = new ListPropertiesVO();
		ListingListVO list = ListingFacade.instance().getUserListings(googleUserVO, googleUserVO.getId(), listProps);
		assertNotNull("Logged in user, so list should not be empty", list);
		checkListingsReturned(list.getListings(), listingList.get(0), listingList.get(1), listingList.get(2), listingList.get(3),
				listingList.get(4), listingList.get(7), listingList.get(8), listingList.get(9), listingList.get(10),
				listingList.get(11), listingList.get(12));
		checkListingsNotReturned(list.getListings(), listingList.get(5), listingList.get(6), listingList.get(14));
		assertEquals("Number of result properly set", list.getListings().size(), list.getListingsProperties().getNumberOfResults());
		//assertEquals("Total result properly set", list.getListings().size(), list.getListingsProperties().getTotalResults());

		// logged in user different to user in query, but listings exist
		listProps = new ListPropertiesVO();
		list = ListingFacade.instance().getUserListings(googleUserVO, super.userList.get(1).getWebKey(), listProps);
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
		list = ListingFacade.instance().getUserListings(googleUserVO, super.userList.get(0).getWebKey(), listProps);
		assertNotNull("Logged in user, so list should not be empty", list);
		assertEquals("We don't have any listings for that user in test data!", 0, list.getListings().size());
		assertEquals("Number of result properly set", list.getListings().size(), list.getListingsProperties().getNumberOfResults());
		//assertEquals("Total result properly set", list.getListings().size(), list.getListingsProperties().getTotalResults());
	}

	@Test
	public void testGetActiveListings() {
		// logged in user
		ListPropertiesVO listProps = new ListPropertiesVO();
		ListingListVO list = ListingFacade.instance().getLatestActiveListings(googleUserVO, listProps);
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
		list = ListingFacade.instance().getLatestActiveListings(null, listProps);
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
		ListingListVO list = ListingFacade.instance().getClosingActiveListings(googleUserVO, listProps);
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
		list = ListingFacade.instance().getClosingActiveListings(null, listProps);
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
	
	@Test
	public void testGetMostDiscussedActiveListings() {
		fail("Not implemented");
	}
	
	@Test
	public void testGetMostPopularActiveListings() {
		fail("Not implemented");
	}
	
	@Test
	public void testGetMostValuedActiveListings() {
		fail("Not implemented");
	}

	@Test
	public void testGetTopActiveListings() {
		fail("Not implemented");
	}
	
	@Test
	public void testLatestActiveListings() {
		fail("Not implemented");
	}
	
	@Test
	public void testListingKeywordSearch() {
		fail("Not implemented");
	}

	@Test
	public void testCalculateListingStatisticsWithEmptyBidsCommentsAndVotes() {
		DateTime beforeTest = new DateTime();
		ListingStats stats = ListingFacade.instance().calculateListingStatistics(listingList.get(0).id);
		assertNotNull("Listing exists so stats should be created", stats);
		assertEquals("Listing key should be set", listingList.get(0).id, (Long)stats.listing.getId());
		assertTrue("Created time should be set", beforeTest.isBefore(new DateTime(stats.created)));
		assertTrue("Modified time should be set", beforeTest.isBefore(new DateTime(stats.modified)));
		assertEquals(listingList.get(0).mockData, stats.mockData);
		assertEquals(listingList.get(0).state, stats.state);
		assertEquals(listingList.get(0).listedOn, stats.previousValuationDate);
		assertEquals(listingList.get(0).suggestedValuation, stats.previousValuation, 0.0001);
		assertEquals(0, stats.numberOfBids);
		assertEquals(0, stats.numberOfComments);
		assertEquals(0, stats.numberOfVotes);
		// no bids then suggested valuation should be used
		assertEquals(listingList.get(0).suggestedValuation, stats.valuation, 0.0001);
		// median is 0 when no bids
		assertEquals(0.0, stats.medianValuation, 0.0001);
		// score is: (votes+comments+bids+median)/timefactor
		assertEquals(0.0, stats.score, 0.0001);

		Date previousValuationDate = stats.created;
		double previousValuation = stats.valuation;
		
		beforeTest = new DateTime();
		// second calculation
		stats = ListingFacade.instance().calculateListingStatistics(listingList.get(0).id);
		assertNotNull("Listing exists so stats should be created", stats);
		assertEquals("Listing key should be set", listingList.get(0).id, (Long)stats.listing.getId());
		assertTrue("Created time should be set", beforeTest.isBefore(new DateTime(stats.created)));
		assertTrue("Modified time should be set", beforeTest.isBefore(new DateTime(stats.modified)));
		assertEquals(listingList.get(0).mockData, stats.mockData);
		assertEquals(listingList.get(0).state, stats.state);
		assertEquals(previousValuationDate, stats.previousValuationDate);
		assertEquals(previousValuation, stats.previousValuation, 0.0001);
		assertEquals(0, stats.numberOfBids);
		assertEquals(0, stats.numberOfComments);
		assertEquals(0, stats.numberOfVotes);
		// no bids then suggested valuation should be used
		assertEquals(listingList.get(0).suggestedValuation, stats.valuation, 0.0001);
		// median is 0 when no bids
		assertEquals(0.0, stats.medianValuation, 0.0001);
		// score is: (votes+comments+bids+median)/timefactor
		assertEquals(0.0, stats.score, 0.0001);

		stats = ListingFacade.instance().calculateListingStatistics(1001);
		assertNull("We should get null for non existing listing", stats);
	}

	@Test
	public void testCalculateListingStatisticsWithData() {
		fail("Needs to be done");
	}

	@Test
	public void testGetListingStatistics() {
		DateTime beforeTest = new DateTime();
		ListingStats stats = ListingFacade.instance().getListingStatistics(listingList.get(0).id);
		assertNotNull("Listing exists so stats should be created", stats);
		assertEquals("Listing key should be set", listingList.get(0).id, (Long)stats.listing.getId());
		assertTrue("Created time should be set", beforeTest.isBefore(new DateTime(stats.created)));
		assertTrue("Modified time should be set", beforeTest.isBefore(new DateTime(stats.modified)));
		assertEquals(listingList.get(0).mockData, stats.mockData);
		assertEquals(listingList.get(0).state, stats.state);
		assertEquals(listingList.get(0).listedOn, stats.previousValuationDate);
		assertEquals(listingList.get(0).suggestedValuation, stats.previousValuation, 0.0001);
		assertEquals(0, stats.numberOfBids);
		assertEquals(0, stats.numberOfComments);
		assertEquals(0, stats.numberOfVotes);
		// no bids then suggested valuation should be used
		assertEquals(listingList.get(0).suggestedValuation, stats.valuation, 0.0001);
		// median is 0 when no bids
		assertEquals(0.0, stats.medianValuation, 0.0001);
		// score is: (votes+comments+bids+median)/timefactor
		assertEquals(0.0, stats.score, 0.0001);

		// this time statistics should not be calculated
		ListingStats stats2 = ListingFacade.instance().getListingStatistics(listingList.get(0).id);
		assertNotNull("Listing exists so stats should be created", stats2);
		assertEquals("Listing key should be set", listingList.get(0).id, (Long)stats2.listing.getId());
		assertEquals(stats.created, stats2.created);
		assertEquals(stats.modified, stats2.modified);
		assertEquals(listingList.get(0).mockData, stats.mockData);
		assertEquals(listingList.get(0).state, stats.state);
		assertEquals(stats.previousValuationDate, stats2.previousValuationDate);
		assertEquals(stats.previousValuation, stats2.previousValuation, 0.0001);

		stats = ListingFacade.instance().calculateListingStatistics(1001);
		assertNull("We should get null for non existing listing", stats);
	}

	@Test
	public void testUpdateAllListingStatistics() {
		fail("Not implemented");
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

	@Test
	public void testValueUpListing() {
		fail("Not implemented");
	}
	
	@Test
	public void testPartialUpdateListing() {
		fail("Not implemented");
	}
}
