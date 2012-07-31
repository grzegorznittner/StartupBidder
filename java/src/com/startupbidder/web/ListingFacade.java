package com.startupbidder.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.files.FinalizationException;
import com.google.appengine.api.files.LockException;
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
import com.startupbidder.dao.NotificationObjectifyDatastoreDAO;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Category;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.ListingDoc.Type;
import com.startupbidder.datamodel.ListingLocation;
import com.startupbidder.datamodel.ListingStats;
import com.startupbidder.datamodel.Location;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.PictureImport;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.util.ImageHelper;
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
import com.startupbidder.vo.ListingTileVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.NotificationVO;
import com.startupbidder.vo.UserAndUserVO;
import com.startupbidder.vo.UserBasicVO;
import com.startupbidder.vo.UserListingsVO;
import com.startupbidder.vo.UserVO;

public class ListingFacade {
	private static final Logger log = Logger.getLogger(ListingFacade.class.getName());
	
	public static enum UpdateReason {NEW_BID, BID_UPDATE, NEW_COMMENT, DELETE_COMMENT, NEW_MONITOR, DELETE_MONITOR, QUESTION_ANSWERED, NONE};
	public static final String MEMCACHE_ALL_LISTING_LOCATIONS = "AllListingLocations";

	private final static int PICTURE_HEIGHT = 452;
	private final static int PICTURE_WIDTH = 622;
	private final static int LOGO_HEIGHT = 146;
	private final static int LOGO_WIDTH = 146;
	
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
	
    private static class PictureData {
    	PictureData(String mimeType, byte data[]) {
    		this.mimeType = mimeType;
    		this.data = data;
    	}
    	byte data[];
    	String mimeType;
    }

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

