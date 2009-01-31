package org.picocontainer.web.sample.jqueryemailui;

import static org.picocontainer.Characteristics.USE_NAMES;

import javax.servlet.ServletContext;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.WebappComposer;
import org.picocontainer.web.remoting.PicoWebRemotingMonitor;

public class JQueryEmailWebappComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer appContainer, ServletContext context) {
        appContainer.addComponent(PicoWebRemotingMonitor.class, JQueryEmailWebRemotingMonitor.class);
        appContainer.addComponent(MessageStore.class, InMemoryMessageStore.class);
    }

    public void composeSession(MutablePicoContainer sessionContainer) {
        // stateless
    }

    public void composeRequest(MutablePicoContainer requestContainer) {

        requestContainer.addAdapter(new User.FromCookie());
        requestContainer.as(USE_NAMES).addComponent(Auth.class);

        requestContainer.as(USE_NAMES).addComponent(Inbox.class);
        requestContainer.as(USE_NAMES).addComponent(Sent.class);
    }

}
