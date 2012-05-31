package com.startupbidder.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.startupbidder.dao.ObjectifyDatastoreDAO.CursorHandler;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.BidUser;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.vo.ListPropertiesVO;

/**
 * Datastore implementation which uses Google's AppEngine Datastore through Objectify interfaces.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class BidObjectifyDatastoreDAO {
	private static final Logger log = Logger.getLogger(BidObjectifyDatastoreDAO.class.getName());
	static BidObjectifyDatastoreDAO instance;
		
	public static BidObjectifyDatastoreDAO getInstance() {
		if (instance == null) {
			instance = new BidObjectifyDatastoreDAO();
		}
		return instance;
	}

	private BidObjectifyDatastoreDAO() {
	}
	
	private Objectify getOfy() {
		Objectify ofy = ObjectifyService.begin();
		return ofy;
	}

	public Bid makeBid(BidUser shorts[], Listing listing, SBUser toUser, SBUser fromUser, Bid.Type bidType, int amount, int percentage, String text) {
		int value = (amount * 100) / percentage;
		Bid bid1 = new Bid(listing, toUser, fromUser, text, amount, percentage, value, bidType);
		Bid bid2 = bid1.createCrossBid();
		
		BidUser short0 = new BidUser(bid1);
		if (shorts[0] != null) {
			short0.id = shorts[0].id;
			short0.counter = shorts[0].counter + 1;
		}
		BidUser short1 = new BidUser(bid2);
		if (shorts[1] != null) {
			short1.id = shorts[1].id;
			short1.counter = shorts[1].counter + 1;
		}
		shorts[0] = short0;
		shorts[1] = short1;
		log.info("Storing bids: " + bid1 + "; " + bid2 + ". Updating bid shorts: " + shorts[0] + "; " + shorts[1]);
		getOfy().put(bid1, bid2, shorts[0], shorts[1]);
		return bid1;
	}
	
	public BidUser[] getBidShorts(Listing listing, SBUser user1, SBUser user2) {
		BidUser bids[] = new BidUser[2];
		List<Key<BidUser>> toDelete = new ArrayList<Key<BidUser>>();

		Key<BidUser> msgKey = getOfy().query(BidUser.class)
				.filter("listing =", listing.getKey())
				.filter("userA =", user1.getKey())
				.filter("userB =", user2.getKey())
				.order("-created").getKey();
		bids[0] = msgKey != null ? getOfy().get(msgKey) : null;
		msgKey = getOfy().query(BidUser.class)
				.filter("listing =", listing.getKey())
				.filter("userA =", user2.getKey())
				.filter("userB =", user1.getKey())
				.order("-created").getKey();
		bids[1] = msgKey != null ? getOfy().get(msgKey) : null;
		if (bids[0] == null ^ bids[1] == null) {
			log.severe("Data inconsistency. It will be fixed by updateReadFlag call. bids[0]: " + bids[0] + ", bids[1]: " + bids[1]);
		}
		return bids;
	}
	
	public List<BidUser> getBidShortList(Listing listing, SBUser user, ListPropertiesVO listProperties) {
		Query<BidUser> query = getOfy().query(BidUser.class)
				.filter("listing =", listing.getKey())
				.filter("userA =", user.getKey())
				.order("-created")
       			.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<BidUser>> keyList = new CursorHandler<BidUser>().handleQuery(listProperties, query);
		List<BidUser> bids = new ArrayList<BidUser>(getOfy().get(keyList).values());
		return bids;
	}
	
	public List<Bid> getBidList(Listing listing, SBUser user, SBUser otherUser, ListPropertiesVO listProperties) {
		Query<Bid> query = getOfy().query(Bid.class)
				.filter("listing =", listing.getKey())
				.filter("userA =", user.getKey())
				.filter("userB =", otherUser.getKey())
				.order("-created")
       			.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<Bid>> keyList = new CursorHandler<Bid>().handleQuery(listProperties, query);
		List<Bid> bids = new ArrayList<Bid>(getOfy().get(keyList).values());
		return bids;
	}

	public void updateReadFlag(Listing listing, SBUser user, SBUser otherUser, List<Bid> bids) {
		List<Bid> forUpdate = new ArrayList<Bid>();
		for (Bid msg : bids) {
			if (!msg.read) {
				msg.read = true;
				forUpdate.add(msg);
			} else {
				break;
			}
		}
		if (forUpdate.size() > 0) {
			BidUser shorts[] = getBidShorts(listing, user, otherUser);
			if (shorts[0] == null && shorts[1] != null) {
				log.severe("Fixing missed BidUser object, should not happen. Other object: " + shorts[1]);
				// it should not happen, but unfortunatelly has already happened
				shorts[0] = new BidUser(shorts[1].createCrossBid());
				shorts[0].counter = shorts[1].counter;
			}
			if (shorts[1] == null && shorts[0] != null) {
				log.severe("Fixing missed BidUser object, should not happen. Other object: " + shorts[1]);
				// it should not happen, but unfortunatelly has already happened
				shorts[1] = new BidUser(shorts[0].createCrossBid());
				shorts[1].counter = shorts[0].counter;
			}
			if (shorts[0] == null && shorts[1] == null) {
				log.severe("Both BidUser objects are missing! Listing: " + listing.getKey()
						+ ", user1: " + user.getKey() + ", user2: " + otherUser.getKey());
			}
			if (shorts[0] != null) {
				shorts[0].read = true;
				forUpdate.add(shorts[0]);
			}
			
			log.info("Updating read flag for " + forUpdate.size() + " bid objects.");
			getOfy().put(forUpdate);
		}
	}
}
