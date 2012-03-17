package com.startupbidder.web;

import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.datanucleus.util.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.utils.SystemProperty;
import com.googlecode.objectify.Key;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Category;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.ListingDoc.Type;
import com.startupbidder.datamodel.ListingStats;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.vo.BaseVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingAndUserVO;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.ListingListVO;
import com.startupbidder.vo.ListingPropertyVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.UserBasicVO;
import com.startupbidder.vo.UserVO;

public class ListingFacade {
	private static final Logger log = Logger.getLogger(ListingFacade.class.getName());
	
	public static enum UpdateReason {NEW_BID, BID_UPDATE, NEW_COMMENT, NEW_VOTE, NONE};
	/**
	 * Delay for listing stats task execution.
	 */
	private static final int LISTING_STATS_UPDATE_DELAY = 60 * 1000;
	/**
	 * Default value for closing on date set during listing activation.
	 */
	private static final int LISTING_DEFAULT_CLOSING_ON_DAYS = 30;
	
	private DateTimeFormatter timeStampFormatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss_SSS");
	private static ListingFacade instance;
	
	public static ListingFacade instance() {
		if (instance == null) {
			instance = new ListingFacade();
		}
		return instance;
	}
	
	private ListingFacade() {
	}
	
	private ObjectifyDatastoreDAO getDAO() {
		return ObjectifyDatastoreDAO.getInstance();
	}

	/**
	 * Creates new listing.
	 * Sets loggedin user as the owner of the listing.
	 * Sets listing's state to NEW.
	 */
	public ListingAndUserVO createListing(UserVO loggedInUser) {
		ListingAndUserVO result = new ListingAndUserVO();
		if (loggedInUser == null) {
			log.log(Level.WARNING, "Only logged in user can create listing", new Exception("Not logged in"));
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage("Only logged in user can create listing");
		} else {
			Listing l = new Listing();
			l.state = Listing.State.NEW;
			l.owner = new Key<SBUser>(loggedInUser.getId());
			l.created = new Date();
			ListingVO newListing = DtoToVoConverter.convert(getDAO().createListing(l));
			loggedInUser.setEditedListing(newListing.getId());
			
			// at that stage listing is not yet active so there is no point of updating statistics
			applyListingData(loggedInUser, newListing);			
			result.setListing(newListing);
		}
		return result;
	}

