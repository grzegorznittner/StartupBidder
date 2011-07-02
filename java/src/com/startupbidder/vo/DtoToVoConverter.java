package com.startupbidder.vo;

import java.util.ArrayList;
import java.util.List;

import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.BusinessPlanDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.RatingDTO;
import com.startupbidder.dto.UserDTO;

public class DtoToVoConverter {
	public static BidVO convert(BidDTO bidDTO) {
		BidVO bid = new BidVO();
		bid.setId(bidDTO.getIdAsString());
		bid.setBusinessPlan(bidDTO.getBusinessPlan());
		bid.setFundType(bidDTO.getFundType().toString());
		bid.setPercentOfCompany(bidDTO.getPercentOfCompany());
		bid.setPlaced(bidDTO.getPlaced());
		bid.setUser(bidDTO.getUser());
		bid.setValue(bidDTO.getValue());
		return bid;
	}
	
	public static BusinessPlanVO convert(BusinessPlanDTO bpDTO) {
		BusinessPlanVO bp = new BusinessPlanVO();
		bp.setId(bpDTO.getIdAsString());
		bp.setAverageValuation(bpDTO.getAverageValuation());
		bp.setClosingOn(bpDTO.getClosingOn());
		bp.setListedOn(bpDTO.getListedOn());
		bp.setMedianValuation(bpDTO.getMedianValuation());
		bp.setName(bpDTO.getName());
		bp.setOwner(bpDTO.getOwner());
		bp.setStartingValuation(bpDTO.getStartingValuation());
		bp.setStartingValuationDate(bpDTO.getStartingValuationDate());
		bp.setState(bpDTO.getState().toString());
		bp.setSummary(bpDTO.getSummary());
		return bp;
	}
	
	public static CommentVO convert(CommentDTO commentDTO) {
		CommentVO comment = new CommentVO();
		comment.setId(commentDTO.getIdAsString());
		comment.setComment(commentDTO.getComment());
		comment.setCommentedOn(commentDTO.getCommentedOn());
		comment.setUser(commentDTO.getUser());
		return comment;
	}
	
	public static RatingVO convert(RatingDTO ratingDTO) {
		RatingVO rating = new RatingVO();
		rating.setId(ratingDTO.getIdAsString());
		rating.setBusinessPlan(ratingDTO.getBusinessPlan());
		rating.setUser(ratingDTO.getUser());
		rating.setValue(ratingDTO.getValue());
		return rating;
	}
	
	public static UserVO convert(UserDTO userDTO) {
		UserVO user = new UserVO();
		user.setId(userDTO.getIdAsString());
		user.setAccreditedInvestor(userDTO.isAccreditedInvestor());
		user.setEmail(userDTO.getEmail());
		user.setFacebook(userDTO.getFacebook());
		user.setFirstName(userDTO.getFirstName());
		user.setJoined(userDTO.getJoined());
		user.setLastLoggedIn(userDTO.getLastLoggedIn());
		user.setLastName(userDTO.getLastName());
		user.setLinkedin(userDTO.getLinkedin());
		user.setModified(userDTO.getModified());
		user.setNickname(userDTO.getNickname());
		user.setOrganization(userDTO.getOrganization());
		user.setTitle(userDTO.getTitle());
		user.setTwitter(userDTO.getTwitter());
		return user;
	}
	
	public static List<BusinessPlanVO> convertBusinessPlans(List<BusinessPlanDTO> bpDtoList) {
		List<BusinessPlanVO> bpVoList = new ArrayList<BusinessPlanVO>();
		for (BusinessPlanDTO bpDTO : bpDtoList) {
			BusinessPlanVO bpVO = convert(bpDTO);
			bpVoList.add(bpVO);
		}
		return bpVoList;
	}

	public static List<CommentVO> convertComments(List<CommentDTO> commentDtoList) {
		List<CommentVO> commentVoList = new ArrayList<CommentVO>();
		for (CommentDTO commentDTO : commentDtoList) {
			CommentVO commentVO = convert(commentDTO);
			commentVoList.add(commentVO);
		}
		return commentVoList;
	}

	public static List<BidVO> convertBids(List<BidDTO> bidDtoList) {
		List<BidVO> bidVoList = new ArrayList<BidVO>();
		for (BidDTO bidDTO : bidDtoList) {
			BidVO bidVO = convert(bidDTO);
			bidVoList.add(bidVO);
		}
		return bidVoList;
	}
}
