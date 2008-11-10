package org.picocontainer.web.sample.jqueryemailui;

import java.util.ArrayList;
import java.util.List;

public class Sent extends Mailbox {

    private final String userName;

    public Sent(MessageStore store, User user) {
        super(store.sentFor(user.getName()));
        userName = user.getName();
    }

    public MessageData send(String to, String subject, String message) {
        return super.addMessage(new MessageData(0, userName, to, subject, message, false));
    }

}