	public ListingAndUserVO getListing(UserVO loggedInUser, String listingId) {
		ListingAndUserVO listingAndUser = new ListingAndUserVO();
		long id = 0;
		try {
			id = BaseVO.toKeyId(listingId);
			ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(id));
			
			if (listing != null) {
				applyListingData(loggedInUser, listing);
				listingAndUser.setListing(listing);
			} else {
				listingAndUser.setErrorCode(ErrorCodes.DATASTORE_ERROR);
				listingAndUser.setErrorMessage("Listing has not been found");
			}
		} catch (Exception e) {
			log.warning("Invalid key passed '" + listingId + "'");
			listingAndUser.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			listingAndUser.setErrorMessage("Invalid key passed");
		}
		return listingAndUser;
	}
	
	public ListingAndUserVO updateListingProperties(UserVO loggedInUser, List<ListingPropertyVO> properties) {
		ListingAndUserVO result = new ListingAndUserVO();

		if (loggedInUser == null || loggedInUser.getEditedListing() == null) {
			result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
			result.setErrorMessage("User is not logged in or is not editing any listing");
			return result;
		}
		// retrieving edited listing
		Listing listing = getDAO().getListing(BaseVO.toKeyId(loggedInUser.getEditedListing()));
		if (listing == null) {
			result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
			result.setErrorMessage("User is not editing any listing");
			return result;
		}
		if (listing.state != Listing.State.NEW) {
			result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
			result.setErrorMessage("Listing is not in NEW state");
			return result;
		}
		boolean fetchedDoc = false;
		boolean fetchError = false;
		StringBuffer infos = new StringBuffer();
		// removing non updatable fields and handling fetched fields
		List<ListingPropertyVO> propsToUpdate = new ArrayList<ListingPropertyVO>();
		for (ListingPropertyVO prop : properties) {
			String propertyName = prop.getPropertyName().toLowerCase();
			if (ListingVO.UPDATABLE_PROPERTIES.contains(propertyName)) {
				propsToUpdate.add(prop);
			} else if (ListingVO.FETCHED_PROPERTIES.contains(propertyName)) {
				ListingDoc doc = fetchAndUpdateListingDoc(listing, prop);
				if (doc != null) {
					Listing updatedlisting = updateListingDoc(loggedInUser, listing, doc);
					if (updatedlisting != null) {
						listing = updatedlisting;
						fetchedDoc = true;
					} else {
						result.setErrorCode(ErrorCodes.DATASTORE_ERROR);
						result.setErrorMessage("Error updating listing. " + infos.toString());
						fetchError = true;
						break;
					}
				} else {
					result.setErrorCode(ErrorCodes.APPLICATION_ERROR);
					result.setErrorMessage("Error while fetching/converting external resource. " + infos.toString());
					fetchError = true;
					break;
				}
			} else {
				infos.append("Removed field '" + propertyName + "' as it's not updatable. ");
			}
		}
		if (fetchError) {
			result.setListing(DtoToVoConverter.convert(listing));
			return result;
		}
		log.log(Level.INFO, infos.toString(), new Exception("Listing update verification"));
		if (propsToUpdate.isEmpty() && !fetchedDoc) {
			result.setListing(DtoToVoConverter.convert(listing));
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage("Nothing to update. " + infos.toString());
			return result;
		}
		
		if (!propsToUpdate.isEmpty()) {
			for (ListingPropertyVO prop : propsToUpdate) {
				VoToModelConverter.updateListingProperty(listing, prop);
			}
			log.info("Updating listing: " + listing);
			getDAO().storeListing(listing);
		}
		result.setListing(DtoToVoConverter.convert(listing));
		
		return result;
	}
	
	private ListingDoc fetchAndUpdateListingDoc(Listing listing, ListingPropertyVO prop) {
		byte[] docBytes = null;
		String mimeType = null;
		try {
			URLConnection con = new URL(prop.getPropertyValue()).openConnection();
			docBytes = IOUtils.toByteArray(con.getInputStream());
			mimeType = con.getContentType();
			log.info("Fetched " + docBytes.length + " bytes, content type '" + mimeType
					+ "', from '" + prop.getPropertyValue() + "'");
		} catch (Exception e) {
			log.log(Level.WARNING, "Error while fetching document from " + prop.getPropertyValue(), e);
			return null;
		}
		if (prop.getPropertyName().equalsIgnoreCase("logo_url") && setLogoBase64(listing, docBytes) == null) {
			log.log(Level.WARNING, "Image conversion error");
			return null;
		}

		try {
			FileService fileService = FileServiceFactory.getFileService();
			AppEngineFile file = fileService.createNewBlobFile(mimeType);
			FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
			writeChannel.write(ByteBuffer.wrap(docBytes));
			writeChannel.closeFinally();

			ListingDoc doc = new ListingDoc();
			doc.blob = fileService.getBlobKey(file);
			if (doc.blob == null) {
				log.warning("Blob not created for file " + file);
				return null;
			}
			doc.type = getListingDocTypeFromPropertyName(prop.getPropertyName());
			getDAO().createListingDocument(doc);
			return doc;
		} catch (Exception e) {
			log.log(Level.WARNING, "Error storing doc as blob", e);
			return null;
		}
	}

	private Type getListingDocTypeFromPropertyName(String propertyName) {
		if (propertyName.equalsIgnoreCase("business_plan_url")) {
			return ListingDoc.Type.BUSINESS_PLAN;
		} else if (propertyName.equalsIgnoreCase("presentation_url")) {
			return ListingDoc.Type.PRESENTATION;
		} else if (propertyName.equalsIgnoreCase("financials_url")) {
			return ListingDoc.Type.FINANCIALS;
		} else if (propertyName.equalsIgnoreCase("logo_url")) {
			return ListingDoc.Type.LOGO;
		}
		log.warning("Not recognized property '" + propertyName + "', defaulting to LOGO");
		return ListingDoc.Type.LOGO;
	}

	public ListingVO updateListing(UserVO loggedInUser, ListingVO listing) {
		Listing dbListing = getDAO().getListing(listing.toKeyId());
		// listing should exist, user should be logged in and an owner of the listing or admin
		if (loggedInUser == null || dbListing == null) {
			log.warning("Listing doesn't exist or user not logged in");
			return null;
		}
		boolean adminOrOwner = StringUtils.areStringsEqual(loggedInUser.getId(), dbListing.owner.getString())
				|| loggedInUser.isAdmin();
		if (!adminOrOwner) {
			log.warning("User must be an owner of the listing or an admin");
			return null;
		}
		// only NEW or ACTIVE listings can be updated
		if (dbListing.state == Listing.State.NEW || dbListing.state == Listing.State.ACTIVE) {
			ListingVO forUpdate = DtoToVoConverter.convert(dbListing);
			forUpdate.setId(listing.getId());
			forUpdate.setName(listing.getName());
			forUpdate.setSummary(listing.getSummary());
			forUpdate.setSuggestedAmount(listing.getSuggestedAmount());
			forUpdate.setSuggestedPercentage(listing.getSuggestedPercentage());
			forUpdate.setPresentationId(listing.getPresentationId());
			forUpdate.setBuinessPlanId(listing.getBuinessPlanId());
			forUpdate.setFinancialsId(listing.getFinancialsId());
			
			Listing updatedListing = getDAO().updateListing(VoToModelConverter.convert(forUpdate));
			if (updatedListing != null && updatedListing.state == Listing.State.ACTIVE) {
				scheduleUpdateOfListingStatistics(updatedListing.getWebKey(), UpdateReason.NONE);
			}
			ListingVO updatedListingVO = DtoToVoConverter.convert(updatedListing);
			applyListingData(loggedInUser, updatedListingVO);
			return updatedListingVO;
		}
		return null;
	}

	/**
	 * Marks listing as prepared for posting on statupbidder.
	 * Only owner of the listing can do that
	 */
	public ListingVO activateListing(UserVO loggedInUser, String listingId) {
		Listing dbListing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (dbListing.state != Listing.State.POSTED && dbListing.state != Listing.State.FROZEN) {
			log.log(Level.WARNING, "Only posted and frozen listings can be activated. This listing is " + dbListing.state, new Exception("Not valid state"));
			return null;
		}
		if (loggedInUser == null) {
			log.log(Level.WARNING, "User is not logged in!", new Exception("Not logged in user"));
			return null;
		}
		// Only admins can do activation
		if (!loggedInUser.isAdmin()) {
			log.log(Level.WARNING, "User " + loggedInUser + " is not an admin. Only admin can activate listings.", new Exception("Not an admin"));
			return null;
		}
		if (dbListing.state == Listing.State.POSTED) {
			DateMidnight midnight = new DateMidnight();
			dbListing.closingOn = midnight.plusDays(LISTING_DEFAULT_CLOSING_ON_DAYS).toDate();
		}
		ListingVO forUpdate = DtoToVoConverter.convert(dbListing);

		forUpdate.setState(Listing.State.ACTIVE.toString());
		
		Listing updatedListing = getDAO().updateListing(VoToModelConverter.convert(forUpdate));
		if (updatedListing != null) {
			scheduleUpdateOfListingStatistics(updatedListing.getWebKey(), UpdateReason.NONE);
		}
		ListingVO toReturn = DtoToVoConverter.convert(updatedListing);
		applyListingData(loggedInUser, toReturn);
		return toReturn;
	}

	/**
	 * Action can be done only by listing owner.
	 * Makes listing ready to be published on StartupBidder website, but t
	 */
	public ListingVO postListing(UserVO loggedInUser, String listingId) {
		Listing dbListing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (loggedInUser == null || dbListing == null) {
			log.log(Level.WARNING, "User " + loggedInUser + " is logged in or listing doesn't exist", new Exception("Not logged in"));
			return null;
		}
		if (!StringUtils.areStringsEqual(loggedInUser.getId(), dbListing.owner.getString())) {
			log.log(Level.WARNING, "User '" + loggedInUser + "' is not an owner of listing " + dbListing, new Exception("Not listing owner"));
			return null;
		}
				
		// only NEW listings can be posted
		if (dbListing.state == Listing.State.NEW) {
			String logs = verifyListingsMandatoryFields(dbListing);
			if (!StringUtils.isEmpty(logs)) {
				log.log(Level.WARNING, "Listing validation error. " + logs, new Exception("Listing verification error"));
				return null;
			}
			
			ListingVO forUpdate = DtoToVoConverter.convert(dbListing);

			forUpdate.setPostedOn(new Date());
			forUpdate.setState(Listing.State.POSTED.toString());
			
			Listing updatedListing = getDAO().updateListing(VoToModelConverter.convert(forUpdate));
			if (updatedListing != null) {
				scheduleUpdateOfListingStatistics(updatedListing.getWebKey(), UpdateReason.NONE);
			}
			ListingVO toReturn = DtoToVoConverter.convert(updatedListing);
			applyListingData(loggedInUser, toReturn);
			return toReturn;
		}
		log.log(Level.WARNING, "Only NEW listing can be marked as POSTED (state is " + dbListing.state + ")", new Exception("Not valid state"));
		return null;
	}

	private String verifyListingsMandatoryFields(Listing listing) {
		StringBuffer logs = new StringBuffer();
		checkMandatoryStringField(logs, "Name", listing.name, 5, 64);
		checkMandatoryStringField(logs, "Mantra", listing.mantra, 5, 128);
		checkMandatoryStringField(logs, "Summary", listing.summary, 5, 512);

		if (!StringUtils.isEmpty(listing.website)) {
			try {
				new URL(listing.website);
			} catch (Exception e) {
				logs.append("Website is not a valid URL. ");
			}
		}
		
		if (listing.category == null || !getCategories().values().contains(listing.category)) {
			logs.append("Category is not set or contains not valid category. ");			
		}

		if (listing.askedForFunding) {
			if (listing.suggestedAmount < 1000) {
				logs.append("Suggested amount is less than 1000. ");
			}
			if (listing.suggestedPercentage < 1 || listing.suggestedPercentage > 75) {
				logs.append("Suggested percentage is not valid. ");
			}
		}
		
//		if (listing.businessPlanId == null) {
//			logs.append("Business plan document not provided. ");
//		}
//		if (listing.presentationId == null) {
//			logs.append("Presentation document not provided. ");
//		}
//		if (listing.financialsId == null) {
//			logs.append("Financial document not provided. ");
//		}
		if (listing.logoBase64 == null) {
			logs.append("Logo image not provided. ");
		}
		if (listing.videoUrl == null) {
			logs.append("Video not provided. ");
		}

		checkMandatoryStringField(logs, "Answer1", listing.answer1, 16, 512);
		checkMandatoryStringField(logs, "Answer2", listing.answer2, 16, 512);
		checkMandatoryStringField(logs, "Answer3", listing.answer3, 16, 512);
		checkMandatoryStringField(logs, "Answer4", listing.answer4, 16, 512);
		checkMandatoryStringField(logs, "Answer5", listing.answer5, 16, 512);
		checkMandatoryStringField(logs, "Answer6", listing.answer6, 16, 512);
		checkMandatoryStringField(logs, "Answer7", listing.answer7, 16, 512);
		checkMandatoryStringField(logs, "Answer8", listing.answer8, 16, 512);
		checkMandatoryStringField(logs, "Answer9", listing.answer9, 16, 512);
		checkMandatoryStringField(logs, "Answer10", listing.answer10, 16, 512);
		checkMandatoryStringField(logs, "Answer11", listing.answer11, 16, 512);
		checkMandatoryStringField(logs, "Answer12", listing.answer12, 16, 512);
		checkMandatoryStringField(logs, "Answer13", listing.answer13, 16, 512);
		checkMandatoryStringField(logs, "Answer14", listing.answer14, 16, 512);
		checkMandatoryStringField(logs, "Answer15", listing.answer15, 16, 512);
		checkMandatoryStringField(logs, "Answer16", listing.answer16, 16, 512);
		checkMandatoryStringField(logs, "Answer17", listing.answer17, 16, 512);
		checkMandatoryStringField(logs, "Answer18", listing.answer18, 16, 512);
		checkMandatoryStringField(logs, "Answer19", listing.answer19, 16, 512);
		checkMandatoryStringField(logs, "Answer20", listing.answer20, 16, 512);
		checkMandatoryStringField(logs, "Answer21", listing.answer21, 16, 512);
		checkMandatoryStringField(logs, "Answer22", listing.answer22, 16, 512);
		checkMandatoryStringField(logs, "Answer23", listing.answer23, 16, 512);
		checkMandatoryStringField(logs, "Answer24", listing.answer24, 16, 512);
		checkMandatoryStringField(logs, "Answer25", listing.answer25, 16, 512);
		checkMandatoryStringField(logs, "Answer26", listing.answer26, 16, 512);

		return logs.toString();
	}

	private void checkMandatoryStringField(StringBuffer logs, String fieldName, String field, int minLength, int maxLength) {
		if (StringUtils.isEmpty(field)) {
			logs.append(fieldName + " is empty. ");
		} else {
			if (field.length() < minLength) {
				logs.append(fieldName + " is too short, has only " + field.length() + " characters. ");
			}
			if (field.length() > maxLength) {
				logs.append(fieldName + " is too long, has " + field.length() + " characters. ");
			}
		}
	}

	/**
	 * Withdraws listing so it's not available for bidding anymore.
	 */
	public ListingVO withdrawListing(UserVO loggedInUser, String listingId) {
		Listing dbListing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (loggedInUser == null || dbListing == null) {
			log.log(Level.WARNING, "Listing doesn't exist or user not logged in", new Exception("Listing doesn't exist"));
			return null;
		}
		if (!StringUtils.areStringsEqual(loggedInUser.getId(), dbListing.owner.getString())) {
			log.log(Level.WARNING, "User must be an owner of the listing", new Exception("Not an owner"));
			return null;
		}
		
		// only ACTIVE and POSTED listings can be WITHDRAWN
		if (dbListing.state != Listing.State.CLOSED && dbListing.state != Listing.State.WITHDRAWN) {
			ListingVO forUpdate = DtoToVoConverter.convert(dbListing);

			forUpdate.setState(Listing.State.WITHDRAWN.toString());
			
			Listing updatedListing = getDAO().updateListing(VoToModelConverter.convert(forUpdate));
			if (updatedListing != null) {
				scheduleUpdateOfListingStatistics(updatedListing.getWebKey(), UpdateReason.NONE);
			}
			ListingVO toReturn = DtoToVoConverter.convert(updatedListing);
			applyListingData(loggedInUser, toReturn);
			return toReturn;
		}
		log.log(Level.WARNING, "CLOSED or WITHDRAWN listings cannot be withdrawn (state was " + dbListing.state + ")", new Exception("Not valid state"));
		return null;
	}

	/**
	 * Sends back listing for update to the owner as it is not ready for posting on startupbidder site
	 */
	public ListingVO sendBackListingToOwner(UserVO loggedInUser, String listingId) {
		Listing dbListing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (loggedInUser == null || dbListing == null || !loggedInUser.isAdmin()) {
			log.warning("User " + loggedInUser + " is not admin");
			return null;
		}
		
		// only available for ACTIVE listings
		if (dbListing.state == Listing.State.ACTIVE) {
			ListingVO forUpdate = DtoToVoConverter.convert(dbListing);

			forUpdate.setState(Listing.State.NEW.toString());
			
			Listing updatedListing = getDAO().updateListing(VoToModelConverter.convert(forUpdate));
			
			ListingVO toReturn = DtoToVoConverter.convert(updatedListing);
			applyListingData(loggedInUser, toReturn);
			return toReturn;
		}
		log.warning("Only ACTIVE listings can be send back for update to owner");
		return null;
	}

	/**
	 * Freeze listing so it cannot be bid or modified. It's an administrative action.
	 */
	public ListingVO freezeListing(UserVO loggedInUser, String listingId) {
		Listing dbListing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (loggedInUser == null || dbListing == null || !loggedInUser.isAdmin()) {
			log.warning("User " + loggedInUser + " is not admin");
			return null;
		}
		
		// admins can always freeze listing
		ListingVO forUpdate = DtoToVoConverter.convert(dbListing);

		forUpdate.setState(Listing.State.FROZEN.toString());
		
		Listing updatedListing = getDAO().updateListing(VoToModelConverter.convert(forUpdate));
		
		ListingVO toReturn = DtoToVoConverter.convert(updatedListing);
		applyListingData(loggedInUser, toReturn);
		return toReturn;
	}

	/**
	 * Deletes exising user's NEW listing.
	 */
	public ListingAndUserVO deleteNewListing(UserVO loggedInUser) {
		ListingAndUserVO result = new ListingAndUserVO();
		Listing deletedListing = getDAO().deleteUsersNewListing(UserVO.toKeyId(loggedInUser.getId()));
		if (deletedListing != null) {
			loggedInUser.setEditedListing(null);
			result.setListing(null);
		} else {
			result.setErrorCode(ErrorCodes.DATASTORE_ERROR);
			result.setErrorMessage("Deletion not successful, user probaly doesn't have new listing");
		}
		return result;
	}

	public ListingListVO getClosingActiveListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getClosingListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
	
		return list;
	}

	/**
	 * Returns the most commented listings
	 */
	public ListingListVO getMostDiscussedActiveListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getMostDiscussedListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
		
		return list;
	}

	/**
	 * Returns the most voted listings
	 * @param listingProperties
	 * @return List of listings
	 */
	public ListingListVO getMostPopularActiveListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getMostPopularListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
	
		return list;
	}

	/**
	 * Returns listings sorted by median valuation
	 * @param listingProperties
	 * @return
	 */
	public ListingListVO getMostValuedActiveListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		ListPropertiesVO tmpProperties = new ListPropertiesVO();
		tmpProperties.setMaxResults(Integer.MAX_VALUE);
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getTopListings(tmpProperties));
		
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing);
		}
		Collections.sort(listings, new Comparator<ListingVO> () {
			public int compare(ListingVO left, ListingVO right) {
				if (left.getMedianValuation() == right.getMedianValuation()) {
					return 0;
				} else if (left.getMedianValuation() > right.getMedianValuation()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		
		listingProperties.setTotalResults(listings.size());
		listings = listings.subList(0, listingProperties.getMaxResults() > listings.size() ? listings.size() : listingProperties.getMaxResults());
		listingProperties.setNumberOfResults(listings.size());
		
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			listing.setOrderNumber(index++);
		}
		
		ListingListVO list = new ListingListVO();
		list.setListings(listings);
		list.setListingsProperties(listingProperties);
	
		return list;
	}

	/**
	 * Returns top rated listings
	 * 
	 * @param listingProperties Standard query parameters (maxResults and cursors)
	 * @return List of listings
	 */
	public ListingListVO getTopActiveListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getTopListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);
		list.setListingsProperties(listingProperties);
	
		return list;
	}

	/**
	 * If queried user is logged in then returns all listings created by specified user.
	 * If not only ACTIVE listings are returned.
	 */
	public ListingListVO getUserListings(UserVO loggedInUser, String userId, ListPropertiesVO listingProperties) {
		
		List<ListingVO> listings = null;
		if (loggedInUser != null && StringUtils.areStringsEqual(userId, loggedInUser.getId())) {
			listings = DtoToVoConverter.convertListings(
				getDAO().getUserListings(BaseVO.toKeyId(userId), listingProperties));
		} else {
			listings = DtoToVoConverter.convertListings(
					getDAO().getUserActiveListings(BaseVO.toKeyId(userId), listingProperties));
		}
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		
		ListingListVO list = new ListingListVO();
		list.setListings(listings);
		list.setListingsProperties(listingProperties);
		list.setUser(new UserBasicVO(UserMgmtFacade.instance().getUser(loggedInUser, userId).getUser()));
	
		return list;
	}

	/**
	 * Returns active listings created by logged in user, sorted by listed on date
	 */
	public ListingListVO getLatestActiveListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getActiveListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing);
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
	
		return list;
	}

	public ListingListVO listingKeywordSearch(UserVO loggedInUser, String text,
			ListPropertiesVO listingProperties) {
		ListingListVO listingsList = new ListingListVO();
		List<ListingVO> listings = new ArrayList<ListingVO>();
		List<String> ids = DocService.instance().fullTextSearch(text);
		for (String id : ids) {
			ListingAndUserVO listingUser = getListing(loggedInUser, id);
			if (listingUser != null) {
				ListingVO listing = listingUser.getListing();
				listing.setOrderNumber(listings.size() + 1);
				if (Listing.State.ACTIVE.toString().equalsIgnoreCase(listing.getState())) {
					log.info("Active listing added to keyword search results " + listing);
					listings.add(listing);
				} else if (loggedInUser.getId().equals(listing.getOwner())) {
					log.info("Owned listing added to keyword search results " + listing);
					listings.add(listing);
				}
				listingsList.setUser(listingUser.getLoggedUser());
			}
		}
		listingsList.setListings(listings);
		listingProperties.setNumberOfResults(listings.size());
		listingsList.setListingsProperties(listingProperties);
		return listingsList;
	}

	public List<ListingStats> updateAllListingStatistics() {
		List<ListingStats> list = new ArrayList<ListingStats>();
	
		List<Listing> listings = getDAO().getAllListings();
		for (Listing listing : listings) {
			list.add(calculateListingStatistics(listing.id));
		}
		log.log(Level.INFO, "Updated stats for " + list.size() + " listings: " + list);
		int updatedDocs = DocService.instance().updateListingData(listings);
		log.log(Level.INFO, "Updated docs for " + updatedDocs + " listings.");
		return list;
	}

	/**
	 * Value up listing
	 */
	public ListingVO valueUpListing(UserVO loggedInUser, String listingId) {
		if (loggedInUser == null) {
			return null;
		}
		ListingVO listing =  DtoToVoConverter.convert(getDAO().valueUpListing(
				BaseVO.toKeyId(listingId), BaseVO.toKeyId(loggedInUser.getId())));
		if (listing != null) {
			scheduleUpdateOfListingStatistics(listing.getId(), UpdateReason.NEW_VOTE);
			UserMgmtFacade.instance().scheduleUpdateOfUserStatistics(loggedInUser.getId(), UserMgmtFacade.UpdateReason.NEW_VOTE);
			ServiceFacade.instance().createNotification(listing.getOwner(), listing.getId(), Notification.Type.NEW_VOTE_FOR_YOUR_LISTING, "");
			applyListingData(loggedInUser, listing);
		}
		return listing;
	}

	public void applyListingData(UserVO loggedInUser, ListingVO listing) {
		// set user data
		SBUser user = getDAO().getUser(listing.getOwner());
		listing.setOwnerName(user != null ? user.nickname : "<<unknown>>");
		
		ListingStats listingStats = getListingStatistics(listing.toKeyId());
		listing.setNumberOfBids(listingStats.numberOfBids);
		listing.setNumberOfComments(listingStats.numberOfComments);
		listing.setNumberOfVotes(listingStats.numberOfVotes);
		listing.setValuation((int)listingStats.valuation);
		listing.setMedianValuation((int)listingStats.medianValuation);
		listing.setPreviousValuation((int)listingStats.previousValuation);
		listing.setScore((int)listingStats.score);
		
		// calculate daysAgo and daysLeft
		Days daysAgo = Days.daysBetween(new DateTime(listing.getListedOn()), new DateTime());
		listing.setDaysAgo(daysAgo.getDays());
	
		Days daysLeft = Days.daysBetween(new DateTime(), new DateTime(listing.getClosingOn()));
		listing.setDaysLeft(daysLeft.getDays());
		
		if (loggedInUser != null) {
			listing.setVotable(getDAO().userCanVoteForListing(loggedInUser.toKeyId(), listing.toKeyId()));
		} else {
			listing.setVotable(false);
		}
	}

	public ListingStats getListingStatistics(long listingId) {
		ListingStats listingStats = getDAO().getListingStatistics(listingId);
		if (listingStats == null) {
			// calculating user stats here may be disabled here
			listingStats = calculateListingStatistics(listingId);
		}
		log.log(Level.INFO, "Listing stats for '" + listingId + "' : " + listingStats);
		return listingStats;
	}

	public ListingStats calculateListingStatistics(long listingId) {
		ListingStats listingStats = getDAO().updateListingStatistics(listingId);
		log.log(Level.INFO, "Calculated listing stats for '" + listingId + "' : " + listingStats);
		return listingStats;
	}

	public void scheduleUpdateOfListingStatistics(String listingWebKey, UpdateReason reason) {
		log.log(Level.INFO, "Scheduling listing stats update for '" + listingWebKey + "', reason: " + reason);
		ListingStats listingStats = getDAO().getListingStatistics(VoToModelConverter.stringToKey(listingWebKey).getId());
		if (listingStats != null) {
			switch(reason) {
			case NEW_BID:
				listingStats.numberOfBids = listingStats.numberOfBids + 1;
				break;
			case NEW_COMMENT:
				listingStats.numberOfComments = listingStats.numberOfComments + 1;
				break;
			case NEW_VOTE:
				listingStats.numberOfVotes = listingStats.numberOfVotes + 1;
				break;
			default:
				// reason can be also null
				break;
			}
			// updates stats in datastore and memcache
			getDAO().storeListingStatistics(listingStats);
		}
		String taskName = timeStampFormatter.print(new Date().getTime()) + "listing_stats_update_" + reason + "_" + listingWebKey;
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/calculate-listing-stats").param("id", listingWebKey)
				.taskName(taskName).countdownMillis(LISTING_STATS_UPDATE_DELAY));
	}

	public ListingDocumentVO createListingDocument(UserVO loggedInUser, ListingDocumentVO doc) {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production && loggedInUser == null) {
			blobstoreService.delete(doc.getBlob());
			doc.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			doc.setErrorMessage("Only logged in users can upload documents");
			return doc;
		}
		if (loggedInUser != null && loggedInUser.getEditedListing() == null) {
			blobstoreService.delete(doc.getBlob());
			doc.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
			doc.setErrorMessage("User is not editing listing");
			return doc;
		}
		Listing listing = getDAO().getListing(BaseVO.toKeyId(loggedInUser.getEditedListing()));
		BlobInfo logoInfo = new BlobInfoFactory().loadBlobInfo(doc.getBlob());
		byte logo[] = blobstoreService.fetchData(doc.getBlob(), 0, logoInfo.getSize() - 1);
		if (setLogoBase64(listing, logo) == null) {
			blobstoreService.delete(doc.getBlob());
			doc.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			doc.setErrorMessage("Image conversion error.");
			return doc;
		}
		ListingDoc docDTO = VoToModelConverter.convert(doc);
		docDTO.created = new Date();
		docDTO = getDAO().createListingDocument(docDTO);
		doc = DtoToVoConverter.convert(docDTO);
		
		updateListingDoc(loggedInUser, listing, docDTO);
		
		return doc;
	}

	private Listing updateListingDoc(UserVO loggedInUser, Listing listing, ListingDoc docDTO) {		
		log.info("Updating listing document " + docDTO);
		Key<ListingDoc> replacedDocId = null;
		switch(docDTO.type) {
		case BUSINESS_PLAN:
			replacedDocId = listing.businessPlanId;
			listing.businessPlanId = new Key<ListingDoc>(ListingDoc.class, docDTO.id);
			break;
		case FINANCIALS:
			replacedDocId = listing.financialsId;
			listing.financialsId = new Key<ListingDoc>(ListingDoc.class, docDTO.id);
			break;
		case PRESENTATION:
			replacedDocId = listing.presentationId;
			listing.presentationId = new Key<ListingDoc>(ListingDoc.class, docDTO.id);
			break;
		case LOGO:
			replacedDocId = listing.logoId;
			listing.logoId = new Key<ListingDoc>(ListingDoc.class, docDTO.id);
			break;
		}
		listing = getDAO().storeListing(listing);
		
		if (replacedDocId != null) {
			try {
				log.info("Deleting doc previously associated with listing " + replacedDocId);
				ListingDoc docToDelete = getDAO().getListingDocument(replacedDocId.getId());
				BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
				blobstoreService.delete(docToDelete.blob);
				getDAO().deleteDocument(replacedDocId.getId());
			} catch (Exception e) {
				log.log(Level.WARNING, "Error while deleting old document " + replacedDocId + " of listing " + listing.id, e);
			}
		}
		return listing;
	}
	
	private Listing setLogoBase64(Listing listing, byte[] logo) {
		String logoBase64 = convertLogoToBase64(logo);
		if (logoBase64 != null) {
			listing.logoBase64 = convertLogoToBase64(logo);
			return listing;
		} else {
			return null;
		}
	}
	
	public String convertLogoToBase64(byte[] logo) {
		final byte[] JPEG = {(byte)0xff, (byte)0xd8, (byte)0xff, (byte)0xe0};
		final byte[] GIF = {0x47, 0x49, 0x46, 0x38};
		final byte[] PNG = {(byte)0x89, 0x50, 0x4e, 0x47};
		
		byte[] magicNumber = ArrayUtils.subarray(logo, 0, 4);
		String format = "";
		if (ArrayUtils.isEquals(magicNumber, JPEG)) {
			format = "image/jpeg";
		} else if (ArrayUtils.isEquals(magicNumber, GIF)) {
			format = "image/gif";
		} else if (ArrayUtils.isEquals(magicNumber, PNG)) {
			format = "image/png";
		} else {
			log.warning("Image not recognized as JPG, GIF or PNG. Magic number was: " + ToStringBuilder.reflectionToString(magicNumber));
			return null;
		}
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
        Image originalImage = ImagesServiceFactory.makeImage(logo);
		log.info("Original image: " + originalImage.getWidth() + " x " + originalImage.getHeight());
        Image newImage = null;
        if (originalImage.getWidth() != originalImage.getHeight()) {
        	Transform crop = null;
        	if (originalImage.getWidth() > originalImage.getHeight()) {
        		double percentCrop = (originalImage.getWidth() - originalImage.getHeight()) / (2.0 * originalImage.getWidth());
        		crop = ImagesServiceFactory.makeCrop(percentCrop, 0.0, 1.0 - percentCrop, 1.0);
        	} else {
        		double percentCrop = (originalImage.getHeight() - originalImage.getWidth()) / (2.0 * originalImage.getHeight());
        		crop = ImagesServiceFactory.makeCrop(0.0, percentCrop, 1.0, 1.0 - percentCrop);
        	}
    		log.info("Center cropping image ...");
        	newImage = imagesService.applyTransform(crop, originalImage);
    		log.info("Cropped image: " + newImage.getWidth() + " x " + newImage.getHeight());
        } else {
        	newImage = originalImage;
        }
        if (newImage.getWidth() != 144) {
        	Transform resize = ImagesServiceFactory.makeResize(144, 144);
        	newImage = imagesService.applyTransform(resize, newImage);
    		log.info("Resized image: " + newImage.getWidth() + " x " + newImage.getHeight());
        }
        byte[] newImageData = newImage.getImageData();
		
		String logo64 = Base64.encodeBase64String(newImageData);
		log.info("Data uri for logo has " + logo64.length() + " bytes");
		return "data:" + format + ";base64," + logo64;
	}

	public List<ListingDocumentVO> getAllListingDocuments(UserVO loggedInUser) {
		if (loggedInUser == null) {
			return null;
		}
		return DtoToVoConverter.convertListingDocuments(getDAO().getAllListingDocuments());
	}

	public ListingDocumentVO getListingDocument(UserVO loggedInUser, String docId) {
		return DtoToVoConverter.convert(getDAO().getListingDocument(BaseVO.toKeyId(docId)));
	}

	public List<ListingDocumentVO> getGoogleDocDocuments() {
		DocService.instance().createFolders();
		return DocService.instance().getAllDocuments();
	}

	public Map<String, String> getCategories() {
		List<Category> categories = getDAO().getCategories();
		
		Map<String, String> result = new LinkedHashMap<String, String>();
		for (Category cat : categories) {
			result.put(cat.name, cat.name);
		}
		return result;
	}
}
