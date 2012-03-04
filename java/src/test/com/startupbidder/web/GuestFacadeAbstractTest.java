package test.com.startupbidder.web;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

public abstract class GuestFacadeAbstractTest extends BaseFacadeAbstractTest {
	protected LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalTaskQueueTestConfig(),
			new LocalUserServiceTestConfig(),
			new LocalDatastoreServiceTestConfig().setNoStorage(true))
				.setEnvIsAdmin(false).setEnvIsLoggedIn(false);

}
