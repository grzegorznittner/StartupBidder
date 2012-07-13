package com.startupbidder.web;

import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.startupbidder.datamodel.Listing;
import com.startupbidder.vo.UserVO;

public class ListingImportService {
	private static final Logger log = Logger.getLogger(ListingImportService.class.getName());
	
	private DateTimeFormatter timeStampFormatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss_SSS");
	private static ListingImportService instance;
	
	public static ListingImportService instance() {
		if (instance == null) {
			instance = new ListingImportService();
		}
		return instance;
	}
	
	/**
	 * Returns map of pairs <id, description> which defines list of available import items for given query.
	 */
	public Map<String, String> getImportSuggestions(UserVO loggedInUser, String type, String query) {
		return new AppStoreImport().getImportSuggestions(loggedInUser, query);
	}
	
	/**
	 * Fills provided listing object with data obtained from external system.
	 * Provided id must uniquely identify data to import.
	 */
	public Listing importListing(UserVO loggedInUser, String type, Listing listing, String id) {
		return new AppStoreImport().importListing(loggedInUser, listing, id);
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

		@Override
		public Map<String, String> getImportSuggestions(UserVO loggedInUser, String query) {
			String queryString = "http://itunes.apple.com/search?term=football&entity=software";
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
					Map<String, String> result = new LinkedHashMap<String, String>();
					Iterator<JsonNode> nodeIt = rootNode.get("results").getElements();
					JsonNode nodeItem = nodeIt.next();
					String trackId = getJsonNodeValue(nodeItem, "trackId");
					
					listing.name = getJsonNodeValue(nodeItem, "trackName");
					listing.founders = getJsonNodeValue(nodeItem, "artistName");
					listing.type = Listing.Type.APPLICATION;
					listing.platform = Listing.Platform.IOS.toString();
					
					listing.notes += "Imported from AppStore url=" + queryString + ", trackName=" + listing.name
							+ "artistName=" + listing.founders + ", version=" + getJsonNodeValue(nodeItem, "version")
							+ "releaseDate=" + getJsonNodeValue(nodeItem, "releaseDate") + "\n";
				} else {
					log.warning("Attribute 'results' not present in the response");
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading AppStore response", e);
			}
			return listing;
		}
		
	}
}
