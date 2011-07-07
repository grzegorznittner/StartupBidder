package com.startupbidder.dto;

import java.util.ArrayList;
import java.util.List;

import com.startupbidder.vo.BidVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.RatingVO;
import com.startupbidder.vo.UserVO;

public class VoToDtoConverter {
	public static BidDTO convert(BidVO bidVO) {
		BidDTO bid = new BidDTO();
		bid.setIdFromString(bidVO.getId());
		bid.setListing(bidVO.getListing());
		bid.setFundType(BidDTO.FundType.valueOf(bidVO.getFundType()));
		bid.setPercentOfCompany(bidVO.getPercentOfCompany());
		bid.setPlaced(bidVO.getPlaced());
		bid.setUser(bidVO.getUser());
		bid.setValue(bidVO.getValue());
		return bid;
	}
	
	public static ListingDTO convert(ListingVO bpVO) {
		ListingDTO bp = new ListingDTO();
		bp.setIdFromString(bpVO.getId());
		bp.setAverageValuation(bpVO.getAverageValuation());
		bp.setClosingOn(bpVO.getClosingOn());
		bp.setListedOn(bpVO.getListedOn());
		bp.setMedianValuation(bpVO.getMedianValuation());
		bp.setName(bpVO.getName());
		bp.setOwner(bpVO.getOwner());
		bp.setStartingValuation(bpVO.getStartingValuation());
		bp.setStartingValuationDate(bpVO.getStartingValuationDate());
		bp.setState(ListingDTO.State.valueOf(bpVO.getState()));
		bp.setSummary(bpVO.getSummary());
		return bp;
	}

	public static CommentDTO convert(CommentVO commentVO) {
		CommentDTO comment = new CommentDTO();
		comment.setIdFromString(commentVO.getId());
		comment.setComment(commentVO.getComment());
		comment.setCommentedOn(commentVO.getCommentedOn());
		comment.setUser(commentVO.getUser());
		return comment;
	}
	
	public static RatingDTO convert(RatingVO ratingVO) {
		RatingDTO rating = new RatingDTO();
		rating.setIdFromString(ratingVO.getId());
		rating.setListing(ratingVO.getListing());
		rating.setUser(ratingVO.getUser());
		rating.setValue(ratingVO.getValue());
		return rating;
	}
	
	public static UserDTO convert(UserVO userVO) {
		UserDTO user = new UserDTO();
		user.setIdFromString(userVO.getId());
		user.setAccreditedInvestor(userVO.isAccreditedInvestor());
		user.setEmail(userVO.getEmail());
		user.setFacebook(userVO.getFacebook());
		user.setFirstName(userVO.getFirstName());
		user.setJoined(userVO.getJoined());
		user.setLastLoggedIn(userVO.getLastLoggedIn());
		user.setLastName(userVO.getLastName());
		user.setLinkedin(userVO.getLinkedin());
		user.setModified(userVO.getModified());
		user.setNickname(userVO.getNickname());
		user.setOrganization(userVO.getOrganization());
		user.setTitle(userVO.getTitle());
		user.setTwitter(userVO.getTwitter());
		return user;
	}

	public static List<ListingDTO> convertListings(List<ListingVO> bpVOList) {
		List<ListingDTO> bpDtoList = new ArrayList<ListingDTO>();
		for (ListingVO bpVO : bpVOList) {
			ListingDTO bpDTO = convert(bpVO);
			bpDtoList.add(bpDTO);
		}
		return bpDtoList;
	}

	public static List<CommentDTO> convertComments(List<CommentVO> commentVoList) {
		List<CommentDTO> commentDTOList = new ArrayList<CommentDTO>();
		for (CommentVO commentVO : commentVoList) {
			CommentDTO commentDTO = convert(commentVO);
			commentDTOList.add(commentDTO);
		}
		return commentDTOList;
	}

	public static List<BidDTO> convertBids(List<BidVO> bidVoList) {
		List<BidDTO> bidDtoList = new ArrayList<BidDTO>();
		for (BidVO bidVO : bidVoList) {
			BidDTO bidDTO = convert(bidVO);
			bidDtoList.add(bidDTO);
		}
		return bidDtoList;
	}

}
