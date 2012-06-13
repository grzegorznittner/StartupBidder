package test.com.startupbidder.datamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.BidUser;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.ListingStats;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.Rank;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.SystemProperty;
import com.startupbidder.datamodel.UserStats;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.datamodel.Vote;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.UserVO;

public class ModelConverterTest {
	protected LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalTaskQueueTestConfig(),
			new LocalUserServiceTestConfig(),
			new LocalDatastoreServiceTestConfig().setNoStorage(true))
				.setEnvIsAdmin(false).setEnvIsLoggedIn(true)
				.setEnvEmail("user@startupbidder.com").setEnvAuthDomain("google.com");

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
		ObjectifyService.register(Rank.class);
		ObjectifyService.register(SystemProperty.class);
		ObjectifyService.register(Vote.class);
	}

	@Before
	public void setUp() {
		helper.setUp();
	}
	
	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Test
	public void testConvertListing() {
		DateTime now = new DateTime();
		
		Listing before = new Listing();
		before.id = 5555L;
		before.name = "Name";
		before.mockData = true;
		before.owner = new Key<SBUser>(SBUser.class, 3001L);
		before.state = Listing.State.FROZEN;
		before.address = "Full address";
		before.summary = "This is listing summary";
		before.suggestedAmount = 59230;
		before.suggestedPercentage = 34;
		before.suggestedValuation = 59230 * 100 / 34; 

		/*
		 * Those properties are not propagated to VO
		before.city = "City";
		before.country = "Country";
		before.usCounty = "County";
		before.usState = "State";
		 */
		
		before.closingOn = now.plusDays(29).toDate();
		before.listedOn = now.minusDays(4).toDate();
		before.posted = now.minusDays(2).toDate();
		//before.modified = new Date();
		
		before.businessPlanId = new Key<ListingDoc>(ListingDoc.class, 4000L);
		before.financialsId = new Key<ListingDoc>(ListingDoc.class, 4000L);
		before.presentationId = new Key<ListingDoc>(ListingDoc.class, 4000L);
		
		ListingVO intrnVO = DtoToVoConverter.convert(before);
		Listing after = VoToModelConverter.convert(intrnVO);
		
		assertEquals(before.id, after.id);
		assertEquals(before.name, after.name);
		assertEquals(before.mockData, after.mockData);
		assertEquals(before.owner, after.owner);
		assertEquals(before.state, after.state);
		assertEquals(before.address, after.address);
		assertEquals(before.summary, after.summary);
		assertEquals(before.suggestedAmount, after.suggestedAmount);
		assertEquals(before.suggestedPercentage, after.suggestedPercentage);
		assertEquals(before.suggestedValuation, after.suggestedValuation);
		assertEquals(before.closingOn, after.closingOn);
		assertEquals(before.listedOn, after.listedOn);
		assertEquals(before.posted, after.posted);
		// modified date is not passed to VO
		// assertEquals(before.modified, after.modified);
		assertEquals(before.businessPlanId, after.businessPlanId);
		assertEquals(before.financialsId, after.financialsId);
		assertEquals(before.presentationId, after.presentationId);
				
		assertTrue("Before: " + ToStringBuilder.reflectionToString(before) + "\n"
				+ " After: " + ToStringBuilder.reflectionToString(after) + "\n"
				+ " VO: " + ToStringBuilder.reflectionToString(intrnVO) + "\n", EqualsBuilder.reflectionEquals(before, after));
	}

	@Test
	public void testConvertComment() {
		DateTime now = new DateTime();
		Comment before = new Comment();
		before.id = 34534L;
		before.comment = "comment";
		before.commentedOn = now.plusDays(3).toDate();
		before.listing = new Key<Listing>(Listing.class, 3434L);
		before.mockData = true;
		// parent id is not passed to VO yet
		//before.parent = new Key<Comment>(Comment.class, 345346L);
		before.user = new Key<SBUser>(SBUser.class, 678678L);
		before.userNickName = "username";

		CommentVO intrnVO = DtoToVoConverter.convert(before);
		Comment after = VoToModelConverter.convert(intrnVO);
		
		assertEquals(before.id, after.id);
		assertEquals(before.mockData, after.mockData);
		assertEquals(before.comment, after.comment);
		assertEquals(before.commentedOn, after.commentedOn);
		assertEquals(before.listing, after.listing);
		assertEquals(before.mockData, after.mockData);
		assertEquals(before.user, after.user);
		assertEquals(before.mockData, after.mockData);
		assertEquals(before.userNickName, after.userNickName);
				
		assertTrue("Before: " + ToStringBuilder.reflectionToString(before) + "\n"
				+ " After: " + ToStringBuilder.reflectionToString(after) + "\n"
				+ " VO: " + ToStringBuilder.reflectionToString(intrnVO) + "\n", EqualsBuilder.reflectionEquals(before, after));
	}

	@Test
	public void testConvertSBUser() {
		DateTime now = new DateTime();
		SBUser before = new SBUser();
		before.id = 34534L;
		before.mockData = true;
		before.name = "username";
		before.email = "email address";
		before.nickname = "nickname";
		before.admin = true;
		before.investor = true;
		before.joined = now.minusDays(40).toDate();
		before.lastLoggedIn = now.minusDays(1).toDate();
		before.location = "User's location";
		before.modified = now.toDate();
		before.notifyEnabled = true;
		before.phone = "345346456754";
		before.status = SBUser.Status.DEACTIVATED;

		UserVO intrnVO = DtoToVoConverter.convert(before);
		SBUser after = VoToModelConverter.convert(intrnVO);
		
		assertEquals(before.id, after.id);
		assertEquals(before.mockData, after.mockData);
		assertEquals(before.name, after.name);
		assertEquals(before.email, after.email);
		assertEquals(before.nickname, after.nickname);
		assertEquals(before.mockData, after.mockData);
		assertEquals(before.admin, after.admin);
		assertEquals(before.authCookie, after.authCookie);
		assertEquals(before.investor, after.investor);
		assertEquals(before.lastLoggedIn, after.lastLoggedIn);
		assertEquals(before.location, after.location);
		assertEquals(before.modified, after.modified);
		assertEquals(before.notifyEnabled, after.notifyEnabled);
		assertEquals(before.phone, after.phone);
		assertEquals(before.status, after.status);
				
		assertTrue("Before: " + ToStringBuilder.reflectionToString(before) + "\n"
				+ " After: " + ToStringBuilder.reflectionToString(after) + "\n"
				+ " VO: " + ToStringBuilder.reflectionToString(intrnVO) + "\n", EqualsBuilder.reflectionEquals(before, after));
	}

}
