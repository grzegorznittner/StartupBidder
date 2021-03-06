package com.startupbidder.web;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.UserStats;
import com.startupbidder.util.FacebookUser;
import com.startupbidder.util.ImageHelper;
import com.startupbidder.vo.BaseVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingTileVO;
import com.startupbidder.vo.UserAndUserVO;
import com.startupbidder.vo.UserBasicVO;
import com.startupbidder.vo.UserListVO;
import com.startupbidder.vo.UserListingsForAdminVO;
import com.startupbidder.vo.UserListingsForUsersVO;
import com.startupbidder.vo.UserShortListVO;
import com.startupbidder.vo.UserShortVO;
import com.startupbidder.vo.UserDataUpdatable;
import com.startupbidder.vo.UserVO;

public class UserMgmtFacade {
	private static final Logger log = Logger.getLogger(UserMgmtFacade.class.getName());
	
	public enum UpdateReason {BID_UPDATE, NEW_BID, NEW_COMMENT, NEW_LISTING, NEW_VOTE, NONE};
	
	private static UserMgmtFacade instance;
	
	private DateTimeFormatter timeStampFormatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss_SSS");
	
	public static UserMgmtFacade instance() {
		if (instance == null) {
			instance = new UserMgmtFacade();
		}
		return instance;
	}
	
	private UserMgmtFacade() {
	}
	
	private ObjectifyDatastoreDAO getDAO() {
		return ObjectifyDatastoreDAO.getInstance();
	}

	public UserVO getLoggedInUser(User loggedInUser) {
		if (loggedInUser == null) {
			return null;
		}
		String email = loggedInUser.getEmail();
		SBUser userDTO = null;
		
		if (StringUtils.isNotEmpty(email)) {
			userDTO = getDAO().getUserByEmail(email);
		} else {
			userDTO = getDAO().getUserByEmail(loggedInUser.getUserId());
		}
		if (userDTO == null) {
			return null;
		}
		userDTO = updateUserGoogleData(loggedInUser, userDTO);
		UserVO user = DtoToVoConverter.convert(userDTO);
		
		if (StringUtils.isEmpty(user.getName())) {
			String parts[] = StringUtils.split(user.getEmail(), "@");
			if (parts != null && parts.length > 0) {
				user.setName(parts[0]);
			} else {
				user.setName("not provided");
			}
		}
		applyUserStatistics(user, user);
		return user;
	}
	
	public UserVO getLoggedInUser(String email) {
		if (email == null) {
			return null;
		}
		UserVO user = DtoToVoConverter.convert(getDAO().getUserByEmail(email));
		if (user == null) {
			return null;
		}
		applyUserStatistics(user, user);
		return user;
	}

	public UserVO getLoggedInUser(FacebookUser fbUser) {
		if (fbUser == null) {
			return null;
		}
		SBUser userDTO = getDAO().getUserByEmail(fbUser.getEmail());
		if (userDTO == null) {
			return null;
		}
		userDTO = updateUserFacebookData(fbUser, userDTO);
		UserVO user = DtoToVoConverter.convert(userDTO);
		applyUserStatistics(user, user);
		return user;
	}

	public UserVO getLoggedInUser(twitter4j.User twitterUser) {
		if (twitterUser == null) {
			return null;
		}
		SBUser userDTO = getDAO().getUserByTwitter(twitterUser.getId());
		if (userDTO == null) {
			return null;
		}
		userDTO = updateUserTwitterData(twitterUser, userDTO);
		UserVO user = DtoToVoConverter.convert(userDTO);
		applyUserStatistics(user, user);
		return user;
	}

	public UserVO requestEmailUpdate(twitter4j.User twitterUser, String email) {
		if (twitterUser == null) {
			return null;
		}
		SBUser userByTwitter = getDAO().getUserByTwitter(twitterUser.getId());
		if (userByTwitter == null) {
			log.warning("User with twitter id " + twitterUser.getId() + " doesn't exist!");
			return null;
		}
		if (!StringUtils.isEmpty(userByTwitter.email)) {
			log.warning("User with twitter id " + twitterUser.getId() + " has already set email. User: " + userByTwitter);
			return null;
		}
		userByTwitter = getDAO().prepareUpdateUsersEmailByTwitter(userByTwitter, email);
		EmailService.instance().sendEmailVerification(userByTwitter);
		
		return DtoToVoConverter.convert(userByTwitter);
	}

