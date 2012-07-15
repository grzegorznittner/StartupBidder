package com.startupbidder.web;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.googlecode.objectify.Key;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.PictureImport;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.ListingPropertyVO;
import com.startupbidder.vo.UserVO;

public class ListingImportService {
	private static final Logger log = Logger.getLogger(ListingImportService.class.getName());
	
	private DateTimeFormatter timeStampFormatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss_SSS");
	private static ListingImportService instance;
	private static Map<String, ImportSource> importMap = new HashMap<String, ImportSource>();
	
	public static ListingImportService instance() {
		if (instance == null) {
			instance = new ListingImportService();
		}
		return instance;
	}
	
	private ListingImportService() {
		importMap.put("AppStore", new AppStoreImport());
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
					fetchImages(listing, logoNode, nodeItem.get("screenshotUrls"), nodeItem.get("ipadScreenshotUrls"));
					
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
		
		void fetchImages(Listing listing, JsonNode logoNode, JsonNode screenshotNode, JsonNode ipadScreenshotNode) {
			if (logoNode != null) {
				ListingPropertyVO prop = new ListingPropertyVO("logo_url", logoNode.getValueAsText());
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
	            	log.warning("Error fetching logo from " + logoNode.getValueAsText());
	            }
			} else {
				log.info("Logo node not available");
			}
			
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
}
