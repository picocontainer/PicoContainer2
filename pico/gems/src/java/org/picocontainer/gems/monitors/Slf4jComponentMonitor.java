/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammaant                                            *
 *****************************************************************************/
package org.picocontainer.gems.monitors;

import static org.picocontainer.monitors.ComponentMonitorHelper.ctorToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.format;
import static org.picocontainer.monitors.ComponentMonitorHelper.memberToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.methodToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.parmsToString;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.monitors.ComponentMonitorHelper;
import org.picocontainer.monitors.NullComponentMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link org.picocontainer.ComponentMonitor} which writes to a Slf4j {@link org.slf4j.Logger} instance.
 * The Logger instance can either be injected or, if not set, the {@link org.slf4j.LoggerFactory}
 * will be used to retrieve it at every invocation of the monitor.
 *
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Michael Rimov
 */
public class Slf4jComponentMonitor implements ComponentMonitor, Serializable {
	/**
	 * Serialization UUID.
	 */
	private static final long serialVersionUID = -4312191439604292585L;

	/**
	 * Slf4j Logger.
	 */
    private transient Logger logger;
    
    /**
     * A serialized string that is used to reconstruct the logger instance after de-serialization.
     */
    private String defaultLoggerCategory;
    
    /**
     * Delegate Monitor.
     */
    private final ComponentMonitor delegate;



    /**
     * Creates a Slf4jComponentMonitor with no Logger instance set.
     * The {@link org.slf4j.LoggerFactory} will be used to retrieve the Logger instance
     * at every invocation of the monitor.
     */
    public Slf4jComponentMonitor() {
        delegate = new NullComponentMonitor();
        
    }
    
    /**
     * Creates a Slf4jComponentMonitor with a given Logger instance class.
     * The class name is used to retrieve the Logger instance.
     *
     * @param loggerClass the class of the Logger
     */
    public Slf4jComponentMonitor(Class<?> loggerClass) {
        this(loggerClass.getName());
    }

    /**
     * Creates a Slf4jComponentMonitor with a given Logger instance name. It uses the
     * {@link org.slf4j.LoggerFactory} to create the Logger instance.
     *
     * @param loggerName the name of the Log
     */
    public Slf4jComponentMonitor(String loggerName) {
        this(LoggerFactory.getLogger(loggerName));
    }

    /**
     * Creates a Slf4jComponentMonitor with a given Logger instance
     *
     * @param logger the Logger to write to
     */
    public Slf4jComponentMonitor(Logger logger) {
        this();
        this.logger = logger;
        defaultLoggerCategory = logger.getName();
    }

    /**
     * Creates a Slf4jComponentMonitor with a given Logger instance class.
     * The class name is used to retrieve the Logger instance.
     *
     * @param loggerClass the class of the Logger
     * @param delegate the delegate
     */
    public Slf4jComponentMonitor(Class<?> loggerClass, ComponentMonitor delegate) {
        this(loggerClass.getName(), delegate);
    }

    /**
     * Creates a Slf4jComponentMonitor with a given Logger instance name. It uses the
     * {@link org.slf4j.LoggerFactory} to create the Logger instance.
     *
     * @param loggerName the name of the Log
     * @param delegate the delegate
     */
    public Slf4jComponentMonitor(String loggerName, ComponentMonitor delegate) {
        this(LoggerFactory.getLogger(loggerName), delegate);
    }

    /**
     * Creates a Slf4jComponentMonitor with a given Slf4j Logger instance
     *
     * @param logger the Logger to write to
     * @param delegate the delegate
     */
    public Slf4jComponentMonitor(Logger logger, ComponentMonitor delegate) {
        this(delegate);
        this.logger = logger;
        defaultLoggerCategory = logger.getName();
    }

    /**
     * Similar to default constructor behavior, but this version wraps a delegate ComponentMonitor.
     * @param delegate  The next component monitor in the chain.
     */
    public Slf4jComponentMonitor(ComponentMonitor delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} **/
    public <T> Constructor<T> instantiating(PicoContainer container, ComponentAdapter<T> componentAdapter,
                                     Constructor<T> constructor
    ) {
        Logger logger = getLogger(constructor);
        if (logger.isDebugEnabled()) {
            logger.debug(format(ComponentMonitorHelper.INSTANTIATING, ctorToString(constructor)));
        }
        return delegate.instantiating(container, componentAdapter, constructor);
    }

