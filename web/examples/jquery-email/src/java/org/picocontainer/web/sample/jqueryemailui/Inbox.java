package org.picocontainer.web.sample.jqueryemailui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Inbox extends Mailbox {

    public Inbox(MessageStore store, User user) {
        super(foo(store, user));
    }

    private static Map<Integer, MessageData> foo(MessageStore store, User user) {
        return store.inboxFor(user.getName());
    }

}
