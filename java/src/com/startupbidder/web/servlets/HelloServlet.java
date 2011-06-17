package com.startupbidder.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.startupbidder.dao.DatastoreDAO;
import com.startupbidder.dao.MockDatastoreDAO;
import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.BusinessPlanDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.UserDTO;

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
			
			if (!user.getNickname().contains("grzegorz.nittner")) {
				out.println("<p>Sorry, you're not authorized to view contents!!!</p>");
				return;
			} else {
				out.println("<p>Place content here!</p>");
			}
			
			out.println("<p><b>Current user data:</b></p>");
			UserDTO currentUser = datastore.getUser(user.getNickname());
			out.println("<p>" + currentUser + "</p>");
			
			currentUser.setAccreditedInvestor(true);
			currentUser.setOrganization("Mock organization");
			datastore.updateUser(currentUser);
			out.println("<p><b>Updated current user data:</b></p>");
			out.println("<p>" + currentUser + "</p>");
			
			out.println("<p><b>Current user business plans:</b></p>");
			for (BusinessPlanDTO bp : datastore.getUserBusinessPlans(currentUser.getId().toString(), 10)) {
				int rating = datastore.getRating(bp.getId().toString());
				int activity = datastore.getActivity(bp.getId().toString());
				out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
				datastore.valueUpBusinessPlan(bp.getId().toString(), currentUser.getId().toString());
			}
			
			out.println("<p><b>Top business plans:</b></p>");
			for (BusinessPlanDTO bp : datastore.getTopBusinessPlans(10)) {
				int rating = datastore.getRating(bp.getId().toString());
				int activity = datastore.getActivity(bp.getId().toString());
				out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
				datastore.valueUpBusinessPlan(bp.getId().toString(), currentUser.getId().toString());
			}
			out.println("<p><b>Active business plans:</b></p>");
			for (BusinessPlanDTO bp : datastore.getActiveBusinessPlans(10)) {
				int rating = datastore.getRating(bp.getId().toString());
				int activity = datastore.getActivity(bp.getId().toString());
				out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
				datastore.valueDownBusinessPlan(bp.getId().toString(), currentUser.getId().toString());
			}
			
			out.println("<p><b>Top business plans (2):</b></p>");
			for (BusinessPlanDTO bp : datastore.getTopBusinessPlans(10)) {
				int rating = datastore.getRating(bp.getId().toString());
				int activity = datastore.getActivity(bp.getId().toString());
				out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
				datastore.valueUpBusinessPlan(bp.getId().toString(), currentUser.getId().toString());
			}
			out.println("<p><b>Active business plans (2):</b></p>");
			for (BusinessPlanDTO bp : datastore.getActiveBusinessPlans(10)) {
				int rating = datastore.getRating(bp.getId().toString());
				int activity = datastore.getActivity(bp.getId().toString());
				out.println("<p>" + "<b>R=" + rating + "</b>" + "<b>A=" + activity + "</b>" + bp + "</p>");
				datastore.valueDownBusinessPlan(bp.getId().toString(), currentUser.getId().toString());
			}

			BusinessPlanDTO topBP = datastore.getTopBusinessPlans(1).get(0);
			out.println("<p><b>Bids for top business plan '" + topBP + "</b></p>");
			for (BidDTO bid : datastore.getBids(topBP.getId().toString())) {
				out.println("<p>" + bid + "</p>");
			}

			BusinessPlanDTO topActiveBP = datastore.getActiveBusinessPlans(1).get(0);
			out.println("<p><b>Comments for most active business plan '" + topActiveBP + "</b></p>");
			for (CommentDTO comment : datastore.getComments(topActiveBP.getId().toString())) {
				out.println("<p>" + comment + "</p>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.println("</body></html>");
		}
	}

}
