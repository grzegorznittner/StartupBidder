package com.startupbidder.dao;

import java.util.List;

import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.BusinessPlanDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.UserStatistics;

public interface DatastoreDAO {
	/**
	 * Returns user data object by userId
	 * 
	 * @param userId User identifier
	 * @return User data
	 */
	UserDTO getUser(String userId);

	/**
	 * Return user statistics like number of comments, bids, etc
	 * @param userId User identifier
	 */
	UserStatistics getUserStatistics(String userId);
	
	/**
	 * Updates/creates user data
	 * If id is empty or user doesn't exist in the repository it will be created.
	 * 
	 * @param user User data object
	 */
	void updateUser(UserDTO user);
	
	/**
	 * Returns business plans created by specified user
	 * 
	 * @param userId User identifier
	 * @param maxItems Maximum number of items returned in the call
	 * @return List of business plans
	 */
	List<BusinessPlanDTO> getUserBusinessPlans(String userId, int maxItems);
	
	/**
	 * Returns top rated business plans
	 * 
	 * @param maxItems Maximum number of items returned in the call
	 * @return List of business plans
	 */
	List<BusinessPlanDTO> getTopBusinessPlans(int maxItems);
	
	/**
	 * Returns most active business plans
	 * 
	 * @param userId User identifier
	 * @param maxItems Maximum number of items returned in the call
	 * @return List of business plans
	 */
	List<BusinessPlanDTO> getActiveBusinessPlans(int maxItems);
	
	/**
	 * Value up business plan
	 *
	 * @param businessPlanId Business plan identifier
	 * @param userId User identifier
	 * @return Business plan rating
	 */
	int valueUpBusinessPlan(String businessPlanId, String userId);
	
	/**
	 * Value down business plan
	 *
	 * @param businessPlanId Business plan identifier
	 * @param userId User identifier
	 * @return Business plan rating
	 */
	int valueDownBusinessPlan(String businessPlanId, String userId);
	
	/**
	 * Returns list of business plan's comments
	 * 
	 * @param businessPlanId Business plan id
	 * @return List of comments
	 */
	List<CommentDTO> getComments(String businessPlanId);
	
	/**
	 * Returns list of business plan's bids
	 * @param businessPlanId Business plan id
	 * @return List of bids
	 */
	List<BidDTO> getBids(String businessPlanId);
	
	/**
	 * Returns business plan's rating
	 * @param businessPlanId Business plan id
	 * @return Current rating
	 */
	int getRating(String businessPlanId);
	
	/**
	 * Returns business plan's activity (number of comments)
	 * @param businessPlanId Business plan id
	 * @return Activity
	 */
	int getActivity(String businessPlanId);
}
