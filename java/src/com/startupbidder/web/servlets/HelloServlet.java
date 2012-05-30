package com.startupbidder.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.startupbidder.dao.MessageObjectifyDatastoreDAO;
import com.startupbidder.dao.ObjectifyDatastoreDAO;
import com.startupbidder.datamodel.OldBid;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.ListingDoc;
import com.startupbidder.datamodel.Monitor;
import com.startupbidder.datamodel.PrivateMessage;
import com.startupbidder.datamodel.PrivateMessageUser;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.VoToModelConverter;
import com.startupbidder.vo.BidListVO;
import com.startupbidder.vo.BidUserListVO;
import com.startupbidder.vo.BidUserVO;
import com.startupbidder.vo.BidVO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingAndUserVO;
import com.startupbidder.vo.ListingDocumentVO;
import com.startupbidder.vo.ListingVO;
import com.startupbidder.vo.PrivateMessageUserVO;
import com.startupbidder.vo.QuestionAnswerVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.BidFacade;
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
			listProperties = new ListPropertiesVO();
			listProperties.setMaxResults(20);
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
			listProperties = new ListPropertiesVO();
			listProperties.setMaxResults(50);
			List<ListingVO> postedListings = DtoToVoConverter.convertListings(datastore.getPostedListings(listProperties));
			printPostedListings(out, currentUser, postedListings);
			
			out.println("<br/><a href=\"/listings/closing/.json?max_results=6\">Closing listings</a><br/>");
			listProperties = new ListPropertiesVO();
			listProperties.setMaxResults(50);
			List<ListingVO> activeListings = DtoToVoConverter.convertListings(datastore.getClosingListings(listProperties));
			printActiveListings(out, currentUser, activeListings);
			
			out.println("<br/><a href=\"/listings/frozen/.json?max_results=6\">Frozen listings</a><br/>");
			listProperties = new ListPropertiesVO();
			listProperties.setMaxResults(50);
			List<ListingVO> frozenListings = DtoToVoConverter.convertListings(datastore.getFrozenListings(listProperties));
			printFrozenListings(out, currentUser, frozenListings);
			
			out.println("<a href=\"/listings/user/active/.json?max_results=6\">User's active listings</a><br/>");
			out.println("<a href=\"/listings/user/withdrawn/.json?max_results=6\">User's withdrawn listings</a><br/>");
			out.println("<a href=\"/listings/user/frozen/.json?max_results=6\">User's frozen listings</a><br/>");
			out.println("<a href=\"/listings/user/closed/.json?max_results=6\">User's closed listings</a><br/><br/>");
			
			out.println("<a href=\"/listings/top/.json?max_results=6\">Top listings</a><br/>");
			out.println("<a href=\"/listings/valuation/.json?max_results=6\">Top valued listings</a><br/>");
			out.println("<a href=\"/listings/popular/.json?max_results=6\">Most popular listings</a><br/>");
			out.println("<a href=\"/listings/latest/.json?max_results=6\">Latest listings</a><br/>");
			out.println("<a href=\"/listings/closing/.json?max_results=6\">Closing listings</a><br/>");
			out.println("<a href=\"/listings/monitored/.json?max_results=6\">Monitored listings (by logged in user)</a><br/>");
			out.println("<form method=\"GET\" action=\"/listing/keyword/.json\"><input name=\"text\" type=\"text\" value=\"business\"/>"
					+ "<input type=\"submit\" value=\"Keyword search\"/></form>");

			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">Comments API:</p>");
			out.println("<a href=\"/listing/comments/" + topListing.getWebKey() + "/.json?max_results=6\">Comments for top listing</a><br/>");
			out.println("<a href=\"/comments/user/" + currentUser.getId() + "/.json?max_results=6\">Comments for current user</a><br/>");
			if (comments != null && comments.size() > 0) {
				out.println("<a href=\"/comments/get/" + comments.get(0).getWebKey() + "/.json\">Get comment id '" + comments.get(0).getWebKey() + "'</a><br/>");
				out.println("<form method=\"POST\" action=\"/listing/post_comment/.json\"><textarea name=\"comment\" rows=\"5\" cols=\"100\">"
							+ "{ \"listing_id\":\"" + topListing.getWebKey() + "\", \"text\":\"comment test\" }"
							+ "</textarea><input type=\"submit\" value=\"Create a comment (for top listing)\"/></form>");
				out.println("<form method=\"POST\" action=\"/listing/delete_comment/.json?id=" + comments.get(0).getWebKey() + "\"><input type=\"submit\" value=\"Deletes comment id '" + comments.get(0).getWebKey() + "'\"/></form>");
			} else {
				out.println("No comments.<br/>");
			}
			out.println("<br/>");
			
			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">Private Messages API:</p>");
			
			out.println("<a href=\"/user/message_users/.json?\">Private message users</a><br/>");
			ListPropertiesVO prop = new ListPropertiesVO();
			prop.setMaxResults(20);
			List<PrivateMessageUser> messageUsers = MessageObjectifyDatastoreDAO.getInstance().getMessageShortList(VoToModelConverter.convert(currentUser), prop);
			if (messageUsers != null && !messageUsers.isEmpty()) {
				for (PrivateMessageUserVO msg : DtoToVoConverter.convertPrivateMessageUsers(messageUsers)) {
					out.println("<p style=\"background: none repeat scroll 0% 0% rgb(220, 220, 220);\">");
					out.println("" + msg.getUserNickname() + " (" + msg.getCounter() + ") last " + msg.getDirection() + " '" + msg.getText() + "' on " + msg.getLastDate() + " ");
					out.println("<a href=\"/user/messages/" + msg.getUser() + "/.json\">View all conversation with " + msg.getUserNickname() + "</a> ");
					out.println("</p>");
					out.println("<form method=\"POST\" action=\"/user/send_message/.json\"><textarea name=\"message\" rows=\"1\" cols=\"120\">"
								+ "{\"profile_id\":\"" + msg.getUser() + "\", \"text\":\"Reply text " + (msg.getCounter() + 1) + " to " + msg.getUserNickname() + "\"}"
								+ "</textarea><input type=\"submit\" value=\"Send private to " + msg.getUserNickname() + "\"/></form>");
				}
			} else {
				out.println("Current user doesn't have any messages<br/>");
			}
			for (SBUser usr : users) {
				if (usr.id != currentUser.toKeyId()) {
					out.println("<form method=\"POST\" action=\"/user/send_message/.json\"><textarea name=\"message\" rows=\"3\" cols=\"50\">"
							+ "{\"profile_id\":\"" + usr.getWebKey() + "\", \"text\":\"Message text to " + usr.nickname + "\"}"
							+ "</textarea><input type=\"submit\" value=\"Send private to " + usr.nickname + "\"/></form>");
				}
			}

			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">Notificatin API:</p>");			
			out.println("<a href=\"/notification/user/.json?max_results=6\">Notifications for current user</a><br/>");

			out.println("<br/>");
			
			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">Monitor API:</p>");
			out.println("Examples for setting and deactivating monitors you'll find in active listing section.<br/>");
			out.println("<a href=\"/monitors/active-for-user/.json?\">Active monitors for logged in user</a><br/>");
			out.println("<a href=\"/monitors/active-for-listing/?id=" + topListing.getWebKey() + "\">Monitors for top listing</a><br/>");
			
			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">File API:</p>");
			out.println("<a href=\"/file/get-upload-url/2/.json\">Get upload URL(s)</a><br/>");
			/*
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
			*/

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.println("</body></html>");
		}
	}

	private void printPostedListings(PrintWriter out, UserVO currentUser, List<ListingVO> postedListings) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
		out.println("<table border=\"1\" width=\"100%\">");
		out.println("<tr><td colspan=\"3\"><b>Posted listings</b></td></tr>");

		if (postedListings == null || postedListings.size() == 0) {
			out.println("<tr><td colspan=\"3\"><b>No posted listings or user is not admin!.<b><small>" + postedListings + "</small></td></tr>");
			out.println("</table>");
			return;
		}

		int count = 0;
		for (ListingVO listing : postedListings) {
			count++;
			out.println("<tr><td>" + listing.getName() + " posted by " + listing.getOwnerName() + "</td>");
			out.println("<td><form method=\"POST\" action=\"/listing/activate/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Activate\"/></form>");
			out.println("<form method=\"POST\" action=\"/listing/send_back/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Send back\"/></form>");
			out.println("<a href=\"/listing/get/" + listing.getId() + ".json?\">View</a></td>");
			out.println("<td>");
			if (count < 5) {
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
			} else {
				out.println("Documents won't be displayed.");
			}
			out.println("<td>");
			out.println("</tr>");
		}
		out.println("</table>");
	}

	private void printActiveListings(PrintWriter out, UserVO currentUser, List<ListingVO> activeListings) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
		out.println("<table border=\"1\" width=\"100%\">");
		out.println("<tr><td colspan=\"3\"><b>Active listings</b></td>");
		
		if (activeListings == null || activeListings.size() == 0) {
			out.println("<tr><td colspan=\"3\"><b>No active listings.<b><small>" + activeListings + "</small></td></tr>");
			out.println("</table>");
			return;
		}

		int count = 0;
		for (ListingVO listing : activeListings) {
			count++;
			out.println("<tr><td>" + listing.getName() + " posted by " + listing.getOwnerName());
			out.println("<br/><a href=\"/listing/get/" + listing.getId() + ".json?\">View</a></td>");
			out.println("<td><table width=\"100%\"><tr>");
			out.println("<td><form method=\"POST\" action=\"/listing/withdraw/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Withdraw\"/></form></td>");
			out.println("<td><form method=\"POST\" action=\"/listing/freeze/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Freeze\"/></form></td>");
			if (currentUser != null) {
				Monitor monitor = ObjectifyDatastoreDAO.getInstance().getListingMonitor(currentUser.toKeyId(), listing.toKeyId());
				if (monitor != null && monitor.active) {
					out.println("<td><form method=\"POST\" action=\"/monitor/deactivate/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Deactivate monitor\"/></form></td>");
				} else {
					out.println("<td><form method=\"POST\" action=\"/monitor/set/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Set monitor\"/></form></td>");
				}
			}
			out.println("</tr></table>");

			ListPropertiesVO listProperties = new ListPropertiesVO();
			listProperties.setMaxResults(50);
			List<QuestionAnswerVO> qas = ServiceFacade.instance().getQuestionsAndAnswers(currentUser, listing.getId(), listProperties);
			for (QuestionAnswerVO qa : qas) {
				if (qa.getAnswerDate() != null) {
					out.println("Q: " + qa.getQuestion() + "<br/>");
					out.println("A: " + qa.getAnswer() + "<br/>");
				} else {
					out.println("Q: " + qa.getQuestion() + "<br/>");
					if (StringUtils.equals(listing.getOwner(), currentUser.getId())) {
						out.println("<form method=\"POST\" action=\"/listing/answer_question/.json\"><textarea name=\"message\" rows=\"2\" cols=\"50\">"
							+ "{\"question_id\":\"" + qa.getId() + "\", \"text\":\"Answer text\"}"
							+ "</textarea><input type=\"submit\" value=\"Answer\"/></form>");
					} else {
						out.println("A: unanswered<br/><br/>");
					}
				}
			}
			out.println("<a href=\"/listing/questions_and_answers/" + listing.getId() + ".json?\">View Q&amp;A</a>");
			out.println("<form method=\"POST\" action=\"/listing/ask_owner/.json\"><textarea name=\"message\" rows=\"3\" cols=\"50\">"
					+ "{\"listing_id\":\"" + listing.getId() + "\", \"text\":\"Message text\"}"
					+ "</textarea><input type=\"submit\" value=\"Ask owner\"/></form>");
			
			if (StringUtils.equals(currentUser.getId(), listing.getOwner())) {
				out.println("<a href=\"/listing/bid_users/" + listing.getId() + ".json?\">View Bid Users</a>");
				listProperties = new ListPropertiesVO();
				listProperties.setMaxResults(50);
				BidUserListVO bidUsers = BidFacade.instance().getBidUsers(currentUser, listing.getId(), listProperties);
				for (BidUserVO bu : bidUsers.getBids()) {
					out.println(bu.getUserNickname() + " (" + bu.getCounter() + ") " + bu.getType() + " " + bu.getAmount() + " for " + bu.getPercentage() + "% valued "
							+ bu.getValue() + " on " + bu.getLastDate() + " ");
					out.println("<a href=\"/listing/bids/" + listing.getId() + "/" + bu.getUser() + ".json?\">View bids from " + bu.getUserNickname() + "</a>");
					out.println("<form method=\"POST\" action=\"/listing/make_bid/.json\"><textarea name=\"bid\" rows=\"3\" cols=\"50\">"
							+ "{\"listing_id\":\"" + listing.getId() + "\", \"text\":\"Bid text text\", "
							+ "\"amt\":\"10000\", \"pct\":\"5\", \"type\":\"INVESTOR_POST\" "
							+ (StringUtils.equals(currentUser.getId(), listing.getOwner()) ? ", \"investor_id\":\"" + bu.getUser() + "\"" : "") + "}"
							+ "</textarea><input type=\"submit\" value=\"Make bid\"/></form>");
				}
			} else {
				listProperties = new ListPropertiesVO();
				listProperties.setMaxResults(50);
				BidListVO bids = BidFacade.instance().getBids(currentUser, listing.getId(), null, listProperties);
				if (bids.getBids() != null) {
					int bidNr = bids.getBids().size();
					for (BidVO bu : bids.getBids()) {
						out.println("" + (bidNr--) + ". " + bu.getType() + " " + bu.getAmount() + " for " + bu.getPercentage() + "% valued "
								+ bu.getValue() + " on " + bu.getCreated() + "<br/> ");
					}
				}
				out.println("<a href=\"/listing/bids/" + listing.getId() + ".json?\">View bids</a>");
				out.println("<form method=\"POST\" action=\"/listing/make_bid/.json\"><textarea name=\"bid\" rows=\"3\" cols=\"50\">"
					+ "{\"listing_id\":\"" + listing.getId() + "\", \"text\":\"Bid text 1\", "
					+ "\"amt\":\"10000\", \"pct\":\"5\", \"type\":\"INVESTOR_POST\" }"
					+ "</textarea><input type=\"submit\" value=\"Make bid\"/></form>");
			}
			
			out.println("</td><td>");
			if (count < 5) {
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
			} else {
				out.println("Documents won't be displayed.");
			}
			out.println("<td>");
			out.println("</tr>");
		}
		out.println("</table>");
	}

	private void printFrozenListings(PrintWriter out, UserVO currentUser, List<ListingVO> frozenListings) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
		out.println("<table border=\"1\" width=\"100%\">");
		out.println("<tr><td colspan=\"3\"><b>Frozen listings</b></td>");
		
		if (frozenListings == null|| frozenListings.size() == 0) {
			out.println("<tr><td colspan=\"3\"><b>No frozen listings or user is not admin!.<b><small>" + frozenListings + "</small></td></tr>");
			out.println("</table>");
			return;
		}

		int count = 0;
		for (ListingVO listing : frozenListings) {
			count ++;
			out.println("<tr><td>" + listing.getName() + " posted by " + listing.getOwnerName() + "</td>");
			out.println("<td><form method=\"POST\" action=\"/listing/activate/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Activate\"/></form>");
			out.println("<form method=\"POST\" action=\"/listing/send_back/" + listing.getId() + "/.json\"><input type=\"submit\" value=\"Send back\"/></form>");
			out.println("<a href=\"/listing/get/" + listing.getId() + ".json?\">View</a></td>");
			out.println("<td>");
			if (count < 5) {
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
			} else {
				out.println("Document won't be displayed.");
			}
			out.println("</td>");
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
				List<OldBid> bidsForUserListings = datastore.getBidsForListing(listing.id);
				for (OldBid bid : bidsForUserListings) {
					if (OldBid.Action.ACTIVATE.equals(bid.action)) {
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
				List<OldBid> bidsForUserListings = datastore.getBidsForListing(listing.id);
				for (OldBid bid : bidsForUserListings) {
					if (OldBid.Action.ACCEPT.equals(bid.action)) {
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

}
