package com.startupbidder.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import static javax.servlet.http.HttpServletResponse.*;

import java.io.IOException;
import java.util.Date;

/**
 * Default implementation of rest info that uses fluent-style construction
 */
public class HttpHeadersImpl implements HttpHeaders {
    String resultCode;
    int status = SC_OK;
    Object etag;
    Object locationId;
    String location;
    boolean disableCaching;
    boolean noETag = false;
    Date lastModified;
    String redirect = null;

    public HttpHeadersImpl() {}

    public HttpHeadersImpl(String result) {
        resultCode = result;
    }

    public HttpHeadersImpl renderResult(String code) {
        this.resultCode = code;
        return this;
    }

    public HttpHeadersImpl withStatus(int code) {
        this.status = code;
        return this;
    }

    public HttpHeadersImpl withETag(Object etag) {
        this.etag = etag;
        return this;
    }

    public HttpHeadersImpl withNoETag() {
        this.noETag = true;
        return this;
    }

    public HttpHeadersImpl setLocationId(Object id) {
        this.locationId = id;
        return this;
    }

    public HttpHeadersImpl setLocation(String loc) {
        this.location = loc;
        return this;
    }

    public HttpHeadersImpl lastModified(Date date) {
        this.lastModified = date;
        return this;
    }

    public HttpHeadersImpl disableCaching() {
        this.disableCaching = true;
        return this;
    }
    
    public void setRedirectUrl(String redirectUrl) {
    	redirect = redirectUrl;
    }
    
    public String getRedirectUrl() {
    	return redirect;
    }
    
    public boolean isRedirect() {
    	return StringUtils.isNotEmpty(redirect);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.rest.HttpHeaders#apply(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    public String apply(HttpServletRequest request, HttpServletResponse response, Object target) {
        if (disableCaching) {
            response.setHeader("Cache-Control", "no-cache");
        }
        if (lastModified != null) {
            response.setDateHeader("Last-Modified", lastModified.getTime());
        }
        if (etag == null && !noETag && target != null) {
            etag = String.valueOf(target.hashCode());
        }
        if (etag != null) {
            response.setHeader("ETag", etag.toString());
        }

        if (locationId != null) {
            String url = request.getRequestURL().toString();
            int lastSlash = url.lastIndexOf("/");
            int lastDot = url.lastIndexOf(".");
            if (lastDot > lastSlash && lastDot > -1) {
                url = url.substring(0, lastDot)+"/"+locationId+url.substring(lastDot);
            } else {
                url += "/"+locationId;
            }
            response.setHeader("Location", url);
            status = SC_CREATED;
        } else if (location != null) {
            response.setHeader("Location", location);
            status = SC_CREATED;
        }

        if (status == SC_OK && !disableCaching) {
            boolean etagNotChanged = false;
            boolean lastModifiedNotChanged = false;
            String reqETag = request.getHeader("If-None-Match");
            if (etag != null) {
                if (etag.equals(reqETag)) {
                    etagNotChanged = true;
                }
            }

            String reqLastModified = request.getHeader("If-Modified-Since");
            if (lastModified != null) {
                if (String.valueOf(lastModified.getTime()).equals(reqLastModified)) {
                    lastModifiedNotChanged = true;
                }

            }

            if ((etagNotChanged && lastModifiedNotChanged) ||
                (etagNotChanged && reqLastModified == null) ||
                (lastModifiedNotChanged && reqETag == null)) {
                status = SC_NOT_MODIFIED;
            }
        }

        response.setStatus(status);
        return resultCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int s) {
    	status = s;
    }

	public String getResultCode() {
		return resultCode;
	}




}
