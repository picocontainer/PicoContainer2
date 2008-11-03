package org.picocontainer.web.sample.jqueryemailui;

import org.picocontainer.web.WebappComposer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Characteristics;
import org.picocontainer.injectors.ProviderAdapter;

import javax.servlet.http.HttpServletRequest;

public class JQueryWebappComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer applicationContainer) {
    }

    public void composeSession(MutablePicoContainer sessionContainer) {
        sessionContainer.as(Characteristics.USE_NAMES).addComponent(Mailbox.class);
    }

    public void composeRequest(MutablePicoContainer requestContainer) {
        //requestContainer.as(Characteristics.USE_NAMES).addComponent(Mailbox.class);
        requestContainer.addAdapter(new StringFromRequest("to"));
        requestContainer.addAdapter(new StringFromRequest("subject"));
        requestContainer.addAdapter(new StringFromRequest("message"));
        requestContainer.addAdapter(new StringFromRequest("delId"));
        requestContainer.addAdapter(new StringFromRequest("msgId"));
        requestContainer.addAdapter(new StringFromRequest("view"));
        requestContainer.addAdapter(new StringFromRequest("userId"));
    }

    public static class StringFromRequest extends ProviderAdapter {
        private final String paramName;

        public StringFromRequest(String paramName) {
            this.paramName = paramName;
        }

        public Class getComponentImplementation() {
            return String.class;
        }

        public Object getComponentKey() {
            return paramName;
        }

        public Object provide(HttpServletRequest req) {
            return req.getParameter(paramName);
        }
    }

}
