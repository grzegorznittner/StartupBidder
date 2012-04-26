package com.startupbidder.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.datanucleus.util.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
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
import com.startupbidder.vo.ErrorCodes;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingAndUserVO;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.ListingListVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.UserListVO;
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
			
			UserVO currentUser = UserMgmtFacade.instance().getLoggedInUserData(user);
			if (currentUser == null) {
				currentUser = UserMgmtFacade.instance().createUser(user);
			}
			currentUser.setAdmin(userService.isUserAdmin());
			
			ListPropertiesVO listProperties = new ListPropertiesVO();
			listProperties.setMaxResults(1);
			List<Listing> listings = datastore.getAllListings();
			Listing topListing = listings != null ? listings.get(0) : new Listing();

			List<SBUser> users = datastore.getAllUsers();

			List<Listing> usersListings = datastore.getUserActiveListings(currentUser.toKeyId(), listProperties);
			List<Comment> comments = datastore.getCommentsForListing(datastore.getMostDiscussedListings(listProperties).get(0).id);
			
			//testMockDatastore(user, datastore, out);
			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">User API:</p>");
			out.println("<a href=\"/user/topinvestor/.json\">Top investor data</a><br/>");
			out.println("<a href=\"/user/loggedin/.json\">Direct link to logged in user data</a><br/>");
			out.println("<a href=\"/user/get/" + currentUser.getId() + "/.json\">Logged in user data via /users/get/ </a><br/>");
			out.println("<a href=\"/user/all/.json\">All users</a><br/>");
			out.println("<form method=\"POST\" action=\"/user/activate/" + currentUser.getId() + "/.json\"><input type=\"submit\" value=\"Activates logged in user\"/></form>");
			out.println("<form method=\"POST\" action=\"/user/deactivate/" + currentUser.getId() + "/.json\"><input type=\"submit\" value=\"Deactivates logged in user\"/></form>");
			out.println("<a href=\"/user/votes/" + currentUser.getId() + "/.json\">Logged in user votes</a><br/>");
			out.println("<form method=\"GET\" action=\"/user/check-user-name/.json\"><input name=\"name\" type=\"text\" value=\"greg\"/>"
					+ "<input type=\"submit\" value=\"Check user name\"/></form>");
			
			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">Listings API:</p>");
			out.println("<a href=\"/listings/discover/.json\">Discover listings</a><br/>");
			out.println("<a href=\"/listings/discover_user/.json\">Discover user's listings</a><br/>");
			out.println("<a href=\"/listings/get/" + topListing.getWebKey() + "/.json\">Top listing data</a><br/>");
			out.println("<form method=\"POST\" action=\"/listing/up/" + topListing.getWebKey() + "/.json\"><input type=\"submit\" value=\"Logged in user votes for top listing (works only once per user)\"/></form>");
			out.println("<form method=\"POST\" action=\"/listing/up/.json\"><input type=\"hidden\" name=\"id\" value=\"" + topListing.getWebKey() + "\"/><input type=\"submit\" value=\"Logged in user votes for top listing (works only once per user), 2nd form\"/></form>");
			out.println("<br/><a href=\"/listings/categories/.json\">All categories</a><br/>");
			out.println("<br/><a href=\"/listings/used_categories/.json\">Used categories</a><br/>");
			out.println("<br/><a href=\"/listings/locations/.json\">Top locations</a><br/>");
			out.println("<br/><a href=\"/listings/all-listing-locations/.json\">All listing locations</a><br/><br/>");
			out.println("<form method=\"POST\" action=\"/listing/create/.json\"><input type=\"submit\" value=\"Creates NEW listing\"/></form>");
			out.println("<form method=\"POST\" action=\"/listing/delete/.json\"><input type=\"submit\" value=\"Deletes edited (NEW) listing\"/></form>");
			
			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">Editing new listing. Update methods:</p>");
			ListingAndUserVO editedListing = ListingFacade.instance().createListing(currentUser);
			if (editedListing.getListing() != null) {
				out.println("<form method=\"POST\" action=\"/listing/update_field/.json\"><textarea name=\"listing\" rows=\"1\" cols=\"100\">"
						+ "{\"title\":\"" + editedListing.getListing().getName() + "\"}"
						+ "</textarea><input type=\"submit\" value=\"Update title\"/></form>");
				out.println("<form method=\"POST\" action=\"/listing/update_field/.json\"><textarea name=\"listing\" rows=\"1\" cols=\"100\">"
						+ "{\"mantra\":\"" + editedListing.getListing().getMantra() + "\"}"
						+ "</textarea><input type=\"submit\" value=\"Update mantra\"/></form>");
				out.println("<form method=\"POST\" action=\"/listing/update_field/.json\"><textarea name=\"listing\" rows=\"1\" cols=\"100\">"
						+ "{\"summary\":\"" + editedListing.getListing().getSummary() + "\"}"
						+ "</textarea><input type=\"submit\" value=\"Update summary\"/></form>");
				out.println("<form method=\"POST\" action=\"/listing/update_field/.json\"><textarea name=\"listing\" rows=\"1\" cols=\"100\">"
						+ "{\"suggested_amt\":\"" + editedListing.getListing().getSuggestedAmount() + "\"}"
						+ "</textarea><input type=\"submit\" value=\"Update suggested amount\"/></form>");
				out.println("<form method=\"POST\" action=\"/listing/update_field/.json\"><textarea name=\"listing\" rows=\"1\" cols=\"100\">"
						+ "{\"logo_url\":\"http://mcsearcher.files.wordpress.com/2008/12/sexy-girl-7.jpg\"}"
						+ "</textarea><input type=\"submit\" value=\"Update logo from URL\"/></form>");
				out.println("<p>Updatable field names: " + ListingVO.UPDATABLE_PROPERTIES + "</p>");
				out.println("<p>Fields which can be set by fetching external resource: " + ListingVO.FETCHED_PROPERTIES + "</p>");

				String[] urls = service.createUploadUrls(currentUser, "/file/upload", 4);
				out.println("<form action=\"" + urls[0] + "\" method=\"post\" enctype=\"multipart/form-data\">"
						+ "<input type=\"file\" name=\"" + ListingDoc.Type.BUSINESS_PLAN.toString() + "\"/>"
						+ "<input type=\"submit\" value=\"Upload business plan\"/></form>");
				out.println("<form action=\"" + urls[1] + "\" method=\"post\" enctype=\"multipart/form-data\">"
						+ "<input type=\"file\" name=\"" + ListingDoc.Type.PRESENTATION.toString() + "\"/>"
						+ "<input type=\"submit\" value=\"Upload presentation\"/></form>");
				out.println("<form action=\"" + urls[2] + "\" method=\"post\" enctype=\"multipart/form-data\">"
						+ "<input type=\"file\" name=\"" + ListingDoc.Type.FINANCIALS.toString() + "\"/>"
						+ "<input type=\"submit\" value=\"Upload financials\"/></form>");
				out.println("<form action=\"" + urls[3] + "\" method=\"post\" enctype=\"multipart/form-data\">"
						+ "<input type=\"file\" name=\"" + ListingDoc.Type.LOGO.toString() + "\"/>"
						+ "<input type=\"submit\" value=\"Upload logo\"/></form>");
			} else {
				out.println("<div><b>Listing doesn't exist.</b> Be aware that this page is calling automatically create method, so edited listing should be always available.</div>");
			}

			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">Listings by categories</p>");
			
			out.println("<form method=\"POST\" action=\"/listing/post/.json\"><input type=\"submit\" value=\"Submits edited listing (sets POST status)\"/></form>");
			out.println("<br/><a href=\"/listings/posted/.json?max_results=6\">Posted listings (admins only)</a><br/>");
			ListingListVO postedListings = ListingFacade.instance().getPostedListings(currentUser, listProperties);
			printPostedListings(out, currentUser, postedListings);
			
			out.println("<br/><a href=\"/listings/closing/.json?max_results=6\">Closing listings</a><br/>");
			ListingListVO activeListings = ListingFacade.instance().getClosingActiveListings(currentUser, listProperties);
			printActiveListings(out, currentUser, activeListings);
			
			out.println("<br/><a href=\"/listings/frozen/.json?max_results=6\">Frozen listings</a><br/>");
			ListingListVO frozenListings = ListingFacade.instance().getFrozenListings(currentUser, listProperties);
			printFrozenListings(out, currentUser, frozenListings);
			
			out.println("<a href=\"/listings/top/.json?max_results=6\">Top listings</a><br/>");
			out.println("<a href=\"/listings/valuation/.json?max_results=6\">Top valued listings</a><br/>");
			out.println("<a href=\"/listings/popular/.json?max_results=6\">Most popular listings</a><br/>");
			out.println("<a href=\"/listings/latest/.json?max_results=6\">Latest listings</a><br/>");
			out.println("<a href=\"/listings/closing/.json?max_results=6\">Closing listings</a><br/>");
			out.println("<a href=\"/listings/monitored/.json?max_results=6\">Monitored listings (by logged in user)</a><br/>");
			out.println("<form method=\"GET\" action=\"/listing/keyword/.json\"><input name=\"text\" type=\"text\" value=\"business\"/>"
					+ "<input type=\"submit\" value=\"Keyword search\"/></form>");

			/* out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">Bids API:</p>");
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
			
			*/
			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">Comments API:</p>");
			out.println("<a href=\"/comments/listing/" + topListing.getWebKey() + "/.json?max_results=6\">Comments for top listing</a><br/>");
			out.println("<a href=\"/comments/user/" + currentUser.getId() + "/.json?max_results=6\">Comments for current user</a><br/>");
			out.println("<a href=\"/comments/get/" + comments.get(0).getWebKey() + "/.json\">Get comment id '" + comments.get(0).getWebKey() + "'</a><br/>");
			out.println("<form method=\"POST\" action=\"/comment/create/.json\"><textarea name=\"comment\" rows=\"5\" cols=\"100\">"
						+ "{ \"listing_id\":\"" + topListing.getWebKey() + "\", \"profile_id\":\"" + currentUser.getId() + "\", \"text\":\"comment test\" }"
						+ "</textarea><input type=\"submit\" value=\"Create a comment (for top listing)\"/></form>");
			out.println("<form method=\"POST\" action=\"/comment/delete/.json?id=" + comments.get(0).getWebKey() + "\"><input type=\"submit\" value=\"Deletes comment id '" + comments.get(0).getWebKey() + "'\"/></form>");
			out.println("<br/>");
			
			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">Notification API:</p>");
			out.println("<a href=\"/notification/user/.json?max_results=6\">Notifications for current user</a><br/>");
			List<Notification> notifications = datastore.getAllUserNotification(currentUser.toKeyId(), new ListPropertiesVO());
			if (!notifications.isEmpty()) {
				out.println("<a href=\"/notification/get/" + notifications.get(0).getWebKey() + "/.json\">First notification for current user</a><br/>");
				out.println("<a href=\"/notification/ack/" + notifications.get(0).getWebKey() + "/.json\">Acknowledging first notification for current user</a><br/>");
			} else {
				out.println("Current user doesn't have any notification, create one first (eg. make a comment)<br/>");
			}
			out.println("<br/>");
			
			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">Monitor API:</p>");
			out.println("Examples for setting and deactivating monitors you'll find in active listing section.<br/>");
			out.println("<a href=\"/monitors/active-for-user/.json?\">Active monitors for logged in user</a><br/>");
			out.println("<a href=\"/monitors/active-for-listing/?id=" + topListing.getWebKey() + "\">Monitors for top listing</a><br/>");
			
			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">File API:</p>");
			out.println("<a href=\"/file/get-upload-url/2/.json\">Get upload URL(s)</a><br/>");
			List<ListingDocumentVO> docs = ListingFacade.instance().getAllListingDocuments(currentUser);
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
			if (docs != null && !docs.isEmpty()) {
				BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
				
				out.println("<table border=\"1\"><tr><td colspan=\"2\">Uploaded documents</td></tr>");
				for (ListingDocumentVO doc : docs) {
					out.println("<tr>");
					out.println("<td>");
					if (ListingDoc.Type.LOGO.toString().equalsIgnoreCase(doc.getType())) {
						BlobInfo logoInfo = new BlobInfoFactory().loadBlobInfo(doc.getBlob());
						if (logoInfo != null) {
							byte logo[] = blobstoreService.fetchData(doc.getBlob(), 0, logoInfo.getSize() - 1);
							out.println("<img src=\"" + ListingFacade.instance().convertLogoToBase64(logo) + "\"/>");
						}
					}
					out.println("<a href=\"/file/download/" + doc.getId() + ".json\">Download "
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

	private void printPostedListings(PrintWriter out, UserVO currentUser, ListingListVO postedListings) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
		out.println("<table border=\"1\" width=\"100%\">");
		out.println("<tr><td colspan=\"3\"><b>Posted listings</b></td></tr>");

		if (postedListings == null || postedListings.getListings() == null || postedListings.getListings().size() == 0
				|| postedListings.getErrorCode() != ErrorCodes.OK) {
			out.println("<tr><td colspan=\"3\"><b>No posted listings or user is not admin!.<b><small>" + postedListings + "</small></td></tr>");
			out.println("</table>");
			return;
		}

		for (ListingVO listing : postedListings.getListings()) {
			out.println("<tr><td>" + listing.getName() + " posted by " + listing.getOwnerName() + "</td>");
			out.println("<td><form method=\"POST\" action=\"/listing/activate/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Activate\"/></form>");
			out.println("<form method=\"POST\" action=\"/listing/send_back/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Send back\"/></form>");
			out.println("<a href=\"/listing/get/" + listing.getId() + ".json?\">View</a></td>");
			out.println("<td>");
			List<ListingDocumentVO> docs = getListingDocs(currentUser, listing);
			if (docs != null && !docs.isEmpty()) {
				BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
				
				out.println("<table border=\"1\"><tr><td colspan=\"2\"><center>Uploaded documents</center></td></tr>");
				for (ListingDocumentVO doc : docs) {
					out.println("<tr>");
					out.println("<td>");
					if (ListingDoc.Type.LOGO.toString().equalsIgnoreCase(doc.getType())) {
						BlobInfo logoInfo = new BlobInfoFactory().loadBlobInfo(doc.getBlob());
						if (logoInfo != null) {
							byte logo[] = blobstoreService.fetchData(doc.getBlob(), 0, logoInfo.getSize() - 1);
							out.println("<img src=\"" + ListingFacade.instance().convertLogoToBase64(logo) + "\"/>");
						}
					}
					out.println("<a href=\"/file/download/" + doc.getId() + ".json\">Download "
							+ doc.getType() + " uploaded " + fmt.print(doc.getCreated().getTime()) + ", type: " + doc.getType() + "</a></td>");
					out.println("<td><form method=\"POST\" action=\"/listing/delete_file/.json?id="
							+ listing.getId() + "&type=" + doc.getType() + "\"><input type=\"submit\" value=\"Delete\"/></form>");
					out.println("</td></tr>");
				}
				out.println("</table>");
			} else {
				out.println("No documents uploaded");
			}
			out.println("<td>");
			out.println("</tr>");
		}
		out.println("</table>");
	}

	private void printActiveListings(PrintWriter out, UserVO currentUser, ListingListVO activeListings) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
		out.println("<table border=\"1\" width=\"100%\">");
		out.println("<tr><td colspan=\"3\"><b>Active listings</b></td>");
		
		if (activeListings == null || activeListings.getListings() == null || activeListings.getListings().size() == 0
				|| activeListings.getErrorCode() != ErrorCodes.OK) {
			out.println("<tr><td colspan=\"3\"><b>No active listings.<b><small>" + activeListings + "</small></td></tr>");
			out.println("</table>");
			return;
		}

		for (ListingVO listing : activeListings.getListings()) {
			out.println("<tr><td>" + listing.getName() + " posted by " + listing.getOwnerName() + "</td>");
			out.println("<td><form method=\"POST\" action=\"/listing/withdraw/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Withdraw\"/></form>");
			out.println("<form method=\"POST\" action=\"/listing/freeze/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Freeze\"/></form>");
			if (currentUser != null) {
				Monitor monitor = ObjectifyDatastoreDAO.getInstance().getListingMonitor(currentUser.toKeyId(), listing.toKeyId());
				if (monitor != null && monitor.active) {
					out.println("<form method=\"POST\" action=\"/monitor/deactivate/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Deactivate monitor\"/></form>");
				} else {
					out.println("<form method=\"POST\" action=\"/monitor/set/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Set monitor\"/></form>");
				}
			}
			out.println("<a href=\"/listing/get/" + listing.getId() + ".json?\">View</a>");
			out.println("<a href=\"/listing/messages/" + listing.getId() + ".json?\">Listing notifications</a></td>");
			out.println("<td>");
			List<ListingDocumentVO> docs = getListingDocs(currentUser, listing);
			if (docs != null && !docs.isEmpty()) {
				BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
				
				out.println("<table border=\"1\"><tr><td colspan=\"2\">Uploaded documents</td></tr>");
				for (ListingDocumentVO doc : docs) {
					out.println("<tr>");
					out.println("<td>");
					if (ListingDoc.Type.LOGO.toString().equalsIgnoreCase(doc.getType())) {
						BlobInfo logoInfo = new BlobInfoFactory().loadBlobInfo(doc.getBlob());
						if (logoInfo != null) {
							byte logo[] = blobstoreService.fetchData(doc.getBlob(), 0, logoInfo.getSize() - 1);
							out.println("<img src=\"" + ListingFacade.instance().convertLogoToBase64(logo) + "\"/>");
						}
					}
					out.println("<a href=\"/listing/logo/" + listing.getId() + ".json?\">View logo</a></td>");
					out.println("<a href=\"/file/download/" + doc.getId() + ".json\">Download "
							+ doc.getType() + " uploaded " + fmt.print(doc.getCreated().getTime()) + ", type: " + doc.getType() + "</a></td>");
					out.println("<td><form method=\"POST\" action=\"/listing/delete_file/.json?id="
							+ listing.getId() + "&type=" + doc.getType() + "\"><input type=\"submit\" value=\"Delete\"/></form>");
					out.println("</td></tr>");
				}
				out.println("</table>");
			} else {
				out.println("No documents uploaded");
			}
			out.println("<td>");
			out.println("</tr>");
		}
		out.println("</table>");
	}

	private void printFrozenListings(PrintWriter out, UserVO currentUser, ListingListVO frozenListings) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
		out.println("<table border=\"1\" width=\"100%\">");
		out.println("<tr><td colspan=\"3\"><b>Frozen listings</b></td>");
		
		if (frozenListings == null || frozenListings.getListings() == null || frozenListings.getListings().size() == 0
				|| frozenListings.getErrorCode() != ErrorCodes.OK) {
			out.println("<tr><td colspan=\"3\"><b>No frozen listings or user is not admin!.<b><small>" + frozenListings + "</small></td></tr>");
			out.println("</table>");
			return;
		}

		for (ListingVO listing : frozenListings.getListings()) {
			out.println("<tr><td>" + listing.getName() + " posted by " + listing.getOwnerName() + "</td>");
			out.println("<td><form method=\"POST\" action=\"/listing/activate/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Activate\"/></form>");
			out.println("<form method=\"POST\" action=\"/listing/send_back/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Send back\"/></form>");
			out.println("<a href=\"/listing/get/" + listing.getId() + ".json?\">View</a></td>");
			out.println("<td>");
			List<ListingDocumentVO> docs = getListingDocs(currentUser, listing);
			if (docs != null && !docs.isEmpty()) {
				BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
				
				out.println("<table border=\"1\"><tr><td colspan=\"2\">Uploaded documents</td></tr>");
				for (ListingDocumentVO doc : docs) {
					out.println("<tr>");
					out.println("<td>");
					if (ListingDoc.Type.LOGO.toString().equalsIgnoreCase(doc.getType())) {
						BlobInfo logoInfo = new BlobInfoFactory().loadBlobInfo(doc.getBlob());
						if (logoInfo != null) {
							byte logo[] = blobstoreService.fetchData(doc.getBlob(), 0, logoInfo.getSize() - 1);
							out.println("<img src=\"" + ListingFacade.instance().convertLogoToBase64(logo) + "\"/>");
						}
					}
					out.println("<a href=\"/file/download/" + doc.getId() + ".json\">Download "
							+ doc.getType() + " uploaded " + fmt.print(doc.getCreated().getTime()) + ", type: " + doc.getType() + "</a></td>");
					out.println("<td><form method=\"POST\" action=\"/listing/delete_file/.json?id="
							+ listing.getId() + "&type=" + doc.getType() + "\"><input type=\"submit\" value=\"Delete\"/></form>");
					out.println("</td></tr>");
				}
				out.println("</table>");
			} else {
				out.println("No documents uploaded");
			}
			out.println("<td>");
			out.println("</tr>");
		}
		out.println("</table>");
	}

	private List<ListingDocumentVO> getListingDocs(UserVO loggedInUser, ListingVO listingVO) {
		List<ListingDocumentVO> list = new ArrayList<ListingDocumentVO>();
		Listing listing = ObjectifyDatastoreDAO.getInstance().getListing(ListingVO.toKeyId(listingVO.getId()));
		
		if (listing.businessPlanId != null) {
			list.add(ListingFacade.instance().getListingDocument(loggedInUser, listing.businessPlanId.getString()));
		}
		if (listing.presentationId != null) {
			list.add(ListingFacade.instance().getListingDocument(loggedInUser, listing.presentationId.getString()));
		}
		if (listing.financialsId != null) {
			list.add(ListingFacade.instance().getListingDocument(loggedInUser, listing.financialsId.getString()));
		}
		if (listing.logoId != null) {
			list.add(ListingFacade.instance().getListingDocument(loggedInUser, listing.logoId.getString()));
		}
		return list;
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
					if (Bid.Action.ACTIVATE.equals(bid.action)) {
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
					if (Bid.Action.ACCEPT.equals(bid.action)) {
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
