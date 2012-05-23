package com.startupbidder.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.startupbidder.dao.ObjectifyDatastoreDAO.CursorHandler;
import com.startupbidder.datamodel.PrivateMessage;
import com.startupbidder.datamodel.PrivateMessageUser;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.vo.ListPropertiesVO;

/**
 * Datastore implementation which uses Google's AppEngine Datastore through Objectify interfaces.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class MessageObjectifyDatastoreDAO {
	private static final Logger log = Logger.getLogger(MessageObjectifyDatastoreDAO.class.getName());
	static MessageObjectifyDatastoreDAO instance;
		
	public static MessageObjectifyDatastoreDAO getInstance() {
		if (instance == null) {
			instance = new MessageObjectifyDatastoreDAO();
		}
		return instance;
	}

	private MessageObjectifyDatastoreDAO() {
	}
	
	private Objectify getOfy() {
		Objectify ofy = ObjectifyService.begin();
		return ofy;
	}
	
	public PrivateMessage createPrivateMessage(SBUser toUser, SBUser fromUser, String text) {
		PrivateMessage msg1 = new PrivateMessage(toUser, fromUser, text);
		PrivateMessage msg2 = msg1.createCrossMessage();
		
		PrivateMessageUser[] shorts = getMessageShorts(toUser, fromUser);
		if (shorts[0] != null) {
			shorts[0].text = text;
			shorts[0].created = msg1.created;
			shorts[0].counter++;
		} else {
			shorts[0] = new PrivateMessageUser(msg1);
			shorts[0].counter = 1;
		}
		if (shorts[1] != null) {
			shorts[1].text = text;
			shorts[1].created = msg1.created;
			shorts[1].counter++;
		} else {
			shorts[1] = new PrivateMessageUser(msg2);
			shorts[1].counter = 1;
		}
		log.info("Storing private messages: " + msg1 + "; " + msg2 + ". Updating message shorts: " + shorts[0] + "; " + shorts[1]);
		getOfy().put(msg1, msg2, shorts[0], shorts[1]);
		return msg1;
	}
	
	private PrivateMessageUser[] getMessageShorts(SBUser user1, SBUser user2) {
		PrivateMessageUser msg[] = new PrivateMessageUser[2];
		List<Key<PrivateMessageUser>> toDelete = new ArrayList<Key<PrivateMessageUser>>();

		Query<PrivateMessageUser> query = getOfy().query(PrivateMessageUser.class)
				.filter("userA =", user1.getKey())
				.filter("userB =", user2.getKey())
				.order("-created")
       			.chunkSize(5).prefetchSize(5);
		int count = 0;
		for (Key<PrivateMessageUser> msgKey : query.fetchKeys()) {
			if (count++ == 0) {
				msg[0] = getOfy().get(msgKey);
			} else {
				toDelete.add(msgKey);
			}
		}
		query = getOfy().query(PrivateMessageUser.class)
				.filter("userA =", user2.getKey())
				.filter("userB =", user1.getKey())
				.order("-created")
       			.chunkSize(5).prefetchSize(5);
		count = 0;
		for (Key<PrivateMessageUser> msgKey : query.fetchKeys()) {
			if (count++ == 0) {
				msg[1] = getOfy().get(msgKey);
			} else {
				toDelete.add(msgKey);
			}
		}
		if (toDelete.size() > 0) {
			log.info("Deleting redundant entries: " + toDelete);
			// deleting redundant entries
			getOfy().delete(toDelete);
		}
		return msg;
	}
	
	public List<PrivateMessageUser> getMessageShortList(SBUser user, ListPropertiesVO listProperties) {
		Query<PrivateMessageUser> query = getOfy().query(PrivateMessageUser.class)
				.filter("userA =", user.getKey())
				.filter("direction =", PrivateMessage.Direction.B_TO_A)
				.order("-created")
       			.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<PrivateMessageUser>> keyList = new CursorHandler<PrivateMessageUser>().handleQuery(listProperties, query);
		List<PrivateMessageUser> msgs = new ArrayList<PrivateMessageUser>(getOfy().get(keyList).values());
		return msgs;
	}
	
	public List<PrivateMessage> getMessageList(SBUser user, SBUser fromUser, ListPropertiesVO listProperties) {
		Query<PrivateMessage> query = getOfy().query(PrivateMessage.class)
				.filter("userA =", user.getKey())
				.filter("userB =", fromUser.getKey())
				.order("-created")
       			.chunkSize(listProperties.getMaxResults())
       			.prefetchSize(listProperties.getMaxResults());
		List<Key<PrivateMessage>> keyList = new CursorHandler<PrivateMessage>().handleQuery(listProperties, query);
		List<PrivateMessage> msgs = new ArrayList<PrivateMessage>(getOfy().get(keyList).values());
		return msgs;
	}
}
