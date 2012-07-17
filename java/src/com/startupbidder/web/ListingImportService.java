package com.startupbidder.web;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.MarketSession.Callback;
import com.gc.android.market.api.model.Market.App;
import com.gc.android.market.api.model.Market.AppsRequest;
import com.gc.android.market.api.model.Market.AppsResponse;
import com.gc.android.market.api.model.Market.GetImageRequest;
import com.gc.android.market.api.model.Market.GetImageRequest.AppImageUsage;
import com.gc.android.market.api.model.Market.GetImageRequest.Builder;
import com.gc.android.market.api.model.Market.ResponseContext;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.googlecode.objectify.Key;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.PictureImport;
import com.startupbidder.datamodel.SystemProperty;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.util.GetImageResponseCallback;
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.ListingPropertyVO;
import com.startupbidder.vo.UserVO;

public class ListingImportService {
	static final Logger log = Logger.getLogger(ListingImportService.class.getName());
	
	private DateTimeFormatter timeStampFormatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss_SSS");
	private static ListingImportService instance;
	private static Map<String, ImportSource> importMap = new HashMap<String, ImportSource>();
	private static Cache cache;
	
	public static ListingImportService instance() {
		if (instance == null) {
			instance = new ListingImportService();
		}
		return instance;
	}
	
	private ListingImportService() {
		try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
        } catch (CacheException e) {
            log.log(Level.SEVERE, "Cache couldn't be created!!!");
        }
		
