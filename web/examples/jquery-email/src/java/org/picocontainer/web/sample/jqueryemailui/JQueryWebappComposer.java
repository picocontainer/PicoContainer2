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

    public static class IntFromRequest extends StringFromRequest {

        public IntFromRequest(String paramName) {
            super(paramName);
        }

        public Class getComponentImplementation() {
            return Integer.class;
        }

        public Object provide(HttpServletRequest req) {
            return Integer.parseInt((String)super.provide(req));
        }
    }

}
