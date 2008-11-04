package org.picocontainer.web.sample.jqueryemailui;

import java.util.ArrayList;

/**
 */
public class Sent extends MailBox {

    public Sent() {
        super(makeStartingMessages());
    }

    private static ArrayList<MessageData> makeStartingMessages() {
        // Use this as our "Database" for this demonstration application
        ArrayList<MessageData> messages = new ArrayList<MessageData>();
        messages.add(new MessageData(1, "Gil Bates", "Jeeves Sobs", "Nice OS", "You've made a great OS there Jeeves", false));
        return messages;
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



}
