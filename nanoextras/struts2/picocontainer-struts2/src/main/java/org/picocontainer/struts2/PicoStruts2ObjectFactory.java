/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.struts2;

import java.util.Map;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.Storing;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * XWork ObjectFactory implementation to deleegate action/component/bean lookups to PicoContainer.
 * See http://picocontainer.org/struts2.html 
 */
public class PicoStruts2ObjectFactory extends ObjectFactory {
    private String compositionClassName;
    private boolean setupDone;
    private static final long serialVersionUID = 3134861897337459958L;

    private final MutablePicoContainer rootContainer = new DefaultPicoContainer(new Caching());
    private WebAppComposition webAppComposition;
    private PicoContainer requestContainer;


    @Inject(value = "pico-composition", required = true)
    void setCompositionClass(String compositionClassName) {
        this.compositionClassName = compositionClassName;
    }

    private void compose(Storing sessionStoring, Storing requestStoring) throws ClassNotFoundException {
        try {
            webAppComposition = (WebAppComposition) Class.forName(compositionClassName).newInstance();
        } catch (InstantiationException e) {
            throw new PicoCompositionException(e);
        } catch (IllegalAccessException e) {
            throw new PicoCompositionException(e);
        }
        requestContainer = webAppComposition.compose(rootContainer, sessionStoring, requestStoring);
    }

    public Class getClassInstance(String name) throws ClassNotFoundException {

        Class clazz = super.getClassInstance(name);

        synchronized (this) {

            PicoStruts2Filter.Stores stores = PicoStruts2Filter.getStores();

            // when the first web request comes in,
            // setup the tiered containers
            if (!setupDone && stores != null) {
                compose(stores.session(), stores.request());
                setupDone = true;
            }

            // xwork gives us additional components to hold, before
            // the first request.
            if (!setupDone) {
                ComponentAdapter ca = rootContainer.getComponentAdapter(clazz);
                if (ca == null) {
                    try {
                        rootContainer.addComponent(clazz);
                    } catch (NoClassDefFoundError e) {
                        if (e.getMessage().equals("org/apache/velocity/context/Context")) {
                            // half expected. XWork seems to setup stuff that cannot work
                        } else {
                            throw e;
                        }
                    }
                }
            }
        }

        return clazz;
    }

    public Object buildBean(Class clazz, Map extraContext) throws Exception {

        if (setupDone) {
            Object component = requestContainer.getComponent(clazz);
            return component;
        }
        return super.buildBean(clazz, extraContext);
    }

    public Interceptor buildInterceptor(InterceptorConfig config, Map params) throws ConfigurationException {
        System.out.println("-->buildInterceptor: " + config.getClassName() + " " + params);
        return super.buildInterceptor(config, params);
    }

    public boolean isNoArgConstructorRequired() {
        return false;
    }

}
