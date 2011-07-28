package test.com.startupbidder.jackson;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.startupbidder.vo.CommentListVO;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingListVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.ServiceFacade;

public class VoTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private ServiceFacade service = ServiceFacade.instance();
	private UserService userService = null;
	private UserVO user;
	
	@Before
	public void setUp() {
		helper.setUp();
		userService = UserServiceFactory.getUserService();
		if(userService.getCurrentUser() != null) {
			user = service.getLoggedInUserData(userService.getCurrentUser());
			if (user == null) {
				user = service.createUser(userService.getCurrentUser());
			}
		}
	}
	
	@After
	public void tearDown() {
		//helper.tearDown();
	}
	
	@Test
	public void arrayNodeTest() {
		ListPropertiesVO listingProperties = new ListPropertiesVO();
		listingProperties.setMaxResults(10);
		ListingListVO listings = service.getTopListings(user, listingProperties);
		
		CommentListVO comments = service.getCommentsForListing(user, listings.getListings().get(0).getId(), listingProperties);
		
		//assertEquals(7, bpList.size());
				
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(System.out, listings);
			System.out.println();
			mapper.writeValue(System.out, comments);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void deserializeTest() throws JsonParseException, JsonMappingException, IOException {
		String request = "{\"listing_id\":\"bc643edc-8fe3-4aef-a8d7-2c6eb654b9bd\",\"title\":\"Enter listing title here\",\"median_valuation\":\"0\",\"num_votes\":\"0\",\"num_bids\":\"0\",\"num_comments\":\"0\",\"profile_id\":\"ag1zdGFydHVwYmlkZGVych4LEgRVc2VyIhQxODU4MDQ3NjQyMjAxMzkxMjQxMQw\",\"profile_username\":\"test@example.com\",\"listing_date\":\"20110728\",\"closing_date\":\"20110728\",\"status\":\"new\",\"suggested_amt\":\"10000\",\"suggested_pct\":\"10\",\"suggested_val\":100000,\"summary\":\"Enter listing summary here.\",\"business_plan_url\":\"\",\"presentation_url\":\"\"}";
		ObjectMapper mapper = new ObjectMapper();
		ListingVO listing = mapper.readValue(request.getBytes(), ListingVO.class);
		System.out.println(listing);
	}
}
