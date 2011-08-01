package test.com.startupbidder;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.ListingDocumentDTO;
import com.startupbidder.dto.ListingRankDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.VoteDTO;

public class EntityConverterTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	
	@Before
	public void setUp() {
		helper.setUp();
	}
	
	@After
	public void tearDown() {
		//helper.tearDown();
	}

	@Test
	public void testBid() {
		BidDTO bid = new BidDTO();
		bid.createKey("" + bid.hashCode());
		bid.setFundType(BidDTO.FundType.NOTE);
		bid.setListing("listing_id");
		bid.setPercentOfCompany(44);
		bid.setPlaced(new Date(100000));
		bid.setStatus(BidDTO.Status.WITHDRAWN);
		bid.setUser("user_id");
		bid.setValuation(99999);
		bid.setValue(44444);
		
		System.out.println("Original: " + bid);
		
		Entity bidEntity = bid.toEntity();
		BidDTO newBid = BidDTO.fromEntity(bidEntity);

		System.out.println("Recreated: " + newBid);
		
		Assert.assertEquals(bid, newBid);
	}
	
	@Test
	public void testComment() {
		CommentDTO comment = new CommentDTO();
		comment.createKey("" + comment.hashCode());
		comment.setComment("comment");
		comment.setCommentedOn(new Date());
		comment.setListing("listing_id");
		comment.setUser("user_id");
		
		System.out.println("Original: " + comment);
		
		Entity bidEntity = comment.toEntity();
		CommentDTO newComment = CommentDTO.fromEntity(bidEntity);

		System.out.println("Recreated: " + newComment);
		
		Assert.assertEquals(comment, newComment);
	}
	
	@Test
	public void testListing() {
		ListingDTO listing = new ListingDTO();
		listing.createKey("" + listing.hashCode());
		listing.setClosingOn(new Date(9999999));
		listing.setListedOn(new Date());
		listing.setName("listing name");
		listing.setOwner("owner id");
		listing.setState(ListingDTO.State.WITHDRAWN);
		listing.setSuggestedAmount(9999999);
		listing.setSuggestedPercentage(49);
		listing.setSuggestedValuation(456789);
		listing.setSummary("summary");
		
		System.out.println("Original: " + listing);
		
		Entity listingEntity = listing.toEntity();
		ListingDTO newListing = ListingDTO.fromEntity(listingEntity);

		System.out.println("Recreated: " + newListing);
		
		Assert.assertEquals(listing, newListing);
	}
	
	@Test
	public void testUser() {
		UserDTO user = new UserDTO();
		user.createKey("" + user.hashCode());
		user.setEmail("email");
		user.setFacebook("facebook");
		user.setInvestor(true);
		user.setJoined(new Date(4444444));
		user.setLastLoggedIn(new Date(222222));
		user.setLinkedin("linkedin");
		user.setModified(new Date(333333));
		user.setName("name");
		user.setNickname("nickame");
		user.setOpenId("open_id");
		user.setOrganization("organization");
		user.setStatus(UserDTO.Status.DEACTIVATED);
		user.setTitle("title");
		user.setTwitter("twitter");
		
		System.out.println("Original: " + user);
		
		Entity userEntity = user.toEntity();
		UserDTO newUser = UserDTO.fromEntity(userEntity);

		System.out.println("Recreated: " + newUser);
		
		Assert.assertEquals(user, newUser);
	}
	
	@Test
	public void testVote() {
		VoteDTO vote = new VoteDTO();
		vote.createKey("" + vote.hashCode());
		vote.setCommentedOn(new Date());
		vote.setListing("listing_id");
		vote.setUser("user_id");
		vote.setValue(1);
		
		System.out.println("Original: " + vote);
		
		Entity voteEntity = vote.toEntity();
		VoteDTO newVote = VoteDTO.fromEntity(voteEntity);

		System.out.println("Recreated: " + newVote);
		
		Assert.assertEquals(vote, newVote);

	}

	@Test
	public void testListingRank() {
		ListingRankDTO rank = new ListingRankDTO();
		rank.createKey("" + rank.hashCode());
		rank.setDate(new Date());
		rank.setListing("listing_id");
		rank.setRank(1);
		
		System.out.println("Original: " + rank);
		
		Entity rankEntity = rank.toEntity();
		ListingRankDTO newRank = ListingRankDTO.fromEntity(rankEntity);

		System.out.println("Recreated: " + newRank);
		
		Assert.assertEquals(rank, newRank);
	}
	
	@Test
	public void testListingDocument() {
		ListingDocumentDTO doc = new ListingDocumentDTO();
		doc.createKey("" + doc.hashCode());
		doc.setBlob("blob");
		doc.setCreated(new Date());
		doc.setListing("listing_id");
		doc.setState(ListingDocumentDTO.State.UPLOADED);
		doc.setType(ListingDocumentDTO.Type.PRESENTATION);
		
		System.out.println("Original: " + doc);
		
		Entity docEntity = doc.toEntity();
		ListingDocumentDTO newDoc = ListingDocumentDTO.fromEntity(docEntity);

		System.out.println("Recreated: " + newDoc);
		
		Assert.assertEquals(doc, newDoc);
	}
}

