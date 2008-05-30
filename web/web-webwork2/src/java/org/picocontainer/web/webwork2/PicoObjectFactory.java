/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.web.webwork2;

import com.opensymphony.xwork.ObjectFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.ObjectReference;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.web.PicoServletContainerFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>
 * XWork ObjectFactory which uses a PicoContainer to create component instances.
 * </p>
 * 
 * @author Cyrille Le Clerc
 * @author Jonas Engman
 * @author Mauro Talevi
 * @author Gr&eacute;gory Joseph
 * @author Konstatin Pribluda 
 */
public class PicoObjectFactory extends ObjectFactory {

    private final Map classCache = new HashMap();

    public boolean isNoArgConstructorRequired() {
        return false;
    }

    /**
     * Webwork-2.2 / XWork-1.1 method. ExtraContext can be ignored.
     * @throws Exception
     * @param clazz
     * @param extraContext
     * @return
     */
    public Object buildBean(Class clazz, Map extraContext) throws Exception {
        return buildBean(clazz);
    }

    /**
     * Webwork-2.2 / XWork-1.1 method. ExtraContext can be ignored.
     * @return
     * @param extraContext
     * @throws Exception
     * @param className
     */
    public Object buildBean(String className, Map extraContext) throws Exception {
        return buildBean(className);
    }

    /**
     * Webwork-2.2 / XWork-1.1 method. Used to validate a class be loaded.
     * Using actionsContainerFactory for consistency with build methods.
     */
    public Class getClassInstance(String className) {
        return getActionClass(className);
    }

    /**
     * Instantiates an action using the PicoContainer found in the request scope.
     * if action or bean is not registered explicitely, new instance will be provided
     * on every invocation.
     * 
     * @see com.opensymphony.xwork.ObjectFactory#buildBean(java.lang.Class)
     */
    public Object buildBean(Class actionClass) throws Exception {
        PicoContainer actionsContainer = PicoServletContainerFilter.getRequestContainerForThread();
        Object action = actionsContainer.getComponent(actionClass);

        if (action == null) {
            // The action wasn't registered. Attempt to instantiate it.
        	// use child container to prevent weirdest errors
        	MutablePicoContainer child = new DefaultPicoContainer(actionsContainer);
        	
            child.addComponent(actionClass);
            action = child.getComponent(actionClass);
        }
        return action;
    }

    /**
     * As {@link ObjectFactory#buildBean(java.lang.String)}does not delegate to
     * {@link ObjectFactory#buildBean(java.lang.Class)} but directly calls
     * <code>clazz.newInstance()</code>, overwrite this method to call
     * <code>buildBean()</code>
     *
     * @see com.opensymphony.xwork.ObjectFactory#buildBean(java.lang.String)
     */
    public Object buildBean(String className) throws Exception {
        Class actionClass = getActionClass(className);
        return buildBean(actionClass);
    }

    public Class getActionClass(String className) throws PicoCompositionException {
        try {
            return loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new PicoCompositionException("Action class '" + className + "' not found", e);
        }
    }

    protected Class loadClass(String className) throws ClassNotFoundException {
        if (classCache.containsKey(className)) {
            return (Class) classCache.get(className);
        } else {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class result = classLoader.loadClass(className);
            classCache.put(className, result);
            return result;
        }
    }  

}