    /** {@inheritDoc} **/
    public <T> void instantiated(PicoContainer container, ComponentAdapter<T> componentAdapter,
                             Constructor<T> constructor,
                             Object instantiated,
                             Object[] parameters,
                             long duration) {
        Logger logger = getLogger(constructor);
        if (logger.isDebugEnabled()) {
            logger.debug(format(ComponentMonitorHelper.INSTANTIATED, ctorToString(constructor), duration, instantiated.getClass().getName(), parmsToString(parameters)));
        }
        delegate.instantiated(container, componentAdapter, constructor, instantiated, parameters, duration);
    }

    /** {@inheritDoc} **/
    public <T> void instantiationFailed(PicoContainer container,
                                    ComponentAdapter<T> componentAdapter,
                                    Constructor<T> constructor,
                                    Exception cause) {
        Logger logger = getLogger(constructor);
        if (logger.isWarnEnabled()) {
            logger.warn(format(ComponentMonitorHelper.INSTANTIATION_FAILED, ctorToString(constructor), cause.getMessage()), cause);
        }
        delegate.instantiationFailed(container, componentAdapter, constructor, cause);
    }

    /** {@inheritDoc} **/
    public void invoking(PicoContainer container,
                         ComponentAdapter<?> componentAdapter,
                         Member member,
                         Object instance) {
        Logger logger = getLogger(member);
        if (logger.isDebugEnabled()) {
            logger.debug(format(ComponentMonitorHelper.INVOKING, memberToString(member), instance));
        }
        delegate.invoking(container, componentAdapter, member, instance);
    }

    /** {@inheritDoc} **/
    public void invoked(PicoContainer container,
                        ComponentAdapter<?> componentAdapter,
                        Method method,
                        Object instance,
                        long duration) {
        Logger logger = getLogger(method);
        if (logger.isDebugEnabled()) {
            logger.debug(format(ComponentMonitorHelper.INVOKED, methodToString(method), instance, duration));
        }
        delegate.invoked(container, componentAdapter, method, instance, duration);
    }

    /** {@inheritDoc} **/
    public void invocationFailed(Member member, Object instance, Exception cause) {
        Logger logger = getLogger(member);
        if (logger.isWarnEnabled()) {
            logger.warn(format(ComponentMonitorHelper.INVOCATION_FAILED, memberToString(member), instance, cause.getMessage()), cause);
        }
        delegate.invocationFailed(member, instance, cause);
    }

    /** {@inheritDoc} **/
    public void lifecycleInvocationFailed(MutablePicoContainer container,
                                          ComponentAdapter<?> componentAdapter, Method method,
                                          Object instance,
                                          RuntimeException cause) {
        Logger logger = getLogger(method);
        if (logger.isWarnEnabled()) {
            logger.warn(format(ComponentMonitorHelper.LIFECYCLE_INVOCATION_FAILED, methodToString(method), instance, cause.getMessage()), cause);
        }
        delegate.lifecycleInvocationFailed(container, componentAdapter, method, instance, cause);
    }

    /** {@inheritDoc} **/
    public Object noComponentFound(MutablePicoContainer container, Object componentKey) {
        Logger logger = this.logger != null ? this.logger : LoggerFactory.getLogger(ComponentMonitor.class);
        if (logger.isWarnEnabled()) {
            logger.warn(format(ComponentMonitorHelper.NO_COMPONENT, componentKey));
        }
        return delegate.noComponentFound(container, componentKey);

    }

    /** {@inheritDoc} **/
    public AbstractInjector newInjectionFactory(AbstractInjector abstractInjector) {
        return delegate.newInjectionFactory(abstractInjector);
    }

    /**
     * Retrieves the logger factory based class being instantiated.
     * @param member Source method/constructor, etc being instantiated.
     * @return an appropriate logger instance for this callback.
     */
    protected Logger getLogger(Member member) {
        if ( logger != null ){
            return logger;
        } 
        return LoggerFactory.getLogger(member.getDeclaringClass());
    }

    
    /**
     * Manually creates a new logger instance if it was defined earlier.
     * @param ois
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    	ois.defaultReadObject();
    	if (this.defaultLoggerCategory != null) {
    		logger = LoggerFactory.getLogger(defaultLoggerCategory);
    	}
    }}
