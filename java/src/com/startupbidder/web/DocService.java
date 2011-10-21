package com.startupbidder.web;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gdata.client.DocumentQuery;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.DocumentEntry;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.docs.FolderEntry;
import com.google.gdata.data.media.MediaByteArraySource;
import com.google.gdata.util.AuthenticationException;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.SystemPropertyDTO;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.UserListVO;
import com.startupbidder.vo.UserVO;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class DocService {
	private static final Logger log = Logger.getLogger(DocService.class.getName());
	public static enum Folder {SUMMARY, BUSINESS_PLAN, FINANCIAL, PRESENTATION};

	private Cache cache;
	private static DocService instance = null;
	
	public static DocService instance() {
		if (instance == null) {
			instance = new DocService();
		}
		return instance;
	}
	
	private DocService() {
		try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
        } catch (CacheException e) {
            log.log(Level.SEVERE, "Cache couldn't be created!!!");
        }
	}
	
	private DocsService getDocsService() {
		String user = (String)cache.get(SystemPropertyDTO.GOOGLEDOC_USER);
		if (user == null) {
			SystemPropertyDTO userProp = ServiceFacade.instance().getDAO().getSystemProperty(SystemPropertyDTO.GOOGLEDOC_USER);
			user = userProp.getValue();
			cache.put(SystemPropertyDTO.GOOGLEDOC_USER, user);
		}
		String pass = (String)cache.get(SystemPropertyDTO.GOOGLEDOC_PASSWORD);
		if (pass == null) {
			SystemPropertyDTO passProp = ServiceFacade.instance().getDAO().getSystemProperty(SystemPropertyDTO.GOOGLEDOC_PASSWORD);
			pass = passProp.getValue();
			cache.put(SystemPropertyDTO.GOOGLEDOC_PASSWORD, pass);
		}
		DocsService client = new DocsService("www-startupbidder-v1");
		try {
			client.setUserCredentials(user, pass);
		} catch (AuthenticationException e) {
			log.log(Level.SEVERE, "Error while logging to GoogleDoc!", e);
			return null;
		}
		return client;
	}
	
	public void updateListingData(ListingVO listing) {
		DocsService client = getDocsService();
		if (client == null) {
			return;
		}

		String summaryId = getFolder(client, Folder.SUMMARY);
		if (summaryId == null) {
			log.severe("Cannot get folder " + Folder.SUMMARY);
		}
		try {
			DocumentListEntry newDocument = getDocument(client, summaryId, listing.getId());
			if (newDocument == null) {
				newDocument = new DocumentEntry();
				newDocument.setTitle(new PlainTextConstruct(listing.getId()));
				// Prevent collaborators from sharing the document with others?
				newDocument.setWritersCanInvite(false);
				client.insert(new URL("https://docs.google.com/feeds/default/private/full/" + summaryId + "/contents"), newDocument);
			}
			byte[] bytes = (listing.getOwnerName() + " " + listing.getName() + " " + listing.getSummary()).getBytes();
			newDocument.setEtag("*");
			newDocument.setMediaSource(new MediaByteArraySource(bytes,
					DocumentListEntry.MediaType.TXT.getMimeType()));
			newDocument.updateMedia(true);
		}
		catch (Exception e) {
			log.log(Level.WARNING, "Error while creating document for listing '" + listing.getId() + "'", e);
		}
	}
	
	public int updateListingData(List<ListingDTO> listings) {
		DocsService client = getDocsService();
		if (client == null) {
			return 0;
		}

		deleteFolder(client, Folder.SUMMARY);
		String summaryId = getFolder(client, Folder.SUMMARY);
		if (summaryId == null) {
			log.severe("Cannot get folder " + Folder.SUMMARY);
			return 0;
		}
		UserListVO userList = ServiceFacade.instance().getAllUsers(null);
		Map<String, UserVO> users = new HashMap<String, UserVO>();
		for (UserVO user : userList.getUsers()) {
			users.put(user.getId(), user);
		}
		
		int updatedDocs = 0;
		for (ListingDTO listing : listings) {
			try {
				DocumentListEntry newDocument = new DocumentEntry();
				newDocument.setTitle(new PlainTextConstruct(listing.getIdAsString()));
				// Prevent collaborators from sharing the document with others?
				newDocument.setWritersCanInvite(false);
				newDocument = client.insert(new URL("https://docs.google.com/feeds/default/private/full/" + summaryId + "/contents"), newDocument);

				UserVO user = users.get(listing.getOwner());
				byte[] bytes = (user.getName() + " " + listing.getName() + " " + listing.getSummary()).getBytes();
				newDocument.setEtag("*");
				newDocument.setMediaSource(new MediaByteArraySource(bytes,
						DocumentListEntry.MediaType.TXT.getMimeType()));
				newDocument.updateMedia(true);
				updatedDocs++;
			} catch (Exception e) {
				log.log(Level.WARNING, "Error while creating document for listing '" + listing.getIdAsString() + "'", e);
			}
		}
		return updatedDocs;
	}
	
	public List<String> fullTextSearch(String searchText) {
		List<String> list = new ArrayList<String>();
		
		DocsService client = getDocsService();
		if (client == null) {
			return list;
		}

		try {
			URL feedUri = new URL("https://docs.google.com/feeds/default/private/full/");
			DocumentQuery query = new DocumentQuery(feedUri);
			query.setFullTextQuery(searchText);
			DocumentListFeed feed = client.getFeed(query, DocumentListFeed.class);
			for (DocumentListEntry entry : feed.getEntries()) {
				list.add(entry.getTitle().getPlainText());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Error while searching docs", e);
		}
		return list;
	}

	public void createFolders() {
		DocsService client = getDocsService();
		if (client == null) {
			return;
		}
		String resourceId = getFolder(client, Folder.SUMMARY);
		log.info("Created folder '" + Folder.SUMMARY + "' with resourceID=" + resourceId);
		resourceId = getFolder(client, Folder.BUSINESS_PLAN);
		log.info("Created folder '" + Folder.BUSINESS_PLAN + "' with resourceID=" + resourceId);
		resourceId = getFolder(client, Folder.PRESENTATION);
		log.info("Created folder '" + Folder.PRESENTATION + "' with resourceID=" + resourceId);
		resourceId = getFolder(client, Folder.FINANCIAL);
		log.info("Created folder '" + Folder.FINANCIAL + "' with resourceID=" + resourceId);
	}
		
	private DocumentListEntry getDocument(DocsService client, String folderResourceId, String name) {
		try {
			URL feedUri = new URL("https://docs.google.com/feeds/default/private/full/"  + folderResourceId + "/contents");
			DocumentQuery query = new DocumentQuery(feedUri);
			query.setTitleExact(true);
			query.setTitleQuery(name);
			DocumentListFeed feed = client.getFeed(query, DocumentListFeed.class);
			for (DocumentListEntry entry : feed.getEntries()) {
				return entry;
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Error while getting doc '" + folderResourceId + "/" + name + "'", e);
		}
		return null;
	}
	
	private String getFolder(DocsService client, Folder folderName) {
		try {
			URL feedUri = new URL("https://docs.google.com/feeds/default/private/full/");
			DocumentQuery query = new DocumentQuery(feedUri);
			query.setTitleExact(true);
			query.setTitleQuery(folderName.name());
			query.setStringCustomParameter("showfolders", "true");
			DocumentListFeed feed = client.getFeed(query, DocumentListFeed.class);
			for (DocumentListEntry entry : feed.getEntries()) {
				if (entry.getResourceId().startsWith("folder")) {
					log.info("Folder '" + folderName + "' already exists.");
					return entry.getResourceId();
				}
			}
			// folder doesn't exist, needs to be created
			DocumentListEntry folder = new FolderEntry();
			folder.setTitle(new PlainTextConstruct(folderName.name()));
			folder = client.insert(feedUri, folder);
			return folder.getResourceId();
		} catch (Exception e) {
			log.log(Level.WARNING, "Error while creating folder '" + folderName + "'", e);
			return null;
		}
	}
	
	private String deleteFolder(DocsService client, Folder folderName) {
		try {
			URL feedUri = new URL("https://docs.google.com/feeds/default/private/full/");
			DocumentQuery query = new DocumentQuery(feedUri);
			query.setTitleExact(true);
			query.setTitleQuery(folderName.name());
			query.setStringCustomParameter("showfolders", "true");
			DocumentListFeed feed = client.getFeed(query, DocumentListFeed.class);
			for (DocumentListEntry entry : feed.getEntries()) {
				if (entry.getResourceId().startsWith("folder")) {
					client.delete(new URL("https://docs.google.com/feeds/default/private/full/" + entry.getResourceId()), "*");
					log.info("Folder '" + folderName + "' deleted.");
					return entry.getResourceId();
				}
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Error while creating folder '" + folderName + "'", e);
		}
		return null;
	}
	
	public List<ListingDocumentVO> getAllDocuments() {
		List<ListingDocumentVO> list = new ArrayList<ListingDocumentVO>();
		
		DocsService client = getDocsService();
		if (client == null) {
			return list;
		}
		DocumentListFeed feed = null;
		try {
			URL feedUri = new URL("https://docs.google.com/feeds/default/private/full/");
			DocumentQuery query = new DocumentQuery(feedUri);
			//query.setTitleExact(true);
			//query.setTitleQuery(SUMMARY);
			//query.setStringCustomParameter("showfolders", "true");

			feed = client.getFeed(query, DocumentListFeed.class);
			for (DocumentListEntry entry : feed.getEntries()) {
				ListingDocumentVO doc = new ListingDocumentVO();
				doc.setId(entry.getTitle().getPlainText());
				doc.setState(entry.getResourceId());
				doc.setCreated(new Date(entry.getEdited().getValue()));
				log.info(ToStringBuilder.reflectionToString(entry));
				
				list.add(doc);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while fetching GoogeDoc feed!", e);
		}
		return list;
	}
}