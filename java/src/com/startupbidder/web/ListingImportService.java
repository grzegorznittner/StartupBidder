package com.startupbidder.web;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.googlecode.objectify.Key;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.PictureImport;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.util.HtmlConverter;
import com.startupbidder.util.ImageHelper;
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.ImportQueryResultsVO;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.ListingPropertyVO;
import com.startupbidder.vo.UserVO;

public class ListingImportService {
	static final Logger log = Logger.getLogger(ListingImportService.class.getName());
	
	private static final int MAX_RESULTS = 20;
	
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
		importMap.put("Startuply", new StartuplyImport());
		importMap.put("Angelco", new AngelCoImport());
		importMap.put("ChromeWebStore", new ChromeWebStoreImport());
	}
	
	private static ObjectifyDatastoreDAO getDAO() {
		return ObjectifyDatastoreDAO.getInstance();
	}
	
	private static byte[] fetchBytes(String url) {
		return fetchBytes(url, null);
	}
	
	private static byte[] fetchBytes(String url, String userAgent) {
		try {
			URLConnection con = new URL(url).openConnection();
			if (userAgent != null) {
				((HttpURLConnection)con).addRequestProperty("User-Agent", userAgent);
			}
			byte[] docBytes = IOUtils.toByteArray(con.getInputStream());
			log.info("Fetched " + docBytes.length + " bytes from " + url);
			return docBytes;
		} catch (Exception e) {
			log.log(Level.WARNING, "Error fetching import source from " + url, e);
			return null;
		}
	}
	
	private static String getJsonNodeValue(JsonNode nodeItem, String name) {
		return nodeItem.get(name) != null ? nodeItem.get(name).getValueAsText() : "";
	}
	
	private static Element getRootElement(byte[] response)
			throws ParserConfigurationException, SAXException, IOException, UnsupportedEncodingException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String pid, String sid) throws SAXException {
				return new InputSource(new StringReader(""));
			}
		});
		String responseText = new String(response, "UTF-8");
		responseText = responseText.substring(responseText.indexOf("<"));
		Document dom = db.parse(IOUtils.toInputStream(responseText));
		return dom.getDocumentElement();
	}
	
	private static Element getFirstElement(Element entry, String name) {
		NodeList nl = entry.getElementsByTagName(name);
		if (nl.getLength() > 0) {
			return (Element)nl.item(0);
		} else {
			return null;
		}
	}
	
	private static String getText(Element parent, String elementName) {
		Element elem = getFirstElement(parent, elementName);
		if (elem != null) {
			return elem.getTextContent();
		} else {
			return null;
		}
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
	
	private static void fillMantraAndSummary(Listing listing, String mantra, String summary) {
		String textMantra = HtmlConverter.convertHtmlToText(mantra);
		String textSummary = HtmlConverter.convertHtmlToText(summary);
		
		// replacing exotic quotation marks with standard ones
		textMantra = StringUtils.replaceChars(textMantra, '\u2018', '\'');
		textMantra = StringUtils.replaceChars(textMantra, '\u2019', '\'');
		textMantra = StringUtils.replaceChars(textMantra, '\u201c', '"');
		textMantra = StringUtils.replaceChars(textMantra, '\u201d', '"');
		textSummary = StringUtils.replaceChars(textSummary, '\u2018', '\'');
		textSummary = StringUtils.replaceChars(textSummary, '\u2019', '\'');
		textSummary = StringUtils.replaceChars(textSummary, '\u201c', '"');
		textSummary = StringUtils.replaceChars(textSummary, '\u201d', '"');
		
		textSummary = StringUtils.trim(textSummary);
		if (StringUtils.isNotBlank(textMantra)) {
			listing.mantra = extractMantra(textMantra.trim());
		} else {
			listing.mantra = extractMantra(textSummary);
		}
		listing.summary = textSummary;
		if (listing.mantra == null) {
			listing.mantra = "";
		}
		if (listing.summary == null) {
			listing.summary = "";
		}
	}

	private static String extractMantra(String description) {
		StringBuffer mantra = new StringBuffer();
		String sentence = null;
		int index = 0;
		while (index >= 0) {
			index = StringUtils.indexOfAny(description, '.', '!', '?');
			if (index >= 0) {
				sentence = description.substring(0, index + 1);
				description = description.substring(index + 2 > description.length() ? description.length() : index + 2);
			} else {
				sentence = description;
			}
			log.info(sentence + "  len=" + sentence.length() + ", total=" + (sentence.length() + mantra.length()));
			if (mantra.length() + sentence.length() < 100) {
				mantra.append(sentence);
			} else if (mantra.length() < 15) {
				for (String word : sentence.split(" ")) {
					if (mantra.length() + word.length() > 100) {
						if (!StringUtils.isAlphanumeric(StringUtils.rightPad(word, 1))) {
							word = StringUtils.substring(word, 0, word.length() - 1);
						}
						if (mantra.length() > 0) {
							mantra.append(" ");
						}
						mantra.append(word).append(" ...");
						break;
					}
					if (mantra.length() > 0) {
						mantra.append(" ");
					}
					mantra.append(word);
				}
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
				switch (picImportList.size()) {
				case 0:
					pic.previousDoc = listing.pic1Id;
					listing.pic1Id = new Key<ListingDoc>(ListingDoc.class, 666);
					break;
				case 1:
					pic.previousDoc = listing.pic2Id;
					listing.pic2Id = new Key<ListingDoc>(ListingDoc.class, 666);
					break;
				case 2:
					pic.previousDoc = listing.pic3Id;
					listing.pic3Id = new Key<ListingDoc>(ListingDoc.class, 666);
					break;
				case 3:
					pic.previousDoc = listing.pic4Id;
					listing.pic4Id = new Key<ListingDoc>(ListingDoc.class, 666);
					break;
				case 4:
					pic.previousDoc = listing.pic5Id;
					listing.pic5Id = new Key<ListingDoc>(ListingDoc.class, 666);
					break;
				}
				picImportList.add(pic);
			}
			getDAO().storePictureImports(picImportList.toArray(new PictureImport[]{}));
		}
	}

	
	/**
	 * Returns map of pairs <id, description> which defines list of available import items for given query.
	 */
	public ImportQueryResultsVO getImportSuggestions(UserVO loggedInUser, String type, String query) {
		ImportQueryResultsVO results = new ImportQueryResultsVO();
		ImportSource importClass = importMap.get(type);
		if (importClass != null) {
			results.setQueryResults(importClass.getImportSuggestions(loggedInUser, query));
		} else {
			log.warning("Import type '" + type + "' not available!");
			results.setErrorCode(ErrorCodes.APPLICATION_ERROR);
			results.setErrorMessage("Import type '" + type + "' not available!");
		}
		return results;
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
				
				Source source = new Source(new StringReader(converted));
				List<net.htmlparser.jericho.Element> elemList = source.getAllElements(HTMLElementName.LI);
				int index = 1;
				Map<String, String> result = new LinkedHashMap<String, String>();
				for (net.htmlparser.jericho.Element elem : elemList) {
					if (index > MAX_RESULTS) {
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
				log.log(Level.WARNING, "Error parsing/loading GooglePlay response", e);
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
				log.info("First lines of HTML: " + StringUtils.substring(converted, 0, 200));
				
				listing.type = Listing.Type.APPLICATION;
				listing.category = "Software";
				listing.platform = Listing.Platform.ANDROID.toString();
	
				Source source = new Source(new StringReader(converted));
				for (net.htmlparser.jericho.Element tag : source.getAllElements("class", Pattern.compile("doc-banner-title"))) {
					if (tag.getName().equalsIgnoreCase("h1")) {
						listing.name = tag.getContent().toString().trim();
						log.info("Name bytes: " + ImageHelper.printStringAsHex(listing.name));
					}
				}
				for (net.htmlparser.jericho.Element tag : source.getAllElements("class", Pattern.compile("doc-header-link"))) {
					if (tag.getName().equalsIgnoreCase("a")) {
						listing.founders = tag.getContent().toString().trim();
					}
				}
				net.htmlparser.jericho.Element descTag = source.getElementById("doc-original-text");
				if (descTag != null) {
					fillMantraAndSummary(listing, null, descTag.getContent().toString().trim());
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
				log.log(Level.WARNING, "Error parsing/loading GooglePlay response", e);
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
						if (result.size() > MAX_RESULTS) {
							break;
						}
						nodeItem = nodeIt.next();
						String trackId = getJsonNodeValue(nodeItem, "trackId");
						String trackName = getJsonNodeValue(nodeItem, "trackName");
						String artistName = getJsonNodeValue(nodeItem, "artistName");
						String version = getJsonNodeValue(nodeItem, "version");
						String releaseDate = getJsonNodeValue(nodeItem, "releaseDate");
						
						if (StringUtils.isNotBlank(trackId) && StringUtils.isNotBlank(trackName)
								&& StringUtils.isNotBlank(artistName)) {
							StringBuffer desc = new StringBuffer();
							if (StringUtils.isNotBlank(version)) {
								desc.append(trackName + " version " + version);
							}
							if (StringUtils.isNotBlank(artistName)) {
								desc.append(" by " + artistName);
							}
							if (StringUtils.isNotBlank(releaseDate)) {
								releaseDate = StringUtils.replace(releaseDate, "T", " ");
								releaseDate = StringUtils.replace(releaseDate, "Z", " ");
								desc.append(" released " + releaseDate);
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
					fillMantraAndSummary(listing, null, getJsonNodeValue(nodeItem, "description"));
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
					if (result.size() > MAX_RESULTS) {
						break;
					}
					Element entry = (Element)nl.item(index);
					String id = getText(entry, "a:id");
					String title = getText(entry, "a:title");
					String released = getText(entry, "releaseDate");
					Element publisher = getFirstElement(entry, "publisher");
					if (publisher == null) {
						continue;
					}
					String publisherName = getText(publisher, "name");
					log.info("id: " + id + ". Title: " + title + ". Released: " + released
							+ ". Publisher: " + publisherName);
					
					if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(title)
							&& StringUtils.isNotBlank(released) && StringUtils.isNotBlank(publisherName)) {
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
				fillMantraAndSummary(listing, null, getText(rootElem, "a:content"));
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
				JsonNode rootNode = mapper.readValue(new String(response, "UTF-8"), JsonNode.class);
				if (rootNode.get("total") != null) {
					numberOfResults = rootNode.get("total").getValueAsInt(-1);
					log.info("Fetched " + numberOfResults + " items from " + queryString);
				}
				if (rootNode.get("results") != null) {
					Map<String, String> result = new LinkedHashMap<String, String>();
					Iterator<JsonNode> nodeIt = rootNode.get("results").getElements();
					for (JsonNode nodeItem; nodeIt.hasNext();) {
						if (result.size() > MAX_RESULTS) {
							break;
						}
						nodeItem = nodeIt.next();
						String trackId = getJsonNodeValue(nodeItem, "permalink");
						String trackName = getJsonNodeValue(nodeItem, "name");
						String overview = getJsonNodeValue(nodeItem, "overview");
						
						if (StringUtils.isNotBlank(trackId) && !StringUtils.equalsIgnoreCase(trackId, "null")
								&& StringUtils.isNotBlank(trackName) && !StringUtils.equalsIgnoreCase(trackName, "null")) {
							StringBuffer desc = new StringBuffer();
							desc.append(trackName);
							if (StringUtils.isNotBlank(overview) && !StringUtils.equalsIgnoreCase(overview, "null")) {
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
				JsonNode rootNode = mapper.readValue(new String(response, "UTF-8"), JsonNode.class);
				if (rootNode.has("permalink")) {
					listing.name = getJsonNodeValue(rootNode, "name");
					listing.founders = getFunders(rootNode.get("relationships"));
					listing.type = Listing.Type.COMPANY;
					listing.platform = Listing.Platform.OTHER.toString();
					listing.category = "Software";
					listing.platform = Listing.Platform.IOS.toString();
					fillMantraAndSummary(listing, getJsonNodeValue(rootNode, "description"),
							getJsonNodeValue(rootNode, "overview"));
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
	
	static class StartuplyImport implements ImportSource {
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
			return "http://www.startuply.com/Startups/Default.aspx?s=" + appStoreQuery.toString();
		}
		
		@Override
		public Map<String, String> getImportSuggestions(UserVO loggedInUser, final String query) {
			try {
				byte bytes[] = fetchBytes(prepareQueryString(query));
				String converted = removeTag(new String(bytes, "UTF-8"), "style");
				converted = removeTag(converted, "script");
				
				Source source = new Source(new StringReader(converted));
				Map<String, String> result = new LinkedHashMap<String, String>();
				
				net.htmlparser.jericho.Element resultsPanel = source.getElementById("resultPanel");
				List<net.htmlparser.jericho.Element> links = resultsPanel.getAllElements("href", Pattern.compile("/Companies/.*aspx"));
				int index = 1;
				for (net.htmlparser.jericho.Element elem : links) {
					if (index > MAX_RESULTS) {
						break;
					}
					String docId = elem.getAttributeValue("href");
					if (!StringUtils.isEmpty(docId)) {
						index++;
						String id = docId.substring(docId.lastIndexOf("/") + 1, docId.lastIndexOf("."));
						String name = elem.getContent().toString().trim();
						if (name != null) {
							result.put(id, name);
						}
					}
				}
				return result;
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading Startuply response", e);
				return null;
			}
		}

		@Override
		public Listing importListing(UserVO loggedInUser, final Listing listing, final String id) {
			log.info("Importing from Startuply " + id);
			String appUrl = "http://www.startuply.com/Companies/" + id + ".aspx";
			try {
				byte bytes[] = fetchBytes(appUrl);
				String converted = removeTag(new String(bytes, "UTF-8"), "style");
				converted = removeTag(converted, "script");
				
				listing.type = Listing.Type.COMPANY;
				listing.platform = Listing.Platform.OTHER.toString();
				listing.category = "Software";
				listing.platform = null;
	
				Source source = new Source(new StringReader(converted));

				net.htmlparser.jericho.Element name = source.getElementById("companyNameHeader");
				if (name != null) {
					listing.name = name.getContent().toString().trim();
				}
				net.htmlparser.jericho.Element contentTag = source.getElementById("ContentPanel");
				if (contentTag != null) {
					net.htmlparser.jericho.Element tableTag = contentTag.getFirstElement("table");
					List<net.htmlparser.jericho.Element> nameTds = tableTag.getAllElements("td");
					net.htmlparser.jericho.Element wwwTd = nameTds.get(1);
					net.htmlparser.jericho.Element wwwTag = wwwTd.getFirstElement("a");
					listing.website = wwwTag.getAttributeValue("href");
					
					net.htmlparser.jericho.Element firstH1Tag = null;
					for (net.htmlparser.jericho.Element tag : contentTag.getAllElements("h1")) {
						if (StringUtils.contains(tag.getContent().toString().toLowerCase(), "mission")) {
							firstH1Tag = tag;
							break;
						}
					}
					String mantra = null;
					String description = null;
					String team = null;
					if (firstH1Tag != null) {
						net.htmlparser.jericho.Element descTag = source.getEnclosingElement(firstH1Tag.getBegin() - 20);
						String groupName = null;
						for (net.htmlparser.jericho.Element tag : descTag.getChildElements()) {
							if (StringUtils.equalsIgnoreCase(tag.getName(), "h1")) {
								groupName = tag.getContent().toString().trim();
							} else {
								if (StringUtils.containsIgnoreCase(groupName, "mission")) {
									mantra = tag.getContent().toString().trim();
								} else if (StringUtils.containsIgnoreCase(groupName, "our products")) {
									description = tag.getContent().toString().trim();
								} else if (StringUtils.containsIgnoreCase(groupName, "our team")) {
									team = tag.getContent().toString().trim();
								}
							}
						}
						if (description == null) {
							description = mantra;
							mantra = extractMantra(description);
						} else {
							mantra = extractMantra(mantra);
						}
					}
					fillMantraAndSummary(listing, mantra, description);
					// listing.answer10 = team; not sure which question is about team
				}
				/*
				net.htmlparser.jericho.Element descTag = source.getElementById("branchTable");
				if (descTag != null && StringUtils.equalsIgnoreCase(descTag.getName(), "table")) {
					for (net.htmlparser.jericho.Element tag : descTag.getAllElements("tr")) {
						List<net.htmlparser.jericho.Element> addressTds = tag.getAllElements("td");
						if (addressTds.size() >= 2) {
							if (StringUtils.equalsIgnoreCase(addressTds.get(0).getContent().toString().trim(), "Headquarters")) {
								listing.address = addressTds.get(1).getContent().toString().trim();
							}
						}
					}
				}
				*/
				for (net.htmlparser.jericho.Element tag : source.getAllElements("src", Pattern.compile("/UserUploads/CompanyLogo/.*"))) {
					if (tag.getName().equalsIgnoreCase("img")) {
						fetchLogo(listing, "http://www.startuply.com" + tag.getAttributeValue("src"));
					}
				}
				List<String> urls = new ArrayList<String>();
				net.htmlparser.jericho.Element photoPanel = source.getElementById("photoPanel");
				if (photoPanel != null) {
					for (net.htmlparser.jericho.Element tag : photoPanel.getAllElements("src", Pattern.compile("../UserUploads/PhotoStorage/.*"))) {
						if (tag.getName().equalsIgnoreCase("img")) {
							String src = tag.getAttributeValue("src");
							if (src.startsWith("..")) {
								src = src.substring(2);
							}
							src = "http://www.startuply.com" + src.replaceAll("_thumb", "");
							urls.add(src);
						}
					}
				}				
				schedulePictureImport(listing, urls);
				
				listing.website = appUrl;
				listing.notes += "Imported from Startuply " + appUrl
						+ " on " + timeStampFormatter.print(new Date().getTime()) + "\n";
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading Startuply response", e);
			}
			return listing;
		}
	}

	static class AngelCoImport implements ImportSource {
		String prepareQueryString(String query) {
			StringBuffer appStoreQuery = new StringBuffer();
			appStoreQuery.append("query=");
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
			appStoreQuery.append("&type=Startup");
			return "https://api.angel.co/1/search?" + appStoreQuery.toString();
		}

		@Override
		public Map<String, String> getImportSuggestions(UserVO loggedInUser, String query) {
			String queryString = prepareQueryString(query);
			byte[] response = fetchBytes(queryString);
			if (response == null) {
				return null;
			}
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readValue(response, JsonNode.class);
				Map<String, String> result = new LinkedHashMap<String, String>();
				
				Iterator<JsonNode> nodeIt = rootNode.getElements();
				for (JsonNode nodeItem = null; nodeIt.hasNext();) {
					if (result.size() > MAX_RESULTS) {
						break;
					}
					nodeItem = nodeIt.next();
					String id = getJsonNodeValue(nodeItem, "id");
					String name = getJsonNodeValue(nodeItem, "name");
					
					if (id != null && name != null) {
						result.put(id, name);
					}
				}
				return result;
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading AngelCo response", e);
				return null;
			}
		}

		@Override
		public Listing importListing(UserVO loggedInUser, Listing listing, String id) {
			String queryString = "http://api.angel.co/1/startups/" + id;
			byte[] response = fetchBytes(queryString);
			if (response == null) {
				return listing;
			}
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readValue(response, JsonNode.class);
				if (rootNode != null) {
					listing.name = getJsonNodeValue(rootNode, "name");
					listing.type = Listing.Type.COMPANY;
					listing.platform = Listing.Platform.OTHER.toString();
					listing.category = "Software";
					fillMantraAndSummary(listing, getJsonNodeValue(rootNode, "high_concept"),
							getJsonNodeValue(rootNode, "product_desc"));					
					listing.website = getJsonNodeValue(rootNode, "company_url");
					listing.videoUrl = getJsonNodeValue(rootNode, "video_url");
					if (StringUtils.isEmpty(listing.videoUrl)) {
						listing.videoUrl = null;
					}

					String logoUrl = getJsonNodeValue(rootNode, "logo_url");
					if (logoUrl != null) {
						fetchLogo(listing, logoUrl);
					}
					fetchImages(listing, rootNode.get("screenshots"));
					
					listing.notes += "Imported from AngelCo url=" + queryString + ", name=" + listing.name
							+ " on " + timeStampFormatter.print(new Date().getTime()) + "\n";
				} else {
					log.warning("No JSON response");
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading AngelCo response", e);
			}
			return listing;
		}
		
		void fetchImages(Listing listing, JsonNode screenshotNode) {
			List<String> urls = new ArrayList<String>();

			if (screenshotNode != null) {
				Iterator<JsonNode> elemIter = screenshotNode.getElements();
				for (String url = null; elemIter.hasNext(); ) {
					url = getJsonNodeValue(elemIter.next(), "original");
					if (url != null && url.startsWith("http")) {
						urls.add(url);
					}
				}
			}
			schedulePictureImport(listing, urls);
		}
	}	

	static class ChromeWebStoreImport implements ImportSource {
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
			return "http://www.google.com/search?hl=en&ie=UTF-8&q=site%3Achrome.google.com+" + appStoreQuery.toString();
		}
		
		@Override
		public Map<String, String> getImportSuggestions(UserVO loggedInUser, final String query) {
			try {
				byte bytes[] = fetchBytes(prepareQueryString(query), "Nokia6021");
				
				Map<String, String> result = new LinkedHashMap<String, String>();
				
				Element rootElem = getRootElement(bytes);
				Element resultsNode = null;
				NodeList nl = rootElem.getElementsByTagName("div");
				for (int i = 0; i < nl.getLength(); i++) {
					Element node = (Element)nl.item(i);
					if (StringUtils.equals(node.getAttribute("id"), "universal")) {
						resultsNode = (Element)node;
					}
				}
				if (resultsNode == null) {
					log.info("No results");
					return result;
				}
				nl = resultsNode.getChildNodes();
				log.info("Got " + nl.getLength() + " results");
				for (int i = 0; i < nl.getLength(); i++) {
					if (result.size() > MAX_RESULTS) {
						break;
					}
					Node item = nl.item(i);
					if (StringUtils.equals(item.getNodeName(), "div")) {
						Element elem = (Element)item;
						if (StringUtils.equals(elem.getAttribute("class"), "web_result")) {
							NodeList anchors = elem.getElementsByTagName("a");
							if (anchors != null && anchors.getLength() > 0) {
								Element anchor = (Element)anchors.item(0);
								String href = anchor.getAttribute("href");
								
								int start = href.indexOf("https");
								int end = href.indexOf("&", start);
								String id = StringUtils.substring(href, start, end);
								id = StringUtils.substring(id, id.lastIndexOf("/") + 1);
								end = id.indexOf("%");
								if (end >= 0) {
									id = id.substring(0, end);
								}
								String name = anchor.getTextContent();
								if (name != null) {
									name = name.replace("Chrome Web Store - ", "");
									result.put(id, name);
								}
							}
						}
					}
				}
				return result;
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading Google response from Chrome Webstore", e);
				return null;
			}
		}

		@Override
		public Listing importListing(UserVO loggedInUser, final Listing listing, final String id) {
			log.info("Importing from Chrome WebStore " + id);
			String appUrl = "https://chrome.google.com/webstore/detail/" + id;
			try {
				byte bytes[] = fetchBytes(appUrl);
				String converted = removeTag(new String(bytes, "UTF-8"), "style");
				converted = removeTag(converted, "script");
				
				listing.type = Listing.Type.APPLICATION;
				listing.platform = Listing.Platform.DESKTOP.toString();
				listing.category = "Software";
				listing.website = appUrl;
	
				Source source = new Source(new StringReader(converted));

				net.htmlparser.jericho.Element name = source.getFirstElement("itemprop", "name", true);
				if (name != null) {
					listing.name = name.getContent().toString().trim();
				}
				net.htmlparser.jericho.Element mantra = source.getFirstElement("itemprop", "description", true);
				if (mantra != null) {
					listing.mantra = mantra.getContent().toString().trim();
				}
				net.htmlparser.jericho.Element overviewBar = source.getFirstElement("class", "overview-tab-right-bar-info", true);
				if (overviewBar != null) {
					net.htmlparser.jericho.Element desc = overviewBar.getFirstElement("pre");
					if (desc != null) {
						listing.summary = desc.getContent().toString().trim();
					}
				}
				fillMantraAndSummary(listing, listing.mantra, listing.summary);

				net.htmlparser.jericho.Element image = source.getFirstElement("itemprop", "image", true);
				if (image != null) {
					fetchLogo(listing, image.getAttributeValue("src"));
				}
				
				List<String> urls = new ArrayList<String>();
				List<net.htmlparser.jericho.Element> photos = source.getAllElements("slideindex", Pattern.compile(".*"));
				for (net.htmlparser.jericho.Element tag : photos) {
					net.htmlparser.jericho.Element imgTag = tag.getFirstElement("img");
					if (imgTag != null) {
						String src = imgTag.getAttributeValue("src");
						urls.add(src);
					}
				}
				schedulePictureImport(listing, urls);
				
				listing.notes += "Imported from Chrome WebStore " + appUrl
						+ " on " + timeStampFormatter.print(new Date().getTime()) + "\n";
			} catch (Exception e) {
				log.log(Level.WARNING, "Error parsing/loading Chrome WebStore response", e);
			}
			return listing;
		}
	}

}
