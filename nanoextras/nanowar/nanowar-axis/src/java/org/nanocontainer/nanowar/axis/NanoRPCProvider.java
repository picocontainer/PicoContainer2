/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.axis;

import org.apache.axis.MessageContext;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.cache.ClassCache;
import org.nanocontainer.nanowar.KeyConstants;
import org.nanocontainer.nanowar.RequestScopeReference;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.ObjectReference;

import javax.servlet.http.HttpServletRequest;

/**
 * Axis provider for RPC-style services that uses the servlet container
 * hierarchy to instantiate service classes and resolve their dependencies.
 *
 * @author <a href="mailto:evan@bottch.com">Evan Bottcher</a>
 */
public class NanoRPCProvider extends RPCProvider implements KeyConstants {

    protected Object makeNewServiceObject(
        MessageContext msgContext,
        String clsName)
        throws Exception {

        ClassLoader cl = msgContext.getClassLoader();
        ClassCache cache = msgContext.getAxisEngine().getClassCache();
        Class svcClass = cache.lookup(clsName, cl).getJavaClass();

        return instantiateService(svcClass, msgContext);

    }

    private Object instantiateService(Class svcClass, MessageContext msgContext) {

        HttpServletRequest request = (HttpServletRequest)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
        ObjectReference ref = new RequestScopeReference(request, REQUEST_CONTAINER);
        MutablePicoContainer requestContainer = (MutablePicoContainer) ref.get();

        MutablePicoContainer container = new DefaultPicoContainer(requestContainer);
        container.addComponent(svcClass);
        return container.getComponent(svcClass);
    }

}
