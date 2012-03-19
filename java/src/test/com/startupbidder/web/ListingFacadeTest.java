package test.com.startupbidder.web;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

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
import com.startupbidder.vo.ListingPropertyVO;
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
		setupNanoHttpd();
	}
	
	@After
	public void tearDown() {
		tearDownNanoHttpd();
		helper.tearDown();
	}
	
	@Test
	public void testUpdateListingPropertyForCreatedListing() {
		ListingAndUserVO userListing = ListingFacade.instance().createListing(googleUserVO);
		assertEquals("We should get OK, we got " + userListing.getErrorMessage(), ErrorCodes.OK, userListing.getErrorCode());
		assertEquals(userListing.getListing().getId(), googleUserVO.getEditedListing());
		ListingVO listing = userListing.getListing();
		
		// set non empty name
		List<ListingPropertyVO> props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("title", "New name"));
		ListingAndUserVO update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertEquals("We should get OK, we got " + update.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
		assertEquals("New name", update.getListing().getName());
		assertEquals(listing.getId(), update.getListing().getId());
		assertEquals(listing.getMantra(), update.getListing().getMantra());
		assertEquals(listing.getSummary(), update.getListing().getSummary());
		assertEquals(listing.getAddress(), update.getListing().getAddress());
		assertEquals(listing.getBuinessPlanId(), update.getListing().getBuinessPlanId());
		assertEquals(listing.getPresentationId(), update.getListing().getPresentationId());
		assertEquals(listing.getFinancialsId(), update.getListing().getFinancialsId());
		assertEquals(listing.getWebsite(), update.getListing().getWebsite());
		assertEquals(listing.getState(), update.getListing().getState());

		ListingAndUserVO updatedListing = ListingFacade.instance().createListing(googleUserVO);
		assertEquals("We should get OK, we got " + updatedListing.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
		assertEquals("Edited listing should be updated", listing.getId(), updatedListing.getListing().getId());
		assertEquals("New name", updatedListing.getListing().getName());
		
		// set an empty name
		props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("title", ""));
		update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertEquals("We should get OK, we got " + updatedListing.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
		assertEquals("", update.getListing().getName());
		assertEquals(listing.getId(), update.getListing().getId());
		assertEquals(listing.getMantra(), update.getListing().getMantra());
		assertEquals(listing.getSummary(), update.getListing().getSummary());
		assertEquals(listing.getAddress(), update.getListing().getAddress());
		assertEquals(listing.getBuinessPlanId(), update.getListing().getBuinessPlanId());
		assertEquals(listing.getPresentationId(), update.getListing().getPresentationId());
		assertEquals(listing.getFinancialsId(), update.getListing().getFinancialsId());
		assertEquals(listing.getWebsite(), update.getListing().getWebsite());
		assertEquals(listing.getState(), update.getListing().getState());
		
		// set name, mantra and summary
		props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("title", "Name"));
		props.add(new ListingPropertyVO("mantra", "Mantra"));
		props.add(new ListingPropertyVO("summary", "Summary"));
		update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertEquals("We should get OK, we got " + updatedListing.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
		assertEquals("Name", update.getListing().getName());
		assertEquals(listing.getId(), update.getListing().getId());
		assertEquals("Mantra", update.getListing().getMantra());
		assertEquals("Summary", update.getListing().getSummary());
		assertEquals(listing.getAddress(), update.getListing().getAddress());
		assertEquals(listing.getBuinessPlanId(), update.getListing().getBuinessPlanId());
		assertEquals(listing.getPresentationId(), update.getListing().getPresentationId());
		assertEquals(listing.getFinancialsId(), update.getListing().getFinancialsId());
		assertEquals(listing.getWebsite(), update.getListing().getWebsite());
		assertEquals(listing.getState(), update.getListing().getState());

		// set name, mantra and summary, update for state should not be applied
		props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("title", "Name2"));
		props.add(new ListingPropertyVO("mantra", "Mantra2"));
		props.add(new ListingPropertyVO("summary", "Summary2"));
		props.add(new ListingPropertyVO("state", "ACTIVE"));
		update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertEquals("We should get OK, we got " + updatedListing.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
		assertEquals("Name2", update.getListing().getName());
		assertEquals(listing.getId(), update.getListing().getId());
		assertEquals("Mantra2", update.getListing().getMantra());
		assertEquals("Summary2", update.getListing().getSummary());
		assertEquals(listing.getAddress(), update.getListing().getAddress());
		assertEquals(listing.getBuinessPlanId(), update.getListing().getBuinessPlanId());
		assertEquals(listing.getPresentationId(), update.getListing().getPresentationId());
		assertEquals(listing.getFinancialsId(), update.getListing().getFinancialsId());
		assertEquals(listing.getWebsite(), update.getListing().getWebsite());
		assertEquals(listing.getState(), update.getListing().getState());

		// set name, mantra and summary, update for state should not work
		props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("state", "ACTIVE"));
		update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertNotSame("We should get failure, we got " + updatedListing.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
	}
	
	@Test
	public void testUpdateLargeLogoUrlListingProperty() {
		ListingAndUserVO userListing = ListingFacade.instance().createListing(googleUserVO);
		assertEquals("We should get OK, we got " + userListing.getErrorMessage(), ErrorCodes.OK, userListing.getErrorCode());
		assertEquals(userListing.getListing().getId(), googleUserVO.getEditedListing());

		// set large logo url as jpg
		List<ListingPropertyVO> props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("logo_url", getTestDocUrl("300x300.jpg")));
		ListingAndUserVO update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertEquals("We should get OK, we got " + update.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
		assertNotNull("base64logo should be set", update.getListing().getLogo());
		assertTrue("JPG uploaded so data uri should have image/jpeg", update.getListing().getLogo().startsWith("data:image/jpeg;base64,"));
		assertTrue("Data uri should be less than 32k", update.getListing().getLogo().length() < 32 * 1024);

		// set large logo url as gif
		props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("logo_url", getTestDocUrl("300x300.gif")));
		update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertEquals("We should get OK, we got " + update.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
		assertNotNull("base64logo should be set", update.getListing().getLogo());
		assertTrue("GIF uploaded so data uri should have image/jpeg", update.getListing().getLogo().startsWith("data:image/gif;base64,"));
		assertTrue("Data uri should be less than 32k", update.getListing().getLogo().length() < 32 * 1024);

		// set large logo url as png
		props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("logo_url", getTestDocUrl("300x300.png")));
		update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertEquals("We should get OK, we got " + update.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
		assertNotNull("base64logo should be set", update.getListing().getLogo());
		assertTrue("PNG uploaded so data uri should have image/jpeg", update.getListing().getLogo().startsWith("data:image/png;base64,"));
		assertTrue("Data uri should be less than 32k", update.getListing().getLogo().length() < 32 * 1024);
	}
	
	@Test
	public void testUpdateLogoUrlListingProperty() {
		ListingAndUserVO userListing = ListingFacade.instance().createListing(googleUserVO);
		assertEquals("We should get OK, we got " + userListing.getErrorMessage(), ErrorCodes.OK, userListing.getErrorCode());
		assertEquals(userListing.getListing().getId(), googleUserVO.getEditedListing());
		ListingVO listing = userListing.getListing();
		
		// set logo url with non image url
		List<ListingPropertyVO> props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("logo_url", getTestDocUrl("business_plan.ppt")));
		ListingAndUserVO update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertNotSame("We should get failure", ErrorCodes.OK, update.getErrorCode());
		assertNotNull("Even for failure we should get listing", update.getListing());
		assertNull("base64logo should be empty", update.getListing().getLogo());

		// set logo url with invalid url
		props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("logo_url", "This is not url"));
		update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertNotSame("We should get failure", ErrorCodes.OK, update.getErrorCode());
		assertNotNull("Even for failure we should get listing", update.getListing());
		assertNull("base64logo should be empty", update.getListing().getLogo());
		
		// set logo url
		props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("logo_url", getTestDocUrl("80x50.jpg")));
		update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertEquals("We should get OK, we got " + update.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
		assertNotNull("base64logo should be set", update.getListing().getLogo());
		assertTrue("JPEG uploaded so data uri should have image/jpeg", update.getListing().getLogo().startsWith("data:image/jpeg;base64,"));
		assertTrue("Data uri should be less than 64k", update.getListing().getLogo().length() < 32 * 1024);
		assertEquals(listing.getName(), update.getListing().getName());
		assertEquals(listing.getId(), update.getListing().getId());
		assertEquals(listing.getMantra(), update.getListing().getMantra());
		assertEquals(listing.getSummary(), update.getListing().getSummary());
		assertEquals(listing.getAddress(), update.getListing().getAddress());
		assertEquals(listing.getBuinessPlanId(), update.getListing().getBuinessPlanId());
		assertEquals(listing.getPresentationId(), update.getListing().getPresentationId());
		assertEquals(listing.getFinancialsId(), update.getListing().getFinancialsId());
		assertEquals(listing.getWebsite(), update.getListing().getWebsite());
		assertEquals(listing.getState(), update.getListing().getState());

		// set logo url as gif
		props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("logo_url", getTestDocUrl("80x50.gif")));
		update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertEquals("We should get OK, we got " + update.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
		assertNotNull("base64logo should be set", update.getListing().getLogo());
		assertTrue("GIF uploaded so data uri should have image/jpeg", update.getListing().getLogo().startsWith("data:image/gif;base64,"));
		assertTrue("Data uri should be less than 32k", update.getListing().getLogo().length() < 32 * 1024);

		// set logo url as png
		props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("logo_url", getTestDocUrl("80x50.png")));
		update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertEquals("We should get OK, we got " + update.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
		assertNotNull("base64logo should be set", update.getListing().getLogo());
		assertTrue("PNG uploaded so data uri should have image/png", update.getListing().getLogo().startsWith("data:image/png;base64,"));
		assertTrue("Data uri should be less than 32k", update.getListing().getLogo().length() < 32 * 1024);

		// set logo url with invalid url
		String previousLogo = update.getListing().getLogo();
		props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("logo_url", "This is not url"));
		update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertNotSame("We should get failure", ErrorCodes.OK, update.getErrorCode());
		assertNotNull("Even for failure we should get listing", update.getListing());
		assertEquals("base64logo should be the same as previously", previousLogo, update.getListing().getLogo());		
	}

	@Test
	public void testUpdateBusinessPlanUrlListingProperty() {
		ListingAndUserVO userListing = ListingFacade.instance().createListing(googleUserVO);
		assertEquals("We should get OK, we got " + userListing.getErrorMessage(), ErrorCodes.OK, userListing.getErrorCode());
		assertEquals(userListing.getListing().getId(), googleUserVO.getEditedListing());
		ListingVO listing = userListing.getListing();
		
		// set business plan with wrong url
		List<ListingPropertyVO> props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("business_plan_url", "invalid url"));
		ListingAndUserVO update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertNotSame("We should get failure", ErrorCodes.OK, update.getErrorCode());
		assertNotNull("Even for failure we should get listing", update.getListing());
		assertNull("Business plan id should be empty", update.getListing().getBuinessPlanId());

		// set business plan with valid url
		props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("business_plan_url", getTestDocUrl("resume.doc")));
		update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertEquals("We should get OK, we got " + update.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
		assertNotNull("Business plan should be set", update.getListing().getBuinessPlanId());
		assertEquals(listing.getName(), update.getListing().getName());
		assertEquals(listing.getId(), update.getListing().getId());
		assertEquals(listing.getMantra(), update.getListing().getMantra());
		assertEquals(listing.getSummary(), update.getListing().getSummary());
		assertEquals(listing.getAddress(), update.getListing().getAddress());
		assertEquals(listing.getLogo(), update.getListing().getLogo());
		assertEquals(listing.getPresentationId(), update.getListing().getPresentationId());
		assertEquals(listing.getFinancialsId(), update.getListing().getFinancialsId());
		assertEquals(listing.getWebsite(), update.getListing().getWebsite());
		assertEquals(listing.getState(), update.getListing().getState());
	}

	@Test
	public void testUpdatePresentationUrlListingProperty() {
		ListingAndUserVO userListing = ListingFacade.instance().createListing(googleUserVO);
		assertEquals("We should get OK, we got " + userListing.getErrorMessage(), ErrorCodes.OK, userListing.getErrorCode());
		assertEquals(userListing.getListing().getId(), googleUserVO.getEditedListing());
		ListingVO listing = userListing.getListing();
		
		// set business plan with wrong url
		List<ListingPropertyVO> props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("presentation_url", "invalid url"));
		ListingAndUserVO update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertNotSame("We should get failure", ErrorCodes.OK, update.getErrorCode());
		assertNotNull("Even for failure we should get listing", update.getListing());
		assertNull("Presentation id should be empty", update.getListing().getPresentationId());

		// set business plan with valid url
		props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("presentation_url", getTestDocUrl("business_plan.ppt")));
		update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertEquals("We should get OK, we got " + update.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
		assertNotNull("Presentation should be set", update.getListing().getPresentationId());
		assertEquals(listing.getName(), update.getListing().getName());
		assertEquals(listing.getId(), update.getListing().getId());
		assertEquals(listing.getMantra(), update.getListing().getMantra());
		assertEquals(listing.getSummary(), update.getListing().getSummary());
		assertEquals(listing.getAddress(), update.getListing().getAddress());
		assertEquals(listing.getLogo(), update.getListing().getLogo());
		assertEquals(listing.getBuinessPlanId(), update.getListing().getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), update.getListing().getFinancialsId());
		assertEquals(listing.getWebsite(), update.getListing().getWebsite());
		assertEquals(listing.getState(), update.getListing().getState());
	}

	@Test
	public void testUpdateFinancialsUrlListingProperty() {
		ListingAndUserVO userListing = ListingFacade.instance().createListing(googleUserVO);
		assertEquals("We should get OK, we got " + userListing.getErrorMessage(), ErrorCodes.OK, userListing.getErrorCode());
		assertEquals(userListing.getListing().getId(), googleUserVO.getEditedListing());
		ListingVO listing = userListing.getListing();
		
		// set financials with wrong url
		List<ListingPropertyVO> props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("financials_url", "invalid url"));
		ListingAndUserVO update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertNotSame("We should get failure", ErrorCodes.OK, update.getErrorCode());
		assertNotNull("Even for failure we should get listing", update.getListing());
		assertNull("Financials id should be empty", update.getListing().getFinancialsId());

		// set financials with valid url
		props = new ArrayList<ListingPropertyVO>();
		props.add(new ListingPropertyVO("financials_url", getTestDocUrl("calc.xls")));
		update = ListingFacade.instance().updateListingProperties(googleUserVO, props);
		assertEquals("We should get OK, we got " + update.getErrorMessage(), ErrorCodes.OK, update.getErrorCode());
		assertNotNull("Financials should be set", update.getListing().getFinancialsId());
		assertEquals(listing.getName(), update.getListing().getName());
		assertEquals(listing.getId(), update.getListing().getId());
		assertEquals(listing.getMantra(), update.getListing().getMantra());
		assertEquals(listing.getSummary(), update.getListing().getSummary());
		assertEquals(listing.getAddress(), update.getListing().getAddress());
		assertEquals(listing.getLogo(), update.getListing().getLogo());
		assertEquals(listing.getBuinessPlanId(), update.getListing().getBuinessPlanId());
		assertEquals(listing.getPresentationId(), update.getListing().getPresentationId());
		assertEquals(listing.getWebsite(), update.getListing().getWebsite());
		assertEquals(listing.getState(), update.getListing().getState());
	}

	@Test
	public void testCreateListing() {
		ListingAndUserVO newListing = ListingFacade.instance().createListing(googleUserVO);
		assertEquals("We should get OK", ErrorCodes.OK, newListing.getErrorCode());
		assertNotNull("Listing not created", newListing.getListing());
		assertNull("Name should be empty", newListing.getListing().getName());
		assertNull("Summary should be null", newListing.getListing().getSummary());
		assertEquals("Proper owner set", googleUserVO.getId(), newListing.getListing().getOwner());
		assertEquals("State is not NEW", Listing.State.NEW.toString(), newListing.getListing().getState());
		assertNotNull("Created date should be set", newListing.getListing().getCreated());
		assertNotNull("Modified date should be set", newListing.getListing().getModified());
		assertEquals("Edited listing for logged in user should be set", newListing.getListing().getId(), googleUserVO.getEditedListing());
		
		ListingAndUserVO newListing2 = ListingFacade.instance().createListing(googleUserVO);
		assertEquals("We should get OK", ErrorCodes.OK, newListing.getErrorCode());
		assertNotNull("Listing not created", newListing2.getListing());
		assertEquals("Only one edited listing allowed", newListing.getListing().getId(), newListing2.getListing().getId());
		
		ListingAndUserVO newListingForAdmin = ListingFacade.instance().createListing(admin);
		assertEquals("We should get OK", ErrorCodes.OK, newListing.getErrorCode());
		assertNotNull("Listing not created", newListingForAdmin.getListing());
		assertNull("Name should be empty", newListingForAdmin.getListing().getName());
		assertNull("Summary should be null", newListingForAdmin.getListing().getSummary());
		assertEquals("Proper owner set", admin.getId(), newListingForAdmin.getListing().getOwner());
		assertEquals("State is not NEW", Listing.State.NEW.toString(), newListingForAdmin.getListing().getState());
		assertNotNull("Created date should be set", newListingForAdmin.getListing().getCreated());
		assertNotNull("Modified date should be set", newListing.getListing().getModified());
		assertEquals("Edited listing for logged in user should be set", newListingForAdmin.getListing().getId(), admin.getEditedListing());
		
		assertNotSame("New listing for admin should be different",
				newListing.getListing().getId(), newListingForAdmin.getListing().getId());
	}
	
	@Test
	public void testCreateNotValidListing() {
		ListingAndUserVO newListing = ListingFacade.instance().createListing(null);
		assertEquals("We should get failure", ErrorCodes.NOT_LOGGED_IN, newListing.getErrorCode());
		assertNotNull("Result should not be empty", newListing);
		assertNull("Listing for that result should be empty", newListing.getListing());
		assertNotSame("We should get failure", ErrorCodes.OK, newListing.getErrorCode());
	}
	
	@Test
	public void testDeleteUserNewListing() {
		// googleuser has a NEW listing, so we can't use that user
		ListingAndUserVO newListing = ListingFacade.instance().createListing(admin);
		assertEquals("We should get OK", ErrorCodes.OK, newListing.getErrorCode());
		assertNotNull("Listing not created", newListing.getListing());
		assertEquals("Proper owner set", admin.getId(), newListing.getListing().getOwner());
		assertEquals("State is not NEW", Listing.State.NEW.toString(), newListing.getListing().getState());
		assertNotNull("Created date should be set", newListing.getListing().getCreated());
		assertNotNull("Modified date should be set", newListing.getListing().getModified());
		assertEquals("Edited listing for logged in user should be set", newListing.getListing().getId(), admin.getEditedListing());

		ListingAndUserVO listing = ListingFacade.instance().getListing(admin, newListing.getListing().getId());
		assertEquals("We should get OK", ErrorCodes.OK, newListing.getErrorCode());
		assertNotNull("List should exist", listing.getListing());
		assertEquals("State should be NEW", Listing.State.NEW.toString(), listing.getListing().getState());

		ListingAndUserVO deletedListing = ListingFacade.instance().deleteNewListing(admin);
		assertEquals("We should get OK", ErrorCodes.OK, newListing.getErrorCode());
		assertNull("Listing should be deleted", deletedListing.getListing());
		assertNull("Edited listing field should be set to null", admin.getEditedListing());
		
		listing = ListingFacade.instance().getListing(admin, newListing.getListing().getId());
		assertNull("List should not exist, but we got " + listing.getListing(), listing.getListing());
		assertNotSame("We should get failure", ErrorCodes.OK, listing.getErrorCode());
	}
	
	@Test
	public void testDeleteUserNewListingWhenItDoesntExist() {
		ListingAndUserVO deletedListing = ListingFacade.instance().deleteNewListing(admin);
		assertNotSame("We should get an error", ErrorCodes.OK, deletedListing.getErrorCode());
		assertNull("Edited listing field should be null anyway", googleUserVO.getEditedListing());
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
		assertNull("Key was fake so listing should be null", returned.getListing());

		returned = ListingFacade.instance().getListing(googleUserVO, null);
		assertNull("Key was null so listing should be null", returned.getListing());
	}
	
	@Test
	public void testUpdateFailedListing() {
		assertFalse("Logged in user should not be an admin", googleUserVO.isAdmin());
		
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
		ListingAndUserVO postedListing = ListingFacade.instance().postListing(googleUserVO, listing.getId());
		assertNull("Withdrawn listing cannot be posted", postedListing.getListing());
		
		listing = DtoToVoConverter.convert(super.listingList.get(8));
		postedListing = ListingFacade.instance().postListing(googleUserVO, listing.getId());
		assertNull("Already posted listing cannot be posted", postedListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(12));
		postedListing = ListingFacade.instance().postListing(googleUserVO, listing.getId());
		assertNull("Closed listing cannot be posted", postedListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(7));
		postedListing = ListingFacade.instance().postListing(DtoToVoConverter.convert(userList.get(BIDDER1)), listing.getId());
		assertNull("New listing, but user is not an owner", postedListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(7));
		postedListing = ListingFacade.instance().postListing(googleUserVO, listing.getId());
		assertNotNull("New listing can be posted", postedListing.getListing());
		assertFalse("Activated listing should be a new instance of the object", listing == postedListing.getListing());
		assertEquals("State should be POSTED", Listing.State.POSTED.toString(), postedListing.getListing().getState());
		assertNotNull("Posted date should be set", postedListing.getListing().getPostedOn());
		assertEquals(listing.getName(), postedListing.getListing().getName());
		assertEquals(listing.getSummary(), postedListing.getListing().getSummary());
		assertEquals(listing.getOwner(), postedListing.getListing().getOwner());
		assertEquals(listing.getClosingOn(), postedListing.getListing().getClosingOn());
		assertEquals(listing.getSuggestedAmount(), postedListing.getListing().getSuggestedAmount());
		assertEquals(listing.getSuggestedPercentage(), postedListing.getListing().getSuggestedPercentage());
		assertEquals(listing.getPresentationId(), postedListing.getListing().getPresentationId());
		assertEquals(listing.getBuinessPlanId(), postedListing.getListing().getBuinessPlanId());
		assertEquals(listing.getFinancialsId(), postedListing.getListing().getFinancialsId());

		listing = DtoToVoConverter.convert(super.listingList.get(4));
		postedListing = ListingFacade.instance().postListing(googleUserVO, listing.getId());
		assertNull("Active listing cannot be posted", postedListing.getListing());

		listing = DtoToVoConverter.convert(super.listingList.get(5));
		postedListing = ListingFacade.instance().postListing(googleUserVO, listing.getId());
		assertNull("Active listing cannot be posted, additionally user is not an owner of the listing", postedListing.getListing());
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
