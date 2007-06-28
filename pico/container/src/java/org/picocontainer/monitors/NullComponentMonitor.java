/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant & Obie Fernandez & Aslak Helles&oslash;y    *
 *****************************************************************************/

package org.picocontainer.monitors;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Member;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoLifecycleException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

/**
 * A {@link ComponentMonitor} which does nothing. 
 * 
 * @author Paul Hammant
 * @author Obie Fernandez
 * @version $Revision$
 */
public class NullComponentMonitor implements ComponentMonitor, Serializable {

    private static NullComponentMonitor instance;

    public Constructor instantiating(PicoContainer container, ComponentAdapter componentAdapter,
                                     Constructor constructor
    ) {
        return constructor;
    }

    public void instantiationFailed(PicoContainer container,
                                    ComponentAdapter componentAdapter,
                                    Constructor constructor,
                                    Exception e) {
    }

    public void instantiated(PicoContainer container, ComponentAdapter componentAdapter,
                             Constructor constructor,
                             Object instantiated,
                             Object[] injected,
                             long duration) {
    }

    public void invoking(PicoContainer container,
                         ComponentAdapter componentAdapter,
                         Member member,
                         Object instance) {
    }

    public void invoked(PicoContainer container,
                        ComponentAdapter componentAdapter,
                        Method method,
                        Object instance,
                        long duration) {
    }

    public void invocationFailed(Member member, Object instance, Exception e) {
    }

    public void lifecycleInvocationFailed(MutablePicoContainer container,
                                          ComponentAdapter componentAdapter, Method method,
                                          Object instance,
                                          RuntimeException cause) {
        throw new PicoLifecycleException(method, instance, cause);
    }

    public void noComponent(MutablePicoContainer container, Object componentKey) {
    }

    public static synchronized NullComponentMonitor getInstance() {
        if (instance == null) {
            instance = new NullComponentMonitor();
        }
        return instance;
    }
}
