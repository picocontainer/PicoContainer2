package org.picocontainer.web.sample.jqueryemailui;

import org.picocontainer.web.WebappComposer;
import org.picocontainer.web.StringFromRequest;
import org.picocontainer.web.IntFromRequest;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Characteristics;
import static org.picocontainer.Characteristics.USE_NAMES;

public class JQueryWebappComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer container) {
        container.addComponent(MessageStore.class);
    }

    public void composeSession(MutablePicoContainer container) {
        // stateless
    }

    public void composeRequest(MutablePicoContainer container) {
        container.addAdapter(new StringFromRequest("to"));
        container.addAdapter(new StringFromRequest("subject"));
        container.addAdapter(new StringFromRequest("message"));
        container.addAdapter(new IntFromRequest("msgId"));
        container.addAdapter(new StringFromRequest("view"));
        container.addAdapter(new StringFromRequest("userName"));
        container.addAdapter(new StringFromRequest("password"));
        container.addAdapter(new IntFromRequest("userId"));
        container.addAdapter(new User.FromCookie());
        container.as(USE_NAMES).addComponent(Auth.class);
        container.as(USE_NAMES).addComponent(Inbox.class);
        container.as(USE_NAMES).addComponent(Sent.class);
    }

}
