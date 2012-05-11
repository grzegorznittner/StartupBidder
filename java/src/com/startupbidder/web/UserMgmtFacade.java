package com.startupbidder.web;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.UserStats;
import com.startupbidder.vo.BaseVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.UserAndUserVO;
import com.startupbidder.vo.UserListVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.vo.UserVotesVO;
import com.startupbidder.vo.VoteVO;

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

	public UserVO getLoggedInUserData(User loggedInUser) {
		if (loggedInUser == null) {
			return null;
		}
		String email = loggedInUser.getEmail();
		UserVO user = null;
		if (StringUtils.isNotEmpty(email)) {
			user = DtoToVoConverter.convert(getDAO().getUserByEmail(email));
		} else {
			user = DtoToVoConverter.convert(getDAO().getUserByEmail(loggedInUser.getUserId()));
		}
		if (user == null) {
			return null;
		}
		applyUserStatistics(user, user);
		return user;
	}

	/**
	 * Returns user data object by userId
	 * 
	 * @param userId User identifier
	 * @return User data as JsonNode
	 */
	public UserAndUserVO getUser(UserVO loggedInUser, String userId) {
		UserVO user = DtoToVoConverter.convert(getDAO().getUser(userId));
		if (user == null) {
			return null;
		}
		applyUserStatistics(loggedInUser, user);

		UserAndUserVO userAndUser = new UserAndUserVO();
		userAndUser.setUser(user);
		return userAndUser;
	}
	
	/**
	 * Creates new user based on Google user object.
	 * Should only be used to log in via google account.
	 */
	public UserVO createUser(User loggedInUser) {
		if (loggedInUser == null) {
			return null;
		}
		
		UserVO user = DtoToVoConverter.convert(getDAO().createUser(loggedInUser.getEmail(), loggedInUser.getNickname()));
		applyUserStatistics(user, user);
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
                log.warning("New nickname '" + name + "' must be no more than 15 characters");
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

    public void applyUserStatistics(UserVO user) {
        applyUserStatistics(null, user);
    }

    public void applyUserStatistics(UserVO loggedInUser, UserVO user) {
		if (user != null && user.getId() != null) {
            /*
			if (loggedInUser != null) {
				user.setVotable(getDAO().userCanVoteForUser(loggedInUser.toKeyId(), user.toKeyId()));
			} else {
				user.setVotable(false);
			}
			*/
            
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
	
    /*
	public UserVO getTopInvestor(UserVO loggedInUser) {
		UserVO user = DtoToVoConverter.convert(getDAO().getTopInvestor());
		applyUserStatistics(loggedInUser, user);
		return user;
	}
	*/

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

    /*
	public UserVotesVO userVotes(UserVO loggedInUser, String userId) {
		UserVotesVO userVotes = new UserVotesVO();
		UserVO user = DtoToVoConverter.convert(getDAO().getUser(userId));
		//applyUserStatistics(loggedInUser, user);
		
		List<VoteVO> votes = DtoToVoConverter.convertVotes(getDAO().getUserVotes(BaseVO.toKeyId(userId)));
		for (VoteVO vote : votes) {
			vote.setUserName(user.getName());
			Listing listing = getDAO().getListing(BaseVO.toKeyId(vote.getListing()));
			vote.setListingName(listing.name);
		}
		userVotes.setVotes(votes);
		
		return userVotes;
	}
    */

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

	/**
	 * Value up user
	 *
	public UserVO valueUpUser(UserVO voter, String userId) {
		if (voter == null) {
			return null;
		}
		UserVO user =  DtoToVoConverter.convert(getDAO().valueUpUser(
				BaseVO.toKeyId(userId), BaseVO.toKeyId(voter.getId())));
		if (user != null) {
			//UserMgmtFacade.instance().scheduleUpdateOfUserStatistics(userId, UserMgmtFacade.UpdateReason.NEW_VOTE);
			//ServiceFacade.instance().createNotification(user.getId(), user.getId(), Notification.Type.NEW_VOTE_FOR_YOU, "");
			//UserMgmtFacade.instance().applyUserStatistics(voter, user);
		}
		return user;
	}
    */
}
