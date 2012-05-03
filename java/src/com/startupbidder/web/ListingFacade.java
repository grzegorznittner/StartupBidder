package com.startupbidder.web;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.math.RandomUtils;
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
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.utils.SystemProperty;
import com.googlecode.objectify.Key;
import com.startupbidder.dao.MockDataBuilder;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Category;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.ListingDoc.Type;
import com.startupbidder.datamodel.ListingLocation;
import com.startupbidder.datamodel.ListingStats;
import com.startupbidder.datamodel.Location;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.vo.BaseVO;
import com.startupbidder.vo.DiscoverListingsVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingAndUserVO;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.ListingListVO;
import com.startupbidder.vo.ListingLocationsVO;
import com.startupbidder.vo.ListingPropertyVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.NotificationListVO;
import com.startupbidder.vo.NotificationVO;
import com.startupbidder.vo.UserBasicVO;
import com.startupbidder.vo.UserListingsVO;
import com.startupbidder.vo.UserVO;

public class ListingFacade {
	private static final Logger log = Logger.getLogger(ListingFacade.class.getName());
	
	public static enum UpdateReason {NEW_BID, BID_UPDATE, NEW_COMMENT, DELETE_COMMENT, NEW_MONITOR, DELETE_MONITOR, NONE};
	public static final String MEMCACHE_ALL_LISTING_LOCATIONS = "AllListingLocations";
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
			l.contactEmail = loggedInUser.getEmail();
			l.founders = !StringUtils.isEmpty(loggedInUser.getName()) ? loggedInUser.getName() : loggedInUser.getNickname();
			l.askedForFunding = true;
			l.suggestedAmount = 20000;
			l.suggestedPercentage = 5;
			l.created = new Date();
			ListingVO newListing = DtoToVoConverter.convert(getDAO().createListing(l));
			loggedInUser.setEditedListing(newListing.getId());
			
