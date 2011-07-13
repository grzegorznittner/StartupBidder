package com.startupbidder.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import test.com.startupbidder.jackson.VoTest;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.startupbidder.dao.DatastoreDAO;
import com.startupbidder.dao.MockDatastoreDAO;
import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.vo.DtoToVoConverter;
import com.startupbidder.vo.ListPropertiesVO;

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
		
		PrintWriter out = resp.getWriter();
		try {
			out.println("<html><body>");
			out.println("<p>Hello, " + user.getNickname() + "</p>");
			
			if (!user.getNickname().contains("grzegorz.nittner") || !user.getNickname().contains("johnarleyburns")) {
				out.println("<p>Sorry, you're not authorized to view contents!!!</p>");
				return;
			} else {
				out.println("<p>Place content here!</p>");
			}
			
			//out.println("<p><b>Jackson tests</b></p>");
			//out.println(VoTest.getArrayNodeString(DtoToVoConverter.convertBusinessPlans(datastore.getActiveBusinessPlans(10))));
			
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
			
			currentUser.setAccreditedInvestor(true);
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.println("</body></html>");
		}
	}

}
