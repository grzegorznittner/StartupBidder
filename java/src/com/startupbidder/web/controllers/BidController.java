package com.startupbidder.web.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import com.startupbidder.vo.BidVO;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

public class BidController extends ModelDrivenController {

	private List<BidVO> bids = null;
	private BidVO bid = null;
	
	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) {
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			// GET method handler
			
			if("all".equalsIgnoreCase(getCommand(1))) {
				return all(request);
			} else {
				return get(request);
			}
		} else if ("PUT".equalsIgnoreCase(request.getMethod()) ||
				"POST".equalsIgnoreCase(request.getMethod())) {
			return create(request);
		} else if ("DELETE".equalsIgnoreCase(request.getMethod())) {
			return delete(request);
		}
		return null;
	}

	private HttpHeaders delete(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("create");
		headers.setStatus(501);
		return headers;
	}

	private HttpHeaders create(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("create");
		headers.setStatus(501);
		return headers;
	}

	private HttpHeaders get(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("get");
		String bidId = getCommand(2).length() > 0 ? getCommand(2) : request.getParameter("bid");
		bid = ServiceFacade.instance().getBid(bidId);
		return headers;
	}

	private HttpHeaders all(HttpServletRequest request) {
		HttpHeaders headers = new DefaultHttpHeaders("create");
		headers.setStatus(501);
		return headers;
	}

	@Override
	public Object getModel() {
		return bids != null ? bids : bid;
	}

}