	public UserVO confirmEmailUpdate(String twitterIdString, String token) {
		if (StringUtils.isEmpty(twitterIdString) || StringUtils.isEmpty(token)) {
			return null;
		}
		long twitterId = Long.parseLong(twitterIdString);
		SBUser twitterUser = getDAO().getUserByTwitter(twitterId);
		if (twitterUser != null) {
			if (StringUtils.equals(token, twitterUser.activationCode)) {
				twitterUser = getDAO().updateUsersEmailByTwitter(twitterUser, twitterUser.twitterEmail);
				return DtoToVoConverter.convert(twitterUser);
			} else {
				log.warning("Confirmation token provided for Twitter user is not valid. Token: " + token
						+ " User: " + twitterUser);
			}
		} else {
			log.warning("Twitter user " + twitterId + " not found in datastore!");
		}
		return null;
	}

	/**
	 * Returns user data object by userId
	 * 
	 * @param userId User identifier
	 * @return User data as JsonNode
	 */
	public UserListingsForAdminVO getUser(UserVO loggedInUser, String userId) {
		UserListingsForAdminVO result = new UserListingsForUsersVO();
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			result = new UserListingsForAdminVO();
		}

		UserVO user = DtoToVoConverter.convert(getDAO().getUser(userId));
		if (user == null) {
			result.setErrorCode(ErrorCodes.APPLICATION_ERROR);
			result.setErrorMessage("User with id '" + userId + "' doesn't exist!");
			log.info("User with id '" + userId + "' doesn't exist!");
			return result;
		}
		ListPropertiesVO props = new ListPropertiesVO();
		
		List<ListingTileVO> allListings = new ArrayList<ListingTileVO>();

		props.setMaxResults(4);
		List<ListingTileVO> activeListings = ListingFacade.instance().prepareListingList(
				getDAO().getUserListings(user.toKeyId(), Listing.State.ACTIVE, props));
		if (activeListings.size() > 0) {
			result.setActiveListings(activeListings);
			allListings.addAll(activeListings);
		}

