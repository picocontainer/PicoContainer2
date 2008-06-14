/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/

package org.picocontainer.web.struts2;

import java.util.Map;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.PicoServletContainerFilter;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * XWork2 ObjectFactory implementation to delegate action/component/bean lookups
 * to PicoContainer.
 * 
 * @author Paul Hammant
 * @author Mauro Talevi
 */
@SuppressWarnings("serial")
public class PicoObjectFactory extends ObjectFactory {

    @SuppressWarnings("unchecked")
    public Class getClassInstance(String name) throws ClassNotFoundException {
        Class clazz = super.getClassInstance(name);
        registerAction(clazz);
        return clazz;
    }

    private void registerAction(Class<?> clazz) throws NoClassDefFoundError {

        synchronized (this) {

            MutablePicoContainer reqContainer = PicoServletContainerFilter.getRequestContainerForThread();
            if (reqContainer == null) {
                return;
            }
            ComponentAdapter<?> ca = reqContainer.getComponentAdapter(clazz);
            if (ca == null) {
                try {
                    reqContainer.addComponent(clazz);
                } catch (NoClassDefFoundError e) {
                    if (e.getMessage().equals("org/apache/velocity/context/Context")) {
                        // half expected. XWork seems to setup stuff that cannot
                        // work
                        // TODO if this is the case we should make configurable
                        // the list of classes we "expect" not to find.  Odd!
                    } else {
                        throw e;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Object buildBean(Class clazz, Map extraContext) throws Exception {

        MutablePicoContainer requestContainer = PicoServletContainerFilter.getRequestContainerForThread();
        if (requestContainer == null) {
            MutablePicoContainer appContainer = PicoServletContainerFilter.getApplicationContainerForThread();
            Object comp = appContainer.getComponent(clazz);
            if (comp == null) {
                appContainer.addComponent(clazz);
                comp = appContainer.getComponent(clazz);
            }
            return comp;

        }
        return requestContainer.getComponent(clazz);

    }

    @SuppressWarnings("unchecked")
    public Interceptor buildInterceptor(InterceptorConfig config, Map params) throws ConfigurationException {
        return super.buildInterceptor(config, params);
    }

    public boolean isNoArgConstructorRequired() {
        return false;
    }

}
