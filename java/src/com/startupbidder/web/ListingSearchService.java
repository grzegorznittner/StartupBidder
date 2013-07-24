package com.startupbidder.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.GeoPoint;
import com.google.appengine.api.search.GetRequest;
import com.google.appengine.api.search.GetResponse;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.SortOptions;
import com.google.appengine.api.search.StatusCode;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.web.ListingFacade.UpdateReason;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class ListingSearchService {
	private static final Logger log = Logger.getLogger(ListingSearchService.class.getName());
	public static enum Folder {SUMMARY, BUSINESS_PLAN, FINANCIAL, PRESENTATION};
	
	private static final String MAIN_INDEX = "listing_index";

	private static ListingSearchService instance = null;
	
	public static ListingSearchService instance() {
		if (instance == null) {
			instance = new ListingSearchService();
		}
		return instance;
	}
	
	private ListingSearchService() {
	}
	
	private Index getIndex() {
		IndexSpec indexSpec = IndexSpec.newBuilder().setName(MAIN_INDEX).build();
		return SearchServiceFactory.getSearchService().getIndex(indexSpec);
	}
	
	public void updateListingData(Listing listing, UpdateReason reason) {
		Document doc = getDocForListing(listing);
		try {
			getIndex().put(doc);
		} catch(PutException adde) {
			log.log(Level.INFO, "Error adding document to search index", adde);
			if (StatusCode.TRANSIENT_ERROR.equals(adde.getOperationResult().getCode())) {
				log.info("Retrying adding search document");
		        // retry adding document
				try {
					getIndex().put(doc);
				} catch (Exception e) {
					log.log(Level.WARNING, "Error while creating document for listing '" + listing.id + "'", e);
				}
		    }
		} catch (Exception e) {
			log.log(Level.WARNING, "Error while creating document for listing '" + listing.id + "'", e);
		}
	}

	private Document getDocForListing(Listing listing) {
		GeoPoint geoPoint = new GeoPoint(listing.latitude, listing.longitude);
		Document doc = Document.newBuilder()
				.setId("" + listing.id)
				.addField(Field.newBuilder().setName("name").setText(listing.name))
			    .addField(Field.newBuilder().setName("status").setText(listing.state.toString().toLowerCase()))
			    .addField(Field.newBuilder().setName("location").setGeoPoint(geoPoint))
			    .addField(Field.newBuilder().setName("category").setText(listing.category))
			    .addField(Field.newBuilder().setName("content").setText(DtoToVoConverter.convert(listing).dataForSearch()))
			    .build();
		return doc;
	}

	public int updateAllListingsData(List<Listing> listings) {
		try {
		    while (true) {
		        List<String> docIds = new ArrayList<String>();
		        // Return a set of document IDs.
		        GetRequest request = GetRequest.newBuilder().setReturningIdsOnly(true).build();
		        GetResponse<Document> response = getIndex().getRange(request);
		        if (response.getResults().isEmpty()) {
		            break;
		        }
		        for (Document doc : response) {
		            docIds.add(doc.getId());
		        }
		        getIndex().delete(docIds);
		    }
		} catch (RuntimeException e) {
		    log.log(Level.SEVERE, "Failed to remove documents", e);
		}
		
		List<Document> docs = new ArrayList<Document>();
		for (Listing listing : listings) {
			try {
				Document doc = getDocForListing(listing);
				docs.add(doc);
			} catch (Exception e) {
				log.log(Level.WARNING, "Error while creating search doc for " + listing, e);
			}
		}
		
		try {
			getIndex().put(docs);
			return docs.size();
		} catch(PutException adde) {
			log.log(Level.INFO, "Error adding document to search index", adde);
			if (StatusCode.TRANSIENT_ERROR.equals(adde.getOperationResult().getCode())) {
				log.info("Retrying adding search document");
		        // retry adding document
				try {
					getIndex().put(docs);
					return docs.size();
				} catch (Exception e) {
					log.log(Level.WARNING, "Error while updating search index for documents", e);
				}
		    }
			return 0;
		} catch (Exception e) {
			log.log(Level.WARNING, "Error while updating search index for documents", e);
			return 0;
		}
	}

	public List<Long> fullTextSearch(String searchText, ListPropertiesVO listingProperties) {
		List<Long> list = new ArrayList<Long>();
		
		if (StringUtils.isEmpty(searchText)) {
			return list;
		}
		
		try {
			SortOptions sortOptions = SortOptions.newBuilder()
		            .setLimit(1000)
		            .build();
			QueryOptions.Builder options = QueryOptions.newBuilder()
		            .setLimit(listingProperties.getMaxResults())
		            .setReturningIdsOnly(true);
			if (listingProperties.getNextCursor() != null) {
				options = options.setCursor(Cursor.newBuilder().build(listingProperties.getNextCursor()));
			} else {
				options = options.setCursor(Cursor.newBuilder().build());
			}
			options = options.setSortOptions(sortOptions);
			
		    // Query the index.
			Query query = Query.newBuilder().setOptions(options.build()).build(searchText);
		    Results<ScoredDocument> results = getIndex().search(query);

		    for (ScoredDocument document : results) {
		        list.add(NumberUtils.toLong(document.getId()));
		    }
		    if (results.getCursor() != null) {
		    	listingProperties.setNextCursor(results.getCursor().toWebSafeString());
		    }
		    listingProperties.setNumberOfResults(results.getNumberReturned());
		    listingProperties.updateMoreResultsUrl();
		    
			log.info("Full text search for term '" + searchText + "' returned " + list.size()
					+ " items. Items: " + Arrays.toString(list.toArray()));
		} catch (Exception e) {
			log.log(Level.WARNING, "Error while searching docs", e);
		}
		return list;
	}
}
