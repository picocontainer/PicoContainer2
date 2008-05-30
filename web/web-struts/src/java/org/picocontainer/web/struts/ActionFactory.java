/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.web.PicoServletContainerFilter;

import java.util.Map;
import java.util.HashMap;

/**
 * Uses PicoContainer to produce Actions and inject dependencies into them. 
 * If you have your own <code>RequestProcessor</code> implementation, you can use an
 * <code>ActionFactory</code> in your <code>RequestProcessor.processActionCreate</code> 
 * method to Picofy your Actions.
 * 
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
public final class ActionFactory {

     private final Map classCache = new HashMap();

    /**
     * Gets the <code>Action</code> specified by the mapping type from a PicoContainer. 
     * The action will be instantiated if necessary, and its
     * dependencies will be injected. The action will be instantiated via a
     * special PicoContainer that just contains actions. If this container
     * already exists in the request attribute, this method will use it. If no
     * such container exists, this method will create a new Pico container and
     * place it in the request. The parent container will either be the request
     * container, or if that container can not be found the session container,
     * or if that container can not be found, the application container. If no
     * parent container can be found, a <code>PicoCompositionException</code>
     * will be thrown. The action path specified in the mapping is used as
     * the component key for the action.
     * 
     * @param request the Http servlet request.
     * @param mapping the Struts mapping object, whose type property tells us what
     *        Action class is required.
     * @param servlet the Struts <code>ActionServlet</code>.  
     * @return the <code>Action</code> instance.
     * @throws PicoCompositionException  if the mapping type does not specify a valid action.
     * @throws PicoCompositionException if no request, session, or application scoped Pico container
     *                                     can be found.
     */
    public Action getAction(HttpServletRequest request, ActionMapping mapping, ActionServlet servlet)
            throws PicoCompositionException
    {

        MutablePicoContainer actionsContainer = PicoServletContainerFilter.getRequestContainerForThread();
        Object actionKey = mapping.getPath();
        Class actionType = getActionClass(mapping.getType());

        Action action = (Action) actionsContainer.getComponent(actionKey);
        if (action == null) {
            actionsContainer.addComponent(actionKey, actionType);
            action = (Action) actionsContainer.getComponent(actionKey);
        }

        action.setServlet(servlet);
        return action;
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