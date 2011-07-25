package com.startupbidder.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.startupbidder.dao.DatastoreDAO;
import com.startupbidder.dao.MockDatastoreDAO;
import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.ServiceFacade;

@SuppressWarnings("serial")
public class HelloServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) {
			resp.setContentType("text/html");
		} else {
			resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
		}
		
		DatastoreDAO datastore = MockDatastoreDAO.getInstance();
		ServiceFacade service = ServiceFacade.instance();
		
		PrintWriter out = resp.getWriter();
		try {
			out.println("<html><body>");
			out.println("<p>Hello, " + user.getNickname() + " ..................................");
			out.println("<a href=\"" + userService.createLogoutURL("/hello") + "\">logout</a></p>");
			
			if (!(user.getNickname().contains("grzegorz.nittner") || user.getNickname().contains("johnarleyburns"))) {
				out.println("<p>Sorry, you're not authorized to view contents!!!</p>");
				return;
			}
			
			UserDTO topInvestor = datastore.getTopInvestor();
			UserVO currentUser = service.getLoggedInUserData(user);
			if (currentUser == null) {
				currentUser = service.createUser(user);
			}
			ListPropertiesVO listProperties = new ListPropertiesVO();
			listProperties.setMaxResults(1);
			ListingDTO topListing = datastore.getTopListings(listProperties).get(0);
			List<BidDTO> bids = datastore.getBidsForUser(topInvestor.getIdAsString());
			List<CommentDTO> comments = datastore.getCommentsForListing(topListing.getIdAsString());
			
			//testMockDatastore(user, datastore, out);
			out.println("<p>User API:</p>");
			out.println("<a href=\"/user/topinvestor/.html\">Top investor data</a><br/>");
			out.println("<a href=\"/user/loggedin/.html\">Direct link to logged in user data</a><br/>");
			out.println("<a href=\"/user/get/" + currentUser.getId() + "/.html\">Logged in user data via /users/get/ </a><br/>");
			out.println("<a href=\"/user/all/.html\">All users</a><br/>");
			
			out.println("<p>Listings API:</p>");
			out.println("<a href=\"/listings/get/" + topListing.getIdAsString() + "/.html\">Top listing data</a><br/>");
			out.println("<a href=\"/listings/up/" + topListing.getIdAsString() + "/" + "/.html\">Logged in user votes for top listing (works only once per user)</a><br/>");
			out.println("<a href=\"/listings/up/.html?id=" + topListing.getIdAsString() + "\">Logged in user votes for top listing (works only once per user), 2nd form</a><br/>");
			out.println("<a href=\"/listings/activate/" + topListing.getIdAsString() + "/.html\">Activate top listing</a><br/>");
			out.println("<a href=\"/listings/withdraw/" + topListing.getIdAsString() + "/.html\">Withdraw top listing</a><br/>");
			out.println("<a href=\"/listings/top/.html?max_results=6\">Top listings</a><br/>");
			out.println("<a href=\"/listings/active/.html?max_results=6\">Active listings</a><br/>");
			out.println("<a href=\"/listings/valuation/.html?max_results=6\">Top valued listings</a><br/>");
			out.println("<a href=\"/listings/popular/.html?max_results=6\">Most popular listings</a><br/>");
			out.println("<a href=\"/listings/latest/.html?max_results=6\">Latest listings</a><br/>");
			out.println("<a href=\"/listings/closing/.html?max_results=6\">Closing listings</a><br/>");
			out.println("<a href=\"/listings/user/" + topInvestor.getIdAsString() + "/.html?max_results=6\">Top investor's listings</a><br/>");

			out.println("<p>Bids API:</p>");
			out.println("<a href=\"/bids/listing/" + topListing.getIdAsString() + "/.html?max_results=6\">Bids for top listing</a><br/>");
			out.println("<a href=\"/bids/user/" + topInvestor.getIdAsString() + "/.html?max_results=6\">Bids for top investor</a><br/>");
			out.println("<a href=\"/bids/get/" + bids.get(0).getIdAsString() + "/.html\">Get bid id '" + bids.get(0).getIdAsString() + "'</a><br/>");
			
			out.println("<p>Comments API:</p>");
			out.println("<a href=\"/comments/listing/" + topListing.getIdAsString() + "/.html?max_results=6\">Comments for top listing</a><br/>");
			out.println("<a href=\"/comments/user/" + topInvestor.getIdAsString() + "/.html?max_results=6\">Comments for top investor</a><br/>");
			out.println("<a href=\"/comments/get/" + comments.get(0).getIdAsString() + "/.html\">Get comment id '" + comments.get(0).getIdAsString() + "'</a><br/>");
			out.println("<br/>");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.println("</body></html>");
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
		for (ListingDTO bp : datastore.getUserListings(currentUser.getIdAsString(), listProperties)) {
			int rating = datastore.getNumberOfVotes(bp.getIdAsString());
			int activity = datastore.getActivity(bp.getIdAsString());
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
			datastore.valueUpListing(bp.getIdAsString(), currentUser.getIdAsString());
		}
		
		out.println("<p><b>Top business plans:</b></p>");
		for (ListingDTO bp : datastore.getTopListings(listProperties)) {
			int rating = datastore.getNumberOfVotes(bp.getIdAsString());
			int activity = datastore.getActivity(bp.getIdAsString());
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
			datastore.valueUpListing(bp.getIdAsString(), currentUser.getIdAsString());
		}
		out.println("<p><b>Active business plans:</b></p>");
		for (ListingDTO bp : datastore.getActiveListings(listProperties)) {
			int rating = datastore.getNumberOfVotes(bp.getIdAsString());
			int activity = datastore.getActivity(bp.getIdAsString());
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
			datastore.valueDownListing(bp.getIdAsString(), currentUser.getIdAsString());
		}
		
		out.println("<p><b>Top business plans (2):</b></p>");
		for (ListingDTO bp : datastore.getTopListings(listProperties)) {
			int rating = datastore.getNumberOfVotes(bp.getIdAsString());
			int activity = datastore.getActivity(bp.getIdAsString());
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
			datastore.valueUpListing(bp.getIdAsString(), currentUser.getIdAsString());
		}
		out.println("<p><b>Active business plans (2):</b></p>");
		for (ListingDTO bp : datastore.getActiveListings(listProperties)) {
			int rating = datastore.getNumberOfVotes(bp.getIdAsString());
			int activity = datastore.getActivity(bp.getIdAsString());
			out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
			datastore.valueDownListing(bp.getIdAsString(), currentUser.getIdAsString());
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
