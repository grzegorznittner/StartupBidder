package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ListingFacade;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class FileController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(FileController.class.getName());
	private Object model;

	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			if("get-upload-url".equalsIgnoreCase(getCommand(1))) {
				return getUploadUrl(request);
			} else if("return-document".equalsIgnoreCase(getCommand(1))) {
				return returnDoc(request);
			} else if("download".equalsIgnoreCase(getCommand(1))) {
				return download(request);
			}
		} else if ("POST".equalsIgnoreCase(request.getMethod())) {
			if("upload".equalsIgnoreCase(getCommand(1))) {
				return upload(request);
			} else if("delete".equalsIgnoreCase(getCommand(1))) {
				return delete(request);
			}
		}
		return null;
	}

	private HttpHeaders getUploadUrl(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("get-upload-url");
		
		int numOfUrls = 1;
		String number = getCommandOrParameter(request, 2, "number");
		if (StringUtils.isNotEmpty(number)) {
			numOfUrls = NumberUtils.createLong(number).intValue();
		}

		model = ServiceFacade.instance().createUploadUrls(getLoggedInUser(), "/file/upload", numOfUrls);
		log.log(Level.INFO, "Returning " + numOfUrls + " upload urls: " + model);
		
		return headers;
	}
	
	private HttpHeaders download(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("download");
		
		String docId = getCommandOrParameter(request, 2, "doc");

		ListingDocumentVO doc = ListingFacade.instance().getListingDocument(getLoggedInUser(), docId);
		log.log(Level.INFO, "Sending back document: " + doc);
		if (doc != null && doc.getBlob() != null) {
			headers.addHeader("Content-Disposition", "attachment; filename=" + doc.getType().toLowerCase());
			headers.setBlobKey(doc.getBlob());
		} else {
			log.log(Level.INFO, "Document not found or blob not available!");
			headers.setStatus(500);
		}
		
		return headers;
	}
	
	private HttpHeaders delete(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("delete");
		
		String docId = getCommandOrParameter(request, 2, "doc");

		log.log(Level.INFO, "Deleting document id: " + docId);
		ListingDocumentVO doc = ServiceFacade.instance().deleteDocument(getLoggedInUser(), docId);
		if (doc != null) {
			model = doc;
			log.log(Level.INFO, "Document deleted: " + doc);
		} else {
			log.log(Level.INFO, "Document not found!");
			headers.setStatus(500);
		}
		
		return headers;
	}
	
	private HttpHeaders returnDoc(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("return-doc");
		
		String docId = getCommandOrParameter(request, 2, "doc");

		model = ListingFacade.instance().getListingDocument(getLoggedInUser(), docId);
		log.log(Level.INFO, "Returning document: " + model);
		
		return headers;
	}
	
	private HttpHeaders upload(HttpServletRequest request) {
		HttpHeadersImpl headers = new HttpHeadersImpl("upload");
		
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(request);
		
		log.log(Level.INFO, "Got blobs : " + blobs);
		ListingDocumentVO doc = null;
		if (blobs.containsKey(ListingDoc.Type.BUSINESS_PLAN.toString())) {
			BlobKey blobKey = blobs.get(ListingDoc.Type.BUSINESS_PLAN.toString());
			doc = new ListingDocumentVO();
			doc.setBlob(blobKey);
			doc.setType(ListingDoc.Type.BUSINESS_PLAN.toString());
			
			blobs.remove(ListingDoc.Type.BUSINESS_PLAN.toString());
		} else if (blobs.containsKey(ListingDoc.Type.PRESENTATION.toString())) {
			BlobKey blobKey = blobs.get(ListingDoc.Type.PRESENTATION.toString());
			doc = new ListingDocumentVO();
			doc.setBlob(blobKey);
			doc.setType(ListingDoc.Type.PRESENTATION.toString());
			
			blobs.remove(ListingDoc.Type.PRESENTATION.toString());
		} else if (blobs.containsKey(ListingDoc.Type.FINANCIALS.toString())) {
			BlobKey blobKey = blobs.get(ListingDoc.Type.FINANCIALS.toString());
			doc = new ListingDocumentVO();
			doc.setBlob(blobKey);
			doc.setType(ListingDoc.Type.FINANCIALS.toString());
			
			blobs.remove(ListingDoc.Type.FINANCIALS.toString());
		} else if (blobs.containsKey(ListingDoc.Type.LOGO.toString())) {
			BlobKey blobKey = blobs.get(ListingDoc.Type.LOGO.toString());
			doc = new ListingDocumentVO();
			doc.setBlob(blobKey);
			doc.setType(ListingDoc.Type.LOGO.toString());
			
			blobs.remove(ListingDoc.Type.LOGO.toString());
		}

		// delete unwanted attachements
		log.log(Level.INFO, "Deleting remaining (unhandled) blobs: " + blobs);
		for (BlobKey blobKey : blobs.values()) {
			blobstoreService.delete(blobKey);
		}

		if (doc != null) {
			log.log(Level.INFO, "Storing document: " + doc);
			doc = ListingFacade.instance().createListingDocument(getLoggedInUser(), doc);
			if (doc != null) {
				headers.setRedirectUrl("/listing/edited/");
				return headers;
			}
		}
		log.log(Level.INFO, "No docs to store!");
		headers.setStatus(500);

		return headers;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
