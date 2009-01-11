package org.picocontainer.web.remoting;

import org.picocontainer.PicoCompositionException;

public interface PicoWebRemotingMonitor {

    Object picoCompositionExceptionForMethodInvocation(PicoCompositionException e);

    Object runtimeExceptionForMethodInvocation(RuntimeException e);

    Object nullParameterForMethodInvocation(String parameterName);
}
