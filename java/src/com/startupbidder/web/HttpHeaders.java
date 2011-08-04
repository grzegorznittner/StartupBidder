package com.startupbidder.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;

/**
 * Defines headers returned from controllers.
 */
public interface HttpHeaders {

    /**
     * Applies headers on HttpServletResponse object
     */
    String apply(HttpServletRequest request, HttpServletResponse response, Object target);

    /**
     * The HTTP status code
     */
    int getStatus();

    /**
     * The HTTP status code
     */
    void setStatus(int status);

    /**
     * The result code to process
     */
    String getResultCode();
    
    /**
     * Sets redirect url
     */
    void setRedirectUrl(String redirectUrl);

    /**
     * Returns redirect url
     */
    String getRedirectUrl();

    /**
     * Checks whether response should be redirect (redirectUrl was set).
     */
    boolean isRedirect();

    /**
     * Sets key to the blob which will be returned.
     */
    void setBlobKey(BlobKey blob);
    
    /**
     * Returns key to the blob
     */
    BlobKey getBlobKey();
    
    /**
     * Checks whether response is returning blob content
     */
    boolean isBlobResponse();
}