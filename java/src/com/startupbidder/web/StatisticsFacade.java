package com.startupbidder.web;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.startupbidder.dto.BidDTO;
import com.startupbidder.vo.GraphDataVO;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.TickerDataVO;

public class StatisticsFacade {
	public enum GraphType {BID_DAY_VOLUME, BID_DAY_VALUATION};
	public enum TickerType {LISTING_VALUATION_CHANGE};

	private static final Logger log = Logger.getLogger(StatisticsFacade.class.getName());
	private static StatisticsFacade instance = null;
	private Cache cache;
	
	public static StatisticsFacade instance() {
		if (instance == null) {
			instance = new StatisticsFacade();
		}
		return instance;
	}
	
	private StatisticsFacade() {
		try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
        } catch (CacheException e) {
            log.log(Level.SEVERE, "Cache couldn't be created!!!");
        }
	}
	
	public static GraphDataVO getGraphData(GraphType type) {
		switch (type) {
		case BID_DAY_VOLUME:
			return instance().getBidDayVolume();
		case BID_DAY_VALUATION:
			return instance().getBidDayValuation();
		}
		return null;
	}

	public static TickerDataVO getTickerData(TickerType type) {
		switch (type) {
		case LISTING_VALUATION_CHANGE:
			return instance().getListingValuationChange();
		}
		return null;
	}

	private TickerDataVO getListingValuationChange() {
		DateMidnight midnight = new DateMidnight();
		TickerDataVO data = (TickerDataVO)cache.get(TickerType.LISTING_VALUATION_CHANGE);
		// are data created today?
		if (data != null && Days.daysBetween(new DateTime(data.getCreated().getTime()), midnight).isLessThan(Days.ONE)) {
			return data;
		}
		data = calculateListingValuationChange();
		
		return null;
	}
	
	public TickerDataVO calculateListingValuationChange() {
		TickerDataVO data = new TickerDataVO();
		
		return data;
	}

	private GraphDataVO getBidDayVolume() {
		DateMidnight midnight = new DateMidnight();
		GraphDataVO data = (GraphDataVO)cache.get(GraphType.BID_DAY_VOLUME);
		// are data created today?
		if (data != null && Days.daysBetween(new DateTime(data.getCreated().getTime()), midnight).isLessThan(Days.ONE)) {
			return data;
		}
		
		data = calculateBidDayVolume();
		
		return data;
	}

	public GraphDataVO calculateBidDayVolume() {
		DateMidnight midnight = new DateMidnight();
		GraphDataVO data = new GraphDataVO(GraphType.BID_DAY_VOLUME.toString());
		
		ListPropertiesVO bidsProperties = new ListPropertiesVO();
		bidsProperties.setMaxResults(100);
		List<BidDTO> bids = ServiceFacade.instance().getDAO().getBidsByDate(bidsProperties);

		int[] values = new int[2];
		if (bids.size() > 1) {
			int bidTimeSpan = Math.abs(Days.daysBetween(new DateTime(bids.get(0).getPlaced()),
					new DateTime(bids.get(bids.size() - 1).getPlaced())).getDays());
			
			values = new int[bidTimeSpan];
			for (BidDTO bid : bids) {
				int days = Math.abs(Days.daysBetween(new DateTime(bid.getPlaced().getTime()), midnight).getDays());
				if (days < values.length) {
					values[days]++;
				}
			}
		}
		
		data.setLabel(values.length + " Day Bid Volume");
		data.setxAxis("days ago");
		data.setyAxis("num bids");
		data.setValues(values);
		data.setCreated(new Date());
		cache.put(GraphType.BID_DAY_VOLUME, data);
		return data;
	}
	
	private GraphDataVO getBidDayValuation() {
		DateMidnight midnight = new DateMidnight();
		GraphDataVO data = (GraphDataVO)cache.get(GraphType.BID_DAY_VALUATION);
		// are data created today?
		if (data != null && Days.daysBetween(new DateTime(data.getCreated().getTime()), midnight).isLessThan(Days.ONE)) {
			return data;
		}
		
		data = calculateBidDayValuation();

		return data;
	}

	public GraphDataVO calculateBidDayValuation() {
		DateMidnight midnight = new DateMidnight();
		GraphDataVO data = new GraphDataVO(GraphType.BID_DAY_VALUATION.toString());
		
		ListPropertiesVO bidsProperties = new ListPropertiesVO();
		bidsProperties.setMaxResults(100);
		List<BidDTO> bids = ServiceFacade.instance().getDAO().getBidsByDate(bidsProperties);

		int[] values = new int[2];
		if (bids.size() > 1) {
			int bidTimeSpan = Math.abs(Days.daysBetween(new DateTime(bids.get(0).getPlaced()),
					new DateTime(bids.get(bids.size() - 1).getPlaced())).getDays());
			
			values = new int[bidTimeSpan];
			for (BidDTO bid : bids) {
				int days = Math.abs(Days.daysBetween(new DateTime(bid.getPlaced().getTime()), midnight).getDays());
				if (days < values.length) {
					values[days] += bid.getValuation();
				}
			}
		}
		
		data.setLabel(values.length + " Day Bid Valuation");
		data.setxAxis("days ago");
		data.setyAxis("bids valuation");
		data.setValues(values);
		data.setCreated(new Date());
		cache.put(GraphType.BID_DAY_VALUATION, data);
		return data;
	}

}
