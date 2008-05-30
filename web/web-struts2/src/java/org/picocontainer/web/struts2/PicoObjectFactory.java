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
        register(clazz);
        return clazz;
    }

    @SuppressWarnings("unchecked")
    private void register(Class clazz) throws NoClassDefFoundError {

        synchronized (this) {

            MutablePicoContainer requestContainer = PicoServletContainerFilter.getRequestContainerForThread();
            if ( requestContainer == null){
                return;
            }
            ComponentAdapter ca = requestContainer.getComponentAdapter(clazz);
            if (ca == null) {
                try {
                    requestContainer.addComponent(clazz);
                } catch (NoClassDefFoundError e) {
                    if (e.getMessage().equals("org/apache/velocity/context/Context")) {
                        // half expected. XWork seems to setup stuff that cannot
                        // work
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
        if ( requestContainer == null ){
            return null;
        }
        return requestContainer.getComponent(clazz);

    }

    @SuppressWarnings("unchecked")
    public Interceptor buildInterceptor(InterceptorConfig config, Map params) throws ConfigurationException {
        System.out.println("-->buildInterceptor: " + config.getClassName() + " " + params);
        return super.buildInterceptor(config, params);
    }

    public boolean isNoArgConstructorRequired() {
        return false;
    }

}
