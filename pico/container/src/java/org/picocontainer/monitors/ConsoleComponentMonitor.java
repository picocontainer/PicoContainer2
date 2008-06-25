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

import static org.picocontainer.monitors.ComponentMonitorHelper.ctorToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.format;
import static org.picocontainer.monitors.ComponentMonitorHelper.memberToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.methodToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.parmsToString;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.AbstractInjector;

/**
 * A {@link ComponentMonitor} which writes to a {@link OutputStream}. 
 * This is typically used to write to a console.
 * 
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 * @todo  After serialization, the output printstream is null.  
 */
@SuppressWarnings("serial")
public final class ConsoleComponentMonitor implements ComponentMonitor, Serializable {


	/**
	 * The outgoing print stream.
	 */
    private final transient PrintStream out;
    
    /**
     * Delegate component monitor (for component monitor chains).
     */
    private final ComponentMonitor delegate;

    /**
     * Constructs a console component monitor that sends output to <tt>System.out</tt>.
     */
    public ConsoleComponentMonitor() {
        this(System.out);
    }

    /**
     * Constructs a console component monitor that sends output to the specified output stream.
     * 
     * @param out  the designated output stream.  Options include System.out, Socket streams, File streams,
     * etc.
     */
    public ConsoleComponentMonitor(OutputStream out) {
        this(out, new NullComponentMonitor());
    }

    /**
     * Constructs a console component monitor chain that sends output to the specified output stream
     * and then sends all events to the delegate component monitor.
     * @param out the output stream of choice.
     * @param delegate the next monitor in the component monitor chain to receive event information.
     */
    public ConsoleComponentMonitor(OutputStream out, ComponentMonitor delegate) {
        this.out = new PrintStream(out);
        this.delegate = delegate;
    }

    public <T> Constructor<T> instantiating(PicoContainer container, ComponentAdapter<T> componentAdapter,
                                     Constructor<T> constructor
    ) {
        out.println(format(ComponentMonitorHelper.INSTANTIATING, ctorToString(constructor)));
        return delegate.instantiating(container, componentAdapter, constructor);
    }

    public <T> void instantiated(PicoContainer container, ComponentAdapter<T> componentAdapter,
                             Constructor<T> constructor,
                             Object instantiated,
                             Object[] parameters,
                             long duration) {
        out.println(format(ComponentMonitorHelper.INSTANTIATED, ctorToString(constructor), duration, instantiated.getClass().getName(), parmsToString(parameters)));
        delegate.instantiated(container, componentAdapter, constructor, instantiated, parameters, duration);
    }

    public <T> void instantiationFailed(PicoContainer container,
                                    ComponentAdapter<T> componentAdapter,
                                    Constructor<T> constructor,
                                    Exception cause) {
        out.println(format(ComponentMonitorHelper.INSTANTIATION_FAILED, ctorToString(constructor), cause.getMessage()));
        delegate.instantiationFailed(container, componentAdapter, constructor, cause);
    }

    public void invoking(PicoContainer container,
                         ComponentAdapter<?> componentAdapter,
                         Member member,
                         Object instance) {
        out.println(format(ComponentMonitorHelper.INVOKING, memberToString(member), instance));
        delegate.invoking(container, componentAdapter, member, instance);
    }

    public void invoked(PicoContainer container,
                        ComponentAdapter<?> componentAdapter,
                        Method method,
                        Object instance,
                        long duration) {
        out.println(format(ComponentMonitorHelper.INVOKED, methodToString(method), instance, duration));
        delegate.invoked(container, componentAdapter, method, instance, duration);
    }

    public void invocationFailed(Member member, Object instance, Exception cause) {
        out.println(format(ComponentMonitorHelper.INVOCATION_FAILED, memberToString(member), instance, cause.getMessage()));
        delegate.invocationFailed(member, instance, cause);
    }

    public void lifecycleInvocationFailed(MutablePicoContainer container,
                                          ComponentAdapter<?> componentAdapter, Method method,
                                          Object instance,
                                          RuntimeException cause) {
        out.println(format(ComponentMonitorHelper.LIFECYCLE_INVOCATION_FAILED, methodToString(method), instance, cause.getMessage()));
        delegate.lifecycleInvocationFailed(container, componentAdapter, method, instance, cause);
    }

    public Object noComponentFound(MutablePicoContainer container, Object componentKey) {
        out.println(format(ComponentMonitorHelper.NO_COMPONENT, componentKey));
        return delegate.noComponentFound(container, componentKey);
    }

    public AbstractInjector newInjectionFactory(AbstractInjector abstractInjector) {
        return delegate.newInjectionFactory(abstractInjector);
    }

}
