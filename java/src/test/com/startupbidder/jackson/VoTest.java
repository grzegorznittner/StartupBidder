package test.com.startupbidder.jackson;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.startupbidder.vo.CommentListVO;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingListVO;
import com.startupbidder.web.ServiceFacade;

public class VoTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private ServiceFacade service = ServiceFacade.instance();
	private UserService userService = null;
	private User user;
	
	@Before
	public void setUp() {
		helper.setUp();
		userService = UserServiceFactory.getUserService();
		user = userService.getCurrentUser();
	}
	
	@After
	public void tearDown() {
		helper.tearDown();
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
}
