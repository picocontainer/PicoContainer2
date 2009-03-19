package org.picocontainer.web.sample.jqueryemail;

import java.util.Map;

public interface MessageStore {

	Map<Integer, Message> inboxFor(User name);

	Map<Integer, Message> sentFor(User name);
}
