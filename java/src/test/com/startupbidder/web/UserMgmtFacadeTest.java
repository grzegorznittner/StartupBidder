package test.com.startupbidder.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.UserAndUserVO;
import com.startupbidder.vo.UserListVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.UserMgmtFacade;

public class UserMgmtFacadeTest extends BaseFacadeAbstractTest {
	private static final Logger log = Logger.getLogger(UserMgmtFacadeTest.class.getName());
	
	@Before
	public void setUp() {
		helper.setUp();
		
		//setupUsers();
		setupDatastore();
	}
	
	@After
	public void tearDown() {
		helper.tearDown();
	}
	
	@Test
	public void testGetLoggedInUserData() {
		log.info("google user: " + googleUser);
		UserVO user2 = UserMgmtFacade.instance().getLoggedInUserData(googleUser);
		assertEquals("Same user should be returned", mocks.JACOB.getId(), user2.getId());
	}

	@Test
	public void testGetUser() {
		UserAndUserVO returnedUser = UserMgmtFacade.instance().getUser(mocks.DRAGON, mocks.JACOB.getId());
		assertEquals("Id should be the same", mocks.JACOB.getId(), returnedUser.getUser().getId());
		assertEquals("Id should be the same", mocks.JACOB.getName(), returnedUser.getUser().getName());
		
		// different user gets the same user data
		UserAndUserVO returnedUser2 = UserMgmtFacade.instance().getUser(mocks.GREG, mocks.JACOB.getId());
		assertNotSame("Id should be different", returnedUser2.getUser().getId(), returnedUser.getUser().getId());
		assertNotSame("Name should be different", returnedUser2.getUser().getName(), returnedUser.getUser().getName());
		
		UserAndUserVO returnedUser3 = UserMgmtFacade.instance().getUser(mocks.DRAGON, "fakekey");
		assertNull("User should be empty for non existing web key", returnedUser3);
	}

	@Test
	public void testCreateUserViaGoogle() {
		User newUser = new User("admin@sb.com", "domain");
		UserVO createdUser = UserMgmtFacade.instance().createUser(newUser);
		//userList was not updated so don't use it in this test
		
		// @FIXME we should check if admin flag is set (currently not)
		
		assertNotNull("createUser should not return null", createdUser);
		assertEquals("createUser email should be the same as google one", createdUser.getEmail(), newUser.getEmail());
		assertNotNull("Modified date should be set", createdUser.getModified());
		assertNotNull("Last login date should be set", createdUser.getLastLoggedIn());
		assertNotNull("Joined date should be set", createdUser.getJoined());
		assertEquals("User created via google should be preactivated",
				SBUser.Status.ACTIVE.toString(), createdUser.getStatus());
		
		SBUser newSBUser = ObjectifyDatastoreDAO.getInstance().getUser(createdUser.getId());
		assertNotNull("User should not be null", newSBUser);
		assertNull("Activation code should be null for google account", newSBUser.activationCode);
		assertEquals("User's status should be ACTIVE for google account", SBUser.Status.ACTIVE, newSBUser.status);

		UserVO createdUser2 = UserMgmtFacade.instance().createUser(null);
		assertNull("createUser should return null", createdUser2);
		
		UserVO notCreated = UserMgmtFacade.instance().createUser(newUser);
		assertNull("User can be created only once for given email", notCreated);
		
	}

