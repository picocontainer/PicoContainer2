package org.picocontainer.web.sample.jqueryemailui;

import java.util.Map;

public interface MessageStore {

	Map<Integer, MessageData> inboxFor(User name);

	Map<Integer, MessageData> sentFor(User name);
}
