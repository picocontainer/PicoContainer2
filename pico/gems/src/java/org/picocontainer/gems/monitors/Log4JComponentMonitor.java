/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.gems.monitors;

import static org.picocontainer.monitors.ComponentMonitorHelper.methodToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.memberToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.ctorToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.parmsToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.format;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.picocontainer.monitors.ComponentMonitorHelper;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;


/**
 * A {@link org.picocontainer.ComponentMonitor} which writes to a Log4J {@link org.apache.log4j.Logger} instance.
 * The Logger instance can either be injected or, if not set, the {@link LogManager LogManager}
 * will be used to retrieve it at every invocation of the monitor.
 *
 * @author Paul Hammant
 * @author Mauro Talevi
 */
public class Log4JComponentMonitor implements ComponentMonitor, Serializable {

    private Logger logger;
    private final ComponentMonitor delegate;

    /**
     * Creates a Log4JComponentMonitor with no Logger instance set.
     * The {@link LogManager LogManager} will be used to retrieve the Logger instance
     * at every invocation of the monitor.
     */
    public Log4JComponentMonitor() {
        delegate = new NullComponentMonitor();
    }
    
    /**
     * Creates a Log4JComponentMonitor with a given Logger instance class.
     * The class name is used to retrieve the Logger instance.
     *
     * @param loggerClass the class of the Logger
     */
    public Log4JComponentMonitor(Class loggerClass) {
        this(loggerClass.getName());
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance name. It uses the
     * {@link org.apache.log4j.LogManager LogManager} to create the Logger instance.
     *
     * @param loggerName the name of the Log
     */
    public Log4JComponentMonitor(String loggerName) {
        this(LogManager.getLogger(loggerName));
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance
     *
     * @param logger the Logger to write to
     */
    public Log4JComponentMonitor(Logger logger) {
        this();
        this.logger = logger;
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance class.
     * The class name is used to retrieve the Logger instance.
     *
     * @param loggerClass the class of the Logger
     * @param delegate the delegate
     */
    public Log4JComponentMonitor(Class loggerClass, ComponentMonitor delegate) {
        this(loggerClass.getName(), delegate);
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance name. It uses the
     * {@link org.apache.log4j.LogManager LogManager} to create the Logger instance.
     *
     * @param loggerName the name of the Log
     * @param delegate the delegate
     */
    public Log4JComponentMonitor(String loggerName, ComponentMonitor delegate) {
        this(LogManager.getLogger(loggerName), delegate);
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance
     *
     * @param logger the Logger to write to
     * @param delegate the delegate
     */
    public Log4JComponentMonitor(Logger logger, ComponentMonitor delegate) {
        this(delegate);
        this.logger = logger;
    }

    public Log4JComponentMonitor(ComponentMonitor delegate) {
        this.delegate = delegate;
    }

    public Constructor instantiating(PicoContainer container, ComponentAdapter componentAdapter,
                                     Constructor constructor
    ) {
        Logger logger = getLogger(constructor);
        if (logger.isDebugEnabled()) {
            logger.debug(format(ComponentMonitorHelper.INSTANTIATING, ctorToString(constructor)));
        }
        return delegate.instantiating(container, componentAdapter, constructor);
    }

    public void instantiated(PicoContainer container, ComponentAdapter componentAdapter,
                             Constructor constructor,
                             Object instantiated,
                             Object[] parameters,
                             long duration) {
        Logger logger = getLogger(constructor);
        if (logger.isDebugEnabled()) {
            logger.debug(format(ComponentMonitorHelper.INSTANTIATED, ctorToString(constructor), duration, instantiated.getClass().getName(), parmsToString(parameters)));
        }
        delegate.instantiated(container, componentAdapter, constructor, instantiated, parameters, duration);
    }

    public void instantiationFailed(PicoContainer container,
                                    ComponentAdapter componentAdapter,
                                    Constructor constructor,
                                    Exception cause) {
        Logger logger = getLogger(constructor);
        if (logger.isEnabledFor(Priority.WARN)) {
            logger.warn(format(ComponentMonitorHelper.INSTANTIATION_FAILED, ctorToString(constructor), cause.getMessage()), cause);
        }
        delegate.instantiationFailed(container, componentAdapter, constructor, cause);
    }

    public void invoking(PicoContainer container,
                         ComponentAdapter componentAdapter,
                         Member member,
                         Object instance) {
        Logger logger = getLogger(member);
        if (logger.isDebugEnabled()) {
            logger.debug(format(ComponentMonitorHelper.INVOKING, memberToString(member), instance));
        }
        delegate.invoking(container, componentAdapter, member, instance);
    }

    public void invoked(PicoContainer container,
                        ComponentAdapter componentAdapter,
                        Method method,
                        Object instance,
                        long duration) {
        Logger logger = getLogger(method);
        if (logger.isDebugEnabled()) {
            logger.debug(format(ComponentMonitorHelper.INVOKED, methodToString(method), instance, duration));
        }
        delegate.invoked(container, componentAdapter, method, instance, duration);
    }

    public void invocationFailed(Member member, Object instance, Exception cause) {
        Logger logger = getLogger(member);
        if (logger.isEnabledFor(Priority.WARN)) {
            logger.warn(format(ComponentMonitorHelper.INVOCATION_FAILED, memberToString(member), instance, cause.getMessage()), cause);
        }
        delegate.invocationFailed(member, instance, cause);
    }

    public void lifecycleInvocationFailed(MutablePicoContainer container,
                                          ComponentAdapter componentAdapter, Method method,
                                          Object instance,
                                          RuntimeException cause) {
        Logger logger = getLogger(method);
        if (logger.isEnabledFor(Priority.WARN)) {
            logger.warn(format(ComponentMonitorHelper.LIFECYCLE_INVOCATION_FAILED, methodToString(method), instance, cause.getMessage()), cause);
        }
        delegate.lifecycleInvocationFailed(container, componentAdapter, method, instance, cause);
    }

    public Object noComponentFound(MutablePicoContainer container, Object componentKey) {
        Logger logger = this.logger != null ? this.logger : LogManager.getLogger(ComponentMonitor.class);
        if (logger.isEnabledFor(Priority.WARN)) {
            logger.warn(format(ComponentMonitorHelper.NO_COMPONENT, componentKey));
        }
        return delegate.noComponentFound(container, componentKey);

    }

    protected Logger getLogger(Member member) {
        if ( logger != null ){
            return logger;
        } 
        return LogManager.getLogger(member.getDeclaringClass());
    }

}
