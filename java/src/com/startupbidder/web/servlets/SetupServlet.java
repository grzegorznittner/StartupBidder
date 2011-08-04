package com.startupbidder.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.startupbidder.vo.SystemPropertyVO;
import com.startupbidder.vo.UserVO;
import com.startupbidder.web.ServiceFacade;

@SuppressWarnings("serial")
public class SetupServlet extends HttpServlet {
	
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
			out.println("<p>Hello, " + user.getNickname() + " ..................................");
			out.println("<a href=\"" + userService.createLogoutURL("/hello") + "\">logout</a></p>");
			
			UserVO currentUser = service.getLoggedInUserData(user);
			if (currentUser == null) {
				currentUser = service.createUser(user);
			}
			if (!(currentUser.getEmail().startsWith("grzegorz.nittner") || currentUser.getEmail().startsWith("johnarleyburns"))) {
				out.println("<p>You're not authorized to use setup page!");
				return;
			}
			
			out.println("<p>Google Doc credentials:</p>");
			out.println("<form method=\"POST\" action=\"/system/set-property/.html\">"
					+ "User: <input name=\"name\" type=\"hidden\" value=\"googledoc.user\"/><input name=\"value\" type=\"text\" value=\"" + currentUser.getEmail() + "\"/></br>"
					+ "Password: <input name=\"name.1\" type=\"hidden\" value=\"googledoc.password\"/><input name=\"value.1\" type=\"password\" value=\"\"/></br>"
					+ "<input type=\"submit\" value=\"Set Google Doc credentials\"/></form>");

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
			out.println("<p>-------------------------------</p>");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.println("</body></html>");
		}

	}

}