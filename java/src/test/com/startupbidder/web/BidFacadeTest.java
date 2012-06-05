package test.com.startupbidder.web;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.startupbidder.vo.UserVO;

public class BidFacadeTest extends BaseFacadeAbstractTest {
	private static final Logger log = Logger.getLogger(BidFacadeTest.class.getName());
	
	@Before
	public void setUp() {
		setupDatastore();
		setupBids();
	}
	
	@After
	public void tearDown() {
		tearDownDatastore();
	}
	
	/**
	 * Keep in mind the following facts:
	 *  BIDDER1 == 2
	 *  BIDDER2 == 0
	 *  BIDDER3 == OWNER1 == 1
	 *  OWNER2 == 3
	 *  
	 *  LISTING1_OWNER1 bid by BIDDER1 => offer, offer, offer, cancel by bidder, offer, offer, offer, cancel by owner
	 *  LISTING1_OWNER1 bid by BIDDER2 => offer
	 *  LISTING2_OWNER2 bid by BIDDER1 => offer, offer
	 *  LISTING3_OWNER2 bid by BIDDER1 => offer, offer, offer, accepted but listing is still in ACTIVE state
	 */
	
	@Test
	public void testMakeBidScenarioForListing1AndBidder2() {
		// scenario for LISTING1_OWNER1 and BIDDER2
		// offer B (fail), update B, counter O, counter O (fail), update O, FREEZE LISTING, counter B (fail), accept O (fail), cancel O (fail)
		//    DEFREEZE LISTING, counter B, WITHDRAW LISTING, accept O (fail), cancel O (fail), counter B (fail)
		
		UserVO owner1 = mocks.GREG;
		UserVO bidder1 = mocks.INSIDER;
		UserVO bidder2 = mocks.DRAGON;
		/*
		OldBidVO bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER2, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE, 0, 3, 20000, 30));
		OldBidsForListingVO bids = OldBidFacade.instance().makeBid(bidder2, bid);
		assertTrue("Activate from bidder2 should be rejected, as there is already active bid", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER2, OldBid.Actor.BIDDER, OldBid.Action.UPDATE, 0, 3, 20500, 30));
		bids = OldBidFacade.instance().makeBid(bidder2, bid);
		assertTrue("Update from bidder2 should be accepted", bids != null && bids.getErrorCode() == ErrorCodes.OK);
		printBids("After update from bidder2.", bids);

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER2, OldBid.Actor.OWNER, OldBid.Action.ACTIVATE, 0, 3, 31000, 30));
		bids = OldBidFacade.instance().makeBid(owner1, bid);
		assertTrue("Counter offer from owner, should be allowed", bids != null && bids.getErrorCode() == ErrorCodes.OK);
		printBids("After counter offer from owner.", bids);

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER2, OldBid.Actor.OWNER, OldBid.Action.ACTIVATE, 0, 3, 33000, 30));
		bids = OldBidFacade.instance().makeBid(owner1, bid);
		assertTrue("Resubmit of counter offer, should be rejected", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER2, OldBid.Actor.OWNER, OldBid.Action.UPDATE, 0, 3, 33000, 30));
		bids = OldBidFacade.instance().makeBid(owner1, bid);
		assertTrue("Update for countered offer, should be allowed", bids != null && bids.getErrorCode() == ErrorCodes.OK);
		printBids("After update for counter offer.", bids);

		// freezing listing
		ListingVO listing = DtoToVoConverter.convert(super.listingList.get(LISTING1_OWNER1));
		ListingVO freezedListing = ListingFacade.instance().freezeListing(mocks.JOHN, listing.getId()).getListing();
		assertNotNull("Listing freezed", freezedListing);
		assertEquals(Listing.State.FROZEN.toString(), freezedListing.getState());
		
		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER2, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE, 0, 3, 31000, 30));
		bids = OldBidFacade.instance().makeBid(bidder2, bid);
		assertTrue("Counter offer for frozen listing, should be rejected", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER2, OldBid.Actor.OWNER, OldBid.Action.ACCEPT, 0, 0, 0, 0));
		bids = OldBidFacade.instance().makeBid(owner1, bid);
		assertTrue("Accept for frozen listing, should be rejected", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER2, OldBid.Actor.OWNER, OldBid.Action.CANCEL, 0, 0, 0, 0));
		bids = OldBidFacade.instance().makeBid(owner1, bid);
		assertTrue("Cancel for frozen listing, should be rejected", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);

		// reactivating listing
		freezedListing = ListingFacade.instance().activateListing(mocks.JOHN, listing.getId()).getListing();
		assertNotNull("Listing reactivated", freezedListing);
		assertEquals(Listing.State.ACTIVE.toString(), freezedListing.getState());
		
		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER2, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE, 0, 3, 31000, 30));
		bids = OldBidFacade.instance().makeBid(bidder2, bid);
		assertTrue("Counter offer for reactivated listing, should be allowed", bids != null && bids.getErrorCode() == ErrorCodes.OK);
		printBids("After counter from bidder2.", bids);

		// withdrawing listing
		freezedListing = ListingFacade.instance().withdrawListing(owner1, listing.getId()).getListing();
		assertNotNull("Listing withdrawn", freezedListing);
		assertEquals(Listing.State.WITHDRAWN.toString(), freezedListing.getState());

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER2, OldBid.Actor.OWNER, OldBid.Action.ACCEPT, 0, 0, 0, 0));
		bids = OldBidFacade.instance().makeBid(owner1, bid);
		assertTrue("Accept for withdrawn listing, should be rejected", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER2, OldBid.Actor.OWNER, OldBid.Action.CANCEL, 0, 0, 0, 0));
		bids = OldBidFacade.instance().makeBid(owner1, bid);
		assertTrue("Cancel for withdrawn listing, should be rejected", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER2, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE, 0, 3, 31000, 30));
		bids = OldBidFacade.instance().makeBid(bidder2, bid);
		assertTrue("Counter offer for withdrawn listing, should be allowed", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);
		*/
	}
	
