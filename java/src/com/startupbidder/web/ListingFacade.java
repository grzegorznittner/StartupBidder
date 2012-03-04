package com.startupbidder.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.datanucleus.util.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.utils.SystemProperty;
import com.googlecode.objectify.Key;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Category;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.ListingStats;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.vo.BaseVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingAndUserVO;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.ListingListVO;
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
	public ListingVO createListing(UserVO loggedInUser, ListingVO listingVO) {
		if (loggedInUser == null) {
			log.warning("Only logged in user can create listing");
			return null;
		}
		Listing l = VoToModelConverter.convert(listingVO);
		l.state = Listing.State.NEW;
		l.owner = new Key<SBUser>(loggedInUser.getId());
		
		ListingVO newListing = DtoToVoConverter.convert(getDAO().createListing(l));
		// at that stage listing is not yet active so there is no point of updating statistics
		applyListingData(loggedInUser, newListing);
		return newListing;
	}

	public ListingAndUserVO getListing(UserVO loggedInUser, String listingId) {
		long id = 0;
		try {
			id = BaseVO.toKeyId(listingId);
		} catch (Exception e) {
			log.warning("Invalid key passed");
			return null;
		}
		ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(id));
		if (listing != null) {
			applyListingData(loggedInUser, listing);
			ListingAndUserVO listingAndUser = new ListingAndUserVO();
			listingAndUser.setListing(listing);
			return listingAndUser;
		}
		return null;
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
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production
				&& loggedInUser == null) {
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			blobstoreService.delete(doc.getBlob());
			return null;
		}
		ListingDoc docDTO = VoToModelConverter.convert(doc);
		docDTO.created = new Date();
		return DtoToVoConverter.convert(getDAO().createListingDocument(docDTO));
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
			result.put(cat.id.toString(), cat.name);
		}
		return result;
	}
}