		importMap.put("AppStore", new AppStoreImport());
		importMap.put("GooglePlay", new AndroidStoreImport());
		importMap.put("CrunchBase", new CrunchBaseImport());
	}
	
	private static ObjectifyDatastoreDAO getDAO() {
		return ObjectifyDatastoreDAO.getInstance();
	}
	
	private static byte[] fetchBytes(String url) {
		try {
			URLConnection con = new URL(url).openConnection();
			byte[] docBytes = IOUtils.toByteArray(con.getInputStream());
			log.info("Fetched " + docBytes.length + " bytes from " + url);
			return docBytes;
		} catch (Exception e) {
			log.log(Level.WARNING, "Error fetching import source from " + url, e);
			return null;
		}
	}
	
	private static String getJsonNodeValue(JsonNode nodeItem, String name) {
		return nodeItem.get(name) != null ? nodeItem.get(name).getValueAsText() : null;
	}
	
	private static void fetchLogo(Listing listing, String logoUrl) {
		ListingPropertyVO prop = new ListingPropertyVO("logo_url", logoUrl);
		ListingDocumentVO doc = ListingFacade.instance().fetchAndUpdateListingDoc(listing, prop);
        if (doc != null && doc.getErrorCode() == ErrorCodes.OK) {
            ListingDoc listingDoc = VoToModelConverter.convert(doc);
            Key<ListingDoc> replacedDocId = listing.logoId;
            // logo data uri has been stored in fetchAndUpdateListingDoc method call
            listing.logoId = new Key<ListingDoc>(ListingDoc.class, listingDoc.id);
			if (replacedDocId != null) {
				try {
					log.info("Deleting doc previously associated with listing " + replacedDocId);
					ListingDoc docToDelete = getDAO().getListingDocument(replacedDocId.getId());
					BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
					blobstoreService.delete(docToDelete.blob);
					ObjectifyDatastoreDAO.getInstance().deleteDocument(replacedDocId.getId());
				} catch (Exception e) {
					log.log(Level.WARNING, "Error while deleting old document " + replacedDocId + " of listing " + listing.id, e);
				}
			}
        } else {
        	log.warning("Error fetching logo from " + logoUrl);
        }
	}	

	private static void setCredentials(MarketSession session) {
		String user = (String)cache.get(SystemProperty.GOOGLEDOC_USER);
		if (user == null) {
			SystemProperty userProp = ServiceFacade.instance().getDAO().getSystemProperty(SystemProperty.GOOGLEDOC_USER);
			if (userProp != null) {
				user = userProp.value;
				cache.put(SystemProperty.GOOGLEDOC_USER, user);
			}
		}
		String pass = (String)cache.get(SystemProperty.GOOGLEDOC_PASSWORD);
		if (pass == null) {
			SystemProperty passProp = ServiceFacade.instance().getDAO().getSystemProperty(SystemProperty.GOOGLEDOC_PASSWORD);
			if (passProp != null) {
				pass = passProp.value;
				cache.put(SystemProperty.GOOGLEDOC_PASSWORD, pass);
			}
		}
		if (user == null || pass == null) {
			log.severe("Google Doc credentials not set up!");
			return;
		}
		session.login(user, pass);
	}
	
	public static GetImageResponseCallback fetchImageFromGooglePlayStore(String propName, String propValue) {
		String[] tokens = propValue.split("#");
		if (tokens.length == 4 && "android".equals(tokens[0])) {
			// android URI is in format: android#<app id>#<pic or icon>#<pic number>
			String appId = tokens[1];
			String type = tokens[2];
			String number = tokens[3];
			MarketSession session = new MarketSession();
			setCredentials(session);
			Builder builder = GetImageRequest.newBuilder().setAppId(appId);
			if ("pic".equals(type)) {
				builder = builder.setImageUsage(AppImageUsage.SCREENSHOT).setImageId(number);
			} else {
				builder = builder.setImageUsage(AppImageUsage.ICON).setImageId(number);
			}
			GetImageRequest imgReq = builder.build();
			GetImageResponseCallback result = new GetImageResponseCallback(propValue);
			session.append(imgReq, result);
			session.flush();
			return result;
		} else {
			log.warning("URI is not android format: " + propValue);
			return null;
		}
	}

	/**
	 * Returns map of pairs <id, description> which defines list of available import items for given query.
	 */
	public Map<String, String> getImportSuggestions(UserVO loggedInUser, String type, String query) {
		ImportSource importClass = importMap.get(type);
		if (importClass != null) {
			return importClass.getImportSuggestions(loggedInUser, query);
		} else {
			log.warning("Import type '" + type + "' not available!");
			return null;
		}
	}
	
	/**
	 * Fills provided listing object with data obtained from external system.
	 * Provided id must uniquely identify data to import.
	 */
	public Listing importListing(UserVO loggedInUser, String type, Listing listing, String id) {
		ImportSource importClass = importMap.get(type);
		if (importClass != null) {
			return importClass.importListing(loggedInUser, listing, id);
		} else {
			log.warning("Import type '" + type + "' not available!");
			return listing;
		}
	}
	
	public List<String> availableImportTypes(UserVO loggedInUser) {
		return new ArrayList<String>(importMap.keySet());
	}
	
	/**
	 * Defines methods for importing listing data.
	 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
	 */
	static interface ImportSource {
		/**
		 * Returns map of pairs <id, description> which defines list of available import items for given query.
		 */
		Map<String, String> getImportSuggestions(UserVO loggedInUser, String query);

		/**
		 * Fills provided listing object with data obtained from external system.
		 * Provided id must uniquely identify data to import.
		 */
		Listing importListing(UserVO loggedInUser, Listing listing, String id);
	}
	
	static class AndroidStoreImport implements ImportSource {
		@Override
		public Map<String, String> getImportSuggestions(UserVO loggedInUser, final String query) {
			MarketSession session = new MarketSession();
			setCredentials(session);

			AppsRequest appsRequest = AppsRequest.newBuilder().setQuery(query)
					.setStartIndex(0).setEntriesCount(5)
					.setWithExtendedInfo(true).build();
			final Map<String, String> result = new LinkedHashMap<String, String>();
			log.info("Searching Android Market for " + query);
			session.append(appsRequest, new Callback<AppsResponse>() {
				@Override
				public void onResult(ResponseContext context, AppsResponse response) {
					log.info("Query for '" + query + "' returned " + response.getAppList().size());
					for (App app : response.getAppList()) {
						result.put(app.getId(), app.getTitle() + " by " + app.getCreator()
										+ " version " + app.getVersion());
					}
				}
			});
			session.flush();

			return result;
		}

		@Override
		public Listing importListing(UserVO loggedInUser, final Listing listing, final String id) {
			MarketSession session = new MarketSession();
			setCredentials(session);

			AppsRequest appsRequest = AppsRequest.newBuilder().setAppId(id)
					.setWithExtendedInfo(true).build();
			log.info("Searching Android Market for application " + id);
			session.append(appsRequest, new Callback<AppsResponse>() {
				@Override
				public void onResult(ResponseContext context, AppsResponse response) {
					log.info("Query for '" + id + "' returned " + response.getAppList().size());
					App app = response.getApp(0);

					listing.name = app.getTitle();
					listing.founders = app.getCreator();
					listing.type = Listing.Type.APPLICATION;
					listing.category = "Software";
					listing.platform = Listing.Platform.ANDROID.toString();
					listing.summary = app.getExtendedInfo().getDescription();
					listing.mantra = StringUtils.left(app.getExtendedInfo().getPromoText(), 100);
					listing.website = app.getExtendedInfo().getContactWebsite();
					listing.videoUrl = app.getExtendedInfo().getPromotionalVideo();
					listing.notes += "Imported from GooglePlay id=" + id
							+ ", creator=" + listing.founders
							+ ", creatorId=" + app.getCreatorId()
							+ ", version=" + app.getVersion() + "\n";
					fetchLogo(listing, id);
					if (app.getExtendedInfo().hasScreenshotsCount() && app.getExtendedInfo().getScreenshotsCount() > 0) {
						List<PictureImport> picImportList = new ArrayList<PictureImport>();
						int count = app.getExtendedInfo().getScreenshotsCount();
						for (int screenshot = 1; screenshot <= count; screenshot++) {
							PictureImport pic = new PictureImport();
							pic.listing = listing.getKey();
							pic.url = "android#" + id + "#pic#" + screenshot;
							picImportList.add(pic);
							log.info("Scheduling picture to import from " + pic.url);
						}
						getDAO().storePictureImports(picImportList.toArray(new PictureImport[]{}));
						
						NotificationFacade.instance().schedulePictureImport(listing, 1);
					}
				}
			});
			session.flush();
			return listing;
		}
		
		private void fetchLogo(Listing listing, String appId) {
			ListingPropertyVO prop = new ListingPropertyVO("logo_url", "android#" + appId + "#logo#1");
			ListingDocumentVO doc = ListingFacade.instance().fetchAndUpdateListingDoc(listing, prop);
            if (doc != null && doc.getErrorCode() == ErrorCodes.OK) {
                ListingDoc listingDoc = VoToModelConverter.convert(doc);
                Key<ListingDoc> replacedDocId = listing.logoId;
                // logo data uri has been stored in fetchAndUpdateListingDoc method call
                listing.logoId = new Key<ListingDoc>(ListingDoc.class, listingDoc.id);
				if (replacedDocId != null) {
					try {
						log.info("Deleting doc previously associated with listing " + replacedDocId);
						ListingDoc docToDelete = getDAO().getListingDocument(replacedDocId.getId());
						BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
						blobstoreService.delete(docToDelete.blob);
						ObjectifyDatastoreDAO.getInstance().deleteDocument(replacedDocId.getId());
					} catch (Exception e) {
						log.log(Level.WARNING, "Error while deleting old document " + replacedDocId + " of listing " + listing.id, e);
					}
				}
            } else {
            	log.warning("Error fetching logo from " + prop.getPropertyValue());
            }
		}
	}
	
	static class AppStoreImport implements ImportSource {
		String prepareQueryString(String query) {
			StringBuffer appStoreQuery = new StringBuffer();
			appStoreQuery.append("term=");
			StringTokenizer tokenizer = new StringTokenizer(query);
			boolean tokenAdded = false;
			for (String token; tokenizer.hasMoreTokens();) {
				token = tokenizer.nextToken();
				if (!(token.contains("=") || token.contains("&") || token.contains("?"))) {
					if (tokenAdded) {
						appStoreQuery.append("+");
					}
					appStoreQuery.append(token);
					tokenAdded = true;
				}
			}
			appStoreQuery.append("&entity=software");
			return "http://itunes.apple.com/search?" + appStoreQuery.toString();
		}

		@Override
		public Map<String, String> getImportSuggestions(UserVO loggedInUser, String query) {
			String queryString = prepareQueryString(query);
			byte[] response = fetchBytes(queryString);
			if (response == null) {
				return null;
			}
			try {
				int numberOfResults = -1;
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readValue(response, JsonNode.class);
				if (rootNode.get("resultCount") != null) {
					numberOfResults = rootNode.get("resultCount").getValueAsInt(-1);
					log.info("Fetched " + numberOfResults + " items from " + queryString);
				}
				if (rootNode.get("results") != null) {
					Map<String, String> result = new LinkedHashMap<String, String>();
					Iterator<JsonNode> nodeIt = rootNode.get("results").getElements();
					for (JsonNode nodeItem; nodeIt.hasNext();) {
						nodeItem = nodeIt.next();
						String trackId = getJsonNodeValue(nodeItem, "trackId");
						String trackName = getJsonNodeValue(nodeItem, "trackName");
						String artistName = getJsonNodeValue(nodeItem, "artistName");
						String version = getJsonNodeValue(nodeItem, "version");
						String releaseDate = getJsonNodeValue(nodeItem, "releaseDate");
						String releaseNotes = getJsonNodeValue(nodeItem, "releaseNotes");
						
						if (trackId != null && trackName != null && artistName != null) {
							StringBuffer desc = new StringBuffer();
							if (version != null) {
								desc.append(trackName + " version " + version + " by " + artistName);
							}
							if (releaseDate != null) {
								desc.append(" released " + releaseDate);
							}
							if (releaseNotes != null) {
								desc.append(" with notes '" + releaseNotes + "'");
							}
							result.put(trackId, desc.toString());
						}
					}
					return result;
				} else {
					log.warning("Attribute 'results' not present in the response");
					return null;
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading AppStore response", e);
				return null;
			}
		}

		@Override
		public Listing importListing(UserVO loggedInUser, Listing listing, String id) {
			String queryString = "http://itunes.apple.com/lookup?id=" + id;
			byte[] response = fetchBytes(queryString);
			if (response == null) {
				return listing;
			}
			try {
				int numberOfResults = -1;
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readValue(response, JsonNode.class);
				if (rootNode.get("resultCount") != null) {
					numberOfResults = rootNode.get("resultCount").getValueAsInt(-1);
					log.info("Fetched " + numberOfResults + " items from " + queryString);
				}
				if (rootNode.get("results") != null) {
					Iterator<JsonNode> nodeIt = rootNode.get("results").getElements();
					JsonNode nodeItem = nodeIt.next();
					String trackId = getJsonNodeValue(nodeItem, "trackId");
					
					listing.name = getJsonNodeValue(nodeItem, "trackName");
					listing.founders = getJsonNodeValue(nodeItem, "artistName");
					listing.type = Listing.Type.APPLICATION;
					listing.category = "Software";
					listing.platform = Listing.Platform.IOS.toString();
					listing.summary = getJsonNodeValue(nodeItem, "description");
					listing.mantra = extractMantra(listing.summary);
					listing.website = getJsonNodeValue(nodeItem, "sellerUrl");
					
					JsonNode logoNode = nodeItem.get("artworkUrl512");
					if (logoNode == null) {
						logoNode = nodeItem.get("artworkUrl100");
						if (logoNode == null) {
							logoNode = nodeItem.get("artworkUrl60");
						}
					}
					if (logoNode != null) {
						fetchLogo(listing, logoNode.getValueAsText());
					}
					fetchImages(listing, nodeItem.get("screenshotUrls"), nodeItem.get("ipadScreenshotUrls"));
					
					listing.notes += "Imported from AppStore url=" + queryString + ", trackName=" + listing.name
							+ ", artistName=" + listing.founders
							+ ", artistViewUrl=" + getJsonNodeValue(nodeItem, "artistViewUrl")
							+ ", version=" + getJsonNodeValue(nodeItem, "version")
							+ ", releaseDate=" + getJsonNodeValue(nodeItem, "releaseDate") + "\n";
				} else {
					log.warning("Attribute 'results' not present in the response");
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading AppStore response", e);
			}
			return listing;
		}
		
		void fetchImages(Listing listing, JsonNode screenshotNode, JsonNode ipadScreenshotNode) {
			List<String> urls = new ArrayList<String>();

			if (ipadScreenshotNode != null) {
				Iterator<JsonNode> elemIter = ipadScreenshotNode.getElements();
				for (; urls.size() < 5 && elemIter.hasNext(); ) {
					urls.add(elemIter.next().getTextValue());
				}
			}
			if (screenshotNode != null) {
				Iterator<JsonNode> elemIter = screenshotNode.getElements();
				for (; urls.size() < 5 && elemIter.hasNext(); ) {
					urls.add(elemIter.next().getTextValue());
				}
			}

			if (urls.size() > 0) {
				List<PictureImport> picImportList = new ArrayList<PictureImport>();
				for (String url : urls) {
					log.info("Scheduling picture to import from " + url);
					PictureImport pic = new PictureImport();
					pic.listing = listing.getKey();
					pic.url = url;
					picImportList.add(pic);
				}
				getDAO().storePictureImports(picImportList.toArray(new PictureImport[]{}));
				
				NotificationFacade.instance().schedulePictureImport(listing, 1);
			}
		}

		String extractMantra(String description) {
			StringBuffer mantra = new StringBuffer();
			for (String sentence : description.split("\\.")) {
				if (mantra.length() + sentence.length() < 100) {
					mantra.append(sentence);
				} else if (mantra.length() < 10) {
					mantra.append(sentence.substring(0, sentence.length() < 100 ? sentence.length() : 99));
					break;
				} else {
					break;
				}
			}
			return mantra.toString();
		}
	}
	
	static class CrunchBaseImport implements ImportSource {
		String prepareQueryString(String query) {
			StringBuffer crunchbaseQuery = new StringBuffer();
			crunchbaseQuery.append("query=");
			StringTokenizer tokenizer = new StringTokenizer(query);
			boolean tokenAdded = false;
			for (String token; tokenizer.hasMoreTokens();) {
				token = tokenizer.nextToken();
				if (!(token.contains("=") || token.contains("&") || token.contains("?"))) {
					if (tokenAdded) {
						crunchbaseQuery.append("+");
					}
					crunchbaseQuery.append(token);
					tokenAdded = true;
				}
			}
			return "http://api.crunchbase.com/v/1/search.js?" + crunchbaseQuery.toString();
		}

		@Override
		public Map<String, String> getImportSuggestions(UserVO loggedInUser, String query) {
			String queryString = prepareQueryString(query);
			byte[] response = fetchBytes(queryString);
			if (response == null) {
				return null;
			}
			try {
				int numberOfResults = -1;
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readValue(response, JsonNode.class);
				if (rootNode.get("total") != null) {
					numberOfResults = rootNode.get("total").getValueAsInt(-1);
					log.info("Fetched " + numberOfResults + " items from " + queryString);
				}
				if (rootNode.get("results") != null) {
					Map<String, String> result = new LinkedHashMap<String, String>();
					Iterator<JsonNode> nodeIt = rootNode.get("results").getElements();
					for (JsonNode nodeItem; nodeIt.hasNext();) {
						nodeItem = nodeIt.next();
						String trackId = getJsonNodeValue(nodeItem, "permalink");
						String trackName = getJsonNodeValue(nodeItem, "name");
						String overview = getJsonNodeValue(nodeItem, "overview");
						
						if (trackId != null && trackName != null) {
							StringBuffer desc = new StringBuffer();
							desc.append(trackName);
							if (StringUtils.isNotEmpty(overview)) {
								desc.append(" - ").append(StringUtils.left(overview, 50)).append("...");
							}
							result.put(trackId, desc.toString());
						}
					}
					return result;
				} else {
					log.warning("Attribute 'results' not present in the response");
					return null;
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading CrunchBase response", e);
				return null;
			}
		}

		@Override
		public Listing importListing(UserVO loggedInUser, Listing listing, String id) {
			String queryString = "http://api.crunchbase.com/v/1/company/" + id + ".js";
			byte[] response = fetchBytes(queryString);
			if (response == null) {
				return listing;
			}
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readValue(response, JsonNode.class);
				if (rootNode.has("permalink")) {
					listing.name = getJsonNodeValue(rootNode, "name");
					listing.founders = getFunders(rootNode.get("relationships"));
					listing.type = Listing.Type.COMPANY;
					listing.category = "Software";
					listing.platform = Listing.Platform.IOS.toString();
					listing.summary = getJsonNodeValue(rootNode, "overview");
					listing.mantra = getJsonNodeValue(rootNode, "description");
					listing.website = getJsonNodeValue(rootNode, "homepage_url");
					
					JsonNode logoNode = rootNode.get("image");
					if (logoNode != null && logoNode.has("available_sizes")) {
						JsonNode sizes = logoNode.get("available_sizes");
						JsonNode largestSize = sizes.get(sizes.size() - 1);
						fetchLogo(listing, "http://www.crunchbase.com/" + largestSize.get(1).getValueAsText());
					}
					fetchImages(listing, rootNode.get("screenshots"));
					
					listing.notes += "Imported from CrunchBase url=" + queryString + ", name=" + listing.name
							+ ", founders=" + listing.founders
							+ ", homepage_url=" + getJsonNodeValue(rootNode, "artistViewUrl")
							+ ", description=" + listing.mantra + "\n";
				} else {
					log.warning("Attribute 'permalink' not present in the response");
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading CrunchBase response", e);
			}
			return listing;
		}
		
		String getFunders(JsonNode relationships) {
			if (relationships == null) {
				return null;
			}
			StringBuffer buf = new StringBuffer();
			Iterator<JsonNode> it = relationships.getElements();
			for (JsonNode relation; it.hasNext(); ) {
				relation = it.next();
				if (relation.has("title") && StringUtils.contains(relation.get("title").getValueAsText(), "Founder")) {
					JsonNode person = relation.get("person");
					if (person != null && person.has("first_name") && person.has("last_name")) {
						if (buf.length() > 0) {
							buf.append(", ");
						}
						buf.append(getJsonNodeValue(person, "first_name")).append(" ").append(getJsonNodeValue(person, "last_name"));
					}
				}
			}
			return buf.toString();
		}
		
		void fetchImages(Listing listing, JsonNode screenshotNode) {
			List<String> urls = new ArrayList<String>();

			if (screenshotNode != null) {
				Iterator<JsonNode> elemIter = screenshotNode.getElements();
				for (; urls.size() < 5 && elemIter.hasNext(); ) {
					JsonNode screenshot = elemIter.next();
					if (screenshot.has("available_sizes")) {
						JsonNode sizes = screenshot.get("available_sizes");
						JsonNode largestSize = sizes.get(sizes.size() - 1);
						
						urls.add("http://www.crunchbase.com/" + largestSize.get(1).getValueAsText());
					}
				}
			}

			if (urls.size() > 0) {
				List<PictureImport> picImportList = new ArrayList<PictureImport>();
				for (String url : urls) {
					log.info("Scheduling picture to import from " + url);
					PictureImport pic = new PictureImport();
					pic.listing = listing.getKey();
					pic.url = url;
					picImportList.add(pic);
				}
				getDAO().storePictureImports(picImportList.toArray(new PictureImport[]{}));
				
				NotificationFacade.instance().schedulePictureImport(listing, 1);
			}
		}
	}
}