	@Test
	public void testMakeBidScenarioForListing1AndBidder1() {
		// scenario for LISTING1_OWNER1 and BIDDER1
		// offer B, offer B (fail), counter O, counter O (fail), counter B, accept O, counter O (fail), accept O (fail), cancel O (fail), cancel B (fail), offer B2 (fail)
		
		UserVO owner1 = mocks.GREG;
		UserVO bidder1 = mocks.INSIDER;
		UserVO bidder2 = mocks.DRAGON;
		
		/*
		OldBidVO bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE, 0, 3, 30000, 30));
		OldBidsForListingVO bids = OldBidFacade.instance().makeBid(bidder1, bid);
		assertTrue("New bid for listing, should be allowed", bids != null && bids.getErrorCode() == ErrorCodes.OK);
		printBids("After new bid from bidder1.", bids);
		
		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE, 0, 3, 31000, 30));
		bids = OldBidFacade.instance().makeBid(bidder1, bid);
		assertTrue("Tried to make the same bid second time, not allowed", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);
		
		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.OWNER, OldBid.Action.ACTIVATE, 0, 3, 35000, 30));
		bids = OldBidFacade.instance().makeBid(owner1, bid);
		assertTrue("Counter offer from owner, should be allowed", bids != null && bids.getErrorCode() == ErrorCodes.OK);
		printBids("After counter offer from owner.", bids);

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.OWNER, OldBid.Action.ACTIVATE, 0, 3, 35000, 30));
		bids = OldBidFacade.instance().makeBid(owner1, bid);
		assertTrue("Second counter offer from owner, not allowed", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE, 0, 3, 32000, 30));
		bids = OldBidFacade.instance().makeBid(bidder1, bid);
		assertTrue("Counter offer from bidder, should be allowed", bids != null && bids.getErrorCode() == ErrorCodes.OK);
		printBids("After counter offer from bidder1.", bids);

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.OWNER, OldBid.Action.ACCEPT, 0, 3, 32000, 30));
		bids = OldBidFacade.instance().makeBid(owner1, bid);
		assertTrue("Owner accepts offer, should be allowed", bids != null && bids.getErrorCode() == ErrorCodes.OK);

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.OWNER, OldBid.Action.ACTIVATE, 0, 3, 33000, 30));
		bids = OldBidFacade.instance().makeBid(owner1, bid);
		assertTrue("Owner tries to counter already accepted offer, should be rejected", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);
		
		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.OWNER, OldBid.Action.ACCEPT, 0, 3, 33000, 30));
		bids = OldBidFacade.instance().makeBid(owner1, bid);
		assertTrue("Owner tries to accept already accepted offer, should be rejected", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);
		
		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.OWNER, OldBid.Action.CANCEL, 0, 0, 0, 0));
		bids = OldBidFacade.instance().makeBid(owner1, bid);
		assertTrue("Owner tries to cancel already accepted offer, should be rejected", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER1, OldBid.Actor.BIDDER, OldBid.Action.CANCEL, 0, 0, 0, 0));
		bids = OldBidFacade.instance().makeBid(bidder1, bid);
		assertTrue("Bidder tries to cancel already accepted bid, should be rejected", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);

		bid = DtoToVoConverter.convert(prepareBid(LISTING1_OWNER1, OWNER1, BIDDER2, OldBid.Actor.BIDDER, OldBid.Action.ACTIVATE, 0, 3, 34000, 30));
		bids = OldBidFacade.instance().makeBid(bidder2, bid);
		assertTrue("Other bidder tries to make new bid for already accepted bid, should be rejected", bids != null && bids.getErrorCode() == ErrorCodes.OPERATION_NOT_ALLOWED);
		*/
	}
	
	@Test
	public void testGetAllBids() {
		UserVO owner1 = mocks.GREG;
		UserVO bidder1 = mocks.INSIDER;
		UserVO bidder2 = mocks.DRAGON;
		/*
		OldBidListVO bidList = OldBidFacade.instance().getBidsForListing(owner1, listingList.get(LISTING1_OWNER1).getWebKey(), new ListPropertiesVO());
		assertNotNull(bidList);
		assertNotNull(bidList.getBids());
		printBids("Get bid list for LISTING1_OWNER1 by owner1.", bidList.getBids());
		*/
	}
	
	/*
	void printBids(String text, OldBidsForListingVO bids) {
		StringBuffer buf = new StringBuffer();
		ListingVO listing = bids.getListing();
		buf.append(text).append(" Bids for listing '").append(listing.getName()).append("', ").append(listing.getState()).append(" :\n");
		for (Entry<String, List<OldBidVO>> perUser: bids.getBidsPerUser().entrySet()) {
			buf.append("   user: ").append(perUser.getKey()).append(" size: ").append(perUser.getValue().size()).append(" :\n");
			for (OldBidVO bid : perUser.getValue()) {
				buf.append("      ").append(bid.getUserName()).append(" ").append(bid.getActor()).append(" ").append(bid.getAction())
					.append(" ").append(bid.getComment()).append("\n");
			}
		}
		log.info(buf.toString());
	}
		*/
	
	/*
	void printBids(String text, List<OldBidVO> bids) {
		StringBuffer buf = new StringBuffer();
		buf.append(text).append(" :\n");
		for (OldBidVO bid : bids) {
			buf.append("      ").append(bid.getUserName()).append(" ").append(bid.getActor()).append(" ").append(bid.getAction())
				.append(" ").append(bid.getComment()).append("\n");
		}
		log.info(buf.toString());
	}
		*/
}
