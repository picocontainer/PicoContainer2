package org.picocontainer.web.sample.jqueryemailui;

import org.picocontainer.web.remoting.NullPicoWebRemotingMonitor;

public class JQueryDemoWebRemotingMonitor extends NullPicoWebRemotingMonitor {

    protected Class<? extends RuntimeException> getAppBaseRuntimeException() {
        return MailAppException.class;
    }
}
