/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammaant                                            *
 *****************************************************************************/

package org.picocontainer.monitors;

import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Member;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

/**
 * A {@link ComponentMonitor} which writes to a {@link Writer}. 
 * 
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 * @version $Revision: 1882 $
 */
public class WriterComponentMonitor extends AbstractComponentMonitor {

    private final PrintWriter out;
    private final ComponentMonitor delegate;

    public WriterComponentMonitor(Writer out) {
        this.out = new PrintWriter(out);
        delegate = NullComponentMonitor.getInstance();
    }

    public WriterComponentMonitor(Writer out, ComponentMonitor delegate) {
        this.out = new PrintWriter(out);
        this.delegate = delegate;
    }

    public Constructor instantiating(PicoContainer container, ComponentAdapter componentAdapter,
                                     Constructor constructor) {
        out.println(format(INSTANTIATING, toString(constructor)));
        return delegate.instantiating(container, componentAdapter, constructor);
    }

    public void instantiated(PicoContainer container, ComponentAdapter componentAdapter,
                             Constructor constructor,
                             Object instantiated,
                             Object[] injected,
                             long duration) {
        out.println(format(INSTANTIATED2, toString(constructor), duration, instantiated.getClass().getName(), toString(injected)));
        delegate.instantiated(container, componentAdapter, constructor, instantiated, injected, duration);
    }

    public void instantiationFailed(PicoContainer container,
                                    ComponentAdapter componentAdapter,
                                    Constructor constructor,
                                    Exception cause) {
        out.println(format(INSTANTIATION_FAILED, toString(constructor), cause.getMessage()));
        delegate.instantiationFailed(container, null, constructor, cause);
    }

    public void invoking(PicoContainer container,
                         ComponentAdapter componentAdapter,
                         Member member,
                         Object instance) {
        out.println(format(INVOKING, toString(member), instance));
        delegate.invoking(container, componentAdapter, member, instance);
    }

    public void invoked(PicoContainer container,
                        ComponentAdapter componentAdapter,
                        Method method,
                        Object instance,
                        long duration) {
        out.println(format(INVOKED, toString(method), instance, duration));
        delegate.invoked(container, componentAdapter, method, instance, duration);
    }

    public void invocationFailed(Member member, Object instance, Exception cause) {
        out.println(format(INVOCATION_FAILED, toString(member), instance, cause.getMessage()));
        delegate.invocationFailed(member, instance, cause);
    }

    public void lifecycleInvocationFailed(MutablePicoContainer container,
                                          ComponentAdapter componentAdapter, Method method,
                                          Object instance,
                                          RuntimeException cause) {
        out.println(format(LIFECYCLE_INVOCATION_FAILED, toString(method), instance, cause.getMessage()));
        delegate.lifecycleInvocationFailed(container, componentAdapter, method, instance, cause);
    }

    public void noComponent(MutablePicoContainer container, Object componentKey) {
        out.println(format(NO_COMPONENT, componentKey));
        delegate.noComponent(container, componentKey);
    }
}
