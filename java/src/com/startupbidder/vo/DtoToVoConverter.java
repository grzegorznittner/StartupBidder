package com.startupbidder.vo;

import java.util.ArrayList;
import java.util.List;

import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.ListingDocumentDTO;
import com.startupbidder.dto.MonitorDTO;
import com.startupbidder.dto.NotificationDTO;
import com.startupbidder.dto.SystemPropertyDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.VoteDTO;

/**
 * Helper class which converts DTO objects to VO objects.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class DtoToVoConverter {
	public static BidVO convert(BidDTO bidDTO) {
		if (bidDTO == null) {
			return null;
		}
		BidVO bid = new BidVO();
		bid.setId(bidDTO.getIdAsString());
		bid.setMockData(bidDTO.isMockData());
		bid.setListing(bidDTO.getListing());
		bid.setFundType(bidDTO.getFundType().toString());
		bid.setPercentOfCompany(bidDTO.getPercentOfCompany());
		bid.setInterestRate(bidDTO.getInterestRate());
		bid.setPlaced(bidDTO.getPlaced());
		bid.setUser(bidDTO.getUser());
		bid.setListingOwner(bidDTO.getListingOwner());
		bid.setValue(bidDTO.getValue());
		bid.setValuation(bidDTO.getValuation());
		bid.setStatus(bidDTO.getStatus().toString());
		bid.setComment(bidDTO.getComment());
		return bid;
	}
	
	public static ListingVO convert(ListingDTO listingDTO) {
		if (listingDTO == null) {
			return null;
		}
		ListingVO listing = new ListingVO();
		listing.setId(listingDTO.getIdAsString());
		listing.setMockData(listingDTO.isMockData());
		listing.setClosingOn(listingDTO.getClosingOn());
		listing.setListedOn(listingDTO.getListedOn());
		listing.setName(listingDTO.getName());
		listing.setOwner(listingDTO.getOwner());
		listing.setSuggestedValuation(listingDTO.getSuggestedValuation());
		listing.setSuggestedAmount(listingDTO.getSuggestedAmount());
		listing.setSuggestedPercentage(listingDTO.getSuggestedPercentage());
		listing.setState(listingDTO.getState().toString());
		listing.setPresentationId(listingDTO.getPresentationId());
		listing.setBuinessPlanId(listingDTO.getBusinessPlanId());
		listing.setFinancialsId(listingDTO.getFinancialsId());
		listing.setSummary(listingDTO.getSummary());
		return listing;
	}
	
	public static CommentVO convert(CommentDTO commentDTO) {
		if (commentDTO == null) {
			return null;
		}
		CommentVO comment = new CommentVO();
		comment.setId(commentDTO.getIdAsString());
		comment.setMockData(commentDTO.isMockData());
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
		rating.setMockData(ratingDTO.isMockData());
		rating.setId(ratingDTO.getIdAsString());
		rating.setListing(ratingDTO.getListing());
		rating.setUser(ratingDTO.getUser());
		rating.setValue(ratingDTO.getValue());
		return rating;
	}
	
	public static ListingDocumentVO convert(ListingDocumentDTO docDTO) {
		if (docDTO == null) {
			return null;
		}
		ListingDocumentVO doc = new ListingDocumentVO();
		doc.setId(docDTO.getIdAsString());
		doc.setMockData(docDTO.isMockData());
		doc.setBlob(docDTO.getBlob());
		doc.setCreated(docDTO.getCreated());
		doc.setType(docDTO.getType().toString());
		return doc;
	}
	
	public static UserVO convert(UserDTO userDTO) {
		if (userDTO == null) {
			return null;
		}
		UserVO user = new UserVO();
		user.setId(userDTO.getIdAsString());
		user.setMockData(userDTO.isMockData());
		user.setAdmin(userDTO.isAdmin());
		user.setAccreditedInvestor(userDTO.isInvestor());
		user.setEmail(userDTO.getEmail());
		user.setFacebook(userDTO.getFacebook());
		user.setName(userDTO.getName());
		user.setJoined(userDTO.getJoined());
		user.setLastLoggedIn(userDTO.getLastLoggedIn());
		user.setLinkedin(userDTO.getLinkedin());
		user.setModified(userDTO.getModified());
		user.setNickname(userDTO.getNickname());
		user.setOrganization(userDTO.getOrganization());
		user.setTitle(userDTO.getTitle());
		user.setTwitter(userDTO.getTwitter());
		user.setStatus(userDTO.getStatus().toString());
		user.setNotifications(userDTO.getNotifications());
		return user;
	}
	
	public static SystemPropertyVO convert(SystemPropertyDTO propertyDTO) {
		if (propertyDTO == null) {
			return null;
		}
		return new SystemPropertyVO(propertyDTO);
	}
	
	public static NotificationVO convert(NotificationDTO notifDTO) {
		if (notifDTO == null) {
			return null;
		}
		NotificationVO notif = new NotificationVO();
		notif.setId(notifDTO.getIdAsString());
		notif.setMockData(notifDTO.isMockData());
		notif.setCreated(notifDTO.getCreated());
		notif.setEmailDate(notifDTO.getEmailDate());
		notif.setUser(notifDTO.getUser());
		notif.setMessage(notifDTO.getMessage());
		notif.setObject(notifDTO.getObject());
		notif.setType(notifDTO.getType().toString());
		notif.setAcknowledged(notifDTO.isAcknowledged());
		return notif;
	}
	
	public static MonitorVO convert(MonitorDTO monitorDTO) {
		if (monitorDTO == null) {
			return null;
		}
		MonitorVO monitor = new MonitorVO();
		monitor.setId(monitorDTO.getIdAsString());
		monitor.setMockData(monitorDTO.isMockData());
		monitor.setCreated(monitorDTO.getCreated());
		monitor.setDeactivated(monitorDTO.getDeactivated());
		monitor.setObjectId(monitorDTO.getObject());
		monitor.setUser(monitorDTO.getUser());
		monitor.setType(monitorDTO.getType().toString());
		monitor.setActive(monitorDTO.isActive());
		return monitor;
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

	public static List<UserVO> convertUsers(List<UserDTO> userDtoList) {
		if (userDtoList == null) {
			return null;
		}
		List<UserVO> userVoList = new ArrayList<UserVO>();
		for (UserDTO userDTO : userDtoList) {
			UserVO bidVO = convert(userDTO);
			userVoList.add(bidVO);
		}
		return userVoList;
	}
	
	public static List<VoteVO> convertVotes(List<VoteDTO> votesDtoList) {
		if (votesDtoList == null) {
			return null;
		}
		List<VoteVO> votesVoList = new ArrayList<VoteVO>();
		for (VoteDTO voteDTO : votesDtoList) {
			VoteVO voteVO = convert(voteDTO);
			votesVoList.add(voteVO);
		}
		return votesVoList;
	}

	public static List<SystemPropertyVO> convertSystemProperties(List<SystemPropertyDTO> propertiesDtoList) {
		if (propertiesDtoList == null) {
			return null;
		}
		List<SystemPropertyVO> propertyVoList = new ArrayList<SystemPropertyVO>();
		for (SystemPropertyDTO propertyDTO : propertiesDtoList) {
			SystemPropertyVO propertyVO = convert(propertyDTO);
			propertyVoList.add(propertyVO);
		}
		return propertyVoList;
	}

	public static List<ListingDocumentVO> convertListingDocuments(List<ListingDocumentDTO> docDtoList) {
		if (docDtoList == null) {
			return null;
		}
		List<ListingDocumentVO> docVoList = new ArrayList<ListingDocumentVO>();
		for (ListingDocumentDTO docDTO : docDtoList) {
			ListingDocumentVO docVO = convert(docDTO);
			docVoList.add(docVO);
		}
		return docVoList;
	}

	public static List<NotificationVO> convertNotifications(List<NotificationDTO> notifDtoList) {
		if (notifDtoList == null) {
			return null;
		}
		List<NotificationVO> notifVoList = new ArrayList<NotificationVO>();
		for (NotificationDTO notifDTO : notifDtoList) {
			NotificationVO notifVO = convert(notifDTO);
			notifVoList.add(notifVO);
		}
		return notifVoList;
	}

	public static List<MonitorVO> convertMonitors(List<MonitorDTO> monitorDtoList) {
		if (monitorDtoList == null) {
			return null;
		}
		List<MonitorVO> monitorVoList = new ArrayList<MonitorVO>();
		for (MonitorDTO monitorDTO : monitorDtoList) {
			MonitorVO monitorVO = convert(monitorDTO);
			monitorVoList.add(monitorVO);
		}
		return monitorVoList;
	}
}
