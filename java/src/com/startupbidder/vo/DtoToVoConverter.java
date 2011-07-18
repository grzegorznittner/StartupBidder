package com.startupbidder.vo;

import java.util.ArrayList;
import java.util.List;

import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.VoteDTO;
import com.startupbidder.dto.UserDTO;

public class DtoToVoConverter {
	public static BidVO convert(BidDTO bidDTO) {
		if (bidDTO == null) {
			return null;
		}
		BidVO bid = new BidVO();
		bid.setId(bidDTO.getIdAsString());
		bid.setListing(bidDTO.getListing());
		bid.setFundType(bidDTO.getFundType().toString());
		bid.setPercentOfCompany(bidDTO.getPercentOfCompany());
		bid.setPlaced(bidDTO.getPlaced());
		bid.setUser(bidDTO.getUser());
		bid.setValue(bidDTO.getValue());
		bid.setValuation(bidDTO.getValuation());
		return bid;
	}
	
	public static ListingVO convert(ListingDTO bpDTO) {
		if (bpDTO == null) {
			return null;
		}
		ListingVO bp = new ListingVO();
		bp.setId(bpDTO.getIdAsString());
		bp.setClosingOn(bpDTO.getClosingOn());
		bp.setListedOn(bpDTO.getListedOn());
		bp.setMedianValuation(bpDTO.getMedianValuation());
		bp.setName(bpDTO.getName());
		bp.setOwner(bpDTO.getOwner());
		bp.setSuggestedValuation(bpDTO.getSuggestedValuation());
		bp.setSuggestedAmount(bpDTO.getSuggestedAmount());
		bp.setSuggestedPercentage(bpDTO.getSuggestedPercentage());
		bp.setState(bpDTO.getState().toString());
		bp.setSummary(bpDTO.getSummary());
		return bp;
	}
	
	public static CommentVO convert(CommentDTO commentDTO) {
		if (commentDTO == null) {
			return null;
		}
		CommentVO comment = new CommentVO();
		comment.setId(commentDTO.getIdAsString());
		comment.setComment(commentDTO.getComment());
		comment.setCommentedOn(commentDTO.getCommentedOn());
		comment.setListing(commentDTO.getListing());
		comment.setUser(commentDTO.getUser());
		return comment;
	}
	
	public static VoteVO convert(VoteDTO ratingDTO) {
		if (ratingDTO == null) {
			return null;
		}
		VoteVO rating = new VoteVO();
		rating.setId(ratingDTO.getIdAsString());
		rating.setListing(ratingDTO.getListing());
		rating.setUser(ratingDTO.getUser());
		rating.setValue(ratingDTO.getValue());
		return rating;
	}
	
	public static UserVO convert(UserDTO userDTO) {
		if (userDTO == null) {
			return null;
		}
		UserVO user = new UserVO();
		user.setId(userDTO.getIdAsString());
		user.setAccreditedInvestor(userDTO.isInvestor());
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
	
	public static List<ListingVO> convertListings(List<ListingDTO> bpDtoList) {
		if (bpDtoList == null) {
			return null;
		}
		List<ListingVO> bpVoList = new ArrayList<ListingVO>();
		for (ListingDTO bpDTO : bpDtoList) {
			ListingVO bpVO = convert(bpDTO);
			bpVoList.add(bpVO);
		}
		return bpVoList;
	}

	public static List<CommentVO> convertComments(List<CommentDTO> commentDtoList) {
		if (commentDtoList == null) {
			return null;
		}
		List<CommentVO> commentVoList = new ArrayList<CommentVO>();
		for (CommentDTO commentDTO : commentDtoList) {
			CommentVO commentVO = convert(commentDTO);
			commentVoList.add(commentVO);
		}
		return commentVoList;
	}

	public static List<BidVO> convertBids(List<BidDTO> bidDtoList) {
		if (bidDtoList == null) {
			return null;
		}
		List<BidVO> bidVoList = new ArrayList<BidVO>();
		for (BidDTO bidDTO : bidDtoList) {
			BidVO bidVO = convert(bidDTO);
			bidVoList.add(bidVO);
		}
		return bidVoList;
	}
}
