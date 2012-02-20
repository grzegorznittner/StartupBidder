package com.startupbidder.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.Notification;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.FrontController;
import com.startupbidder.web.ListingFacade;
import com.startupbidder.web.ServiceFacade;
import com.startupbidder.web.UserMgmtFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
public class HelloServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(HelloServlet.class.getName());
	
	static {
		new FrontController ();
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) {
			resp.setContentType("text/html");
		} else {
			resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
		}
		
		ServiceFacade service = ServiceFacade.instance();
		ObjectifyDatastoreDAO datastore = ServiceFacade.instance().getDAO();
		
		PrintWriter out = resp.getWriter();
		try {
			out.println("<html><head><title>StartupBidder test page</title></head><body>");
			out.println("<p>Hello, " + user.getNickname() + " ..................................");
			out.println("<a href=\"" + userService.createLogoutURL("/hello") + "\">logout</a></p>");
			
//			if (!(user.getNickname().contains("grzegorz.nittner") || user.getNickname().contains("johnarleyburns"))) {
//				out.println("<p>Sorry, you're not authorized to view contents!!!</p>");
//				return;
//			}
			out.println("<a href=\"/setup/\">Setup page</a></p>");
			
			SBUser topInvestor = datastore.getTopInvestor();
			UserVO currentUser = UserMgmtFacade.instance().getLoggedInUserData(user);
			if (currentUser == null) {
				currentUser = UserMgmtFacade.instance().createUser(user);
			}
			ListPropertiesVO listProperties = new ListPropertiesVO();
			listProperties.setMaxResults(1);
			List<Listing> listings = datastore.getAllListings();
			Listing topListing = listings != null ? listings.get(0) : new Listing();
			List<Bid> bids = datastore.getBidsForUser(topInvestor.id);
			List<Listing> usersListings = datastore.getUserActiveListings(currentUser.toKeyId(), listProperties);
			List<Comment> comments = datastore.getCommentsForListing(datastore.getMostDiscussedListings(listProperties).get(0).id);
			
			//testMockDatastore(user, datastore, out);
			out.println("<p>User API:</p>");
			out.println("<a href=\"/user/topinvestor/.json\">Top investor data</a><br/>");
			out.println("<a href=\"/user/loggedin/.json\">Direct link to logged in user data</a><br/>");
			out.println("<a href=\"/user/get/" + currentUser.getId() + "/.json\">Logged in user data via /users/get/ </a><br/>");
			out.println("<a href=\"/user/all/.json\">All users</a><br/>");
			out.println("<form method=\"POST\" action=\"/user/activate/" + currentUser.getId() + "/.json\"><input type=\"submit\" value=\"Activates logged in user\"/></form>");
			out.println("<form method=\"POST\" action=\"/user/deactivate/" + currentUser.getId() + "/.json\"><input type=\"submit\" value=\"Deactivates logged in user\"/></form>");
			out.println("<form method=\"POST\" action=\"/user/up/" + topInvestor.getWebKey() + "/.json\"><input type=\"submit\" value=\"Logged in user votes for top investor\"/></form>");
			out.println("<a href=\"/user/votes/" + currentUser.getId() + "/.json\">Logged in user votes</a><br/>");
			out.println("<form method=\"GET\" action=\"/user/check-user-name/.json\"><input name=\"name\" type=\"text\" value=\"greg\"/>"
					+ "<input type=\"submit\" value=\"Check user name\"/></form>");
			
			out.println("<p>Listings API:</p>");
			out.println("<a href=\"/listings/get/" + topListing.getWebKey() + "/.json\">Top listing data</a><br/>");
			out.println("<form method=\"POST\" action=\"/listing/up/" + topListing.getWebKey() + "/.json\"><input type=\"submit\" value=\"Logged in user votes for top listing (works only once per user)\"/></form>");
			out.println("<form method=\"POST\" action=\"/listing/up/.json\"><input type=\"hidden\" name=\"id\" value=\"" + topListing.getWebKey() + "\"/><input type=\"submit\" value=\"Logged in user votes for top listing (works only once per user), 2nd form\"/></form>");
			out.println("<form method=\"POST\" action=\"/listing/activate/" + topListing.getWebKey() + "/.json\"><input type=\"submit\" value=\"Activate top listing\"/></form>");
			out.println("<form method=\"POST\" action=\"/listing/withdraw/" + topListing.getWebKey() + "/.json\"><input type=\"submit\" value=\"Withdraw top listing\"/></form>");
			out.println("<a href=\"/listings/top/.json?max_results=6\">Top listings</a><br/>");
			out.println("<a href=\"/listings/active/.json?max_results=6\">Active listings</a><br/>");
			out.println("<a href=\"/listings/valuation/.json?max_results=6\">Top valued listings</a><br/>");
			out.println("<a href=\"/listings/popular/.json?max_results=6\">Most popular listings</a><br/>");
			out.println("<a href=\"/listings/latest/.json?max_results=6\">Latest listings</a><br/>");
			out.println("<a href=\"/listings/closing/.json?max_results=6\">Closing listings</a><br/>");
			out.println("<a href=\"/listings/user/" + topInvestor.getWebKey() + "/.json?max_results=6\">Top investor's listings</a><br/>");
			out.println("<form method=\"POST\" action=\"/listing/create/.json\"><textarea name=\"listing\" rows=\"5\" cols=\"100\">"
					+ "{\"title\":\"Listing title\",\"median_valuation\":\"0\",\"num_votes\":\"0\",\"num_bids\":\"0\",\"num_comments\":\"0\",\"profile_id\":\"ag1zdGFydHVwYmlkZGVych4LEgRVc2VyIhQxODU4MDQ3NjQyMjAxMzkxMjQxMQw\",\"profile_username\":\"test@example.com\",\"listing_date\":\"20110802\",\"closing_date\":\"2011-08-01\",\"status\":\"new\",\"suggested_amt\":\"10000\",\"suggested_pct\":\"10\",\"suggested_val\":100000,\"summary\":\"Enter listing summary here.\",\"business_plan_url\":\"\",\"presentation_url\":\"\"}"
					+ "</textarea><input type=\"submit\" value=\"Create a listing\"/></form>");
			out.println("<form method=\"GET\" action=\"/listing/keyword/.json\"><input name=\"text\" type=\"text\" value=\"business\"/>"
					+ "<input type=\"submit\" value=\"Keyword search\"/></form>");

			out.println("<p>Bids API:</p>");
			log.info("Selected bid: " + bids.get(0).toString());
			out.println("<a href=\"/bids/listing/" + topListing.getWebKey() + "/.json?max_results=6\">Bids for top listing</a><br/>");
			out.println("<a href=\"/bids/user/" + topInvestor.getWebKey() + "/.json?max_results=6\">Bids for top investor</a><br/>");
			out.println("<a href=\"/bids/accepted-by-user/" + topInvestor.getWebKey() + "/.json?max_results=6\">Bids accepted by top investor</a><br/>");
			out.println("<a href=\"/bids/funded-by-user/" + topInvestor.getWebKey() + "/.json?max_results=6\">Bids funded by top investor</a><br/>");
			out.println("<a href=\"/bids/get/" + bids.get(0).getWebKey() + "/.json\">Get bid id '" + bids.get(0).getWebKey() + "'</a><br/>");
			out.println("<form method=\"POST\" action=\"/bid/create/.json\"><textarea name=\"bid\" rows=\"5\" cols=\"100\">"
					+ "{ \"listing_id\":\"" + topListing.getWebKey() + "\", \"profile_id\":\"" + topInvestor.getWebKey() + "\", \"amount\":\"14000\", \"equity_pct\":\"10\", \"bid_type\":\"common\", \"interest_rate\":0 }"
					+ "</textarea><input type=\"submit\" value=\"Create a bid\"/></form>");
			out.println("<form method=\"POST\" action=\"/bid/activate/.json\"> <input type=\"hidden\" name=\"id\" value=\"" + bids.get(0).getWebKey() + "\"/><input type=\"submit\" value=\"Activate bid id '" + bids.get(0).getWebKey() + "'\"/></form>");
			out.println("<form method=\"POST\" action=\"/bid/reject/.json\"> <input type=\"hidden\" name=\"id\" value=\"" + bids.get(0).getWebKey() + "\"/><input type=\"submit\" value=\"Reject bid id '" + bids.get(0).getWebKey() + "'\"/></form>");
			out.println("<form method=\"POST\" action=\"/bid/withdraw/.json\"> <input type=\"hidden\" name=\"id\" value=\"" + bids.get(0).getWebKey() + "\"/><input type=\"submit\" value=\"Withdraw bid id '" + bids.get(0).getWebKey() + "'\"/></form>");
			out.println("<form method=\"POST\" action=\"/bid/accept/.json\"> <input type=\"hidden\" name=\"id\" value=\"" + bids.get(0).getWebKey() + "\"/><input type=\"submit\" value=\"Accept bid id '" + bids.get(0).getWebKey() + "' (most likely fails)\"/></form>");
			printAcceptBid(datastore, out, usersListings);
			out.println("<form method=\"POST\" action=\"/bid/paid/.json\"> <input type=\"hidden\" name=\"id\" value=\"" + bids.get(0).getWebKey() + "\"/><input type=\"submit\" value=\"Mark bid as paid, id '" + bids.get(0).getWebKey() + "' (most likely fails)\"/></form>");
			printPayBid(datastore, out, usersListings);
			
			out.println("<a href=\"/bids/statistics/.json\">Get bid statistics (deprecated)</a><br/>");
			out.println("<a href=\"/bids/bid-day-volume/.json\">Get bid day volume</a><br/>");
			out.println("<a href=\"/bids/bid-day-valuation/.json\">Get bid day valuation</a><br/>");
			
			out.println("<p>Comments API:</p>");
			out.println("<a href=\"/comments/listing/" + topListing.getWebKey() + "/.json?max_results=6\">Comments for top listing</a><br/>");
			out.println("<a href=\"/comments/user/" + topInvestor.getWebKey() + "/.json?max_results=6\">Comments for top investor</a><br/>");
			out.println("<a href=\"/comments/get/" + comments.get(0).getWebKey() + "/.json\">Get comment id '" + comments.get(0).getWebKey() + "'</a><br/>");
			out.println("<form method=\"POST\" action=\"/comment/create/.json\"><textarea name=\"comment\" rows=\"5\" cols=\"100\">"
						+ "{ \"listing_id\":\"" + topListing.getWebKey() + "\", \"profile_id\":\"" + topInvestor.getWebKey() + "\", \"text\":\"comment test\" }"
						+ "</textarea><input type=\"submit\" value=\"Create a comment\"/></form>");
			out.println("<form method=\"POST\" action=\"/comment/delete/.json?id=" + comments.get(0).getWebKey() + "\"><input type=\"submit\" value=\"Deletes comment id '" + comments.get(0).getWebKey() + "'\"/></form>");
			out.println("<br/>");
			
			out.println("<p>Notification API:</p>");
			out.println("<a href=\"/notification/user/" + currentUser.getId() + "/.json?max_results=6\">Notifications for current user</a><br/>");
			List<Notification> notifications = datastore.getUserNotification(currentUser.toKeyId(), new ListPropertiesVO());
			if (!notifications.isEmpty()) {
				out.println("<a href=\"/notification/get/" + notifications.get(0).getWebKey() + "/.json\">First notification for current user</a><br/>");
				out.println("<a href=\"/notification/ack/" + notifications.get(0).getWebKey() + "/.json\">Acknowledging first notification for current user</a><br/>");
			} else {
				out.println("Current user doesn't have any notification, create one first</a><br/>");
			}
			out.println("<form method=\"POST\" action=\"/notification/create/.json\"><textarea name=\"notification\" rows=\"5\" cols=\"100\">"
						+ "{ \"object_id\":\"" + topListing.getWebKey() + "\", \"profile_id\":\"" + currentUser.getId() + "\", "
						+ "  \"type\":\"new_listing\", \"message\":\"Sample message\" }"
						+ "</textarea><input type=\"submit\" value=\"Create a notification\"/></form>");
			out.println("<br/>");
			
			out.println("<p>Monitor API:</p>");
			out.println("<a href=\"/monitors/active-for-user/.json?type=Listing\">All active monitors for logged in user</a><br/>");
			out.println("<a href=\"/monitors/active-for-user/.json?\">Active listing monitors for logged in user</a><br/>");
			out.println("<a href=\"/monitors/active-for-object/?type=Listing&id=" + topListing.getWebKey() + "\">Monitors for top listing</a><br/>");
			out.println("<form method=\"POST\" action=\"/monitor/set/.json\"><textarea name=\"monitor\" rows=\"5\" cols=\"100\">"
					+ "{ \"object_id\":\"" + topListing.getWebKey() + "\", \"profile_id\":\"" + currentUser.getId() + "\", \"type\":\"Listing\" }"
					+ "</textarea><input type=\"submit\" value=\"Create a monitor for top listing\"/></form>");
			List<Monitor> monitors = datastore.getMonitorsForUser(currentUser.toKeyId(), null);
			out.println("<form method=\"POST\" action=\"/monitor/deactivate/.json\"" + (monitors.isEmpty() ? " disabled=\"disabled\">" : ">")
					+ "<input type=\"hidden\" name=\"id\" value=\"" + (monitors.isEmpty() ? "empty" : monitors.get(0).getWebKey()) + "\"/>"
					+ "<input type=\"submit\" value=\"Deactivate monitor "
					+ (monitors.isEmpty() ? "(no monitors)" : (monitors.get(0).type + " " + monitors.get(0).object)) + "\"/></form>");
			
			out.println("<p>File API:</p>");
			out.println("<a href=\"/file/get-upload-url/2/.json\">Get upload URL(s)</a><br/>");
			String[] urls = service.createUploadUrls(currentUser, "/file/upload", 3);
			out.println("<form action=\"" + urls[0] + "\" method=\"post\" enctype=\"multipart/form-data\">"
					+ "<input type=\"file\" name=\"" + ListingDoc.Type.BUSINESS_PLAN.toString() + "\"/>"
					+ "<input type=\"submit\" value=\"Upload business plan\"/></form>");
			out.println("<form action=\"" + urls[1] + "\" method=\"post\" enctype=\"multipart/form-data\">"
					+ "<input type=\"file\" name=\"" + ListingDoc.Type.PRESENTATION.toString() + "\"/>"
					+ "<input type=\"submit\" value=\"Upload presentation\"/></form>");
			out.println("<form action=\"" + urls[2] + "\" method=\"post\" enctype=\"multipart/form-data\">"
					+ "<input type=\"file\" name=\"" + ListingDoc.Type.FINANCIALS.toString() + "\"/>"
					+ "<input type=\"submit\" value=\"Upload financials\"/></form>");
			List<ListingDocumentVO> docs = ListingFacade.instance().getAllListingDocuments(currentUser);
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
			if (docs != null && !docs.isEmpty()) {
				out.println("<table border=\"1\"><tr><td colspan=\"2\">Uploaded documents</td></tr>");
				for (ListingDocumentVO doc : docs) {
					out.println("<tr>");
					out.println("<td><a href=\"/file/download/" + doc.getId() + ".json\">Download "
							+ doc.getType() + " uploaded " + fmt.print(doc.getCreated().getTime()) + ", type: " + doc.getType() + "</a></td>");
					out.println("<td><form method=\"POST\" action=\"/file/delete/.json?doc=" + doc.getId() + "\"><input type=\"submit\" value=\"Delete file\"/></form></td>");
					out.println("</tr>");
				}
				out.println("</table>");
			} else {
				out.println("No documents uploaded</br>");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.println("</body></html>");
		}
	}

	private void printAcceptBid(ObjectifyDatastoreDAO datastore, PrintWriter out,
			List<Listing> usersListings) {
		if (usersListings.size() == 0) {
			out.println("Can't test bid accept as user doesn't have any listing.</br>");
		} else {
			boolean validBid = false;
			for (Listing listing : usersListings) {
				List<Bid> bidsForUserListings = datastore.getBidsForListing(listing.id);
				for (Bid bid : bidsForUserListings) {
					if (Bid.Status.ACTIVE.equals(bid.status)) {
						out.println("<form method=\"POST\" action=\"/bid/accept/.json\"> <input type=\"hidden\" name=\"id\" value=\"" + bid.getWebKey() + "\"/><input type=\"submit\" value=\"Accept bid id '" + bid.getWebKey() + "' (should work)\"/></form>");
						validBid = true;
						break;
					}
				}
				if (validBid) {
					break;
				}
			}
			if (!validBid) {
				out.println("Can't test bid accept as user's listings don't have any active bids.</br>");
			}
		}
	}

	private void printPayBid(ObjectifyDatastoreDAO datastore, PrintWriter out,
			List<Listing> usersListings) {
		if (usersListings.size() == 0) {
			out.println("Can't test marking bid as paid as user doesn't have any listing.</br>");
		} else {
			boolean validBid = false;
			for (Listing listing : usersListings) {
				List<Bid> bidsForUserListings = datastore.getBidsForListing(listing.id);
				for (Bid bid : bidsForUserListings) {
					if (Bid.Status.ACCEPTED.equals(bid.status)) {
						out.println("<form method=\"POST\" action=\"/bid/paid/.json\"> <input type=\"hidden\" name=\"id\" value=\"" + bid.getWebKey() + "\"/><input type=\"submit\" value=\"Mark bid id '" + bid.getWebKey() + "' as paid (should work)\"/></form>");
						validBid = true;
						break;
					}
				}
				if (validBid) {
					break;
				}
			}
			if (!validBid) {
				out.println("Can't test marking bid as paid as user's listings don't have any accepted bids.</br>");
			}
		}
	}

	private void testMockDatastore(User user, PrintWriter out) {
		out.println("<p><b>Datastore key function test:</b></p>");
		Key testStringKey = KeyFactory.createKey(Listing.class.getSimpleName(), "bpId");
		Key testLongKey = KeyFactory.createKey(Listing.class.getSimpleName(), 1234L);
		out.println("testStringKey.toString() = " + testStringKey.toString() + "</br>");
		out.println("testLongKey.toString() = " + testLongKey.toString() + "</br>");
		out.println("KeyFactory.keyToString(testStringKey) = " + KeyFactory.keyToString(testStringKey) + "</br>");
		out.println("KeyFactory.keyToString(testLongKey) = " + KeyFactory.keyToString(testLongKey) + "</br>");
		out.println("KeyFactory.stringToKey(KeyFactory.keyToString(testStringKey)) = " + KeyFactory.stringToKey(KeyFactory.keyToString(testStringKey)) + "</br>");
		out.println("KeyFactory.stringToKey(KeyFactory.keyToString(testLongKey)) = " + KeyFactory.stringToKey(KeyFactory.keyToString(testLongKey)) + "</br>");
		
		out.println("<p><b>Current user data:</b></p>");
		SBUser currentUser = ObjectifyDatastoreDAO.getInstance().getUser(user.getNickname());
		out.println("<p>" + currentUser + "</p>");
		
		currentUser.investor = true;
		ObjectifyDatastoreDAO.getInstance().updateUser(currentUser);
		out.println("<p><b>Updated current user data:</b></p>");
		out.println("<p>" + currentUser + "</p>");
		
		ListPropertiesVO listProperties = new ListPropertiesVO();
		listProperties.setMaxResults(10);
		
		ObjectifyDatastoreDAO datastore = ObjectifyDatastoreDAO.getInstance();
		out.println("<p><b>Current user business plans:</b></p>");
		for (Listing bp : datastore.getUserActiveListings(currentUser.id, listProperties)) {
			int rating = datastore.getNumberOfVotesForListing(bp.id);
			int activity = datastore.getActivity(bp.id);
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
			datastore.valueUpListing(bp.id, currentUser.id);
		}
		
		out.println("<p><b>Top business plans:</b></p>");
		for (Listing bp : datastore.getTopListings(listProperties)) {
			int rating = datastore.getNumberOfVotesForListing(bp.id);
			int activity = datastore.getActivity(bp.id);
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
			datastore.valueUpListing(bp.id, currentUser.id);
		}
		out.println("<p><b>Active business plans:</b></p>");
		for (Listing bp : datastore.getActiveListings(listProperties)) {
			int rating = datastore.getNumberOfVotesForListing(bp.id);
			int activity = datastore.getActivity(bp.id);
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
		}
		
		out.println("<p><b>Top business plans (2):</b></p>");
		for (Listing bp : datastore.getTopListings(listProperties)) {
			int rating = datastore.getNumberOfVotesForListing(bp.id);
			int activity = datastore.getActivity(bp.id);
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
			datastore.valueUpListing(bp.id, currentUser.id);
		}
		out.println("<p><b>Active business plans (2):</b></p>");
		for (Listing bp : datastore.getActiveListings(listProperties)) {
			int rating = datastore.getNumberOfVotesForListing(bp.id);
			int activity = datastore.getActivity(bp.id);
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
		}

		Listing topBP = datastore.getTopListings(listProperties).get(0);
		out.println("<p><b>Bids for top business plan '" + topBP + "</b></p>");
		for (Bid bid : datastore.getBidsForListing(topBP.id)) {
			out.println("<p>" + bid + "</p>");
		}

		Listing topActiveBP = datastore.getActiveListings(listProperties).get(0);
		out.println("<p><b>Comments for most active business plan '" + topActiveBP + "</b></p>");
		for (Comment comment : datastore.getCommentsForListing(topActiveBP.id)) {
			out.println("<p>" + comment + "</p>");
		}
	}

}