	@Test
	public void testCreateUser() {
		UserVO createdUser = UserMgmtFacade.instance()
				.createUser("new@sb.com", "password", "New User", "Outside city 34, Germany", false);
		//userList was not updated so don't use it in this test
		
		// @FIXME we should check if admin flag is set (currently not)
		
		assertNotNull("createUser should not return null for valid data", createdUser);
		assertEquals("createUser email should be the same as passed one", createdUser.getEmail(), "new@sb.com");
		assertNotNull("Modified date should be set", createdUser.getModified());
		assertNull("Last login date should not be set", createdUser.getLastLoggedIn());
		assertNotNull("Joined date should be set", createdUser.getJoined());
		assertEquals("User created via internal system should not be activated",
				SBUser.Status.CREATED.toString(), createdUser.getStatus());
		
		SBUser newSBUser = ObjectifyDatastoreDAO.getInstance().getUser(createdUser.getId());
		assertNotNull("User should not be null", newSBUser);
		assertNotNull("Activation code should be set", newSBUser.activationCode);
		assertEquals("User's status should be CREATED before activation", SBUser.Status.CREATED, newSBUser.status);

		UserVO notCreatedUser = UserMgmtFacade.instance().createUser(null, "password", "New User", "Outside city 34, Germany", false);
		assertNull("empty email should return null", notCreatedUser);
		notCreatedUser = UserMgmtFacade.instance().createUser("mike", "password", "New User", "Outside city 34, Germany", false);
		assertNull("wrong email should return null", notCreatedUser);
		notCreatedUser = UserMgmtFacade.instance().createUser("a@b", "password", "New User", "Outside city 34, Germany", false);
		assertNull("short email should return null", notCreatedUser);
		notCreatedUser = UserMgmtFacade.instance().createUser("fake@address.com", "pass", "New User", "Outside city 34, Germany", false);
		assertNull("password too short, should return null", notCreatedUser);
		notCreatedUser = UserMgmtFacade.instance().createUser("fake@address.com", "Password", "John Password", "Outside city 34, Germany", false);
		assertNull("password is part of name, should return null", notCreatedUser);
		notCreatedUser = UserMgmtFacade.instance().createUser("fake@address.com", "password", "User", "Outside city 34, Germany", false);
		assertNull("name too short, should return null", notCreatedUser);
		notCreatedUser = UserMgmtFacade.instance().createUser("new@sb.com", "password", "User", "Outside city 34, Germany", false);
		assertNull("email already registered, should return null", notCreatedUser);
	}

	@Test
	public void testGetAllUsers() {
		UserListVO allUsers = UserMgmtFacade.instance().getAllUsers(mocks.GREG);
		assertNotNull(allUsers.getUsers());
		assertEquals("Admin called getAllUsers, should return all created users", mocks.users.size(), allUsers.getUsers().size());
		assertTrue("allUsers should be sorted by email asceding order", 
				allUsers.getUsers().get(0).getEmail().compareTo(allUsers.getUsers().get(1).getEmail()) < 0);
		assertTrue("allUsers should be sorted by email asceding order",
				allUsers.getUsers().get(1).getEmail().compareTo(allUsers.getUsers().get(2).getEmail()) < 0);
		
		UserListVO allUsers2 = UserMgmtFacade.instance().getAllUsers(mocks.DRAGON);
		assertNotNull("allUsers called by non admin, should not return null", allUsers2);
		assertNotSame("We should get failure", allUsers2.getErrorCode(), ErrorCodes.OK);
		assertNotNull("allUsers called by non admin, should not return null user list", allUsers2.getUsers());
		assertEquals("allUsers called by non admin, should return empty allUser list", 0, allUsers2.getUsers().size());

		allUsers2 = UserMgmtFacade.instance().getAllUsers(null);
		assertNotNull("allUsers should not return null", allUsers2);
		assertNotSame("We should get failure", allUsers2.getErrorCode(), ErrorCodes.OK);
		assertNotNull("allUsers should not return null user list", allUsers2.getUsers());
		assertEquals("empty logged in user should cause empty allUser list", 0, allUsers2.getUsers().size());
	}

