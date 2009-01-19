package org.picocontainer.web.sample.jqueryemailui;

import org.picocontainer.web.WebappComposer;
import org.picocontainer.web.remoting.PicoWebRemotingMonitor;
import org.picocontainer.web.remoting.NullPicoWebRemotingMonitor;
import static org.picocontainer.web.StringFromRequest.addStringRequestParameters;
import static org.picocontainer.web.IntFromRequest.addIntegerRequestParameters;
import org.picocontainer.MutablePicoContainer;
import static org.picocontainer.Characteristics.USE_NAMES;

import javax.servlet.ServletContext;

public class JQueryWebappComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer appContainer, ServletContext context) {
        appContainer.addComponent(PicoWebRemotingMonitor.class, NullPicoWebRemotingMonitor.class);
        appContainer.addComponent(MessageStore.class, InMemoryMessageStore.class);
    }

    public void composeSession(MutablePicoContainer sessionContainer) {
        // stateless
    }

    public void composeRequest(MutablePicoContainer requestContainer) {

        addStringRequestParameters(requestContainer,
                "to", "subject", "message", "view",
                "userName", "password", "userId", "sec");

        addIntegerRequestParameters(requestContainer, "msgId");

        requestContainer.addAdapter(new User.FromCookie());
        requestContainer.as(USE_NAMES).addComponent(Auth.class);

        requestContainer.as(USE_NAMES).addComponent(Inbox.class);
        requestContainer.as(USE_NAMES).addComponent(Sent.class);
    }

}
