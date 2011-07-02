package com.startupbidder.web.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import com.startupbidder.vo.BusinessPlanVO;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

public class ListingController extends ModelDrivenController {
	private static int DEFAULT_MAX_RESULTS = 5;
	
	List<BusinessPlanVO> bpList = null;
	
	public HttpHeaders executeAction(HttpServletRequest request) {
		if("top".equalsIgnoreCase(getCommand(1))) {
			return top(request);
		} else if("active".equalsIgnoreCase(getCommand(1))) {
			return active(request);
		} else if("valuation".equalsIgnoreCase(getCommand(1))) {
			return top(request);
		} else if("popular".equalsIgnoreCase(getCommand(1))) {
			return top(request);
		} else if("discussed".equalsIgnoreCase(getCommand(1))) {
			return top(request);
		} else if("latest".equalsIgnoreCase(getCommand(1))) {
			return top(request);
		} else if("closing".equalsIgnoreCase(getCommand(1))) {
			return top(request);
		} else if("user".equalsIgnoreCase(getCommand(1))) {
			return user(request);
		} else {
			return top(request);
		}
	}
	
    // GET /listings/top
    public HttpHeaders top(HttpServletRequest request) {
    	String maxItemsStr = request.getParameter("maxItems");
    	int maxItems = maxItemsStr != null ? Integer.parseInt(maxItemsStr) : DEFAULT_MAX_RESULTS;
    	bpList = ServiceFacade.instance().getTopBusinessPlans(maxItems, request.getParameter("cursor"));
        return new DefaultHttpHeaders("top").disableCaching();
    }

    // GET /listings/active
    public HttpHeaders active(HttpServletRequest request) {
    	String maxItemsStr = request.getParameter("maxItems");
    	int maxItems = maxItemsStr != null ? Integer.parseInt(maxItemsStr) : DEFAULT_MAX_RESULTS;
    	bpList = ServiceFacade.instance().getActiveBusinessPlans(maxItems, request.getParameter("cursor"));
        return new DefaultHttpHeaders("active").disableCaching();
    }

    // GET /listings/user
    public HttpHeaders user(HttpServletRequest request) {
    	String maxItemsStr = request.getParameter("maxItems");
    	int maxItems = maxItemsStr != null ? Integer.parseInt(maxItemsStr) : DEFAULT_MAX_RESULTS;
    	String userId = request.getParameter("user");
    	bpList = ServiceFacade.instance().getUserBusinessPlans(userId, maxItems, request.getParameter("cursor"));
        return new DefaultHttpHeaders("active").disableCaching();
    }

    public Object getModel() {
    	return bpList;
    }
}
