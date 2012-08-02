package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.taskqueue.TaskQueuePb;
import com.startupbidder.vo.ErrorCodes;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
		String listingId = getCommandOrParameter(request, 2, "id");
		
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(request);
		
		log.log(Level.INFO, "Got blobs : " + blobs);
		ListingDocumentVO doc = null;
		if (blobs.containsKey(ListingDoc.Type.BUSINESS_PLAN.toString())) {
			doc = handleBlobType(blobs, ListingDoc.Type.BUSINESS_PLAN);
		} else if (blobs.containsKey(ListingDoc.Type.PRESENTATION.toString())) {
			doc = handleBlobType(blobs, ListingDoc.Type.PRESENTATION);
		} else if (blobs.containsKey(ListingDoc.Type.FINANCIALS.toString())) {
			doc = handleBlobType(blobs, ListingDoc.Type.FINANCIALS);
		} else if (blobs.containsKey(ListingDoc.Type.LOGO.toString())) {
			doc = handleBlobType(blobs, ListingDoc.Type.LOGO);
		} else if (blobs.containsKey(ListingDoc.Type.PIC1.toString())) {
			doc = handleBlobType(blobs, ListingDoc.Type.PIC1);
		} else if (blobs.containsKey(ListingDoc.Type.PIC2.toString())) {
			doc = handleBlobType(blobs, ListingDoc.Type.PIC2);
		} else if (blobs.containsKey(ListingDoc.Type.PIC3.toString())) {
			doc = handleBlobType(blobs, ListingDoc.Type.PIC3);
		} else if (blobs.containsKey(ListingDoc.Type.PIC4.toString())) {
			doc = handleBlobType(blobs, ListingDoc.Type.PIC4);
		} else if (blobs.containsKey(ListingDoc.Type.PIC5.toString())) {
			doc = handleBlobType(blobs, ListingDoc.Type.PIC5);
		}

		// delete unwanted attachements
		log.log(Level.INFO, "Deleting remaining (unhandled) blobs: " + blobs);
		for (BlobKey blobKey : blobs.values()) {
			blobstoreService.delete(blobKey);
		}

		if (doc != null) {
			log.log(Level.INFO, "Storing document: " + doc);
			doc = ListingFacade.instance().createListingDocument(getLoggedInUser(), listingId, doc);
			if (doc != null) {
                String errorMsg = doc.getErrorCode() != ErrorCodes.OK ? "?errorMsg=" + doc.getErrorMessage() : "";
				headers.setRedirectUrl("/listing/edited/" + doc.getType() + "/" + errorMsg);
			}
            else {
                log.warning("Document upload error");
                headers.setStatus(500);
            }
		}
        else {
		    log.log(Level.INFO, "No docs to store!");
		    headers.setStatus(500);
        }

		return headers;
	}

	private ListingDocumentVO handleBlobType(Map<String, BlobKey> blobs, ListingDoc.Type type) {
		ListingDocumentVO doc;
		BlobKey blobKey = blobs.get(type.toString());
		doc = new ListingDocumentVO();
		doc.setBlob(blobKey);
		doc.setType(type.toString());
		
		blobs.remove(type.toString());
		return doc;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
