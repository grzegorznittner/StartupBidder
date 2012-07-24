package com.startupbidder.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
	static final Logger log = Logger.getLogger(ListingImportService.class.getName());
	
	private static DateTimeFormatter timeStampFormatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss_SSS");
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
		importMap.put("GooglePlay", new AndroidStoreImport());
		importMap.put("WindowsMarketplace", new WindowsMarketplaceImport());
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
	
	private static String removeTag(String text, String tag) {
		String tagStart = "<" + tag;
		String tagEnd = "</" + tag + ">";
		String converted = "";
		int search = text.indexOf(tagStart);
		while (search > 0) {
			converted += text.substring(0, search);
			int last = text.indexOf(tagEnd, search + 1);
			text = text.substring(last + tagEnd.length());
			log.info("Removed " + (last - search) + " bytes");
			search = text.indexOf(tagStart);
		}
		converted += text;
		return converted;
	}

	private static String extractMantra(String description) {
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
	
	private static void schedulePictureImport(Listing listing, List<String> urls) {
		if (urls.size() > 0) {
			List<PictureImport> picImportList = new ArrayList<PictureImport>();
			for (String url : urls) {
				if (picImportList.size() == 5) {
					break;
				}
				log.info("Scheduling picture to import from " + url);
				PictureImport pic = new PictureImport();
				pic.listing = listing.getKey();
				pic.url = url;
				picImportList.add(pic);
			}
			getDAO().storePictureImports(picImportList.toArray(new PictureImport[]{}));
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
		String prepareQueryString(String query) {
			StringBuffer appStoreQuery = new StringBuffer();
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
			return "https://play.google.com/store/search?c=apps&hl=en&q=" + appStoreQuery.toString();
		}
		
		@Override
		public Map<String, String> getImportSuggestions(UserVO loggedInUser, final String query) {
			try {
				byte bytes[] = fetchBytes(prepareQueryString(query));
				String converted = removeTag(new String(bytes, "UTF-8"), "style");
				converted = removeTag(converted, "script");
				
				Source source = new Source(IOUtils.toInputStream(converted));
				List<net.htmlparser.jericho.Element> elemList = source.getAllElements(HTMLElementName.LI);
				int index = 1;
				Map<String, String> result = new LinkedHashMap<String, String>();
				for (net.htmlparser.jericho.Element elem : elemList) {
					if (index > 20) {
						break;
					}
					String docId = elem.getAttributeValue("data-docid");
					if (!StringUtils.isEmpty(docId)) {
						index++;
						String name = null;
						String author = null;
						List<net.htmlparser.jericho.Element> aList = elem.getAllElements(HTMLElementName.A);
						for (net.htmlparser.jericho.Element aElem : aList) {
							String classAttr = aElem.getAttributeValue("class");
							if (StringUtils.equals(classAttr, "title")) {
								name = aElem.getAttributeValue("title");
							} else if (StringUtils.equals(classAttr, "goog-inline-block")) {
								author = aElem.getContent().toString().trim();
							}
						}
						if (name != null && author != null) {
							result.put(docId, "'" + name + "' by " + author);
						}
					}
				}
				return result;
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading AppStore response", e);
				return null;
			}
		}

		@Override
		public Listing importListing(UserVO loggedInUser, final Listing listing, final String id) {
			log.info("Searching Android Market for application " + id);
			String appUrl = "https://play.google.com/store/apps/details?feature=search_result&hl=en&id=" + id;
			try {
				byte bytes[] = fetchBytes(appUrl);
				String converted = removeTag(new String(bytes, "UTF-8"), "style");
				converted = removeTag(converted, "script");
				
				listing.type = Listing.Type.APPLICATION;
				listing.category = "Software";
				listing.platform = Listing.Platform.ANDROID.toString();
	
				Source source = new Source(IOUtils.toInputStream(converted));
				for (net.htmlparser.jericho.Element tag : source.getAllElements("class", Pattern.compile("doc-banner-title"))) {
					if (tag.getName().equalsIgnoreCase("h1")) {
						listing.name = tag.getContent().toString().trim();
					}
				}
				for (net.htmlparser.jericho.Element tag : source.getAllElements("class", Pattern.compile("doc-header-link"))) {
					if (tag.getName().equalsIgnoreCase("a")) {
						listing.founders = tag.getContent().toString().trim();
					}
				}
				net.htmlparser.jericho.Element descTag = source.getElementById("doc-original-text");
				if (descTag != null) {
					listing.summary = descTag.getContent().toString().trim();
					listing.mantra = extractMantra(listing.summary);
				}
				net.htmlparser.jericho.Element categoryTag = source.getFirstElement("href", Pattern.compile("/store/apps/category.*"));
				if (categoryTag != null) {
					log.info("Category: " + categoryTag.getContent().toString().trim()
							+ ", id: " + categoryTag.getAttributeValue("href"));
				}
				for (net.htmlparser.jericho.Element tag : source.getAllElements("class", Pattern.compile("doc-banner-icon"))) {
					if (tag.getName().equalsIgnoreCase("div")) {
						net.htmlparser.jericho.Element img = tag.getFirstElement("img");
						if (img != null && img.getAttributeValue("src") != null) {
							fetchLogo(listing, img.getAttributeValue("src"));
						}
					}
				}
				
				List<String> urls = new ArrayList<String>();
				net.htmlparser.jericho.Element bannerTag = source.getFirstElement("class", Pattern.compile("doc-banner-image-container"));
				if (bannerTag != null) {
					net.htmlparser.jericho.Element bannerImgTag = bannerTag.getFirstElement("img");
					if (bannerImgTag != null) {
						String src = bannerImgTag.getAttributeValue("src");
						if (src != null && src.startsWith("http")) {
							urls.add(src);
						}
					}
				}
				for (net.htmlparser.jericho.Element tag : source.getAllElements("itemprop", Pattern.compile("screenshots"))) {
					if (tag.getName().equalsIgnoreCase("img")) {
						String src = tag.getAttributeValue("src");
						if (src != null && src.startsWith("http")) {
							urls.add(src);
						}
					}
				}
				schedulePictureImport(listing, urls);
				
				listing.website = appUrl;
				listing.notes += "Imported from GooglePlay " + appUrl
						+ ", creator=" + listing.founders
						+ " on " + timeStampFormatter.print(new Date().getTime()) + "\n";
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading AppStore response", e);
			}
			return listing;
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
							+ ", releaseDate=" + getJsonNodeValue(nodeItem, "releaseDate")
							+ " on " + timeStampFormatter.print(new Date().getTime()) + "\n";
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

			schedulePictureImport(listing, urls);
		}
	}
	
	static class WindowsMarketplaceImport implements ImportSource {
		String prepareQueryString(String query) {
			StringBuffer marketplaceQuery = new StringBuffer();
			StringTokenizer tokenizer = new StringTokenizer(query);
			boolean tokenAdded = false;
			for (String token; tokenizer.hasMoreTokens();) {
				token = tokenizer.nextToken();
				if (!(token.contains("=") || token.contains("&") || token.contains("?"))) {
					if (tokenAdded) {
						marketplaceQuery.append("%20");
					}
					marketplaceQuery.append(token);
					tokenAdded = true;
				}
			}
			return "http://catalog.zune.net/v3.2/en-US/apps?clientType=WinMobile%207.1&store=zest&q="
				+ marketplaceQuery.toString();
		}
		
		@Override
		public Map<String, String> getImportSuggestions(UserVO loggedInUser, String query) {
			Map<String, String> result = new LinkedHashMap<String, String>();
			String queryString = prepareQueryString(query);
			log.info("Calling " + queryString);
			
			byte[] response = fetchBytes(queryString);
			
			try {
				Element rootElem = getRootElement(response);
				NodeList nl = rootElem.getElementsByTagName("a:entry");
				log.info("Received " + nl.getLength() + " entries in result");
				for (int index = 0; index < nl.getLength(); index++) {
					Element entry = (Element)nl.item(index);
					String id = getText(entry, "a:id");
					String title = getText(entry, "a:title");
					String released = getText(entry, "releaseDate");
					Element publisher = getFirstElement(entry, "publisher");
					log.info("id: " + id + ". Title: " + title + ". Released: " + released
							+ ". Publisher: " + publisher);
					
					if (id != null && title != null && released != null && publisher != null) {
						String publisherName = getText(publisher, "name");
						id = trimId(id);
						result.put(id, "'" + title + "' by " + publisherName + " on " + released);
						log.info(id + ": " + result.get(id));
					}
				}
				
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading Windows Marketplace response", e);
				return null;
			}
			return result;
		}

		@Override
		public Listing importListing(UserVO loggedInUser, Listing listing, String id) {
			String queryString = "http://catalog.zune.net/v3.2/en-US/apps/" + id
				+ "/?version=latest&clientType=CLIENT_TYPE&store=Zest&client";
			log.info("Calling " + queryString);			
			byte[] response = fetchBytes(queryString);
			
			try {
				Element rootElem = getRootElement(response);
				
				listing.name = getText(rootElem, "a:title");
				listing.founders = getText(rootElem, "publisher");
				listing.type = Listing.Type.APPLICATION;
				listing.category = "Software";
				listing.platform = Listing.Platform.WINDOWS_PHONE.toString();
				listing.summary = getText(rootElem, "a:content");
				listing.mantra = extractMantra(listing.summary);
				listing.website = "http://www.windowsphone.com/en-US/apps/" + id;
				
				Element logoNode = getFirstElement(rootElem, "image");
				if (logoNode != null) {
					String logoId = getText(logoNode, "id");
					fetchLogo(listing, "http://image.catalog.zune.net/v3.2/en-US/image/"
						+ trimId(logoId) + "?width=240&height=240");
				}
				
				Element picsNode = getFirstElement(rootElem, "screenshots");
				if (picsNode != null) {
					List<String> urls = new ArrayList<String>();
					
					NodeList pics = picsNode.getElementsByTagName("screenshot");
					for (int index = 0; index < pics.getLength(); index++) {
						Element screenshot = (Element)pics.item(index);
						String screenshotId = getText(screenshot, "id");
						if (!StringUtils.isEmpty(screenshotId)) {
							urls.add("http://image.catalog.zune.net/v3.2/en-US/image/"
								+ trimId(screenshotId) + "?width=480&height=480");
						}
					}
					
					schedulePictureImport(listing, urls);
				}
				
				listing.notes += "Imported from WindowsMarketplace " + queryString
					 	+ ", name=" + listing.name
						+ ", founders=" + listing.founders
						+ ", description=" + listing.mantra
						+ " on " + timeStampFormatter.print(new Date().getTime()) + "\n";
				return listing;
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading Windows Marketplace response", e);
				return null;
			}
		}

		private String trimId(String id) {
			if (id.startsWith("urn:uuid:")) {
				id = id.substring(9);
			}
			return id;
		}

		private Element getRootElement(byte[] response)
				throws ParserConfigurationException, SAXException, IOException, UnsupportedEncodingException {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			String responseText = new String(response, "UTF-8");
			responseText = responseText.substring(responseText.indexOf("<"));
			Document dom = db.parse(IOUtils.toInputStream(responseText));
			return dom.getDocumentElement();
		}
		
		Element getFirstElement(Element entry, String name) {
			NodeList nl = entry.getElementsByTagName(name);
			if (nl.getLength() > 0) {
				return (Element)nl.item(0);
			} else {
				return null;
			}
		}
		
		String getText(Element parent, String elementName) {
			Element elem = getFirstElement(parent, elementName);
			if (elem != null) {
				return elem.getTextContent();
			} else {
				return null;
			}
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
							+ ", description=" + listing.mantra
							+ " on " + timeStampFormatter.print(new Date().getTime()) + "\n";
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

			schedulePictureImport(listing, urls);
		}

	}

	public static void main(String args[]) throws FileNotFoundException, IOException {
		byte bytes[] = fetchBytes("https://play.google.com/store/search?c=apps&hl=en&q=kids+quiz");
		String result = new String(bytes, "UTF-8");
		String converted = removeTag(result, "style");
		converted = removeTag(converted, "script");		
		converted = converted.replace(">", ">\n");
		
		Source source = new Source(IOUtils.toInputStream(converted));
		List<net.htmlparser.jericho.Element> elemList = source.getAllElements(HTMLElementName.LI);
		int index = 1;
		for (net.htmlparser.jericho.Element elem : elemList) {
			String docId = elem.getAttributeValue("data-docid");
			if (!StringUtils.isEmpty(docId)) {
				System.out.println("" + index + ". " + docId);
				index++;
				List<net.htmlparser.jericho.Element> aList = elem.getAllElements(HTMLElementName.A);
				for (net.htmlparser.jericho.Element aElem : aList) {
					String classAttr = aElem.getAttributeValue("class");
					if (StringUtils.equals(classAttr, "thumbnail")) {
						System.out.println("    link: " + aElem.getAttributeValue("href"));
					} else if (StringUtils.equals(classAttr, "title")) {
						System.out.println("    name: " + aElem.getAttributeValue("title"));
					} else if (StringUtils.equals(classAttr, "goog-inline-block")) {
						System.out.println("    author: " + aElem.getContent().toString().trim());
					}
				}
			}
		}
		
		bytes = fetchBytes("https://play.google.com/store/apps/details?feature=search_result&hl=en&id=mpem.info.lite");
		converted = removeTag(new String(bytes, "UTF-8"), "style");
		converted = removeTag(converted, "script");
		converted = converted.replace(">", ">\n");
		
		source = new Source(IOUtils.toInputStream(converted));
		for (net.htmlparser.jericho.Element tag : source.getAllElements("class", Pattern.compile("doc-banner-title"))) {
			if (tag.getName().equalsIgnoreCase("h1")) {
				System.out.println("    name: " + tag.getContent().toString().trim());
			}
		}
		for (net.htmlparser.jericho.Element tag : source.getAllElements("class", Pattern.compile("doc-header-link"))) {
			if (tag.getName().equalsIgnoreCase("a")) {
				System.out.println("    author: " + tag.getContent().toString().trim());
			}
		}
		net.htmlparser.jericho.Element descTag = source.getElementById("doc-original-text");
		if (descTag != null) {
			System.out.println("    description: " + descTag.getContent().toString().trim());
		}
		net.htmlparser.jericho.Element categoryTag = source.getFirstElement("href", Pattern.compile("/store/apps/category.*"));
		if (categoryTag != null) {
			System.out.println("    category: " + categoryTag.getContent().toString().trim());
			System.out.println("    category id: " + categoryTag.getAttributeValue("href"));
		}
		for (net.htmlparser.jericho.Element tag : source.getAllElements("class", Pattern.compile("doc-banner-icon"))) {
			if (tag.getName().equalsIgnoreCase("div")) {
				net.htmlparser.jericho.Element img = tag.getFirstElement("img");
				if (img != null && img.getAttributeValue("src") != null) {
					System.out.println("    logo url: " + img.getAttributeValue("src"));
				}
			}
		}
		for (net.htmlparser.jericho.Element tag : source.getAllElements("itemprop", Pattern.compile("screenshots"))) {
			if (tag.getName().equalsIgnoreCase("img")) {
				System.out.println("    image url: " + tag.getAttributeValue("src"));
			}
		}
		
		//IOUtils.write(converted, new FileOutputStream("e://projects//startupbidder//google-play-game.txt"));
	}
}
