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
import com.startupbidder.dao.DatastoreDAO;
import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.ListingDocumentDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.ServiceFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
public class HelloServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(HelloServlet.class.getName());
	
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
		DatastoreDAO datastore = ServiceFacade.instance().getDAO();
		
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
			
			UserDTO topInvestor = datastore.getTopInvestor();
			UserVO currentUser = service.getLoggedInUserData(user);
			if (currentUser == null) {
				currentUser = service.createUser(user);
			}
			ListPropertiesVO listProperties = new ListPropertiesVO();
			listProperties.setMaxResults(1);
			ListingDTO topListing = datastore.getTopListings(listProperties).get(0);
			List<BidDTO> bids = datastore.getBidsForUser(topInvestor.getIdAsString());
			List<ListingDTO> usersListings = datastore.getUserActiveListings(currentUser.getId(), listProperties);
			List<CommentDTO> comments = datastore.getCommentsForListing(datastore.getMostDiscussedListings(listProperties).get(0).getIdAsString());
			
			//testMockDatastore(user, datastore, out);
			out.println("<p>User API:</p>");
			out.println("<a href=\"/user/topinvestor/.json\">Top investor data</a><br/>");
			out.println("<a href=\"/user/loggedin/.json\">Direct link to logged in user data</a><br/>");
			out.println("<a href=\"/user/get/" + currentUser.getId() + "/.json\">Logged in user data via /users/get/ </a><br/>");
			out.println("<a href=\"/user/all/.json\">All users</a><br/>");
			out.println("<form method=\"POST\" action=\"/user/activate/" + currentUser.getId() + "/.json\"><input type=\"submit\" value=\"Activates logged in user\"/></form>");
			out.println("<form method=\"POST\" action=\"/user/deactivate/" + currentUser.getId() + "/.json\"><input type=\"submit\" value=\"Deactivates logged in user\"/></form>");
			out.println("<form method=\"POST\" action=\"/user/up/" + topInvestor.getIdAsString() + "/.json\"><input type=\"submit\" value=\"Logged in user votes for top investor\"/></form>");
			out.println("<a href=\"/user/votes/" + currentUser.getId() + "/.json\">Logged in user votes</a><br/>");
			out.println("<form method=\"GET\" action=\"/user/check-user-name/.json\"><input name=\"name\" type=\"text\" value=\"greg\"/>"
					+ "<input type=\"submit\" value=\"Check user name\"/></form>");
			
			out.println("<p>Listings API:</p>");
			out.println("<a href=\"/listings/get/" + topListing.getIdAsString() + "/.json\">Top listing data</a><br/>");
			out.println("<form method=\"POST\" action=\"/listing/up/" + topListing.getIdAsString() + "/.json\"><input type=\"submit\" value=\"Logged in user votes for top listing (works only once per user)\"/></form>");
			out.println("<form method=\"POST\" action=\"/listing/up/.json\"><input type=\"hidden\" name=\"id\" value=\"" + topListing.getIdAsString() + "\"/><input type=\"submit\" value=\"Logged in user votes for top listing (works only once per user), 2nd form\"/></form>");
			out.println("<form method=\"POST\" action=\"/listing/activate/" + topListing.getIdAsString() + "/.json\"><input type=\"submit\" value=\"Activate top listing\"/></form>");
			out.println("<form method=\"POST\" action=\"/listing/withdraw/" + topListing.getIdAsString() + "/.json\"><input type=\"submit\" value=\"Withdraw top listing\"/></form>");
			out.println("<a href=\"/listings/top/.json?max_results=6\">Top listings</a><br/>");
			out.println("<a href=\"/listings/active/.json?max_results=6\">Active listings</a><br/>");
			out.println("<a href=\"/listings/valuation/.json?max_results=6\">Top valued listings</a><br/>");
			out.println("<a href=\"/listings/popular/.json?max_results=6\">Most popular listings</a><br/>");
			out.println("<a href=\"/listings/latest/.json?max_results=6\">Latest listings</a><br/>");
			out.println("<a href=\"/listings/closing/.json?max_results=6\">Closing listings</a><br/>");
			out.println("<a href=\"/listings/user/" + topInvestor.getIdAsString() + "/.json?max_results=6\">Top investor's listings</a><br/>");
			out.println("<form method=\"POST\" action=\"/listing/create/.json\"><textarea name=\"listing\" rows=\"5\" cols=\"100\">"
					+ "{\"title\":\"Listing title\",\"median_valuation\":\"0\",\"num_votes\":\"0\",\"num_bids\":\"0\",\"num_comments\":\"0\",\"profile_id\":\"ag1zdGFydHVwYmlkZGVych4LEgRVc2VyIhQxODU4MDQ3NjQyMjAxMzkxMjQxMQw\",\"profile_username\":\"test@example.com\",\"listing_date\":\"20110802\",\"closing_date\":\"2011-08-01\",\"status\":\"new\",\"suggested_amt\":\"10000\",\"suggested_pct\":\"10\",\"suggested_val\":100000,\"summary\":\"Enter listing summary here.\",\"business_plan_url\":\"\",\"presentation_url\":\"\"}"
					+ "</textarea><input type=\"submit\" value=\"Create a listing\"/></form>");

			out.println("<p>Bids API:</p>");
			log.info("Selected bid: " + bids.get(0).toString());
			out.println("<a href=\"/bids/listing/" + topListing.getIdAsString() + "/.json?max_results=6\">Bids for top listing</a><br/>");
			out.println("<a href=\"/bids/user/" + topInvestor.getIdAsString() + "/.json?max_results=6\">Bids for top investor</a><br/>");
			out.println("<a href=\"/bids/accepted-by-user/" + topInvestor.getIdAsString() + "/.json?max_results=6\">Bids accepted by top investor</a><br/>");
			out.println("<a href=\"/bids/funded-by-user/" + topInvestor.getIdAsString() + "/.json?max_results=6\">Bids funded by top investor</a><br/>");
			out.println("<a href=\"/bids/get/" + bids.get(0).getIdAsString() + "/.json\">Get bid id '" + bids.get(0).getIdAsString() + "'</a><br/>");
			out.println("<form method=\"POST\" action=\"/bid/create/.json\"><textarea name=\"bid\" rows=\"5\" cols=\"100\">"
					+ "{ \"listing_id\":\"" + topListing.getIdAsString() + "\", \"profile_id\":\"" + topInvestor.getIdAsString() + "\", \"amount\":\"14000\", \"equity_pct\":\"10\", \"bid_type\":\"common\", \"interest_rate\":0 }"
					+ "</textarea><input type=\"submit\" value=\"Create a bid\"/></form>");
			out.println("<form method=\"POST\" action=\"/bid/activate/.json\"> <input type=\"hidden\" name=\"id\" value=\"" + bids.get(0).getIdAsString() + "\"/><input type=\"submit\" value=\"Activate bid id '" + bids.get(0).getIdAsString() + "'\"/></form>");
			out.println("<form method=\"POST\" action=\"/bid/reject/.json\"> <input type=\"hidden\" name=\"id\" value=\"" + bids.get(0).getIdAsString() + "\"/><input type=\"submit\" value=\"Reject bid id '" + bids.get(0).getIdAsString() + "'\"/></form>");
			out.println("<form method=\"POST\" action=\"/bid/withdraw/.json\"> <input type=\"hidden\" name=\"id\" value=\"" + bids.get(0).getIdAsString() + "\"/><input type=\"submit\" value=\"Withdraw bid id '" + bids.get(0).getIdAsString() + "'\"/></form>");
			out.println("<form method=\"POST\" action=\"/bid/accept/.json\"> <input type=\"hidden\" name=\"id\" value=\"" + bids.get(0).getIdAsString() + "\"/><input type=\"submit\" value=\"Accept bid id '" + bids.get(0).getIdAsString() + "' (most likely fails)\"/></form>");
			printAcceptBid(datastore, out, usersListings);
			out.println("<form method=\"POST\" action=\"/bid/paid/.json\"> <input type=\"hidden\" name=\"id\" value=\"" + bids.get(0).getIdAsString() + "\"/><input type=\"submit\" value=\"Mark bid as paid, id '" + bids.get(0).getIdAsString() + "' (most likely fails)\"/></form>");
			printPayBid(datastore, out, usersListings);
			
			out.println("<a href=\"/bids/statistics/.json\">Get bid statistics (deprecated)</a><br/>");
			out.println("<a href=\"/bids/bid-day-volume/.json\">Get bid day volume</a><br/>");
			out.println("<a href=\"/bids/bid-day-valuation/.json\">Get bid day valuation</a><br/>");
			
			out.println("<p>Comments API:</p>");
			out.println("<a href=\"/comments/listing/" + topListing.getIdAsString() + "/.json?max_results=6\">Comments for top listing</a><br/>");
			out.println("<a href=\"/comments/user/" + topInvestor.getIdAsString() + "/.json?max_results=6\">Comments for top investor</a><br/>");
			out.println("<a href=\"/comments/get/" + comments.get(0).getIdAsString() + "/.json\">Get comment id '" + comments.get(0).getIdAsString() + "'</a><br/>");
			out.println("<form method=\"POST\" action=\"/comment/create/.json\"><textarea name=\"comment\" rows=\"5\" cols=\"100\">"
						+ "{ \"listing_id\":\"" + topListing.getIdAsString() + "\", \"profile_id\":\"" + topInvestor.getIdAsString() + "\", \"text\":\"comment test\" }"
						+ "</textarea><input type=\"submit\" value=\"Create a comment\"/></form>");
			out.println("<form method=\"POST\" action=\"/comment/delete/.json?id=" + comments.get(0).getIdAsString() + "\"><input type=\"submit\" value=\"Deletes comment id '" + comments.get(0).getIdAsString() + "'\"/></form>");
			out.println("<br/>");
			
			out.println("<p>File API:</p>");
			out.println("<a href=\"/file/get-upload-url/2/.json\">Get upload URL(s)</a><br/>");
			String[] urls = service.createUploadUrls(currentUser, "/file/upload", 3);
			out.println("<form action=\"" + urls[0] + "\" method=\"post\" enctype=\"multipart/form-data\">"
					+ "<input type=\"file\" name=\"" + ListingDocumentDTO.Type.BUSINESS_PLAN.toString() + "\"/>"
					+ "<input type=\"submit\" value=\"Upload business plan\"/></form>");
			out.println("<form action=\"" + urls[1] + "\" method=\"post\" enctype=\"multipart/form-data\">"
					+ "<input type=\"file\" name=\"" + ListingDocumentDTO.Type.PRESENTATION.toString() + "\"/>"
					+ "<input type=\"submit\" value=\"Upload presentation\"/></form>");
			out.println("<form action=\"" + urls[2] + "\" method=\"post\" enctype=\"multipart/form-data\">"
					+ "<input type=\"file\" name=\"" + ListingDocumentDTO.Type.FINANCIALS.toString() + "\"/>"
					+ "<input type=\"submit\" value=\"Upload financials\"/></form>");
			List<ListingDocumentVO> docs = service.getAllListingDocuments(currentUser);
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

	private void printAcceptBid(DatastoreDAO datastore, PrintWriter out,
			List<ListingDTO> usersListings) {
		if (usersListings.size() == 0) {
			out.println("Can't test bid accept as user doesn't have any listing.</br>");
		} else {
			boolean validBid = false;
			for (ListingDTO listing : usersListings) {
				List<BidDTO> bidsForUserListings = datastore.getBidsForListing(listing.getIdAsString());
				for (BidDTO bid : bidsForUserListings) {
					if (BidDTO.Status.ACTIVE.equals(bid.getStatus())) {
						out.println("<form method=\"POST\" action=\"/bid/accept/.json\"> <input type=\"hidden\" name=\"id\" value=\"" + bid.getIdAsString() + "\"/><input type=\"submit\" value=\"Accept bid id '" + bid.getIdAsString() + "' (should work)\"/></form>");
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

	private void printPayBid(DatastoreDAO datastore, PrintWriter out,
			List<ListingDTO> usersListings) {
		if (usersListings.size() == 0) {
			out.println("Can't test marking bid as paid as user doesn't have any listing.</br>");
		} else {
			boolean validBid = false;
			for (ListingDTO listing : usersListings) {
				List<BidDTO> bidsForUserListings = datastore.getBidsForListing(listing.getIdAsString());
				for (BidDTO bid : bidsForUserListings) {
					if (BidDTO.Status.ACCEPTED.equals(bid.getStatus())) {
						out.println("<form method=\"POST\" action=\"/bid/paid/.json\"> <input type=\"hidden\" name=\"id\" value=\"" + bid.getIdAsString() + "\"/><input type=\"submit\" value=\"Mark bid id '" + bid.getIdAsString() + "' as paid (should work)\"/></form>");
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

	private void testMockDatastore(User user, DatastoreDAO datastore, PrintWriter out) {
		out.println("<p><b>Datastore key function test:</b></p>");
		Key testStringKey = KeyFactory.createKey(ListingDTO.class.getSimpleName(), "bpId");
		Key testLongKey = KeyFactory.createKey(ListingDTO.class.getSimpleName(), 1234L);
		out.println("testStringKey.toString() = " + testStringKey.toString() + "</br>");
		out.println("testLongKey.toString() = " + testLongKey.toString() + "</br>");
		out.println("KeyFactory.keyToString(testStringKey) = " + KeyFactory.keyToString(testStringKey) + "</br>");
		out.println("KeyFactory.keyToString(testLongKey) = " + KeyFactory.keyToString(testLongKey) + "</br>");
		out.println("KeyFactory.stringToKey(KeyFactory.keyToString(testStringKey)) = " + KeyFactory.stringToKey(KeyFactory.keyToString(testStringKey)) + "</br>");
		out.println("KeyFactory.stringToKey(KeyFactory.keyToString(testLongKey)) = " + KeyFactory.stringToKey(KeyFactory.keyToString(testLongKey)) + "</br>");
		
		out.println("<p><b>Current user data:</b></p>");
		UserDTO currentUser = datastore.getUser(user.getNickname());
		out.println("<p>" + currentUser + "</p>");
		
		currentUser.setInvestor(true);
		currentUser.setOrganization("Mock organization");
		datastore.updateUser(currentUser);
		out.println("<p><b>Updated current user data:</b></p>");
		out.println("<p>" + currentUser + "</p>");
		
		ListPropertiesVO listProperties = new ListPropertiesVO();
		listProperties.setMaxResults(10);
		
		out.println("<p><b>Current user business plans:</b></p>");
		for (ListingDTO bp : datastore.getUserActiveListings(currentUser.getIdAsString(), listProperties)) {
			int rating = datastore.getNumberOfVotesForListing(bp.getIdAsString());
			int activity = datastore.getActivity(bp.getIdAsString());
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
			datastore.valueUpListing(bp.getIdAsString(), currentUser.getIdAsString());
		}
		
		out.println("<p><b>Top business plans:</b></p>");
		for (ListingDTO bp : datastore.getTopListings(listProperties)) {
			int rating = datastore.getNumberOfVotesForListing(bp.getIdAsString());
			int activity = datastore.getActivity(bp.getIdAsString());
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
			datastore.valueUpListing(bp.getIdAsString(), currentUser.getIdAsString());
		}
		out.println("<p><b>Active business plans:</b></p>");
		for (ListingDTO bp : datastore.getActiveListings(listProperties)) {
			int rating = datastore.getNumberOfVotesForListing(bp.getIdAsString());
			int activity = datastore.getActivity(bp.getIdAsString());
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
		}
		
		out.println("<p><b>Top business plans (2):</b></p>");
		for (ListingDTO bp : datastore.getTopListings(listProperties)) {
			int rating = datastore.getNumberOfVotesForListing(bp.getIdAsString());
			int activity = datastore.getActivity(bp.getIdAsString());
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
			datastore.valueUpListing(bp.getIdAsString(), currentUser.getIdAsString());
		}
		out.println("<p><b>Active business plans (2):</b></p>");
		for (ListingDTO bp : datastore.getActiveListings(listProperties)) {
			int rating = datastore.getNumberOfVotesForListing(bp.getIdAsString());
			int activity = datastore.getActivity(bp.getIdAsString());
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
		}

		ListingDTO topBP = datastore.getTopListings(listProperties).get(0);
		out.println("<p><b>Bids for top business plan '" + topBP + "</b></p>");
		for (BidDTO bid : datastore.getBidsForListing(topBP.getIdAsString())) {
			out.println("<p>" + bid + "</p>");
		}

		ListingDTO topActiveBP = datastore.getActiveListings(listProperties).get(0);
		out.println("<p><b>Comments for most active business plan '" + topActiveBP + "</b></p>");
		for (CommentDTO comment : datastore.getCommentsForListing(topActiveBP.getIdAsString())) {
			out.println("<p>" + comment + "</p>");
		}
	}

}
