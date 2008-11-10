package org.picocontainer.web.sample.jqueryemailui;

import java.util.ArrayList;
import java.util.List;

public class Inbox extends Mailbox {

    public Inbox(MessageStore store, User user) {
        super(store.inboxFor(user.getName()));
    }

}
