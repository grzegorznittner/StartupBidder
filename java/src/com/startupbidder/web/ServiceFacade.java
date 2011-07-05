package com.startupbidder.web;

import java.util.List;

import com.startupbidder.dao.DatastoreDAO;
import com.startupbidder.dao.MockDatastoreDAO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.UserStatistics;
import com.startupbidder.dto.VoToDtoConverter;
import com.startupbidder.vo.BidVO;
import com.startupbidder.vo.BusinessPlanVO;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.UserVO;

public class ServiceFacade {
	private static ServiceFacade instance;
	
	public static ServiceFacade instance() {
		if (instance == null) {
			instance = new ServiceFacade();
		}
		return instance;
	}
	
	private DatastoreDAO getDAO () {
		return MockDatastoreDAO.getInstance();
	}
	
	/**
	 * Returns user data object by userId
	 * 
	 * @param userId User identifier
	 * @return User data as JsonNode
	 */
	public UserVO getUser(String userId) {
		UserVO user = DtoToVoConverter.convert(getDAO().getUser(userId));
		
		UserStatistics stat = getDAO().getUserStatistics(userId);
		user.setNumberOfListings(stat.getNumberOfListings());
		user.setNumberOfBids(stat.getNumberOfBids());
		user.setNumberOfComments(stat.getNumberOfComments());
		
		return user;
	}
	
	/**
	 * Updates/creates user data
	 * If id is empty or user doesn't exist in the repository it will be created.
	 * 
	 * @param userData User data object
	 */
	public UserVO updateUser(UserVO userData) {
		getDAO().updateUser(VoToDtoConverter.convert(userData));
		return DtoToVoConverter.convert(getDAO().getUser(userData.getId()));
	}
	
	/**
	 * Returns business plans created by specified user
	 * 
	 * @param userId User identifier
	 * @param maxItems Maximum number of items returned in the call
	 * @param cursor Cursor string
	 * @return List of business plans as JsonNode's tree
	 */
	public List<BusinessPlanVO> getUserBusinessPlans(String userId, int maxItems, String cursor) {
		List<BusinessPlanVO> bpList = DtoToVoConverter.convertBusinessPlans(getDAO().getUserBusinessPlans(userId, maxItems));
		for (BusinessPlanVO bp : bpList) {
			bp.setNumberOfComments(getDAO().getActivity(bp.getId()));
			bp.setRating(getDAO().getRating(bp.getId()));
			bp.setNumberOfBids(getDAO().getBids(bp.getId()).size());
		}
		return bpList;
	}
	
	/**
	 * Returns top rated business plans
	 * 
	 * @param maxItems Maximum number of items returned in the call
	 * @param cursor Cursor string
	 * @return List of business plans
	 */
	public List<BusinessPlanVO> getTopBusinessPlans(int maxItems, String cursor) {
		List<BusinessPlanVO> bpList = DtoToVoConverter.convertBusinessPlans(getDAO().getTopBusinessPlans(maxItems));
		for (BusinessPlanVO bp : bpList) {
			bp.setNumberOfComments(getDAO().getActivity(bp.getId()));
			bp.setRating(getDAO().getRating(bp.getId()));
			bp.setNumberOfBids(getDAO().getBids(bp.getId()).size());
		}
		return bpList;
	}
	
	/**
	 * Returns most active business plans
	 * 
	 * @param userId User identifier
	 * @param maxItems Maximum number of items returned in the call
	 * @param cursor Cursor string
	 * @return List of business plans
	 */
	public List<BusinessPlanVO> getActiveBusinessPlans(int maxItems, String cursor) {
		List<BusinessPlanVO> bpList = DtoToVoConverter.convertBusinessPlans(getDAO().getActiveBusinessPlans(maxItems));
		for (BusinessPlanVO bp : bpList) {
			bp.setNumberOfComments(getDAO().getActivity(bp.getId()));
			bp.setRating(getDAO().getRating(bp.getId()));
			bp.setNumberOfBids(getDAO().getBids(bp.getId()).size());
		}
		return bpList;
	}
	
	/**
	 * Value up business plan
	 *
	 * @param businessPlanId Business plan identifier
	 * @param userId User identifier
	 * @return Business plan rating
	 */
	public int valueUpBusinessPlan(String businessPlanId, String userId) {
		return getDAO().valueUpBusinessPlan(businessPlanId, userId);
	}
	
	/**
	 * Value down business plan
	 *
	 * @param businessPlanId Business plan identifier
	 * @param userId User identifier
	 * @return Business plan rating
	 */
	public int valueDownBusinessPlan(String businessPlanId, String userId) {
		return getDAO().valueDownBusinessPlan(businessPlanId, userId);
	}
	
	/**
	 * Returns list of business plan's comments
	 * 
	 * @param businessPlanId Business plan id
	 * @param cursor Cursor string
	 * @return List of comments
	 */
	public List<CommentVO> getComments(String businessPlanId, String cursor) {
		return DtoToVoConverter.convertComments(getDAO().getComments(businessPlanId));
	}
	
	/**
	 * Returns list of business plan's bids
	 * @param businessPlanId Business plan id
	 * @param cursor Cursor string
	 * @return List of bids
	 */
	public List<BidVO> getBids(String businessPlanId, String cursor) {
		return DtoToVoConverter.convertBids(getDAO().getBids(businessPlanId));
	}
	
	/**
	 * Returns business plan's rating
	 * @param businessPlanId Business plan id
	 * @return Current rating
	 */
	public int getRating(String businessPlanId) {
		return getDAO().getRating(businessPlanId);
	}
	
	/**
	 * Returns business plan's activity (number of comments)
	 * @param businessPlanId Business plan id
	 * @return Activity
	 */
	public int getActivity(String businessPlanId) {
		return getDAO().getActivity(businessPlanId);
	}

}
