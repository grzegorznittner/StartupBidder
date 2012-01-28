package com.startupbidder.vo;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.SystemProperty;
import com.startupbidder.datamodel.Vote;

/**
 * Helper class which converts DTO objects to VO objects.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class DtoToVoConverter {
	public static String keyToString(Key<?> key) {
		if (key != null) {
			return "" + key.getId();
		} else {
			return null;
		}
	}

	public static String keyToString(long key) {
		if (key != 0) {
			return "" + key;
		} else {
			return null;
		}
	}

	public static BidVO convert(Bid bidDTO) {
		if (bidDTO == null) {
			return null;
		}
		BidVO bid = new BidVO();
		bid.setId(keyToString(bidDTO.id));
		bid.setMockData(bidDTO.mockData);
		bid.setListing(keyToString(bidDTO.listing));
		bid.setFundType(bidDTO.fundType.toString());
		bid.setPercentOfCompany(bidDTO.percentOfCompany);
		bid.setInterestRate(bidDTO.interestRate);
		bid.setPlaced(bidDTO.placed);
		bid.setUser(keyToString(bidDTO.bidder));
		bid.setListingOwner(keyToString(bidDTO.listingOwner));
		bid.setValue(bidDTO.value);
		bid.setValuation(bidDTO.valuation);
		bid.setStatus(bidDTO.status.toString());
		bid.setComment(bidDTO.comment);
		return bid;
	}
	
	public static ListingVO convert(Listing listingDTO) {
		if (listingDTO == null) {
			return null;
		}
		ListingVO listing = new ListingVO();
		listing.setId(keyToString(listingDTO.id));
		listing.setMockData(listingDTO.mockData);
		listing.setClosingOn(listingDTO.closingOn);
		listing.setListedOn(listingDTO.listedOn);
		listing.setName(listingDTO.name);
		listing.setOwner(keyToString(listingDTO.owner));
		listing.setSuggestedValuation(listingDTO.suggestedValuation);
		listing.setSuggestedAmount(listingDTO.suggestedAmount);
		listing.setSuggestedPercentage(listingDTO.suggestedPercentage);
		listing.setState(listingDTO.state.toString());
		listing.setPresentationId(keyToString(listingDTO.presentationId));
		listing.setBuinessPlanId(keyToString(listingDTO.businessPlanId));
		listing.setFinancialsId(keyToString(listingDTO.financialsId));
		listing.setSummary(listingDTO.summary);
		return listing;
	}
	
	public static CommentVO convert(Comment commentDTO) {
		if (commentDTO == null) {
			return null;
		}
		CommentVO comment = new CommentVO();
		comment.setId(keyToString(commentDTO.id));
		comment.setMockData(commentDTO.mockData);
		comment.setComment(commentDTO.comment);
		comment.setCommentedOn(commentDTO.commentedOn);
		comment.setListing(keyToString(commentDTO.listing));
		comment.setUser(keyToString(commentDTO.user));
		return comment;
	}
	
	public static VoteVO convert(Vote ratingDTO) {
		if (ratingDTO == null) {
			return null;
		}
		VoteVO rating = new VoteVO();
		rating.setMockData(ratingDTO.mockData);
		rating.setId(keyToString(ratingDTO.id));
		rating.setListing(keyToString(ratingDTO.listing));
		rating.setUser(keyToString(ratingDTO.user));
		rating.setValue(ratingDTO.value);
		return rating;
	}
	
	public static ListingDocumentVO convert(ListingDoc docDTO) {
		if (docDTO == null) {
			return null;
		}
		ListingDocumentVO doc = new ListingDocumentVO();
		doc.setId(keyToString(docDTO.id));
		doc.setMockData(docDTO.mockData);
		doc.setBlob(docDTO.blob);
		doc.setCreated(docDTO.created);
		doc.setType(docDTO.type.toString());
		return doc;
	}
	
	public static UserVO convert(SBUser userDTO) {
		if (userDTO == null) {
			return null;
		}
		UserVO user = new UserVO();
		user.setId(keyToString(userDTO.id));
		user.setMockData(userDTO.mockData);
		user.setAdmin(userDTO.admin);
		user.setAccreditedInvestor(userDTO.investor);
		user.setEmail(userDTO.email);
		user.setName(userDTO.name);
		user.setJoined(userDTO.joined);
		user.setLastLoggedIn(userDTO.lastLoggedIn);
		user.setModified(userDTO.modified);
		user.setNickname(userDTO.nickname);
		user.setStatus(userDTO.status.toString());
		//user.set(userDTO.notifyEnabled);
		return user;
	}
	
	public static SystemPropertyVO convert(SystemProperty propertyDTO) {
		if (propertyDTO == null) {
			return null;
		}
		SystemPropertyVO prop = new SystemPropertyVO();
		prop.setName(propertyDTO.name);
		prop.setValue(propertyDTO.value);
		prop.setAuthor(propertyDTO.author);
		prop.setCreated(propertyDTO.created);
		prop.setModified(propertyDTO.modified);
		return prop;
	}
	
	public static NotificationVO convert(Notification notifDTO) {
		if (notifDTO == null) {
			return null;
		}
		NotificationVO notif = new NotificationVO();
		notif.setId(keyToString(notifDTO.id));
		notif.setMockData(notifDTO.mockData);
		notif.setCreated(notifDTO.created);
		notif.setEmailDate(notifDTO.emailDate);
		notif.setUser(keyToString(notifDTO.user));
		notif.setMessage(notifDTO.message);
		notif.setObject(keyToString(notifDTO.object));
		notif.setType(notifDTO.type.toString());
		notif.setAcknowledged(notifDTO.acknowledged);
		return notif;
	}
	
	public static MonitorVO convert(Monitor monitorDTO) {
		if (monitorDTO == null) {
			return null;
		}
		MonitorVO monitor = new MonitorVO();
		monitor.setId(keyToString(monitorDTO.id));
		monitor.setMockData(monitorDTO.mockData);
		monitor.setCreated(monitorDTO.created);
		monitor.setDeactivated(monitorDTO.deactivated);
		monitor.setObjectId(keyToString(monitorDTO.object));
		monitor.setUser(keyToString(monitorDTO.user));
		monitor.setType(monitorDTO.type.toString());
		monitor.setActive(monitorDTO.active);
		return monitor;
	}
	
	public static List<ListingVO> convertListings(List<Listing> bpDtoList) {
		if (bpDtoList == null) {
			return null;
		}
		List<ListingVO> bpVoList = new ArrayList<ListingVO>();
		for (Listing bpDTO : bpDtoList) {
			ListingVO bpVO = convert(bpDTO);
			bpVoList.add(bpVO);
		}
		return bpVoList;
	}

	public static List<CommentVO> convertComments(List<Comment> commentDtoList) {
		if (commentDtoList == null) {
			return null;
		}
		List<CommentVO> commentVoList = new ArrayList<CommentVO>();
		for (Comment commentDTO : commentDtoList) {
			CommentVO commentVO = convert(commentDTO);
			commentVoList.add(commentVO);
		}
		return commentVoList;
	}

	public static List<BidVO> convertBids(List<Bid> bidDtoList) {
		if (bidDtoList == null) {
			return null;
		}
		List<BidVO> bidVoList = new ArrayList<BidVO>();
		for (Bid bidDTO : bidDtoList) {
			BidVO bidVO = convert(bidDTO);
			bidVoList.add(bidVO);
		}
		return bidVoList;
	}

	public static List<UserVO> convertUsers(List<SBUser> userDtoList) {
		if (userDtoList == null) {
			return null;
		}
		List<UserVO> userVoList = new ArrayList<UserVO>();
		for (SBUser userDTO : userDtoList) {
			UserVO bidVO = convert(userDTO);
			userVoList.add(bidVO);
		}
		return userVoList;
	}
	
	public static List<VoteVO> convertVotes(List<Vote> votesDtoList) {
		if (votesDtoList == null) {
			return null;
		}
		List<VoteVO> votesVoList = new ArrayList<VoteVO>();
		for (Vote voteDTO : votesDtoList) {
			VoteVO voteVO = convert(voteDTO);
			votesVoList.add(voteVO);
		}
		return votesVoList;
	}

	public static List<SystemPropertyVO> convertSystemProperties(List<SystemProperty> propertiesDtoList) {
		if (propertiesDtoList == null) {
			return null;
		}
		List<SystemPropertyVO> propertyVoList = new ArrayList<SystemPropertyVO>();
		for (SystemProperty propertyDTO : propertiesDtoList) {
			SystemPropertyVO propertyVO = convert(propertyDTO);
			propertyVoList.add(propertyVO);
		}
		return propertyVoList;
	}

	public static List<ListingDocumentVO> convertListingDocuments(List<ListingDoc> docDtoList) {
		if (docDtoList == null) {
			return null;
		}
		List<ListingDocumentVO> docVoList = new ArrayList<ListingDocumentVO>();
		for (ListingDoc docDTO : docDtoList) {
			ListingDocumentVO docVO = convert(docDTO);
			docVoList.add(docVO);
		}
		return docVoList;
	}

	public static List<NotificationVO> convertNotifications(List<Notification> notifDtoList) {
		if (notifDtoList == null) {
			return null;
		}
		List<NotificationVO> notifVoList = new ArrayList<NotificationVO>();
		for (Notification notifDTO : notifDtoList) {
			NotificationVO notifVO = convert(notifDTO);
			notifVoList.add(notifVO);
		}
		return notifVoList;
	}

	public static List<MonitorVO> convertMonitors(List<Monitor> monitorDtoList) {
		if (monitorDtoList == null) {
			return null;
		}
		List<MonitorVO> monitorVoList = new ArrayList<MonitorVO>();
		for (Monitor monitorDTO : monitorDtoList) {
			MonitorVO monitorVO = convert(monitorDTO);
			monitorVoList.add(monitorVO);
		}
		return monitorVoList;
	}
}
