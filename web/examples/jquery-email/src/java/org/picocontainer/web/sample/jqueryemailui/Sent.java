package org.picocontainer.web.sample.jqueryemailui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sent extends Mailbox {

    private final String userName;

    public Sent(MessageStore store, User user) {
        super(foo(store, user));
        userName = user.getName();
    }

    private static Map<Integer, MessageData> foo(MessageStore store, User user) {
        return store.sentFor(user.getName());
    }

    public MessageData send(String to, String subject, String message) {
        return super.addMessage(new MessageData(0, userName, to, subject, message, false));
    }

}
