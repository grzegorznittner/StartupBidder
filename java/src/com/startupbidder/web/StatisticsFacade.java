package com.startupbidder.web;

import java.util.List;

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

	private static StatisticsFacade instance = null;
	
	public static StatisticsFacade instance() {
		if (instance == null) {
			instance = new StatisticsFacade();
		}
		return instance;
	}
	
	private StatisticsFacade() {
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

	public static List<TickerDataVO> getGraphData(TickerType type) {
		switch (type) {
		case LISTING_VALUATION_CHANGE:
			return instance().getListingValuationChange();
		}
		return null;
	}

	private List<TickerDataVO> getListingValuationChange() {
		// TODO Auto-generated method stub
		return null;
	}

	private GraphDataVO getBidDayVolume() {		
		ListPropertiesVO bidsProperties = new ListPropertiesVO();
		bidsProperties.setMaxResults(100);
		List<BidDTO> bids = ServiceFacade.instance().getDAO().getBidsByDate(bidsProperties);

		int[] values = new int[2];
		if (bids.size() > 1) {
			int bidTimeSpan = Math.abs(Days.daysBetween(new DateTime(bids.get(0).getPlaced()),
					new DateTime(bids.get(bids.size() - 1).getPlaced())).getDays());
			
			values = new int[bidTimeSpan];
			DateMidnight midnight = new DateMidnight();
			for (BidDTO bid : bids) {
				int days = Math.abs(Days.daysBetween(new DateTime(bid.getPlaced().getTime()), midnight).getDays());
				if (days < values.length) {
					values[days]++;
				}
			}
		}
		
		GraphDataVO data = new GraphDataVO(GraphType.BID_DAY_VOLUME.toString());
		data.setLabel(values.length + " Day Bid Valume");
		data.setxAxis("days ago");
		data.setyAxis("num bids");
		data.setValues(values);

		return data;
	}
	
	private GraphDataVO getBidDayValuation() {		
		ListPropertiesVO bidsProperties = new ListPropertiesVO();
		bidsProperties.setMaxResults(100);
		List<BidDTO> bids = ServiceFacade.instance().getDAO().getBidsByDate(bidsProperties);

		int[] values = new int[2];
		if (bids.size() > 1) {
			int bidTimeSpan = Math.abs(Days.daysBetween(new DateTime(bids.get(0).getPlaced()),
					new DateTime(bids.get(bids.size() - 1).getPlaced())).getDays());
			
			values = new int[bidTimeSpan];
			DateMidnight midnight = new DateMidnight();
			for (BidDTO bid : bids) {
				int days = Math.abs(Days.daysBetween(new DateTime(bid.getPlaced().getTime()), midnight).getDays());
				if (days < values.length) {
					values[days] += bid.getValuation();
				}
			}
		}
		
		GraphDataVO data = new GraphDataVO(GraphType.BID_DAY_VALUATION.toString());
		data.setLabel(values.length + " Day Bid Valume");
		data.setxAxis("days ago");
		data.setyAxis("bids valuation");
		data.setValues(values);

		return data;
	}

}