		if (loggedInUser != null && (loggedInUser.isAdmin() || user.toKeyId() == loggedInUser.toKeyId())) {
			props = new ListPropertiesVO();
			props.setMaxResults(4);
			List<ListingTileVO> withdrawnListings = ListingFacade.instance().prepareListingList(
					getDAO().getUserListings(user.toKeyId(), Listing.State.WITHDRAWN, props));
			if (withdrawnListings.size() > 0) {
				result.setWithdrawnListings(withdrawnListings);
				allListings.addAll(withdrawnListings);
			}

			props = new ListPropertiesVO();
			props.setMaxResults(4);
			List<ListingTileVO> frozenListings = ListingFacade.instance().prepareListingList(
					getDAO().getUserListings(user.toKeyId(), Listing.State.FROZEN, props));
			if (frozenListings.size() > 0) {
				result.setFrozenListings(frozenListings);
				allListings.addAll(frozenListings);
			}
			
			props = new ListPropertiesVO();
			props.setMaxResults(4);
			List<ListingTileVO> closedListings = ListingFacade.instance().prepareListingList(
					getDAO().getUserListings(user.toKeyId(), Listing.State.CLOSED, props));
			if (closedListings.size() > 0) {
				result.setClosedListings(closedListings);
				allListings.addAll(closedListings);
			}

			if (user.getEditedListing() != null) {
				Listing editedListing = getDAO().getListing(BaseVO.toKeyId(user.getEditedListing()));
				result.setEditedListing(DtoToVoConverter.convert(editedListing));
				allListings.add(result.getEditedListing());
			}
			applyUserStatistics(loggedInUser, loggedInUser);
		} else {
			user.setEmail("");
			user.setLocation("");
			user.setPhone("");
			user.setEditedListing("");
			user.setEditedStatus("");
		}
		
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			result.setUser(user);
		} else {
			((UserListingsForUsersVO)result).setUserBasic(new UserBasicVO(user));
		}
		return result;
	}
	
	/**
	 * Creates new user based on Google user object.
	 * Should only be used to log in via google account.
	 */
	public UserVO createUser(User loggedInUser) {
		if (loggedInUser == null) {
			return null;
		}
		
		SBUser userDTO = getDAO().createUser(loggedInUser.getEmail(), loggedInUser.getNickname());
		userDTO = updateUserGoogleData(loggedInUser, userDTO);
		
		UserVO user = DtoToVoConverter.convert(userDTO);
		applyUserStatistics(user, user);
		return user;
	}
	
	private SBUser updateUserGoogleData(User googleUser, SBUser user) {
		boolean needsUpdate = false;
		if (StringUtils.isEmpty(user.googleId)) {
			user.googleId = googleUser.getUserId();
			needsUpdate = true;
		}
		if (StringUtils.isEmpty(user.googleEmail)) {
			user.googleEmail = googleUser.getEmail();
			needsUpdate = true;
		}
		if (StringUtils.isEmpty(user.googleName)) {
			user.googleName = googleUser.getNickname();
			needsUpdate = true;
		}
		if (StringUtils.isEmpty(user.avatarUrl) && StringUtils.isNotEmpty(user.googleId)) {
			user.avatarUrl = ImageHelper.getGooglePlusAvatarUrl(user.googleId, user.googleEmail);
			needsUpdate =  true;
		}
		if (needsUpdate) {
			user = getDAO().updateUser(user);
		}
		return user;
	}
	
	/**
	 * Creates new user based on Facebook user object.
	 */
	public UserVO createUser(FacebookUser fbUser) {
		if (fbUser == null) {
			return null;
		}
		
		String fullName = fbUser.getFirstName() + " " + fbUser.getLastName();
		SBUser userDTO = getDAO().createUser(fbUser.getEmail(), fbUser.getFirstName(), fullName.trim());
		
		userDTO = updateUserFacebookData(fbUser, userDTO);
		UserVO user = DtoToVoConverter.convert(userDTO);
		applyUserStatistics(user, user);
		return user;
	}
	
	private SBUser updateUserFacebookData(FacebookUser facebookUser, SBUser user) {
		boolean needsUpdate = false;
		if (StringUtils.isEmpty(user.facebookId)) {
			user.facebookId = facebookUser.getId();
			needsUpdate = true;
		}
		if (StringUtils.isEmpty(user.facebookEmail)) {
			user.facebookEmail = facebookUser.getEmail();
			needsUpdate = true;
		}
		if (StringUtils.isEmpty(user.facebookName)) {
			user.facebookName = facebookUser.getFirstName() + " " + facebookUser.getLastName();
			needsUpdate = true;
		}
		if (StringUtils.isEmpty(user.avatarUrl) && StringUtils.isNotEmpty(user.facebookId)) {
			user.avatarUrl = ImageHelper.getFacebookAvatarUrl(user.facebookId);
			needsUpdate =  true;
		}
		if (needsUpdate) {
			user = getDAO().updateUser(user);
		}
		return user;
	}
	
	/**
	 * Creates new user based on Twitter user object.
	 * Should only be used to log in via twitter account.
	 */
	public UserVO createUser(twitter4j.User twitterUser) {
		if (twitterUser == null) {
			return null;
		}
		
		SBUser userDTO = getDAO().createUser(twitterUser.getId(), twitterUser.getScreenName());
		userDTO = updateUserTwitterData(twitterUser, userDTO);
		UserVO user = DtoToVoConverter.convert(userDTO);
		applyUserStatistics(user, user);
		return user;
	}

	private SBUser updateUserTwitterData(twitter4j.User twitterUser, SBUser user) {
		boolean needsUpdate = false;
		if (StringUtils.isEmpty(user.avatarUrl) && twitterUser.getProfileImageURL() != null) {
			user.avatarUrl = twitterUser.getProfileImageURL().toString();
			needsUpdate =  true;
		}
		if (needsUpdate) {
			user = getDAO().updateUser(user);
		}
		return user;
	}
	
	private boolean validateEmailAddress(String email) {
		if (StringUtils.isEmpty(email)) {
			log.warning("Email is empty");
			return false;
		}
		String expression = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		if (!matcher.matches()) {
			log.warning("Invalid email address: " + email);
			return false;
		}
		return true;
	}
	
	private boolean validatePassword(String email, String password, String name) {
		if (password == null || password.length() < 6) {
			log.warning("Password is too short: " + password.length());
			return false;
		}
		if (name == null || name.contains(password)) {
			log.warning("Password is part of name: " + name);
			return false;
		}
		return true;
	}
	
	private boolean validateName(String name) {
		if (name == null || name.length() < 6) {
			log.warning("Name is too short: " + name);
			return false;
		}
		return true;
	}
	
	/**
	 * Creates new user based on Google user object.
	 * Should only be used to log in via google account.
	 */
	public UserVO createUser(String email, String password, String name, String location, boolean investor) {
		UserVO user = null;
		if (!validateEmailAddress(email) || !validateName(name) || !validatePassword(email, password, name)) {
			return null;
		}
		String encryptedPassword = encryptPassword(password);
		if (encryptedPassword == null) {
			return null;
		}
		String authCookie = encryptPassword(encryptedPassword + new Date().getTime());
		
		user = DtoToVoConverter.convert(getDAO().createUser(email, encryptedPassword, authCookie, name, location, investor));
		applyUserStatistics(user, user);
		return user;
	}

	private String encryptPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return new String(md.digest(password != null ? password.getBytes("UTF-8") : new byte[0])); 
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while encrypting password", e);
			return null;
		}
	}

	/**
	 * Verifies user/password, returns authCookie which should be set in the browser
	 */
	public String checkUserCredentials(String email, String password) {
		SBUser user = getDAO().getUserByEmail(email);
		if(StringUtils.equals(user.password, encryptPassword(password))) {
			return user.authCookie;
		} else {
			return null;
		}
	}

	/**
	 * Returns user for provided authCookie.
	 * Make sure that authCookie expires on the browser.
	 */
	public UserVO checkUserCredentials(String authCookie) {
		SBUser user = getDAO().getUserByAuthCookie(authCookie);
		return DtoToVoConverter.convert(user);
	}
	
	public String changePassword(String email, String oldPassword, String newPassword) {
		SBUser user = getDAO().getUserByEmail(email);
		if(StringUtils.equals(user.password, encryptPassword(oldPassword))) {
			user.password = encryptPassword(newPassword);
			user.authCookie = encryptPassword(user.password + new Date().getTime());
			log.info("User '" + email + "' is going to update password");
			user =  getDAO().updateUser(user);
			return user != null ? user.authCookie : null;
		} else {
			log.warning("User '" + email + "' tried to change password but provided wrong existing password");
			return null;
		}
	}
	
	public UserAndUserVO promoteToDragon(UserVO loggedInUser, String userId) {
		UserAndUserVO result = new UserAndUserVO();
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			log.warning("User not logged in or is not an admin");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage("User not logged in or is not an admin");
			return result;
		}
		SBUser user = getDAO().getUser(userId);
		if (user == null) {
			log.warning("User with id '" + userId + "' not found");
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage("User not found");
			return result;
		}
		user.userClass = "dragon";
		user.dragon = true;
		user = getDAO().updateUser(user);
		log.info("Promoted to Dragon: " + user);
		
		result.setUser(DtoToVoConverter.convert(user));
		return result;
	}
	
	public UserAndUserVO requestDragon(UserVO loggedInUser) {
		UserAndUserVO result = new UserAndUserVO();
		if (loggedInUser == null) {
			log.warning("User not logged in");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage("User not logged in or is not an admin");
			return result;
		}
		SBUser user = getDAO().getUser(loggedInUser.getId());
		if (!StringUtils.contains(user.userClass, "dragon")) {
			user.userClass = "requested_dragon";
			user = getDAO().updateUser(user);
			loggedInUser.setUserClass(user.userClass);
		
			NotificationFacade.instance().scheduleUserDragonRequestNotification(user);
		} else {
			log.warning("User not logged in");
			result.setErrorCode(ErrorCodes.APPLICATION_ERROR);
			result.setErrorMessage("User has already requested dragon badge");
			return result;

		}
		result.setUser(DtoToVoConverter.convert(user));
		return result;
	}
	
	/**
	 * Updates user data. Field validation is done before.
	*/
	public UserVO updateUser(UserVO loggedInUser, String name, String nickname, String location, String phone, Boolean investor, Boolean notifyEnabled) {
		SBUser oldUser = getDAO().getUser(loggedInUser.getId());
		if (oldUser == null) {
			log.warning("User '" + loggedInUser.getEmail() + "' doesn't exist!");
			return null;
		}
		if (oldUser.status != SBUser.Status.ACTIVE) {
			log.warning("User '" + loggedInUser.getEmail() + "' is not active!");
			return null;
		}
		if (StringUtils.isNotEmpty(nickname)) {
            if (nickname.length() < 3) {
                log.warning("New nickname '" + name + "' must be at least 3 characters");
                return null;
            }
            else if (nickname.length() > 30) {
                log.warning("New nickname '" + name + "' must be no more than 30 characters");
                return null;
            }
            else if (!checkUserNameIsValid(loggedInUser, nickname)) {
				log.warning("Nickname '" + nickname + "' for user is not unique!");
				return null;
			}
            else {
				oldUser.nickname = nickname;
			}
        }
		if (StringUtils.isNotEmpty(name)) {
			if (name.length() < 3) {
				log.warning("New user name '" + name + "' must be at least 3 characters");
				return null;
			}
            else if (name.length() > 100) {
				log.warning("New user name '" + name + "' must be no more than 100 characters");
				return null;
			}
            else {
                oldUser.name = name;
			}
		}
		// @FIXME Implement proper location verifier
		if (StringUtils.isNotEmpty(location)) {
			if (location.length() < 10) {
				log.warning("New user location '" + name + "' is too short!");
				return null;
			} else {
				oldUser.location = location;
			}
		}
		// @FIXME User regexp for phone number validation
		if (StringUtils.isNotEmpty(phone)) {
			if (phone.length() < 7) {
				log.warning("New phone '" + phone + "' is too short!");
				return null;
			} else {
				oldUser.phone = phone;
			}
		}
		if (investor != null) {
			oldUser.investor = investor;
		}
		if (notifyEnabled != null) {
			oldUser.notifyEnabled = notifyEnabled;
		}
		UserVO user = DtoToVoConverter.convert(getDAO().updateUser(oldUser));
		if (user != null) {
			loggedInUser.setEmail(user.getEmail());
			loggedInUser.setNickname(user.getNickname());
			loggedInUser.setName(user.getName());
			loggedInUser.setLocation(user.getLocation());
			loggedInUser.setPhone(user.getPhone());
			loggedInUser.setAccreditedInvestor(user.isAccreditedInvestor());
			loggedInUser.setNotifyEnabled(user.isNotifyEnabled());
			applyUserStatistics(loggedInUser, user);
			//ServiceFacade.instance().createNotification(user.getId(), user.getId(), Notification.Type.YOUR_PROFILE_WAS_MODIFIED, "");
		}
		return user;
	}

	/**
	 * Returns list of all registered users
	 * @return List of users
	 */
	public UserListVO getAllUsers(UserVO loggedInUser) {
		UserListVO userList = new UserListVO();
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			log.warning("Only admins can retrieve all users list");
			userList.setUsers(new ArrayList<UserVO>());
			return userList;
		}
		
		List<UserVO> users = DtoToVoConverter.convertUsers(getDAO().getAllUsers());
		int index = 1;
		for (UserVO user : users) {
			applyUserStatistics(loggedInUser, user);
			user.setOrderNumber(index++);
		}

		userList.setUsers(users);
		return userList;
	}

	/**
	 * Returns list of all dragons
	 */
	public UserShortListVO getDragons(UserVO loggedInUser, ListPropertiesVO userProperties) {
		UserShortListVO userList = new UserShortListVO();
		
		List<UserShortVO> users = DtoToVoConverter.convertShortUsers(getDAO().getDragons(userProperties));
		userList.setUsers(users);
		if (loggedInUser != null) {
			userList.setUser(loggedInUser);
		}
		userList.setUsersProperties(userProperties);
		return userList;
	}

	/**
	 * Returns list of all listers
	 */
	public UserShortListVO getListers(UserVO loggedInUser, ListPropertiesVO userProperties) {
		UserShortListVO userList = new UserShortListVO();
		
		List<UserShortVO> users = DtoToVoConverter.convertShortUsers(getDAO().getListers(userProperties));
		userList.setUsers(users);
		userList.setUsersProperties(userProperties);
		if (loggedInUser != null) {
			userList.setUser(loggedInUser);
		}
		return userList;
	}

    public void applyUserStatistics(UserVO user) {
        applyUserStatistics(null, user);
    }

    public void applyUserStatistics(UserVO loggedInUser, UserVO user) {
		if (user != null && user.getId() != null) {
			UserStats userStats = getUserStatistics(user.getId());
			if (userStats != null) {
				//user.setNumberOfBids(userStats.numberOfBids);
				//user.setNumberOfComments(userStats.numberOfComments);
				//user.setNumberOfListings(userStats.numberOfListings);
				//user.setNumberOfVotes(userStats.numberOfVotes);
				//user.setNumberOfAcceptedBids(userStats.numberOfAcceptedBids);
				//user.setNumberOfFundedBids(userStats.numberOfFundedBids);
				user.setNumberOfNotifications(userStats.numberOfNotifications);
			} else {
				log.info("User statistics not available for user '" + user.getEmail() + "'");
			}
		}
	}
	
	public UserVO activateUser(String userId, String activationCode) {
		UserVO user = DtoToVoConverter.convert(getDAO().activateUser(BaseVO.toKeyId(userId), activationCode));
		if (user != null) {
			applyUserStatistics(user);
		}
		return user;
	}

	public UserVO deactivateUser(UserVO loggedInUser, String userId) {
		if (loggedInUser == null || !loggedInUser.getId().equals(userId)) {
			if (UserServiceFactory.getUserService().isUserLoggedIn() && UserServiceFactory.getUserService().isUserAdmin()) {
				log.info("Admin user '" + loggedInUser.getEmail() + "' is deactivating user " + userId);
			} else {
				return null;
			}
		}
		UserVO user = DtoToVoConverter.convert(getDAO().deactivateUser(BaseVO.toKeyId(userId)));
		applyUserStatistics(loggedInUser, user);
		return user;
	}

	public Boolean checkUserNameIsValid(UserVO loggedInUser, String nickName) { // true if nickname is a valid username in use, false otherwise
        if (StringUtils.isEmpty(nickName)) { // empty nickname not allowed
            return false;
        }
        if (!StringUtils.isEmpty(loggedInUser.getNickname()) && loggedInUser.getNickname().equalsIgnoreCase(nickName)) { // keeping my existing name is okay
            return true;
        }
        if (getDAO().checkNickNameInUse(nickName)) { // same as existing nickname not allowed
            return false;
        }
        return true;
	}
	
	public void scheduleUpdateOfUserStatistics(String userId, UpdateReason reason) {
		log.log(Level.INFO, "Scheduling user stats update for '" + userId + "', reason: " + reason);
//		UserStats userStats = (UserStats)cache.get(USER_STATISTICS_KEY + userId);
//		if (userStats != null)
//			switch(reason) {
//			case NEW_BID:
//				userStats.numberOfBids = userStats.numberOfBids + 1;
//				break;
//			case NEW_COMMENT:
//				userStats.numberOfComments = userStats.numberOfComments + 1;
//				break;
//			case NEW_LISTING:
//				userStats.numberOfListings = userStats.numberOfListings + 1;
//				break;
//			case NEW_VOTE:
//				userStats.numberOfVotes = userStats.numberOfVotes + 1;
//				break;
//			default:
//				// reason can be also null
//				break;
//			}
//			cache.put(USER_STATISTICS_KEY + userId, userStats);
//		}
		String taskName = timeStampFormatter.print(new Date().getTime()) + "user_stats_update_" + reason + "_" + userId;
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/calculate-user-stats").param("id", userId)
				.taskName(taskName));
	}
	
	public UserStats calculateUserStatistics(String userId) {
		log.log(Level.INFO, "Calculating user stats for '" + userId + "'");
		UserStats userStats = getDAO().updateUserStatistics(BaseVO.toKeyId(userId));
		return userStats;
	}
	
	private UserStats getUserStatistics(String userId) {
		UserStats userStats = getDAO().getUserStatistics(BaseVO.toKeyId(userId));
		if (userStats == null) {
			// calculating user stats here may be disabled here
			userStats = calculateUserStatistics(userId);
		}
		log.log(Level.INFO, "User stats for '" + userId + "' : " + userStats);
		return userStats;
	}
	
	public List<UserStats> updateAllUserStatistics() {
		List<UserStats> list = new ArrayList<UserStats>();
		for (SBUser user : getDAO().getAllUsers()) {
			list.add(calculateUserStatistics("" + user.id));
		}
		
		return list;
	}

	public Object verifyEmail(UserVO loggedInUser, String email) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object requestEmailAccess(UserVO loggedInUser, String email, String url) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void updateUserData(UserDataUpdatable item) {
		if (item == null) {
			return;
		}
		SBUser user = getDAO().getUser(item.getUser());
		item.setAvatar(user.avatarUrl);
		item.setUserClass(user.userClass);
		item.setUserNickname(user.nickname);
	}
	
	public void updateUserData(List<? extends UserDataUpdatable> items) {
		if (items == null || items.size() == 0) {
			return;
		}
		Set<Key<Object>> userKeys = new HashSet<Key<Object>>();
		for (UserDataUpdatable item : items) {
			if (item.getUser() != null) {
				userKeys.add(Key.create(item.getUser()));
			}
		}
		if (userKeys.size() > 0) {
			log.info("Getting user's data for " + userKeys.size() + " users");
			Map<String, SBUser> users = getDAO().getUsers(userKeys);
			for (UserDataUpdatable item : items) {
				SBUser user = users.get(item.getUser());
				if (user != null) {
					item.setAvatar(user.avatarUrl);
					item.setUserClass(user.userClass);
					item.setUserNickname(user.nickname);
				}
			}
		}
	}
}
