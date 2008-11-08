package org.picocontainer.web.sample.jqueryemailui;

import org.picocontainer.web.WebappComposer;
import org.picocontainer.web.StringFromRequest;
import org.picocontainer.web.IntFromRequest;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Characteristics;

public class JQueryWebappComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer applicationContainer) {
    }

    public void composeSession(MutablePicoContainer sessionContainer) {
        sessionContainer.as(Characteristics.USE_NAMES).addComponent(Inbox.class);
        sessionContainer.as(Characteristics.USE_NAMES).addComponent(Sent.class);
    }

    public void composeRequest(MutablePicoContainer requestContainer) {
        requestContainer.addAdapter(new StringFromRequest("to"));
        requestContainer.addAdapter(new StringFromRequest("subject"));
        requestContainer.addAdapter(new StringFromRequest("message"));
        requestContainer.addAdapter(new StringFromRequest("msgId"));
        requestContainer.addAdapter(new StringFromRequest("view"));
        requestContainer.addAdapter(new IntFromRequest("userId"));
    }

}