	private NotificationObjectifyDatastoreDAO getNotificationDAO() {
		return NotificationObjectifyDatastoreDAO.getInstance();
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
			loggedInUser.setEditedStatus(Listing.State.NEW.toString());
			
			// at that stage listing is not yet active so there is no point of updating statistics
			Monitor monitor = getDAO().getListingMonitor(loggedInUser.toKeyId(), newListing.toKeyId());
			applyListingData(loggedInUser, newListing, monitor);
			result.setListing(newListing);
			result.setCategories(getCategories());
		}
		return result;
	}

	/**
	 * Imports listing data from external resources.
	 * Sets loggedin user as the owner of the listing.
	 * Sets listing's state to NEW.
	 */
	public ListingAndUserVO importListing(UserVO loggedInUser, String type, String id) {
		ListingAndUserVO result = new ListingAndUserVO();
		if (loggedInUser == null) {
			log.log(Level.WARNING, "Only logged in user can create listing", new Exception("Not logged in"));
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage("Only logged in user can create listing");
		} else {
			Listing newListing = null;
			if (loggedInUser.getEditedListing() != null) {
				newListing = getDAO().getListing(ListingVO.toKeyId(loggedInUser.getEditedListing()));
				if (newListing.state != Listing.State.NEW) {
					loggedInUser.setEditedListing(null);
					loggedInUser.setEditedStatus(null);
					result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
					result.setErrorMessage("User has already posted listing");
					return null;
				}
			} else {
				newListing = new Listing();
				newListing.state = Listing.State.NEW;
				newListing.owner = new Key<SBUser>(loggedInUser.getId());
				newListing.contactEmail = loggedInUser.getEmail();
				newListing.founders = !StringUtils.isEmpty(loggedInUser.getName()) ? loggedInUser.getName() : loggedInUser.getNickname();
				newListing.askedForFunding = false;
				newListing.created = new Date();
			}
			
			newListing = ListingImportService.instance().importListing(loggedInUser, type, newListing, id);

			ListingVO newListingVO;
			if (loggedInUser.getEditedListing() != null) {
				newListingVO = DtoToVoConverter.convert(getDAO().storeListing(newListing));
			} else {
				newListingVO = DtoToVoConverter.convert(getDAO().createListing(newListing));
			}
			loggedInUser.setEditedListing(newListingVO.getId());
			loggedInUser.setEditedStatus(newListingVO.getState());
			
			NotificationFacade.instance().schedulePictureImport(newListing, 1);

			// at that stage listing is not yet active so there is no point of updating statistics
			Monitor monitor = getDAO().getListingMonitor(loggedInUser.toKeyId(), newListing.id);
			ListingVO listing = DtoToVoConverter.convert(newListing);
			applyListingData(loggedInUser, listing, monitor);
			result.setListing(listing);
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
			} else {
				loggedInUser.setEditedStatus(listing.state.toString());
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
			Listing listingDTO = getDAO().getListing(id);
			ListingVO listing = DtoToVoConverter.convert(listingDTO);
			
			if (listing != null) {
				if (loggedInUser != null && loggedInUser.isAdmin()) {
					listing.setNotes(listingDTO.notes);
				}
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
		return updateListingProperties(loggedInUser, null, properties);
	}
	
	public ListingAndUserVO updateListingProperties(UserVO loggedInUser, String listingId, List<ListingPropertyVO> properties) {
		ListingAndUserVO result = new ListingAndUserVO();

		if (loggedInUser == null) {
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage("User is not logged in");
			return result;
		}
		if (listingId == null && loggedInUser.getEditedListing() == null) {
			result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
			result.setErrorMessage("User is not editing any listing");
			return result;
		}
		String editedListingId = loggedInUser.getEditedListing();
		if (!StringUtils.isEmpty(listingId)) {
			editedListingId = listingId;
		}
		// retrieving edited listing
		Listing listing = getDAO().getListing(BaseVO.toKeyId(editedListingId));
		if (listing == null) {
			result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
			result.setErrorMessage("Listing doesn't exist");
			return result;
		}
		if (!(loggedInUser.isAdmin() || listing.owner.getId() == loggedInUser.toKeyId())) {
			result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
			result.setErrorMessage("User is not an owner of the listing");
			return result;
		}
		if (listing.state != Listing.State.NEW) {
			// in not NEW state only certain properties are updatable
			List<ListingPropertyVO> propsToUpdate = new ArrayList<ListingPropertyVO>();
			for (ListingPropertyVO prop : properties) {
				String propertyName = prop.getPropertyName().toLowerCase();
				if (ListingVO.ACTIVE_UPDATABLE_PROPERTIES.contains(propertyName)) {
					propsToUpdate.add(prop);
				}
			}

			if (propsToUpdate.isEmpty()) {
				result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
				result.setErrorMessage("Listing properties not updatable in state " + listing.state);
				return result;
			}
			properties = propsToUpdate;
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
			}
            else if (ListingVO.FETCHED_PROPERTIES.contains(propertyName)) {
                ListingDocumentVO doc = fetchAndUpdateListingDoc(listing, prop);
                if (doc == null) {
                    result.setErrorCode(ErrorCodes.DATASTORE_ERROR);
                    result.setErrorMessage("Unable to fetch listing document");
                    return result;
                }
                if (doc.getErrorCode() != ErrorCodes.OK) {
                    String errorMsg = doc.getErrorMessage() != null ? doc.getErrorMessage() : "Unable to fetch listing document";
                    result.setErrorCode(doc.getErrorCode());
                    result.setErrorMessage(errorMsg);
                    return result;
                }
                ListingDoc listingDoc = VoToModelConverter.convert(doc);
    			Listing updatedlisting = updateListingDoc(listing, listingDoc);
				if (updatedlisting != null) {
					listing = updatedlisting;
					fetchedDoc = true;
				}
                else {
					result.setErrorCode(ErrorCodes.DATASTORE_ERROR);
					result.setErrorMessage("Error updating listing. " + infos.toString());
					fetchError = true;
					break;
				}
			}
            else {
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
        if (name.equals("LONG_locality") && value == null) { /* fallback */
            value = properties.get("formatted_address");
            int idx = value.indexOf(",");
            if (value != null && idx >= 0) {
                value = value.substring(0, idx);
            }
            log.warning("createMandatoryProperty could not find property: [" + name + "] thus falling back to: [" + (value != null ? value : "") + "]");
        }
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
	
	public ListingDocumentVO fetchAndUpdateListingDoc(Listing listing, ListingPropertyVO prop) {
        ListingDocumentVO doc = new ListingDocumentVO();
        doc.setErrorCode(ErrorCodes.OK);
		byte[] docBytes = null;
		String mimeType = null;
		String propName = prop.getPropertyName();
		String propValue = prop.getPropertyValue();
		try {
			boolean isDevEnvironment = com.google.appengine.api.utils.SystemProperty.environment.value() == com.google.appengine.api.utils.SystemProperty.Environment.Value.Development;
			String url = prop.getPropertyValue();
			if(!url.startsWith("http") && !url.startsWith("android") && isDevEnvironment && new File("./test-docs").exists()) {
				docBytes = FileUtils.readFileToByteArray(new File(propValue));
				mimeType = ImageHelper.getMimeTypeFromFileName(propValue);
			} else {
				URLConnection con = new URL(url).openConnection();
				docBytes = IOUtils.toByteArray(con.getInputStream());
				mimeType = con.getContentType();
			}
			log.info("Fetched " + docBytes.length + " bytes, content type '" + mimeType
					+ "', from '" + propValue + "'");
		} catch (Exception e) {
            String errorMsg = "Error while fetching document from " + propValue;
            log.log(Level.WARNING, errorMsg, e);
			doc.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			doc.setErrorMessage(errorMsg);
			return doc;
		}
		if (propName.equalsIgnoreCase("logo_url")) {
            String errorMsg = setLogoBase64(listing, docBytes);
            if (errorMsg != null) {
                log.log(Level.WARNING, "fetchAndUpdateListingDoc: " + errorMsg);
                doc.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
                doc.setErrorMessage(errorMsg);
                return doc;
            }
		}
		if (propName.equalsIgnoreCase("pic1_url") || propName.equalsIgnoreCase("pic2_url") || propName.equalsIgnoreCase("pic3_url")
				|| propName.equalsIgnoreCase("pic4_url") || propName.equalsIgnoreCase("pic5_url")) {
            PictureData convertedData = convertPicture(docBytes);
            if (convertedData == null || convertedData.data == null) {
                String errorMsg = convertedData != null ? convertedData.mimeType : "Image conversion error";
                log.warning("fetchAndUpdateListingDoc: " + errorMsg);
                doc.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
                doc.setErrorMessage(errorMsg);
                return doc;
			}
			docBytes = convertedData.data;
		}

		try {
			Type type = getListingDocTypeFromPropertyName(propName);
			ListingDoc listingDoc = new ListingDoc();
			listingDoc.blob = createBlob(docBytes, type, mimeType);
			listingDoc.type = type;
			getDAO().createListingDocument(listingDoc);
            doc = DtoToVoConverter.convert(listingDoc);
            doc.setErrorCode(ErrorCodes.OK);
			return doc;
		} catch (Exception e) {
            String errorMsg = "Error storing document";
            log.warning("fetchAndUpdateListingDoc: " + errorMsg);
            doc.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
            doc.setErrorMessage(errorMsg);
            return doc;
		}
	}

	private BlobKey createBlob(byte[] docBytes, Type type, String mimeType)
			throws IOException, FileNotFoundException, FinalizationException, LockException {
		FileService fileService = FileServiceFactory.getFileService();
		AppEngineFile file = fileService.createNewBlobFile(mimeType);
		FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
		writeChannel.write(ByteBuffer.wrap(docBytes));
		writeChannel.closeFinally();
		BlobKey key = fileService.getBlobKey(file);
		if (key == null) {
			log.warning("Blob not created for file " + file);
			return null;
		}
		return key;
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
		} else if (propertyName.equalsIgnoreCase("pic1_url")) {
			return ListingDoc.Type.PIC1;
		} else if (propertyName.equalsIgnoreCase("pic2_url")) {
			return ListingDoc.Type.PIC2;
		} else if (propertyName.equalsIgnoreCase("pic3_url")) {
			return ListingDoc.Type.PIC3;
		} else if (propertyName.equalsIgnoreCase("pic4_url")) {
			return ListingDoc.Type.PIC4;
		} else if (propertyName.equalsIgnoreCase("pic5_url")) {
			return ListingDoc.Type.PIC5;
		}
		log.warning("Not recognized property '" + propertyName + "', defaulting to LOGO");
		return ListingDoc.Type.LOGO;
	}

	public Listing importListingPictures(String listingId, int index) {
		Listing listing = getDAO().getListing(ListingVO.toKeyId(listingId));
		PictureImport picture = getDAO().getFirstPictureImport(listing.getKey());
		if (picture != null) {
			log.info("Importing picture for listing '" + listing.getWebKey() + "' from url " + picture.url);
			String propertyName = "pic" + index + "_url";
			ListingPropertyVO prop = new ListingPropertyVO(propertyName, picture.url);
			ListingDocumentVO doc = ListingFacade.instance().fetchAndUpdateListingDoc(listing, prop);
	        if (doc != null && doc.getErrorCode() == ErrorCodes.OK) {
	            ListingDoc listingDoc = VoToModelConverter.convert(doc);
	            updateListingDoc(listing, listingDoc);
	            NotificationFacade.instance().schedulePictureImport(listing, index + 1);
	        } else {
	        	log.warning("Error fetching '" + propertyName + "' from " + picture.url);
	        	NotificationFacade.instance().schedulePictureImport(listing, index);
	        }
		} else {
			log.info("No more pictures to import for listing " + listing.getWebKey());
		}
        return listing;
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
			
			Listing updatedListing = getDAO().updateListingStateAndDates(VoToModelConverter.convert(forUpdate),
					"Listing updated as a whole object on " + new Date() + " by " + loggedInUser.getNickname());
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
		Listing updatedListing = getDAO().updateListingStateAndDates(VoToModelConverter.convert(forUpdate),
				"Listing activated on " + new Date() + " by " + loggedInUser.getNickname());
		if (updatedListing != null && updatedListing.state == Listing.State.ACTIVE) {
			loggedInUser.setEditedListing(null);
			loggedInUser.setEditedStatus(null);
			scheduleUpdateOfListingStatistics(updatedListing.getWebKey(), UpdateReason.NONE);
			NotificationFacade.instance().scheduleListingStateNotification(updatedListing);
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
			
			Listing updatedListing = getDAO().updateListingStateAndDates(VoToModelConverter.convert(forUpdate),
					"Listing posted on " + new Date() + " by " + loggedInUser.getNickname());
			if (updatedListing != null) {
				if (StringUtils.equals(updatedListing.owner.getString(), loggedInUser.getId())) {
					loggedInUser.setEditedListing(null);
					loggedInUser.setEditedStatus(null);
				}
				scheduleUpdateOfListingStatistics(updatedListing.getWebKey(), UpdateReason.NONE);
				NotificationFacade.instance().scheduleListingStateNotification(updatedListing);
			} else {
				loggedInUser.setEditedStatus(Listing.State.POSTED.toString());
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
		
		// only ACTIVE listings can be WITHDRAWN
		if (dbListing.state == Listing.State.ACTIVE) {
			ListingVO forUpdate = DtoToVoConverter.convert(dbListing);

			forUpdate.setState(Listing.State.WITHDRAWN.toString());
			
			Listing updatedListing = getDAO().updateListingStateAndDates(VoToModelConverter.convert(forUpdate),
					"Listing withdrawn on " + new Date() + " by " + loggedInUser.getNickname());
			if (updatedListing != null) {
				scheduleUpdateOfListingStatistics(updatedListing.getWebKey(), UpdateReason.NONE);
				NotificationFacade.instance().scheduleListingStateNotification(updatedListing);
			} else {
				returnValue.setErrorMessage("Listing not updated");
				returnValue.setErrorCode(ErrorCodes.DATASTORE_ERROR);
			}
			ListingVO toReturn = DtoToVoConverter.convert(updatedListing);
            if (toReturn != null) {
			    Monitor monitor = getDAO().getListingMonitor(loggedInUser.toKeyId(), toReturn.toKeyId());
			    applyListingData(loggedInUser, toReturn, monitor);
			    returnValue.setListing(toReturn);
            }
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
	public ListingAndUserVO sendBackListingToOwner(UserVO loggedInUser, String listingId, String message) {
		ListingAndUserVO returnValue = new ListingAndUserVO();
		
		Listing dbListing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (loggedInUser == null || dbListing == null || !loggedInUser.isAdmin()) {
			log.warning("User " + loggedInUser + " is not admin");
			returnValue.setErrorMessage("User " + loggedInUser + " is not admin");
			returnValue.setErrorCode(ErrorCodes.NOT_AN_ADMIN);
			return returnValue;
		}
		
		UserAndUserVO userVO = UserMgmtFacade.instance().getUser(null, dbListing.owner.getString());
		if (userVO == null) {
			log.warning("Listing owner " + dbListing.owner + " cannot be found!");
			returnValue.setErrorMessage("Listing owner cannot be found");
			returnValue.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			return returnValue;
		}
		if (dbListing.state == Listing.State.POSTED || dbListing.state == Listing.State.FROZEN) {
			List<Listing> newOrPosted = getDAO().getUserNewOrPostedListings(dbListing.owner.getId());
            SBUser user = getDAO().getUser(dbListing.owner.getString());
            String nickname = user != null ? user.nickname : "unknown";
            Listing.State listingState = (newOrPosted != null && newOrPosted.size() > 0 && newOrPosted.get(0) != null) ? newOrPosted.get(0).state : null;
            String errorStr = null;
			if (listingState == Listing.State.NEW) {
                errorStr = "Listing owner with nickname '" + nickname + "' already has a new listing, cannot send back.";
            }
            else if (listingState == Listing.State.POSTED && dbListing.state == Listing.State.FROZEN) {
                errorStr = "Listing owner with nickname '" + nickname + "' already has an in-progress listing, cannot send back";
			}
            if (errorStr != null) {
				log.warning(errorStr);
				returnValue.setErrorMessage(errorStr);
				returnValue.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
				return returnValue;
			}
			
			ListingVO forUpdate = DtoToVoConverter.convert(dbListing);
			forUpdate.setState(Listing.State.NEW.toString());
			
			Listing updatedListing = getDAO().updateListingStateAndDates(VoToModelConverter.convert(forUpdate),
					"Listing sent back on " + new Date() + " by " + loggedInUser.getNickname());
			if (updatedListing == null) {
                log.severe("Could not update listing in datastore to NEW status: " + forUpdate.getId());
				returnValue.setErrorMessage("Listing not updated");
				returnValue.setErrorCode(ErrorCodes.DATASTORE_ERROR);
			}
            else {
                user.editedListing = dbListing.getKey();
                getDAO().updateUser(user);
                if (user.getWebKey().equals(loggedInUser.getId())) {

                }
				NotificationFacade.instance().scheduleListingStateNotification(updatedListing);
				if (message == null) {
					message = "Your listing has been send back for revision, please see the private administrator message for details.";
				}
				MessageFacade.instance().sendPrivateMessage(loggedInUser, updatedListing.owner.getString(), message);
			}
			ListingVO toReturn = DtoToVoConverter.convert(updatedListing);
			Monitor monitor = getDAO().getListingMonitor(loggedInUser.toKeyId(), toReturn.toKeyId());
			applyListingData(loggedInUser, toReturn, monitor);
			returnValue.setListing(toReturn);
			return returnValue;
		}
		returnValue.setErrorMessage("Only posted or frozen listings can be send back for update to owner");
		returnValue.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
		return returnValue;
	}

	/**
	 * Freeze listing so it cannot be bid or modified. It's an administrative action.
	 */
	public ListingAndUserVO freezeListing(UserVO loggedInUser, String listingId, String message) {
		ListingAndUserVO returnValue = new ListingAndUserVO();
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			log.warning("User not logged in or '" + loggedInUser + "' is not an admin");
			returnValue.setErrorMessage("Only admins can freeze listings.");
			returnValue.setErrorCode(ErrorCodes.NOT_AN_ADMIN);
			return returnValue;
		}
		
		Listing dbListing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (dbListing == null || dbListing.state == Listing.State.NEW || dbListing.state == Listing.State.POSTED) {
			log.warning("Listing does not exist or is not yet activated. Listing: " + dbListing);
			returnValue.setErrorMessage("Listing does not exist or is not yet activated");
			returnValue.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			return returnValue;
		}
		
		if (dbListing.state != Listing.State.NEW && dbListing.state != Listing.State.POSTED) {
			// admins can always freeze listing
			ListingVO forUpdate = DtoToVoConverter.convert(dbListing);
	
			forUpdate.setState(Listing.State.FROZEN.toString());
			
			Listing updatedListing = getDAO().updateListingStateAndDates(VoToModelConverter.convert(forUpdate),
					"Listing frozen on " + new Date() + " by " + loggedInUser.getNickname());
			if (updatedListing == null) {
				returnValue.setErrorMessage("Listing not updated");
				returnValue.setErrorCode(ErrorCodes.DATASTORE_ERROR);
			} else {
				NotificationFacade.instance().scheduleListingStateNotification(updatedListing);
				if (message == null) {
					message = "Your listing has been frozen. An administrator will contact you via private message.";
				}
				MessageFacade.instance().sendPrivateMessage(loggedInUser, updatedListing.owner.getString(), message);
			}
			
			ListingVO toReturn = DtoToVoConverter.convert(updatedListing);
			Monitor monitor = getDAO().getListingMonitor(loggedInUser.toKeyId(), toReturn.toKeyId());
			applyListingData(loggedInUser, toReturn, monitor);
			returnValue.setListing(toReturn);
			return returnValue;
		}
		returnValue.setErrorMessage("NEW or POSTED listings cannot be frozen as they are not yet active.");
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
		
		ListPropertiesVO props = new ListPropertiesVO();
		props.setMaxResults(4);
		List<ListingTileVO> list = prepareListingList(loggedInUser, getDAO().getTopListings(props));
		result.setTopListings(list);

		props = new ListPropertiesVO();
		props.setMaxResults(4);
		list = prepareListingList(loggedInUser, getDAO().getClosingListings(props));
		result.setClosingListings(list);

		props = new ListPropertiesVO();
		props.setMaxResults(4);
		list = prepareListingList(loggedInUser, getDAO().getLatestListings(props));
		result.setLatestListings(list);
		
		if (loggedInUser != null) {
			props = new ListPropertiesVO();
			props.setMaxResults(4);
			list = prepareListingList(loggedInUser, getDAO().getUserActiveListings(loggedInUser.toKeyId(), props));
			result.setUsersListings(list);
			
			props = new ListPropertiesVO();
			props.setMaxResults(4);
			list = prepareListingList(loggedInUser, getDAO().getMonitoredListings(loggedInUser.toKeyId(), props));
			result.setMonitoredListings(list);
			
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
		ListPropertiesVO props = new ListPropertiesVO();
		props.setMaxResults(4);
		List<ListingTileVO> activeListings = prepareListingList(loggedInUser,
				getDAO().getUserListings(loggedInUser.toKeyId(), Listing.State.ACTIVE, props));
		props = new ListPropertiesVO();
		props.setMaxResults(4);
		List<ListingTileVO> withdrawnListings = prepareListingList(loggedInUser,
				getDAO().getUserListings(loggedInUser.toKeyId(), Listing.State.WITHDRAWN, props));
		props = new ListPropertiesVO();
		props.setMaxResults(4);
		List<ListingTileVO> frozenListings = prepareListingList(loggedInUser,
				getDAO().getUserListings(loggedInUser.toKeyId(), Listing.State.FROZEN, props));
		props = new ListPropertiesVO();
		props.setMaxResults(4);
		List<ListingTileVO> closedListings = prepareListingList(loggedInUser,
				getDAO().getUserListings(loggedInUser.toKeyId(), Listing.State.CLOSED, props));

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
		
		props = new ListPropertiesVO();
		props.setMaxResults(4);
		List<Listing> monitoredListing = getDAO().getMonitoredListings(loggedInUser.toKeyId(), props);
		List<ListingTileVO> list = prepareListingList(loggedInUser, monitoredListing);
		result.setCommentedListings(list);
		
		props = new ListPropertiesVO();
		props.setMaxResults(10);
		List<NotificationVO> notifications = DtoToVoConverter.convertNotifications(
				getNotificationDAO().getAllUserNotifications(VoToModelConverter.convert(loggedInUser), props));
		result.setNotifications(notifications);
		
		if (loggedInUser.isAdmin()) {
			props = new ListPropertiesVO();
			props.setMaxResults(4);
			List<Listing> adminFrozenListing = getDAO().getFrozenListings(props);
			list = prepareListingList(loggedInUser, adminFrozenListing);
			result.setAdminFrozenListings(list);

			props = new ListPropertiesVO();
			props.setMaxResults(4);
			List<Listing> adminPostedListing = getDAO().getPostedListings(props);
			list = prepareListingList(loggedInUser, adminPostedListing);
			result.setAdminPostedListings(list);
		}
		
		result.setCategories(getTopCategories());
		result.setTopLocations(getTopLocations());

		return result;
	}

	private List<ListingTileVO> prepareListingList(UserVO loggedInUser, List<Listing> listings) {
		ListingTileVO listingVO = null;
		int index = 1;
		List<ListingTileVO> list = new ArrayList<ListingTileVO>();
		for (Listing listing : listings) {
			listingVO = DtoToVoConverter.convertTile(listing);
			listingVO.setOrderNumber(index++);
			list.add(listingVO);
		}
		return list;
	}
	
	public ListingListVO getMonitoredListings(UserVO loggedInUser, ListPropertiesVO listProperties) {
		ListingListVO list = new ListingListVO();
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			list.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			list.setErrorMessage("User is not logged in.");
			return list;
		}
		List<ListingTileVO> listings = DtoToVoConverter.convertListingTiles(
				getDAO().getMonitoredListings(loggedInUser.toKeyId(), listProperties));
		int index = listProperties.getStartIndex() > 0 ? listProperties.getStartIndex() : 1;
		for (ListingTileVO listing : listings) {
			listing.setOrderNumber(index++);
		}
		applyShortNotifications(loggedInUser, list);
		list.setListings(listings);
		list.setListingsProperties(listProperties);
		list.setCategories(getTopCategories());
		list.setTopLocations(getTopLocations());
	
		return list;
	}

	public ListingListVO getClosingActiveListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		List<ListingTileVO> listings = DtoToVoConverter.convertListingTiles(getDAO().getClosingListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingTileVO listing : listings) {
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
		List<ListingTileVO> listings = DtoToVoConverter.convertListingTiles(getDAO().getMostDiscussedListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingTileVO listing : listings) {
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
		List<ListingTileVO> listings = DtoToVoConverter.convertListingTiles(getDAO().getMostPopularListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingTileVO listing : listings) {
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
		if (true) {
			throw new RuntimeException("This method is not working!");
		}
		List<ListingVO> listings = DtoToVoConverter.convertListings(getDAO().getTopListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingVO listing : listings) {
			listing.setOrderNumber(index++);
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
		
		ListingListVO list = new ListingListVO();
//		list.setListings(listings);
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
		List<ListingTileVO> listings = DtoToVoConverter.convertListingTiles(getDAO().getTopListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingTileVO listing : listings) {
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
	public ListingListVO getUserListings(UserVO loggedInUser, String stateString, ListPropertiesVO listingProperties) {
		ListingListVO list = new ListingListVO();
		if (loggedInUser == null) {
			log.log(Level.WARNING, "User not logged in");
			list.setErrorMessage("User not logged in");
			list.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			return list;
		}
		Listing.State state = stateString != null ? Listing.State.valueOf(stateString.toUpperCase()) : null;
		List<ListingTileVO> listings = DtoToVoConverter.convertListingTiles(
				getDAO().getUserListings(loggedInUser.toKeyId(), state, listingProperties));

		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingTileVO listing : listings) {
			listing.setOrderNumber(index++);
		}
		
		applyShortNotificationsAndMonitoredListings(loggedInUser, list);
		
		list.setListings(listings);
		list.setListingsProperties(listingProperties);
		list.setCategories(getTopCategories());
		list.setTopLocations(getTopLocations());
		list.setUser(new UserBasicVO(loggedInUser));
	
		return list;
	}

	public void applyShortNotifications(UserVO loggedInUser, ListingListVO list) {
		List<NotificationVO> notifications = null;
		ListPropertiesVO notifProperties = new ListPropertiesVO();
		notifProperties.setMaxResults(5);
		notifications = DtoToVoConverter.convertNotifications(
				getNotificationDAO().getAllUserNotifications(loggedInUser, notifProperties));
        list.setNotifications(notifications);
    }
	
	public void applyShortNotificationsAndMonitoredListings(UserVO loggedInUser, ListingListVO list) {
		List<NotificationVO> notifications = null;
		ListPropertiesVO notifProperties = new ListPropertiesVO();
		notifProperties.setMaxResults(5);
		notifications = DtoToVoConverter.convertNotifications(
				getNotificationDAO().getAllUserNotifications(loggedInUser, notifProperties));
		list.setNotifications(notifications);

		ListPropertiesVO props = new ListPropertiesVO();
		props.setMaxResults(4);
		List<Listing> monitoredListing = getDAO().getMonitoredListings(loggedInUser.toKeyId(), props);
		List<ListingTileVO> monitored = prepareListingList(loggedInUser, monitoredListing);
		list.setMonitoredListings(monitored);
	}
	
	public ListingListVO getPostedListings(UserVO loggedInUser, ListPropertiesVO listingProperties) {
		ListingListVO list = new ListingListVO();
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			list.setErrorCode(ErrorCodes.NOT_AN_ADMIN);
			list.setErrorMessage("Only admins can see posted listings");
			return list;
		}
		List<ListingTileVO> listings = DtoToVoConverter.convertListingTiles(getDAO().getPostedListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingTileVO listing : listings) {
			listing.setOrderNumber(index++);
		}
		
		applyShortNotificationsAndMonitoredListings(loggedInUser, list);
		
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
		List<ListingTileVO> listings = DtoToVoConverter.convertListingTiles(getDAO().getActiveListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingTileVO listing : listings) {
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
		List<ListingTileVO> listings = DtoToVoConverter.convertListingTiles(getDAO().getFrozenListings(listingProperties));
		int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
		for (ListingTileVO listing : listings) {
			listing.setOrderNumber(index++);
		}
		
		applyShortNotificationsAndMonitoredListings(loggedInUser, list);
		
		list.setListings(listings);		
		list.setListingsProperties(listingProperties);
		list.setCategories(getTopCategories());
		list.setTopLocations(getTopLocations());
	
		return list;
	}

    public ListingListVO getListingsForCategory(String categoryString, ListPropertiesVO listingProperties) {
        ListingListVO list = new ListingListVO();
        if (StringUtils.isEmpty(categoryString)) {
            return list;
        }
        List<Listing> categoryListings = getDAO().getListingsForCategory(categoryString, listingProperties);
        log.info("Category search for '" + categoryString + "' returned " + categoryListings.size() + " items.");
        List<ListingTileVO> listings = DtoToVoConverter.convertListingTiles(categoryListings);
        int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
        for (ListingTileVO listing : listings) {
            listing.setOrderNumber(index++);
        }
        list.setListings(listings);
        list.setListingsProperties(listingProperties);
        list.setCategories(getTopCategories());
        list.setTopLocations(getTopLocations());
        return list;
    }

    public ListingListVO getListingsForLocation(String locationString, ListPropertiesVO listingProperties) {
        ListingListVO list = new ListingListVO();
        if (StringUtils.isEmpty(locationString)) {
            return list;
        }
        String[] location = splitLocationString(locationString);
        List<Listing> locationListings = getDAO().getListingsForLocation(location[2], location[1], location[0], listingProperties);
        log.info("Location search for '" + Arrays.toString(location) + "' returned " + locationListings.size() + " items.");
        List<ListingTileVO> listings = DtoToVoConverter.convertListingTiles(locationListings);
        int index = listingProperties.getStartIndex() > 0 ? listingProperties.getStartIndex() : 1;
        for (ListingTileVO listing : listings) {
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
		List<ListingTileVO> listings = new ArrayList<ListingTileVO>();
		String[] keywords = splitSearchKeywords(text);
        
        // rational inits
        listingProperties.setStartIndex(listingProperties.getStartIndex() >= 1 ? listingProperties.getStartIndex() : 1);
        listingProperties.setMaxResults(listingProperties.getMaxResults() >= 1 && listingProperties.getMaxResults() <= 20 ? listingProperties.getMaxResults() : 20);
        int limitSize = listingProperties.getStartIndex() + 2*listingProperties.getMaxResults(); // always ask for extra since there may be sent back/frozen/withdrawn before google docs is updated
		log.info("Ready to query for keywords=[" + keywords[0] + "] with limitSize = [" + limitSize + "]");
        
		List<Long> results = null;
		if (!StringUtils.isEmpty(keywords[0])) {
			results = DocService.instance().fullTextSearch(keywords[0], limitSize);
		}
        log.info("Got back results size=[" + results.size() + "]");
        // use dedicated /listing/category and /listing/location searches instead 
        /*
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
		*/

        // limit results returned
        if (results.size() > 0) {
		    results = results.subList(listingProperties.getStartIndex() - 1, results.size());
        }
		log.info("Results to be returned contains " + results.size() + " items. Items: " + Arrays.toString(results.toArray()));
		
        // get our final list of listings, and construct more results cursor if necessary along the way
        boolean moreResults = false;
        int nextStartIndex = listingProperties.getStartIndex();
		List<Listing> listingList = getDAO().getListings(results);
		for (Listing listingDAO : listingList) {
            boolean potentialAdd = false;
			ListingTileVO listing = DtoToVoConverter.convertTile(listingDAO);
			if (Listing.State.ACTIVE.toString().equalsIgnoreCase(listing.getState())) { // should always be the case
				log.info("Active listing potentially added to keyword search results " + listing);
                potentialAdd = true;
            }
            else if (loggedInUser != null && loggedInUser.getId().equals(listing.getOwner())) {
				log.info("Owned listing potentially added to keyword search results " + listing);
                potentialAdd = true;
				listings.add(listing);
			}
            else {
				log.info("Listing not added to results, listing: " + listing);
			}
            if (potentialAdd) {
                if (listings.size() == listingProperties.getMaxResults()) { // too big, can't add, but have more results
                    moreResults = true;
                    break;
                }
                else {
                    listing.setOrderNumber(listings.size() + 1);
                    listings.add(listing);
                }
            }
            nextStartIndex++;
		}

        // excess result calculations
        listingProperties.setNumberOfResults(listings.size());
        if (moreResults) {
            listingProperties.updateMoreResultsUrl(nextStartIndex);
        }
        log.info("Calculated numResults=[" + listings.size() + "] with moreResults=[" + moreResults + "] and moreResultsUrl=[" + listingProperties.getMoreResultsUrl() + "]");

		if (loggedInUser != null) {
			listingsList.setUser(new UserBasicVO(loggedInUser));
		}
		listingsList.setListings(listings);
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

    public String updateAllAggregateStatistics() {
        List<Category> c = getDAO().getCategories();
        log.log(Level.INFO, "Starting category calculation for " + c.size() + " categories");
        Map<String, Category> categories = new HashMap<String, Category>();
        for(Category category : c) {
            category.count = 0;
            categories.put(category.name, category);
        }
        log.log(Level.INFO, "Calculated count statistics for " + c.size() + " categories");

        Map<String, Location> locations = new HashMap<String, Location>();
        List<Listing> listings = getDAO().getAllListingsInternal();
        log.log(Level.INFO, "Starting listing calculation for " + listings.size() + " listings");
          for (Listing listing : listings) {
            if (listing.state == Listing.State.ACTIVE) {
                // updating top locations data
                Location loc = locations.get(listing.briefAddress);
                if (loc != null) {
                    loc.value++;
                } else {
                    locations.put(listing.briefAddress, new Location(listing.briefAddress));
                }
                // updating top categories data
                Category cat = categories.get(listing.category);
                if (cat != null) {
                    cat.count++;
                }
            }
        }
        log.log(Level.INFO, "Generated " + locations.size() + " locations for " + listings.size() + " listings");

        getDAO().storeCategories(new ArrayList<Category>(categories.values()));
        log.log(Level.INFO, "Updated count statistics for " + c.size() + " categories ");
        getDAO().storeLocations(new ArrayList<Location>(locations.values()));
        log.log(Level.INFO, "Updated location statistics for " + locations.size() + " locations");

        return "Updated statistics for " + c.size() + " categories and " + locations.size() + " locations";
    }
    
	public String updateAllListingStatistics() {
		List<Listing> listings = getDAO().getAllListingsInternal();
        log.log(Level.INFO, "Starting stat update for " + listings.size() + " listings");
		for (Listing listing : listings) {
			calculateListingStatistics(listing.id);
		}
		log.log(Level.INFO, "Updated stats for " + listings.size() + " listings");
        return "Updated stats for " + listings.size() + " listings";
	}

    public String updateAllListingDocuments() {
        List<Listing> listings = getDAO().getAllListingsInternal();
        log.log(Level.INFO, "Starting doc update for " + listings.size() + " listings.");
        int updatedDocs = DocService.instance().updateAllListingsData(listings);
        log.log(Level.INFO, "Scheduled google doc update for " + updatedDocs + " listings.");
        return "Scheduled google doc update for " + updatedDocs + " listings.";
    }
    
	public void applyListingData(UserVO loggedInUser, ListingVO listing, Monitor monitor) {
		// set user data
		SBUser user = getDAO().getUser(listing.getOwner());
		listing.setOwnerName(user != null ? user.nickname : "<<unknown>>");
		
		ListingStats listingStats = getListingStatistics(listing.toKeyId());
		listing.setNumberOfBids(listingStats.numberOfBids);
		listing.setNumberOfComments(listingStats.numberOfComments);
        listing.setNumberOfQuestions(listingStats.numberOfQuestions);
		listing.setValuation((int)listingStats.valuation);
		listing.setMedianValuation((int)listingStats.medianValuation);
		listing.setPreviousValuation((int)listingStats.previousValuation);
		listing.setScore((int)listingStats.score);
		
		// calculate daysAgo and daysLeft

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
            case QUESTION_ANSWERED:
                listingStats.numberOfQuestions++;
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
		ListingDoc.Type docType = ListingDoc.Type.valueOf(doc.getType());
		if (docType == ListingDoc.Type.LOGO) {
			BlobInfo logoInfo = new BlobInfoFactory().loadBlobInfo(doc.getBlob());
			byte logo[] = blobstoreService.fetchData(doc.getBlob(), 0, logoInfo.getSize() - 1);
            String errorMsg = setLogoBase64(listing, logo);
            if (errorMsg != null) {
                log.warning("createListingDocument: " + errorMsg);
				blobstoreService.delete(doc.getBlob());
				doc.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
				doc.setErrorMessage(errorMsg);
				return doc;
			}
		}
		if (docType == ListingDoc.Type.PIC1 || docType == ListingDoc.Type.PIC2 || docType == ListingDoc.Type.PIC3
				|| docType == ListingDoc.Type.PIC4 || docType == ListingDoc.Type.PIC5) {
			BlobInfo picInfo = new BlobInfoFactory().loadBlobInfo(doc.getBlob());
			byte pic[] = blobstoreService.fetchData(doc.getBlob(), 0, picInfo.getSize() - 1);
			PictureData convertedData = convertPicture(pic);
			if (convertedData == null || convertedData.data == null) {
                String errorMsg = convertedData != null ? convertedData.mimeType : "Image conversion error";
                log.warning(errorMsg);
				deleteBlob(blobstoreService, doc.getBlob());
				doc.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
				doc.setErrorMessage(errorMsg);
				return doc;
			}
			deleteBlob(blobstoreService, doc.getBlob());
			try {
				doc.setBlob(createBlob(convertedData.data, docType, convertedData.mimeType));
				log.info("New Blob for converted image created: " + doc.getBlob());
			} catch (Exception e) {
				log.log(Level.WARNING, "Error storing doc as blob", e);
				return null;
			}
		}
		ListingDoc docDTO = VoToModelConverter.convert(doc);
		docDTO = getDAO().createListingDocument(docDTO);
		doc = DtoToVoConverter.convert(docDTO);
		
		updateListingDoc(listing, docDTO);
		
		return doc;
	}

	private void deleteBlob(BlobstoreService blobstoreService, BlobKey blobToDelete) {
		try {
			blobstoreService.delete(blobToDelete);
		} catch (BlobstoreFailureException e) {
			log.log(Level.WARNING, "Error deleting blob", e);
		}
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
		case PIC1:
			replacedDocId = listing.pic1Id;
			listing.pic1Id = new Key<ListingDoc>(ListingDoc.class, docDTO.id);
			break;
		case PIC2:
			replacedDocId = listing.pic2Id;
			listing.pic2Id = new Key<ListingDoc>(ListingDoc.class, docDTO.id);
			break;
		case PIC3:
			replacedDocId = listing.pic3Id;
			listing.pic3Id = new Key<ListingDoc>(ListingDoc.class, docDTO.id);
			break;
		case PIC4:
			replacedDocId = listing.pic4Id;
			listing.pic4Id = new Key<ListingDoc>(ListingDoc.class, docDTO.id);
			break;
		case PIC5:
			replacedDocId = listing.pic5Id;
			listing.pic5Id = new Key<ListingDoc>(ListingDoc.class, docDTO.id);
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
	
	private String setLogoBase64(Listing listing, byte[] logo) {
		String logoBase64 = convertLogoToBase64(logo);
        String errorMsg = null;
        if (logoBase64 != null && logoBase64.indexOf("data:") == 0) {
            listing.logoBase64 = logoBase64;
        }
        else if (logoBase64 != null) {
            errorMsg = logoBase64;
        }
        else {
            errorMsg = "Could not convert image data to logo";
		}
        if (errorMsg != null) {
            log.warning("setLogoBase64: " + errorMsg);
        }
        return errorMsg;
	}
	
	public String convertLogoToBase64(byte[] logo) {
		String format = null;
		try {
			format = ImageHelper.checkMagicNumber(logo);
		} catch (ImageHelper.ImageFormatException e) {
			log.log(Level.WARNING, "Not recognized logo format", e);
			return e.getMessage();
		}
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
        Image originalImage = ImagesServiceFactory.makeImage(logo);
		log.info("Original image: " + originalImage.getWidth() + " x " + originalImage.getHeight() + " " + format);
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
        if (newImage.getWidth() != LOGO_WIDTH) {
        	Transform resize = ImagesServiceFactory.makeResize(LOGO_WIDTH, LOGO_HEIGHT);
        	newImage = imagesService.applyTransform(resize, newImage);
    		log.info("Resized image: " + newImage.getWidth() + " x " + newImage.getHeight());
        }
        byte[] newImageData = newImage.getImageData();
		
		String logo64 = Base64.encodeBase64String(newImageData);
		log.info("Data uri for logo has " + logo64.length() + " bytes");
		return "data:" + format + ";base64," + logo64;
	}

	public PictureData convertPicture(byte[] picture) {
		String format = null;
		try {
			format = ImageHelper.checkMagicNumber(picture);
		} catch (ImageHelper.ImageFormatException e) {
            String errorStr = "Image not recognized as JPG, GIF or PNG.";
            PictureData pd = new PictureData(errorStr, null);
			return pd;
		}
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
        Image originalImage = ImagesServiceFactory.makeImage(picture);
		log.info("Original image: " + originalImage.getWidth() + " x " + originalImage.getHeight() + " " + format);
        Image newImage = null;
        if (originalImage.getWidth() > PICTURE_WIDTH || originalImage.getHeight() > PICTURE_HEIGHT) {
        	// we need to shrink image based on horizontal difference
        	Transform resize = ImagesServiceFactory.makeResize(PICTURE_WIDTH, PICTURE_HEIGHT);
        	newImage = imagesService.applyTransform(resize, originalImage);
        } else {
        	newImage = originalImage;
        }
    	log.info("Resized image: " + newImage.getWidth() + " x " + newImage.getHeight());
        byte[] newImageData = newImage.getImageData();
		return new PictureData(format, newImageData);
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
		case PIC1:
			docToDelete = listing.pic1Id;
			listing.pic1Id = null;
			break;
		case PIC2:
			docToDelete = listing.pic2Id;
			listing.pic2Id = null;
			break;
		case PIC3:
			docToDelete = listing.pic3Id;
			listing.pic3Id = null;
			break;
		case PIC4:
			docToDelete = listing.pic4Id;
			listing.pic4Id = null;
			break;
		case PIC5:
			docToDelete = listing.pic5Id;
			listing.pic5Id = null;
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
		return new String[] {df.format(loc.latitude + ((double)new Random().nextInt(100)) * 0.000001),
				df.format(loc.longitude + ((double)new Random().nextInt(100)) * 0.000001)};
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
                ListingDocumentVO doc = fetchAndUpdateListingDoc(listing, prop);
                if (doc != null && doc.getErrorCode() == ErrorCodes.OK) {
                    ListingDoc listingDoc = VoToModelConverter.convert(doc);
                    Listing updatedlisting = updateListingDoc(listing, listingDoc);
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
    
    public void updateMockListingPictures(Listing listing, String imageUrls[]) {
		MockDataBuilder mock = new MockDataBuilder();
		if (imageUrls == null) {
			return;
		}
        try {
        	int index = 1;
            for (String imageUrl : imageUrls) {
            	if (index > 5) {
            		// we support only 5 images
            		break;
            	}
            	ListingPropertyVO prop = new ListingPropertyVO("pic" + index++ + "_url", imageUrl);
                ListingDocumentVO doc = fetchAndUpdateListingDoc(listing, prop);
                if (doc != null && doc.getErrorCode() == ErrorCodes.OK) {
                    ListingDoc listingDoc = VoToModelConverter.convert(doc);
                    Listing updatedlisting = updateListingDoc(listing, listingDoc);
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
            log.log(Level.WARNING, "Exception while updating mock pictures for listing: " + listing, e);
        }
		getDAO().storeListing(listing);
	}
    
    public Pair<BlobKey, Listing.State> getPictureBlob(String listingId, int picNr) {
    	Listing listing = ObjectifyDatastoreDAO.getInstance().getListing(BaseVO.toKeyId(listingId));
		if (listing == null) {
			log.log(Level.INFO, "Listing not found!");
			return null;
		}
		Pair<ListingDoc.Type, Key<ListingDoc>> docKey = getPictureKey(listing, picNr);
		ListingDoc doc = ObjectifyDatastoreDAO.getInstance().getListingDocument(docKey.getRight());
		if (doc.blob != null) {
			return new ImmutablePair<BlobKey, Listing.State>(doc.blob, listing.state);
		} else {
			return null;
		}
    }
    
	public boolean swapPictures(UserVO loggedInUser, String listingId, int picFromNr, int picToNr) {
		if (loggedInUser == null) {
			log.log(Level.INFO, "User not logged in!");
			return false;
		}
		Listing listing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (listing == null) {
			log.log(Level.INFO, "Listing not found!");
			return false;
		}
		if (listing.owner.getId() != loggedInUser.toKeyId()) {
			log.log(Level.INFO, "User is not an owner of the listing");
			return false;
		}
		if (listing.state != Listing.State.NEW) {
			log.log(Level.INFO, "Listing not in edit state.");
			return false;
		}
		if (picFromNr < 1 || picFromNr > 5 || picToNr < 1 || picToNr > 5) {
			log.log(Level.INFO, "Wrong picture number(s), picFromNr=" + picFromNr + ", picToNr=" + picToNr);
			return false;
		}
		Pair<ListingDoc.Type, Key<ListingDoc>> docFromKey = getPictureKey(listing, picFromNr);
		Pair<ListingDoc.Type, Key<ListingDoc>> docToKey = getPictureKey(listing, picToNr);
		ListingDoc docFrom = getDAO().getListingDocument(docFromKey.getRight());
		ListingDoc docTo = getDAO().getListingDocument(docToKey.getRight());
		log.info("Swaping pictures " + docFrom + " <-> " + docTo);
		
		setPictureKey(listing, picToNr, docFrom == null ? null : docFrom.getKey());
		setPictureKey(listing, picFromNr, docTo == null ? null : docTo.getKey());
		if (docFrom != null) {
			docFrom.type = docToKey.getKey();
		}
		if (docTo != null) {
			docTo.type = docFromKey.getKey();
		}
		getDAO().storeListingAndDocs(listing, docFrom, docTo);
		return true;
	}

	private Pair<ListingDoc.Type, Key<ListingDoc>> getPictureKey(Listing listing, int picNr) {
		switch(picNr) {
		case 5:
			return new ImmutablePair<ListingDoc.Type, Key<ListingDoc>>(ListingDoc.Type.PIC5, listing.pic5Id);
		case 4:
			return new ImmutablePair<ListingDoc.Type, Key<ListingDoc>>(ListingDoc.Type.PIC4, listing.pic4Id);
		case 3:
			return new ImmutablePair<ListingDoc.Type, Key<ListingDoc>>(ListingDoc.Type.PIC3, listing.pic3Id);
		case 2:
			return new ImmutablePair<ListingDoc.Type, Key<ListingDoc>>(ListingDoc.Type.PIC2, listing.pic2Id);
		case 1:
		default:
			return new ImmutablePair<ListingDoc.Type, Key<ListingDoc>>(ListingDoc.Type.PIC1, listing.pic1Id);
		}
	}

	private void setPictureKey(Listing listing, int picNr, Key<ListingDoc> docKey) {
		switch(picNr) {
		case 5:
			listing.pic5Id = docKey;
			break;
		case 4:
			listing.pic4Id = docKey;
			break;
		case 3:
			listing.pic3Id = docKey;
			break;
		case 2:
			listing.pic2Id = docKey;
			break;
		case 1:
			listing.pic1Id = docKey;
			break;
		default:
			throw new IllegalArgumentException("Invalid picture index " + picNr);
		}
	}
}
