package com.startupbidder.web.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.startupbidder.vo.BidVO;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;
import com.startupbidder.web.StatisticsFacade;
import com.startupbidder.web.StatisticsFacade.GraphType;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class BidController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(BidController.class.getName());

	private Object model = null;
	
	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			// GET method handler
			
			if("all".equalsIgnoreCase(getCommand(1))) {
				return all(request);
			} else if("listing".equalsIgnoreCase(getCommand(1))) {
				return listing(request);
			} else if("user".equalsIgnoreCase(getCommand(1))) {
				return user(request);
			} else if("accepted-by-user".equalsIgnoreCase(getCommand(1))) {
				return acceptedByUser(request);
			} else if("funded-by-user".equalsIgnoreCase(getCommand(1))) {
				return fundedByUser(request);
			} else if("statistics".equalsIgnoreCase(getCommand(1))) {
				return bidDayVolume(request);
			} else if("bid-day-volume".equalsIgnoreCase(getCommand(1))) {
				return bidDayVolume(request);
			} else if("bid-day-valuation".equalsIgnoreCase(getCommand(1))) {
				return bidDayValuation(request);
			} else {
				return get(request);
			}
		} else if ("POST".equalsIgnoreCase(request.getMethod())) {
			if("create".equalsIgnoreCase(getCommand(1))) {
				return create(request);
			} else if("update".equalsIgnoreCase(getCommand(1))) {
				return update(request);
			} else if("activate".equalsIgnoreCase(getCommand(1))) {
				return activate(request);
			} else if("reject".equalsIgnoreCase(getCommand(1))) {
				return reject(request);
			} else if("withdraw".equalsIgnoreCase(getCommand(1))) {
				return withdraw(request);
			} else if("accept".equalsIgnoreCase(getCommand(1))) {
				return accept(request);
			} else if("paid".equalsIgnoreCase(getCommand(1))) {
				return paid(request);
			}
		} else if ("DELETE".equalsIgnoreCase(request.getMethod())) {
			return delete(request);
		}
		return null;
	}

	/*
	 * PUT /bid/activate?id=<id>
	 */
	private HttpHeaders activate(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("activate");
		
		String bidId = getCommandOrParameter(request, 3, "id");
		if (!StringUtils.isEmpty(bidId)) {
			model = ServiceFacade.instance().activateBid(getLoggedInUser(), bidId);
			if (model == null) {
				headers.setStatus(500);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'id' is not provided!");
			headers.setStatus(500);
		}
		
		return headers;
	}

	/*
	 * PUT /bid/withdraw?id=<id>
	 */
	private HttpHeaders withdraw(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("withdraw");
		
		String bidId = getCommandOrParameter(request, 3, "id");
		if (!StringUtils.isEmpty(bidId)) {
			model = ServiceFacade.instance().withdrawBid(getLoggedInUser(), bidId);
			if (model == null) {
				headers.setStatus(500);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'id' is not provided!");
			headers.setStatus(500);
		}
		
		return headers;
	}

	/*
	 * PUT /bid/accept?id=<id>
	 */
	private HttpHeaders accept(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("accept");
		
		String bidId = getCommandOrParameter(request, 3, "id");
		if (!StringUtils.isEmpty(bidId)) {
			model = ServiceFacade.instance().acceptBid(getLoggedInUser(), bidId);
			if (model == null) {
				headers.setStatus(500);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'id' is not provided!");
			headers.setStatus(500);
		}
		
		return headers;
	}

	/*
	 * PUT /bid/reject?id=<id>
	 */
	private HttpHeaders reject(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("reject");
		
		String bidId = getCommandOrParameter(request, 3, "id");
		if (!StringUtils.isEmpty(bidId)) {
			model = ServiceFacade.instance().rejectBid(getLoggedInUser(), bidId);
			if (model == null) {
				headers.setStatus(500);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'id' is not provided!");
			headers.setStatus(500);
		}
		
		return headers;
	}

	/*
	 * PUT /bid/accept?id=<id>
	 */
	private HttpHeaders paid(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("paid");
		
		String bidId = getCommandOrParameter(request, 3, "id");
		if (!StringUtils.isEmpty(bidId)) {
			model = ServiceFacade.instance().markBidAsPaid(getLoggedInUser(), bidId);
			if (model == null) {
				headers.setStatus(500);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'id' is not provided!");
			headers.setStatus(500);
		}
		
		return headers;
	}

	/*
	 * DELETE /bid?id=<id>
	 */
	private HttpHeaders delete(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("delete");
		
		String bidId = getCommandOrParameter(request, 2, "id");
		if (!StringUtils.isEmpty(bidId)) {
			model = ServiceFacade.instance().deleteBid(getLoggedInUser(), bidId);
			if (model == null) {
				log.log(Level.WARNING, "Bid not found!");
				headers.setStatus(500);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'id' is not provided!");
			headers.setStatus(500);
		}
		
		return headers;
	}

	/*
	 * POST /bid/create?bid=<bid json>
	 */
	private HttpHeaders create(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("create");
		
		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String bidString = request.getParameter("bid");
		if (!StringUtils.isEmpty(bidString)) {
			BidVO bid = mapper.readValue(bidString, BidVO.class);
			log.log(Level.INFO, "Creating bid: " + model);
			model = ServiceFacade.instance().createBid(getLoggedInUser(), bid);
			if (model == null) {
				log.log(Level.WARNING, "Bid not created!");
				headers.setStatus(500);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'bid' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}

	/*
	 * PUT /bid/update?bid=<bid json>
	 */
	private HttpHeaders update(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("update");
		
		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String bidString = request.getParameter("bid");
		if (!StringUtils.isEmpty(bidString)) {
			BidVO bid = mapper.readValue(bidString, BidVO.class);
			log.log(Level.INFO, "Updating bid: " + bid);
			if (bid.getId() == null) {
				log.log(Level.WARNING, "Bid id not provided!");
				headers.setStatus(500);
			} else {
				model = ServiceFacade.instance().updateBid(getLoggedInUser(), bid);
				if (model == null) {
					log.log(Level.WARNING, "Bid not found!");
					headers.setStatus(500);
				}
			}
		} else {
			log.log(Level.WARNING, "Parameter 'bid' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}

	private HttpHeaders get(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("get");
		String bidId = getCommandOrParameter(request, 2, "id");
		model = ServiceFacade.instance().getBid(getLoggedInUser(), bidId);
		return headers;
	}

	/*
	 *  /bids/listing/?id=ag1zdGFydHVwYmlkZGVychQLEgdMaXN0aW5nIgdtaXNsZWFkDA
	 */
	private HttpHeaders listing(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("listing");
		
		ListPropertiesVO bidProperties = getListProperties(request);
		String listingId = getCommandOrParameter(request, 2, "id");
		model = ServiceFacade.instance().getBidsForListing(getLoggedInUser(), listingId, bidProperties);
		
		return headers;
	}

	/*
	 *  /bids/statistics/
	 *  /bids/bid-day-volume/
	 */
	private HttpHeaders bidDayVolume(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("bid-day-volume");		
		model = StatisticsFacade.getGraphData(GraphType.BID_DAY_VOLUME);
		return headers;
	}

	/*
	 *  /bids/bid-day-valuation/
	 */
	private HttpHeaders bidDayValuation(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("bid-day-valuation");		
		model = StatisticsFacade.getGraphData(GraphType.BID_DAY_VALUATION);
		return headers;
	}

	/*
	 *  /bids/user/ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw/
	 *  /bids/user/?id=ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw
	 */
	private HttpHeaders user(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("user");
		
		ListPropertiesVO bidProperties = getListProperties(request);
		String userId = getCommandOrParameter(request, 2, "id");
		model = ServiceFacade.instance().getBidsForUser(getLoggedInUser(), userId, bidProperties);
		
		return headers;
	}

	/*
	 *  /bids/accepted-by-user/ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw/
	 *  /bids/accepted-by-user/?id=ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw
	 */
	private HttpHeaders acceptedByUser(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("accepted-by-user");
		
		ListPropertiesVO bidProperties = getListProperties(request);
		String userId = getCommandOrParameter(request, 2, "id");
		model = ServiceFacade.instance().getBidsAcceptedByUser(getLoggedInUser(), userId, bidProperties);
		
		return headers;
	}

	/*
	 *  /bids/funded-by-user/ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw/
	 *  /bids/funded-by-user/?id=ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw
	 */
	private HttpHeaders fundedByUser(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("funded-by-user");
		
		ListPropertiesVO bidProperties = getListProperties(request);
		String userId = getCommandOrParameter(request, 2, "id");
		model = ServiceFacade.instance().getBidsFundedByUser(getLoggedInUser(), userId, bidProperties);
		
		return headers;
	}

	private HttpHeaders all(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("all");
		headers.setStatus(501);
		return headers;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
