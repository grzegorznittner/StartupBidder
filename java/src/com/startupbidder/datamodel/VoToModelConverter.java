package com.startupbidder.datamodel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.googlecode.objectify.Key;
import com.startupbidder.vo.CommentVO;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.ListingPropertyVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.MonitorVO;
import com.startupbidder.vo.NotificationVO;
import com.startupbidder.vo.QuestionAnswerVO;
import com.startupbidder.vo.SystemPropertyVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.vo.VoteVO;

/**
 * Helper classes which converts VO objects to model objects.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class VoToModelConverter {
	@SuppressWarnings("rawtypes")
	public static Key stringToKey(String webSafeKey) {
		try {
			return Key.create(webSafeKey);
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static QuestionAnswer convert(QuestionAnswerVO qaVO) {
		QuestionAnswer qa = new QuestionAnswer();
		if (!StringUtils.isEmpty(qaVO.getId())) {
			qa.id = qaVO.toKeyId();
		}
		qa.answer = qaVO.getAnswer();
		qa.answerDate = qaVO.getAnswerDate();
		qa.created = qaVO.getCreated();
		qa.published = qaVO.isPublished();
		qa.question = qaVO.getQuestion();
		qa.user = (Key<SBUser>)stringToKey(qaVO.getUser());
		qa.userNickname = qaVO.getUserNickname();
		qa.listing = (Key<Listing>)stringToKey(qaVO.getListing());		
		return qa;
	}
	
	@SuppressWarnings("unchecked")
	public static Listing convert(ListingVO listingVO) {
		Listing listing = new Listing();
		if (!StringUtils.isEmpty(listingVO.getId())) {
			listing.id = listingVO.toKeyId();
		}
		listing.mockData = listingVO.isMockData();
		listing.modified = listingVO.getModified();
		listing.created = listingVO.getCreated();
		listing.closingOn = listingVO.getClosingOn();
		listing.listedOn = listingVO.getListedOn();
		listing.posted = listingVO.getPostedOn();
		listing.name = listingVO.getName();
		listing.owner = (Key<SBUser>)stringToKey(listingVO.getOwner());
		listing.contactEmail = listingVO.getContactEmail();
		listing.founders = listingVO.getFounders();
		listing.askedForFunding = listingVO.isAskedForFunding();
		listing.suggestedValuation = listingVO.getSuggestedValuation();
		listing.suggestedPercentage = listingVO.getSuggestedPercentage();
		listing.suggestedAmount = listingVO.getSuggestedAmount();
		if (!StringUtils.isEmpty(listingVO.getState())) {
			listing.state = Listing.State.valueOf(StringUtils.upperCase(listingVO.getState()));
		}
		listing.presentationId = listingVO.getPresentationId() != null ? stringToKey(listingVO.getPresentationId()) : null;
		listing.businessPlanId = listingVO.getBuinessPlanId() != null ? stringToKey(listingVO.getBuinessPlanId()) : null;
		listing.financialsId = listingVO.getFinancialsId() != null ? stringToKey(listingVO.getFinancialsId()) : null;
		listing.videoUrl = listingVO.getVideo();
		listing.summary = listingVO.getSummary();
		listing.mantra = listingVO.getMantra();
		listing.website = listingVO.getWebsite();
		listing.category = listingVO.getCategory();
		if (!StringUtils.isEmpty(listingVO.getType())) {
			listing.type = Listing.Type.valueOf(StringUtils.upperCase(listingVO.getType()));
		}
		listing.platform = listingVO.getPlatform();
		listing.notes = listingVO.getNotes();
		listing.address = listingVO.getAddress();
		listing.latitude = listingVO.getLatitude();
		listing.longitude = listingVO.getLongitude();
		
		listing.answer1 = listingVO.getAnswer1();
		listing.answer2 = listingVO.getAnswer2();
		listing.answer3 = listingVO.getAnswer3();
		listing.answer4 = listingVO.getAnswer4();
		listing.answer5 = listingVO.getAnswer5();
		listing.answer6 = listingVO.getAnswer6();
		listing.answer7 = listingVO.getAnswer7();
		listing.answer8 = listingVO.getAnswer8();
		listing.answer9 = listingVO.getAnswer9();
		listing.answer10 = listingVO.getAnswer10();
		listing.answer11 = listingVO.getAnswer11();
		listing.answer12 = listingVO.getAnswer12();
		listing.answer13 = listingVO.getAnswer13();
		listing.answer14 = listingVO.getAnswer14();
		listing.answer15 = listingVO.getAnswer15();
		listing.answer16 = listingVO.getAnswer16();
		listing.answer17 = listingVO.getAnswer17();
		listing.answer18 = listingVO.getAnswer18();
		listing.answer19 = listingVO.getAnswer19();
		listing.answer20 = listingVO.getAnswer20();
		listing.answer21 = listingVO.getAnswer21();
		listing.answer22 = listingVO.getAnswer22();
		listing.answer23 = listingVO.getAnswer23();
		listing.answer24 = listingVO.getAnswer24();
		listing.answer25 = listingVO.getAnswer25();
		listing.answer26 = listingVO.getAnswer26();
		return listing;
	}
	
	public static void updateListingProperty(Listing listing, ListingPropertyVO property) {
		String name = property.getPropertyName();
		if (name.equalsIgnoreCase("title")) {
			listing.name = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("mantra")) {
			listing.mantra = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("summary")) {
			listing.summary = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("website")) {
			listing.website = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("category")) {
			listing.category = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("address")) {
			listing.address = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("latitude")) {
			listing.latitude = NumberUtils.toDouble(property.getPropertyValue());
		} else if (name.equalsIgnoreCase("longitude")) {
			listing.longitude = NumberUtils.toDouble(property.getPropertyValue());
		} else if (name.equalsIgnoreCase("country")) {
			listing.country = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("state")) {
			listing.usState = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("city")) {
			listing.city = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("asked_fund")) {
			listing.askedForFunding = BooleanUtils.toBoolean(property.getPropertyValue());
		} else if (name.equalsIgnoreCase("suggested_amt")) {
			listing.suggestedAmount = NumberUtils.toInt(property.getPropertyValue(), 0);
		} else if (name.equalsIgnoreCase("suggested_pct")) {
			listing.suggestedPercentage = NumberUtils.toInt(property.getPropertyValue(), 0);
		} else if (name.equalsIgnoreCase("answer1")) {
			listing.answer1 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer2")) {
			listing.answer2 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer3")) {
			listing.answer3 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer4")) {
			listing.answer4 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer5")) {
			listing.answer5 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer6")) {
			listing.answer6 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer7")) {
			listing.answer7 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer8")) {
			listing.answer8 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer9")) {
			listing.answer9 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer10")) {
			listing.answer10 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer11")) {
			listing.answer11 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer12")) {
			listing.answer12 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer13")) {
			listing.answer13 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer14")) {
			listing.answer14 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer15")) {
			listing.answer15 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer16")) {
			listing.answer16 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer17")) {
			listing.answer17 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer18")) {
			listing.answer18 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer19")) {
			listing.answer19 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer20")) {
			listing.answer20 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer21")) {
			listing.answer21 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer22")) {
			listing.answer22 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer23")) {
			listing.answer23 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer24")) {
			listing.answer24 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer25")) {
			listing.answer25 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("answer26")) {
			listing.answer26 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("logoBase64")) {
			listing.logoBase64 = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("contact_email")) {
			listing.contactEmail = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("founders")) {
			listing.founders = property.getPropertyValue();
		} else if (name.equalsIgnoreCase("video")) {
			listing.videoUrl = property.getPropertyValue();
		}
	}

	public static Comment convert(CommentVO commentVO) {
		Comment comment = new Comment();
		if (!StringUtils.isEmpty(commentVO.getId())) {
			comment.id = commentVO.toKeyId();
		}
		comment.comment = commentVO.getComment();
		comment.listing = new Key<Listing>(commentVO.getListing());
		comment.commentedOn = commentVO.getCommentedOn();
		comment.user = new Key<SBUser>(commentVO.getUser());
		comment.userNickName = commentVO.getUserName();
		return comment;
	}
	
	public static Vote convert(VoteVO ratingVO) {
		Vote rating = new Vote();
		if (!StringUtils.isEmpty(ratingVO.getId())) {
			rating.id = new Key<Vote>(ratingVO.getId()).getId();
		}
		rating.mockData = ratingVO.isMockData();
		rating.listing = new Key<Listing>(ratingVO.getListing());
		rating.user = new Key<SBUser>(ratingVO.getUser());
		rating.value = ratingVO.getValue();
		return rating;
	}
	
	public static ListingDoc convert(ListingDocumentVO docVO) {
		ListingDoc doc = new ListingDoc();
		if (!StringUtils.isEmpty(docVO.getId())) {
			doc.id = new Key<ListingDoc>(docVO.getId()).getId();
		}
		doc.mockData = docVO.isMockData();
		doc.blob = docVO.getBlob();
		doc.created = docVO.getCreated();
		if (!StringUtils.isEmpty(docVO.getType())) {
			doc.type = ListingDoc.Type.valueOf(docVO.getType());
		}
		return doc;
	}
	
	@SuppressWarnings("unchecked")
	public static SBUser convert(UserVO userVO) {
		SBUser user = new SBUser();
		if (!StringUtils.isEmpty(userVO.getId())) {
			user.id = new Key<SBUser>(userVO.getId()).getId();
		}
		user.mockData = userVO.isMockData();
		user.admin = userVO.isAdmin();
		user.investor = userVO.isAccreditedInvestor();
		user.email = userVO.getEmail();
		user.name = userVO.getName();
		user.joined = userVO.getJoined();
		user.lastLoggedIn = userVO.getLastLoggedIn();
		user.modified = userVO.getModified();
		user.nickname = userVO.getNickname();
		user.notifyEnabled = userVO.isNotifyEnabled();
		user.phone = userVO.getPhone();
		user.location = userVO.getLocation();
		user.editedListing = stringToKey(userVO.getEditedListing());
		if (!StringUtils.isEmpty(userVO.getStatus())) {
			user.status = SBUser.Status.valueOf(StringUtils.upperCase(userVO.getStatus()));
		}
		return user;
	}
	
	public static SystemProperty convert(SystemPropertyVO propertyVO) {
		SystemProperty property = new SystemProperty();
		property.author = propertyVO.getAuthor();
		property.created = propertyVO.getCreated();
		property.name = propertyVO.getName();
		property.value = propertyVO.getValue();
		return property;
	}

	public static Notification convert(NotificationVO notificationVO) {
		Notification notification = new Notification();
		if (!StringUtils.isEmpty(notificationVO.getId())) {
			notification.id = new Key<Notification>(notificationVO.getId()).getId();
		}
		notification.created = notificationVO.getCreated();
		notification.sentDate = notificationVO.getSentDate();
		notification.type = Notification.Type.valueOf(notificationVO.getType().toUpperCase());
		notification.listing = new Key<Listing>(notificationVO.getListing());
		notification.read = notificationVO.isRead();

		return notification;
	}
	
	public static Monitor convert(MonitorVO monitorVO) {
		Monitor monitor = new Monitor();
		if (!StringUtils.isEmpty(monitorVO.getId())) {
			monitor.id = new Key<Monitor>(monitorVO.getId()).getId();
		}
		monitor.mockData = monitorVO.isMockData();
		monitor.created = monitorVO.getCreated();
		monitor.deactivated = monitorVO.getDeactivated();
		monitor.monitoredListing = new Key<Listing>(monitorVO.getListingId());
		monitor.user = new Key<SBUser>(monitorVO.getUser());
		monitor.active = monitorVO.isActive();

		return monitor;
	}
	
	public static List<Listing> convertListings(List<ListingVO> bpVOList) {
		List<Listing> bpDtoList = new ArrayList<Listing>();
		for (ListingVO bpVO : bpVOList) {
			Listing bpDTO = convert(bpVO);
			bpDtoList.add(bpDTO);
		}
		return bpDtoList;
	}

	public static List<Comment> convertComments(List<CommentVO> commentVoList) {
		List<Comment> commentDTOList = new ArrayList<Comment>();
		for (CommentVO commentVO : commentVoList) {
			Comment commentDTO = convert(commentVO);
			commentDTOList.add(commentDTO);
		}
		return commentDTOList;
	}

	public static List<Notification> convertNotifications(List<NotificationVO> notifVoList) {
		List<Notification> notifDtoList = new ArrayList<Notification>();
		for (NotificationVO notifVO : notifVoList) {
			Notification notifDTO = convert(notifVO);
			notifDtoList.add(notifDTO);
		}
		return notifDtoList;
	}

	public static List<Monitor> convertMonitors(List<MonitorVO> monitorVoList) {
		List<Monitor> monitorDtoList = new ArrayList<Monitor>();
		for (MonitorVO monitorVO : monitorVoList) {
			Monitor monitorDTO = convert(monitorVO);
			monitorDtoList.add(monitorDTO);
		}
		return monitorDtoList;
	}
	
	public static List<QuestionAnswer> convertQuestionAnswers(List<QuestionAnswerVO> monitorVoList) {
		List<QuestionAnswer> qaDtoList = new ArrayList<QuestionAnswer>();
		for (QuestionAnswerVO qaVO : monitorVoList) {
			QuestionAnswer qaDTO = convert(qaVO);
			qaDtoList.add(qaDTO);
		}
		return qaDtoList;
	}
}
