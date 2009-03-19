package org.picocontainer.web.sample.jqueryemail;

import static org.picocontainer.Characteristics.USE_NAMES;

import javax.servlet.ServletContext;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.WebappComposer;
import org.picocontainer.web.remoting.PicoWebRemotingMonitor;

public class JQueryEmailWebappComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer pico, ServletContext context) {
        pico.addComponent(PicoWebRemotingMonitor.class, JQueryEmailWebRemotingMonitor.class);
        pico.addComponent(MessageStore.class, InMemoryMessageStore.class);
        pico.addComponent(UserStore.class, InMemoryUserStore.class);
    }

    public void composeSession(MutablePicoContainer pico) {
        // stateless
    }

    public void composeRequest(MutablePicoContainer pico) {

        pico.addAdapter(new User.FromCookie());
        pico.as(USE_NAMES).addComponent(Auth.class);

        pico.as(USE_NAMES).addComponent(Inbox.class);
        pico.as(USE_NAMES).addComponent(Sent.class);
    }

}
