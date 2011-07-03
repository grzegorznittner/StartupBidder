package test.com.startupbidder.jackson;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.startupbidder.dao.DatastoreDAO;
import com.startupbidder.dao.MockDatastoreDAO;
import com.startupbidder.vo.BusinessPlanVO;
import com.startupbidder.vo.DtoToVoConverter;

public class VoTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private DatastoreDAO datastore;
	
	@Before
	public void setUp() {
		helper.setUp();
		datastore = MockDatastoreDAO.getInstance();
	}
	
	@After
	public void tearDown() {
		helper.tearDown();
	}
	
	@Test
	public void arrayNodeTest() {
		List<BusinessPlanVO> bpList = DtoToVoConverter.convertBusinessPlans(datastore.getActiveBusinessPlans(10));
		ArrayNode rootNode = JsonNodeFactory.instance.arrayNode();
		
		assertEquals(7, bpList.size());
		
		for (BusinessPlanVO bp : bpList) {
			rootNode.addPOJO(bp);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(System.out, rootNode);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
