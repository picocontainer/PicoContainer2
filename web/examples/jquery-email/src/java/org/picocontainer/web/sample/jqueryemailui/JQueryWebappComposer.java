package org.picocontainer.web.sample.jqueryemailui;

import org.picocontainer.web.WebappComposer;
import org.picocontainer.MutablePicoContainer;

public class JQueryWebappComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer applicationContainer) {
    }

    public void composeSession(MutablePicoContainer sessionContainer) {
        sessionContainer.addComponent(Mailbox.class);
    }

    public void composeRequest(MutablePicoContainer requestContainer) {
    }
}