	@Test
	public void testActivateUser() {
		assertNotNull("User of index 8 should exist", mocks.users.get(8));
		assertEquals("User should be in state CREATED", SBUser.Status.CREATED, mocks.users.get(8).status);
		UserVO user = UserMgmtFacade.instance().activateUser(mocks.users.get(8).getWebKey(), mocks.users.get(8).activationCode);
		assertNotNull("Should not be null as activated correctly", user);
		assertEquals("Should be activated", SBUser.Status.ACTIVE.toString(), user.getStatus());
		
		UserVO newUser = UserMgmtFacade.instance().createUser("test@sb.com", "password", "Test User", "Dog Fields 13", false);
		SBUser newSBUser = ObjectifyDatastoreDAO.getInstance().getUser(newUser.getId());
		assertNotNull("User should not be null", newSBUser);
		assertNotNull("Activation code should be set", newSBUser.activationCode);
		assertEquals("User's status should be CREATED before activation", SBUser.Status.CREATED, newSBUser.status);
		
		// trying to activate with wrong code
		UserVO shouldBeNull = UserMgmtFacade.instance().activateUser(newUser.getId(), "wrongcode");
		assertNull("Trying to activate with wrong code, should be null", shouldBeNull);
		
		// proper activation
		UserVO activatedUser = UserMgmtFacade.instance().activateUser(newUser.getId(), newSBUser.activationCode);
		assertNotNull("Activate correctly, we should get actived user", activatedUser);
		assertEquals("And user should be active", SBUser.Status.ACTIVE.toString(), activatedUser.getStatus());
		assertEquals("Joined date should not be modified", newUser.getJoined(), activatedUser.getJoined());
		assertEquals("We haven't activated created account", "test@sb.com", activatedUser.getEmail());

		// trying to activate already activated account with valid code
		UserVO user3 = UserMgmtFacade.instance().activateUser(newUser.getId(), newSBUser.activationCode);
		assertNotNull("Already activated user should return user", user3);
		// trying to activate already activated account with invalid code
		UserVO user4 = UserMgmtFacade.instance().activateUser(newUser.getId(), newSBUser.activationCode);
		assertNotNull("Already activated user should return user", user4);
	}

	@Test
	public void testDectivateUser() {
		Date previousModifcation = mocks.DRAGON.getModified();
		UserVO user = UserMgmtFacade.instance().deactivateUser(mocks.DRAGON, mocks.DRAGON.getId());
		assertNotNull("Should not be null as deactivated correctly", user);
		assertEquals("Should be deactivated", SBUser.Status.DEACTIVATED.toString(), user.getStatus());
		assertTrue("Deactivation should change modification date", previousModifcation.getTime() < user.getModified().getTime());
	}

