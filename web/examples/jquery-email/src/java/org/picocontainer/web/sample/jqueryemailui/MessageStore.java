package org.picocontainer.web.sample.jqueryemailui;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: Dec 27, 2008
 * Time: 9:58:16 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MessageStore {
    Map<Integer, MessageData> inboxFor(User name);

    Map<Integer, MessageData> sentFor(User name);
}
