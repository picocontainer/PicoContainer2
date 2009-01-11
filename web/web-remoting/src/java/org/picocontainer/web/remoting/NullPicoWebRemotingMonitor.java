package org.picocontainer.web.remoting;

import org.picocontainer.PicoCompositionException;

public class NullPicoWebRemotingMonitor implements PicoWebRemotingMonitor {

    public Object picoCompositionExceptionForMethodInvocation(PicoCompositionException e) {
        return new ErrorReply(e.getMessage());
    }

    public Object runtimeExceptionForMethodInvocation(RuntimeException e) {
        return new ErrorReply(e.getMessage());
    }

    public Object nullParameterForMethodInvocation(String parameterName) {
        return new ErrorReply("Parameter '" + parameterName + "' missing");
    }
}