	@Test
	public void testValidUpdateUser() {
		UserVO previousUser = mocks.JACOB;
		UserVO user = UserMgmtFacade.instance().updateUser(mocks.JACOB, null, "New Nickname", null, null, null, null);
		assertNotNull("Name updated correctly", user);
		assertEquals("Name should not be touched", previousUser.getName(), user.getName());
		assertEquals("Nickname should be updated", "New Nickname", user.getNickname());
		assertEquals("Location should not be touched", previousUser.getLocation(), user.getLocation());
		assertEquals("Phone should not be touched", previousUser.getPhone(), user.getPhone());
		assertEquals("Investor flag should not be touched", previousUser.isAccreditedInvestor(), user.isAccreditedInvestor());
		assertEquals("NotifyEnabled flag should not be touched", previousUser.isNotifyEnabled(), user.isNotifyEnabled());
		assertTrue("Update should change modification date", previousUser.getModified().getTime() <= user.getModified().getTime());

		previousUser = user;
		user = UserMgmtFacade.instance().updateUser(mocks.JACOB, "Brand New Name", null, null, null, null, null);
		assertNotNull("Name updated correctly", user);
		assertEquals("Name should be updated", "Brand New Name", user.getName());
		assertEquals("Nickname should not be touched", previousUser.getNickname(), user.getNickname());
		assertEquals("Location should not be touched", previousUser.getLocation(), user.getLocation());
		assertEquals("Phone should not be touched", previousUser.getPhone(), user.getPhone());
		assertEquals("Investor flag should not be touched", previousUser.isAccreditedInvestor(), user.isAccreditedInvestor());
		assertEquals("NotifyEnabled flag should not be touched", previousUser.isNotifyEnabled(), user.isNotifyEnabled());
		assertTrue("Update should change modification date", previousUser.getModified().getTime() <= user.getModified().getTime());

		previousUser = user;
		user = UserMgmtFacade.instance().updateUser(mocks.JACOB, null, null, "Des Moines, IO, US", null, null, null);
		assertNotNull("Name updated correctly", user);
		assertEquals("Name should not be touched", previousUser.getName(), user.getName());
		assertEquals("Nickname should not be touched", previousUser.getNickname(), user.getNickname());
		assertEquals("Location should be updated", "Des Moines, IO, US", user.getLocation());
		assertEquals("Phone should not be updated", previousUser.getPhone(), user.getPhone());
		assertEquals("Investor flag should not be touched", previousUser.isAccreditedInvestor(), user.isAccreditedInvestor());
		assertEquals("NotifyEnabled flag should not be touched", previousUser.isNotifyEnabled(), user.isNotifyEnabled());
		assertTrue("Update should change modification date", previousUser.getModified().getTime() <= user.getModified().getTime());

		previousUser = user;
		user = UserMgmtFacade.instance().updateUser(mocks.JACOB, null, null, null, "1234567890", null, null);
		assertNotNull("Name updated correctly", user);
		assertEquals("Name should not be touched", previousUser.getName(), user.getName());
		assertEquals("Nickname should not be touched", previousUser.getNickname(), user.getNickname());
		assertEquals("Location should not be touched", previousUser.getLocation(), user.getLocation());
		assertEquals("Phone should be updated", "1234567890", user.getPhone());
		assertEquals("Investor flag should not be touched", previousUser.isAccreditedInvestor(), user.isAccreditedInvestor());
		assertEquals("NotifyEnabled flag should not be touched", previousUser.isNotifyEnabled(), user.isNotifyEnabled());
		assertTrue("Update should change modification date", previousUser.getModified().getTime() <= user.getModified().getTime());

		previousUser = user;
		user = UserMgmtFacade.instance().updateUser(mocks.JACOB, null, null, null, null, !previousUser.isAccreditedInvestor(), null);
		assertNotNull("Name updated correctly", user);
		assertEquals("Name should not be touched", previousUser.getName(), user.getName());
		assertEquals("Nickname should not be touched", previousUser.getNickname(), user.getNickname());
		assertEquals("Location should not be touched", previousUser.getLocation(), user.getLocation());
		assertEquals("Phone should not be touched", previousUser.getPhone(), user.getPhone());
		assertEquals("Investor flag should be updated", !previousUser.isAccreditedInvestor(), user.isAccreditedInvestor());
		assertEquals("NotifyEnabled flag should not be touched", previousUser.isNotifyEnabled(), user.isNotifyEnabled());
		assertTrue("Update should change modification date", previousUser.getModified().getTime() <= user.getModified().getTime());

		previousUser = user;
		user = UserMgmtFacade.instance().updateUser(mocks.JACOB, null, null, null, null, null, !previousUser.isNotifyEnabled());
		assertNotNull("Name updated correctly", user);
		assertEquals("Name should not be touched", previousUser.getName(), user.getName());
		assertEquals("Nickname should not be touched", previousUser.getNickname(), user.getNickname());
		assertEquals("Location should not be touched", previousUser.getLocation(), user.getLocation());
		assertEquals("Phone should not be touched", previousUser.getPhone(), user.getPhone());
		assertEquals("Investor flag should not be updated", previousUser.isAccreditedInvestor(), user.isAccreditedInvestor());
		assertEquals("NotifyEnabled flag should be updated", !previousUser.isNotifyEnabled(), user.isNotifyEnabled());
		assertTrue("Update should change modification date", previousUser.getModified().getTime() <= user.getModified().getTime());
	}

	@Test
	public void testInvalidUpdateUser() {
		UserVO user = UserMgmtFacade.instance().updateUser(mocks.JACOB, null, mocks.DRAGON.getNickname(), null, null, null, null);
		assertNull("Nick already exist", user);
		
		user = UserMgmtFacade.instance().updateUser(mocks.JACOB, "ab", null, null, null, null, null);
		assertNull("Name too short", user);

		user = UserMgmtFacade.instance().updateUser(mocks.JACOB, null, null, "Here", null, null, null);
		assertNull("Location too short", user);

		user = UserMgmtFacade.instance().updateUser(mocks.JACOB, null, null, null, "123", null, null);
		assertNull("Phone too short", user);
	}

