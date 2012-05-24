package com.startupbidder.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.googlecode.objectify.Key;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.PrivateMessage;
import com.startupbidder.datamodel.PrivateMessageUser;
import com.startupbidder.datamodel.QuestionAnswer;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.SystemProperty;
import com.startupbidder.datamodel.Vote;

/**
 * Helper class which converts DTO objects to VO objects.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class DtoToVoConverter {
	private static String keyToString(Key<?> key) {
		if (key != null) {
			return key.getString();
		} else {
			return null;
		}
	}
	
	public static PrivateMessageVO convert(PrivateMessage msgDTO) {
		if (msgDTO == null) {
			return null;
		}
		PrivateMessageVO qa = new PrivateMessageVO();
		qa.setText(msgDTO.text);
		qa.setCreated(msgDTO.created);
		qa.setDirection(msgDTO.direction == PrivateMessage.Direction.A_TO_B ? "sent" : "received");
		return qa;
	}
	
	public static PrivateMessageUserVO convert(PrivateMessageUser msgDTO) {
		if (msgDTO == null) {
			return null;
		}
		PrivateMessageUserVO qa = new PrivateMessageUserVO();
		qa.setText(msgDTO.text);
		qa.setLastDate(msgDTO.created);
		qa.setDirection(msgDTO.direction == PrivateMessage.Direction.A_TO_B ? "sent" : "received");
		qa.setUser(msgDTO.direction == PrivateMessage.Direction.A_TO_B ? msgDTO.userA.getString() : msgDTO.userB.getString());
		qa.setUserNickname(msgDTO.direction == PrivateMessage.Direction.A_TO_B ? msgDTO.userANickname: msgDTO.userBNickname);
		return qa;
	}
	
	public static QuestionAnswerVO convert(QuestionAnswer qaDTO) {
		if (qaDTO == null) {
			return null;
		}
		QuestionAnswerVO qa = new QuestionAnswerVO();
		qa.setId(qaDTO.id != null ? new Key<QuestionAnswer>(QuestionAnswer.class, qaDTO.id).getString() : null);
		qa.setAnswer(qaDTO.answer);
		qa.setAnswerDate(qaDTO.answerDate);
		qa.setCreated(qaDTO.created);
		qa.setListing(keyToString(qaDTO.listing));
		qa.setPublished(qaDTO.published);
		qa.setQuestion(qaDTO.question);
		qa.setUser(keyToString(qaDTO.user));
		qa.setUserNickname(qaDTO.userNickname);
		return qa;
	}

	public static BidVO convert(Bid bidDTO) {
		if (bidDTO == null) {
			return null;
		}
		BidVO bid = new BidVO();
		bid.setId(bidDTO.id != null ? new Key<Bid>(Bid.class, bidDTO.id).getString() : null);
		bid.setMockData(bidDTO.mockData);
		bid.setListing(keyToString(bidDTO.listing));
		bid.setListingName(bidDTO.listingName);
		bid.setFundType(bidDTO.fundType.toString());
		bid.setPercentOfCompany(bidDTO.percentOfCompany);
		bid.setInterestRate(bidDTO.interestRate);
		bid.setPlaced(bidDTO.placed);
		bid.setUser(keyToString(bidDTO.bidder));
		bid.setUserName(bidDTO.bidderName);
		bid.setListingOwner(keyToString(bidDTO.listingOwner));
		bid.setValue(bidDTO.value);
		bid.setValuation(bidDTO.valuation);
		bid.setAction(bidDTO.action.toString());
		bid.setActor(bidDTO.actor.toString());
		bid.setExpires(bidDTO.expires);
		bid.setComment(bidDTO.comment);
		return bid;
	}
	
	public static ListingVO convert(Listing listingDTO) {
		if (listingDTO == null) {
			return null;
		}
		ListingVO listing = new ListingVO();
		listing.setId(new Key<Listing>(Listing.class, listingDTO.id).getString());
		listing.setMockData(listingDTO.mockData);
		listing.setModified(listingDTO.modified);
		listing.setCreated(listingDTO.created);
		listing.setClosingOn(listingDTO.closingOn);
		listing.setListedOn(listingDTO.listedOn);
		listing.setPostedOn(listingDTO.posted);
		listing.setName(listingDTO.name);
		listing.setOwner(keyToString(listingDTO.owner));
		listing.setContactEmail(listingDTO.contactEmail);
		listing.setFounders(listingDTO.founders);
		listing.setAskedForFunding(listingDTO.askedForFunding);
		listing.setSuggestedValuation(listingDTO.suggestedValuation);
		listing.setSuggestedAmount(listingDTO.suggestedAmount);
		listing.setSuggestedPercentage(listingDTO.suggestedPercentage);
		listing.setState(listingDTO.state.toString());
		listing.setPresentationId(keyToString(listingDTO.presentationId));
		listing.setBuinessPlanId(keyToString(listingDTO.businessPlanId));
		listing.setFinancialsId(keyToString(listingDTO.financialsId));
		listing.setLogo(listingDTO.logoBase64);
		listing.setVideo(listingDTO.videoUrl);
		listing.setSummary(listingDTO.summary);
		listing.setMantra(listingDTO.mantra);
		listing.setWebsite(listingDTO.website);
		listing.setCategory(listingDTO.category);

		listing.setAddress(listingDTO.address);
		listing.setLatitude(listingDTO.latitude);
		listing.setLongitude(listingDTO.longitude);

		listing.setBriefAddress(listingDTO.briefAddress);
		
		// calculating days left and days ago
		if (listingDTO.listedOn != null) {
			DateMidnight listed = new DateMidnight(listingDTO.listedOn.getTime());
			if (listed.isAfterNow()) {
				listing.setDaysAgo(-new Interval(new DateTime(), listed).toPeriod().getDays());
			} else {
				listing.setDaysAgo(new Interval(listed, new DateTime()).toPeriod().getDays());
			}
		} else {
			listing.setDaysAgo(0);
		}
		if (listingDTO.closingOn != null) {
			DateMidnight closing = new DateMidnight(listingDTO.closingOn.getTime()).plusDays(1);
			if (closing.isAfterNow()) {
				listing.setDaysLeft(new Interval(new DateTime(), closing).toPeriod().getDays());
			} else {
				listing.setDaysLeft(-new Interval(closing, new DateTime()).toPeriod().getDays());
			}
		} else {
			listing.setDaysLeft(0);
		}
		
		listing.setAnswer1(listingDTO.answer1);
		listing.setAnswer2(listingDTO.answer2);
		listing.setAnswer3(listingDTO.answer3);
		listing.setAnswer4(listingDTO.answer4);
		listing.setAnswer5(listingDTO.answer5);
		listing.setAnswer6(listingDTO.answer6);
		listing.setAnswer7(listingDTO.answer7);
		listing.setAnswer8(listingDTO.answer8);
		listing.setAnswer9(listingDTO.answer9);
		listing.setAnswer10(listingDTO.answer10);
		listing.setAnswer11(listingDTO.answer11);
		listing.setAnswer12(listingDTO.answer12);
		listing.setAnswer13(listingDTO.answer13);
		listing.setAnswer14(listingDTO.answer14);
		listing.setAnswer15(listingDTO.answer15);
		listing.setAnswer16(listingDTO.answer16);
		listing.setAnswer17(listingDTO.answer17);
		listing.setAnswer18(listingDTO.answer18);
		listing.setAnswer19(listingDTO.answer19);
		listing.setAnswer20(listingDTO.answer20);
		listing.setAnswer21(listingDTO.answer21);
		listing.setAnswer22(listingDTO.answer22);
		listing.setAnswer23(listingDTO.answer23);
		listing.setAnswer24(listingDTO.answer24);
		listing.setAnswer25(listingDTO.answer25);
		listing.setAnswer26(listingDTO.answer26);
		return listing;
	}

	public static ListingTileVO convertTile(Listing listingDTO) {
		if (listingDTO == null) {
			return null;
		}
		ListingTileVO listing = new ListingTileVO();
		listing.setId(new Key<Listing>(Listing.class, listingDTO.id).getString());
		listing.setMockData(listingDTO.mockData);
		listing.setModified(listingDTO.modified);
		listing.setCreated(listingDTO.created);
		listing.setClosingOn(listingDTO.closingOn);
		listing.setListedOn(listingDTO.listedOn);
		listing.setPostedOn(listingDTO.posted);
		listing.setName(listingDTO.name);
		listing.setOwner(keyToString(listingDTO.owner));
		listing.setAskedForFunding(listingDTO.askedForFunding);
		listing.setSuggestedValuation(listingDTO.suggestedValuation);
		listing.setSuggestedAmount(listingDTO.suggestedAmount);
		listing.setSuggestedPercentage(listingDTO.suggestedPercentage);
		listing.setState(listingDTO.state.toString());
		listing.setLogo(listingDTO.logoBase64);
		listing.setSummary(listingDTO.summary);
		listing.setMantra(listingDTO.mantra);
		listing.setWebsite(listingDTO.website);
		listing.setCategory(listingDTO.category);

		listing.setLatitude(listingDTO.latitude);
		listing.setLongitude(listingDTO.longitude);

		listing.setBriefAddress(listingDTO.briefAddress);
		
		return listing;
	}

	public static CommentVO convert(Comment commentDTO) {
		if (commentDTO == null) {
			return null;
		}
		CommentVO comment = new CommentVO();
		comment.setId(new Key<Comment>(Comment.class, commentDTO.id).getString());
		comment.setMockData(commentDTO.mockData);
		comment.setComment(commentDTO.comment);
		comment.setCommentedOn(commentDTO.commentedOn);
		comment.setListing(keyToString(commentDTO.listing));
		comment.setUser(keyToString(commentDTO.user));
		comment.setUserName(commentDTO.userNickName);
		return comment;
	}
	
	public static VoteVO convert(Vote ratingDTO) {
		if (ratingDTO == null) {
			return null;
		}
		VoteVO rating = new VoteVO();
		rating.setMockData(ratingDTO.mockData);
		rating.setId(new Key<Vote>(Vote.class, ratingDTO.id).getString());
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
		doc.setId(new Key<ListingDoc>(ListingDoc.class, docDTO.id).getString());
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
		user.setId(new Key<SBUser>(SBUser.class, userDTO.id).getString());
		user.setMockData(userDTO.mockData);
		user.setAdmin(userDTO.admin);
		user.setAccreditedInvestor(userDTO.investor);
		user.setEmail(userDTO.email);
		user.setName(userDTO.name);
		user.setJoined(userDTO.joined);
		user.setLastLoggedIn(userDTO.lastLoggedIn);
		user.setModified(userDTO.modified);
		user.setNickname(userDTO.nickname);
		user.setPhone(userDTO.phone);
		user.setLocation(userDTO.location);
		user.setStatus(userDTO.status.toString());
		user.setNotifyEnabled(userDTO.notifyEnabled);
		user.setEditedListing(keyToString(userDTO.editedListing));
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
		notif.setId(new Key<Notification>(Notification.class, notifDTO.id).getString());
		if (notifDTO.direction == Notification.Direction.A_TO_B) {
			notif.setUser(notifDTO.userB.getString());
			notif.setUserNickname(notifDTO.userBNickname);
			notif.setFromUser(notifDTO.userA != null ? notifDTO.userA.getString() : null);
			notif.setFromUserNickname(notifDTO.userANickname);
		} else {
			notif.setUser(notifDTO.userA.getString());
			notif.setUserNickname(notifDTO.userANickname);
			notif.setFromUser(notifDTO.userB != null ? notifDTO.userB.getString() : null);
			notif.setFromUserNickname(notifDTO.userBNickname);
		}
		notif.setParentNotification(notifDTO.parentNotification != null ? notifDTO.parentNotification.getString() : null);
		notif.setContextNotificationId(new Key<Notification>(Notification.class, notifDTO.context).getString());
		notif.setCreated(notifDTO.created);
		notif.setSentDate(notifDTO.sentDate);
		notif.setListing(notifDTO.listing != null ? notifDTO.listing.getString() : null);
		notif.setListingName(notifDTO.listingName);
		notif.setListingOwner(notifDTO.listingOwner);
		notif.setListingMantra(notifDTO.listingMantra);
		notif.setListingCategory(notifDTO.listingCategory);
		notif.setListingBriefAddress(notifDTO.listingBriefAddress);
		notif.setType(notifDTO.type.toString());
		notif.setRead(notifDTO.read);
		notif.setReplied(notifDTO.replied);
		String listingLink = notif.getLink();
		String fromUserNickname = notifDTO.direction == Notification.Direction.A_TO_B ? notifDTO.userANickname : notifDTO.userBNickname;
		switch(notifDTO.type) {
		case NEW_COMMENT_FOR_MONITORED_LISTING:
			notif.setTitle("New comment for listing " + notifDTO.listingName);
			notif.setText1("Listing " + notifDTO.listingName + " has received a new comment.");
			notif.setText3("In order to view comment(s) please visit <a href=\"" + listingLink + "\">company's page at startupbidder.com</a>.");
			break;
		case NEW_COMMENT_FOR_YOUR_LISTING:
			notif.setTitle("New comment for listing " + notifDTO.listingName);
			notif.setText1("Your listing \"" + notifDTO.listingName + "\" has received a new comment.");
			notif.setText3("In order to view comment(s) please visit <a href=\"" + listingLink + "\">company's page at startupbidder.com</a>.");
			break;
		case NEW_LISTING:
			notif.setTitle("New listing " + notifDTO.listingName + " posted");
			notif.setText1("A new listing " + notifDTO.listingName + " has been posted by " + notifDTO.listingOwner + " on startupbidder.com");
			notif.setText3("Please visit <a href=\"" + listingLink + "\">company's page at startupbidder.com</a>.");
			break;
		case ASK_LISTING_OWNER:
            if (notif.getParentNotification() == null) {
			    notif.setTitle("A question from " + fromUserNickname + " concerning listing " + notifDTO.listingName);
                notif.setText1("Question about listing " + notifDTO.listingName + " has been posted by " + fromUserNickname + ":");
            } else {
                notif.setTitle("Received reply concerning question for listing " + notifDTO.listingName + " from " + fromUserNickname);
                notif.setText1("Reply concerning listing " + notifDTO.listingName + " has been posted by " + fromUserNickname + ":");
            }
			notif.setText3("Please visit <a href=\"" + listingLink + "\">company's page at startupbidder.com</a>.");
			if (StringUtils.isEmpty(notif.getParentNotification())) {
				// it's question
				notif.setText2(notifDTO.message);
			} else {
				// it's answer
				notif.setText2(notifDTO.question);
				notif.setCreated(notifDTO.questionDate);
				notif.setAnswer(notifDTO.message); // setting answer
				notif.setAnswerDate(notifDTO.created);
			}
			break;
		case PRIVATE_MESSAGE:
            if (notif.getParentNotification() == null) {
			    notif.setTitle("Private message from " + fromUserNickname + " concerning listing " + notifDTO.listingName );
			    notif.setText1("Private message from user " + fromUserNickname + ":");
			    notif.setText2(notifDTO.message);
            } else {
                notif.setTitle("Received reply concerning private message for listing " + notifDTO.listingName + " from " + fromUserNickname);
                notif.setText1("Private reply concerning listing " + notifDTO.listingName + " has been posted by " + fromUserNickname + ":");
                notif.setText2(notifDTO.message);
            }
            notif.setText3("Please visit <a href=\"" + listingLink + "\">company's page at startupbidder.com</a>.");
			break;
		}

		return notif;
	}
	
	public static MonitorVO convert(Monitor monitorDTO) {
		if (monitorDTO == null) {
			return null;
		}
		MonitorVO monitor = new MonitorVO();
		monitor.setId(new Key<Monitor>(Monitor.class, monitorDTO.id).getString());
		monitor.setMockData(monitorDTO.mockData);
		monitor.setCreated(monitorDTO.created);
		monitor.setDeactivated(monitorDTO.deactivated);
		monitor.setUser(keyToString(monitorDTO.user));
		monitor.setListingId(monitorDTO.monitoredListing.getString());
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

	public static List<ListingTileVO> convertListingTiles(List<Listing> bpDtoList) {
		if (bpDtoList == null) {
			return null;
		}
		List<ListingTileVO> bpVoList = new ArrayList<ListingTileVO>();
		for (Listing bpDTO : bpDtoList) {
			bpVoList.add(convertTile(bpDTO));
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
	
	public static List<QuestionAnswerVO> convertQuestionAnswers(List<QuestionAnswer> qaDtoList) {
		if (qaDtoList == null) {
			return null;
		}
		List<QuestionAnswerVO> qaVoList = new ArrayList<QuestionAnswerVO>();
		for (QuestionAnswer qaDTO : qaDtoList) {
			QuestionAnswerVO qaVO = convert(qaDTO);
			qaVoList.add(qaVO);
		}
		return qaVoList;
	}
	
	public static List<PrivateMessageVO> convertPrivateMessage(List<PrivateMessage> msgDtoList) {
		if (msgDtoList == null) {
			return null;
		}
		List<PrivateMessageVO> msgVoList = new ArrayList<PrivateMessageVO>();
		for (PrivateMessage msgDTO : msgDtoList) {
			PrivateMessageVO msgVO = convert(msgDTO);
			msgVoList.add(msgVO);
		}
		return msgVoList;
	}
	
	public static List<PrivateMessageUserVO> convertPrivateMessageUser(List<PrivateMessageUser> msgDtoList) {
		if (msgDtoList == null) {
			return null;
		}
		List<PrivateMessageUserVO> qaVoList = new ArrayList<PrivateMessageUserVO>();
		for (PrivateMessageUser msgDTO : msgDtoList) {
			PrivateMessageUserVO msgVO = convert(msgDTO);
			qaVoList.add(msgVO);
		}
		return qaVoList;
	}
	
	public static Map<String, List<BidVO>> convertBidMap(Map<Key<SBUser>, List<Bid>> bidMap) {
		Map<String, List<BidVO>> result = new HashMap<String, List<BidVO>>();
		for (Map.Entry<Key<SBUser>, List<Bid>> entry : bidMap.entrySet()) {
			result.put(entry.getKey().getString(), convertBids(entry.getValue()));
		}
		return result;
	}

	public static void updateBriefAddress(Listing listingDTO) {
		String briefAddress = "";
		if (!StringUtils.isEmpty(listingDTO.country)) {
			briefAddress = listingDTO.country;
    		listingDTO.country = listingDTO.country.toLowerCase();
		}
		if (!StringUtils.isEmpty(listingDTO.usState) && briefAddress.equals("USA")) {
			briefAddress = listingDTO.usState + (briefAddress.length() > 0 ? ", " : "") + briefAddress;
    		listingDTO.usState = listingDTO.usState.toLowerCase();
		}
		if (!StringUtils.isEmpty(listingDTO.city)) {
			briefAddress = listingDTO.city + (briefAddress.length() > 0 ? ", " : "") + briefAddress;
    		listingDTO.city = listingDTO.city.toLowerCase();
		}
		listingDTO.briefAddress = briefAddress;
	}

}
