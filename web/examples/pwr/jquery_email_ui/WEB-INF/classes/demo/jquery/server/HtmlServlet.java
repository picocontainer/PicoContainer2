
package demo.jquery.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import demo.jquery.MessageDB;
import demo.jquery.MessageData;

public class HtmlServlet extends HttpServlet
{
	public static final int SEND_MESSAGE = 1;
	public static final int READ_MESSAGE = 2;
	public static final int DELETE_MESSAGES = 3;


	public void init(ServletConfig config) throws ServletException
	{
		System.out.println("***** JQuery Demo Servlet Started ********");
		super.init(config);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException
	{		
		String uri = req.getRequestURI();
		int firstPos = uri.lastIndexOf('/') + 1;
		int requestType = Integer.parseInt(uri.substring(firstPos, uri.length() - 3));
			
		String forwardTo = "";
				
		try
		{
			// normally we'd get this information from the session, but for this demo it's hardcoded
		    int userID = 1;
			String userName = "Gil Bates";
			
			switch (requestType)
			{						
					case SEND_MESSAGE:
						MessageData msg = new MessageData();
						msg.to = req.getParameter("to");
						msg.subject = req.getParameter("subject");
						msg.message = req.getParameter("message");
						msg.from = userName; 
						// Send the message here - nothing happens in demo
						// MessageAction.send(msg);
						forwardTo = "messages.jsp";
						break;
					case READ_MESSAGE:
						int messageId = Integer.parseInt(req.getParameter("messageId"));
						String view = req.getParameter("view");
						if (view.equals("inbox"))
						{
							MessageDB.read(messageId);
						}
						MessageData mess = MessageDB.lookup(messageId);
						resp.getWriter().print(new JSONObject(mess).toString());
						resp.getWriter().flush();
						return;
					case DELETE_MESSAGES:
						String delId = req.getParameter("delId");
						if (delId != null)
						{
							MessageDB.delete(Integer.parseInt(delId));
						}
						return;		
				}
				req.getRequestDispatcher(forwardTo).forward(req, resp);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	}
	


}
