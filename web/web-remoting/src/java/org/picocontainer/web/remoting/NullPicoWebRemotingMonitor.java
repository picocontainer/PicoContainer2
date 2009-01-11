/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
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
