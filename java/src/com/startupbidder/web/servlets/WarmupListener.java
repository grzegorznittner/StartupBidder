/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.web.servlets;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.googlecode.objectify.ObjectifyService;
import com.startupbidder.dao.AngelListCache;
import com.startupbidder.dao.GeocodeLocation;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.dao.StartuplyCache;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.BidUser;
import com.startupbidder.datamodel.Category;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.ListingLocation;
import com.startupbidder.datamodel.ListingStats;
import com.startupbidder.datamodel.Location;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.PictureImport;
import com.startupbidder.datamodel.PrivateMessage;
import com.startupbidder.datamodel.PrivateMessageUser;
import com.startupbidder.datamodel.QuestionAnswer;
import com.startupbidder.datamodel.Rank;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.SystemProperty;
import com.startupbidder.datamodel.UserStats;
import com.startupbidder.datamodel.Vote;
import com.startupbidder.util.TwitterHelper;
import com.startupbidder.web.ListingFacade;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class WarmupListener implements ServletContextListener {
	private static final Logger log = Logger.getLogger(WarmupListener.class.getName());
	
	public static String MAIN_CSS_FILE;
	public static String MAIN_JS_FILE;
	public static String JS_FOLDER;
	
	static {
		ObjectifyService.register(SBUser.class);
		ObjectifyService.register(Listing.class);
		ObjectifyService.register(UserStats.class);
		ObjectifyService.register(Comment.class);
		ObjectifyService.register(ListingDoc.class);
		ObjectifyService.register(ListingStats.class);
		ObjectifyService.register(Monitor.class);
		ObjectifyService.register(Notification.class);
		ObjectifyService.register(QuestionAnswer.class);
		ObjectifyService.register(PrivateMessage.class);
		ObjectifyService.register(PrivateMessageUser.class);
		ObjectifyService.register(Bid.class);
		ObjectifyService.register(BidUser.class);
		ObjectifyService.register(Rank.class);
		ObjectifyService.register(SystemProperty.class);
		ObjectifyService.register(Vote.class);
		ObjectifyService.register(Category.class);
		ObjectifyService.register(Location.class);
		ObjectifyService.register(ListingLocation.class);
        ObjectifyService.register(AngelListCache.class);
        ObjectifyService.register(GeocodeLocation.class);
        ObjectifyService.register(StartuplyCache.class);
        ObjectifyService.register(PictureImport.class);
	}
	
	public void contextInitialized(ServletContextEvent event) {
		// This will be invoked as part of a warmup request, or the first user
		// request if no warmup request was invoked.
		
		File css = new File("./css");
		if (css.exists()) {
			File csses[] = css.listFiles();
			if (csses.length > 0) {
				MAIN_CSS_FILE = "./css/" + csses[0].getName();
			}
		}
		File js = new File("./js");
		if (js.exists()) {
			for(File jsFile : js.listFiles()) {
				if (jsFile.isDirectory()) {
					JS_FOLDER = "./js/" + jsFile.getName();
				}
				if (jsFile.isFile()) {
					MAIN_JS_FILE = "./js/" + jsFile.getName();
				}
			}
		}
		log.info("MAIN_CSS_FILE = " + MAIN_CSS_FILE);
		log.info("MAIN_JS_FILE = " + MAIN_JS_FILE);
		log.info("JS_FOLDER = " + JS_FOLDER);
		
		TwitterHelper.configureTwitterFactory();
		List<SBUser> users = ObjectifyDatastoreDAO.getInstance().getAllUsers();
		log.info("Fetched " + users.size() + " users");
		ListingFacade.instance().getDiscoverListingList(null);
	}

	public void contextDestroyed(ServletContextEvent event) {
		// App Engine does not currently invoke this method.
	}
}
