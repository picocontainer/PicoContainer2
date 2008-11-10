package org.picocontainer.web.sample.jqueryemailui;

import org.picocontainer.web.WebappComposer;
import org.picocontainer.web.StringFromRequest;
import org.picocontainer.web.IntFromRequest;
import org.picocontainer.web.StringFromCookie;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Characteristics;

public class JQueryWebappComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer applicationContainer) {
        applicationContainer.addComponent(MessageStore.class);
    }

    public void composeSession(MutablePicoContainer sessionContainer) {
    }

    public void composeRequest(MutablePicoContainer requestContainer) {
        requestContainer.addAdapter(new StringFromRequest("to"));
        requestContainer.addAdapter(new StringFromRequest("subject"));
        requestContainer.addAdapter(new StringFromRequest("message"));
        requestContainer.addAdapter(new StringFromRequest("msgId"));
        requestContainer.addAdapter(new StringFromRequest("view"));
        requestContainer.addAdapter(new IntFromRequest("userId"));
        requestContainer.addAdapter(new User.FromCookie());
        requestContainer.as(Characteristics.USE_NAMES).addComponent(Inbox.class);
        requestContainer.as(Characteristics.USE_NAMES).addComponent(Sent.class);
    }

}
