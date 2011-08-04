package com.startupbidder.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    
    void setRedirectUrl(String redirectUrl);
    
    String getRedirectUrl();
    
    boolean isRedirect();

}