			// at that stage listing is not yet active so there is no point of updating statistics
			Monitor monitor = getDAO().getListingMonitor(loggedInUser.toKeyId(), newListing.toKeyId());
			applyListingData(loggedInUser, newListing, monitor);
			result.setListing(newListing);
			result.setCategories(getCategories());
		}
		return result;
	}

	/**
	 * Returns edited/posted listing.
	 */
	public ListingVO editedListing(UserVO loggedInUser) {
		if (loggedInUser == null) {
			log.log(Level.WARNING, "Only logged in user can have edited listing", new Exception("Not logged in"));
			return null;
		} else {
			Listing listing = getDAO().getListing(ListingVO.toKeyId(loggedInUser.getEditedListing()));
			if (listing.state != Listing.State.NEW && listing.state != Listing.State.POSTED) {
				loggedInUser.setEditedListing(null);
				loggedInUser.setEditedStatus(null);
				return null;
			}
			ListingVO listingVO = DtoToVoConverter.convert(listing);
			return listingVO;
		}
	}
	
	public ListingAndUserVO getListing(UserVO loggedInUser, String listingId) {
		ListingAndUserVO listingAndUser = new ListingAndUserVO();
		long id = 0;
		try {
			id = BaseVO.toKeyId(listingId);
			ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(id));
			
			if (listing != null) {
				Monitor monitor = loggedInUser != null ? getDAO().getListingMonitor(loggedInUser.toKeyId(), listing.toKeyId()) : null;
				applyListingData(loggedInUser, listing, monitor);
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
		infos.append("Update listing's properties logs. ");
		// removing non updatable fields and handling fetched fields
		List<ListingPropertyVO> propsToUpdate = new ArrayList<ListingPropertyVO>();
		for (ListingPropertyVO prop : properties) {
			String propertyName = prop.getPropertyName().toLowerCase();
			if (ListingVO.UPDATABLE_PROPERTIES.contains(propertyName)) {
				propsToUpdate.add(prop);
			} else if (ListingVO.FETCHED_PROPERTIES.contains(propertyName)) {
				ListingDoc doc = fetchAndUpdateListingDoc(listing, prop);
				if (doc != null) {
					Listing updatedlisting = updateListingDoc(listing, doc);
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
		log.log(Level.INFO, infos.toString());
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

	public ListingAndUserVO updateListingAddressProperties(UserVO loggedInUser, Map<String, String> properties) {
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
		StringBuffer infos = new StringBuffer();
		// removing non updatable fields and handling fetched fields
		List<ListingPropertyVO> propsToUpdate = new ArrayList<ListingPropertyVO>();
		createMandatoryProperty(propsToUpdate, infos, properties, "latitude", "latitude");
		createMandatoryProperty(propsToUpdate, infos, properties, "longitude", "longitude");
		if ("US".equals(properties.get("SHORT_country"))) {
			propsToUpdate.add(new ListingPropertyVO("country", "USA"));
			ListingPropertyVO state = createProperty(properties, "SHORT_administrative_area_level_1", "state");
			if (state != null) {
				propsToUpdate.add(state);
			}
		} else {
			createMandatoryProperty(propsToUpdate, infos, properties, "LONG_country", "country");
		}
		createMandatoryProperty(propsToUpdate, infos, properties, "LONG_locality", "city");
		createMandatoryProperty(propsToUpdate, infos, properties, "formatted_address", "address");
		
		if (infos.length() > 0 ) {
			log.warning("Missing mandatory address field(s): " + infos.toString());
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage("Missing mandatory address field(s).");
			return result;
		}
		
		if (!propsToUpdate.isEmpty()) {
			for (ListingPropertyVO prop : propsToUpdate) {
				VoToModelConverter.updateListingProperty(listing, prop);
			}
			// creates brief addres and makes city/state/country lowercase
			DtoToVoConverter.updateBriefAddress(listing);
			log.info("Updating listing: " + listing);
			getDAO().storeListing(listing);
		}
		result.setListing(DtoToVoConverter.convert(listing));
		
		return result;
	}
	
	private ListingPropertyVO createMandatoryProperty(List<ListingPropertyVO> propsToUpdate, StringBuffer infos, 
			Map<String, String> properties, String name, String propertyName) {
		String value = properties.get(name);
		if (value != null) {
			ListingPropertyVO prop = new ListingPropertyVO(propertyName, value);
			propsToUpdate.add(prop);
			return prop;
		} else {
			infos.append("'" + name + "' ");
			return null;
		}
	}
	
	private ListingPropertyVO createProperty(Map<String, String> properties, String name, String propertyName) {
		String value = properties.get(name);
		if (value != null) {
			return new ListingPropertyVO(propertyName, value);
		}
		return null;
	}

	private ListingDoc fetchAndUpdateListingDoc(Listing listing, ListingPropertyVO prop) {
		byte[] docBytes = null;
		String mimeType = null;
		try {
			boolean isDevEnvironment = com.google.appengine.api.utils.SystemProperty.environment.value() == com.google.appengine.api.utils.SystemProperty.Environment.Value.Development;
			String url = prop.getPropertyValue();
			if(!url.startsWith("http") && isDevEnvironment && new File("./test-docs").exists()) {
				docBytes = FileUtils.readFileToByteArray(new File(prop.getPropertyValue()));
				if (prop.getPropertyValue().endsWith(".gif")) {
					mimeType = "image/gif";
				} else if (prop.getPropertyValue().endsWith(".jpg")) {
					mimeType = "image/jpeg";
				} else if (prop.getPropertyValue().endsWith(".png")) {
					mimeType = "image/png";
				} else if (prop.getPropertyValue().endsWith(".doc")) {
					mimeType = "application/msword";
				} else if (prop.getPropertyValue().endsWith(".ppt")) {
					mimeType = "application/vnd.ms-powerpoint";
				} else if (prop.getPropertyValue().endsWith(".xls")) {
					mimeType = "application/vnd.ms-excel";
				} else if (prop.getPropertyValue().endsWith(".pdf")) {
					mimeType = "application/pdf";
				}
			} else {
				URLConnection con = new URL(url).openConnection();
				docBytes = IOUtils.toByteArray(con.getInputStream());
				mimeType = con.getContentType();
			}
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
		boolean adminOrOwner = StringUtils.equals(loggedInUser.getId(), dbListing.owner.getString())
				|| loggedInUser.isAdmin();
		if (!adminOrOwner) {
			log.warning("User must be an owner of the listing or an admin");
			return null;
		}
		// only NEW or ACTIVE listings can be updated
		if (dbListing.state == Listing.State.NEW || dbListing.state == Listing.State.ACTIVE) {
			ListingVO forUpdate = DtoToVoConverter.convert(dbListing);
			
			Listing updatedListing = getDAO().updateListingStateAndDates(VoToModelConverter.convert(forUpdate));
			if (updatedListing != null && updatedListing.state == Listing.State.ACTIVE) {
				scheduleUpdateOfListingStatistics(updatedListing.getWebKey(), UpdateReason.NONE);
			}
			ListingVO updatedListingVO = DtoToVoConverter.convert(updatedListing);
			Monitor monitor = getDAO().getListingMonitor(loggedInUser.toKeyId(), updatedListingVO.toKeyId());
			applyListingData(loggedInUser, updatedListingVO, monitor);
			return updatedListingVO;
		}
		return null;
	}

	/**
	 * Marks listing as prepared for posting on statupbidder.
	 * Only owner of the listing can do that
	 */
	public ListingAndUserVO activateListing(UserVO loggedInUser, String listingId) {
		ListingAndUserVO returnValue = new ListingAndUserVO();
		if (loggedInUser == null) {
			log.log(Level.WARNING, "User is not logged in!", new Exception("Not logged in user"));
			returnValue.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			returnValue.setErrorMessage("User is not logged in!");
			return returnValue;
		}
		// Only admins can do activation
		if (!loggedInUser.isAdmin()) {
			log.log(Level.WARNING, "User " + loggedInUser + " is not an admin. Only admin can activate listings.", new Exception("Not an admin"));
			returnValue.setErrorCode(ErrorCodes.NOT_AN_ADMIN);
			returnValue.setErrorMessage("User " + loggedInUser + " is not an admin. Only admin can activate listings.");
			return returnValue;
		}

		Listing dbListing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (dbListing.state != Listing.State.POSTED && dbListing.state != Listing.State.FROZEN) {
			log.log(Level.WARNING, "Only posted and frozen listings can be activated. This listing is " + dbListing.state, new Exception("Not valid state"));
			returnValue.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			returnValue.setErrorMessage("Only posted and frozen listings can be activated. This listing is " + dbListing.state);
			return returnValue;
		}
		// activation also could be done for FROZEN listings
		if (dbListing.state == Listing.State.POSTED) {
			dbListing.listedOn = new Date();
			// only listings which ask for funding have closing date set
			if (dbListing.askedForFunding) {
				DateMidnight midnight = new DateMidnight().plusDays(1);
				dbListing.closingOn = midnight.plusDays(LISTING_DEFAULT_CLOSING_ON_DAYS).toDate();
			}
		}
		log.info("Activating listing: " + dbListing);
		dbListing.state = Listing.State.ACTIVE;
		
		ListingVO forUpdate = DtoToVoConverter.convert(dbListing);
		Listing updatedListing = getDAO().updateListingStateAndDates(VoToModelConverter.convert(forUpdate));
		if (updatedListing != null) {
			loggedInUser.setEditedListing(null);
			loggedInUser.setEditedStatus(null);
			scheduleUpdateOfListingStatistics(updatedListing.getWebKey(), UpdateReason.NONE);
			ServiceFacade.instance().createListingActivatedNotification(updatedListing, Notification.Type.NEW_LISTING);
		}
		ListingVO toReturn = DtoToVoConverter.convert(updatedListing);
		Monitor monitor = getDAO().getListingMonitor(loggedInUser.toKeyId(), toReturn.toKeyId());
		applyListingData(loggedInUser, toReturn, monitor);
		returnValue.setListing(toReturn);
		return returnValue;
	}

	/**
	 * Action can be done only by listing owner.
	 * Makes listing ready to be published on StartupBidder website, but t
	 */
	public ListingAndUserVO postListing(UserVO loggedInUser, String listingId) {
		ListingAndUserVO returnValue = new ListingAndUserVO();
		
		Listing dbListing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (loggedInUser == null || dbListing == null) {
			log.log(Level.WARNING, "User " + loggedInUser + " is logged in or listing doesn't exist", new Exception("Not logged in"));
			returnValue.setErrorMessage("User not logged in");
			returnValue.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			return returnValue;
		}
		if (!StringUtils.equals(loggedInUser.getId(), dbListing.owner.getString())) {
			log.log(Level.WARNING, "User '" + loggedInUser + "' is not an owner of listing " + dbListing, new Exception("Not listing owner"));
			returnValue.setErrorMessage("User is not an owner of the listing");
			returnValue.setErrorCode(ErrorCodes.NOT_AN_OWNER);
			return returnValue;
		}
				
		// only NEW listings can be posted
		if (dbListing.state == Listing.State.NEW) {
//			String logs = verifyListingsMandatoryFields(dbListing);
//			if (!StringUtils.isEmpty(logs)) {
//				log.log(Level.WARNING, "Listing validation error. " + logs, new Exception("Listing verification error"));
//				returnValue.setErrorMessage("Listing cannot be posted. " + logs);
//				returnValue.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
//				return returnValue;
//			}
			
			ListingVO forUpdate = DtoToVoConverter.convert(dbListing);

			forUpdate.setPostedOn(new Date());
			forUpdate.setState(Listing.State.POSTED.toString());
			
			Listing updatedListing = getDAO().updateListingStateAndDates(VoToModelConverter.convert(forUpdate));
			if (updatedListing != null) {
				if (StringUtils.equals(updatedListing.owner.getString(), loggedInUser.getId())) {
					loggedInUser.setEditedListing(null);
					loggedInUser.setEditedStatus(null);
				}
				scheduleUpdateOfListingStatistics(updatedListing.getWebKey(), UpdateReason.NONE);
			}
			ListingVO toReturn = DtoToVoConverter.convert(updatedListing);
			Monitor monitor = getDAO().getListingMonitor(loggedInUser.toKeyId(), toReturn.toKeyId());
			applyListingData(loggedInUser, toReturn, monitor);
			returnValue.setListing(toReturn);
			return returnValue;
		}
		log.log(Level.WARNING, "Only NEW listing can be marked as POSTED (state is " + dbListing.state + ")", new Exception("Not valid state"));
		returnValue.setErrorMessage("Listing is not in NEW state.");
		returnValue.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
		return returnValue;
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

//		checkMandatoryStringField(logs, "Answer1", listing.answer1, 16, 512);
//		checkMandatoryStringField(logs, "Answer2", listing.answer2, 16, 512);
//		checkMandatoryStringField(logs, "Answer3", listing.answer3, 16, 512);
//		checkMandatoryStringField(logs, "Answer4", listing.answer4, 16, 512);
//		checkMandatoryStringField(logs, "Answer5", listing.answer5, 16, 512);
//		checkMandatoryStringField(logs, "Answer6", listing.answer6, 16, 512);
//		checkMandatoryStringField(logs, "Answer7", listing.answer7, 16, 512);
//		checkMandatoryStringField(logs, "Answer8", listing.answer8, 16, 512);
//		checkMandatoryStringField(logs, "Answer9", listing.answer9, 16, 512);
//		checkMandatoryStringField(logs, "Answer10", listing.answer10, 16, 512);
//		checkMandatoryStringField(logs, "Answer11", listing.answer11, 16, 512);
//		checkMandatoryStringField(logs, "Answer12", listing.answer12, 16, 512);
//		checkMandatoryStringField(logs, "Answer13", listing.answer13, 16, 512);
//		checkMandatoryStringField(logs, "Answer14", listing.answer14, 16, 512);
//		checkMandatoryStringField(logs, "Answer15", listing.answer15, 16, 512);
//		checkMandatoryStringField(logs, "Answer16", listing.answer16, 16, 512);
//		checkMandatoryStringField(logs, "Answer17", listing.answer17, 16, 512);
//		checkMandatoryStringField(logs, "Answer18", listing.answer18, 16, 512);
//		checkMandatoryStringField(logs, "Answer19", listing.answer19, 16, 512);
//		checkMandatoryStringField(logs, "Answer20", listing.answer20, 16, 512);
//		checkMandatoryStringField(logs, "Answer21", listing.answer21, 16, 512);
//		checkMandatoryStringField(logs, "Answer22", listing.answer22, 16, 512);
//		checkMandatoryStringField(logs, "Answer23", listing.answer23, 16, 512);
//		checkMandatoryStringField(logs, "Answer24", listing.answer24, 16, 512);
//		checkMandatoryStringField(logs, "Answer25", listing.answer25, 16, 512);
//		checkMandatoryStringField(logs, "Answer26", listing.answer26, 16, 512);

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
	public ListingAndUserVO withdrawListing(UserVO loggedInUser, String listingId) {
		ListingAndUserVO returnValue = new ListingAndUserVO();
		
		if (loggedInUser == null) {
			log.log(Level.WARNING, "User not logged in");
			returnValue.setErrorMessage("User not logged in");
			returnValue.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			return returnValue;
		}
		Listing dbListing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (dbListing == null) {
			log.log(Level.WARNING, "Listing doesn't exist", new Exception("Listing doesn't exist"));
			returnValue.setErrorMessage("Listing doesn't exist");
			returnValue.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			return returnValue;
		}
		if (!StringUtils.equals(loggedInUser.getId(), dbListing.owner.getString())) {
			log.log(Level.WARNING, "User must be an owner of the listing", new Exception("Not an owner"));
			returnValue.setErrorMessage("User must be an owner of the listing");
			returnValue.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			return returnValue;
		}
		
		// only ACTIVE and POSTED listings can be WITHDRAWN
		if (dbListing.state != Listing.State.CLOSED && dbListing.state != Listing.State.WITHDRAWN) {
			ListingVO forUpdate = DtoToVoConverter.convert(dbListing);

			forUpdate.setState(Listing.State.WITHDRAWN.toString());
			
			Listing updatedListing = getDAO().updateListingStateAndDates(VoToModelConverter.convert(forUpdate));
			if (updatedListing != null) {
				scheduleUpdateOfListingStatistics(updatedListing.getWebKey(), UpdateReason.NONE);
			} else {
				returnValue.setErrorMessage("Listing not updated");
				returnValue.setErrorCode(ErrorCodes.DATASTORE_ERROR);
			}
			ListingVO toReturn = DtoToVoConverter.convert(updatedListing);
			Monitor monitor = getDAO().getListingMonitor(loggedInUser.toKeyId(), toReturn.toKeyId());
			applyListingData(loggedInUser, toReturn, monitor);
			returnValue.setListing(toReturn);
			return returnValue;
		}
		log.log(Level.WARNING, "CLOSED or WITHDRAWN listings cannot be withdrawn (state was " + dbListing.state + ")", new Exception("Not valid state"));
		returnValue.setErrorMessage("CLOSED or WITHDRAWN listings cannot be withdrawn (state was " + dbListing.state + ")");
		returnValue.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
		return returnValue;
	}

	/**
	 * Sends back listing for update to the owner as it is not ready for posting on startupbidder site
	 */
	public ListingAndUserVO sendBackListingToOwner(UserVO loggedInUser, String listingId) {
		ListingAndUserVO returnValue = new ListingAndUserVO();
		
		Listing dbListing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (loggedInUser == null || dbListing == null || !loggedInUser.isAdmin()) {
			log.warning("User " + loggedInUser + " is not admin");
			returnValue.setErrorMessage("User " + loggedInUser + " is not admin");
			returnValue.setErrorCode(ErrorCodes.NOT_AN_ADMIN);
			return returnValue;
		}
		
		if (dbListing.state == Listing.State.POSTED || dbListing.state == Listing.State.FROZEN) {
			List<Listing> newOrPosted = getDAO().getUserNewOrPostedListings(dbListing.owner.getId());
			if (newOrPosted != null && newOrPosted.size() > 0) {
				log.warning("Listing owner '" + loggedInUser + "' has already have NEW/POSTED listing");
				returnValue.setErrorMessage("Listing owner '" + loggedInUser + "' has already have NEW/POSTED listing");
				returnValue.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
				return returnValue;
			}
			
			ListingVO forUpdate = DtoToVoConverter.convert(dbListing);
			forUpdate.setState(Listing.State.NEW.toString());
			
			Listing updatedListing = getDAO().updateListingStateAndDates(VoToModelConverter.convert(forUpdate));
			if (updatedListing == null) {
				returnValue.setErrorMessage("Listing not updated");
				returnValue.setErrorCode(ErrorCodes.DATASTORE_ERROR);
			}
			ListingVO toReturn = DtoToVoConverter.convert(updatedListing);
			Monitor monitor = getDAO().getListingMonitor(loggedInUser.toKeyId(), toReturn.toKeyId());
			applyListingData(loggedInUser, toReturn, monitor);
			returnValue.setListing(toReturn);
			return returnValue;
		}
		returnValue.setErrorMessage("Only active listings can be send back for update to owner");
		returnValue.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
		return returnValue;
	}

	/**
	 * Freeze listing so it cannot be bid or modified. It's an administrative action.
	 */
	public ListingAndUserVO freezeListing(UserVO loggedInUser, String listingId) {
		ListingAndUserVO returnValue = new ListingAndUserVO();
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			log.warning("User not logged in or '" + loggedInUser + "' is not an admin");
			returnValue.setErrorMessage("Only admins can freeze listings.");
			returnValue.setErrorCode(ErrorCodes.NOT_AN_ADMIN);
			return returnValue;
		}
		
		Listing dbListing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (dbListing == null || dbListing.state == Listing.State.NEW || dbListing.state == Listing.State.POSTED) {
			log.warning("Listing not exists or is not yet activated. Listing: " + dbListing);
			returnValue.setErrorMessage("Listing not exists or is not yet activated");
			returnValue.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			return returnValue;
		}
		
		if (dbListing.state != Listing.State.NEW && dbListing.state != Listing.State.POSTED) {
			// admins can always freeze listing
			ListingVO forUpdate = DtoToVoConverter.convert(dbListing);
	
			forUpdate.setState(Listing.State.FROZEN.toString());
			
			Listing updatedListing = getDAO().updateListingStateAndDates(VoToModelConverter.convert(forUpdate));
			if (updatedListing == null) {
				returnValue.setErrorMessage("Listing not updated");
				returnValue.setErrorCode(ErrorCodes.DATASTORE_ERROR);
			}
			
			ListingVO toReturn = DtoToVoConverter.convert(updatedListing);
			Monitor monitor = getDAO().getListingMonitor(loggedInUser.toKeyId(), toReturn.toKeyId());
			applyListingData(loggedInUser, toReturn, monitor);
			returnValue.setListing(toReturn);
			return returnValue;
		}
		returnValue.setErrorMessage("NEW or POSTED listings cannot be send back for update to owner");
		returnValue.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
		return returnValue;
	}

	/**
	 * Deletes exising user's NEW listing.
	 */
	public ListingAndUserVO deleteEditedListing(UserVO loggedInUser) {
		ListingAndUserVO result = new ListingAndUserVO();
		if (StringUtils.isEmpty(loggedInUser.getEditedListing())) {
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage("Deletion not successful, user doesn't have edited listing");
			return result;
		}
		Listing deletedListing = getDAO().deleteEditedListing(ListingVO.toKeyId(loggedInUser.getEditedListing()));
		if (deletedListing != null) {
			loggedInUser.setEditedListing(null);
			loggedInUser.setEditedStatus(null);
			result.setListing(null);
		} else {
			result.setErrorCode(ErrorCodes.DATASTORE_ERROR);
			result.setErrorMessage("Deletion not successful, user probaly doesn't have new listing or it's already active.");
		}
		return result;
	}
	
	public DiscoverListingsVO getDiscoverListingList(UserVO loggedInUser) {
		DiscoverListingsVO result = new DiscoverListingsVO();
		Map<String, Monitor> monitors = loggedInUser != null ? getDAO().getMonitorsMapForUser(loggedInUser.toKeyId()) : new HashMap<String, Monitor>();
		
		ListPropertiesVO props = new ListPropertiesVO();
		props.setMaxResults(4);
		List<ListingVO> list = prepareListingList(loggedInUser, getDAO().getTopListings(props), monitors, 4);
		result.setTopListings(list);

		props = new ListPropertiesVO();
		props.setMaxResults(4);
		list = prepareListingList(loggedInUser, getDAO().getClosingListings(props), monitors, 4);
		result.setClosingListings(list);

		props = new ListPropertiesVO();
		props.setMaxResults(4);
		list = prepareListingList(loggedInUser, getDAO().getLatestListings(props), monitors, 4);
		result.setLatestListings(list);
		
		if (loggedInUser != null) {
			props = new ListPropertiesVO();
			props.setMaxResults(4);
			list = prepareListingList(loggedInUser, getDAO().getUserActiveListings(loggedInUser.toKeyId(), props), monitors, 4);
			result.setUsersListings(list);
			
			if (loggedInUser.getEditedListing() != null) {
				Listing editedListing = getDAO().getListing(BaseVO.toKeyId(loggedInUser.getEditedListing()));
				result.setEditedListing(DtoToVoConverter.convert(editedListing));
			}
		}
		
		result.setCategories(getTopCategories());
		result.setTopLocations(getTopLocations());

		return result;
	}

	public UserListingsVO getDiscoverUserListings(UserVO loggedInUser) {
		UserListingsVO result = new UserListingsVO();

		if (loggedInUser == null) {
			log.warning("User not logged in.");
			// on John's request we return 200 when user is not logged in
			return result;
		}
		Map<String, Monitor> monitors = getDAO().getMonitorsMapForUser(loggedInUser.toKeyId());
		
		ListPropertiesVO props = new ListPropertiesVO();
		props.setMaxResults(100);
		List<ListingVO> list = prepareListingList(loggedInUser, getDAO().getUserListings(loggedInUser.toKeyId(), props), monitors, 100);
		log.info("Fetched listings owned by user '" + loggedInUser.toKeyId() + "': " + list);
		
		List<ListingVO> activeListings = new ArrayList<ListingVO>();
		List<ListingVO> withdrawnListings = new ArrayList<ListingVO>();
		List<ListingVO> frozenListings = new ArrayList<ListingVO>();
		List<ListingVO> closedListings = new ArrayList<ListingVO>();
		for (ListingVO listing : list) {
			Listing.State state = Listing.State.valueOf(listing.getState());
			switch(state) {
			case ACTIVE:
				activeListings.add(listing);
				break;
			case WITHDRAWN:
				withdrawnListings.add(listing);
				break;
			case FROZEN:
				frozenListings.add(listing);
				break;
			case CLOSED:
				closedListings.add(listing);
				break;
			}
		}
		if (activeListings.size() > 0) {
			result.setActiveListings(activeListings);
		}
		if (withdrawnListings.size() > 0) {
			result.setWithdrawnListings(withdrawnListings);
		}
		if (frozenListings.size() > 0) {
			result.setFrozenListings(frozenListings);
		}
		if (closedListings.size() > 0) {
			result.setClosedListings(closedListings);
		}

		if (loggedInUser.getEditedListing() != null) {
			Listing editedListing = getDAO().getListing(BaseVO.toKeyId(loggedInUser.getEditedListing()));
			result.setEditedListing(DtoToVoConverter.convert(editedListing));
		}
		
		List<Listing> monitoredListing = getMonitoredListings(loggedInUser, 4);
		list = prepareListingList(loggedInUser, monitoredListing, monitors, 4);
		result.setCommentedListings(list);
		
		props = new ListPropertiesVO();
		props.setMaxResults(10);
		List<NotificationVO> notifications = DtoToVoConverter.convertNotifications(
				getDAO().getAllUserNotification(loggedInUser.toKeyId(), props));
		result.setNotifications(notifications);
		
		if (loggedInUser.isAdmin()) {
			props = new ListPropertiesVO();
			props.setMaxResults(4);
			List<Listing> adminFrozenListing = getDAO().getFrozenListings(props);
			list = prepareListingList(loggedInUser, adminFrozenListing, monitors, 4);
			result.setAdminFrozenListings(list);

			props = new ListPropertiesVO();
			props.setMaxResults(4);
			List<Listing> adminPostedListing = getDAO().getPostedListings(props);
			list = prepareListingList(loggedInUser, adminPostedListing, monitors, 4);
			result.setAdminPostedListings(list);
		}
		
		result.setCategories(getTopCategories());
		result.setTopLocations(getTopLocations());

		return result;
	}

	private List<ListingVO> prepareListingList(UserVO loggedInUser, List<Listing> listings,
			Map<String, Monitor> monitors, int maxResults) {
		ListingVO listingVO;
		int index = 1;
		List<ListingVO> list = new ArrayList<ListingVO>();
		for (Listing listing : listings) {
			listingVO = DtoToVoConverter.convert(listing);
			applyListingData(loggedInUser, listingVO, monitors.get(listingVO.getId()));
			listingVO.setOrderNumber(index++);
			list.add(listingVO);
			if (index > maxResults) {
				break;
			}
		}
		return list;
	}
	
	private List<Listing> getMonitoredListings(UserVO loggedInUser, int maxResults) {
		List<Key<Listing>> monitoredListingKeys = new ArrayList<Key<Listing>>();
		List<Monitor> monitors = getDAO().getMonitorsForUser(loggedInUser.toKeyId());
		log.info("Fetched monitors for user '" + loggedInUser.toKeyId() + "': " + monitors);
		for (Monitor monitor : monitors) {
			monitoredListingKeys.add(new Key<Listing>(Listing.class, monitor.monitoredListing.getId()));
			if (monitoredListingKeys.size() > maxResults) {
				break;
			}
		}
		return getDAO().getListingsByKeys(monitoredListingKeys);
	}

	public ListingListVO getMonitoredListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(
				getMonitoredListings(loggedInUser, listingProperties.getMaxResults()));
		Map<String, Monitor> monitors = loggedInUser != null ? getDAO().getMonitorsMapForUser(loggedInUser.toKeyId()) : new HashMap<String, Monitor>();
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing, monitors.get(listing.getId()));
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);
		list.setListingsProperties(listingProperties);
		list.setCategories(getTopCategories());
		list.setTopLocations(getTopLocations());
	
		return list;
	}

	public ListingListVO getClosingActiveListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getClosingListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		Map<String, Monitor> monitors = loggedInUser != null ? getDAO().getMonitorsMapForUser(loggedInUser.toKeyId()) : new HashMap<String, Monitor>();
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing, monitors.get(listing.getId()));
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);
		list.setListingsProperties(listingProperties);
		list.setCategories(getTopCategories());
		list.setTopLocations(getTopLocations());
	
		return list;
	}

	/**
	 * Returns the most commented listings
	 */
	public ListingListVO getMostDiscussedActiveListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getMostDiscussedListings(listingProperties));
		Map<String, Monitor> monitors = loggedInUser != null ? getDAO().getMonitorsMapForUser(loggedInUser.toKeyId()) : new HashMap<String, Monitor>();
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing, monitors.get(listing.getId()));
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
		list.setCategories(getTopCategories());
		list.setTopLocations(getTopLocations());
		
		return list;
	}

	/**
	 * Returns the most voted listings
	 * @param listingProperties
	 * @return List of listings
	 */
	public ListingListVO getMostPopularActiveListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getMostPopularListings(listingProperties));
		Map<String, Monitor> monitors = loggedInUser != null ? getDAO().getMonitorsMapForUser(loggedInUser.toKeyId()) : new HashMap<String, Monitor>();
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing, monitors.get(listing.getId()));
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
		list.setCategories(getTopCategories());
		list.setTopLocations(getTopLocations());
	
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
		Map<String, Monitor> monitors = loggedInUser != null ? getDAO().getMonitorsMapForUser(loggedInUser.toKeyId()) : new HashMap<String, Monitor>();
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing, monitors.get(listing.getId()));
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
		list.setCategories(getTopCategories());
		list.setTopLocations(getTopLocations());
	
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
		Map<String, Monitor> monitors = loggedInUser != null ? getDAO().getMonitorsMapForUser(loggedInUser.toKeyId()) : new HashMap<String, Monitor>();
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing, monitors.get(listing.getId()));
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);
		list.setListingsProperties(listingProperties);
		list.setCategories(getTopCategories());
		list.setTopLocations(getTopLocations());
	
		return list;
	}

	/**
	 * If queried user is logged in then returns all listings created by specified user.
	 * If not only ACTIVE listings are returned.
	 */
	public ListingListVO getUserListings(UserVO loggedInUser, String userId, ListPropertiesVO listingProperties) {
		
		List<ListingVO> listings = null;
		if (loggedInUser != null && StringUtils.equals(userId, loggedInUser.getId())) {
			listings = DtoToVoConverter.convertListings(
				getDAO().getUserListings(BaseVO.toKeyId(userId), listingProperties));
		} else {
			listings = DtoToVoConverter.convertListings(
					getDAO().getUserActiveListings(BaseVO.toKeyId(userId), listingProperties));
		}
		Map<String, Monitor> monitors = loggedInUser != null ? getDAO().getMonitorsMapForUser(loggedInUser.toKeyId()) : new HashMap<String, Monitor>();
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing, monitors.get(listing.getId()));
			listing.setOrderNumber(index++);
		}
		
		ListingListVO list = new ListingListVO();
		list.setListings(listings);
		list.setListingsProperties(listingProperties);
		list.setCategories(getTopCategories());
		list.setTopLocations(getTopLocations());
		list.setUser(new UserBasicVO(UserMgmtFacade.instance().getUser(loggedInUser, userId).getUser()));
	
		return list;
	}
	
	public ListingListVO getPostedListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		ListingListVO list = new ListingListVO();
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			list.setErrorCode(ErrorCodes.NOT_AN_ADMIN);
			list.setErrorMessage("Only admins can see posted listings");
			return list;
		}
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getPostedListings(listingProperties));
		Map<String, Monitor> monitors = loggedInUser != null ? getDAO().getMonitorsMapForUser(loggedInUser.toKeyId()) : new HashMap<String, Monitor>();
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing, monitors.get(listing.getId()));
			listing.setOrderNumber(index++);
		}
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
		list.setCategories(getTopCategories());
		list.setTopLocations(getTopLocations());
	
		return list;
	}

	/**
	 * Returns active listings, sorted by listed on date
	 */
	public ListingListVO getLatestActiveListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getActiveListings(listingProperties));
		Map<String, Monitor> monitors = loggedInUser != null ? getDAO().getMonitorsMapForUser(loggedInUser.toKeyId()) : new HashMap<String, Monitor>();
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing, monitors.get(listing.getId()));
			listing.setOrderNumber(index++);
		}
		ListingListVO list = new ListingListVO();
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
		list.setCategories(getTopCategories());
		list.setTopLocations(getTopLocations());
	
		return list;
	}

	/**
	 * Returns frozen listings
	 */
	public ListingListVO getFrozenListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		ListingListVO list = new ListingListVO();
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			list.setErrorCode(ErrorCodes.NOT_AN_ADMIN);
			list.setErrorMessage("Only admins can see posted listings");
			return list;
		}
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getFrozenListings(listingProperties));
		Map<String, Monitor> monitors = loggedInUser != null ? getDAO().getMonitorsMapForUser(loggedInUser.toKeyId()) : new HashMap<String, Monitor>();
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			applyListingData(loggedInUser, listing, monitors.get(listing.getId()));
			listing.setOrderNumber(index++);
		}
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
		list.setCategories(getTopCategories());
		list.setTopLocations(getTopLocations());
	
		return list;
	}

	public ListingListVO listingKeywordSearch(UserVO loggedInUser, String text, ListPropertiesVO listingProperties) {
		ListingListVO listingsList = new ListingListVO();
		List<ListingVO> listings = new ArrayList<ListingVO>();
		String[] keywords = splitSearchKeywords(text);
		
		List<Long> results = null;
		if (!StringUtils.isEmpty(keywords[0])) {
			results = DocService.instance().fullTextSearch(keywords[0], listingProperties);
		}
		if (!StringUtils.isEmpty(keywords[1])) {
			List<Long> categoryResults = getDAO().getListingsIdsForCategory(keywords[1], listingProperties);
			log.info("Category search for '" + keywords[1] + "' returned " + categoryResults.size()
					+ " items. Items: " + Arrays.toString(categoryResults.toArray()));
			if (results != null) {
				results.retainAll(categoryResults);
			} else {
				results = categoryResults;
			}
		}
		if (!StringUtils.isEmpty(keywords[2])) {
			String[] location = splitLocationString(keywords[2]);
			List<Long> locationResults = getDAO().getListingsIdsForLocation(location[2], location[1], location[0], listingProperties);
			log.info("Location search for '" + Arrays.toString(location) + "' returned " + locationResults.size()
					+ " items. Items: " + Arrays.toString(locationResults.toArray()));
			if (results != null) {
				results.retainAll(locationResults);
			} else {
				results = locationResults;
			}
		}
		results = results.subList(0, listingProperties.getMaxResults() > results.size() ? results.size() : listingProperties.getMaxResults());
		log.info("Combined results contains " + results.size() + " items. Items: " + Arrays.toString(results.toArray()));
		
		List<Listing> listingList = getDAO().getListings(results);
		for (Listing listingDAO : listingList) {
			ListingVO listing = DtoToVoConverter.convert(listingDAO);
			listing.setOrderNumber(listings.size() + 1);
			if (Listing.State.ACTIVE.toString().equalsIgnoreCase(listing.getState())) {
				log.info("Active listing added to keyword search results " + listing);
				listings.add(listing);
			} else if (loggedInUser.getId().equals(listing.getOwner())) {
				log.info("Owned listing added to keyword search results " + listing);
				listings.add(listing);
			} else {
				log.info("Listing not added to results, listing: " + listing);
			}
			listingsList.setUser(new UserBasicVO(loggedInUser));
		}
		listingsList.setListings(listings);
		listingProperties.setNumberOfResults(listings.size());
		listingsList.setListingsProperties(listingProperties);
		listingsList.setCategories(getTopCategories());
		listingsList.setTopLocations(getTopLocations());
		return listingsList;
	}

	private String[] splitLocationString(String location) {
		String[] result = new String[3];
		StringTokenizer tokenizer = new StringTokenizer(location.toLowerCase(), ",");
		int locationParts = tokenizer.countTokens();
		String[] tokens = new String[locationParts];
		int tokenNr = 0;
		while(tokenizer.hasMoreTokens()) {
			tokens[tokenNr++] = tokenizer.nextToken().trim();
		}
		if (locationParts >= 3) {
			// city first, eg. Austin, TX, USA
			result[0] = tokens[0];
			result[1] = tokens[1];
			result[2] = tokens[2];
		} else if (locationParts == 2) {
			if (tokens[0].length() == 2) {
				// state and country provided, eg. CA, USA
				result[1] = tokens[0];
				// assuming that next token is country
				result[2] = tokens[1];
			} else {
				// city and coutry provided, eg. Rybnik, Poland
				result[0] = tokens[0];
				// assuming that first token is country
				result[2] = tokens[1];
			}
		} else if (locationParts == 1) {
			// only country was provided, eg. The Nederlands
			result[2] = tokens[0];
		}
		return result;
	}

	/**
	 * Returns search text split by type.
	 * 0 is full text search, 1 is category, 2 is location.
	 */
	public String[] splitSearchKeywords(String searchText) {
		String[] result = new String[] {"", "", ""};
		StringTokenizer tokenizer = new StringTokenizer(searchText);
		int lastType = -1;
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.startsWith("location:")) {
				result[2] = token.substring("location:".length());
				lastType = 2;
			} else if (token.startsWith("category:")) {
				result[1] = token.substring("category:".length());
				lastType = 1;
			} else if(lastType > 0) {
				result[lastType] += " " + token;
			} else {
				result[0] += token + " ";
				lastType = 0;
			}
		}
		result[0] = result[0].trim();
		result[1] = result[1].trim();
		result[2] = result[2].trim();
		return result;
	}

    public NotificationListVO getListingPrivateMessages(UserVO loggedInUser, String listingId, ListPropertiesVO notifProperties) {
        Notification.Type[] includeTypes = { Notification.Type.PRIVATE_MESSAGE };
        return getListingNotifications(loggedInUser, listingId, notifProperties, includeTypes);
    }

    public NotificationListVO getListingQuestionsAndAnswers(UserVO loggedInUser, String listingId, ListPropertiesVO notifProperties) {
        Notification.Type[] includeTypes = { Notification.Type.ASK_LISTING_OWNER };
        return getListingNotifications(loggedInUser, listingId, notifProperties, includeTypes);
    }  
    
	private NotificationListVO getListingNotifications(UserVO loggedInUser, String listingId, ListPropertiesVO notifProperties, Notification.Type[] includeTypes) {
		NotificationListVO list = new NotificationListVO();
		Listing listing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (listing == null) {
			list.setErrorCode(ErrorCodes.DATASTORE_ERROR);
			list.setErrorMessage("Listing doesn't exist!");
			log.log(Level.WARNING, "Listing '" + listingId + "' doesn't exist in datastore.");
			return list;
		}
		List<Notification> notifications = getDAO().getAllListingNotifications(BaseVO.toKeyId(listingId), notifProperties);

        Map<Notification.Type, Boolean> includeMap = new HashMap<Notification.Type, Boolean>();
        for (Notification.Type includeType : includeTypes) {
            includeMap.put(includeType, true);
        }
        
		//boolean isListingOwner = loggedInUser != null && loggedInUser.toKeyId() == listing.owner.getId();
		List<NotificationVO> filteredNotif = new ArrayList<NotificationVO>();
		int num = notifProperties.getStartIndex() > 0 ? notifProperties.getStartIndex() : 1;
		for (Notification notification : notifications) {
            boolean isApplicable = notification.type != null && includeMap.containsKey(notification.type) && includeMap.get(notification.type);
            if (notification.type == Notification.Type.PRIVATE_MESSAGE) { // special case, hide appropriately
                boolean authorOrAddresee = loggedInUser != null && notification.fromUser != null
                        && (notification.fromUser.getId() == loggedInUser.toKeyId() || notification.user.getId() == loggedInUser.toKeyId());
                if (!authorOrAddresee) { // isListingOwner never matters here
                    isApplicable = false;
                }
            }
            if (isApplicable) {
                NotificationVO notifVO = DtoToVoConverter.convert(notification);
                notifVO.setOrderNumber(num++);
                filteredNotif.add(notifVO);
            }
		}
		notifProperties.setTotalResults(notifications.size());
		list.setNotifications(filteredNotif);
		list.setNotificationsProperties(notifProperties);
		list.setUser(new UserBasicVO(loggedInUser));
		
		return list;
	}

	public List<ListingStats> updateAllListingStatistics() {
		List<ListingStats> list = new ArrayList<ListingStats>();
		Map<String, Location> locations = new HashMap<String, Location>();
		Map<String, Category> categories = new HashMap<String, Category>();
		for(Category category : getDAO().getCategories()) {
			category.count = 0;
			categories.put(category.name, category);
		}
	
		List<Listing> listings = getDAO().getAllListings();
		for (Listing listing : listings) {
			ListingStats stats = calculateListingStatistics(listing.id);
			if (listing.state == Listing.State.ACTIVE) {
				// updating top locations data
				Location loc = locations.get(listing.briefAddress);
				if (loc != null) {
					loc.value++;
				} else {
					locations.put(listing.briefAddress, new Location(listing.briefAddress));
				}
				list.add(stats);
				// updating top categories data
				Category cat = categories.get(listing.category);
				if (cat != null) {
					cat.count++;
				}
			}
		}
		getDAO().storeLocations(new ArrayList<Location>(locations.values()));
		getDAO().storeCategories(new ArrayList<Category>(categories.values()));
		
		log.log(Level.INFO, "Updated stats for " + list.size() + " listings: " + list);
		int updatedDocs = DocService.instance().updateListingData(listings);
		log.log(Level.INFO, "Updated docs for " + updatedDocs + " listings.");
		return list;
	}

	public void applyListingData(UserVO loggedInUser, ListingVO listing, Monitor monitor) {
		// set user data
		SBUser user = getDAO().getUser(listing.getOwner());
		listing.setOwnerName(user != null ? user.nickname : "<<unknown>>");
		
		ListingStats listingStats = getListingStatistics(listing.toKeyId());
		listing.setNumberOfBids(listingStats.numberOfBids);
		listing.setNumberOfComments(listingStats.numberOfComments);
		listing.setValuation((int)listingStats.valuation);
		listing.setMedianValuation((int)listingStats.medianValuation);
		listing.setPreviousValuation((int)listingStats.previousValuation);
		listing.setScore((int)listingStats.score);
		
		// calculate daysAgo and daysLeft
		Days daysAgo = Days.daysBetween(new DateTime(listing.getListedOn()), new DateTime());
		listing.setDaysAgo(daysAgo.getDays());
	
		Days daysLeft = Days.daysBetween(new DateTime(), new DateTime(listing.getClosingOn()));
		listing.setDaysLeft(daysLeft.getDays());
		
		listing.setMonitored(monitor != null && monitor.active);
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
			case DELETE_COMMENT:
				listingStats.numberOfComments = listingStats.numberOfComments - 1;
				break;
			case NEW_MONITOR:
				listingStats.numberOfMonitors = listingStats.numberOfMonitors + 1;
				break;
			case DELETE_MONITOR:
				listingStats.numberOfMonitors = listingStats.numberOfMonitors - 1;
				break;
			default:
				// reason can be also null
				break;
			}
			// updates stats in datastore and memcache
			log.info("Updating listing stats due to " + reason + ": " + listingStats);
			getDAO().storeListingStatistics(listingStats);
		}
		if (reason == UpdateReason.NONE) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "listing_stats_update_" + reason + "_" + listingWebKey;
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/calculate-listing-stats").param("id", listingWebKey).param("update_type", reason.name())
				.taskName(taskName).countdownMillis(LISTING_STATS_UPDATE_DELAY));
		}
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
		long editedListingId = BaseVO.toKeyId(loggedInUser.getEditedListing());
		Listing listing = getDAO().getListing(editedListingId);
		if (ListingDoc.Type.valueOf(doc.getType()) == ListingDoc.Type.LOGO) {
			BlobInfo logoInfo = new BlobInfoFactory().loadBlobInfo(doc.getBlob());
			byte logo[] = blobstoreService.fetchData(doc.getBlob(), 0, logoInfo.getSize() - 1);
			if (setLogoBase64(listing, logo) == null) {
				blobstoreService.delete(doc.getBlob());
				doc.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
				doc.setErrorMessage("Image conversion error.");
				return doc;
			}
		}
		ListingDoc docDTO = VoToModelConverter.convert(doc);
		docDTO = getDAO().createListingDocument(docDTO);
		doc = DtoToVoConverter.convert(docDTO);
		
		updateListingDoc(listing, docDTO);
		
		return doc;
	}

	private Listing updateListingDoc(Listing listing, ListingDoc docDTO) {
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
        if (newImage.getWidth() != 146) {
        	Transform resize = ImagesServiceFactory.makeResize(146, 146);
        	newImage = imagesService.applyTransform(resize, newImage);
    		log.info("Resized image: " + newImage.getWidth() + " x " + newImage.getHeight());
        }
        byte[] newImageData = newImage.getImageData();
		
		String logo64 = Base64.encodeBase64String(newImageData);
		log.info("Data uri for logo has " + logo64.length() + " bytes");
		return "data:" + format + ";base64," + logo64;
	}

	public ListingAndUserVO deleteListingFile(UserVO loggedInUser, String listingId, ListingDoc.Type docType) {
		ListingAndUserVO result = new ListingAndUserVO();
		
		Listing listing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (!loggedInUser.isAdmin() && !StringUtils.equals(listing.owner.getString(), loggedInUser.getId())) {
			result.setErrorCode(ErrorCodes.NOT_AN_OWNER);
			result.setErrorMessage("User '" + loggedInUser.getName() + "' is not an owner of listing.");
			return result;
		}
		if (!loggedInUser.isAdmin() && (listing.state == Listing.State.ACTIVE
				|| listing.state == Listing.State.FROZEN || listing.state == Listing.State.POSTED)) {
			result.setErrorCode(ErrorCodes.NOT_AN_ADMIN);
			result.setErrorMessage("User '" + loggedInUser.getName() + "' cannot modify this listing.");
			return result;
		}
		if (listing.state == Listing.State.CLOSED || listing.state == Listing.State.WITHDRAWN) {
			result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
			result.setErrorMessage("Listing in state " + listing.state + " cannot be edited.");
			return result;
		}
		
		Key<ListingDoc> docToDelete = null;
		switch(docType) {
		case BUSINESS_PLAN:
			docToDelete = listing.businessPlanId;
			listing.businessPlanId = null;
			break;
		case FINANCIALS:
			docToDelete = listing.financialsId;
			listing.financialsId = null;
			break;
		case PRESENTATION:
			docToDelete = listing.presentationId;
			listing.presentationId = null;
			break;
		case LOGO:
			docToDelete = listing.logoId;
			listing.logoId = null;
			listing.logoBase64 = null;
			break;
		}
		// we need to update listing just in case logoBase64 is filled but logoId not
		listing = getDAO().storeListing(listing);
		if (docToDelete != null) {
			try {
				ListingDoc doc = ObjectifyDatastoreDAO.getInstance().getListingDocument(docToDelete.getId());
				ObjectifyDatastoreDAO.getInstance().deleteDocument(docToDelete.getId());
				
				BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
				blobstoreService.delete(doc.blob);
			} catch (Exception e) {
				log.log(Level.WARNING, "Document object cannot be deleted from datastore.", e);
			}
		}
		result.setListing(DtoToVoConverter.convert(listing));

		return result;
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
	
	public Map<String, Integer> getTopCategories() {
		List<Category> categories = getDAO().getCategories();
		
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (Category cat : categories) {
			if (cat.count > 0) {
				result.put(cat.name, cat.count);
			}
		}
		return result;
	}
	
	public Map<String, Integer> getTopLocations() {
		List<Location> locations = getDAO().getTopLocations();
		
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (Location loc : locations) {
			result.put(loc.briefAddress, loc.value);
		}
		return result;
	}

	public ListingLocationsVO getAllListingLocations() {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		List<Object[]> data = (List<Object[]>)mem.get(MEMCACHE_ALL_LISTING_LOCATIONS);
		if (data == null) {
			List<ListingLocation> locations = getDAO().getAllListingLocations();
			
			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			dfs.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("###.######", dfs);
			data = new ArrayList<Object[]>();
			Set<String> locationSet = new HashSet<String>();
			
			String location[] = new String[2];
			for (ListingLocation loc : locations) {
				location[0] = df.format(loc.latitude);
				location[1] = df.format(loc.longitude);
				while (locationSet.contains(location[0] + location[1])) {
					location = randomizeLocation(loc, df);
				}
				locationSet.add(location[0] + location[1]);
				data.add(new Object[] {loc.getWebKey(), location[0], location[1]});
			}
			// all listing locations cache is also modified in method ObjectifyDatastoreDAO.updateListingStateAndDates
			mem.put(MEMCACHE_ALL_LISTING_LOCATIONS, data);
		}
		ListingLocationsVO result = new ListingLocationsVO();
		result.setListings(data);

		ListPropertiesVO props = new ListPropertiesVO();
		props.setMaxResults(data.size());
		props.setNumberOfResults(data.size());
		props.setTotalResults(data.size());
		result.setListingsProperties(props);
		
		return result;
	}

    private String[] randomizeLocation(ListingLocation loc, DecimalFormat df) {
    	log.info("Randomizing: " + loc);
		return new String[] {df.format(loc.latitude + ((double)RandomUtils.nextInt(100)) * 0.000001),
				df.format(loc.longitude + ((double)RandomUtils.nextInt(100)) * 0.000001)};
	}

	public void updateMockListingImages(long listingId) {
        updateMockListingImages(listingId, true);
    }

    public void updateMockListingImages(long listingId, boolean addDocs) {
		MockDataBuilder mock = new MockDataBuilder();
		
		List<ListingPropertyVO> props = new ArrayList<ListingPropertyVO>();
		
		Listing listing = getDAO().getListing(listingId);
		if (addDocs) {
			props.add(new ListingPropertyVO("business_plan_url", mock.getBusinessPlan()));
			props.add(new ListingPropertyVO("presentation_url", mock.getPresentation()));
			props.add(new ListingPropertyVO("financials_url", mock.getFinancials()));
		}
        props.add(new ListingPropertyVO("logo_url", mock.getLogo(listingId)));
        try {
            for (ListingPropertyVO prop : props) {
                if (prop.getPropertyValue() == null) {
                    continue;
                }
                ListingDoc doc = fetchAndUpdateListingDoc(listing, prop);
                if (doc != null) {
                    Listing updatedlisting = updateListingDoc(listing, doc);
                    if (updatedlisting != null) {
                        listing = updatedlisting;
                    } else {
                        log.warning("Error updating listing. " + listing);
                    }
                } else {
                    log.warning("Error while fetching/converting external resource. " + prop);
                }
            }
        }
        catch (Exception e) {
            log.log(Level.WARNING, "Exception while updating mock images for listing id: " + listingId, e);
        }
		getDAO().storeListing(listing);
	}

}
