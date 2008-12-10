package org.picocontainer.web.sample.jqueryemailui;

import org.picocontainer.web.WebappComposer;
import static org.picocontainer.web.StringFromRequest.addStringRequestParameters;
import static org.picocontainer.web.IntFromRequest.addIntegerRequestParameters;
import org.picocontainer.MutablePicoContainer;
import static org.picocontainer.Characteristics.USE_NAMES;
import static org.picocontainer.Characteristics.GUARD;

public class JQueryWebappComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer container) {
        container.addComponent(MessageStore.class);
    }

    public void composeSession(MutablePicoContainer container) {
        // stateless
    }

    public void composeRequest(MutablePicoContainer container) {
        addStringRequestParameters(container,
                "to", "subject", "message", "view",
                "userName", "password", "userId", "sec");
        addIntegerRequestParameters(container, "msgId");
        container.addAdapter(new User.FromCookie());
        container.as(USE_NAMES).addComponent(Auth.class);
        container.as(USE_NAMES).addComponent(SecuritySeed.class);
        container.change(GUARD);
        container.as(USE_NAMES).addComponent(Inbox.class);
        container.as(USE_NAMES).addComponent(Sent.class);
    }


}
