package org.picocontainer.web.sample.jqueryemailui;

import java.util.ArrayList;

public class Mailbox {

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

    private String userName = "Gil Bates";

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


}