	@Test
	public void testCheckUserCredentials() {
		UserVO createdUser = UserMgmtFacade.instance()
				.createUser("new@sb.com", "password", "New User", "Outside city 34, Germany", false);
		assertNotNull("createUser should not return null for valid data", createdUser);
		assertEquals("createUser email should be the same as passed one", createdUser.getEmail(), "new@sb.com");
		
		assertNotNull("Password is correct", UserMgmtFacade.instance().checkUserCredentials("new@sb.com", "password"));
		assertNull("Password is incorrect", UserMgmtFacade.instance().checkUserCredentials("new@sb.com", "wrongpass"));
		assertNull("Password is empty", UserMgmtFacade.instance().checkUserCredentials("new@sb.com", ""));
		assertNull("Password is null", UserMgmtFacade.instance().checkUserCredentials("new@sb.com", null));
	}

	@Test
	public void testCheckUserCredentialsForAuthCookie() {
		UserVO createdUser = UserMgmtFacade.instance()
				.createUser("new@sb.com", "password", "New User", "Outside city 34, Germany", false);
		assertNotNull("createUser should not return null for valid data", createdUser);
		assertEquals("createUser email should be the same as passed one", createdUser.getEmail(), "new@sb.com");
		
		SBUser createdSBUser = ObjectifyDatastoreDAO.getInstance().getUser(createdUser.getId());
		assertNotNull("user should exist", createdSBUser);
		
		assertNull("Not activated yet", UserMgmtFacade.instance().checkUserCredentials(createdSBUser.authCookie));
		
		UserMgmtFacade.instance().activateUser(createdUser.getId(), createdSBUser.activationCode);
		createdSBUser = ObjectifyDatastoreDAO.getInstance().getUser(createdUser.getId());
		assertNotNull("user should exist", createdSBUser);
		assertEquals("User should be active now", SBUser.Status.ACTIVE, createdSBUser.status);
		
		UserVO authUser = UserMgmtFacade.instance().checkUserCredentials(createdSBUser.authCookie);
		assertEquals("Should be created user", "new@sb.com", authUser.getEmail());
		
		assertNull("AuthCookie is not correct", UserMgmtFacade.instance().checkUserCredentials("fakekey"));
		
		String newAuthCookie = UserMgmtFacade.instance().changePassword(createdSBUser.email, "password", "anewpassword");
		assertNull("Old authCookie used", UserMgmtFacade.instance().checkUserCredentials(createdSBUser.authCookie));
		assertNotNull("New authCookie used, should be ok", UserMgmtFacade.instance().checkUserCredentials(newAuthCookie));
	}

	@Test
	public void testChangePassword() {
		UserVO createdUser = UserMgmtFacade.instance()
				.createUser("new@halloo.com", "password", "New User", "Outside city 34, Germany", false);
		assertNotNull("createUser should not return null for valid data", createdUser);
		assertEquals("createUser email should be the same as passed one", createdUser.getEmail(), "new@halloo.com");
		
		SBUser createdSBUser = ObjectifyDatastoreDAO.getInstance().getUser(createdUser.getId());
		assertNotNull("user should exist", createdSBUser);
		
		String authCookie = UserMgmtFacade.instance().changePassword(createdSBUser.email, "password", "alamakota");
		assertNotSame("AuthCookie should be different", createdSBUser.authCookie, authCookie);
		
		SBUser createdSBUser2 = ObjectifyDatastoreDAO.getInstance().getUser(createdUser.getId());
		assertNotSame("Password MD5 should be different", createdSBUser.password, createdSBUser2.password);
		
		String authCookie2 = UserMgmtFacade.instance().changePassword(createdSBUser.email, "password", "alamakota");
		assertNull("AuthCookie should be null as password was wrong", authCookie2);
		
		SBUser createdSBUser3 = ObjectifyDatastoreDAO.getInstance().getUser(createdUser.getId());
		assertEquals("Password MD5 should be the same", createdSBUser2.password, createdSBUser3.password);
		
	}
}
