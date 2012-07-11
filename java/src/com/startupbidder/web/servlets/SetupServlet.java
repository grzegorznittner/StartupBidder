package com.startupbidder.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Index;
import com.google.appengine.api.datastore.Index.IndexState;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.startupbidder.dao.MockDataBuilder;
import com.startupbidder.vo.SystemPropertyVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.FrontController;
import com.startupbidder.web.ServiceFacade;
import com.startupbidder.web.UserMgmtFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
public class SetupServlet extends HttpServlet {
	
	static {
		new FrontController ();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		if (user != null) {
			resp.setContentType("text/html");
		} else {
			resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
		}
		
		ServiceFacade service = ServiceFacade.instance();
		PrintWriter out = resp.getWriter();
		
		try {
			out.println("<html><head><title>StartupBidder setup page</title></head><body>");
            out.println("<h1>User Info</h1>");
			out.println("<p>Hello, " + user.getNickname() + " ..................................");
			out.println("<a href=\"" + userService.createLogoutURL("/hello") + "\">logout</a></p>");
			
			UserVO currentUser = UserMgmtFacade.instance().getLoggedInUserData(user);
			if (currentUser == null) {
				currentUser = UserMgmtFacade.instance().createUser(user);
			}
			currentUser.setAdmin(userService.isUserAdmin());
			if (!currentUser.isAdmin()) {
				out.println("<p>You're not authorized to use setup page! Only Admin users can access this page! </p>");
				return;
			}

			out.println("<h1>Migration</h1>");
			
			out.println("<form method=\"POST\" action=\"/system/migrate201207101218_to_current.html\">"
					+ "<input type=\"submit\" value=\"Migrate version migrate201207101218 to current\"/></form>");
			out.println("<form method=\"POST\" action=\"/system/associate_mock_images.html\">"
					+ "<input type=\"submit\" value=\"Associate mock images\"/></form>");
			
            out.println("<h1>Mock Data</h1>");

            out.println("Mock data files will be fetched from: " + new MockDataBuilder().getTestDataPath() + "</br>");
			
			out.println("<form method=\"POST\" action=\"/system/create-mock-datastore/.html\">"
					+ "<input type=\"submit\" value=\"Recreate mock datastore\"/></form>");


            out.println("<h1>Statistics</h1>");

            out.println("<form method=\"POST\" action=\"/cron/update-aggregate-stats/.html\">"
                    + "<input type=\"submit\" value=\"Update all aggregate stats\"/></form>");

            out.println("<form method=\"POST\" action=\"/cron/update-listing-stats/.html\">"
                    + "<input type=\"submit\" value=\"Update all listings stats\"/></form>");


            out.println("<h1>Documents</h1>");

            out.println("<form method=\"POST\" action=\"/cron/update-listing-docs/.html\">"
                    + "<input type=\"submit\" value=\"Update all listings docs\"/></form>");

            out.println("<p>Google Doc credentials:</p>");
            out.println("<form method=\"POST\" action=\"/system/set-property/.html\">"
                    + "User: <input name=\"name\" type=\"hidden\" value=\"googledoc.user\"/><input name=\"value\" type=\"text\" value=\"" + currentUser.getEmail() + "\"/></br>"
                    + "Password: <input name=\"name.1\" type=\"hidden\" value=\"googledoc.password\"/><input name=\"value.1\" type=\"password\" value=\"\"/></br>"
                    + "<input type=\"submit\" value=\"Set Google Doc credentials\"/></form>");


            out.println("<h1>Imports</h1>");

            out.println("<form method=\"POST\" action=\"/system/delete-geocode-cache/.html\">"
                    + "<input type=\"submit\" value=\"Delete Geocode Cache\"/></form>");

            out.println("<p>Startuply Startup Import:</p>");
            out.println("<form method=\"POST\" action=\"/system/import-startuply-data/.html\">"
                    + "Max Count to Import: <input name=\"max\" type=\"text\" value=\"1000\"/></br>"
                    + "<input type=\"submit\" value=\"Import Data\"/></form>");

            out.println("<p>Startuply Delete Cache:</p>");
            out.println("<form method=\"POST\" action=\"/system/delete-startuply-cache/.html\">"
                    + "<input type=\"submit\" value=\"Delete Data\"/></form>");

            out.println("<p>AngelList Startup Import:</p>");
            out.println("<form method=\"POST\" action=\"/system/import-angellist-data/.html\">"
                    + "From ID: <input name=\"fromId\" type=\"text\" value=\"19000\"/></br>"
                    + "To ID: <input name=\"toId\" type=\"text\" value=\"19100\"/></br>"
                    + "<input type=\"submit\" value=\"Import Data\"/></form>");

            out.println("<p>AngelList Startup Delete Cache:</p>");
            out.println("<form method=\"POST\" action=\"/system/delete-angellist-cache/.html\">"
                    + "From ID: <input name=\"fromId\" type=\"text\" value=\"19000\"/></br>"
                    + "To ID: <input name=\"toId\" type=\"text\" value=\"19100\"/></br>"
                    + "<input type=\"submit\" value=\"Delete Data\"/></form>");

            out.println("<form method=\"POST\" action=\"/system/delete-angellist-cache/.html\">"
                    + "<input type=\"submit\" value=\"Delete Full AngelList Cache\"/></form>");


            out.println("<h1>System Settings</h1>");

            out.println("<p>Available system properties:");
            out.println("<ul><li>notification_real_receivers - if true then notification emails are sent to real receivers");
            out.println("<li>notification_no_bcc_admins - if empty or false then notification emails are BCC to admins. Only when notification_real_receivers = true");
            out.println("</ul></p>");
            
			out.println("<p>Set system property:</p>");
			out.println("<form method=\"POST\" action=\"/system/set-property/.html\">"
					+ "Name: <input name=\"name\" type=\"text\" value=\"\"/></br>"
					+ "Value: <input name=\"value\" type=\"text\" value=\"\"/></br>"
					+ "<input type=\"submit\" value=\"Set\"/></form>");
			
			out.println("<p>Current system properties:</p>");
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
			List<SystemPropertyVO> props = service.getSystemProperties(currentUser);
			for (SystemPropertyVO prop : props) {
				out.println(prop.getName() + " : " + prop.getValue()
						+ " <sup>(" + prop.getAuthor() + ", " + fmt.print(prop.getCreated().getTime()) + ")</sup></br>");
			}
			
			out.println("<p>Environment type: " + System.getProperty("com.google.appengine.runtime.environment") + "</p>");
			
			out.println("<p>Datastore indexes:</p>");
			Map<Index, IndexState> indexes = DatastoreServiceFactory.getDatastoreService().getIndexes();
			for (Entry<Index, IndexState> index : indexes.entrySet()) {
				out.println(index.getKey().getKind() + "<br/>");
				out.println("&nbsp;&nbsp;&nbsp;&nbsp;|-- " + index.getKey().getProperties() + "<br/>");
				out.println("&nbsp;&nbsp;&nbsp;&nbsp;|-- " + index.getValue() + "<br/>");
			}
			out.println("<p>-------------------------------</p>");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.println("</body></html>");
		}

	}

}
