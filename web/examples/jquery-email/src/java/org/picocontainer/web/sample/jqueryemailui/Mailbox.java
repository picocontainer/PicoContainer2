package org.picocontainer.web.sample.jqueryemailui;

import java.util.ArrayList;

/**
 * For now, this is a fork of the HTMLServlet.
 * In time, that servlet will not be used, and this will take it's place with PWR handling it.
 */
public class Mailbox {

    private String userName = "Gil Bates";

    public Object read(String msgId, String view) {
        int messageId = Integer.parseInt(msgId);
        if (view.equals("inbox"))
        {
            MessageDB.read(messageId);
        }
        return MessageDB.lookup(messageId);
    }

    public Boolean delete(String delId) {
        if (delId != null)
        {
            MessageDB.delete(Integer.parseInt(delId));
        }
        return true;

    }

    public Boolean send(String to, String subject, String message) {
        MessageData msg = new MessageData();
        msg.to = to;
        msg.subject = subject;
        msg.message = message;
        msg.from = userName;
        // Send the message here - nothing happens in demo
        // MessageAction.send(msg);
        return true;
    }

    public MessageSet messages(int userID) {
		return MessageDB.lookupForUser(userID);
	}

    public Object pete() {
        return "hello";
    }


}
